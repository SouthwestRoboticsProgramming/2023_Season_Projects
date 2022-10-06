package com.swrobotics.robot.subsystem.climber;

import java.util.function.Supplier;

import com.team2129.lib.encoder.filters.JumpToZeroFilter;
import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.calc.PIDCalculator;
import com.team2129.lib.motor.rev.BrushlessSparkMaxMotor;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;

import edu.wpi.first.math.MathUtil;

public class RotatingArm implements Subsystem {

    private static final NTDouble TEST_ROTS = new NTDouble("Test/Test/Rotating_Test_Pos", 0);

    private static final NTDouble KP = new NTDouble("Climber/Rotating/kP", 0.01);
    private static final NTDouble KI = new NTDouble("Climber/Rotating/kI", 0.0);
    private static final NTDouble KD = new NTDouble("Climber/Rotating/kD", 0.0);

    private static final double ARM_LENGTH = 15.0;
    private static final double BASE_LENGTH = 6.76;
    private static final double ROTS_PER_INCH = 11.0;
    private static final double CALIBRATED_LENGTH = 12.38;

    private static final int MIN_ANGLE_CWDEG = 40;
    private static final int MAX_ANGLE_CWDEG = 120;

    private final BrushlessSparkMaxMotor motor;

    private final Supplier<Angle> currentAngle;

    private Angle targetAngle;
    
    public RotatingArm(int motorID) {
        motor = new BrushlessSparkMaxMotor(this, motorID);
        motor.setEncoderFilter(new JumpToZeroFilter());
        motor.setPositionCalculator(new PIDCalculator(KP, KI, KD));

        currentAngle = () -> getAngle();

        setTargetAngle(Angle.cwDeg(90));
    }

    public void setTargetAngle(Angle angle) {
        double cwDeg = MathUtil.clamp(angle.getCWDeg(), MIN_ANGLE_CWDEG, MAX_ANGLE_CWDEG);
        targetAngle = Angle.cwDeg(cwDeg);
    }

    public Angle getAngle() {
        // Get angle from encoder
        // double encoderRots = motor.getEncoder().getAngle().getCWRot();
        double encoderRots = TEST_ROTS.get();

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

    @Override
    public void periodic() {
        motor.position(currentAngle, targetAngle);
    }
    
}
