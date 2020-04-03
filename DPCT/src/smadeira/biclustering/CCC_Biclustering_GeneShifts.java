package smadeira.biclustering;

import java.io.*;
import java.util.*;
import java.util.Iterator;

import javax.swing.text.*;
import javax.swing.text.html.*;

import smadeira.suffixtrees.*;
import smadeira.utils.*;

/**
 * <p>Title: CCC Biclustering with Gene Shifts</p>
 *
 * <p>Description: CCC-Biclustering algorithm extension for identifying
 *                 CCC Biclusters with gene shifts (scaled patterns).</p>
 *
 * <p>Copyright:   Copyright (C) 2007  Joana P. Gonçalves, Sara C. Madeira
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
public class CCC_Biclustering_GeneShifts
    extends CCC_Biclustering implements Cloneable, Serializable {
  public static final long serialVersionUID = -1312861820995633490L;

  /**
   * Number of symbols used in expression pattern shifting.
   */
  protected int numberOfShifts;

  //######################################################################################################################
  //######################################################################################################################

  /**
   * this.numberOfShifts = m.getAlphabet().length
   *
   * @param m DiscretizedExpressionMatrix
   * @throws Exception
   */
  public CCC_Biclustering_GeneShifts(DiscretizedExpressionMatrix m) throws
      Exception {
    this(m, m.getAlphabet().length);
  }

  /**
   *
   * @param m DiscretizedExpressionMatrix
   * @param numberOfShifts int
   * @throws Exception
   */
  public CCC_Biclustering_GeneShifts(DiscretizedExpressionMatrix m,
                                     int numberOfShifts) throws Exception {
    super(m);
    this.numberOfShifts = numberOfShifts;
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @return int
   */
  public int getNumberOfShifts() {
    return this.numberOfShifts;
  }

  //###############################################################################################################################
  //###############################################################################################################################
  /**
   *
   * @return char[]
   */
  protected char[] getAlphabetAfterGeneShifts() {
    char[] alphabetAfterGeneShifts = new char[this.matrix.getAlphabet().length +
        2 * this.numberOfShifts];
    //alphabet to shift down
    for (int i = 0; i < this.numberOfShifts; i++) {
      alphabetAfterGeneShifts[i] = (char) (this.matrix.getAlphabet()[0] -
                                           (this.numberOfShifts - i));
    }
    //alphabet
    for (int i = 0; i < this.matrix.getAlphabet().length; i++) {
      alphabetAfterGeneShifts[this.numberOfShifts +
          i] = this.matrix.getAlphabet()[i];
    }
    //alphabet to shift down
    for (int i = 1; i <= this.numberOfShifts; i++) {
      alphabetAfterGeneShifts[this.numberOfShifts +
          this.matrix.getAlphabet().length + i - 1] =
          (char) (this.matrix.getAlphabet()[this.matrix.getAlphabet().length -
                  1] + i);
    }
    return alphabetAfterGeneShifts;
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @return char[][]
   */
  public char[][] getDiscretizedMatrixAfterGeneShifts() {
    int numberOfRows = this.matrix.getSymbolicExpressionMatrix().length;
    int numberOfColumns = this.matrix.getNumberOfConditions() + 1; // plus 1 to terminator
    int finalNumberOfRows = numberOfRows + numberOfRows * 2 * this.numberOfShifts;
    char[][] discretizedMatrixWithGeneShifts = new char[finalNumberOfRows][numberOfColumns - 1];
    char[] alphabetAfterGeneShifts = this.getAlphabetAfterGeneShifts();
    // genesShifted.get(0) --> ArrayList<char[]> with first gene shifted this.numberOfShifts times
    // ...
    // genesShifted.get(numberOfRows) --> ArrayList<char[]> with last gene shifted this.numberOfShifts times
    ArrayList<ArrayList<char[]>>
        genesShifted = new ArrayList<ArrayList<char[]>> (numberOfRows);

    for (int i = 0; i < numberOfRows; i++) {
      char[] pattern = this.matrix.getSymbolicExpressionMatrix()[i];
      discretizedMatrixWithGeneShifts[i] = pattern;
      if(this.matrix.hasMissingValues()){ // !!!
        genesShifted.add(i, Shift_Pattern.shiftPattern(pattern,
            alphabetAfterGeneShifts, this.numberOfShifts, this.matrix.getMissingValue()));
      }
      else{
        genesShifted.add(i, Shift_Pattern.shiftPattern(pattern,alphabetAfterGeneShifts, this.numberOfShifts));
      }
    }

    //INSERT SHIFTED PATTERNS
    for (int g = 0; g < numberOfRows; g++) {
      ArrayList<char[]> shifts_gene_g = genesShifted.get(g);
      for (int i = 1; i <= 2 * this.numberOfShifts; i++) { // shifts DOWN + UP
        for (int j = 0; j < numberOfColumns - 1; j++) {
          discretizedMatrixWithGeneShifts[numberOfRows * i + g][j] = shifts_gene_g.get(i - 1)[j];
        }
      }
    }
    return discretizedMatrixWithGeneShifts;
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * Constructs a matrix with int values to be used in biclustering algorithms using suffix trees.
   *
   * @return int[][]
   * @throws Exception
   */
  protected int[][] getMatrixToConstructSuffixTrees() {
    int numberOfRows = this.matrix.getSymbolicExpressionMatrix().length;
    int numberOfColumns = this.matrix.getNumberOfConditions() + 1; // plus 1 to terminator
    int finalNumberOfRows = numberOfRows +numberOfRows * 2 * this.numberOfShifts;
    int[][] matrixToConstructSuffixTrees = new int[finalNumberOfRows][numberOfColumns];
    char[] alphabetAfterGeneShifts = this.getAlphabetAfterGeneShifts();
    // genesShifted.get(0) --> ArrayList<char[]> with first gene shifted this.numberOfShifts times
    // ...
    // genesShifted.get(numberOfRows) --> ArrayList<char[]> with last gene shifted this.numberOfShifts times
    ArrayList<ArrayList<char[]>>
        genesShifted = new ArrayList<ArrayList<char[]>> (numberOfRows);

    for (int i = 0; i < numberOfRows; i++) {
      char[] pattern = this.matrix.getSymbolicExpressionMatrix()[i];
      genesShifted.add(i,
                       Shift_Pattern.shiftPattern(pattern,
                                                  alphabetAfterGeneShifts,
                                                  this.numberOfShifts));
    }

    //INSERT ORIGINAL PATTERNS
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
      element = - (finalNumberOfRows - i);
      matrixToConstructSuffixTrees[i][numberOfColumns - 1] = element;
    }

    //INSERT SHIFTED PATTERNS
    for (int g = 0; g < numberOfRows; g++) {
      ArrayList<char[]> shifts_gene_g = genesShifted.get(g);
      for (int i = 1; i <= 2 * this.numberOfShifts; i++) { // shifts DOWN + UP
        String c = new String();
        String p = new String();
        String s = new String();
        int element;
        for (int j = 0; j < numberOfColumns - 1; j++) {
          c = Integer.toString( (int) shifts_gene_g.get(i - 1)[j]);
          p = Integer.toString(j + 1);
          s = new String(c + p);
          element = Integer.parseInt(s);
          matrixToConstructSuffixTrees[numberOfRows * i + g][j] = element;
        }
        element = - (finalNumberOfRows - (numberOfRows * i + g)); // terminator
        matrixToConstructSuffixTrees[numberOfRows * i + g][numberOfColumns-1] = element;
      }
    }
    return matrixToConstructSuffixTrees;
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   *
   * @param rowsQuorum int
   * @param columnsQuorum int
   * @throws Exception
   */
  public void computeBiclusters(int rowsQuorum, int columnsQuorum) throws Exception {

    if (this.matrix.hasMissingValues()) {
      throw new Exception("This algorithm does not support missing values: \n please remove the genes with missing values or fill them before applying the algorithm.");
    }

    if (rowsQuorum < 1 || columnsQuorum < 1 || rowsQuorum > this.matrix.getNumberOfGenes() || columnsQuorum > this.matrix.getNumberOfConditions()) {
      throw new Exception("rowsQuorum in [1, numberOfGenes] and columndQuorum in [1, numberOfConditions]");
    }

    this.setTaskPercentages(new TaskPercentages(this));
    // construct ccc-biclusters suffix-tree - LINEAR
    SuffixTreeNoErrors cccBiclustersSuffixTree = this.constructSuffixTree();

    // create set of objects of class CCC_Bicluster and initialize them with the information of each maximal
    // ccc-bicluster - NOT LINEAR
    // CREATE HASHMAP TO STORE BICLUSTERS WITHOUT REPETITIONS

    int worstCaseNumberOfBiclusters = this.getNumberOfShifts() *
        this.matrix.getAlphabet().length * this.matrix.getNumberOfGenes() *
        this.matrix.getNumberOfConditions();
    HashMap<Integer, CCC_Bicluster> biclustersWithoutRepetitions = new
        LinkedHashMap<Integer, CCC_Bicluster> (worstCaseNumberOfBiclusters,
                                               (float) 0.75);

    // GET BICLUSTERS FROM SUFFIX TREE
    // the root is not a bicluster
    this.setOfBiclusters = new ArrayList<Bicluster> (0);
    NodeInterface firstChild = cccBiclustersSuffixTree.getRoot().getFirstChild();
/*    this.createMaximalBiclusters(cccBiclustersSuffixTree, firstChild,
                                 biclustersWithoutRepetitions, rowsQuorum, columnsQuorum);
 */
this.countAndCreateMaximalBiclusters(cccBiclustersSuffixTree, firstChild,
                                 biclustersWithoutRepetitions, rowsQuorum, columnsQuorum);
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   *
   * @return SuffixTreeNoErrors
   */
  protected SuffixTreeNoErrors constructSuffixTree() {
    int numColumns = this.matrix.getNumberOfConditions() + 1; // one for the string terminator
    // GENE-SHIFTS
    SuffixTreeNoErrors_Extended cccBiclustersSuffixTree = new
        SuffixTreeNoErrors_Extended(numColumns, this.matrix.getNumberOfGenes());
    // COMPUTE MATRIX TO CONSTRUCT SUFFIX TREE

//    try {
//      PrintWriter pw = new PrintWriter("times_0.txt");
//      long time = System.currentTimeMillis();
      int[][] matrixToConstructSuffixTrees = this.
          getMatrixToConstructSuffixTrees();
      this.incrementPercentDone();
/*      pw.println("GET MATRIX TO CONSTRUCT SUFFIX TREE: " +
                 (System.currentTimeMillis() - time));
 */

      // INSERT MATRIX IN THE TREE

      //INSERT ORIGINAL MATRIX
//      time = System.currentTimeMillis();
      this.setSubtaskValues(this.matrix.getNumberOfGenes());
      SuffixTreeNoErrors_Extended_STEP1_Builder builder1 = new
          SuffixTreeNoErrors_Extended_STEP1_Builder(cccBiclustersSuffixTree);
      for (int i = 0; i < this.matrix.getNumberOfGenes(); i++) {
        //      System.out.println("Adding Row = " + i);
        builder1.addToken(matrixToConstructSuffixTrees[i]);
        this.incrementSubtaskPercentDone();
      }
      this.updatePercentDone();
//      pw.println("ADD TOKENS TO TREE: " + (System.currentTimeMillis() - time));

      //    System.out.println("Adding path length");
//      time = System.currentTimeMillis();
      cccBiclustersSuffixTree.addPathLength();
      this.incrementPercentDone();
//      pw.println("ADD PATH LENGTH: " + (System.currentTimeMillis() - time));

      //    System.out.println("Adding number of leaves");
//      time = System.currentTimeMillis();
      cccBiclustersSuffixTree.addNumberOfLeaves();
      this.incrementPercentDone();
//      pw.println("ADD NUM LEAVES: " + (System.currentTimeMillis() - time));

      //    System.out.println("Marking non-maximal nodes");
      // !!! BEFORE GENE-SHIFTS
//      time = System.currentTimeMillis();
      cccBiclustersSuffixTree.addIsNotLeftMaximal();
      this.incrementPercentDone();
/*      pw.println("MARK NON LEFT MAXIMAL (BEFORE GENE SHIFTS): " +
                 (System.currentTimeMillis() - time));
 */

      //INSERT MATRIX WITH GENE-SHIFTS
//      time = System.currentTimeMillis();
      this.setSubtaskValues(matrixToConstructSuffixTrees.length
                            -this.matrix.getNumberOfGenes());
      SuffixTreeNoErrors_Extended_STEP2_Builder builder2 = new
          SuffixTreeNoErrors_Extended_STEP2_Builder(cccBiclustersSuffixTree);
      for (int i = this.matrix.getNumberOfGenes();
           i < matrixToConstructSuffixTrees.length; i++) {
        //      System.out.println("Adding Row = " + i);
        builder2.addToken(matrixToConstructSuffixTrees[i]);
        this.incrementSubtaskPercentDone();
      }
      this.updatePercentDone();
/*      pw.println("ADD GENE SHIFTS TOKENS: " +
                 (System.currentTimeMillis() - time));
 */

//      time = System.currentTimeMillis();
      //    System.out.println("Adding path length");
      cccBiclustersSuffixTree.addPathLength();
      this.incrementPercentDone();
//      pw.println("ADD PATH LENGTH: " + (System.currentTimeMillis() - time));

//      time = System.currentTimeMillis();
      //    System.out.println("Adding number of leaves");
      cccBiclustersSuffixTree.addNumberOfLeaves();
      this.incrementPercentDone();
//      pw.println("ADD NUM LEAVES: " + (System.currentTimeMillis() - time));

//      time = System.currentTimeMillis();
      //    System.out.println("Marking non-maximal nodes");
      // !!! AFTER GENE-SHIFTS
      cccBiclustersSuffixTree.addIsNotLeftMaximal_After_Extensions();
      this.incrementPercentDone();
/*      pw.println("MARK NON LEFT MAXIMAL (AFTER GENE SHIFTS): " +
                 (System.currentTimeMillis() - time));
      pw.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
 */
    return (cccBiclustersSuffixTree);
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   *
   * @param b CCC_Bicluster
   * @param biclustersWithoutRepetitions HashMap
   */
  protected void keepBicluster(CCC_Bicluster b, HashMap<Integer, CCC_Bicluster>
      biclustersWithoutRepetitions) {
    // CHECK IF BICLUSTER ALREADY EXISTS AND STORE IT IF IT DOESN'T
    int hashCode = b.hashCode();
    if (biclustersWithoutRepetitions.containsKey(hashCode)) {
      CCC_Bicluster object = biclustersWithoutRepetitions.get(hashCode); // get object that has this key
      if (!object.equals(b)) {
        // THERE IS NOT A BICLUSTER IN THE HASHTREE THAT REPRESENTS THE SAME BICLUSTER
        biclustersWithoutRepetitions.put(hashCode, b);
        this.setOfBiclusters.add(b);
      }
    }
    else {
      // THERE IS NOT A BICLUSTER IN THE HASHTREE THAT REPRESENTS THE SAME BICLUSTER
      biclustersWithoutRepetitions.put(hashCode, b);
      this.setOfBiclusters.add(b);
    }
  }

  //######################################################################################################################

  protected void countAndCreateMaximalBiclusters(SuffixTreeNoErrors
                                         cccBiclustersSuffixTree,
                                         NodeInterface node,
                                         HashMap<Integer, CCC_Bicluster> biclustersWithoutRepetitions,
                                         int rowsQuorum,
                                         int columnsQuorum) throws Exception {
/*    PrintWriter pw = new PrintWriter("ccc_gene_shifts_times_1.txt");
    long time = System.currentTimeMillis();
 */

    int[] numberOfBiclusters = new int[1];
    numberOfBiclusters[0] = 0;

    countMaximalBiclusters(cccBiclustersSuffixTree, node,
                           biclustersWithoutRepetitions,
                           rowsQuorum,
                           columnsQuorum, numberOfBiclusters);
//    pw.println("COUNT MAXIMAL BICLUSTERS: " + (System.currentTimeMillis()-time));

//    this.setSubtaskValues(numberOfBiclusters[0]);
/*    createMaximalBiclusters(cccBiclustersSuffixTree, node,
                            biclustersWithoutRepetitions,
                                    rowsQuorum, columnsQuorum,
                                    numberOfBiclusters);
 */
//time = System.currentTimeMillis();
this.setSubtaskValues(numberOfBiclusters[0]);
this.createMaximalBiclusters(cccBiclustersSuffixTree, node,
                            biclustersWithoutRepetitions,
                                    rowsQuorum, columnsQuorum, numberOfBiclusters);
this.updatePercentDone();
/*pw.println("CREATE MAXIMAL BICLUSTERS: " + (System.currentTimeMillis()-time));
pw.close();
 */
}

  /**
   *
   * @param cccBiclustersSuffixTree SuffixTreeNoErrors
   * @param node NodeInterface
   * @param biclustersWithoutRepetitions HashMap
   * @param rowsQuorum int
   * @param columnsQuorum int
   */
  protected void countMaximalBiclusters(
      SuffixTreeNoErrors cccBiclustersSuffixTree,
      NodeInterface node,
      HashMap<Integer, CCC_Bicluster> biclustersWithoutRepetitions,
      int rowsQuorum, int columnsQuorum, int[] numberOfBiclusters) {
    if (node == null) {
      return;
    }

    int firstColumn;
    int numberOfColumns = 0;
    boolean thisNodeIsMaximalBicluster = false;
    if (node instanceof InternalNodeNoErrors_Extended) {
      thisNodeIsMaximalBicluster = ( (InternalNodeNoErrors_Extended) node).getIsMaximal_After_Extensions();

      // CHECK IF BICLUSTER IS MAXIMAL AFTER GENE SHIFTS
      // !!!
      // CHECK ROWS QUORUM
      if (thisNodeIsMaximalBicluster && node.getNumberOfLeaves() >= rowsQuorum) {
        //compute first column
        firstColumn = cccBiclustersSuffixTree.computeFirstColumn(node);
        //compute number of columns
        int patternLength = cccBiclustersSuffixTree.computeNumberOfColumns(node);
        if (this.matrix.getIsVBTP()) { // **IF MATRIX WAS DISCRETIZED USING VBTP (VARIATIONS BETWEEN TIME-POINTS)**
          numberOfColumns = patternLength + 1;
        }
        else {
          numberOfColumns = patternLength;
        }

        // CHECK COLUMNS QUORUM
        if (numberOfColumns >= columnsQuorum) {

          // initialize ARRAY columnIndexes
          int[] columnsIndexes = new int[numberOfColumns];
          for (int j = 0; j < numberOfColumns; j++) {
            columnsIndexes[j] = firstColumn + j;
          }
          // initialize ARRAY rowIndexes
          // when this.geneShifts = true each gene may be found more than once
          TreeSet<Integer> rows = new TreeSet<Integer> ();
          // go down
          this.getRows(cccBiclustersSuffixTree,
                       ( (InternalNodeNoErrors_Extended) node).getFirstChild(),
                       rows);

          // CHECK ROWS QUORUM
          if (rows.size() >= rowsQuorum) {
            numberOfBiclusters[0] = numberOfBiclusters[0] + 1;
          }
        }
      }
      // GO DOWN
      this.countMaximalBiclusters(cccBiclustersSuffixTree,
                                   ( (InternalNodeNoErrors_Extended) node).
                                   getFirstChild(),
                                   biclustersWithoutRepetitions,
                                   rowsQuorum, columnsQuorum,
                                   numberOfBiclusters);
      // GO RIGHT
      if (node.getRightSybling() != null) {
        this.countMaximalBiclusters(cccBiclustersSuffixTree,
                                     node.getRightSybling(),
                                     biclustersWithoutRepetitions,
                                     rowsQuorum, columnsQuorum,
                                     numberOfBiclusters);
      }
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        thisNodeIsMaximalBicluster = ( (LeafNodeNoErrors) node).getIsMaximal();
        if (thisNodeIsMaximalBicluster && rowsQuorum == 1) { // the bicluster is maximal and trivial
          numberOfBiclusters[0] = numberOfBiclusters[0] + 1;
        }
        // GO RIGHT
        if (node.getRightSybling() != null) {
          this.countMaximalBiclusters(cccBiclustersSuffixTree,
                                       node.getRightSybling(),
                                       biclustersWithoutRepetitions,
                                       rowsQuorum, columnsQuorum, numberOfBiclusters);
        }
      }
    }
  }

  /**
   *
   * @param cccBiclustersSuffixTree SuffixTreeNoErrors
   * @param node NodeInterface
   * @param biclustersWithoutRepetitions HashMap
   * @param rowsQuorum int
   * @param columnsQuorum int
   */
  protected void createMaximalBiclusters(
      SuffixTreeNoErrors cccBiclustersSuffixTree,
      NodeInterface node,
      HashMap<Integer, CCC_Bicluster> biclustersWithoutRepetitions,
      int rowsQuorum, int columnsQuorum, int[] numberOfBiclusters) {
    if (node == null) {
      return;
    }

    int firstColumn;
    int numberOfColumns = 0;
    boolean thisNodeIsMaximalBicluster = false;
    if (node instanceof InternalNodeNoErrors_Extended) {
      thisNodeIsMaximalBicluster = ( (InternalNodeNoErrors_Extended) node).getIsMaximal_After_Extensions();

      // CHECK IF BICLUSTER IS MAXIMAL AFTER GENE SHIFTS
      // !!!
      // CHECK ROWS QUORUM
      if (thisNodeIsMaximalBicluster && node.getNumberOfLeaves() >= rowsQuorum) {
        //compute first column
        firstColumn = cccBiclustersSuffixTree.computeFirstColumn(node);
        //compute number of columns
        int patternLength = cccBiclustersSuffixTree.computeNumberOfColumns(node);
        if (this.matrix.getIsVBTP()) { // **IF MATRIX WAS DISCRETIZED USING VBTP (VARIATIONS BETWEEN TIME-POINTS)**
          numberOfColumns = patternLength + 1;
        }
        else {
          numberOfColumns = patternLength;
        }

        // CHECK COLUMNS QUORUM
        if (numberOfColumns >= columnsQuorum) {

          // initialize ARRAY columnIndexes
          int[] columnsIndexes = new int[numberOfColumns];
          for (int j = 0; j < numberOfColumns; j++) {
            columnsIndexes[j] = firstColumn + j;
          }
          // initialize ARRAY rowIndexes
          // when this.geneShifts = true each gene may be found more than once
          TreeSet<Integer> rows = new TreeSet<Integer> ();
          // go down
          this.getRows(cccBiclustersSuffixTree,
                       ( (InternalNodeNoErrors_Extended) node).getFirstChild(),
                       rows);

          // CHECK ROWS QUORUM
          if (rows.size() >= rowsQuorum) {
            // INSERT BICLUSTER IN THE ARRAY SetOfBiclusters
            int[] rowsIndexes = new int[rows.size()];
            Iterator<Integer> i = rows.iterator();
            int p = 0;
            boolean hasGeneShifts = false;
            char firstSymbol = ' ';
            while (i.hasNext()) {
              rowsIndexes[p] = i.next().intValue();
              if (p == 0) {
                firstSymbol = this.matrix.getSymbolicExpressionMatrix()[
                    rowsIndexes[0] - 1][columnsIndexes[0] - 1];
              }
              else {
                char currentFirstSymbol = this.matrix.
                    getSymbolicExpressionMatrix()[
                    rowsIndexes[p] - 1][columnsIndexes[0] - 1];
                if (firstSymbol != currentFirstSymbol) {
                  hasGeneShifts = true;
                }
              }
              p++;
            }

            //CREATE BICLUSTER
            if (hasGeneShifts) {
              CCC_Bicluster_GeneShifts b = new CCC_Bicluster_GeneShifts(this,
                  this.getBiclusters().size() + 1, rowsIndexes, columnsIndexes);
              this.keepBicluster(b, biclustersWithoutRepetitions);
            }
            else { // TEST IF NODE WAS MAXIMAL BEFORE EXTENSIONS
              boolean wasMaximalBeforeGeneShifts = ( (
                  InternalNodeNoErrors_Extended) node).getIsMaximal();
              if (wasMaximalBeforeGeneShifts) {
                CCC_Bicluster b = new CCC_Bicluster(this,
                    this.getBiclusters().size() +
                    1, rowsIndexes,
                    columnsIndexes);
                this.keepBicluster(b, biclustersWithoutRepetitions);
              }
            }
            this.updateBiclusterPercentDone(numberOfBiclusters);
          }
        }
      }
      // GO DOWN
      this.createMaximalBiclusters(cccBiclustersSuffixTree,
                                   ( (InternalNodeNoErrors_Extended) node).
                                   getFirstChild(),
                                   biclustersWithoutRepetitions, rowsQuorum,
                                   columnsQuorum, numberOfBiclusters);
      // GO RIGHT
      if (node.getRightSybling() != null) {
        this.createMaximalBiclusters(cccBiclustersSuffixTree,
                                     node.getRightSybling(),
                                     biclustersWithoutRepetitions,
                                     rowsQuorum, columnsQuorum,
                                     numberOfBiclusters);
      }
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        thisNodeIsMaximalBicluster = ( (LeafNodeNoErrors) node).getIsMaximal();
        if (thisNodeIsMaximalBicluster && rowsQuorum == 1) { // the bicluster is maximal and trivial
          // print bicluster identification together with its rows
          //compute the first column
          firstColumn = cccBiclustersSuffixTree.computeFirstColumn(node);
          int patternLength = cccBiclustersSuffixTree.computeNumberOfColumns(
              node);
          //compute number of columns
          if (this.matrix.getIsVBTP()) {
            numberOfColumns = patternLength + 1;
          }
          else {
            numberOfColumns = patternLength;
          }
          // initialize ARRAY columnIndexes
          int[] columnsIndexes = new int[numberOfColumns];
          for (int j = 0; j < numberOfColumns; j++) {
            columnsIndexes[j] = firstColumn + j;
          }

          // initialize ARRAY rowIndexes
          int[] rowsIndexes = new int[1];
          rowsIndexes[0] = cccBiclustersSuffixTree.computeRowNumber( (
              LeafNodeNoErrors) node);
          // CREATE BICLUSTER
          CCC_Bicluster b = new CCC_Bicluster(this,
                                              this.getBiclusters().size() + 1,
                                              rowsIndexes, columnsIndexes);
          this.keepBicluster(b, biclustersWithoutRepetitions);
          this.updateBiclusterPercentDone(numberOfBiclusters);
        }
        // GO RIGHT
        if (node.getRightSybling() != null) {
          this.createMaximalBiclusters(cccBiclustersSuffixTree,
                                       node.getRightSybling(),
                                       biclustersWithoutRepetitions,
                                       rowsQuorum, columnsQuorum,
                                       numberOfBiclusters);
        }
      }
    }
  }

  /**
   *
   * @param cccBiclustersSuffixTree SuffixTreeNoErrors
   * @param node NodeInterface
   * @param biclustersWithoutRepetitions HashMap
   * @param rowsQuorum int
   * @param columnsQuorum int
   */
  protected void createMaximalBiclusters(
      SuffixTreeNoErrors cccBiclustersSuffixTree,
      NodeInterface node,
      HashMap<Integer, CCC_Bicluster> biclustersWithoutRepetitions,
      int rowsQuorum, int columnsQuorum) {
    if (node == null) {
      return;
    }

    int firstColumn;
    int numberOfColumns = 0;
    boolean thisNodeIsMaximalBicluster = false;
    if (node instanceof InternalNodeNoErrors_Extended) {
      thisNodeIsMaximalBicluster = ( (InternalNodeNoErrors_Extended) node).getIsMaximal_After_Extensions();

      // CHECK IF BICLUSTER IS MAXIMAL AFTER GENE SHIFTS
      // !!!
      // CHECK ROWS QUORUM
      if (thisNodeIsMaximalBicluster && node.getNumberOfLeaves() >= rowsQuorum) {
        //compute first column
        firstColumn = cccBiclustersSuffixTree.computeFirstColumn(node);
        //compute number of columns
        int patternLength = cccBiclustersSuffixTree.computeNumberOfColumns(node);
        if (this.matrix.getIsVBTP()) { // **IF MATRIX WAS DISCRETIZED USING VBTP (VARIATIONS BETWEEN TIME-POINTS)**
          numberOfColumns = patternLength + 1;
        }
        else {
          numberOfColumns = patternLength;
        }

        // CHECK COLUMNS QUORUM
        if (numberOfColumns >= columnsQuorum) {

          // initialize ARRAY columnIndexes
          int[] columnsIndexes = new int[numberOfColumns];
          for (int j = 0; j < numberOfColumns; j++) {
            columnsIndexes[j] = firstColumn + j;
          }
          // initialize ARRAY rowIndexes
          // when this.geneShifts = true each gene may be found more than once
          TreeSet<Integer> rows = new TreeSet<Integer> ();
          // go down
          this.getRows(cccBiclustersSuffixTree,
                       ( (InternalNodeNoErrors_Extended) node).getFirstChild(),
                       rows);

          // CHECK ROWS QUORUM
          if (rows.size() >= rowsQuorum) {
            // INSERT BICLUSTER IN THE ARRAY SetOfBiclusters
            int[] rowsIndexes = new int[rows.size()];
            Iterator<Integer> i = rows.iterator();
            int p = 0;
            boolean hasGeneShifts = false;
            char firstSymbol = ' ';
            while (i.hasNext()) {
              rowsIndexes[p] = i.next().intValue();
              if (p == 0) {
                firstSymbol = this.matrix.getSymbolicExpressionMatrix()[
                    rowsIndexes[0] - 1][columnsIndexes[0] - 1];
              }
              else {
                char currentFirstSymbol = this.matrix.
                    getSymbolicExpressionMatrix()[
                    rowsIndexes[p] - 1][columnsIndexes[0] - 1];
                if (firstSymbol != currentFirstSymbol) {
                  hasGeneShifts = true;
                }
              }
              p++;
            }

            //CREATE BICLUSTER
            if (hasGeneShifts) {
              CCC_Bicluster_GeneShifts b = new CCC_Bicluster_GeneShifts(this,
                  this.getBiclusters().size() + 1, rowsIndexes, columnsIndexes);
              this.keepBicluster(b, biclustersWithoutRepetitions);
            }
            else { // TEST IF NODE WAS MAXIMAL BEFORE EXTENSIONS
              boolean wasMaximalBeforeGeneShifts = ( (
                  InternalNodeNoErrors_Extended) node).getIsMaximal();
              if (wasMaximalBeforeGeneShifts) {
                CCC_Bicluster b = new CCC_Bicluster(this,
                    this.getBiclusters().size() +
                    1, rowsIndexes,
                    columnsIndexes);
                this.keepBicluster(b, biclustersWithoutRepetitions);
              }
            }
          }
        }
      }
      // GO DOWN
      this.createMaximalBiclusters(cccBiclustersSuffixTree,
                                   ( (InternalNodeNoErrors_Extended) node).
                                   getFirstChild(),
                                   biclustersWithoutRepetitions, rowsQuorum, columnsQuorum);
      // GO RIGHT
      if (node.getRightSybling() != null) {
        this.createMaximalBiclusters(cccBiclustersSuffixTree,
                                     node.getRightSybling(),
                                     biclustersWithoutRepetitions,
                                     rowsQuorum, columnsQuorum);
      }
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        thisNodeIsMaximalBicluster = ( (LeafNodeNoErrors) node).getIsMaximal();
        if (thisNodeIsMaximalBicluster && rowsQuorum == 1) { // the bicluster is maximal and trivial
          // print bicluster identification together with its rows
          //compute the first column
          firstColumn = cccBiclustersSuffixTree.computeFirstColumn(node);
          int patternLength = cccBiclustersSuffixTree.computeNumberOfColumns(
              node);
          //compute number of columns
          if (this.matrix.getIsVBTP()) {
            numberOfColumns = patternLength + 1;
          }
          else {
            numberOfColumns = patternLength;
          }
          // initialize ARRAY columnIndexes
          int[] columnsIndexes = new int[numberOfColumns];
          for (int j = 0; j < numberOfColumns; j++) {
            columnsIndexes[j] = firstColumn + j;
          }

          // initialize ARRAY rowIndexes
          int[] rowsIndexes = new int[1];
          rowsIndexes[0] = cccBiclustersSuffixTree.computeRowNumber( (
              LeafNodeNoErrors) node);
          // CREATE BICLUSTER
          CCC_Bicluster b = new CCC_Bicluster(this,
                                              this.getBiclusters().size() + 1,
                                              rowsIndexes, columnsIndexes);
          this.keepBicluster(b, biclustersWithoutRepetitions);
        }
        // GO RIGHT
        if (node.getRightSybling() != null) {
          this.createMaximalBiclusters(cccBiclustersSuffixTree,
                                       node.getRightSybling(),
                                       biclustersWithoutRepetitions,
                                       rowsQuorum, columnsQuorum);
        }
      }
    }
  }

  //######################################################################################################################

  /**
   * Inserts the indexes of rows of the maximal ccc-bicluster in its array RowIndexes.
   *
   * @param cccBiclustersSuffixTree SuffixTreeNoErrors
   * @param node NodeInterface
   * @param rows TreeSet
   */
  private void getRows(SuffixTreeNoErrors cccBiclustersSuffixTree,
                       NodeInterface node, TreeSet<Integer> rows) {
    if (node instanceof InternalNodeNoErrors_Extended) {
      // GO DOWN
      this.getRows(cccBiclustersSuffixTree,
                   ( (InternalNodeNoErrors_Extended) node).getFirstChild(),
                   rows);
      // GO RIGHT
      if (node.getRightSybling() != null) {
        this.getRows(cccBiclustersSuffixTree, node.getRightSybling(), rows);
      }
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        //INSERT ROW INDEX
        int rowNumber = cccBiclustersSuffixTree.computeRowNumber( (
            LeafNodeNoErrors) node);
        if (rowNumber > this.matrix.getNumberOfGenes()) { // GENE SHIFTS
          rowNumber = rowNumber % this.matrix.getNumberOfGenes();
          if (rowNumber == 0) { // rowNumber = last gene
            rowNumber = this.matrix.getNumberOfGenes();
          }
        }
        rows.add(rowNumber);
        // GO RIGHT
        if (node.getRightSybling() != null) {
          this.getRows(cccBiclustersSuffixTree, node.getRightSybling(), rows);
        }
      }
    }
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   *
   * @return Object
   */
  public Object clone() {
    return super.clone();
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
}
