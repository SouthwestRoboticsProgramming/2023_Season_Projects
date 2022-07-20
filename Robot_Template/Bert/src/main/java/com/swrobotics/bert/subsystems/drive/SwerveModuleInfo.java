package com.swrobotics.bert.subsystems.drive;

public final class SwerveModuleInfo {
    private final int driveID;
    private final int cancoderID;
    private final double cancoderOffset;

    public SwerveModuleInfo(int driveID, int cancoderID, double cancoderOffset) {
        this.driveID = driveID;
        this.cancoderID = cancoderID;
        this.cancoderOffset = cancoderOffset;
    }

    public int getDriveID() {
        return driveID;
    }

    public int getCancoderID() {
        return cancoderID;
    }

    public double getCancoderOffset() {
        return cancoderOffset;
    }
}
