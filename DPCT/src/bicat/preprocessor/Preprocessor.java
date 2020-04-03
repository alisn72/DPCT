package bicat.preprocessor;

import bicat.gui.BicatGui;
import bicat.util.BicatError;
import bicat.util.PreUtil;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;
import java.util.Vector;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * @author D. Frick, A. Prelic
 * @version 1.0
 **/

/**
 * Preprocessor is used to read data from files and perform initial steps such
 * as normalization, log operations and discretization of the original data
 * matrix.
 *
 * The methods in this class are generally called from the governing
 * <code>bicat.gui.BicatGui</code> object. The <code>Preprocessor</code>
 * will keep a copy of the original data at all times, allowing the user to
 * experiment with different preprocessing steps before running any clustering
 * algorithms.
 * <p>
 * Once the files have been read, the files need to be preprocessed, so that the
 * biclustering can be run on it.
 * <p>
 *
 * @author A. Prelic, D.Frick
 * @version 0.1
 *
 */

public class Preprocessor {

    static BicatGui owner;

    public static PreprocessOption preOption; // maintains the current

    public static boolean dataContainsNegValues;

    // preprocessing setting.

    /** Data from the main data file. */
    static float[][] rawData;

    static float[][] preprocessedData;

    static int[][] discretizedData;

    /**
     * Contains gene names, as read from main data file.<br>
     *
     * When the data matrix has more than a single column header (for genes,
     * normally) the last header column is assumed to contain the gene labels.
     *
     * @todo WISH FEATURE (AP): Relax Column / Row Offset requirement (allow the
     *       user to choose what is the gene column in the matrix).
     *
     */
    public String[] geneNames;

    /** Contains the labels of the <code>fileOffset</code> column headers. */
    public static Vector labels;

    /** Contains the column header information. */
    static Vector labelArrays;

    /**
     * Contains chip names, as read from main data file (chip names are read
     * from the header row of the input file; the first chip name follows at
     * offset <code>fileOffset</code>).<br>
     *
     */
    static String[] chipNames;

    /** Chip names with dependencies (no control chip). */
    static String[] workChipNames;

    /**
     * Chip to Control mappings.<br>
     *
     * Element <code>i</code> is the index of the control value for chip
     * <code>i</code>, and -1 if chip <code>i</code> is a control chip.
     */
    static int[] dependencies;

    static float MISSING_VALUE = 0;

    static int DISCRETE_MISSING_VALUE = 0; // 9;

    static boolean chipToControlsLoaded = false;

    // ===========================================================================

    /**
     * Default constructor, initializes some values.
     *
     */
    public Preprocessor() {

        rawData = null;
        preprocessedData = null;
        discretizedData = null;

        labels = null;
        labelArrays = null;

        preOption = new PreprocessOption();
    }

    public Preprocessor(BicatGui o) {
        this();
        owner = o;
    }

