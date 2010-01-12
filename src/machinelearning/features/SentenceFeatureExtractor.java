package machinelearning.features;

import search.object.Sentence;

public abstract class SentenceFeatureExtractor extends FeatureExtractor
{
    public abstract double getFeature(Sentence s);
}
