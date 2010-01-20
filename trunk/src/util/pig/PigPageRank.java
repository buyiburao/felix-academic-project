package util.pig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.zunge.WSReader;

public class PigPageRank {

	private static WSReader bigGraph = null;

	private static Set<String> nodes = new HashSet<String>();

	public PigPageRank(String folder) {
		if (bigGraph == null) {
			bigGraph = new WSReader(folder);
		}
	}

	public PigPageRank selectNodes(Set<String> set, int ext) {
		nodes = extend(bigGraph, set, ext);
		return this;
	}

	private Set<String> extend(WSReader bigGraph, Set<String> set, int extend) {
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
				if (bins != null) {
					for (int bin : bins) {
						ret.add(bigGraph.getCcpt(bin));
					}
				}
			}
		}

		return ret;
	}

	public Map<String, Double> calc(Map<String, Double> pBias, double lambda,
			int rounds) {
		List<String> list = new ArrayList<String>(nodes);
		Collections.sort(list);
		int N = list.size();
		System.out.println(N + "\t" + bigGraph.ccpts() + "\t"
				+ (N * 100.0 / bigGraph.ccpts()) + "%\t" + new Date());
		
		double[] bias = new double[N];
		for (String s : pBias.keySet()) {
			int id = Collections.binarySearch(list, s);
			if (id >= 0) {
				bias[id] = pBias.get(s);
			}
		}
		
		List<List<Integer>> inlinks = new ArrayList<List<Integer>>();
		int[] outdegrees = new int[N];
		for (int i = 0; i < N; ++i) {
			String s = list.get(i);
			List<Integer> li = new ArrayList<Integer>();
			int[] ins = bigGraph.getInLink(bigGraph.getId(s));
			if (ins == null) {
				ins = new int[0];
			}
			for (int in : ins) {
				String t = bigGraph.getCcpt(in);
				int newIn = Collections.binarySearch(list, t);
				if (newIn >= 0) {
					li.add(newIn);
					outdegrees[newIn]++;
				}
			}
			inlinks.add(li);
		}

		double[] cur = Arrays.copyOf(bias, bias.length);
		double[] temp = new double[N];
		for (int i = 0; i < rounds; ++i) {
			System.out.println("Pig Rank " + i + "\t" + new Date());
			Arrays.fill(temp, 0);
			
			for (int j = 0; j < N; ++j) {
				for (int k : inlinks.get(j)) {
					temp[j] += cur[k] / outdegrees[k];
				}
				temp[j] = temp[j] * (1 - lambda) + lambda * bias[j] / N;
			}
			
			cur = Arrays.copyOf(temp, temp.length);
		}
		
		Map<String, Double> ret = new HashMap<String, Double>();
		for (int i = 0; i < list.size(); ++i) {
			ret.put(list.get(i), cur[i]);
		}
		return ret;
	}

}