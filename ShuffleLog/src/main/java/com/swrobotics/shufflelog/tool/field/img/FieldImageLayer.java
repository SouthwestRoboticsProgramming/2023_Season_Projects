package com.swrobotics.shufflelog.tool.field.img;

import com.swrobotics.shufflelog.tool.field.FieldLayer;
import edu.wpi.fields.FieldImages;
import imgui.ImGui;
import imgui.type.ImBoolean;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * Layer to show reference image of the field from above
 */
public final class FieldImageLayer implements FieldLayer {
    // Change this each season to reflect current field image
    // TODO: Maybe make selectable in GUI?
    private static final String CONFIG_PATH = FieldImages.k2022RapidReactFieldConfig;

    private final FieldImageConfig config;
    private final PImage img;

    private final ImBoolean show;

    public FieldImageLayer() {
        config = FieldImageConfig.load(CONFIG_PATH);
        img = config.loadFieldImage();

        show = new ImBoolean(true);
    }

    @Override
    public String getName() {
        return "Field Image";
    }

    @Override
    public void draw(PGraphics g, float metersScale) {
        if (!show.get())
            return;

        float width = (float) config.fieldUnit.toMeters(config.fieldSize.width);
        float height = (float) config.fieldUnit.toMeters(config.fieldSize.height);
        g.rotate((float) Math.PI / 2); // WPILib field images are oriented incorrectly
        g.image(
                img,
                -width/2, -height/2,
                width, height,
                config.fieldCorners.topLeft.x, config.fieldCorners.bottomRight.y,
                config.fieldCorners.bottomRight.x, config.fieldCorners.topLeft.y
        );
    }

    @Override
    public void showGui() {
        ImGui.checkbox("Show", show);
        ImGui.separator();
        ImGui.textWrapped("Note: This image is for reference, and is not completely accurate to the actual field.");
    }
}
