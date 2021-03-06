package bicat.gui;

import bicat.biclustering.Bicluster;
import bicat.biclustering.Dataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Vector;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author D. Frick, A. Prelic
 * @version 1.0
 *
 **/

/**
 * Custom drawing pane for display of the data matrices, can also be arranged to
 * display biclusters.
 * <p>
 * 
 * There are several options that apply to the visualization of the data matrix.
 * <ul>
 * <li>The zoom level, which can be changed from the main menu (View... ),
 * changes the basic cell size (and all related metrics) to a new scale.
 * <li>The display can be limited to a certain number of genes to bring
 * performance to an acceptable level.
 * <li>User clicks can be categorized according to their location, and
 * selections are visualized with bounding boxes.
 * <li>Finally, the displayed matrix can be rearranged so that a particular
 * bicluster is displayed in the top left corner.
 * </ul>
 * 
 * @author A. Prelic, D. Frick
 * 
 * @todo WISH FEATURE (AP): Different coloring / Scaling Schemes, ...
 * 
 */
public class PicturePane extends JPanel implements ActionListener {

	/**
	 * The location of a click was below and/or to the right of the displayed
	 * matrix.
	 */
	public static int NOWHERE = -1;

	/** The location of the click was inside the matrix, in one of the samples. */
	public static int IN_MATRIX = 0;

	/**
	 * The location of the clisk was in the region of the chip names, above the
	 * matrix.
	 */
	public static int IN_CHIPS = 1;

	/**
	 * The location of the click was in the region of the gene names, to the
	 * left of the matrix.
	 */
	public static int IN_GENES = 2;

	/**
	 * The location of the click was in the top left corner, between gene and
	 * chip names. ("clear area")
	 */
	public static int OUTSIDE = 3;

	private BufferedImage bufferedImage = null;

	/** Hook to the governing <code>BicaGUI</code>. */
	BicatGui owner;

	/** Contains matrix data for display, normalized to [0,1] range. */
	float[][] floatData;

	/**
	 * If true, only a certain number of gene rows (up to
	 * <code>displayLimit</code>) will be displayed.
	 */
	boolean limitDisplay;

	/**
	 * Determines the maximal number of genes rows to display if
	 * <code>limitDisplay</code> is true.
	 */
	int displayLimit;

	/**
	 * An index translation table that rearranges the matrix rows so the genes
	 * of a bicluster come first.
	 */
	int[] geneTranslationTable;

	/**
	 * An index translation table that rearranges the matrix columns so the
	 * chips of a bicluster come first (only with the preprocessed matrix).
	 */
	int[] chipTranslationTable;

	/** The number of genes in the bicluster that is being displayed. */
	int bcGenes;

	/** The number of chips in the bicluster that is being displayed. */
	int bcChips;

	/** The bounding area of the rectangle that has been drawn in. */
	Rectangle filledRect;

	/** Horizontal size of cells in matrix. */
	int xSize;

	/** Vertical size of cells in matrix. */
	int ySize;

	/**
	 * Horizontal offset of matrix, to make room for gene names to the left of
	 * the displayed matrix.
	 */
	int xOffset;

	/**
	 * Vertical offset of matrix, to make room for chip names to the top of the
	 * displayed matrix.
	 */
	int yOffset;

	/** Array full of preset color to distinguish selections. */
	Color[] ColorWheel;

	/** Resizeable list of genes that the user has selected. */
	Vector markedGenes;

	/** Resizeable list of chips that the user has selected. */
	Vector markedChips;

	/** Translation tables for genes (bicluster visualization). */
	HashMap geneValueToPosition; // 'value' is the original index of the
									// gene/chip

	HashMap genePositionToValue; // 'position' is the current physical
									// positioning

	/** Translation tables for (working) chips (bicluster visualization). */
	HashMap chipValueToPosition; // 'value' is the original index of the
									// gene/chip

	HashMap chipPositionToValue; // 'position' is the current physical
									// positioning

	int xSizeFit2pp;

	int ySizeFit2pp;

	boolean fit2pp;

	static boolean labeled;

	static int labelIdx;

	// ===========================================================================
	/*
	 * ..... different coloring schemes? (data dependent, red-yellow, green-red?
	 * ...)
	 */
	private BufferedImage negGreenBlackImage = createGradientImage(Color.green,
			Color.black);

	private BufferedImage posBlackRedImage = createGradientImage(Color.black,
			Color.red);

	private BufferedImage negBlueBlackImage = createGradientImage(Color.blue,
			Color.black);

	private BufferedImage posBlackYellowImage = createGradientImage(
			Color.black, Color.yellow);

	private BufferedImage negCustomBlackImage;

	private BufferedImage posBlackCustomImage;

	static boolean grscale = true;

	static int colorScheme; // 0, 1, 2

	static final int GREEN_RED_SCHEME = 1;

	static final int BLUE_YELLOW_SCHEME = 2;

	static final int CUSTOM_SCHEME = 3;

