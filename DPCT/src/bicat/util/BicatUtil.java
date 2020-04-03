package bicat.util;

import bicat.biclustering.Dataset;
import bicat.gui.BicatGui;

import javax.swing.tree.TreePath;
import java.util.LinkedList;
import java.util.regex.Pattern;

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

public class BicatUtil {

	public BicatUtil() {
	}

	// ===========================================================================
	public static boolean isBC(String id) {
		return Pattern.matches("ID: .*", id);
	}

	public static boolean isCollectionOfLists(String label) {
		return (Pattern.matches("Bicluster results", label)
				|| Pattern.matches("Cluster results", label)
				|| Pattern.matches("Filter results", label)
				|| Pattern.matches("Search results", label) || Pattern.matches(
				"Analysis results", label));
	}

	public static boolean isDataset(String label) {
		/*
		 * if(Pattern.matches("DataSet .*", label)) System.out.println("THE
		 * DATASET IT IS! (BicatUtil) === ."+label+".");
		 */
		return Pattern.matches("DataSet .*", label);
	}

	public static boolean isListOfBCs(String label) {
		return (Pattern.matches("All biclusters .*", label)
				|| Pattern.matches("All clusters .*", label)
				|| Pattern.matches("Search result of .*", label)
				|| Pattern.matches("Filter result of .*", label) || Pattern
				.matches("Analysis result of .*", label));
	}

	// ===========================================================================
	// recognize the selected node, return the list of (bi)clusters associated
	// ...
	public static LinkedList getListOfBiclusters(TreePath treePath,
			LinkedList datasetList) {

		int dataset = getDataset(treePath.getPathComponent(1).toString())[0];

		// which list of BCs (bcs, filter, search)?
		int[] listAndIdx = getListAndIdxTree(treePath.getPathComponent(3)
				.toString());

		if (BicatGui.debug) {
			System.out.println("Debug: "
					+ treePath.getPathComponent(3).toString());
			System.out.println("Debug: dataset = " + dataset + ", l_0 = "
					+ listAndIdx[0] + // what list selected (L, F, S) ... oder
					// C (clusters, as of 16.03.2005)
					", l_1 = " + listAndIdx[1] + // what was the original
					// list (if any) (L, F,
					// S)_orig
					", l_2 = " + listAndIdx[2] + // idx original list
					", l_3 = " + listAndIdx[3]); // idx list selected
		}

		Dataset br = ((Dataset) datasetList.get(dataset));
		return br.getBCList(listAndIdx[0], listAndIdx[3]);
	}

	// ===========================================================================
	public static boolean isAnalysisNode(String label) {
		return Pattern.matches("Analysis result of.*", label);
	}

	// ===========================================================================
	// int idx = BicaToolUtil.getAnalysisNodeIdx((String)maybeBC);
	public static int getAnalysisNodeIdx(String label) {
		String[] sp = label.split("\\0056");
		int idx = (new Integer(sp[1])).intValue();
		System.out.println("idx A.x= " + idx);
		return idx;
	}

	// ===========================================================================
	public static int[] getDataset(String label) {
		// Dataset X

		Pattern.matches("DataSet .*", label);
		int[] result = new int[1];
		String[] sp = label.split(" ");
		result[0] = (new Integer(sp[1])).intValue();

		return result;
	}

	public static int getDatasetID(String label) {
		// Dataset X
		Pattern.matches("DataSet .*", label);
		String[] sp = label.split(" ");
		return (new Integer(sp[1])).intValue();
	}

	public boolean is_in_array(int[] arr, int el) {
		for (int i = 0; i < arr.length; i++)
			if (arr[i] == el)
				return true;
		return false;
	}

