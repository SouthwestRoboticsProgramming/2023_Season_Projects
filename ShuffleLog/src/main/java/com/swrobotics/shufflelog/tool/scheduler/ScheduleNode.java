package com.swrobotics.shufflelog.tool.scheduler;

import java.util.UUID;

public abstract class ScheduleNode {
    private String typeName;
    private UUID id;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
