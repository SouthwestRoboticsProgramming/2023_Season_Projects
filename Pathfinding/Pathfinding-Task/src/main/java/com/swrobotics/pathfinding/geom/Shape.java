package com.swrobotics.pathfinding.geom;

import com.swrobotics.messenger.client.MessageBuilder;
import com.swrobotics.pathfinding.grid.ShapeGrid;
import com.swrobotics.pathfinding.task.PathfinderTask;

import java.util.UUID;

public abstract class Shape {
    private UUID id;
    private ShapeGrid parent;

    public Shape() {
        id = UUID.randomUUID();
    }

    public abstract boolean collidesWith(RobotShape robot, double robotX, double robotY);

    public void writeToMessenger(MessageBuilder builder) {
        builder.addLong(id.getMostSignificantBits());
        builder.addLong(id.getLeastSignificantBits());
    }

    public void register(PathfinderTask task) {
        task.registerShape(this);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ShapeGrid getParent() {
        return parent;
    }

    public void setParent(ShapeGrid parent) {
        this.parent = parent;
    }
}
