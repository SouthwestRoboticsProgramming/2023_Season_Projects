package com.swrobotics.robot.control;

import java.util.TreeMap;
import java.util.Map.Entry;

import com.team2129.lib.math.MathUtil;

import edu.wpi.first.wpilibj.DriverStation;

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

    private double calculateHood(double distance, boolean aimHighHub) {
        double hood;
        if (aimHighHub) {
            double raw = MathUtil.map(distance, MIN_HIGH_HUB_DISTANCE, MAX_HIGH_HUB_DISTANCE, 0, 1);
            hood = MathUtil.clamp(raw, 0, 1);

            if (distance < 10) { hood -= 0.5;}
        } else {
            double raw = MathUtil.map(distance, MIN_LOW_HUB_DISTANCE, MAX_LOW_HUB_DISTANCE, 0, 1);
            hood = MathUtil.clamp(raw, 0, 1);
        }

        return hood;
    }

    private double calculateRPM(double distance, boolean aimHighHub) {
        Entry<Double, Double> lowerEntry;
        Entry<Double, Double> upperEntry;

        // Extract values
        if (aimHighHub) {
            lowerEntry = highHubMap.ceilingEntry(distance);
            upperEntry = highHubMap.ceilingEntry(distance);
        } else {
            lowerEntry = lowHubMap.ceilingEntry(distance);
            upperEntry = lowHubMap.ceilingEntry(distance);
        }

        // Check that there are values in the map
        if (lowerEntry == null && upperEntry == null) {
            DriverStation.reportError("No tuning values for throwing HIGH HUB: " + aimHighHub, true);
            return 0; // TODO: Shoot at other hub without creating loop
        }

        if (lowerEntry == null) { // Too close to hub
            if (aimHighHub) {
                return calculateRPM(distance, false); // If too close to shoot high, shoot low
            } else {
                return lowHubMap.firstEntry().getValue();
            }
        }

        if (upperEntry == null) { // Further than last tuning
            Entry<Double, Double> firstEntry;
            Entry<Double, Double> lastEntry;
            
            if (aimHighHub) {
                firstEntry = highHubMap.firstEntry();
                lastEntry = highHubMap.lastEntry();
            } else {
                firstEntry = lowHubMap.firstEntry();
                lastEntry = lowHubMap.lastEntry();
            }

            double rise = firstEntry.getValue() - lastEntry.getValue();
            double run = firstEntry.getKey() - lastEntry.getKey();
            double slope = rise / run;

            // If distance beyond last key, approximate using slope of the entire tuning.
            return lastEntry.getValue() + slope * (distance - lastEntry.getKey());
        }

        // If within the map, linearly interpolate between the closest points.
        return MathUtil.map(distance, lowerEntry.getKey(), upperEntry.getKey(), lowerEntry.getValue(), upperEntry.getValue());
    }
}