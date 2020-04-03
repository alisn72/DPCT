package bicat.algorithms.clustering;

import bicat.gui.BicatGui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 **/

/**
 * Ideja je da graficki prikazem rezultat HCLa. - sort the items, according to
 * 'HCL_output' - the CROSS table - the Dendrogram (mozda)
 * 
 */
public class GraphicUtil {

	static int nrItems;

	static Vector clusters;

	static int[][] indexes;

	static int[][] crossTable;

	// ===========================================================================
	public GraphicUtil() {
	}

	// ===========================================================================
	public static LinkedList getClusters(int n, int nrChips) {

		LinkedList result = new LinkedList();
		int[] boundaries = new int[n + 1];
		boundaries[0] = -1;
		boundaries[n] = nrItems + clusters.size() - 1;
		int cnt = 1;

		if (true)
			System.out.println("clusters.size() = " + clusters.size()
					+ ", last boundaries = " + boundaries[n]);

		for (int k = 0; k < n - 1; k++) {

			String[] ch = (String[]) clusters.get(clusters.size() - 1 - k);
			if (true)
				System.out.println("Ch_1: " + ch[0] + ", Ch_2: " + ch[1]);

			int GRENZE = -1;

			// get C_idx ch[0]
			if (Pattern.matches("C.*", ch[0])) {
				String[] s = ch[0].split("C");
				int ch_idx1 = new Integer(s[1]).intValue();
				GRENZE = indexes[ch_idx1 - 1][1];
			} else if (Pattern.matches("C.*", ch[1])) {
				String[] s = ch[1].split("C");
				int ch_idx2 = new Integer(s[1]).intValue();
				GRENZE = indexes[ch_idx2 - 1][0] - 1;
			} else { // sind beide L's
				if (true)
					if ((indexes[clusters.size() - 1 - k][1] - 1)
							- indexes[clusters.size() - 1 - k][0] != 2) {
						System.out
								.println("(bicat.runMachine.clustering.GraphicUtil.getClusters) went wrong"
										+ "::: "
										+ indexes[clusters.size() - 1 - k][0]
										+ ", "
										+ indexes[clusters.size() - 1 - k][1]);
						// System.exit(333);
					}
				GRENZE = indexes[clusters.size() - 1 - k][0] + 1; // ??? - 1;
			}

			// BASE + 0 + 2*s1-1 - da ist der cross.
			System.out.println("Update cnt = " + cnt);
			boundaries[cnt] = GRENZE;
			cnt++;
		}

		int[] sorted_bs = sortArray(boundaries);

		if (true) {
			System.out.println("Limit values: " + sorted_bs[0] + ", "
					+ sorted_bs[sorted_bs.length - 1]);
			for (int i = 0; i < boundaries.length; i++)
				if (i > 0)
					System.out.println("Grenze (" + i + ") = (sorted) "
							+ sorted_bs[i] + ", ["
							+ (sorted_bs[i] - sorted_bs[i - 1] - 1) + "]");
		}

		for (int k = 0; k < n; k++) {
			Vector gs = new Vector();
			for (int j = sorted_bs[k] + 1; j <= sorted_bs[k + 1]; j++) {
				if (crossTable[j][clusters.size() - 1 - k] == 1)
					gs.add(new Integer(origIdx[j]));
				// OK???
			}
			int[] genes = fromIntVector(gs);
			int[] chips = new int[nrChips];
			for (int i = 0; i < nrChips; i++)
				chips[i] = i;
			bicat.biclustering.Bicluster bc = new bicat.biclustering.Bicluster(
					k + 1, genes, chips);
			result.add(bc);
		}
		return result;
	}

	static int[] fromIntVector(Vector vec) {
		int[] r = new int[vec.size()];
		for (int i = 0; i < vec.size(); i++)
			r[i] = ((Integer) vec.get(i)).intValue();
		return r;
	}

	static int[] sortArray(int[] arr) {

		Vector v = new Vector();
		for (int i = 0; i < arr.length; i++)
			v.add(new Integer(arr[i]));
		Collections.sort(v);

		int[] r = new int[arr.length];
		for (int i = 0; i < v.size(); i++)
			r[i] = ((Integer) v.get(i)).intValue();
		return r;
	}

