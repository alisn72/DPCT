package bicat.postprocessor;

import bicat.gui.BicatGui;
import bicat.util.AnalysisUtil;
import bicat.util.PostUtil;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * <p>
 * Title: BicAT Tool
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * @author D. Frick, A. Prelic
 * @version 1.0
 * 
 */

/*
 * Postprocessor is used to run clustering algorithms on the data sets and
 * search/sort the results.
 * 
 * It is important to note that <code>Postprocessor</code> uses its own class -
 * <code>Bicluster</code> - instead of the <code>bicat.Bicluster_bitset</code>
 * objects. <p> The methods in this class are generally called from the
 * governing <code>BicaGUI</code> object. <p> The data must be read from a
 * file and prepared by the <code>Preprocessor</code> before being handed to
 * the <code>PostProcessor</code> for biclustering. Although only the
 * binarized data is required for (bi)clustering, <code>Postprocessor</code>
 * will also keep a copy of the raw data for use in creation of <code>Bicluster</code>
 * objects and searching. <p> Once biclustering has been performed, the <code>Postprocessor</code>
 * will keep one copy of the original results in <code>bcs_add</code>, and
 * put all search/sort results into <code>bcs_add_results</code>. This allows
 * an arbitrary number of searches to be performed on the original set of
 * results.
 * 
 * @author D.Frick
 * 
 * @version 0.1
 * 
 */

// SHOULD CONTAIN THE MANAGEMENT OF THE BICLUSTERING LISTS, SEARCH, FILTER, AND
// ANALYSIS RESULTS! (ONLY)
public class Postprocessor {

	// static Dataset currentBcR;

	/**
	 * Hook to the governing <code>BicaGUI</code>, needed to inform it of
	 * process in biclustering.
	 */
	public static BicatGui owner;

	/** Contains processed but not discretized data taken from preprocessor. */
	// public static float[][] rawData;
	/** True if biclustering has been performed and list of results is available. */
	// public static boolean gotBiclusters;
	/**
	 * True if search has been performed and list of search results is
	 * available.
	 */
	// public static boolean gotSearchResults;
	/**
	 * Original list of Biclique (BICA) objects, taken straight from
	 * <code>block_bica_3d</code>.
	 */
	// public static LinkedList bcs;
	/**
	 * Master list of <code>Bicluster</code> objects, corresponds to list of
	 * results from <code>block_bica_3d</code>.
	 */
	// public static LinkedList bcs_add;
	/**
	 * List of <code>Bicluster</code> objects that have passed search
	 * criteria.
	 */
	// public static LinkedList bcs_search_results;
	/**
	 * True if degenerate (only one row/column) biclusters can be ignored,
	 * currently defaults to true.
	 */
	// public static boolean cullDegenerate; // SHOULD SET IT TO FALSE, 300404
	// new: static boolean extended;
	
	static int total_genes = 0; // this information should be associated with
								// the biclustersList (pre-processed, nrG, nrC,
								// extended, cullDegenerate...)

	static int total_chips = 0;

	// ===========================================================================
	public Postprocessor() {
	}

	/** Default constructor, initializes some values. */
	public Postprocessor(BicatGui o) {

		owner = o;

		// cullDegenerate = false; // DF: true;
		// gotBiclusters = false;
		// gotSearchResults = false;
		// extended = false;

		// rawData = null;

		// bcs = null;
		// bcs_add = null;
		// bcs_search_results = null;

		// biclusterList = new LinkedList();
		// searchList = new LinkedList();
		// filterList = new LinkedList();
		// analysisList = new LinkedList();
	}

	// ************************************************************************
	// //
	// * * //
	// * Post-processing procedures: filter, search, gpa... * //
	// * * //
	// ************************************************************************
	// //

	/**
	 * Filter out biclusters where: (a) area of BC smaller than <code>bcD</code>,
	 * or (b) gene set cardinality smaller than <code>gD</code>, or (c) chip
	 * set cardinality smaller that <code>cD</code>.
	 * <p>
	 * 
	 * Additionally, updates the dataset list with obtained results.
	 * 
	 */
	public LinkedList filterBySize(LinkedList bcList, int minG, int maxG,
			int minC, int maxC, int limitG, int limitC) {
		return PostUtil.filterBySize(minG, maxG, minC, maxC, limitG, limitC,
				bcList);
	}

