
/**
 * Write a description of class Interval here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Interval
{
    // instance variables - replace the example below with your own
    private float left;
    private float right;
    private float width;

    /**
     * Constructor for objects of class Interval
     * 
     * Default behavior is the unit interval (0,1)
     */
    public Interval()
    {
        // initialise instance variables
        left = 0.0f;
        right = 1.0f;
        width = getWidth();
    }
    
    /**
     * Constructor for objects of class Interval
     * 
     * @param left   the left endpoint of the interval
     * @param right   the right endpoint of the interval
     */
    public Interval(float leftEndpoint, float rightEndpoint) throws IntervalException
    {
        if (leftEndpoint <= rightEndpoint)
        {
            left = leftEndpoint;
            right = rightEndpoint;
            width = getWidth();
        } else { throw new IntervalException("Attempted to create an improper interval"); }
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public int compareTo(Interval i) throws IntervalException
    {
        if (right <= i.left)
        {
            return -1;
        } else if (left >= i.right)
        {
            return 1;
        } else if (left == i.left && right == i.right)
        {
            return 0;
        } else { throw new IntervalException("Attempted to compare overlapping intervals"); }
        
    }
    
    public boolean equals(Interval y)
    {
        return left == y.left && right == y.right;
    }
    
    public Interval concatenate(Interval y) throws IntervalException
    {
        if (right == y.left)
        {
            return new Interval(left, y.right);
        } else if (left == y.right)
        {
            return new Interval(y.left, right);
        } else { throw new IntervalException("Attempted to concatenate inconsecutive intervals"); }
    }
    
    public float getWidth()
    {
        return right - left;
    }
    
    public float getLeft(){ return left; }
    public float getRight(){ return right; }
    
    public Interval[] subdivideEvenly(int parts) throws IntervalException
    {
        float newWidth = width/parts;
        Interval output[] = new Interval[parts];
        float newLeft = left;
        float newRight = left + newWidth;
        for (int i = 0; i < parts-1; i++)
        {
            output[i] = new Interval(newLeft + (i * newWidth), newRight + (i * newWidth));
        }
        int i = parts-1;
        // guarantee that even with floating point nonsense the last interval has the correct
        // right endpoint
        output[i] = new Interval(newLeft + (i * newWidth), right);
        return output;
    }
    
    /**
     * Display the interval in a human-readable format
     */
    public String print()
    {
        String output = "(" + left + "," + right + ")";
        return output;
    }
}