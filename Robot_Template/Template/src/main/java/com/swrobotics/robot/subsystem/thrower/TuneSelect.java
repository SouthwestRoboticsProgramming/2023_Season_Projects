package com.swrobotics.robot.subsystem.thrower;

import java.util.TreeMap;

import com.team2129.lib.net.NTDoubleArray;
import com.team2129.lib.net.NTEnum;

public class TuneSelect {

    private final TreeMap<Double, Double> defaultMap;

    private final NTEnum<ThrowerTuneSelector> tuneSelector;

    private final NTDoubleArray[] keys;
    private final NTDoubleArray[] values;

    public TuneSelect(String tuneSelectorPath, TreeMap<Double, Double> defaultMap) {
        this.defaultMap = defaultMap;

        tuneSelector = new NTEnum<ThrowerTuneSelector>(tuneSelectorPath, ThrowerTuneSelector.class, ThrowerTuneSelector.DEFAULT);

        keys = new NTDoubleArray[ThrowerTuneSelector.values().length];
        values = new NTDoubleArray[ThrowerTuneSelector.values().length];

        // TODO: Convert from Double[] to double[] for defaults for NTDoubleArrays

        for (int i = 0; i < ThrowerTuneSelector.values().length; i++) {
            // Don't put defaults into networktables
            if (ThrowerTuneSelector.values()[i].equals(ThrowerTuneSelector.DEFAULT)) {
                continue;
            }

            // For all tuning setups, create two arrays
            keys[i] = new NTDoubleArray(ThrowerTuneSelector.values()[i].getPath() + "/Keys", new double[] {10, 2, 4}); // FIXME: Somehow swap to default values
            values[i] = new NTDoubleArray(ThrowerTuneSelector.values()[i].getPath() + "/Values", new double[] {20, 4, 5});
        }
    }

    public TreeMap<Double, Double> getMap() {

        // Avoid a null
        if (tuneSelector.get().equals(ThrowerTuneSelector.DEFAULT)) {
            return defaultMap;
        }

        int selectedIndex = tuneSelector.get().ordinal();

        // Check that arrays are the same length
        if (keys[selectedIndex].get().length != values[selectedIndex].get().length) {
            // TODO: What do I do?
            // Pop the end of the longer one until it works?
            // Go back to default? <-
            return defaultMap;
        }

        TreeMap<Double, Double> map = new TreeMap<Double, Double>();

        // Insert keys and values into map
        for (int i = 0; i < keys[selectedIndex].get().length; i++) {
            map.putIfAbsent(keys[selectedIndex].get()[i], values[selectedIndex].get()[i]);
        }
        return map;
    }
    
}