	/**
	 * Filter out biclusters to obtain a <i>representative selection</i> of
	 * biclusters. A representative selection of biclusters are largest
	 * <code>bcNr</code> biclusters with an area overlap limited by parameter
	 * <code>thr</code>.
	 * <p>
	 * 
	 * Additionally, updates the dataset list with obtained results.
	 * 
	 */
	public LinkedList filterByOverlap(LinkedList bcList, int nrBCs, int overlap) {
		return PostUtil.filterByOverlap(nrBCs, overlap, bcList);
	}

	/**
	 * Filter out biclusters to obtain the <i>largest coverage</i> of the input
	 * matrix. The largest coverage of the input matrix is a list of biclusters,
	 * greedily selected: up to <code>bcNr</code> biclusters, each next
	 * bicluster selected contributes at most by the new matrix coverage.
	 * <p>
	 * 
	 * Additionally, updates the dataset list with obtained results.
	 * 
	 */
	/*
	 * public void filterByNewArea(Dataset br, int bcNr, int thr, int list, int
	 * idx) {
	 * 
	 * LinkedList bcList = br.getBCList(list, idx); //getBCList(br,list, idx);
	 * LinkedList filter = PostUtil.filterByNewArea(bcNr, thr, bcList,
	 * total_genes, total_chips);
	 * 
	 * br.updateFilterBiclustersLists(filter, list,
	 * idx);//updateFilterBiclustersLists(br,filter,list,idx); }
	 */

	/**
	 * Extend biclusters so to allow <i>error</i> in the patterns. The simple
	 * Hamming Distance was used to set the error, which has to be below the
	 * threshold parameter, <code>maxErr</code>.
	 * <p>
	 * 
	 * Additionally, updates the dataset list with obtained results.
	 * 
	 */
	/*
	 * public void filterByHammingDistanceExtension(Dataset br,int maxErr, int
	 * list, int idx, boolean isExtended) {
	 * 
	 * LinkedList bcList = br.getBCList(list, idx); //getBCList(br,list, idx);
	 * LinkedList filter; //if(isExtended) // filter =
	 * PostUtil.filterByHammingDistance(maxErr, bcList, total_genes/2,
	 * total_chips/2, // Preprocessor.discreteMatrixExtended); //else filter =
	 * PostUtil.filterByHammingDistance(maxErr, bcList, total_genes,
	 * total_chips, Preprocessor.getDiscreteData());
	 * 
	 * br.updateFilterBiclustersLists(filter, list, idx);
	 * //updateFilterBiclustersLists(br,filter,list,idx); }
	 */

	// ===========================================================================
	static public bicat.biclustering.Bicluster getExtension(
			bicat.biclustering.Bicluster bc, int error, boolean ext,
			int[][] dm, boolean gene_d_first) { // error is in percentage!

		int gd = 0;
		int cd = 0;
		if (ext) {
			gd = dm.length / 2;
			cd = dm[0].length / 2;
		} else {
			gd = dm.length;
			cd = dm[0].length;
		}

		return PostUtil.getHammingDistanceExtension(bc, gd, cd,
				error, dm,
				// ext,
				gene_d_first);
	}

	/**
	 * Search for biclusters in <code>list.idx</code> that satisfy given
	 * constraints. This search can look for an intersection and/or union of
	 * specified genes/chips.
	 * <p>
	 * 
	 * Additionally, updates the datasets list with obtained results.
	 * 
	 */
	/*
	 * public void search(Dataset br, int list, int idx, String geneStr, String
	 * chipStr, boolean andSearch) {
	 * 
	 * LinkedList bcList = br.getBCList(list, idx); // getBCList(br, list, idx);
	 * if(owner.debug) System.out.println("BC list size = "+bcList.size());
	 * 
	 * LinkedList search = PostUtil.search(0,0,0, geneStr, chipStr, andSearch,
	 * bcList); if(owner.debug) System.out.println("BC list Search size =
	 * "+search.size());
	 * 
	 * br.updateSearchBiclustersLists(search, list, idx);
	 * //updateSearchBiclustersLists(br, search, list, idx); }
	 */

