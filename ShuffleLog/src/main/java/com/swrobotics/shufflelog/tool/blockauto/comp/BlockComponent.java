package com.swrobotics.shufflelog.tool.blockauto.comp;

import processing.core.PFont;
import processing.core.PGraphics;

public interface BlockComponent {
    float getWidth(PFont font);
    float getHeight(PFont font);
    void draw(PGraphics g, float x, float y);
}
