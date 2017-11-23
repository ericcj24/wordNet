package com.algorithmsii.week1;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class WordNet {

	private final class SynsetID {
		private List<Integer> ids;
		public SynsetID(Integer firstId) {
			ids = new LinkedList<>();
			ids.add(firstId);
		}
		public void addId(Integer moreId) {
			ids.add(moreId);
		}
		public Iterable<Integer> getId() {
			return ids;
		}
	}

	private TreeMap<String, SynsetID> dict;
	private SAP sap;

	// constructor takes the name of the two input files
	public WordNet(String synsets, String hypernyms) {
		if (null == synsets || null == hypernyms) {
			throw new java.lang.IllegalArgumentException();
		}

		this.dict = new TreeMap<>();

		In insynsets = new In(synsets);
		In inhypernyms = new In(hypernyms);

		int size =0;
		while (!insynsets.isEmpty()) {
			size++;
			String[] threeSeg = insynsets.readLine().split(",");
			int synsetid = Integer.parseInt(threeSeg[0]);
			String[] synsetStrs = threeSeg[1].split(" ");

			for (String synsetStr : synsetStrs) {
				if (dict.containsKey(synsetStr)) {
					dict.get(synsetStr).addId(synsetid);
				} else {
					dict.put(synsetStr, new SynsetID(synsetid));
				}
			}

		}

		//82191
		Digraph digraph = new Digraph(size);
		while (!inhypernyms.isEmpty()) {
			String[] threeSeg = inhypernyms.readLine().split(",");
			int synsetid = Integer.parseInt(threeSeg[0]);
			for (int i=1; i<threeSeg.length; i++) {
				int hypernym = Integer.parseInt(threeSeg[i]);
				digraph.addEdge(synsetid, hypernym);
			}
		}

		// check make sure it is a rooted DAG
		// no circle, has root
		StdOut.println("E:" + digraph.E() + ", V:"+digraph.V());

		DigraphCircleDetection digraphCircleDetection = new DigraphCircleDetection(digraph);
		boolean hasCircle = digraphCircleDetection.hasCircle();
		boolean isRooted = digraphCircleDetection.isRooted();
		if (hasCircle || !isRooted) {
			throw new java.lang.IllegalArgumentException();
		}

		sap = new SAP(digraph);
	}

	private final class DigraphCircleDetection {
		private HashSet<Integer> visitedFromThisVetex;
		private boolean hasCircle=false;
		private boolean isRooted = true;
		private int root = -1;
		public DigraphCircleDetection(Digraph digraph) {
			this.visitedFromThisVetex = new HashSet<>();

			for (int i=0; i<digraph.V(); i++) {
				visitedFromThisVetex.clear();
				visitedFromThisVetex.add(i);
				dfs(digraph, i);
			}
		}
		private void dfs(Digraph digraphcp, int v) {

			for (int vv : digraphcp.adj(v)) {
				if (visitedFromThisVetex.contains(vv)) {
					hasCircle = true;
					break;
				} else {
					visitedFromThisVetex.add(vv);
				}
				dfs(digraphcp, vv);
				visitedFromThisVetex.remove(vv);
			}
			if (!digraphcp.adj(v).iterator().hasNext()) {
				// this vertex has no more adj
				if (root == -1) {
					root = v;
				} else {
					if (root != v) {
						isRooted = false;
					}
				}
			}
		}
		public boolean hasCircle() {
			return hasCircle;
		}
		public boolean isRooted() {
			return isRooted;
		}
	}

	// returns all WordNet nouns
	public Iterable<String> nouns() {
		return dict.keySet();
	}

	// is the word a WordNet noun?
	// log
	public boolean isNoun(String word) {
		if (null == word) {
			throw new java.lang.IllegalArgumentException();
		}

		return dict.containsKey(word);
	}

	// distance between nounA and nounB (defined below)
	// linear
	public int distance(String nounA, String nounB) {
		if (nounA == null || nounB == null) {
			throw new java.lang.IllegalArgumentException();
		}
		if (!isNoun(nounA) || !isNoun(nounB)) {
			throw new java.lang.IllegalArgumentException();
		}

		int distance = sap.length(dict.get(nounA).getId(), dict.get(nounB).getId());

		return distance;
	}

	// a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
	// in a shortest ancestral path (defined below)
	// linear
	public String sap(String nounA, String nounB) {
		if (nounA == null || nounB == null) {
			throw new java.lang.IllegalArgumentException();
		}

		if (!isNoun(nounA) || !isNoun(nounB)) {
			throw new java.lang.IllegalArgumentException();
		}

		int ancestor = sap.ancestor(dict.get(nounA).getId(), dict.get(nounB).getId());

		String ancestorStr=null;
		for (String key : dict.keySet()) {
			for (int synset : dict.get(key).getId()) {
				if (synset == ancestor) {
					ancestorStr = key;
					break;
				}
			}
		}

		return ancestorStr;
	}



	// do unit testing of this class
	public static void main(String[] args) {
		WordNet wn = new WordNet(args[0], args[1]);
		StdOut.println(wn.distance("worm", "bird"));
		StdOut.println(wn.distance("white_marlin", "mileage"));
		StdOut.println(wn.distance("Black_Plague", "black_marlin"));
		StdOut.println(wn.distance("American_water_spaniel", "histology"));
		StdOut.println(wn.distance("Brown_Swiss", "barrel_roll"));
	}
}