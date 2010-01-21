package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import machinelearning.snippet.ConfigConstant;
import search.esa.EsaInfo;
import search.esa.GetEsa;
import search.object.Document;
import search.object.Query;
import search.object.Sentence;

public class QueryDocumentConceptRankEvaluator {
	protected String folder;
	protected double queryBoost;
	protected Query q = new Query("");
	protected Document doc = new Document("");
	protected int iterNum;

	public QueryDocumentConceptRankEvaluator(Properties prop) {
		this.folder = prop.getProperty(ConfigConstant.LINK_FOLDER_CONFIG, ConfigConstant.DEFAULT_LINK_FOLDER);
		this.queryBoost = Double.parseDouble(
				prop.getProperty(ConfigConstant.QUERY_CONCEPT_BOOST_CONFIG, ConfigConstant.DEFAULT_QUERY_CONCEPT_BOOST));
		this.iterNum = Integer.parseInt(
				prop.getProperty(ConfigConstant.ITER_NUM_CONFIG, ConfigConstant.DEFAULT_ITER_NUM));
	}

	public double getConceptRank(Query q, Sentence sentence) throws Exception {
		GetConceptSingleton getter = GetConceptSingleton.getInstance();
		if (!q.getString().equals(this.q.getString()) || !sentence.getDoc().equals(this.doc)) {
			// recalculate PR
			List<String> queryConceptList = getter.getConcepts(q.getString());
			List<String> docConceptList = new ArrayList<String>();
			for(Sentence sentenceInDoc : sentence.getDoc().getSentences()){
				docConceptList.addAll(getter.getConcepts(sentenceInDoc.getString()));
			}
			calculate(queryConceptList, docConceptList);
			this.q = q;
			this.doc = sentence.getDoc();
		}
		List<String> sentenceConcepts = getter.getConcepts(sentence.getString());
		double sum = 0;
		for (String str : sentenceConcepts) {
			sum += getPRByConcept(str);
		}
		return sum;

	}

	protected double getPRByConcept(String str) {
		return ConceptPRCalculator.getPRByConcept(str);
	}

	protected void calculate(List<String> queryConceptList,
			List<String> docConceptList) {
		ConceptPRCalculator.calculate(queryConceptList, docConceptList, 0.1, queryBoost, this.folder, iterNum);
	}
}
