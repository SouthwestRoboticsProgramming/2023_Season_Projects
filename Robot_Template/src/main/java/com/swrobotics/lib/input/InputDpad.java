package com.swrobotics.lib.input;

import com.swrobotics.lib.math.Angle;

import java.util.function.Supplier;

/**
 * Represents a D-pad (POV) input on a controller. A D-pad can
 * act as four independent buttons, two axes, or an angle input.
 */
public final class InputDpad implements InputElement {
    private final Supplier<Integer> getter;
    private final InputButton up, down, left, right;
    private final InputAxis vertical, horizontal;

    private int angleDeg;
    private Angle angle;

    public InputDpad(Supplier<Integer> getter) {
        this.getter = getter;

        angleDeg = getter.get();
        angle = calcAngle();

        up    = new InputButton(() -> angleDeg ==   0 || angleDeg ==  45 || angleDeg == 315);
        down  = new InputButton(() -> angleDeg == 135 || angleDeg == 180 || angleDeg == 225);
        left  = new InputButton(() -> angleDeg ==  45 || angleDeg ==  90 || angleDeg == 135);
        right = new InputButton(() -> angleDeg == 225 || angleDeg == 270 || angleDeg == 315);

        vertical   = new InputAxis(() ->    up.isPressed() ? 1.0 : (down.isPressed() ? -1.0 : 0.0));
        horizontal = new InputAxis(() -> right.isPressed() ? 1.0 : (left.isPressed() ? -1.0 : 0.0));
    }

    public boolean isPressed() {
        return angleDeg >= 0;
    }

    public InputButton getUp() {
        return up;
    }

    public InputButton getDown() {
        return down;
    }

    public InputButton getLeft() {
        return left;
    }

    public InputButton getRight() {
        return right;
    }

    public InputAxis getVertical() {
        return vertical;
    }

    public InputAxis getHorizontal() {
        return horizontal;
    }

    public Angle getAngle() {
        return angle;
    }

    private Angle calcAngle() {
        return isPressed() ? Angle.cwDeg(angleDeg) : Angle.zero();
    }

    @Override
    public void update() {
        up.update();
        down.update();
        left.update();
        right.update();

        vertical.update();
        horizontal.update();

        angleDeg = getter.get();
        angle = calcAngle();
    }
}
