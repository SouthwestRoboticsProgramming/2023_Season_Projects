package com.swrobotics.shufflelog.tool.field.tag;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.math.Matrix4f;
import com.swrobotics.shufflelog.math.Vector3f;
import com.swrobotics.shufflelog.tool.ToolConstants;
import com.swrobotics.shufflelog.tool.field.FieldLayer;
import com.swrobotics.shufflelog.tool.field.FieldViewTool;
import com.swrobotics.shufflelog.tool.field.GizmoTarget;
import com.swrobotics.shufflelog.util.Cooldown;
import imgui.ImGui;
import imgui.type.ImBoolean;
import processing.core.PGraphics;
import processing.core.PMatrix3D;

import java.util.ArrayList;
import java.util.List;

import static com.swrobotics.shufflelog.util.ProcessingUtils.setPMatrix;

public final class TagTrackerLayer implements FieldLayer {
    private static final String MSG_QUERY_ENVIRONMENT = "TagTracker:QueryEnvironment";
    private static final String MSG_ENVIRONMENT = "TagTracker:Environment";

    private final ImBoolean showTags = new ImBoolean(true);

    private boolean hasEnvironment;
    private final Cooldown queryEnvironmentCooldown;

    private final FieldViewTool tool;
    private final MessengerClient msg;
    private final List<ReferenceTag> tags;
    private final List<Camera> cameras;
    private GizmoTarget selection;
    private RobotPose robotPose;

    public TagTrackerLayer(FieldViewTool tool, MessengerClient msg) {
        this.tool = tool;
        this.msg = msg;

        hasEnvironment = false;
        queryEnvironmentCooldown = new Cooldown(ToolConstants.MSG_QUERY_COOLDOWN_TIME);

        tags = new ArrayList<>();
        cameras = new ArrayList<>();
        selection = null;

        msg.addHandler(MSG_ENVIRONMENT, this::onEnvironment);

        // TODO: This should come from the tag tracker estimate
        robotPose = new RobotPose(new Vector3f(1, 1, 1), new Matrix4f().translate(new Vector3f(0, -4, 0)));
    }

    @Override
    public String getName() {
        return "Tag Tracker";
    }

    private Matrix4f readMatrix(MessageReader reader) {
        float[] data = new float[16];
        for (int i = 0; i < data.length; i++)
            data[i] = reader.readFloat();
        return Matrix4f.fromColumnMajor(data);
    }

    private void onEnvironment(String type, MessageReader reader) {
        int tagCount = reader.readInt();
        for (int i = 0; i < tagCount; i++) {
            double size = reader.readDouble();
            int id = reader.readInt();
            Matrix4f transform = readMatrix(reader);
            tags.add(new ReferenceTag("Tag " + id, size, transform));
        }

        int cameraCount = reader.readInt();
        for (int i = 0; i < cameraCount; i++) {
            String name = reader.readString();
            int port = reader.readInt();
            Matrix4f transform = readMatrix(reader);
            cameras.add(new Camera(name + " (" + port + ")", port, transform, robotPose.getTransform()));
        }

        hasEnvironment = true;
    }

    @Override
    public void draw(PGraphics g, float metersScale) {
        if (!hasEnvironment && queryEnvironmentCooldown.request())
            msg.send(MSG_QUERY_ENVIRONMENT);

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

        // Robot bounding box
        g.noFill();
        g.stroke(185, 66, 245);
        g.strokeWeight(3);
        g.pushMatrix();
        setPMatrix(txMat, robotPose.getTransform());
        g.applyMatrix(txMat);
        Vector3f sz = robotPose.getSize();
        g.translate(0, 0, sz.z/2);
        g.box(sz.x, sz.y, sz.z);
        g.popMatrix();

        g.noFill();
        g.stroke(255, 255, 0);
        g.strokeWeight(4);
        float s = 0.1f;
        for (Camera cam : cameras) {
            g.pushMatrix();
            setPMatrix(txMat, cam.getTransform());
            g.applyMatrix(txMat);

            // Pyramid thing that isn't a pyramid
            g.line(0, 0, 0, -s, s, s*-2);
            g.line(0, 0, 0, s, s, s*-2);
            g.line(0, 0, 0, s, -s, s*-2);
            g.line(0, 0, 0, -s, -s, s*-2);

            g.popMatrix();
        }
    }

    private void select(GizmoTarget tag) {
        selection = tag;
        tool.setGizmoTarget(tag);
    }

    @Override
    public void showGui() {
        ImGui.checkbox("Show Tags", showTags);

        for (ReferenceTag tag : tags) {
            if (ImGui.selectable(tag.getName(), tag.equals(selection))) {
                select(tag);
            }
        }

        ImGui.separator();

        for (Camera camera : cameras) {
            if (ImGui.selectable(camera.getName(), camera.equals(selection))) {
                select(camera);
            }
        }
    }

    @Override
    public boolean shouldOffset() {
        // This is 3D, we don't need to apply 2D layer stacking
        return false;
    }
}
