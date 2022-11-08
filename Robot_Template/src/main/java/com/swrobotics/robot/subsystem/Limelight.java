package com.swrobotics.robot.subsystem;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.math.CWAngle;
import com.swrobotics.lib.net.NTBoolean;
import com.swrobotics.lib.net.NTDouble;
import com.swrobotics.lib.schedule.Subsystem;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight implements Subsystem {

    /*
     * Tuning:
     * 1. Use limelight UI to place crosshair so that it is horizontal to the robot.
     *    This will allow the angles to be acurate.
     */

    private static final double LIMELIGHT_MOUNT_HEIGHT = 0.8604; // Meters
    private static final double TARGET_HEIGHT = 2.2416; // Meters
    private static final double HEIGHT_DIFF = TARGET_HEIGHT - LIMELIGHT_MOUNT_HEIGHT;

    private static final NTBoolean LIGHTS_ON = new NTBoolean("Limelight/Lights_On", true);

    private static final NTDouble L_DISTANCE = new NTDouble("Limelight/Distance", 2129);

    private final NetworkTableEntry xAngle;
    private final NetworkTableEntry yAngle;
    private final NetworkTableEntry targetArea;

    private final NetworkTableEntry lightsOn;

    private Angle x;
    private Angle y;
    private double area;

    public Limelight() {
        LIGHTS_ON.set(true); // Default to lights on so as not to forget

        NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
        this.xAngle = table.getEntry("tx");
        this.yAngle = table.getEntry("ty");
        this.targetArea = table.getEntry("ta");

        this.lightsOn = table.getEntry("ledMode");

        // FIXME
        // setLights(LIGHTS_ON.get());
        // LIGHTS_ON.onChange(() -> setLights(LIGHTS_ON.get()));

        y = Angle.ZERO;
        x = Angle.ZERO;
        area = 0;

        L_DISTANCE.setTemporary();
    }

    public Angle getXAngle() {
        return x;
    }

    public Angle getYAngle() {
        return y;
    }

    public double getArea() {
        return area;
    }

    /**
     * In meters
     * @return
     */
    public double getDistance() {
        Angle angle = getYAngle();

        try {
            return HEIGHT_DIFF / Math.tan(angle.cw().rad());
        } catch (ArithmeticException e) {
            return 0;
        }
    }

    public boolean isAccurate() {
        return (
            x.cw().deg() != 0 &&
            y.cw().deg() != 0 &&
            getDistance() > 0 &&
            getDistance() < 18 * 0.305 &&
            getArea() > 0);
    }

    // FIXME
    // public void setLights(boolean on) {
    //     int value = 1;
    //     if (on) value = 3;
    //     lightsOn.setNumber(value);
    //     LIGHTS_ON.set(on);
    // }

    @Override
    public void periodic() {
        x = CWAngle.deg(xAngle.getDouble(0.0));
        y = CWAngle.deg(yAngle.getDouble(0.0));
        area = targetArea.getDouble(0.0);

        L_DISTANCE.set(getDistance());
    }


}
