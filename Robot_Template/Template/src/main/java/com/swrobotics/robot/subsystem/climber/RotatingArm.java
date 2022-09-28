package com.swrobotics.robot.subsystem.climber;

import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.calc.PIDCalculator;
import com.team2129.lib.motor.rev.BrushlessSparkMaxMotor;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;

public class RotatingArm implements Subsystem {

    private static final NTDouble LOADED_KP = new NTDouble("Climber/Rotating/kP", defaultVal);

    private static final double ARM_LENGTH = 15.0017;
    private static final double BASE_LENGTH = 6.76;
    private static final double ROTS_PER_INCH = 11.0;

    private final PIDCalculator unloadedCalc;
    private final PIDCalculator loadedCalc;

    private final BrushlessSparkMaxMotor motor;

    public RotatingArm(int motorID, boolean inverted) {
        unloadedCalc = new PIDCalculator(kP, kI, kD);
        loadedCalc = new PIDCalculator(kP, kI, kD);

        motor = new BrushlessSparkMaxMotor(this, motorID);
        motor.setInverted(inverted);
    }

    public void setAngle(Angle angle) {

    }
}
