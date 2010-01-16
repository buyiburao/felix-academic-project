package search.object;

import java.util.ArrayList;
import java.util.List;

public class LengthSentenceFilter implements SentenceFilter
{

    private int min = 5;
    private int max = 249;
    
    
    public LengthSentenceFilter(int min, int max)
    {
        this.min = min;
        this.max = max;
    }
    
    public LengthSentenceFilter()
    {
        
    }
    
    @Override
    public List<Sentence> filter(List<Sentence> sentences)
    {
        List<Sentence> toRet = new ArrayList<Sentence>();
        for(Sentence sentence: sentences)
        {
            if( sentence.getString().length() > min && sentence.getString().length() < max)
            {
                toRet.add(sentence);
            }
        }
        return toRet;
    }
}
