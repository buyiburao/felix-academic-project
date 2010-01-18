package search.object;

import java.util.List;

public class Sentence extends Token
{
    private static SentenceTokenizer defaultTokenizer = new DefaultSentenceTokenizer();
    TermOccurence occurence = null;
    List<Term> terms;
    Document doc;
    boolean withQuestionMark = false;
    double percentageLocation = 0.0;
    
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

    public List<Term> getTerms()
    {
        return terms;
    }

    public Document getDoc()
    {
        return doc;
    }

    public void setDoc(Document doc)
    {
        this.doc = doc;
    }
    
    
    private void readyOccurence()
    {
        if (occurence == null)
        {
            occurence = new TermOccurence();
            for(Term t : terms)
            {
                occurence.addTerm(t.getNormalized());
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
    public boolean isQuestion(){
    	return withQuestionMark;
    }
    public void setQuestion(){
    	withQuestionMark = true;
    }
    public double getPercentageLocation(){
    	return percentageLocation;
    }
    public void setPercentageLocation(double percent){
    	percentageLocation = percent;
    }
    
}
