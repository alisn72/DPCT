package smadeira.biclustering;

import java.io.*;
import java.util.*;
import java.util.Iterator;

import javax.swing.text.*;
import javax.swing.text.html.*;

import smadeira.utils.*;

/**
 * <p>Title: Post-Processed Biclustering</p>
 *
 * <p>Description: Contains methods for post-processing groups of biclusters
 *                 (biclustering objects) defined in discretized matrices.</p>
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
public abstract class PostProcessedBiclusteringInDiscretizedMatrix
    extends PostProcessedBiclustering {

  /**
   *
   * @param bic BiclusteringInDiscretizedMatrix
   * @param significanceThreshold float
   * @param markovChainOrder int
   * @throws Exception
   */
  public static void
      filter_BiclusterPatternWithColumns_BonferroniCorrected_pValue(
      BiclusteringInDiscretizedMatrix bic, float significanceThreshold,
      int markovChainOrder) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    ArrayList<Bicluster> new_bic = new ArrayList<Bicluster> (0);

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      CCC_Bicluster b = (CCC_Bicluster) bic.getBiclusters().get(k);
      if (b.getBiclusterPatternWithColumns_BonferroniCorrected_pValue(
          markovChainOrder) < significanceThreshold) {
        new_bic.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
    bic.setBiclusters(new_bic);
  }

  //#####################################################################################################################

  /**
   *
   * @param bic BiclusteringInDiscretizedMatrix
   * @param significanceThreshold float
   * @param markovChainOrder int
   * @throws Exception
   */
  public static void filter_BiclusterPattern_BonferroniCorrected_pValue(
      BiclusteringInDiscretizedMatrix bic, float significanceThreshold,
      int markovChainOrder) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    ArrayList<Bicluster> new_bic = new ArrayList<Bicluster> (0);

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      CCC_Bicluster b = (CCC_Bicluster) bic.getBiclusters().get(k);
      if (b.getBiclusterPattern_BonferroniCorrected_pValue(markovChainOrder) <
          significanceThreshold) {
        new_bic.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
    bic.setBiclusters(new_bic);
  }

  /**
   *
   * @param bic BiclusteringInDiscretizedMatrix
   * @param significanceThreshold float
   * @param markovChainOrder int
   * @return BiclusteringInDiscretizedMatrix
   * @throws Exception
   */
  public static BiclusteringInDiscretizedMatrix
      new_filter_BiclusterPatternWithColumns_BonferroniCorrected_pValue(
      BiclusteringInDiscretizedMatrix bic, float significanceThreshold,
      int markovChainOrder) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    BiclusteringInDiscretizedMatrix new_bic = (BiclusteringInDiscretizedMatrix)
        bic.clone();
    PostProcessedBiclusteringInDiscretizedMatrix.
        filter_BiclusterPatternWithColumns_BonferroniCorrected_pValue(new_bic,
        significanceThreshold, markovChainOrder);
    return new_bic;
  }

  //#####################################################################################################################

  /**
   *
   * @param bic BiclusteringInDiscretizedMatrix
   * @param significanceThreshold float
   * @param markovChainOrder int
   * @return BiclusteringInDiscretizedMatrix
   * @throws Exception
   */
  public static BiclusteringInDiscretizedMatrix
      new_filter_BiclusterPattern_BonferroniCorrected_pValue(
      BiclusteringInDiscretizedMatrix bic, float significanceThreshold,
      int markovChainOrder) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    BiclusteringInDiscretizedMatrix new_bic = (BiclusteringInDiscretizedMatrix)
        bic.clone();
    PostProcessedBiclusteringInDiscretizedMatrix.
        filter_BiclusterPattern_BonferroniCorrected_pValue(new_bic,
        significanceThreshold, markovChainOrder);
    return new_bic;
  }

  //#####################################################################################################################
  //################################### FILTER BICLUSTERS ACCORDING TO GIVEN CRITERIA ###################################
  //#####################################################################################################################

  /**
   * USED IN TCBB SUBMISSION APRIL 2007
   *
   * @param bic BiclusteringInDiscretizedMatrix
   * @param significanceThreshold float
   * @param markovChainOrder int
   * @throws Exception
   */
  public static void filter_CCC_Biclusters_TCBB(BiclusteringInDiscretizedMatrix
                                                bic,
                                                float significanceThreshold,
                                                int markovChainOrder) throws
      Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    ArrayList<Bicluster> new_bic = new ArrayList<Bicluster> (0);

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      CCC_Bicluster b = (CCC_Bicluster) bic.getBiclusters().get(k);
      if (b.getBiclusterPatternWithColumns_BonferroniCorrected_pValue(
          markovChainOrder) < significanceThreshold) {
        new_bic.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
    bic.setBiclusters(new_bic);
  }

  //#####################################################################################################################

  /**
   * USED IN TCBB SUBMISSION APRIL 2007.
   *
   * @param bic BiclusteringInDiscretizedMatrix
   * @param significanceThreshold float
   * @param markovChainOrder int
   * @return BiclusteringInDiscretizedMatrix
   * @throws Exception
   */
  public static BiclusteringInDiscretizedMatrix new_filter_CCC_Bicluster_TCBB(
      BiclusteringInDiscretizedMatrix bic,
      float significanceThreshold, int markovChainOrder) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    BiclusteringInDiscretizedMatrix new_bic = (BiclusteringInDiscretizedMatrix)
        bic.clone();
    PostProcessedBiclusteringInDiscretizedMatrix.filter_CCC_Biclusters_TCBB(
        new_bic, significanceThreshold, markovChainOrder);
    return (new_bic);
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * Filter CONSTANT BICLUSTERS (THE EXPRESSION PATTERN HAS ONLY ONE SYMBOL).
   *
   * @param bic BiclusteringInDiscretizedMatrix
   * @throws Exception
   */
  public static void filter_CONSTANT_EXPRESSION_PATTERN(
      BiclusteringInDiscretizedMatrix bic) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    ArrayList<Bicluster> new_bic = new ArrayList<Bicluster> (0);

    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      CCC_Bicluster b = (CCC_Bicluster) bic.getBiclusters().get(k);
      char[] pattern = b.getBiclusterExpressionPattern();
      char firstCharacter = pattern[0];
      boolean constant = true;
      for (int i = 1; i < pattern.length; i++) {
        if (pattern[i] != firstCharacter) {
          i = pattern.length; // stop
          constant = false;
        }
      }
      if (!constant) {
        new_bic.add(b);
      }
      bic.incrementSubtaskPercentDone();
    }
    bic.setBiclusters(new_bic);
  }

  //#####################################################################################################################

  /**
   * Filter CONSTANT BICLUSTERS (THE EXPRESSION PATTERN HAS ONLY ONE SYMBOL).
   *
   * @param bic BiclusteringInDiscretizedMatrix
   * @return BiclusteringInDiscretizedMatrix
   * @throws Exception
   */
  public static BiclusteringInDiscretizedMatrix
      new_filter_CONSTANT_EXPRESSION_PATTERN(BiclusteringInDiscretizedMatrix
                                             bic) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    BiclusteringInDiscretizedMatrix new_bic = (BiclusteringInDiscretizedMatrix)
        bic.clone();
    PostProcessedBiclusteringInDiscretizedMatrix.
        filter_CONSTANT_EXPRESSION_PATTERN(new_bic);
    return (new_bic);
  }

  /**
   * USED IN TCBB SUBMISSION APRIL 2007.
   *
   * @param bic BiclusteringInDiscretizedMatrix
   * @param filename String
   * @param markovChainOrder int
   * @throws Exception
   */
  public static void print_CCC_Biclusters_TCBB_ToFile(
      BiclusteringInDiscretizedMatrix bic, String filename,
      int markovChainOrder) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new
        FileWriter(filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      CCC_Bicluster b = (CCC_Bicluster) bic.getBiclusters().get(k);
      fileOut.println("#BICLUSTER_" + b.getID() + "\t" +
                      "CONDITIONS= " + b.getNumberOfConditions() + "\t" +
                      "FIRST-LAST= " + b.getColumnsIndexes()[0] + "-" +
                      b.columnsIndexes[b.getNumberOfConditions() - 1] + "\t" +
                      "GENES= " + b.getNumberOfGenes() + "\t" +
                      "P-VALUE= " +
                      b.getBiclusterPatternWithColumns_pValue(markovChainOrder));
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
    fileOut.close();
  }

  //#####################################################################################################################

  /**
   * USED IN TCBB SUBMISSION APRIL 2007.
   *
   * @param bic BiclusteringInDiscretizedMatrix
   * @param filename String
   * @param markovChainOrder int
   * @throws Exception
   */
  public static void print_CCC_Biclusters_MSR_TCBB_ToFile(
      BiclusteringInDiscretizedMatrix bic, String filename,
      int markovChainOrder) throws Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);
    for (int k = 0; k < bic.getNumberOfBiclusters(); k++) {
      CCC_Bicluster b = (CCC_Bicluster) bic.getBiclusters().get(k);
      fileOut.println("#BICLUSTER_" + b.getID() + "\t" +
                      "CONDITIONS= " + b.getNumberOfConditions() + "\t" +
                      "FIRST-LAST= " + b.columnsIndexes[0] + "-" +
                      b.columnsIndexes[b.getNumberOfConditions() - 1] +
                      "\t" +
                      "GENES= " + b.getNumberOfGenes() + "\t" +
                      "MSR= " + b.computeMSR() + "\t" +
                      "P-VALUE= " +
                      b.getBiclusterPatternWithColumns_pValue(markovChainOrder));
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
    fileOut.close();
  }

  //#####################################################################################################################
  //################################################# SORT BICLUSTERS####################################################
  //####################################################################################################################

  /**
   *
   * @param bic BiclusteringInDiscretizedMatrix
   * @param markovChainOrder int
   * @throws Exception
   */
  public static void sort_BiclusterPattern_pValue_ASC(
      BiclusteringInDiscretizedMatrix bic, int markovChainOrder) throws
      Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(),
                     new
                     Comparator_HASH_biclusterPattern_pValue_ASC(bic, markovChainOrder));
  }

  //####################################################################################################################

  /**
   *
   * @param bic BiclusteringInDiscretizedMatrix
   * @param markovChainOrder int
   * @throws Exception
   */
  public static void sort_BiclusterPatternWithColumns_pValue_ASC(
      BiclusteringInDiscretizedMatrix bic,
      int markovChainOrder) throws Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    Collections.sort(bic.getBiclusters(),
                     new
                     Comparator_HASH_biclusterPatternWithColumns_pValue_ASC(bic,
        markovChainOrder));
  }

  //####################################################################################################################
  //####################################################################################################################

  /**
   *
   * @param bic BiclusteringInDiscretizedMatrix
   * @param markovChainOrder int
   * @return BiclusteringInDiscretizedMatrix
   * @throws Exception
   */
  public static BiclusteringInDiscretizedMatrix
      new_sort_BiclusterPattern_pValue_ASC(
          BiclusteringInDiscretizedMatrix bic, int markovChainOrder) throws
      Exception {
    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }

    BiclusteringInDiscretizedMatrix new_bic = (BiclusteringInDiscretizedMatrix)
        bic.clone();
    Collections.sort(new_bic.getBiclusters(),
                     new Comparator_HASH_biclusterPattern_pValue_ASC(new_bic,
        markovChainOrder));
    return (new_bic);
  }

  //####################################################################################################################
  //####################################################################################################################

  /**
   *
   * @param bic BiclusteringInDiscretizedMatrix
   * @param markovChainOrder int
   * @return BiclusteringInDiscretizedMatrix
   * @throws Exception
   */
  public static BiclusteringInDiscretizedMatrix
      new_sort_BiclusterPatternWithColumns_pValue_ASC(
          BiclusteringInDiscretizedMatrix bic, int markovChainOrder) throws
      Exception {

    if (bic.getNumberOfBiclusters() == 0) {
      throw new Exception("biclusters not computed");
    }


    BiclusteringInDiscretizedMatrix new_bic = (BiclusteringInDiscretizedMatrix)
        bic.clone();
    Collections.sort(new_bic.getBiclusters(),
                     new Comparator_HASH_biclusterPatternWithColumns_pValue_ASC(
        new_bic, markovChainOrder));
    new_bic.updatePercentDone();
    return (new_bic);
  }

  //######################################################################################################################
  //###################### FIND e-NEIGHBOURS OF 0-CCC-BICLUSTERS IN THE e-CCC-BICLUSTERING RESULTS #######################
  //######################################################################################################################

  /**
   *
   * @param perfectBiclusterPattern char[]
   * @param perfectBiclusterAlphabet char[]
   * @param firstColumn int
   * @param lastColumn int
   * @param columnsInExpressionMatrix int
   * @param numErrors int
   * @return ArrayList<HashKey_Pattern>
   */
  private static ArrayList<HashKey_Pattern>
      compute_NeighboursOfPatternOfPerfectBicluster(char[]
      perfectBiclusterPattern,
      char[] perfectBiclusterAlphabet,
      int firstColumn, int lastColumn,
      int columnsInExpressionMatrix,
      int numErrors) {

    ArrayList<HashKey_Pattern> neighbours = new ArrayList<HashKey_Pattern> (0);
    String perfectBiclusterPatternAsString = new String(perfectBiclusterPattern);
    // #Errors = 0
    neighbours.add(0, new HashKey_Pattern(perfectBiclusterPatternAsString,
                                          firstColumn,
                                          lastColumn));
    ArrayList<HashKey_Pattern>
        patternsWithErrorsInsidePattern = new ArrayList<HashKey_Pattern> (0);
    ArrayList<HashKey_Pattern> patternsWithErrorsBefore = new ArrayList(0);
    ArrayList<HashKey_Pattern> patternsWithErrorsAfter = new ArrayList(0);
    ArrayList<HashKey_Pattern> patternsWithErrorsBeforeAndAfter = new ArrayList();

    if (numErrors > 0) {
      // NEIGHBORS WHERE ERRORS TAKE PLACE BETWEEN FIRST AND LAST COLUMN OF THE PATTERN
      // places where the errors can take place
      String[] elements = new String[perfectBiclusterPattern.length];
      for (int i = 0; i < perfectBiclusterPattern.length; i++) {
        elements[i] = new String(new Integer(i).toString());
      }

      for (int e = 1; e <= numErrors; e++) {
        // generate all combinations of the indexes 0..s.length-1, taken numErrors at a time
        CombinationGenerator x = new CombinationGenerator(elements.length, e); // C_elements.length_e

        while (x.hasMore()) {
          int[] indexes = x.getNext(); // places where the errors will take place
          int numberOfPatterns = (int) Math.pow(perfectBiclusterAlphabet.length -
                                                1, e);
          for (int p = 0; p < numberOfPatterns; p++) {
            patternsWithErrorsInsidePattern.add(p, new HashKey_Pattern(
                perfectBiclusterPatternAsString,
                firstColumn, lastColumn));
          }
          for (int i = 0; i < indexes.length; i++) {
            int numberOfSymbolRepetitions = (int) Math.pow(
                perfectBiclusterAlphabet.length - 1, e - i - 1);
            int t = 0;
            while (t < numberOfPatterns) {
              for (int s = 0; s < perfectBiclusterAlphabet.length; s++) {
                if (perfectBiclusterPattern[indexes[i]] !=
                    perfectBiclusterAlphabet[s]) {
                  for (int r = 0; r < numberOfSymbolRepetitions; r++) {
                    char[] changedPattern = patternsWithErrorsInsidePattern.get(
                        t).getPattern().toCharArray();
                    changedPattern[indexes[i]] = perfectBiclusterAlphabet[s];
                    patternsWithErrorsInsidePattern.get(t).setPattern(new
                        String(changedPattern));
                    t++;
                  }
                }
              }
            }
          }
          neighbours.addAll(patternsWithErrorsInsidePattern);
        }
      }

      //        System.out.println("ALL ERRORS IN THE PATTERN: \n" + neighbours.toString());
      // NEIGHBORS WHERE ALL THE ERRORS TAKE PLACE BEFORE THE PATTERN
      // NEIGHBORS WHERE ALL THE ERRORS TAKE PLACE AFTER THE PATTERN
      // NEIGHBORS WHERE ALL THE ERRORS TAKE PLACE BEFORE AND AFTER THE PATTERN
      ArrayList<String> [] tempPatterns = new ArrayList[numErrors];
      for (int i = 1; i <= numErrors; i++) {
        int numberOfPatterns = (int) Math.pow(perfectBiclusterAlphabet.length,
                                              i);
        //System.out.println("#patterns = " + numberOfPatterns);
        tempPatterns[i - 1] = new ArrayList(numberOfPatterns);
        for (int p = 0; p < numberOfPatterns; p++) {
          //System.out.println("INICIALIZING PATTERN " + p);
          tempPatterns[i - 1].add(p, new String(""));
        }
        for (int j = 1; j <= i; j++) {
          int nr = (int) Math.pow(perfectBiclusterAlphabet.length, i - j);
          int p = 0;
          while (p < numberOfPatterns) {
            for (int s = 0; s < perfectBiclusterAlphabet.length; s++) {
              for (int r = 1; r <= nr; r++) {
                String pattern = tempPatterns[i - 1].get(p);
                char[] symbol = new char[1];
                symbol[0] = perfectBiclusterAlphabet[s];
                String newPattern = pattern.concat(new String(symbol));
                tempPatterns[i - 1].set(p, newPattern);
                p++;
              }
            }
          }
        }
        //          System.out.println(tempPatterns[i - 1].toString());
      }
      // PATTERNS WITH ALL ERRORS BEFORE THE PATTERN (IF POSSIBLE)
      if (firstColumn - numErrors > 0) {
        for (int p = 0; p < tempPatterns[numErrors - 1].size(); p++) {
          patternsWithErrorsBefore.add(p, new HashKey_Pattern(
              tempPatterns[numErrors -
              1].get(p).concat(perfectBiclusterPatternAsString),
              firstColumn - numErrors, lastColumn));
        }
      }
      //        System.out.println("ALL ERRORS BEFORE: \n" + patternsWithErrorsBefore.toString());
      neighbours.addAll(patternsWithErrorsBefore);

      // PATTERNS WITH ALL ERRORS AFTER THE PATTERN (IF POSSIBLE)
      if (lastColumn + numErrors <= columnsInExpressionMatrix) {
        for (int p = 0; p < tempPatterns[numErrors - 1].size(); p++) {
          patternsWithErrorsAfter.add(p, new HashKey_Pattern(
              perfectBiclusterPatternAsString.concat(tempPatterns[numErrors -
              1].get(p)),
              firstColumn, lastColumn + numErrors));
        }
      }
      //        System.out.println("ALL ERRORS AFTER: \n" + patternsWithErrorsAfter.toString());
      neighbours.addAll(patternsWithErrorsAfter);

      // COMBINE ERRORS BEFORE AND AFTER THE PATTERN
      int p = 0;
      for (int eBefore = 1; eBefore < numErrors; eBefore++) {
        //          System.out.println("eBefore = " + eBefore);
        if (firstColumn - eBefore > 0) { //columnsAvailableBefore
          for (int eAfter = numErrors - 1; eAfter > 0; eAfter--) {
            if (eBefore + eAfter <= numErrors) { // columnsAvailableAfter
              //                System.out.println("\t eAfter = " + eAfter);
              for (int pb = 0; pb < tempPatterns[eBefore - 1].size(); pb++) {
                for (int pa = 0; pa < tempPatterns[eAfter - 1].size(); pa++) {
                  patternsWithErrorsBeforeAndAfter.add(p, new HashKey_Pattern(
                      tempPatterns[eBefore -
                      1].get(pb).concat(perfectBiclusterPatternAsString).concat(
                          tempPatterns[eAfter - 1].get(pa)),
                      firstColumn - eBefore, lastColumn + eAfter));
                  p++;
                }
              }
            }
          }
        }
      }
      neighbours.addAll(patternsWithErrorsBeforeAndAfter);
    }
    //System.out.println("NEIGHBOURS WITH ERRORS BEFORE AND AFTER PATTERN: \n" + neighbours.toString());
    return (neighbours);
  }

  //######################################################################################################################


  /**
   * Computes the e-neighbours of each 0-CCC-Bicluster and checks if they were found ad e-CCC-Biclusters by the
   * e-CCC-Biclustering algorithm. The goal is to check if the errors improved the metrics of original (perfect)
   * CCC-Biclusters.
   *
   * @param biclustersWithoutErrors E_CCC_Bicluster[]
   * @param biclustersWithErrors E_CCC_Bicluster[]
   * @param columnsInExpressionMatrix int
   * @return TreeSet[]
   */
  private static TreeSet<E_CCC_Bicluster>[]
      compute_ComparisonBetweenResultsWithAndWithoutErrors(
          CCC_Bicluster[] biclustersWithoutErrors,
          E_CCC_Bicluster[] biclustersWithErrors,
          int columnsInExpressionMatrix) {

    // COMPUTE HASHTABLE WITH BICLUSTERS WITH ERRORS

    int numErrors = biclustersWithErrors[0].getMaxNumberOfErrors();

    //HashMap(int�initialCapacity, float�loadFactor) �
    HashMap hashtreeBiclustersWithErrors = new LinkedHashMap(
        biclustersWithErrors.length, (float) 0.75);

    for (int i = 0; i < biclustersWithErrors.length; i++) {

      E_CCC_Bicluster bicluster = biclustersWithErrors[i];
      int firstColumn = bicluster.columnsIndexes[0];
      int lastColumn = firstColumn + bicluster.getNumberOfConditions() - 1;
      String pattern = new String(bicluster.getBiclusterExpressionPattern());

      HashKey_Pattern key = new HashKey_Pattern(pattern, firstColumn,
                                                lastColumn);
      E_CCC_Bicluster key_object = bicluster;

      if (hashtreeBiclustersWithErrors.containsKey(key) == true) {
        E_CCC_Bicluster object = (E_CCC_Bicluster)
            hashtreeBiclustersWithErrors.get(key); // get object that has this key
        if (object.equals(key_object)) {
          // THERE IS A PATTERN IN THE HASHTREE THAT REPRESENTS THE SAME BICLUSTER
        }
        else {
          // THERE IS NOT A PATTERN IN THE HASHTREE THAT REPRESENTS THE SAME BICLUSTER
          hashtreeBiclustersWithErrors.put(key, key_object);
        }
      }
      else {
        // THERE IS NOT A PATTERN IN THE HASHTREE THAT REPRESENTS THE SAME BICLUSTER
        hashtreeBiclustersWithErrors.put(key, key_object);
      }
    }

    // COMPUTE e-NEIGHBOURS OF EACH BICLUSTER WITHOUT ERRORS AND TRY TO FIND HIM IN THE SUFFIX TREE
    // FOR EACH BICLUSTER WITHOUR ERRORS STORE THE INFORMATION ABOUT EACH BICLUSTER WITH ERRORS GENERATED FROM HIM

    TreeSet<E_CCC_Bicluster> []
        biclustersWithErrorsCorrespondingToNeighboursOfBiclustersWithoutErrors =
        new TreeSet[biclustersWithoutErrors.length];

    for (int i = 0; i < biclustersWithoutErrors.length; i++) { // FOR EACH O-CCC-BICLUSTER

      // INICILIAZE ARRAYLIST[i]
      biclustersWithErrorsCorrespondingToNeighboursOfBiclustersWithoutErrors[i] = new
          TreeSet<E_CCC_Bicluster> ();

      CCC_Bicluster b = biclustersWithoutErrors[i];
      ArrayList<HashKey_Pattern> neighbours =
          PostProcessedBiclusteringInDiscretizedMatrix.
          compute_NeighboursOfPatternOfPerfectBicluster(b.
          getBiclusterExpressionPattern(),
          b.getAlphabet(),
          b.columnsIndexes[0],
          b.columnsIndexes[0] +
          b.getNumberOfConditions() - 1,
          columnsInExpressionMatrix,
          numErrors);

      //        System.out.println("#NEIGHBOURS = " + neighbours.size());
      //        System.out.println("NEIGHBOURS = " + neighbours);
      for (int j = 0; j < neighbours.size(); j++) { // FOR EACH e-NEIGHBOUR
        HashKey_Pattern neighbour = neighbours.get(j);
        //        System.out.println("neighbour = " + neighbour.toString());
        if (hashtreeBiclustersWithErrors.containsKey(neighbour)) {
          E_CCC_Bicluster key_object =
              (E_CCC_Bicluster) hashtreeBiclustersWithErrors.get(neighbour); // get object that has this key
          //          System.out.println("key_object = " + key_object.toString());
          int key_object_firstColumn = key_object.columnsIndexes[0];
          int key_object_lastColumn = key_object_firstColumn +
              key_object.getNumberOfConditions() - 1;
          String key_object_pattern = new String(key_object.
                                                 getBiclusterExpressionPattern());
          if (neighbour.getFirstColumn() == key_object_firstColumn &&
              neighbour.getLastColumn() == key_object_lastColumn &&
              neighbour.getPattern().equals(key_object_pattern)) {
            // THERE IS A BICLUSTER IN THE HASHTREE THAT REPRESENTS THE e-NEIGHBOUR
            //            System.out.println("THERE IS A BICLUSTER IN THE HASHTREE THAT REPRESENTS THE e-NEIGHBOUR \n");

            biclustersWithErrorsCorrespondingToNeighboursOfBiclustersWithoutErrors[
                i].add(key_object); // ADDS IF IT'S NOT ALREADY THERE
          }
          else {
            // THERE IS NOT A BICLUSTER IN THE HASHTREE THAT REPRESENTS THE SAME e-NEIGHBOUR
            //            System.out.println("THERE IS NOT A BICLUSTER IN THE HASHTREE THAT REPRESENTS THE SAME e-NEIGHBOUR \n");
          }
        }
      } // END FOR EACH NEIGHBOUR
    } // END FOR EACH 0-CCC-BICLUSTER

    return (
        biclustersWithErrorsCorrespondingToNeighboursOfBiclustersWithoutErrors);
  }

  //######################################################################################################################

  /**
   *
   * @param biclustersWithoutErrors CCC_Bicluster[]
   * @param biclustersWithErrors E_CCC_Bicluster[]
   * @param columnsInExpressionMatrix int
   */
  public static void compare_BiclusteringResultsWithAndWithoutErrors(
      CCC_Bicluster[] biclustersWithoutErrors,
      E_CCC_Bicluster[] biclustersWithErrors, int columnsInExpressionMatrix) {

    System.out.println(
        "### COMPUTING AND PRINTING COMPARISON BETWEEN 0-CCC-BICLUSTERS AND " +
        biclustersWithErrors[0].getMaxNumberOfErrors() + "-CCC-BICLUSTERS ###");

    TreeSet<E_CCC_Bicluster> [] results =
        PostProcessedBiclusteringInDiscretizedMatrix.
        compute_ComparisonBetweenResultsWithAndWithoutErrors(
            biclustersWithoutErrors,
            biclustersWithErrors, columnsInExpressionMatrix);

    for (int i = 0; i < biclustersWithoutErrors.length; i++) {
      System.out.println("######################################################################## 0-CCC-BICLUSTER ######################################################################## \n");
      System.out.println(biclustersWithoutErrors[i].toString());
      System.out.println(
          "######################################################################## " +
          biclustersWithErrors[0].getMaxNumberOfErrors() + "-CCC-BICLUSTERS #######################################################################");
      System.out.println();

      Iterator<E_CCC_Bicluster> it = results[i].iterator();
      while (it.hasNext()) {
        E_CCC_Bicluster b = it.next();
        System.out.println(b.toString());
      }
      System.out.println("#################################################################################################################################################################################### \n");
    }
    System.out.println("### DONE! ###");
  }

  //######################################################################################################################

  /**
   *
   * @param biclustersWithoutErrors E_CCC_Bicluster[]
   * @param biclustersWithErrors E_CCC_Bicluster[]
   * @param columnsInExpressionMatrix int
   * @param filename String
   * @throws IOException
   */
  public static void compare_BiclusteringResultsWithAndWithoutErrorsToFile(
      E_CCC_Bicluster[] biclustersWithoutErrors,
      E_CCC_Bicluster[] biclustersWithErrors,
      int columnsInExpressionMatrix, String filename) throws IOException {

    System.out.println(
        "### COMPUTING AND PRINTING TO FILE COMPARISON BETWEEN 0-CCC-BICLUSTERS AND " +
        biclustersWithErrors[0].getMaxNumberOfErrors() + "-CCC-BICLUSTERS ###");

    PrintWriter fileout = new PrintWriter(new BufferedWriter(new FileWriter(
        filename)), true);

    TreeSet<E_CCC_Bicluster> [] results =
        PostProcessedBiclusteringInDiscretizedMatrix.
        compute_ComparisonBetweenResultsWithAndWithoutErrors(
            biclustersWithoutErrors,
            biclustersWithErrors, columnsInExpressionMatrix);

    for (int i = 0; i < biclustersWithoutErrors.length; i++) {
      fileout.println("######################################################################## 0-CCC-BICLUSTER ######################################################################## \n");
      fileout.println(biclustersWithoutErrors[i].toString());
      fileout.println(
          "######################################################################## " +
          biclustersWithErrors[0].getMaxNumberOfErrors() + "-CCC-BICLUSTERS #######################################################################");
      fileout.println();

      Iterator<E_CCC_Bicluster> it = results[i].iterator();
      while (it.hasNext()) {
        E_CCC_Bicluster b = it.next();
        fileout.println(b.toString());
      }
    }
    fileout.println("########################################################################################## \n");
    fileout.close();

    System.out.println("### DONE! ###");
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

} // END CLASS