	static int[] origIdx;

	// ===========================================================================
	/**
	 * read the output, and reconstruct the joins.
	 * 
	 * returns the "correct" order of clustered items. + string representation
	 * of the 'Cross Table'
	 * 
	 */
	static public int[] getCrossTable(String hclOut, int nrIt, int nrJoins) {

		// .... Initialize

		nrItems = nrIt;

		// int[][]
		crossTable = new int[nrItems + nrJoins][nrJoins];
		for (int i = 0; i < (nrItems + nrJoins); i++)
			for (int j = 0; j < nrJoins; j++)
				crossTable[i][j] = -1; // once a leaf found, everything above
		// it ist -1 (".").
		// also, all cells above a cross are 0 (" ").

		// Vector
		clusters = new Vector(); // of String[2] (el1, el2)
		int[] lParents = new int[nrItems];
		origIdx = new int[nrItems + nrJoins];
		for (int i = 0; i < (nrItems + nrJoins); i++)
			origIdx[i] = -1; // items nr'ed starting @ 1
		for (int i = 0; i < nrItems; i++)
			lParents[i] = 0;

		int[] clusterSizes = new int[nrJoins];
		for (int i = 0; i < nrJoins; i++) {
			clusterSizes[i] = 0;
		}

		// .... Get the HCL information

		// try {
		StringTokenizer st = new StringTokenizer(hclOut, " ");

		String t, el1, el2;
		int idx, t1, t2;

		while (st.hasMoreTokens()) {

			t = st.nextToken();
			idx = (new Integer(st.nextToken())).intValue();
			st.nextToken();

			el1 = st.nextToken();
			t1 = (new Integer(st.nextToken())).intValue();
			el2 = st.nextToken();
			t2 = (new Integer(st.nextToken())).intValue();

			// Read in & register the sizes:
			if (el1.equals("L") && el2.equals("L"))
				clusterSizes[idx - 1] = 2;
			else if (el1.equals("C") && el2.equals("L"))
				clusterSizes[idx - 1] = 1 + clusterSizes[t1 - 1];
			else if (el1.equals("L") && el2.equals("C"))
				clusterSizes[idx - 1] = 1 + clusterSizes[t2 - 1];
			else
				// if beide "C"
				clusterSizes[idx - 1] = clusterSizes[t1 - 1]
						+ clusterSizes[t2 - 1];

			// ... & get the leaf names
			String[] children = new String[2];
			children[0] = el1 + new Integer(t1).toString();
			children[1] = el2 + new Integer(t2).toString();

			if (el1.equals("L"))
				lParents[t1] = idx;
			if (el2.equals("L"))
				lParents[t2] = idx;

			clusters.add(children); // at index 'idx-1' is the representation (I
			// loose the distance infos)

			if (BicatGui.debug)
				System.out.println("Read: " + t + " " + idx + " = " + el1 + " "
						+ t1 + " + " + el2 + " " + t2 + " [" + clusters.size()
						+ "]");
		}
		// } catch(Exception e) { System.err.println(e.toString());
		// System.exit(222); }

		// ....
		// .... Fill in the crossTable

		// int[][]
		indexes = new int[nrJoins][2]; // (start idx, end idx)
		for (int i = 0; i < nrJoins; i++) {
			indexes[i][0] = 0;
			indexes[i][1] = 0;
		}
		indexes[nrJoins - 1][1] = nrJoins + nrItems;

		int BASE;

		int s1;
		int s2;
		int idx1 = -1;
		int idx2 = -1;
		boolean leaf_1 = false, leaf_2 = false;

		for (int i = (clusters.size() - 1); i >= 0; i--) {

			String[] children = (String[]) clusters.get(i);

			// if(true) System.out.println("Children are " +children[0]+",
			// "+children[1]);

			if (Pattern.matches("C.*", children[0])) {
				String[] tokens = children[0].split("C");
				idx1 = (new Integer(tokens[1])).intValue();
				s1 = clusterSizes[idx1 - 1];
				leaf_1 = false;
			} else {
				String[] tokens = children[0].split("L");
				idx1 = (new Integer(tokens[1])).intValue();
				s1 = 1;
				leaf_1 = true;
			}

			if (Pattern.matches("C.*", children[1])) {
				String[] tokens = children[1].split("C");
				idx2 = (new Integer(tokens[1])).intValue();
				s2 = clusterSizes[idx2 - 1];
				leaf_2 = false;
			} else {
				String[] tokens = children[1].split("L");
				idx2 = (new Integer(tokens[1])).intValue();
				s2 = 1;
				leaf_2 = true;
			}

			BASE = indexes[i][0];

			if (!leaf_1) {
				indexes[idx1 - 1][0] = BASE;
				indexes[idx1 - 1][1] = (BASE + 2 * s1 - 1);
			}

			// BASE + 0 + 2*s1-1 - da ist der cross.

			if (!leaf_2) {
				indexes[idx2 - 1][0] = BASE + 2 * s1;
				indexes[idx2 - 1][1] = (BASE + 2 * s1 + 2 * s2 - 1);
			}

			// get the right values in the right places
			for (int k = BASE; k < BASE + 2 * s1 - 1; k++)
				crossTable[k][i] = 1;
			if (s1 == 1) {
				int k = BASE;
				for (int p = 0; p < i; p++)
					crossTable[k][p] = -1;
				for (int p = i + 1; p < nrJoins; p++)
					crossTable[k][p] = 1;
				origIdx[k] = idx1;
			}

//			// 1.4.2006 debug out of bounds Exception.
//			int baseIndex = BASE + 2 * s1 - 1;
//			System.out.println("BASE + 2 * s1 - 1 = " + baseIndex);
//			//
//			crossTable[BASE + 2 * s1 - 1][i] = 2; // the CROSS
			for (int p = 0; p < i; p++)
				crossTable[BASE + 2 * s1 - 1][p] = 0;
			for (int p = i + 1; p < nrJoins; p++) {
				crossTable[BASE + 2 * s1 - 1][p] = 2;
			}

			for (int k = BASE + 2 * s1; k < BASE + 2 * s1 + 2 * s2 - 1; k++) {
				if (k <= crossTable.length && i <= crossTable.length) {
					crossTable[k][i] = 1;
				}
			}
			// System.out.println("crossTable.length: " + crossTable.length);
			if (s2 == 1) {
				int k = BASE + 2 * s1;
				for (int p = 0; p < i; p++)
					crossTable[k][p] = -1;
				for (int p = i + 1; p < nrJoins; p++)
					crossTable[k][p] = 1;
				origIdx[k] = idx2;
			}

		} // end all joins (i)

		// .... String representation
		/*
		 * StringBuffer sbb = new StringBuffer();
		 * 
		 * for(int i = 0; i < (nrItems+nrJoins); i++) {
		 * 
		 * for(int j = 0; j < nrJoins; j++) {
		 * 
		 * if (crossTable[i][j] == -1) { System.out.print("."); sbb.append("0
		 * "); } else if (crossTable[i][j] == 0) { System.out.print(" ");
		 * sbb.append("0 "); } else if (crossTable[i][j] == 1) {
		 * System.out.print("x"); sbb.append("15 "); } else if (crossTable[i][j] ==
		 * 2) { System.out.print("X"); sbb.append("50 "); } else {
		 * System.out.println("Not foreseen! (print crossTable) \n");
		 * System.exit(222); } }
		 * 
		 * System.out.println(); sbb.append("\n"); }
		 * 
		 * try { FileWriter fw = new FileWriter("OUT_HCL.txt");
		 * fw.write(sbb.toString()); fw.close(); } catch(Exception e) {
		 * System.err.println(e.toString()); System.exit(222); }
		 */

		int[] result = new int[nrItems];
		int c = 0;
		for (int i = 0; i < origIdx.length; i++)
			if (origIdx[i] != -1) {
				result[c] = origIdx[i];
				c++;
				System.out.print(" " + origIdx[i]);
			}
		return result;
	}

}