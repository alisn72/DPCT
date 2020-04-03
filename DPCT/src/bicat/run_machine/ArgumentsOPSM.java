package bicat.run_machine;

/**
 * <p>
 * Title: BicAT Tool
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * @author Simon Barkow
 * @version 1.0
 * 
 * Arguments for OPSM. 
 * 
 */

public class ArgumentsOPSM extends Arguments {

	int nr_l; //OPSM parameter: Anzahl uebergebener Models

	float[][] myData;

	public ArgumentsOPSM() {
	}

	public void setMyData(float[][] d) {
		myData = d;
	}

	public float[][] getMyData() {
		return myData;
	}

	public int getNumberGenes() {
		return myData.length;
	}

	public int getNumberChips() {
		return myData[0].length;
	}

	public void setl(int l) {
		nr_l = l;
	}

	public int getl() {
		return nr_l;
	}

}