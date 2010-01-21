package machinelearning.features;

import search.esa.ConceptVector;
import search.esa.EsaInfo;
import search.esa.GetEsa;
import search.object.Query;
import search.object.Sentence;
import search.snippet.MysqlDriver;
import util.GetConceptSingleton;

public class ConceptSimilarityFeatureExtractor extends
		QueryBiasedSentenceFeatureExtractor {
	ConceptVector queryInfo;
	public ConceptSimilarityFeatureExtractor(Query query) throws Exception{
		super(query);
		queryInfo = GetConceptSingleton.getInstance().getConceptVector(query.getString());
		System.out.println(query.getString());
		for (int i : queryInfo.getIdSet()){
			System.out.println(i);
		}
	}

	@Override
	public double getFeature(Sentence s){
		ConceptVector sentenceInfo = null;
		try{
			sentenceInfo = GetConceptSingleton.getInstance().getConceptVector(s.getString());
		}catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
		if (sentenceInfo != null)
			return sentenceInfo.similarity(queryInfo);
		else
			return 0;
	}

	@Override
	public String getName() {
		return null;
	}
	public static void main(String[] args) throws Exception{
		Query q = new Query("reverse phone numbers");
		Sentence s = new Sentence("com Find phone numbers for free on PhoneNumber.");
		ConceptSimilarityFeatureExtractor sim = new ConceptSimilarityFeatureExtractor(q);
		System.out.println(sim.getFeature(s));
	}

}
