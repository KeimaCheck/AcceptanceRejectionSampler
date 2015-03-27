
/**
 * Write a description of class OverlapException here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class IntervalException extends Exception
{
    private Interval argument1;
    private Interval argument2;
    private float leftEndpoint;
    private float rightEndpoint;
    
    
    /**
     * Constructor for objects of class OverlapException
     */
    public IntervalException()
    {
        super();
    }
    
    /**
     * Constructor for objects of class OverlapException, providing a message
     */
    public IntervalException(String message)
    {
        super(message);
    }
    
    public IntervalException(String message, Interval firstArg, Interval secondArg)
    {
        super(message + " " + firstArg.print() + ", " + secondArg.print());
        argument1 = firstArg;
        argument2 = secondArg;
    }
    
    public IntervalException(float left, float right)
    {
        super("Improper Interval: (" + left + ", "+ right + ")");
        leftEndpoint = left;
        rightEndpoint = right;
    }
}
