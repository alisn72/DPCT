package bicat.biclustering;

import bicat.gui.BicatGui;
import bicat.preprocessor.PreprocessOption;
import bicat.util.BicatUtil;

import java.util.HashMap;
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

public class Dataset {

	public boolean preprocessComplete = false;

	public void setPreprocessComplete() {
		preprocessComplete = true;
	}

	public boolean preprocessComplete() {
		return preprocessComplete;
	}

	/** Data matrix for the <code>this</code> dataset. */
	float[][] origDataMatrix;

	/** Data matrix preprocessed, as to the last preprocessing options. */
	float[][] preDataMatrix;

	/** Data matrix discretized, as to the last preprocessing options. */
	int[][] discrDataMatrix;

	/**
	 * Boolean indicator:<br>
	 * <code>true</code>, if matrix has been loaded with the chips-2-controls
	 * file;<br>
	 * <code>false</code>, otherwise.
	 * <p>
	 * 
	 */
	boolean withCtrlInfo;

	/** Dependencies matrix that contains the mappings: chips-2-controls. */
	int[] chipToControls;

	public static int BICLUSTER_LIST = 0; // "Bicluster Results";

	public static int SEARCH_LIST = 1; // "Search Results";

	public static int FILTER_LIST = 2; // "Filter Results";

	public static int CLUSTER_LIST = 3; // "Cluster Results";

	public int valid_biclusters = 0;

	public int valid_clusters = 0;

	public int valid_filters = 0;

	public int valid_searches = 0;

	// ....
	LinkedList preprocessOptionsList;

	LinkedList biclusterList;

	Vector BiclusterList_names;

	Vector BiclusterList_valid;

	// ....
	LinkedList preprocessOptionsList_cluster;

	LinkedList clusterList;

	Vector ClusterList_names;

	Vector ClusterList_valid;

	// ....
	LinkedList filterList;

	Vector FilterList_names;

	Vector FilterList_valid;

	LinkedList f2l;

	LinkedList f2c;

	LinkedList f2s;

	LinkedList f2f;

	HashMap f2list;

	HashMap f2idx;

	HashMap f2clist;

	HashMap f2cidx;

	LinkedList f2l_valid;

	LinkedList f2s_valid;

	LinkedList f2f_valid;

	LinkedList f_valid;

	LinkedList fc_valid;

	HashMap f2l_realIdx;

	HashMap f2c_realIdx;

	HashMap f2s_realIdx;

	HashMap f2f_realIdx;

	// ....
	LinkedList searchList;

	Vector SearchList_names;

	Vector SearchList_valid;

	LinkedList s2l;

	LinkedList s2s;

	LinkedList s2f;

	HashMap s2list;

	HashMap s2idx;

	HashMap s2l_realIdx;

	HashMap s2s_realIdx;

	HashMap s2f_realIdx;

	LinkedList s2l_valid;

	LinkedList s2s_valid;

	LinkedList s2f_valid;

	LinkedList s_valid;

	LinkedList s2c;

	HashMap s2c_realIdx;

	HashMap s2clist;

	HashMap s2cidx;

	LinkedList sc_valid;

	// ....
	LinkedList analysisList;

	Vector AnalysisList_names;

	Vector AnalysisList_valid;

	LinkedList a2l;

	LinkedList a2s;

	LinkedList a2f;

	HashMap a2list;

	HashMap a2idx;

	HashMap a2l_realIdx;

	HashMap a2s_realIdx;

	HashMap a2f_realIdx;

	LinkedList a2l_valid;

	LinkedList a2s_valid;

	LinkedList a2f_valid;

	LinkedList a_valid;

	LinkedList a2c;

	HashMap a2c_realIdx;

	HashMap a2clist;

	HashMap a2cidx;

	LinkedList ac_valid;

	LinkedList l_valid;

	// ....
	Vector geneNames;

	Vector chipNames;

	Vector workChipNames;

	Vector headerColLabels;

	Vector headerColInfo;

	// ===========================================================================

	/*
	 * Vector labels; Vector labelArrays;
	 */

	public Vector getHeaderColumnLabels() {
		return headerColLabels;
	}

	public Vector getHeaderColumns() {
		return headerColInfo;
	}

	// ===========================================================================
	public Dataset() {
	}

