package com.swrobotics.bert.profiler;

public final class Profiler {
    private static final Profiler INSTANCE = new Profiler();
    public static Profiler get() {
        return INSTANCE;
    }

    private ProfileNode root;
    private ProfileNode current;

    public void beginMeasurements(String rootName) {
        root = new ProfileNode(rootName, null);
        current = root;
        root.begin();
    }

    public void push(String name) {
        ProfileNode next = new ProfileNode(name, current);
        current.addChild(next);
        current = next;
        current.begin();
    }

    public void pop() {
        current.end();
        current = current.getParent();
        current.begin();
    }

    public void endMeasurements() {
        current.end();
    }

    public ProfileNode getData() {
        return root;
    }
}
