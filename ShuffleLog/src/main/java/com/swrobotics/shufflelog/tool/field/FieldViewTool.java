package com.swrobotics.shufflelog.tool.field;

import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.ShuffleLog;
import com.swrobotics.shufflelog.math.Matrix4f;
import com.swrobotics.shufflelog.math.Vector3f;
import com.swrobotics.shufflelog.tool.ViewportTool;
import com.swrobotics.shufflelog.tool.field.img.FieldImageLayer;
import com.swrobotics.shufflelog.tool.field.img.FieldVectorLayer;
import com.swrobotics.shufflelog.tool.field.path.PathfindingLayer;
import com.swrobotics.shufflelog.tool.field.tag.TagTrackerLayer;
import com.swrobotics.shufflelog.util.SmoothFloat;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;

import java.util.ArrayList;
import java.util.List;

import static com.swrobotics.shufflelog.util.ProcessingUtils.setPMatrix;
import static processing.core.PConstants.*;

public final class FieldViewTool extends ViewportTool {
    public static final double WIDTH = 8.2296;
    public static final double HEIGHT = 16.4592;

    private static final float SMOOTH = 12;

    private final List<FieldLayer> layers;

    private Matrix4f projection, view;
    private GizmoTarget gizmoTarget;
    private int gizmoOp, gizmoMode;

    private final SmoothFloat cameraRotX, cameraRotY;
    private final SmoothFloat cameraTargetX, cameraTargetY, cameraTargetZ;
    private final SmoothFloat cameraDist;

    public FieldViewTool(ShuffleLog log) {
        // Be in 3d mode
        super(log, "Field View", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse, P3D);

        MessengerClient msg = log.getMsg();
        layers = new ArrayList<>();
        layers.add(new FieldImageLayer());
        layers.add(new MeterGridLayer());
        layers.add(new FieldVectorLayer());
        layers.add(new PathfindingLayer(msg));
        layers.add(new TagTrackerLayer(this, msg));

        cameraRotX = new SmoothFloat(SMOOTH, 0);
        cameraRotY = new SmoothFloat(SMOOTH, 0);
        cameraTargetX = new SmoothFloat(SMOOTH, 0);
        cameraTargetY = new SmoothFloat(SMOOTH, 0);
        cameraTargetZ = new SmoothFloat(SMOOTH, 0);
        cameraDist = new SmoothFloat(SMOOTH, 10);

        gizmoOp = Operation.TRANSLATE;
        gizmoMode = Mode.WORLD;
    }

    @Override
    protected void drawViewportContent(PGraphics pGraphics) {
        cameraRotX.step(); cameraRotY.step(); cameraDist.step();
        cameraTargetX.step(); cameraTargetY.step(); cameraTargetZ.step();

        // Custom projection and view matrices because Processing's defaults are pretty bad
        PGraphicsOpenGL g = (PGraphicsOpenGL) pGraphics;
        projection = new Matrix4f().perspective((float) Math.toRadians(80), g.width / (float) g.height, 0.01f, 1000f);
        setPMatrix(g.projection, projection);
        view = new Matrix4f()
                .translate(new Vector3f(cameraTargetX.get(), cameraTargetY.get(), cameraTargetZ.get()))
                .rotateZ(cameraRotY.get())
                .rotateX(cameraRotX.get())
                .translate(new Vector3f(0, 0, cameraDist.get()))
                .invert();
        setPMatrix(g.modelview, view);
        g.modelviewInv.set(g.modelview);
        g.modelviewInv.invert();
        g.camera.set(g.modelview);
        g.cameraInv.set(g.modelviewInv);
        g.updateProjmodelview();

        g.background(0);

        // Basic lighting for 3d objects
        g.directionalLight(255, 255, 255, 1, 1, 1);
        g.ambientLight(175, 175, 175);

        float offset = 0;
        for (FieldLayer layer : layers) {
            g.pushMatrix();
            if (layer.shouldOffset()) {
                g.translate(0, 0, offset);
                offset += 0.01;
            }
            layer.draw(g, 1);
            g.popMatrix();
        }
    }

    public void setGizmoTarget(GizmoTarget target) {
        gizmoTarget = target;
    }

