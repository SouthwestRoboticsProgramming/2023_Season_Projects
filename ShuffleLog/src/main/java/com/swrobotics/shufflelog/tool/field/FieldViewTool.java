package com.swrobotics.shufflelog.tool.field;

import com.swrobotics.shufflelog.ShuffleLog;
import com.swrobotics.shufflelog.math.Matrix4f;
import com.swrobotics.shufflelog.math.Vector3f;
import com.swrobotics.shufflelog.tool.ViewportTool;
import com.swrobotics.shufflelog.tool.field.img.FieldImageLayer;
import com.swrobotics.shufflelog.tool.field.img.FieldVectorLayer;
import com.swrobotics.shufflelog.tool.field.path.PathfindingLayer;
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
import processing.core.PMatrix;
import processing.core.PMatrix3D;
import processing.opengl.PGraphicsOpenGL;

import java.util.ArrayList;
import java.util.List;

import static processing.core.PConstants.*;

public final class FieldViewTool extends ViewportTool {
    public static final double WIDTH = 8.2296;
    public static final double HEIGHT = 16.4592;

    private static final float SMOOTH = 12;

    private final List<FieldLayer> layers;

    // TODO: Move into layer
    private final List<ReferenceTag> tags;
    private ReferenceTag selectedTag;

    private Matrix4f projection, view;
    private Matrix4f gizmoTarget;
    private int gizmoOp, gizmoMode;
    // END TODO

    private final SmoothFloat cameraRotX, cameraRotY;
    private final SmoothFloat cameraPosX, cameraPosY, cameraPosZ;

    public FieldViewTool(ShuffleLog log) {
        // Be in 3d mode
        super(log, "Field View", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse, P3D);

        layers = new ArrayList<>();
        layers.add(new FieldImageLayer());
        layers.add(new MeterGridLayer());
        layers.add(new FieldVectorLayer());
        layers.add(new PathfindingLayer(log.getMsg()));

        tags = new ArrayList<>();
        tags.add(new ReferenceTag("Tag 1", 1));
        tags.add(new ReferenceTag("Tag 2", 2));

        cameraRotX = new SmoothFloat(SMOOTH, 0);
        cameraRotY = new SmoothFloat(SMOOTH, 0);

        cameraPosX = new SmoothFloat(SMOOTH, 0);
        cameraPosY = new SmoothFloat(SMOOTH, 0);
        cameraPosZ = new SmoothFloat(SMOOTH, 10);

        gizmoOp = Operation.TRANSLATE;
        gizmoMode = Mode.WORLD;
    }

    private void setPMatrix(PMatrix dst, Matrix4f src) {
        dst.set(
                src.m00, src.m01, src.m02, src.m03,
                src.m10, src.m11, src.m12, src.m13,
                src.m20, src.m21, src.m22, src.m23,
                src.m30, src.m31, src.m32, src.m33
        );
    }

    @Override
    protected void drawViewportContent(PGraphics pGraphics) {
        cameraPosX.step(); cameraPosY.step(); cameraPosZ.step();
        cameraRotX.step(); cameraRotY.step();

        // Custom projection and view matrices because Processing's defaults are pretty bad
        PGraphicsOpenGL g = (PGraphicsOpenGL) pGraphics;
        projection = new Matrix4f().perspective((float) Math.toRadians(80), g.width / (float) g.height, 0.01f, 1000f);
        setPMatrix(g.projection, projection);
        view = new Matrix4f()
                .translate(new Vector3f(cameraPosX.get(), cameraPosY.get(), cameraPosZ.get()))
                .rotateZ(cameraRotY.get())
                .rotateX(cameraRotX.get())
                .invert();
        setPMatrix(g.modelview, view);
        g.modelviewInv.set(g.modelview);
        g.modelviewInv.invert();
        g.camera.set(g.modelview);
        g.cameraInv.set(g.modelviewInv);
        g.updateProjmodelview();

        g.background(0);

        // Basic lighting
        g.directionalLight(255, 255, 255, 1, 1, 1);
        g.ambientLight(175, 175, 175);

        int i = 0;
        for (FieldLayer layer : layers) {
            g.pushMatrix();
            g.translate(0, 0, 0.01f * i++);
            layer.draw(g, 1);
            g.popMatrix();
        }

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
        gizmoTarget = tag.getTransform();
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
                float[] transArr = gizmoTarget.getColumnMajor();
                ImGuizmo.setRect(x, y, size.x, size.y);
                ImGuizmo.setAllowAxisFlip(true);
                ImGuizmo.manipulate(view.getColumnMajor(), projection.getColumnMajor(), transArr, gizmoOp, gizmoMode);
                if (ImGuizmo.isUsing()) {
                    gizmoTarget.setFromColumnMajor(transArr);
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

                if (io.getMouseDown(ImGuiMouseButton.Left)) {
                    // Pan

                    float deltaX = io.getMouseDeltaX();
                    float deltaY = io.getMouseDeltaY();

                    Vector3f up = viewRotInv.transformPosition(new Vector3f(0, 1, 0)).normalize();
                    Vector3f right = viewRotInv.transformPosition(new Vector3f(1, 0, 0)).normalize();

                    float scaleUp = deltaY * 0.01f;
                    float scaleRight = deltaX * -0.01f;

                    cameraPosX.set(cameraPosX.getTarget() + up.x * scaleUp + right.x * scaleRight);
                    cameraPosY.set(cameraPosY.getTarget() + up.y * scaleUp + right.y * scaleRight);
                    cameraPosZ.set(cameraPosZ.getTarget() + up.z * scaleUp + right.z * scaleRight);
                }

                if (io.getMouseDown(ImGuiMouseButton.Right)) {
                    // Turn

                    float deltaX = io.getMouseDeltaX();
                    float deltaY = io.getMouseDeltaY();

                    cameraRotX.set(cameraRotX.getTarget() + deltaY * 0.007f);
                    cameraRotY.set(cameraRotY.getTarget() + deltaX * 0.007f);
                }

                float scroll = io.getMouseWheel();
                Vector3f forward = viewRotInv.transformPosition(new Vector3f(0, 0, -1)).normalize();

                float scale = scroll * -0.2f;
                cameraPosX.set(cameraPosX.getTarget() + forward.x * scale);
                cameraPosY.set(cameraPosY.getTarget() + forward.y * scale);
                cameraPosZ.set(cameraPosZ.getTarget() + forward.z * scale);
            }

            ImGui.tableNextColumn();

            for (FieldLayer layer : layers) {
                if (ImGui.collapsingHeader(layer.getName())) {
                    ImGui.indent();
                    layer.showGui();
                    ImGui.unindent();
                }
            }

            ImGui.separator();
            for (ReferenceTag tag : tags) {
                if (ImGui.selectable(tag.getName(), tag.equals(selectedTag))) {
                    select(tag);
                }
            }

            ImGui.endTable();
        }
    }
}
