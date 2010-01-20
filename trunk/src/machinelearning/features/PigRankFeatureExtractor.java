package machinelearning.features;

import java.util.Properties;

import search.object.Query;
import search.object.Sentence;
import util.QueryDocumentConceptPigEvaluator;

public class PigRankFeatureExtractor extends
		QueryBiasedSentenceFeatureExtractor {

	private QueryDocumentConceptPigEvaluator eval;

	public PigRankFeatureExtractor(Query query, Properties prop) {
		super(query);
		eval = new QueryDocumentConceptPigEvaluator(prop);
		
	}

	@Override
	public double getFeature(Sentence s) {
		try {
			return eval.getConceptRank(query, s);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
