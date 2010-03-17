package org.apexlab;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoogleSearcher implements ApexSearcher{
	private final int NUM_PER_QUERY = 8;
	private final int MAX_ITERS = 8;

	public List<SearchResultEntry> search(String query, int numResults)
			throws Exception {
		int maxNum = NUM_PER_QUERY * MAX_ITERS;
		if (numResults > maxNum || numResults == 0)
			numResults = maxNum;
		int remainingNum = numResults;
		List<SearchResultEntry> results = new ArrayList<SearchResultEntry>();
		int startNum = 0;
		while (remainingNum > 0) {
			List<SearchResultEntry> partialResults = searchInternal(query,
					startNum);
			results.addAll(partialResults);
			if (partialResults.size() < NUM_PER_QUERY)
				break;
			remainingNum -= NUM_PER_QUERY;
			startNum += NUM_PER_QUERY;
		}
		if (results.size() > remainingNum)
			return results.subList(0, numResults);
		return results;
	}

	public List<SearchResultEntry> searchInternal(String query, int startIndex)
			throws Exception {
		System.err.println(startIndex);
		URL url = new URL(
				"http://ajax.googleapis.com/ajax/services/search/web?start="
						+ startIndex + "&max-results=8&rsz=large&v=1.0&q="
						+ query);
		URLConnection connection = url.openConnection();
		connection.addRequestProperty("Referer", "http://www.apexlab.org");
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}

		String response = builder.toString();
		JSONObject json = new JSONObject(response);
		JSONArray results = json.getJSONObject("responseData").getJSONArray(
				"results");
		List<SearchResultEntry> list = new ArrayList<SearchResultEntry>();
		for (int i = 0; i < results.length(); ++i) {
			JSONObject jsonResult = results.getJSONObject(i);
			SearchResultEntry entry = new SearchResultEntry(jsonResult
					.getString("url"), jsonResult
					.getString("titleNoFormatting"), jsonResult
					.getString("content"));
			list.add(entry);
		}
		return list;
	}

	public static void main(String[] args) throws Exception {
		GoogleSearcher searcher = new GoogleSearcher();
		int counter = 0;
		for (SearchResultEntry entry : searcher.search("python", 30)) {
			System.out.println(++counter);
			System.out.println(entry.getUrl());
			System.out.println(entry.getTitle());
			System.out.println(entry.getContent());
		}
	}
}
