package smadeira.biclustering;

import smadeira.ontologizer.GOFrontEnd;
import smadeira.ontologizer.GeneSetResult;
import smadeira.utils.*;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * <p>Title: Post-Processed Biclustering</p>
 *
 * <p>Description: Contains methods for post-processing groups of biclusters
 *                 (biclustering objects).</p>
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
public class PostProcessedBiclustering {

  //######################################################################################################################
  //############################################ PRINT BICLUSTERS#########################################################
  //######################################################################################################################

  /**
   * Prints all maximal biclusters with their gene names and their pattern.
   *
   * EXAMPLE:
   *
   * DISCRETIZED MATRIX
   *
   * N   U   D   U   N
   * D   U   D   U   D
   * N   N   N   U   N
   * U   U   D   U   U
   *
   * #BICLUSTER	1	4-4	4
   * G4	U
   * G1	U
   * G3	U
   * G2	U
   * #BICLUSTER	2	4-5	2
   * G1	UN
   * G3	UN
   * #BICLUSTER	3	2-4	3
   * G4	UDU
   * G1	UDU
   * G2	UDU
   * #BICLUSTER	5	1-5	1
   * G4	UUDUU
   * #BICLUSTER	1	1-1	2
   * G1	N
   * G3	N
   * #BICLUSTER	5	1-5	1
   * G1	NUDUN
   * #BICLUSTER	5	1-5	1
   * G3	NNNUN
   * #BICLUSTER	5	1-5	1
   * G2	DUDUD
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void print_BiclustersGeneNamesPattern(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      System.out.print(bic.getBiclusters().get(k).toString());
    }
  }

  //#####################################################################################################################

  /**
   *
   * @param b Bicluster
   * @param go GOFrontEnd
   */
  private static void print_Bicluster(Bicluster b, GOFrontEnd go) {

    GeneSetResult geneSetResult = b.computeGeneOntologyTerms(go, false);

    System.out.print("#BICLUSTER_" + b.getID() + "\t" +
                     "CONDITIONS= " + b.getNumberOfConditions() + "\t" +
                     "FIRST-LAST= " + b.columnsIndexes[0] + "-" +
                     b.columnsIndexes[b.getNumberOfConditions() - 1] +
                     "\t" +
                     "GENES= " + b.getNumberOfGenes() + "\t");
    if (b instanceof CCC_Bicluster) {
      if (b instanceof CCC_Bicluster_SignChanges) {
        System.out.print("PATTERN=" +
                         new String( ( (CCC_Bicluster_SignChanges) b).
                                    getBiclusterExpressionPattern())
                         + " OR PATTERN= " +
                         new String( ( (CCC_Bicluster_SignChanges) b).
                                    getBiclusterExpressionPattern_SignChanges()) +
                         "\t"
                         + "PVALUE_PATTERN = " +
                         ( (CCC_Bicluster_SignChanges) b).
                         getBiclusterPattern_pValue(1) + "\t" +
                         "PVALUE_PATTERN_WITH_COLUMNS= " +
                         ( (CCC_Bicluster_SignChanges) b).
                         getBiclusterPatternWithColumns_pValue(1) + "\t");
      }
      else {
        if (b instanceof CCC_Bicluster_TimeLags) {
          System.out.print("PATTERN=" +
                           new String( ( (CCC_Bicluster_TimeLags) b).
                                      getBiclusterExpressionPattern()) + "\t"
                           + "PVALUE_PATTERN = " +
                           ( (CCC_Bicluster_TimeLags) b).
                           getBiclusterPattern_pValue(1) + "\t" +
                           "PVALUE_PATTERN_WITH_COLUMNS= " +
                           ( (CCC_Bicluster_TimeLags) b).
                           getBiclusterPatternWithColumns_pValue(1) + "\t");
        }
        else {
          System.out.print("PATTERN=" +
                           new String( ( (CCC_Bicluster) b).
                                      getBiclusterExpressionPattern()) + "\t"
                           + "PVALUE_PATTERN = " +
                           ( (CCC_Bicluster) b).getBiclusterPattern_pValue(1) +
                           "\t" +
                           "PVALUE_PATTERN_WITH_COLUMNS= " +
                           ( (CCC_Bicluster) b).
                           getBiclusterPatternWithColumns_pValue(1) + "\t");
        }
      }
    }
    System.out.print("MSR= " + b.computeMSR() + "\t" +
                     "VAR= " + b.computeVAR() + "\t" +
                     "ARV= " + b.computeARV() + "\t" +
                     "ACV= " + b.computeACV() + "\t" +
                     "ARV-MSR= " + (b.computeARV() - b.computeMSR()) + "\t");
    if (go != null) {
      System.out.println("BEST_GO_PVALUE= " +
                         b.computeBest_GO_pValue(geneSetResult) + "\t" +
                         "BEST_BONFERRONI_GO_PVALUE= " +
                         b.computeBest_GO_BonferroniCorrected_pValue(
                             geneSetResult) + "\t" +
                         "NUM_SIGNIFICANT_GO_PVALUES= " +
                         b.computeSignificant_GO_BonferroniCorrected_pValues(
                             geneSetResult));
    }
    else {
      System.out.println();
    }
    for (int g = 0; g < b.getNumberOfGenes(); g++) {
      if (b instanceof CCC_Bicluster) {
        if (b instanceof CCC_Bicluster_TimeLags) {
          String patternPositions = new String();
          for (int j = 0;
               j <
               ( (CCC_Bicluster_TimeLags) b).getBiclusterExpressionPattern().length;
               j++) {
            int[][] allColumnsIndexes = ( (CCC_Bicluster_TimeLags) b).
                getAllColumnsIndexes();
            patternPositions = patternPositions + allColumnsIndexes[g][j] +
                "\t";
          }
          System.out.println(b.getGenesNames()[g] + "\t" +
                             new String( ( (CCC_Bicluster) b).
                                        getBiclusterSymbolicMatrix()[g]) + "\t" +
                             patternPositions);

        }
        else {
          System.out.println(b.getGenesNames()[g] + "\t" +
                             new String( ( (CCC_Bicluster) b).
                                        getBiclusterSymbolicMatrix()[g]));
        }
      }
      else {
        System.out.print(b.getGenesNames()[g] + "\t");
        for (int c = 0; c < b.getNumberOfConditions(); c++) {
          System.out.print(b.getBiclusterExpressionMatrix()[g][c] + "\t");
        }
        System.out.println();
      }
    }
  }

  //#####################################################################################################################

