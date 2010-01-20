package util.pig;

import java.util.ArrayList;
import java.util.Arrays;
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
			System.out.println(">> " + ret.size());
			Map<String, Integer> newcomers = extend(bigGraph, border);
			border = new HashSet<String>(newcomers.keySet());
			border.removeAll(ret);
			
			for (String s : border) {
				if (newcomers.get(s) > i * 3) {
					ret.add(s);
				}
			}
		}
		System.out.println(">> " + ret.size());

		return ret;
	}

	private Map<String, Integer> extend(WSReader bigGraph, Set<String> border) {
		Map<String, Integer> ret = new HashMap<String, Integer>();

		for (String b : border) {
			int id = bigGraph.getId(b);
			if (id >= 0) {
				int[] bins = bigGraph.getInLink(id);
				if (bins != null) {
					for (int bin : bins) {
						String c = bigGraph.getCcpt(bin);
						if (ret.containsKey(c)) {
							ret.put(c, ret.get(c) + 1);
						} else {
							ret.put(c, 1);
						}
					}
				}
			}
		}

		return ret;
	}

	public Map<String, Double> calc(Map<String, Double> pBias, double lambda,
			int rounds) {
		System.out.println("Calc Begin\t" + new Date());
		Map<String, Integer> map = new HashMap<String, Integer>(nodes.size() * 2, 0.5f);
		int index = 0;
		for (String n : nodes) {
			map.put(n, index);
			index++;
		}
		
		int N = map.size();
		System.out.println(N + "\t" + bigGraph.ccpts() + "\t"
				+ (N * 100.0 / bigGraph.ccpts()) + "%");
		
		double[] bias = new double[N];
		for (String s : pBias.keySet()) {
			Integer id = map.get(s);
			if (id != null) {
				bias[id] = pBias.get(s);
			}
		}
		
		Map<Integer, List<Integer>> tInlinks = new HashMap<Integer, List<Integer>>();
		int[] outdegrees = new int[N];
		for (String s : map.keySet()) {
			List<Integer> li = new ArrayList<Integer>();
			int[] ins = bigGraph.getInLink(bigGraph.getId(s));
			if (ins == null) {
				ins = new int[0];
			}
			for (int in : ins) {
				String t = bigGraph.getCcpt(in);
				Integer newIn = map.get(t);
				if (newIn != null) {
					li.add(newIn);
					outdegrees[newIn]++;
				}
			}
			tInlinks.put(map.get(s), li);
		}
		List<List<Integer>> inlinks = new ArrayList<List<Integer>>();
		for (int i = 0; i < N; ++i) {
			inlinks.add(tInlinks.get(i));
		}

		double[] cur = Arrays.copyOf(bias, bias.length);
		double[] temp = new double[N];
		for (int i = 0; i < rounds; ++i) {
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
		for (String s : map.keySet()) {
			int i = map.get(s);
			ret.put(s, cur[i]);
		}
		
		System.out.println("Calc End\t" + new Date());
		return ret;
	}

}
