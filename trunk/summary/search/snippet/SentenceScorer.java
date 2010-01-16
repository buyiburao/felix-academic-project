package search.snippet;

import search.object.Sentence;

public interface SentenceScorer
{
    public double score(Sentence sentence);
}
