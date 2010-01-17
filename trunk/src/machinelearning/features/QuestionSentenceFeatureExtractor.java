package machinelearning.features;

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

}
