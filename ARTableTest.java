

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
    public void maxValueTest() throws IntervalException
    {
        ARTable aRTable1 = new ARTable();
        LinearTestPDF linearTe1 = new LinearTestPDF(0, 1, 1, 0);
        assertEquals(1, aRTable1.getMax(linearTe1, linearTe1.getSupport()), 0.1);
        assertEquals(0, aRTable1.getMin(linearTe1, linearTe1.getSupport()), 0.1);
    }

    @Test
    public void ARTableLinearPDFTest() throws IntervalException
    {
        LinearTestPDF linearTe1 = new LinearTestPDF(0, 1, 4, 1);
        ARTable aRTable1 = new ARTable(linearTe1);
    }

    @Test
    public void ARTableQuadraticPDFTest() throws IntervalException
    {
        QuadraticTestPDF quadrati1 = new QuadraticTestPDF(-1, 1, 3, 2, 1);
        ARTable aRTable1 = new ARTable(quadrati1);
    }
}



