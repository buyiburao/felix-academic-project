package machinelearning.features;

import search.object.Query;
import search.object.Sentence;

public class ExactMatch extends QueryBiasedSentenceFeatureExtractor
{

    public ExactMatch(Query query)
    {
        super(query);
    }

    @Override
    public double getFeature(Sentence sentence)
    {
        if (sentence.getString().toLowerCase().contains(query.getString()))
        {
            return 1.0;
        }
        else
        {
            return 0.0;
        }
    }

    @Override
    public String getName()
    {
        return "Query exact match sentence feature extractor";
    }

}