    /**
     * Reads the input data file and saves it locally, for management and
     * visualization purposes.<br>
     *
     * Input file format must be as follows:
     * <ul>
     * <li>The first line contains <code>offset</code> header columns,
     * followed by the chip names (all separated by tabs; whitespaces are also
     * allowed);
     * <li>Whitespace is defined as exactly one tab or space character;
     * <li>All names and data values may <b>not</b> contain any whitespace;
     * <li>Two consecutive whitespaces mark a missing data value. These
     * omissions are duly noted by the program.
     * </ul>
     *
     */
    public void readMainDataFile(File inputFile, int colOffset,
                                 int rowOffset) throws FileOffsetException {
        // tab-separated input file
        try {

//            System.out.println("\nReading datafile...");
            // System.out.println("File Offset: " + colOffset);

            FileReader fr = new FileReader(inputFile);
            LineNumberReader lnr = new LineNumberReader(fr);

            Vector dataVector = new Vector();
            Vector geneNameVector = new Vector();

            String[] fragments;
            float[] data;
            dataContainsNegValues = false;

            // each line: replace whitespaces with blank space character; split
            // the line
            String s = lnr.readLine(); // read top line with chip names
            s = s.replaceAll(" ", ""); // delete all spaces
            s = s.replace((char) 9, (char) 32); // replace tabs with spaces
            fragments = s.split(" ", -1); // ... and split line along its
            // spaces, reading all chip names

            // initialize the header columns information
            labels = new Vector(colOffset);
            for (int i = 0; i < colOffset; i++)
                labels.add(fragments[i]);
            labelArrays = new Vector(colOffset);
            for (int i = 0; i < colOffset; i++)
                labelArrays.add(new Vector());

            if (colOffset > 0) {
                chipNames = new String[fragments.length - colOffset];
                for (int i = colOffset; i < fragments.length; i++) {
                    chipNames[i - colOffset] = fragments[i];
                }
            } else {
                chipNames = new String[fragments.length];
                for (int i = 0; i < fragments.length; i++) {
                    chipNames[i] = "Chip " + i + 1;
                }
            }

            for (int i = 1; i < rowOffset; i++)
                lnr.readLine();

            if (owner.debug)
                for (int i = 0; i < chipNames.length; i++)
                    System.out.println(chipNames[i]);

            s = lnr.readLine(); // read first line of real data

            while ((null != s) && (0 != s.length())) {

                // each line: replace whitespaces with blank space character;
                // split the line
                s = s.trim();
                // char[] ca = s.toCharArray();
                // char c = ca[0];
                // if (c == (char) 32) {
                // BicatError.spaceError();
                // }
                s = s.replace((char) 9, (char) 32);
                fragments = s.split(" ", -1);

                data = new float[fragments.length - colOffset]; // initialize
                // array for next line of data values..
                dataVector.add(data); // .. and append said array to the
                // dataVector

                // update column headers...
                for (int i = 0; i < colOffset; i++)
                    ((Vector) labelArrays.get(i)).add(fragments[i]);

                // assume the last header column are the gene names
                geneNameVector.add(fragments[colOffset - 1]);
                // value at index offset in line is gene name, insert into
                // geneNameVector

                // all following values are samples
                for (int i = colOffset; i < fragments.length; i++) {
                    if (0 == fragments[i].length())
                        data[i - colOffset] = MISSING_VALUE;// Float.NaN;
                    else if (null == fragments[i])
                        data[i - colOffset] = MISSING_VALUE;// Float.NaN;
                    else
                        data[i - colOffset] = Float.parseFloat(fragments[i]);
                }

                s = lnr.readLine();
            }

            // trim empty elements off all the vectors
            dataVector.trimToSize();
            geneNameVector.trimToSize();

            geneNames = new String[geneNameVector.size()];

            rawData = new float[dataVector.size()][chipNames.length];
            for (int i = 0; i < dataVector.size(); i++) {
                data = (float[]) dataVector.get(i);
                geneNames[i] = (String) geneNameVector.get(i);
                for (int j = 0; j < chipNames.length; j++)
                    rawData[i][j] = data[j];
            }
            // preprocessedData = rawData;

            preprocessedData = new float[rawData.length][rawData[0].length];
            for (int i = 0; i < rawData.length; i++)
                for (int j = 0; j < rawData[0].length; j++)
                    preprocessedData[i][j] = rawData[i][j];

            // discretizedData = new int[dataVector.size()][chipNames.length];
            // originalData = rawData;

            workChipNames = chipNames;

            lnr.close();
        } catch (FileNotFoundException e) {
            BicatError.errorMessage(e, owner, true, "File not found");
        } catch (NumberFormatException e) {
            System.err.println(e);
            throw new FileOffsetException();
        } catch (IOException e) {
            System.err.println(e);
            // System.exit(1);
        }
    }

    // ************************************************************************
    // //
    // * * //
    // * Pre-processor: Preprocess Data... * //
    // * * //
    // ************************************************************************
    // //

    public void resetData() {

        preprocessedData = new float[rawData.length][rawData[0].length];
        for (int i = 0; i < rawData.length; i++)
            for (int j = 0; j < rawData[0].length; j++)
                preprocessedData[i][j] = rawData[i][j];

        // preprocessedData = rawData; //originalData;
        discretizedData = null;
    }

    public void resetData(float[][] d) {
        rawData = new float[d.length][d[0].length];
        preprocessedData = new float[d.length][d[0].length];
        for (int i = 0; i < d.length; i++)
            for (int j = 0; j < d[0].length; j++) {
                rawData[i][j] = d[i][j]; // ???
                preprocessedData[i][j] = d[i][j];
            }
        discretizedData = null;
    }

    public static boolean extended = false;

