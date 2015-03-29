import java.lang.Float;
/**
 * Simple wrapper class providing uniform sampling of reals, and
 * intervals thereof, making use of the AcceptanceRejectionSampler.
 * For testing purposes.
 * 
 * @author (Nicholas Padinha) 
 * @version (3/20/2015)
 */
public class UniformSampler extends AcceptanceRejectionSampler
{
    
    private float left;
    private float right;
    private double width;
    private String name;
    public String getName(){ return name; }
    public Interval getSupport() throws IntervalException{ return new Interval(left,right); }

    /**
     * Constructor for objects of class UniformSampler
     */
    public UniformSampler()
    {

        initializeGenerator();
        left = Float.MAX_VALUE * -1.0f;
        right = Float.MAX_VALUE;
        width = (double)right - (double)left;
        name = "DefaultUniformSampler";
    }
    
    /**
     * Constructor for objects of class UniformSampler with given interval
     */
    public UniformSampler(float leftEndpoint,float rightEndpoint)
    {
        initializeGenerator();
        left = leftEndpoint;
        right = rightEndpoint;
        width = (double)right - (double)left;
        name = createName(leftEndpoint, rightEndpoint);
    }
    
    private String createName(float leftEndpoint, float rightEndpoint)
    {
        String leftString = String.valueOf(leftEndpoint);
        String rightString = String.valueOf(rightEndpoint);
        return leftString + "-" + rightString + "UniformDistr";
    }
    
    /**
     * Gives the uniform probability density function over the specified interval
     * 
     * @param  x   the x-value where we want to find the density
     * @return     the density
     */
    public float probabilityDensity(float x)
    {
        if (left <= x && x <= right)
        {
            return 1/(float)width;
        } else
        {
            return 0;
        }
    }
}
