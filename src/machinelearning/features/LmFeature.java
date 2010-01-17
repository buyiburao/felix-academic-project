package machinelearning.features;

import search.object.Query;
import search.object.Sentence;
import util.CompactDirectory;

public class LmFeature extends QueryBiasedSentenceFeatureExtractor
{
	private static int DOC_NUM = 1000000;
	private CompactDirectory dict;
    public LmFeature(Query query, String tfFile) throws Exception
    {
        super(query);
        dict = new CompactDirectory();
        dict.LoadFromFile(tfFile);
    }

    @Override
    public double getFeature(Sentence sentence)
    {
        double score = 0.0;
        double u = 0.1;
        for (String t : query.getTermSet())
        {
            double pwc = dict.lookup(t);
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
