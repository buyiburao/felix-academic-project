package machinelearning.features;

import search.object.Query;
import search.object.Sentence;

public class UniqueTermRepetitionSentenceFeatureExtractor extends
		QueryBiasedSentenceFeatureExtractor {

	public UniqueTermRepetitionSentenceFeatureExtractor(Query query) {
		super(query);
	}

	@Override
	public double getFeature(Sentence s) {
		int sum = 0;
		for(String t : query.getTermSet()){
			sum += s.getOccur(t) > 0 ? 1 : 0;
		}
		return sum;
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

}
