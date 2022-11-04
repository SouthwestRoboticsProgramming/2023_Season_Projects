package com.team2129.lib.encoder;

import java.util.function.Supplier;

import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.ctre.TalonFXMotor;

/**
 * A general wrapper for any external encoder. This can include
 * encoders built into a motor, such as the internal encoders in
 * {@link TalonFXMotor}.
 */
public abstract class Encoder implements Supplier<Angle> {
    private Angle offset = Angle.ccwRad(0); // Subtracted to get angle
    private OutputFilter filter = (angle) -> angle; // Applied to angle in getAngle()
    private boolean inverted = false;

    protected abstract Angle getRawAngleImpl();
    protected abstract Angle getVelocityImpl();

    /**
     * Sets an {@code OutputFilter} to process the encoder's reading
     * before being returned from {@link #getAngle}.
     * 
     * @param filter OutputFilter to set
     */
    public void setOutputFilter(OutputFilter filter) {
        this.filter = filter;
    }

    /**
     * Gets the angular position of the encoder as an Angle.
     * 
     * @return the angle of the sensor with no offset applied
     */
    public Angle getRawAngle() {
        if (inverted) {
            return Angle.zero().sub(getRawAngleImpl());
        } else {
            return getRawAngleImpl();
        }
    }

    /**
     * Gets the angular velocity of the encoder.
     * 
     * @return the velocity of the encoder in Angle/Second
     */
    public Angle getVelocity() {
        if (inverted) {
            return Angle.zero().sub(getVelocityImpl());
        } else {
            return getVelocityImpl();
        }
    }

    // Control offset

    /**
     * Sets the current angle of the encoder to zero. This means that angles
     * from {@link #getAngle} will be relative to the current position when
     * calling this method.
     */
    public void zeroAngle() {
        offset = getRawAngle();
    }

    /**
     * Defines what angle the encoder is actually at.
     * All measurements with getAngle() will take into account this offset.
     * 
     * @param angle define the real current angle of the encoder
     */
    public void setAngle(Angle angle) {
        offset = getRawAngle().sub(angle);
    }

    /**
     * Defines what angle needs to be subtracted to achieve the correct angle.
     * 
     * @param offset fffset to be applied to getAngle()
     */
    public void setOffset(Angle offset) {
        this.offset = offset;
    }

    /**
     * Gets the angle of the encoder taking into account any defined offset and
     * the currently set {@link OutputFilter}.
     * 
     * @return the angle of the encoder
     */
    public Angle getAngle() {
        return filter.filter(getUnfilteredAngle());
    }

    /**
     * Gets the current angle without passing it through the output filter, taking
     * into account the defined offset.
     * 
     * @return the angle of the encoder
     */
    public Angle getUnfilteredAngle() {
        return getRawAngle().sub(offset);
    }

    /**
     * Sets whether this encoder's output should be inverted.
     * 
     * @param inverted whether to invert the output
     */
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    /**
     * @see #getAngle()
     */
    @Override
    public Angle get() {
        return getAngle();
    }
}
