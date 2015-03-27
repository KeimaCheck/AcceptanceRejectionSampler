import java.util.Hashtable;
import java.lang.Float;
import java.lang.Integer;
import java.util.Random;
/**
 * Implements generating and sampling from a boxed envelope function for use in
 * acceptance-rejection PRNG. The primary data structure is that of a binary search
 * tree of non-overlapping intervals that concatenate to the support of the distribution
 * in question.
 * 
 * Limitations: (1) The density function must have finite support. (2) The density 
 * function should be monotonic on each interval in some finite partition of its suport
 * for optimal performance.
 * 
 * 
 * @author (Nicholas Padinha) 
 * @version (0.2, 3/22/2015)
 */
public class ARTable    // extends ProbabilityDistribution? (because the boxed envelope is "just" another distribution)
{
    private Entry tableRoot;
    private Logger logger;
    private ProbabilityDistribution distribution;
    private Random rng;
    
    // These fields used when saving or loading a previously-computed table
    private Interval[] loadAxisIntervals;
    private Interval[] loadProbabilityIntervals;
    private float[] loadBoxes;
    
    // These fields store data used in sampling the distribution
    private int numberOfBoxes;
    private float totalBoxArea;
    
    // These statics control the running and stopping behavior of the algorithm
    private static int TEST_POINTS = 100;
    private static int SUBDIVISIONS = 3;
    private static float RATIO_THRESH = .75f;
    private static float WIDTH_THRESH = .1e-5f;
    
    // These statics improve the readability of the subdivision code
    private static int LEFT_ENTRY = 0;
    private static int MIDDLE_ENTRY = 1;
    private static int RIGHT_ENTRY = 2;
    
    // These statics define modes for looking up intervals by a contained value
    private static int PROBABILITY_MODE = -274;
    private static int SAMPLING_MODE = -275;


    /**
     * Constructor for objects of class ARTable
     * 
     * Default behavior is null
     */
    public ARTable()
    {
        // There's no practical application, other than testing, of this class when instantiated
        // with no information about a distribution.
        tableRoot = null;
    }
    
    /**
     * Constructor for objects of class ARTable
     * 
     * Loads or computes a lookup table for a given distribution depending on its internal state
     */
    public ARTable(ProbabilityDistribution newDistribution) throws IntervalException
    {
        rng = new Random(System.currentTimeMillis());
        distribution = newDistribution;
        // check if the distribution has a lookup table already computed and load it
        // if (FILE EXISTS: distribution.getName()) { LOAD TABLE FROM FILE }
        // else
        logger = new Logger("ARTable");
        logger.writeMessage("Initialized ARTable for distribution " + distribution.getName() + "\n");
        computeLookupTable();
        logger.writeMessage("Here is the table in sequential order:\n");
        logger.writeMessage(printInOrder());
        // next we need to compute the data used for sampling the boxed distribution
        // first traverse the tree, computing for each interval the area of the box there (max * width)
        // sum the computed areas while traversing and keep track of the total number of partitions
        computeAreas();
        // traverse the tree again, computing A_i = (Area of ith box)/(Total area under BE graph)
        // and setting P_0,left = 0
        //             P_i,right = P_i,left + A_i  for i in (0, n-1) where n is the numer of partitions
        //             P_i+1,left = P_i,right for i in (0,n-1)
        //             P_n,right = 1
        computeProbabilityIntervals();
    }

  
    /**
     * Compute the lookup table for the given distribution
     */
    private void computeLookupTable() throws IntervalException
    {
        Interval support = distribution.getSupport();
        logger.writeMessage("Support of distribution (beginning root interval): "
                            + support.print() + "\n");
        
        // The max value of the density function on a given interval is used in building
        // the boxed envelope function. The min value is used in halting.
        float maxGuess = distribution.getMax(support, TEST_POINTS);
        float minGuess = distribution.getMin(support, TEST_POINTS);
        logger.writeMessage("Distribution max: " + maxGuess + "\n");
        logger.writeMessage("Distribution min: " + minGuess + "\n");
        
        Entry newEntry = new Entry(support, maxGuess);
        logger.writeMessage("New entry created with root interval and found max value...\n");
        
        // Entry.subdivide handles the stopping logic and the recursing logic
        tableRoot = newEntry.subdivide(minGuess);
        
        logger.writeMessage("Recursion complete. Table is ready for next step.\n");
    }
    
