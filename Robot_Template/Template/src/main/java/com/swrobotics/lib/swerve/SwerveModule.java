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

    private final double metersToRadians;

    // TODO: Tolerence and odometry
    
    /**
     * A single swerve module capable of both steering and driving.
     * TIP: Create a helper class to configure the modules.
     * @param driveMotor A motor to drive the module, already configured with an encoder.
     * @param steerMotor A motor to steer the module.
     * @param steerEncoder An absolute encoder to read the angle of the module.
     * @param position The position of the module in meters, relative to the center of rotation of the robot.
     * @param gearRatio The gear ratio of the module. 1:8 becomes 1/8.
     * @param wheelRadius The radius of wheel in meters.
     */
    public SwerveModule(Motor driveMotor, Motor steerMotor, AbsoluteEncoder steerEncoder, Vec2d position, double gearRatio, double wheelRadius) {
        this.driveMotor = driveMotor;
        this.driveEncoder = driveMotor.getEncoder();

        this.steerMotor = steerMotor;
        this.steerEncoder = steerEncoder;
        steerMotor.assignEncoder(steerEncoder);

        this.position = position;

        metersToRadians = 1.0 / (gearRatio * wheelRadius);
    }

    public Vec2d getPosition() {
        return position;
    }

    public void setState(SwerveModuleState desiredState) {


        // Normalize current angle to -90 to 90 degrees. (Fix optimization)
        double rawCurrentAngle = steerEncoder.getAngle().getCWDeg();
        double currentAngle = rawCurrentAngle;

        if (rawCurrentAngle < 90) {
            currentAngle = rawCurrentAngle;
        } else if (rawCurrentAngle > 270) {
            currentAngle = rawCurrentAngle - 360;
        } else if (rawCurrentAngle < 270) {
            currentAngle = rawCurrentAngle - 180;
        }

        Angle current = Angle.cwDeg(currentAngle);

        SwerveModuleState state = SwerveModuleState.optimize(desiredState, current.toRotation2dCW()); // Check angle measurement
        // Set steer angle
        steerMotor.angle(Angle.cwDeg(state.angle.getDegrees())); // Check direction

        // Set drive speed
        driveMotor.velocity(Angle.cwRad(state.speedMetersPerSecond * metersToRadians));

    }

    public SwerveModuleState getState() {
        double velocity = driveEncoder.getVelocity().getCWRad() / metersToRadians; // Check direction
        Angle angle = steerEncoder.getAngle();

        return new SwerveModuleState(velocity, angle.toRotation2dCW());
    }
}
