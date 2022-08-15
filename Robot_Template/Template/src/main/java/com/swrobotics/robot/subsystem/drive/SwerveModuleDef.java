package com.swrobotics.robot.subsystem.drive;

import com.team2129.lib.math.Angle;
import com.team2129.lib.net.NTDouble;

public enum SwerveModuleDef {
    // FIXME: TODO: Fix me
    MODULE_1(73846, 73846, "Drive/Offsets/Module 1 Offset"),
    MODULE_2(73846, 73846, "Drive/Offsets/Module 2 Offset"),
    MODULE_3(73846, 73846, "Drive/Offsets/Module 3 Offset"),
    MODULE_4(73846, 73846, "Drive/Offsets/Module 4 Offset"),
    MODULE_5(73846, 73846, "Drive/Offsets/Module 5 Offset");

    private final int driveId;
    private final int encoderId;
    private final NTDouble encoderOffset;

    SwerveModuleDef(int driveId, int encoderId, String encoderOffset) {
        this.driveId = driveId;
        this.encoderId = encoderId;
        this.encoderOffset = new NTDouble(encoderOffset, 0);
    }

    public int getDriveId() {
        return driveId;
    }

    public int getEncoderId() {
        return encoderId;
    }

    public NTDouble getEncoderOffset() {
        return encoderOffset;
    }
}
