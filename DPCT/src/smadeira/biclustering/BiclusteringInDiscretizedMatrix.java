package smadeira.biclustering;

import java.io.*;

import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.JProgressBar;


/**
 * <p>Title: Biclustering in a Discretized Matrix</p>
 *
 * <p>Description: Defines a group of biclusters in a discretized matrixx.</p>
 *
 * <p>Copyright:   Copyright (C) 2007  Joana P. Gon�alves, Sara C. Madeira
 *                 This program is free software; you can redistribute
 *                 it and/or modify it under the terms of the GNU General
 *                 Public License as published by the Free Software
 *                 Foundation; either version 3 of the License, or
 *                 any later version.
 *
 *                 This program is distributed in the hope that it will
 *                 be useful, but WITHOUT ANY WARRANTY; without even the
 *                 implied warranty of MERCHANTABILITY or FITNESS FOR A
 *                 PARTICULAR PURPOSE.  See the
 *                 <a href="http://www.gnu.org/licenses/gpl.html">
 *                 GNU General Public License</a> for more details.
 * </p>
 *
 * @author Sara C. Madeira
 * @version 1.0
 */
public abstract class BiclusteringInDiscretizedMatrix
    extends Biclustering<IDiscretizedMatrix>
    implements Cloneable, Serializable, NodeObjectInterface {
  public static final long serialVersionUID =-3998970939333112481L;

  /**
   *
   * @param m DiscretizedExpressionMatrix
   */
  public BiclusteringInDiscretizedMatrix(DiscretizedExpressionMatrix m) {
    this.matrix = m;
  }


  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @return char[]
   */
  public char[] getAlphabet() {
    return this.matrix.getAlphabet();
  }

  /**
   *
   * @return char[][]
   */
  public char[][] getSymbolicExpressionMatrix() {
    return this.matrix.getSymbolicExpressionMatrix();
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * Constructs a matrix with int values to be used in biclustering algorithms using suffix trees.
   * In this the usual string composed of character in the alphabet is replaced by an array of ints
   * where each int is inserted into the suffix tree instead of the usual char.
   * This matrix is obtained from matrixWithSymbolsAndColumns by transforming each of its
   * elements to ints (2 bytes) as folows:
   * compute a int value representing one of the characters {D,N,U} concatenated with its
   * column position.
   * int Values for the characters:
   * D = 68
   * N = 78
   * U = 85
   *
   * Example:
   *
   * U1 D2 N3 -2
   * N1 N2 U3 -1
   *
   * is converted to
   *
   * 851 682 783 -2
   * 781 782 853 -1
   *
   *
   * @return int[][]
   * @throws Exception
   */
  protected int[][] getMatrixToConstructSuffixTrees() {
    int numberOfRows = this.matrix.getSymbolicExpressionMatrix().length;
    int numberOfColumns = this.matrix.getSymbolicExpressionMatrix()[0].length +
        1; // plus 1 to terminator
    int[][] matrixToConstructSuffixTrees = new int[numberOfRows][
        numberOfColumns];
    for (int i = 0; i < numberOfRows; i++) {
      String c = new String();
      String p = new String();
      String s = new String();
      int element;
      for (int j = 0; j < numberOfColumns - 1; j++) {
        c = Integer.toString( (int)this.matrix.getSymbolicExpressionMatrix()[i][
                             j]);
        p = Integer.toString(j + 1);
        s = new String(c + p);
        element = Integer.parseInt(s);
        matrixToConstructSuffixTrees[i][j] = element;
      }
      element = - (numberOfRows - i);
      matrixToConstructSuffixTrees[i][numberOfColumns - 1] = element;
    }
    return matrixToConstructSuffixTrees;
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   *
   * @return Object
   */
  public Object clone() {
    BiclusteringInDiscretizedMatrix o = (BiclusteringInDiscretizedMatrix)super.
        clone();
    o.matrix = (IDiscretizedMatrix)this.matrix.clone();
    return o;
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   *
   */
  public void printMatrixToComputeSuffixTree() {
    int[][] matrix = this.getMatrixToConstructSuffixTrees();
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        System.out.print(matrix[i][j] + "\t");
      }
      System.out.println();
    }
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * Prints the following information for each bicluster:
   * Bicluster ID
   * pValue --> computed with b.getBiclusterPatternWithColumns_pValue(markovChainOrder)
   * Significant 0.01 <= < 0.05 --> true OR false
   * Highly Significant < 0.01 --> true OR false
   * (Bonferroni corrected) p-value -> Computed with b.getBiclusterPatternWithColumns_BonferroniCorrected_pValue(markovChainOrder)
   * Significant 0.01 <= < 0.05
   * Highly Significant < 0.01
   *
   * @param pathToDirectory String
   * @param filename String
   * @param markovChainOrder int
   * @param numBiclustersToAnalyze int
   * @throws IOException
   */
  public void printStatisticalSignificanceSummary_ToFile(String pathToDirectory, String filename,
                                              int markovChainOrder, int numBiclustersToAnalyze) throws IOException{

    if (this.getNumberOfBiclusters() > 0){

      PrintWriter fileOut;
      if (pathToDirectory != null) {
        // CREATE DIRECTORY TO STORE THE RESULTS
        File dirForResults = new File(pathToDirectory);
        boolean exists = (dirForResults).exists();
        if (!exists) {
          dirForResults.mkdirs();
        }
        if(filename != null){
          fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new
              File(pathToDirectory, filename))), true);
        }
        else{
          fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new
              File(pathToDirectory, "PVALUES_Summary.txt"))), true);
        }
      }
      else {
        if(filename != null){
          fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new
              File(pathToDirectory, filename))), true);
        }
        else{
          fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new File(
              "PVALUES_Summary.txt"))), true);
        }
      }

      int toPrint = numBiclustersToAnalyze;
      if (numBiclustersToAnalyze > this.getNumberOfBiclusters()){
        toPrint = this.getNumberOfBiclusters();
      }

      fileOut.println("Bicluster ID" + "\t" + "pValue" + "\t" + "Highly Significant < 0.01" + "\t" + "Significant 0.01 >= < 0.05" + "\t" +
                      "(Bonferroni corrected) p-value" + "\t" + "Highly Significant < 0.01" + "\t" + "Significant 0.01 >= < 0.05");

      for (int i = 0; i < toPrint; i++) {
        CCC_Bicluster b = (CCC_Bicluster)this.getBiclusters().get(i);
        double pvalue = b.getBiclusterPatternWithColumns_pValue(markovChainOrder);
        boolean pvalue_significant = false;
        boolean pvalue_highlySignificant = false;
        double pvalueCorrected = b.getBiclusterPatternWithColumns_BonferroniCorrected_pValue(markovChainOrder);
        boolean pvalueCorrected_significant = false;
        boolean pvalueCorrected_highlySignificant = false;
        if(pvalue < 0.01){
          pvalue_highlySignificant = true;
        }
        else{
          if (pvalue < 0.05) {
            pvalue_significant = true;
          }
        }

        if(pvalueCorrected < 0.01){
          pvalueCorrected_highlySignificant = true;
        }
        else{
          if (pvalueCorrected < 0.05) {
            pvalueCorrected_significant = true;
          }
        }

        fileOut.println(b.getID() + "\t" +
                        pvalue + "\t" +
                        pvalue_highlySignificant + "\t" + pvalue_significant + "\t" +
                        pvalueCorrected + "\t" +
                        pvalueCorrected_highlySignificant + "\t" + pvalueCorrected_significant);
      }
      fileOut.close();
    }
  }


  //#####################################################################################################################
  //#####################################################################################################################

  /**
   *
   * @param pathToDirectory String
   * @param width int
   * @param height int
   * @param legend boolean
   * @param numBiclustersToAnalyze int
   */
  public void printColorChartBiclusterExpressionPattern_ToFiles(String pathToDirectory,
       int width, int height, boolean legend, int numBiclustersToAnalyze){
    if (this.getNumberOfBiclusters() > 0){
      int toPrint = numBiclustersToAnalyze;
      if (numBiclustersToAnalyze > this.getNumberOfBiclusters()){
        toPrint = this.getNumberOfBiclusters();
      }
      for (int k=0; k < toPrint; k++){
        CCC_Bicluster b = (CCC_Bicluster) this.getBiclusters().get(k);
        b.printColorChartBiclusterExpressionPattern_ToFile(pathToDirectory,
            "BICLUSTER_" + b.getID() + "_ExpressionPatternChart.png", width, height, legend);
      }

    }
    else{
      System.out.println("THERE ARE NO BICLUSTER: IMPOSSIBLE TO PRINT CHARTS");
    }
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   *
   * TO BE IMPLEMENTED !!!!!!
   *
   * @return StyledDocument
   */
  public StyledDocument info() {

    StyledDocument doc = new DefaultStyledDocument();
    StyledDocument doc2 = new HTMLDocument();

    // ...

    return (doc);
  }

} // END CLASS
