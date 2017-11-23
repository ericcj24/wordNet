package com.algorithmsii.week1;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
	private final WordNet wn;

	// constructor takes a WordNet object
	public Outcast(WordNet wordnet) {
		wn = wordnet;
	}

	// given an array of WordNet nouns, return an outcast
	// which noun is the least related to the others
	public String outcast(String[] nouns) {
		int largestDist = Integer.MIN_VALUE;
		String outcast = null;
		for (String nounA : nouns) {
			int tempLargestDist = 0;
			for (String nounB : nouns) {
				tempLargestDist += wn.distance(nounA, nounB);
			}

			if (tempLargestDist > largestDist) {
				largestDist = tempLargestDist;
				outcast = nounA;

			}
		}
		return outcast;
	}

	// see test client below
	public static void main(String[] args) {
		WordNet wordnet = new WordNet(args[0], args[1]);
	    Outcast outcast = new Outcast(wordnet);
	    for (int t = 2; t < args.length; t++) {
	        In in = new In(args[t]);
	        String[] nouns = in.readAllStrings();
	        StdOut.println(args[t] + ": " + outcast.outcast(nouns));
	    }
	}
}