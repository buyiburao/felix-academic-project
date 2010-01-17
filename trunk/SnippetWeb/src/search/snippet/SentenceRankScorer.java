package search.snippet;

import java.util.List;

public interface SentenceRankScorer
{
    public double getScore(List<Integer> rankList);
}
