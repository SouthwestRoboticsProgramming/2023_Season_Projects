package com.swrobotics.robot.subsystem;

import com.swrobotics.robot.Constants;
import com.swrobotics.robot.control.Input;
import com.team2129.lib.motor.ctre.TalonFXMotor;
import com.team2129.lib.net.NTDouble;
import com.team2129.lib.schedule.Subsystem;

public class Intake implements Subsystem {
    private static final NTDouble ON_PERCENT = new NTDouble("Intake/On Percent Out", 0.7);
    private static final NTDouble OFF_PERCENT = new NTDouble("Intake/Off Percent Out", 0.0);
    private static final NTDouble EJECT_PERCENT = new NTDouble("Intake/Eject Percent Out", -0.5);

    public enum State {
        ON(ON_PERCENT),
        OFF(OFF_PERCENT),
        EJECT(EJECT_PERCENT);

        private final NTDouble percent;

        State(NTDouble percent) {
            this.percent = percent;
        }

        public double getPercent() {
            return percent.get();
        }
    }


    private static final int MOTOR_ID = 11;

    private final TalonFXMotor motor;
    private final Input input;
    private State state;
    
    public Intake(Input input) {
        this.input = input;

        motor = new TalonFXMotor(this, MOTOR_ID, Constants.CANIVORE);
        motor.setInverted(true);

        state = State.OFF;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public void teleopPeriodic() {
        // Update state based on input
        if (input.getIntakeOn()) { // TODO: Do I need to have eject override this?
            state = State.ON;
        } else {
            state = State.OFF;
        }

        System.out.println(state.getPercent());
        motor.percent(state.getPercent());
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