	public Dataset(float[][] data, boolean withCtrl, int[] chip2ctrl,
			Vector gNames, Vector cNames, Vector workCNames, Vector colLabels,
			Vector colInfo) {
		// withCtrl = true, wenn Dataset aus Kontrolle / Experiment besteht
		// chip2ctrl zum Ratios ausrechnen

		origDataMatrix = data;

		if (withCtrl) {
			withCtrlInfo = true;
			chipToControls = chip2ctrl;
		}

		geneNames = new Vector(gNames);
		chipNames = new Vector(cNames);
		workChipNames = new Vector(workCNames);
		headerColLabels = new Vector(colLabels);
		headerColInfo = new Vector(colInfo);

		// initialize all fields
		preprocessOptionsList = new LinkedList();
		preprocessOptionsList_cluster = new LinkedList();

		biclusterList = new LinkedList();
		clusterList = new LinkedList();
		searchList = new LinkedList();
		filterList = new LinkedList();
		analysisList = new LinkedList();

		BiclusterList_names = new Vector();
		ClusterList_names = new Vector();
		FilterList_names = new Vector();
		SearchList_names = new Vector();
		AnalysisList_names = new Vector();

		BiclusterList_valid = new Vector();
		ClusterList_valid = new Vector();
		FilterList_valid = new Vector();
		SearchList_valid = new Vector();
		AnalysisList_valid = new Vector();

		// ...
		f2l = new LinkedList();
		f2s = new LinkedList();
		f2f = new LinkedList();
		f2list = new HashMap();
		f2idx = new HashMap();

		f2c = new LinkedList();
		f2clist = new HashMap();
		f2cidx = new HashMap();

		f2l_realIdx = new HashMap();
		f2c_realIdx = new HashMap();
		f2s_realIdx = new HashMap();
		f2f_realIdx = new HashMap();
		f2l_valid = new LinkedList();
		// f2c_valid = new LinkedList();
		f2s_valid = new LinkedList();
		f2f_valid = new LinkedList();
		f_valid = new LinkedList();
		fc_valid = new LinkedList();
		l_valid = new LinkedList();

		// ...
		s2l = new LinkedList();
		s2s = new LinkedList();
		s2f = new LinkedList();
		s2list = new HashMap();
		s2idx = new HashMap();

		s2l_realIdx = new HashMap();
		s2s_realIdx = new HashMap();
		s2f_realIdx = new HashMap();
		s2l_valid = new LinkedList();
		s2s_valid = new LinkedList();
		s2f_valid = new LinkedList();
		s_valid = new LinkedList();

		s2c = new LinkedList();
		s2cidx = new HashMap();
		s2clist = new HashMap();
		s2c_realIdx = new HashMap();
		sc_valid = new LinkedList();

		// ...
		a2l = new LinkedList();
		a2s = new LinkedList();
		a2f = new LinkedList();
		a2list = new HashMap();
		a2idx = new HashMap();

		a2l_realIdx = new HashMap();
		a2s_realIdx = new HashMap();
		a2f_realIdx = new HashMap();
		a2l_valid = new LinkedList();
		a2s_valid = new LinkedList();
		a2f_valid = new LinkedList();
		a_valid = new LinkedList();

		a2c = new LinkedList();
		a2cidx = new HashMap();
		a2clist = new HashMap();
		a2c_realIdx = new HashMap();
		ac_valid = new LinkedList();

	}

	public Dataset(float[][] data, float[][] dataPre, Vector gNames,
			Vector cNames, Vector workCNames, Vector colLabels, Vector colInfo) {

		/*
		 * origDataMatrix = data; preDataMatrix = dataPre;
		 */

		origDataMatrix = new float[data.length][data[0].length];
		preDataMatrix = new float[dataPre.length][dataPre[0].length];
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[0].length; j++)
				origDataMatrix[i][j] = data[i][j];
		for (int i = 0; i < dataPre.length; i++)
			for (int j = 0; j < dataPre[0].length; j++)
				preDataMatrix[i][j] = dataPre[i][j];

		if (true) {
			withCtrlInfo = true;
			chipToControls = null; // chip2ctrl;
		}

		geneNames = new Vector(gNames);
		chipNames = new Vector(cNames);
		workChipNames = new Vector(workCNames);
		headerColLabels = new Vector(colLabels);
		headerColInfo = new Vector(colInfo);

		// initialize all fields
		preprocessOptionsList = new LinkedList();
		preprocessOptionsList_cluster = new LinkedList();

		biclusterList = new LinkedList();
		clusterList = new LinkedList();
		searchList = new LinkedList();
		filterList = new LinkedList();
		analysisList = new LinkedList();

		BiclusterList_names = new Vector();
		ClusterList_names = new Vector();
		FilterList_names = new Vector();
		SearchList_names = new Vector();
		AnalysisList_names = new Vector();

		BiclusterList_valid = new Vector();
		ClusterList_valid = new Vector();
		FilterList_valid = new Vector();
		SearchList_valid = new Vector();
		AnalysisList_valid = new Vector();

		// ...
		f2l = new LinkedList();
		f2s = new LinkedList();
		f2f = new LinkedList();
		f2list = new HashMap();
		f2idx = new HashMap();

		f2c = new LinkedList();
		f2clist = new HashMap();
		f2cidx = new HashMap();

		f2l_realIdx = new HashMap();
		f2c_realIdx = new HashMap();
		f2s_realIdx = new HashMap();
		f2f_realIdx = new HashMap();
		f2l_valid = new LinkedList();
		// f2c_valid = new LinkedList();
		f2s_valid = new LinkedList();
		f2f_valid = new LinkedList();
		f_valid = new LinkedList();
		fc_valid = new LinkedList();
		l_valid = new LinkedList();

		// ...
		s2l = new LinkedList();
		s2s = new LinkedList();
		s2f = new LinkedList();
		s2list = new HashMap();
		s2idx = new HashMap();

