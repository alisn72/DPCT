package bicat.util;

import bicat.biclustering.Bicluster;
import bicat.gui.BicatGui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * <p>
 * Title: BicAT Tool
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * @author Amela Prelic
 * @version 1.0
 * 
 */

public class AnalysisUtil {

	// ===========================================================================
	public AnalysisUtil() {
	}

	// ===========================================================================
	// GENE PAIR ANALYSIS ("gpa")....

	// ===========================================================================
	public static HashMap gpaByCoocurrence(int gThr, LinkedList list) {

		HashMap GPs_scores = new HashMap();

		for (int i = 0; i < list.size(); i++) {
			int[] genes = ((Bicluster) (list.get(i))).getGenes();
			for (int p = 0; p < genes.length - 1; p++)
				for (int q = p + 1; q < genes.length; q++) {
					int min = genes[p];
					int max = genes[q];
					if (max < min) {
						int tmp = max;
						max = min;
						min = tmp;
					}
					String skey = ((new Integer(min)).toString()) + "\t"
							+ ((new Integer(max)).toString()); // ???
					// System.out.println(skey+"?");

					if (GPs_scores.containsKey(skey)) {
						Integer score = (Integer) ((Object[]) GPs_scores
								.get(skey))[0];
						int is = score.intValue();
						is++;
						Object[] scores = new Object[2];
						scores[0] = new Integer(is);
						GPs_scores.put(skey, scores);
						// GPs_scores.put(skey,new Integer(is));
					} else {
						Object[] scores = new Object[2];
						scores[0] = new Integer(1);
						GPs_scores.put(skey, scores);
						// GPs_scores.put(skey,new Integer(1));
					}
				}
		}

		// remove the distances smaller than the given 'distanceThreshold'
		Set ks = GPs_scores.keySet();
		Object[] ka = ks.toArray();
		for (int i = 0; i < ka.length; i++) {
			// int d =
			// ((Integer)((Object[])(GPs_scores.get(ka[i]))[0]).intValue());
			int d = ((Integer) ((Object[]) GPs_scores.get(ka[i]))[0])
					.intValue();
			if (d < gThr)
				GPs_scores.remove(ka[i]);
		}

		// Compute the graph distances (between all the pairs), in the remaining
		// hash structure!
		int[][] FW = computeAllPairsShortestPaths(GPs_scores);
		GPs_scores = updateGPAResults(GPs_scores, FW);

		// sort the GP distance list!
		// ..

		return GPs_scores;
	}

	static HashMap name_2_id = null;

	static HashMap id_2_name = null;

	// ===========================================================================
	private static int[][] computeAllPairsShortestPaths(HashMap gps) {

		name_2_id = new HashMap();
		id_2_name = new HashMap();
		int idx = 0;

		Set ks = gps.keySet();
		Object[] ka = ks.toArray();
		for (int i = 0; i < ka.length; i++) {
			String[] sp = ((String) ka[i]).split("\t");
			if (name_2_id.containsKey(sp[0]) == false) {
				name_2_id.put(sp[0], new Integer(idx));
				id_2_name.put(new Integer(idx), sp[0]);
				idx++;
			}
			if (name_2_id.containsKey(sp[1]) == false) {
				name_2_id.put(sp[1], new Integer(idx));
				id_2_name.put(new Integer(idx), sp[1]);
				idx++;
			}
		}

		// initialize the adjacency matrix:
		int[][] adj_matrix = new int[name_2_id.size()][name_2_id.size()];
		for (int i = 0; i < ka.length; i++) {
			String[] sp = ((String) ka[i]).split("\t");
			int idx1 = ((Integer) name_2_id.get(sp[0])).intValue();
			int idx2 = ((Integer) name_2_id.get(sp[1])).intValue();
			adj_matrix[idx1][idx2] = 1;
			adj_matrix[idx2][idx1] = 1;
		}

		floyd.AllPairsShortestPath apsp = new floyd.AllPairsShortestPath(
				adj_matrix);
		return apsp.getFWResult();
	}

