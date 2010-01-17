package search.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TermOccurence
{
    Map<String, Integer> occurMap = new HashMap<String, Integer>();
    private int termCount = 0;
    public TermOccurence()
    {
        
    }
    
    public int getTermOccur(String term)
    {
        if (occurMap.containsKey(term))
        {
            return occurMap.get(term);
        }
        else
        {
            return 0;
        }
    }
    
    public void addTerm(String term)
    {
        termCount++;
        if (occurMap.containsKey(term))
        {
            occurMap.put(term, occurMap.get(term) + 1);
        }
        else
        {
            occurMap.put(term, 1);
        }
    }
    
    public int getTotal()
    {
        return termCount;
    }
    
    public int getDistinctNum()
    {
        return occurMap.size();
    }
    
    public Set<String> getTermSet()
    {
        return occurMap.keySet();
    }
}
