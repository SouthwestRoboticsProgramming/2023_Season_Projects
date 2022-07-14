package com.swrobotics.lib.swerve;

import com.swrobotics.lib.encoder.AbsoluteEncoder;
import com.swrobotics.lib.encoder.Encoder;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.motor.Motor;

import edu.wpi.first.math.kinematics.SwerveModuleState;

public class SwerveModule {

    private final Motor driveMotor;
    private final Motor steerMotor;

    private final Encoder driveEncoder;
    private final AbsoluteEncoder steerEncoder;

    private final Vec2d position;

    // TODO: Tolerence and odometry
    
    /**
     * A single swerve module capable of both steering and driving.
     * @param driveMotor A motor to drive the module, already configured with an encoder.
     * @param steerMotor A motor to steer the module.
     * @param steerEncoder An absolute encoder to read the angle of the module.
     * @param position The position of the module in meters, relative to the center of rotation of the robot.
     */
    public SwerveModule(Motor driveMotor, Motor steerMotor, AbsoluteEncoder steerEncoder, Vec2d position) {
        this.driveMotor = driveMotor;
        this.driveEncoder = driveMotor.getEncoder();

        this.steerMotor = steerMotor;
        this.steerEncoder = steerEncoder;
        steerMotor.assignEncoder(steerEncoder);

        this.position = position;
    }

    public Vec2d getPosition() {
        return position;
    }

    public void setState(SwerveModuleState desiredState) {
        SwerveModuleState.optimize(desiredState, steerEncoder.getAngle().toRotation2dCW()); // Check angle measurement
        // Set steer angle
        steerMotor.angle(Angle.cwDeg(desiredState.angle.getDegrees())); // Check direction

        // Set drive speed
        driveMotor.velocity(Angle.cwDeg(desiredState.speedMetersPerSecond * 360 )); // TODO: Velocity to motor conversion
    }

    public SwerveModuleState getState() {
        double velocity = driveEncoder.getVelocity().getCWDeg(); // Check direction, TODO: Motor to velocity conversion
        Angle angle = steerEncoder.getAngle();

        return new SwerveModuleState(velocity, angle.toRotation2dCW());
    }
}
