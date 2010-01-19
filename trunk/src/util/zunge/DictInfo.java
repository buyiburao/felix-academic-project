package util.zunge;

import java.util.Dictionary;
import java.util.Hashtable;

public class DictInfo {
	static int ccptCount;
	static Dictionary<String,Integer> idict
		= new Hashtable<String, Integer>();
	static String[] dict;
	static int current = 0;
	
	
	public static void init(int ccptCount){
		DictInfo.ccptCount = ccptCount;
		idict = new Hashtable<String, Integer>();
		dict = new String[ccptCount];
		current = 0;
	}
	
	public static void put(String input){
		dict[current] = input;
		idict.put(input, current);
		current++;
	}
	
	public static int ccpts(){
		return ccptCount;
	}
	public static String getCcpt(int id){
		return dict[id];
	}
	public static int getId(String ccpt){
		if (idict.get(ccpt)==null){
			return -1;
		}
		else{
			return idict.get(ccpt);
		}
	}
}
