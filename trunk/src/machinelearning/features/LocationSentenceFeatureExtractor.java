package machinelearning.features;

import search.object.Sentence;

public class LocationSentenceFeatureExtractor extends SentenceFeatureExtractor {

	@Override
	public double getFeature(Sentence s) {
		return s.getPercentageLocation();
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

}
