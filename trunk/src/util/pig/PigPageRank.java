package util.pig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.zunge.WSReader;

public class PigPageRank {

	private static WSReader bigGraph = null;
	
	private Map<String, List<String>> inlinks = new HashMap<String, List<String>>();

	private Map<String, Integer> outdegrees = new HashMap<String, Integer>();

	public PigPageRank(String folder) {
		if (bigGraph == null) {
			bigGraph = new WSReader(folder);
		}
	}
	
	public PigPageRank selectNodes(Set<String> set, int ext) {
		Set<String> extended = extend(bigGraph, set, ext);

		for (String concept : extended) {
			inlinks.put(concept, new ArrayList<String>());
			outdegrees.put(concept, 0);
		}

		Set<String> nodes = outdegrees.keySet();
		for (String node : nodes) {
			List<String> list = inlinks.get(node);

			int[] ins = bigGraph.getInLink(bigGraph.getId(node));
			for (int in : ins) {
				String concept = bigGraph.getCcpt(in);
				if (nodes.contains(concept)) {
					list.add(concept);
					outdegrees.put(concept, outdegrees.get(concept) + 1);
				}
			}
		}
		
		return this;
	}
	
	private Set<String> extend(WSReader bigGraph, Set<String> set,
			int extend) {
		Set<String> ret = new HashSet<String>();
		
		Set<String> border = new HashSet<String>();
		for (String node : set) {
			if (bigGraph.getId(node) >= 0) {
				border.add(node);
			}
		}
		ret.addAll(border);
		
		for (int i = 0; i < extend; ++i) {
			border = extend(bigGraph, border);
			border.removeAll(ret);
			ret.addAll(border);
		}
		
		return ret;
	}

	private Set<String> extend(WSReader bigGraph, Set<String> border) {
		Set<String> ret = new HashSet<String>();

		for (String b : border) {
			int id = bigGraph.getId(b);
			if (id >= 0) {
				int[] bins = bigGraph.getInLink(id);
				for (int bin : bins) {
					ret.add(bigGraph.getCcpt(bin));
				}
			}
		}
		
		return ret;
	}

	public Map<String, Double> calc(Map<String, Double> bias, double lambda,
			int rounds) {
		Set<String> nodes = bias.keySet();
		int N = nodes.size();
		Map<String, Double> curr = new HashMap<String, Double>(bias);

		for (int i = 0; i < rounds; ++i) {
			Map<String, Double> temp = getEmptyVector(nodes);
			
			for (String node : bias.keySet()) {
				double rank = 0;
				
				for (String source : inlinks.get(node)) {
					rank += curr.get(source) / outdegrees.get(source);
				}
				
				rank = (1 - lambda) * rank + lambda / N;
				temp.put(node, rank);
			}
			
			curr = new HashMap<String, Double>(temp);
		}

		return curr;
	}

	private Map<String, Double> getEmptyVector(Set<String> nodes) {
		Map<String, Double> ret = new HashMap<String, Double>();
		for (String s : nodes) {
			ret.put(s, 0.0);
		}
		return ret;
	}
	
}
