package machinelearning.snippet;

import java.util.List;
import java.util.Properties;

import search.object.Document;
import search.object.FeatureValue;
import search.object.Query;
import search.object.Sentence;
import util.LineReader;
import util.QueryDocumentConceptRankEvaluator;
import util.SVMLightInputGenerator;

public class SnippetSVMLightInputGenerator {
	private SVMLightInputGenerator gen = new SVMLightInputGenerator();
	private Properties properties;
	private QueryDocumentConceptRankEvaluator evaluator;
	public SnippetSVMLightInputGenerator(){
		this(new Properties());
	}
	public SnippetSVMLightInputGenerator(Properties p){
		properties = p;
		evaluator = new QueryDocumentConceptRankEvaluator(p);
	}
	public void addCase(Sentence s, Query q, double target, boolean withQid, int qid) throws Exception{
		SnippetFeatureExtractor sfe = new SnippetFeatureExtractor(s, q, evaluator, properties);
		List<FeatureValue> features = sfe.getFeatures();
		for(FeatureValue fv : features){
			gen.addFeatureValue(fv);
		}
		gen.setTarget(target);
		if (withQid)
			gen.setQid(qid);
		gen.finishCurrentCase();
	}
	public void addCase(Sentence s, Query q, double target) throws Exception{
		addCase(s, q, target, false, 0);
	}
	public void addCase(Sentence s, Query q, double target, int qid) throws Exception{
		addCase(s, q, target, true, qid);
	}
	public String dumpToString(){
		return gen.dumpToString();
	}
	public static void main(String[] args) throws Exception{
		LineReader reader = new LineReader("d:\\us.txt");
		String all = reader.readToEnd();
		Document doc = new Document(all);
		Query q = new Query("united states of america");
		Properties prop = new Properties();
		prop.setProperty(ConfigConstant.TF_FILE_CONFIG, "d:\\wikitf_partial");
		prop.setProperty(ConfigConstant.DEFAULT_DOC_NUM, "1000000");
		SnippetSVMLightInputGenerator gen = new SnippetSVMLightInputGenerator(prop);
		int target = 0;
		for(Sentence s : doc.getSentences()){
			gen.addCase(s, q, target++ + 0.5, target);
		}
		System.out.print(gen.dumpToString());
	}
}