	// ===========================================================================
	public static int[] getListAndIdx(String label) {

		int[] result = new int[3];
		result[0] = -1;
		result[1] = -1;
		result[2] = -1;
		// Possibilities are: All biclusters, L.*
		// Search results of L/F/S*, S.*
		// Filter results of L/F/S*, F.*

		// old, bis 16.03'05: boolean bc_list = Pattern.matches("D\\d* All
		// biclusters, L.*",label);
		boolean bc_list = Pattern.matches("D\\d* All biclusters .*", label);
		boolean c_list = Pattern.matches("D\\d* All clusters .*", label);
		boolean search_list = Pattern.matches("D\\d* Search result of .*",
				label);
		boolean filter_list = Pattern.matches("D\\d* Filter result of .*",
				label);

		if (Pattern.matches("D.*", label)) {
			String[] x = label.split(" ");
			String[] a = x[0].split("D");
			result[2] = (new Integer(a[1])).intValue();
		}

		if (bc_list) {
			System.out.println("Bicluster list selected.");
			String[] sp = label.split("L.");
			result[0] = Dataset.BICLUSTER_LIST;
			result[1] = (new Integer(sp[1])).intValue();
		} else if (c_list) {
			System.out.println("Cluster list selected.");
			String[] sp = label.split(", C."); //6.1.2006: was String[] sp = label.split("C.");
			result[0] = Dataset.CLUSTER_LIST;
			result[1] = (new Integer(sp[1])).intValue();
		} else if (search_list) {
			System.out.println("Search list selected.");
			String[] sp = label.split(", S.");
			result[0] = Dataset.SEARCH_LIST;
			result[1] = (new Integer(sp[1])).intValue();
		} else if (filter_list) {
			System.out.println("Filter list selected.");
			String[] sp = label.split(", F.");
			result[0] = Dataset.FILTER_LIST;
			result[1] = (new Integer(sp[1])).intValue();
		} else {
			//System.out.println("Error in decoding the selected label!");
		}
		return result;
	}

	// ===========================================================================
	public static int getBcId(String label) {
		String[] ps = label.split(",");
		String[] pps = ps[0].split(" ");
		return (new Integer(pps[1])).intValue();
	}

