package machinelearning.features;

import java.util.HashSet;
import java.util.Set;

import search.object.Query;
import search.object.Sentence;
import search.object.Term;

public class OverlapFeatureExtractor extends
		QueryBiasedSentenceFeatureExtractor {

	public OverlapFeatureExtractor(Query query) {
		super(query);
	}

	@Override
	public double getFeature(Sentence s) {
		Set<Term> sentenceTermSet = new HashSet<Term>();
		Set<Term> queryTermSet = new HashSet<Term>();
		sentenceTermSet.addAll(s.getTerms());
		queryTermSet.addAll(query.getTermList());
		int all = queryTermSet.size();
		queryTermSet.removeAll(sentenceTermSet);
		int overlap = all - queryTermSet.size();
		return overlap / 1.0 /all;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

}
