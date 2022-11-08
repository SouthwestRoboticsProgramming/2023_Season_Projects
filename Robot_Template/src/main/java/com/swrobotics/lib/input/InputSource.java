package com.swrobotics.lib.input;

import com.swrobotics.lib.schedule.Subsystem;

/**
 * Interface to represent any device that receives input from
 * the drivers in the driver station. This can include controllers,
 * joysticks, buttons, etc.
 */
public abstract class InputSource implements Subsystem {
    @Override
    public void periodic() {
        for (InputElement elem : getElements()) {
            elem.update();
        }
    }

    /**
     * Gets all the elements present in this source.
     *
     * @return elements present
     */
    protected abstract Iterable<InputElement> getElements();
}
