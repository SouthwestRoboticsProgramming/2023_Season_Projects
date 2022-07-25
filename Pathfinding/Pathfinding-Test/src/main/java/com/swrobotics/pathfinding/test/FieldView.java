package com.swrobotics.pathfinding.test;

import processing.core.PApplet;
import processing.core.PGraphics;

public final class FieldView extends ProcessingView {
    public FieldView(PApplet app) {
        super(app);
    }

    @Override
    protected void drawContent(PGraphics g) {
        g.background(128);
        g.stroke(0);
        g.fill(255);
        g.rect(10, 10, 100, 100);
    }
}
