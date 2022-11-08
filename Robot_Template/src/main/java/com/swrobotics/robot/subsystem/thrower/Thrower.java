package com.swrobotics.robot.subsystem.thrower;

import java.util.TreeMap;
import java.util.Map.Entry;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.MathUtil;
import com.swrobotics.lib.net.NTBoolean;
import com.swrobotics.lib.net.NTDouble;
import com.swrobotics.lib.schedule.Scheduler;
import com.swrobotics.lib.schedule.Subsystem;
import com.swrobotics.lib.wpilib.RobotState;
import com.swrobotics.robot.Robot;
import com.swrobotics.robot.control.Input;
import com.swrobotics.robot.subsystem.Localization;
import com.swrobotics.robot.subsystem.thrower.commands.ShootCommand;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

/*
 * For my memory:
 * Have an enum with 3 different settings
 * 1. Default (mura tuning)
 * 2. Last match tuning
 * 3. 2 matches ago tuning (for if last match was a fluke)
 * 
 * These 3 sets of settings are in networktables / messanger?
 * Keep default tuning in a file for if networktables won't connect
 * Read data from networktables and put those values into the tree map
 */

public class Thrower implements Subsystem {
    private static final double MIN_HIGH_HUB_DISTANCE = 0.305;
    private static final double MAX_HIGH_HUB_DISTANCE = 18 * 0.305;

    private static final double MIN_LOW_HUB_DISTANCE = 0;  // Guess
    private static final double MAX_LOW_HUB_DISTANCE = 10 * 0.305; // Guess

    private static final NTDouble FLYWHEEL_SHUTOFF_SECONDS = new NTDouble("Thrower/Flywheel/Shutoff_Time", 1.0);
    private static final NTBoolean STRICT_AIM = new NTBoolean("Thrower/Strict_Aim", false);
    private static final NTDouble TEST_DISTANCE = new NTDouble("Test/Test/Thrower Distance", 0);

    // private static final NTEnum<ThrowerTuneSelector> TUNE_SELECTOR = new NTEnum<>("Thrower/Tuning/Tune_Select", ThrowerTuneSelector.class, ThrowerTuneSelector.DEFAULT);

    private final Input input;
    private final Hopper hopper;
    private final Hood hood;
    private final Flywheel flywheel;
    private final Localization loc;

    private final Timer flywheelShutoff;

    private final TreeMap<Double, Double> highHubMap;
    private final TreeMap<Double, Double> lowHubMap;

    private boolean isClimbing;

    public Thrower(Input input, Localization loc) {
        highHubMap = new TreeMap<Double, Double>();
        lowHubMap = new TreeMap<Double, Double>();

        this.input = input;

        BallDetector ballDetector = new BallDetector();
        hopper = new Hopper(ballDetector);
        hood = new Hood();
        flywheel = new Flywheel();
        this.loc = loc;

        Scheduler sch = Scheduler.get();
        sch.addSubsystem(this, ballDetector);
        sch.addSubsystem(this, hopper);
        sch.addSubsystem(this, hood);
        sch.addSubsystem(this, flywheel);

        flywheelShutoff = new Timer();
        isClimbing = false;

        // Temporary copied from old constants (no this is wrong not anymore)
//        highHubMap.put(3.46, 50.4);
//        highHubMap.put(1.92, 35.1);
//        highHubMap.put(4.47, 65.0);
//        highHubMap.put(2.97, 45.0);
        double scale = 0.92;
        highHubMap.put(3.75, 40.3 * scale);
        highHubMap.put(3.01, 36.3 * scale);
        highHubMap.put(2.04, 30.5 * scale);
        highHubMap.put(4.57, 49.8 * scale);
    }

    public Hopper getHopper() {
        return hopper;
    }