    @Override
    protected void drawGuiContent() {
        if (ImGui.beginTable("layout", 2, ImGuiTableFlags.BordersInner | ImGuiTableFlags.Resizable)) {
            ImGui.tableNextColumn();

            ImGui.alignTextToFramePadding();
            ImGui.text("Tool:");
            ImGui.sameLine();
            if (ImGui.radioButton("Move", gizmoOp == Operation.TRANSLATE))
                gizmoOp = Operation.TRANSLATE;
            ImGui.sameLine();
            if (ImGui.radioButton("Rotate", gizmoOp == Operation.ROTATE))
                gizmoOp = Operation.ROTATE;

            ImGui.alignTextToFramePadding();
            ImGui.text("Space:");
            ImGui.sameLine();
            if (ImGui.radioButton("World", gizmoMode == Mode.WORLD))
                gizmoMode = Mode.WORLD;
            ImGui.sameLine();
            if (ImGui.radioButton("Local", gizmoMode == Mode.LOCAL))
                gizmoMode = Mode.LOCAL;

            ImGui.separator();

            ImVec2 pos = ImGui.getWindowPos();
            ImVec2 cursor = ImGui.getCursorPos();
            ImVec2 size = ImGui.getContentRegionAvail();
            drawViewport(size.x, size.y, false);
            boolean hovered = ImGui.isItemHovered();

            float x = pos.x + cursor.x;
            float y = pos.y + cursor.y;

            boolean gizmoConsumesMouse = false;
            if (gizmoTarget != null) {
                float[] transArr = gizmoTarget.getTransform().getColumnMajor();
                ImGuizmo.setRect(x, y, size.x, size.y);
                ImGuizmo.setAllowAxisFlip(true);
                ImGuizmo.manipulate(view.getColumnMajor(), projection.getColumnMajor(), transArr, gizmoOp, gizmoMode);
                if (ImGuizmo.isUsing()) {
                    gizmoTarget.setTransform(Matrix4f.fromColumnMajor(transArr));
                }
                gizmoConsumesMouse = ImGuizmo.isOver();
            }

            if (hovered && !gizmoConsumesMouse) {
                ImGuiIO io = ImGui.getIO();

                Matrix4f viewRotInv = new Matrix4f(view).invert();
                // Remove translation
                viewRotInv.m03 = 0;
                viewRotInv.m13 = 0;
                viewRotInv.m23 = 0;

                if (io.getMouseDown(ImGuiMouseButton.Right)) {
                    // Pan

                    float deltaX = io.getMouseDeltaX();
                    float deltaY = io.getMouseDeltaY();

                    Vector3f up = viewRotInv.transformPosition(new Vector3f(0, 1, 0)).normalize();
                    Vector3f right = viewRotInv.transformPosition(new Vector3f(1, 0, 0)).normalize();

                    float dist = cameraDist.get();
                    float scaleUp = deltaY * 0.002f * dist;
                    float scaleRight = deltaX * -0.002f * dist;

                    cameraTargetX.set(cameraTargetX.getTarget() + up.x * scaleUp + right.x * scaleRight);
                    cameraTargetY.set(cameraTargetY.getTarget() + up.y * scaleUp + right.y * scaleRight);
                    cameraTargetZ.set(cameraTargetZ.getTarget() + up.z * scaleUp + right.z * scaleRight);
                }

                if (io.getMouseDown(ImGuiMouseButton.Left)) {
                    // Turn

                    float deltaX = io.getMouseDeltaX();
                    float deltaY = io.getMouseDeltaY();

                    cameraRotX.set(cameraRotX.getTarget() - deltaY * 0.007f);
                    cameraRotY.set(cameraRotY.getTarget() - deltaX * 0.007f);
                }

                float scroll = io.getMouseWheel();
                float scale = 1 + scroll * -0.05f;
                cameraDist.set(cameraDist.getTarget() * scale);
            }

            ImGui.tableNextColumn();

            for (FieldLayer layer : layers) {
                if (ImGui.collapsingHeader(layer.getName())) {
                    ImGui.pushID(layer.getName());
                    ImGui.indent();
                    layer.showGui();
                    ImGui.unindent();
                    ImGui.popID();
                }
            }

            ImGui.endTable();
        }
    }
}
