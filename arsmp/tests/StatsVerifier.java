package arsmp.tests;
import arsmp.distributions.SymbolicallyCumulativeDistribution;
import arsmp.AcceptanceRejectionSampler;
import arsmp.IntervalException;

/**
 * Computes and outputs the error between the observed and the expected properties
 * of a distribution sample computed by AcceptanceRejectionSampler.
 * 
 * @author (Nicholas Padinha) 
 * @version (0.3, 3/31/2015)
 */
public class StatsVerifier
{
    private SymbolicallyCumulativeDistribution distribution;
    private AcceptanceRejectionSampler sampler;
    private float testPoints[];
    private float benchmarkData[];

    private static int TEST_POINTS = 1000;
    
    /**
     * Constructor for objects of class StatsVerifier
     */
    public StatsVerifier(SymbolicallyCumulativeDistribution newDistr)
                         throws IntervalException
    {
        distribution = newDistr;
        sampler = new AcceptanceRejectionSampler(distribution);
        testPoints = generateTestPoints();
    }

    public float[] generateTestPoints()
    {
        // obvious placeholder
        float output[] = {1};
        return output;
    }

}