	// ===========================================================================
	public static int[] getListAndIdxTree(String label) {

		if(BicatGui.debug)System.out.println("Debug, get List and Idx (Tree): " + label);

		int[] result = new int[4];
		result[0] = -1;
		result[1] = -1;
		result[2] = -1; // from which list is this (current) list created
		result[3] = -1;

		// Possibilities are: All biclusters, L.*
		// Search results of L/F/S*, S.*
		// Filter results of L/F/S*, F.*

		boolean bc_list = Pattern.matches("All biclusters .*", label);
		boolean c_list = Pattern.matches("All clusters .*", label);
		boolean search_list = Pattern.matches("Search result of .*", label);
		boolean filter_list = Pattern.matches("Filter result of .*", label);

		if (bc_list) {

			String[] sp = label.split(", L.");
			result[0] = Dataset.BICLUSTER_LIST;
			result[1] = Dataset.BICLUSTER_LIST;

			result[2] = (new Integer(sp[1])).intValue();
			result[3] = (new Integer(sp[1])).intValue();

		} else if (c_list) {

			String[] sp = label.split(", C.");
			result[0] = Dataset.CLUSTER_LIST;
			result[1] = Dataset.CLUSTER_LIST; // (here, nur repeat)

			result[2] = (new Integer(sp[1])).intValue();
			result[3] = (new Integer(sp[1])).intValue();

		} else if (search_list) {

			String[] sp = label.split(", S.");
			result[0] = Dataset.SEARCH_LIST;
			result[3] = (new Integer(sp[1])).intValue();

			if (Pattern.matches("Search result of L\\d*", sp[0])) {

				String[] x = sp[0].split(" ");
				String[] xx = x[x.length - 1].split("L");

				result[1] = Dataset.BICLUSTER_LIST; // on a BC list, was
				// filtered
				result[2] = (new Integer(xx[1])).intValue(); // what BC list
				// was filtered
				// on

			} else if (Pattern.matches("Search result of C\\d*", sp[0])) {

				String[] x = sp[0].split(" ");
				String[] xx = x[x.length - 1].split("C");

				result[1] = Dataset.CLUSTER_LIST; // on a C list, was filtered
				result[2] = (new Integer(xx[1])).intValue(); // what C list
				// was filtered
				// on

			} else if (Pattern.matches("Search result of F\\d*", sp[0])) {

				String[] x = sp[0].split(" ");
				String[] xx = x[x.length - 1].split("F");

				result[1] = Dataset.FILTER_LIST; // on a ... list, was
				// filtered
				result[2] = (new Integer(xx[1])).intValue(); // what BC list
				// was filtered
				// on

			} else if (Pattern.matches("Search result of S\\d*", sp[0])) {

				String[] x = sp[0].split(" ");
				String[] xx = x[x.length - 1].split("S");

				result[1] = Dataset.SEARCH_LIST; // on a BC list, was
				// filtered
				result[2] = (new Integer(xx[1])).intValue(); // what BC list
				// was filtered
				// on

			} else { /* SHOULD NOT HAPPEN! */
			}

		} else if (filter_list) {

			String[] sp = label.split(", F.");
			result[0] = 2; // it is filter list
			result[3] = (new Integer(sp[1])).intValue(); // the name of the
			// filter list (not
			// unique!!!)

			if (Pattern.matches("Filter result of L\\d*", sp[0])) {

				String[] x = sp[0].split(" ");
				String[] xx = x[x.length - 1].split("L");

				result[1] = Dataset.BICLUSTER_LIST; // on a BC list, was
				// filtered
				result[2] = (new Integer(xx[1])).intValue(); // what BC list
				// was filtered
				// on

			} else if (Pattern.matches("Filter result of C\\d*", sp[0])) {

				String[] x = sp[0].split(" ");
				String[] xx = x[x.length - 1].split("C");

				result[1] = Dataset.CLUSTER_LIST; // on a BC list, was
				// filtered
				result[2] = (new Integer(xx[1])).intValue(); // what BC list
				// was filtered
				// on

			} else if (Pattern.matches("Filter result of F\\d*", sp[0])) {

				String[] x = sp[0].split(" ");
				String[] xx = x[x.length - 1].split("F");

				result[1] = Dataset.FILTER_LIST; // on a BC list, was
				// filtered
				result[2] = (new Integer(xx[1])).intValue(); // what BC list
				// was filtered
				// on

			} else if (Pattern.matches("Filter result of S\\d*", sp[0])) {

				String[] x = sp[0].split(" ");
				String[] xx = x[x.length - 1].split("S");

				result[1] = Dataset.SEARCH_LIST; // on a BC list, was
				// filtered
				result[2] = (new Integer(xx[1])).intValue(); // what BC list
				// was filtered
				// on

			} else { /* SHOULD NOT HAPPEN! */
			}
		}

		else {
			System.out.println("Error in decoding the selected label!");
		}

		return result;
	}

	// ==========================================================================
	public static int[] getListAndIdxPath(TreePath treePath) {

		int dataset = getDataset(treePath.getPathComponent(1).toString())[0];

		int[] result = new int[5];
		int[] listAndIdx = getListAndIdxTree(treePath.getLastPathComponent()
				.toString());

		for (int i = 0; i < listAndIdx.length; i++)
			result[i] = listAndIdx[i];
		result[4] = dataset;

		return result;
	}

	// ==========================================================================
	public static int[] getListOfDatasetIdx(TreePath treePath) {

		int dataset = getDataset(treePath.getPathComponent(1).toString())[0];

		int[] result = new int[2];
		int list = getWhichList(treePath.getLastPathComponent().toString());

		result[0] = list;
		result[1] = dataset;

		return result;
	}

	static int getWhichList(String label) {
		if (Pattern.matches("Bicluster results", label))
			return Dataset.BICLUSTER_LIST;
		else if (Pattern.matches("Cluster results", label))
			return Dataset.CLUSTER_LIST;
		else if (Pattern.matches("Filter results", label))
			return Dataset.FILTER_LIST;
		else if (Pattern.matches("Search results", label))
			return -1;// return Dataset.SEARCH_LIST;
		else if (Pattern.matches("Analysis results", label))
			return -1;
		else
			return -1;
	}

	// ===========================================================================
	public static void print_arr(int[] a) {
		System.out.print("** => ");
		for (int i = 0; i < a.length; i++)
			System.out.print(" " + a[i]);
		System.out.println();
	}

}
