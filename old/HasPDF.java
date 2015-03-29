import java.lang.String;
/**
 * Classes implementing this interface represent a random variable, in the sense of
 * having a probability density function,
 * and are intended for use in random number sampling. Use of this interface allows
 * for calls to ARTable.computeLookupTable(HasPDF) on any object with a PDF
 * 
 * @author (Nicholas Padinha) 
 * @version (3/20/2015)
 */
public interface HasPDF
{
    // The constructor of a HasPDF must produce its name in a consistent way,
    // meaning that two HasPDF objects with the same class type and same 
    // parameter should have the same name. This is used by the ARTable class
    // to attempt to load the ARTable from hard drive before resorting to
    // computation
    public String getName();
    
    public abstract Interval getSupport() throws IntervalException;
    
    public abstract float probabilityDensity(float x);
}
