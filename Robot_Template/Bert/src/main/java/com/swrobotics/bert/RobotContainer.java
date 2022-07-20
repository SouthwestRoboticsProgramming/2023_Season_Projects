package com.swrobotics.bert;

import com.swrobotics.bert.commands.MessengerReadCommand;
import com.swrobotics.bert.commands.PublishLocalizationCommand;
import com.swrobotics.bert.commands.taskmanager.TaskManagerSetupCommand;
import com.swrobotics.bert.control.Input;
import com.swrobotics.bert.subsystems.Lights;
import com.swrobotics.bert.subsystems.Localization;
import com.swrobotics.bert.subsystems.PDP;
import com.swrobotics.bert.subsystems.auto.Autonomous;
import com.swrobotics.bert.subsystems.auto.Pathfinding;
import com.swrobotics.bert.subsystems.camera.CameraController;
import com.swrobotics.bert.subsystems.camera.Limelight;
import com.swrobotics.bert.subsystems.climber.Climber;
import com.swrobotics.bert.subsystems.drive.SwerveDrive;
import com.swrobotics.bert.subsystems.drive.SwerveDriveController;
import com.swrobotics.bert.subsystems.intake.Intake;
import com.swrobotics.bert.subsystems.intake.IntakeController;
import com.swrobotics.bert.subsystems.shooter.BallDetector;
import com.swrobotics.bert.subsystems.shooter.Flywheel;
import com.swrobotics.bert.subsystems.shooter.Hopper;
import com.swrobotics.bert.subsystems.shooter.NewHood;
import com.swrobotics.bert.subsystems.shooter.ShooterController;
import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.taskmanager.api.TaskManagerAPI;

import edu.wpi.first.wpilibj.SPI;

import static com.swrobotics.bert.constants.CommunicationConstants.*;
import static com.swrobotics.bert.constants.SubsystemConstants.*;

import java.io.IOException;

import com.kauailabs.navx.frc.AHRS;

public final class RobotContainer {
    // Messenger
    public final MessengerClient msg;
    public final TaskManagerAPI raspberryPi;

    // Sensors
    public final AHRS gyro;

    public final Input input;
    public final SwerveDrive drive;
    public final SwerveDriveController driveController;
    public final Limelight limelight;
    public final Localization localization;
    public final CameraController cameraController;
    public final BallDetector ballDetector;
    public final Hopper hopper;
    public final Flywheel flywheel;
    public final NewHood hood;
    public final Intake intake;
    public final IntakeController intakeController;
    public final ShooterController shooterController;
    public final Climber climber;
    public final Lights lights;
    public final PDP pdp;
    public final Autonomous autonomous;

    // Messenger-dependent subsystems
    public final Pathfinding pathfinding;

    public RobotContainer() {
        // Messenger & TaskManager setup
        {
            msg = connectToMessenger();
            if (msg != null) {
                Scheduler.get().addCommand(new MessengerReadCommand(msg));
                raspberryPi = new TaskManagerAPI(msg, RASPBERRY_PI_PREFIX);
                Scheduler.get().addCommand(new TaskManagerSetupCommand(raspberryPi, "Pathfinding"));
            } else {
                raspberryPi = null;
            }
        }

        // Sensors
        {
            gyro = new AHRS(SPI.Port.kMXP, (byte) 200);
        }

        // Subsystems
        {
            input = new Input();
            drive = new SwerveDrive(gyro);
            driveController = new SwerveDriveController(input, gyro, drive);
            limelight = new Limelight();
            localization = new Localization(gyro, drive, limelight, msg, input);
            cameraController = new CameraController(limelight, localization, input);
            ballDetector = new BallDetector();
            hopper = new Hopper(ballDetector, input);
            flywheel = new Flywheel();
            hood = new NewHood();
            intake = new Intake();
            intakeController = new IntakeController(input, intake, hopper);
            shooterController = new ShooterController(input, hopper, flywheel, hood, localization);
            climber = new Climber(input, gyro);
            lights = new Lights();
            pdp = new PDP();

            if (msg != null) {
                pathfinding = new Pathfinding(this);
                Scheduler.get().addCommand(new PublishLocalizationCommand(msg, localization));
            } else {
                pathfinding = null;
            }

            autonomous = new Autonomous(this);
        }


        // Switches
        {
            new SubsystemSwitch(input,             ENABLE_INPUT);
            new SubsystemSwitch(drive,             ENABLE_DRIVE);
            new SubsystemSwitch(driveController,   ENABLE_DRIVE_CONTROLLER);
            new SubsystemSwitch(limelight,         ENABLE_LIMELIGHT);
            new SubsystemSwitch(localization,      ENABLE_LOCALIZATION);
            new SubsystemSwitch(cameraController,  ENABLE_CAMERA_CONTROLLER);
            new SubsystemSwitch(ballDetector,      ENABLE_BALL_DETECTOR);
            new SubsystemSwitch(hopper,            ENABLE_HOPPER);
            new SubsystemSwitch(flywheel,          ENABLE_FLYWHEEL);
            new SubsystemSwitch(hood,              ENABLE_HOOD);
            new SubsystemSwitch(intake,            ENABLE_INTAKE);
            new SubsystemSwitch(intakeController,  ENABLE_INTAKE_CONTROLLER);
            new SubsystemSwitch(shooterController, ENABLE_SHOOTER_CONTROLLER);
            new SubsystemSwitch(climber,           ENABLE_CLIMBER);
            new SubsystemSwitch(lights,            ENABLE_LIGHTS);
            new SubsystemSwitch(pdp,               ENABLE_PDP);
            new SubsystemSwitch(autonomous,        ENABLE_AUTONOMOUS);

            new SubsystemSwitch(pathfinding, ENABLE_PATHFINDING);
        }
    }

    private MessengerClient connectToMessenger() {
        MessengerClient msg = null;

        for (int i = 0; i < MESSENGER_CONNECT_MAX_ATTEMPTS && msg == null; i++) {
            try {
                msg = new MessengerClient(
                    MESSENGER_HOST,
                    MESSENGER_PORT,
                    MESSENGER_NAME
                );
            } catch (IOException e) {
                System.out.println("Messenger connection failed, trying again");
            }

            try {
                Thread.sleep(MESSENGER_CONNECT_RETRY_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return msg;
    }
}
