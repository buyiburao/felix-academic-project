package util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import search.esa.Concept;
import search.esa.ConceptVector;
import search.snippet.MysqlDriver;

public class GetConceptSingleton {
	private GetConceptSingleton()
	{
		try {
			driver.connect();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private MysqlDriver driver = new MysqlDriver();
	
	private static GetConceptSingleton instance = new GetConceptSingleton();
	
	public List<String> getConcepts(String query){
		ConceptVector vec = driver.getConceptVector(query);
		ArrayList<String> list = new ArrayList<String>();
		Map<Integer, Concept> map = vec.getVectorMap();
		for(Concept c : map.values()){
			list.add(c.getConcept());
		}
		return list;
	}
	
	public ConceptVector getConceptVector(String query){
		return driver.getConceptVector(query);
	}
	
	public static GetConceptSingleton getInstance(){
		return instance;
	}
}
