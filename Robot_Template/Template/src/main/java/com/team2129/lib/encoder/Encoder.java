package com.team2129.lib.encoder;

import com.team2129.lib.math.Angle;

/**
 * A general wrapper for any external encoder
 */
public abstract class Encoder {
    private Angle offset = Angle.ccwRad(0); // Subtracted to get angle
    private OutputFilter filter = (angle) -> angle; // Applied to angle in getAngle()

    /**
     * Get the angular position of the encoder as an Angle.
     * @return The angle of the sensor with no offset applied.
     */
    public abstract Angle getRawAngle();

    /**
     * Get the angular velocity of the encoder.
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

    /**
     * Define what angle needs to be subtracted to achieve the correct angle.
     * @param offset Offset to be applied to getAngle().
     */
    public void setOffset(Angle offset) {
        this.offset = offset;
    }

    /**
     * Get the angle of the encoder taking into account any defined offset and
     * the currently set {@link OutputFilter}.
     * @return The angle of the encoder.
     */
    public Angle getAngle() {
        return filter.filter(getUnfilteredAngle());
    }

    /**
     * Gets the current angle without passing it through the output filter, taking
     * into account and defined offset.
     * @return The angle of the encoder
     */
    public Angle getUnfilteredAngle() {
        return getRawAngle().sub(offset);
    }
}
