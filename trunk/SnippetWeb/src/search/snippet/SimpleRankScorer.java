package search.snippet;

import java.util.List;

public class SimpleRankScorer implements SentenceRankScorer
{

    @Override
    public double getScore(List<Integer> rankList)
    {
        double score = 0;
        for(Integer integer : rankList)
        {
            score += Math.pow(0.9, integer);
        }
        return score;
    }
}
