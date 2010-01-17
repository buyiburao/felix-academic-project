package search.object;

import java.util.List;

public abstract class DocumentTokenizer extends Tokenizer
{

    public abstract List<Sentence> tokenize(Document doc);
    
}