    // ===========================================================================
    public static void preprocessData(PreprocessOption po, double gamma) {

        preOption = new PreprocessOption(po); // all false!
        boolean negValues = false;
        po.discretizationThreshold = gamma;

        // check if the data to be preprocessed has
        // negative values
        for (int i = 0; i < preprocessedData[0].length; i++)
            for (int j = 0; j < preprocessedData.length; j++)
                if (preprocessedData[j][i] <= 0) {
                    negValues = true;
                }

        // if(po.do_compute_ratio) computeRatio();
        if (po.do_compute_logarithm) {
            if (negValues == true) {
                JOptionPane
                        .showMessageDialog(
                                null,
                                "The data contains negative values\nThe logarithm cannot be calculated.\nMaybe the data are already logarithmized");
            } else {
                computeLogarithm(po.logarithmBase);
            }
        }

        // as of 22.03.2005: if(po.do_normalize)
        // normalize(po.normalizationScheme);
        if (po.do_normalize) {
            if (po.do_normalize_genes)
                normalize(po.normalizationScheme, 0);
            if (po.do_normalize_chips)
                normalize(po.normalizationScheme, 1);
        }

        if (po.do_discretize && po.discretizationMode == "threshold") {
            System.out.println("Discretize by threshold");
            if (po.discretizationScheme == PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_COEXPRESSED) {
                extended = true;
                discretizeByThresholdExtended(po.discretizationThreshold);
            } else {
                extended = false;
                discretizeByThreshold(po.discretizationThreshold,
                        po.discretizationScheme);
            }
            preOption.setDiscreteMatrix(discretizedData);
        }

        if (po.do_discretize && po.discretizationMode == "onesPercentage") {
            System.out.println("Discretize by onesPercentage");
            System.out.println("po.onesPercentage: " + po.onesPercentage);
            if (po.discretizationScheme == PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_UP)
                po.discretizationThreshold = computeThreshold(po.onesPercentage);
            if (po.discretizationScheme == PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_DOWN)
                po.discretizationThreshold = computeThreshold(100 - po.onesPercentage);
            if (po.discretizationScheme == PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_CHANGE)
                po.discretizationThreshold = computeThresholdChange(po.onesPercentage);

            discretizeByThreshold(po.discretizationThreshold, po.discretizationScheme);
            preOption.setDiscreteMatrix(discretizedData);
        }
        if (po.do_discretize && po.discretizationMode == "nominal") {
//            System.out.println("nominal Discretion");
            switch (po.discretizationScheme) {
                case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_GENE_EXPRESSION_AVERAGE:
                    discretizeByGene_Expression_Average();
                case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_GENE_MID_RANGE:
                    discretizeByGene_Mid_Range();
                case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_GENE_EXPRESSION_MEAN_STD:
                    discretizeByGene_Expression_Mean_Std(gamma);
                case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_TRANSITIONAL_STATE_DISCRIMINATION:
                case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_VARIATION_BETWEEN_TIME_POINTS_TWO_SYMBOLS:
                    discretizeBy_Variation_Between_Time_Points_Two_Symbols(gamma);
                case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_VARIATION_BETWEEN_TIME_POINTS_THREE_SYMBOLS:
                    discretizeBy_Variation_Between_Time_Points_Three_Symbols(gamma);
                case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_VARIATION_BETWEEN_TIME_POINTS_THREE_SYMBOLS_NEW:
                    discretizeBy_Variation_Between_Time_Points_Three_Symbols_new(gamma);
                case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_By_Active_Threshold:
                    discretizeBy_Active_Threshold();
            }
            preOption.setDiscreteMatrix(discretizedData);
            //discretizedData
        }
        // set the matrices...

        preOption.setPreprocessedMatrix(preprocessedData);
    }

    private static void discretizeByGene_Expression_Mean_Std(double gamma) {
        int gCnt = preprocessedData.length;
        int cCnt = preprocessedData[0].length;
        discretizedData = new int[gCnt][cCnt];
        double[] ave_gene = new double[gCnt];
        double[] standard_Deviation_gene = new double[gCnt];
        for (int i = 0; i < gCnt; i++) {
            for (int j = 0; j < cCnt; j++)
                ave_gene[i] += preprocessedData[i][j];
            ave_gene[i] /= cCnt;
        }
        for (int i = 0; i < gCnt; i++) {
            for (int j = 0; j < cCnt; j++)
                standard_Deviation_gene[i] += Math.pow(Math.abs(preprocessedData[i][j] - ave_gene[i]), 2);
            standard_Deviation_gene[i] = Math.sqrt(standard_Deviation_gene[i] / cCnt);
        }
        for (int i = 0; i < gCnt; i++) {
            for (int j = 0; j < cCnt; j++) {
                double t = Math.abs(ave_gene[i] - gamma * standard_Deviation_gene[i]);
                if (preprocessedData[i][j] < t)
                    discretizedData[i][j] = 0;
                else
                    discretizedData[i][j] = 1;
            }
        }
    }

