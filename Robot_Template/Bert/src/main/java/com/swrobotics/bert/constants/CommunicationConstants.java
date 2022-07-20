package com.swrobotics.bert.constants;

import edu.wpi.first.wpilibj.RobotBase;

public final class CommunicationConstants {
    // If simulating, connect to a local Messenger server
    public static final String MESSENGER_HOST = RobotBase.isReal() ? "10.21.29.3" : "localhost";
    public static final int MESSENGER_PORT = 5805;
    public static final String MESSENGER_NAME = "RoboRIO";
    public static final int MESSENGER_CONNECT_MAX_ATTEMPTS = 15;
    public static final long MESSENGER_CONNECT_RETRY_INTERVAL = 1000;

    public static final String RASPBERRY_PI_PREFIX = "RPi";
    public static final String JETSON_NANO_PREFIX = "Nano";

    public static final String LIDAR_NAME = "Lidar";
    public static final String PATHFINDING_NAME = "Pathfinding";
    public static final String VISION_NAME = "Vision";
    
    public static final String LOCALIZATION_MSG = "RoboRIO:Location";

    private CommunicationConstants() {
        throw new AssertionError();
    }
}
