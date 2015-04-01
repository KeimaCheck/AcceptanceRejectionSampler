package arsmp;
import arsmp.distributions.ProbabilityDistribution;
import arsmp.distributions.SymbolicallyCumulativeDistribution;

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
     * Samples the box-envelope function of the distribution, applying acceptance-rejection,
     * until a value is found that is accepted, then returns that value.
     * 
     * @returns A PSRN, such that with a large number of samples the collected data will
     *          exhibit the statistical properties of the chosen distribution
     */
    public float sampleDistribution() throws IntervalException, IntervalTreeException
    {
        float x;
        float u;
        
        do
        {
            x = lookup.sample();        // sample x from g(x)
            u = generator.nextFloat();  // and u from U(0,1);
        } while (u < (distribution.probabilityDensity(x) / lookup.probabilityDensity(x)));     
                                                                // if u < f(x)/g(x) holds,
                                                                // then accept x as a realization of f(x)
        
        return x;
    }
}
