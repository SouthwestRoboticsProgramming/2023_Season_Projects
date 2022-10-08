package com.swrobotics.robot.subsystem.climber;

import com.team2129.lib.encoder.Encoder;
import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.Motor;
import com.team2129.lib.schedule.Command;
import com.team2129.lib.utils.TimeoutTimer;

public class Calibrator implements Command {

    private final TimeoutTimer timeout;
    private final double calibrateionPercent;
    private final Angle velocityTolerance;
    private final Motor[] motors;
    private final Encoder encoder;

    // TODO: Docs and abstraction

    public Calibrator(Angle velocityTolerance, double calibrateionPercent, double calibrationTimeout, Encoder readingEncoder, Motor... motors) {
        timeout = new TimeoutTimer(calibrationTimeout);
        this.calibrateionPercent = calibrateionPercent;
        this.velocityTolerance = velocityTolerance;
        this.motors = motors;
        encoder = readingEncoder;
    }

    @Override
    public boolean run() {
        for (Motor motor : motors) {
            motor.percent(calibrateionPercent);
        }

        System.out.println("Enc: " + encoder.getVelocity().getCWDeg());
        // If the velocity is in tolerance
        if (encoder.getVelocity().getCWDeg() <= velocityTolerance.getCWDeg()) {
            // Start timer
            timeout.start(false);
            System.out.println("Timer started");

            if (timeout.get()) {
                System.out.println("Done calibrating");
                return true;
            }
        } else {
            timeout.stop();
        }

        return false;
    }

    @Override
    public void end(boolean wasCancelled) {
        System.out.println("End!!!!!!!!!");
        for (Motor motor : motors) {
            motor.getEncoder().setAngle(Angle.cwDeg(0));
            motor.stop();
        }
    }
}
