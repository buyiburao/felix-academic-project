package util.zunge;

import java.util.List;

public class EvalPR {
	static WSReader r;
	static CalcPR cpr;

	public static void init(WSReader r) {
		EvalPR.r = r;
		cpr = new CalcPR(r);
	}

	static int queryErrorCount = 0;
	static int docErrorCount = 0;

	static int lineCount = 0;
	static double[] prForConcepts;
	static double[] bias;

	public static double getPRByConcept(String concept) {
		int id = DictInfo.getId(concept);
		if (id < 0)
			return 0;
		return prForConcepts[id];
	}

	public static void eval(List<String> queryConcepts, List<String> docConcepts, double d, double queryBoost) {
		queryErrorCount = docErrorCount = 0;
		// PR for ccpts
		/*
		 * Input: bias: ccpt vector that appeared d: expand factor Algo: PR_n =
		 * d bias + (1-d) G PR_(n-1) Output: PR vector
		 */
		if (bias == null || bias.length != r.ccpts())
			bias = new double[r.ccpts()];
		for (int i = 0; i < r.ccpts(); ++i) {
			bias[i] = 0;
		}
		for (String s : queryConcepts) {
			int id = DictInfo.getId(s);
			if (id == -1){
				//System.err.println("Concept " + s + " not found.");
				++queryErrorCount;
				continue;
			}
			bias[id]+=queryBoost;
		}
		for (String s : docConcepts) {
			int id = DictInfo.getId(s);
			if (id == -1){
				//System.err.println("Concept " + s + " not found.");
				++docErrorCount;
				continue;
			}
			bias[id]++;
		}
		bias = cpr.normalize(bias);
		System.out.println("Query Concept Errors: " + queryErrorCount + "/" + queryConcepts.size());
		System.out.println("Doc Concept Errors: " + docErrorCount + "/" + docConcepts.size());
		prForConcepts = cpr.calc(bias, d);
		
	}
	

}
