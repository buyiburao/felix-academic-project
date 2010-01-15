package machinelearning.features;

import search.object.Query;
import search.object.Sentence;

public class TermRepetitionSentenceFeatureExtractor 
extends QueryBiasedSentenceFeatureExtractor{

	public TermRepetitionSentenceFeatureExtractor(Query query) {
		super(query);
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

	@Override
	public double getFeature(Sentence s) {
		int sum = 0;
		for(String t : query.getTermSet()){
			sum += s.getOccur(t);
		}
		return sum;
	}
	
	
	

}
