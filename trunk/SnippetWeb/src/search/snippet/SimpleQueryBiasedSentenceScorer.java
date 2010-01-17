package search.snippet;

import java.sql.SQLException;

import search.esa.ConceptVector;
import search.object.Query;
import search.object.Sentence;

public class SimpleQueryBiasedSentenceScorer implements SentenceScorer
{
    public static void main(String[] args) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, SQLException
    {
        SimpleQueryBiasedSentenceScorer scorer = new SimpleQueryBiasedSentenceScorer(
                "shit");
        Sentence sentence = new Sentence("About TAXI!");

        System.out.println(scorer.score(sentence));
    }
    private MysqlDriver driver = new MysqlDriver();

    private ConceptVector queryConceptVector;

    public SimpleQueryBiasedSentenceScorer(Query query)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, SQLException
    {

        driver.connect();
        queryConceptVector = driver.getConceptVector(query.getString());
    }

    public SimpleQueryBiasedSentenceScorer(String queryString)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, SQLException
    {

        driver.connect();
        queryConceptVector = driver.getConceptVector(queryString);
    }

    @Override
    public double score(Sentence sentence)
    {
        return score(sentence.getString());
    }

    @Override
    public double score(String sentenceString)
    {
        ConceptVector sentenceConceptVector = driver
                .getConceptVector(sentenceString);
        return queryConceptVector.similarity(sentenceConceptVector);
    }

}
