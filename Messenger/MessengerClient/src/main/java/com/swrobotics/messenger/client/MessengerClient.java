package com.swrobotics.messenger.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
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

    private final String host;
    private final int port;
    private final String name;

    private AtomicBoolean connected;
    private Thread connectThread;
    private final ScheduledExecutorService executor;
    private final ScheduledFuture<?> heartbeatFuture;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public MessengerClient(String host, int port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;

        socket = null;
        connected = new AtomicBoolean(false);

        startConnectThread();

        executor = Executors.newSingleThreadScheduledExecutor();
        heartbeatFuture = executor.scheduleAtFixedRate(() -> {
            sendMessage(HEARTBEAT, new byte[0]);
        }, 0, 1, TimeUnit.SECONDS);
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
                } catch (IOException e) {
                    System.err.println("Messenger connection failed (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")");
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }

            System.out.println("Debug: Messenger connect thread terminated");
            connectThread = null;
        }, "Messenger Reconnect Thread");

        connectThread.start();
    }

    private void handleWriteError(IOException e) {
        terminateHeartbeatExecutor();

        System.err.println("Messenger connection lost:");
        e.printStackTrace();

        try {
            socket.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        connected.set(false);
        startConnectThread();
    }

    private void terminateHeartbeatExecutor() {
        heartbeatFuture.cancel(false);
        executor.shutdown();
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


    }

    public boolean isConnected() {
        return connected.get();
    }

    public void disconnect() {
        terminateHeartbeatExecutor();
    }

    public MessageBuilder builder(String type) {
        return new MessageBuilder(this, type);
    }

    public void addHandler(String type, MessageHandler handler) {

    }

    void sendMessage(String type, byte[] data) {
        synchronized (out) {
            try {
                out.writeUTF(type);
                out.writeInt(data.length);
                out.write(data);
            } catch (IOException e) {
                handleWriteError(e);
            }
        }
    }
}
