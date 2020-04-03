package bicat.algorithms.clustering;

import bicat.gui.BicatGui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Vector;

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

public class HCL {

	final public static int SINGLE_LINKAGE = 0;

	final public static int COMPLETE_LINKAGE = 1;

	final public static int AVERAGE_LINKAGE = 2;

	final public static int AGGLOMERATIVE = 0;

	final public static int DIVISIVE = 1; // NOT implemented!

	final public static int EUCLIDEAN_DISTANCE = 0;

	final public static int PEARSON_CORRELATION = 1;

	final public static int MANHATTAN_DISTANCE = 2;

	final public static int MINKOWSKI_DISTANCE = 3; // power 2

	final public static int COSINE_DISTANCE = 4;

	static int linkage_method; // {0: single, 1: complete, 2: avg}

	int distance_metric; // {0: euclidean, 1: Pearson correlation , 2:

	// hamming distance (?)}

	static int modus; // agglomerative, OR divisive

	static int N; // number of cluster to visualize / bound

	static boolean DEBUG = false;

	// ===========================================================================
	public HCL() {
	}

	BicatGui owner;

	public HCL(int linkage, int distMetr, int mode) {
		linkage_method = linkage;
		distance_metric = distMetr;
		modus = mode;
	}

	public HCL(int nr_Cs, int linkage, int dist, int mode) {
		N = nr_Cs;
		linkage_method = linkage;
		distance_metric = dist;
		modus = mode;
	}

	public HCL(BicatGui o, int linkage, int dist, int mode) {
		owner = o;
		linkage_method = linkage;
		distance_metric = dist;
		modus = mode;
	}

	public void runHCL(float[][] data, int distMetr) {
		if (modus == AGGLOMERATIVE)
			runAgglomerative(data, distMetr);
		else if (modus == DIVISIVE) // not implemented yet
			runDivisive(data);
	}

	static double curr_distance;

	static public String HCL_output;

