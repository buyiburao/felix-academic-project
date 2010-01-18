package machinelearning.features;

import java.util.ArrayList;

import search.object.Sentence;

public class CombinedSentenceFeatureExtractor extends SentenceFeatureExtractor{
	ArrayList<SentenceFeatureExtractor> fes = new ArrayList<SentenceFeatureExtractor>();
	ArrayList<Double> weights = new ArrayList<Double>();

	public void addSentenceFeatureExtractor(SentenceFeatureExtractor fe, double weight){
		fes.add(fe);
		weights.add(weight);
	}
	public int getFeatureExtractorCount(){
		return fes.size();
	}
	@Override
	public String getName() {
		StringBuilder builder = new StringBuilder();
		builder.append("Sum of : ");
		for(int i = 0; i < fes.size(); ++i){
			builder.append(fes.get(i).getName());
			builder.append("*");
			builder.append(weights.get(i));
			builder.append(",");
		}
		return builder.toString();
	}

	@Override
	public double getFeature(Sentence s) {
		double sum = 0;
		for(int i = 0; i < fes.size(); ++i){
			sum += fes.get(i).getFeature(s) * weights.get(i);
		}
		return sum;
	}
}
