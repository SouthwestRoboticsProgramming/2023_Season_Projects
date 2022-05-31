package com.swrobotics.robot;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.RobotBase;

public final class Robot extends RobotBase {
  @Override
  public void startCompetition() {
    HAL.observeUserProgramStarting();
  }

  @Override
  public void endCompetition() {
    
  }
}
