package com.swrobotics.robot.subsystem.drive;

import com.team2129.lib.net.NTDouble;

public enum SwerveModuleDef {
    MODULE_1(1, 6, "Swerve/Offsets/Module 1 Offset"),
    MODULE_2(2, 7, "Swerve/Offsets/Module 2 Offset"),
    MODULE_3(3, 8, "Swerve/Offsets/Module 3 Offset"),
    MODULE_4(4, 9, "Swerve/Offsets/Module 4 Offset"),
    MODULE_5(5, 10, "Swerve/Offsets/Module 5 Offset");

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
