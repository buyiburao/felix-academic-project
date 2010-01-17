package search.object;

import java.util.List;

public interface SentenceFilter
{
    public List<Sentence> filter(List<Sentence> sentences);
}
