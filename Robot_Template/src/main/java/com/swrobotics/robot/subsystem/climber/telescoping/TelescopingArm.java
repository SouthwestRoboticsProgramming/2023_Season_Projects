package com.swrobotics.robot.subsystem.climber.telescoping;

import java.util.function.Supplier;

import com.swrobotics.lib.encoder.filters.JumpToZeroFilter;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.motor.calc.BangBangCalculator;
import com.swrobotics.lib.motor.ctre.NeutralMode;
import com.swrobotics.lib.motor.rev.BrushlessSparkMaxMotor;
import com.swrobotics.lib.net.NTDouble;
import com.swrobotics.lib.schedule.Subsystem;
import com.swrobotics.lib.utils.TimeoutTimer;
import com.swrobotics.lib.wpilib.RobotState;
import com.swrobotics.robot.Robot;

// FIXME-MERGE: Ignore all changes in this file, I was just getting it to compile
public class TelescopingArm implements Subsystem {

    private static final NTDouble TEST_PERC = new NTDouble("Test/Test/Climber percussion", 0);

    private static final NTDouble LIFT_PERC_LOADED = new NTDouble("Climber/Tele/Loaded Lift Percent", 0.8); // Lift the robot, should be positive
    private static final NTDouble LIFT_PERC_UNLOADED = new NTDouble("Climber/Tele/Unloaded Lift Percent", 0.5); // Lift the robot, should be positive

    private static final NTDouble DROP_PERC_LOADED = new NTDouble("Climber/Tele/Loaded Drop Percent", -0.05); // Drop the robot
    private static final NTDouble DROP_PERC_UNLOADED = new NTDouble("Climber/Tele/Unloaded Drop Percent", -0.05); // Drop the robot

    // Feedforward to keep robot hanging at specified height
    private static final NTDouble FEED_FORWARD_LOADED = new NTDouble("Climber/Tele/Loaded Feedforward", 0.3);
    private static final NTDouble FEED_FORWARD_UNLOADED = new NTDouble("Climber/Tele/Unloaded Feedforward", 0.01);

    private static final NTDouble CALIBRATE_PERCENT = new NTDouble("Climber/Tele/Calibrate percent", 0.1);
    private static final NTDouble CALIBRATE_VELOCITY_TOLERANCE = new NTDouble("Climber/Tele/Calibrate Velocity Tolerance", 1); // Degrees per second
    private static final double CALIBRATE_TIMEOUT = 0.2;

    private static final double DEGREES_TO_MAX_HEIGHT = -24300;

    private final BangBangCalculator bangCalc;


    private final BrushlessSparkMaxMotor motor1;
    private final BrushlessSparkMaxMotor motor2;

//    private final BangBangCalculator bangCalc;

private final TimeoutTimer calibrateTimer;

    private final Supplier<Angle> currentHeight;

    private double targetHeight;
    private boolean underLoad;
    private boolean shouldBeCalibrating;

    /**
     * Create a new TelescopingArm. The motor should be configured so that a positive outputput will pull the arm down,
     * lifting the robot, and a negative output will let the arm up, lowering the robot.
     * @param canID1
     * @param canID2
     * @param inverted Invert the motors that that a positive demand will make the arm pull in.
     */
    public TelescopingArm(int canID1, int canID2, boolean inverted) {
        motor1 = new BrushlessSparkMaxMotor(this, canID1);
        motor2 = new BrushlessSparkMaxMotor(this, canID2);

        motor1.setInverted(inverted);
        motor2.setInverted(inverted);

        motor1.setNeutralMode(NeutralMode.BRAKE);
        motor2.setNeutralMode(NeutralMode.BRAKE);

        // motor1.setNeutralMode(NeutralMode.COAST);
        // motor2.setNeutralMode(NeutralMode.COAST);

        motor1.setEncoderFilter(new JumpToZeroFilter());
        motor2.setEncoderFilter(new JumpToZeroFilter());

        motor1.getEncoder().setAngle(Angle.zero());
        motor2.getEncoder().setAngle(Angle.zero());

        motor1.getEncoder().setInverted(!inverted);
        motor1.getEncoder().setInverted(!inverted);

        // Give both motors the same encoder
        motor2.setEncoder(motor1.getEncoder());

        bangCalc = new BangBangCalculator();
        bangCalc.setLowerThreshold(Angle.cwDeg(-5));
        bangCalc.setUpperThreshold(Angle.cwDeg(5));

        motor1.setPositionCalculator(bangCalc);
        motor2.setPositionCalculator(bangCalc);

        shouldBeCalibrating = true;
        calibrateTimer = new TimeoutTimer(CALIBRATE_TIMEOUT);

        currentHeight = () -> getHeight();
    }

