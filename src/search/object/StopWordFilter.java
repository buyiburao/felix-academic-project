package search.object;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StopWordFilter extends TermFilter
{

    static Set<String> stopWords = null;
    @Override
    public List<Term> filter(List<Term> origin)
    {
        if (stopWords == null)
        {
            stopWords = new HashSet<String>();
            String[] words = new String[]{
                    "I", "a", "about", "an", "are", "as", 
                    "at", "be", "by", "com", "de", "en", 
                    "for", "form", "how", "in", "is", "it", 
                    "la", "of", "on", "or", "that", "the", 
                    "this", "to", "was", "what", "when",
                    "where", "who", "will", "with", "und",
                    "the", "www"
            };
            for (int i = 0; i < words.length; i++)
            {
                stopWords.add(words[i]);
            }
        }
        
        List<Term> ret = new ArrayList<Term>(origin.size());
        for (Term t : origin)
        {
            if (!stopWords.contains(t.getNormalized()))
            {
                ret.add(t);
            }
        }
        return ret;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
