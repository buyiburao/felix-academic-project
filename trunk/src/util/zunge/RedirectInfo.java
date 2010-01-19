package util.zunge;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class RedirectInfo {
	static DictInfo dictInfo;
	public static HashMap<String,List<Integer>> mapInto = new HashMap<String,List<Integer>>();
	
	public static void init(DictInfo dictInfo){
		RedirectInfo.dictInfo = dictInfo;
	}
	
	public static void add(String input,Integer id){
		if (mapInto.get(input)==null){
			mapInto.put(input,new Vector<Integer>());
		}
		mapInto.get(input).add(id);
		//System.out.println(".");
	}
}
