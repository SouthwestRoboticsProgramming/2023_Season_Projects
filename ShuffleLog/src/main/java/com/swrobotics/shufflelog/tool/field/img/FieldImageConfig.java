package com.swrobotics.shufflelog.tool.field.img;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import processing.core.PImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * Represents a WPILib field image configuration file.
 * For some reason WPILib does not have API for this.
 */
public final class FieldImageConfig {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(FieldImageConfig.class, new Serializer())
            .create();

    public static FieldImageConfig load(String path) {
        return GSON.fromJson(new InputStreamReader(FieldImageConfig.class.getResourceAsStream(path)), FieldImageConfig.class);
    }

    public String game;
    public String fieldImage;
    public Corners fieldCorners;
    public FieldSize fieldSize;
    public DistanceUnit fieldUnit;

    private FieldImageConfig() {}

    public FieldImageConfig(String game, String fieldImage, Corners fieldCorners, FieldSize fieldSize, DistanceUnit fieldUnit) {
        this.game = game;
        this.fieldImage = fieldImage;
        this.fieldCorners = fieldCorners;
        this.fieldSize = fieldSize;
        this.fieldUnit = fieldUnit;
    }

    public String getGame() {
        return game;
    }

    public String getFieldImage() {
        return fieldImage;
    }

    public String getFullFieldImagePath() {
        return "/edu/wpi/first/fields/" + fieldImage;
    }

    public PImage loadFieldImage() {
        try {
            InputStream in = getClass().getResourceAsStream(getFullFieldImagePath());
            BufferedImage img = ImageIO.read(in);
            PImage p = new PImage(img);
            in.close();
            return p;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load field image: " + getFullFieldImagePath());
        }
    }

    public Corners getFieldCorners() {
        return fieldCorners;
    }

    public FieldSize getFieldSize() {
        return fieldSize;
    }

    public DistanceUnit getFieldUnit() {
        return fieldUnit;
    }

    public static final class Corners {
        public PixelCoord topLeft;
        public PixelCoord bottomRight;

        private Corners() {}

        public Corners(PixelCoord topLeft) {
            this.topLeft = topLeft;
        }

        @Override
        public String toString() {
            return "Corners{" +
                    "topLeft=" + topLeft +
                    ", bottomRight=" + bottomRight +
                    '}';
        }
    }

    public static final class PixelCoord {
        public int x, y;

        private PixelCoord() {}

        public PixelCoord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "PixelCoord{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    public static final class FieldSize {
        public double width, height;

        private FieldSize() {}

        public FieldSize(double width, double height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return "FieldSize{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    public enum DistanceUnit {
        FOOT(0.3048);

        private final double toMetersScale;

        DistanceUnit(double toMetersScale) {
            this.toMetersScale = toMetersScale;
        }

        public double toMeters(double measure) {
            return measure * toMetersScale;
        }
    }

    public static final class Serializer implements JsonDeserializer<FieldImageConfig> {
        private PixelCoord getPixelCoord(JsonElement elem) {
            JsonArray arr = elem.getAsJsonArray();
            return new PixelCoord(
                    arr.get(0).getAsInt(),
                    arr.get(1).getAsInt()
            );
        }

        private FieldSize getFieldSize(JsonElement elem) {
            JsonArray arr = elem.getAsJsonArray();
            return new FieldSize(
                    arr.get(0).getAsDouble(),
                    arr.get(1).getAsDouble()
            );
        }

        @Override
        public FieldImageConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            FieldImageConfig conf = new FieldImageConfig();
            conf.game = obj.get("game").getAsString();
            conf.fieldImage = obj.get("field-image").getAsString();

            conf.fieldCorners = new Corners();
            JsonObject corners = obj.getAsJsonObject("field-corners");
            conf.fieldCorners.topLeft = getPixelCoord(corners.get("top-left"));
            conf.fieldCorners.bottomRight = getPixelCoord(corners.get("bottom-right"));

            conf.fieldSize = getFieldSize(obj.get("field-size"));
            String unitStr = obj.get("field-unit").getAsString();
            switch (unitStr) {
                case "foot":
                case "feet":
                    conf.fieldUnit = DistanceUnit.FOOT;
                    break;
                default:
                    throw new JsonParseException("Unknown field unit: " + unitStr);
            }

            return conf;
        }
    }
}
