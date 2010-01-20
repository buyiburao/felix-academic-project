package machinelearning.features;

import java.util.List;

import search.object.Query;
import search.object.Sentence;
import search.object.Term;

public class ExactMatchFeatureExtractor extends
		QueryBiasedSentenceFeatureExtractor {

	private String mergedQueryStr;
	public ExactMatchFeatureExtractor(Query q) {
		super(q);
		mergedQueryStr = mergeTermToString(q.getTermList());
	}
	private String mergeTermToString(List<Term> terms){
		StringBuilder builder = new StringBuilder();
		for(Term t : terms){
			builder.append(t.getNormalized());
			builder.append(" ");
		}
		return builder.toString();
		
	}
	@Override
	public double getFeature(Sentence s) {
		String mergedSentenceStr = mergeTermToString(s.getTerms());
		return mergedSentenceStr.contains(mergedQueryStr) ? 1.0 : 0.0;
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}
	public static void main(String[] args){
		Sentence sentence = new Sentence("I believe in god");
		Query query = new Query("believes in");
		ExactMatchFeatureExtractor exact = new ExactMatchFeatureExtractor(query);
		System.out.println(exact.getFeature(sentence));
	}
}
