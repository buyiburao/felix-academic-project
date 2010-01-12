package machinelearning.features;

import search.object.Query;

public abstract class QueryBiasedSentenceFeatureExtractor extends SentenceFeatureExtractor
{
    protected Query query;
    public QueryBiasedSentenceFeatureExtractor(Query query)
    {
        this.query = query;
    }
}