	// ===========================================================================
	/**
	 * First dim (data.length) is the number of items to cluster. Dementspreched
	 * is the second dim (data[0].length) number of attributes.
	 * 
	 * Add the labeling management. + Outputting (joins)
	 * 
	 * OUTPUT format: C.nr_joins.dist\n L x1 L x2
	 * 
	 * C.nr_joins.dist\n L x3 C N1 ...
	 * 
	 * @param distMetr
	 * 
	 */
	void runAgglomerative(float[][] data, int distMetr) {

		StringBuffer sb = new StringBuffer(); // at the end, write it OUT
		distance_metric = distMetr;
		int nr_genes = data.length;
		int nr_joins = 0; // numbering of clusters begins @ 1
		int[] back_translation = new int[nr_genes]; // current L idx -> original
		// L idx
		// int[] nr_negations = new int[nr_items]; // contains the nr of times
		// the el has been decremented
		Vector clusters = new Vector(); // contains extended view of the
		// clusters, as they are created

		if (DEBUG)
			System.out.println("nr_items = " + nr_genes);

		// INIT: compute the distance matrix, and create the singletons:
		double[][] distance_matrix = new double[nr_genes][nr_genes];
		if (BicatGui.debug)
			System.out.println("The distance metric is: " + distance_metric);

		for (int i = 0; i < nr_genes; i++) {
			distance_matrix[i][i] = (double) 0.0;
			for (int j = i + 1; j < nr_genes; j++) {

				switch (distance_metric) {

				case EUCLIDEAN_DISTANCE:
					distance_matrix[i][j] = Util.computeEuclideanDistance(
							data[i], data[j]);
					break;

				case PEARSON_CORRELATION:
					distance_matrix[i][j] = Util
							.computePearsonCorrelationDistance(data[i], data[j]);
					break;

				case MANHATTAN_DISTANCE:
					distance_matrix[i][j] = Util.computeManhattanDistance(
							data[i], data[j]);
					break;

				case MINKOWSKI_DISTANCE:
					distance_matrix[i][j] = Util.computeMinkowskiDistance(
							data[i], data[j]);
					break;

				case COSINE_DISTANCE:
					distance_matrix[i][j] = Util.computeCosineDistance(data[i],
							data[j]);
					break;

				default:
					break; /* not foreseen */

				}

				distance_matrix[j][i] = distance_matrix[i][j];
				//6.1.2006
				if(BicatGui.debug)System.out.println("i: " + i + " j: " + j
						+ " distance Matrix[i][j]: " + distance_matrix[i][j]);

			}
		}

		cluster_idxs = new Vector();
		for (int i = 0; i < nr_genes; i++) {
			int[] cl = new int[1];
			cl[0] = i;
			cluster_idxs.add(cl);
		}

		Vector cluster_dists = new Vector();
		for (int i = 0; i < nr_genes; i++) {
			Vector distance_vector = new Vector();
			for (int j = 0; j < nr_genes; j++)
				distance_vector.add(new Double(distance_matrix[i][j]));
			cluster_dists.add(distance_vector);
		}

		for (int i = 0; i < nr_genes; i++) {
			back_translation[i] = i; // '-1' wert fuer ungueltige labels ...
			// nr_negations[i] = 0; // original values initially
		}

		// ------------------------------
		// N statt 1
		while (cluster_idxs.size() > N) {

			// if(DEBUG) if(cluster_dists.size() != cluster_idxs.size())
			// System.out.println("Something's VERY wrong!");

			// 2. merge (into) clusters
			int[] idxs = findMinDistancePair(cluster_dists); // 2 (i,j)

			// if(DEBUG) System.out.println("cl_dists size = " +
			// cluster_dists.size());
			// if(DEBUG) System.out.println("cl_idxs size = " +
			// cluster_idxs.size());

			int[] cl_1 = (int[]) cluster_idxs.get(idxs[0]);
			int[] cl_2 = (int[]) cluster_idxs.get(idxs[1]);

			// if(DEBUG) System.out.println("\n1: Merging clusters "+idxs[0]+",
			// "+idxs[1]);
			// if(DEBUG) System.out.println("2: Merging clusters
			// "+back_translation[idxs[0]]+", "+back_translation[idxs[1]]);
			if (DEBUG) {
				if (idxs[0] >= idxs[1]) {
					System.out.println("ATTENTION: idxs[0] >= idxs[1] ");
				}// System.exit(333);
			}
			// --- Management Ops:
			nr_joins++;
			// sb.append("C."+nr_joins+"."+curr_distance+"\n");
			sb.append("C " + nr_joins + " " + curr_distance + " ");
			if (((int[]) cluster_idxs.get(idxs[0])).length == 1) // sb.append("L
				// "+
				// back_translation[idxs[0]]
				// +"\n");
				sb.append("L " + back_translation[idxs[0]] + " ");
			else {
				int[] cl_0 = ((int[]) cluster_idxs.get(idxs[0]));
				int N = findClusterIdx(cl_0, clusters);
				sb.append("C " + N + " "); // \n");
			}
			if (((int[]) cluster_idxs.get(idxs[1])).length == 1)
				sb.append("L " + back_translation[idxs[1]] + " "); // \n\n");
			else {
				int[] cl_0 = ((int[]) cluster_idxs.get(idxs[1]));
				int N = findClusterIdx(cl_0, clusters);
				sb.append("C " + N + " "); // \n\n");
			}

			Vector new_cl = new Vector();
			int[] new_arr = appendArrays(cl_1, cl_2);
			for (int i = 0; i < new_arr.length; i++)
				new_cl.add(new Integer(new_arr[i]));
			clusters.add(new_cl); // its idx is -1 of the ""official"" idx
			// --- Ende Management Ops.

			cluster_idxs.remove(idxs[1]);
			cluster_idxs.set(idxs[0], appendArrays(cl_1, cl_2));

			// mngmt op:
			for (int j = idxs[1]; j < cluster_idxs.size(); j++)
				back_translation[j] = back_translation[j + 1];
			for (int j = cluster_idxs.size(); j < nr_genes; j++)
				back_translation[j] = -1;
			// ende mngmt op.

			// 3. recompute the distances
			for (int j = 0; j < cluster_idxs.size(); j++) {
				switch (linkage_method) {

				case SINGLE_LINKAGE:
					((Vector) cluster_dists.get(idxs[0])).set(j, min(
							(Double) ((Vector) cluster_dists.get(idxs[0]))
									.get(j), (Double) ((Vector) cluster_dists
									.get(idxs[1])).get(j)));
					break;

				case COMPLETE_LINKAGE:
					((Vector) cluster_dists.get(idxs[0])).set(j, max(
							(Double) ((Vector) cluster_dists.get(idxs[0]))
									.get(j), (Double) ((Vector) cluster_dists
									.get(idxs[1])).get(j)));
					break;

				case AVERAGE_LINKAGE:
					((Vector) cluster_dists.get(idxs[0])).set(j, avg(
							(Double) ((Vector) cluster_dists.get(idxs[0]))
									.get(j), (Double) ((Vector) cluster_dists
									.get(idxs[1])).get(j), cl_1.length,
							cl_2.length));
					break;

				default:
					break; /* not foreseen! */
				}
			}
			for (int i = 0; i <= cluster_idxs.size(); i++)
				((Vector) cluster_dists.get(i)).remove(idxs[1]);

			cluster_dists.remove(idxs[1]);

			for (int i = 0; i < cluster_idxs.size(); i++) {
				if (i != idxs[0])
					((Vector) cluster_dists.get(i)).set(idxs[0],
							((Vector) cluster_dists.get(idxs[0])).get(i));
			}

			// repeat 2 and 3 until all items are in a single cluster
		}

		HCL_output = sb.toString();

		if (BicatGui.debug)
			System.out.println("nr_items, nr_joins: " + nr_genes + ", "
					+ nr_joins);
//		int[] genes = GraphicUtil.getCrossTable(HCL_output, nr_genes, nr_joins);
		int[] chipsTemplate = new int[owner.pre.getWorkingChipCount()];
		for (int i = 0; i < chipsTemplate.length; i++)
			chipsTemplate[i] = i;

		// ....
		quasi_bi_clusters = new LinkedList();
		for (int i = 0; i < cluster_idxs.size(); i++) {
			
			int[] genes = (int[]) cluster_idxs.get(i);
			int[] chips = (int[]) chipsTemplate.clone();
			bicat.biclustering.Bicluster bc = new bicat.biclustering.Bicluster(
					i + 1, genes, chips);
			quasi_bi_clusters.add(bc);
		}
		
//		quasi_bi_clusters = GraphicUtil.getClusters(N, owner.pre
//				.getWorkingChipCount());

		// BIG TEST: (ok) ... OVO TREBAM PROMIJENITI! TO DO (09.03.2005)
		// !!!!CHECK THIS!!!!

        // i commented it
//		owner.setData(owner.pre.getPreprocessedData());
//		owner.pp.setData(owner.rawData);
//		int[] temp = (int[]) cluster_idxs.get(0);
//		bicat.biclustering.Bicluster selection = new bicat.biclustering.Bicluster(
//				0, (int[])temp.clone(), chipsTemplate);
//		owner.currentBiclusterSelection = owner.pp
//				.setTranslationTable(selection);
//		// refreshGraphicPanel();
//		owner.matrixScrollPane.repaint();
//		owner.readjustPictureSize();
//		owner.pp.repaint();

	}

