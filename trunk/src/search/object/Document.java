package search.object;

import java.util.List;

public class Document extends Token
{
    public static DefaulDocumentTokenizer defaultTokenizer = new DefaulDocumentTokenizer();

    List<Sentence> sentences;
    
    public Document(String string)
    {
        super(string);
        sentences = defaultTokenizer.tokenize(originalString);
    }
    
    public Document(String string, DocumentTokenizer tokenizer)
    {
        super(string);
        sentences = tokenizer.tokenize(originalString);
    }
}