  /**
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @throws Exception
   */
  public static void print_BiclustersGeneNamesPatternMetrics(Biclustering bic,
      GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      PostProcessedBiclustering.print_Bicluster(b, go);
    }
  }

  //#####################################################################################################################

  /**
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @throws Exception
   */
  public static void print_NonTrivialBiclustersGeneNamesPatternMetrics(
      Biclustering bic, GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() > 1) { // non-trivial bicluster
        PostProcessedBiclustering.print_Bicluster(b, go);
      }
    }
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS.
   * Filter #COLUMNS > 1 AND #ROWS < G (G>1).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param G int
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_GENES(Biclustering bic,
      GOFrontEnd go, int G) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() >= G) {
        PostProcessedBiclustering.print_Bicluster(b, go);
      }
    }
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS.
   * Filter ROWS = 1 AND #COLUMNS < C (C>1).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param C int
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_CONDITIONS(Biclustering
      bic, GOFrontEnd go, int C) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() >= C && b.getNumberOfGenes() > 1) {
        // non-trivial bicluster with #CONDITIONS >= C
        PostProcessedBiclustering.print_Bicluster(b, go);
      }
    }
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS TO FILE.
   * Filter #COLUMNS > 1 AND #ROWS = 1 AND ARV < Y.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param Y float
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_ARV(Biclustering bic,
      GOFrontEnd go, float Y) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() > 1 &&
          b.computeARV() >= Y) {
        // non-trivial bicluster with ARV >= Y
        PostProcessedBiclustering.print_Bicluster(b, go);
      }
    }
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS.
   * Filter #COLUMNS > 1 AND #ROWS = 1 AND ACV > Z.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param Z float
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_ACV(Biclustering bic,
      GOFrontEnd go, float Z) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() > 1 &&
          b.computeACV() <= Z) {
        // non-trivial bicluster with ARV >= Z
        PostProcessedBiclustering.print_Bicluster(b, go);
      }
    }
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS.
   * Filter #COLUMNS > 1 AND #ROWS = 1 AND SIZE < X AND ARV < Y AND ACV > Z.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param X int
   * @param Y float
   * @param Z float
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_SIZE_ARV_ACV(
      Biclustering bic, GOFrontEnd go, int X, float Y, float Z) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() > 1 &&
          b.computeSIZE() >= X && b.computeARV() >= Y && b.computeACV() <= Z) {
        // non-trivial bicluster with SIZE >= X AND ARC >= Y and ARV <= Z
        PostProcessedBiclustering.print_Bicluster(b, go);
      }
    }
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS.
   * Filter #COLUMNS > 1 AND #ROWS = 1 AND ARV < Y AND ACV > Z.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param Y float
   * @param Z float
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_ARV_ACV(Biclustering bic,
      GOFrontEnd go, float Y, float Z) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() > 1 &&
          b.computeARV() >= Y && b.computeACV() <= Z) {
        // non-trivial bicluster with ARC >= Y and ARV >= Z
        PostProcessedBiclustering.print_Bicluster(b, go);
      }
    }
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS.
   * Filter #COLUMNS < C (C>1) AND #ROWS < G (G<1) AND ARV < Y AND ACV > Z.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param C int
   * @param G int
   * @param Y float
   * @param Z float
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_CONDITIONS_GENES_ARV_ACV(
      Biclustering bic, GOFrontEnd go, int C, int G, float Y, float Z) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() >= C && b.getNumberOfGenes() >= G &&
          b.computeARV() >= Y && b.computeACV() <= Z) {
        // non-trivial bicluster with #CONDITIONS >= C AND #GENES >= G AND ARC >= Y and ARV <= Z
        PostProcessedBiclustering.print_Bicluster(b, go);
      }
    }
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS.
   * Filter #COLUMNS < C (C>1) and #ROWS < G (G>1) AND ARV < Y AND ACV > Z AND MSR > W.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param C int
   * @param G int
   * @param Y float
   * @param Z float
   * @param W float
   * @throws Exception
   */
  public static void
      print_NonTrivialBiclusters_FILTER_CONDITIONS_GENES_ARV_ACV_MSR(
          Biclustering bic, GOFrontEnd go, int C, int G, float Y, float Z,
          float W) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() >= C && b.getNumberOfGenes() >= G &&
          b.computeARV() >= Y && b.computeACV() <= Z && b.computeMSR() <= W) {
        // non-trivial bicluster with #CONDITIONS >= C AND #GENES >= G AND ARC >= Y and ARV <= Z and MSR <= W
        PostProcessedBiclustering.print_Bicluster(b, go);
      }
    }
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS TO FILE.
   * Filter #COLUMNS = 1 AND #ROWS = 1 AND MSR > W.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param W float
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_MSR(Biclustering bic,
      GOFrontEnd go, float W) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() > 1 &&
          b.computeMSR() <= W) {
        // non-trivial bicluster with MSR <= W
        PostProcessedBiclustering.print_Bicluster(b, go);
      }
    }
  }

  //######################################################################################################################
  //############################################### PRINT BICLUSTERS TO FILE #############################################
  //######################################################################################################################

  /**
   * Prints all biclusters with their gene names and their pattern to the file passed as parameter.
   *
   * @param bic Biclustering
   * @param filename String
   * @throws Exception
   */
  public static void print_BiclustersGeneNamesPattern_ToFile(
      Biclustering bic, String filename) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      fileOut.print(bic.getBiclusters().get(k).toString());
    }
    fileOut.close();
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   * Prints the top X biclusters with their gene names and their pattern to the file passed as parameter.
   * The value of X is passed in the parameter numberOfBiclustersToBePrinted.
   *
   * @param bic Biclustering
   * @param filename String
   * @param numberOfBiclustersToBePrinted int
   * @throws Exception
   */
  public static void print_BiclustersGeneNamesPattern_ToFile(
      Biclustering bic, String filename, int numberOfBiclustersToBePrinted) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    if (numberOfBiclustersToBePrinted > bic.getNumberOfBiclusters()) {
      throw new Exception("TOTAL NUMBER OF BICLUSTERS = " +
                          bic.getNumberOfBiclusters() + "\n" +
                          "CANNOT PRESENT THE TOP " +
                          numberOfBiclustersToBePrinted);
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(filename)), true);
    for (int k = 0; k < numberOfBiclustersToBePrinted; k++) {
      fileOut.print(bic.getBiclusters().get(k).toString());
    }
    fileOut.close();
  }

  //#####################################################################################################################

  /**
   * Prints the bicluster passed as paremeter to the a given PrinterWriter.
   *
   * @param b Bicluster
   * @param go GOFrontEnd
   * @param fileOut PrintWriter
   */
  private static void print_Bicluster_ToFile(Bicluster b, GOFrontEnd go,
                                             PrintWriter fileOut) {

    GeneSetResult geneSetResult = b.computeGeneOntologyTerms(go, false);
    fileOut.print("#BICLUSTER_" + b.getID() + "\t" +
                  "CONDITIONS= " + b.getNumberOfConditions() + "\t" +
                  "FIRST-LAST= " + b.columnsIndexes[0] + "-" +
                  b.columnsIndexes[b.getNumberOfConditions() - 1] +
                  "\t" +
                  "GENES= " + b.getNumberOfGenes() + "\t" +
                  "SIZE= " + b.computeSIZE() + "\t" +
                  "MSR= " + b.computeMSR() + "\t" +
                  "VAR= " + b.computeVAR() + "\t" +
                  "ARV= " + b.computeARV() + "\t" +
                  "ACV= " + b.computeACV() + "\t" +
                  "ARV-MSR= " + (b.computeARV() - b.computeMSR()) + "\t");
    if (b instanceof CCC_Bicluster) {
      CCC_Bicluster b1 = (CCC_Bicluster) b;
      fileOut.println("PVALUE_PATTERN= " + b1.getBiclusterPattern_pValue(1) +
                      "\t" +
                      "PVALUE_PATTERN_WITH_COLUMNS= " +
                      b1.getBiclusterPatternWithColumns_pValue(1) + "\t" +
                      "BONFERRONI_CORRECTED_PVALUE_PATTERN= " +
                      b1.getBiclusterPattern_BonferroniCorrected_pValue(1) +
                      "\t" +
                      "BONFERRONI_CORRECTED_PVALUE_PATTERN_WITH_COLUMNS= " +
                      b1.
                      getBiclusterPatternWithColumns_BonferroniCorrected_pValue(1));
    }
    if (go != null) {
      fileOut.println("BEST_GO_PVALUE= " +
                      b.computeBest_GO_pValue(geneSetResult) + "\t" +
                      "BEST_BONFERRONI_CORRECTED_GO_PVALUE= " +
                      b.computeBest_GO_BonferroniCorrected_pValue(
                          geneSetResult) + "\t" +
                      "#SIGNIFICANT_GO_PVALUES (P-VALUE<0.01) = " +
                      b.
                      computeHighlySignificant_GO_BonferroniCorrected_pValues(
                          geneSetResult) +
                      "\t" +
                      "#HIGHLY_SIGNIFICANT_GO_PVALUES (0.01<=P-VALUE <0.05) = " +
                      b.computeSignificant_GO_BonferroniCorrected_pValues(
                          geneSetResult));
    }
    for (int i = 0; i < b.getNumberOfGenes(); i++) {
      if (b instanceof CCC_Bicluster) {
        fileOut.println(b.getGenesNames()[i] + "\t" +
                        new String( ( (CCC_Bicluster) b).
                                   getBiclusterExpressionPattern()));
      }
      else {
        fileOut.print(b.getGenesNames()[i] + "\t");
        for (int j = 0; j < b.getNumberOfConditions(); j++) {
          fileOut.print(b.getBiclusterExpressionMatrix()[i][j] + "\t");
        }
        fileOut.println();
      }
    }
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   * Prints all biclusters together with the value of the evaluation metrics to the file passed as parameter.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param filename String
   * @throws Exception
   */
  public static void print_BiclustersGeneNamesPatternMetrics_ToFile(
      Biclustering bic, GOFrontEnd go, String filename) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      PostProcessedBiclustering.print_Bicluster_ToFile(b, go, fileOut);
    }
    fileOut.close();
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   * Prints the top X biclusters together with the value of the evaluation metrics to the file passed as parameter.
   * The value of X is passed in the parameter numberOfBiclustersToBePrinted.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param filename String
   * @param numberOfBiclustersToBePrinted int
   * @throws Exception
   */
  public static void print_BiclustersGeneNamesPatternMetrics_ToFile(
      Biclustering bic, GOFrontEnd go, String filename,
      int numberOfBiclustersToBePrinted) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    if (numberOfBiclustersToBePrinted > bic.getNumberOfBiclusters()) {
      throw new Exception("TOTAL NUMBER OF BICLUSTERS = " +
                          bic.getNumberOfBiclusters() + "\n" +
                          "CANNOT PRESENT THE TOP " +
                          numberOfBiclustersToBePrinted);
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int k = 0; k < numberOfBiclustersToBePrinted; k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      PostProcessedBiclustering.print_Bicluster_ToFile(b, go, fileOut);
    }
    fileOut.close();
  }

  //######################################################################################################################

  /**
   * Prints all biclusters together with the value of
   * #genes,#columns,#size, MSR, VAR, ARV, ACV, pValue1, pValue2 and pValue3
   * to the file passed as parameter.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param filename String
   * @throws Exception
   */
  public static void print_NonTrivialBiclustersGeneNamesPatternMetrics_ToFile(
      Biclustering bic, GOFrontEnd go, String filename) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() > 1) {
        PostProcessedBiclustering.print_Bicluster_ToFile(b, go, fileOut);
      }
    }
    fileOut.close();
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS TO FILE.
   * Filter #COLUMNS = 1 AND #ROWS = 1 (TRIVIAL CCC-BICLUSTERS).
   *
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param filename String
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_ToFile(
      Biclustering bic, GOFrontEnd go, String filename) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() > 1) { // non-trivial bicluster
        PostProcessedBiclustering.print_Bicluster_ToFile(b, go, fileOut);
      }
    }
    fileOut.close();
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS TO FILE.
   * Filter #COLUMNS = 1 AND #ROWS = 1 and SIZE < X.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param X minimum size
   * @param filename String
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_SIZE_ToFile(
      Biclustering bic, GOFrontEnd go, int X, String filename) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() > 1 &&
          b.computeSIZE() >= X) { // non-trivial bicluster with SIZE >= X
        PostProcessedBiclustering.print_Bicluster_ToFile(b, go, fileOut);
      }
    }
    fileOut.close();
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS TO FILE.
   * Filter #COLUMNS = 1 AND #ROWS < G (G>1).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param filename String
   * @param G int
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_GENES_ToFile(
      Biclustering bic, GOFrontEnd go, int G, String filename) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() >= G) {
        // non-trivial bicluster with #GENES >= G
        PostProcessedBiclustering.print_Bicluster_ToFile(b, go, fileOut);
      }
    }
    fileOut.close();
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS TO FILE.
   * Filter #ROWS = 1 AND #COLUMNS < C (C>1).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param filename String
   * @param C int
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_CONDITIONS_ToFile(
      Biclustering bic, GOFrontEnd go, int C, String filename) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfGenes() > 1 && b.getNumberOfConditions() >= C) {
        // non-trivial bicluster with #CONDITIONS >= C
        PostProcessedBiclustering.print_Bicluster_ToFile(b, go, fileOut);
      }
    }
    fileOut.close();
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS TO FILE.
   * Filter #COLUMNS > 1 AND #ROWS = 1 AND ARV < Y.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param Y Minimum ARV.
   * @param filename String
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_ARV_ToFile(
      Biclustering bic, GOFrontEnd go, float Y, String filename) throws
      Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() > 1 &&
          b.computeARV() >= Y) {
        // non-trivial bicluster with ARV >= Y
        PostProcessedBiclustering.print_Bicluster_ToFile(b, go, fileOut);
      }
    }
    fileOut.close();
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS TO FILE.
   * Filter #COLUMNS > 1 AND #ROWS = 1 AND ACV > Z.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param Z Maximum ACV.
   * @param filename String
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_ACV_ToFile(
      Biclustering bic, GOFrontEnd go, float Z, String filename) throws
      Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() > 1 &&
          b.computeACV() <= Z) {
        // non-trivial bicluster with ARV >= Z
        PostProcessedBiclustering.print_Bicluster_ToFile(b, go, fileOut);
      }
    }
    fileOut.close();
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS TO FILE.
   * Filter #COLUMNS > 1 AND #ROWS = 1 AND ARV < Y AND ACV > Z.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param Y Minimum ARV.
   * @param Z Maximum ACV.
   * @param filename String
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_ARV_ACV_ToFile(
      Biclustering bic, GOFrontEnd go, float Y, float Z, String filename) throws
      Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() > 1 &&
          b.computeARV() >= Y && b.computeACV() <= Z) {
        // non-trivial bicluster with ARC >= Y and ARV >= Z
        PostProcessedBiclustering.print_Bicluster_ToFile(b, go, fileOut);
      }
    }
    fileOut.close();
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS TO FILE.
   * Filter #COLUMNS > 1 AND #ROWS = 1 AND SIZE < X AND ARV < Y AND ACV > Z.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param X Mimimum SIZE.
   * @param Y Minimum ARV.
   * @param Z Maximum ACV.
   * @param filename String
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_SIZE_ARV_ACV_ToFile(
      Biclustering bic, GOFrontEnd go, int X, float Y, float Z, String filename) throws
      Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() > 1 &&
          b.computeSIZE() >= X && b.computeARV() >= Y && b.computeACV() <= Z) {
        // non-trivial bicluster with SIZE >= X AND ARC >= Y and ARV <= Z
        PostProcessedBiclustering.print_Bicluster_ToFile(b, go, fileOut);
      }
    }
    fileOut.close();
  }

  //##################################################################################################################### static

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS TO FILE.
   * Filter #COLUMNS < C (C>1) and #ROWS < G (G>1) AND ARV < Y AND ACV > Z.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param C Minimum number of conditions.
   * @param G Minimum number of genes.
   * @param Y Minimum ARV.
   * @param Z Maximum ACV.
   * @param filename String
   * @throws Exception
   */
  public static void
      print_NonTrivialBiclusters_FILTER_CONDITIONS_GENES_ARV_ACV_ToFile(
          Biclustering bic, GOFrontEnd go, int C, int G, float Y, float Z,
          String filename) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() >= C && b.getNumberOfGenes() >= G &&
          b.computeARV() >= Y && b.computeACV() <= Z) {
        // non-trivial bicluster with #CONDITIONS >= C AND #GENES >= G AND ARC >= Y and ARV <= Z
        PostProcessedBiclustering.print_Bicluster_ToFile(b, go, fileOut);
      }
    }
    fileOut.close();
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS TO FILE.
   * Filter #COLUMNS < C (C>1) AND #ROWS < G (G>1) AND ARV < Y AND ACV > Z AND MSR > W.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param C Minimum number of conditions.
   * @param G Minimum number of genes.
   * @param Y Minimum ARV.
   * @param Z Maximum ACV.
   * @param W Maximum MSR.
   * @param filename String
   * @throws Exception
   */
  public static void
      print_NonTrivialBiclusters_FILTER_CONDITIONS_GENES_ARV_ACV_MSR_ToFile(
          Biclustering bic, GOFrontEnd go, int C, int G, float Y, float Z,
          float W, String filename) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() >= C && b.getNumberOfGenes() >= G &&
          b.computeARV() >= Y && b.computeACV() <= Z && b.computeMSR() <= W) {
        // non-trivial bicluster with #CONDITIONS >= C AND #GENES >= G AND ARC >= Y and ARV <= Z and MSR <= W
        PostProcessedBiclustering.print_Bicluster_ToFile(b, go, fileOut);
      }
    }
    fileOut.close();
  }

  //#####################################################################################################################

  /**
   * PRINT MAXIMAL NON-TRIVIAL CCC-BICLUSTERS TO FILE.
   * Filter #COLUMNS = 1 AND #ROWS = 1 AND MSR > W.
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param W Maximum MSR.
   * @param filename String
   * @throws Exception
   */
  public static void print_NonTrivialBiclusters_FILTER_MSR_ToFile(
      Biclustering bic, GOFrontEnd go, float W, String filename) throws
      Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() > 1 && b.getNumberOfGenes() > 1 &&
          b.computeMSR() <= W) {
        // non-trivial bicluster with MSR <= W
        PostProcessedBiclustering.print_Bicluster_ToFile(b, go, fileOut);
      }
    }
    fileOut.close();
  }

  //#####################################################################################################################
  //################################### FILTER BICLUSTERS ACCORDING TO GIVEN CRITERIA ###################################
  //#####################################################################################################################

  /**
   * Filter #COLUMNS = 1 OR #ROWS = 1 (TRIVIAL CCC-BICLUSTERS).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void filter_TRIVIAL(Biclustering bic) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }
    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() == 1 || b.getNumberOfGenes() == 1) { // non-trivial bicluster
      }
      else {
        set.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
    bic.setBiclusters(set);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS = 1 AND #ROWS = 1 and SIZE < X.
   *
   * @param bic Biclustering
   * @param X int
   * @throws Exception
   */
  public static void filter_SIZE(Biclustering bic, int X) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }
    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() == 1 || b.getNumberOfGenes() == 1 ||
          b.computeSIZE() < X) { // non-trivial bicluster with SIZE >= X
      }
      else {
        set.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
    bic.setBiclusters(set);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS = 1 AND #ROWS < G (G>1).
   *
   * @param bic Biclustering
   * @param G int
   * @throws Exception
   */
  public static void filter_GENES(Biclustering bic, int G) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }
    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() == 1 || b.getNumberOfGenes() < G) {
      }
      else {
        set.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
    bic.setBiclusters(set);
  }

  //#####################################################################################################################

  /**
   * Filter #ROWS = 1 AND #COLUMNS < C (C>1).
   *
   * @param bic Biclustering
   * @param C int
   * @throws Exception
   */
  public static void filter_CONDITIONS(Biclustering bic, int C) throws
      Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfGenes() == 1 || b.getNumberOfConditions() < C) {
      }
      else {
        set.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
    bic.setBiclusters(set);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS = 1 OR #ROWS = 1 OR ARV < Y.
   *
   * @param bic Biclustering
   * @param Y float
   * @throws Exception
   */
  public static void filter_ARV(Biclustering bic, float Y) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }
    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() == 1 || b.getNumberOfGenes() == 1 ||
          b.computeARV() < Y) {
      }
      else {
        set.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
    bic.setBiclusters(set);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS = 1 OR #ROWS = 1 AND ACV > Z.
   *
   * @param bic Biclustering
   * @param Z float
   * @throws Exception
   */
  public static void filter_ACV(Biclustering bic, float Z) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() == 1 || b.getNumberOfGenes() == 1 ||
          b.computeACV() > Z) {
      }
      else {
        set.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
    bic.setBiclusters(set);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS = 1 OR #ROWS = 1 OR ARV < Y OR ACV > Z.
   *
   * @param bic Biclustering
   * @param Y float
   * @param Z float
   * @throws Exception
   */
  public static void filter_ARV_ACV(Biclustering bic, float Y, float Z) throws
      Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }
    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() == 1 || b.getNumberOfGenes() == 1 ||
          b.computeARV() < Y && b.computeACV() > Z) {
      }
      else {
        set.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
    bic.setBiclusters(set);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS = 1 OR #ROWS = 1 OR SIZE < X AND ARV < Y AND ACV > Z.
   *
   * @param bic Biclustering
   * @param X int
   * @param Y float
   * @param Z float
   * @throws Exception
   */
  public static void filter_SIZE_ARV_ACV(Biclustering bic, int X, float Y,
                                         float Z) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }
    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() == 1 || b.getNumberOfGenes() == 1 ||
          b.computeSIZE() < X || b.computeARV() < Y || b.computeACV() > Z) {
      }
      else {
        set.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
    bic.setBiclusters(set);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS = C (C>1) OR #ROWS = G (G>1) OR ARV < Y OR ACV > Z.
   *
   * @param bic Biclustering
   * @param C int
   * @param G int
   * @param Y float
   * @param Z float
   * @throws Exception
   */
  public static void filter_CONDITIONS_GENES_ARV_ACV(Biclustering bic, int C,
      int G, float Y, float Z) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }
    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() >= C || b.getNumberOfGenes() < G ||
          b.computeARV() < Y || b.computeACV() > Z) {
      }
      else {
        set.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
    bic.setBiclusters(set);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS < C (C>1) OR #ROWS < G (G>1) OR ARV < Y OR ACV > Z OR MSR > W.
   *
   * @param bic Biclustering
   * @param C int
   * @param G int
   * @param Y float
   * @param Z float
   * @param W float
   * @throws Exception
   */
  public static void filter_CONDITIONS_GENES_ARV_ACV_MSR(Biclustering bic,
      int C, int G, float Y, float Z, float W) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }
    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() == C || b.getNumberOfGenes() == G ||
          b.computeARV() < Y && b.computeACV() > Z || b.computeMSR() > W) {
      }
      else {
        set.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
    bic.setBiclusters(set);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS = 1 OR #ROWS = 1 AND MSR > W.
   *
   * @param bic Biclustering
   * @param W float
   * @throws Exception
   */
  public static void filter_MSR(Biclustering bic, float W) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      if (b.getNumberOfConditions() == 1 || b.getNumberOfGenes() == 1 ||
          b.computeMSR() > W) {
      }
      else {
        set.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
    bic.setBiclusters(set);
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * Filter #COLUMNS = 1 AND #ROWS = 1 (TRIVIAL CCC-BICLUSTERS).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_filter_TRIVIAL(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_TRIVIAL(newBic);
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS = 1 AND #ROWS = 1 and SIZE < X.
   *
   * @param bic Biclustering
   * @param X int
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_filter_SIZE(Biclustering bic, int X) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_SIZE(newBic, X);
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS = 1 AND #ROWS < G (G>1).
   *
   * @param bic Biclustering
   * @param G int
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_filter_GENES(Biclustering bic, int G) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_GENES(newBic, G);
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Filter #ROWS = 1 AND #COLUMNS < C (C>1).
   *
   * @param bic Biclustering
   * @param C int
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_filter_CONDITIONS(Biclustering bic, int C) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_CONDITIONS(newBic, C);
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS > 1 AND #ROWS = 1 AND ARV < Y.
   *
   * @param bic Biclustering
   * @param Y float
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_filter_ARV(Biclustering bic, float Y) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_ARV(newBic, Y);
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS > 1 AND #ROWS = 1 AND ACV > Z.
   *
   * @param bic Biclustering
   * @param Z float
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_filter_ACV(Biclustering bic, float Z) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_ACV(newBic, Z);
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS > 1 AND #ROWS = 1 AND ARV < Y AND ACV > Z.
   *
   * @param bic Biclustering
   * @param Y float
   * @param Z float
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_filter_ARV_ACV(Biclustering bic, float Y,
                                                float Z) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_ARV_ACV(newBic, Y, Z);
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS > 1 AND #ROWS = 1 AND SIZE < X AND ARV < Y AND ACV > Z.
   *
   * @param bic Biclustering
   * @param X int
   * @param Y float
   * @param Z float
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_filter_SIZE_ARV_ACV(Biclustering bic, int X,
      float Y, float Z) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_SIZE_ARV_ACV(newBic, X, Y, Z);
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS < C (C>1) and #ROWS < G (G>1) AND ARV < Y AND ACV > Z.
   *
   * @param bic Biclustering
   * @param C int
   * @param G int
   * @param Y float
   * @param Z float
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering
      new_filter_CONDITIONS_GENES_ARV_ACV(Biclustering bic, int C, int G,
                                          float Y, float Z) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_CONDITIONS_GENES_ARV_ACV(newBic, C, G,
        Y, Z);
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS < C (C>1) AND #ROWS < G (G>1) AND ARV < Y AND ACV > Z AND MSR > W.
   *
   * @param bic Biclustering
   * @param C int
   * @param G int
   * @param Y float
   * @param Z float
   * @param W float
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering
      new_filter_CONDITIONS_GENES_ARV_ACV_MSR(Biclustering bic, int C, int G,
                                              float Y, float Z, float W) throws
      Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_CONDITIONS_GENES_ARV_ACV_MSR(newBic,
        C, G, Y, Z, W);
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Filter #COLUMNS = 1 AND #ROWS = 1 AND MSR > W.
   *
   * @param bic Biclustering
   * @param W float
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_filter_MSR(Biclustering bic, float W) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_MSR(newBic, W);
    return (newBic);
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * Filter biclusters with number of significant GO p-values = 0 (significant p-value => p-value < 0.05)
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @throws Exception
   */
  public static void filter_NOT_Significant_GO_pValues(Biclustering bic,
      GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      GeneSetResult result = b.computeGeneOntologyTerms(go, false);
      if (b.computeSignificant_GO_pValues(result) != 0) {
        set.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
  }

  //#####################################################################################################################

  /**
   * Filter biclusters with number of significant (Bonferroni corrected) GO p-values = 0 (significant p-value => p-value < 0.05)
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @throws Exception
   */
  public static void filter_NOT_Significant_GO_BonferroniCorrected_pValues(
      Biclustering bic, GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      GeneSetResult result = b.computeGeneOntologyTerms(go, false);
      if (b.computeSignificant_GO_BonferroniCorrected_pValues(result) != 0) {
        set.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
  }

  //#####################################################################################################################

  /**
   * Filter biclusters with number of highly significant GO p-values = 0 (highly significant p-value => p-value < 0.01)
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @throws Exception
   */
  public static void filter_NOT_HighlySignificant_GO_pValues(Biclustering bic,
      GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      GeneSetResult result = b.computeGeneOntologyTerms(go, false);
      if (b.computeHighlySignificant_GO_pValues(result) != 0) {
        set.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
  }

  //#####################################################################################################################

  /**
   * Filter biclusters with number of highly significant (Bonferroni corrected) GO p-values = 0 (highly significant p-value => p-value < 0.01)
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @throws Exception
   */
  public static void
      filter_NOT_HighlySignificant_GO_BonferroniCorrected_pValues(Biclustering
      bic, GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      GeneSetResult result = b.computeGeneOntologyTerms(go, false);
      if (b.computeHighlySignificant_GO_BonferroniCorrected_pValues(result) !=
          0) {
        set.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
  }

  //#####################################################################################################################

  /**
   * Filter biclusters with number of significant according to user threshold (Bonferroni corrected) GO p-values = 0 (highly significant p-value => p-value < user threshold)
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param GO_pValue_userThreshold float
   * @throws Exception
   */
  public static void
      filter_NOT_Significant_GO_BonferroniCorrected_pValues_userThreshold(
      Biclustering bic, GOFrontEnd go, float GO_pValue_userThreshold) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);Bicluster b;
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
        //System.out.print("bicluster : "+k+"\n");
        b = new Bicluster();
        b = (Bicluster) bic.getBiclusters().get(k);//((Bicluster)bic.getBiclusters().get(k))instead of b
        try {
            GeneSetResult result = b.computeGeneOntologyTerms(go, true);
            if (b.computeSignificant_GO_BonferroniCorrected_pValues_UserThreshold(
                    result, GO_pValue_userThreshold) != 0) {
                set.add(b);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
      //bic.incrementSubtaskPercentDone();
    }
    bic.setOfBiclusters = set;//خودم اضافه کردم
  }

  //#####################################################################################################################

  /**
   * Filter biclusters with number of significant according to user threshold GO p-values = 0 (highly significant p-value => p-value < user threshold)
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param GO_pValue_userThreshold float
   * @throws Exception
   */
  public static void filter_NOT_Significant_GO_pValues_userThreshold(
      Biclustering bic, GOFrontEnd go, float GO_pValue_userThreshold) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      Bicluster b = (Bicluster) bic.getBiclusters().get(k);
      GeneSetResult result = b.computeGeneOntologyTerms(go, false);
      if (b.computeSignificant_GO_pValues_UserThreshold(result,
          GO_pValue_userThreshold) > 0) {//اصلاحش کردم
        set.add(b);
      }
      //bic.incrementSubtaskPercentDone();
    }
      bic.setOfBiclusters = set;//خودم اضافه کردم
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * Filter biclusters with number of significant GO p-values = 0 (significant p-value => p-value < 0.05)
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_filter_NOT_Significant_GO_pValues(Biclustering
      bic, GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_NOT_Significant_GO_pValues(newBic, go);
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Filter biclusters with number of significant (Bonferroni corrected) GO p-values = 0 (significant p-value => p-value < 0.05)
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering
      new_filter_NOT_Significant_BonferroniCorrected_GO_pValues(Biclustering
      bic, GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.
        filter_NOT_Significant_GO_BonferroniCorrected_pValues(newBic, go);
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Filter biclusters with number of highly significant GO p-values = 0 (highly significant p-value => p-value < 0.01)
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_filter_NOT_HighlySignificant_GO_pValues(
      Biclustering bic, GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_NOT_HighlySignificant_GO_pValues(newBic,
        go);
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Filter biclusters with number of highly significant (Bonferroni corrected) GO p-values = 0 (highly significant p-value => p-value < 0.01)
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering
      new_filter_NOT_HighlySignificant_BonferroniCorrected_GO_pValues(
      Biclustering bic, GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.
        filter_NOT_HighlySignificant_GO_BonferroniCorrected_pValues(newBic, go);
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Filter biclusters with number of significant according to user threshold (Bonferroni corrected) GO p-values = 0 (highly significant p-value => p-value < user threshold)
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param GO_pValue_userThreshold float
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering
      new_filter_NOT_Significant_BonferroniCorrected_GO_pValues_userThreshold(
      Biclustering bic, GOFrontEnd go, float GO_pValue_userThreshold) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.
        filter_NOT_Significant_GO_BonferroniCorrected_pValues_userThreshold(
        newBic, go, GO_pValue_userThreshold);
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Filter biclusters with number of significant according to user threshold GO p-values = 0 (highly significant p-value => p-value < user threshold)
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param GO_pValue_userThreshold float
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering
      new_filter_NOT_Significant_GO_pValues_userThreshold(Biclustering bic,
      GOFrontEnd go, float GO_pValue_userThreshold) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_NOT_Significant_GO_pValues_userThreshold(
        newBic, go, GO_pValue_userThreshold);
    return (newBic);
  }

  //#####################################################################################################################
  //################################################# SORT BICLUSTERS####################################################
  //#####################################################################################################################

  /**
   * Sort biclusters by number of conditions in ascending order (ASC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_CONDITIONS_ASC(Biclustering bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new
                     Comparator_HASH_numberOfConditions_ASC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by number of conditions in descending order (DESC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_CONDITIONS_DESC(Biclustering bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new
                     Comparator_HASH_numberOfConditions_DESC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by number of genes in ascending order (ASC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_GENES_ASC(Biclustering bic) {
    Collections.sort(bic.getBiclusters(),
                     new Comparator_HASH_numberOfGenes_ASC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by number of genes in descending order (DESC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_GENES_DESC(Biclustering bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(),
                     new Comparator_HASH_numberOfGenes_DESC(bic));
  }

  /**
   * Sort biclusters by size in ascending order (ASC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_SIZE_ASC(Biclustering bic) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new Comparator_HASH_SIZE_ASC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by size in descending order (DESC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_SIZE_DESC(Biclustering bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new Comparator_HASH_SIZE_DESC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by mean squared residue (MSR) in ascending order (ASC).
   *
   * @param bic Biclustering
   */
  public static void sort_MSR_ASC(Biclustering bic) {
    Collections.sort(bic.getBiclusters(), new Comparator_HASH_MSR_ASC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by mean squared residue (MSR) in descending order (DESC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_MSR_DESC(Biclustering bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new Comparator_HASH_MSR_DESC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by variance (VAR) in ascending order (ASC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_VAR_ASC(Biclustering bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new Comparator_HASH_VAR_ASC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by variance (VAR) in descending order (DESC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_VAR_DESC(Biclustering bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new Comparator_HASH_VAR_DESC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by average row variance (ARV) in ascending order (ASC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_ARV_ASC(Biclustering bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new Comparator_HASH_ARV_ASC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by average row variance (ACV) in descending order (DESC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_ARV_DESC(Biclustering bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new Comparator_HASH_ARV_DESC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by average column variance (ACV) in ascending order (ASC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_ACV_ASC(Biclustering bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new Comparator_HASH_ACV_ASC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by average column variance (ACV) in descending order (DESC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_ACV_DESC(Biclustering bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }
    Collections.sort(bic.getBiclusters(), new Comparator_HASH_ACV_DESC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by (ARV-ACV) in descending order (DESC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_ARV_ACV_DESC(Biclustering bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new Comparator_HASH_ARV_ACV_DESC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by (ARV-ACV) in ascending (ASC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_ARV_ACV_ASC(Biclustering bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new Comparator_HASH_ARV_ACV_ASC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by (ARV-MSR) in descending order (DESC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_ARV_MSR_DESC(Biclustering bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new Comparator_HASH_ARV_MSR_DESC(bic));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by (ARV-MSR) in ascending (ASC).
   *
   * @param bic Biclustering
   * @throws Exception
   */
  public static void sort_ARV_MSR_ASC(Biclustering bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new Comparator_HASH_ARV_MSR_ASC(bic));
  }

  //##################################################################################################################### static
  //##################################################################################################################### static

  /**
   * Sort biclusters by best_GO_pValue in ascending order (ASC).
   *
   * @param bic CCC_Biclustering
   * @param go GOFrontEnd
   * @throws Exception
   */
  public static void sort_Best_GO_pValue_ASC(CCC_Biclustering bic,
                                             GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(),
                     new Comparator_HASH_best_GO_pValue_ASC(bic, go));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by best_GO_BonferroniCorrected_pValue in ascending order (ASC).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @throws Exception
   */
  public static void sort_Best_GO_BonferroniCorrected_pValue_ASC(Biclustering
      bic, GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new
                     Comparator_HASH_best_GO_BonferroniCorrected_pValue_ASC(bic,
        go));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by significant_GO_BonferroniCorrected_pValues in descending order (DESC).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @throws Exception
   */
  public static void sort_Significant_GO_BonferroniCorrected_pValues_DESC(
      Biclustering bic, GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }
    Collections.sort(bic.getBiclusters(),
                     new
        Comparator_HASH_significant_GO_BonferroniCorrected_pValues_DESC(bic, go));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by significant_GO_pValues in descending order (DESC).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @throws Exception
   */
  public static void sort_Significant_GO_pValues_DESC(Biclustering bic,
      GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(),
                     new Comparator_HASH_significant_GO_pValues_DESC(bic, go));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by highlySignificant_GO_BonferroniCorrected_pValues in descending order (DESC).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @throws Exception
   */
  public static void sort_HighlySignificant_GO_BonferroniCorrected_pValues_DESC(
      Biclustering bic, GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new
                     Comparator_HASH_highlySignificant_GO_BonferroniCorrected_pValues_DESC(
        bic, go));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by highlySignificant_GO_pValues in descending order (DESC).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @throws Exception
   */
  public static void sort_HighlySignificant_GO_pValues_DESC(Biclustering bic,
      GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(),
                     new
                     Comparator_HASH_highlySignificant_GO_pValues_DESC(bic, go));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by significant_GO_BonferroniCorrected_pValues in descending order (DESC).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param GO_UserThreshold float
   * @throws Exception
   */
  public static void sort_Significant_GO_BonferroniCorrected_pValues_DESC(
      Biclustering bic, GOFrontEnd go, float GO_UserThreshold) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(), new
                     Comparator_HASH_significant_GO_BonferroniCorrected_pValues_userThreshold_DESC(
        bic, go, GO_UserThreshold));
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by significant_GO_pValues_UserThreshold in descending order (DESC).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param GO_UserThreshold float
   * @throws Exception
   */
  public static void sort_Significant_GO_pValues_UserThreshold_DESC(
      Biclustering bic, GOFrontEnd go, float GO_UserThreshold) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(),
                     new
                     Comparator_HASH_significant_GO_pValues_userThreshold_DESC(
        bic, go, GO_UserThreshold));
  }

  //#####################################################################################################################
  //#####################################################################################################################
  //#####################################################################################################################
  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * Sort biclusters by number of conditions in ascending order (ASC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_CONDITIONS_ASC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_numberOfConditions_ASC(newBic));
    return (newBic);
  }

  //#####################################################################################################################

  /**
   * Sort biclusters by number of conditions in descending order (DESC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_CONDITIONS_DESC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
//     Comparator_NumberOfConditions_DESC c = new
//         Comparator_NumberOfConditions_DESC();
//     Collections.sort(newBic.getBiclusters(), c);
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_numberOfConditions_DESC(newBic));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by number of genes in ascending order (ASC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_GENES_ASC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_numberOfGenes_ASC(newBic));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by number of genes in descending order (DESC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_GENES_DESC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
//     Comparator_NumberOfGenes_DESC c = new Comparator_NumberOfGenes_DESC();
//     Collections.sort(newBic.getBiclusters(), c);
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_numberOfGenes_DESC(newBic));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by size in ascending order (ASC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_SIZE_ASC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_SIZE_ASC(newBic));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by size in descending order (DESC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_SIZE_DESC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
//     Comparator_SIZE_DESC c = new Comparator_SIZE_DESC();
//     Collections.sort(newBic.getBiclusters(), c);
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_SIZE_DESC(newBic));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by mean squared residue (MSR) in ascending order (ASC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_MSR_ASC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(), new Comparator_HASH_MSR_ASC(newBic));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by mean squared residue (MSR) in descending order (DESC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_MSR_DESC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
//     Comparator_MSR_DESC c = new Comparator_MSR_DESC();
//     Collections.sort(newBic.getBiclusters(), c);
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_MSR_DESC(newBic));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by variance (VAR) in ascending order (ASC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_VAR_ASC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(), new Comparator_HASH_VAR_ASC(newBic));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by variance (VAR) in descending order (DESC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_VAR_DESC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_SIZE_DESC(newBic));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by average row variance (ARV) in ascending order (ASC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_ARV_ASC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(), new Comparator_HASH_ARV_ASC(newBic));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by average row variance (ACV) in descending order (DESC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_ARV_DESC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_ARV_DESC(newBic));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by average column variance (ACV) in ascending order (ASC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_ACV_ASC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(), new Comparator_HASH_ACV_ASC(newBic));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by average column variance (ACV) in descending order (DESC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_ACV_DESC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_ACV_DESC(newBic));
    return (newBic);
  }

  /**
   * Sort biclusters by (ARV-ACV) in descending order (DESC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_ARV_ACV_DESC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_ARV_ACV_DESC(newBic));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by (ARV-ACV) in ascending (ASC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_ARV_ACV_ASC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_ARV_ACV_ASC(newBic));
    return (newBic);
  }

  /**
   * Sort biclusters by (ARV-MSR) in descending order (DESC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_ARV_MSR_DESC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_ARV_MSR_DESC(newBic));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by (ARV-MSR) in ascending (ASC).
   *
   * @param bic Biclustering
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_ARV_MSR_ASC(Biclustering bic) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_ARV_MSR_ASC(newBic));
    return (newBic);
  }

  //##################################################################################################################### static
  //##################################################################################################################### static

  /**
   * Sort biclusters by best_GO_pValue in ascending order (ASC).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_Best_GO_pValue_ASC(Biclustering bic,
      GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_best_GO_pValue_ASC(newBic, go));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by best_GO_BonferroniCorrected_pValue in ascending order (ASC).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_Best_GO_BonferroniCorrected_pValue_ASC(
      Biclustering bic, GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new
                     Comparator_HASH_best_GO_BonferroniCorrected_pValue_ASC(newBic,
        go));
    return (newBic);
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by significant_GO_BonferroniCorrected_pValues in descending order (DESC).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering
      new_sort_Significant_GO_BonferroniCorrected_pValues_DESC(Biclustering bic,
      GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
        new
        Comparator_HASH_significant_GO_BonferroniCorrected_pValues_DESC(newBic,
        go));
    return newBic;
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by significant_GO_pValues in descending order (DESC).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_Significant_GO_pValues_DESC(Biclustering
      bic, GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_significant_GO_pValues_DESC(newBic, go));
    return newBic;
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by highlySignificant_GO_BonferroniCorrected_pValues in descending order (DESC).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering
      new_sort_HighlySignificant_GO_BonferroniCorrected_pValues_DESC(
      Biclustering bic, GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
        new
        Comparator_HASH_highlySignificant_GO_BonferroniCorrected_pValues_DESC(newBic,
        go));
    return newBic;
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by highlySignificant_GO_pValues in descending order (DESC).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_HighlySignificant_GO_pValues_DESC(
      Biclustering bic, GOFrontEnd go) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new Comparator_HASH_significant_GO_pValues_DESC(newBic, go));
    return newBic;
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by significant_GO_BonferroniCorrected_pValues in descending order (DESC).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param GO_UserThreshold float
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering
      new_sort_Significant_GO_BonferroniCorrected_pValues_DESC(Biclustering bic,
      GOFrontEnd go, float GO_UserThreshold) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new
        Comparator_HASH_significant_GO_BonferroniCorrected_pValues_userThreshold_DESC(
        newBic, go, GO_UserThreshold));
    return newBic;
  }

  //##################################################################################################################### static

  /**
   * Sort biclusters by significant_GO_pValues_UserThreshold in descending order (DESC).
   *
   * @param bic Biclustering
   * @param go GOFrontEnd
   * @param GO_UserThreshold float
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_sort_Significant_GO_pValues_UserThreshold_DESC(
      Biclustering bic, GOFrontEnd go, float GO_UserThreshold) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    Collections.sort(newBic.getBiclusters(),
                     new
                     Comparator_HASH_significant_GO_pValues_userThreshold_DESC(
        newBic, go, GO_UserThreshold));
    return newBic;
  }

  //######################################################################################################################
  //############################################### FILTER OVERLAPPING BICLUSTERS ########################################
  //######################################################################################################################

  /**
   * b1 = (I1,J1) and b2 = (I2,J2)
   *
   * I1 intersection I2
   *
   * @param b1 Bicluster
   * @param b2 Bicluster
   * @return TreeSet
   */
  public static TreeSet<Integer> compute_CommonRows(Bicluster b1, Bicluster b2) {
    TreeSet<Integer> commonRows = new TreeSet<Integer> ();
    // SET GENES b1
    TreeSet<Integer> set_genes_b1 = new TreeSet();
    for (int g = 0; g < b1.getNumberOfGenes(); g++) {
      set_genes_b1.add(new Integer(b1.rowsIndexes[g]));
    }
    // SET GENES b2
    TreeSet<Integer> set_genes_b2 = new TreeSet();
    for (int g = 0; g < b2.getNumberOfGenes(); g++) {
      set_genes_b2.add(new Integer(b2.rowsIndexes[g]));
    }

    if (b1.getNumberOfGenes() >= b2.getNumberOfGenes()) { // CHECK WHICH GENES IN b2 ARE ALSO IN b1
      Iterator i = set_genes_b2.iterator();
      while (i.hasNext()) {
        Integer index = (Integer) i.next();
        if (set_genes_b1.contains(index)) {
          commonRows.add(index);
        }
      }
    }
    else { // CHECK WHICH GENES IN b1 ARE ALSO IN b2
      Iterator i = set_genes_b1.iterator();
      while (i.hasNext()) {
        Integer index = (Integer) i.next();
        if (set_genes_b2.contains(index)) {
          commonRows.add(index);
        }
      }
    }
    return (commonRows);
  }

  //######################################################################################################################

  /**
   * b1 = (I1,J1) and b2 = (I2,J2)
   *
   * J1 intersection J2
   *
   * @param b1 Bicluster
   * @param b2 Bicluster
   * @return TreeSet
   */
  public static TreeSet<Integer> compute_CommonConditions(Bicluster b1,
      Bicluster b2) {
    TreeSet<Integer> commonConditions = new TreeSet<Integer> ();
    // SET CONDITIONS b1
    TreeSet<Integer> set_conditions_b1 = new TreeSet();
    for (int g = 0; g < b1.getNumberOfConditions(); g++) {
      set_conditions_b1.add(new Integer(b1.getColumnsIndexes()[g]));
    }
    // SET CONDITIONS b2
    TreeSet<Integer> set_conditions_b2 = new TreeSet();
    for (int g = 0; g < b2.getNumberOfConditions(); g++) {
      set_conditions_b2.add(new Integer(b2.getColumnsIndexes()[g]));
    }

    if (b1.getNumberOfGenes() >= b2.getNumberOfConditions()) { // CHECK WHICH CONDITIONS IN b2 ARE ALSO IN b1
      Iterator i = set_conditions_b2.iterator();
      while (i.hasNext()) {
        Integer index = (Integer) i.next();
        if (set_conditions_b1.contains(index)) {
          commonConditions.add(index);
        }
      }
    }
    else { // CHECK WHICH CONDITIONS IN b1 ARE ALSO IN b2
      Iterator i = set_conditions_b1.iterator();
      while (i.hasNext()) {
        Integer index = (Integer) i.next();
        if (set_conditions_b2.contains(index)) {
          commonConditions.add(index);
        }
      }
    }
    return (commonConditions);
  }

  //######################################################################################################################

  /**
   * b1 = (I1,J1) and b2 = (I2,J2)
   *
   * |I1 intersection I2|
   *
   * @param b1 Bicluster
   * @param b2 Bicluster
   * @return int
   */
  public static int compute_NumberOfCommonRows(Bicluster b1, Bicluster b2) {
    // SET GENES b1
    TreeSet<Integer> genes_b1 = new TreeSet();
    for (int g = 0; g < b1.getNumberOfGenes(); g++) {
      genes_b1.add(new Integer(b1.rowsIndexes[g]));
    }
    // SET GENES b2
    TreeSet<Integer> genes_b2 = new TreeSet();
    for (int g = 0; g < b2.getNumberOfGenes(); g++) {
      genes_b2.add(new Integer(b2.rowsIndexes[g]));
    }

    // SET OF ALL GENES
    TreeSet<Integer> all_genes = new TreeSet();
    all_genes.addAll(genes_b1);
    all_genes.addAll(genes_b2);

    return (genes_b1.size() + genes_b2.size() - all_genes.size());
  }

  //######################################################################################################################

  /**
   * b1 = (I1,J1) and b2 = (I2,J2)
   *
   * |J1 intersection J2|
   *
   * @param b1 Bicluster
   * @param b2 Bicluster
   * @return int
   */
  public static int compute_NumberOfCommonConditions(Bicluster b1, Bicluster b2) {
    // CONDITIONS b1
    TreeSet<Integer> conditions_b1 = new TreeSet();
    for (int g = 0; g < b1.getNumberOfConditions(); g++) {
      conditions_b1.add(new Integer(b1.getColumnsIndexes()[g]));
    }
    // CONDITIONS b2
    TreeSet<Integer> conditions_b2 = new TreeSet();
    for (int g = 0; g < b2.getNumberOfConditions(); g++) {
      conditions_b2.add(new Integer(b2.getColumnsIndexes()[g]));
    }
    // SET OF ALL CONDITIONS
    TreeSet<Integer> all_conditions = new TreeSet();
    all_conditions.addAll(conditions_b1);
    all_conditions.addAll(conditions_b2);
    return (conditions_b1.size() + conditions_b2.size() - all_conditions.size());
  }

  //######################################################################################################################

  /**
   * b1 = (I1,J1) and b2 = (I2,J2)
   *
   * |b1 intersect b2|
   *
   * @param b1 Bicluster
   * @param b2 Bicluster
   * @return int
   */
  public static int compute_NumberOfCommonElements(Bicluster b1, Bicluster b2) {
    int numCommonGenes = PostProcessedBiclustering.compute_NumberOfCommonRows(
        b1, b2);
    int numCommonConditions = PostProcessedBiclustering.
        compute_NumberOfCommonConditions(b1, b2);
    return (numCommonGenes * numCommonConditions);
  }

  //############################################### SIMILARITY BETWEEN BICLUSTERS ############################################

  /**
   * b1 = (I1,J1) and b2 = (I2,J2)
   *
   * JACCARD SIMILARITY COEFFICIENT
   *
   * J(b1,b2) = |b1 intersect b2| / |b1 union b2|
   * = |B_11| / (|B_10| + |B_01| + |B_11|)
   *
   * |B_11| = |b1 intersect b2|
   * = |I1 intersect I2| * |J1 intersect J2|
   *
   * |B_10| = |b1| - |b1 intersect b2|
   * = ||I1|*|J1|| - |I1 intersect I2| * |J1 intersect J2|
   *
   * |B_01| = |b2| - |b1 intersect b2|
   * = ||I2|*|J2|| - |I1 intersect I2| * |J1 intersect J2|
   *
   * @param bic Biclustering
   * @return float[][]
   * @throws Exception
   */
  public static float[][]
      compute_SimilarityMatrixBetweenBiclusters_Rows_Columns(Biclustering bic) throws
      Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    float[][] similarities = new float[bic.getNumberOfBiclusters()][bic.
        getNumberOfBiclusters()];
    for (int i = 0; i < bic.getNumberOfBiclusters(); i++) {
      for (int j = 0; j < bic.getNumberOfBiclusters(); j++) {
        int B_11 = PostProcessedBiclustering.compute_NumberOfCommonElements(bic.
            getSetOfBiclusters()[i], bic.getSetOfBiclusters()[j]); // !!!
        int b_i_size = bic.getSetOfBiclusters()[i].getNumberOfGenes() *
            bic.getSetOfBiclusters()[i].getNumberOfConditions();
        int B_10 = b_i_size - B_11;
        int b_j_size = bic.getSetOfBiclusters()[j].getNumberOfGenes() *
            bic.getSetOfBiclusters()[j].getNumberOfConditions();
        int B_01 = b_j_size - B_11;
        similarities[i][j] = ( (float) B_11) / (B_10 + B_01 + B_11);
        bic.incrementSubtaskPercentDone();
      }
//      System.out.println("SIM " + i);
    }
    bic.updatePercentDone();
    return (similarities);
  }

  //######################################################################################################################

  /**
   * b1 = (I1,J1) and b2 = (I2,J2)
   * JACCARD SIMILARITY COEFFICIENT
   * J(I1,I2) = |I1 intersection I2| / |I1 union I2|
   *
   * @param bic Biclustering
   * @return float[][]
   * @throws Exception
   */
  public static float[][] compute_SimilarityMatrixBetweenBiclusters_Rows(
      Biclustering bic) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    float[][] similarities = new float[bic.getNumberOfBiclusters()][bic.
        getNumberOfBiclusters()];
    for (int i = 0; i < bic.getBiclusters().size(); i++) {
      for (int j = 0; j < bic.getBiclusters().size(); j++) {
        int numComumnGenes = PostProcessedBiclustering.
            compute_NumberOfCommonRows(bic.getSetOfBiclusters()[i],
                                       bic.getSetOfBiclusters()[j]);
        similarities[i][j] = ( (float) numComumnGenes) /
            (bic.getSetOfBiclusters()[i].getNumberOfGenes() +
             bic.getSetOfBiclusters()[j].getNumberOfGenes() - numComumnGenes);
        bic.incrementSubtaskPercentDone();
      }
    }
    bic.updatePercentDone();
    return (similarities);
  }

  //######################################################################################################################

  /**
   * b1 = (I1,J1) and b2 = (I2,J2)
   * JACCARD SIMILARITY COEFFICIENT
   * J(J1,J2) = |J1 intersection J2| / |J1 union J2|
   *
   * @param bic Biclustering
   * @return float[][]
   * @throws Exception
   */
  public static float[][] compute_SimilarityMatrixBetweenBiclusters_Columns(
      Biclustering bic) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    float[][] similarities = new float[bic.getNumberOfBiclusters()][bic.
        getNumberOfBiclusters()];
    for (int i = 0; i < bic.getBiclusters().size(); i++) {
      for (int j = 0; j < bic.getBiclusters().size(); j++) {
        int numComumnConditions = PostProcessedBiclustering.
            compute_NumberOfCommonConditions(bic.getSetOfBiclusters()[i],
                                             bic.getSetOfBiclusters()[j]);
        similarities[i][j] = ( (float) numComumnConditions) /
            (bic.getSetOfBiclusters()[i].getNumberOfConditions() +
             bic.getSetOfBiclusters()[j].getNumberOfConditions() -
             numComumnConditions);
        bic.incrementSubtaskPercentDone();
      }
    }
    bic.updatePercentDone();
    return (similarities);
  }

  //######################################################################################################################

  /**
   *
   * @param similarities float[][]
   * @throws Exception
   */
  public static void print_SimilarityMatrixBetweenBiclusters(float[][]
      similarities) throws Exception {

    if (similarities == null) {
      throw new Exception("similarities not computed");
    }

    for (int i = 0; i < similarities.length; i++) {
      for (int j = 0; j < similarities[0].length; j++) {
        System.out.print(similarities[i][j] + "\t");
      }
      System.out.println();
    }
  }

  //######################################################################################################################

  /**
   *
   * @param similarities float[][]
   * @param filename String
   * @throws Exception
   */
  public static void print_SimilarityMatrixBetweenBiclusters_ToFile(float[][]
      similarities, String filename) throws Exception {

    if (similarities == null) {
      throw new Exception("similarities not computed");
    }

    PrintWriter fileout = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int i = 0; i < similarities.length; i++) {
      for (int j = 0; j < similarities[0].length; j++) {
        fileout.print(similarities[i][j] + "\t");
      }
      fileout.println();
    }
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   *
   //* @param setOfBiclusters Bicluster[]
   * @param similarities float[][]
   * @param threshold float
   * @return Bicluster[]
   * @throws Exception
   */
  public static ArrayList<Bicluster> filter_OVERLAPPING(Biclustering bic,
                                               float[][] similarities,
                                               float threshold) throws Exception {
    ArrayList<Bicluster> setOfBiclusters = bic.getBiclusters();

    if (setOfBiclusters.size() == 0) {
      throw new Exception("biclusters not computed");
    }

    if (similarities == null) {
      throw new Exception("similarities not computed");
    }

    //SELECT BICLUSTERS
    ArrayList<Integer> selectedBiclustersIndex = new ArrayList<Integer> (0);
    ArrayList<Bicluster> selectedBiclusters = new ArrayList<Bicluster> (0);
    //select bicluster 0
    selectedBiclustersIndex.add(new Integer(0));
    selectedBiclusters.add(setOfBiclusters.get(0));

    bic.setSubtaskValues(similarities.length);
    for (int i = 0; i < similarities.length; i++) {
      boolean filterBicluster = false;
      int index = 0;
      //compare similarity between current bicluster and the ones already selected
      while (index < selectedBiclustersIndex.size() && filterBicluster == false) {
        //compare similarity between bicluster i and the selected bicluster stored at position index
        if (similarities[i][selectedBiclustersIndex.get(index).intValue()] > threshold) {
          filterBicluster = true;
//          System.out.println("REMOVE INDEX " + i);
        }
        index++;
      }
      if (filterBicluster == false) { //select bicluster i
        //System.out.println("ADD INDEX " + i);
        selectedBiclustersIndex.add(new Integer(i));
        selectedBiclusters.add(setOfBiclusters.get(i));
      }
      bic.incrementSubtaskPercentDone();
    }
    return (selectedBiclusters);
  }


  //######################################################################################################################
  //######################################################################################################################

  /**
   *  FILTER BICLUSTERS WITH SIMILARITY (REGARDING ROWS AND COLUMNS) GREATER THAN A GIVEN THRESHOLD.
   *
   * @param bic Biclustering
   * @param threshold float
   * @throws Exception
   */
  public static void filter_OVERLAPPING_ROWS_COLUMNS(Biclustering bic,
      float threshold) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    //COMPUTE SIMILARITIES
    float[][] similarities = PostProcessedBiclustering.
        compute_SimilarityMatrixBetweenBiclusters_Rows_Columns(bic);
    //FILTER BICLUSTERS
    Biclustering newBic = (Biclustering) bic.clone();
    bic.setBiclusters(PostProcessedBiclustering.filter_OVERLAPPING(newBic, similarities, threshold));
  }

  //##################################################################################################################### static

  /**
   *  FILTER BICLUSTERS WITH SIMILARITY (REGARDING ROWS AND COLUMNS) GREATER THAN A GIVEN THRESHOLD
   *
   * @param bic Biclustering
   * @param threshold float
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_filter_OVERLAPPING_ROWS_COLUMNS(Biclustering
      bic, float threshold) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    if (threshold <= 0 || threshold >= 1) {
      throw new Exception("0 < threshold < 1");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_OVERLAPPING_ROWS_COLUMNS(newBic, threshold);
    return (newBic);
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   *  FILTER BICLUSTERS WITH SIMILARITY (REGARDING ROWS) GREATER THAN A GIVEN THRESHOLD.
   *
   * @param bic Biclustering
   * @param threshold float
   * @throws Exception
   */
  public static void filter_OVERLAPPING_ROWS(Biclustering bic, float threshold) throws
      Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    if (threshold <= 0 || threshold >= 1) {
      throw new Exception("0 < threshold < 1");
    }

    //COMPUTE SIMILARITIES
    float[][] similarities = PostProcessedBiclustering.
        compute_SimilarityMatrixBetweenBiclusters_Rows(bic);
    Biclustering newBic = (Biclustering) bic.clone();
    //FILTER BICLUSTERS
    bic.setBiclusters(PostProcessedBiclustering.filter_OVERLAPPING(newBic, similarities, threshold));
  }

  //##################################################################################################################### static

  /**
   *  FILTER BICLUSTERS WITH SIMILARITY (REGARDING ROWS) IS GREATER THAN A GIVEN THRESHOLD
   *
   * @param bic Biclustering
   * @param threshold float
   * @return Biclustering
   * @throws Exception
   */
  public static Biclustering new_filter_OVERLAPPING_ROWS(Biclustering bic,
      float threshold) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    if (threshold <= 0 || threshold >= 1) {
      throw new Exception("0 < threshold < 1");
    }

    Biclustering newBic = (Biclustering) bic.clone();
    PostProcessedBiclustering.filter_OVERLAPPING_ROWS(newBic, threshold);
    return (newBic);
  }

  //######################################################################################################################
  //######################################################################################################################

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
    public static void filter_NOT_Significant_GO_new_measure(
            Biclustering bic, GOFrontEnd go, float GO_pValue_userThreshold) throws
            Exception {
        if (bic.getNumberOfBiclusters() == 0) {
            throw new Exception("biclusters not computed");
        }

        ArrayList<Bicluster> set = new ArrayList<Bicluster> (0);
        for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
            Bicluster b = (Bicluster) bic.getBiclusters().get(k);
            GeneSetResult result = b.computeGeneOntologyTerms(go, false);
            if (b.computeSignificant_GO_new_measure(result,
                    GO_pValue_userThreshold) > 0) {
                set.add(b);
            }
            //bic.incrementSubtaskPercentDone();
        }
        bic.setOfBiclusters = set;
    }

} // END CLASS
