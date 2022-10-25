package com.swrobotics.shufflelog.tool.field;

import com.swrobotics.shufflelog.math.Matrix4f;

public final class ReferenceTag {
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

    public Matrix4f getTransform() {
        return transform;
    }
}
