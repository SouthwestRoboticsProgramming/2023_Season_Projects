package com.swrobotics.lib.input;

/**
 * Represents one element of a controller that can receive
 * input. This includes axes, buttons, and d-pads.
 */
public interface InputElement {
    void update();
}
