package org.apexlab;

import java.net.URLEncoder;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;

import org.apexlab.docserver.Config;
import org.apexlab.docserver.IWikiDocProvider;
import org.apexlab.esa.EsaInfo;
import org.apexlab.esa.GetEsa;

public class EsaSearcher implements ApexSearcher {
	private final int SNIPPET_SIZE = 100;
	@Override
	public List<SearchResultEntry> search(String query, int numResults)
			throws Exception {
		EsaInfo ei = GetEsa.getEsa(query, numResults);
		List<SearchResultEntry> list = new ArrayList<SearchResultEntry>();
		System.out.println(Config.serverString);
		IWikiDocProvider wdp = (IWikiDocProvider)Naming.lookup(Config.serverString);
		for (int i = 0; i < ei.hits; ++i) {
			System.out.println(i);
			String url = "doc.jsp?title=" + URLEncoder.encode(ei.concepts[i], "utf8");
			String title = ei.concepts[i];
			String content = trimToProperSize(wdp.getDocContentByTitle(title));
			SearchResultEntry entry = new SearchResultEntry(url, title, content);
			list.add(entry);
		}
		return list;
	}
	private String trimToProperSize(String originalStr){
		if (originalStr.length() < SNIPPET_SIZE)
			return originalStr;
		int index = originalStr.indexOf('.', SNIPPET_SIZE);
		if (index < SNIPPET_SIZE * 2)
			return originalStr.substring(0, index + 1);
		else 
			return originalStr.substring(0, SNIPPET_SIZE);
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
