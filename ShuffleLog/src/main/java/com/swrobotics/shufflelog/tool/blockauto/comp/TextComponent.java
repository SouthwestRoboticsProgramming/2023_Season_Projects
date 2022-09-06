package com.swrobotics.shufflelog.tool.blockauto.comp;

import processing.core.PFont;
import processing.core.PGraphics;

public final class TextComponent implements BlockComponent {
    private final String text;

    public TextComponent(String text) {
        this.text = text;
    }

    @Override
    public float getWidth(PFont font) {
        float w = 0;
        for (char c : text.toCharArray()) {
            w += font.width(c) * font.getSize();
        }
        return w;
    }

    @Override
    public float getHeight(PFont font) {
        return font.ascent() * font.getSize();
    }

    @Override
    public void draw(PGraphics g, float x, float y) {
        g.fill(255);
        g.text(text, x, y + g.textAscent());
    }
}
