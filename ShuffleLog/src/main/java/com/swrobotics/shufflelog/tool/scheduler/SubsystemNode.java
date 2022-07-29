package com.swrobotics.shufflelog.tool.scheduler;

import java.util.ArrayList;
import java.util.List;

public final class SubsystemNode extends ScheduleNode {
    private final List<ScheduleNode> children;

    public SubsystemNode() {
        children = new ArrayList<>();
    }

    public void addChild(ScheduleNode child) {
        children.add(child);
    }

    public List<ScheduleNode> getChildren() {
        return children;
    }
}
