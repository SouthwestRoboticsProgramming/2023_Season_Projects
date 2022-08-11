package com.swrobotics.pathfinding.geom;

import com.swrobotics.messenger.client.MessageBuilder;

import java.util.UUID;

public abstract class Shape {
    private final UUID id;

    public Shape() {
        id = UUID.randomUUID();
    }

    public abstract boolean collidesWith(RobotShape robot, double robotX, double robotY);

    public void writeToMessenger(MessageBuilder builder) {
        builder.addLong(id.getMostSignificantBits());
        builder.addLong(id.getLeastSignificantBits());
    }
}
