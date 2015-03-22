
/**
 * Simple finitely-supported PDF for testing the ARTable class
 * 
 * @author (Nicholas Padinha) 
 * @version (3/22/2015)
 */
public class LinearTestPDF implements HasPDF
{
    // instance variables - replace the example below with your own
    private String name;
    private Interval support;
    private float m;
    private float b;

    /**
     * Constructor for objects of type LinearTestPDF
     * 
     * @param leftEndpoint   the left endpoint of the support domain
     * @param rightEndpoint   the right endpoint of the support domain
     * @param slope         the slope
     * @param intercept     the intercept
     */
    public LinearTestPDF(float leftEndpoint, float rightEndpoint, float slope, float intercept)
                         throws IntervalException
    {
        support = new Interval(leftEndpoint, rightEndpoint);
        m = slope;
        b = intercept;
        name = createName();
    }
    
    /**
     * Contractual obligation.
     */
    public Interval getSupport()
    {
        return support;
    }
    
    /**
     * Create a name for this distribution: LeftEndpoint-RightEndpoint-Slope-Intercept-LinearDistr
     */
    private String createName()
    {
        return support.getLeft() + "-" + support.getRight() + "-" + m + "-" + b + "LinearDistr";
    }
    
    /**
     * Contractual obligation
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Contractual obligation.
     */
    public float probabilityDensity(float x)
    {
        return m*x + b;
    }
}
