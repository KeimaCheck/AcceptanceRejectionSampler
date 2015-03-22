import java.util.Random;
import java.lang.Float;
/**
 * Abstract class AcceptanceRejectionSample - provides the core logic of the
 * acceptance-rejection algorithm for generating random numbers according to a 
 * particular probability density function.
 * 
 * Usage is by subclassing and providing an implementation of the probability
 * density.
 * 
 * @author (Nicholas Padinha)
 * @version (0.1)
 */
public abstract class AcceptanceRejectionSampler implements HasPDF, RandomSampler
{
    // instance variables - replace the example below with your own
    private float x;
    private float y;
    private Random generator;
    private ARTable lookup;
    
    /**
     * Instantiates a random number generator for use by this class
     */
    public void initializeGenerator()
    {
        generator = new Random(System.currentTimeMillis());
    }
    
    /**
     * Instantiate the lookup table for the algorithm
     */
    public void initializeLookup() throws IntervalException
    {
        lookup = new ARTable(this);
    }
    
    /**
     * Samples the upper half-plane for y in [0,1] uniformly and sets the internal state
     */
    private void samplePlaneUniformly()
    {
        if (lookup == null)     // default behavior
        {
            float a = (generator.nextFloat() * 2) - 1;
            a = a * (Float.MAX_VALUE - 1);
            float b = generator.nextFloat();
            x = a;
            y = b;
        } else
        {
            // use the lookup table to prevent wasting space with lots of dud samples
        }
    }
    
    /**
     * Samples the upper half-plane for y in [0,1] repeatedly until a point (x,y) is found
     * below the graph of the probability density function, then return x.
     * 
     */
    public float sampleDistribution()
    {
        do
        {
            samplePlaneUniformly();
        } while (y >= probabilityDensity(x));   // stop when y is found < f(x)
        
        return x;
    }
    
    /**
     * The probability density of the distribution being sampled from. For
     * best performance this should be normalized to have a max value of 1, and
     * for sanity the density function should decay to 0 at infinity.
     * 
     * @param x   a float, the point on the x-axis where we want the density
     * @return    the density
     */
    public abstract float probabilityDensity(float x);
}
