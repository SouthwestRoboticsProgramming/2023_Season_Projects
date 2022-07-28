package com.swrobotics.shufflelog.tool.profile;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.profile.ProfileNode;

public final class RobotProfilerTool extends ProfilerTool {
    private ProfileNode lastData;

    public RobotProfilerTool(MessengerClient msg) {
        super("Robot Profiler");

        msg.addHandler("Profiler:Data", this::onProfileData);
    }

    private ProfileNode readProfileNode(MessageReader reader, ProfileNode parent) {
        String name = reader.readString();
        long time = reader.readLong();
        ProfileNode node = new ProfileNode(name, parent, time);

        int childCount = reader.readInt();
        for (int i = 0; i < childCount; i++) {
            node.getChildren().add(readProfileNode(reader, node));
        }

        return node;
    }

    private void onProfileData(String type, MessageReader reader) {
        lastData = readProfileNode(reader, null);
    }

    @Override
    protected void showHeader() {}

    @Override
    protected ProfileNode getLastData() {
        return lastData;
    }
}
