package search.object;

import java.util.List;

public class Sentence extends Token
{
    private static SentenceTokenizer defaultTokenizer = new DefaultSentenceTokenizer();
    
    List<Term> terms;
    
    public Sentence(String string)
    {
        super(string);
        terms = defaultTokenizer.tokenize(originalString);
    }
    
    public Sentence(String string, SentenceTokenizer tokenizer)
    {
        super(string);
        terms = tokenizer.tokenize(originalString);
    }
    
}
