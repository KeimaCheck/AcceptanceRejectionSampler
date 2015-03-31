package arsmp;
import arsmp.distributions.ProbabilityDistribution;

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
// TODO: rewrite as a concrete class which takes a ProbabilityDistribution as a construction
// argument.
public class AcceptanceRejectionSampler
{
    // instance variables - replace the example below with your own
    private float x;
    private float y;
    private Random generator;
    private ProbabilityDistribution distribution;
    private ARTable lookup;
    
    /**
     * Constructor for AcceptanceRejectionSampler objects
     */
    public AcceptanceRejectionSampler()
    {
        initializeGenerator();
        distribution = null;
        lookup = null;
    }
    
    /**
     * Constructor for AcceptanceRejectionSampler
     */
    public AcceptanceRejectionSampler(ProbabilityDistribution newDistribution)
                                      throws IntervalException
    {
        initializeGenerator();
        distribution = newDistribution;
        initializeLookup();
    }
    
    
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
        lookup = new ARTable(distribution);
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
        } while (y >= distribution.probabilityDensity(x));   // stop when y is found < f(x)
        
        return x;
    }
}
