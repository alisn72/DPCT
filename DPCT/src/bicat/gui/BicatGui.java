package bicat.gui;

import bicat.biclustering.Dataset;
import bicat.gui.window.*;
import bicat.postprocessor.Postprocessor;
import bicat.preprocessor.FileOffsetException;
import bicat.preprocessor.PreprocessOption;
import bicat.preprocessor.Preprocessor;
import bicat.run_machine.*;
import bicat.util.BicatError;
import bicat.util.BicatUtil;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author D. Frick, A. Prelic
 * @version 1.0
 *
 **/

/**
 * <code>BicatGui</code> is the central manager for the Graphical Interface,
 * datasets management, coordination of I/O operations.
 * 
 */
public class BicatGui extends JFrame implements ActionListener,
		TreeSelectionListener, MouseListener, ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static int DEFAULT_TOLERABLE = 1000;

	/* Matrix visualization: whether add contrast? */
	public static boolean enlargeContrast = true;

	public static float CONTRAST_VALUE = (float) 0; // 1; // 2; // 0.50;

	/* Current working directory */
	public static String currentDirectoryPath;

	/* Print debug information */
	public static boolean debug = false;

	static ImageIcon stop_icon;

	static ImageIcon bc_icon;

	public static Vector[] currentBiclusterSelection; // for selecting a BC at once

	/**
	 * Used to read data from files and perform basic processing steps on said
	 * raw data
	 */
	public static Preprocessor pre;

	/** Used to perform search/sort/filter/analysis operations */
	public static Postprocessor post;

	/** Used to perform biclustering: RunMachine for BiMax */
	public static RunMachine_BiMax runMachineBiMax;

	/** Used to perform biclustering: RunMachine for Cheng and Church (2000) */
	public static RunMachine_CC runMachine_CC;

	/**
	 * Used to perform biclustering: RunMachine for Iterative Signature
	 * Algorithm (ISA, 2002)
	 */
	public static RunMachine_ISA runMachine_ISA;

	/** Used to perform biclustering: RunMachine for xMotifs (2003) */
	public static RunMachine_XMotifs runMachine_XMotifs;

	/** Used to perform biclustering: RunMachine for OPSM */
	public static RunMachine_OPSM runMachineOPSM;

	/** Used to perform clustering: RunMachine for Hierarchical Clustering */
	public static RunMachine_HCL runMachineHCL;

	/** Used to perform clustering: RunMachine for K-Means Clustering */
	public static RunMachine_KMeans runMachineKMeans;

	// ....

	public static Dataset currentDataset = null;

	public static int currentDatasetIdx = 0;

	public static LinkedList datasetList = null;

	/** Keep the last Preprocessing Option aktuell ! */
	public static PreprocessOption preprocessOptions;

	/**
	 * 100504: will generalize this!!! .... 2 if files have an offset that must
	 * be ignored (e.g. pathway IDs)
	 */
	public static int fileOffset;

	/**
	 * 1 if no log operation is desired in the preprocessing step, 2 or 10 for
	 * respective base logarithm.
	 */
	public static int logBaseSetting;

	/**
	 * Applied to the preprocessed data to get the discretized matrix for
	 * biclustering
	 */
	public static float discretizationThreshold;

	/** True id preprocessing has been performed */
	public static boolean preprocessComplete;

	static boolean discretizeComplete;

	/** A scroll pane for display of biclusters and other things in a tree. */
	public static JScrollPane listScrollPane;

	/** A scroll pane for matrix display. */
	public static JScrollPane matrixScrollPane;

	/** A scroll pane for expression graph graph(ik) display. */
	public static JScrollPane graphScrollPane;

	/** A scroll pane for visualizing GenePair Analysis results. */
	public static JScrollPane otherScrollPane;

	/** Used to draw the data matrix and display biclusters. */
	public static PicturePane pp;

	/** Used to draw expression graph(ik). */
	public static GraphicPane gp;

	/** Used to display the GPA table (and, eventually the graph) */
	public static AnalysisPane op;

	/** @todo would like to "personalize" it. */
	public static PopupMenu pm;

	/**
	 * Contains whatever dataset that is currently being displayed (can be
	 * non-normalized data).
	 */
	public static float[][] rawData;

	/**
	 * Contains various display options, list of biclusters and list of search
	 * results, if available
	 */
	public static JTree tree;

	/** Root node of <code>tree</code>. */
	public static DefaultMutableTreeNode top;

	/** Model for <code>tree</code>. */
	public static DefaultTreeModel treeModel;

	/** Tree path to node that represents original data display. */
	public static TreePath originalPath;

	/** Tree path to node that represents display of preprocessed data. */
	public static TreePath preprocessedPath;

	/** Provides information in the matrix display panel. */
	public static JLabel matrixInfoLabel;

	/** Provides informations in the expression graph(ik) panel. */
	public static JLabel graphInfoLabel;

	/** Provides information in the analysis panel. */
	public static JLabel otherInfoLabel;

	public static boolean dataAndChipInformationLoaded;

	// ....

	public static int gpDistanceThreshold;

	public static float hammingDistance; // for visualization purposes! (for

	// now, symmetric! = in 2
	// dimensions.)

	public static boolean preprocessExtended;

	public static boolean processExtended;

	static boolean discretizeExtended;

	public static JMenu preprocessOptionsMenu = null;

	public static Vector preprocessLabels = null;

	public static JMenu labelMenu = null;

	public static JMenu saveAnalysisMenu = null;

	public static JMenu saveBiclusterMenu = null;

	public static JMenu saveClusterMenu = null;

	public static JMenu saveSearchMenu = null;

	public static JMenu saveFilterMenu = null;

	JMenuItem loadMenuItem;

	public static Vector labels = null;

	static int currentLabel = -1;

	// ===========================================================================
	public void itemStateChanged(ItemEvent e) {
		JMenuItem source = (JMenuItem) e.getSource();
		String s = "Item event detected."
				+ "\n"
				+ "\tEvent source: "
				+ source.getText()
				+ " (an instance of "
				+ getClassName(source)
				+ ")"
				+ "\n"
				+ "\tNew state: "
				+ ((e.getStateChange() == ItemEvent.SELECTED) ? "selected"
						: "unselected");
		// different from the sample:
		System.out.println(s);
	}

	// ===========================================================================
	protected String getClassName(Object o) {
		String classString = o.getClass().getName();
		int dotIndex = classString.lastIndexOf(".");
		return classString.substring(dotIndex + 1);
	}

	// ===========================================================================
	/**
	 * For <code>MouseListener</code> interface, called when mouse is clicked
	 * inside <code>PicturePane</code>
	 * 
	 */
	public void mouseClicked(MouseEvent e) {

		if (false == pre.dataReady())
			matrixInfoLabel.setText(" Load data files for analysis");

		else {
			if (PicturePane.IN_MATRIX == pp.clickedRegion(e.getX(), e.getY())) { // click
				// in
				// matrix,
				// so
				// update
				// info
				// label

				Point clickedRect = pp
						.getRectFromCoordinate(e.getX(), e.getY());
				String geneName, chipName;

				if (null == clickedRect) {
					geneName = null;
					chipName = null;
				} else if (rawData[0].length == pre.getWorkingChipCount()) { // if
					// working
					// with
					// data
					// stripped
					// of
					// control
					// chips
					geneName = currentDataset.getGeneName((int) clickedRect
							.getY());
					chipName = currentDataset
							.getWorkingChipName((int) clickedRect.getX());
				} else { // if working with data that includes control chips
					geneName = currentDataset.getGeneName((int) clickedRect
							.getY());
					chipName = currentDataset.getChipName((int) clickedRect
							.getX());
				}

				if ((null == geneName) || (null == chipName))
					matrixInfoLabel
							.setText(" Click on a sample to get gene and chip names");
				else
					matrixInfoLabel.setText(" Gene: " + geneName + "\tChip: "
							+ chipName);
			}

			else if (PicturePane.IN_GENES == pp.clickedRegion(e.getX(), e
					.getY())) { // click on gene names, update selections
				pp.geneSelected(pp.getGeneNameIndex(e.getY()));
				refreshGraphicPanel();
				pp.repaint();
				
			}

			else if (PicturePane.IN_CHIPS == pp.clickedRegion(e.getX(), e
					.getY())) { // click on chip names, update selections
				pp.chipSelected(pp.getChipNameIndex(e.getX()));
				pp.repaint();
			}

			else if (PicturePane.OUTSIDE == pp
					.clickedRegion(e.getX(), e.getY())) { // click in clear
				// area, clear
				// selections
				pp.clearSelections();
				pp.repaint();
				gp.setGraphDataList(null, null, null);
			}

			else
				// click wasn't in a valid clickable region
				matrixInfoLabel
						.setText(" Click on a sample to get gene and chip names");
		}
	}

	// ===========================================================================
	/**
	 * For <code>MouseListener</code> interface, called when mouse is
	 * depressed inside <code>PicturePane</code>
	 */
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * For <code>MouseListener</code> interface, called when mouse is released
	 * inside <code>PicturePane</code>
	 */
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * For <code>MouseListener</code> interface, called when mouse enters
	 * <code>PicturePane</code>
	 */
	public void mouseEntered(MouseEvent e) {
		if (preprocessComplete == false)
			matrixInfoLabel.setText(" Load data files for analysis");
		else
			matrixInfoLabel
					.setText(" Click on a sample to get gene and chip names");
	}

	/**
	 * For <code>MouseListener</code> interface, called when mouse exits
	 * <code>PicturePane</code>
	 */
	public void mouseExited(MouseEvent e) {
		if (pre.dataReady() == false)
			matrixInfoLabel.setText(" Load data files for analysis");
		else if (anyBiclustersAvailable() == false)
			matrixInfoLabel.setText(" Run bi/clustering algorithm on data");
		else
			matrixInfoLabel
					.setText(" Select biclusters from left panel to display them");
	}

	// ===========================================================================
	/**
	 * For <code>TreeSelectionListener</code> interface, called when a tree is
	 * selected
	 */
	public void valueChanged(TreeSelectionEvent e) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		if (node == null)
			return;

		Object maybeBC = node.getUserObject();

		// **********************************************************************
		// //
		if ("Original data".equals(node.toString())) { // check which tree node
			// has been selected

			// get the correct dataset, and display it:
			TreePath tp = tree.getSelectionPath();
			int dataset_idx = BicatUtil.getDataset((tp.getPathComponent(1))
					.toString())[0];
			updateCurrentDataset(dataset_idx);

			if (null == currentDataset.getOrigData())
				JOptionPane.showMessageDialog(this, "No data loaded", "Error",
						JOptionPane.ERROR_MESSAGE);
			else {
				// display original data
				setData(currentDataset.getOrigData()); // get the data to
				// visualize on the PP
				pp.setData(rawData);
				readjustPictureSize();
				pp.repaint();
				updateColumnHeadersMenu();
			}
		}

		// **********************************************************************
		// //
		else if ("Preprocessed data".equals(node.toString())) {

			// get the correct dataset, and display it:
			TreePath tp = tree.getSelectionPath();
			int dataset_idx = BicatUtil.getDataset((tp.getPathComponent(1))
					.toString())[0];
			updateCurrentDataset(dataset_idx);

			if (null == currentDataset.getPreData()) {
				JOptionPane.showMessageDialog(this, "Data not preprocessed",
						"Error", JOptionPane.ERROR_MESSAGE);
				tree.setSelectionPath(preprocessedPath); // originalPath);
			} else {
				// display processed data
				setData(currentDataset.getPreData()); // pre.getPreprocessedData());
				pp.setData(rawData);
				readjustPictureSize();
				pp.repaint();
				updateColumnHeadersMenu();
			}
		}

		// **********************************************************************
		// //
		else if ("Discretized data".equals(node.toString())) {

			// get the correct dataset, and display it:
			TreePath tp = tree.getSelectionPath();
			int dataset_idx = BicatUtil.getDataset((tp.getPathComponent(1))
					.toString())[0];
			updateCurrentDataset(dataset_idx);

			if (null == currentDataset.getDiscrData()) {
				JOptionPane.showMessageDialog(this, "Data not discretized",
						"Error", JOptionPane.ERROR_MESSAGE);
				tree.setSelectionPath(originalPath);
			}

			else {
				// display binary matrix
				setData(currentDataset.getVisualDiscrData(currentDataset
						.isExtended()));
				pp.setData(rawData);
				readjustPictureSize();
				pp.repaint();
				matrixInfoLabel
						.setText("Threshold: " + discretizationThreshold);
				updateColumnHeadersMenu();
			}
		}

		// **********************************************************************
		// //
		else if (Pattern.matches("DataSet \\d*", node.toString())) {

			// if(debug) System.out.println("D: selected node
			// \""+node.toString()+"\"");

			// get the correct dataset, and display it:
			TreePath tp = tree.getSelectionPath();
			int dataset_idx = BicatUtil.getDataset((tp.getPathComponent(1))
					.toString())[0];
			updateCurrentDataset(dataset_idx);

			// display original data
			setData(currentDataset.getOrigData()); // get the data to visualize
			// on the PP
			pp.setData(rawData);
			readjustPictureSize();
			pp.repaint();
			updateColumnHeadersMenu();
		}

		// **********************************************************************
		// //
		else if ("Data Display".equals(node.toString())) {

			// get the correct dataset, and display it:
			TreePath tp = tree.getSelectionPath();
			int dataset_idx = BicatUtil.getDataset((tp.getPathComponent(1))
					.toString())[0];
			updateCurrentDataset(dataset_idx);

			// display original data
			setData(currentDataset.getOrigData()); // get the data to visualize
			// on the PP
			pp.setData(rawData);
			readjustPictureSize();
			pp.repaint();
			updateColumnHeadersMenu();
		}

		// **********************************************************************
		// //
		else if// ( "Display".equals(node.toString())
		// || (
		("Bicluster results".equals(node.toString())
				|| "Cluster results".equals(node.toString())
				|| "Filter results".equals(node.toString())
				|| "Search results".equals(node.toString())
				|| "Analysis results".equals(node.toString())) {

			// if(debug) System.out.println("D: selected node
			// \""+node.toString()+"\"");

			// Always display the correct dataset...
			// get the correct dataset, and display it:
			TreePath tp = tree.getSelectionPath();
			int dataset_idx = BicatUtil.getDataset((tp.getPathComponent(1))
					.toString())[0];
			updateCurrentDataset(dataset_idx);

			// display original data
			setData(currentDataset.getOrigData()); // get the data to visualize
			// on the PP
			pp.setData(rawData);
			readjustPictureSize();
			pp.repaint();
			updateColumnHeadersMenu();
		}

		// **********************************************************************
		// //
		else if (Pattern.matches("Analysis result of .*", node.toString())) {

			// get the correct dataset, and display it:
			TreePath tp = tree.getSelectionPath();
			int dataset_idx = BicatUtil.getDataset((tp.getPathComponent(1))
					.toString())[0];
			updateCurrentDataset(dataset_idx);

			updateColumnHeadersMenu();
			// display original data
			setData(currentDataset.getOrigData()); // get the data to visualize
			// on the PP
			pp.setData(rawData);
			readjustPictureSize();
			pp.repaint();

			// ....
			int idx = BicatUtil.getAnalysisNodeIdx(node.toString());

			/*
			 * if(debug) { // System.out.println("D: "+e.getPath().toString()); //
			 * System.out.println("D: depth: " + node.getDepth()); TreeNode[] tn =
			 * node.getPath(); for (int p = 0; p < tn.length; p++) {
			 * System.out.println("D: node in path: " + tn[p].toString()); } }
			 */

			TreeNode[] tn = node.getPath();
			int data = BicatUtil.getDataset(tn[1].toString())[0];

			// JOS UVIJEK NIJE NAJBOLJE: moram skontati koji je ovo path (which
			// dataset, and not Last in the datasetList)?
			refreshAnalysisPanel(data, idx);

			// otherInfoLabel.setText(""); // trick, just to repaint the Pane
			// (ruzno.)
		}

		// **********************************************************************
		// //
		else if ("bicat.biclustering.Bicluster".equals(maybeBC.getClass()
				.getName())) { // a bicluster node has been selected

			// get the correct dataset, and display it:

			TreePath tp = tree.getSelectionPath();
			int dataset_idx = BicatUtil.getDataset((tp.getPathComponent(1))
					.toString())[0];
			updateCurrentDataset(dataset_idx);
			updateColumnHeadersMenu();

			// display original data
			/*
			 * setData(currentDataset.getOrigData()); // get the data to
			 * visualize on the PP pp.setData(rawData); readjustPictureSize();
			 * pp.repaint();
			 */

			// *** visualize the selected bicluster in the upper left corner
			/*
			 * if(rawData[0].length != pre.getWorkingChipCount()) { // if the
			 * user is viewing original data with control chips
			 * setData(pre.getPreprocessedData()); pp.setData(rawData); }
			 */
			// Q: da li da ostavim ovako?
			setData(currentDataset.getPreData());
			pp.setData(rawData);

			bicat.biclustering.Bicluster selection = (bicat.biclustering.Bicluster) (node
					.getUserObject());
			currentBiclusterSelection = pp.setTranslationTable(selection);

			matrixScrollPane.repaint();
			readjustPictureSize();
			pp.repaint();

			// *** in bg, visualize the gene expression profiles graphik (with
			// discr. thr. plotted)

			int dataset = BicatUtil.getDataset((tp.getPathComponent(1))
					.toString())[0];
			int[] listAndIdx = BicatUtil.getListAndIdxTree(tp.getPathComponent(
					3).toString());

			if (BicatGui.debug) {
				System.out.println("Debug: "
						+ tp.getPathComponent(3).toString());
				System.out.println("Debug: dataset = " + dataset + ", l_0 = "
						+ listAndIdx[0] + // what list selected (L, F, S) ...
						// oder C (clusters, as of
						// 16.03.2005)
						", l_1 = " + listAndIdx[1] + // what was the original
						// list (if any) (L, F,
						// S)_orig
						", l_2 = " + listAndIdx[2] + // idx original list
						", l_3 = " + listAndIdx[3]); // idx list selected
			}

			Dataset br = ((Dataset) datasetList.get(dataset));

			while (listAndIdx[1] != 0 && listAndIdx[1] != 3) { // what is the
				// original BC
				// list?
				// if(debug) System.out.println("Before: "+listAndIdx[0]+",
				// "+listAndIdx[1]+", "+listAndIdx[2]+", "+listAndIdx[3]);
				listAndIdx = br
						.getOriginatingList(listAndIdx[1], listAndIdx[2]);
				// if(debug) System.out.println("After: "+listAndIdx[0]+",
				// "+listAndIdx[1]+", "+listAndIdx[2]+", "+listAndIdx[3]);
			}
			if (debug)
				System.out.println("Original BC list is " + listAndIdx[1]
						+ ", " + listAndIdx[2]);

			double discretizationThr;
			if (listAndIdx[1] == 0)
				discretizationThr = br.getPreprocessOptions(listAndIdx[2]).discretizationThreshold;
			else
				discretizationThr = br
						.getPreprocessOptionsClusters(listAndIdx[2]).discretizationThreshold;

			pp.setTranslationTable(selection);
			// pp.setTranslationTable(selection, rawData_for_GP);

			matrixScrollPane.repaint();
			readjustPictureSize();
			pp.repaint();
			pp.biclusterSelected(currentBiclusterSelection[1],
					currentBiclusterSelection[2]);

			gp.setGraphDataList(currentBiclusterSelection[0],
					currentBiclusterSelection[1], currentBiclusterSelection[2]);
			
			refreshGraphicPanel(discretizationThr);

			// *** visualize the list of gene IDs in the Analysis Pane

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < currentBiclusterSelection[1].size(); i++)
				sb.append(currentDataset
						.getGeneName(((Integer) currentBiclusterSelection[1]
								.get(i)).intValue())
						+ "\n");

			op.repaint();
			op.removeAll();
			JTextPane tpane = new JTextPane();
			tpane.setText(sb.toString());
			tpane.setAlignmentX((float) 0.0);
			op.add(tpane, BorderLayout.WEST);

		} else if (node.isLeaf()) {
			if (debug)
				System.out.println("D: Leaf node has been selected: \""
						+ (String) maybeBC + "\"");
		} else {

			if (BicatUtil.isListOfBCs((String) maybeBC)) {
				LinkedList list = BicatUtil.getListOfBiclusters(tree
						.getSelectionPath(), datasetList);
				if (list.size() < DEFAULT_TOLERABLE)
					gp.visualizeAll(list, rawData_for_GP, gp.DEFAULT_X_TABLE); 
			}

			// check if some Bicluster List has been selected
			if (debug) {
				String node_str = (String) maybeBC;
				System.out
						.println("D (final else clause, BicatGui.ValueChanged): selected node \""
								+ node_str + "\"");
			}
		}
	}

	// ************************************************************************
	// //
	// * * //
	// * Central: datasets management * //
	// * * //
	// ************************************************************************
	// //

	/** Check if any Biclustering results are available. */
	public boolean anyBiclustersAvailable() {
		for (int i = 0; i < datasetList.size(); i++) {
			Dataset br = (Dataset) datasetList.get(i);
			if (br.getBiclusters().size() + br.getClusters().size() > 0)
				return true;
		}
		return false;
	}

	/**
	 * Check if any Valid Bicluster / Cluster / Filter / Search list is
	 * available.
	 */
	public boolean anyValidListAvailable() {
		for (int i = 0; i < datasetList.size(); i++) {
			Dataset BcR = (Dataset) datasetList.get(i);
			if ((BcR.valid_biclusters + BcR.valid_clusters + BcR.valid_filters + BcR.valid_searches) > 0)
				return true;
		}
		return false;
	}

	/** Obtain the names of all lists of biclusters. */
	public Vector getListNamesAll() {

		Vector vec = new Vector();
		vec.add("Choose bicluster list...");

		for (int i = 0; i < datasetList.size(); i++) {
			Dataset br = (Dataset) datasetList.get(i);

			Vector bcN = br.getBiclustersNames();
			Vector bV = br.getBiclustersValid();
			for (int j = 0; j < bcN.size(); j++)
				if (((Boolean) bV.get(j)).booleanValue())
					vec.add("D" + i + " " + (String) bcN.get(j));

			Vector cN = br.getClustersNames();
			Vector cV = br.getClustersValid();
			for (int j = 0; j < cN.size(); j++)
				if (((Boolean) cV.get(j)).booleanValue())
					vec.add("D" + i + " " + (String) cN.get(j));

			Vector sN = br.getSearchNames();
			Vector sV = br.getSearchValid();
			for (int j = 0; j < sN.size(); j++)
				if (((Boolean) sV.get(j)).booleanValue())
					vec.add("D" + i + " " + (String) sN.get(j));

			Vector fN = br.getFiltersNames();
			Vector fV = br.getFiltersValid();
			for (int j = 0; j < fN.size(); j++)
				if (((Boolean) fV.get(j)).booleanValue())
					vec.add("D" + i + " " + (String) fN.get(j));
		}

		if (debug)
			System.out.println("D: Size of all datasets list names = "
					+ vec.size());

		return vec;
	}

	/** Obtain the names of all lists of biclusters of the dataset <code>d</code>. */
	// Q: Gdje ovo koristim?
	public Vector getListNames(int d) {
		Dataset data = (Dataset) datasetList.get(d);

		Vector vec = new Vector();
		vec.addAll(data.getBiclustersNames());
		vec.addAll(data.getClustersNames());
		vec.addAll(data.getSearchNames());
		vec.addAll(data.getFiltersNames());

		if (debug)
			System.out
					.println("D: Size of all biclusters lists (dataset d) names = "
							+ vec.size());

		return vec;
	}

	/** Obtain the names of all datasets. */
	public Vector getListDatasets() {
		Vector vec = new Vector();
		for (int i = 0; i < datasetList.size(); i++)
			vec.add("Dataset " + i);
		if (debug)
			System.out.println("D: Size of all datasets list names = "
					+ vec.size());
		return vec;
	}

	/** Add dataset to the central management list (is done after loading data). */
	public void addDataset(float[][] data, boolean withCtrl, int[] dep,
			Vector gNames, Vector chipNames, Vector workCNames,
			Vector colLabels, Vector colInfo) {
		datasetList.add(new Dataset(data, withCtrl, dep, gNames, chipNames,
				workCNames, colLabels, colInfo));
		updateCurrentDataset(datasetList.size() - 1);

		if (debug)
			System.out.println("New dataset added [" + datasetList.size()
					+ "]. ");
	}

	public void addDataset(float[][] data, float[][] dataPre,
			int[][] dataDiscr, Vector gN, Vector cN, Vector wCN, Vector cL,
			Vector cI) {
		datasetList.add(new Dataset(data, dataPre, dataDiscr, gN, cN, wCN, cL,
				cI));
		updateCurrentDataset(datasetList.size() - 1);

		if (debug)
			System.out.println("New dataset added [" + datasetList.size()
					+ "]. ");
	}

	/**
	 * Add bicluster results to the dataset <code>BcR</code> (done after
	 * running biclustering).
	 */
	public void addBiclusters(Dataset BcR, LinkedList newBcs, int wBCing) {
		BcR.addBiclusters(newBcs);
		BcR.addBiclustersName(wBCing);
	}

	public void addPreprocessOptions(Dataset BcR, PreprocessOption po) {
		BcR.addPreprocessOption(po);
		updateCurrentDataset(BcR);
	}

	/**
	 * Add cluster results to the dataset <code>BcR</code> (done after running
	 * clustering).
	 */
	public void addClusters(Dataset BcR, LinkedList newBcs, int wBCing) {
		System.out.println("LinkedList newBcs: "+newBcs.toString());
		BcR.addClusters(newBcs);
		BcR.addClustersName(wBCing);
	}

	public void addClustersPreprocessOptions(Dataset BcR, PreprocessOption po) {
		BcR.addClustersPreprocessOption(po);
		updateCurrentDataset(BcR);
	}

	/** Set the current dataset to <code>datasetList.get(x)</code>. */
	public void updateCurrentDataset(int x) {
		if (debug)
			System.out.println("Update the \"current\" dataset! (" + x + ")");
		currentDataset = (Dataset) datasetList.get(x);
		currentDatasetIdx = x;
	}

	/** Set the current dataset to <code>br</code>. */
	public void updateCurrentDataset(Dataset br) {
		currentDataset = br;
	}

	// ************************************************************************
	// //
	// * * //
	// * Action Manager * //
	// * * //
	// ************************************************************************
	// //

	/**
	 * For <code>ActionListener</code> interface, called when an action
	 * command is performed (usually through the pull down menus)
	 * 
	 */
	public void actionPerformed(ActionEvent e) {

		try {

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			if (ActionManager.MAIN_QUIT.equals(e.getActionCommand()))
				System.exit(0);

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (Pattern.matches("\\d*", (CharSequence) (e
					.getActionCommand()))
					&& (new Integer(e.getActionCommand())).intValue() >= 0
					&& (new Integer(e.getActionCommand())).intValue() < pp.owner.pre.labels
							.size()) {

				// mozda bih trebala das ding update (dynamic menu, as the
				// datasets are being selected...)
				// auch labeled, muss false (resetted werden)
				pp.labeled = true;
				pp.labelIdx = (new Integer(e.getActionCommand())).intValue();
				pp.owner.matrixScrollPane.repaint();
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			// CHECK IF CORRECT:
			else if (Pattern.matches("save_analysis_\\d*.*", (CharSequence) (e
					.getActionCommand()))) {

				String command = (e.getActionCommand());
				String[] sp = command.split("_");
				int dIdx = (new Integer(sp[3])).intValue();
				int lIdx = (new Integer(sp[4])).intValue();

				writeGPA(writeAnalysisResults_BioLayout(((Dataset) datasetList
						.get(dIdx)).getAnalysis(lIdx))); // TO DO.
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			// CHECK IF CORRECT:
			else if (Pattern.matches("save_bicluster_results_\\d*.*",
					(CharSequence) (e.getActionCommand()))) {

				String command = (e.getActionCommand());
				String[] sp = command.split("_");
				int dIdx = (new Integer(sp[3])).intValue();
				int lIdx = (new Integer(sp[4])).intValue();

				writeBiclustersHuman(((Dataset) datasetList.get(dIdx))
						.getBiclusters(lIdx));
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			/*
			 * else if("load_bcs".equals(e.getActionCommand())) {
			 * 
			 * if(pre.discreteDataReady()) { LinkedList list = readBiclusters();
			 * if(null != list) { post.loadList(list, rawData); buildTree(); //
			 * build tree with results tree.setSelectionPath(preprocessedPath); }
			 * else JOptionPane.showMessageDialog(this, "An error occurred while
			 * loading the bicluster file"); } else
			 * JOptionPane.showMessageDialog(this, "Must load and preprocess
			 * data before loading bicluster file"); }
			 */

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (Pattern.matches("save_cluster_results_\\d*.*",
					(CharSequence) (e.getActionCommand()))) {

				String command = (e.getActionCommand());
				String[] sp = command.split("_");
				int dIdx = (new Integer(sp[3])).intValue();
				int lIdx = (new Integer(sp[4])).intValue();

				writeBiclustersHuman(((Dataset) datasetList.get(dIdx))
						.getClusters(lIdx));
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (Pattern.matches("save_search_results_\\d*.*",
					(CharSequence) (e.getActionCommand()))) {

				String command = (e.getActionCommand());

				String[] sp = command.split("_");
				int dIdx = (new Integer(sp[3])).intValue();
				int lIdx = (new Integer(sp[4])).intValue();
				writeBiclustersHuman(((Dataset) datasetList.get(dIdx))
						.getSearch(lIdx));
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (Pattern.matches("save_filter_results_\\d*.*",
					(CharSequence) (e.getActionCommand()))) {

				String command = (e.getActionCommand());
				String[] sp = command.split("_");
				int dIdx = (new Integer(sp[3])).intValue();
				int lIdx = (new Integer(sp[4])).intValue();

				writeBiclustersHuman(((Dataset) datasetList.get(dIdx))
						.getFilters(lIdx));
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (Pattern.matches("show_info_\\d*.*", (CharSequence) (e
					.getActionCommand()))) {

				String command = (e.getActionCommand());

				int idx = command.lastIndexOf("_");
				String[] sp = command.split("_");
				int dIdx = (new Integer(sp[2])).intValue();
				int lIdx = (new Integer(sp[3])).intValue();

				String rit = ((Dataset) datasetList.get(dIdx))
						.getPreprocessOptions(lIdx).toString();
				RunInfo info = new RunInfo(this, new String("L."
						+ command.substring(idx + 1)), rit);
				info.makeWindow();
				return;
			}

			/* -=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-==-=- */
			else if (ActionManager.MAIN_LOAD_TWO_INPUT_FILES.equals(e
					.getActionCommand())) {
				LoadData ldw = new LoadData(this);
				ldw.makeWindow();
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (ActionManager.MAIN_RUN_BIMAX.equals(e.getActionCommand())) {

				// at least one preprocessed dataset should be ready ...
				if (false == currentDataset.preprocessComplete())
					JOptionPane.showMessageDialog(this,
							"Load and discretize data before proceeding",
							"Error", JOptionPane.ERROR_MESSAGE);

				else {

					runMachineBiMax = new RunMachine_BiMax(this);

					ArgumentsBiMax bmxa = new ArgumentsBiMax();
					float[][] data = currentDataset.getPreData();
					int[][] discreteData = currentDataset.getDiscrData();
					// correct the discrete matrix into binary matrix (needed
					// for BiMax)
					for (int i = 0; i < discreteData.length; i++)
						for (int j = 0; j < discreteData[0].length; j++)
							if (discreteData[i][j] == -1)
								discreteData[i][j] = 1;

					bmxa.setBinaryData(discreteData);
					if (data.length == discreteData.length)
						bmxa.setExtended(false);
					else
						bmxa.setExtended(true);
					bmxa.setGeneNumber(data.length);
					bmxa.setChipNumber(data[0].length);
					
					bmxa.setDatasetIdx(currentDatasetIdx);
					bmxa.setPreprocessOptions(preprocessOptions);

					RunDialog_BiMax rbmxd = new RunDialog_BiMax(this, bmxa);
					rbmxd.makeWindow();
				}
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (ActionManager.MAIN_RUN_CC.equals(e.getActionCommand())) {

				runMachine_CC = new RunMachine_CC(this);

				// prepare the ChengChurchArguments ...
				ArgumentsCC cca = new ArgumentsCC();

				float[][] dd = currentDataset.getPreData(); // pre.getOriginalData();
				double[] data = new double[dd.length * dd[0].length];
				for (int i = 0; i < dd.length; i++)
					for (int j = 0; j < dd[0].length; j++)
						data[i * dd[0].length + j] = (double) dd[i][j];
				cca.setData(data);
				cca.setGeneNumber(dd.length);
				cca.setChipNumber(dd[0].length);

				// INTERNAL, not needed, if (randomize == 0)
				cca.setRandomize(0);
				cca.setPR1(0.5);
				cca.setPR2(0.5);
				cca.setPR3(0.5);
				// compute the logarithm, from the BicAT, sonst == 0:
				cca.setLogarithm(0);

				cca.setOutputFile("output.cc");

				cca.setDatasetIdx(currentDatasetIdx);
				cca.setPreprocessOptions(preprocessOptions);

				// Get User-Defined parameters (and run things):

				RunDialog_CC rccd = new RunDialog_CC(this, cca);
				rccd.makeWindow();
				// }
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (ActionManager.MAIN_RUN_ISA.equals(e.getActionCommand())) {

				runMachine_ISA = new RunMachine_ISA(this);

				ArgumentsISA isaa = new ArgumentsISA();

				float[][] dd = currentDataset.getPreData(); // pre.getOriginalData();
				double[] data = new double[dd.length * dd[0].length];
				for (int i = 0; i < dd.length; i++)
					for (int j = 0; j < dd[0].length; j++)
						data[i * dd[0].length + j] = (double) dd[i][j];
				isaa.setData(data);
				isaa.setGeneNumber(dd.length); // 100
				isaa.setChipNumber(dd[0].length); // 50

				// internal:
				// isaa.setMaxSize(100); // max_size == n_fix !
				isaa.setLogarithm(0);

				isaa.setOutputFile("output.isa");

				isaa.setDatasetIdx(currentDatasetIdx);
				isaa.setPreprocessOptions(preprocessOptions);

				RunDialog_ISA risad = new RunDialog_ISA(this, isaa);
				risad.makeWindow();
			}
			// }

			/* -=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (ActionManager.MAIN_RUN_XMOTIF.equals(e.getActionCommand())) {

				runMachine_XMotifs = new RunMachine_XMotifs(this);

				ArgumentsXMotifs xma = new ArgumentsXMotifs();

				float[][] dd = currentDataset.getPreData(); // pre.getOriginalData();
				double[] data = new double[dd.length * dd[0].length];
				for (int i = 0; i < dd.length; i++)
					for (int j = 0; j < dd[0].length; j++)
						data[i * dd[0].length + j] = (double) dd[i][j];
				xma.setData(data);
				xma.setGeneNumber(dd.length); // 100
				xma.setChipNumber(dd[0].length); // 50

				xma.setLogarithm(0);
				xma.setOutputFile("output.xMotifs");

				xma.setDatasetIdx(currentDatasetIdx);
				xma.setPreprocessOptions(preprocessOptions);

				RunDialog_XMotifs rxmd = new RunDialog_XMotifs(this, xma);
				rxmd.makeWindow();
				// }
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (ActionManager.MAIN_RUN_OPSM.equals(e.getActionCommand())) {

				runMachineOPSM = new RunMachine_OPSM(this);

				ArgumentsOPSM OPSMa = new ArgumentsOPSM();
				float[][] dd = currentDataset.getPreData();
				double[] data = new double[dd.length * dd[0].length];
				for (int i = 0; i < dd.length; i++)
					for (int j = 0; j < dd[0].length; j++)
						data[i * dd[0].length + j] = (double) dd[i][j];
				OPSMa.setData(data);
				OPSMa.setMyData(dd);

				OPSMa.setDatasetIdx(currentDatasetIdx);
				OPSMa.setPreprocessOptions(preprocessOptions);

				RunDialog_OPSM rOPSMd = new RunDialog_OPSM(this, OPSMa);
				rOPSMd.makeWindow();
				// }
			}
			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (ActionManager.MAIN_RUN_HCL.equals(e.getActionCommand())) {

				runMachineHCL = new RunMachine_HCL(this);

				ArgumentsHCL hcla = new ArgumentsHCL();
				float[][] dd = currentDataset.getPreData();
				double[] data = new double[dd.length * dd[0].length];
				for (int i = 0; i < dd.length; i++)
					for (int j = 0; j < dd[0].length; j++)
						data[i * dd[0].length + j] = (double) dd[i][j];
				hcla.setData(data);
				hcla.setMyData(dd);

				hcla.setDatasetIdx(currentDatasetIdx);
				hcla.setPreprocessOptions(preprocessOptions);

				RunDialog_HCL rhcld = new RunDialog_HCL(this, hcla);
				rhcld.makeWindow();
				// }
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (ActionManager.MAIN_RUN_KMEANS.equals(e.getActionCommand())) {

				runMachineKMeans = new RunMachine_KMeans(this);

				ArgumentsKMeans kma = new ArgumentsKMeans();
				float[][] dd = currentDataset.getPreData();
				double[] data = new double[dd.length * dd[0].length];
				for (int i = 0; i < dd.length; i++)
					for (int j = 0; j < dd[0].length; j++)
						data[i * dd[0].length + j] = (double) dd[i][j];
				kma.setData(data);
				kma.setMyData(dd);

				kma.setDatasetIdx(currentDatasetIdx);
				kma.setPreprocessOptions(preprocessOptions);

				RunDialog_KMeans rkmd = new RunDialog_KMeans(this, kma);
				rkmd.makeWindow();
				// }
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			/*
			 * else if("save_preprocessed".equals(e.getActionCommand())) {
			 * 
			 * if(preprocessComplete) ; //
			 * writePreprocessed(pre.getPreprocessedData()); else
			 * JOptionPane.showMessageDialog(this, "Must preprocess data
			 * first"); }
			 */

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if ("save_selected_bc".equals(e.getActionCommand())) {

				Object maybeBC = ((DefaultMutableTreeNode) tree
						.getSelectionPath().getLastPathComponent())
						.getUserObject();
				try {

					if ("bicat.biclustering.Bicluster".equals(maybeBC
							.getClass().getName())) {
						// System.out.println("is a BC");
						BiclusterInfo info = new BiclusterInfo(this,
								(bicat.biclustering.Bicluster) maybeBC);
						info.makeWindow();
						return;
					}
				} catch (Exception wee) {
					System.err.println(wee);
				}
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			/*
			 * else if("export...".equals(e.getActionCommand())) {
			 * System.out.println("To be implemented
			 * ("+e.getActionCommand()+")!"); }
			 */

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if ("preprocess".equals(e.getActionCommand())) {

				if (debug)
					System.out.println("D: dataset size is = "
							+ datasetList.size());

				if (datasetList.size() == 0 || false == pre.dataReady())
					JOptionPane.showMessageDialog(this,
							"Load input data first before proceeding");

				else {
					PreprocessData pdw = new PreprocessData(this);
					pdw.makeWindow();
				}
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (ActionManager.MAIN_SEARCH_BICLUSTERS.equals(e
					.getActionCommand())) {

				if (anyValidListAvailable()) {
					Search search = new Search(this);
					search.makeWindow();
				} else
					JOptionPane.showMessageDialog(this,
							"Perform biclustering before Searching!");
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (ActionManager.MAIN_FILTER_BICLUSTERS.equals(e
					.getActionCommand())) {

				if (anyValidListAvailable()) {
					Filter filter = new Filter(this);
					filter.makeWindow();
				} else
					JOptionPane.showMessageDialog(this,
							"Perform biclustering before Filtering!");
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (ActionManager.MAIN_GENE_PAIR_ANALYSIS.equals(e
					.getActionCommand())) {

				if (anyValidListAvailable()) {
					GenePairAnalysis gpa = new GenePairAnalysis(this);
					gpa.makeWindow();
				} else
					JOptionPane.showMessageDialog(this,
							"Perform biclustering before Gene-Pair Analysis!");
			}

			// PENDING....
			/*******************************************************************
			 * else
			 * if(ActionManager.MAIN_GENE_ANNOTATION_ANALYSIS.equals(e.getActionCommand()))
			 * System.out.println("To be implemented
			 * ("+e.getActionCommand()+")!");
			 * 
			 * else
			 * if(ActionManager.MAIN_GENE_NETWORK_ANALYSIS.equals(e.getActionCommand()))
			 * System.out.println("To be implemented
			 * ("+e.getActionCommand()+")!");
			 ******************************************************************/

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if ("info_bc".equals(e.getActionCommand())) {

				Object maybeBC = ((DefaultMutableTreeNode) tree
						.getSelectionPath().getLastPathComponent())
						.getUserObject();
				try {
					if ("bica_gui.Bicluster".equals(maybeBC.getClass()
							.getName())) {
						BiclusterInfo info = new BiclusterInfo(this,
								(bicat.biclustering.Bicluster) maybeBC);
						info.makeWindow();
						return;
					}
				} catch (Exception wee) {
					System.err.println(wee);
				}
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (ActionManager.MAIN_INFO_HELP.equals(e.getActionCommand())) {
				System.out
						.println("To be implemented (" + e.getActionCommand());
				System.out
						.println(")! => check the 'Readme_Help_BINAr.txt' file in the installation directory!.");
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (ActionManager.MAIN_INFO_LICENSE.equals(e
					.getActionCommand())) {
				System.out.println("To be implemented (" + e.getActionCommand()
						+ ")!");
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			else if (ActionManager.MAIN_INFO_ABOUT.equals(e.getActionCommand())) {
				About aw = new About(this);
				aw.makeWindow();
			}

			/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
			// Popup Menu:
			else if ("deleteNode".equals(e.getActionCommand())) {

				TreePath currentSelection = tree.getSelectionPath();
				if (currentSelection != null) {

					DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection
							.getLastPathComponent());

					if ((currentNode.toString()).equals("Data Display")
							|| (currentNode.toString()).equals("Original data")
							|| (currentNode.toString())
									.equals("Preprocessed data")
							|| (currentNode.toString())
									.equals("Discretized data")) {
						; // do not delete
					} else {

						MutableTreeNode parent = (MutableTreeNode) (currentNode
								.getParent());
						if (parent != null) {

							treeModel.removeNodeFromParent(currentNode);

							// (correct) Management:
							if (debug)
								System.out
										.println("BicatGui.actionPerformed().deleteNode.ACTION,");
							if (debug)
								System.out.println("Selected node is: "
										+ currentNode.toString());

							// **** Dataset?
							if (BicatUtil.isDataset(currentNode.toString())) { // delete
								// all
								// structures
								// associated
								// with
								// the
								// Dataset;

								int idx = BicatUtil.getDatasetID(currentNode
										.toString());
								if (BicatGui.debug)
									System.out
											.println("Debug: Removing dataset idxed "
													+ idx);
								datasetList.remove(idx);

								// remove the dataMatrices lists ...
								rawData = null;
								rawData_for_GP = null;

								// matrix Labels (hdr columns/rows)
								labelMenu = new JMenu();

								// TO DO (as of 21.03.2005): if there are other
								// datasets -> get the data visualized,
								if (datasetList.size() > 0) {
									// UPDATE THE PP & GP tab (visualize the
									// last dataset : update also the LABEL
									// (MATRIX VIEW - DataSet X))

									// TO DO: IMPLEMENT !!!

								} else {
									// OTHERWISE, clean the visualization
									// Panes...

									if (debug)
										System.out
												.println("No more datasets loaded (clean the Panes) ...");

									// temporary, only 1 dataset at a time is
									// allowed.
									// loadMenuItem.setEnabled(true);

									cleanPicturePane();
									cleanGraphicPane();
									cleanAnalysisPane();
								}
							}

							// *** Collection of BC lists?
							else if (BicatUtil.isCollectionOfLists(currentNode
									.toString())) {

								if (debug)
									System.out
											.println("Delete the list of Collection");

								// invalid all the BC lists of the collection
								int[] idxs = BicatUtil
										.getListOfDatasetIdx(currentSelection);
								Dataset BcR = (Dataset) datasetList
										.get(idxs[1]);
								for (int i = 0; i < (BcR.getBCsList(idxs[0]))
										.size(); i++)
									BcR.invalidList(idxs[0], i);
							}

							// *** List of (B)Cs?
							else if (BicatUtil.isListOfBCs(currentNode
									.toString())) {

								if (debug)
									System.out
											.println("Delete the list of BCs");

								int[] idxs = BicatUtil
										.getListAndIdxPath(currentSelection); // tree.getSelectionPath());
								((Dataset) datasetList.get(idxs[4]))
										.invalidList(idxs[0], idxs[3]);

								// update the menu's (for save, filter, search,
								// gpa, ...)
								switch (idxs[0]) {
								case 0:
									updateBiclusterMenu();
									break;
								case 1:
									updateSearchMenu();
									break;
								case 2:
									updateFilterMenu();
									break;
								case 3:
									updateClusterMenu();
									break;
								default:
									updateAnalysisMenu();
									break;
								}

							}

							// *** (Bi)cluster?
							else if (BicatUtil.isBC(currentNode.toString())) {

								if (debug)
									System.out.println("Delete a BC");
								// get the list and remove the bc from this list
								int[] idxs = BicatUtil
										.getListAndIdxPath(currentSelection);
								int id = BicatUtil.getBcId(currentSelection
										.getLastPathComponent().toString());
								Dataset BcR = ((Dataset) datasetList
										.get(idxs[4]));
								BcR.removeBicluster(BcR.getBCList(idxs[0],
										idxs[3]), id);
							}

							else if (debug)
								System.out.println("Should not happen!");

							// buildTree();
						}
					}
				}
			}

			else
				System.out
						.println("BicatGui.actionPerformed(), Unknown event: "
								+ e.paramString());
		}

		catch (Exception ee) {
		}

	}

	// ************************************************************************
	// //
	// * * //
	// * GUI-specific: windows, trees, panes... * //
	// * * //
	// ************************************************************************
	// //

	void cleanPicturePane() {
		// picture panel
		pp.removeAll();

		pp = new PicturePane();
		pp.addMouseListener(this);
		pp.setBackground(Color.WHITE);
		pp.setOpaque(true);

		matrixScrollPane.removeAll();
		matrixScrollPane = new JScrollPane(pp);

		matrixViewPane.removeAll();
		matrixViewPane.add(matrixInfoLabel, BorderLayout.SOUTH);
		matrixViewPane.add(matrixScrollPane, BorderLayout.CENTER);

		matrixInfoLabel
				.setText(" Click on a sample to get gene and chip names");
	}

	void cleanGraphicPane() {
		// graph panel
		gp.removeAll();

		gp = new GraphicPane();
		gp.addMouseListener(this);
		gp.setBackground(Color.WHITE);
		gp.setOpaque(true);

		graphScrollPane.removeAll();
		graphScrollPane = new JScrollPane(gp);

		graphViewPane.removeAll();
		graphViewPane.add(graphInfoLabel, BorderLayout.SOUTH);
		graphViewPane.add(graphScrollPane, BorderLayout.CENTER);

		graphInfoLabel.setText("");
	}

	void cleanAnalysisPane() {
		// analysis panel : to visualize the (various) analysis results
		op.removeAll();

		op = new AnalysisPane();
		op.addMouseListener(this);
		op.setBackground(Color.WHITE);
		op.setOpaque(true);

		otherScrollPane.removeAll();
		otherScrollPane = new JScrollPane(op);

		otherViewPane.removeAll();
		otherViewPane.add(otherInfoLabel, BorderLayout.SOUTH);
		otherViewPane.add(otherScrollPane, BorderLayout.CENTER);

		otherInfoLabel.setText("");
	}

	// ===========================================================================
	/**
	 * Builds a tree containing the hierarchy for datasets: runs, search, filter
	 * and analysis results.
	 * 
	 * Creates the tree that is visible in the lefthand part of the splitpane in
	 * the GUI.
	 * 
	 */
	public static void buildTree() {

		DefaultMutableTreeNode category = null;
		DefaultMutableTreeNode matrix = null;
		DefaultMutableTreeNode dataset = null;

		// re-set
		top.removeAllChildren();

		for (int a = 0; a < datasetList.size(); a++) {

			dataset = new DefaultMutableTreeNode("DataSet " + a);
			top.add(dataset);

			// For each dataset, get its corresponding data: BC lists, Filter
			// lists, Analysis lists...

			// ========================================================================
			category = new DefaultMutableTreeNode("Data Display"); // Preprocessing
			// steps");
			dataset.add(category);

			matrix = new DefaultMutableTreeNode("Original data");
			category.add(matrix);

			Object[] pathList = new Object[4];
			pathList[0] = top;
			pathList[1] = dataset;
			pathList[2] = category;
			pathList[3] = matrix;
			originalPath = new TreePath(pathList);

			matrix = new DefaultMutableTreeNode("Preprocessed data");
			category.add(matrix);
			pathList[3] = matrix;
			preprocessedPath = new TreePath(pathList);

			matrix = new DefaultMutableTreeNode("Discretized data");
			category.add(matrix);

			// **************************************** //
			// todo_eventualno_1(); // ... add icons to the nodes of the tree.

			// **************************************** //
			Dataset BcR = (Dataset) datasetList.get(a);

			if (BcR.biclustersAvailable() == false
					&& BcR.clustersAvailable() == false)

				continue; // go to the next dataset, if any.

			else {

				/** display the hierarchy: */

				if (BcR.validBiclustersAvailable()) {
					if (debug)
						System.out
								.println("Building subtree with biclustering results...");
					buildSubTree(dataset, BcR.getBiclusters(), BcR
							.getBiclustersNames(), BcR.getBiclustersValid(),
							"Bicluster results");
				}

				if (BcR.validClustersAvailable()) {
					if (debug)
						System.out
								.println("Building subtree with clustering results...");
					buildSubTree(dataset, BcR.getClusters(), BcR
							.getClustersNames(), BcR.getClustersValid(),
							"Cluster results");
				}

				if (BcR.validSearchesAvailable()) {
					if (debug)
						System.out
								.println("Building subtree with search results...");
					buildSubTree(dataset, BcR.getSearch(),
							BcR.getSearchNames(), BcR.getSearchValid(),
							"Search results");
				}

				if (BcR.validFiltersAvailable()) {
					if (debug)
						System.out
								.println("Building subtree with filtering results...");
					buildSubTree(dataset, BcR.getFilters(), BcR
							.getFiltersNames(), BcR.getFiltersValid(),
							"Filter results");
				}

				buildAnalysisSubTree(dataset, BcR);
			}
		}

		// *************************************** //
		listScrollPane.validate();
		listScrollPane.repaint();
		treeModel.reload();
	}

	// ===========================================================================
	private static void buildSubTree(DefaultMutableTreeNode top,
			LinkedList biclusterList, Vector biclusterListNames,
			Vector biclusterListValid, String label) {

		DefaultMutableTreeNode category = new DefaultMutableTreeNode(label);
		top.add(category);

		for (int i = 0; i < biclusterList.size(); i++) {

			if (((Boolean) biclusterListValid.get(i)).booleanValue()) {
				DefaultMutableTreeNode lb = new DefaultMutableTreeNode(
						biclusterListNames.get(i));
				LinkedList bcs = (LinkedList) biclusterList.get(i);

				for (int j = 0; j < bcs.size(); j++) {
					lb.add(new DefaultMutableTreeNode(bcs.get(j)));
					// tree.addMouseListener(popupListener);
					// tree.addMouseListener(createPopupMenu_new());
				}
				category.add(lb);
			}
		}
		tree.treeDidChange();
	}

	// ===========================================================================
	// 
	private static void buildAnalysisSubTree(DefaultMutableTreeNode top,
			Dataset BcR) {
		if (BcR.analysisResultsAvailable()) {

			Vector analysisItems = new Vector();
			if (debug)
				System.out.println("Building subtree with analysis results...");

			DefaultMutableTreeNode category = new DefaultMutableTreeNode(
					"Analysis results");
			top.add(category);

			for (int j = 0; j < BcR.getAnalysis().size(); j++) {
				DefaultMutableTreeNode lb = new DefaultMutableTreeNode(BcR
						.getAnalysisNames().get(j));
				// HashMap gp_scores = BcR.getAnalysis(j);
				analysisItems.add(lb);
				category.add(lb);
			}
			tree.treeDidChange();
		}

		else {
			// System.out.println("No analysis results available.");
		}
	}

	// ===========================================================================
	static MouseListener popupListener;

	private void createPopupMenu() {

		JMenuItem menuItem;

		JPopupMenu popup = new JPopupMenu();

		menuItem = new JMenuItem("Delete entry");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("deleteNode");
		popup.add(menuItem);

		// add MouseListener for this PopupMenu
		popupListener = new PopupListener(popup);

		tree.addMouseListener(popupListener);
	}

	JPanel matrixViewPane;

	JPanel graphViewPane;

	JPanel otherViewPane;

	// ===========================================================================
	/**
	 * Default constructor, initializes many local values and creates most of
	 * GUI
	 * 
	 */
	public BicatGui() {
		// main window
		super("BicAT");

		// Set the default settings:

		try {
			File f = new File(".");
			currentDirectoryPath = f.getCanonicalPath();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			// System.exit(10);
		}

		datasetList = new LinkedList();
		preprocessOptions = new PreprocessOption();

		dataAndChipInformationLoaded = false; // mada konnte schon auch true -
		// default sein! boh
		// (02.06.2004)

		discretizeComplete = false;
		preprocessComplete = false;

		discretizeExtended = false;
		preprocessExtended = false;
		processExtended = false;

		fileOffset = 1; // assuming the data file has BOTH the column and row
		// headers
		logBaseSetting = 2;
		discretizationThreshold = (float) 2.0; // 1.0 <=> 2-fold change (when
		// Log Base is 2)

		// post-processing stuff:
		gpDistanceThreshold = 0; // correct this! (so to have only the
		// induced subgraph (degree should be
		// thresholded, as well!))
		hammingDistance = (float) 0.8; // should be user-input, though! (20 %)

		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */

		top = new DefaultMutableTreeNode("Display");
		treeModel = new DefaultTreeModel(top);
		tree = new JTree(treeModel);
		tree.addTreeSelectionListener(this);

		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
		createPopupMenu();

		tree.setShowsRootHandles(true);
		listScrollPane = new JScrollPane(tree);
		buildTree();

		// ----
		JTextPane workflowPane = new JTextPane();
		workflowPane.setEditable(false);

		String wfString = new String();
		wfString = wfString + "1. Load Data\n\n";
		wfString = wfString + "2. Preprocess Data\n\n";
		wfString = wfString + "3. Run Biclustering\n\n";
		wfString = wfString + "4. Data Analysis\n\n";
		wfString = wfString + "5. Export Results\n\n";

		workflowPane.setText(wfString);

		// ------------------------

		JTabbedPane tabbedPane1 = new JTabbedPane();
		tabbedPane1.setTabPlacement(JTabbedPane.BOTTOM);
		tabbedPane1.addTab("Display view", listScrollPane);
		tabbedPane1.addTab("Workflow", workflowPane);

		// ------------------------

		// picture panel
		pp = new PicturePane();
		pp.addMouseListener(this);
		pp.setBackground(Color.WHITE);
		pp.setOpaque(true);
		matrixScrollPane = new JScrollPane(pp);

		// graph panel
		gp = new GraphicPane();
		gp.addMouseListener(this);
		gp.setBackground(Color.WHITE);
		gp.setOpaque(true);
		graphScrollPane = new JScrollPane(gp);

		// other panel : to visualize the (various) analysis results
		op = new AnalysisPane();
		op.addMouseListener(this);
		op.setBackground(Color.WHITE);
		op.setOpaque(true);
		otherScrollPane = new JScrollPane(op);

		// matrix view tab on the right side of the splitpane, contains picture
		// panel and infolabel
		matrixInfoLabel = new JLabel(" ", JLabel.CENTER);
		matrixViewPane = new JPanel(new BorderLayout());
		matrixViewPane.add(matrixInfoLabel, BorderLayout.SOUTH);
		matrixViewPane.add(matrixScrollPane, BorderLayout.CENTER);

		// graph view tab on the right side of the splitpane, contains graph
		// panel and infolabel
		graphInfoLabel = new JLabel(" ", JLabel.CENTER);
		graphViewPane = new JPanel(new BorderLayout());
		graphViewPane.add(graphInfoLabel, BorderLayout.SOUTH);
		graphViewPane.add(graphScrollPane, BorderLayout.CENTER);

		// Analysis view tab on the right side of the splitpane
		otherInfoLabel = new JLabel(" ", JLabel.CENTER);
		otherViewPane = new JPanel(new BorderLayout());
		otherViewPane.add(otherInfoLabel, BorderLayout.SOUTH);
		otherViewPane.add(otherScrollPane, BorderLayout.CENTER);

		// create tabbed pane
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Matrix view", matrixViewPane);
		tabbedPane.addTab("Expression view", graphViewPane);
		tabbedPane.addTab("Analysis view", otherViewPane);

		// create split pane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				tabbedPane1, tabbedPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(250); // 200

		// set dimensions and location of the main window
		int inset = 50;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(inset, inset, screenSize.width - 2 * inset, screenSize.height
				- 2 * inset);
		getContentPane().add(splitPane);

		setJMenuBar(createMenuBar());
	}

	// ===========================================================================
	/**
	 * Creates top bar of pull down menus with all menu items.
	 * 
	 * Requires a <code>BicaGUI</code> and a <code>PicturePane</code> to add
	 * as action listeners.
	 * 
	 * @return the complete menu bar
	 * 
	 */
	private JMenuBar createMenuBar() {

		JMenuBar menuBar = new JMenuBar();
		JMenu menu, subMenu;
		JMenuItem menuItem;
		JCheckBoxMenuItem cbMenuItem;
		JRadioButtonMenuItem rbMenuItem;
		ButtonGroup group;

		stop_icon = new ImageIcon("images/realfrown.gif");

		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

		// create main menu...
		menu = new JMenu("File");
		// menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

		subMenu = new JMenu("Load... ");

		loadMenuItem = new JMenuItem("Expression Data");
		loadMenuItem.setActionCommand(ActionManager.MAIN_LOAD_TWO_INPUT_FILES);
		loadMenuItem.addActionListener(this);
		loadMenuItem.setEnabled(true);
		subMenu.add(loadMenuItem);

		menu.add(subMenu);

		// menuItem = new JMenuItem("List of biclusters");
		// // PRECONDITION: data
		// // must match the list
		// // of BCs
		// menuItem.setActionCommand(ActionManager.MAIN_LOAD_BICLUSTERS);
		// menuItem.addActionListener(this);
		// menuItem.setEnabled(true);
		// subMenu.add(menuItem);
		//
		// menu.addSeparator();
		//
		// /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

		subMenu = new JMenu("Export..."); // for ex. -> XML, SVG (graph), PDF
		menu.add(subMenu);

		menuItem = new JMenuItem("Selected bicluster"); // HERE SHOULD BE A
		// CHOICE AVAILABLE,
		// which list of BCs
		menuItem.setActionCommand("save_selected_bc");
		menuItem.addActionListener(this);
		subMenu.add(menuItem);

		subMenu.addSeparator();

		saveBiclusterMenu = new JMenu("Biclustering results...");
		saveBiclusterMenu.setEnabled(false);
		subMenu.add(saveBiclusterMenu);

		saveClusterMenu = new JMenu("Clustering results...");
		saveClusterMenu.setEnabled(false);
		subMenu.add(saveClusterMenu);

		saveSearchMenu = new JMenu("Search results...");
		saveSearchMenu.setEnabled(false);
		subMenu.add(saveSearchMenu);

		saveFilterMenu = new JMenu("Filter results...");
		saveFilterMenu.setEnabled(false);
		subMenu.add(saveFilterMenu);

		saveAnalysisMenu = new JMenu("Gene Pair Analysis results...");
		saveAnalysisMenu.setEnabled(false);
		subMenu.add(saveAnalysisMenu);

		menu.addSeparator();

		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

		menuItem = new JMenuItem("Quit");
		menuItem.setMnemonic(KeyEvent.VK_Q);
		menuItem.setActionCommand(ActionManager.MAIN_QUIT);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
		menu = new JMenu("Preprocess");
		menuBar.add(menu);

		// perform an automatized preprocessing of the matrix:

		menuItem = new JMenuItem("Preprocess data"); // preprocess in one
		// step
		menuItem.setMnemonic(KeyEvent.VK_P);
		menuItem.setActionCommand("preprocess");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		preprocessOptionsMenu = new JMenu("View preprocess options..."); 
		// preprocess in one step
		preprocessOptionsMenu.setEnabled(false);
		menu.add(preprocessOptionsMenu);

		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
		menu = new JMenu("Run");
		menuBar.add(menu);

		menuItem = new JMenuItem("Biclustering BiMax");
		menuItem.setMnemonic(KeyEvent.VK_B);
		menuItem.setActionCommand(ActionManager.MAIN_RUN_BIMAX);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Biclustering CC");
		menuItem.setMnemonic(KeyEvent.VK_B);
		menuItem.setActionCommand(ActionManager.MAIN_RUN_CC);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Biclustering ISA");
		menuItem.setMnemonic(KeyEvent.VK_B);
		menuItem.setActionCommand(ActionManager.MAIN_RUN_ISA);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Biclustering xMotifs");
		menuItem.setMnemonic(KeyEvent.VK_B);
		menuItem.setActionCommand(ActionManager.MAIN_RUN_XMOTIF);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Biclustering OPSM");
		menuItem.setMnemonic(KeyEvent.VK_B);
		menuItem.setActionCommand(ActionManager.MAIN_RUN_OPSM);
		menuItem.addActionListener(this);
		menuItem.setEnabled(true);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Clustering HCL");
		menuItem.setMnemonic(KeyEvent.VK_B);
		menuItem.setActionCommand(ActionManager.MAIN_RUN_HCL);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Clustering K-means");
		menuItem.setMnemonic(KeyEvent.VK_B);
		menuItem.setActionCommand(ActionManager.MAIN_RUN_KMEANS);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

		menu = new JMenu("Tools");
		menu.setMnemonic(KeyEvent.VK_P);
		menuBar.add(menu);

		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= (10.07.2004) */

		menuItem = new JMenuItem("Search biclusters ");
		menuItem.setMnemonic(KeyEvent.VK_S);
		menuItem.setActionCommand(ActionManager.MAIN_SEARCH_BICLUSTERS);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

		menuItem = new JMenuItem("Filter biclusters ");
		menuItem.setMnemonic(KeyEvent.VK_F);
		menuItem.setActionCommand(ActionManager.MAIN_FILTER_BICLUSTERS);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

		// menuItem = new JMenuItem("Extend biclusters ");
		// menuItem.setMnemonic(KeyEvent.VK_F);
		// menuItem.setActionCommand(ActionManager.MAIN_EXTEND_BICLUSTERS);
		// menuItem.addActionListener(this);
		// menuItem.setEnabled(false);
		// menu.add(menuItem);
		//
		// menu.addSeparator();
		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

		menuItem = new JMenuItem("Gene Pair Analysis");
		menuItem.setActionCommand(ActionManager.MAIN_GENE_PAIR_ANALYSIS);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

		// create display options menu...
		menu = new JMenu("View");
		menuBar.add(menu);

		// labelMenu = new JMenu("Label..."); // dodati dynamically!
		// labelMenu.setEnabled(false);
		// menu.add(labelMenu);

		// Hamming distance BCs (pop-up menu!)

		subMenu = new JMenu("Zoom...");
		subMenu.setMnemonic(KeyEvent.VK_Z);
		menu.add(subMenu);

		group = new ButtonGroup();

		rbMenuItem = new JRadioButtonMenuItem("25 %");
		rbMenuItem.setActionCommand(ActionManager.PICTUREPANE_ZOOM_25);
		rbMenuItem.addActionListener(pp);
		rbMenuItem.addActionListener(gp);
		group.add(rbMenuItem);
		subMenu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("50 %");
		rbMenuItem.setActionCommand(ActionManager.PICTUREPANE_ZOOM_50);
		rbMenuItem.addActionListener(pp);
		rbMenuItem.addActionListener(gp);
		group.add(rbMenuItem);
		subMenu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("75 %");
		rbMenuItem.setActionCommand(ActionManager.PICTUREPANE_ZOOM_75);
		rbMenuItem.addActionListener(pp);
		rbMenuItem.addActionListener(gp);
		group.add(rbMenuItem);
		subMenu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("100 %");
		rbMenuItem.setActionCommand(ActionManager.PICTUREPANE_ZOOM_100);
		rbMenuItem.setSelected(true);
		rbMenuItem.addActionListener(pp);
		rbMenuItem.addActionListener(gp);
		group.add(rbMenuItem);
		subMenu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("150 %");
		rbMenuItem.setActionCommand(ActionManager.PICTUREPANE_ZOOM_150);
		rbMenuItem.addActionListener(pp);
		rbMenuItem.addActionListener(gp);
		group.add(rbMenuItem);
		subMenu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("200 %");
		rbMenuItem.setActionCommand(ActionManager.PICTUREPANE_ZOOM_200);
		rbMenuItem.addActionListener(pp);
		rbMenuItem.addActionListener(gp);
		group.add(rbMenuItem);
		subMenu.add(rbMenuItem);

		menu.addSeparator();

		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

		// limit display options..
		// cbMenuItem = new JCheckBoxMenuItem("Limit number of displayed
		// genes");
		// cbMenuItem.setSelected(true);
		// cbMenuItem.setActionCommand("limit_toggle");
		// cbMenuItem.addActionListener(pp);
		// menu.add(cbMenuItem);
		subMenu = new JMenu("Limit...");
		subMenu.setMnemonic(KeyEvent.VK_L);
		menu.add(subMenu);

		group = new ButtonGroup();

		rbMenuItem = new JRadioButtonMenuItem("100 genes");
		rbMenuItem.setActionCommand(ActionManager.PICTUREPANE_LIMIT_100);
		rbMenuItem.addActionListener(pp);
		group.add(rbMenuItem);
		subMenu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("500 genes");
		rbMenuItem.setActionCommand(ActionManager.PICTUREPANE_LIMIT_500);
		rbMenuItem.setSelected(true);
		rbMenuItem.addActionListener(pp);
		group.add(rbMenuItem);
		subMenu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("1000 genes");
		rbMenuItem.setActionCommand(ActionManager.PICTUREPANE_LIMIT_1000);
		rbMenuItem.addActionListener(pp);
		group.add(rbMenuItem);
		subMenu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("5000 genes");
		rbMenuItem.setActionCommand(ActionManager.PICTUREPANE_LIMIT_5000);
		rbMenuItem.addActionListener(pp);
		group.add(rbMenuItem);
		subMenu.add(rbMenuItem);

//		menu.addSeparator();

//		cbMenuItem = new JCheckBoxMenuItem("Fit to frame..."); // fast ok.
//		cbMenuItem.setActionCommand(ActionManager.PICTUREPANE_FIT_2_FRAME);
//		cbMenuItem.setSelected(false);
//		cbMenuItem.addActionListener(pp);
//		menu.add(cbMenuItem);
//
//		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

		menu = new JMenu("About");
		menuBar.add(menu);
		//
		// menuItem = new JMenuItem("Help"); // , stop_icon);
		// menuItem.setActionCommand("info_help");
		// menuItem.addActionListener(this);
		// menuItem.setEnabled(false);
		// menu.add(menuItem);
		//
		// menuItem = new JMenuItem("License"); // , stop_icon);
		// menuItem.setActionCommand("info_license");
		// menuItem.addActionListener(this);
		// menuItem.setEnabled(false);
		// menu.add(menuItem);

		menuItem = new JMenuItem("About");
		menuItem.setActionCommand("info_about");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

		return menuBar;
	}

	// ===========================================================================
	/**
	 * Alerts the scroll pane to a change in the matrix size, causing the scroll
	 * bars to adjust.
	 * 
	 */
	public static void readjustPictureSize() {
		pp.setMinimumSize(new Dimension(0, 0));
		pp.setPreferredSize(pp.getPictureSize());
		matrixScrollPane.setMinimumSize(new Dimension(0, 0));
		matrixScrollPane.setPreferredSize(pp.getPictureSize());
		matrixScrollPane.revalidate();
	}

	// ===========================================================================
	/**
	 * Alerts the graph panel to changes in geneList selection, causing a repaint.
	 * 
	 */
	public static void refreshGraphicPanel() {

		Vector markedGeneList = pp.getMarkedGeneList();
		Vector markedChipList = pp.getMarkedChipList();
		Vector graphDataList = new Vector();

		for (int i = 0; i < markedGeneList.size(); i++)
			graphDataList.add(rawData_for_GP[((Integer) markedGeneList.get(i))
					.intValue()]);
		// bis 27.01.2005:
		// graphDataList.add(rawData[((Integer)markedGeneList.get(i)).intValue()]);
		// graphDataList.add(pre.dataMatrix[((Integer)markedGeneList.get(i)).intValue()]);
		gp.setGraphDataList(graphDataList, markedGeneList, markedChipList);

		gp.repaint();

		// bis 27.01.2005: gp.updateGraphic();
		gp.updateGraphic(2.0, true);
	}

	/**
	 * Alerts the graph panel to changes in gene selection, causing a repaint.
	 * 
	 */
	public static void refreshGraphicPanel(double dThr) {

		
		Vector markedGeneList = pp.getMarkedGeneList();
		Vector markedChipList = pp.getMarkedChipList();
		Vector graphDataList = new Vector();

		for (int i = 0; i < markedGeneList.size(); i++)
			graphDataList.add(rawData_for_GP[((Integer) markedGeneList.get(i))
					.intValue()]);
		// bis 27.01.2005:
		// graphDataList.add(rawData[((Integer)markedGeneList.get(i)).intValue()]);
		// graphDataList.add(pre.dataMatrix[((Integer)markedGeneList.get(i)).intValue()]);
		gp.setGraphDataList(graphDataList, markedGeneList, markedChipList);

		gp.repaint();
		// bis 27.01.2005: gp.updateGraphic();
		gp.updateGraphic(dThr, true);
	}

	// ===========================================================================
	/**
	 * Alerts the graph panel to changes in gene selection, causing a repaint.
	 * 
	 */
	public static void refreshAnalysisPanel() {
		op.repaint();
		op.revalidate();
		readjustPictureSize();
	}

	public void refreshAnalysisPanel(int d, int x) {
		op.setTable(((Dataset) datasetList.get(d)).getAnalysis(x));
		op.repaint();
		op.revalidate();
		readjustPictureSize();
	}

	// ************************************************************************
	// //
	// * * //
	// * Dynamic Menus management * //
	// * * //
	// ************************************************************************
	// //

	/** Update the Export Biclustering Results... menu list. */
	public void updateBiclusterMenu() {

		saveBiclusterMenu.removeAll();

		saveBiclusterMenu.setEnabled(true);
		for (int i = 0; i < datasetList.size(); i++) {
			JMenuItem mi = new JMenuItem("Dataset " + i);
			saveBiclusterMenu.add(mi);

			Vector bNames = ((Dataset) datasetList.get(i)).getBiclustersNames();
			Vector bValid = ((Dataset) datasetList.get(i)).getBiclustersValid();
			for (int j = 0; j < bNames.size(); j++) {

				if (((Boolean) bValid.get(j)).booleanValue()) {
					mi = new JMenuItem("\"" + (String) bNames.get(j) + "\"");
					mi
							.setActionCommand("save_bicluster_results_" + i
									+ "_" + j);
					mi.addActionListener(this);
					saveBiclusterMenu.add(mi);
				}
			}
		}
	}

	/** Update the Export Clustering Results... menu list. */
	public void updateClusterMenu() {

		saveClusterMenu.removeAll();

		saveClusterMenu.setEnabled(true);
		for (int i = 0; i < datasetList.size(); i++) {
			JMenuItem mi = new JMenuItem("Dataset " + i);
			saveClusterMenu.add(mi);

			Vector cNames = ((Dataset) datasetList.get(i)).getClustersNames();
			Vector cValid = ((Dataset) datasetList.get(i)).getClustersValid();
			for (int j = 0; j < cNames.size(); j++) {

				if (((Boolean) cValid.get(j)).booleanValue()) {
					mi = new JMenuItem("\"" + (String) cNames.get(j) + "\"");
					mi.setActionCommand("save_cluster_results_" + i + "_" + j);
					mi.addActionListener(this);
					saveClusterMenu.add(mi);
				}
			}
		}
	}

	/** Update the Export Filter Results... menu list. */
	public void updateFilterMenu() {

		saveFilterMenu.removeAll();

		saveFilterMenu.setEnabled(true);
		for (int i = 0; i < datasetList.size(); i++) {
			JMenuItem mi = new JMenuItem("Dataset " + i);
			saveFilterMenu.add(mi);

			Vector fNames = ((Dataset) datasetList.get(i)).getFiltersNames();
			Vector fValid = ((Dataset) datasetList.get(i)).getFiltersValid();
			for (int j = 0; j < fNames.size(); j++) {
				if (((Boolean) fValid.get(j)).booleanValue()) {
					mi = new JMenuItem("\"" + (String) fNames.get(j) + "\"");
					mi.setActionCommand("save_filter_results_" + i + "_" + j);
					mi.addActionListener(this);
					saveFilterMenu.add(mi);
				}
			}
		}
	}

	/** Update the Export Search Results... menu list. */
	public void updateSearchMenu() {

		saveSearchMenu.removeAll();

		saveSearchMenu.setEnabled(true);
		for (int i = 0; i < datasetList.size(); i++) {
			JMenuItem mi = new JMenuItem("Dataset " + i);
			saveSearchMenu.add(mi);

			Vector sNames = ((Dataset) datasetList.get(i)).getSearchNames();
			Vector sValid = ((Dataset) datasetList.get(i)).getSearchValid();
			for (int j = 0; j < sNames.size(); j++) {
				if (((Boolean) sValid.get(j)).booleanValue()) {
					mi = new JMenuItem("\"" + (String) sNames.get(j) + "\"");
					mi.setActionCommand("save_search_results_" + i + "_" + j);
					mi.addActionListener(this);
					saveSearchMenu.add(mi);
				}
			}
		}
	}

	/** Update the Export Analysis Results... menu list. */
	public void updateAnalysisMenu() {

		saveAnalysisMenu.removeAll();

		saveAnalysisMenu.setEnabled(true);
		for (int i = 0; i < datasetList.size(); i++) {
			JMenuItem mi = new JMenuItem("Dataset " + i);
			saveAnalysisMenu.add(mi);

			Vector aNames = ((Dataset) datasetList.get(i)).getAnalysisNames();
			Vector aValid = ((Dataset) datasetList.get(i)).getAnalysisValid();
			for (int j = 0; j < aNames.size(); j++) {
				if (((Boolean) aValid.get(j)).booleanValue()) {
					mi = new JMenuItem("\"" + (String) aNames.get(j) + "\"");
					mi.setActionCommand("save_analysis_results_" + i + "_" + j);
					mi.addActionListener(this);
					saveAnalysisMenu.add(mi);
				}
			}
		}
	}

	// ===========================================================================
	/** @todo get the Column/Row Headers done. */
	public void updateColumnHeadersMenu() {

		/*
		 * labelMenu.removeAll();
		 * 
		 * //labels = pre.getHeaderColumnLabels();
		 * 
		 * labelMenu.setEnabled(true); for (int i = 0; i<datasetList.size();
		 * i++) { JMenuItem mi = new JMenuItem("Dataset "+i);
		 * saveAnalysisMenu.add(mi);
		 * 
		 * Vector aNames = ((Dataset)datasetList.get(i)).getAnalysisNames(); for
		 * (int j = 0; j<aNames.size(); j++) {
		 * 
		 * mi = new JMenuItem("\""+(String)aNames.get(j)+"\"");
		 * mi.setActionCommand("save_analysis_results_"+i+"_"+j);
		 * mi.addActionListener(this); saveAnalysisMenu.add(mi); } }
		 */

		// labelMenu.removeAll();
		// ... add the new labels...
		labels = currentDataset.getHeaderColumnLabels();
		// labels = pre.getHeaderColumnLabels();
		// labelMenu.setEnabled(true);
		// for (int i = 0; i < labels.size(); i++) {
		// JMenuItem mi = new JMenuItem("Label by \"" + (String) labels.get(i)
		// + "\"");
		// mi.setActionCommand("" + i);
		// mi.addActionListener(this);
		// labelMenu.add(mi);
		// }

	}

	// ===========================================================================
	public void updatePreprocessOptionsMenu() {

		preprocessOptionsMenu.removeAll();

		preprocessOptionsMenu.setEnabled(true);
		for (int i = 0; i < datasetList.size(); i++) {

			if (((Dataset) datasetList.get(i)).preprocessComplete()) {

				JMenuItem mi = new JMenuItem("Dataset " + i);
				preprocessOptionsMenu.add(mi);

				Vector bNames = ((Dataset) datasetList.get(i))
						.getBiclustersNames();
				Vector bValid = ((Dataset) datasetList.get(i))
						.getBiclustersValid();
				for (int j = 0; j < bNames.size(); j++) {
					if (((Boolean) bValid.get(j)).booleanValue()) {
						mi = new JMenuItem("for run \""
								+ (String) bNames.get(j) + "\"");
						mi.setActionCommand("show_info_" + i + "_" + j);
						mi.addActionListener(this);
						preprocessOptionsMenu.add(mi);
					}
				}

				Vector cNames = ((Dataset) datasetList.get(i))
						.getClustersNames();
				Vector cValid = ((Dataset) datasetList.get(i))
						.getClustersValid();
				if (cNames.size() > 0)
					preprocessOptionsMenu.addSeparator();
				for (int j = 0; j < cNames.size(); j++) {
					if (((Boolean) cValid.get(j)).booleanValue()) {
						mi = new JMenuItem("for run \""
								+ (String) cNames.get(j) + "\"");
						mi.setActionCommand("show_info_" + i + "_" + j);
						mi.addActionListener(this);
						preprocessOptionsMenu.add(mi);
					}
				}

			}
		}
	}

	// ************************************************************************
	// //
	// * * //
	// * I/O-specific: load, save, export facilities... * //
	// * * //
	// ************************************************************************
	// //

	String writeAnalysisResults_BioLayout(HashMap analysis) {

		StringBuffer sb = new StringBuffer();

		Set ks = analysis.keySet();
		Object[] ka = ks.toArray();
		for (int i = 0; i < ka.length; i++) {
			String key = (String) ka[i];
			String[] gs = key.split("\t");

			sb.append(currentDataset.getGeneName((new Integer(gs[0]))
					.intValue())
					+ "\t"
					+ currentDataset.getGeneName((new Integer(gs[1]))
							.intValue())
					+ "\t"
					+ ((Integer) ((Object[]) analysis.get(key))[0])
					+ "\t"
					+ ((Integer) ((Object[]) analysis.get(key))[1]) + "\n");
		}

		return sb.toString();
	}

	public void writeGPA(String content) {

		JFileChooser jfc = new JFileChooser(currentDirectoryPath);
		jfc.setDialogTitle("Save Analysis Results:");
		File file;
		int returnVal = jfc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				file = jfc.getSelectedFile();
				FileWriter fw = new FileWriter(file);
				fw.write(content);
				fw.close();
			} catch (IOException ioe) {
				System.err.println(ioe);
			}
		}

	}

	/**
	 * Writes a list of biclusters to a file that the user can choose using a
	 * pop up window.
	 * 
	 * Overwrites existing files without asking. (CORRECT THIS!) First line
	 * contains number of biclusters, blocks after that are as follows: (number
	 * of genes) (number of chips) (gene indices) (chip indices)
	 * 
	 * @param BiclusterList
	 *            linked list of biclusters that are written to file
	 * 
	 */
	public void writeBiclusters(LinkedList BiclusterList) {

		JFileChooser jfc = new JFileChooser(currentDirectoryPath);
		jfc.setDialogTitle("Save bicluster data:");
		File file;
		int returnVal = jfc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				file = jfc.getSelectedFile();
				FileWriter fw = new FileWriter(file);
				String writeBuffer = new String("Number of BCs = "
						+ BiclusterList.size() + "\n\n\n");

				bicat.biclustering.Bicluster bc;
				for (int i = 0; i < BiclusterList.size(); i++) {
					bc = (bicat.biclustering.Bicluster) BiclusterList.get(i);
					writeBuffer = new String(bc.gd + " " + bc.cd + "\n");

					// write genes to file
					for (int j = 0; j < bc.gd; j++)
						writeBuffer += bc.genes[j] + " ";
					writeBuffer = writeBuffer.trim(); // remove trailing
					// whitespace! (OVO
					// NISAM ZNALA)
					writeBuffer += "\n";

					// write chips to file
					for (int j = 0; j < bc.cd; j++)
						writeBuffer += bc.chips[j] + " ";
					writeBuffer = writeBuffer.trim(); // remove trailing
					// whitespace! (OVO
					// NISAM ZNALA)
					writeBuffer += "\n";

					fw.write(writeBuffer);
				}
				fw.close();
			} catch (IOException ioe) {
				System.err.println(ioe);
			}
		}
	}

	/**
	 * Writes a list of biclusters to a file that the user can choose using a
	 * pop up window.
	 * 
	 * Overwrites existing files without asking. (CORRECT THIS!) First line
	 * contains number of biclusters, blocks after that are as follows: (number
	 * of genes) (number of chips) (gene indices) (chip indices)
	 * 
	 * @param BiclusterList
	 *            linked list of biclusters that are written to file
	 * 
	 */
	public void writeBiclustersHuman(LinkedList BiclusterList) {

		JFileChooser jfc = new JFileChooser(currentDirectoryPath); // open a
		// file
		// chooser
		// dialog
		// window
		jfc.setDialogTitle("Save bicluster data:");
		File file;
		int returnVal = jfc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				file = jfc.getSelectedFile();
				FileWriter fw = new FileWriter(file);
				String writeBuffer = new String("Number of BCs = "
						+ BiclusterList.size() + "\n\n\n");

				bicat.biclustering.Bicluster bc;
				for (int i = 0; i < BiclusterList.size(); i++) {
					bc = (bicat.biclustering.Bicluster) BiclusterList.get(i);
					writeBuffer = new String(bc.gd + " " + bc.cd + "\n");

					// write genes to file
					for (int j = 0; j < bc.gd; j++)
						writeBuffer += currentDataset.getGeneName(bc.genes[j])
								+ " ";// pre.getGeneName(bc.genes[j]) + " ";
					writeBuffer = writeBuffer.trim(); // remove trailing
					// whitespace! (OVO
					// NISAM ZNALA)
					writeBuffer += "\n";

					// write chips to file
					for (int j = 0; j < bc.cd; j++)
						writeBuffer += currentDataset
								.getWorkingChipName(bc.chips[j])
								+ " ";// pre.getChipName(bc.chips[j]) + " ";
					writeBuffer = writeBuffer.trim(); // remove trailing
					// whitespace! (OVO
					// NISAM ZNALA)
					writeBuffer += "\n";

					fw.write(writeBuffer);
				}
				fw.close();
			} catch (IOException ioe) {
				System.err.println(ioe);
			}
		}
	}

	// ************************************************************************
	// //
	// * * //
	// * Preprocessing Settings... * //
	// * * //
	// ************************************************************************
	// //

	public void setPreprocessRun(PreprocessOption po) {

		if (debug)
			System.out.println("What current Dataset is it? "
					+ currentDatasetIdx);

		preprocessOptions = new PreprocessOption(po);

		if (po.do_compute_logarithm)
			logBaseSetting = po.logarithmBase;

		if (po.do_normalize) { /* ... */
		}

		if (po.do_discretize) {
			
			if (po.discretizationScheme == PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_COEXPRESSED)
				preprocessExtended = true;
			else
				preprocessExtended = false;
		}

		// ....
		
		pre.resetData(currentDataset.getOrigData());
		pre.preprocessData(po);
		System.out.println("Preprocessing with setup: \n" + po.toString());

		// ....
		if (preprocessOptions.discretizationScheme == PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_COEXPRESSED) {
			processExtended = true;
			discretizeExtended = true;
		}

		discretizeComplete = true;
		preprocessComplete = true; // preprocessed data are now available

		// show results to user
		setData(pre.getPreprocessedData());
		pp.setData(rawData);
		readjustPictureSize();
		pp.repaint();

		// ... update the just-processed data
		if (preprocessExtended)
			currentDataset.setExtended();
		else
			currentDataset.resetExtended();

		currentDataset.setPreData(pre.getPreprocessedData());

		if (po.do_discretize) {
			currentDataset.setDiscrData(pre.getDiscreteData());
		} // binary,
		// or
		// discrete
		// currentDataset.setVisualDiscrData(pre.getDiscreteData()); // discrete
		currentDataset.setPreprocessComplete();
	}

	// ===========================================================================
	// DISPLAY PURPOSES ...

	/**
	 * Hands a discretized data matrix to the for display.<br>
	 * 
	 * Reads the values from the provided <code>int[][]</code> array and
	 * places them in <code>rawData</code>.
	 * <p>
	 * 
	 * //...Missing values are set in the middle of the range, for want of a
	 * better solution.
	 * 
	 */
	public static void setData(int[][] dataMatrix) {

		float[][] intDataMatrix = new float[dataMatrix.length][dataMatrix[0].length];
		for (int i = 0; i < dataMatrix.length; i++)
			for (int j = 0; j < dataMatrix[0].length; j++)
				intDataMatrix[i][j] = (float) dataMatrix[i][j];

		setData(intDataMatrix);
		/**
		 * TEST, 30.03.2005: rawData = new
		 * float[dataMatrix.length][dataMatrix[0].length]; for(int i=0; i<dataMatrix.length;
		 * i++) for(int j=0; j<dataMatrix[0].length; j++) { rawData[i][j] =
		 * (float)dataMatrix[i][j]; //if(Preprocessor.DISCRETE_MISSING_VALUE ==
		 * dataMatrix[i][j]) //rawData[i][j] = (float)0.5; }
		 */
	}

	/**
	 * Hands a <code>float</code> data matrix for display.<br>
	 * 
	 * Normalization of the values for display is needed, because of the color
	 * mapping.
	 * 
	 */

	static float[][] rawData_for_GP;

	public static void setData(float[][] dataMatrix) {

		rawData = new float[dataMatrix.length][dataMatrix[0].length];

		rawData_for_GP = new float[dataMatrix.length][dataMatrix[0].length];
		for (int i = 0; i < dataMatrix.length; i++)
			for (int j = 0; j < dataMatrix[0].length; j++)
				rawData_for_GP[i][j] = dataMatrix[i][j];

		float minValue = Float.MAX_VALUE;
		float maxValue = Float.MIN_VALUE;

		for (int i = 0; i < dataMatrix.length; i++)
			for (int j = 0; j < dataMatrix[0].length; j++) {
				if (maxValue < dataMatrix[i][j])
					maxValue = dataMatrix[i][j];
				if (minValue > dataMatrix[i][j])
					minValue = dataMatrix[i][j];
			}

		// squeeze the values of the matrix, for visualization purposes:
		if (maxValue == minValue)
			for (int i = 0; i < dataMatrix.length; i++)
				for (int j = 0; j < dataMatrix[0].length; j++)
					rawData[i][j] = (float) 0.5;
		else
			for (int i = 0; i < dataMatrix.length; i++)
				for (int j = 0; j < dataMatrix[0].length; j++)
					rawData[i][j] = (dataMatrix[i][j] - minValue)
							/ (maxValue - minValue);

		/*
		 * if(debug) { System.out.println("Dimensions, 1: "+dataMatrix.length+",
		 * "+dataMatrix[0].length); System.out.println("Dimensions, 2:
		 * "+rawData.length+", "+rawData[0].length);
		 * System.out.println("Dimensions, 3: "+rawData_for_GP.length+",
		 * "+rawData_for_GP[0].length); System.out.flush(); }
		 */
	}

	// ===========================================================================
	public void finishUpRun(int datasetIdx, LinkedList result,
			PreprocessOption preOpts, String algo) {

		int ALGO = 0;
		if (algo.equals("BiMax"))
			ALGO = RunMachine.BIMAX_ID;
		else if (algo.equals("CC"))
			ALGO = RunMachine.CC_ID;
		else if (algo.equals("ISA"))
			ALGO = RunMachine.ISA_ID;
		else if (algo.equals("xMotif"))
			ALGO = RunMachine.XMOTIF_ID;
		else if (algo.equals("OPSM"))
			ALGO = RunMachine.OPSM_ID;
		;
		updateCurrentDataset(datasetIdx);
		addBiclusters(currentDataset, result, ALGO);
		addPreprocessOptions(currentDataset, preOpts);

		updatePreprocessOptionsMenu();
		updateBiclusterMenu();
		buildTree();
	}

	// ===========================================================================
	public void finishUpClusterRun(int datasetIdx, LinkedList result,
			PreprocessOption preOpts, String algo) {

		int ALGO = 0;
		if (algo.equals("HCL"))
			ALGO = RunMachine.HCL_ID;
		else if (algo.equals("KMeans"))
			ALGO = RunMachine.KMEANS_ID;
		else
			;

		updateCurrentDataset(datasetIdx);
		addClusters(currentDataset, result, ALGO);
		addClustersPreprocessOptions(currentDataset, preOpts);

		updatePreprocessOptionsMenu();
		updateClusterMenu();
		buildTree();
	}

	// ===========================================================================
	/**
	 * Called from bicat.gui.window.LoadData
	 * 
	 */
	public void loadData(File file, int colOffset, int rowOffset) {
		
		try {

			if (debug)
				System.out.println("Opening: " + file.getName() + ".");

			pre.readMainDataFile(file, colOffset, rowOffset);

			// get the last loaded dataset be the current one ...
			addDataset(pre.getOriginalData(), pre.getOriginalData(), pre
					.getDiscreteData(), // (true) ?
					pre.getGeneNames(), pre.getChipNames(), pre
							.getWorkingChipNames(),
					pre.getHeaderColumnLabels(), pre.getHeaderColumns());

			// make sure BicaGUI has the data and display it in the PicturePane
			setData(currentDataset.getOrigData());
			pp.setData(rawData);
			readjustPictureSize();
			pp.repaint();

			/*
			 * ... add the new labels... labels = pre.getHeaderColumnLabels();
			 * for(int i = 0; i < labels.size(); i++) { JMenuItem mi = new
			 * JMenuItem("Label by \""+(String)labels.get(i)+"\"");
			 * mi.setActionCommand(""+i); mi.addActionListener(this);
			 * labelMenu.add(mi); }
			 */
			
			updateColumnHeadersMenu(); // does the same twice?

			buildTree();
			tree.setSelectionPath(originalPath);
		} catch (FileOffsetException ee) {
			BicatError.wrongOffsetError();
		}
	}

	// ===========================================================================
	/**
	 * Called from bicat.gui.window.Search
	 * 
	 */
	public void search(int data, int list, int idx, String genes, String chips,
			boolean andSearch) {

		if (debug)
			System.out.println("Starting search ...");

		Dataset BcR = (Dataset) datasetList.get(data);
		LinkedList biclusterList = (LinkedList) BcR.getBCsList(list).get(idx);

		biclusterList = post.search(biclusterList, genes, chips, andSearch);

		// do management ...
		BcR.updateSearchBiclustersLists(biclusterList, list, idx);

		updateSearchMenu();
		buildTree();

		tree.setSelectionPath(preprocessedPath);
		tree.setSelectionPath(preprocessedPath);
		 int row = tree.getRowCount() - 1;
		    while (row >= 0) {
		        tree.collapseRow(row);
		        row--;
		    }
		
	}

	// ===========================================================================
	/**
	 * Called from bicat.gui.window.Filter
	 * 
	 */
	public void filter(int data, int list, int idx, int minG, int maxG,
			int minC, int maxC, int nrBCs, int overlap) {

		if (debug)
			System.out.println("Starting filter ...");

		Dataset BcR = (Dataset) datasetList.get(data);

		LinkedList biclusterList = (LinkedList) BcR.getBCsList(list).get(idx);
		int limit_G = BcR.getGeneCount();
		int limit_C = BcR.getWorkingChipCount();

		biclusterList = post.filterBySize(biclusterList, minG, maxG, minC,
				maxC, limit_G, limit_C);
		biclusterList = post.filterByOverlap(biclusterList, nrBCs, overlap);

		// do management ...
		BcR.updateFilterBiclustersLists(biclusterList, list, idx);

		updateFilterMenu();
		buildTree();

		tree.setSelectionPath(preprocessedPath);
		 int row = tree.getRowCount() - 1;
		    while (row >= 0) {
		        tree.collapseRow(row);
		        row--;
		    }
		
	}

	// ===========================================================================
	/**
	 * Called from bicat.gui.window.GenePairAnalysis
	 * 
	 */
	public void genePairAnalysis(int data, int list, int idx, int minCoocScore,
			int minCommonScore, boolean byCooc) {

		Dataset BcR = (Dataset) datasetList.get(data);
		LinkedList biclusterList = (LinkedList) BcR.getBCsList(list).get(idx);

		HashMap gpa = new HashMap();

		if (debug) {
			System.out.println("Starting gene pair analysis ...");
			System.out.println("Hashmap for biclusterList: "
					+ biclusterList.toString());
		}

		if (byCooc)
			gpa = post.gpaByCoocurrence(biclusterList, minCoocScore);
		else
			gpa = post.gpaByCommonChips(biclusterList, minCommonScore);

		if (debug)
			System.out.println("Hashmap size for gpa: " + gpa.size());
		// do management ...
		BcR.updateAnalysisBiclustersLists(gpa, list, idx);

		updateAnalysisMenu();
		buildTree();

		tree.setSelectionPath(preprocessedPath);
		tree.setSelectionPath(preprocessedPath);
		 int row = tree.getRowCount() - 1;
		    while (row >= 0) {
		        tree.collapseRow(row);
		        row--;
		    }
		

	}

	// ===========================================================================
	/**
	 * Creates all basic objects and starts the GUI in a separate thread.
	 * 
	 * This is where the program is started.
	 * 
	 * @param args
	 *            not used
	 * 
	 */
	public static void main(String[] args) {

		final BicatGui frame = new BicatGui();

		pre = new Preprocessor(frame);
		post = new Postprocessor(frame);

//		int[][] x = new int[3][5];
//		int l = x.length;
//		int w = x[1].length;
//		System.out.println("length: "+ l);
//		System.out.println("width: "+ w);
//		
		/*
		 * runMachineBiMax = new RunMachine_BiMax(frame); runMachineCC = new
		 * RunMachine_CC(frame); runMachineISA = new RunMachine_ISA(frame);
		 * runMachineXMotifs = new RunMachine_XMotifs(frame); runMachineHCL =
		 * new RunMachine_HCL(frame); runMachineKMeans = new
		 * RunMachine_KMeans(frame);
		 */

		// @todo OPSM einbinden
		// runMachineOPSM = new RunMachineOPSM(frame);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame.setDefaultLookAndFeelDecorated(true);
				JDialog.setDefaultLookAndFeelDecorated(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});

		pp.setOwner(frame);
		gp.setOwner(frame);

		listScrollPane.setMinimumSize(new Dimension(0, 0));
		listScrollPane.setPreferredSize(new Dimension(50, 200));

		readjustPictureSize();
	}

}
