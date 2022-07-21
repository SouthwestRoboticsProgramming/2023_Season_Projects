package com.swrobotics.bert.control;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public abstract class Controller {
    public static class Button {
        private final JoystickButton button;

        private boolean pressed;
        private boolean last;

        public Button(Joystick stick, int buttonID) {
            button = new JoystickButton(stick, buttonID);
        }

        public boolean isPressed() {
            return pressed;
        }

        public boolean leadingEdge() {
            return pressed && !last;
        }

        public boolean fallingEdge() {
            return !pressed && last;
        }

        protected void update() {
            last = pressed;
            pressed = button.get();
        }
    }

    public static class DpadButton {
        private final Joystick stick;
        private final int a, b, c;

        private boolean pressed;
        private boolean last;

        public DpadButton(Joystick stick, int a, int b, int c) {
            this.stick = stick;
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public boolean isPressed() {
            return pressed;
        }

        public boolean leadingEdge() {
            return pressed && !last;
        }

        public boolean fallingEdge() {
            return !pressed && last;
        }

        protected void update() {
            last = pressed;

            int pov = stick.getPOV();
            pressed = pov == a || pov == b || pov == c;
        }
    }

    public static class Axis {
        private final Joystick stick;
        private final int axisID;
        private final boolean inverted;

        private double value;

        public Axis(Joystick stick, int axisID, boolean inverted) {
            this.stick = stick;
            this.axisID = axisID;
            this.inverted = inverted;
        }

        public double get() {
            return value;
        }

        void update() {
            if (axisID < 0) return;
            
            if (inverted) {
                value = -stick.getRawAxis(axisID);
            } else {
                value = stick.getRawAxis(axisID);
            }
        }
    }

    public final Button a, b, x, y;
    public final Button leftShoulder, rightShoulder;
    public final Button select, start;    
    public final Button leftStickIn, rightStickIn;

    public final DpadButton dpadUp, dpadDown, dpadLeft, dpadRight;

    public final Axis leftStickX, leftStickY;
    public final Axis rightStickX, rightStickY;
    public final Axis leftTrigger, rightTrigger;

    public Controller(int id, int a_, int b_, int x_, int y_, int ls, int rs, int sel, int st, int lst, int rst, int lsx, int lsy, int rsx, int rsy, int lt, int rt) {
        Joystick stick = new Joystick(id);

        a             = new Button(stick, a_);
        b             = new Button(stick, b_);
        x             = new Button(stick, x_);
        y             = new Button(stick, y_);
        leftShoulder  = new Button(stick, ls);
        rightShoulder = new Button(stick, rs);
        select        = new Button(stick, sel);
        start         = new Button(stick, st);
        leftStickIn   = new Button(stick, lst);
        rightStickIn  = new Button(stick, rst);

        dpadUp    = new DpadButton(stick,   0,  45, 315);
        dpadDown  = new DpadButton(stick, 135, 180, 225);
        dpadLeft  = new DpadButton(stick, 225, 270, 315);
        dpadRight = new DpadButton(stick,  45,  90, 135);

        leftStickX   = new Axis(stick, lsx, false);
        leftStickY   = new Axis(stick, lsy, true);
        rightStickX  = new Axis(stick, rsx, false);
        rightStickY  = new Axis(stick, rsy, true);
        leftTrigger  = new Axis(stick, lt, false);
        rightTrigger = new Axis(stick, rt, false);
    }

    public void update() {
        a.update();
        b.update();
        x.update();
        y.update();
        leftShoulder.update();
        rightShoulder.update();
        select.update();
        start.update();
        dpadUp.update();
        dpadDown.update();
        dpadLeft.update();
        dpadRight.update();
        leftStickIn.update();
        rightStickIn.update();

        leftStickX.update();
        leftStickY.update();
        rightStickX.update();
        rightStickY.update();
        leftTrigger.update();
        rightTrigger.update();
    }
}