	static int DEFAULT_X_SIZE = 20;

	static int DEFAULT_Y_SIZE = 10;

	static int DEFAULT_X_OFFSET = 80;

	static int DEFAULT_Y_OFFSET = 120;

	static int DEFAULT_LIMIT_DISPLAY = 500;

	// ===========================================================================
	/** Basic constructor, initializes some values. */
	public PicturePane() {

		floatData = null;

		labeled = false;

		xSize = DEFAULT_X_SIZE; // 20;
		ySize = DEFAULT_Y_SIZE; // 10;
		xOffset = DEFAULT_X_OFFSET; // 80;
		yOffset = DEFAULT_Y_OFFSET; // 120;

		markedGenes = new Vector();
		markedChips = new Vector();

		ColorWheel = new Color[8];
		ColorWheel[0] = Color.BLUE;
		ColorWheel[1] = Color.CYAN;
		ColorWheel[2] = Color.GREEN;
		ColorWheel[3] = Color.MAGENTA;
		ColorWheel[4] = Color.ORANGE;
		ColorWheel[5] = Color.PINK;
		ColorWheel[6] = Color.RED;
		ColorWheel[7] = Color.YELLOW;

		limitDisplay = false;
		displayLimit = DEFAULT_LIMIT_DISPLAY;

		fit2pp = false;

		geneValueToPosition = new HashMap();
		genePositionToValue = new HashMap();
		chipValueToPosition = new HashMap();
		chipPositionToValue = new HashMap();
	}

	/**
	 * Gets the size of the area that has been drawn to.
	 * 
	 */
	public Dimension getPictureSize() {
		if (null != floatData) {
			int geneLimit = (displayLimit < floatData.length) ? (ySize * displayLimit)
					: (ySize * floatData.length);
			return new Dimension((xSize * floatData[0].length + xOffset),
					geneLimit + yOffset);
		} else
			return null;
	}

	/**
	 * Hands the governing <code>BicatGui</code> to this
	 * <code>PicturePane</code>.
	 * 
	 */
	public void setOwner(BicatGui frame) {
		owner = frame;
	}

	// ************************************************************************
	// //
	// * * //
	// * Picture Pane: Data & BC visualization * //
	// * * //
	// ************************************************************************
	// //
	/**
	 * Hands a matrix of data to the code <code>PicturePane</code> for
	 * display.<br>
	 * 
	 * PRE-CONDITION: Data must be normalized to [0,1] range.<br>
	 * 
	 */

	float[][] rawData_for_GP;

	public void setData(float[][] dataMatrix) {

		/*
		 * rawData_for_GP = new float[dataMatrix.length][dataMatrix[0].length];
		 * for(int i = 0; i < dataMatrix.length; i++) for(int j = 0; j<
		 * dataMatrix[0].length; j++) rawData_for_GP[i][j] = dataMatrix[i][j];
		 */

		floatData = dataMatrix;
		geneTranslationTable = new int[dataMatrix.length];
		chipTranslationTable = new int[dataMatrix[0].length];

		filledRect = new Rectangle(0, 0,
				dataMatrix[0].length * xSize + xOffset, dataMatrix.length
						* ySize + yOffset);

		bcGenes = 0;
		bcChips = 0;
		for (int i = 0; i < geneTranslationTable.length; i++)
			geneTranslationTable[i] = i;
		for (int i = 0; i < chipTranslationTable.length; i++)
			chipTranslationTable[i] = i;
		setImageNull();
	}

