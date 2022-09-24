package com.swrobotics.robot.subsystem;

import com.team2129.lib.math.Angle;
import com.team2129.lib.net.NTBoolean;
import com.team2129.lib.schedule.Subsystem;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight implements Subsystem {
    private static final Angle LIMELIGHT_MOUNT_ANGLE = Angle.cwDeg(15.5);
    private static final double LIMELIGHT_MOUNT_HEIGHT = 0.8604; // Meters
    private static final double TARGET_HEIGHT = 2.2416;
    private static final double HEIGHT_DIFF = TARGET_HEIGHT - LIMELIGHT_MOUNT_HEIGHT;

    private static final NTBoolean LIGHTS_ON = new NTBoolean("Limelight/Lights_On", true);

    private final NetworkTableEntry xAngle;
    private final NetworkTableEntry yAngle;
    private final NetworkTableEntry targetArea;

    private final NetworkTableEntry lightsOn;

    private Angle x;
    private Angle y;
    private double area;

    public Limelight() {
        NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
        this.xAngle = table.getEntry("tx");
        this.yAngle = table.getEntry("ty");
        this.targetArea = table.getEntry("ta");

        this.lightsOn = table.getEntry("ledMode");

        setLights(LIGHTS_ON.get());
        LIGHTS_ON.onChange(() -> setLights(LIGHTS_ON.get()));
    }

    public Angle getXAngle() {
        return x;
    }

    public Angle getRawYAngle() {
        return y;
    }

    public double getArea() {
        return area;
    }

    public double getDistance() {
        Angle angle = getRawYAngle();

        try {
            return HEIGHT_DIFF / Math.tan(angle.getCWRad());
        } catch (ArithmeticException e) {
            return 0;
        }
    }

    public boolean isAccurate() {
        return (
            x.getCWDeg() > 0 && // FIXME-Mason: Is this correct?
            y.getCWDeg() > 0 &&
            getDistance() > 0 &&
            getDistance() < 20);
    }

    public void setLights(boolean on) {
        int value = 1;
        if (on) value = 3;
        lightsOn.setNumber(value);
        LIGHTS_ON.set(on);
    }

    @Override
    public void periodic() {
        x = Angle.cwDeg(xAngle.getDouble(0.0));
        y = Angle.cwDeg(yAngle.getDouble(0.0));
        area = targetArea.getDouble(0.0);
    }


}