	// ===========================================================================
	private static HashMap updateGPAResults(HashMap gps, int[][] distances) {

		Set ks = gps.keySet();
		Object[] ka = ks.toArray();
		for (int i = 0; i < ka.length; i++) {
			String[] sp = ((String) ka[i]).split("\t");

			int idx1 = ((Integer) name_2_id.get(sp[0])).intValue();
			int idx2 = ((Integer) name_2_id.get(sp[1])).intValue();

			gps.put(ka[i], new Object[] { ((Object[]) gps.get(ka[i]))[0],
					new Integer(distances[idx1][idx2]) });
		}

		return gps;
	}

	// ===========================================================================
	public static HashMap gpaByCommonChips(int gThr, LinkedList list) {
		// TO DO!

		HashMap GPs_scores = new HashMap();

		for (int i = 0; i < list.size(); i++) {
			int[] genes = ((Bicluster) (list.get(i))).getGenes();
			int cs = ((Bicluster) (list.get(i))).getChipsSize();
			for (int p = 0; p < genes.length - 1; p++)
				for (int q = p + 1; q < genes.length; q++) {
					int min = genes[p];
					int max = genes[q];
					if (max < min) {
						int tmp = max;
						max = min;
						min = tmp;
					}
					String skey = ((new Integer(min)).toString()) + "\t"
							+ ((new Integer(max)).toString()); // ???
					// System.out.println(skey+"?");

					if (GPs_scores.containsKey(skey)) {
						Integer score = (Integer) ((Object[]) GPs_scores
								.get(skey))[0];
						// Integer score = (Integer)GPs_scores.get(skey);
						int is = score.intValue();
						if (is < cs) {
							Object[] scores = new Object[2];
							scores[0] = new Integer(is);
							GPs_scores.put(skey, scores);
						}
						// GPs_scores.put(skey,new Integer(cs));
					} else {
						Object[] scores = new Object[2];
						scores[0] = new Integer(cs);
						GPs_scores.put(skey, scores);
						// GPs_scores.put(skey,new Integer(cs));
					}
				}
		}

		// remove the distances smaller than the given 'distanceThreshold'
		Set ks = GPs_scores.keySet();
		Object[] ka = ks.toArray();
		for (int i = 0; i < ka.length; i++) {
			int d = ((Integer) ((Object[]) GPs_scores.get(ka[i]))[0])
					.intValue();
			// int d = ((Integer)(GPs_scores.get(ka[i]))).intValue();
			if (d < gThr)
				GPs_scores.remove(ka[i]);
		}

		// Compute the graph distances (between all the pairs), in the remaining
		// hash structure!
		int[][] FW = computeAllPairsShortestPaths(GPs_scores);
		GPs_scores = updateGPAResults(GPs_scores, FW);

		// .should sort it somehow!>>.
		return GPs_scores;
	}

	// ===========================================================================
	public static String toString_exportToBioLayout(HashMap GPs_scores) {

		StringBuffer sb = new StringBuffer();

		Set ks = GPs_scores.keySet();
		Object[] ka = ks.toArray();
		for (int i = 0; i < ka.length; i++) {
			String key = (String) ka[i];
			String[] gs = key.split("\t");
			// sb.append(gs[0]+"\t"+gs[1]+"\t"+((Integer)GPs_scores.get(key))+"\n");
			/*
			 * sb.append(BicaGUI.pre.getGeneNameAtPosition((new
			 * Integer(gs[0])).intValue())+"\t"+
			 * BicaGUI.pre.getGeneNameAtPosition((new
			 * Integer(gs[1])).intValue())+"\t"+
			 * ((Integer)GPs_scores.get(key))+"\n");
			 */
			sb.append(BicatGui.currentDataset.getGeneName((new Integer(gs[0]))
					.intValue())
					+ "\t"
					+
					// pre.getGeneNameAtPosition((new
					// Integer(gs[0])).intValue())+"\t"+
					BicatGui.currentDataset.getGeneName((new Integer(gs[1]))
							.intValue())
					+ "\t"
					+
					// BicatGui.pre.getGeneNameAtPosition((new
					// Integer(gs[1])).intValue())+"\t"+
					((Integer) ((Object[]) GPs_scores.get(key))[0])
					+ "\t"
					+ ((Integer) ((Object[]) GPs_scores.get(key))[1]) + "\n");
		}

		return sb.toString();
	}

}
