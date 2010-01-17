package search.esa;

public class Concept
{
    private int id;
    private String concept;
    private double weight;
    
    public Concept(int id, String concept, double weight)
    {
        super();
        this.id = id;
        this.concept = concept;
        this.weight = weight;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight(double weight)
    {
        this.weight = weight;
    }

    public int getId()
    {
        return id;
    }

    public String getConcept()
    {
        return concept;
    }
}
