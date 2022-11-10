package com.swrobotics.shufflelog.tool.profile;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.ShuffleLog;
import com.swrobotics.shufflelog.profile.MemoryStats;
import com.swrobotics.shufflelog.profile.ProfileNode;

public final class RobotProfilerTool extends ProfilerTool {
    private ProfileNode lastData;
    private MemoryStats lastMem;

    public RobotProfilerTool(ShuffleLog log) {
        super(log, "Robot Profiler");

        log.getMsg().addHandler("Profiler:Data", this::onProfileData);
        lastMem = new MemoryStats(0, 0, 0);
    }

    private ProfileNode readProfileNode(MessageReader reader, ProfileNode parent) {
        String name = reader.readString();
        long self = reader.readLong();
        long total = reader.readLong();
        ProfileNode node = new ProfileNode(name, parent, self, total);

        int childCount = reader.readInt();
        for (int i = 0; i < childCount; i++) {
            node.getChildren().add(readProfileNode(reader, node));
        }

        return node;
    }

    private void onProfileData(String type, MessageReader reader) {
        lastData = readProfileNode(reader, null);

        long free = reader.readLong();
        long max = reader.readLong();
        long total = reader.readLong();
        lastMem = new MemoryStats(free, max, total);
    }

    @Override
    protected void showHeader() {}

    @Override
    protected ProfileNode getLastData() {
        return lastData;
    }

    @Override
    protected MemoryStats getMemStats() {
        return lastMem;
    }
}