    private double[] calculateAim(double distance, boolean aimHighHub, boolean forceHubChoice) {
        double rpm;
        double hood;

        TreeMap<Double, Double> map;
        if (aimHighHub) {
            map = highHubMap;
        } else {
            map = lowHubMap;
        }

        if (lowHubMap.isEmpty() && aimHighHub) {forceHubChoice = true;}
        if (highHubMap.isEmpty() && !aimHighHub) {forceHubChoice = true;}

        // Check that there are values in the map
        if (map.isEmpty()) {
            if (forceHubChoice) {
                DriverStation.reportError("No values in thrower tuning maps", true);
                return new double[]{0,0};
            } else {
                return calculateAim(distance, !aimHighHub, true);
            }
        }

        Entry<Double, Double> lowerEntry = map.floorEntry(distance);
        Entry<Double, Double> upperEntry = map.ceilingEntry(distance);

        if (lowerEntry == null) { // Too close to hub
            if (aimHighHub && !forceHubChoice) {
                return calculateAim(distance, !aimHighHub, true);
            } else {
                rpm = map.firstEntry().getValue();
            }
        } else if (upperEntry == null) { // Further than last tuning
            if (!aimHighHub && !forceHubChoice) { // Aim high
                return calculateAim(distance, aimHighHub, true);
            }

            // If distance is beyond last key, approximate using slope of entire map
            double rise = map.firstEntry().getValue() - map.lastEntry().getValue();
            double run = map.firstKey() - map.lastKey();
            double slope = rise / run;

            rpm = map.lastEntry().getValue() + slope * (distance - map.lastKey());
        } else {
            // If within the map, linearly interpolate
            rpm = MathUtil.map(distance, lowerEntry.getKey(), upperEntry.getKey(), lowerEntry.getValue(), upperEntry.getValue());
        }
        
        /* Calculate hood angle */
        if (aimHighHub) {
            hood = MathUtil.clamp(MathUtil.map(distance, MIN_HIGH_HUB_DISTANCE, MAX_HIGH_HUB_DISTANCE, 0, 1), 0, 1);
            if (distance < 10.0 * 0.305) {hood -= 0.15;}
        } else {
            hood = MathUtil.clamp(MathUtil.map(distance, MIN_LOW_HUB_DISTANCE, MAX_LOW_HUB_DISTANCE, 0, 1), 0, 1);
        }

        return new double[]{rpm, hood};
    }

    public void setClimbing(boolean isClimbing) {
        this.isClimbing = isClimbing;
    }

    private static final NTDouble TEST_VEL = new NTDouble("Test/Shooter velocity", 0);
    static {
        TEST_VEL.setTemporary();
    }

    @Override
    public void periodic() {
//        flywheel.setFlywheelVelocity(Angle.cwRot(TEST_VEL.get()));

         double distance = loc.getMetersToHub(); //FIXME
        // double distance = TEST_DISTANCE.get();

         if (isClimbing) {
             hood.calibrate();
             flywheel.stop();
             return;
         }

         if (hopper.isBallGone()) {
             flywheelShutoff.reset();
             flywheelShutoff.start();
         }

//        double[] aim = calculateAim(loc.getFeetToHub(), true, false);
//        hood.setPosition(aim[1]);

        // System.out.println("DIstance: " + distance);

         if ((hopper.isBallDetected() || /*!flywheelShutoff.hasElapsed(FLYWHEEL_SHUTOFF_SECONDS.get())*/false) && loc.isLimelightAccurate()) {
                 double[] aim = calculateAim(distance, true, STRICT_AIM.get());
                 flywheel.setFlywheelVelocity(Angle.cwRot(aim[0])); // Convert rpm to Angle/second // TODO: Check functionality
//             flywheel.setFlywheelVelocity(Angle.cwRot(TEST_VEL.get()));
             hood.setPosition(aim[1]);
                 // System.out.println("Distance: " + distance + " Hood: " + aim[1] + " Flywheel: " + aim[0]);
         } else { // If no ball for the duration of the timer
             hood.calibrate();
             flywheel.stop();
         }

        if (input.getShoot() && Robot.get().getCurrentState() == RobotState.TELEOP) {
            Scheduler.get().addCommand(new ShootCommand(hopper));
        }
    }
}
