package label;

public class SentenceInfo implements Comparable<SentenceInfo> {

	private String sentence;
	
	private double score;

	public SentenceInfo(String sentence, double score) {
		super();
		this.sentence = sentence;
		this.score = score;
	}

	public String getSentence() {
		return sentence;
	}

	public double getScore() {
		return score;
	}

	@Override
	public int compareTo(SentenceInfo other) {
		return Double.valueOf(this.score).compareTo(other.score);
	}
	
	
}
