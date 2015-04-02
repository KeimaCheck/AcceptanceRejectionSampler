package arsmp.tests;
import arsmp.Logger;
import arsmp.IntervalException;
import arsmp.IntervalTreeException;
import arsmp.AcceptanceRejectionSampler;
import arsmp.distributions.*;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The test class ARSamplerTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class ARSamplerTest
{
    /**
     * Default constructor for test class ARSamplerTest
     */
    public ARSamplerTest()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
    }
    
    @Test
    public void aRSampleSpeedTest()
    {
        try
        {
            Logger log = new Logger("ARSampler-Speed-test");
            float coefs[] = {1,2,3};
            AcceptanceRejectionSampler go = new AcceptanceRejectionSampler(
                                                new PolynomialTestPDF(coefs, -1, 1));
            float samples[] = new float[100000];
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 100000; i++)
            {
                samples[i] = go.sampleDistribution();
            }
            long endTime = System.currentTimeMillis();
            float stoppingTime = endTime - startTime;
            float averageTime = stoppingTime/100000;
            log.writeMessage("Total time: " + stoppingTime + "ms\n");
            log.writeMessage("Average time: " + averageTime + "ms\n");
            log.writeMessage("Index | Sample\n");
            log.writeEnumerated(samples);
        } catch (IntervalException|IntervalTreeException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
}
