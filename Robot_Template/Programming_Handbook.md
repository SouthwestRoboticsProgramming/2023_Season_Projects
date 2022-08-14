# Programming Handbook

### Writing a Class
#### General Process:
1. Create final variables and define them in the constructor. (Motors, PIDControllers, etc.)
2. Write empty functions with everything the class should do.
3. Fill functions with logic
4. Debug.
5. Add on as needed.



**Create final variables:** <br>
Start by trying to think of what motors, sensors, etc. your class will need and create the variables above the constructor. Once all of these are created, give them values inside of the constructor. This should look like: <br> 
```
private final TalonSRX motor; // Create variable

public ClassName() {         // Constructor
    motor = new TalonSRX(0); // Assign variable
}
```
**Write empty functions:** <br>
All you need to do in this step is define what your class should be able to do. Functions should compile but they don't need any logic. An example of this could be: <br>
```
public double getRPM() {
    return 342432.2; // Random number for now
}
```
The reason for doing this is to figure out exactly what logic you have to write and how it will interact with other classes.

**Fill in functions with logic:** <br>
This is where you actually need to write some code. There is no template for this section so good luck! Look at other, finished classes for examples of logic.

**Debug:** <br>
The hardest part of the whole process, debugging. What this step mostly comes down to is a repeatable process to figure it out in no time at all! <br>
1. Test (If it works, you're done!)
2. Find the number that is used last (This is often the output of a PID controller)
3. Print that number using `System.out.println(number)`.
4. Find where this number comes from and print that out.
5. Repeat until you find the bug.
6. Give up and ask Ryan.