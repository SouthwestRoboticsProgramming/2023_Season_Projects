package com.swrobotics.shufflelog.tool.blockauto;

import com.swrobotics.shufflelog.tool.blockauto.comp.BlockComponent;
import processing.core.PFont;
import processing.core.PGraphics;

import java.util.Arrays;
import java.util.List;

public final class Block {
    private static final float COMPONENT_SPACING = 5;
    private static final float PADDING = 5;

    private final List<BlockComponent> components;

    public Block(BlockComponent... components) {
        this.components = Arrays.asList(components);
    }

    public float getRenderedWidth(PFont font) {
        float width = (components.size() - 1) * COMPONENT_SPACING;
        for (BlockComponent c : components) {
            width += c.getWidth(font);
        }
        return width + 2 * PADDING;
    }

    public float getRenderedHeight(PFont font) {
        float maxHeight = 0;
        for (BlockComponent c : components) {
            maxHeight += Math.max(maxHeight, c.getHeight(font));
        }

        return maxHeight + 2 * PADDING;
    }

    public void render(PGraphics g, float x, float y) {
        float w = getRenderedWidth(g.textFont);
        float h = getRenderedHeight(g.textFont);

        g.fill(0, 0, 255);
        g.stroke(0, 0, 128);
        g.rect(x, y, w, h);

        x += PADDING;
        y += Math.floor(h / 2); // No fractional pixel positions because they look bad

        for (BlockComponent c : components) {
            float halfCompH = (float) Math.floor(c.getHeight(g.textFont) / 2);
            c.draw(g, x, y - halfCompH);
            x += c.getWidth(g.textFont) + COMPONENT_SPACING;
        }
    }
}
