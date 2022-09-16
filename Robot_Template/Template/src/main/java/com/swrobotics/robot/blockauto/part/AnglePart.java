package com.swrobotics.robot.blockauto.part;

import java.util.HashMap;
import java.util.Map;

import com.team2129.lib.math.Angle;
import com.team2129.lib.messenger.MessageBuilder;
import com.team2129.lib.messenger.MessageReader;

public final class AnglePart implements ParamPart {
    public enum Mode {
        CW_DEG(0) {
            @Override
            public Angle toAngle(double val) {
                return Angle.cwDeg(val);
            }
        },
        CCW_DEG(1) {
            @Override
            public Angle toAngle(double val) {
                return Angle.ccwDeg(val);
            }
        },
        CW_RAD(2) {
            @Override
            public Angle toAngle(double val) {
                return Angle.cwRad(val);
            }
        },
        CCW_RAD(3) {
            @Override
            public Angle toAngle(double val) {
                return Angle.ccwRad(val);
            }
        },
        CW_ROT(4) {
            @Override
            public Angle toAngle(double val) {
                return Angle.cwRot(val);
            }
        },
        CCW_ROT(5) {
            @Override
            public Angle toAngle(double val) {
                return Angle.ccwRot(val);
            }
        };

        private static final Map<Integer, Mode> BY_ID = new HashMap<>();
        static {
            for (Mode mode : values()) {
                BY_ID.put(mode.id, mode);
            }
        }

        private final int id;

        Mode(int id) {
            this.id = id;
        }

        public abstract Angle toAngle(double val);
    }

    private final Mode mode;
    private final double def;

    public AnglePart(Mode mode, double def) {
        this.mode = mode;
        this.def = def;
    }

    @Override
    public Object readInst(MessageReader reader) {
        double val = reader.readDouble();
        return mode.toAngle(val);
    }

    @Override
    public void writeToMessenger(MessageBuilder builder) {
        builder.addByte(PartTypes.ANGLE.getId());
        
        builder.addByte((byte) mode.id);
        builder.addDouble(def);
    }
}
