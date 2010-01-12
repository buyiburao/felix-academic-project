package search.object;

import java.util.List;

public class Document extends Token
{
    public static DefaulDocumentTokenizer defaultTokenizer = new DefaulDocumentTokenizer();

    List<Sentence> sentences;
    TermOccurence occurence;
    
    public Document(String string)
    {
        super(string);
        occurence = null;
        sentences = defaultTokenizer.tokenize(this);
    }
    
    public Document(String string, DocumentTokenizer tokenizer)
    {
        super(string);
        occurence = null;
        sentences = tokenizer.tokenize(this);
    }

    public List<Sentence> getSentences()
    {
        return sentences;
    }
    
    public int getOccur(String term)
    {
        readyOccurence();
        return occurence.getTermOccur(term);
    }
    
    private void readyOccurence()
    {
        if (occurence == null)
        {
            occurence = new TermOccurence();
            for (Sentence s : sentences)
            {
                for (Term t : s.getTerms())
                {
                    occurence.addTerm(t.getNormalized());
                }
            }
        }
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
}