	/**
	 * Creates translation tables that determine how matrix rows and columns
	 * have to be rearranged so the bicluster shows up in the upper left corner.
	 * 
	 * @param bc
	 *            bicluster that provides lists of genes and chips, tables is
	 *            cleared if input is null
	 * 
	 */
	public Vector[] setTranslationTable(Bicluster bc) {

		// the assumption is that the same dataset, same preprocessing is
		// referred to.
		// reinitialize translation tables
		bcGenes = 0;
		bcChips = 0;

		for (int i = 0; i < geneTranslationTable.length; i++)
			geneTranslationTable[i] = i;
		for (int i = 0; i < chipTranslationTable.length; i++)
			chipTranslationTable[i] = i;

		for (int i = 0; i < geneTranslationTable.length; i++) {
			geneValueToPosition.put(new Integer(i), new Integer(i));
			genePositionToValue.put(new Integer(i), new Integer(i));
		}
		for (int i = 0; i < chipTranslationTable.length; i++) {
			chipValueToPosition.put(new Integer(i), new Integer(i));
			chipPositionToValue.put(new Integer(i), new Integer(i));
		}

		int[] genes = null;
		int[] chips = null;

		if (null != bc) { // if a bicluster has been selected, rearrange
							// translation table

			genes = bc.getGenes();
			chips = bc.getChips();
			bcGenes = genes.length;
			bcChips = chips.length;

			/*
			 * if(owner.debug) {
			 * BicatUtil.print_arr(genes);//System.out.println("D, genes of a
			 * bc: "+genes.toString());
			 * BicatUtil.print_arr(chips);//System.out.println("D, chips of a
			 * bc: "+chips.toString()); }
			 */
			// EXCHANGE in the GENE DIMENSION!
			int offset = 0;
			int exchange_count = 0;
			// place the BC in the top left corner!
			for (int i = 0; i < genes.length; i++) {
				int v1 = genes[i];
				int p2 = exchange_count + offset;
				// if(owner.debug) System.out.println("DEBUG / set g Value to
				// Position: v1 = "+v1);
				int p1 = ((Integer) geneValueToPosition.get(new Integer(v1)))
						.intValue();

				int v2 = ((Integer) genePositionToValue.get(new Integer(p2)))
						.intValue();

				// data part is handled elsewhere (simply update the pointers)
				geneValueToPosition.put(new Integer(v1), new Integer(p2));
				genePositionToValue.put(new Integer(p2), new Integer(v1));

				geneValueToPosition.put(new Integer(v2), new Integer(p1));
				genePositionToValue.put(new Integer(p1), new Integer(v2));

				geneTranslationTable[p2] = v1;
				geneTranslationTable[p1] = v2;

				exchange_count++;
			}

			// EXCHANGE in the CHIP DIMENSION!
			offset = 0;
			exchange_count = 0;
			// place the BC in the top left corner!
			for (int i = 0; i < chips.length; i++) {
				int v1 = chips[i];
				int p2 = exchange_count + offset;
				int p1 = ((Integer) chipValueToPosition.get(new Integer(v1)))
						.intValue();
				int v2 = ((Integer) chipPositionToValue.get(new Integer(p2)))
						.intValue();

				// data part is handled elsewhere (simply update the pointers)
				chipValueToPosition.put(new Integer(v1), new Integer(p2));
				chipPositionToValue.put(new Integer(p2), new Integer(v1));

				chipValueToPosition.put(new Integer(v2), new Integer(p1));
				chipPositionToValue.put(new Integer(p1), new Integer(v2));

				chipTranslationTable[p2] = v1;
				chipTranslationTable[p1] = v2;

				exchange_count++;
			}
		}

		Vector[] result = new Vector[3];
		// graphs
		result[0] = new Vector();
		for (int i = 0; i < genes.length; i++)
			result[0].add(floatData[genes[i]]);
		// genes
		result[1] = new Vector();
		// chips
		result[2] = new Vector();
		for (int i = 0; i < genes.length; i++)
			result[1].add(new Integer(genes[i]));
		for (int i = 0; i < chips.length; i++)
			result[2].add(new Integer(chips[i]));
		return result;
	}

	/**
	 * Creates translation tables that determine how matrix rows and columns
	 * have to be rearranged so the bicluster shows up in the upper left corner.
	 * 
	 * @param bc
	 *            bicluster that provides lists of genes and chips, tables is
	 *            cleared if input is null
	 * 
	 */
	public Vector[] setTranslationTable(Bicluster bc, float[][] rawMatrixGP) {

		// the assumption is that the same dataset, same preprocessing is
		// referred to.
		// reinitialize translation tables
		bcGenes = 0;
		bcChips = 0;

		for (int i = 0; i < geneTranslationTable.length; i++)
			geneTranslationTable[i] = i;
		for (int i = 0; i < chipTranslationTable.length; i++)
			chipTranslationTable[i] = i;

		for (int i = 0; i < geneTranslationTable.length; i++) {
			geneValueToPosition.put(new Integer(i), new Integer(i));
			genePositionToValue.put(new Integer(i), new Integer(i));
		}
		for (int i = 0; i < chipTranslationTable.length; i++) {
			chipValueToPosition.put(new Integer(i), new Integer(i));
			chipPositionToValue.put(new Integer(i), new Integer(i));
		}

		int[] genes = null;
		int[] chips = null;

		if (null != bc) { // if a bicluster has been selected, rearrange
							// translation table

			genes = bc.getGenes();
			chips = bc.getChips();
			bcGenes = genes.length;
			bcChips = chips.length;

			/*
			 * if(owner.debug) {
			 * BicatUtil.print_arr(genes);//System.out.println("D, genes of a
			 * bc: "+genes.toString());
			 * BicatUtil.print_arr(chips);//System.out.println("D, chips of a
			 * bc: "+chips.toString()); }
			 */
			// EXCHANGE in the GENE DIMENSION!
			int offset = 0;
			int exchange_count = 0;
			// place the BC in the top left corner!
			for (int i = 0; i < genes.length; i++) {
				int v1 = genes[i];
				int p2 = exchange_count + offset;
				// if(owner.debug) System.out.println("D: v1 = "+v1);
				int p1 = ((Integer) geneValueToPosition.get(new Integer(v1)))
						.intValue();

				int v2 = ((Integer) genePositionToValue.get(new Integer(p2)))
						.intValue();

				// data part is handled elsewhere (simply update the pointers)
				geneValueToPosition.put(new Integer(v1), new Integer(p2));
				genePositionToValue.put(new Integer(p2), new Integer(v1));

				geneValueToPosition.put(new Integer(v2), new Integer(p1));
				genePositionToValue.put(new Integer(p1), new Integer(v2));

				geneTranslationTable[p2] = v1;
				geneTranslationTable[p1] = v2;

				exchange_count++;
			}

			// EXCHANGE in the CHIP DIMENSION!
			offset = 0;
			exchange_count = 0;
			// place the BC in the top left corner!
			for (int i = 0; i < chips.length; i++) {
				int v1 = chips[i];
				int p2 = exchange_count + offset;
				int p1 = ((Integer) chipValueToPosition.get(new Integer(v1)))
						.intValue();
				int v2 = ((Integer) chipPositionToValue.get(new Integer(p2)))
						.intValue();

				// data part is handled elsewhere (simply update the pointers)
				chipValueToPosition.put(new Integer(v1), new Integer(p2));
				chipPositionToValue.put(new Integer(p2), new Integer(v1));

				chipValueToPosition.put(new Integer(v2), new Integer(p1));
				chipPositionToValue.put(new Integer(p1), new Integer(v2));

				chipTranslationTable[p2] = v1;
				chipTranslationTable[p1] = v2;

				exchange_count++;
			}
		}

		Vector[] result = new Vector[3];
		// graphs
		result[0] = new Vector();
		for (int i = 0; i < genes.length; i++)
			result[0].add(rawMatrixGP[genes[i]]);
		// result[0].add(floatData[genes[i]]);
		// genes
		result[1] = new Vector();
		// chips
		result[2] = new Vector();
		for (int i = 0; i < genes.length; i++)
			result[1].add(new Integer(genes[i]));
		for (int i = 0; i < chips.length; i++)
			result[2].add(new Integer(chips[i]));
		return result;
	}

