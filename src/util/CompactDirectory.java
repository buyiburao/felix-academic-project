package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CompactDirectory {
	private static final int DEFAULT_THRESHOLD = 50;
	private int threshold;
	private ArrayList<ArrayList<String>> lowArrays;
	private Map<String, Integer> highMap = new HashMap<String, Integer>();
	public CompactDirectory(){
		this(DEFAULT_THRESHOLD);
	}
	public CompactDirectory(int threshold){
		this.threshold = threshold;
		lowArrays = new ArrayList<ArrayList<String>>();
		for(int i = 0; i < threshold; ++i){
			lowArrays.add(new ArrayList<String>());
		}
	}
	public void LoadFromFile(String fileName) throws Exception{
		 LineReader reader = new LineReader(fileName);
		 while(reader.hasNext()){
			 String[] parts = reader.next().split("\t");
			 String term = parts[0];
			 int value = Integer.parseInt(parts[1]);
			 if (value > threshold){
				 highMap.put(term, value);
			 }
			 else{
				 lowArrays.get(value).add(term);
			 }
		 }
		 reader.close();
		 for(int i = 0; i < lowArrays.size(); ++i){
			 Collections.sort(lowArrays.get(i));
		 }
	}
	public int lookup(String term){
		 for (int i = 0; i < lowArrays.size(); ++i){
			 int index;
			 if ((index = Collections.binarySearch(lowArrays.get(i), term)) != -1){
				 return i;
			 }
		 }
		 if (highMap.get(term) != null)
			 return highMap.get(term);
		 else
			 return 0;
	}

}
