package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import machinelearning.features.CombinedSentenceFeatureExtractor;
import machinelearning.features.QueryRunSentenceFeatureExtractor;
import machinelearning.features.TermRepetitionSentenceFeatureExtractor;
import machinelearning.features.UniqueTermRepetitionSentenceFeatureExtractor;
import search.object.Document;
import search.object.Query;
import search.object.Sentence;

public class SlidingWindowSnippetGenerator {
	private int windowSize;
	public SlidingWindowSnippetGenerator(int windowSize){
		this.windowSize = windowSize;
	}
	List<Sentence> generate(Document d, Query q){
		System.out.println(q.getString());
		List<Sentence> window = new LinkedList<Sentence>();
		List<Sentence> allSentences = d.getSentences();
		CombinedSentenceFeatureExtractor fe = new CombinedSentenceFeatureExtractor();
		fe.addSentenceFeatureExtractor(
				new TermRepetitionSentenceFeatureExtractor(q),
				1
				);
		fe.addSentenceFeatureExtractor(
				new UniqueTermRepetitionSentenceFeatureExtractor(q),
				3
				);
		fe.addSentenceFeatureExtractor(
				new QueryRunSentenceFeatureExtractor(q),
				1
				);
		double highestScore = 0;
		List<Sentence> selectedWindow = new ArrayList<Sentence>();
		for(Sentence s : allSentences){
			if (window.size() < windowSize){
				window.add(s);
			}
			else {
				window.remove(0);
				window.add(s);
			}
			if (window.size() == windowSize){
				double sum = 0;
				for(Sentence sentenceInWindow : window){
					sum += fe.getFeature(sentenceInWindow);
				}
				if (sum > highestScore){
					selectedWindow.clear();
					selectedWindow.addAll(window);
					highestScore = sum;
					System.out.println();
					System.out.println(highestScore);
					for(Sentence ss : window){
						System.out.println(ss.getString());
					}
				}
			}
		}
		return selectedWindow;
	}
	public static void main(String[] args) throws Exception{
		LineReader reader = new LineReader("d:\\us.txt");
		SlidingWindowSnippetGenerator generator = new SlidingWindowSnippetGenerator(3);
		String all = reader.readToEnd();
		Document doc = new Document(all);
		BufferedReader stdinReader = new BufferedReader(new InputStreamReader(System.in));
		String queryStr;
		while((queryStr = stdinReader.readLine()) != null){
			Query q = new Query(queryStr);
			List<Sentence> snippet =  generator.generate(doc, q);
			System.out.println("###############");
			for(Sentence s : snippet){
				System.out.println(s.getString());
			}
		}
		stdinReader.close();
		
	}
}
