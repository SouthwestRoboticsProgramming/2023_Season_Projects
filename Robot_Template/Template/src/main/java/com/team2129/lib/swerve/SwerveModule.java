package com.team2129.lib.swerve;

import com.team2129.lib.motor.Motor;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.Vec2d;
import com.team2129.lib.encoder.AbsoluteEncoder;
import com.team2129.lib.encoder.Encoder;

import edu.wpi.first.math.kinematics.SwerveModuleState;

/**
 * A class to control a single swerve module with both a steer and drive motor, along with an encoder.
 */
public class SwerveModule {
    private final Motor driveMotor;
    private final Motor steerMotor;

    private final Encoder driveEncoder;
    private final AbsoluteEncoder steerEncoder;

    private final Vec2d position;

    private final double metersToRadians;

    private Angle tolerance;
    private SwerveModuleState currentDesiredState;
    
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
        steerMotor.setEncoder(steerEncoder);

        this.position = position;

        metersToRadians = 1.0 / (gearRatio * wheelRadius);

        tolerance = Angle.cwDeg(1);
        currentDesiredState = new SwerveModuleState();
    }

    /**
     * Set the angle that the wheel can be off and stop trying to adjust.
     * @param tolerance Angle tolerance of the module. (Default is 1 degree)
     */
    public void setTolerance(Angle tolerance) {
        this.tolerance = tolerance;
    }

    /**
     * Get the vector position of the swerve module.
     * @return The vector position of the module relative to the center of the robot.
     */
    public Vec2d getPosition() {
        return position;
    }

    /**
     * Get if the wheel is within the defined steering tolerance.
     * @return If the wheel is within tolerance.
     */
    public boolean inTolerance() {
        Angle desired = Angle.cwDeg(currentDesiredState.angle.getDegrees());
        Angle actual = steerEncoder.getAngle();
        Angle difference = desired.sub(actual);

        boolean normal = difference.lessThan(tolerance) && difference.greaterThan(tolerance.scaleBy(-1));
        boolean overAxis = difference.lessThan(tolerance.addCWDeg(180)) && difference.greaterThan(tolerance.scaleBy(-1).addCWDeg(180));

        return normal || overAxis;
    }

    /**
     * Set the desired state of the module.
     * @param desiredState Desired state of the module, as calculated by a kinematics object.
     */
    public void setState(SwerveModuleState desiredState) {
        Angle current = steerEncoder.getAngle();
        
        SwerveModuleState state = SwerveModuleState.optimize(desiredState, current.toRotation2dCW());
  
        if (!inTolerance()) {
            // Set steer angle
            steerMotor.position(() -> steerEncoder.getAngle().normalizeDeg(-180, 180), Angle.ccwDeg(state.angle.getDegrees()));
        }
        
        // Set drive speed
        driveMotor.velocity(Angle.cwRad(state.speedMetersPerSecond * metersToRadians));
        
        currentDesiredState = state;
    }

    /**
     * Get the real state of the module as reported by the sensors onboard.
     * @return The real state of the module
     */
    public SwerveModuleState getState() {
        double velocity = driveEncoder.getVelocity().getCWRad() / metersToRadians;
        Angle angle = steerEncoder.getAngle();

        return new SwerveModuleState(velocity, angle.toRotation2dCW());
    }

    /**
     * Get the real angle of the module without the offset applied. (Useful for calibration)
     * @return The angle of the module before the offset is applied.
     */
    public Angle getRawAngle() {
        return steerEncoder.getRawAngle();
    }
}
