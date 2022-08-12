package com.swrobotics.robot.subsystem;

import com.swrobotics.robot.control.Input;
import com.team2129.lib.math.Angle;
import com.team2129.lib.motor.TalonMotor;
import com.team2129.lib.schedule.Subsystem;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;

import static com.swrobotics.robot.Constants.*;


public class Intake implements Subsystem {

    private final TalonMotor motor;
    private final Input input;
    
    public Intake(Input input) {
        this.input = input;

        TalonFX talon_toWrap = new TalonFX(INTAKE_MOTOR_ID);
        talon_toWrap.configFactoryDefault();

        TalonFXConfiguration talonConfig = new TalonFXConfiguration();
        {
            SupplyCurrentLimitConfiguration talonSupplyLimit = new SupplyCurrentLimitConfiguration(
                true,
                35, // Continuous current limit
                50, // Peak current limit
                0.1 // Peak current limit duration
            );

            talonConfig.supplyCurrLimit = talonSupplyLimit;
            // TODO: Is it inverted?
        }

        motor = new TalonMotor(this, talon_toWrap);
    }

    public void setPercent(double percent) {
        motor.percent(percent);
    }

    public void setVelocity(Angle velocity) {
        motor.velocity(velocity);
    }

    @Override
    public void teleopPeriodic() {
        if (input.getIntakeOn()) {
            setPercent(INTAKE_ON_PERCENT);
        } else {
            setPercent(0);
        }
    }

    /*
     * My process for creating this subsystem:
     * 
     * 1. Package and class statements.
     * 2. "implements subsystem" because I will need periodic.
     * 3. Created constructor
     * 4. Created TalonMotor
     * 5. Realize I need constants for motor ID (import static)
     * 6. Add intake motor id to constants (Set it to a crazy number for now)
     * 7. Configure the talon_toWrap using config copied from SwerveModuleMaker (copied from old stuff)
     * 8. Add todo(CAPS) for inversion that I have to check on the actual robot.
     * 9. Finally wrap the motor (I should add more docs to TalonMotor, my bad)
     * 10. Add private final variable for Input
     * 11. Give input variable a value using this.input = input.
     * 12. Move this.input = input to top of constructor because it looks better first (shorter than motor config)
     * 
     * Constructor done! (for now)
     * 
     * Onto the functions
     * 
     * 1. Created a private function setPercent() (private because I will use it in the periodic)
     * 2. Gave setPercent() an input of the percent to set
     * 3. Switch setPercent to public so that it can be used in a command for ejecting a ball
     * 4. Created another function (pretty much the same) to control velocity because it might be useful for consistent ball ejection.
     * 
     * Now to use the Input
     * 1. Type @Override to define what it is supposed to do in periodic
     * 2. Go to com/team2129/lib/schedule to look at what periodic's are available
     * 3. Type out the rest: public void teleopPeriodic()
     * 4. Start the 'if' statement making up a fake function to get if the intake is on (I'll program this in Intake.java later)
     * 5. Set the intake percent to a constant that I made up (I'll add this later too)
     * 6. Put an else to turn the intake off if it should be off
     * 
     * Time to get fancy with it.
     * I'm going to add intake states to make it easier to add modes
     * 1. Realize that it isn't worth the complexity
     * ^ To have it be able to have both mode and state, I would have to have complex demands
     * 2. Test another way to get fancy with it with Java's built in Runnable type.
     * 3. Nope, that doesn't work either
     * ^ There is not a way to tell it to spin the motor before the motor is defined.
     * 
     * 4. Delete the fancy stuff to keep the file simple.
     * 
     * Fix the issues and do some housekeeping
     * 1. Add a function to input to get if the intake is on
     */
}