    private void computeAreas() throws IntervalException
    {
        totalBoxArea = 0;
        logger.writeMessage("Traversing tree to compute area under graph...\n");
        numberOfBoxes = tableRoot.computeAreas();
        logger.writeMessage("Total area computed: " + totalBoxArea + "\n"
                            + "Number of boxes: " + numberOfBoxes + "\n"
                            + "Table is ready for next step.\n");
    }
    
    private void computeProbabilityIntervals() throws IntervalException
    {
        logger.writeMessage("Traversing tree to assign probabilityIntervals...\n");
        tableRoot.computeProbabilityIntervals(0);
        // Fix rightmost interval
        Entry rightmost = tableRoot.rightmost();
        rightmost.probabilityInterval = new Interval(rightmost.probabilityInterval.getLeft(), 1);
        logger.writeMessage("probabilityIntervals computed. Table is ready for use.\n");
        
    }
    
    /**
     * Get (hopefully a good approximation to) the max value of a distribution on an interval
     * Deprecated as of 0.2, reason: poor OO design choice putting this function here in the first place
     * 
     * @param distribution   the distribution
     * @param interval   the interval
     * @returns   the max value of the distribution over the interval
     */
    @Deprecated
    public float getMax(HasPDF distribution, Interval interval)
    {
        float domainWidth = interval.getWidth();
        float evalPoint = interval.getLeft();
        float subparcel = domainWidth / TEST_POINTS;
        float max = distribution.probabilityDensity(evalPoint);
        float a;
        
        // Check some (reasonably) large number of evenly spaced points throughout the interval
        // and return the max value obtained on those points
        for (int i = 0; i < TEST_POINTS; i++)
        {
            evalPoint += subparcel;
            a = distribution.probabilityDensity(evalPoint);
            if (max < a) { max = a; }
        }
        return max;
    }
    
    
    /**
     * Basically identical to getMax, only gets the minimum instead
     * Deprecated as of 0.2, reason: see ARTable.getMax()
     * 
     * @param distribution   the distribution
     * @param interval   the interval
     * @return   the min value of the density function of the distribution over the interval
     */
    @Deprecated
    public float getMin(HasPDF distribution, Interval interval)
    {
        float domainWidth = interval.getWidth();
        float evalPoint = interval.getLeft();
        float subparcel = domainWidth / TEST_POINTS;
        float min = distribution.probabilityDensity(evalPoint);
        float a;
        
        // See comment in getMax()
        for (int i = 0; i < TEST_POINTS; i++)
        {
            // next line causes a lack of precision for the same reason that subdivide was all screwy
            evalPoint += subparcel;
            a = distribution.probabilityDensity(evalPoint);
            if (min > a) { min = a; }
        }
        return min;
    }
    
    /**
     * Load the lookup table from a file when possible
     * 
     * @param filename   the name of the file to load from
     */
    private void loadLookupTable(String filename)
    {
        
    }
    
    /**
     * Save the lookup table stored in this object
     * 
     * @param filename   the name of the file to save to
     */
    private void saveLookupTable(String filename)
    {
        
    }
    
    /**
     * Print the contents of the table to a string, in order, by accessing
     * each Entry's printRecursive() method.
     */
    public String printInOrder()
    {
        return tableRoot.printRecursive();
    }
    
    /**
     * 
     * 
     * @returns The probability density at the point x.
     */
    public float probabilityDensity(float x) throws IntervalException, IntervalTreeException
    {
        logger.writeMessage("Finding box height at " + x + "\n");
        Entry containingBox = tableRoot.findBelow(x, SAMPLING_MODE);
        logger.writeMessage(x + " is in " + containingBox.axisInterval
                            + ", with box height" + containingBox.box + "\n");
        return containingBox.box;
    }
    
