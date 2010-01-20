package machinelearning.snippet;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import machinelearning.features.ConceptRankFeatureExtractor;
import machinelearning.features.ExactMatchFeatureExtractor;
import machinelearning.features.LmFeature;
import machinelearning.features.LocationSentenceFeatureExtractor;
import machinelearning.features.OverlapFeatureExtractor;
import machinelearning.features.QueryRunSentenceFeatureExtractor;
import machinelearning.features.QuestionSentenceFeatureExtractor;
import machinelearning.features.SentenceFeatureExtractor;
import machinelearning.features.SentenceLength;
import machinelearning.features.TermRepetitionSentenceFeatureExtractor;
import machinelearning.features.UniqueTermRepetitionSentenceFeatureExtractor;
import search.object.FeatureValue;
import search.object.Query;
import search.object.Sentence;
import util.QueryDocumentConceptRankEvaluator;

public class SnippetFeatureExtractor {
	private ArrayList<SentenceFeatureExtractor> fes = new ArrayList<SentenceFeatureExtractor>();
	private Sentence sentence;
	public SnippetFeatureExtractor(Sentence s, Query q, QueryDocumentConceptRankEvaluator evaluator, Properties properties) throws Exception{
		sentence = s;
		fes.add(new ConceptRankFeatureExtractor(q, evaluator));
		fes.add(new ExactMatchFeatureExtractor(q));
		fes.add(new LmFeature(
				q,
				properties.getProperty(ConfigConstant.TF_FILE_CONFIG, ConfigConstant.DEFAULT_TF_FILE),
				Integer.parseInt(properties.getProperty(ConfigConstant.DOC_NUM_CONFIG, ConfigConstant.DEFAULT_DOC_NUM))));
		fes.add(new LocationSentenceFeatureExtractor());
		fes.add(new OverlapFeatureExtractor(q));
		fes.add(new QueryRunSentenceFeatureExtractor(q));
		fes.add(new QuestionSentenceFeatureExtractor());
		fes.add(new SentenceLength());
		fes.add(new TermRepetitionSentenceFeatureExtractor(q));
		fes.add(new UniqueTermRepetitionSentenceFeatureExtractor(q));
	}
	public List<FeatureValue> getFeatures(){
		ArrayList<FeatureValue> fList = new ArrayList<FeatureValue>();
		for(int i = 0; i < fes.size(); ++i){
			fList.add(new FeatureValue(i + 1, fes.get(i).getFeature(sentence)));
		}
		return fList;
	}
	
}
