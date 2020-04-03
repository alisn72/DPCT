package bicat.run_machine;

import bicat.gui.BicatGui;

/**
 * <p>
 * Title: BicAT Tool
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @author Amela Prelic, Simon Barkow
 * @version 1.1
 * 
 * Arguments for BiMax. BiMax needs the discretized matrix to be able to run.
 * 
 */

public class ArgumentsBiMax extends Arguments {

	int[][] binaryData;

	int lower_genes;

	int lower_chips;

	boolean extended;

	// ===========================================================================
	public ArgumentsBiMax() {
	}

	// ===========================================================================
	public void setBinaryData(int[][] d) {
		binaryData = d;
	}

	public void setMinGenes(int m) {
		lower_genes = m;
	}

	public void setMinChips(int m) {
		lower_chips = m;
	}

	public void setExtended(boolean b) {
		extended = b;
	}

	// ===========================================================================
	public int[][] getBinaryData() {
		return binaryData;
	}

	public int[] getBinaryDataAsVector() {
		int[][] bD = getBinaryData();
		int l = bD.length;
		int w = bD[0].length;
		int[] binaryDataAsVector = new int[l*w];
		for (int i = 0; i < l; i++) {
			for (int j = 0; j < w; j++) {
				binaryDataAsVector[i*w+j]=bD[i][j];
				if(BicatGui.debug)System.out.println(bD[i][j] + " ");
			}
		}
		if(BicatGui.debug)System.out.println("Length of Vector: "+ binaryDataAsVector.length + " length of original Matrix: "+l+" width: "+w);
		return binaryDataAsVector;
	}

	public int getMinGenes() {
		return lower_genes;
	}

	public int getMinChips() {
		return lower_chips;
	}

	// ===========================================================================
	public boolean isExtended() {
		return extended;
	}

	public int getNumberGenes() {
		return binaryData.length;
	}

	public int getNumberChips() {
		return binaryData[0].length;
	}

}