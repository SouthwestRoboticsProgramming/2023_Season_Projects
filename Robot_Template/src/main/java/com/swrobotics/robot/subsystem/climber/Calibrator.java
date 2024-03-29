package com.swrobotics.robot.subsystem.climber;

import com.swrobotics.lib.encoder.Encoder;
import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.motor.Motor;
import com.swrobotics.lib.schedule.Command;
import com.swrobotics.lib.utils.TimeoutTimer;

public class Calibrator implements Command {

    private final TimeoutTimer timeout;
    private final double calibrationPercent;
    private final Angle velocityTolerance;
    private final Motor[] motors;
    private final Encoder encoder;

    // TODO: Docs and abstraction

    public Calibrator(Angle velocityTolerance, double calibrationPercent, double calibrationTimeout, Encoder readingEncoder, Motor... motors) {
        timeout = new TimeoutTimer(calibrationTimeout);
        this.calibrationPercent = calibrationPercent;
        this.velocityTolerance = velocityTolerance;
        this.motors = motors;
        encoder = readingEncoder;
    }

    @Override
    public boolean run() {
        for (Motor motor : motors) {
            motor.percent(calibrationPercent);
        }

        // If the velocity is in tolerance
        if (Math.abs(encoder.getVelocity().getCWDeg()) <= velocityTolerance.getCWDeg()) {
            // Start timer
            timeout.start(false);

            if (timeout.get()) {
                return true;
            }
        } else {
            timeout.stop();
        }

        return false;
    }

    @Override
    public void end(boolean wasCancelled) {
        for (Motor motor : motors) {
            motor.getEncoder().setAngle(Angle.cwDeg(0));
            motor.stop();
        }
    }
}
