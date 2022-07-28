package com.team2129.lib.schedule.subsystem;

public interface Subsystem {
    default void onAdd() {}

    // Remove handler to shut down all components
    // Example: Set motor percent output to 0 when system removed
    default void onRemove() {}

    // Global periodic, called in all states
    default void periodic() {}

    // Robot state handlers
    default void disabledInit() {}
    default void disabledPeriodic() {}
    default void autonomousInit() {}
    default void autonomousPeriodic() {}
    default void teleopInit() {}
    default void teleopPeriodic() {}
    default void testInit() {}
    default void testPeriodic() {}
}
