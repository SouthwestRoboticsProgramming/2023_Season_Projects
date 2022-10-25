package com.swrobotics.shufflelog.tool.field.tag;

import com.swrobotics.shufflelog.tool.field.FieldLayer;
import com.swrobotics.shufflelog.tool.field.FieldViewTool;
import imgui.ImGui;
import imgui.type.ImBoolean;
import processing.core.PGraphics;
import processing.core.PMatrix3D;

import java.util.ArrayList;
import java.util.List;

import static com.swrobotics.shufflelog.util.ProcessingUtils.setPMatrix;

public final class TagTrackerLayer implements FieldLayer {
    private final ImBoolean showTags = new ImBoolean(true);

    private final FieldViewTool tool;
    private final List<ReferenceTag> tags;
    private ReferenceTag selectedTag;

    public TagTrackerLayer(FieldViewTool tool) {
        this.tool = tool;

        tags = new ArrayList<>();
        tags.add(new ReferenceTag("Tag 1", 1));
        tags.add(new ReferenceTag("Tag 2", 2));

        selectedTag = null;
    }

    @Override
    public String getName() {
        return "Tag Tracker";
    }

    @Override
    public void draw(PGraphics g, float metersScale) {
        if (!showTags.get())
            return;

        g.fill(0, 255, 0, 64);
        g.stroke(0, 255, 0);
        g.strokeWeight(4);
        PMatrix3D txMat = new PMatrix3D();
        for (ReferenceTag tag : tags) {
            g.pushMatrix();
            setPMatrix(txMat, tag.getTransform());
            g.applyMatrix(txMat);

            float s = (float) tag.getSize();
            g.rect(-s/2, -s/2, s, s);
            g.popMatrix();
        }
    }

    private void select(ReferenceTag tag) {
        selectedTag = tag;
        tool.setGizmoTarget(tag);
    }

    @Override
    public void showGui() {
        ImGui.checkbox("Show Tags", showTags);

        for (ReferenceTag tag : tags) {
            if (ImGui.selectable(tag.getName(), tag.equals(selectedTag))) {
                select(tag);
            }
        }
    }

    @Override
    public boolean shouldOffset() {
        // This is 3D, we don't need to apply 2D layer stacking
        return false;
    }
}
