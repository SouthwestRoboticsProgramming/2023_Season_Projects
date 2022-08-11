package com.swrobotics.messenger.server;

import com.swrobotics.messenger.server.log.FileLogger;
import com.swrobotics.messenger.server.log.MessageLogger;
import com.swrobotics.messenger.server.log.NoOpLogger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class MessengerServer {
    private static final MessengerServer INSTANCE = new MessengerServer();
    public static MessengerServer get() { return INSTANCE; }

    private final MessengerConfiguration config;
    private final Set<Client> clients;
    private final MessageLogger log;

    private MessengerServer() {
        config = MessengerConfiguration.loadFromFile(new File("config.properties"));
        clients = Collections.synchronizedSet(new HashSet<>());

        if (config.getLogFile() == null) {
            log = new NoOpLogger();
        } else {
            log = new FileLogger(config.getLogFile(), config.isCompressLog());
        }
    }

    public void broadcastEvent(String type, String name, String descriptor) {
        log.logEvent(type, name, descriptor);

        byte[] data;
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            out.writeUTF(type);
            out.writeUTF(name);
            out.writeUTF(descriptor);

            data = b.toByteArray();
        } catch (IOException e) {
            return;
        }

        dispatchMessage(new Message("Messenger:Event", data));
    }

    public void onMessage(Message msg) {
        if (log != null)
            log.logMessage(msg);

        dispatchMessage(msg);
    }

    private void dispatchMessage(Message msg) {
        // FIXME: apparently it is possible to have a ConcurrentModificationException here
        for (Client client : clients) {
            if (client.listensTo(msg.getType())) {
                client.sendMessage(msg);
            }
        }
    }

    public void addClient(Client client) {
        clients.add(client);
    }

    public void removeClient(Client client) {
        clients.remove(client);
    }

    public MessengerConfiguration getConfig() {
        return config;
    }

    public MessageLogger getLog() {
        return log;
    }
}
