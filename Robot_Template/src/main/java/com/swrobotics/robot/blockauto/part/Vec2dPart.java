package com.swrobotics.robot.blockauto.part;

import com.swrobotics.lib.math.Vec2d;
import com.swrobotics.lib.messenger.MessageBuilder;
import com.swrobotics.lib.messenger.MessageReader;

public class Vec2dPart implements ParamPart {
    private final double defX;
    private final double defY;

    public Vec2dPart(double defX, double defY) {
        this.defX = defX;
        this.defY = defY;
    }

    @Override
    public Object readInst(MessageReader reader) {
        return new Vec2d(reader.readDouble(), reader.readDouble());
    }

    @Override
    public void writeInst(MessageBuilder builder, Object val) {
        Vec2d v = (Vec2d) val;
        builder.addDouble(v.x);
        builder.addDouble(v.y);
    }

    // To be overridden by FieldPointPart
    protected byte getTypeId() {
        return PartTypes.VEC2D.getId();
    }

    @Override
    public void writeToMessenger(MessageBuilder builder) {
        builder.addByte(getTypeId());
        builder.addDouble(defX);
        builder.addDouble(defY);
    }
}
