package com.team2129.lib.motor.tests;

import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.Motor;
import com.team2129.lib.schedule.TestCommand;

public class VelocityTest extends TestCommand {

    private final Motor motor;
    private final Angle targetVelocity;
    private final Angle tolerance;

    /**
     * Create a test to measure if the motor calculator can navigate the motor the desired velocity.
     * @param motor
     * @param timeoutSeconds
     * @param targetVelocity
     * @param tolerance
     */
    public VelocityTest(Motor motor, double timeoutSeconds, Angle targetVelocity, Angle tolerance) {
        setTimeout(timeoutSeconds);
        this.motor = motor;
        this.targetVelocity = targetVelocity;
        this.tolerance = tolerance;
    }

    @Override
    public boolean run() {
        motor.velocity(targetVelocity);
        return hasTimedOut();
    }

    @Override
    public boolean hasFailed() {
        // Angle velocityDiff = targetVelocity.sub(motor.getEncoder().getVelocity()); // FIXME
        // return hasTimedOut() && velocityDiff.absoluteValue().lessOrEqualTo(tolerance); // FIXME
        return true;
    }
    
}
