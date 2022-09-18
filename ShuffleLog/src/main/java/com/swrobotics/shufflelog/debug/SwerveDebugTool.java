package com.swrobotics.shufflelog.debug;

import com.swrobotics.shufflelog.tool.ViewportTool;
import com.swrobotics.shufflelog.util.Vec2d;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImBoolean;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

import static com.swrobotics.shufflelog.debug.Utils.*;
import static imgui.ImGui.*;

/**
 * Swerve drive simulator and debugger
 *
 * Not actually part of ShuffleLog, just put here since ShuffleLog
 * has nice utilities and ImGui
 */
public final class SwerveDebugTool extends ViewportTool {
    /*
     * m0 -- m1
     *  |    |
     * m2 -- m3
     */
    private static final double HALF_SIZE = 75;
    private static final SwerveModule[] MODULES = {
            new SwerveModule(-HALF_SIZE, HALF_SIZE),  // Module 0, Front left
            new SwerveModule(HALF_SIZE, HALF_SIZE),   // Module 1, Front right
            new SwerveModule(-HALF_SIZE, -HALF_SIZE), // Module 2, Back left
            new SwerveModule(HALF_SIZE, -HALF_SIZE)   // Module 3, Back right
    };
    private static final int[] CLOCKWISE_ORDER = {0, 1, 3, 2}; // For drawing perimeter box
    private static final Vec2d CENTER_OF_ROT = new Vec2d(0, 0);
    private static final Translation2d CENTER_OF_ROT_WPI = toWPIRobotCoords(CENTER_OF_ROT);
    private static final double MAX_WHEEL_VELOCITY = 100; // Meters per second

    // Speed per second
    private static final double DRIVE_SPEED = 50; // Meters
    private static final double TURN_SPEED = Math.PI / 6; // Radians

    private final SwerveDriveKinematics kinematics;
    private Pose2d odometryPose;

    private final ImBoolean showTarget, showWheel, showDrive;

    public SwerveDebugTool(PApplet app) {
        super(app, "Swerve Drive");

        Translation2d[] positions = new Translation2d[MODULES.length];
        for (int i = 0; i < MODULES.length; i++) {
            positions[i] = toWPIRobotCoords(MODULES[i].getPosition());
        }

        kinematics = new SwerveDriveKinematics(positions);
        odometryPose = new Pose2d();

        showTarget = new ImBoolean(true);
        showWheel = new ImBoolean(true);
        showDrive = new ImBoolean(true);
    }

    private void setTargetSpeeds(ChassisSpeeds speeds) {
        SwerveModuleState[] targetStates = kinematics.toSwerveModuleStates(speeds, CENTER_OF_ROT_WPI);
        SwerveDriveKinematics.desaturateWheelSpeeds(targetStates, MAX_WHEEL_VELOCITY);

        for (int i = 0; i < targetStates.length; i++) {
            MODULES[i].setTargetState(targetStates[i]);
        }
    }

    private boolean up, down, left, right, ccw, cw;

    @Override
    public void onKeyPress(char key, int keyCode) {
        switch (key) {
            case 'q': ccw = true; break;
            case 'w': up = true; break;
            case 'e': cw = true; break;
            case 'a': left = true; break;
            case 's': down = true; break;
            case 'd': right = true; break;
        }
    }

    @Override
    public void onKeyRelease(char key, int keyCode) {
        switch (key) {
            case 'q': ccw = false; break;
            case 'w': up = false; break;
            case 'e': cw = false; break;
            case 'a': left = false; break;
            case 's': down = false; break;
            case 'd': right = false; break;
        }
    }

    private void doPeriodic() {
        // Angles are CCW radians here

        double driveX = 0, driveY = 0, turn = 0;
        if (up) driveY += DRIVE_SPEED;
        if (down) driveY -= DRIVE_SPEED;
        if (left) driveX -= DRIVE_SPEED;
        if (right) driveX += DRIVE_SPEED;
        if (ccw) turn += TURN_SPEED;
        if (cw) turn -= TURN_SPEED;

        Rotation2d fakeGyro = odometryPose.getRotation();

        Translation2d wpiDrive = toWPIFieldCoords(new Vec2d(driveX, driveY));
        ChassisSpeeds speeds = ChassisSpeeds.fromFieldRelativeSpeeds(wpiDrive.getX(), wpiDrive.getY(), turn, fakeGyro);

        setTargetSpeeds(speeds);

        double delta = getIO().getDeltaTime();
        for (SwerveModule module : MODULES) {
            module.doPeriodic(delta);
        }

        SwerveModuleState[] currentStates = new SwerveModuleState[MODULES.length];
        for (int i = 0; i < MODULES.length; i++) {
            currentStates[i] = MODULES[i].getCurrentState();
        }

        // Modified odometry without gyro (odometry is used to estimate gyro behavior here)
        ChassisSpeeds simSpeeds = kinematics.toChassisSpeeds(currentStates);
        System.out.println(simSpeeds);
        odometryPose = odometryPose.exp(new Twist2d(
                simSpeeds.vxMetersPerSecond * delta,
                simSpeeds.vyMetersPerSecond * delta,
                simSpeeds.omegaRadiansPerSecond * delta)
        );
    }

    @Override
    protected void drawViewportContent(PGraphics g) {
        g.background(0);
        g.translate(g.width / 2f, g.height / 2f);
        g.scale(1, -1); // Invert Y axis to match field coordinates

        Vec2d fieldPos = toFieldCoords(odometryPose.getTranslation());
        double rot = odometryPose.getRotation().getRadians();

        // Transform into robot relative coords
        g.translate((float) fieldPos.x, (float) fieldPos.y);
        g.translate((float) CENTER_OF_ROT.x, (float) CENTER_OF_ROT.y);
        g.rotate((float) rot);
        g.translate((float) -CENTER_OF_ROT.x, (float) -CENTER_OF_ROT.y);

        // Border
        g.strokeWeight(1);
        g.stroke(255);
        g.beginShape(PConstants.LINE_LOOP);
        for (int i : CLOCKWISE_ORDER) {
            SwerveModule module = MODULES[i];
            Vec2d pos = module.getPosition();
            g.vertex((float) pos.x, (float) pos.y);
        }
        g.endShape();

        for (SwerveModule module : MODULES) {
            module.draw(g, showTarget.get(), showWheel.get(), showDrive.get());
        }
    }

    @Override
    protected void drawGuiContent() {
        doPeriodic();
        if (beginTable("layout", 2, ImGuiTableFlags.Resizable)) {
            tableNextColumn();
            drawViewport();
            tableNextColumn();
            text("WASD to drive, QE to turn");
            separator();
            checkbox("Show target (blue)", showTarget);
            checkbox("Show wheels (yellow)", showWheel);
            checkbox("Show drive (red)", showDrive);
            separator();
            Vec2d pos = toFieldCoords(odometryPose.getTranslation());
            text("Position: " + pos.x + ", " + pos.y);
            text("Rotation: " + odometryPose.getRotation().getDegrees() + " deg ccw");
            separator();
            for (int i = 0; i < MODULES.length; i++) {
                SwerveModule module = MODULES[i];
                text("Module " + i);
                indent();
                module.showGui();
                unindent();
            }
            endTable();
        }
    }
}
