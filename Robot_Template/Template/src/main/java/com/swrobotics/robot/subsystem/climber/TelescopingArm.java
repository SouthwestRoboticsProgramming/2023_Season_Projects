package com.swrobotics.robot.subsystem.climber;

import com.swrobotics.robot.Robot;
import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.calc.BangBangCalculator;
import com.team2129.lib.motor.ctre.NeutralMode;
import com.team2129.lib.motor.rev.BrushlessSparkMaxMotor;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;
import com.team2129.lib.utils.TimeoutTimer;
import com.team2129.lib.wpilib.RobotState;

import edu.wpi.first.wpilibj.Timer;

public class TelescopingArm implements Subsystem {
    private static final NTDouble LIFT_PERC_LOADED = new NTDouble("Climber/Tele/Loaded Lift Percent", -0.8);
    private static final NTDouble LIFT_PERC_UNLOADED = new NTDouble("Climber/Tele/Unloaded Lift Percent", -0.5);

    private static final NTDouble DROP_PERC_LOADED = new NTDouble("Climber/Tele/Loaded Drop Percent", 0.05);
    private static final NTDouble DROP_PERC_UNLOADED = new NTDouble("Climber/Tele/Unloaded Drop Percent", 0.05);

    // Feedforward to keep robot hanging at specified height
    private static final NTDouble FEED_FORWARD_LOADED = new NTDouble("Climber/Tele/Loaded Feedforward", -0.3);
    private static final NTDouble FEED_FORWARD_UNLOADED = new NTDouble("Climber/Tele/Unloaded Feedforward", -0.01);

    private static final NTDouble CALIBRATE_PERCENT = new NTDouble("Climber/Tele/Calibrate percent", -0.1);
    private static final NTDouble CALIBRATE_VELOCITY_TOLERANCE = new NTDouble("Climber/Tele/Calibrate Velocity Tolerance", 1);
    private static final double CALIBRATE_TIMEOUT = 0.2;

    private static final double DEGREES_TO_MAX_HEIGHT = 60;


    private final BrushlessSparkMaxMotor motor1;
    private final BrushlessSparkMaxMotor motor2;

    private final BangBangCalculator bangCalc;

    private final TimeoutTimer calibrateTimer;

    private double targetHeight;
    private boolean underLoad;
    private boolean shouldBeCalibrating;

    public TelescopingArm(int canID1, int canID2, boolean inverted, String name) {
        motor1 = new BrushlessSparkMaxMotor(this, canID1);
        motor2 = new BrushlessSparkMaxMotor(this, canID2);

        motor1.setInverted(inverted);
        motor2.setInverted(inverted);

        motor1.setNeutralMode(NeutralMode.BRAKE);
        motor2.setNeutralMode(NeutralMode.BRAKE);

        // motor1.setNeutralMode(NeutralMode.COAST);
        // motor2.setNeutralMode(NeutralMode.COAST);

        motor1.getEncoder().setAngle(Angle.zero());
        motor2.getEncoder().setAngle(Angle.zero());

        motor1.getEncoder().setInverted(false);
        motor1.getEncoder().setInverted(false);

        // Give both motors the same encoder
        motor2.setEncoder(motor1.getEncoder());

        bangCalc = new BangBangCalculator();
        bangCalc.setLowerThreshold(Angle.cwDeg(-5));
        bangCalc.setUpperThreshold(Angle.cwDeg(5));
        bangCalc.inputInverted(true);

        motor1.setPositionCalculator(bangCalc);
        motor2.setPositionCalculator(bangCalc);

        shouldBeCalibrating = true;
        calibrateTimer = new TimeoutTimer(CALIBRATE_TIMEOUT);
    }

    public void setCalibrating(boolean calibrate) {
        shouldBeCalibrating = calibrate;
    }

    public void setHeight(double percentOfMax, boolean underLoad) {
        targetHeight = percentOfMax;
        this.underLoad = underLoad;
    }

    private boolean shouldCalibrate() {
        // If calibrating isn't requested, don't do it
            if (!shouldBeCalibrating) {
                calibrateTimer.stop();
                return false;
            }
    
            // Read current velocity
            double currentVelocity = Math.abs(motor1.getEncoder().getVelocity().getCWDeg());
            RobotState state = Robot.get().getCurrentState();

            // Start timer if it isn't moving
            if (currentVelocity < CALIBRATE_VELOCITY_TOLERANCE.get() && state == RobotState.TELEOP) {
                calibrateTimer.start(false);
            } else {
                calibrateTimer.stop();
            }
    
            // Decide, based on the timer, if should stop calibrating
            return !calibrateTimer.get();
        }

    private void calibrate() {
        motor1.percent(CALIBRATE_PERCENT.get());
        motor2.percent(CALIBRATE_PERCENT.get());
    }

    @Override
    public void periodic() {

        // Check if it is calibrating and if it should finish
        boolean calibrated = !shouldCalibrate();

        // Get leading edge to calibrate position
        if (calibrated && shouldBeCalibrating) {
            motor1.getEncoder().setAngle(Angle.zero());
        }

        shouldBeCalibrating = !calibrated;

        // Update bang bang with outputs determined by weight.
        if (underLoad) {
            bangCalc.setMinOutput(DROP_PERC_LOADED.get());
            bangCalc.setMultiplier(LIFT_PERC_LOADED.get());
        } else {
            bangCalc.setMinOutput(DROP_PERC_UNLOADED.get());
            bangCalc.setMultiplier(LIFT_PERC_UNLOADED.get());
        }

        // Calibrate by spinning motors
        if (shouldBeCalibrating) {
            calibrate();
            return;
        } else {
            motor1.stop();
            motor2.stop();
        }

        /*
         * If the motor is demanded to pull the robot up, it will use
         * the multiplier.
         * 
         * If the motor is demanded to let the robot down, it will use the minOutput.
         */

        System.out.println(motor1.getEncoder().getAngle().getCWDeg());

        motor1.position(Angle.cwRot(targetHeight * DEGREES_TO_MAX_HEIGHT));
        motor2.position(Angle.cwRot(targetHeight * DEGREES_TO_MAX_HEIGHT));

        if (bangCalc.inTolerance() && underLoad) {
            motor1.percent(FEED_FORWARD_LOADED.get());
            motor2.percent(FEED_FORWARD_LOADED.get());
        }

        if (bangCalc.inTolerance() && !underLoad) {
            motor1.percent(FEED_FORWARD_UNLOADED.get());
            motor2.percent(FEED_FORWARD_UNLOADED.get());
        }
    }

    @Override
    public void teleopInit() {
        setHeight(0, false);
        shouldCalibrate();
    }

    @Override
    public void autonomousInit() {
        setHeight(0.25, false);
    }
}
