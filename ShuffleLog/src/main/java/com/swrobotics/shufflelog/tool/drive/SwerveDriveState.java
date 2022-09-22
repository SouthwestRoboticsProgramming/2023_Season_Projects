package com.swrobotics.shufflelog.tool.drive;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.shufflelog.util.Vec2d;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.Arrays;

public final class SwerveDriveState {
    private final SwerveModuleState[] modules;

    public SwerveDriveState(MessageReader reader) {
        int count = reader.readInt();
        modules = new SwerveModuleState[count];
        for (int i = 0; i < count; i++) {
            modules[i] = new SwerveModuleState(reader);
        }

        // Sort modules into ccw order for border rendering
        Arrays.sort(modules);
    }

    public void draw(PGraphics g, float strokeMul, boolean target, boolean wheel, boolean drive) {
        g.strokeWeight(1 * strokeMul);
        g.stroke(255);
        g.beginShape(PConstants.LINE_LOOP);
        for (SwerveModuleState module : modules) {
            Vec2d pos = module.getPosition();
            g.vertex((float) pos.x, (float) pos.y);
        }
        g.endShape();

        for (SwerveModuleState module : modules) {
            module.draw(g, strokeMul, target, wheel, drive);
        }
    }
}
