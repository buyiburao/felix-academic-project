package search.object;

import java.util.List;

public abstract class QueryTokenizer
{
    public abstract List<Term> tokenize(Query query);
}