	public LinkedList search(LinkedList bcList, String geneStr, String chipStr,
			boolean andSearch) {
		return PostUtil.search(0, 0, 0, geneStr, chipStr, andSearch, bcList);
	}

	/**
	 * TO DO!!!
	 * 
	 * Sorts both the master list and search result list by size.
	 * 
	 * Goes through both <code>Bicluster</code> lists and sorts them according
	 * to the size (number of genes times number of chips) of each bicluster.
	 * Will sort all lists that are available, and do nothing if they aren't.
	 * <p>
	 * Uses what may be the least efficient sorting algorithm in the universe.
	 * 
	 */
	public static void sortListBySize() {

		/*
		 * // sort main BC list (bcs_add) //if(false == gotBiclusters) return; //
		 * only proceed if list of BCs is available
		 * 
		 * int index; bicat.biclustering.Bicluster insertMe; LinkedList newList =
		 * new LinkedList();
		 * 
		 * for(int i=0; i<bcs_add.size(); i++) { index = 0; insertMe =
		 * (bicat.biclustering.Bicluster)bcs_add.get(i); // gget next BC to
		 * insert into sorted list
		 * 
		 * while(newList.size() > index) { // go through list of already sorted
		 * BCs if(insertMe.getSize() >
		 * ((bicat.biclustering.Bicluster)newList.get(index)).getSize()) break; //
		 * looking for a place to insert the new one .. index++; }
		 * newList.add(index, insertMe); // .. and insert it at that spot }
		 * bcs_add = newList;
		 *  // sort list of search results (bcs_serach_results) //if(false ==
		 * gotSearchResults) return; // only proceed if list of serach results
		 * is available
		 * 
		 * newList = new LinkedList();
		 * 
		 * for(int i=0; i<bcs_search_results.size(); i++) { index = 0; insertMe =
		 * (bicat.biclustering.Bicluster)bcs_search_results.get(i); // get next
		 * BC to insert into sorted list
		 * 
		 * while(newList.size() > index) { // go through list of already sorted
		 * BCs... if(insertMe.getSize() >
		 * ((bicat.biclustering.Bicluster)newList.get(index)).getSize()) break; //
		 * ..looking for a place to insert the new one.. index++; }
		 * newList.add(index,insertMe); // .. and insert it at that spot }
		 * bcs_search_results = newList;
		 */
	}

	/**
	 * Gene Pair Analysis (GPA) by Cooccurrence, of biclusters in the
	 * <code>list.idx</code>. Each gene pair obtains a score, which is a
	 * frequence of co-occurrence in considered biclusters.
	 * <p>
	 * 
	 * Additionally, updates the dataset list with obtained results.
	 * 
	 */
	public HashMap gpaByCoocurrence(LinkedList bcList, int minCoocScore) {
		return AnalysisUtil.gpaByCoocurrence(minCoocScore, bcList);
	}

	/**
	 * Gene Pair Analysis (GPA) by Common Chips, of biclusters in the
	 * <code>list.idx</code>. Each gene pair obtains a score, which is the
	 * maximum size of the chip set, where the two genes are co-biclustered.
	 * <p>
	 * 
	 * Additionally, updates the dataset list with obtained results.
	 * 
	 */
	public HashMap gpaByCommonChips(LinkedList bcList, int minCommonScore) {
		return AnalysisUtil.gpaByCommonChips(minCommonScore, bcList);
	}

	// ===========================================================================
	// ovo je kao toString() .... zasto mi ovo treba? BOH.... chech this.
	public String gpaWriteResults() {
		// HashMap GPs_scores = (HashMap)analysisList.getLast();
		// return AnalysisUtil.toString_exportToBioLayout(GPs_scores);
		return null;
	}