	LinkedList quasi_bi_clusters;
    Vector cluster_idxs;

	// =-=-
	public Vector getClusters() {
        //return quasi_bi_clusters;
        return  cluster_idxs;
    }

	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=--------

	Double min(Double v1, Double v2) {
		if (v1.doubleValue() < v2.doubleValue())
			return v1;
		else
			return v2;
	}

	Double max(Double v1, Double v2) {
		if (v1.doubleValue() < v2.doubleValue())
			return v2;
		else
			return v1;
	}

	Double avg(Double v1, Double v2, int s1, int s2) {
		return new Double((v1.doubleValue() * (double) s1 + v2.doubleValue()
				* (double) s2)
				/ (double) (s1 + s2));
	}

	static int[] findMinDistancePair(Vector dists) {

		double minimum = Double.MAX_VALUE;
		int[] pair = new int[2];
		// 4.1.2006 pair[] initialisieren
		pair[0] = 0;
		pair[1] = 1;
		for (int i = 0; i < dists.size(); i++) {
			Vector inner_dists = (Vector) dists.get(i);
			for (int j = i + 1; j < inner_dists.size(); j++) {
				double value = ((Double) inner_dists.get(j)).doubleValue();
				//value >= 0.0 && raus
				if (value < minimum) {
					pair[0] = i;
					pair[1] = j;
					minimum = value;
				}
			}
		}
		// if(DEBUG) System.out.println("Minimum Distance Pair: ("+pair[0]+",
		// "+pair[1]+"), dist = "+minimum);
		curr_distance = minimum;
		return pair;
	}

	int[] appendArrays(int[] arr1, int[] arr2) {
		int[] result = new int[arr1.length + arr2.length];
		for (int i = 0; i < arr1.length; i++)
			result[i] = arr1[i];
		for (int i = 0; i < arr2.length; i++)
			result[arr1.length + i] = arr2[i];
		return result;
	}

