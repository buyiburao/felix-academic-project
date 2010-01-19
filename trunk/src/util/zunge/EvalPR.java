package util.zunge;

import java.util.List;

public class EvalPR {
	static WSReader r;
	static CalcPR cpr;

	public static void init(WSReader r) {
		EvalPR.r = r;
		cpr = new CalcPR(r);
	}

	static int lineCount = 0;
	static double[] prForConcepts;
	static double[] bias;

	public static double getPRByConcept(String concept) {
		return prForConcepts[DictInfo.getId(concept)];
	}

	public static void eval(List<String> concepts, double d) {
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
		for (String s : concepts) {
			int id = DictInfo.getId(s);
			bias[id]++;
		}
		bias = cpr.normalize(bias);
		prForConcepts = cpr.calc(bias, d);
	}
	

}
