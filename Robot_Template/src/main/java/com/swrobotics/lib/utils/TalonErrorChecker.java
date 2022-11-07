package com.swrobotics.lib.utils;

import com.ctre.phoenix.ErrorCode;
import edu.wpi.first.wpilibj.DriverStation;

public class TalonErrorChecker {
    /**
     * Checks if the error code has issues
     * 
     * @param errorCode Current error code. This may be "OK", that is what this tool checks.
     * @param message Message to print if an error occurs
     */
    public static void checkError(ErrorCode errorCode, String message) {
        if (errorCode != ErrorCode.OK) {
            DriverStation.reportError(message + errorCode, false);
        }
    }
}
