package search.object;

import java.util.List;

public abstract class TermFilter
{
    public abstract List<Term> filter(List<Term> origin);
}
