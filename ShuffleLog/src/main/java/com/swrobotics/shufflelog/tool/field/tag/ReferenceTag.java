package com.swrobotics.shufflelog.tool.field.tag;

import com.swrobotics.shufflelog.math.Matrix4f;
import com.swrobotics.shufflelog.tool.field.GizmoTarget;

public final class ReferenceTag implements GizmoTarget {
    private final String name;
    private final double size;
    private final Matrix4f transform; // Encodes position and rotation

    public ReferenceTag(String name, double size) {
        this.name = name;
        this.size = size;
        transform = new Matrix4f();
    }

    public String getName() {
        return name;
    }

    public double getSize() {
        return size;
    }

    @Override
    public Matrix4f getTransform() {
        return transform;
    }
}
