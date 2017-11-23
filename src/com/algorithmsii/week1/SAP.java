package com.algorithmsii.week1;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * Immutable data type
 *
 * */
public class SAP {
	private final int size;
	private final Digraph gCp;

	// constructor takes a digraph (not necessarily a DAG)
	// E+V
	public SAP(Digraph G) {
		// make a defensive copy
		gCp = new Digraph(G);
		size = gCp.V();

	}

	// length of shortest ancestral path between v and w; -1 if no such path
	// bfs
	// E+V
	public int length(int v, int w) {
		if (!isValid(v) || !isValid(w)) {
			throw new java.lang.IllegalArgumentException();
		}

		PathFinder pf = new PathFinder();
		findPath(pf, v, w);

		return pf.getShortestLength();
	}

	private void findPath(PathFinder pf, int v, int w) {
		Queue<Integer> qv = new Queue<>();
		qv.enqueue(v);
		Queue<Integer> qw = new Queue<>();
		qw.enqueue(w);
		boolean[] markedv = new boolean[size];
		boolean[] markedw = new boolean[size];
		markedv[v] = true;
		markedw[w] = true;
		int[] distv = new int[size];
		int[] distw = new int[size];
		int farv=-1;
		int farw=-1;
		int bestLength= Integer.MAX_VALUE;
		int commonAncestor = -1;
		while (!qv.isEmpty() || !qw.isEmpty()) {

			if (!qv.isEmpty()) {
				int tempv = qv.dequeue();
				// w has been there, found a common vertex
				if (markedw[tempv]) {
					int tempLength = distw[tempv] + distv[tempv];
					if (bestLength > tempLength) {
						bestLength = tempLength;
						commonAncestor = tempv;
					}
				}
				for (int nb : gCp.adj(tempv)) {
					if (!markedv[nb]) {
						qv.enqueue(nb);
						markedv[nb] = true;
						distv[nb] = distv[tempv] + 1;
						farv=Math.max(farv, distv[nb]);
					}
				}
			}

			if (!qw.isEmpty()) {
				int tempw = qw.dequeue();

				// v has been there, found a common vertex
				if (markedv[tempw]) {
					int tempLength = distw[tempw] + distv[tempw];
					if (bestLength > tempLength) {
						bestLength = tempLength;
						commonAncestor = tempw;
					}
				}

				for (int nb : gCp.adj(tempw)) {
					if (!markedw[nb]) {
						qw.enqueue(nb);
						markedw[nb] = true;
						distw[nb] = distw[tempw] + 1;
						farw = Math.max(farw, distw[nb]);
					}
				}
			}

			// in the case there is circle
			if (farv > bestLength && farw > bestLength) {
				// done
				break;
			}
		}

		if (bestLength == Integer.MAX_VALUE) {
			bestLength = -1;
		}
		pf.setCommonAncestor(commonAncestor);
		pf.setShortestLength(bestLength);

	}

	private final class PathFinder {
		private int shortestLength;
		public int getShortestLength() {
			return shortestLength;
		}
		public void setShortestLength(int shortestLength) {
			this.shortestLength = shortestLength;
		}
		public int getCommonAncestor() {
			return commonAncestor;
		}
		public void setCommonAncestor(int commonAncestor) {
			this.commonAncestor = commonAncestor;
		}
		private int commonAncestor;

	}

	// not between 0 and G.V() - 1.
	private boolean isValid(int v) {
		if (v<0 || v>size-1) {
			return false;
		} else {
			return true;
		}
	}

	// a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
	// E+V
	public int ancestor(int v, int w) {
		if (!isValid(v) || !isValid(w)) {
			throw new java.lang.IllegalArgumentException();
		}

		PathFinder pf = new PathFinder();
		findPath(pf, v, w);

		return pf.getCommonAncestor();
	}

	// length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
	// E+V
	public int length(Iterable<Integer> v, Iterable<Integer> w) {
		if (v == null || w == null) {
			throw new java.lang.IllegalArgumentException();
		}

		PathFinder pf = new PathFinder();
		findPath(pf, v, w);

		return pf.getShortestLength();
	}

	private void findPath(PathFinder pf, Iterable<Integer> v, Iterable<Integer> w) {
		int shortestLength = Integer.MAX_VALUE;
		int commonAncestor = -1;
		for (int vv : v) {
			if (!isValid(vv)) {
				throw new java.lang.IllegalArgumentException();
			}
			for (int ww : w) {
				if (!isValid(ww)) {
					throw new java.lang.IllegalArgumentException();
				}

				PathFinder tempPf = new PathFinder();
				findPath(tempPf, vv, ww);
				int tempShortest = tempPf.getShortestLength();
				int tempAncestor = tempPf.getCommonAncestor();
				if (tempShortest < shortestLength) {
					shortestLength = tempShortest;
					commonAncestor = tempAncestor;
				}
			}
		}
		if (shortestLength == Integer.MAX_VALUE) {
			shortestLength = -1;
		}
		pf.setCommonAncestor(commonAncestor);
		pf.setShortestLength(shortestLength);
	}

	// a common ancestor that participates in shortest ancestral path; -1 if no such path
	// E+V
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		if (v == null || w == null) {
			throw new java.lang.IllegalArgumentException();
		}

		PathFinder pf = new PathFinder();
		findPath(pf, v, w);

		return pf.getCommonAncestor();
	}

	// do unit testing of this class
   public static void main(String[] args) {
	   	In in = new In(args[0]);
	    Digraph G = new Digraph(in);
	    SAP sap = new SAP(G);
	    while (!StdIn.isEmpty()) {
	        int v = StdIn.readInt();
	        int w = StdIn.readInt();
	        int length   = sap.length(v, w);
	        int ancestor = sap.ancestor(v, w);
	        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
	    }
   }
}