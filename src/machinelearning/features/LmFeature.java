package machinelearning.features;

import search.object.Query;
import search.object.Sentence;

public class LmFeature extends QueryBiasedSentenceFeatureExtractor
{

    public LmFeature(Query query)
    {
        super(query);
    }

    @Override
    public double getFeature(Sentence sentence)
    {
        double score = 0.0;
        double u = 0.1;
        double pwc = 1;
        for (String t : query.getTermSet())
        {
            score += query.getOccur(t) * Math.log((sentence.getOccur(t) + u * pwc)/ (sentence.getLength() + u));
        }
        return score;
    }

    @Override
    public String getName()
    {
        return "LM feature extractor";
    }
}
