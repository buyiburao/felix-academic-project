package util.zunge;

import java.util.HashMap;

public class DisambiguationInfo {
	static DictInfo dictInfo;
	public static HashMap<String,Integer> mapInto = new HashMap<String,Integer>();
	
	public static void init(DictInfo dictInfo){
		DisambiguationInfo.dictInfo = dictInfo;
	}
	
	public static void add(String input,Integer id){
		mapInto.put(input, id);
		//System.out.println(".");
	}
}
