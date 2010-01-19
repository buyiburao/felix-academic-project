package search.object;


public class FeatureValue implements Comparable<FeatureValue>{
	public int featureId;
	public double value;
	
	public FeatureValue(int id, double value){
		this.featureId = id;
		this.value = value;
	}
	
	@Override
	public int compareTo(FeatureValue arg0) {
		return new Integer(featureId).compareTo(arg0.featureId);
	}
	
}
