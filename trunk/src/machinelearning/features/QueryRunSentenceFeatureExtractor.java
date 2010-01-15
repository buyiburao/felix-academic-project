package machinelearning.features;

import java.util.Set;

import search.object.Query;
import search.object.Sentence;
import search.object.Term;

public class QueryRunSentenceFeatureExtractor extends QueryBiasedSentenceFeatureExtractor{

	public QueryRunSentenceFeatureExtractor(Query query) {
		super(query);
	}

	@Override
	public double getFeature(Sentence s) {
		Set<String> queryTermSet = query.getTermSet();
		int run = 0;
		int runLocal = 0;
		for(Term t : s.getTerms()){
			if (queryTermSet.contains(t.getNormalized())){
				++runLocal;
			}
			else{
				if (runLocal > run)
					run = runLocal;
				runLocal = 0;
			}
		}
		if (runLocal > run)
			run = runLocal;
		return run;
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

}