	/**
	 * Creates translation tables that determine how matrix rows and columns
	 * have to be rearranged so the bicluster shows up in the upper left corner.
	 * 
	 * @param bc
	 *            bicluster that provides lists of genes and chips, tables is
	 *            cleared if input is null
	 * @param br
	 *            the dataset whose bicluster <code>bc</code> is to be
	 *            visualized
	 * @param pIdx
	 *            the preprocessing options index for the dataset
	 *            <code>br</code>
	 */

	// UPDATED: 27.01.2005 (raw Data - float Data? )
	public Vector[] setTranslationTable(Dataset br, int pIdx, Bicluster bc) {

		// the assumption is that the same dataset, same preprocessing is
		// referred to.
		// reinitialize translation tables
		bcGenes = 0;
		bcChips = 0;

		floatData = br.getData();

		geneTranslationTable = new int[br.getData().length];
		chipTranslationTable = new int[br.getData()[0].length];
		geneValueToPosition = new HashMap();
		chipValueToPosition = new HashMap();

		for (int i = 0; i < geneTranslationTable.length; i++)
			geneTranslationTable[i] = i;
		for (int i = 0; i < chipTranslationTable.length; i++)
			chipTranslationTable[i] = i;

		for (int i = 0; i < geneTranslationTable.length; i++) {
			geneValueToPosition.put(new Integer(i), new Integer(i));
			genePositionToValue.put(new Integer(i), new Integer(i));
		}
		for (int i = 0; i < chipTranslationTable.length; i++) {
			chipValueToPosition.put(new Integer(i), new Integer(i));
			chipPositionToValue.put(new Integer(i), new Integer(i));
		}

		int[] genes = null;
		int[] chips = null;

		if (null != bc) { // if a bicluster has been selected, rearrange
							// translation table

			genes = bc.getGenes();
			chips = bc.getChips();
			bcGenes = genes.length;
			bcChips = chips.length;

			// EXCHANGE GENE DIMENSION!
			int offset = 0;
			int exchange_count = 0;
			// place the BC in the top left corner!
			for (int i = 0; i < genes.length; i++) {
				int v1 = genes[i];
				int p2 = exchange_count + offset;
				int p1 = ((Integer) geneValueToPosition.get(new Integer(v1)))
						.intValue();

				int v2 = ((Integer) genePositionToValue.get(new Integer(p2)))
						.intValue();

				// data part is handled elsewhere (simply update the pointers)
				geneValueToPosition.put(new Integer(v1), new Integer(p2));
				genePositionToValue.put(new Integer(p2), new Integer(v1));

				geneValueToPosition.put(new Integer(v2), new Integer(p1));
				genePositionToValue.put(new Integer(p1), new Integer(v2));

				geneTranslationTable[p2] = v1;
				geneTranslationTable[p1] = v2;

				exchange_count++;
			}

			// EXCHANGE CHIP DIMENSION!
			offset = 0;
			exchange_count = 0;
			// place the BC in the top left corner!
			for (int i = 0; i < chips.length; i++) {
				int v1 = chips[i];
				int p2 = exchange_count + offset;
				int p1 = ((Integer) chipValueToPosition.get(new Integer(v1)))
						.intValue();
				int v2 = ((Integer) chipPositionToValue.get(new Integer(p2)))
						.intValue();

				// data part is handled elsewhere (simply update the pointers)
				chipValueToPosition.put(new Integer(v1), new Integer(p2));
				chipPositionToValue.put(new Integer(p2), new Integer(v1));

				chipValueToPosition.put(new Integer(v2), new Integer(p1));
				chipPositionToValue.put(new Integer(p1), new Integer(v2));

				chipTranslationTable[p2] = v1;
				chipTranslationTable[p1] = v2;

				exchange_count++;
			}
		}

		Vector[] result = new Vector[3];
		// graphs
		result[0] = new Vector();
		for (int i = 0; i < genes.length; i++)
			// result[0].add(rawData_for_GP[genes[i]]);
			result[0].add(floatData[genes[i]]); // ..... ist gsii bis:
												// 27.01.2005!
		// genes
		result[1] = new Vector();
		// chips
		result[2] = new Vector();
		for (int i = 0; i < genes.length; i++)
			result[1].add(new Integer(genes[i]));
		for (int i = 0; i < chips.length; i++)
			result[2].add(new Integer(chips[i]));
		return result;
	}