    public static void preprocessData(PreprocessOption po) {

        preOption = new PreprocessOption(po); // all false!
        boolean negValues = false;

        // check if the data to be preprocessed has
        // negative values
        for (int i = 0; i < preprocessedData[0].length; i++)
            for (int j = 0; j < preprocessedData.length; j++)
                if (preprocessedData[j][i] <= 0) {
                    negValues = true;
                }

        // if(po.do_compute_ratio) computeRatio();
        if (po.do_compute_logarithm) {
            if (negValues == true) {
                JOptionPane
                        .showMessageDialog(
                                null,
                                "The data contains negative values\nThe logarithm cannot be calculated.\nMaybe the data are already logarithmized");
            } else {
                computeLogarithm(po.logarithmBase);
            }
        }

        // as of 22.03.2005: if(po.do_normalize)
        // normalize(po.normalizationScheme);
        if (po.do_normalize) {
            if (po.do_normalize_genes)
                normalize(po.normalizationScheme, 0);
            if (po.do_normalize_chips)
                normalize(po.normalizationScheme, 1);
        }

        if (po.do_discretize && po.discretizationMode == "threshold") {
            System.out.println("Discretize by threshold");
            if (po.discretizationScheme == PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_COEXPRESSED) {
                extended = true;
                discretizeByThresholdExtended(po.discretizationThreshold);
            } else {
                extended = false;
                discretizeByThreshold(po.discretizationThreshold,
                        po.discretizationScheme);
            }
            preOption.setDiscreteMatrix(discretizedData);
        }

        if (po.do_discretize && po.discretizationMode == "onesPercentage") {
            System.out.println("Discretize by onesPercentage");
            System.out.println("po.onesPercentage: " + po.onesPercentage);
            if (po.discretizationScheme == PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_UP)
                po.discretizationThreshold = computeThreshold(po.onesPercentage);
            if (po.discretizationScheme == PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_DOWN)
                po.discretizationThreshold = computeThreshold(100 - po.onesPercentage);
            if (po.discretizationScheme == PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_CHANGE)
                po.discretizationThreshold = computeThresholdChange(po.onesPercentage);

            discretizeByThreshold(po.discretizationThreshold, po.discretizationScheme);
            preOption.setDiscreteMatrix(discretizedData);
        }
        if (po.do_discretize && po.discretizationMode == "nominal") {
//            System.out.println("nominal Discretion");
            switch (po.discretizationScheme) {
                case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_GENE_EXPRESSION_AVERAGE:
                    discretizeByGene_Expression_Average();
                case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_GENE_MID_RANGE:
                    discretizeByGene_Mid_Range();
                case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_GENE_EXPRESSION_MEAN_STD:
                    discretizeByGene_Expression_Mean_Std(0.2);
                case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_TRANSITIONAL_STATE_DISCRIMINATION:
                case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_VARIATION_BETWEEN_TIME_POINTS_TWO_SYMBOLS:
                    discretizeBy_Variation_Between_Time_Points_Two_Symbols(0.2);
                case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_VARIATION_BETWEEN_TIME_POINTS_THREE_SYMBOLS:
            }
            preOption.setDiscreteMatrix(discretizedData);
            //discretizedData
        }
        // set the matrices...

        preOption.setPreprocessedMatrix(preprocessedData);
    }

    /**
     * Compute the logarithm of the data.
     *
     * PRE-CONDITION: preprocessedData must be initialized.
     *
     */
    static void computeLogarithm(int base) {

        for (int i = 0; i < preprocessedData[0].length; i++)
            for (int j = 0; j < preprocessedData.length; j++)
                if (preprocessedData[j][i] != Float.NaN
                        && preprocessedData[j][i] != 0)
                    preprocessedData[j][i] = (float) (Math
                            .log((double) preprocessedData[j][i]) / base);
    }

    // if(po.do_normalize) normalize(po.normalizationScheme);

    /**
     * Compute the normalization on the data.
     *
     * PRE-CONDITION: preprocessedData must be initialized.
     *
     */
    static void normalize(int scheme) {

        switch (scheme) {
            case PreprocessOption.PREPROCESS_OPTIONS_NORMALIZATION_IT:
                // normalize_InfoTheory(preprocessedData);
                break;
            case PreprocessOption.PREPROCESS_OPTIONS_NORMALIZATION_MIXTURE:
                // normalize_Mixture(preprocessedData);
                break;
            case PreprocessOption.PREPROCESS_OPTIONS_NORMALIZATION_MEAN_CENTRING:
                normalize_MeanCentring(preprocessedData, false);
                break;
            default:
                break;
        }
    }

    // static boolean dataPreprocessed_Genes = false;

    static void normalize(int scheme, int direction) {

        switch (scheme) {
            case PreprocessOption.PREPROCESS_OPTIONS_NORMALIZATION_IT:
                // normalize_InfoTheory(preprocessedData);
                break;
            case PreprocessOption.PREPROCESS_OPTIONS_NORMALIZATION_MIXTURE:
                // normalize_Mixture(preprocessedData);
                break;
            case PreprocessOption.PREPROCESS_OPTIONS_NORMALIZATION_MEAN_CENTRING:
                if (direction == 0)
                    normalize_MeanCentring(preprocessedData);
                if (direction == 1)
                    normalize_MeanCentring(preprocessedData, true);
                break;
            default:
                break;
        }
    }

    // ... as of 22.03.2005: IMAM OSJECAJ DA NESTO NIJE U REDU, S OVOM
    // IMPLEMENTACIJOM,... check
    static void normalize_MeanCentring(float[][] data) {

        if (owner.debug)
            System.out.println("D: Preprocessor.normalize_MeanCentring,GENES: "
                    + data.length + ", " + data[0].length);

        int gCnt = data.length;
        int cCnt = data[0].length;

        // correct the means (per gene)
        float[] row_means = new float[gCnt];
        for (int i = 0; i < gCnt; i++) {
            int sum = 0;
            for (int j = 0; j < cCnt; j++)
                sum += data[i][j];
            row_means[i] = sum / cCnt;
        }

        for (int i = 0; i < gCnt; i++)
            for (int j = 0; j < cCnt; j++)
                data[i][j] -= row_means[i];

        // compute the variance
        float[] row_vars = new float[gCnt];
        for (int i = 0; i < gCnt; i++)
            row_vars[i] = PreUtil.computeVariance(data[i]);

        for (int i = 0; i < gCnt; i++)
            for (int j = 0; j < cCnt; j++)
                if (row_vars[i] != 0)
                    // / ??? data[i][j] = data[i][j] / row_vars[i];
                    preprocessedData[i][j] = data[i][j] / row_vars[i];
                else
                    /* what to do? */;
    }