    /**
     * Sample the boxed envelope distribution represented by this table
     * 
     * @returns A randomly-generated number sampled from the boxed envelope distribution.
     */
    public float sample() throws IntervalException, IntervalTreeException
    {
        logger.writeMessage("Sampling from boxed envelope distribution...");
        float uniformRandom = rng.nextFloat();

        // find the entry whose probabilityInterval contains that float,
        // get its axisInterval
        logger.writeMessage("Selecting box...\n");
        Interval sampleFromWithin = tableRoot.findBelow(uniformRandom,PROBABILITY_MODE).axisInterval;
        logger.writeMessage("Box at " + sampleFromWithin.print() + " selected.\n");
        
        float scale = sampleFromWithin.getWidth();
        float translate = sampleFromWithin.getLeft();
        
        // refresh uniformRandom
        uniformRandom = rng.nextFloat();
        
        // affine transform uniformRandom so that it is a uniform random float
        // between sampleFromWithin.left and sampleFromWithin.right
        uniformRandom *= scale;
        uniformRandom += translate;
        
        // uniformRandom now contains a number effectively drawn at random from the distribution
        // represented by this table
        return uniformRandom;
    }
    
    /**
     * Objects of class Entry are the main data elements of ARTale objects.
     * They consist of two Intervals and a float, as well as references to right and left child
     * Entries. Letting $f$ refer to the density function of the distribution in question,
     * and letting $g$ refer to the boxed envelope of $f$:
     * axisInterval is a subinterval of the domain of $f$,
     * probabilityInterval is a subinterval of (0,1) whose width equals the probability that
     * a plane point uniformly sampled from the area under the graph of $g$ lives in axisInterval,
     * used for sampling from the distribution of $g$,
     * and box is the max value of $f$ on axisInterval, which corresponds to $g(x)$ for x in
     * axisInterval.
     * 
     * @author (Nicholas Padinha)
     * @version (3/22/2015)
     */
    private class Entry
    {
        public Entry leftChild;
        public Entry rightChild;
        
        public Interval axisInterval;
        public Interval probabilityInterval;
        public float box;
        
        public float boxArea;
        
        /**
         * Constructor for objects of type Entry
         */
        public Entry(Interval newInterval, float newValue)
        {
            leftChild = null;
            rightChild = null;
            axisInterval = newInterval;
            probabilityInterval = null;
            box = newValue;
        }
        
        /**
         * Insert a new entry below this one. Will either place the new 
         * entry in an empty (null-referenced) child field or call itself on a child.
         * 
         * @param newKey   The key of the entry to be placed
         * @param newValue   The value of the entry to be placed
         */
        public void insertBelow(Interval newInterval, float newValue) throws IntervalException, IntervalTreeException
        {
            if (newInterval.compareTo(axisInterval) == -1)    // new entry goes to the left
            {
                if (leftChild == null) { leftChild = new Entry(newInterval, newValue); }
                else { leftChild.insertBelow(newInterval, newValue); }
            } else if (newInterval.compareTo(axisInterval) == 1)
            {
                if (rightChild == null) { rightChild = new Entry(newInterval, newValue); }
                else { rightChild.insertBelow(newInterval, newValue); }
            } else
            {
                throw new IntervalTreeException("Supposedly new interval already has an entry.");
            }
        }
        
        /**
         * Search below this entry for an entry with the given key
         * 
         * @param findKey   The key to be searched for
         */
        public Entry findBelow(Interval findInterval) throws IntervalException, IntervalTreeException
        {
            Entry output = null;
            if (findInterval.compareTo(axisInterval) == -1)
            {
                // if the interval is in the table, it's below the left child of this entry
                if (leftChild == null)
                {
                    throw new IntervalTreeException("Sought interval is not in the table");
                }
                // if (leftChild.interval.compareTo(findInterval) == 0) { return leftChild; }
                else { output = leftChild.findBelow(findInterval); }
            } else if (findInterval.compareTo(axisInterval) == 1)
            {
                if (rightChild == null)
                {
                    throw new IntervalTreeException("Sought interval is not in the table");
                }
                // if (rightChild.interval.compareTo(findInterval) == 0) { return rightChild; }
                else { output = rightChild.findBelow(findInterval); }
            } else { output = this; } // 
            
            return output;
        }
        