	/**
	 * Inform this <code>PicturePane</code> that a particular gene has been
	 * selected, so selection can be stored and gene can be marked.
	 * 
	 * All selections are toggles, so clicking on a marked gene will unselect
	 * it.
	 * 
	 * @param clickedGene
	 *            index of the gene that has been selected
	 * 
	 */
	public void geneSelected(int clickedGene) {
		int selectedGene = geneTranslationTable[clickedGene];
		for (int i = 0; i < markedGenes.size(); i++)
			if (clickedGene == ((Integer) markedGenes.get(i)).intValue()) {
				markedGenes.removeElementAt(i);
				return;
			}
		markedGenes.add(new Integer(clickedGene));
	}

	/**
	 * Inform this <code>PicturePane</code> that a particular chip has been
	 * selected, so selection can be stored and chip can be marked.
	 * 
	 * All selections are toggles, so clicking on a marked chip will unselect
	 * it.
	 * 
	 * @param clickedChip
	 *            index of the chip that has been selected
	 * 
	 */
	public void chipSelected(int clickedChip) {
		int selectedChip = chipTranslationTable[clickedChip];
		for (int i = 0; i < markedChips.size(); i++)
			if (clickedChip == ((Integer) markedChips.get(i)).intValue()) {
				markedChips.removeElementAt(i);
				return;
			}
		markedChips.add(new Integer(clickedChip));
	}

	/**
	 * Inform this <code>PicturePane</code> that a particular bicluster has
	 * been selected, so selection can be stored and the relevant genes and
	 * chips can be marked.
	 * 
	 * All selections are toggles, so clicking on a selected bicluster will
	 * unselect it.
	 * 
	 * @param genes
	 * @param chips
	 * 
	 */
	public void biclusterSelected(Vector genes, Vector chips) {
		markedGenes = genes; // vector of integers
		markedChips = chips;
	}

	/**
	 * Gets list of genes that have been selected.
	 * 
	 * @see #geneSelected(int) geneSelected
	 * 
	 */
	public Vector getMarkedGeneList() {
		return markedGenes;
	}

	/**
	 * Gets list of chip that have been selected.
	 * 
	 * @see #chipSelected(int) chipSelected
	 * 
	 */
	public Vector getMarkedChipList() {
		return markedChips;
	}

	/**
	 * Gets the true index if a possibly transposed gene that has been clicked
	 * on.
	 * 
	 * @param y
	 *            the y coordinate of a mouse click in the matrix panel
	 * @return the true (reverse translated) index of the gene that was selected
	 * 
	 * @todo FIX BUG: CHECK if this is correct! (getGeneNameIndex)
	 * 
	 */
	public int getGeneNameIndex(int y) {
		return geneTranslationTable[(y - yOffset) / ySize];
	}

	/**
	 * Gets the true index if a possibly transposed chip that has been clicked
	 * on.
	 * 
	 * @param x
	 *            the x coordinate of a mouse click in the matrix panel
	 * @return the true (reverse translated) index of the chip that was selected
	 * 
	 */
	public int getChipNameIndex(int x) {
		return chipTranslationTable[(x - xOffset) / xSize];
	}

	/**
	 * Gets the transformed (transmuted) location of a chip in the displayed
	 * matrix.
	 * 
	 * @param realChip
	 *            the index of the chip that is to be located
	 * @return the index that gives the location of the chip inside the
	 *         displayed matrix
	 * 
	 * @todo FIX BUG: CHECK if this is correct!
	 */
	public int getTransformedChip(int realChip) {
		for (int i = 0; i < chipTranslationTable.length; i++)
			if (chipTranslationTable[i] == realChip)
				return i;
		System.out.println("chipFook"); // ???
		return 0;
	}

