package com.swrobotics.lib.routines;

public interface Routine {
    default void init() {};

    boolean run();

    default void end() {};

    default void periodic() {};

    default void autonomous_periodic() {};

    default void teleop_periodic() {};
}
