package com.swrobotics.shufflelog.tool.field;

import processing.core.PGraphics;

public interface FieldLayer {
    /**
     * Gets the name of this layer for use in ImGui.
     * This name is required to be unique per layer.
     *
     * @return name
     */
    String getName();

    /**
     * Draws the content of this layer. The expected units are in
     * meters.
     *
     * @param g graphics to draw with
     */
    void draw(PGraphics g, float metersScale);

    /**
     * Draws the ImGui content for this layer.
     */
    void showGui();
}
