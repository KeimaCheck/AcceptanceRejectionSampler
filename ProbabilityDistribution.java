
/**
 * Abstract class ProbabilityDistribution - write a description of the class here
 * 
 * @author (your name here)
 * @version (version number or date here)
 */
public abstract class ProbabilityDistribution
{
    // instance variables - replace the example below with your own
    private int x;
    private String name;
    private Interval support;

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y    a sample parameter for a method
     * @return        the sum of x and y 
     */
    public int sampleMethod(int y)
    {
        // put your code here
        return x + y;
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
    abstract String createName();
    
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
    abstract public float getMax(Interval overInterval, int testPoints);
    
    /**
     * Return the minimum value of the density function at a specified number of evenly
     * spaced test points.
     * 
     * @param overInterval   the interval to find the min value over
     * @param testPoints    the number of points to check the function at
     */    
    abstract public float getMin(Interval overInterval, int testPoints);
}
