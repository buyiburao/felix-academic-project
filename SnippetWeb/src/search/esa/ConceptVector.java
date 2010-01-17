package search.esa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConceptVector
{
    Map<Integer, Concept> vectorMap = new HashMap<Integer, Concept>();
    
    public ConceptVector(EsaInfo info)
    {
        double sum = 0;
        for (int i = 0; i < info.hits; i++)
        {
            sum += Math.pow(info.weights[i], 2);
        }
        // Normalize Factor
        double factor = 1 / Math.sqrt(sum);
        
        for (int i = 0; i < info.hits; i++)
        {
            Concept concept = new Concept(info.ids[i], info.concepts[i], info.weights[i] * factor);
            vectorMap.put(info.ids[i], concept);
        }
    }
    
    public ConceptVector(List<Concept> concepts)
    {
        double sum = 0;
        for(Concept concept : concepts)
        {
            sum += Math.pow(concept.getWeight(), 2);
        }
        // Normalization factor
        double factor = 1 / Math.sqrt(sum);
        
        for(Concept concept : concepts)
        {
            concept.setWeight(factor * concept.getWeight());
            vectorMap.put(concept.getId(), concept);
        }
    }
    
    public double similarity(ConceptVector other)
    {
        if (other == null)
        {
            return 0;
        }
        double score = 0;
        for (int id : this.vectorMap.keySet())
        {
            double otherWeight = other.vectorMap.containsKey(id) ? other.vectorMap.get(id).getWeight() : 0.0;
            score += vectorMap.get(id).getWeight() * otherWeight;
        }
        return score;
    }
    
    public Set<Integer> getIdSet()
    {
        return vectorMap.keySet();
    }

    public Map<Integer, Concept> getVectorMap()
    {
        return vectorMap;
    }
}
