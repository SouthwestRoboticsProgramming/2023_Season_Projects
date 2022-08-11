package com.swrobotics.pathfinding.grid;

import com.swrobotics.messenger.client.MessageBuilder;
import com.swrobotics.pathfinding.geom.RobotShape;
import com.swrobotics.pathfinding.geom.Shape;
import com.swrobotics.pathfinding.Field;

import java.util.HashSet;
import java.util.Set;

public final class ShapeGrid extends BitfieldGrid {
    private final RobotShape robot;
    private final Field field;
    private final Set<Shape> shapes;
    private boolean needsRegenerateBitfield;

    public ShapeGrid(int width, int height, Field field, RobotShape robot) {
        super(width, height);
        this.field = field;
        this.robot = robot;
        shapes = new HashSet<>();
        needsRegenerateBitfield = false;
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
        needsRegenerateBitfield = true;
    }

    public void removeShape(Shape shape) {
        shapes.remove(shape);
        needsRegenerateBitfield = true;
    }

    private void regenerateBitfield() {
        needsRegenerateBitfield = false;

        for (int y = 0; y < getCellHeight(); y++) {
            for (int x = 0; x < getCellWidth(); x++) {
                double robotX = field.getCellCenterX(x);
                double robotY = field.getCellCenterY(y);

                boolean canPass = true;
                for (Shape shape : shapes) {
                    if (shape.collidesWith(robot, robotX, robotY)) {
                        canPass = false;
                    }
                }

                set(x, y, canPass);
            }
        }
    }

    @Override
    public boolean canCellPass(int x, int y) {
        if (needsRegenerateBitfield)
            regenerateBitfield();

        return super.canCellPass(x, y);
    }

    @Override
    public void writeToMessenger(MessageBuilder builder) {
        builder.addByte(GridTypeIds.SHAPE);
        builder.addInt(shapes.size());
        for (Shape shape : shapes) {
            shape.writeToMessenger(builder);
        }
    }
}
