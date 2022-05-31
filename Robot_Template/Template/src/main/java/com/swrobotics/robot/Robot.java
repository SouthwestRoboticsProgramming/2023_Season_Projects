package com.swrobotics.robot;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.swrobotics.lib.motor.CTREMotor;
import com.swrobotics.lib.motor.NewMotor;
import com.swrobotics.lib.swerve.SwerveDriveHelper;
import com.swrobotics.lib.swerve.SwerveModule;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.RobotBase;

public final class Robot extends RobotBase {
  @Override
  public void startCompetition() {
    HAL.observeUserProgramStarting();
  }

  public void test() {

    SwerveModule[] modules = [
      new SwerveModule(new CTREMotor(new), drive, encoder, position)
    ]
  }

  @Override
  public void endCompetition() {
    
  }
}
