package com.swrobotics.lib.encoder;

import com.swrobotics.lib.math.Angle;

public abstract class RelativeEncoder {

    private Angle offset;

    /**
     * Relative encoder implementation.
     */
    public RelativeEncoder() {
        offset = Angle.cwDeg(0);
    }

    /**
     * Get the relative position of the encoder as an Angle.
     * @return The relative position of the encoder relative
     * to when you set the position.
     */
    public Angle getRelativeAngle() {
        return getRawPosition().add(offset);
    }

    /**
     * Get the clockwise velocity of the encoder in RPM.
     * @return Clockwise RPM of the encoder.
     */
    public abstract double getRPM();

    /**
     * Get the relative position of the encoder, not including any offset
     * given by the user.
     * @return The raw position of the encoder as an Angle.
     */
    public abstract Angle getRawPosition();

    /**
     * Sets the current positon of the encoder to 0.
     */
    public void zeroPosition() {
        offset = Angle.cwDeg(getRawPosition().getCCWDeg());
    }

    /**
     * Set the position of the encoder to a desired angle.
     * @param position Angle to set the postion of the encoder to.
     */
    public void setPosition(Angle position) {
        offset = getRawPosition().sub(position);
    }
}
