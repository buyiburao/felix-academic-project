package util;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import search.object.DefaultSentenceTokenizer;
import search.object.SentenceTokenizer;
import search.object.Term;



public class WikiDFTF {
	static Map<Term, Integer> globalTermMap = new HashMap<Term, Integer>();
	static Map<Term, Integer> localTermMap = new HashMap<Term, Integer>();
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		if (args.length != 3)
		{
			System.out.println("usage: prog [tf|df] inputfile df_out");
			System.exit(0);
		}
		boolean tf = true;
		if (args[0].equals("df"))
			tf = false;
		FileLineReader reader = new FileLineReader(args[1]);
		SentenceTokenizer tokenizer = new DefaultSentenceTokenizer();
		int lineCounter = 0;
		while(reader.hasNext())
		{
			if (++lineCounter % 10000 == 0)
				System.err.println("Line:" + lineCounter);
			localTermMap.clear();
			String text = reader.next().split("\t", 2)[1];
			List<Term> tokens = tokenizer.tokenize(text);
			for(Term t : tokens){
				if (localTermMap.containsKey(t))
					localTermMap.put(t, localTermMap.get(t) + 1);
				else
					localTermMap.put(t, 1);
			}
			for(Term t : localTermMap.keySet()){
				int addValue = 0;
				if (tf)
					addValue = localTermMap.get(t);
				else
					addValue = 1;
				Integer it = globalTermMap.get(t);
				if (it == null)
					globalTermMap.put(t, addValue);
				else{
					globalTermMap.put(t, globalTermMap.get(t) + addValue);
				}
			}
		}
		reader.close();
		PrintWriter writer = new PrintWriter(new FileWriter(args[2]));
		for(Term term : globalTermMap.keySet()){
			writer.println(term.getNormalized() + "\t" + globalTermMap.get(term));
		}
		writer.close();
	}

}

class Item
{
	public Item(int tf, int df){
		this.tf = tf;
//		this.df = df;
	}
	public int getDF(){
//		return df;
		return 0;
	}
	public void setDF(int df){
//		this.df = df;
	}
	public int getTF(){
		return tf;
	}
	public void setTF(int tf){
		this.tf = tf;
	}
//	int df = 0;
	int tf = 0;
	
}