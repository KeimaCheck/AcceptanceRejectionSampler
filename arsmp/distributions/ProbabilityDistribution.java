package arsmp.distributions;
import arsmp.*;

/**
 * Abstract class ProbabilityDistribution - write a description of the class here
 * 
 * @author (your name here)
 * @version (version number or date here)
 */
public abstract class ProbabilityDistribution
{
    protected String name;
    protected Interval support;
    
    public ProbabilityDistribution() throws IntervalException
    {
        name = "dummy";
        support = new Interval(0,1);
    }
    
    public ProbabilityDistribution(String newName, Interval newSupport) throws IntervalException
    {
        name = newName;
        support = newSupport;
    }

    /**
     * Getter method for the name of this distribution
     * 
     * @return the name of this distribution
     */
    public String getName() { return name; }
    
    /**
     * Create the name string for this distribution.
     * In order to save and reload precomputed boxed envelopes, it's
     * necessary that every distribution create a unique name from its paramters
     */
    public abstract String createName();
    
    /**
     * Getter method for the support of this distribution
     * 
     * @return the support interval of this distribution
     */
    public Interval getSupport() { return support; }
    
    /**
     * The density function of this distribution
     * 
     * @param x   the argument of the PDF
     * @return    the density at x
     */
    public abstract float probabilityDensity(float x);
    
    /**
     * Return the maximum value of the density function at a specified number of evenly
     * spaced test points.
     * 
     * @param overInterval   the interval to find the max value over
     * @param testPoints    the number of points to check the function at
     */
    public float getMax(Interval overInterval, int testPoints)
    {
        float domainWidth = overInterval.getWidth();
        float evalPoint = overInterval.getLeft();
        float subparcel = domainWidth / testPoints;
        float max = probabilityDensity(evalPoint);
        float a;
        
        // Check some (reasonably) large number of evenly spaced points throughout the interval
        // and return the max value obtained on those points
        for (int i = 0; i < testPoints; i++)
        {
            evalPoint += subparcel;
            a = probabilityDensity(evalPoint);
            if (max < a) { max = a; }
        }
        return max;
    }
    
    /**
     * Return the minimum value of the density function at a specified number of evenly
     * spaced test points.
     * 
     * @param overInterval   the interval to find the min value over
     * @param testPoints    the number of points to check the function at
     */    
    public float getMin(Interval overInterval, int testPoints)
    {
        float domainWidth = overInterval.getWidth();
        float evalPoint = overInterval.getLeft();
        float subparcel = domainWidth / testPoints;
        float min = probabilityDensity(evalPoint);
        float a;
        
        // See comment in getMax()
        for (int i = 0; i < testPoints; i++)
        {
            evalPoint += subparcel;
            a = probabilityDensity(evalPoint);
            if (min > a) { min = a; }
        }
        return min;        
    }
    
}
