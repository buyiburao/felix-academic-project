package util;

import java.util.ArrayList;
import java.util.List;

import search.esa.EsaInfo;
import search.esa.GetEsa;
import search.object.Query;
import search.object.Term;
import util.zunge.EvalPR;
import util.zunge.WSReader;

public class ConceptPRCalculator {
	static boolean isInited = false;

	static void init(String graphFolder) {
		if (isInited)
			return;
		WSReader r = new WSReader(graphFolder);
		EvalPR.init(r);
	}

	static void calculate(List<String> queryConcepts, List<String> docConcepts, double d, double queryBoost, String graphFolder) {
		init(graphFolder);
		EvalPR.eval(queryConcepts, docConcepts, d, queryBoost);
	}

	static double getPRByConcept(String concept) {
		return EvalPR.getPRByConcept(concept);
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("usage: prog graphfolder");
			System.exit(0);
		}
		String graphFolder = args[0];
		Query q = new Query("united states");
		ArrayList<String> conceptList = new ArrayList<String>();
		for (Term t : q.getTermList()) {
			EsaInfo info = GetEsa.getEsa(t.getString());
			System.out.println("concepts: ");
			for (String con : info.concepts) {
				System.out.print(con + " ");
				conceptList.add(con);
			}
		}
		System.out.println();
		long beginTime = System.currentTimeMillis();
		ConceptPRCalculator.calculate(conceptList, new ArrayList<String>(), 0.1, 10, graphFolder);
		System.out.println((System.currentTimeMillis() - beginTime) + "ms");
		System.out.println(ConceptPRCalculator.getPRByConcept(conceptList.get(0)));
	}
}