	/**
	 * Gets the transformed (transmuted) location of a gene in the displayed
	 * matrix.
	 * 
	 * @param realGene
	 *            the index of the gene that is to be located
	 * @return the index that gives the location of the gene inside the
	 *         displayed matrix
	 * 
	 * @todo FIX BUG: CHECK if this is correct!
	 */
	public int getTransformedGene(int realGene) {
		for (int i = 0; i < geneTranslationTable.length; i++)
			if (geneTranslationTable[i] == realGene)
				return i;
		System.out.println("geneFook"); // ???
		return 0;
	}

	/**
	 * Clears lists of gene and chip selections.
	 */
	public void clearSelections() {
		markedGenes.clear();
		markedChips.clear();
	}

	// ************************************************************************
	// //
	// * * //
	// * Picture Pane: Action Manager... * //
	// * * //
	// ************************************************************************
	// //

	/**
	 * For <code>ActionListener</code> interface, mostly reacts to user input
	 * from pull down menus.
	 * 
	 * @todo BUG, CORRECT: get the current picture/whole window size! Check
	 *       inconsistencies, Why is not working nice when Zooming things
	 *       in/out?
	 * 
	 */
	public void actionPerformed(ActionEvent e) {

		/* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */

		// View: (ActionListener is the one of the PicturePane!)
		/*
		 * Zoom... 25, 50 75, 100, 150, 200 Limit nr of displayed genes..
		 * Limit... 100, 200, 500, 800 Fit to frame size..
		 */

		if (e.getActionCommand().equals(ActionManager.PICTUREPANE_ZOOM_25)) {
			if (fit2pp) {
				xSize = (int) Math.ceil((double) xSize * 0.25);
				ySize = (int) Math.ceil((double) ySize * 0.25);
				xOffset = xSize * 8;
				yOffset = ySize * 14;
			} else {
				xSize = 5;
				ySize = 3;
				xOffset = 20;
				yOffset = 30;
			}
		} else if (e.getActionCommand().equals(
				ActionManager.PICTUREPANE_ZOOM_50)) {
			if (fit2pp) {
				xSize = (int) Math.ceil((double) xSize * 0.5);
				ySize = (int) Math.ceil((double) ySize * 0.5);
				xOffset = xSize * 8;
				yOffset = ySize * 14;
			} else {
				xSize = 10;
				ySize = 5;
				xOffset = 40;
				yOffset = 60;
			}
		} else if (e.getActionCommand().equals(
				ActionManager.PICTUREPANE_ZOOM_75)) {
			if (fit2pp) {
				xSize = (int) Math.ceil((double) xSize * 0.75);
				ySize = (int) Math.ceil((double) ySize * 0.75);
				xOffset = xSize * 8;
				yOffset = ySize * 14;
			} else {
				xSize = 15;
				ySize = 8;
				xOffset = 60;
				yOffset = 90;
			}
		} else if (e.getActionCommand().equals(
				ActionManager.PICTUREPANE_ZOOM_100)) {
			xSize = 20;
			ySize = 10;
			xOffset = 80;
			yOffset = 120;
		} else if (e.getActionCommand().equals(
				ActionManager.PICTUREPANE_ZOOM_150)) {
			if (fit2pp) {
				xSize = (int) Math.ceil((double) xSize * 1.5);
				ySize = (int) Math.ceil((double) ySize * 1.5);
				xOffset = xSize * 8;
				yOffset = ySize * 14;
			} else {
				xSize = 30;
				ySize = 15;
				xOffset = 120;
				yOffset = 180;
			}
		} else if (e.getActionCommand().equals(
				ActionManager.PICTUREPANE_ZOOM_200)) {
			if (fit2pp) {
				xSize = (int) Math.ceil((double) xSize * 2.0);
				ySize = (int) Math.ceil((double) ySize * 2.0);
				xOffset = xSize * 8;
				yOffset = ySize * 14;
			} else {
				xSize = 40;
				ySize = 20;
				xOffset = 160;
				yOffset = 240;
			}
		} else if (e.getActionCommand().equals(
				ActionManager.PICTUREPANE_LIMIT_100))
			displayLimit = 100;
		else if (e.getActionCommand().equals(
				ActionManager.PICTUREPANE_LIMIT_500))
			displayLimit = 500;
		else if (e.getActionCommand().equals(
				ActionManager.PICTUREPANE_LIMIT_1000))
			displayLimit = 1000;
		else if (e.getActionCommand().equals(
				ActionManager.PICTUREPANE_LIMIT_5000))
			displayLimit = 5000;

		else if (e.getActionCommand().equals(
				ActionManager.PICTUREPANE_FIT_2_FRAME)) {
			if (fit2pp == false) {
				// 13.06.2004: naci kako da nadjem the current window
				// size/position!
				fit2pp = true;
				// System.out.println("xS = "+xSize+", yS = "+ySize);

				Rectangle visibleRect = getVisibleRect();

				double dx = visibleRect.width - 200; // 100; //250;
				xSizeFit2pp = (int) Math.ceil((double) dx
						/ owner.pre.getOriginalChipCount());

				double dy = visibleRect.height - 200; // 100; //250;
				// System.out.println("DIM: visible rect x =
				// "+visibleRect.width+", visible rect y =
				// "+visibleRect.height);
				ySizeFit2pp = (int) Math.ceil((double) dy
						/ owner.pre.getGeneCount());

				if (xSizeFit2pp == 0)
					xSizeFit2pp = 1;
				if (ySizeFit2pp == 0)
					ySizeFit2pp = 1;

				if (ySizeFit2pp > xSizeFit2pp)
					ySizeFit2pp = xSizeFit2pp; // don't let the gene size be
												// greater than the chip (cell)
												// dim
				// new: (ne znam da li ovo funkcionise!... valja mi provjeriti
				// sa velikom matricom!)
				else if (xSizeFit2pp > ySizeFit2pp)
					xSizeFit2pp = ySizeFit2pp; // don't let the gene size be
												// greater than the chip (cell)
												// dim

				xSize = xSizeFit2pp;
				ySize = ySizeFit2pp;

				// System.out.println("xSf = "+xSizeFit2pp+", ySf =
				// "+ySizeFit2pp);
			} else { // reset display mode to the default!
				fit2pp = false;
				xSize = 20;
				ySize = 10;
				xOffset = 80;
				yOffset = 120;
			}
		} else if (e.getActionCommand().equals(
				ActionManager.PICTUREPANE_LIMIT_TOGGLE)) {
			limitDisplay = (true == limitDisplay) ? false : true;
			if (limitDisplay == false)
				displayLimit = Integer.MAX_VALUE;
		}
		setImageNull();
		setFilledRect();
		owner.readjustPictureSize();
		owner.matrixScrollPane.repaint();
	}

