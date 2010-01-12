package machinelearning.features;

import search.object.Sentence;

public class SentenceLength extends SentenceFeatureExtractor
{

    @Override
    public double getFeature(Sentence s)
    {
        return s.getLength();
    }

    @Override
    public String getName()
    {
        return "Sentence Length Feature Extractor";
    }

}
