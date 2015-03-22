
/**
 * Simple finitely-supported PDF for testing the ARTable class
 * 
 * @author (Nicholas Padinha)
 * @version (3/22/2015)
 */
public class QuadraticTestPDF implements HasPDF
{
    // instance variables - replace the example below with your own
    private String name;
    private Interval support;
    
    // think aX^2 + bx + c if this worries you. Gosh.
    private float a;
    private float b;
    private float c;

    /**
     * Constructor for objects of class QuadraticTestPDF
     */
    public QuadraticTestPDF(float leftEndpoint, float rightEndpoint,
                            float quad, float lin, float constant) throws IntervalException
    {
        support = new Interval(leftEndpoint, rightEndpoint);
        a = quad;
        b = lin;
        c = constant;
    }
    
    /**
     * Contractual obligation.
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Contractual obligation.
     */
    public Interval getSupport()
    {
        return support;
    }
    
    /**
     * Contractual obligation.
     */
    public float probabilityDensity(float x)
    {
        return (a*x*x) + (b*x) + c;
    }
    
    /**
     * Create a name for this distribution:
     * Left-Right-QuadTerm-LinearTerm-ConstantTerm-QuadrDistr
     */
    private String createName()
    {
        return support.getLeft() + "-"
                + support.getRight() + "-"
                + a + "-" + b + "-" + c + "-"
                + "QuadrDistr";
    }
}