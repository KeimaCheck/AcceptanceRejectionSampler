package arsmp.distributions;


/**
 * Specifies a distribution with a closed-form cumulative distribution function,
 * accessed by a method public float cumulativeDistribution(float x)
 */
public abstract class SymbollicalyCumulativeDistribution extends ProbabilityDistribution
{
    public abstract float cumulativeDistribution(float x);
}
