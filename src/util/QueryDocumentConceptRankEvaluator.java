package util;

import java.util.ArrayList;

import search.esa.EsaInfo;
import search.esa.GetEsa;
import search.object.Document;
import search.object.Query;
import search.object.Sentence;
import search.object.Term;

public class QueryDocumentConceptRankEvaluator {
	private String folder;
	private Query q;
	private Document doc;

	public QueryDocumentConceptRankEvaluator(String folder) {
		this.folder = folder;
	}

	public double getConceptRank(Query q, Sentence sentence) throws Exception {
		if (!q.getString().equals(this.q.getString()) || !sentence.getDoc().equals(this.doc)) {
			// recalculate PR
			EsaInfo info = GetEsa.getEsa(q.getString());
			ArrayList<String> conceptList = new ArrayList<String>();
			for(String concept : info.concepts)conceptList.add(concept);
			ConceptPRCalculator.calculate(conceptList, 0.1, this.folder);
			this.q = q;
			this.doc = sentence.getDoc();
		}
		EsaInfo info = GetEsa.getEsa(sentence.getString());
		double sum = 0;
		for (String str : info.concepts) {
			sum += ConceptPRCalculator.getPRByConcept(str);
		}
		return sum;

	}
}
