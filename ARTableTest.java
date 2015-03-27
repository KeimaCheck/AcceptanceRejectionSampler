

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
    private ARTable aRTable1;
    private ARTable aRTable2;
    
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
        aRTable1 = new ARTable(linearTe1);
        aRTable2 = new ARTable(polynomi1);
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
}



