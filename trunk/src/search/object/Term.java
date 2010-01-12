package search.object;

public class Term extends Token
{
    String normalized;
    
    public Term(String term, String normalized)
    {
        super(term);
        this.normalized = normalized;
    }

    public String getTerm()
    {
        return originalString;
    }
    
    public String getNormalized()
    {
        return normalized;
    }
    
    public boolean equal(Term other)
    {
        return this.normalized == other.normalized;
    }
}
