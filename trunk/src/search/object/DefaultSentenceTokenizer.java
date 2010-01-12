package search.object;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DefaultSentenceTokenizer extends SentenceTokenizer
{
    
    static Set<Character> charSet = null;
    static PorterStemmer stemmer = null;
    
    public DefaultSentenceTokenizer()
    {
        if (charSet == null)
        {
            charSet = new HashSet<Character>();
            char[] set = new char[]{
                    '.', '!', '?', ' ', 
                    '@', '#', '$','%','^',
                    '&','*','(',')',
                    '<','>',',','.',':',
                    '\"',';'};
            
            for (int i = 0; i < set.length; i++)
            {
                charSet.add(set[i]);
            }
        }
        
        if (stemmer == null)
        {
            stemmer = new PorterStemmer();
        }
    }
    
    private String termNormalize(String term)
    {
        if (term.length() > 0 && charSet.contains((term.charAt(term.length() - 1))))
        {
            term = term.substring(0, term.length() - 1);
        }
        
        term = term.toLowerCase();
        return stemmer.stem(term);
    }
    
    @Override
    public List<Term> tokenize(String string)
    {
        List<Term> tokens = new LinkedList<Term>();
        for (int start = 0, current = 0; start < string.length(); current++)
        {
            if(charSet.contains(string.charAt(current)) || current == string.length() - 1)
            {
                String term = string.substring(start, current + 1).trim();
                tokens.add(new Term(term, termNormalize(term)));
                start = current + 1;
            }
        }
        return tokens;
    }
    
    public static void main(String[] args)
    {
        String test = "You don't know how many times successful men have been fucked!";
        DefaultSentenceTokenizer tokenizer = new DefaultSentenceTokenizer();
        for (Term t : tokenizer.tokenize(test))
        {
            System.out.println(t.getNormalized());
        }
    }

}