	// ===========================================================================
	public String gpaWriteResults(int i) {
		// HashMap GPs_scores = (HashMap)analysisList.get(i);
		// return AnalysisUtil.toString_exportToBioLayout(GPs_scores);
		return null;
	}

	// ===========================================================================
	// ===========================================================================
	// ===========================================================================
	// OTHER....

	// ===========================================================================
	/**
	 * ....
	 * 
	 */
	// public static void setCullDegenerate(boolean yesNo) { cullDegenerate =
	// yesNo; }
	// ===========================================================================
	/**
	 * 
	 * the dataset corresponding to the BC list need to be loaded already! Plus,
	 * this should be done by BicatGui???
	 * 
	 */
	public static void loadList(LinkedList list, float[][] originalData) {

		// bcs = null;
		// bcs_add = list;
		// bcs_search_results = null;

		// rawData = originalData;

		// gotBiclusters = true;
		// gotSearchResults = false;
	}

	// ===========================================================================
	/*
	 * Takes a dataset, stores it, runs <code>block_bica_3d</code> on it, and
	 * performs basic operations on the results to prepare them for local use.
	 * 
	 * This is the method to call outside when one wants to perform biclustering
	 * on a given dataset. However, calling <code>processMatrix</code> will
	 * only START the biclustering process; its completion is marked by a
	 * function call in the <code>finished</code> method in the local <code>SwingWorker</code>
	 * object.
	 * 
	 * @param binary_matrix two-dimensional array of discretized values that
	 * will be handed to <code>block_bica_3d</code> @param originalData
	 * two-dimensional array of preprocessed but not yet discretized data
	 * 
	 * @see #sortListsByMSRS() sortListsByMSRS
	 * @see #sortListsBySize() sortListsBySize
	 * @see #performSearch(int, int, int, float, String, String, boolean)
	 *      performSearch
	 * 
	 */
	/*
	 * public synchronized void runBiclustering(int[][] binary_matrix, float[][]
	 * originalData, ProgressMonitor pm) {
	 * 
	 * rawData = originalData;
	 *  // ATTENZIONE: rawData is only the quarter A (in the extended case!)
	 *  // create new Bica linked with progress Monitor final BiMax b = new
	 * BiMax(pm); final int[][] matrix = binary_matrix;
	 * 
	 * total_genes = binary_matrix.length; total_chips =
	 * binary_matrix[0].length;
	 *  // start Bica in a separate thread final SwingWorker worker = new
	 * SwingWorker() {
	 * 
	 * public Object construct() { // run Bica with no print to screen, no prnt
	 * to file, finding only "1" BCs
	 * 
	 * if(extended) System.out.println("Running Extended Biclustering... ");
	 * System.out.println("Started Bica in a new thread! Matrix dimensions:
	 * "+matrix.length+"/"+matrix[0].length); b.run(matrix, matrix.length,
	 * matrix[0].length, "1", "no", null); return null; }
	 * 
	 * public void finished() { System.out.println("Getting results from Bica");
	 *  // get result from Bica bcs = b.getBiclusters();
	 *  // clear old results bcs_add = null; bcs_search_results = null;
	 * 
	 * if(extended) transferListExtended(matrix); else transferList(matrix);
	 * System.out.println(bcs_add.size() + " out of " +bcs.size() + " BCs not
	 * degenerate");
	 * 
	 * if(extended) bcs_add = cleanBiclusterList(bcs_add); biclusterList.add(new
	 * LinkedList(bcs_add));
	 * 
	 * //currentBcR.addBiclusters(new LinkedList(bcs_add)); //int currentData =
	 * owner.datasetList.size() - 1;
	 * //((Dataset)owner.datasetList.get(currentData)).addBiclusters(new
	 * LinkedList(bcs_add));
	 * 
	 * owner.addBiclusters(owner.currentDataset,bcs_add);
	 * 
	 * owner.updatePreprocessOptionsMenu(); owner.updateBiclusterMenu();
	 * owner.buildTree(); } } ; worker.start(); }
	 * 
	 */
	// ===========================================================================
	void to_dos() {
		/*
		 * public LinkedList sortListByMSRS() { return null; }
		 */
	}

}
