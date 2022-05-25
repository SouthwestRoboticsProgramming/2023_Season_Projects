package com.swrobotics.lib.swerve;

import com.swrobotics.lib.encoder.AbsoluteEncoder;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.motor.NewMotor;

public class SwerveDriveBuilder {

    private SwerveDriveSettings settings;

    private NewMotor[] driveMotors;
    private NewMotor[] steerMotors;
    private AbsoluteEncoder[] encoders;
    private Vec2d[] wheelPositions;

    public SwerveDriveBuilder() {
        settings = new SwerveDriveSettings();
    }

    public SwerveDriveBuilder setDriveMotors(NewMotor[] driveMotors) {
        // Do things so that they're usefull
        this.driveMotors = driveMotors;
        return this;
    }

    public SwerveDriveBuilder setSteerMotors(NewMotor[] steerMotors) {
        this.steerMotors = steerMotors;
        return this;
    }

    public SwerveDriveBuilder setEncoders(AbsoluteEncoder[] encoders) {
        this.encoders = encoders;
        return this;
    }

    public SwerveDriveBuilder setWheelPositions(Vec2d[] wheelPositions) {
        this.wheelPositions = wheelPositions;
        return this;
    }

    public SwerveDriveSettings build() {
        // Do things
        return null;
    }
}
