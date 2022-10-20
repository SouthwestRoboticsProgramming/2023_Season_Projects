package com.team2129.lib.profile;

public final class Profiler {
    private static ProfileNode root, current, last;

    private Profiler() {
        throw new AssertionError();
    }

    public static void beginMeasurements(String rootName) {
        root = new ProfileNode(rootName, null);
        current = root;
        root.begin();
    }

    public static void push(String name) {
        ProfileNode next = new ProfileNode(name, current);
        current.pause();
        current.addChild(next);
        current = next;
        current.begin();
    }

    public static void pop() {
        current.end();
        current = current.getParent();
        current.unpause();
    }

    public static void endMeasurements() {
        current.end();
        last = root;
    }

    public static ProfileNode getLastData() {
        return last;
    }
}
