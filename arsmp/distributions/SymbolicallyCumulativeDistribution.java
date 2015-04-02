package arsmp.distributions;


/**
 * Specifies a distribution with a closed-form cumulative distribution function,
 * accessed by a method public float cumulativeDistribution(float x)
 */
public abstract class SymbolicallyCumulativeDistribution extends ProbabilityDistribution
{
    // must be normalized to zero at the left endpoint
    public abstract float cumulativeDistribution(float x);
}
