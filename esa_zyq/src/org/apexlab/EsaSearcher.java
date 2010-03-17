package org.apexlab;

import java.util.ArrayList;
import java.util.List;

import org.apexlab.esa.EsaInfo;
import org.apexlab.esa.GetEsa;

public class EsaSearcher implements ApexSearcher {

	@Override
	public List<SearchResultEntry> search(String query, int numResults)
			throws Exception {
		EsaInfo ei = GetEsa.getEsa(query, numResults);
		List<SearchResultEntry> list = new ArrayList<SearchResultEntry>();
		for (int i = 0; i < ei.hits; ++i) {
			String url = "http://nonexist";
			String title = ei.concepts[i];
			String content = "placeholder";
			SearchResultEntry entry = new SearchResultEntry(url, title, content);
			list.add(entry);
		}
		return list;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		ApexSearcher searcher = new EsaSearcher();
		int counter = 0;
		for (SearchResultEntry entry : searcher.search("python", 30)) {
			System.out.println(++counter);
			System.out.println(entry.getUrl());
			System.out.println(entry.getTitle());
			System.out.println(entry.getContent());
		}
	}

}
