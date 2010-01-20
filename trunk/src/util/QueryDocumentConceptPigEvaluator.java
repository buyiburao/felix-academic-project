package util;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import machinelearning.snippet.ConfigConstant;
import util.pig.PigPRCalculator;

public class QueryDocumentConceptPigEvaluator extends QueryDocumentConceptRankEvaluator{

	private PigPRCalculator calc;
	private Map<String, Double> pigMap;

	public QueryDocumentConceptPigEvaluator(Properties prop) {
		super(prop);
		calc = new PigPRCalculator(prop.getProperty(
				ConfigConstant.LINK_FOLDER_CONFIG,
				ConfigConstant.DEFAULT_LINK_FOLDER));

	}
	protected double getPRByConcept(String str) {
		Double value = pigMap.get(str);
		if (value != null)
			return value.doubleValue();
		else
			return 0;
	}

	protected void calculate(ArrayList<String> queryConceptList,
			ArrayList<String> docConceptList) {
		pigMap = calc.calc(queryConceptList, docConceptList, 0.1, queryBoost);
	}

}
