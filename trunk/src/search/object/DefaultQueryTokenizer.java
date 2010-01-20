package search.object;

import java.util.LinkedList;
import java.util.List;

public class DefaultQueryTokenizer extends QueryTokenizer
{

    private static PorterStemmer stemmer = new PorterStemmer();
    
    @Override
    public List<Term> tokenize(Query query)
    {
        LinkedList<Term> ret = new LinkedList<Term>();
        String string = query.getString();
        for (String token: string.split("[ !@#$%^&*()<>?,./:\";{}+|\\-=\\\\]+"))
        {
            String normalized = stemmer.stem(token.toLowerCase());
            ret.add(new Term(token, normalized));
        }
        return ret;
    }
    
    public static void main(String[] args)
    {
//        String test = "c\\a& xxxx e482734 32874&^ dfjk& believe in me";
    	String test = "believe in me";
        Query query = new Query(test);
        DefaultQueryTokenizer tokenizer = new DefaultQueryTokenizer();
        for(Term t : tokenizer.tokenize(query))
        {
            System.out.println(t.getString() + "\t" + t.getNormalized());
        }
    }

}
