package machinelearning.features;

import search.object.Query;
import search.object.Sentence;

public abstract class QueryBiasedSentenceFeatureExtractor
{
    public abstract double getFeature(Query query, Sentence sentence);
}
