package util.pig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PigPRCalculator {

	private static final int EXTEND = 2;

	private static final int ITER_ROUNDS = 20;
	
	private PigPageRank pig;

	public PigPRCalculator(String folder) {
		this.pig = new PigPageRank(folder);
	}

	public Map<String, Double> calc(List<String> queryConcepts,
			List<String> docConcepts, double lambda, double queryBoost) {
		Map<String, Double> bias = new HashMap<String, Double>();
		
		for (String qc : queryConcepts) {
			if (bias.containsKey(qc)) {
				bias.put(qc, bias.get(qc) + queryBoost);
			} else {
				bias.put(qc, queryBoost);
			}
		}
		
		for (String dc : docConcepts) {
			if (bias.containsKey(dc)) {
				bias.put(dc, bias.get(dc) + 1);
			} else {
				bias.put(dc, 1.0);
			}
		}
		
		return pig.selectNodes(bias.keySet(), EXTEND).calc(bias, lambda, ITER_ROUNDS);
	}

}
