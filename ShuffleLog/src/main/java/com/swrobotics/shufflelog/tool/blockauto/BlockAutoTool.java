package com.swrobotics.shufflelog.tool.blockauto;

import com.swrobotics.shufflelog.ShuffleLog;
import com.swrobotics.shufflelog.tool.ViewportTool;
import com.swrobotics.shufflelog.tool.blockauto.comp.TextComponent;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;

import static imgui.ImGui.*;

public final class BlockAutoTool extends ViewportTool {
    private final List<BlockCategory> categories;
    private final List<BlockStack> stacks;

    public BlockAutoTool(ShuffleLog log) {
        super(log, "Block Autonomous", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        categories = new ArrayList<>();
        categories.add(new BlockCategory(log, "Control", new Block(new TextComponent("Test block")), new Block(new TextComponent("Test block with a longer name"))));

        stacks = new ArrayList<>();
        stacks.add(new BlockStack(
                new Block(new TextComponent("Block 1")),
                new Block(new TextComponent("Block 2 (lllllooooooooooooooonnnnnnnnnnnngggggggg)")),
                new Block(new TextComponent("Block 3"))
        ));
    }

    @Override
    protected void drawViewportContent(PGraphics g) {
        g.background(15, 15, 150);

        for (BlockStack stack : stacks) {
            stack.draw(g);
        }
    }

    @Override
    protected void drawGuiContent() {
        if (beginTable("view_layout", 2, ImGuiTableFlags.BordersInner | ImGuiTableFlags.Resizable)) {
            tableNextColumn();
            drawViewport();

            tableNextColumn();
            text("Block palette");
            separator();

            for (BlockCategory cat : categories) {
                cat.draw();
            }

            endTable();
        }
    }
}
