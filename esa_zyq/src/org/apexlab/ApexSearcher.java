package org.apexlab;

import java.util.List;

public interface ApexSearcher {
	public List<SearchResultEntry> search(String query, int numResults) throws Exception;

}
