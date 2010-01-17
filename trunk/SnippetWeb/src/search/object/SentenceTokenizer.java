package search.object;

import java.util.List;

public abstract class SentenceTokenizer extends Tokenizer
{

    public abstract List<Term> tokenize(String string);
    
}