	/**
	 * Updates size of bounding rectangle of the area that has been drawn to.
	 * 
	 */
	public void setFilledRect() {
		if (null == floatData)
			filledRect = new Rectangle(0, 0, 0, 0);
		else {
			if (limitDisplay)
				filledRect = new Rectangle(0, 0, floatData[0].length * xSize
						+ xOffset, displayLimit * ySize + yOffset);
			else
				filledRect = new Rectangle(0, 0, floatData[0].length * xSize
						+ xOffset, floatData.length * ySize + yOffset);
		}
	}

	/**
	 * Checks which region of the display a particular click falls into.
	 * 
	 * @param x
	 *            the x (horizontal) coordinate of the click
	 * @param y
	 *            the y (vertical) coordinate of the click
	 * 
	 * @see #NOWHERE NOWHERE
	 * @see #IN_MATRIX IN_MATRIX
	 * @see #IN_CHIPS IN_CHIPS
	 * @see #IN_GENES IN_GENES
	 * @see #OUTSIDE OUTSIDE
	 * 
	 */
	public int clickedRegion(int x, int y) {
		if (filledRect == null) {
			return PicturePane.NOWHERE;
		}
		if ((y >= filledRect.getHeight()) || (x >= filledRect.getWidth()))
			return PicturePane.NOWHERE;
		else if ((y == yOffset) || (x == xOffset))
			return PicturePane.NOWHERE;
		else if ((xOffset < x) && (yOffset < y))
			return PicturePane.IN_MATRIX;
		else if ((xOffset < x) && (yOffset > y))
			return PicturePane.IN_CHIPS;
		else if ((xOffset > x) && (yOffset < y))
			return PicturePane.IN_GENES;
		else
			return PicturePane.OUTSIDE;
	}

	/**
	 * Determines the cell that was clicked on, using the coordinates of the
	 * click.
	 * 
	 * @param x
	 *            the x (horizontal) index of the cell that was clicked on
	 * @param y
	 *            the y (vertical) index of the cell that was clicked on
	 * @return the indices (gene, chip - possibly transposed) of the cell that
	 *         was selected
	 * 
	 */
	public Point getRectFromCoordinate(int x, int y) {
		return new Point(getChipNameIndex(x), getGeneNameIndex(y));
	}

