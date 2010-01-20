package search.snippet;

import java.util.List;

public class SentenceAverageScorer implements SentenceRankScorer {

	@Override
	public double getScore(List<Integer> rankList) {
		double score = 0.0;
		for(Integer i : rankList)
		{
			score += i;
		}
		score /= rankList.size();
		return score;
	}

}
