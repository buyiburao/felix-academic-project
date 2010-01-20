package machinelearning.features;

import search.object.Document;
import search.object.Sentence;

public class QuestionSentenceFeatureExtractor extends SentenceFeatureExtractor {

	@Override
	public double getFeature(Sentence s) {
		return s.isQuestion() ? 1.0 : 0.0;
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}
	
	public static void main(String[] args){
		Document doc = new Document("Are you kidding?");
		Sentence s = doc.getSentences().get(0);
		QuestionSentenceFeatureExtractor question = new QuestionSentenceFeatureExtractor();
		System.out.println(question.getFeature(s));
	}

}