    public void setCalibrating(boolean calibrate) {
        shouldBeCalibrating = calibrate;
    }

    public void setHeight(double percentOfMax, boolean underLoad) {
        targetHeight = 1 - percentOfMax; // Invert position so that 0 is all the way up.
        this.underLoad = underLoad;
    }

    public boolean inTolerance() {
        bangCalc.calculate(getHeight(), Angle.cwRot(targetHeight)); // Must occur due to lazy motor behavior
        return bangCalc.inTolerance();
    }

    private Angle getHeight() {
        return Angle.cwRot(1 - motor1.getEncoder().getAngle().getCWDeg() / DEGREES_TO_MAX_HEIGHT); // Inverted
    }

    /**
     * Check that it should be calibrating, update the {@code shouldBeCalibrating} variable, then calibrate if needed.
     */
    private void calibrate() {
        // If calibrating isn't requested, don't do it
        if (!shouldBeCalibrating) {
            calibrateTimer.stop();
            return;
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

        // Get leading edge of timer to zero encoders
        if (calibrateTimer.get() && shouldBeCalibrating) {
            shouldBeCalibrating = false;
            motor1.getEncoder().setAngle(Angle.zero());
            motor2.getEncoder().setAngle(Angle.zero());

            motor1.stop();
            motor2.stop();
        }

        // Decide, based on the timer, if should stop calibrating
        shouldBeCalibrating = !calibrateTimer.get();
        if (!shouldBeCalibrating) {
            calibrateTimer.reset();
            return;
        };

        motor1.percent(CALIBRATE_PERCENT.get());
        motor2.percent(CALIBRATE_PERCENT.get());
    }

    @Override
    public void periodic() {

        // motor1.percent(TEST_PERC.get());
        // motor2.percent(TEST_PERC.get());

        
        calibrate();
        if (shouldBeCalibrating) return;
        
        // Update bang bang with outputs determined by weight.
        if (underLoad) {
            bangCalc.setMinOutput(DROP_PERC_LOADED.get());
            bangCalc.setMultiplier(LIFT_PERC_LOADED.get());
        } else {
            bangCalc.setMinOutput(DROP_PERC_UNLOADED.get());
            bangCalc.setMultiplier(LIFT_PERC_UNLOADED.get());
        }
        
        /*
        * If the motor is demanded to pull the robot up, it will use
        * the multiplier.
        * 
        * If the motor is demanded to let the robot down, it will use the minOutput.
        */
        
        motor1.position(currentHeight, Angle.cwRot(targetHeight));
        motor2.position(currentHeight, Angle.cwRot(targetHeight));

        bangCalc.calculate(getHeight(), Angle.cwRot(targetHeight)); // Otherwise, motor is too lazy and skips this
        
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
        setHeight(0.5, false);
        shouldBeCalibrating = true;
    }

    @Override
    public void autonomousInit() {
        setHeight(0.25, false);
    }

    public String getTolInfo() {
        return String.format("(C %.2f T %.2f)", getHeight().getCWRot(), targetHeight);
    }
}