    // ... new, as of 22.03.2005:
    static void normalize_MeanCentring(float[][] data, boolean chips) { // DA LI
        // JE
        // OVO
        // PRAVILNO
        // ?

        if (owner.debug)
            System.out.println("D: Preprocessor.normalize_MeanCentring,CHIPS: "
                    + data.length + ", " + data[0].length);

        int gCnt = data.length;
        int cCnt = data[0].length;

        // correct the means (per gene)
        float[] col_means = new float[cCnt];
        for (int i = 0; i < cCnt; i++) {
            int sum = 0;
            for (int j = 0; j < gCnt; j++)
                sum += data[j][i];
            col_means[i] = sum / gCnt;
        }

        for (int i = 0; i < cCnt; i++)
            for (int j = 0; j < gCnt; j++)
                data[j][i] -= col_means[i];

        // compute the variance
        float[] col_vars = new float[cCnt];
        for (int i = 0; i < cCnt; i++) {
            float[] column = new float[gCnt];
            for (int k = 0; k < gCnt; k++)
                column[k] = data[k][i];
            col_vars[i] = PreUtil.computeVariance(column);
        }

        for (int i = 0; i < cCnt; i++)
            for (int j = 0; j < gCnt; j++)
                if (col_vars[i] != 0)
                    preprocessedData[j][i] = data[j][i] / col_vars[i];
                else
                    /* what to do? */
                    ;
    }

    /**
     * Applies a threshold to discretize the processed data into 1s and 0s,
     * marking missing values with a <code>DISCRETE_MISSING_VALUE</code>. The
     * preprocessed data matrix is copied four times, to obtain the extended
     * discretized matrix.
     *
     */
    static void discretizeByThresholdExtended(double threshold) {

        if (owner.debug)
            System.out.println("Threshold ist = " + threshold);

        // scheme ==
        // PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_COEXPRESSED

        int gCnt = preprocessedData.length;
        int cCnt = preprocessedData[0].length;

        discretizedData = new int[2 * gCnt][2 * cCnt];

        for (int i = 0; i < 2 * gCnt; i++)
            for (int j = 0; j < 2 * cCnt; j++) {

                // determine the quadrant and discretize accordingly!
                // discretized matrix:
                // A | B
                // -----
                // C | D

                // A or D
                if ((i < gCnt && j < cCnt) || (i >= gCnt && j >= cCnt)) {

                    if (preprocessedData[i % gCnt][j % cCnt] == Float.NaN)
                        discretizedData[i][j] = DISCRETE_MISSING_VALUE;
                    else if (threshold < preprocessedData[i % gCnt][j % cCnt]) {
                        discretizedData[i][j] = 1;
                        if (owner.enlargeContrast)
                            preprocessedData[i % gCnt][j % cCnt] += owner.CONTRAST_VALUE; // up-regulated
                    } else
                        discretizedData[i][j] = 0;
                }

                // B or C
                else if ((i >= gCnt && j < cCnt) || (i < gCnt && j >= cCnt)) {

                    if (preprocessedData[i % gCnt][j % cCnt] == Float.NaN)
                        discretizedData[i][j] = DISCRETE_MISSING_VALUE;
                    else if (
                        /*
                         * preprocessedData[i % gCnt][j % cCnt] < 0 && threshold <
                         * Math.abs(preprocessedData[i % gCnt][j % cCnt])
                         */
                            (0 - threshold) > preprocessedData[i % gCnt][j % cCnt]) {
                        discretizedData[i][j] = -1;
                        if (owner.enlargeContrast)
                            preprocessedData[i % gCnt][j % cCnt] -= owner.CONTRAST_VALUE; // down-regulated
                    } else
                        discretizedData[i][j] = 0;
                }
            }
    }

