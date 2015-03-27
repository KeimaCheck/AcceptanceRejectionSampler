

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The test class ARTableTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class ARTableTest
{
    private LinearTestPDF linearTe1;
    private PolynomialTestPDF polynomi1;
    
    /**
     * Default constructor for test class ARTableTest
     */
    public ARTableTest()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp() throws IntervalException
    {
        linearTe1 = new LinearTestPDF(0, 1, 4, 0);
        float[] coefs = {1,2,3};
        polynomi1 = new PolynomialTestPDF(coefs, -1, 1);
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
    public void maxValueTest() throws IntervalException
    {
        assertEquals(4, linearTe1.getMax(linearTe1.getSupport(), 100), 0.1);
        assertEquals(0, linearTe1.getMin(linearTe1.getSupport(), 100), 0.1);
    }
    
    /**
     * Completes without exception if
     *      findBelow works without exception in both modes on a variety of test cases
     *      and sample works without exception
     * Passes if the BE function returns a value greater than the density function
     * for every test case.
     * In case of failure, reports the percentage of samples for which the box height
     * was too low, the average error among erroneous samples, and the average
     * error among all samples.
     * 
     */
    @Test
    public void densityTestAndSampleSanityTest() throws IntervalException, IntervalTreeException
    {
        Logger log = new Logger("ART-Density-Test");
        
        ARTable aRTable1 = new ARTable(polynomi1);
        int iterations = 1000;
        float x[] = new float[iterations];
        float errors[] = new float[iterations];
        float diff;
        
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++)
        {
            x[i] = aRTable1.sample();
            diff = aRTable1.probabilityDensity(x[i]) - polynomi1.probabilityDensity(x[i]);
            if (diff < 0) { errors[i] = -1*diff; }
            else { errors[i] = 0; }
        }
        long endTime = System.currentTimeMillis();
        
        int errorCount = 0;
        float errorTotal = 0;
        for (int i = 0; i < iterations; i++)
        {
            if (errors[i] > 0)
            {
                errorCount++;
                errorTotal += errors[i];
            }
        }
        
        float errPerc = errorCount/iterations;
        float erroneousAvg = errorTotal/errorCount;
        float errorsAllAvg = errorTotal/iterations;
        
        long sampleTime = endTime - startTime;
        
        log.writeMessage("Percentage of time erroneous samples found: " + errPerc + "%\n");
        log.writeMessage("Average error among erroneous samples: " + erroneousAvg + "\n");
        log.writeMessage("Average error among al samples: " + errorsAllAvg + "\n");
        log.writeMessage("Sampled in " + sampleTime + " ms.\n");
        
        assert errorTotal == 0;
        
    }
}



