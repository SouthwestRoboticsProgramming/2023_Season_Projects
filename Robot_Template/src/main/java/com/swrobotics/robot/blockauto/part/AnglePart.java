package com.swrobotics.robot.blockauto.part;

import java.util.HashMap;
import java.util.Map;

import com.swrobotics.lib.math.Angle;
import com.swrobotics.lib.messenger.MessageBuilder;
import com.swrobotics.lib.messenger.MessageReader;

public final class AnglePart implements ParamPart {
    public enum Mode {
        CW_DEG(0) {
            @Override
            public Angle toAngle(double val) {
                return Angle.cwDeg(val);
            }

            @Override
            public double fromAngle(Angle val) {
                return val.getCWDeg();
            }
        },
        CCW_DEG(1) {
            @Override
            public Angle toAngle(double val) {
                return Angle.ccwDeg(val);
            }

            @Override
            public double fromAngle(Angle val) {
                return val.getCCWDeg();
            }
        },
        CW_RAD(2) {
            @Override
            public Angle toAngle(double val) {
                return Angle.cwRad(val);
            }

            @Override
            public double fromAngle(Angle val) {
                return val.getCWRad();
            }
        },
        CCW_RAD(3) {
            @Override
            public Angle toAngle(double val) {
                return Angle.ccwRad(val);
            }

            @Override
            public double fromAngle(Angle val) {
                return val.getCCWRad();
            }
        },
        CW_ROT(4) {
            @Override
            public Angle toAngle(double val) {
                return Angle.cwRot(val);
            }

            @Override
            public double fromAngle(Angle val) {
                return val.getCWRot();
            }
        },
        CCW_ROT(5) {
            @Override
            public Angle toAngle(double val) {
                return Angle.ccwRot(val);
            }

            @Override
            public double fromAngle(Angle val) {
                return val.getCCWRot();
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
        public abstract double fromAngle(Angle val);
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
    public void writeInst(MessageBuilder builder, Object val) {
        builder.addDouble(mode.fromAngle((Angle) val));
    }

    @Override
    public void writeToMessenger(MessageBuilder builder) {
        builder.addByte(PartTypes.ANGLE.getId());
        
        builder.addByte((byte) mode.id);
        builder.addDouble(def);
    }
}
