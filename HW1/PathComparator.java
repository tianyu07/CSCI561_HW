

import java.util.ArrayList;
// StringLengthComparator.java
import java.util.Comparator;
import java.util.LinkedList;

public class PathComparator implements Comparator<Path>
{
    @Override
    public int compare(Path x, Path y)
    {
        // Assume neither string is null. Real code should
        // probably be more robust
        // You could also just return x.length() - y.length(),
        // which would be more efficient.
    	if (x.pathCost < y.pathCost) return -1;
        if (x.pathCost > y.pathCost) return 1;
        if (x.pathCost == y.pathCost) {
        	if (x.tag < y.tag) return -1;
        	else return 1;
        }
        return 0;
    }
}