package arsmp.tests;
import arsmp.distributions.SymbolicallyCumulativeDistribution;
import arsmp.AcceptanceRejectionSampler;
import arsmp.Logger;
import java.util.Arrays;
import arsmp.IntervalException;
import arsmp.IntervalTreeException;
import arsmp.distributions.PolynomialTestPDF;

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
    private float sampleData[];
    private Logger log;

    private static int TEST_POINTS = 1000;
    private static int SAMPLE_SIZE = 100000;
    
    public static void main(String[] args)
    {
        float coefs[] = {1,2,3};
        try
        {
            StatsVerifier go = new StatsVerifier(new PolynomialTestPDF(coefs, -1, 1));
        } catch (IntervalException|IntervalTreeException ex)
        {
            System.out.println("Fail.");
        }
    }
    
    /**
     * Constructor for objects of class StatsVerifier
     */
    public StatsVerifier(SymbolicallyCumulativeDistribution newDistr)
                         throws IntervalException, IntervalTreeException
    {
        distribution = newDistr;
        sampler = new AcceptanceRejectionSampler(distribution);
        log = new Logger("StatsVerification");
        generateTestPoints();
        generateBenchmarkData();
        runTest();
        logResults();
    }

    public void generateTestPoints()
    {
        float left = distribution.getSupport().getLeft();
        float right = distribution.getSupport().getRight();
        float width = right - left;
        testPoints = new float[TEST_POINTS];
        
        testPoints[0] = left + (width / TEST_POINTS);
        for(int i = 1; i < TEST_POINTS - 1; i++)
        {
            width = right - testPoints[i-1];
            testPoints[i] = testPoints[i-1] + (width / (TEST_POINTS - i));
        }
        testPoints[TEST_POINTS - 1] = right;
        
        log.writeMessage("Test points:\n");
        log.writeEnumerated(testPoints);
        
    }

    public void generateBenchmarkData()
    {
        benchmarkData = new float[TEST_POINTS];
        float left = distribution.getSupport().getLeft();
        float right = distribution.getSupport().getRight();
        float wholeIntegral = distribution.cumulativeDistribution(right)
                                - distribution.cumulativeDistribution(left);
        float integral;
        for(int i = 0; i < TEST_POINTS; i++)
        {
            integral = distribution.cumulativeDistribution(testPoints[i])
                         - distribution.cumulativeDistribution(left);
            benchmarkData[i] = integral/wholeIntegral;
        }
    }
    
    public void runTest() throws IntervalException, IntervalTreeException
    {
        sampleData = new float[TEST_POINTS];
        float samples[] = new float[SAMPLE_SIZE];
        for(int i = 0; i < SAMPLE_SIZE; i++)
        {
            samples[i] = sampler.sampleDistribution();
        }
        
        Arrays.sort(samples);
        
        int j = 0;
        for(int i = 0; i < SAMPLE_SIZE; i++)
        {
            if (samples[i] > testPoints[j] )
            {
                sampleData[j] = (float)(i - 1) / (float)SAMPLE_SIZE;
                j++;
            }
        }
    }
    
    public void logResults()
    {
        log.writeMessage("Comparison of sample data with benchmark data:\n");
        for(int i = 0; i < TEST_POINTS; i++)
        {
            log.writeMessage(sampleData[i] + " vs " + benchmarkData[i] + "\n");
        }
    }
}
