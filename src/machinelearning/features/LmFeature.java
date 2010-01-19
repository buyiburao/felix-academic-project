package machinelearning.features;

import search.object.Query;
import search.object.Sentence;
import util.CompactDirectory;

public class LmFeature extends QueryBiasedSentenceFeatureExtractor
{
	private int docNum = 1000000;
	private static CompactDirectory dict;
	private static String tfFileName = "";
    public LmFeature(Query query, String tfFile, int docNum) throws Exception
    {
        super(query);
    	this.docNum = docNum;
        if (!tfFile.equals(tfFileName) || dict == null){
        	dict = new CompactDirectory();
        	dict.LoadFromFile(tfFile);
        	tfFileName = tfFile;
        }
    }

    @Override
    public double getFeature(Sentence sentence)
    {
        double score = 0.0;
        double u = 0.1;
        for (String t : query.getTermSet())
        {
        	int val = dict.lookup(t);
        	if (val == 0)
        		val = 1;
            double pwc = val / 1.0 / docNum;
            score += query.getOccur(t) * Math.log((sentence.getOccur(t) + u * pwc)/ (sentence.getLength() + u));
//            System.out.println(score);
//            if (Double.isInfinite(score)){
//            	System.out.println(query.getOccur(t) + " " + sentence.getOccur(t) + " " + sentence.getLength());
//            }
        }
        return score;
    }

    @Override
    public String getName()
    {
        return "LM feature extractor";
    }
}
