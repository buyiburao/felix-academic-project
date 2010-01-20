package machinelearning.features;

import search.object.Query;
import search.object.Sentence;
import util.QueryDocumentConceptRankEvaluator;

public class ConceptRankFeatureExtractor extends
		QueryBiasedSentenceFeatureExtractor {
	QueryDocumentConceptRankEvaluator evaluator;
	public ConceptRankFeatureExtractor(Query query, QueryDocumentConceptRankEvaluator evaluator) {
		super(query);
		this.evaluator = evaluator;
	}

	@Override
	public double getFeature(Sentence s) {
		try{
			return evaluator.getConceptRank(query, s);
		}
		catch(Exception e){
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
