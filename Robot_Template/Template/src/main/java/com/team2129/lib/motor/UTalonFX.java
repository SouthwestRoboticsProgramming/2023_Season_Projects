package com.team2129.lib.motor;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.team2129.lib.encoder.Encoder;
import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.calculators.PositionCalculator;
import com.team2129.lib.motor.calculators.VelocityCalculator;

public class UTalonFX extends TalonFX {

    private VelocityCalculator velocityCalculator;
    private PositionCalculator positionCalculator;

    private Encoder selectedSensor;

    public UTalonFX(int deviceNumber) {
        super(deviceNumber);
        configMotor();

    }

    public UTalonFX(int deviceNumber, String canbus) {
        super(deviceNumber, canbus);
        configMotor();
    }

    private void configMotor() {
        velocityCalculator = new VelocityCalculator() {
            @Override
            public double calculate(Angle current, Angle target) {
                return 0;
            }
        };

        positionCalculator = new PositionCalculator() {
            @Override
            public double calculate(Angle current, Angle target) {
                return 0;
            }
        };

        selectedSensor = new Encoder() {

            @Override
            protected Angle getRawAngleImpl() {
                return null; // TODO: How?
            }

            @Override
            protected Angle getVelocityImpl() {
                return null;
            }
            
        }
    }


    /**
     * Set the desired output of the TalonFX
     * @param mode
     * @param outputValue Demanded output in <pre>
     * [PercentOut, percent], 
     * [Position cwDeg], 
     * [Velocity cwDeg per second]
     */
    @Override
    public void set(ControlMode mode, double outputValue) {
        switch (mode) {
            case Velocity:
                super.set(TalonFXControlMode.PercentOutput, velocityCalculator.calculate(getSelectedSensorAngle(), Angle.cwDeg(outputValue)));
                break;

            case Position:
                super.set(TalonFXControlMode.PercentOutput, positionCalculator.calculate(getSelectedSensorAngularVelocity(), Angle.cwDeg(outputValue)));
                break;
        
            default:
                super.set(mode, outputValue);
                break;
        }
        super.set(TalonFXControlMode.PercentOutput, outputValue);
    }

    /**
     * Set the desired output of the TalonFX
     * @param mode
     * @param value Demanded output in <pre>
     * [PercentOut, percent], 
     * [Position cwDeg], 
     * [Velocity cwDeg per second]
     */
    @Override
    public void set(TalonFXControlMode mode, double value) {
        switch (mode) {
            case Velocity:
                set(ControlMode.Velocity, value);
                break;

            case Position:
                set(ControlMode.Position, value);
                break;

            default:
                super.set(mode, value);
                break;
        }
    }

    
    /**
     * Set the output of the motor in angle. <br>
     * NOTE: This is only for Velocity and Position control.
     * @param mode Either ControlMode.Velocity or ControlMode.Position
     * @param outputValue Demanded output in either [Position, angle], or [Velocity, angle per second]
     */
    public void set(ControlMode mode, Angle outputValue) throws IllegalArgumentException{
        if (mode != ControlMode.Position || mode != ControlMode.Velocity) {throw new IllegalArgumentException("Wrong ControlMode for Angle argument");}
        set(mode, outputValue.getCWDeg());
    }

    /**
     * Set the output of the motor in angle. <br>
     * NOTE: This is only for Velocity and Position control.
     * @param mode Either ControlMode.Velocity or ControlMode.Position
     * @param outputValue Demanded output in either [Position, angle], or [Velocity, angle per second]
     */
    public void set(TalonFXControlMode mode, Angle outputValue) throws IllegalArgumentException {
        if (mode != TalonFXControlMode.Position || mode != TalonFXControlMode.Velocity) {throw new IllegalArgumentException("Wrong TalonFXControlMode for Angle argument");}
        set(mode, outputValue.getCWDeg());
    }
    
    public void setSelectedSensor(Encoder sensor) {
        selectedSensor = sensor;
    }
    
    /**
     * Get the angle of the sensor selected by {@code }
     * @return
     */
    public Angle getSelectedSensorAngle() {
        return null;
    }
    
    public Angle getSelectedSensorAngularVelocity() {
        return null;
    }
    
    
    /* Old Methods */
    
    /**
     * Old method to get position in native sensor ticks.
     * This method is not recommended, use {@code getSelectedSensorAngle()} instead.
     */
    @Deprecated
    @Override
    public double getSelectedSensorPosition() {
        return super.getSelectedSensorPosition();
    }
    
    /**
     * Old method to get velocity in native sensor ticks per 100ms.
     * This method is not recommended, use {@code getSelectedSensorAngularVelocity()} instead.
     */
    @Deprecated
    @Override
    public double getSelectedSensorVelocity() {
        return super.getSelectedSensorVelocity();
    }
    
    /**
     * Not implemented yet.
     */
    @Deprecated
    @Override
    public void set(ControlMode arg0, double arg1, DemandType arg2, double arg3) {
        super.set(arg0, arg1, arg2, arg3);
    }
    
    /**
     * Not implemented yet.
     */
    @Deprecated
    @Override
    public void set(TalonFXControlMode mode, double demand0, DemandType demand1Type, double demand1) {
        super.set(mode, demand0, demand1Type, demand1);
    }
}
