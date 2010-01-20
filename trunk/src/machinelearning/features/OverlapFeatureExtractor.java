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
		Set<String> sentenceTermSet = new HashSet<String>();
		Set<String> queryTermSet = new HashSet<String>();
		for(Term t : s.getTerms())
			sentenceTermSet.add(t.getNormalized());
		for(Term t : query.getTermList()){
			queryTermSet.add(t.getNormalized());
		}
			
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
	public static void main(String[] args){
		Sentence sentence = new Sentence("I believe in god");
		Query query = new Query("believes in me");
		OverlapFeatureExtractor ofe = new OverlapFeatureExtractor(query);
		System.out.println(ofe.getFeature(sentence));
	}

}
