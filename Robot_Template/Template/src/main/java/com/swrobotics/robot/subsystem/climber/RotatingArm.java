package com.swrobotics.robot.subsystem.climber;

import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.rev.BrushedSparkMaxMotor;
import com.team2129.lib.schedule.Subsystem;

import edu.wpi.first.math.MathUtil;

public class RotatingArm implements Subsystem {

    private static final double ARM_LENGTH = 15.0;
    private static final double BASE_LENGTH = 6.76;
    private static final double ROTS_PER_INCH = 11.0;
    private static final double CALIBRATED_LENGTH = 12.38;

    private static final int MIN_ANGLE_CWDEG = 40;
    private static final int MAX_ANGLE_CWDEG = 120;

    private final BrushedSparkMaxMotor motor;

    private Angle targetAngle;
    
    public RotatingArm(int motorID) {
        motor = new BrushedSparkMaxMotor(this, motorID);
        // TODO: Encoder filter
    }

    public void setTargetAngle(Angle angle) {
        double cwDeg = MathUtil.clamp(angle.getCWDeg(), MIN_ANGLE_CWDEG, MAX_ANGLE_CWDEG);
        targetAngle = Angle.cwDeg(cwDeg);
    }

    public Angle getAngle() {
        // Get angle from encoder
        double encoderRots = motor.getEncoder().getAngle().getCWRot();

        // Calculate length of Igus Shaft between pivot points
        double currentIgusLength = encoderRots / ROTS_PER_INCH + CALIBRATED_LENGTH;

        // Calculate angle using law of cosines
        // Angle = (Base^2 + Arm^2 - Igus^2) / (2*Arm*Base)
        double cwRad = Math.acos((BASE_LENGTH*BASE_LENGTH +
                                  ARM_LENGTH*ARM_LENGTH -
                                  currentIgusLength*currentIgusLength) /
                                  (2 * ARM_LENGTH * BASE_LENGTH)
        );

        return Angle.cwRad(cwRad);
    }


    
}
