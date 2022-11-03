package com.swrobotics.robot.subsystem.thrower;

import com.team2129.lib.encoder.QuadratureEncoder;
import com.team2129.lib.math.Angle;
import com.team2129.lib.math.MathUtil;
import com.team2129.lib.motor.ctre.TalonSRXMotor;
import com.team2129.lib.net.NTBoolean;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;

import edu.wpi.first.wpilibj.DigitalInput;

public class Hood implements Subsystem {
    private static final int ENCODER_ID_1 = 8;
    private static final int ENCODER_ID_2 = 7;
    private static final int MOTOR_ID = 5;
    private static final int LIMIT_SWITCH_ID = 0;
    
    private static final int TICKS_PER_FLOOR_TO_CEILING = 588;

    
    private static final NTDouble KP = new NTDouble("Thrower/Hood/kP", 0.05);
    private static final NTDouble KI = new NTDouble("Thrower/Hood/kI", 0.0);
    private static final NTDouble KD = new NTDouble("Thrower/Hood/kD", 0.0);
    
    private static final NTDouble CALIBRATE_PERCENT = new NTDouble("Thrower/Hood/Calibration Speed", -0.1);

    private static final NTDouble TEST_POS = new NTDouble("Test/Test/Hood_Pos", 0);
    
    private static final NTDouble L_HOOD_POS = new NTDouble("Thrower/Hood/Hood Position", 2129);
    private static final NTBoolean L_LIMIT_SWITCH_PRESSED = new NTBoolean("Thrower/Hood/Limit Switch Pressed", false);

    private final QuadratureEncoder encoder;
    private final TalonSRXMotor motor;
    private final DigitalInput limitSwitch;

    private boolean isCalibrating;
    private Angle targetPosition;

    public Hood() {
        encoder = new QuadratureEncoder(ENCODER_ID_1, ENCODER_ID_2, TICKS_PER_FLOOR_TO_CEILING); // Not ticks per rotation but ticks per floor to ceiling of movement.
 
        motor = new TalonSRXMotor(this, MOTOR_ID);
        motor.setEncoder(encoder);
        motor.setPIDCalculators(KP, KI, KD);

        limitSwitch = new DigitalInput(LIMIT_SWITCH_ID);

        isCalibrating = true;
        targetPosition = Angle.zero();

        L_LIMIT_SWITCH_PRESSED.setTemporary();
        L_HOOD_POS.setTemporary();
    }

    /**
     * 
     * @param percent Percent height of the hood.
     */
    public void setPosition(double percent) {
        percent = MathUtil.clamp(percent, 0, 1);
        targetPosition = Angle.cwRot(percent);
    }

    public void calibrate() {
        isCalibrating = true;
    }

    @Override
    public void periodic() {
        L_LIMIT_SWITCH_PRESSED.set(limitSwitch.get());
        L_HOOD_POS.set(motor.getEncoder().getAngle().getCWDeg() / 360);

        // System.out.println("Hood: " + isCalibrating + " " + targetPosition);

        if (isCalibrating) {
            if (limitSwitch.get()) {
                isCalibrating = false;
                encoder.zeroAngle();
                motor.stop();
            } else {
                motor.percent(CALIBRATE_PERCENT.get());
            }
            return;
        }

        if (limitSwitch.get()) {
            encoder.zeroAngle();
        }
        
        // System.out.println("Going to... " + targetPosition.getCWRot());
        motor.position(targetPosition);
    }
}
