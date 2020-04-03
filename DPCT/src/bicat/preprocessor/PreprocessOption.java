package bicat.preprocessor;

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

public class PreprocessOption {

	public boolean do_compute_ratio;
	public boolean do_compute_logarithm;
	public boolean do_normalize;
	public boolean do_normalize_genes;
	public boolean do_normalize_chips;
	public boolean do_discretize;
	public int logarithmBase;
	public int normalizationScheme;
	public int discretizationScheme;
	public String discretizationMode;
	public int onesPercentage;
	static int defaultOnesPercentage = 10;
	public double discretizationThreshold;
	static int defaultLogBase = 2;
	static double defaultDiscrThr = 0.55;
	public int[][] discreteMatrix;
	public float[][] preprocessedMatrix;
	public static final int PREPROCESS_OPTIONS_NORMALIZATION_MEAN_CENTRING = 1;
	public static final int PREPROCESS_OPTIONS_NORMALIZATION_MIXTURE = 4;
	public static final int PREPROCESS_OPTIONS_NORMALIZATION_IT = 5;
	public static final int PREPROCESS_OPTIONS_DISCRETIZATION_CHANGE = 4;
	public static final int PREPROCESS_OPTIONS_DISCRETIZATION_UP = 1;
	public static final int PREPROCESS_OPTIONS_DISCRETIZATION_DOWN = 2;
	public static final int PREPROCESS_OPTIONS_DISCRETIZATION_COEXPRESSED = 3;
    public static final int PREPROCESS_OPTIONS_DISCRETIZATION_GENE_EXPRESSION_AVERAGE = 6;
    public static final int PREPROCESS_OPTIONS_DISCRETIZATION_GENE_MID_RANGE = 7;
    public static final int PREPROCESS_OPTIONS_DISCRETIZATION_GENE_EXPRESSION_MEAN_STD= 8;
    public static final int PREPROCESS_OPTIONS_DISCRETIZATION_TRANSITIONAL_STATE_DISCRIMINATION= 9;
    public static final int PREPROCESS_OPTIONS_DISCRETIZATION_VARIATION_BETWEEN_TIME_POINTS_TWO_SYMBOLS = 10;
    public static final int PREPROCESS_OPTIONS_DISCRETIZATION_VARIATION_BETWEEN_TIME_POINTS_THREE_SYMBOLS = 11;
    public static final int PREPROCESS_OPTIONS_DISCRETIZATION_VARIATION_BETWEEN_TIME_POINTS_THREE_SYMBOLS_NEW = 12;
    public static final int PREPROCESS_OPTIONS_DISCRETIZATION_By_Active_Threshold = 13;

    // ===========================================================================
	/**
	 * Constructor: assumes that the default setting is to be used (common for
	 * oligonucleids microarrays: (eventually) normalize, discretize).
	 * 
	 */
	public PreprocessOption(String def) {

		if (def.equals("default")) {
			do_compute_ratio = false; // true;
			do_compute_logarithm = true; // true;
			do_normalize = true;
			do_discretize = true;

			logarithmBase = defaultLogBase;
			normalizationScheme = PREPROCESS_OPTIONS_NORMALIZATION_MEAN_CENTRING;
			discretizationScheme = PREPROCESS_OPTIONS_DISCRETIZATION_COEXPRESSED;
			discretizationThreshold = defaultDiscrThr;
		}
	}

	/**
	 * Constructor: all options are reset (set to false). All user desired
	 * preprocessing settings need to be set manually.
	 * 
	 */
	public PreprocessOption() {

		do_compute_ratio = false;
		do_compute_logarithm = false;
		do_normalize = false;
		do_discretize = false;

		logarithmBase = defaultLogBase;
		normalizationScheme = PREPROCESS_OPTIONS_NORMALIZATION_MEAN_CENTRING;
		discretizationScheme = PREPROCESS_OPTIONS_DISCRETIZATION_UP;//PREPROCESS_OPTIONS_DISCRETIZATION_DOWN;
		discretizationThreshold = defaultDiscrThr;
	}

	/**
	 * Constructor: create a copy of the parameter <code>po</code> object.
	 * 
	 */
	public PreprocessOption(PreprocessOption po) {

		do_compute_ratio = po.do_compute_ratio;
		do_compute_logarithm = po.do_compute_logarithm;
		do_normalize = po.do_normalize;
		do_discretize = po.do_discretize;

		logarithmBase = po.logarithmBase;
		normalizationScheme = po.normalizationScheme;
		discretizationScheme = po.discretizationScheme;
		discretizationThreshold = po.discretizationThreshold;
	}

	// ===========================================================================
	public void setComputeRatio() {
		do_compute_ratio = true;
	}

	public void resetComputeRatio() {
		do_compute_ratio = false;
	}

	// ===========================================================================
	public int[][] getDiscreteMatrix() {
		return discreteMatrix;
	}

