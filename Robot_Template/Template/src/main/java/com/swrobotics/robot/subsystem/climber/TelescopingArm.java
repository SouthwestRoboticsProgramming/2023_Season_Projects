package com.swrobotics.robot.subsystem.climber;

import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.calc.BangBangCalculator;
import com.team2129.lib.motor.ctre.NeutralMode;
import com.team2129.lib.motor.rev.BrushlessSparkMaxMotor;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;

public class TelescopingArm implements Subsystem {
    private static final NTDouble LIFT_PERC_LOADED = new NTDouble("Climber/Tele/Loaded Lift Percent", 0.8);
    private static final NTDouble LIFT_PERC_UNLOADED = new NTDouble("Climber/Tele/Unloaded Lift Percent", 0.5);

    private static final NTDouble DROP_PERC_LOADED = new NTDouble("Climber/Tele/Loaded Drop Percent", -0.05);
    private static final NTDouble DROP_PERC_UNLOADED = new NTDouble("Climber/Tele/Loaded Drop Percent", -0.1);

    // Feedforward to keep robot hanging at specified height
    private static final NTDouble FEED_FORWARD_LOADED = new NTDouble("Climber/Tele/Loaded Feedforward", 0.5);

    private static final NTDouble CALIBRATE_PERCENT = new NTDouble("Climber/Tele/Calibrate percent", -0.1);
    private static final NTDouble CALIBRATE_VELOCITY_TOLERANCE = new NTDouble("Climber/Tele/Calibrate Velocity Tolerance", 2);

    private static final double DEGREES_TO_MAX_HEIGHT = 60;


    private final BrushlessSparkMaxMotor motor1;
    private final BrushlessSparkMaxMotor motor2;

    private final BangBangCalculator bangCalc;

    private double targetHeight;
    private boolean underLoad;
    private boolean isCalibrating;

    public TelescopingArm(int canID1, int canID2, boolean inverted, String name) {
        motor1 = new BrushlessSparkMaxMotor(this, canID1);
        motor2 = new BrushlessSparkMaxMotor(this, canID2);

        motor1.setInverted(inverted);
        motor2.setInverted(inverted);

        motor1.setNeutralMode(NeutralMode.BRAKE);
        motor2.setNeutralMode(NeutralMode.BRAKE);

        motor1.getEncoder().setAngle(Angle.zero());
        motor2.getEncoder().setAngle(Angle.zero());

        // Give both motors the same encoder
        motor2.setEncoder(motor1.getEncoder());

        bangCalc = new BangBangCalculator();
        bangCalc.setLowerThreshold(Angle.cwDeg(-5));
        bangCalc.setUpperThreshold(Angle.cwDeg(5));
    }

    public void setHeight(double percentOfMax, boolean underLoad) {
        targetHeight = percentOfMax;
        this.underLoad = underLoad;
    }


    @Override
    public void periodic() {

        // Update bang bang with outputs determined by weight.
        if (underLoad) {
            bangCalc.setMinOutput(DROP_PERC_LOADED.get());
            bangCalc.setMultiplier(LIFT_PERC_LOADED.get());
        } else {
            bangCalc.setMinOutput(DROP_PERC_UNLOADED.get());
            bangCalc.setMultiplier(LIFT_PERC_UNLOADED.get());
        }

        // Calibrate through percent out until it is not longer moving
        if (isCalibrating) {
            motor1.percent(CALIBRATE_PERCENT.get());
            motor2.percent(CALIBRATE_PERCENT.get());

            if (Math.abs(motor1.getEncoder().getVelocity().getCWDeg()) < CALIBRATE_VELOCITY_TOLERANCE.get()) {
                isCalibrating = false;
            }

            return;
        }

        /*
         * If the motor is demanded to pull the robot up, it will use
         * the multiplier.
         * 
         * If the motor is demanded to let the robot down, it will use the minOutput.
         */

        motor1.position(Angle.cwRot(targetHeight * DEGREES_TO_MAX_HEIGHT));
        motor2.position(Angle.cwRot(targetHeight * DEGREES_TO_MAX_HEIGHT));

        if (bangCalc.inTolerance()) {
            motor1.percent(FEED_FORWARD_LOADED.get());
            motor2.percent(FEED_FORWARD_LOADED.get());
        }
    }
}
