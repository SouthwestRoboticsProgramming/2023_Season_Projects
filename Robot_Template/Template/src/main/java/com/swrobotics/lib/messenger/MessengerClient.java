package com.swrobotics.lib.messenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MessengerClient {
    private static final String HEARTBEAT = "_Heartbeat";
    private static final String LISTEN = "_Listen";
    private static final String UNLISTEN = "_Unlisten";
    private static final String DISCONNECT = "_Disconnect";

    private String host;
    private int port;
    private String name;

    private final AtomicBoolean connected;
    private final ScheduledExecutorService executor;
    private final ScheduledFuture<?> heartbeatFuture;
    private Thread connectThread;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private final Set<String> listening;
    private final Set<Handler> handlers;

    private Exception lastConnectFailException;

    public MessengerClient(String host, int port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;

        socket = null;
        connected = new AtomicBoolean(false);

        executor = Executors.newSingleThreadScheduledExecutor();
        heartbeatFuture = executor.scheduleAtFixedRate(() -> {
            sendMessage(HEARTBEAT, new byte[0]);
        }, 0, 1, TimeUnit.SECONDS);

        listening = Collections.synchronizedSet(new HashSet<>());
        handlers = new HashSet<>();

        lastConnectFailException = null;

        startConnectThread();
    }

    public void reconnect(String host, int port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;

        send(DISCONNECT);
        disconnectSocket();
        connected.set(false);

        startConnectThread();
    }

    public Exception getLastConnectionException() {
        return lastConnectFailException;
    }

    private void startConnectThread() {
        connectThread = new Thread(() -> {
            while (!connected.get() && !Thread.interrupted()) {
                try {
                    socket = new Socket();
                    socket.setSoTimeout(1000);
                    socket.connect(new InetSocketAddress(host, port), 1000);
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF(name);

                    connected.set(true);

                    for (String listen : listening) {
                        listen(listen);
                    }
                } catch (Exception e) {
                    lastConnectFailException = e;
                    System.err.println("Messenger connection failed (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")");
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }

            connectThread = null;
        }, "Messenger Reconnect Thread");

        connectThread.start();
    }

    private void handleError(IOException e) {
        disconnectSocket();

        System.err.println("Messenger connection lost:");
        e.printStackTrace();

        connected.set(false);
        startConnectThread();
    }

    private void disconnectSocket() {
        if (connectThread != null)
            connectThread.interrupt();
        connectThread = null;

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readMessages() {
        if (!isConnected())
            return;

        try {
            while (in.available() > 0) {
                String type = in.readUTF();
                int dataSize = in.readInt();
                byte[] data = new byte[dataSize];
                in.readFully(data);

                for (Handler handler : handlers) {
                    handler.handle(type, data);
                }
            }
        } catch (IOException e) {
            handleError(e);
        }
    }

    public boolean isConnected() {
        return connected.get();
    }

    public void disconnect() {
        send(DISCONNECT);

        heartbeatFuture.cancel(false);
        executor.shutdown();

        disconnectSocket();
        connected.set(false);
    }

    public MessageBuilder prepare(String type) {
        return new MessageBuilder(this, type);
    }

    public void send(String type) {
        sendMessage(type, new byte[0]);
    }

    public void addHandler(String type, MessageHandler handler) {
        Handler h;
        if (type.endsWith("*")) {
            h = new WildcardHandler(type.substring(0, type.length() - 1), handler);
        } else {
            h = new DirectHandler(type, handler);
        }
        handlers.add(h);

        if (!listening.contains(type)) {
            listening.add(type);

            if (connected.get()) {
                listen(type);
            }
        }
    }

    private void listen(String type) {
        prepare(LISTEN)
                .addString(type)
                .send();
    }

    void sendMessage(String type, byte[] data) {
        if (!connected.get())
            return;

        synchronized (out) {
            try {
                out.writeUTF(type);
                out.writeInt(data.length);
                out.write(data);
            } catch (IOException e) {
                handleError(e);
            }
        }
    }

    private interface Handler {
        void handle(String type, byte[] data);
    }

    private static final class DirectHandler implements Handler {
        private final String targetType;
        private final MessageHandler handler;

        public DirectHandler(String targetType, MessageHandler handler) {
            this.targetType = targetType;
            this.handler = handler;
        }

        @Override
        public void handle(String type, byte[] data) {
            if (type.equals(targetType)) {
                handler.handle(type, new MessageReader(data));
            }
        }
    }

    private static final class WildcardHandler implements Handler {
        private final String targetPrefix;
        private final MessageHandler handler;

        public WildcardHandler(String targetPrefix, MessageHandler handler) {
            this.targetPrefix = targetPrefix;
            this.handler = handler;
        }

        @Override
        public void handle(String type, byte[] data) {
            if (type.startsWith(targetPrefix)) {
                handler.handle(type, new MessageReader(data));
            }
        }
    }
}
