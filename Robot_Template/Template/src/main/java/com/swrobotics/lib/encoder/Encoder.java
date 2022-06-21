package com.swrobotics.lib.encoder;

import com.swrobotics.lib.math.Angle;

// TODO: Come up with a standard for which direction the encoder is spinning -> Top or bottom view?


/**
 * A general wrapper for any external encoder
 */
public abstract class Encoder {

    private Angle offset = Angle.ccwRad(0); // Subtracted to get angle

    /**
     * Get the anglular position of the encoder as an Angle.
     * @return The angle of the sensor with no offset applied.
     */
    public abstract Angle getRawAngle();

    /**
     * Get the anglular velocity of the encoder.
     * @return The velocity of the encoder in Angle/Second.
     */
    public abstract Angle getVelocity();


    // Control offset

    /**
     * Set the current angle of the encoder to zero.
     */
    public void zeroAngle() {
        offset = getRawAngle();
    }

    /**
     * Define what angle the encoder is actually at.
     * All measurements with getAngle() will take into account this offset.
     * @param angle Define the real current angle of the encoder.
     */
    public void setAngle(Angle angle) {
        offset = getRawAngle().sub(angle);
    }

    public void setOffset(Angle offset) {
        this.offset = offset;
    }

    // Use offset

    /**
     * Get the angle of the encoder taking into account any defined offset.
     * @return The angle of the encoder.
     */
    public Angle getAngle() {
        return getRawAngle().sub(offset);
    }
}
