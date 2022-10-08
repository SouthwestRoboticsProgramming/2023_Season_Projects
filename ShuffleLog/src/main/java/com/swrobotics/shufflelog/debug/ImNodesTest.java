package com.swrobotics.shufflelog.debug;

import static imgui.ImGui.*;
import static imgui.extension.imnodes.ImNodes.*;

public final class ImNodesTest {
    public static void show() {
        if (begin("Node editor")) {
            beginNodeEditor();
            beginNode(1);

            beginNodeTitleBar();
            textUnformatted("Simple node");
            endNodeTitleBar();

            beginInputAttribute(2);
            text("Input");
            endInputAttribute();

            beginOutputAttribute(3);
            indent(40);
            text("Output");
            endOutputAttribute();

            endNode();
            endNodeEditor();
        }
        end();
    }
}