	/**
	 * Repaints the matrix, along with gene and chip names.
	 * 
	 * Called internally whenever needed. Totally inefficient. (sta ovo znaci?)
	 * 
	 * @todo .... Work out an efficient procedure. correct the inconsistencies.
	 *       is it correct, anayway?
	 * 
	 */
	protected void paintComponent(Graphics g) {

		if (bufferedImage == null) {

			if (null != floatData) {
				// System.out.println("creating image");
				Dimension dim = getPictureSize();
				bufferedImage = new BufferedImage(dim.width, dim.height,
						BufferedImage.TYPE_BYTE_INDEXED);
				Graphics2D g2 = bufferedImage.createGraphics();
				// Rectangle visibleRect = getVisibleRect();

				g2.setBackground(Color.BLUE);

				// write chip names along the top edge
				// @todo Laenge der chipnamen beachten!
				boolean useOriginalChipNames = (floatData[0].length == owner.currentDataset
						.getChipCount());
				g2.setFont(new Font("Times", Font.PLAIN, ySize));
				g2.rotate(Math.PI / 2);

				for (int i = 0; i < chipTranslationTable.length; i++) {
					/*
					 * if (useOriginalChipNames)
					 * g2.drawString(owner.currentDataset.getChipName(chipTranslationTable[i]),
					 * 0, -xOffset - i * xSize - 1); else
					 */
					g2.drawString(owner.currentDataset
							.getWorkingChipName(chipTranslationTable[i]), 0,
							-xOffset - i * xSize - 1);
				}
				g2.rotate(-Math.PI / 2);

				// write gene names along left edge
				for (int i = 0; i < geneTranslationTable.length; i++) {
					if ((true == limitDisplay) && (i >= displayLimit))
						continue;
					if (labeled) // CHECK THIS!
						g2.drawString(owner.currentDataset.getLabelName(
								geneTranslationTable[i], labelIdx), 0, yOffset
								+ (i + 1) * ySize);
					else
						g2.drawString(owner.currentDataset
								.getGeneName(geneTranslationTable[i]), 0,
								yOffset + (i + 1) * ySize);
				}

				// draw matrix
				// System.out.println("draw the matrix");
				float currentValue;
				for (int i = 0; i < geneTranslationTable.length; i++) {
					if ((true == limitDisplay) && (i >= displayLimit))
						break;
					for (int j = 0; j < chipTranslationTable.length; j++) {
						currentValue = floatData[geneTranslationTable[i]
								% geneTranslationTable.length][chipTranslationTable[j]
								% chipTranslationTable.length];
						if (0.5 >= currentValue)
							g2
									.setColor(new Color((float) 0.0,
											(float) (1 - 2 * currentValue),
											(float) 0.0));
						else
							g2.setColor(new Color(
									(float) (2 * currentValue - 1),
									(float) 0.0, (float) 0.0));
						// fill3DRect ineffizient, deshalb leider nur
						// 2D-Rectangles
						g2.fill3DRect(j * xSize + xOffset, i * ySize + yOffset,
								xSize, ySize, true);
						// g2.fillRect(j * xSize + xOffset, i * ySize + yOffset,
						// xSize - 1, ySize - 1);
					}
				}
				g2.setStroke(new BasicStroke((float) 0.05 * ySize));

				// draw bounding box for current bicluster
				g2.setColor(Color.WHITE); // BLUE);
				g2.draw3DRect(xOffset, yOffset, xSize * bcChips, ySize
						* bcGenes, false);
			}

		}
		Rectangle visibleRect = getVisibleRect();
		Graphics2D g2 = (Graphics2D) g;
		g2.clearRect(visibleRect.x, visibleRect.y, visibleRect.width,
				visibleRect.height);
		g2.setBackground(Color.WHITE);
		if (bufferedImage != null) {
			// System.out.println("painting image");
			g.drawImage(bufferedImage, 0, 0, Color.WHITE, this);
		}

	}

	public void setImageNull() {
		bufferedImage = null;
	}

	// ************************************************************************
	// //
	// * * //
	// * Picture Pane: Coloring Scales.... * //
	// * * //
	// ************************************************************************
	// //
	// ===========================================================================
	public BufferedImage createGradientImage(Color color1, Color color2) {
		// BufferedImage image = (BufferedImage)createCompatibleImage(256,1);

		BufferedImage image = (BufferedImage) GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().createCompatibleImage(256, 1);
		Graphics2D graphics = image.createGraphics();
		GradientPaint gp = new GradientPaint(0, 0, color1, 255, 0, color2);
		graphics.setPaint(gp);
		graphics.drawRect(0, 0, 255, 1);
		return image;
	}

	// ===========================================================================
	public boolean isGreenRedScale() {
		return grscale;
	}

	public BufferedImage getPositiveGradientImage() {
		BufferedImage image = this.posBlackRedImage;
		switch (this.colorScheme) {

		case GREEN_RED_SCHEME:
			break;

		case BLUE_YELLOW_SCHEME:
			image = this.posBlackYellowImage;
			break;

		case CUSTOM_SCHEME:
			if (this.posBlackCustomImage != null)
				image = this.posBlackCustomImage;
			break;
		}
		return image;
	}

	public BufferedImage getNegativeGradientImage() {
		BufferedImage image = this.negGreenBlackImage;
		switch (this.colorScheme) {

		case GREEN_RED_SCHEME:
			break;

		case BLUE_YELLOW_SCHEME:
			image = this.negBlueBlackImage;
			break;

		case CUSTOM_SCHEME:
			if (this.negCustomBlackImage != null)
				image = this.negCustomBlackImage;
			break;
		}
		return image;
	}

}
