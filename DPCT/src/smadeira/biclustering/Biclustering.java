package smadeira.biclustering;

import ontologizer.go.Namespace;
import ontologizer.go.OBOParserException;
import smadeira.ontologizer.GOFrontEnd;
import smadeira.ontologizer.GeneSetResult;
import smadeira.utils.BiologicalRelevanceSummary;
import smadeira.utils.TimeConsumingTasks;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * <p>Title: Biclustering</p>
 *
 * <p>Description: Defines a group of biclusters.</p>
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
public abstract class Biclustering<T> extends TimeConsumingTasks
    implements Cloneable, Serializable, NodeObjectInterface {
  public static final long serialVersionUID = -3874323059294088573L;

  // FIELDS

  /**
   * Matrix used in the biclustering algorithm.
   */
  protected T matrix;

  /**
   * Set of biclusters.
   */
  protected ArrayList<Bicluster> setOfBiclusters;

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * Default Constructor.
   */
  public Biclustering() {
    this.setOfBiclusters = new ArrayList<Bicluster> (0);
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   *
   * @throws Exception
   */
  public abstract void computeBiclusters() throws Exception;

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   *
   * @return IMatrix
   */
  public T getMatrix() {
    return (this.matrix);
  }

  /**
   *
   * @return int
   */
  public int getNumberOfBiclusters() {
    if (this.setOfBiclusters != null) {
      return (this.setOfBiclusters.size());
    }
    else {
      return (0);
    }
  }

  //#####################################################################################################################

  /**
   *
   * @return Bicluster[]
   */
  public Bicluster[] getSetOfBiclusters() {
    Bicluster[] biclusters = new Bicluster[this.setOfBiclusters.size()];
    return (this.setOfBiclusters.toArray(biclusters));
  }

  //#####################################################################################################################

  /**
   *
   * @return ArrayList
   */
  public ArrayList<Bicluster> getBiclusters() {
    return (this.setOfBiclusters);
  }

  //#####################################################################################################################

  /**
   *
   * @param b Bicluster[]
   */
  public void setSetOfBiclusters(Bicluster[] b) {
    this.setOfBiclusters = new ArrayList<Bicluster> (0);
    for (int i = 0; i < b.length; i++) {
      this.setOfBiclusters.add(i, b[i]);
    }
  }

  //#####################################################################################################################

  /**
   *
   * @param b ArrayList
   */
  public void setBiclusters(ArrayList<Bicluster> b) {
    this.setOfBiclusters = b;
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * Deletes repeated biclusters.
   * A bicluster is repeated if there is another one in the set with the same conditions and genes.
   */
  protected void deleteRepeatedBiclusters() {
    //HashMap(int�initialCapacity, float�loadFactor) �
    HashMap<Integer, Bicluster>
        hashtree = new LinkedHashMap<Integer,
        Bicluster> (this.getNumberOfBiclusters(),
                    (float) 0.75);
    for (int i = 0; i < this.setOfBiclusters.size(); i++) {
      Bicluster bic = this.setOfBiclusters.get(i);
      int hashCode = bic.hashCode();
      if (hashtree.containsKey(hashCode)) {
        Bicluster object = hashtree.get(hashCode); // get object that has this key
        if (!object.equals(bic)) {
          // THERE IS NOT A BICLUSTER IN THE HASHTREE THAT REPRESENTS THE SAME BICLUSTER
          hashtree.put(hashCode, bic);
        }
      }
      else {
        // THERE IS NOT A BICLUSTER IN THE HASHTREE THAT REPRESENTS THE SAME BICLUSTER
        hashtree.put(hashCode, bic);
      }
    }
    this.setOfBiclusters = new ArrayList<Bicluster> (0);
    this.setOfBiclusters.addAll(hashtree.values());
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   *
   * @param pathTo_GeneOntologyFile String
   * @param pathTo_GeneAssociationFile String
   * @return GOFrontEnd
   */
  public GOFrontEnd computeGoFrontEnd(String pathTo_GeneOntologyFile,
                                      String pathTo_GeneAssociationFile)
      throws IOException, OBOParserException {
    GOFrontEnd go = new GOFrontEnd( ( (IMatrix)this.matrix).getGenesNames(),
                                   pathTo_GeneOntologyFile,
                                   pathTo_GeneAssociationFile);
    return go;
  }

  //#####################################################################################################################

  /**
   *
   * @param pathTo_GeneOntologyFile String
   * @param pathTo_GeneAssociationFile String
   * @param ontology char Filter gene ontology terms to a given ontology:
   * 'B' - BIOLOGICAL PROCESS
   * 'C' - CELLULAR COMPONENT
   * 'M' - METABOLIC FUNCTION
   * @return GOFrontEnd
   * @throws Exception
   */
  public GOFrontEnd computeGoFrontEnd(String pathTo_GeneOntologyFile,
                                      String pathTo_GeneAssociationFile,
                                      char ontology) throws Exception {
    if (ontology != 'B' && ontology != 'C' && ontology != 'M') {
      throw new Exception("WRONG PARAMETER: ontology CAN ONLY BE one of the following: \n 'B' - BIOLOGICAL PROCESS \n 'C' - CELLULAR COMPONENT or 'M' - METABOLIC FUNCTION");
    }

    GOFrontEnd go = new GOFrontEnd( ( (IMatrix)this.getMatrix()).
                                   getGenesNames(),
                                   pathTo_GeneOntologyFile,
                                   pathTo_GeneAssociationFile);
    if (ontology == 'B') {
      go.setFilterByTermNamespace(new Namespace("biological_process"));
    }
    else {
      if (ontology == 'C') {
        go.setFilterByTermNamespace(new Namespace("cellular_component"));
      }
      else {
        go.setFilterByTermNamespace(new Namespace("molecular_function"));
      }
    }
    return go;
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * PRINTS TABLE WITH BIOLOGICAL FUNCTIONS AND P-VALUES COMPUTED WITH ONTOLOGIZER TO FILE - FOR EACH BICLUSTER.

   * @param go GOFrontEnd
   * @param pathToDirectory String
   * @param GO_pValues_userThreshold float
   * @param useCorrectedPValues boolean
   * @param numBiclustersToAnalyze int
   * @throws IOException
   */
  public void printTablesWithGeneOntologyTerms_ToFiles(GOFrontEnd go,
      String pathToDirectory, float GO_pValues_userThreshold,
      boolean useCorrectedPValues, int numBiclustersToAnalyze) throws
      IOException {

    if (this.getNumberOfBiclusters() > 0) {
      int toPrint = numBiclustersToAnalyze;
      if (numBiclustersToAnalyze > this.getNumberOfBiclusters()) {
        toPrint = this.getNumberOfBiclusters();
      }

      for (int i = 0; i < toPrint; i++) {
        GeneSetResult geneSetResult = this.setOfBiclusters.get(i).
            computeGeneOntologyTerms(go, false);
        Bicluster b = this.setOfBiclusters.get(i);
        b.printTableWithGeneOntologyTerms_ToFile(geneSetResult, pathToDirectory,
                                                 "BICLUSTER_" + b.ID +
                                                 "_GO_Table.txt",
                                                 GO_pValues_userThreshold,
                                                 useCorrectedPValues);
      }
    }
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * PRINTS GRAPH WITH ENRICHED BIOLOGICAL FUNCTIONS AND P-VALUES COMPUTED WITH ONTOLOGIZER TO FILE - FOR EACH BICLUSTER.
   * ENRICHMENT IS ALWAYS CONSIDERED IF THE P-VALUE PASSES THE STATISTICAL TEST AFTER bONFERRONI CORRECTION.
   *
   * @param go GOFrontEnd
   * @param pathToDirectory String
   * @param GO_pValues_userThreshold float
   * @param numBiclustersToAnalyze int
   * @throws Exception
   */
  public void printGraphWithGeneOntologyTerms_ToFiles(GOFrontEnd go,
      String pathToDirectory, float GO_pValues_userThreshold,
      int numBiclustersToAnalyze) throws Exception {

    if (this.getNumberOfBiclusters() > 0) {
      int toPrint = numBiclustersToAnalyze;
      if (numBiclustersToAnalyze > this.getNumberOfBiclusters()) {
        toPrint = this.getNumberOfBiclusters();
      }

      for (int i = 0; i < toPrint; i++) {
        Bicluster b = this.setOfBiclusters.get(i);
        b.printGraphWithGeneOntologyTerms_ToFile(go, pathToDirectory,
                                                 "BICLUSTER_" + b.ID +
                                                 "_GO_Graph.dot",
                                                 GO_pValues_userThreshold);
      }
    }
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * PRINTS GENES TO FILE - FOR EACH BICLUSTER.
   *
   * @param pathToDirectory String
   * @throws IOException
   */
  public void printGenes_ToFiles(String pathToDirectory) throws
      IOException {
    for (int i = 0; i < this.getNumberOfBiclusters(); i++) {
      this.setOfBiclusters.get(i).printGenes_ToFile(pathToDirectory,
          new String("genes_BICLUSTER_" + this.setOfBiclusters.get(i).getID() +
                     ".txt"));
    }
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * PRINTS DATA NEEDED TO PLOT BICLUSTER EXPRESSION PATTERNS IN GNUPLOT - FOR EACH BICLUSTER.
   *
   * CREATES THE FOLLOWING PAIRS OF FILES:
   *
   * BICLUSTER_ID.dat BICLUSTER_ID.gplot
   * BICLUSTER_ID_all.dat BICLUSTER_ID_all.gplot
   *
   * EACH ROW ON BICLUSTER_ID.dat IS A COLUMN OF THE BICLUSTER EXPRESSION MATRIX
   *
   * EXAMPLE OF BICLUSTER_ID.gplot TO PLOT EXPRESSION PATTERN OF THE FIRST TWO GENES IN A BICLUSTER WITH COLUMNS 1-8:
   *
   * set term postscript eps
   * set size 0.5,0.5
   * set output "BICLUSTER_1893.eps"
   * set xlabel "Time-Points"
   * set ylabel "Normalized Expression Value"
   * set xtics ("1" 0, "2" 1, "3" 2, "4" 3, "5" 4, "6" 5, "7" 6, "8" 7)
   * set nokey
   * plot \
   *        "BICLUSTER_1893.dat" using 1 with lines,\
   *        "BICLUSTER_1893.dat" using 2 with lines
   *
   * GENERATE GRAPHIC WITH:
   *
   * gnuplot BICLUSTER_1893.gplot
   *
   * @param pathToResultsDirectory String
   * @param setOfBiclusters Bicluster[][]
   * @param numBiclustersToAnalyse int
   * @param originalGeneExpressionMatrix float[][]
   * @param normalizedGeneExpressionMatrix float[][]
   * @param printNormalizedExpression boolean
   * @throws IOException
   */
  public static void printExpression_ToFiles(String pathToResultsDirectory,
                                             Bicluster[] setOfBiclusters,
                                             float[][]
                                             originalGeneExpressionMatrix,
                                             float[][]
                                             normalizedGeneExpressionMatrix,
                                             int numBiclustersToAnalyse,
                                             boolean printNormalizedExpression) throws
      IOException {
    float[][] geneExpressionMatrix;
    if (printNormalizedExpression) {
      geneExpressionMatrix = normalizedGeneExpressionMatrix;
    }
    else {
      geneExpressionMatrix = originalGeneExpressionMatrix;
    }

    if (pathToResultsDirectory != null) {
      // CREATE DIRECTORY TO STORE THE RESULTS
      File dirForResults = new File(pathToResultsDirectory);
      boolean exists = (dirForResults).exists();
      if (exists) {
//        System.out.println("THE DIRECTORY TO STORE THE RESULTS ALREADY EXISTS!");
      }
      else {
        System.out.println("CREATING THE DIRECTORY TO STORE THE RESULTS...");
        boolean success = (new File(pathToResultsDirectory)).mkdirs();
        if (!success) {
          System.out.println(
              "CREATION OF DIRECTORY TO STORE THE RESULTS FAILED!");
        }
      }
    }

    // PRINT DATA TO PRINT BICLUSTER EXPRESSION PLOTS IN GNUPLOT
    System.out.println(
        "PRINTING DATA NEEDED TO PLOT BICLUSTER EXPRESSION PATTERNS IN GNUPLOT...");

    for (int k = 0; k < numBiclustersToAnalyse; k++) {
      Bicluster b = setOfBiclusters[k];
      // PRINT FILE BICLUSTER_ID.dat

      // print bicluster expression matrix transposed
      // each column is the set of expression values of gene_i in the columns in the bicluster

      String filename;
      if (printNormalizedExpression) {
        filename = new String("BICLUSTER_" + b.getID() + "_N.dat");
      }
      else {
        filename = new String("BICLUSTER_" + b.getID() + ".dat");
      }
      PrintWriter fileOut;
      if (pathToResultsDirectory != null) {
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new
            File(pathToResultsDirectory, filename))), true);
      }
      else {
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(filename)), true);
      }

      for (int j = 0; j < b.getNumberOfConditions(); j++) {
        for (int i = 0; i < b.getNumberOfGenes(); i++) {
          fileOut.print(geneExpressionMatrix[b.getRowsIndexes()[i] -
                        1][b.getColumnsIndexes()[j] - 1] + "\t");
        }
        fileOut.println();
      }
      fileOut.close();

      // PRINT FILE BICLUSTER_ID_all.dat

      //print expression of columns in the bicluster transposed
      // each column is the set of expression values of gene_i in all columns in the expression matrix
      if (printNormalizedExpression) {
        filename = new String("BICLUSTER_" + b.getID() + "_all_N.dat");
      }
      else {
        filename = new String("BICLUSTER_" + b.getID() + "_all.dat");
      }

      if (pathToResultsDirectory != null) {
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new File(
            pathToResultsDirectory, filename))), true);
      }
      else {
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(filename)), true);
      }

      for (int j = 0; j < geneExpressionMatrix[0].length; j++) {
        for (int i = 0; i < b.getNumberOfGenes(); i++) {
          fileOut.print("" + geneExpressionMatrix[b.getRowsIndexes()[i] - 1][j] +
                        "\t");
        }
        fileOut.println();
      }
      fileOut.close();

      // PRINT FILE BICLUSTER_ID.gplot
      if (printNormalizedExpression) {
        filename = new String("BICLUSTER_" + b.getID() + "_N.gplot");
      }
      else {
        filename = new String("BICLUSTER_" + b.getID() + ".gplot");
      }

      if (pathToResultsDirectory != null) {
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new File(
            pathToResultsDirectory, filename))), true);
      }
      else {
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(filename)), true);
      }

      fileOut.println("set term postscript eps");
      fileOut.println("set size 0.5,0.5");
      if (printNormalizedExpression) {
        fileOut.println("set output " + '"' + "BICLUSTER_" + b.getID() +
                        "_N.eps" + '"');
      }
      else {
        fileOut.println("set output " + '"' + "BICLUSTER_" + b.getID() + ".eps" +
                        '"');
      }
      fileOut.println("set xlabel " + '"' + "Bicluster Time-Points" + '"');
      if (printNormalizedExpression) {
        fileOut.println("set ylabel " + '"' + "Normalized Expression Value" +
                        '"');
      }
      else {
        fileOut.println("set ylabel " + '"' + "Expression Value" + '"');
      }
      String xtics = new String();
      int index = 0;
      int firstColumn = b.getColumnsIndexes()[0];
      for (int c = firstColumn; c < firstColumn + b.getNumberOfConditions(); c++) {
        if (index != 0) {
          xtics = xtics + ",";
        }
        xtics = xtics + '"' + c + '"' + ' ' + index;
        index++;
      }
      fileOut.println("set xtics (" + xtics + ")");
      fileOut.println("set nokey");
      fileOut.println("plot \\");
      String datFile;
      if (printNormalizedExpression) {
        datFile = new String("BICLUSTER_" + b.getID() + "_N.dat");
      }
      else {
        datFile = new String("BICLUSTER_" + b.getID() + ".dat");
      }
      for (int g = 1; g <= b.getNumberOfGenes(); g++) {
        String plot = '"' + datFile + '"' + " using " + g + " with lines";
        if (g != b.getNumberOfGenes()) {
          plot = plot + ",\\";
        }
        fileOut.println(plot);
      }
      fileOut.close();

      // PRINT FILE BICLUSTER_ID_all.gplot
      if (printNormalizedExpression) {
        filename = new String("BICLUSTER_" + b.getID() + "_all_N.gplot");
      }
      else {
        filename = new String("BICLUSTER_" + b.getID() + "_all.gplot");
      }

      if (pathToResultsDirectory != null) {
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new File(
            pathToResultsDirectory, filename))), true);
      }
      else {
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(filename)), true);
      }

      fileOut.println("set term postscript eps");
      fileOut.println("set size 0.5,0.5");
      if (printNormalizedExpression) {
        fileOut.println("set output " + '"' + "BICLUSTER_" + b.getID() +
                        "_all_N.eps" + '"');
      }
      else {
        fileOut.println("set output " + '"' + "BICLUSTER_" + b.getID() +
                        "_all.eps" + '"');
      }
      fileOut.println("set xlabel " + '"' + "All Time-Points" + '"');
      if (printNormalizedExpression) {
        fileOut.println("set ylabel " + '"' + "Normalized Expression Value" +
                        '"');
      }
      else {
        fileOut.println("set ylabel " + '"' + "Expression Value" + '"');
      }
      xtics = new String();
      for (int c = 0; c < geneExpressionMatrix[0].length; c++) {
        if (c != 0) {
          xtics = xtics + ",";
        }
        xtics = xtics + '"' + (c + 1) + '"' + ' ' + c;
      }
      fileOut.println("set xtics (" + xtics + ")");
      fileOut.println("set nokey");
      fileOut.println("plot \\");
      if (printNormalizedExpression) {
        datFile = new String("BICLUSTER_" + b.getID() + "_all_N.dat");
      }
      else {
        datFile = new String("BICLUSTER_" + b.getID() + "_all.dat");
      }
      for (int g = 1; g <= b.getNumberOfGenes(); g++) {
        String plot = '"' + datFile + '"' + " using " + g + " with lines";
        if (g != b.getNumberOfGenes()) {
          plot = plot + ",\\";
        }
        fileOut.println(plot);
      }
      fileOut.close();
    }
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   *
   * @param pathToResultsDirectory String
   * @param width int
   * @param height int
   * @param normalizeExpression boolean
   * @param legend boolean
   * @param numBiclustersToAnalyze int
   */
  public void printColorChartGeneExpressionBiclusterConditions_ToFiles(String
      pathToResultsDirectory,
      int width, int height, boolean normalizeExpression, boolean legend,
      int numBiclustersToAnalyze) {
    if (this.getNumberOfBiclusters() > 0) {
      int toPrint = numBiclustersToAnalyze;
      if (numBiclustersToAnalyze > this.getNumberOfBiclusters()) {
        toPrint = this.getNumberOfBiclusters();
      }
      for (int k = 0; k < toPrint; k++) {
        Bicluster b = this.getBiclusters().get(k);
        b.printColorChartGeneExpressionBiclusterConditions_ToFile(
            pathToResultsDirectory,
            "BICLUSTER_" + b.getID() + "_ExpressionChart.png", width, height,
            normalizeExpression, legend);
      }

    }
    else {
      System.out.println("THERE ARE NO BICLUSTER: IMPOSSIBLE TO PRINT CHARTS");
    }

  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   *
   * @param pathToDirectory String
   * @param width int
   * @param height int
   * @param normalizeExpression boolean
   * @param legend boolean
   * @param numBiclustersToAnalyze int
   */
  public void printColorChartGeneExpressionAllConditions_ToFiles(String
      pathToDirectory,
      int width, int height, boolean normalizeExpression, boolean legend,
      int numBiclustersToAnalyze) {
    if (this.getNumberOfBiclusters() > 0) {
      int toPrint = numBiclustersToAnalyze;
      if (numBiclustersToAnalyze > this.getNumberOfBiclusters()) {
        toPrint = this.getNumberOfBiclusters();
      }
      for (int k = 0; k < toPrint; k++) {
        Bicluster b = this.getBiclusters().get(k);
        b.printColorChartGeneExpressionAllConditions_ToFile(pathToDirectory,
            "BICLUSTER_" + b.getID() + "_ExpressionChartAllConditions.png",
            width, height, normalizeExpression, legend);
      }
    }
    else {
      System.out.println("THERE ARE NO BICLUSTER: IMPOSSIBLE TO PRINT CHARTS");
    }
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * Returns an Hashtable with the following information for each bicluster:
   * Bicluster ID - key
   * Best p-value
   * Best (Bonferroni corrected) p-value
   * # significant terms --> 0.01 <= (Bonferroni corrected) p-value < 0.05
   * # highly significant terms --> (Bonferroni corrected)p-value < 0.01
   * # significant terms according to user threshold --> (Bonferroni corrected) p-value < GO_pValues_userThreshold
   *
   * @param pathToGeneOntologyFile String
   * @param pathToGeneAssociationFile String
   * @param GO_pValues_userThreshold float
   * @return Hashtable
   * @throws Exception
   */
  public Hashtable computeBiologicalRelevanceUsingOntologizer(String
      pathToGeneOntologyFile,
      String pathToGeneAssociationFile,
      float GO_pValues_userThreshold) throws Exception {
    GOFrontEnd go = new GOFrontEnd( ( (IMatrix)this.matrix).getGenesNames(),
                                   pathToGeneOntologyFile,
                                   pathToGeneAssociationFile);
    return this.computeBiologicalRelevanceUsingOntologizer(go,
        GO_pValues_userThreshold);
  }

  //#####################################################################################################################

  /**
   * Returns an Hashtable with the following information for each bicluster:
   * Bicluster ID - key
   * Best p-value
   * Best (Bonferroni corrected) p-value
   * # significant terms --> 0.01 <= (Bonferroni corrected) p-value < 0.05
   * # highly significant terms --> (Bonferroni corrected)p-value < 0.01
   * # significant terms according to user threshold --> (Bonferroni corrected) p-value < GO_pValues_userThreshold
   *
   * @param go GOFrontEnd
   * @param GO_pValues_userThreshold float
   * @return Hashtable
   * @throws Exception
   */
  public Hashtable<Integer, BiologicalRelevanceSummary>
      computeBiologicalRelevanceUsingOntologizer(GOFrontEnd go,
                                                 float GO_pValues_userThreshold) throws
      Exception {

    if (this.getNumberOfBiclusters() > 0) {
      Hashtable<Integer, BiologicalRelevanceSummary>
          hashtable = new Hashtable<Integer,
          BiologicalRelevanceSummary> (this.getNumberOfBiclusters(),
                                       (float) 0.75);

      // Bicluster ID - key
      // Best p-value
      // Best (Bonferroni corrected) p-value
      // # significant terms --> 0.01 <= (Bonferroni corrected) p-value < 0.05
      // # highly significant terms --> (Bonferroni corrected)p-value < 0.01
      // # significant terms according to user threshold --> (Bonferroni corrected) p-value < GO_pValues_userThreshold

      //this.setSubtaskValues(this.getNumberOfBiclusters());
      for (int i = 0; i < this.getNumberOfBiclusters(); i++) {
        GeneSetResult geneOntologyTerms_i = this.getBiclusters().get(i).
            computeGeneOntologyTerms(go, false);
        BiologicalRelevanceSummary summary = this.setOfBiclusters.get(i).
            computeBiologicalRelevanceSummary(geneOntologyTerms_i,
                                              GO_pValues_userThreshold);
        hashtable.put(this.setOfBiclusters.get(i).getID(), summary);
        // no problems with colisions since IDs are unique
        //this.incrementSubtaskPercentDone();
      }
      //this.updatePercentDone();
      return hashtable;
    }
    else {
      return null;
    }
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * Prints the following information for each bicluster:
   * Bicluster ID
   * Best p-value
   * Best (Bonferroni corrected) p-value
   * # significant terms --> 0.01 <= (Bonferroni corrected) p-value < 0.05
   * # highly significant terms --> (Bonferroni corrected)p-value < 0.01
   * # significant terms according to user threshold --> (Bonferroni corrected) p-value < GO_pValues_userThreshold
   *
   * @param go GOFrontEnd
   * @param pathToDirectory String
   * @param filename String
   * @param GO_pValues_userThreshold float
   * @param numBiclustersToAnalyze int
   * @throws IOException
   */
  public void printBiologicalRelevanceSummary_ToFile(GOFrontEnd go,
      String pathToDirectory, String filename,
      float GO_pValues_userThreshold, int numBiclustersToAnalyze) throws
      IOException {

    if (this.getNumberOfBiclusters() > 0) {

      PrintWriter fileOut;
      if (pathToDirectory != null) {
        // CREATE DIRECTORY TO STORE THE RESULTS
        File dirForResults = new File(pathToDirectory);
        boolean exists = (dirForResults).exists();
        if (!exists) {
          dirForResults.mkdirs();
        }
        if (filename != null) {
          fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new
              File(pathToDirectory, filename))), true);
        }
        else {
          fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new
              File(pathToDirectory, "GO_Summary.txt"))), true);
        }
      }
      else {
        if (filename != null) {
          fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new
              File(pathToDirectory, filename))), true);
        }
        else {
          fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new File(
              "GO_Summary.txt"))), true);
        }
      }

      int toPrint = numBiclustersToAnalyze;
      if (numBiclustersToAnalyze > this.getNumberOfBiclusters()) {
        toPrint = this.getNumberOfBiclusters();
      }

      // Bicluster ID
      // Best p-value
      // Best (Bonferroni corrected) p-value
      // # significant terms --> 0.01 <= (Bonferroni corrected) p-value < 0.05
      // # highly significant terms --> (Bonferroni corrected)p-value < 0.01
      // # significant terms according to user threshold --> (Bonferroni corrected) p-value < GO_pValues_userThreshold

      fileOut.println("Bicluster ID" + "\t" + "Best p-value" + "\t" +
                      "Best p-value (Bonferroni corected)" + "\t" +
                      "# significant terms --> 0.01 >= (Bonferroni corrected) p-value < 0.05" +
                      "\t" +
                      "# highly significant terms --> (Bonferroni corrected)p-value < 0.01" +
                      "\t" +
                      "# significant terms according to user threshold --> (Bonferroni corrected) p-value < GO_pValues_userThreshold");

      for (int i = 0; i < toPrint; i++) {
        Bicluster b = this.getBiclusters().get(i);
        GeneSetResult geneOntologyTerms = b.computeGeneOntologyTerms(go, false);
        String bioSummary = b.getBiologicalRelevanceSummary(geneOntologyTerms,
            GO_pValues_userThreshold);
        fileOut.println(bioSummary);
      }
      fileOut.close();
    }
  }

  /**
   *
   * @param pathToDirectory String
   * @param filename String
   * @param numBiclustersToAnalyze int
   * @throws IOException
   */
  public void printBiclustersSummary_ToFile(String pathToDirectory, String filename,int numBiclustersToAnalyze) throws IOException{
    if (this.getNumberOfBiclusters() > 0){
      PrintWriter fileOut;
      if (pathToDirectory != null) {
        // CREATE DIRECTORY TO STORE THE RESULTS
        File dirForResults = new File(pathToDirectory);
        boolean exists = (dirForResults).exists();
        if (!exists) {
          dirForResults.mkdirs();
        }
        if (filename != null) {
          fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new
              File(pathToDirectory, filename))), true);
        }
        else {
          fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new
              File(pathToDirectory, "BICLUSTERS_Summary.txt"))), true);
        }
      }
      else {
        if (filename != null) {
          fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new
              File(pathToDirectory, filename))), true);
        }
        else {
          fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new File(
              "BICLUSTERS_Summary.txt"))), true);
        }
      }

      int toPrint = numBiclustersToAnalyze;
      if (numBiclustersToAnalyze > this.getNumberOfBiclusters()) {
        toPrint = this.getNumberOfBiclusters();
      }

      for (int i = 0; i < toPrint; i++) {
        this.getBiclusters().get(i).summaryToString();
        fileOut.print(this.getBiclusters().get(i).summaryToString());
      }
      fileOut.close();
    }
  }

  //#####################################################################################################################
  //#####################################################################################################################

  public Object clone() {
    Biclustering o = null;
    try {
      o = (Biclustering)super.clone();
    }
    catch (CloneNotSupportedException e) {
      System.err.println("Biclustering can't clone");
    }
    o.matrix = this.matrix;
    o.setOfBiclusters = (ArrayList<Bicluster>)this.setOfBiclusters.clone();
    o.setOfBiclusters = new ArrayList(0);
    for (int i = 0; i < this.getNumberOfBiclusters(); i++) {
      o.setOfBiclusters.add(i, this.getBiclusters().get(i).clone());
    }
    o.progressBar = this.progressBar;
    o.taskPercentages = this.taskPercentages;
    return o;
  }

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
    public void convert_From_Bimax_HashMaps(HashMap biClusters) {
        //setOfBiclusters = new ArrayList<Bicluster>();
        Bicluster bicluster;
        ArrayList<String> t;
        Set keys = biClusters.keySet();
        Object[] ka = keys.toArray();
        int[] rowIndexes, conditionIndexes;
        for (int i = 0; i < ka.length; i++){
            BitSet g = (BitSet) ((BitSet) ka[i]).clone();
            BitSet c = (BitSet)biClusters.get(ka[i]);

            rowIndexes = new int[g.cardinality()];int k=0;
            for (int j = g.nextSetBit(0); j >= 0; j = g.nextSetBit(j + 1))
                rowIndexes[k++] = j;

            conditionIndexes = new int[c.cardinality()];k=0;
            for (int j = c.nextSetBit(0); j >= 0; j = c.nextSetBit(j + 1))
                conditionIndexes[k++] = j;
            bicluster = new Bicluster(this,i,rowIndexes,conditionIndexes);
            setOfBiclusters.add(bicluster);
        }

    }
  public void convert_From_VAR_File(int Row) throws IOException {
    FileInputStream fstream = null;
    try {
      fstream = new FileInputStream("VAR.txt");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    DataInputStream in = new DataInputStream(fstream);
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    String line;
    StringTokenizer st;
      int sizeOfGenepart = Integer.parseInt(br.readLine());
      int sizeOfConditionPart = Integer.parseInt(br.readLine());
      ArrayList<Integer> rowIndexes = new ArrayList<Integer>();
      ArrayList<Integer> col_Indexes = new ArrayList<Integer>();
      int k = 0;int rowCounter=0;
    try {
      while ((line = br.readLine()) != null) {
          if( rowCounter==Row)break;
          st = new StringTokenizer(line);
          while (st.hasMoreTokens()) {
            rowIndexes = new ArrayList<Integer>();
            col_Indexes = new ArrayList<Integer>();
              for(int i=0;i<sizeOfGenepart;i++) {
                  int gIndex = (int)Double.parseDouble(st.nextToken());
                  if ( gIndex!= -1&& !rowIndexes.contains(gIndex))
                      rowIndexes.add(gIndex);
              }
              for(int i=0;i<sizeOfConditionPart;i++) {
                  int cIndex = (int)Double.parseDouble(st.nextToken());
                  if ( cIndex!= -1&& !col_Indexes.contains(cIndex))
                      col_Indexes.add(cIndex);
              }
              int []rIndexes = new int[rowIndexes.size()];
              int []cIndexes = new int[col_Indexes.size()];

              for(int i=0;i<rowIndexes.size();i++)
                  rIndexes[i] = rowIndexes.get(i);
              for(int i=0;i<col_Indexes.size();i++)
                  cIndexes[i] = col_Indexes.get(i);

              Bicluster bicluster = new Bicluster(this,k,rIndexes,cIndexes);
              setOfBiclusters.add(bicluster);
              k++;
          }
          rowCounter++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void convert_From_VAR_File_Binary() throws IOException {
      FileInputStream fstream = null;
      try {
          fstream = new FileInputStream("VAR");
      } catch (FileNotFoundException e) {
          e.printStackTrace();
      }
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String line;
      int sizeOfGenepart = Integer.parseInt(br.readLine());
      int sizeOfConditionPart = Integer.parseInt(br.readLine());
      ArrayList<Integer> rowIndexes = new ArrayList<Integer>();
      ArrayList<Integer> col_Indexes = new ArrayList<Integer>();
      int k = 0;
      try {
          while ((line = br.readLine()) != null) {
              rowIndexes = new ArrayList<Integer>();
              col_Indexes = new ArrayList<Integer>();
              for (int i = 0; i < sizeOfGenepart; i++) {
                  if (line.charAt(i) == '1') rowIndexes.add(i);
              }
              for (int i = sizeOfGenepart; i < sizeOfGenepart + sizeOfConditionPart; i++) {
                  if (line.charAt(i) == '1') col_Indexes.add(i - sizeOfGenepart);

              }
              int[] rIndexes = new int[rowIndexes.size()];
              int[] cIndexes = new int[col_Indexes.size()];

              for (int i = 0; i < rowIndexes.size(); i++)
                  rIndexes[i] = rowIndexes.get(i);
              for (int i = 0; i < col_Indexes.size(); i++)
                  cIndexes[i] = col_Indexes.get(i);

              Bicluster bicluster = new Bicluster(this, k, rIndexes, cIndexes);


//          int[] r = new int[sizeOfGenepart];
//          int[] c = new int[sizeOfConditionPart];
//          for(int i=0;i<sizeOfGenepart;i++)
//            r[i] = i;
//          for(int i=0;i<sizeOfConditionPart;i++)
//            c[i] = i;
//          Bicluster bicluster = new Bicluster(this,k,r,c);


              setOfBiclusters.add(bicluster);
              k++;
          }
      }catch(IOException e){
          e.printStackTrace();
      }

  }

} // END CLASS
