package com.swrobotics.robot;

import com.swrobotics.robot.input.Input;
import com.swrobotics.robot.tests.MotorTest;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.RobotBase;

public final class Robot extends RobotBase {
  private MotorTest test = new MotorTest();
  private Input input = new Input();

  @Override
  public void startCompetition() {
    HAL.observeUserProgramStarting();

    // TODO: Is this periodic?

  }

  @Override
  public void endCompetition() {
    
  }
}
