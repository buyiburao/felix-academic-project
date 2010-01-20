package util;

import java.util.ArrayList;
import java.util.Collections;

import search.object.FeatureValue;

public class SVMLightInputGenerator {
	
	class Case{
		double target;
		boolean withQid;
		int qid;
		ArrayList<FeatureValue> featureList = new ArrayList<FeatureValue>();
		public String toString(){
			StringBuilder builder = new StringBuilder();
			builder.append(target);
			builder.append(" ");
			if (withQid){
				builder.append("qid:" + qid);
				builder.append(" ");
			}
			boolean first = true;
			for (FeatureValue fv : featureList){
				if (!first){
					builder.append(" ");
				}
				first = false;
				builder.append(fv.featureId + ":" + fv.value);
			}
			return builder.toString();
		}
	}
	
	private Case currentCase = new Case();
	private ArrayList<Case> cases = new ArrayList<Case>();
	
	public void setTarget(double target){
		currentCase.target = target;
	}
	
	public void setQid(int qid){
		currentCase.qid = qid;
		currentCase.withQid = true;
	}
	
	public void addFeatureValue(int feature, double value){
		addFeatureValue(new FeatureValue(feature, value));
	}
	
	public void addFeatureValue(FeatureValue fv){
		currentCase.featureList.add(fv);
	}
	
	public void finishCurrentCase(){
		cases.add(currentCase);
		Collections.sort(currentCase.featureList);
		System.err.println(currentCase);
		currentCase = new Case();
	}
	
	public String dumpToString(){
		StringBuilder builder = new StringBuilder();
		for (Case c : cases){
			builder.append(c.toString());
			builder.append("\n");
		}
		return builder.toString();
	}

	public static void main(String[] args){
		SVMLightInputGenerator gen = new SVMLightInputGenerator();
		gen.setTarget(1.0);
		gen.setQid(1);
		gen.addFeatureValue(1, 1.0);
		gen.addFeatureValue(3, 3.0);
		gen.addFeatureValue(2, 2.0);
		gen.addFeatureValue(5, 5.0);
		gen.finishCurrentCase();
		gen.setTarget(5.0);
		gen.setQid(2);
		gen.addFeatureValue(1, 3.0);
		gen.addFeatureValue(2, 4.0);
		gen.addFeatureValue(5, 2.0);
		gen.finishCurrentCase();
		System.out.println(gen.dumpToString());
		
	}
}
