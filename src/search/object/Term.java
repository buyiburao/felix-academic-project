package search.object;

public class Term extends Token
{
    int id;
    
    public Term(String term, int id)
    {
        super(term);
        this.id = id;
    }

    public String getTerm()
    {
        return originalString;
    }

    public int getId()
    {
        return id;
    }
    
    public boolean equal(Term other)
    {
        return this.id == other.id;
    }
}
