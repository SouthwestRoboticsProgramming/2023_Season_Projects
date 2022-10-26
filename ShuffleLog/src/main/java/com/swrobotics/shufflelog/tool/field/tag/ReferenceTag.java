package com.swrobotics.shufflelog.tool.field.tag;

import com.swrobotics.shufflelog.math.Matrix4f;
import com.swrobotics.shufflelog.tool.field.GizmoTarget;

public final class ReferenceTag implements GizmoTarget {
    private final String name;
    private final double size;
    private Matrix4f transform; // Encodes position and rotation

    public ReferenceTag(String name, double size, Matrix4f transform) {
        this.name = name;
        this.size = size;
        this.transform = transform;
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

    @Override
    public void setTransform(Matrix4f transform) {
        this.transform = transform;
    }
}
