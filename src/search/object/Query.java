package search.object;

import java.util.List;
import java.util.Set;

public class Query
{
    private String originalString;
    private TermOccurence occurence = null;
    private List<Term> terms = null;
    private QueryTokenizer tokenzier = new DefaultQueryTokenizer();
    private TermFilter stopFilter = new StopWordFilter();
    private TermFilter emptyFilter = new EmptyWordFilter();
    
    
    public Query(String query)
    {
        originalString = query;
    }
    
    public String getString()
    {
        return originalString;
    }
    
    private void readyTerms()
    {
        if (terms == null)
        {
            terms = tokenzier.tokenize(this);
            terms = emptyFilter.filter(stopFilter.filter(terms));
        }
    }
    
    private void readyOccurence()
    {
        readyTerms();
        if (occurence == null)
        {
            for(Term t : terms)
            {
                occurence.addTerm(t.normalized);
            }
        }
    }
    
    public int getOccur(String term)
    {
        readyOccurence();
        return occurence.getTermOccur(term);
    }
    
    public int getLength()
    {
        readyOccurence();
        return occurence.getTotal();
    }
    
    public int getDstTermNum()
    {
        readyOccurence();
        return occurence.getDistinctNum();
    }
    
    public Set<String> getTermSet()
    {
        readyOccurence();
        return occurence.getTermSet();
    }
}
