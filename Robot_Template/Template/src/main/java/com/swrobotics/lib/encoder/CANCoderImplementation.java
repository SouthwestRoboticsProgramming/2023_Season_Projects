package com.swrobotics.lib.encoder;

import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderStatusFrame;
import com.swrobotics.lib.math.Angle;

public class CANCoderImplementation extends AbsoluteEncoder {

    private final CANCoder encoder;

    public CANCoderImplementation(int ID) {
        encoder = new CANCoder(ID);
    }

    public CANCoderImplementation(int ID, String canbus) {
        encoder = new CANCoder(ID, canbus);
        encoder.setStatusFramePeriod(CANCoderStatusFrame.SensorData, 20);
    }

    @Override
    public double getRPM() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Angle getRawPosition() {
        // TODO Auto-generated method stub
        return null;
    }


    public void setStatusFramePeriod() {
        // TODO
    }
}
