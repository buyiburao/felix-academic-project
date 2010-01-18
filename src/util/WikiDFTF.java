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
	static Map<String, IntObject> globalTermMap = new HashMap<String, IntObject>();
	static Map<String, IntObject> localTermMap = new HashMap<String, IntObject>();
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
		LineReader reader = new LineReader(args[1]);
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
				IntObject obj = localTermMap.get(t.getNormalized());
				if (obj != null)
					obj.value++;
				else
					localTermMap.put(t.getNormalized(), new IntObject(1));
			}
			for(String t : localTermMap.keySet()){
				int addValue = 0;
				if (tf)
					addValue = localTermMap.get(t).value;
				else
					addValue = 1;
				IntObject it = globalTermMap.get(t);
				if (it == null)
					globalTermMap.put(t, new IntObject(addValue));
				else{
					it.value += addValue;
				}
			}
		}
		reader.close();
		PrintWriter writer = new PrintWriter(new FileWriter(args[2]));
		for(String term : globalTermMap.keySet()){
			writer.println(term + "\t" + globalTermMap.get(term).value);
		}
		writer.close();
	}

}

class IntObject
{
	public IntObject(int v){
		value = v;
	}
	int value;
	
}