package test_suite;

/**
 * Simple finitely-supported PDF for testing the ARTable class
 * 
 * @author (Nicholas Padinha)
 * @version (3/22/2015)
 */
public class QuadraticTestPDF extends ProbabilityDistribution
{
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
        name = createName();
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
    public String createName()
    {
        return support.getLeft() + "-"
                + support.getRight() + "-"
                + a + "-" + b + "-" + c + "-"
                + "QuadrDistr";
    }
}