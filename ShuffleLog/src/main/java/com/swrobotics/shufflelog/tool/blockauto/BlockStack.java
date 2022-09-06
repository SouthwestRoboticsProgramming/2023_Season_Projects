package com.swrobotics.shufflelog.tool.blockauto;

import processing.core.PGraphics;

import java.util.Arrays;
import java.util.List;

public final class BlockStack {
    private final List<Block> blocks;
    private float x, y;

    public BlockStack(Block... blocks) {
        this.blocks = Arrays.asList(blocks);
    }

    public void draw(PGraphics g) {
        float posY = y;
        for (Block block : blocks) {
            block.render(g, x, posY);
            posY += block.getRenderedHeight(g.textFont);
        }
    }
}
