package search.object;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EmptyWordFilter extends TermFilter
{

    @Override
    public List<Term> filter(List<Term> origin)
    {
        List<Term> ret = new ArrayList<Term>();
        for (Term t : origin)
        {
            if (t.getNormalized().trim().length() != 0)
            {
                ret.add(t);
            }
        }
        return ret;
    }
    
    public static void main(String[] args)
    {
        List<Term> terms = new LinkedList<Term>();
        terms.add(new Term("", ""));
        terms.add(new Term("dfkla", ","));
        TermFilter filter = new EmptyWordFilter(); 
        System.out.println(filter.filter(terms).size());
    }

}
