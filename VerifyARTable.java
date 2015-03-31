
/**
 * Wraps ARTable with getter methods for statistical
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class VerifyARTable extends ARTable
{
   
    /**
     * Constructor for objects of class VerifyARTable
     */
    public VerifyARTable(ProbabilityDistribution distribution) throws IntervalException
    {
        super(distribution);
    }
    
    public Interval[] getIntervals()
    {
        return loadAxisIntervals;
    }
    
    public float[] getHeights()
    {
        return loadBoxes;
    }
    
    public float getTotalArea()
    {
        return totalBoxArea;
    }

}
