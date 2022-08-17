package com.team2129.lib.motor;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2129.lib.encoder.Encoder;
import com.team2129.lib.encoder.NullEncoder;
import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.calculators.PositionCalculator;
import com.team2129.lib.motor.calculators.VelocityCalculator;

public class UTalonSRX extends TalonSRX implements IMotor {

    private VelocityCalculator velocityCalculator;
    private PositionCalculator positionCalculator;

    private Encoder selectedSensor;

    /**
     * Create a TalonSRX without the use of a connected sensor. This sensor can be configured later using {@code selectIntegratedSensor()}.
     * @param deviceNumber CAN ID of the TalonSRX
     */
    public UTalonSRX(int deviceNumber) {
        super(deviceNumber);
        configMotor(0);
    }

    /**
     * Create a TalonSRX using a sensor connected through the sensor port on the top of the SRX.
     * @param deviceNumber CAN ID of the TalonSRX
     * @param sensorTicksPerRotation Number of ticks per rotation of the controlled motor.
     */
    public UTalonSRX(int deviceNumber, double sensorTicksPerRotation) {
        super(deviceNumber);
        configMotor(sensorTicksPerRotation);
    }

    private void configMotor(double ticksPerRotation) {
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

        selectIntegratedSensor(ticksPerRotation);
    }

    /* Implementations */
    @Override
    public void percent(double percent) {
        set(ControlMode.PercentOutput, percent);
    }

    @Override
    public void velocity(Angle velocity) {
        set(ControlMode.Velocity, velocity);
    }

    @Override
    public void position(Angle position) {
        set(ControlMode.Position, position);
    }

    /**
     * Define the calculations that the motor should to to go from input to output when in position control mode;
     * @param calculator Calculator to use for position control.
     * @see PositionCalculator
     */
    public void setPositionCalculator(PositionCalculator calculator) {
        positionCalculator = calculator;
    }

    /**
     * Define the calculations that the motor should to to go from input to output when in velocity control mode;
     * @param calculator Calculator to use for velocity control.
     * @see VelocityCalculator
     */
    public void setVelocityCalculator(VelocityCalculator calculator) {
        velocityCalculator = calculator;
    }


    /**
     * Set the desired output of the TalonSRX
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
                super.set(TalonSRXControlMode.PercentOutput, velocityCalculator.calculate(getSelectedSensorAngle(), Angle.cwDeg(outputValue)));
                break;

            case Position:
                super.set(TalonSRXControlMode.PercentOutput, positionCalculator.calculate(getSelectedSensorAngularVelocity(), Angle.cwDeg(outputValue)));
                break;
        
            default:
                super.set(mode, outputValue);
                break;
        }
        super.set(TalonSRXControlMode.PercentOutput, outputValue);
    }

    /**
     * Set the desired output of the TalonSRX
     * @param mode
     * @param value Demanded output in <pre>
     * [PercentOut, percent], 
     * [Position cwDeg], 
     * [Velocity cwDeg per second]
     */
    @Override
    public void set(TalonSRXControlMode mode, double value) {
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
    public void set(TalonSRXControlMode mode, Angle outputValue) throws IllegalArgumentException {
        if (mode != TalonSRXControlMode.Position || mode != TalonSRXControlMode.Velocity) {throw new IllegalArgumentException("Wrong TalonSRXControlMode for Angle argument");}
        set(mode, outputValue.getCWDeg());
    }
    
    /**
     * Select a different sensor for position and velocity readings.
     * @param sensor Sensor to select.
     */
    public void setSelectedSensor(Encoder sensor) {
        selectedSensor = sensor;
    }

    /**
     * Select the default sensor to use for position and velocity readings.
     * @param ticksPerRotation The number of ticks per rotation of the motor.
     */
    public void selectIntegratedSensor(double ticksPerRotation) {
        if (ticksPerRotation == 0) {
            selectedSensor = new NullEncoder();
            return;
        }

        UTalonSRX motor = this;
        selectedSensor = new Encoder() {

            @Override
            protected Angle getRawAngleImpl() {
                return Angle.cwRot(motor.getSelectedSensorPosition() / ticksPerRotation);
            }

            @Override
            protected Angle getVelocityImpl() {
                return Angle.cwRot(motor.getSelectedSensorVelocity() / ticksPerRotation / 10);
            }
            
        };
    }
    
    /**
     * Get the angle of the sensor selected by {@code setSelectedSensor()} or by default.
     * @return Angle read by the selected sensor.
     */
    public Angle getSelectedSensorAngle() {
        return selectedSensor.getAngle();
    }
    

    /**
     * Get the velocity of the sensor selected by {@code setSelectedSensor()} or by default.
     * @return Velocity read by the selected sensor in Angle per second.
     */
    public Angle getSelectedSensorAngularVelocity() {
        return selectedSensor.getVelocity();
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
    public void set(TalonSRXControlMode mode, double demand0, DemandType demand1Type, double demand1) {
        super.set(mode, demand0, demand1Type, demand1);
    }
}
