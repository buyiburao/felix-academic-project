package search.snippet;

import java.sql.SQLException;

import search.esa.ConceptVector;
import search.object.Sentence;

public class SimpleQueryBiasedSentenceScorer implements SentenceScorer
{
    private ConceptVector queryConceptVector;
    private MysqlDriver driver = new MysqlDriver();
    
    public SimpleQueryBiasedSentenceScorer(String query) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException
    {
        
        driver.connect();
        queryConceptVector = driver.getConceptVector(query); 
    }

    @Override
    public double score(Sentence sentence)
    {
        ConceptVector sentenceConceptVector = driver.getConceptVector(sentence.getString());
        return queryConceptVector.similarity(sentenceConceptVector);
    }
    
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException
    {
        SimpleQueryBiasedSentenceScorer scorer = new SimpleQueryBiasedSentenceScorer("shit");
        Sentence sentence = new Sentence("About TAXI!");
        
        System.out.println(scorer.score(sentence));
    }

}
