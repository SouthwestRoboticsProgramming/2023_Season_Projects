package com.swrobotics.robot;

public final class Constants {
    public static final double EXAMPLE_CONSTANT = 5.0; // Formatting for constants: All caps, public static final.





    private Constants() { // Don't do ' new Constants '
        throw new AssertionError("Don't make an instance of constants, just import static"); // Ignore this
    }
}
