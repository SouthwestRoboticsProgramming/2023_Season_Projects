package com.swrobotics.bert.profiler;

import java.util.ArrayList;
import java.util.List;

public final class ProfileNode {
    private final String name;
    private final ProfileNode parent;
    private final List<ProfileNode> children;
    private long accumulator;
    private long startTime;

    public ProfileNode(String name, ProfileNode parent) {
        this.name = name;
        this.parent = parent;
        children = new ArrayList<>();
        accumulator = 0;
    }

    public void begin() {
        startTime = System.nanoTime();
    }

    public void end() {
        accumulator += System.nanoTime() - startTime;
    }

    public void addChild(ProfileNode child) {
        children.add(child);
    }

    public String getName() {
        return name;
    }

    public ProfileNode getParent() {
        return parent;
    }

    public List<ProfileNode> getChildren() {
        // Intentionally not defensively copied for performance
        return children;
    }

    public long getElapsedTimeNanoseconds() {
        return accumulator;
    }
}
