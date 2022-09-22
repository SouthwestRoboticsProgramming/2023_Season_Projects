package com.swrobotics.shufflelog.tool.drive;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.shufflelog.util.Vec2d;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.Arrays;

import static imgui.ImGui.*;

public final class SwerveDriveState {
    private final SwerveModuleState[] modules;
    private float minX, minY;
    private float maxX, maxY;

    public SwerveDriveState(MessageReader reader) {
        int count = reader.readInt();
        modules = new SwerveModuleState[count];
        for (int i = 0; i < count; i++) {
            modules[i] = new SwerveModuleState(reader);
        }

        // Sort modules into ccw order for border rendering
        Arrays.sort(modules);

        // Find bounding box for rendering zoom calculation
        minX = Float.POSITIVE_INFINITY;
        minY = Float.POSITIVE_INFINITY;
        maxX = Float.NEGATIVE_INFINITY;
        maxY = Float.NEGATIVE_INFINITY;
        for (SwerveModuleState state : modules) {
            Vec2d pos = state.getPosition();
            minX = (float) Math.min(minX, pos.x);
            minY = (float) Math.min(minY, pos.y);
            maxX = (float) Math.max(maxX, pos.x);
            maxY = (float) Math.max(maxY, pos.y);
        }
    }

    public void readState(MessageReader reader) {
        for (SwerveModuleState state : modules) {
            state.readState(reader);
        }
    }

    public void draw(PGraphics g, float strokeMul, float driveScale, boolean target, boolean wheel, boolean drive) {
        g.strokeWeight(1 * strokeMul);
        g.stroke(255);
        g.beginShape(PConstants.LINE_LOOP);
        for (SwerveModuleState module : modules) {
            Vec2d pos = module.getPosition();
            g.vertex((float) pos.x, (float) pos.y);
        }
        g.endShape();

        for (SwerveModuleState module : modules) {
            module.draw(g, strokeMul, driveScale, target, wheel, drive);
        }
    }

    public void showGui() {
        textWrapped("Note: Module info is in WPI robot-relative coordinate space");
        separator();
        for (int i = 0; i < modules.length; i++) {
            text("Module " + i);
            indent();
            modules[i].showGui();
            unindent();
        }
    }

    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }
}
