package machinelearning.features;

import search.esa.ConceptVector;
import search.esa.EsaInfo;
import search.esa.GetEsa;
import search.object.Query;
import search.object.Sentence;

public class ConceptSimilarityFeatureExtractor extends
		QueryBiasedSentenceFeatureExtractor {
	EsaInfo queryInfo;
	public ConceptSimilarityFeatureExtractor(Query query) throws Exception{
		super(query);
		queryInfo = GetEsa.getEsa(query.getString());
	}

	@Override
	public double getFeature(Sentence s){
		EsaInfo sentenceInfo = new EsaInfo();
		try{
			sentenceInfo = GetEsa.getEsa(s.getString());
		}catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
		ConceptVector qcv = new ConceptVector(queryInfo);
		ConceptVector scv = new ConceptVector(sentenceInfo);
		return qcv.similarity(scv);
	}

	@Override
	public String getName() {
		return null;
	}

}
