

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;

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
        int iterations = 100;
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
    
    @Test
    public void sampleSpeedTest() throws IntervalException, IntervalTreeException
    {
        Logger log = new Logger("BESampling-Speed-Test");
        
        int iterations = 100000;
        ARTable aRTable1 = new ARTable(polynomi1);
        float xs[] = new float[iterations];
        
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++)
        {
            xs[i] = aRTable1.sample();
        }
        long endTime = System.currentTimeMillis();
        
        float stoppingTime = endTime - startTime;
        float averageTime = stoppingTime/iterations;
        log.writeMessage("Total time: " + stoppingTime + "ms\n");
        log.writeMessage("Average time: " + averageTime + "ms\n");
        log.writeMessage("Index | Sample\n");
        log.writeEnumerated(xs);
        
        Arrays.sort(xs);
        
    }
    
    @Test
    public void verifySamples() throws IntervalException, IntervalTreeException
    {
        Logger log = new Logger("BESample-Verification");
        VerifyARTable verifyA1 = new VerifyARTable(polynomi1);
        
        Interval intervals[] = verifyA1.getIntervals();
        float heights[] = verifyA1.getHeights();
        float totalArea = verifyA1.getTotalArea();
        
        float sampleRatio;
        float areaRatio;
        float spacingRatio;
        float area;
        
        int iterations = 100000;
        float samples[] = new float[iterations];
        for (int i = 0; i < iterations; i++)
        {
            samples[i] = verifyA1.sample();
        }
        Arrays.sort(samples);

        float diffs[] = new float[iterations - 1];
        for (int i = 0; i < iterations - 1; i++)
        {
            diffs[i] = samples[i+1] - samples[i];
        }
        
        int lastFirst = 0;
        int countLocal = 0;
        int intervalIndex = 0;        
        for (int i = 0; i < iterations; i++)
        {
            if ( intervals[intervalIndex].contains(samples[i]) )
            {
                countLocal++;
            }
            else 
            {
                
                sampleRatio = (float)countLocal / (float)iterations;
                area = intervals[intervalIndex].getWidth() * heights[intervalIndex];
                areaRatio = area / totalArea;
                log.writeMessage(sampleRatio + " vs " + areaRatio + "\n");
                spacingRatio = area / countLocal;
                log.writeMessage(spacingRatio + "\n");
                
                log.writeEnumerated(Arrays.copyOfRange(diffs, lastFirst, i - 1));
                intervalIndex++;
                lastFirst = i + 1;
                countLocal = 0;
            }
            
        }
    }
}