		s2l_realIdx = new HashMap();
		s2s_realIdx = new HashMap();
		s2f_realIdx = new HashMap();
		s2l_valid = new LinkedList();
		s2s_valid = new LinkedList();
		s2f_valid = new LinkedList();
		s_valid = new LinkedList();

		s2c = new LinkedList();
		s2cidx = new HashMap();
		s2clist = new HashMap();
		s2c_realIdx = new HashMap();
		sc_valid = new LinkedList();

		// ...
		a2l = new LinkedList();
		a2s = new LinkedList();
		a2f = new LinkedList();
		a2list = new HashMap();
		a2idx = new HashMap();

		a2l_realIdx = new HashMap();
		a2s_realIdx = new HashMap();
		a2f_realIdx = new HashMap();
		a2l_valid = new LinkedList();
		a2s_valid = new LinkedList();
		a2f_valid = new LinkedList();
		a_valid = new LinkedList();

		a2c = new LinkedList();
		a2cidx = new HashMap();
		a2clist = new HashMap();
		a2c_realIdx = new HashMap();
		ac_valid = new LinkedList();
	}

	public Dataset(float[][] data, float[][] dataPre, int[][] dataDiscr,
			Vector gNames, Vector cNames, Vector workCNames, Vector colLabels,
			Vector colInfo) {

		this(data, dataPre, gNames, cNames, workCNames, colLabels, colInfo);
		discrDataMatrix = null;
		/*
		 * discrDataMatrix = new int[dataDiscr.length][dataDiscr[0].length];
		 * for(int i = 0; i < dataDiscr.length; i++) for(int j = 0; j <
		 * dataDiscr[0].length; j++) discrDataMatrix[i][j] = dataDiscr[i][j];
		 */

		// System.out.println("1: "+data.length+", "+data[0].length);
		// System.out.println("2: "+dataPre.length+", "+dataPre[0].length);
		// System.out.println("3: "+dataDiscr.length+", "+dataDiscr[0].length);
	}

	// ===========================================================================
	public float[][] getData() {
		return origDataMatrix;
	}

	public float[][] getOrigData() {
		return origDataMatrix;
	}

	public float[][] getPreData() {
		return preDataMatrix;
	}

	public void setPreData(float[][] d) {
		preDataMatrix = new float[d.length][d[0].length];
		for (int i = 0; i < d.length; i++)
			for (int j = 0; j < d[0].length; j++)
				preDataMatrix[i][j] = d[i][j];
	}

	// ===========================================================================
	int[][] visualDiscrDataMatrix;

	boolean extended;

	public boolean isExtended() {
		return extended;
	}

	public void setExtended() {
		extended = true;
	}

	public void resetExtended() {
		extended = false;
	}

	// ===========================================================================
	/*
	 * public void setVisualDiscrData(int[][] d) { visualDiscrDataMatrix = new
	 * int[d.length][d[0].length]; for(int i = 0; i<d.length; i++) for(int j =
	 * 0; j<d[0].length; j++) visualDiscrDataMatrix[i][j] = d[i][j]; }
	 */
	/** Return a binary matrix. */
	public int[][] getVisualDiscrData(boolean exted) {

		if (!exted)
			return visualDiscrDataMatrix;

		int BASE_1 = visualDiscrDataMatrix.length / 2;
		int BASE_2 = visualDiscrDataMatrix[0].length / 2;

		int[][] new_discr = new int[BASE_1][BASE_2];
		for (int i = 0; i < BASE_1; i++)
			for (int j = 0; j < BASE_2; j++)
				new_discr[i][j] = visualDiscrDataMatrix[i][j];

		for (int i = BASE_1 + 0; i < BASE_1 * 2; i++)
			for (int j = BASE_2 + 0; j < BASE_2 * 2; j++)
				if (visualDiscrDataMatrix[i][j] == -1)
					new_discr[i - BASE_1][j - BASE_2] = -1;

		return new_discr;
	}

	// ===========================================================================
	public int[][] getDiscrData() {
		return discrDataMatrix;
	}

	public void setDiscrData(int[][] d) {
		discrDataMatrix = new int[d.length][d[0].length];
		visualDiscrDataMatrix = new int[d.length][d[0].length];
		for (int i = 0; i < d.length; i++)
			for (int j = 0; j < d[0].length; j++) {
				if (d[i][j] == -1)
					discrDataMatrix[i][j] = 1;
				else
					discrDataMatrix[i][j] = d[i][j];
				visualDiscrDataMatrix[i][j] = d[i][j];
			}
	}

	public String getGeneName(int i) {
		return (String) geneNames.get(i);
	}

	public int getGeneIdx(String gn) {
		for (int i = 0; i < geneNames.size(); i++)
			if (geneNames.get(i).equals(gn))
				return i;
		return -1;
	}

	public int getGeneCount() {
		return geneNames.size();
	}

	public int getChipCount() {
		return chipNames.size();
	}

	public int getWorkingChipCount() {
		return workChipNames.size();
	}

	public String getChipName(int i) {
		return (String) chipNames.get(i);
	}

	public String getWorkingChipName(int i) {
		return (String) workChipNames.get(i);
	}

	public int getWorkChipIdx(String wcn) {
		// System.out.println("Dddd: "+workChipNames.size());
		for (int i = 0; i < workChipNames.size(); i++)
			if (workChipNames.get(i).equals(wcn))
				return i;
		return -1;
	}

	public String getLabelName(int gIdx, int label) {
		if (gIdx >= geneNames.size() || gIdx < 0)
			return null;
		return (String) ((Vector) headerColInfo.get(label)).get(gIdx);
	}

	// ===========================================================================
	public LinkedList getBiclusters() {
		return biclusterList;
	}

	public Vector getBiclustersNames() {
		return BiclusterList_names;
	}

	public Vector getBiclustersValid() {
		return BiclusterList_valid;
	}

	public LinkedList getBiclusters(int i) {
		return (LinkedList) biclusterList.get(i);
	}

	public LinkedList getClusters() {
		return clusterList;
	}

	public Vector getClustersNames() {
		return ClusterList_names;
	}

	public Vector getClustersValid() {
		return ClusterList_valid;
	}

	public LinkedList getClusters(int i) {
		return (LinkedList) clusterList.get(i);
	}

	public LinkedList getPreprocessOptions() {
		return preprocessOptionsList;
	}

	public PreprocessOption getPreprocessOptions(int i) {
		return (PreprocessOption) preprocessOptionsList.get(i);
	}

	public LinkedList getPreprocessOptionsClusters() {
		return preprocessOptionsList_cluster;
	}

	public PreprocessOption getPreprocessOptionsClusters(int i) {
		return (PreprocessOption) preprocessOptionsList_cluster.get(i);
	}

	public LinkedList getSearch() {
		return searchList;
	}

	public Vector getSearchNames() {
		return SearchList_names;
	}

	public Vector getSearchValid() {
		return SearchList_valid;
	}

	public LinkedList getSearch(int i) {
		return (LinkedList) searchList.get(i);
	}

	public LinkedList getFilters() {
		return filterList;
	}

	public Vector getFiltersNames() {
		return FilterList_names;
	}

	public Vector getFiltersValid() {
		return FilterList_valid;
	}

	public LinkedList getFilters(int i) {
		return (LinkedList) filterList.get(i);
	}

	public LinkedList getAnalysis() {
		return analysisList;
	}

	public Vector getAnalysisNames() {
		return AnalysisList_names;
	}

	public Vector getAnalysisValid() {
		return AnalysisList_valid;
	}

	public HashMap getAnalysis(int i) {
		return (HashMap) analysisList.get(i);
	}

	// ===========================================================================
	HashMap getF2List() {
		return f2list;
	}

	HashMap getF2Idx() {
		return f2idx;
	}

	int getF2List(int j) {
		return ((Integer) f2list.get(new Integer(j))).intValue();
	}

	int getF2Idx(int j) {
		return ((Integer) f2idx.get(new Integer(j))).intValue();
	}

	LinkedList getF2F() {
		return f2f;
	}

	LinkedList getF2L() {
		return f2l;
	}

	LinkedList getF2S() {
		return f2s;
	}

	int getF2F(int j) {
		return ((Integer) f2f.get(j)).intValue();
	}

	int getF2S(int j) {
		return ((Integer) f2s.get(j)).intValue();
	}

	int getF2L(int j) {
		return ((Integer) f2l.get(j)).intValue();
	}

	// ...
	HashMap getS2List() {
		return s2list;
	}

	HashMap getS2Idx() {
		return s2idx;
	}

	int getS2List(int j) {
		return ((Integer) s2list.get(new Integer(j))).intValue();
	}

	int getS2Idx(int j) {
		return ((Integer) s2idx.get(new Integer(j))).intValue();
	}

	LinkedList getS2F() {
		return s2f;
	}

	LinkedList getS2L() {
		return s2l;
	}

	LinkedList getS2S() {
		return s2s;
	}

	int getS2F(int j) {
		return ((Integer) s2f.get(j)).intValue();
	}

	int getS2S(int j) {
		return ((Integer) s2s.get(j)).intValue();
	}

	int getS2L(int j) {
		return ((Integer) s2l.get(j)).intValue();
	}

	// ...
	HashMap getA2List() {
		return a2list;
	}

	HashMap getA2Idx() {
		return a2idx;
	}

	int getA2List(int j) {
		return ((Integer) a2list.get(new Integer(j))).intValue();
	}

	int getA2Idx(int j) {
		return ((Integer) a2idx.get(new Integer(j))).intValue();
	}

	LinkedList getA2F() {
		return a2f;
	}

	LinkedList getA2L() {
		return a2l;
	}

	LinkedList getA2S() {
		return a2s;
	}

	int getA2F(int j) {
		return ((Integer) a2f.get(j)).intValue();
	}

	int getA2S(int j) {
		return ((Integer) a2s.get(j)).intValue();
	}

	int getA2L(int j) {
		return ((Integer) a2l.get(j)).intValue();
	}

	// ===========================================================================
	public boolean biclustersAvailable() {
		return biclusterList.size() > 0;
	}

	public boolean clustersAvailable() {
		return clusterList.size() > 0;
	}

	public boolean searchResultsAvailable() {
		return searchList.size() > 0;
	}

	public boolean filterResultsAvailable() {
		return filterList.size() > 0;
	}

	public boolean analysisResultsAvailable() {
		return analysisList.size() > 0;
	}

	public boolean validBiclustersAvailable() {
		return valid_biclusters > 0;
	}

	public boolean validClustersAvailable() {
		return valid_clusters > 0;
	}

	public boolean validSearchesAvailable() {
		return valid_searches > 0;
	}

	public boolean validFiltersAvailable() {
		return valid_filters > 0;
	}

	// ===========================================================================

	public void removeBicluster(LinkedList list, int id) {

		for (int i = 0; i < list.size(); i++) {
			int cid = ((Bicluster) list.get(i)).getId();
			if (cid == id) {
				list.remove(i);
				if(BicatGui.debug)System.out.println("Debug: Removed the BC of ID " + id);
				return;
			}
		}
	}

	// ************************************************************************
	// //
	// * * //
	// * Dataset: Results Updates, Translations... * //
	// * * //
	// ************************************************************************
	// //

	/** Get the bicluster list <code>w.i</code> of the dataset. */
	public LinkedList getBCList(int w, int i) {
		if (w == BICLUSTER_LIST)
			return getBiclusters(i);
		else if (w == SEARCH_LIST)
			return getSearch(i);
		else if (w == FILTER_LIST)
			return getFilters(i);
		else if (w == CLUSTER_LIST)
			return getClusters(i);
		else {
			System.out.println("ERROR/BUG: Unknown List Selected!");
			return null;
		}
	}

	public LinkedList getBCsList(int w) {
		if (w == BICLUSTER_LIST)
			return getBiclusters();
		else if (w == SEARCH_LIST)
			return getSearch();
		else if (w == FILTER_LIST)
			return getFilters();
		else if (w == CLUSTER_LIST)
			return getClusters();
		else {
			System.out.println("ERROR/BUG: Unknown List Selected!");
			return null;
		}
	}

	public void shadeList(int which_list, int real_idx) {
		if (which_list == BICLUSTER_LIST) {
			l_valid.set(real_idx, new Integer(0));
		} else if (which_list == SEARCH_LIST) {
			s_valid.set(real_idx, new Integer(0));
		} else if (which_list == FILTER_LIST) {
			f_valid.set(real_idx, new Integer(0));
		} else {
			System.out.println("Should not happen? Analysis?");
		}
	}

	// ===========================================================================
	public void invalidList(int which_list, int real_idx) {
		if (which_list == BICLUSTER_LIST) {
			if (true)
				System.out.println("Remove BiclusterList " + real_idx);
			BiclusterList_valid.set(real_idx, new Boolean(false));
			valid_biclusters--;
		} else if (which_list == CLUSTER_LIST) {
			ClusterList_valid.set(real_idx, new Boolean(false));
			valid_clusters--;
		} else if (which_list == FILTER_LIST) {
			FilterList_valid.set(real_idx, new Boolean(false));
			valid_filters--;
		} else if (which_list == SEARCH_LIST) {
			SearchList_valid.set(real_idx, new Boolean(false));
			valid_searches--;
		}
		// else if(which_list == ANALYSIS_LIST) AnalysisList_valid.set(real_idx,
		// new Boolean(false));

		else
			;
	}

	/** Obtain the physical index of the filter list selected */
	public int getFilterRealIdx(int which_list, int which_list_idx, int f_idx) {
		if (which_list == BICLUSTER_LIST) {
			System.out.println("F2L: BC list original, real idx....");
			return ((Integer) f2l_realIdx.get("" + f_idx + "," + which_list_idx
					+ "")).intValue();
		} else if (which_list == CLUSTER_LIST) {
			System.out.println("F2L: Cluster list original, real idx....");
			return ((Integer) f2c_realIdx.get("" + f_idx + "," + which_list_idx
					+ "")).intValue();
		} else if (which_list == SEARCH_LIST) {
			System.out.println("F2S: search list original, real idx....");
			return ((Integer) f2s_realIdx.get("" + f_idx + "," + which_list_idx
					+ "")).intValue();
		} else if (which_list == FILTER_LIST) {
			System.out.println("F2F: filter list original, real idx....");
			return ((Integer) f2f_realIdx.get("" + f_idx + "," + which_list_idx
					+ "")).intValue();
		} else
			return 0;
	}

	/** Obtain the physical index of the search list selected */
	public int getSearchRealIdx(int which_list, int which_list_idx, int f_idx) {
		if (which_list == BICLUSTER_LIST) {
			System.out.println("S2L: BC list original, real idx....");
			return ((Integer) s2l_realIdx.get("" + f_idx + "," + which_list_idx
					+ "")).intValue();
		} else if (which_list == SEARCH_LIST) {
			System.out.println("S2S: search list original, real idx....");
			return ((Integer) s2s_realIdx.get("" + f_idx + "," + which_list_idx
					+ "")).intValue();
		} else if (which_list == FILTER_LIST) {
			System.out.println("S2F: filter list original, real idx....");
			return ((Integer) s2f_realIdx.get("" + f_idx + "," + which_list_idx
					+ "")).intValue();
		} else
			return 0;
	}

	/** Obtain the physical index of the analysis list selected */
	public int getAnalysisRealIdx(int which_list, int which_list_idx, int f_idx) {
		if (which_list == BICLUSTER_LIST) {
			System.out.println("A2L: BC list original, real idx....");
			return ((Integer) a2l_realIdx.get("" + f_idx + "," + which_list_idx
					+ "")).intValue();
		} else if (which_list == SEARCH_LIST) {
			System.out.println("A2S: search list original, real idx....");
			return ((Integer) a2s_realIdx.get("" + f_idx + "," + which_list_idx
					+ "")).intValue();
		} else if (which_list == FILTER_LIST) {
			System.out.println("A2F: filter list original, real idx....");
			return ((Integer) a2f_realIdx.get("" + f_idx + "," + which_list_idx
					+ "")).intValue();
		} else
			return 0;
	}

	/** Get the source list of the filtering <code>j</code>. */
	public int getFilterOnXList(int j) {
		return getF2List(j);
	}

	/** Get the source list idx of the filtering <code>j</code>. */
	public int getFilterOnXIdx(int j) {
		return getF2Idx(j);
	}

	/** Get the filter idx of the filtering <code>w.i</code>. */
	public int getBCListIdxForFilterResult(int which, int i) {
		if (which == BICLUSTER_LIST)
			return getF2L(i);
		else if (which == SEARCH_LIST)
			return getF2S(i);
		else
			return getF2F(i);
	}

	// ***
	public int getSearchOnXList(int j) {
		return getS2List(j);
	}

	public int getSearchOnXIdx(int j) {
		return getS2Idx(j);
	}

	public int getBCListIdxForSearchResult(int which, int i) {
		if (which == BICLUSTER_LIST)
			return getS2L(i);
		else if (which == SEARCH_LIST)
			return getS2S(i);
		else
			return getS2F(i);
	}

	// ***
	public int getAnalysisOnXList(int j) {
		return getA2List(j);
	}

	public int getAnalysisOnXIdx(int j) {
		return getA2Idx(j);
	}

	public int getBCListIdxForAnalysisResult(int which, int i) {
		if (which == BICLUSTER_LIST)
			return getA2L(i);
		else if (which == SEARCH_LIST)
			return getA2S(i);
		else
			return getA2F(i);
	}

	/** Add <code>bcs</code> list to the list of biclusters in the dataset. */
	public void addBiclusters(LinkedList bcs) {
		biclusterList.add(bcs);
		l_valid.add(new Integer(1));
		// valid_biclusters++;
	}

	public void addPreprocessOption(PreprocessOption po) {
		preprocessOptionsList.add(po);
	}

	/** Name the new bicluster list in the dataset. */
	public void addBiclustersName() {
		// if(BiclusterList_names.size() == 0)
		// BiclusterList_names.add("Choose Biclusters list...");
		int j = biclusterList.size() - 1;
		BiclusterList_names.add("All biclusters, L." + j);
		BiclusterList_valid.add(new Boolean(true));
		// valid_biclusters++;
	}

	public void addBiclustersName(int whichBCing) {
		int j = biclusterList.size() - 1;
		String wBC;
		switch (whichBCing) {
		case 0:
			wBC = "BiMax";
			break;
		case 1:
			wBC = "ISA";
			break;
		case 2:
			wBC = "CC";
			break;
		case 3:
			wBC = "xMotif";
			break;
		case 4:
			wBC = "OPSM";
			break;
		default:
			wBC = "";
			break;
		}
		BiclusterList_names.add("All biclusters / " + wBC + " /, L." + j);
		BiclusterList_valid.add(new Boolean(true));
		valid_biclusters++;
	}

	/** Add <code>bcs</code> list to the list of clusters in the dataset. */
	public void addClusters(LinkedList bcs) {
		clusterList.add(bcs);
		// valid_clusters++;
		// l_valid.add(new Integer(1));
	}

	public void addClustersPreprocessOption(PreprocessOption po) {
		preprocessOptionsList_cluster.add(po);
	}

	/** Name the new bicluster list in the dataset. */
	public void addClustersName() {
		int j = clusterList.size() - 1;
		ClusterList_names.add("All clusters, C." + j);
		ClusterList_valid.add(new Boolean(true));
		// valid_clusters++;
	}

	public void addClustersName(int whichBCing) {
		int j = clusterList.size() - 1;
		String wBC;
		switch (whichBCing) {
		case 0:
			wBC = "HCL";
			break;
		case 1:
			wBC = "K-means";
			break;
		default:
			wBC = "";
			break;
		}
		ClusterList_names.add("All clusters / " + wBC + " /, C." + j);
		ClusterList_valid.add(new Boolean(true));
		valid_clusters++;
	}

	/* Add <code>bcs</code> list to the list of search results in the dataset. */
	void addSearch(LinkedList bcs) {
		searchList.add(bcs);
	}

	/* Name the new search results list in the dataset. */
	void addSearchNames(int l, int lidx, int ni) {
		if (l == BICLUSTER_LIST)
			SearchList_names.add("Search result of L" + lidx + ", S." + ni);
		else if (l == CLUSTER_LIST)
			SearchList_names.add("Search result of C" + lidx + ", S." + ni);
		else if (l == SEARCH_LIST)
			SearchList_names.add("Search result of S" + lidx + ", S." + ni);
		else
			// FILTER_LIST
			SearchList_names.add("Search result of F" + lidx + ", S." + ni);
		SearchList_valid.add(new Boolean(true));
		valid_searches++;
	}

	/*
	 * Add <code>bcs</code> list to the list of analysis results in the
	 * dataset.
	 */
	void addAnalysis(LinkedList bcs) {
		analysisList.add(bcs);
	}

	/* Name the new analysis result in the dataset. */
	void addAnalysisNames(int l, int lidx, int ni) {
		if (l == BICLUSTER_LIST)
			AnalysisList_names.add("Analysis result of L" + lidx + ", A." + ni);
		else if (l == CLUSTER_LIST)
			AnalysisList_names.add("Analysis result of C" + lidx + ", A." + ni);
		else if (l == SEARCH_LIST)
			AnalysisList_names.add("Analysis result of S" + lidx + ", A." + ni);
		else
			// FILTER_LIST
			AnalysisList_names.add("Analysis result of F" + lidx + ", A." + ni);
		AnalysisList_valid.add(new Boolean(true));
	}

	/* Add <code>bcs</code> list to the list of filter results in the dataset. */
	void addFilter(LinkedList bcs) {
		filterList.add(bcs);
	}

	/* Name the new filter results list in the dataset. */
	void addFilterNames(int l, int lidx, int ni) {
		if (l == BICLUSTER_LIST)
			FilterList_names.add("Filter result of L" + lidx + ", F." + ni);
		else if (l == CLUSTER_LIST)
			FilterList_names.add("Filter result of C" + lidx + ", F." + ni);
		else if (l == SEARCH_LIST)
			FilterList_names.add("Filter result of S" + lidx + ", F." + ni);
		else
			// FILTER_LIST
			FilterList_names.add("Filter result of F" + lidx + ", F." + ni);

		FilterList_valid.add(new Boolean(true));
		valid_filters++;
	}

	// ////////////////////////////////////////////////////////////////////////////

	public int[] getOriginatingList(int list, int list_idx) {

		if (list == 1) { // SEARCH?
			for (int i = 0; i < SearchList_names.size(); i++) {
				int[] lai = BicatUtil
						.getListAndIdxTree((String) SearchList_names.get(i));
				if (lai[3] == list_idx)
					return lai;
			}
			return null;
		} else if (list == 2) { // FILTER?
			for (int i = 0; i < FilterList_names.size(); i++) {
				int[] lai = BicatUtil
						.getListAndIdxTree((String) FilterList_names.get(i));
				if (lai[3] == list_idx)
					return lai;
			}
			return null; // ... should not happen ... (if NULL -> get an
			// error Message!)
		} else { // if(list == 3) { // ANALYSIS?
			// WIRD WAHRSCHEINLICH NICHT/NIE BENUTZT (boh, ostaje da se vidi.)!
			for (int i = 0; i < AnalysisList_names.size(); i++) {
				int[] lai = BicatUtil
						.getListAndIdxTree((String) AnalysisList_names.get(i));
				if (lai[3] == list_idx)
					return lai;
			}
			return null;
		}
	}

	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Update the management information for the Filter results of the dataset.
	 * Adds the last filtering result, and updates the Filter list of names.
	 * 
	 */
	public void updateFilterBiclustersLists(LinkedList filter, int list, int idx) {

		filterList.add(new LinkedList(filter));
		f2list.put(new Integer(filterList.size() - 1), new Integer(list)); // filter
		// for
		// which
		// kind
		// of
		// list
		// (L,
		// F,
		// S)
		// ..
		// oder
		// C
		// (as
		// of
		// 16.03.2005)

		if (list == BICLUSTER_LIST) {
			f2l.add(new Integer(idx)); // FILTER for which idx of the L list,
			// say
			f2idx.put(new Integer(filterList.size() - 1), new Integer(f2l
					.size() - 1));
			addFilterNames(list, idx, filterList.size() - 1); // f2l.size()-1);

			f2l_realIdx.put("" + (f2l.size() - 1) + "," + idx + "",
					new Integer(filterList.size() - 1));
		} else if (list == CLUSTER_LIST) {
			f2c.add(new Integer(idx)); // FILTER for which idx of the C list,
			// say
			f2cidx.put(new Integer(filterList.size() - 1), new Integer(f2c
					.size() - 1));
			addFilterNames(list, idx, filterList.size() - 1);

			f2c_realIdx.put("" + (f2c.size() - 1) + "," + idx + "",
					new Integer(filterList.size() - 1));
		} else if (list == SEARCH_LIST) {
			f2s.add(new Integer(idx));
			f2idx.put(new Integer(filterList.size() - 1), new Integer(f2s
					.size() - 1));
			addFilterNames(list, idx, filterList.size() - 1); // f2s.size()-1);

			f2s_realIdx.put("" + (f2s.size() - 1) + "," + idx + "",
					new Integer(filterList.size() - 1));
		} else if (list == FILTER_LIST) { // { /* made on filter list! */
			f2f.add(new Integer(idx));
			f2idx.put(new Integer(filterList.size() - 1), new Integer(f2f
					.size() - 1));
			addFilterNames(list, idx, filterList.size() - 1); // f2f.size()-1);

			f2f_realIdx.put("" + (f2f.size() - 1) + "," + idx + "",
					new Integer(filterList.size() - 1));
		} else {

			System.out.println("ERROR/BUG: Unknown BC list - Filtering on!");

		}
	}

	/**
	 * Update the management information for the Search results of the dataset.
	 * Adds the last search result, and updates the Search list of names.
	 * 
	 */
	public void updateSearchBiclustersLists(LinkedList search, int list, int idx) {

		searchList.add(new LinkedList(search));
		s2list.put(new Integer(searchList.size() - 1), new Integer(list));

		if (list == BICLUSTER_LIST) {
			s2l.add(new Integer(idx));
			s2idx.put(new Integer(searchList.size() - 1), new Integer(s2l
					.size() - 1));
			addSearchNames(list, idx, searchList.size() - 1); // s2l.size()-1);

			s2l_realIdx.put("" + (s2l.size() - 1) + "," + idx + "",
					new Integer(searchList.size() - 1));
		} else if (list == CLUSTER_LIST) {
			s2c.add(new Integer(idx));
			s2cidx.put(new Integer(searchList.size() - 1), new Integer(s2c
					.size() - 1));
			addSearchNames(list, idx, searchList.size() - 1); // s2l.size()-1);

			s2c_realIdx.put("" + (s2c.size() - 1) + "," + idx + "",
					new Integer(searchList.size() - 1));
		} else if (list == SEARCH_LIST) {
			s2s.add(new Integer(idx));
			s2idx.put(new Integer(searchList.size() - 1), new Integer(s2s
					.size() - 1));
			addSearchNames(list, idx, searchList.size() - 1);

			s2s_realIdx.put("" + (s2s.size() - 1) + "," + idx + "",
					new Integer(searchList.size() - 1));
		} else { /* made on filter list! */
			s2f.add(new Integer(idx));
			s2idx.put(new Integer(searchList.size() - 1), new Integer(s2f
					.size() - 1));
			addSearchNames(list, idx, searchList.size() - 1);

			s2f_realIdx.put("" + (s2f.size() - 1) + "," + idx + "",
					new Integer(searchList.size() - 1));
		}
	}

	/**
	 * Update the management information for the Analysis results of the
	 * dataset. Adds the last analysis result, and updates the Analysis list of
	 * names.
	 * 
	 */
	public void updateAnalysisBiclustersLists(HashMap analysis, int list,
			int idx) {

		analysisList.add(analysis);
		a2list.put(new Integer(analysisList.size() - 1), new Integer(list));

		if (list == BICLUSTER_LIST) {
			a2l.add(new Integer(idx));
			a2idx.put(new Integer(analysisList.size() - 1), new Integer(a2l
					.size() - 1));
			addAnalysisNames(list, idx, analysisList.size() - 1);

			a2l_realIdx.put("" + (a2l.size() - 1) + "," + idx + "",
					new Integer(analysisList.size() - 1));
		}
		if (list == CLUSTER_LIST) {
			a2c.add(new Integer(idx));
			a2cidx.put(new Integer(analysisList.size() - 1), new Integer(a2c
					.size() - 1));
			addAnalysisNames(list, idx, analysisList.size() - 1);

			a2c_realIdx.put("" + (a2c.size() - 1) + "," + idx + "",
					new Integer(analysisList.size() - 1));
		} else if (list == SEARCH_LIST) {
			a2s.add(new Integer(idx));
			a2idx.put(new Integer(analysisList.size() - 1), new Integer(a2s
					.size() - 1));
			addAnalysisNames(list, idx, analysisList.size() - 1);

			a2s_realIdx.put("" + (a2s.size() - 1) + "," + idx + "",
					new Integer(analysisList.size() - 1));
		} else { /* made on filter list! */
			a2f.add(new Integer(idx));
			a2idx.put(new Integer(analysisList.size() - 1), new Integer(a2f
					.size() - 1));
			addAnalysisNames(list, idx, analysisList.size() - 1);

			a2f_realIdx.put("" + (a2f.size() - 1) + "," + idx + "",
					new Integer(analysisList.size() - 1));
		}
	}

}
