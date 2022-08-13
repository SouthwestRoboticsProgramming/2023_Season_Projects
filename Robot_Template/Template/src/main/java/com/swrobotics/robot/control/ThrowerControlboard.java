package com.swrobotics.robot.control;

import java.util.TreeMap;
import java.util.Map.Entry;

import com.team2129.lib.math.MathUtil;
import com.team2129.lib.net.NTDouble;

import edu.wpi.first.wpilibj.DriverStation;

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

public class ThrowerControlboard {
    private static final int MIN_HIGH_HUB_DISTANCE = 1;
    private static final int MAX_HIGH_HUB_DISTANCE = 18;

    private static final int MIN_LOW_HUB_DISTANCE = 0;  // Guess
    private static final int MAX_LOW_HUB_DISTANCE = 10; // Guess

    private final TreeMap<Double, Double> highHubMap;
    private final TreeMap<Double, Double> lowHubMap;


    public ThrowerControlboard() {
        highHubMap = new TreeMap<Double, Double>();
        lowHubMap = new TreeMap<Double, Double>();
    }

    private double[] calculateAim(double distance, boolean aimHighHub, boolean forceHubChoice) {
        double rpm;
        double hood;

        TreeMap<Double, Double> map;
        if (aimHighHub) {
            map = highHubMap; // Should I use clone?
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
        }

        if (upperEntry == null) { // Further than last tuning
            if (!aimHighHub && !forceHubChoice) { // Aim high
                return calculateAim(distance, aimHighHub, true);
            }

            // If distance is beyond last key, approximate using slope of entire map
            double rise = map.firstEntry().getValue() - map.lastEntry().getValue();
            double run = map.firstKey() - map.lastKey();
            double slope = rise / run;

            rpm = map.lastEntry().getValue() + slope * (distance - map.lastKey());
        }

        // If within the map, linearly interpolate
        rpm = MathUtil.map(distance, lowerEntry.getKey(), upperEntry.getKey(), lowerEntry.getValue(), upperEntry.getValue());

        /* Calculate hood angle */
        if (aimHighHub) {
            hood = MathUtil.clamp(MathUtil.map(distance, MIN_HIGH_HUB_DISTANCE, MAX_HIGH_HUB_DISTANCE, 0, 1), 0, 1);
            if (distance < 10.0) {hood -= 0.15;}
        } else {
            hood = MathUtil.clamp(MathUtil.map(distance, MIN_LOW_HUB_DISTANCE, MAX_LOW_HUB_DISTANCE, 0, 1), 0, 1);
        }

        return new double[]{rpm, hood};
    }

    private double[] calculateAim(double distance, boolean aimHighHub) {
        return calculateAim(distance, aimHighHub, false);
    }
}
