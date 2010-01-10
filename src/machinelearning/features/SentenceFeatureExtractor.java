package machinelearning.features;

import search.object.Sentence;

public abstract class SentenceFeatureExtractor
{
    public abstract double getFeature(Sentence s);
}
