package test_suite;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The test class IntervalTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class IntervalTest
{
    /**
     * Default constructor for test class IntervalTest
     */
    public IntervalTest()
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
    public void intervalComparisonTest() throws IntervalException
    {
        // basic assertions
        Interval interval3 = new Interval(0, 1);
        Interval interval4 = new Interval(1, 2);
        Interval interval5 = new Interval(1, 2);
        Interval interval6 = new Interval(-6.5f, -1.2f);
        Interval interval7 = new Interval(-3.4f, 2.0f);
        
        assertEquals(1, interval4.compareTo(interval6));
        assertEquals(-1, interval3.compareTo(interval4));
        assertEquals(1, interval4.compareTo(interval3));
        assertEquals(0, interval5.compareTo(interval4));
        assertEquals(-1, interval6.compareTo(interval3));

        // test exceptions
        boolean exception_overlap = false;
        boolean exception_improper = false;
        try
        { interval7.compareTo(interval3); }
        catch (IntervalException e) 
        { 
            exception_overlap = true;
        }
        try
        { Interval interval8 = new Interval( 1.0f, -1.0f); }
        catch (IntervalException e)
        { 
            exception_improper = true;
        }
        
        assertEquals(exception_overlap, true);
        assertEquals(exception_improper, true);
        
    }

    @Test
    public void concatenationTest() throws IntervalException
    {
        Interval interval1 = new Interval(-0.5f, 0.5f);
        Interval interval2 = new Interval(0.5f, 1.0f);
        Interval concatenatedInterval1 = interval1.concatenate(interval2);
        Interval interval3 = new Interval(-0.5f, 1.0f);
        assertEquals(interval3.equals(concatenatedInterval1), true);
        Interval concatenatedInterval2 = interval2.concatenate(interval1);
        assertEquals(interval3.equals(concatenatedInterval2), true);
        
        // test exceptions
        Interval interval4 = new Interval(1.5f, 2.0f);
        boolean exception_concat = false;
        try
        { Interval fakeInterval = interval2.concatenate(interval4); }
        catch (IntervalException e)
        { exception_concat = true; }
        
        assertEquals(exception_concat, true);
    }

    @Test
    public void subdivideTest() throws IntervalException
    {
        Interval interval1 = new Interval(0, 3);
        Interval[] interval2 = interval1.subdivideEvenly(3);
        Interval subdivisionOne = interval2[0];
        Interval subdivisionTwo = interval2[1];
        Interval subdivisionThree = interval2[2];
        Interval interval3 = subdivisionOne.concatenate(subdivisionTwo);
        Interval interval4 = subdivisionThree.concatenate(interval3);
        assertEquals(true, interval4.equals(interval1));
        Interval wonkyInterval = new Interval(-0.5f, 4.2f);
        Interval[] wonkyIntervalSubdivision = wonkyInterval.subdivideEvenly(2);
        Interval wonkySubdivisionOne = wonkyIntervalSubdivision[0];
        Interval wonkySubdivisionTwo = wonkyIntervalSubdivision[1];
        Interval interval5 = wonkySubdivisionTwo.concatenate(wonkySubdivisionOne);
        assertEquals(true, interval5.equals(wonkyInterval));
    }
    
    @Test
    public void subdivideFPTest() throws IntervalException
    {
        int parts = 1;
        for (int j = 1; j < 7; j++)
        {
            parts *= 10;
            Interval interval1 = new Interval(0,1);
            Interval[] subdivision = interval1.subdivideEvenly(parts);
            for(int i = 1; i < parts; i++)
            {
                assert subdivision[i].getLeft() == subdivision[i-1].getRight();
            }
        }
    }
    
    @Test
    public void evenSubdivisionTest() throws IntervalException
    {
        int parts = 1;
        Logger log = new Logger("Even_Subdivision_Test");
        for (int j = 1; j < 5; j++)
        {
            parts *= 10;
            Interval interval1 = new Interval(0,1);
            Interval[] subdivision = interval1.subdivideEvenly(parts);
            float [] widths = new float[parts];
            log.writeMessage("Ready to test 10 to the " + j + " intervals\n");
            log.writeMessage("Index | Width\n");
            for (int i = 0; i <parts; i++)
            {
                widths[i] = subdivision[i].getWidth();
            }
            log.writeEnumerated(widths);
        }
    }
}



