package smadeira.utils;//package smadeira.utils;
//
//import java.awt.*;
//import java.util.*;
//
//import org.jfree.chart.*;
//import org.jfree.chart.axis.*;
//import org.jfree.chart.labels.*;
//import org.jfree.chart.plot.*;
//import org.jfree.chart.renderer.category.*;
//import org.jfree.data.category.*;
//import smadeira.biclustering.*;
//
///**
// * <p>Title: Expression Chart Panel</p>
// *
// * <p>Description: This is a specialized panel which holds an expression chart.
// *                 Allows one to construct specific charts for gene
// *                 expression data.</p>
// *
// * <p>Copyright:   Copyright (C) 2007  Joana Gon�alves
// *                 This program is free software; you can redistribute
// *                 it and/or modify it under the terms of the GNU General
// *                 Public License as published by the Free Software
// *                 Foundation; either version 3 of the License, or
// *                 any later version.
// *
// *                 This program is distributed in the hope that it will
// *                 be useful, but WITHOUT ANY WARRANTY; without even the
// *                 implied warranty of MERCHANTABILITY or FITNESS FOR A
// *                 PARTICULAR PURPOSE.  See the
// *                 <a href="http://www.gnu.org/licenses/gpl.html">
// *                 GNU General Public License</a> for more details.
// * </p>
// *
// * @author Joana P. Gon�alves
// * @version 1.0
// */
//public class PanelExpressionChart extends ChartPanel {
//    /**
//     * Holds the names of the genes for the expression chart
//     * for the following chart types:
//     * - matrix charts (ExpressionMatrix, PreProcessedExpressionMatrix,
//     * DiscretizedExpressionMatrix)
//     * - bicluster all time-points charts (bicluster's genes * all conditions)
//     */
//    private String[] genesNames;
//
//    /**
//     * Holds the names of the conditions for the expression chart
//     * for the following chart types:
//     * - matrix charts (ExpressionMatrix, PreProcessedExpressionMatrix,
//     * DiscretizedExpressionMatrix)
//     * - bicluster all time-points charts (bicluster's genes * all conditions)
//     */
//    private String[] conditionsNames;
//
//    /**
//     * Holds the expression values to be represented in the expression chart
//     * for the following chart types:
//     * - matrix charts (ExpressionMatrix, PreProcessedExpressionMatrix,
//     * DiscretizedExpressionMatrix)
//     * - bicluster all time-points charts (bicluster's genes * all conditions)
//     */
//    private float[][] expressionValues;
//
//    private float missingValueFloat;
//
//    /**
//     * Bicluster. It is null when this chart is a:<br/>
//     * - bicluster all time-points chart (bicluster's genes * all conditions)<br/>
//     * - matrix chart (ExpressionMatrix, PreProcessedExpressionMatrix,
//     * DiscretizedExpressionMatrix)<br/>
//     * It holds a Bicluster object (Bicluster, CCC_Bicluster,
//     * CCC_Bicluster_GeneShifts, CCC_Bicluster_SignChanges,
//     * CCC_Bicluster_Time_Lags, CCC_Bicluster_SignChanges_TimeLags,
//     * E_CCC_Bicluster, E_CCC_Bicluster_GeneShifts,
//     * E_CCC_Bicluster_RestrictedErrors,
//     * E_CCC_Bicluster_GeneShifts_RestrictedErrors) when chart is a:<br/>
//     * - bicluster time-points chart (bicluster's genes * bicluster's conditions)<br/>
//     * - pattern chart (only for CCC_Bicluster and its subclasses)
//     */
//    private Bicluster bicluster;
//
//    /**
//     * Constructs a gene or pattern expression chart with the genes names,
//     * conditions names and gene expression data retrieved from the given
//     * <code>bicluster</code>.
//     *
//     * @param bicluster the <code>Bicluster</code> which contains the matrix
//     *                 object with the data to represent on the expression
//     *                 chart
//     * @param pattern the <code>boolean</code> value that indicates if this
//     *                chart is meant to be a pattern expression chart (
//     *                <code>true</code>)or a gene expression chart (
//     *                <code>false</code>)
//     * @param width the <code>int</code> width of the expression chart
//     * @param height the <code>int</code> height of the expression chart
//     * @param minimumDrawWidth the <code>int</code> minimum draw width
//     *                         of the expression chart
//     * @param minimumDrawHeight the <code>int</code> minimum draw height
//     *                          of the expression chart
//     * @param maximumDrawWidth the <code>int</code> maximum draw width
//     *                         of the expression chart
//     * @param maximumDrawHeight the <code>int</code> maximum draw height
//     *                          of the expression chart
//     * @param useBuffer <code>boolean</code> <code>true</code> if a
//     *                  buffer should be used; <code>false</code> if not
//     * @param properties <code>boolean</code> <code>true</code> if properties
//     *                   should be accessible via context menu;
//     *                   <code>false</code> if otherwise
//     * @param save <code>boolean</code> <code>true</code> if save option
//     *             should be available via context menu; <code>false</code>
//     *             if otherwise
//     * @param print <code>boolean</code> <code>true</code> if print option
//     *             should be available via context menu; <code>false</code>
//     *             if otherwise
//     * @param zoom <code>boolean</code> <code>true</code> if zoom option
//     *             should be available via context menu; <code>false</code>
//     *             if otherwise
//     * @param tooltips <code>boolean</code> <code>true</code> if tooltips
//     *                 should be displayed; <code>false</code> otherwise
//     * @param shapesVisible <code>boolean</code> <code>true</code> if the
//     *                      shapes of the expression points should be
//     *                      displayed in the chart
//     * @param includeLegend <code>boolean</code> <code>true</code> if a legend
//     *                      with the genes names should be displayed;
//     *                      <code>false</code> othwerwise
//     * @param normalize <code>boolean</code> <code>true</code> if expression
//     *                  values should be normalized before creating the
//     *                  expression chart; <code>false</code> otherwise
//     * @throws InvalidNodeObjectTypeException if <code>nodeInfo</code> does
//     *         not contain a valid node object
//     */
//    public PanelExpressionChart(Bicluster bicluster,
//                                boolean pattern,
//                                int width,
//                                int height,
//                                int minimumDrawWidth,
//                                int minimumDrawHeight,
//                                int maximumDrawWidth,
//                                int maximumDrawHeight,
//                                boolean useBuffer,
//                                boolean properties,
//                                boolean save,
//                                boolean print,
//                                boolean zoom,
//                                boolean tooltips,
//                                boolean shapesVisible,
//                                boolean includeLegend,
//                                boolean normalize) throws
//            InvalidNodeObjectTypeException {
//        super(createBiclusterChart(createBiclusterDataset(bicluster, pattern,
//                normalize), pattern, shapesVisible, includeLegend),
//              width, height, minimumDrawWidth, minimumDrawHeight,
//              maximumDrawWidth, maximumDrawHeight, useBuffer, properties,
//              save, print, zoom, tooltips);
//        this.bicluster = bicluster;
//        this.getChart().setBackgroundPaint(this.getBackground());
//        if (pattern) {
//            ValueAxis patternValuesAxis = ((CategoryPlot)this.getChart().
//                                           getPlot()).getRangeAxis();
//            patternValuesAxis.setTickLabelsVisible(false);
//            patternValuesAxis.setTickMarksVisible(false);
//            patternValuesAxis.setStandardTickUnits(NumberAxis.
//                    createIntegerTickUnits());
//            if (bicluster instanceof CCC_Bicluster) {
//                int rangeLimit = ((CCC_Bicluster) bicluster).getAlphabet().
//                                 length / 2 + 1;
//                patternValuesAxis.setRange( -rangeLimit, rangeLimit);
//            }
//        }
//    }
//
//    /**
//     * Constructs a gene expression chart with the given
//     * <code>genesNames</code>, <code>conditionsNames</code> and respective
//     * <code>expressionValues</code>
//     *
//     * @param genesNames the <code>String[]</code> set of genes names
//     * @param conditionsNames the <code>String[]</code> set of conditions names
//     * @param expressionValues the <code>float[][]</code> set of expression values
//     * @param missingValue the value which marks missings
//     * @param width the <code>int</code> width of the expression chart
//     * @param height the <code>int</code> height of the expression chart
//     * @param minimumDrawWidth the <code>int</code> minimum draw width
//     *                         of the expression chart
//     * @param minimumDrawHeight the <code>int</code> minimum draw height
//     *                          of the expression chart
//     * @param maximumDrawWidth the <code>int</code> maximum draw width
//     *                         of the expression chart
//     * @param maximumDrawHeight the <code>int</code> maximum draw height
//     *                          of the expression chart
//     * @param useBuffer <code>boolean</code> <code>true</code> if a
//     *                  buffer should be used; <code>false</code> if not
//     * @param properties <code>boolean</code> <code>true</code> if properties
//     *                   should be accessible via context menu;
//     *                   <code>false</code> if otherwise
//     * @param save <code>boolean</code> <code>true</code> if save option
//     *             should be available via context menu; <code>false</code>
//     *             if otherwise
//     * @param print <code>boolean</code> <code>true</code> if print option
//     *             should be available via context menu; <code>false</code>
//     *             if otherwise
//     * @param zoom <code>boolean</code> <code>true</code> if zoom option
//     *             should be available via context menu; <code>false</code>
//     *             if otherwise
//     * @param tooltips <code>boolean</code> <code>true</code> if tooltips
//     *                 should be displayed; <code>false</code> otherwise
//     * @param shapesVisible <code>boolean</code> <code>true</code> if the
//     *                      shapes of the expression points should be
//     *                      displayed in the chart
//     * @param includeLegend <code>boolean</code> <code>true</code> if a legend
//     *                      with the genes names should be displayed;
//     *                      <code>false</code> othwerwise
//     * @param normalize <code>boolean</code> <code>true</code> if expression
//     *                  values should be normalized before creating the
//     *                  expression chart; <code>false</code> otherwise
//     * @throws InvalidNodeObjectTypeException if <code>nodeInfo</code> does
//     *         not contain a valid node object
//     */
//    public PanelExpressionChart(String[] genesNames,
//                                String[] conditionsNames,
//                                float[][] expressionValues,
//                                float missingValue,
//                                int width,
//                                int height,
//                                int minimumDrawWidth,
//                                int minimumDrawHeight,
//                                int maximumDrawWidth,
//                                int maximumDrawHeight,
//                                boolean useBuffer,
//                                boolean properties,
//                                boolean save,
//                                boolean print,
//                                boolean zoom,
//                                boolean tooltips,
//                                boolean shapesVisible,
//                                boolean includeLegend,
//                                boolean normalize) throws
//            InvalidNodeObjectTypeException {
//        super(createGeneExpressionChart(createExpressionDataset(genesNames,
//                conditionsNames, expressionValues, missingValue, normalize), shapesVisible,
//                                        includeLegend),
//              width, height, minimumDrawWidth, minimumDrawHeight,
//              maximumDrawWidth, maximumDrawHeight, useBuffer, properties,
//              save, print, zoom, tooltips);
//        this.genesNames = genesNames;
//        this.conditionsNames = conditionsNames;
//        this.expressionValues = expressionValues;
//        this.missingValueFloat = missingValue;
//        this.getChart().setBackgroundPaint(this.getBackground());
//    }
//
//    /**
//     * Constructs a pattern expression chart based on the given
//     * expression <code>pattern</code> (sequence of symbols) within
//     * the given <code>alphabet</code> (set of symbols that can appear
//     * in <code>pattern</code>).
//     *
//     * @param pattern the <code>char[]</code> expression pattern
//     * @param alphabet the <code>char[]</code> expression alphabet
//     * @param missingValue the value which marks missings
//     * @param conditionsNames the <code>String[]</code> names of the conditions
//     * @param columnIndexes the <code>int[]</code> indexes of the columns
//     *                      from which the conditions were taken (exclusively
//     *                      for charts of expression patterns originated from
//     *                      matrices discretized by variations between
//     *                      time-points techniques
//     * @param width the <code>int</code> width of the expression chart
//     * @param height the <code>int</code> height of the expression chart
//     * @param minimumDrawWidth the <code>int</code> minimum draw width
//     *                         of the expression chart
//     * @param minimumDrawHeight the <code>int</code> minimum draw height
//     *                          of the expression chart
//     * @param maximumDrawWidth the <code>int</code> maximum draw width
//     *                         of the expression chart
//     * @param maximumDrawHeight the <code>int</code> maximum draw height
//     *                          of the expression chart
//     * @param useBuffer <code>boolean</code> <code>true</code> if a
//     *                  buffer should be used; <code>false</code> if not
//     * @param properties <code>boolean</code> <code>true</code> if properties
//     *                   should be accessible via context menu;
//     *                   <code>false</code> if otherwise
//     * @param save <code>boolean</code> <code>true</code> if save option
//     *             should be available via context menu; <code>false</code>
//     *             if otherwise
//     * @param print <code>boolean</code> <code>true</code> if print option
//     *             should be available via context menu; <code>false</code>
//     *             if otherwise
//     * @param zoom <code>boolean</code> <code>true</code> if zoom option
//     *             should be available via context menu; <code>false</code>
//     *             if otherwise
//     * @param tooltips <code>boolean</code> <code>true</code> if tooltips
//     *                 should be displayed; <code>false</code> otherwise
//     * @param shapesVisible <code>boolean</code> <code>true</code> if the
//     *                      shapes of the expression points should be
//     *                      displayed in the chart
//     * @param includeLegend <code>boolean</code> <code>true</code> if a legend
//     *                      with the genes names should be displayed;
//     *                      <code>false</code> othwerwise
//     * @throws Exception
//     */
//    public PanelExpressionChart(char[] pattern,
//                                char[] alphabet,
//                                char missingValue,
//                                String[] conditionsNames,
//                                int[] columnIndexes,
//                                int width,
//                                int height,
//                                int minimumDrawWidth,
//                                int minimumDrawHeight,
//                                int maximumDrawWidth,
//                                int maximumDrawHeight,
//                                boolean useBuffer,
//                                boolean properties,
//                                boolean save,
//                                boolean print,
//                                boolean zoom,
//                                boolean tooltips,
//                                boolean shapesVisible,
//                                boolean includeLegend) throws Exception {
//        super(createExpressionPatternChart(
//            createPatternExpressionDataset(
//                pattern, alphabet, missingValue,
//                conditionsNames, columnIndexes),
//            shapesVisible, includeLegend),
//              width, height, minimumDrawWidth, minimumDrawHeight,
//              maximumDrawWidth, maximumDrawHeight, useBuffer,
//              properties, save, print, zoom, tooltips);
//        this.getChart().setBackgroundPaint(this.getBackground());
//        ValueAxis patternValuesAxis = ((CategoryPlot)this.getChart().getPlot()).
//                                      getRangeAxis();
//        patternValuesAxis.setTickLabelsVisible(false);
//        patternValuesAxis.setTickMarksVisible(false);
//        patternValuesAxis.setStandardTickUnits(NumberAxis.
//                                               createIntegerTickUnits());
//        int rangeLimit = alphabet.length / 2 + 1;
//        patternValuesAxis.setRange( -rangeLimit, rangeLimit);
//    }
//
//    /**
//     * Creates an expression pattern dataset based on the given
//     * expression <code>pattern</code> (sequence of symbols) within
//     * the given <code>alphabet</code> (set of symbols that can appear
//     * in <code>pattern</code>).
//     *
//     * @param pattern the <code>char[]</code> expression pattern
//     * @param alphabet the <code>char[]</code> expression alphabet
//     * @param missingValue the value which marks missings
//     * @param conditionsNames the <code>String[]</code> names of the conditions
//     * @param columnIndexes the <code>int[]</code> indexes of the columns
//     *                      from which the conditions were taken (exclusively
//     *                      for charts of expression patterns originated from
//     *                      matrices discretized by variations between
//     *                      time-points techniques
//     * @return the <code>CategoryDataset</code> representing the given
//     *         expression <code>pattern</code>
//     * @throws Exception
//     */
//    public static CategoryDataset createPatternExpressionDataset(char[] pattern,
//            char[] alphabet, char missingValue,
//            String[] conditionsNames, int[] columnIndexes) throws
//            Exception {
//        if ((pattern.length > conditionsNames.length) ||
//            (pattern.length < conditionsNames.length - 1)) {
//            throw (new Exception("Pattern must have the same elements or one " +
//                                 "element less than conditions names"));
//        }
//
//        int[] patternValues = patternToValues(pattern, alphabet);
//        if (pattern.length == conditionsNames.length - 1) {
//            /**
//             * this pattern corresponds to a
//             * discretized VBTP (variations between time-points) matrix
//             */
//
//            /**
//             * create and initialize the dataset with matrix/bicluster data
//             */
//            DefaultCategoryDataset expressionDataset = new
//                    DefaultCategoryDataset();
//            for (int i = 0; i < pattern.length; i++) {
//                if (patternValues[i] != missingValue) {
//                    expressionDataset.addValue(patternValues[i],
//                                               "Pattern",
//                                               "TP" + columnIndexes[i] + "_TP" +
//                                               columnIndexes[i + 1]);
//                }
//            }
//            return expressionDataset;
//        }
//
//        /**
//         * create and initialize the dataset with matrix/bicluster data
//         */
//        DefaultCategoryDataset expressionDataset = new DefaultCategoryDataset();
//        for (int i = 0; i < pattern.length; i++) {
//            if (patternValues[i] != missingValue) {
//                expressionDataset.addValue(patternValues[i],
//                                           "Pattern",
//                                           conditionsNames[i]);
//            }
//        }
//        return expressionDataset;
//    }
//
//    /**
//     * Returns an integer value for each character in the <code>pattern</code>,
//     * so that it gets possible to draw a category chart of the bicluster's
//     * discrete pattern.<br/>
//     *
//     * All characters in the <code>pattern</code> must be contained in
//     * the <code>alphabet</code>. <code>alphabet</code> is supposed to
//     * be lexicographically sorted.
//     *
//     * @param pattern the <code>char[]</code> which holds the discrete
//     *                pattern to be drawn in the chart
//     * @param alphabet the <code>char[]</code> that contains the characters
//     *                used to discretize the matrix from which the bicluster
//     *                was derived
//     * @return int[] the set of integer values that correspond to the
//     *               characters in <code>pattern</code>
//     */
//    private static int[] patternToValues(char[] pattern, char[] alphabet) {
//        int[] alphabetValues = new int[alphabet.length];
//        int[] patternValues = new int[pattern.length];
//        int middleIndex = alphabet.length / 2;
//        if (alphabet.length % 2 != 0) {
//            alphabetValues[middleIndex] = 0;
//        } else {
//            alphabetValues[middleIndex] = 1;
//        }
//
//        int value = alphabetValues[middleIndex] + 1;
//        for (int i = middleIndex + 1; i < alphabet.length; i++) {
//            alphabetValues[i] = value;
//            value++;
//        }
//
//        value = -1;
//        for (int i = middleIndex - 1; i > -1; i--) {
//            alphabetValues[i] = value;
//            value--;
//        }
//
//        for (int i = 0; i < pattern.length; i++) {
//            for (int j = 0; j < alphabet.length; j++) {
//                if (pattern[i] == alphabet[j]) {
//                    patternValues[i] = alphabetValues[j];
//                    break;
//                }
//            }
//        }
//
//        return patternValues;
//    }
//
//    /**
//     * Creates a pattern expression dataset for the given
//     * <code>bicluster</code>. Handles all kinds of biclusters
//     * obtained from discrete data and also biclusters from VBTP
//     * matrices (discretized with variations between
//     * time-points technique), as well as biclusters with gene
//     * shifts and/or restricted errors, sign changes and/or time lags.
//     *
//     * @param bicluster the <code>CCC_Bicluster</code> or one of its subclasses
//     *                  object with data to create the pattern expression dataset
//     * @return CategoryDataset the pattern expression dataset
//     */
//    public static CategoryDataset createPatternExpressionDataset(CCC_Bicluster
//            bicluster) {
//        char[] pattern = bicluster.getBiclusterExpressionPattern();
//        char[] alphabet = bicluster.getAlphabet();
//        int[] columnsIndexes = bicluster.getColumnsIndexes();
//        char missingValue = ((DiscretizedExpressionMatrix)bicluster.
//                             getBiclusterSource().getMatrix()).getMissingValue();
//        /**
//         * Convert pattern characters to integer values, so that they can
//         * be referenced in chart's axis.
//         */
//        int[] patternValues = patternToValues(pattern, alphabet);
//        /**
//         * Get bicluster conditions names. Even if from a bicluster extracted
//         * from a discretized VBTP matrix, conditions names will be the
//         * original ones, so they have to be converted to the form:
//         * TP1_TP2, TP2_TP3, TP3_TP4 ...
//         */
//        String[] conditionsNames = bicluster.getConditionsNames();
//        if (((DiscretizedExpressionMatrix) bicluster.getBiclusterSource().
//             getMatrix()).getIsVBTP()) {
//            conditionsNames = new String[columnsIndexes.length - 1];
//            for (int i = 0; i < conditionsNames.length; i++) {
//                conditionsNames[i] = "TP" + columnsIndexes[i] + "_TP" +
//                                     columnsIndexes[i + 1];
//            }
//        }
//        String[] genesNames = bicluster.getGenesNames();
//        /**
//         * Get all conditions names from the Expressionmatrix,
//         * PreProcessedExpressionMatrix or DiscretizedExpressionMatrix
//         * from which the bicluster was extracted.
//         * Conditions names from a VBTP discretized matrix will have
//         * the following configuration: TP1_TP2, TP2_TP3, TP3_TP4 ...
//         */
//        String[] allConditionsNames = ((DiscretizedExpressionMatrix) bicluster.
//                                       getBiclusterSource().getMatrix()).
//                                      getConditionsNames();
//
//        DefaultCategoryDataset expressionDataset = new DefaultCategoryDataset();
//
//        if (bicluster instanceof E_CCC_Bicluster) {
//            /**
//             * bicluster is an
//             * E_CCC_Bicluster,
//             * E_CCC_Bicluster_GeneShifts,
//             * E_CCC_Bicluster_RestrictedErrors,
//             * E_CCC_Bicluster_GeneShifts_RestrictedErrors
//             */
//            /**
//             * Add each pattern value to the dataset
//             */
//            for (int i = 0; i < pattern.length; i++) {
//                if (patternValues[i] != missingValue) {
//                    expressionDataset.addValue(patternValues[i],
//                                               "Pattern",
//                                               conditionsNames[i]);
//                }
//            }
//            return expressionDataset;
//        }
//        if (bicluster instanceof CCC_Bicluster_SignChanges) {
//            /**
//             * bicluster is a
//             * CCC_Bicluster_SignChanges
//             */
//            /**
//             * Get the symmetric pattern.
//             */
//            char[] signChangesPattern = ((CCC_Bicluster_SignChanges) bicluster).
//                                        getBiclusterExpressionPattern_SignChanges();
//            /**
//             * Convert symmetric pattern characters to integer values, so
//             * that they can be referenced in chart's axis.
//             */
//            int[] signChangesPatternValues = patternToValues(
//                    signChangesPattern, alphabet);
//            /**
//             * signChanges has the following information for each gene
//             * of the bicluster:
//             * - true, if the pattern for that gene is the symmetric pattern
//             * - false, if the pattern for that gene is the original pattern
//             */
//            boolean[] signChanges = ((CCC_Bicluster_SignChanges) bicluster).
//                                    getSignChanges();
//
//            /**
//             * patternString will have the names of all genes for which
//             * the pattern is the original pattern
//             */
//            String patternString = "";
//
//            /**
//             * signChangesPatternString will have the names of all genes
//             * for which the pattern is the symmetric pattern
//             */
//            String signChangesPatternString = "";
//
//            /**
//             * Add the names of the genes to their corresponding patterns.
//             */
//            for (int i = 0; i < signChanges.length; i++) {
//                if (signChanges[i]) {
//                    signChangesPatternString += " " + genesNames[i];
//                } else {
//                    patternString += " " + genesNames[i];
//                }
//            }
//
//            /**
//             * Add each pattern to the chart dataset.
//             */
//            for (int i = 0; i < pattern.length; i++) {
//                if (patternValues[i] != missingValue) {
//                    expressionDataset.addValue(patternValues[i],
//                                               patternString,
//                                               conditionsNames[i]);
//                }
//                if (signChangesPatternValues[i] != missingValue) {
//                    expressionDataset.addValue(signChangesPatternValues[i],
//                                               signChangesPatternString,
//                                               conditionsNames[i]);
//                }
//            }
//            return expressionDataset;
//        }
//        if (bicluster instanceof CCC_Bicluster_SignChanges_TimeLags) {
//            /**
//             * bicluster is a
//             * CCC_Bicluster_SignChanges_TimeLags
//             */
//            /**
//             * Get the symmetric pattern.
//             */
//            char[] signChangesPattern = ((CCC_Bicluster_SignChanges_TimeLags)
//                                         bicluster).
//                                        getBiclusterExpressionPattern_SignChanges();
//            /**
//             * Convert symmetric pattern characters to integer values, so
//             * that they can be referenced in chart's axis.
//             */
//            int[] signChangesPatternValues = patternToValues(
//                    signChangesPattern, alphabet);
//
//            /**
//             * Get the indexes of the columns in the bicluster for all genes.
//             * First dimension is the gene dimension (it has the same nr of
//             * rows than the rows in the bicluster). Second dimension is the
//             * column indexes dimension. Columns indexes refer to the
//             * DiscretizedExpressionMatrix conditions indexes.
//             */
//            int[][] allColumnsIndexes = ((CCC_Bicluster_SignChanges_TimeLags)
//                                         bicluster).getAllColumnsIndexes();
//
//            /**
//             * signChanges has the following information for each gene
//             * of the bicluster:
//             * - true, if the pattern for that gene is the symmetric pattern
//             * - false, if the pattern for that gene is the original pattern
//             */
//            boolean[] signChanges = ((CCC_Bicluster_SignChanges_TimeLags)
//                                     bicluster).getSignChanges();
//
//            /**
//             * Each of the startColumnIndexes elements holds the index of a
//             * starting position of the original or symmetric pattern in
//             * the bicluster.
//             */
//            ArrayList<Integer> startColumnIndexes = new ArrayList<Integer>();
//
//            /**
//             * Each of the groups elements holds the set of genes indexes
//             * for which the pattern in the bicluster is exactly the same
//             * (starts in the corresponding startColumnIndexes index and
//             * is an original/symmetric pattern, as stated by the
//             * corresponding index at signs). Genes indexes refer to
//             * indexes within the bicluster genes, not the
//             * DiscretizedExpressionMatrix from which the bicluster was
//             * extracted.
//             */
//            ArrayList<ArrayList<Integer>> groups = new ArrayList<ArrayList<Integer>>();
//
//            /**
//             * Each string in strings contains the names of the genes
//             * with the original or symmetric pattern with the starting
//             * position in the bicluster referenced by the same index at
//             * startColumnIndexes. These names correspond also to the
//             * set of genes indexes referenced by the same index at groups.
//             */
//            ArrayList<String> strings = new ArrayList<String>();
//
//            /**
//             * Each boolean in signs states if the pattern starting in the
//             * column referenced by the same index at startColumnIndexes and
//             * happening for each gene of the set referenced by the same
//             * index at groups is the original pattern (false) or the
//             * symmetric pattern (true).
//             */
//            ArrayList<Boolean> signs = new ArrayList<Boolean>();
//
//            /**
//             * Contains a set of genes indexes with a similar pattern
//             * (coherent expression, same starting column).
//             */
//            ArrayList<Integer> group = new ArrayList<Integer>();
//
//            /**
//             * Initialize startColumnIndexes, groups, strings and signs.
//             */
//            startColumnIndexes.add(Integer.valueOf(allColumnsIndexes[0][
//                    0]));
//            group = new ArrayList<Integer>();
//            group.add(Integer.valueOf(0));
//            groups.add(group);
//            strings.add(genesNames[0]);
//            signs.add(Boolean.valueOf(signChanges[0]));
//
//            /**
//             * For each gene of the bicluster,
//             */
//            for (int i = 1; i < allColumnsIndexes.length; i++) {
//                int j;
//                /**
//                 * Run over all the starting columns for the patterns
//                 * in the bicluster already computed, to check if the
//                 * pattern for the current gene is already there
//                 * (different patterns are identified by a starting
//                 * position and a type - original or symmetric pattern)
//                 */
//                for (j = 0; j < startColumnIndexes.size(); j++) {
//                    /**
//                     * If the pattern is found, i.e., was already computed,
//                     * then we will just add the current gene index to the
//                     * set of genes with this pattern and also add the name of
//                     * the gene to the corresponding string.
//                     */
//                    if (allColumnsIndexes[i][0] ==
//                        startColumnIndexes.get(j).intValue()
//                        && signChanges[i] == signs.get(j).booleanValue()) {
//                        groups.get(j).add(Integer.valueOf(i));
//                        strings.set(j, strings.get(j) + " " +
//                                             genesNames[i]);
//                        break;
//                    }
//                }
//                if (j == startColumnIndexes.size()) {
//                    /**
//                     * The pattern was not found, so we will have to add it:
//                     * - add its starting column index to startColumnIndexes
//                     * - add the first gene index to the set of genes indexes
//                     * (the current gene, which has that pattern in the bicluster)
//                     * - add the first gene to the string
//                     * - add the type of pattern to signs (true - symmetric,
//                     * false - original)
//                     */
//                    startColumnIndexes.add(Integer.valueOf(
//                            allColumnsIndexes[i][0]));
//                    group = new ArrayList<Integer>();
//                    group.add(Integer.valueOf(i));
//                    groups.add(group);
//                    strings.add(genesNames[i]);
//                    signs.add(Boolean.valueOf(signChanges[i]));
//                }
//            }
//
//            /**
//             * For each discovered pattern
//             */
//            while (!startColumnIndexes.isEmpty()) {
//                /**
//                 * Find the minimum starting column index
//                 */
//                Integer min = startColumnIndexes.get(0);
//                for (int i = 1; i < startColumnIndexes.size(); i++) {
//                    if (min.compareTo(startColumnIndexes.get(i)) > 0) {
//                        min = startColumnIndexes.get(i);
//                    }
//                }
//
//                /**
//                 * For all the sets of genes which have the pattern with
//                 * the minimum starting column index, add their
//                 * corresponding pattern (original or symmetric one) to
//                 * the chart's dataset.
//                 */
//                for (int i = 0; i < startColumnIndexes.size(); i++) {
//                    if (min.compareTo(startColumnIndexes.get(i)) == 0) {
//                        int[] columnsIndexesGene =
//                                allColumnsIndexes[
//                                groups.get(i).get(0).intValue()
//                                ];
//                        if (signs.get(i).booleanValue()) {
//                            /**
//                             * add SYMMETRIC pattern to the dataset, with a
//                             * specific starting column index and set of
//                             * genes names
//                             */
//                            for (int j = 0; j < signChangesPatternValues.length;
//                                         j++) {
//                                if (signChangesPatternValues[j] != missingValue) {
//                                    expressionDataset.addValue(
//                                            signChangesPatternValues[j],
//                                            strings.get(i),
//                                            allConditionsNames[
//                                            columnsIndexesGene[j] -
//                                            1]);
//                                }
//                            }
//                        } else {
//                            /**
//                             * add ORIGINAL pattern to the dataset, with a
//                             * specific starting column index and set of
//                             * genes names
//                             */
//                            for (int j = 0; j < patternValues.length; j++) {
//                                if (patternValues[j] != missingValue) {
//                                    expressionDataset.addValue(
//                                            patternValues[j],
//                                            strings.get(i),
//                                            allConditionsNames[
//                                            columnsIndexesGene[j] -
//                                            1]);
//                                }
//                            }
//                        }
//                        /**
//                         * Remove all the information about the current
//                         * pattern, so that it won't be added to the
//                         * chart's dataset again.
//                         */
//                        groups.remove(i);
//                        strings.remove(i);
//                        signs.remove(i);
//                        startColumnIndexes.remove(i);
//
//                        /**
//                         * As we removed one element from the Vectors that
//                         * we are processing, we will need to process the
//                         * current index again (it contains now a new element).
//                         */
//                        i--;
//                    }
//                }
//            }
//
//            return expressionDataset;
//        }
//        if (bicluster instanceof CCC_Bicluster_TimeLags) {
//            /**
//             * bicluster is a
//             * CCC_Bicluster_TimeLags
//             */
//            /**
//             * Get the indexes of the columns in the bicluster for all genes.
//             * First dimension is the gene dimension (it has the same nr of
//             * rows than the rows in the bicluster). Second dimension is the
//             * column indexes dimension. Columns indexes refer to the
//             * DiscretizedExpressionMatrix conditions indexes.
//             */
//            int[][] allColumnsIndexes = ((CCC_Bicluster_TimeLags) bicluster).
//                                        getAllColumnsIndexes();
//
//            /**
//             * Each of the startColumnIndexes elements holds the index of a
//             * starting position of the pattern in the bicluster.
//             */
//            ArrayList<Integer> startColumnIndexes = new ArrayList<Integer>();
//
//            /**
//             * Each of the groups elements holds the set of genes indexes
//             * for which the pattern in the bicluster is exactly the same
//             * (starts in the corresponding startColumnIndexes index).
//             * Genes indexes refer to indexes within the bicluster genes,
//             * not the DiscretizedExpressionMatrix from which the bicluster
//             * was extracted.
//             */
//            ArrayList<ArrayList<Integer>> groups = new ArrayList<ArrayList<Integer>>();
//
//            /**
//             * Each string in strings contains the names of the genes
//             * with the pattern with the starting position in the
//             * bicluster referenced by the same index at
//             * startColumnIndexes. These names correspond also to the
//             * set of genes indexes referenced by the same index at groups.
//             */
//            ArrayList<String> strings = new ArrayList<String>();
//
//            /**
//             * Contains a set of genes indexes with a similar pattern
//             * (same starting column).
//             */
//            ArrayList<Integer> group = new ArrayList<Integer>();
//
//            /**
//             * Initialize startColumnIndexes, groups, strings and signs.
//             */
//            startColumnIndexes.add(Integer.valueOf(allColumnsIndexes[0][
//                    0]));
//            group = new ArrayList<Integer>();
//            group.add(Integer.valueOf(0));
//            groups.add(group);
//            strings.add(genesNames[0]);
//
//            /**
//             * For each gene of the bicluster,
//             */
//            for (int i = 1; i < allColumnsIndexes.length; i++) {
//                int j;
//                /**
//                 * Run over all the starting columns for the patterns
//                 * in the bicluster already computed, to check if the
//                 * pattern for the current gene is already there
//                 * (different patterns are identified by a different
//                 * starting column index)
//                 */
//                for (j = 0; j < startColumnIndexes.size(); j++) {
//                    /**
//                     * If the pattern is found, i.e., was already computed,
//                     * then we will just add the current gene index to the
//                     * set of genes with this pattern and also add the name of
//                     * the gene to the corresponding string.
//                     */
//                    if (allColumnsIndexes[i][0] ==
//                        startColumnIndexes.get(j).intValue()) {
//                        groups.get(j).add(Integer.valueOf(i));
//                        strings.set(j, strings.get(j) + " " +
//                                             genesNames[i]);
//                        break;
//                    }
//                }
//                if (j == startColumnIndexes.size()) {
//                    /**
//                     * The pattern was not found, so we will have to add it:
//                     * - add its starting column index to startColumnIndexes
//                     * - add the first gene index to the set of genes indexes
//                     * (the current gene, which has that pattern in the bicluster)
//                     * - add the first gene to the string
//                     */
//                    startColumnIndexes.add(Integer.valueOf(
//                            allColumnsIndexes[i][0]));
//                    group = new ArrayList<Integer>();
//                    group.add(Integer.valueOf(i));
//                    groups.add(group);
//                    strings.add(genesNames[i]);
//                }
//            }
//
//            /**
//             * For each discovered pattern
//             */
//            while (!startColumnIndexes.isEmpty()) {
//                /**
//                 * Find the minimum starting column index
//                 */
//                Integer min = startColumnIndexes.get(0);
//                for (int i = 1; i < startColumnIndexes.size(); i++) {
//                    if (min.compareTo(startColumnIndexes.get(i)) > 0) {
//                        min = startColumnIndexes.get(i);
//                    }
//                }
//
//                /**
//                 * For all the sets of genes which have the pattern with
//                 * the minimum starting column index, add their
//                 * corresponding pattern to the chart's dataset.
//                 */
//                for (int i = 0; i < startColumnIndexes.size(); i++) {
//                    if (min.compareTo(startColumnIndexes.get(i)) == 0) {
//                        int[] columnsIndexesGene =
//                                allColumnsIndexes[
//                                groups.get(i).get(0).intValue()
//                                ];
//                        /**
//                         * Add the pattern to the dataset.
//                         */
//                        for (int j = 0; j < patternValues.length; j++) {
//                            if (patternValues[j] != missingValue) {
//                                expressionDataset.addValue(
//                                        patternValues[j],
//                                        strings.get(i),
//                                        allConditionsNames[columnsIndexesGene[j] -
//                                        1]);
//                            }
//                        }
//                        /**
//                         * Remove all the information about the current
//                         * pattern, so that it won't be added to the
//                         * chart's dataset again.
//                         */
//                        groups.remove(i);
//                        strings.remove(i);
//                        startColumnIndexes.remove(i);
//
//                        /**
//                         * Get out, since there are no more groups with
//                         * this pattern (distinct pattern <-> distinct group).
//                         */
//                        break;
//                    }
//                }
//            }
//            return expressionDataset;
//        }
//        /**
//         * bicluster is a
//         * Bicluster,
//         * CCC_Bicluster,
//         * CCC_Bicluster_GeneShifts
//         */
//        for (int i = 0; i < pattern.length; i++) {
//            if (patternValues[i] != missingValue) {
//                expressionDataset.addValue(patternValues[i],
//                                           "Pattern",
//                                           conditionsNames[i]);
//            }
//        }
//        return expressionDataset;
//    }
//
//
//    /**
//     * Creates an expression/pattern category dataset based on genes names,
//     * conditions names and expression values or pattern retrieved from the
//     * <code>bicluster</code> object.
//     *
//     * @param bicluster the <code>Bicluster</code> from which the
//     *                  expression data will be retrieved
//     * @param pattern the <code>boolean</code> value that indicates if this
//     *                chart is meant to be a pattern expression chart (
//     *                <code>true</code>)or a gene expression chart (
//     *                <code>false</code>)
//     * @param normalize the <code>boolean</code> <code>true</code> if the
//     *                 original expression values should be normalized before
//     *                 creating the expression chart; <code>false</code>
//     *                 otherwise
//     * @return the <code>CategoryDataset</code> containing the necessary
//     *                 data to construct an expression chart
//     * @throws InvalidNodeObjectTypeException
//     */
//    public static CategoryDataset createBiclusterDataset(Bicluster bicluster,
//            boolean pattern, boolean normalize) throws
//            InvalidNodeObjectTypeException {
//
//        if (pattern && bicluster instanceof CCC_Bicluster) {
//            return createPatternExpressionDataset((CCC_Bicluster) bicluster);
//        }
//        return createExpressionDataset(bicluster, normalize);
//    }
//
//    /**
//     * Creates an expression dataset for a bicluster.
//     *
//     * @param bicluster the <code>Bicluster</code> from which the
//     *                  expression dataset will be constructed.
//     * @param normalize <code>boolean</code> <code>true</code> if
//     *                  expression data in the dataset should be
//     *                  normalized by gene
//     * @return CategoryDataset the bicluster expression dataset
//     */
//    public static CategoryDataset createExpressionDataset(
//            Bicluster bicluster,
//            boolean normalize) {
//        DefaultCategoryDataset expressionDataset = new DefaultCategoryDataset();
//        /**
//         * Get bicluster columns indexes. These indexes refer to the
//         * ExpressionMatrix, PreProcessedMatrix or DiscretizedExpressionMatrix
//         * from which the bicluster was extracted.
//         */
//        int[] columnsIndexes = bicluster.getColumnsIndexes();
//
//        /**
//         * Get bicluster rows indexes. These indexes refer also to the
//         * matrix from which the bicluster was extracted.
//         */
//        int[] rowsIndexes = bicluster.getRowsIndexes();
//
//        float[][] biclusterGenesAllColumnsMatrix = null;
//        float[][] normalizedBiclusterGenesAllColumnsMatrix = null;
//        float missingValue;
//
//        /**
//         * Names of the genes in the bicluster.
//         */
//        String[] genesNames = bicluster.getGenesNames();
//
//        /**
//         * Names of the conditions in the bicluster.
//         */
//        String[] conditionsNames = bicluster.getConditionsNames();
//        String[] allConditionsNames = null;
//
//        /**
//         * Get the names of the conditions in the matrix from which
//         * the bicluster was extracted.
//         */
//        if (bicluster instanceof CCC_Bicluster) {
//            /**
//             * If bicluster is a CCC_Bicluster or one of its subclasses,
//             * then it may have been extracted from a discretized VBTP
//             * matrix and, in that case, calling getConditionsNames()
//             * on that matrix would return conditions names like:
//             * TP1_TP2, TP2_TP3. WE DO NOT WANT THAT!! So, we get the names
//             * of all the conditions from the original expression matrix.
//             */
//            allConditionsNames = ((DiscretizedExpressionMatrix) bicluster.
//                                  getBiclusterSource().getMatrix()).
//                                 getOriginalExpressionMatrix().
//                                 getConditionsNames();
//            missingValue = ((DiscretizedExpressionMatrix) bicluster.
//                                  getBiclusterSource().getMatrix()).
//                                 getOriginalExpressionMatrix().
//                                 getMissingValue();
//
//        } else {
//            allConditionsNames = ((AbstractExpressionMatrix)
//                                  bicluster.
//                                  getBiclusterSource().getMatrix()).
//                                 getConditionsNames();
//            missingValue = ((AbstractExpressionMatrix)
//                            bicluster.
//                            getBiclusterSource().getMatrix()).
//                           getMissingValue();
//        }
//        /**
//         * Get the original gene expression matrix.
//         */
//        float[][] geneExpressionMatrix = null;
//        geneExpressionMatrix = ((IMatrix)
//                                bicluster.
//                                getBiclusterSource().
//                                getMatrix()).
//                               getGeneExpressionMatrix();
//
//        /**
//         * Construct a matrix with bicluster genes and all conditions
//         * from the original gene expression matrix.
//         */
//        biclusterGenesAllColumnsMatrix =
//                new float[rowsIndexes.length][geneExpressionMatrix[0].
//                length];
//        for (int i = 0; i < biclusterGenesAllColumnsMatrix.length; i++) {
//            for (int j = 0;
//                         j < biclusterGenesAllColumnsMatrix[0].length;
//                         j++) {
//                biclusterGenesAllColumnsMatrix[i][j] =
//                        geneExpressionMatrix[rowsIndexes[i] - 1][j];
//            }
//        }
//
//        if (normalize) {
//            /**
//             * Normalize the matrix with bicluster genes and all conditions.
//             * We don't do this directly on the bicluster expression matrix,
//             * because we are using a normalization by gene and we have to
//             * include all the conditions of the experiment to do this.
//             */
//            if (bicluster instanceof CCC_Bicluster) {
//                normalizedBiclusterGenesAllColumnsMatrix =
//                        PreProcessedExpressionMatrix.
//                        normalizeGeneExpressionMatrixByGene(
//                        biclusterGenesAllColumnsMatrix,
//                        missingValue);
//            }
//            else {
//                normalizedBiclusterGenesAllColumnsMatrix =
//                        PreProcessedExpressionMatrix.
//                        normalizeGeneExpressionMatrixByGene(
//                                biclusterGenesAllColumnsMatrix,
//                                missingValue);
//            }
//        }
//
//        if (bicluster instanceof E_CCC_Bicluster) {
//            /**
//             * bicluster may be an
//             * E_CCC_Bicluster,
//             * E_CCC_Bicluster_GeneShifts,
//             * E_CCC_Bicluster_RestrictedErrors,
//             * E_CCC_Bicluster_GeneShifts_RestrictedErrors
//             */
//            float[][] expressionValues;
//            if (normalize) {
//                /**
//                 * Re-construct the bicluster gene expression matrix
//                 * from the normalized bicluster genes * all conditions
//                 * matrix.
//                 */
//                expressionValues = new float[rowsIndexes.length][columnsIndexes.
//                                   length];
//                for (int i = 0; i < expressionValues.length; i++) {
//                    for (int j = 0; j < expressionValues[0].length; j++) {
//                        expressionValues[i][j] =
//                                normalizedBiclusterGenesAllColumnsMatrix
//                                [i][columnsIndexes[j] - 1];
//                    }
//                }
//            } else {
//                expressionValues = bicluster.getBiclusterExpressionMatrix();
//            }
//
//            /**
//             * Add all expression values to the chart's dataset.
//             */
//            for (int i = 0; i < expressionValues.length; i++) {
//                for (int j = 0; j < expressionValues[0].length; j++) {
//                    if (expressionValues[i][j] != missingValue) {
//                        expressionDataset.addValue(expressionValues[i][j],
//                                genesNames[i],
//                                conditionsNames[j]);
//                    }
//                }
//            }
//
//            return expressionDataset;
//        }
//        if (bicluster instanceof CCC_Bicluster_TimeLags) {
//            /**
//             * bicluster may be a
//             * CCC_Bicluster_TimeLags,
//             * CCC_Bicluster_SignChanges_TimeLags
//             */
//            int[][] allColumnsIndexes = ((CCC_Bicluster_TimeLags) bicluster).
//                                        getAllColumnsIndexes();
//
//            if (normalize) {
//                /**
//                 * Add the normalized gene expression values to
//                 * the chart's dataset.
//                 */
//                for (int k = 0; k < columnsIndexes.length; k++) {
//                    int index = columnsIndexes[k];
//
//                    for (int i = 0; i < allColumnsIndexes.length; i++) {
//                        if (index == allColumnsIndexes[i][0]) {
//                            for (int j = 0; j < allColumnsIndexes[i].length; j++) {
//                                float value =
//                                        normalizedBiclusterGenesAllColumnsMatrix
//                                        [i][allColumnsIndexes[i][j] - 1];
//
//                                if (value != missingValue) {
//                                    expressionDataset.addValue(
//                                            value,
//                                            genesNames[i],
//                                            allConditionsNames[allColumnsIndexes[i][
//                                            j] -
//                                            1]);
//                                }
//                            }
//                        }
//                    }
//                }
//            } else {
//                /**
//                 * Add the gene expression values to
//                 * the chart's dataset.
//                 */
//                for (int k = 0; k < columnsIndexes.length; k++) {
//                    int index = columnsIndexes[k];
//
//                    for (int i = 0; i < allColumnsIndexes.length; i++) {
//                        if (index == allColumnsIndexes[i][0]) {
//                            for (int j = 0; j < allColumnsIndexes[i].length; j++) {
//                                float value = biclusterGenesAllColumnsMatrix
//                                              [i][allColumnsIndexes[i][j] - 1];
//                                if (value != missingValue) {
//                                    expressionDataset.addValue(
//                                            value,
//                                            genesNames[i],
//                                            allConditionsNames[
//                                            allColumnsIndexes[i][
//                                            j] -
//                                            1]);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            return expressionDataset;
//        }
//
//        /**
//         * bicluster is a
//         * Bicluster,
//         * CCC_Bicluster,
//         * CCC_Bicluster_GeneShifts,
//         * CCC_Bicluster_SignChanges
//         */
//        float[][] expressionValues;
//        if (normalize) {
//            expressionValues = new float[rowsIndexes.length][columnsIndexes.
//                               length];
//            for (int i = 0; i < expressionValues.length; i++) {
//                for (int j = 0; j < expressionValues[0].length; j++) {
//                    expressionValues[i][j] =
//                            normalizedBiclusterGenesAllColumnsMatrix
//                            [i][columnsIndexes[j] - 1];
//                }
//            }
//        } else {
//            expressionValues = bicluster.getBiclusterExpressionMatrix();
//        }
//
//        for (int i = 0; i < expressionValues.length; i++) {
//            for (int j = 0; j < expressionValues[0].length; j++) {
//                if (expressionValues[i][j] != missingValue) {
//                    expressionDataset.addValue(expressionValues[i][j],
//                                               genesNames[i],
//                                               conditionsNames[j]);
//                }
//            }
//        }
//
//        return expressionDataset;
//    }
//
//    /**
//     * Creates a new expression category dataset with the
//     * <code>given genesNames</code>, <code>conditionsNames</code> and
//     * <code>expressionValues</code>.
//     *
//     * @param genesNames the <code>String[]</code> set of genes names
//     * @param conditionsNames the <code>String[]</code> set of conditions names
//     * @param expressionValues the <code>float[][]</code> set of expression values
//     * @param missingValue the value which marks missings
//     * @param normalize the <code>boolean</code> <code>true</code> if the
//     *                 original expression values should be normalized before
//     *                 creating the expression chart; <code>false</code>
//     *                 otherwise
//     * @return the <code>CategoryDataset</code> containing the necessary
//     *                 data to construct an expression chart
//     */
//    public static CategoryDataset createExpressionDataset(
//            String[] genesNames,
//            String[] conditionsNames,
//            float[][] expressionValues,
//            float missingValue,
//            boolean normalize) {
//        /**
//         * create and initialize the dataset with matrix/bicluster data
//         */
//        DefaultCategoryDataset expressionDataset = new DefaultCategoryDataset();
//
//        if (normalize) {
//            expressionValues = PreProcessedExpressionMatrix.
//                               normalizeGeneExpressionMatrixByGene(
//                                       expressionValues, missingValue);
//        }
//
//        for (int i = 0; i < genesNames.length; i++) {
//            for (int j = 0; j < conditionsNames.length; j++) {
//                if (expressionValues[i][j] != missingValue) {
//                    expressionDataset.addValue(expressionValues[i][j],
//                                               genesNames[i],
//                                               conditionsNames[j]);
//                }
//            }
//        }
//
//        return expressionDataset;
//    }
//
//    /**
//     * Creates a new gene expression chart from a given
//     * <code>expressionDataset</code>.
//     *
//     * @param expressionDataset the expression <code>CategoryDataset</code>
//     * @param shapesVisible the <code>boolean</code> <code>true</code>
//     *                      if the shapes in the charts time-points should
//     *                      be displayed; <code>false</code> if otherwise
//     * @param includeLegend the <code>boolean</code> <code>true</code> if
//     *                      a legend of the chart should be included;
//     *                      <code>false</code> otherwise
//     * @return the newly constructed gene expression <code>JFreeChart</code>
//     */
//    public static JFreeChart createGeneExpressionChart(CategoryDataset
//            expressionDataset,
//            boolean shapesVisible,
//            boolean includeLegend) {
//        return (createExpressionChart(expressionDataset,
//                                      "Gene Expression Chart",
//                                      "conditions",
//                                      "gene expression",
//                                      shapesVisible,
//                                      includeLegend));
//    }
//
//    /**
//     * Creates a new pattern expression chart from a given
//     * expression <code>patternDataset</code>.
//     *
//     * @param patternDataset the expression pattern <code>CategoryDataset</code>
//     * @param shapesVisible the <code>boolean</code> <code>true</code>
//     *                      if the shapes in the charts time-points should
//     *                      be displayed; <code>false</code> if otherwise
//     * @param includeLegend the <code>boolean</code> <code>true</code> if
//     *                      a legend of the chart should be included;
//     *                      <code>false</code> otherwise
//     * @return the newly constructed expression pattern <code>JFreeChart</code>
//     */
//    public static JFreeChart createExpressionPatternChart(CategoryDataset
//            patternDataset,
//            boolean shapesVisible,
//            boolean includeLegend) {
//        return (createExpressionChart(patternDataset,
//                                      "Bicluster Pattern Chart",
//                                      "conditions",
//                                      "pattern units",
//                                      shapesVisible,
//                                      includeLegend));
//    }
//
//    public static JFreeChart createBiclusterChart(CategoryDataset dataset,
//                                                  boolean pattern,
//                                                  boolean shapesVisible,
//                                                  boolean includeLegend) {
//        if (pattern) {
//            return (createExpressionPatternChart(dataset,
//                                                 shapesVisible,
//                                                 includeLegend));
//        }
//        return createGeneExpressionChart(dataset, shapesVisible, includeLegend);
//    }
//
//    /**
//     * Creates a general expression chart from the given
//     * <code>expressionDataset</code> and with the given
//     * <code>chartTitle</code>, <code>conditionsAxisName</code>,
//     * <code>expressionAxisName</code>.
//     *
//     * @param expressionDataset the expression <code>CategoryDataset</code>
//     * @param chartTitle the chart title <code>String</code>
//     * @param conditionsAxisName the name of the conditions axis <code>String</code>
//     * @param expressionAxisName the name of the expression values axis <code>String</code>
//     * @param shapesVisible the <code>boolean</code> <code>true</code>
//     *                      if the shapes in the charts time-points should
//     *                      be displayed; <code>false</code> if otherwise
//     * @param includeLegend the <code>boolean</code> <code>true</code> if
//     *                      a legend of the chart should be included;
//     *                      <code>false</code> otherwise
//     * @return the newly constructed expression <code>JFreeChart</code>
//     */
//    public static JFreeChart createExpressionChart(
//            CategoryDataset expressionDataset,
//            String chartTitle,
//            String conditionsAxisName,
//            String expressionAxisName,
//            boolean shapesVisible,
//            boolean includeLegend) {
//        /**
//         * create renderer for the chart
//         */
//        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
//        renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
//        renderer.setStroke(new BasicStroke((float) 1.5));
//        renderer.setShapesVisible(shapesVisible);
//
//        /**
//         * create gene expression and conditions axis for the chart
//         */
//        CategoryAxis conditionsAxis = new CategoryAxis(conditionsAxisName);
//        NumberAxis geneExpressionAxis = new NumberAxis(expressionAxisName);
//
//        /**
//         * create a category plot for the chart
//         */
//        CategoryPlot plot = new CategoryPlot(expressionDataset, conditionsAxis,
//                                             geneExpressionAxis, renderer);
//        plot.setBackgroundPaint(Color.white);
//        plot.setRangeGridlinePaint(Color.lightGray);
//        plot.setDomainGridlinesVisible(true);
//        plot.setDomainGridlinePaint(Color.lightGray);
//
//        JFreeChart chart = new JFreeChart(
//                chartTitle, // chart title
//                JFreeChart.DEFAULT_TITLE_FONT, // title font
//                plot, // plot for the chart
//                includeLegend // include legend
//                           );
//        if (includeLegend) {
//            chart.getLegend().setItemFont(new Font(chart.getLegend().
//                    getItemFont().getName(), Font.PLAIN, 9));
//        }
//        return chart;
//    }
//
//    /**
//     * Creates a new time-points expression chart panel for a given
//     * <code>bicluster</code>.
//     *
//     * @param bicluster a <code>Bicluster</code> from which the
//     *                  expression data will be retrieved
//     * @param width the <code>int</code> fixed draw width of the expression chart
//     * @param height the <code>int</code> fixed draw height of the expression chart
//     * @param title the <code>String</code> title for the chart panel
//     * @param normalize the <code>boolean</code> <code>true</code> if the
//     *                  original expression values should be normalized before
//     *                  creating the expression chart; <code>false</code>
//     *                  otherwise
//     * @param miniature the <code>boolean</code> <code>true</code> if the
//     *                  chart to create is a miniature (it will then contain
//     *                  no legend, no normalized data and other specific
//     *                  characteristics)
//     * @param legend boolean
//     * @return the new <code>PanelExpressionChart</code>
//     * @throws InvalidNodeObjectTypeException
//     */
//    public static PanelExpressionChart createExpressionBiclusterTimePointsChart(
//            Bicluster bicluster,
//            int width,
//            int height,
//            String title,
//            boolean normalize,
//            boolean miniature,
//            boolean legend) //sara
//        throws InvalidNodeObjectTypeException {
//        boolean shapesVisible = false;
//        if (bicluster.getNumberOfConditions() == 1) {
//            shapesVisible = true;
//        }
//        PanelExpressionChart chartPanel;
//        if (miniature) {
//            chartPanel = new PanelExpressionChart(
//                    bicluster,
//                    false, // pattern
//                    width, // width
//                    height, // height
//                    width, // minimum draw width
//                    height, // minimum draw height
//                    width, // maximum draw width
//                    height, // maximum draw height
//                    false, // use buffer
//                    false, // properties
//                    false, // save
//                    false, // print
//                    false, // zoom
//                    false, // tooltips
//                    shapesVisible, // shapes visible
//                    false, // legend
//                    false); // normalize data
//
//            JFreeChart chart = chartPanel.getChart();
//            Font font = new Font(chart.DEFAULT_TITLE_FONT.getName(), Font.PLAIN,
//                                 8);
//            if (title != null) {
//                chart.setTitle(title);
//            } else {
//                chart.setTitle("Bicluster " + bicluster.getID());
//            }
//            chart.getTitle().setFont(new Font(font.getName(), Font.PLAIN, 9));
//            CategoryPlot plot = (CategoryPlot) chart.getPlot();
//            plot.getRangeAxis().setLabelFont(font);
//            plot.getRangeAxis().setTickLabelFont(font);
//            plot.getDomainAxis().setLabelFont(font);
//            plot.getDomainAxis().setTickLabelFont(font);
//
//            return chartPanel;
//        }
//
//        chartPanel = new PanelExpressionChart(
//                bicluster,
//                false, // pattern
//                width, // width
//                height, // height
//                width, // minimum draw width
//                height, // minimum draw height
//                width, // maximum draw width
//                height, // maximum draw height
//                false, // use buffer
//                true, // properties
//                true, // save
//                true, // print
//                true, // zoom
//                true, // tooltips
//                shapesVisible, // shapes visible
////                true, // legend
//                legend, //legend sara
//                normalize); // normalize data
//
//        return chartPanel;
//    }
//
//    /**
//     * Creates a new time-points expression chart panel for a given
//     * <code>bicluster</code>.
//     *
//     * @param bicluster a <code>Bicluster</code> from which the
//     *                  expression data will be retrieved
//     * @param width the <code>int</code> fixed draw width of the expression chart
//     * @param height the <code>int</code> fixed draw height of the expression chart
//     * @param normalize the <code>boolean</code> <code>true</code> if the
//     *                  original expression values should be normalized before
//     *                  creating the expression chart; <code>false</code>
//     *                  otherwise
//     * @param miniature the <code>boolean</code> <code>true</code> if the
//     *                  chart to create is a miniature (it will then contain
//     *                  no legend, no normalized data and other specific
//     *                  characteristics)
//     * @param legend boolean
//     * @return the new <code>PanelExpressionChart</code>
//     * @throws InvalidNodeObjectTypeException
//     * @see #createExpressionBiclusterTimePointsChart(CCC_Bicluster bicluster, int width, int height, String title, boolean normalize, boolean miniature)
//     */
//    /**
//     *
//     * @param bicluster Bicluster
//     * @param width int
//     * @param height int
//     * @param normalize boolean
//     * @param miniature boolean
//     * @param legend boolean
//     * @return PanelExpressionChart
//     * @throws InvalidNodeObjectTypeException
//     */
//    public static PanelExpressionChart createExpressionBiclusterTimePointsChart(
//            Bicluster bicluster,
//            int width,
//            int height,
//            boolean normalize,
//            boolean miniature,
//            boolean legend) // sara
//        throws InvalidNodeObjectTypeException {
//        return createExpressionBiclusterTimePointsChart(bicluster, width,
//                height, null, normalize, miniature, legend);
//    }
//
//    /**
//     * Creates a new bicluster expression pattern chart panel for a given
//     * <code>bicluster</code>.
//     *
//     * @param bicluster a <code>CCC_Bicluster</code> from which the
//     *                  expression data will be retrieved
//     * @param width the <code>int</code> fixed draw width of the expression chart
//     * @param height the <code>int</code> fixed draw height of the expression chart
//     * @param title the <code>String</code> title for the chart panel
//     * @param miniature the <code>boolean</code> <code>true</code> if the
//     *                  chart to create is a miniature (it will then contain
//     *                  no legend, no normalized data and other specific
//     *                  characteristics)
//     * @param legend boolean
//     * @return the new pattern <code>PanelExpressionChart</code>
//     * @throws Exception
//     */
//    public static PanelExpressionChart createExpressionBiclusterPatternChart(
//            CCC_Bicluster bicluster,
//            int width,
//            int height,
//            String title,
//            boolean miniature,
//            boolean legend) //sara
//        throws Exception {
//        PanelExpressionChart chartPanel;
//        boolean shapesVisible = false;
//        if (bicluster.getBiclusterExpressionPattern().length == 1) {
//            shapesVisible = true;
//        }
//        if (miniature) {
//            chartPanel = new PanelExpressionChart(
//                    bicluster,
//                    true, // pattern
//                    width, // width
//                    height, // height
//                    width, // minimum draw width
//                    height, // minimum draw height
//                    width, // maximum draw width
//                    height, // maximum draw height
//                    false, // use buffer
//                    false, // properties
//                    false, // save
//                    false, // print
//                    false, // zoom
//                    false, // tooltips
//                    shapesVisible, // shapes visible
//                    false, // legend
//                    false); // normalize
//
//            JFreeChart chart = chartPanel.getChart();
//            Font font = new Font(chart.DEFAULT_TITLE_FONT.getName(), Font.PLAIN,
//                                 8);
//            if (title != null) {
//                chart.setTitle(title);
//            } else {
//                chart.setTitle("Bicluster  " + bicluster.getID());
//            }
//            chart.getTitle().setFont(new Font(font.getName(), Font.PLAIN, 9));
//            CategoryPlot plot = (CategoryPlot) chart.getPlot();
//            plot.getRangeAxis().setLabelFont(font);
//            plot.getRangeAxis().setTickLabelFont(font);
//            plot.getDomainAxis().setLabelFont(font);
//            plot.getDomainAxis().setTickLabelFont(font);
//
//            return chartPanel;
//        }
//
//        chartPanel = new PanelExpressionChart(
//                bicluster,
//                true, // pattern
//                width, // width
//                height, // height
//                width, // minimum draw width
//                height, // minimum draw height
//                width, // maximum draw width
//                height, // maximum draw height
//                false, // use buffer
//                true, // properties
//                true, // save
//                true, // print
//                true, // zoom
//                true, // tooltips
//                shapesVisible, // shapes visible
////                true, // legend
//                legend, // legend sara
//                false); // normalize
//
//        return chartPanel;
//    }
//
//    /**
//     * Creates a new bicluster expression pattern chart panel for a given
//     * <code>bicluster</code>.
//     *
//     * @param bicluster a <code>CCC_Bicluster</code> from which the
//     *                  expression data will be retrieved
//     * @param width the <code>int</code> fixed draw width of the expression chart
//     * @param height the <code>int</code> fixed draw height of the expression chart
//     * @param miniature the <code>boolean</code> <code>true</code> if the
//     *                  chart to create is a miniature (it will then contain
//     *                  no legend, no normalized data and other specific
//     *                  characteristics)
//     * @param legend boolean
//     * @return the new pattern <code>PanelExpressionChart</code>
//     * @throws Exception
//     * @see #createExpressionBiclusterPatternChart(CCC_Bicluster bicluster, int width, int height, String title, boolean miniature)
//     */
//    public static PanelExpressionChart createExpressionBiclusterPatternChart(
//            CCC_Bicluster bicluster,
//            int width,
//            int height,
//            boolean miniature,
//            boolean legend) //sara
//        throws Exception {
//        return createExpressionBiclusterPatternChart(bicluster, width, height, null,
//                miniature, legend);
//    }
//
//
//    /**
//     * Creates a new panel holding an all time-points expression chart,
//     * with expression data retrieved from <code>bicluster</code> and
//     * <code>biclustering</code> objects.
//     *
//     * @param bicluster a <code>Bicluster</code> from which the
//     *                  names of the genes will be retrieved
//     * @param width the <code>int</code> fixed draw width of the expression chart
//     * @param height the <code>int</code> fixed draw height of the expression chart
//     * @param normalize the <code>boolean</code> <code>true</code> if the
//     *                  original expression values should be normalized before
//     *                  creating the expression chart; <code>false</code>
//     *                  otherwise
//     * @param legend boolean
//     * @return the new all time-points <code>PanelExpressionChart</code>
//     * @throws InvalidNodeObjectTypeException
//     */
//    public static PanelExpressionChart createExpressionAllTimePointsChart(
//            Bicluster bicluster,
//            int width,
//            int height,
//            boolean normalize,
//            boolean legend) //sara
//            throws InvalidNodeObjectTypeException {
//        Biclustering biclustering = bicluster.getBiclusterSource();
//        String[] genesNames = bicluster.getGenesNames();
//        String[] conditionsNames = null;
//        String[] biclusteringGenesNames = new String[0];
//        float[][] biclusteringExpressionData = new float[0][0];
//        float missingValue = Integer.MIN_VALUE;
//
//        if (biclustering instanceof BiclusteringInDiscretizedMatrix) {
//            AbstractExpressionMatrix originalMatrix = (
//                    (DiscretizedExpressionMatrix) biclustering.
//                    getMatrix()).getOriginalExpressionMatrix();
//            conditionsNames = originalMatrix.getConditionsNames();
//            biclusteringGenesNames = originalMatrix.getGenesNames();
//            biclusteringExpressionData = originalMatrix.getGeneExpressionMatrix();
//            missingValue = ((DiscretizedExpressionMatrix)
//                            ((BiclusteringInDiscretizedMatrix)biclustering).
//                            getMatrix()).
//                           getOriginalExpressionMatrix().getMissingValue();
//        } else
//        if (biclustering instanceof CC_TSB_Biclustering) {
//            CC_TSB_Biclustering ccTSBBiclustering = (CC_TSB_Biclustering)
//                    biclustering;
//            conditionsNames = ccTSBBiclustering.getMatrix().getConditionsNames();
//            biclusteringGenesNames = ccTSBBiclustering.getMatrix().
//                                     getGenesNames();
//            biclusteringExpressionData = ccTSBBiclustering.getMatrix().
//                                         getGeneExpressionMatrix();
//            missingValue = ((AbstractExpressionMatrix)
//                            ((BiclusteringInDiscretizedMatrix)biclustering).
//                            getMatrix()).getMissingValue();
//        } else {
//            //other types of bicluster groups objects
//        }
//
//        float[][] expressionValues = new float[genesNames.length][
//                                     conditionsNames.length];
//
//        for (int i = 0; i < genesNames.length; i++) {
//            String geneName = genesNames[i];
//            for (int j = 0; j < biclusteringGenesNames.length; j++) {
//                if (biclusteringGenesNames[j].equals(geneName)) {
//                    for (int k = 0; k < conditionsNames.length; k++) {
//                        expressionValues[i][k] =
//                                biclusteringExpressionData[j][k];
//                    }
//                    break;
//                }
//            }
//        }
//
//        boolean shapesVisible = false;
//        if (conditionsNames.length == 1) {
//            shapesVisible = true;
//        }
//
//        PanelExpressionChart expressionChart = new PanelExpressionChart(
//                genesNames,
//                conditionsNames,
//                expressionValues,
//                missingValue,
//                width, // width
//                height, // height
//                width, // minimum draw width
//                height, // minimum draw height
//                width, // maximum draw width
//                height, // maximum draw height
//                false, // use buffer
//                true, // properties
//                true, // save
//                true, // print
//                true, // zoom
//                true, // tooltips
//                shapesVisible, // shapes visible
//                legend, // legend
////                false, // legend
//                normalize); // normalize data
//
//        return expressionChart;
//    }
//
//    /**
//     * Returns a new time-points (bicluster or all time-points)
//     * chart panel, repainted from the the current one.
//     *
//     * @param width the <code>int</code> fixed draw width of the expression chart
//     * @param height the <code>int</code> fixed draw height of the expression chart
//     * @param normalize the <code>boolean</code> <code>true</code> if the
//     *                  original expression values should be normalized before
//     *                  creating the expression chart; <code>false</code>
//     *                  otherwise
//     * @return the repainted time-points <code>PanelExpressionChart</code>
//     * @throws InvalidNodeObjectTypeException
//     * @throws Exception
//     */
//    public PanelExpressionChart repaintTimePointsChart(
//            int width,
//            int height,
//            boolean normalize) throws InvalidNodeObjectTypeException, Exception {
//
//        if (bicluster != null) {
//            boolean shapesVisible = false;
//            if (bicluster.getConditionsNames().length == 1) {
//                shapesVisible = true;
//            }
//            PanelExpressionChart expressionChart = new PanelExpressionChart(
//                    bicluster,
//                    false, // pattern
//                    width, // width
//                    height, // height
//                    width, // minimum draw width
//                    height, // minimum draw height
//                    width, // maximum draw width
//                    height, // maximum draw height
//                    false, // use buffer
//                    true, // properties
//                    true, // save
//                    true, // print
//                    true, // zoom
//                    true, // tooltips
//                    shapesVisible, // shapes visible
//                    true, // legend
//                    normalize); // normalize data
//            return expressionChart;
//        }
//        if (genesNames == null || conditionsNames == null ||
//            expressionValues == null) {
//            throw new Exception(
//                    "There are no data to repaint time points chart.");
//        }
//        boolean shapesVisible = false;
//        if (conditionsNames.length == 1) {
//            shapesVisible = true;
//        }
//        PanelExpressionChart expressionChart = new PanelExpressionChart(
//                genesNames,
//                conditionsNames,
//                expressionValues,
//                missingValueFloat,
//                width, // width
//                height, // height
//                width, // minimum draw width
//                height, // minimum draw height
//                width, // maximum draw width
//                height, // maximum draw height
//                false, // use buffer
//                true, // properties
//                true, // save
//                true, // print
//                true, // zoom
//                true, // tooltips
//                shapesVisible, // shapes visible
//                true, // legend
//                normalize); // normalize data
//        return expressionChart;
//    }
//
//    /**
//     * Returns the names of the genes in the expression chart.
//     *
//     * @return the <code>String[]</code> set of genes names
//     */
//    public String[] getGenesNames() {
//        return genesNames;
//    }
//
//    /**
//     * Returns the names of the conditions in the expression chart.
//     *
//     * @return the <code>String[]</code> set of conditions names
//     */
//    public String[] getConditionsNames() {
//        return conditionsNames;
//    }
//
//    /**
//     * Returns the expression values in the expression chart.
//     *
//     * @return the <code>float[][]</code> set of expression values
//     */
//    public float[][] getExpressionValues() {
//        return expressionValues;
//    }
//}