	int findClusterIdx(int[] cl, Vector clusters) {
		// int idx = -1;
		Vector cluster = new Vector();
		for (int i = 0; i < cl.length; i++)
			cluster.add(new Integer(cl[i]));
		Collections.sort(cluster);

		for (int i = 0; i < clusters.size(); i++) {
			Vector existing = (Vector) clusters.get(i);
			Collections.sort(existing);
			if (existing.equals(cluster))
				return (i + 1);
		}
		return -1;
	}

	// ===========================================================================
	void runDivisive(float[][] data) {

	}

	// ===========================================================================
	// ===========================================================================
	// ===========================================================================
	/**
	 * First dim (data.length) is the number of items to cluster. Dementspreched
	 * is the second dim (data[0].length) number of attributes.
	 * 
	 */
	void runAgglomerative_ORIG(float[][] data) {

		int nr_items = data.length;
		if (DEBUG)
			System.out.println("nr_items = " + nr_items);

		// INIT: compute the distance matrix, and create the singletons:
		double[][] distance_matrix = new double[nr_items][nr_items];
		for (int i = 0; i < nr_items; i++) {
			distance_matrix[i][i] = (double) 0.0;
			for (int j = i + 1; j < nr_items; j++) {
				if (distance_metric == EUCLIDEAN_DISTANCE) {
					distance_matrix[i][j] = Util.computeEuclideanDistance(
							data[i], data[j]);
					distance_matrix[j][i] = distance_matrix[i][j];
				}
				/*
				 * else if(distance_metric == PEARSON_CORRELATION) { } else
				 * if(distance_metric == HAMMING_DISTANCE) { } else { /* not
				 * foreseen! ../ }
				 */
			}
		}
		if (DEBUG)
			System.out.println("(2) nr_items = " + nr_items);

		Vector cluster_idxs = new Vector();
		for (int i = 0; i < nr_items; i++) {
			int[] cl = new int[1];
			cl[0] = i;
			cluster_idxs.add(cl);
		}

		Vector cluster_dists = new Vector();
		for (int i = 0; i < nr_items; i++) {
			Vector distance_vector = new Vector();
			for (int j = 0; j < nr_items; j++)
				distance_vector.add(new Double(distance_matrix[i][j]));
			cluster_dists.add(distance_vector);
		}

		while (cluster_idxs.size() > 1) {

			// 2. merge (into) clusters
			int[] idxs = findMinDistancePair(cluster_dists); // 2 (i,j)

			int[] cl_1 = (int[]) cluster_idxs.get(idxs[0]);
			int[] cl_2 = (int[]) cluster_idxs.get(idxs[1]);

			if (DEBUG)
				System.out.println("Merging clusters " + idxs[0] + ", "
						+ idxs[1]);
			if (idxs[0] > idxs[1])
				System.out.println("ACHTUNG: 0 veci od 1!!!");

			cluster_idxs.remove(idxs[1]);
			cluster_idxs.set(idxs[0], appendArrays(cl_1, cl_2));

			for (int i = 0; i < cluster_idxs.size(); i++)
				((Vector) cluster_dists.get(i)).remove(idxs[1]);

			// 3. recompute the distances
			for (int j = 0; j < cluster_idxs.size(); j++) {
				if (linkage_method == SINGLE_LINKAGE)
					((Vector) cluster_dists.get(idxs[0])).set(j, min(
							(Double) ((Vector) cluster_dists.get(idxs[0]))
									.get(j), (Double) ((Vector) cluster_dists
									.get(idxs[1])).get(j)));
				else if (linkage_method == COMPLETE_LINKAGE)
					((Vector) cluster_dists.get(idxs[0])).set(j, max(
							(Double) ((Vector) cluster_dists.get(idxs[0]))
									.get(j), (Double) ((Vector) cluster_dists
									.get(idxs[1])).get(j)));
				/*
				 * else if(linkage_method == AVERAGE_LINKAGE) {} else { /* not
				 * foreseen! ../ }
				 */
			}

			cluster_dists.remove(idxs[1]);

			for (int i = 0; i < cluster_idxs.size(); i++) {
				// if(i != idxs[0])
				// ((Vector)cluster_dists.get(i)).remove(idxs[1]);
				if (i != idxs[0])
					((Vector) cluster_dists.get(i)).set(idxs[0],
							((Vector) cluster_dists.get(idxs[0])).get(i));
			}

			// repeat 2 and 3 until all items are in a single cluster
		}
	}

}
