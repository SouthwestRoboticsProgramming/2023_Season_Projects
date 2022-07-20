package com.swrobotics.lib.motor;

import com.team2129.lib.drivers.Motor;
import com.team2129.lib.math.Angle;

import edu.wpi.first.math.controller.PIDController;

public class AutoTuner {

    private static final int MAX_TUNE_TIME = 10000;

    private final Motor motor;
    
    public AutoTuner(Motor motor) {
        this.motor = motor;
    }

    public PIDController tuneKP(PIDController pid, double timeThreshold, Angle tolerance, int maxAttempts, Angle target1, Angle target2) {

        /*
         * Strategy:
         * 
         * Create a time threshold and a reasonable target and starting pose
         * Each time, swap targets
         * 
         * Start with kp of 0 and work up until the time to settle is longer than last time
         * 
         */
        motor.setAngleTolerance(tolerance);

        double increment = 0.001;

        pid.setP(0.0);

        boolean altTarget = false;
        Angle target = target1;

        int lastSettleTime = Integer.MAX_VALUE;

        while (maxAttempts > 0) {

            // Alternate targets
            if (altTarget) {
                target = target2;
            } else {
                target = target1;
            }

            // Set target
            motor.angle(target);

            // Update pid
            pid.setP(pid.getP() + increment);
            motor.setPIDController(pid);
            
            // Begin test
            int currentSettleTime = testSettleTime();
            
            // If the test performs worse, revert and return.
            if (lastSettleTime < currentSettleTime) {
                pid.setP(pid.getP() - increment);
                break;
            }
            
            
            altTarget = !altTarget;
            maxAttempts--;
        }
        
        motor.setPIDController(pid);
        return pid;
    }

    private int testSettleTime() {
        int time = 0;
        while (time < MAX_TUNE_TIME && !motor.inTolerance()) {
            time++;
        }

        return time;
    }
}
