import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * Could I have used Log4j? Yes.
 * Should I have? Maybe.
 * Can I retrofit the system to work with Log4j later? Yes.
 * Will I? Probably not.
 * 
 * Quick, dirty, gets the job done.
 * 
 * @author (Nicholas Padinha) 
 * @version (3/22/2015)
 */
public class Logger
{
    // instance variables - replace the example below with your own
    private String logFile;
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");

    /**
     * Constructor for objects of class Logger
     */
    public Logger()
    {
        Date date = new Date();
        logFile = dateFormat.format(date) + ".log";
    }
    
    /**
     * Constructor for objects of class Logger allowing some control over the log file name
     * 
     * @param prefix   a word to append to the beginning of the log file name
     */
    public Logger(String prefix)
    {
        Date date = new Date();
        logFile = prefix + dateFormat.format(date) + ".log";
    }
    
    public void writeMessage(String message)
    {
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true))))
        {
            out.println(message);
        } catch (IOException ex) {
            System.out.println("Failure while logging, not sure what to do now.");
        }
    }
}
