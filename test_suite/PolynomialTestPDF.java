package test_suite;

import java.lang.Math;
/**
 * Write a description of class PolynomialTestPDF here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class PolynomialTestPDF extends ProbabilityDistribution
{
    private float[] coefficients;

    /**
     * Constructor for objects of class PolynomialTestPDF
     * 
     * converts an array 
     */
    public PolynomialTestPDF(float[] newCoefficients, float left, float right)
                             throws IntervalException
    {
        coefficients = newCoefficients;
        support = new Interval(left,right);
        name = createName();
    }

    /**
     * Create the name of this distribution for archiving purposes
     */
    public String createName()
    {
        String output = "";
        for (int i = 0; i < coefficients.length; i++)
        {
            output += coefficients[i] + "-";
        }
        output += "(" + support.getLeft() + "," + support.getRight() + " )-PolynomialDistr";
        return output;
    }
    
    /**
     * Evaluate the polynomial determined by the coefficients previously passed to the
     * constructor.
     */
    public float probabilityDensity(float x)
    {
        float output = 0;
        if (support.contains(x))
        {
            output = coefficients[0] + coefficients[1]*x;
            for (int i = 2; i < coefficients.length; i++)
            {
                output += coefficients[i] * Math.pow(x,i);
            }
        } else { output = 0; }
        
        return output;
    }
}
