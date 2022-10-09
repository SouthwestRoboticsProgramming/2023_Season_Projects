package com.swrobotics.robot.subsystem.climber.rotating;

import java.util.function.Supplier;

import com.swrobotics.robot.subsystem.climber.Calibrator;
import com.team2129.lib.encoder.filters.JumpToZeroFilter;
import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.calc.PIDCalculator;
import com.team2129.lib.motor.rev.BrushlessSparkMaxMotor;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Scheduler;
import com.team2129.lib.schedule.Subsystem;

import edu.wpi.first.math.MathUtil;

public class RotatingArm implements Subsystem {

    private static final NTDouble TEST_ROTS = new NTDouble("Test/Test/Rotating_Test_Pos", 0);
    private static final NTDouble TEST_TARGET = new NTDouble("Test/Test/Rotating_Test_Target", 90);

    private static final NTDouble KP = new NTDouble("Climber/Rotating/kP", 0.0001);
    private static final NTDouble KI = new NTDouble("Climber/Rotating/kI", 0.0);
    private static final NTDouble KD = new NTDouble("Climber/Rotating/kD", 0.0);

    private static final NTDouble TOLERANCE = new NTDouble("Climber/Rotating/Tolerance Deg", 5);

    private static final NTDouble CALIBRATE_PERCENT = new NTDouble("Climber/Rotating/Calibration Percent", -0.1);
    private static final NTDouble CALIBRATE_VELOCITY_TOLERANCE = new NTDouble("Climber/Rotating/Calibration Velocity Tolerence", 1); // Degrees per second
    private static final double CALIBRATE_TIMEOUT = 0.2;

    private static final double ARM_LENGTH = 15.0;
    private static final double BASE_LENGTH = 6.76;
    private static final double ROTS_PER_INCH = 11.0;
    private static final double CALIBRATED_LENGTH = 12.38;

    private static final int MIN_ANGLE_CWDEG = 40;
    private static final int MAX_ANGLE_CWDEG = 120;

    private final BrushlessSparkMaxMotor motor;

    private final Supplier<Angle> currentAngle;

    private final Calibrator calibrator;

    private Angle targetAngle;
    
    public RotatingArm(int motorID) {
        motor = new BrushlessSparkMaxMotor(this, motorID);
        motor.setEncoderFilter(new JumpToZeroFilter());
        motor.setPositionCalculator(new PIDCalculator(KP, KI, KD));

        currentAngle = () -> getAngle();

        setTargetAngle(Angle.cwDeg(90));

        calibrator = new Calibrator(Angle.cwDeg(CALIBRATE_VELOCITY_TOLERANCE.get()),
        CALIBRATE_PERCENT.get(),
        CALIBRATE_TIMEOUT, motor.getEncoder(),
        motor);
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

    public boolean inTolerance() {
        double degDiff = Math.abs(targetAngle.getCWDeg() - getAngle().getCWDeg());
        
        return degDiff < TOLERANCE.get();
    }

    @Override
    public void teleopInit() {
        Scheduler.get().addCommand();
    }

    @Override
    public void periodic() {

        // FIXME: Wait until done calibrating to start moving
        targetAngle = Angle.cwDeg(TEST_TARGET.get());
        // System.out.println("Mot: " + motor.getEncoder().getAngle().getCWDeg());
        // System.out.println("Current: "+ getAngle().getCWDeg() + " Tol: " + inTolerance());
        motor.position(currentAngle, targetAngle);
        // motor.percent(0.1);
    }
    
}