        /**
         * Search below this entry for an entry whose axisInterval contains a given value.
         * Essentially the default behavior for the next overload of this function, which
         * lets the caller determine whether they want to find a probability interval or
         * an axis interval
         * 
         * @param x   the value to find
         * @return    the entry containing that value
         */
        public Entry findBelow(float findNumber) throws IntervalException, IntervalTreeException
        {
            return findBelow(findNumber, SAMPLING_MODE);
        }
        
        /**
         * Multi-purpose search function that can find an entry whose probabilityInterval
         * or axisInterval contains a given number. A mode paramter chooses which
         * interval system to search.
         */
        public Entry findBelow(float findNumber, int mode) throws IntervalException, IntervalTreeException
        {
            Interval searchInterval = null;
            if (mode == PROBABILITY_MODE) { searchInterval = probabilityInterval; }
            else if (mode == SAMPLING_MODE) { searchInterval = axisInterval; }
            else { throw new IntervalTreeException("Search mode must be probabiity or sampling"); }
            assert searchInterval != null;
            
            Entry output = null;
            
            if (findNumber < searchInterval.getLeft())
            {
                if (leftChild!= null)
                { 
                    logger.writeMessage("Looking to the left of " + searchInterval.print() + " for " + findNumber + "\n");
                    output = leftChild.findBelow(findNumber, mode); 
                }
                else {throw new IntervalTreeException(findNumber + " is not in any interval in the tree"); }
            } else if (findNumber > searchInterval.getRight())
            {
                if (rightChild != null)
                {
                    logger.writeMessage("Looking to the right of " + searchInterval.print() + " for " + findNumber + "\n");
                    output = rightChild.findBelow(findNumber, mode); 
                }
                else { throw new IntervalTreeException(findNumber + " is not in any interval in the tree"); }
            } else
            {
                assert searchInterval.contains(findNumber);
                logger.writeMessage(searchInterval + " contains " + findNumber + "\n");
                output = this;
            }
            
            assert output != null;
            return output;
        }
        
        /**
         * Get the leftmost entry of the subtree hanging below this entry
         * 
         * @return   the leftmost (lowest) entry below this one
         */
        public Entry leftmost()
        {
            if (leftChild == null) { return this; }
            else { return leftChild.leftmost(); }
        }
        
        /**
         * Get the rightmost entry of the subtree hanging below this entry
         * 
         * @return   the rightmost (greatest) entry below this one
         */
        public Entry rightmost()
        {
            if (rightChild == null) { return this; }
            else { return rightChild.rightmost(); }
        }
        
        /**
         * Append the tree T rooted at head to the beginning of the table.
         * 
         * Asserts that the rightmost (greatest) entry of T be less than the leftmost
         * entry below this one.
         */
        public void prepend(Entry head) throws IntervalException
        {
            logger.writeMessage("Prepending tree under " + head.print() + " to tree under "
                                + print() + ".\n");
            Entry leastEntryOfThis = leftmost();
            Entry greatestEntryOfHead = head.rightmost();
            
            int check = -2;     // magic number yesss
            String errorMessage = "Attempted to prepend a tree which was not wholly less than this tree";
            try
            {
                check = greatestEntryOfHead.axisInterval.compareTo(leastEntryOfThis.axisInterval);
            } catch (IntervalException e)
            {
                logger.writeMessage(e.getMessage());
                check = -2;     // sanity
                errorMessage = "IntervalException thrown. See log";
            } 

            assert (check == -1): errorMessage;
            leastEntryOfThis.leftChild = head;
            logger.writeMessage("Successful.\n");
        }
        
        
        /**
         * Append the tree T rooted at tail to the end of the table.
         * 
         * Asserts that the leftmost (least) entry of T be greater than the rightmost entry
         * below this one.
         */
        public void postpend(Entry tail) throws IntervalException
        {
            logger.writeMessage("Postpending tree under " + tail.print() + " to tree under "
                                + print() + ".\n");
            Entry greatestEntryOfThis = rightmost();
            Entry leastEntryOfTail = tail.leftmost();
            
            int check = -2;     // magic number yesss
            String errorMessage = "Attempted to prepend a tree which was not wholly greater than this tree";
            try
            {
                check = leastEntryOfTail.axisInterval.compareTo(greatestEntryOfThis.axisInterval);
            } catch (IntervalException e)
            {
                logger.writeMessage(e.getMessage());
                check = -2;     // sanity
                errorMessage = "IntervalException thrown. See log";
            } 
            
            assert (check == 1): errorMessage;
            greatestEntryOfThis.rightChild = tail;
            logger.writeMessage("Successful.\n");
        }
        
