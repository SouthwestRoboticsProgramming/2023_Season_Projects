package com.team2129.lib.profile;

import java.util.ArrayList;
import java.util.List;

public final class ProfileNode {
    private final String name;
    private final ProfileNode parent;
    private final List<ProfileNode> children;
    private long total;
    private long accumulator;
    private long startTime, pauseTime;

    public ProfileNode(String name, ProfileNode parent) {
        this.name = name;
        this.parent = parent;
        children = new ArrayList<>();
        accumulator = 0;
        total = 0;
    }

    public ProfileNode(String name, ProfileNode parent, long accumulator, long total) {
        this.name = name;
        this.parent = parent;
        children = new ArrayList<>();
        this.accumulator = accumulator;
        this.total = total;
    }

    public void begin() {
        startTime = System.nanoTime();
        unpause();
    }

    public void pause() {
        accumulator += System.nanoTime() - pauseTime;
    }

    public void unpause() {
        pauseTime = System.nanoTime();
    }

    public void end() {
        pause();
        total = System.nanoTime() - startTime;
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
        // Intentionally not defensively copied
        return children;
    }

    public long getSelfTimeNanoseconds() {
        return accumulator;
    }

    public long getTotalTimeNanoseconds() { return total; }
}