    /**
     * Applies a threshold to discretize the processed data into 1s and 0s,
     * marking missing values with a <code>DISCRETE_MISSING_VALUE</code>.
     *
     */
    private static void discretizeByThreshold(double threshold, int scheme) {

        if (owner.debug)
            System.out.println("Threshold ist = " + threshold);

        int gCnt = preprocessedData.length;
        int cCnt = preprocessedData[0].length;

        discretizedData = new int[gCnt][cCnt];
        switch (scheme) {

            case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_UP:

                for (int i = 0; i < gCnt; i++)
                    for (int j = 0; j < cCnt; j++)
                        if (preprocessedData[i][j] == Float.NaN)
                            discretizedData[i][j] = DISCRETE_MISSING_VALUE;
                        else {
                            if (threshold < preprocessedData[i][j]) {
                                discretizedData[i][j] = 1;
                                if (owner.enlargeContrast)
                                    preprocessedData[i][j] += owner.CONTRAST_VALUE;
                            } else
                                discretizedData[i][j] = 0;
                        }
                break;

            case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_CHANGE:

                for (int i = 0; i < gCnt; i++)
                    for (int j = 0; j < cCnt; j++)
                        if (preprocessedData[i][j] == Float.NaN)
                            discretizedData[i][j] = DISCRETE_MISSING_VALUE;
                        else {
                            if (threshold < Math.abs(preprocessedData[i][j])) {
                                discretizedData[i][j] = 1; /*
                                 * hier, lasse ich es
                                 * so!
                                 */
                                if (owner.enlargeContrast)
                                    preprocessedData[i][j] += owner.CONTRAST_VALUE;
                            } else {
                                discretizedData[i][j] = 0;
                                if (owner.enlargeContrast)
                                    preprocessedData[i][j] -= owner.CONTRAST_VALUE;
                            }
                        }
                break;

            case PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_DOWN:

                for (int i = 0; i < gCnt; i++)
                    for (int j = 0; j < cCnt; j++)
                        if (preprocessedData[i][j] == Float.NaN)
                            discretizedData[i][j] = DISCRETE_MISSING_VALUE;
                        else {
                            if ((0 - threshold) > preprocessedData[i][j]) {
                                discretizedData[i][j] = -1;
                                if (owner.enlargeContrast)
                                    preprocessedData[i][j] -= owner.CONTRAST_VALUE;
                            } else {
                                discretizedData[i][j] = 0;
                            }
                        }
                break;

            default:
                break;
        }
    }


    private static double computeThreshold(int onesPercentage) {

        int gCnt = preprocessedData.length;
        int cCnt = preprocessedData[0].length;
        int numberOfValues = gCnt * cCnt;
        int numOnes = 0;
        double threshold = 0.0;
        float[] preprocessedData_1d = new float[numberOfValues + 1];

        // Calculate number of ones
        numOnes = Math.round(numberOfValues * onesPercentage / 100);

        int k = 0;
        for (int i = 0; i < gCnt; i++)
            for (int j = 0; j < cCnt; j++) {
                k++;
                preprocessedData_1d[k] = preprocessedData[i][j];
                // System.out.println(k);
            }
        // sort Array
        Arrays.sort(preprocessedData_1d);
        // Calculate threshold
        threshold = preprocessedData_1d[numberOfValues - numOnes];

        if (BicatGui.debug) {
            System.out.println("DEBUG discrization by percentage of ones:");
            System.out.println("numberOfValues: " + numberOfValues);
            System.out.println("onesPercentage: " + onesPercentage);
            System.out.println("Number of Ones: " + numOnes);
            System.out.println("Length of 1D-Array: "
                    + preprocessedData_1d.length);
            System.out.println("Position 1: " + preprocessedData_1d[1]);
            System.out.println("Position 100: " + preprocessedData_1d[100]);
            System.out.println("Position 1000: " + preprocessedData_1d[1000]);
        }
        return threshold;

    }

    private static double computeThresholdChange(int onesPercentage) {
        int gCnt = preprocessedData.length;
        int cCnt = preprocessedData[0].length;
        int numberOfValues = gCnt * cCnt;
        int numOnes = 0;
        double threshold = 0.0;
        float[] preprocessedData_1d = new float[numberOfValues + 1];

        // Calculate number of ones
        numOnes = Math.round(numberOfValues * onesPercentage / 100);

        int k = 0;
        for (int i = 0; i < gCnt; i++)
            for (int j = 0; j < cCnt; j++) {
                k++;
                preprocessedData_1d[k] = Math.abs(preprocessedData[i][j]);

            }
        // sort Array
        Arrays.sort(preprocessedData_1d);
        // Calculate threshold
        threshold = preprocessedData_1d[numberOfValues - numOnes + 1];

        if (BicatGui.debug) {
            System.out.println("DEBUG discrization by percentage of ones:");
            System.out.println("numberOfValues: " + numberOfValues);
            System.out.println("onesPercentage: " + onesPercentage);
            System.out.println("Number of Ones: " + numOnes);
            System.out.println("Length of 1D-Array: "
                    + preprocessedData_1d.length);
            System.out.println("Position 1: " + preprocessedData_1d[1]);
            System.out.println("Position 100: " + preprocessedData_1d[100]);
            System.out.println("Position 1000: " + preprocessedData_1d[1000]);
        }
        return threshold;
    }

    // ************************************************************************
    // //
    // * * //
    // * Preprocessor: Interface to others ... * //
    // * * //
    // ************************************************************************
    // //

    // related to the current dataset (just loaded) only:

    public static boolean dataReady() {
        return null != rawData;
    }

    public static boolean discreteDataReady() {
        return null != discretizedData;
    }