        /**
         * Implements the recursive part of the table-building logic.
         * 
         * Subdivide the given interval into three parts and find the max/min of $f$ on each interval.
         * If the max on either interval differs from that on the current interval, then current
         * interval will be replaced by the subdivision. In this case, new entry objects are created
         * and subdivide calls itself on those unless they satisfy the stopping criterion. If no
         * difference between the current interval's max and the max calculated for the subintervals,
         * then return the current interval.
         * 
         * @param minGuess    the (algorithm's best guess at) the minimum value of the probability
         *                    density on this interval
         * 
         * @returns   either the current interval-max value pair, or the subtree replacing it.
         * 
         */
        public Entry subdivide(float minGuess) throws IntervalException
        {
            // there's an annoying bug in here somewhere--some kind of floating point error
            // as of 3/24/15, it seems like the bug has to do with interval subdivision,
            // based on error messages returned by the Entry.prepend/postpend methods.
            
            logger.writeMessage("Subdividing " + print() + "\n");
            float trapezoidToBoxRatio = (1.0f + (minGuess / box)) / 2.0f;
            float currentWidth = axisInterval.getWidth();
            
            Entry output = null;
            // if the ratio of the min-max trapezoidal area to the box area on this interval
            // is sufficiently close to 1, or if the width of this interval is sufficiently small,
            // we may terminate this branch of the recursion because we've constrained overhead
            // sufficiently
            if (trapezoidToBoxRatio > RATIO_THRESH || currentWidth < WIDTH_THRESH)
            // might be useful to point out here that if the minimum value of the function is
            // close to zero anywhere, the stopping criterion implemented here is going to
            // let recursion go on until you get below the width threshold
            {
                logger.writeMessage("Trap-to-box ratio: " + trapezoidToBoxRatio + " > " + RATIO_THRESH + "\n");
                logger.writeMessage("Therefore, I will stop subdividing and return this Entry\n");
                output = this;
            }
            // otherwise we enter the adaptive part of the routine
            else
            {
                logger.writeMessage("Entry failed first stopping test.\n");
                logger.writeMessage("(Trap-to-box ratio: " + trapezoidToBoxRatio + ". Width: "
                                    + currentWidth + ")\n");
                logger.writeMessage("vs (" + RATIO_THRESH + ", " + WIDTH_THRESH + ")\n");
                // get three even parts of the current interval and the relevant data
                Interval newIntervals[] = axisInterval.subdivideEvenly(SUBDIVISIONS);
                Entry subEntries[] = new Entry[SUBDIVISIONS];
                float maxs[] = new float[SUBDIVISIONS];
                float mins[] = new float[SUBDIVISIONS];
                
                boolean doDivide = false;
                // if any of the parts exhibit a different max value of the function
                // than the current interval then we should replace the current interval with
                // the subdivision/partition and recurse on each of those
                for(int i = 0; i < SUBDIVISIONS; i++)
                {
                    maxs[i] = distribution.getMax(newIntervals[i], TEST_POINTS);
                    mins[i] = distribution.getMin(newIntervals[i], TEST_POINTS);
                    subEntries[i] = new Entry(newIntervals[i], maxs[i]);
                    if (maxs[i] != box)
                    { 
                        doDivide = true;
                        logger.writeMessage("Entry's " + i + "th part exhibits max value " + maxs[i] + "\n");
                        logger.writeMessage("Therefore I will recursively act on all three parts.\n");
                    }
                }
                
                // call subdivide to build trees for each of the parts, then put the trees together
                if (!doDivide)
                {
                    output = this;
                    logger.writeMessage("Entry passed second stopping test " +
                                        "(max value preserved by subdivisions)\n");
                    logger.writeMessage("Therefore I will stop recursing this branch and return the Entry.\n");
                }
                else
                {
                    Entry returnedRoot = subEntries[MIDDLE_ENTRY];
                    output = returnedRoot.subdivide(mins[MIDDLE_ENTRY]);
                    logger.writeMessage("Finished recursing on middle part of " + print() +"\n");
                    
                    Entry returnedLeftChild = subEntries[LEFT_ENTRY];
                    output.prepend(returnedLeftChild.subdivide(mins[LEFT_ENTRY]));
                    logger.writeMessage("Finished recursing on left part of " + print() + "\n");
                    
                    Entry returnedRightChild = subEntries[RIGHT_ENTRY];
                    output.postpend(returnedRightChild.subdivide(mins[RIGHT_ENTRY]));
                    logger.writeMessage("Finished recursing on right part of " + print() + "\n");
                }
            }
            
            return output;
        }