	public float[][] getPreprocessedMatrix() {
		return preprocessedMatrix;
	}

	public void setDiscreteMatrix(int[][] data) {
		discreteMatrix = new int[data.length][data[0].length];
			for (int i = 0; i < data.length; i++)
				for (int j = 0; j < data[0].length; j++)
					discreteMatrix[i][j] = data[i][j];
		}
	

	public void setPreprocessedMatrix(float[][] data) {
		preprocessedMatrix = new float[data.length][data[0].length];
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[0].length; j++)
				preprocessedMatrix[i][j] = data[i][j];
	}

	// ===========================================================================
	public void setComputeLogarithm() {
		do_compute_logarithm = true;
	}

	public void resetComputeLogarithm() {
		do_compute_logarithm = false;
	}

	public void setLogarithmBase(int i) {
		logarithmBase = i;
	}

	public void resetLogarithmBase() {
		logarithmBase = defaultLogBase;
	}

	// ===========================================================================
	public void setNormalization() {
		do_normalize = true;
	}

	public void resetNormalization() {
		do_normalize = false;
	}

	public void setGeneNormalization() {
		do_normalize_genes = true;
	}

	public void resetGeneNormalization() {
		do_normalize_genes = false;
	}

	public void setChipNormalization() {
		do_normalize_chips = true;
	}

	public void resetChipNormalization() {
		do_normalize_chips = false;
	}

	public void setNormalizationScheme(int i) {
		normalizationScheme = i;
	}

	public void resetNormalizationScheme() {
		normalizationScheme = PREPROCESS_OPTIONS_NORMALIZATION_MEAN_CENTRING;
	}

	// ===========================================================================
	public void setDiscretizationTrue() {
		do_discretize = true;
	}

	public void resetDiscretization() {
		do_discretize = false;
	}

	public void setDiscretizationScheme(int i) {
		discretizationScheme = i;
	}

	public void resetDiscretizationScheme() {
		discretizationScheme = PREPROCESS_OPTIONS_DISCRETIZATION_COEXPRESSED;
	}

	public void setDiscretizationThreshold(double thr) {
		discretizationThreshold = thr;
	}

	public double getOnesPercentage() {
		return onesPercentage;
	}

	public void setOnesPercentage(int onesPercentage) {
		this.onesPercentage = onesPercentage;
	}

	public void resetOnesPercentage() {
		onesPercentage = defaultOnesPercentage;
	}


	// ===========================================================================

	/** String representation of the preprocess settings. */
	public String toString() {
		StringBuffer sbuff = new StringBuffer();

		sbuff.append("\nPreprocessing Options:\n");
		sbuff.append("  Compute ratio: " + do_compute_ratio + "\n");
		sbuff.append("  Compute log  : " + do_compute_logarithm); // +"
		// ("+logarithmBase+")\n");
		if (do_compute_logarithm)
			sbuff.append(" (base = " + logarithmBase + ")\n");
		else
			sbuff.append("\n");

		String nS = NStoString(normalizationScheme);
		sbuff.append("  Normalize     : " + do_normalize + " (" + nS + ")\n");
		
		String dM = discretizationMode;
		sbuff.append("  Discretization Mode     : " +  dM + "\n");

		String dS = DStoString(discretizationScheme);
		sbuff.append("  Discretize      : " + do_discretize + " (" + dS + ", "
				+ discretizationThreshold + ")\n");
		sbuff.append("\n");

		return sbuff.toString();
	}

	String NStoString(int a) {
		switch (a) {
		case PREPROCESS_OPTIONS_NORMALIZATION_IT:
			return "Info Theory"; // Norm";
		case PREPROCESS_OPTIONS_NORMALIZATION_MEAN_CENTRING:
			return "Mean Centring 0,1"; // Norm";
		case PREPROCESS_OPTIONS_NORMALIZATION_MIXTURE:
			return "Mixture"; // Norm";
		default:
			System.out.println("Undefined Normalization Scheme!\n");
			return null;
		}
	}

	String DStoString(int a) {
		switch (a) {
		case PREPROCESS_OPTIONS_DISCRETIZATION_COEXPRESSED:
			return "Complementary pattern";
		// return "Coexpression Discr";
		case PREPROCESS_OPTIONS_DISCRETIZATION_DOWN:
			return "Down-regulated genes";
		// return "Under-expression Discr";
		case PREPROCESS_OPTIONS_DISCRETIZATION_UP:
			return "Up-regulated genes";
		// return "Over-expression Discr";
		case PREPROCESS_OPTIONS_DISCRETIZATION_CHANGE:
			return "Changed genes";
		default:
			System.out.println("Undefined Discretization Scheme!\n");
			return null;
		}
	}

	public void setDiscretizationMode(String discretizationMode) {
		this.discretizationMode = discretizationMode;
		
	}

}