    public static int getWorkingChipCount() {
        return preprocessedData[0].length;
    }

    public static int getOriginalChipCount() {
        return rawData[0].length;
    }

    public static int getGeneCount() {
        return rawData.length;
    }

    public static float[][] getOriginalData() {
        return rawData;
    }

    public static float[][] getPreprocessedData() {
        return preprocessedData;
    }

    public static int[][] getDiscreteData() {
        return discretizedData;
    }

    // ===========================================================================
    public Vector getGeneNames() {
        Vector gNs = new Vector();
        for (int i = 0; i < geneNames.length; i++)
            gNs.add(geneNames[i]);
        return gNs;
    }

    public static Vector getChipNames() {
        Vector cNs = new Vector();
        for (int i = 0; i < chipNames.length; i++)
            cNs.add(chipNames[i]);
        return cNs;
    }

    public static Vector getWorkingChipNames() {
        Vector cNs = new Vector();
        for (int i = 0; i < workChipNames.length; i++)
            cNs.add(workChipNames[i]);
        return cNs;
    }

    public int[] getDependencies() {
        return dependencies;
    }

    public static Vector getHeaderColumnLabels() {
        return labels;
    }

    public static Vector getHeaderColumns() {
        return labelArrays;
    }

    private static void discretizeByGene_Expression_Average() {

        int gCnt = preprocessedData.length;
        int cCnt = preprocessedData[0].length;
        double[] gene_Average = new double[gCnt];
        discretizedData = new int[gCnt][cCnt];
        for (int i = 0; i < gCnt; i++) {
            for (int j = 0; j < cCnt; j++)
                gene_Average[i] += preprocessedData[i][j];
            gene_Average[i] /= cCnt;
        }
        for (int i = 0; i < gCnt; i++)
            for (int j = 0; j < cCnt; j++)
                if (preprocessedData[i][j] == Float.NaN)
                    discretizedData[i][j] = DISCRETE_MISSING_VALUE;
                else {
                    if (preprocessedData[i][j] >= gene_Average[i]) {
                        discretizedData[i][j] = 1;
                        if (owner.enlargeContrast)
                            preprocessedData[i][j] += owner.CONTRAST_VALUE;
                    } else {
                        discretizedData[i][j] = 0;
                    }
                }


    }

    private static void discretizeByGene_Mid_Range() {

        int gCnt = preprocessedData.length;
        int cCnt = preprocessedData[0].length;
        double[] gene_Max_Range = new double[gCnt];
        double[] gene_Min_Range = new double[gCnt];
        discretizedData = new int[gCnt][cCnt];
        double max, min;
        for (int i = 0; i < gCnt; i++) {
            max = preprocessedData[i][0];
            min = preprocessedData[i][0];
            for (int j = 0; j < cCnt; j++) {
                if (preprocessedData[i][j] > max) max = preprocessedData[i][j];
                if (preprocessedData[i][j] < min) min = preprocessedData[i][j];
            }
            gene_Max_Range[i] = max;
            gene_Min_Range[i] = min;
        }
        for (int i = 0; i < gCnt; i++)
            for (int j = 0; j < cCnt; j++)
                if (preprocessedData[i][j] == Float.NaN)
                    discretizedData[i][j] = DISCRETE_MISSING_VALUE;
                else {
                    if (preprocessedData[i][j] >= (gene_Max_Range[i] - gene_Min_Range[i]) / 2) {
                        discretizedData[i][j] = 1;
                        if (owner.enlargeContrast)
                            preprocessedData[i][j] += owner.CONTRAST_VALUE;
                    } else {
                        discretizedData[i][j] = 0;
                    }
                }


    }

    private static void discretizeBy_Variation_Between_Time_Points_Two_Symbols(double alpha) {
        int gCnt = preprocessedData.length;
        int cCnt = preprocessedData[0].length;
        discretizedData = new int[gCnt][cCnt];
        double ave_time_0 = 0;
        for (int i = 0; i < gCnt; i++) {
            ave_time_0 += preprocessedData[i][0];
        }
        ave_time_0 /= gCnt;
        double standard_Deviation_Time_0 = 0;
        for (int i = 0; i < gCnt; i++) {
            standard_Deviation_Time_0 += Math.pow(preprocessedData[i][0] - ave_time_0, 2);
        }
        standard_Deviation_Time_0 /= gCnt;
        standard_Deviation_Time_0 = Math.sqrt(standard_Deviation_Time_0);
        double threshold = alpha * standard_Deviation_Time_0;
        for (int i = 0; i < gCnt; i++) {
            for (int j = 1; j < cCnt; j++) {
                if (Math.abs(preprocessedData[i][j] - preprocessedData[i][j - 1]) > threshold)
                    discretizedData[i][j] = 1;
                else
                    discretizedData[i][j] = 0;
            }
        }
        for (int i = 0; i < gCnt; i++) {
            if (Math.abs(preprocessedData[i][0] - preprocessedData[i][cCnt - 1]) > threshold)
                discretizedData[i][0] = 1;
            else
                discretizedData[i][0] = 0;
        }
    }