        /**
         * Computes the area of this entry, add it to the total area for the whole table,
         * and do the same for each subtree. Also do the accounting on the total number of
         * intervals in the tree.
         * 
         * @return the number of intervals in the subtree under this entry, including this entry's.
         */
        public int computeAreas()
        {
            // compute area, update total
            boxArea = axisInterval.getWidth() * box;
            totalBoxArea += boxArea;
            
            logger.writeMessage("Box at " + axisInterval.print() + " has area " + boxArea + "\n");
            logger.writeMessage("Found total area " + totalBoxArea + " so far.\n"); 
            
            // account for this entry's interval in the count
            int subtreeTotalIntervals = 1;

            if (leftChild != null) { subtreeTotalIntervals += leftChild.computeAreas(); }
            if (rightChild != null) { subtreeTotalIntervals += rightChild.computeAreas(); }
            
            return subtreeTotalIntervals;
        }
        
        /**
         * Implements the recursive logic of computing the probability intervals used for sampling.
         * 
         * 
         * @param leftEdge
         * 
         * @returns the rightmost endpoint of the subtree of probabilityIntervals rooted at this node
         */
        public float computeProbabilityIntervals(float leftEdge) throws IntervalException
        {
            float leftEndpoint = 0;
            float rightEndpoint;
            float rightEdge = 0;
            
            // supplied with the rightmost endpoint that the calling tree knows about
            // set that as the leftmost endpoint of the left subtree and proceed
            if (leftChild != null)
            {
                leftEndpoint = leftChild.computeProbabilityIntervals(leftEdge);
            } else { leftEndpoint = leftEdge; }

            rightEndpoint = leftEndpoint + (boxArea/totalBoxArea);
            probabilityInterval = new Interval(leftEndpoint, rightEndpoint);

            // proceed on the right subtree
            if (rightChild != null)
            {
                rightEdge = rightChild.computeProbabilityIntervals(rightEndpoint);
            } else { rightEdge = rightEndpoint; }
            
            logger.writeMessage("axisInterval: " + axisInterval.print() + ", probabilityInterval: " + probabilityInterval.print());
            
            // return the rightmost endpoint of the probability intervals under this entry,
            // so that the next higher layer of recursion can use that as the left endpoint
            return rightEdge;
        }
        
        /**
         * Convert the Entry to a human-readable string
         */
        public String print()
        {
            String output = "Entry with axisInterval " + axisInterval.print() + " and box height "
                            + box + "\n";
            return output;
        }
        
        /**
         * Recursive logic for printing the entire table in order
         */
        public String printRecursive()
        {
            String output = "";
            if (leftChild != null)
            {
                output = output + "\n" + leftChild.printRecursive();
            }
            output = output + "\n" + print() ;
            if (rightChild != null)
            {
                output = output + "\n" + rightChild.printRecursive();
            }
            return output;
        }
    }
}
