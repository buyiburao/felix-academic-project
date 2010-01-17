package search.object;

public class Term extends Token
{
    private String normalized;
    private double weight;
    
    public Term(String term, String normalized)
    {
        super(term);
        this.normalized = normalized;
        weight = 1.0;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight(double weight)
    {
        this.weight = weight;
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
