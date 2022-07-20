package com.swrobotics.bert;

import com.swrobotics.bert.shuffle.TunableBoolean;
import com.swrobotics.bert.subsystems.Subsystem;

public class SubsystemSwitch {
    private final Subsystem system;
    private final TunableBoolean enable;

    public SubsystemSwitch(Subsystem system, TunableBoolean enable) {
        this.system = system;
        this.enable = enable;

        if (system == null) {
            return;
        }

        enable.onChange(this::update);
        update();
    }

    private void update() {
        boolean enabled = enable.get();
        Scheduler scheduler = Scheduler.get();

        if (enabled && !scheduler.hasSubsystem(system)) {
            scheduler.addSubsystem(system);
        } else if (!enabled && Scheduler.get().hasSubsystem(system)) {
            scheduler.removeSubsystem(system);
        }
    }
}