    private static void discretizeBy_Variation_Between_Time_Points_Three_Symbols(double alpha) {
        int gCnt = preprocessedData.length;
        int cCnt = preprocessedData[0].length;
        discretizedData = new int[gCnt][cCnt];
        double ave_time_0 = 0;
        for (int i = 0; i < gCnt; i++) {
            ave_time_0 += preprocessedData[i][0];
        }
        ave_time_0 /= gCnt;
        double standard_Deviation_Time_0 = 0;
        for (int i = 0; i < gCnt; i++) {
            standard_Deviation_Time_0 += Math.pow(preprocessedData[i][0] - ave_time_0, 2);
        }
        standard_Deviation_Time_0 /= gCnt;
        standard_Deviation_Time_0 = Math.sqrt(standard_Deviation_Time_0);
        double threshold = alpha * standard_Deviation_Time_0;
        for (int i = 0; i < gCnt; i++) {
            for (int j = 1; j < cCnt; j++) {
                if (Math.abs(preprocessedData[i][j] - preprocessedData[i][j - 1]) > threshold)
                    discretizedData[i][j] = 1;
                else if (Math.abs(preprocessedData[i][j] - preprocessedData[i][j - 1]) < threshold)
                    discretizedData[i][j] = -1;
                else
                    discretizedData[i][j] = 0;
            }
        }
        for (int i = 0; i < gCnt; i++) {
            if (Math.abs(preprocessedData[i][0] - preprocessedData[i][cCnt - 1]) > threshold)
                discretizedData[i][0] = 1;
            else if (Math.abs(preprocessedData[i][0] - preprocessedData[i][cCnt - 1]) < threshold)
                discretizedData[i][0] = -1;
            else
                discretizedData[i][0] = 0;
        }
    }

    private static void discretizeBy_Variation_Between_Time_Points_Three_Symbols_new(double alpha) {
//        int gCnt = preprocessedData.length;
//        int cCnt = preprocessedData[0].length;
//        discretizedData = new int[gCnt][cCnt];
//        double[] ave_gene = new double[gCnt];
//        double[] standard_Deviation_gene = new double[gCnt];
//        for (int i = 0; i < gCnt; i++) {
//            for (int j = 0; j < cCnt; j++)
//                ave_gene[i] += preprocessedData[i][j];
//            ave_gene[i] /= cCnt;
//        }
//        for (int i = 0; i < gCnt; i++) {
//            for (int j = 0; j < cCnt; j++)
//                standard_Deviation_gene[i] += Math.pow(Math.abs(preprocessedData[i][j]-ave_gene[i]),2);
//            standard_Deviation_gene[i] = Math.sqrt(standard_Deviation_gene[i]/cCnt);
//        }
//        for (int i = 0; i < gCnt; i++) {
//            for (int j = 1; j < cCnt; j++) {
//                if(Math.abs(preprocessedData[i][j]-preprocessedData[i][j-1]) > threshold)
//                    discretizedData[i][j] = 1;
//                else if(Math.abs(preprocessedData[i][j]-preprocessedData[i][j-1]) < threshold)
//                    discretizedData[i][j] = -1;
//                else
//                    discretizedData[i][j] = 0;
//            }
//        }
//        for (int i = 0; i < gCnt; i++) {
//            if(Math.abs(preprocessedData[i][0]-preprocessedData[i][cCnt-1]) > threshold)
//                discretizedData[i][0] = 1;
//            else if(Math.abs(preprocessedData[i][0]-preprocessedData[i][cCnt-1]) < threshold)
//                discretizedData[i][0] = -1;
//            else
//                discretizedData[i][0] = 0;
//        }
    }

    private static void discretizeBy_Active_Threshold() {
        int gCnt = preprocessedData.length;
        int cCnt = preprocessedData[0].length;
        discretizedData = new int[gCnt][cCnt];
        double[] ave_genes = new double[gCnt];
        double[] ave_cons = new double[cCnt];
        for (int i = 0; i < gCnt; i++) {
            for (int j = 0; j < cCnt; j++)
                ave_genes[i] += preprocessedData[i][j];
            ave_genes[i] /= cCnt;
        }
        for (int j = 0; j < cCnt; j++) {
            for (int i = 0; i < gCnt; i++)
                ave_cons[j] += preprocessedData[i][j];
            ave_cons[j] /= gCnt;
        }
        for (int i = 0; i < gCnt; i++) {
            for (int j = 0; j < cCnt; j++)
                if (preprocessedData[i][j] > Math.max(ave_genes[i], ave_cons[j]))
                    discretizedData[i][j] = 1;
                else if (preprocessedData[i][j] < Math.min(ave_genes[i], ave_cons[j]))
                    discretizedData[i][j] = -1;
                else
                    discretizedData[i][j] = 0;
        }
    }
}
