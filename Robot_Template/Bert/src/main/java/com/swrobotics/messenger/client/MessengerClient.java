package com.swrobotics.messenger.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.swrobotics.messenger.client.MessengerUtils.encodeStringUTF;

public final class MessengerClient {
    private static final String HEARTBEAT = "_Heartbeat";
    private static final String LISTEN = "_Listen";
    private static final String UNLISTEN = "_Unlisten";
    private static final String DISCONNECT = "_Disconnect";

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final ScheduledExecutorService executor;
    private final ScheduledFuture<?> heartbeatFuture;

    private final Map<String, Set<MessageHandler>> listeners;

    public MessengerClient(String host, int port, String name) throws IOException {
        socket = new Socket(host, port);

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        out.writeUTF(name);

        executor = Executors.newSingleThreadScheduledExecutor();
        heartbeatFuture = executor.scheduleAtFixedRate(() -> {
            sendMessage(HEARTBEAT, new byte[0]);
        }, 0, 1, TimeUnit.SECONDS);

        listeners = new HashMap<>();
    }

    public void sendMessage(String type) {
        sendMessage(type, new byte[0]);
    }

    public void sendMessage(String type, byte[] data) {
        synchronized (out) {
            try {
                out.writeUTF(type);
                out.writeInt(data.length);
                out.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public MessageBuilder builder(String type) {
        return new MessageBuilder(this, type);
    }

    public MessageHandler makeHandler() {
        return new MessageHandler(this);
    }

    public void readMessages() {
        try {
            while (in.available() > 0) {
                String type = in.readUTF();
                byte[] data = new byte[in.readInt()];
                in.readFully(data);

                Set<MessageHandler> handlerSet = listeners.get(type);
                if (handlerSet != null) {
                    for (MessageHandler handler : handlerSet) {
                        handler.dispatch(type, data);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        sendMessage(DISCONNECT, new byte[0]);

        heartbeatFuture.cancel(false);
        executor.shutdown();

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void addListener(String type, MessageHandler handler) {
        Set<MessageHandler> handlers = listeners.computeIfAbsent(type, (t) -> new HashSet<>());

        if (handlers.isEmpty()) {
            sendMessage(LISTEN, encodeStringUTF(type));
        }

        handlers.add(handler);
    }

    void removeListener(String type, MessageHandler handler) {
        Set<MessageHandler> handlers = listeners.get(type);
        if (handlers == null) {
            return;
        }

        handlers.remove(handler);

        if (handlers.isEmpty()) {
            sendMessage(UNLISTEN, encodeStringUTF(type));
        }
    }

    void removeListenerFully(MessageHandler handler) {
        Set<String> keysToRemove = new HashSet<>();

        for (Map.Entry<String, Set<MessageHandler>> entry : listeners.entrySet()) {
            Set<MessageHandler> handlerSet = entry.getValue();

            if (handlerSet.remove(handler) && handlerSet.isEmpty()) {
                keysToRemove.add(entry.getKey());
                sendMessage(UNLISTEN, encodeStringUTF(entry.getKey()));
            }
        }

        for (String key : keysToRemove) {
            listeners.remove(key);
        }
    }
}
