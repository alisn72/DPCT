package smadeira.biclustering;

import java.io.*;
import java.util.*;

import smadeira.suffixtrees.*;
import smadeira.utils.*;

/**
 * <p>Title: CCC Biclustering Sign Changes</p>
 *
 * <p>Description: CCC-Biclustering algorithm extension for identifying
 *                 CCC Biclusters with sign changes (anticorrelated
 *                 patterns).</p>
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
public class CCC_Biclustering_SignChanges
    extends CCC_Biclustering implements Cloneable, Serializable {
  public static final long serialVersionUID = 3840978182424241111L;

  /**
   *
   * @param m DiscretizedExpressionMatrix
   * @throws Exception
   */
  public CCC_Biclustering_SignChanges(DiscretizedExpressionMatrix m) {
    super(m);
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   *
   * @return char[][]
   */
  public char[][] getDiscretizedMatrixWithSignChanges() {
    int numberOfRows = this.matrix.getSymbolicExpressionMatrix().length * 2;
    int numberOfColumns = this.matrix.getSymbolicExpressionMatrix()[0].length;
    char[][] matrix = new char[numberOfRows][numberOfColumns];
    //ORIGINAL SYMBOLIC MATRIX
    for (int i = 0; i < numberOfRows / 2; i++) {
      for (int j = 0; j < numberOfColumns; j++) {
        matrix[i][j] = this.matrix.getSymbolicExpressionMatrix()[i][j];
      }
    }
    //SYMBOLIC MATRIX WITH SIGN CHANGES
    for (int i = numberOfRows / 2; i < numberOfRows; i++) {
      for (int j = 0; j < numberOfColumns; j++) {
        char currentSymbol = this.matrix.getSymbolicExpressionMatrix()[i -
            numberOfRows / 2][j];
        char opposedSymbol;
        // MISSING VALUE ?
        if (currentSymbol == this.matrix.getMissingValue()) {
          opposedSymbol = currentSymbol;
        }
        else {
          boolean currentSymbolFound = false;
          int position = 0;
          while (!currentSymbolFound && position < this.getAlphabet().length) {
            if (this.getAlphabet()[position] == currentSymbol) {
              currentSymbolFound = true;
            }
            else {
              position++;
            }
          }

          if (position == this.getAlphabet().length / 2) {
            opposedSymbol = currentSymbol;
          }
          else {
            opposedSymbol = this.getAlphabet()[this.getAlphabet().length -
                position - 1];
          }
        }
        matrix[i][j] = opposedSymbol;
      }
    }
    return matrix;
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   * Example:
   *
   * U D N
   * N N U
   *
   * WOULD BE CONVERTED TO
   *
   * U1 D2 N3 -2
   * N1 N2 U3 -1
   *
   * IF WE APPLY SIGN CHANGES
   *
   * D-->U
   * N-->N
   * U-->D
   *
   * U1 D2 N3 -4
   * N1 N2 U3 -3
   * D1 U2 N3 -2
   * N1 N2 D3 -1
   *
   * is converted to
   *
   * 851 682 783 -4
   * 781 782 853 -3
   * 681 852 783 -2
   * 781 782 683 -1
   *
   * @return int[][]
   * @throws Exception
   */
  protected int[][] getMatrixToConstructSuffixTrees() {
    int numberOfRows = this.matrix.getSymbolicExpressionMatrix().length * 2;
    int numberOfColumns = this.matrix.getNumberOfConditions() + 1; // plus 1 to terminator
    int[][] matrixToConstructSuffixTrees = new int[numberOfRows][
        numberOfColumns];
    //ORIGINAL SYMBOLIC MATRIX
    for (int i = 0; i < numberOfRows / 2; i++) {
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
//         System.out.print(matrixToConstructSuffixTrees[i][j] + "\t");
      }
      element = - (numberOfRows - i);
      matrixToConstructSuffixTrees[i][numberOfColumns - 1] = element;
//       System.out.println(matrixToConstructSuffixTrees[i][numberOfColumns - 1]);
    }
    //SYMBOLIC MATRIX WITH SIGN CHANGES
    for (int i = numberOfRows / 2; i < numberOfRows; i++) {
      String c = new String();
      String p = new String();
      String s = new String();
      int element;
      for (int j = 0; j < numberOfColumns - 1; j++) {
        char currentSymbol = this.matrix.getSymbolicExpressionMatrix()[i -
            numberOfRows / 2][j];
        boolean currentSymbolFound = false;
        int position = 0;
        while (!currentSymbolFound && position < this.getAlphabet().length) {
          if (this.getAlphabet()[position] == currentSymbol) {
            currentSymbolFound = true;
          }
          else {
            position++;
          }
        }
        char opposedSymbol;
        if (position == this.getAlphabet().length / 2) {
          opposedSymbol = currentSymbol;
        }
        else {
          opposedSymbol = this.getAlphabet()[this.getAlphabet().length -
              position - 1];
        }
        c = Integer.toString( (int) opposedSymbol);
        p = Integer.toString(j + 1);
        s = new String(c + p);
        element = Integer.parseInt(s);
        matrixToConstructSuffixTrees[i][j] = element;
      }
      element = - (numberOfRows - i);
      matrixToConstructSuffixTrees[i][numberOfColumns - 1] = element;
//       System.out.println(matrixToConstructSuffixTrees[i][numberOfColumns - 1]);
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
    SuffixTreeNoErrors_Extended cccBiclustersSuffixTree = (
        SuffixTreeNoErrors_Extended)this.constructSuffixTree();
    // create set of objects of class CCC_Bicluster and initialize them with the information of each maximal
    // ccc-bicluster - NOT LINEAR

    // CREATE HASHMAP TO STORE BICLUSTERS WITHOUT REPETITIONS
    int worstCaseNumberOfBiclusters = 2 * this.matrix.getNumberOfGenes() *
        this.matrix.getNumberOfConditions();
    HashMap<Integer, CCC_Bicluster_SignChanges> biclustersWithoutRepetitions = new
        LinkedHashMap<Integer, CCC_Bicluster_SignChanges> (worstCaseNumberOfBiclusters,
                                               (float) 0.75);

    // GET BICLUSTERS FROM SUFFIX TREE
    // the root is not a bicluster
    this.setOfBiclusters = new ArrayList<Bicluster> (0);
    NodeInterface firstChild = cccBiclustersSuffixTree.getRoot().getFirstChild();
/*    this.createMaximalBiclusters(cccBiclustersSuffixTree, firstChild,
                                 biclustersWithoutRepetitions, rowsQuorum, columnsQuorum);
 */
this.countAndCreateMaximalBiclusters(cccBiclustersSuffixTree, firstChild,
                                     biclustersWithoutRepetitions, rowsQuorum,
                                     columnsQuorum);
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

/*    try {
      PrintWriter pw = new PrintWriter("ccc_sign_changes_times_0.txt");
      long time = System.currentTimeMillis();
 */
      // COMPUTE MATRIX TO CONSTRUCT SUFFIX TREE
      int[][] matrixToConstructSuffixTrees = this.getMatrixToConstructSuffixTrees();
      this.incrementPercentDone();
/*      pw.println("GET MATRIX TO CONSTRUCT SUFFIX TREE: " +
                 (System.currentTimeMillis() - time));
 */

      // Build generalized suffix tree.

//      time = System.currentTimeMillis();
      this.setSubtaskValues(this.matrix.getNumberOfGenes());
      // ADD ORIGINAL MATRIX
      SuffixTreeNoErrors_Extended_STEP1_Builder builder1 = new
          SuffixTreeNoErrors_Extended_STEP1_Builder(cccBiclustersSuffixTree);
      for (int i = 0; i < this.matrix.getNumberOfGenes(); i++) {
        //      System.out.println("Adding Row = " + i);
        builder1.addToken(matrixToConstructSuffixTrees[i]);
        this.incrementSubtaskPercentDone();
      }
      this.updatePercentDone();
//      pw.println("ADD TOKENS: " + (System.currentTimeMillis() - time));

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
      // !!! BEFORE SIGN-CHANGES
      cccBiclustersSuffixTree.addIsNotLeftMaximal();
      this.incrementPercentDone();
//      pw.println("ADD NON LEFT MAX: " + (System.currentTimeMillis() - time));

//      time = System.currentTimeMillis();
      this.setSubtaskValues(matrixToConstructSuffixTrees.length-this.matrix.getNumberOfGenes());
      // ADD ORIGINAL MATRIX WITH SIGN.CHANGES
      SuffixTreeNoErrors_Extended_STEP2_Builder builder2 = new
          SuffixTreeNoErrors_Extended_STEP2_Builder(cccBiclustersSuffixTree);
      for (int i = this.matrix.getNumberOfGenes();
           i < matrixToConstructSuffixTrees.length; i++) {
        //      System.out.println("Adding Row = " + i);
        builder2.addToken(matrixToConstructSuffixTrees[i]);
        this.incrementSubtaskPercentDone();
      }
      this.updatePercentDone();
//      pw.println("ADD TOKENS: " + (System.currentTimeMillis() - time));

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
      // !!! AFTER SIGN-CHANGES
      cccBiclustersSuffixTree.addIsNotLeftMaximal_After_Extensions();
      this.incrementPercentDone();
/*      pw.println("ADD NON LEFT MAX: " + (System.currentTimeMillis() - time));
      pw.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
 */

    return (cccBiclustersSuffixTree);
  }

  //######################################################################################################################

  /**
   *
   * @param b CCC_Bicluster
   * @param biclustersWithoutRepetitions HashMap
   */
  protected void keepBicluster(CCC_Bicluster_SignChanges b, HashMap<Integer, CCC_Bicluster_SignChanges> biclustersWithoutRepetitions) {
    // CHECK IF BICLUSTER ALREADY EXISTS AND STORE IT IF IT DOESN'T
    int hashCode = b.hashCode();
    if (biclustersWithoutRepetitions.containsKey(hashCode)) {
      CCC_Bicluster_SignChanges object = biclustersWithoutRepetitions.get(hashCode); // get object that has this key
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
  //######################################################################################################################

  /**
   *
   * @param cccBiclustersSuffixTree SuffixTreeNoErrors
   * @param node NodeInterface
   * @param biclustersWithoutRepetitions HashMap
   * @param rowsQuorum int
   * @param columnsQuorum int
   */
  protected void countMaximalBiclusters(
      SuffixTreeNoErrors
      cccBiclustersSuffixTree,
      NodeInterface node,
      HashMap<Integer, CCC_Bicluster_SignChanges> biclustersWithoutRepetitions,
      int rowsQuorum, int columnsQuorum, int[] numberOfBiclusters) {
    if (node == null) {
      return;
    }

    int firstColumn;
    int numberOfColumns = 0;
    boolean thisNodeIsMaximalBicluster = false;
    if (node instanceof InternalNodeNoErrors_Extended) {
      thisNodeIsMaximalBicluster = ((InternalNodeNoErrors_Extended) node).getIsMaximal_After_Extensions();
      // CHECK IF BICLUSTER IS MAXIMAL AFTER SIGN-CHANGES
      // !!!
      // CHECK ROWS MAXIMUM QUORUM BY CHEKING NUMBER OF LEAVES
      if (thisNodeIsMaximalBicluster && node.getNumberOfLeaves() >= rowsQuorum) {
        //compute the first column
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
          // initialize ARRAY rowIndexes
          // when this.geneShifts = true each gene may be found more than once
          TreeMap<Integer, Row_SignChange> rows_signChanges = new TreeMap<Integer, Row_SignChange> ();
          this.getRows(cccBiclustersSuffixTree,
                       ( (InternalNodeNoErrors_Extended) node).getFirstChild(),
                       rows_signChanges);

          // CHECK "REAL" ROWS QUORUM
          // WHEN ALPHABET HAS AN ODD NUMBER OF SYMBOLS SUCH AS {D,N,U} THE SYMBOL IN THE MIDDLE DOES NOT
          // CHANGE WHEN SIGN CHANGES ARE APPLIED THIS MEANS THAT WE CAN HAVE AN INTERNAL NODE WITH A PATTERN
          // THAT HAS ONLY THE SYMBOL 'N' WITH TWO LEAVES THAT CORRESPOND TO THE SAME GENE (ORIGINAL AND WITH SIGN CHANGES)
          // THIS WILL IDENTIFY A BICLUSTER THAT IS MARKED AS MAXIMAL IN THE TREE BUT SHOULD SHOULD NOT BE REPORTED
          // UNLESS IT HAS THE MAXIMAL NUMBER OF COLUMNS (# COLUMNS IN THE MATRIX) ALSO REQUIRED FOR LEAF NODES TO BE
          // CONSIDERED MAXIMAL
          int rowsInBicluster = rows_signChanges.size();
          if ((rowsInBicluster > rowsQuorum) // => rowsInBicluster > 1 => no problem
              ||
              (rowsInBicluster == rowsQuorum && rowsQuorum != 1)
              ||
              (rowsInBicluster == rowsQuorum && rowsQuorum == 1 &&
               numberOfColumns == this.matrix.getNumberOfConditions())) {
            numberOfBiclusters[0] = numberOfBiclusters[0]+1;
          }
        }
      }
      // GO DOWN
      this.countMaximalBiclusters(cccBiclustersSuffixTree,
                                   ( (InternalNodeNoErrors_Extended) node).
                                   getFirstChild(),
                                   biclustersWithoutRepetitions, rowsQuorum,
                                   columnsQuorum, numberOfBiclusters);
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
          numberOfBiclusters[0] = numberOfBiclusters[0]+1;
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

  protected void countAndCreateMaximalBiclusters(SuffixTreeNoErrors cccBiclustersSuffixTree,
                                         NodeInterface node,
                                         HashMap<Integer, CCC_Bicluster_SignChanges> biclustersWithoutRepetitions,
                                         int rowsQuorum,
                                         int columnsQuorum) throws Exception {
    int[] numberOfBiclusters = new int[1];
    numberOfBiclusters[0] = 0;

/*    PrintWriter pw = new PrintWriter("ccc_sign_changes_times_1.txt");
    long time = System.currentTimeMillis();
 */
    countMaximalBiclusters(cccBiclustersSuffixTree, node,
                           biclustersWithoutRepetitions, rowsQuorum,
                           columnsQuorum, numberOfBiclusters);
//    pw.println("COUNT MAXIMAL BICLUSTERS: " + (System.currentTimeMillis()-time));

//    time = System.currentTimeMillis();
/*    createMaximalBiclusters(cccBiclustersSuffixTree, node,
                                    biclustersWithoutRepetitions,
                                    rowsQuorum, columnsQuorum);
 */
this.setSubtaskValues(numberOfBiclusters[0]);
createMaximalBiclusters(cccBiclustersSuffixTree, node,
                                    biclustersWithoutRepetitions,
                                    rowsQuorum, columnsQuorum,
                                    numberOfBiclusters);
this.updatePercentDone();

/*    pw.println("CREATE MAXIMAL BICLUSTERS: " + (System.currentTimeMillis()-time));
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
  protected void createMaximalBiclusters(
      SuffixTreeNoErrors cccBiclustersSuffixTree,
      NodeInterface node,
      HashMap<Integer, CCC_Bicluster_SignChanges> biclustersWithoutRepetitions,
      int rowsQuorum, int columnsQuorum, int[] numberOfBiclusters) {
    if (node == null) {
      return;
    }

    int firstColumn;
    int numberOfColumns = 0;
    boolean thisNodeIsMaximalBicluster = false;
    if (node instanceof InternalNodeNoErrors_Extended) {
      thisNodeIsMaximalBicluster = ( (InternalNodeNoErrors_Extended) node).getIsMaximal_After_Extensions();
      // CHECK IF BICLUSTER IS MAXIMAL AFTER SIGN-CHANGES
      // !!!
      // CHECK ROWS MAXIMUM QUORUM BY CHEKING NUMBER OF LEAVES
      if (thisNodeIsMaximalBicluster && node.getNumberOfLeaves() >= rowsQuorum) {
        //compute the first column
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
          TreeMap<Integer, Row_SignChange> rows_signChanges = new TreeMap<Integer, Row_SignChange> ();
          this.getRows(cccBiclustersSuffixTree,
                       ( (InternalNodeNoErrors_Extended) node).getFirstChild(),
                       rows_signChanges);

          // CHECK "REAL" ROWS QUORUM
          // WHEN ALPHABET HAS AN ODD NUMBER OF SYMBOLS SUCH AS {D,N,U} THE SYMBOL IN THE MIDDLE DOES NOT
          // CHANGE WHEN SIGN CHANGES ARE APPLIED THIS MEANS THAT WE CAN HAVE AN INTERNAL NODE WITH A PATTERN
          // THAT HAS ONLY THE SYMBOL 'N' WITH TWO LEAVES THAT CORRESPOND TO THE SAME GENE (ORIGINAL AND WITH SIGN CHANGES)
          // THIS WILL IDENTIFY A BICLUSTER THAT IS MARKED AS MAXIMAL IN THE TREE BUT SHOULD SHOULD NOT BE REPORTED
          // UNLESS IT HAS THE MAXIMAL NUMBER OF COLUMNS (# COLUMNS IN THE MATRIX) ALSO REQUIRED FOR LEAF NODES TO BE
          // CONSIDERED MAXIMAL
          int rowsInBicluster = rows_signChanges.size();
          if ((rowsInBicluster > rowsQuorum) // => rowsInBicluster > 1 => no problem
              ||
              (rowsInBicluster == rowsQuorum && rowsQuorum != 1)
              ||
              (rowsInBicluster == rowsQuorum && rowsQuorum == 1 && numberOfColumns == this.matrix.getNumberOfConditions())) {
            // INSERT BICLUSTER IN THE ARRAY SetOfBiclusters
            int[] rowsIndexes = new int[rowsInBicluster];
            boolean[] signChanges = new boolean[rowsInBicluster];
            Iterator<Row_SignChange> i = rows_signChanges.values().iterator();
            boolean hasTrue = false;
            boolean hasFalse = false;
            int p = 0;
            while (i.hasNext()) {
              Row_SignChange element = i.next();
              rowsIndexes[p] = element.getRow();
              signChanges[p] = element.getSignChange();

              // !!!
              // I WILL ALWAYS CREATE CCC_BICLUSTERS_WITH_SIGN_CHANGES (EVEN WHEN THE BICLUSTER DISCOVER IS JUST A CCC-BICLUSTER)
              // SINCE ITS PVALUE MUST BE COMPUTED USING P(PATTERN)+ P(OPPOSED PATTERN) BECAUSE WE USED BOTH PATTERNS TO PERFORM THE
              // SEARCH FOR PATTERNS IN THE ALGORITHM
              // !!!
              //
              if (signChanges[p] == true) {
                hasTrue = true;
              }
              else {
                hasFalse = true;
              }
              p++;
            }
            boolean hasSignChanges = hasTrue && hasFalse;

            // CREATE OBJECT BICLUSTER
            CCC_Bicluster_SignChanges b = new CCC_Bicluster_SignChanges(this,
                this.getBiclusters().size() + 1, rowsIndexes, columnsIndexes, signChanges);
            this.updateBiclusterPercentDone(numberOfBiclusters);

            if (hasSignChanges) {
              // !!!
              // CHECK IF hasSignChanged IS NOT WRONG
              // FOR INSTANCE IF THE BICLUSTER PATTERN IS NN IT MIGHT HAVE COME FROM NODES WITH SIGN-CHANGES BUT
              // IT IT NOT REALLY A SIGN CHANGE
              // !!!
              if (this.hasReallySignChanges(b.getBiclusterSymbolicMatrix())) {
                this.keepBicluster(b, biclustersWithoutRepetitions);
              }
              else {
                boolean nodeWasMaximalBeforeSignChanges = ((InternalNodeNoErrors_Extended) node).getIsMaximal();
                if (nodeWasMaximalBeforeSignChanges) {
                  //CCC_Bicluster b1 = new CCC_Bicluster(this,this.getBiclusters().size() +1,rowsIndexes,columnsIndexes);
                  //this.keepBicluster(b1, biclustersWithoutRepetitions);
                  this.keepBicluster(b, biclustersWithoutRepetitions);
                }
              }
            }
            else { // CHECK IF NODE WAS MAXIMAL BEFORE SIGN-CHANGES
              boolean wasMaximalBeforeSignChanges = ((InternalNodeNoErrors_Extended) node).getIsMaximal();
              if (wasMaximalBeforeSignChanges) {
                //CCC_Bicluster b = new CCC_Bicluster(this,this.getBiclusters().size() +1, rowsIndexes,columnsIndexes);
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
                                   biclustersWithoutRepetitions, rowsQuorum,
                                   columnsQuorum, numberOfBiclusters);
      // GO RIGHT
      if (node.getRightSybling() != null) {
        this.createMaximalBiclusters(cccBiclustersSuffixTree,
                                     node.getRightSybling(),
                                     biclustersWithoutRepetitions,
                                     rowsQuorum, columnsQuorum, numberOfBiclusters);
      }
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        thisNodeIsMaximalBicluster = ( (LeafNodeNoErrors) node).getIsMaximal();
        if (thisNodeIsMaximalBicluster && rowsQuorum == 1) { // the bicluster is maximal and trivial
          // print bicluster identification together with its rows
          //compute the first column
          firstColumn = cccBiclustersSuffixTree.computeFirstColumn(node);
          int patternLength = cccBiclustersSuffixTree.computeNumberOfColumns(node);
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
          int rowNumber = cccBiclustersSuffixTree.computeRowNumber( (
              LeafNodeNoErrors) node);
          if (rowNumber > this.matrix.getNumberOfGenes()) { // SIGN CHANGE
            if (rowNumber == 2 * this.matrix.getNumberOfGenes()) { //last gene
              rowsIndexes[0] = this.matrix.getNumberOfGenes();
            }
            else {
              rowsIndexes[0] = rowNumber % this.matrix.getNumberOfGenes();
            }
          }
          else {
            rowsIndexes[0] = rowNumber;
          }

          // INSERT BICLUSTER IN THE ARRAY SetOfBiclusters
          // NO SIGN CHANGE --> CREATE CCC_BICLUSTER ? --> NO!! ALWAYS CREATE CCC_BICLUSTER_WITH_SIGN_CHANGES
          boolean[] signChanges = {false};
          CCC_Bicluster_SignChanges b = new CCC_Bicluster_SignChanges(this,
                                              this.getBiclusters().size() + 1,
                                              rowsIndexes, columnsIndexes, signChanges);
          this.updateBiclusterPercentDone(numberOfBiclusters);
          // CHECK IF BICLUSTER ALREADY EXISTS AND STORE IT IF IT DOESN'T
          int hashCode = b.hashCode();
          if (biclustersWithoutRepetitions.containsKey(hashCode)) {
            CCC_Bicluster_SignChanges object = biclustersWithoutRepetitions.get(hashCode); // get object that has this key
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
      SuffixTreeNoErrors
      cccBiclustersSuffixTree,
      NodeInterface node,
      HashMap<Integer, CCC_Bicluster_SignChanges> biclustersWithoutRepetitions,
      int rowsQuorum, int columnsQuorum) {
    if (node == null) {
      return;
    }

    int firstColumn;
    int numberOfColumns = 0;
    boolean thisNodeIsMaximalBicluster = false;
    if (node instanceof InternalNodeNoErrors_Extended) {
      thisNodeIsMaximalBicluster = ( (InternalNodeNoErrors_Extended) node).getIsMaximal_After_Extensions();
      // CHECK IF BICLUSTER IS MAXIMAL AFTER SIGN-CHANGES
      // !!!
      // CHECK ROWS MAXIMUM QUORUM BY CHEKING NUMBER OF LEAVES
      if (thisNodeIsMaximalBicluster && node.getNumberOfLeaves() >= rowsQuorum) {
        //compute the first column
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
          TreeMap<Integer, Row_SignChange> rows_signChanges = new TreeMap<Integer, Row_SignChange> ();
          this.getRows(cccBiclustersSuffixTree,
                       ( (InternalNodeNoErrors_Extended) node).getFirstChild(),
                       rows_signChanges);

          // CHECK "REAL" ROWS QUORUM
          // WHEN ALPHABET HAS AN ODD NUMBER OF SYMBOLS SUCH AS {D,N,U} THE SYMBOL IN THE MIDDLE DOES NOT
          // CHANGE WHEN SIGN CHANGES ARE APPLIED THIS MEANS THAT WE CAN HAVE AN INTERNAL NODE WITH A PATTERN
          // THAT HAS ONLY THE SYMBOL 'N' WITH TWO LEAVES THAT CORRESPOND TO THE SAME GENE (ORIGINAL AND WITH SIGN CHANGES)
          // THIS WILL IDENTIFY A BICLUSTER THAT IS MARKED AS MAXIMAL IN THE TREE BUT SHOULD SHOULD NOT BE REPORTED
          // UNLESS IT HAS THE MAXIMAL NUMBER OF COLUMNS (# COLUMNS IN THE MATRIX) ALSO REQUIRED FOR LEAF NODES TO BE
          // CONSIDERED MAXIMAL
          int rowsInBicluster = rows_signChanges.size();
          if ((rowsInBicluster > rowsQuorum) // => rowsInBicluster > 1 => no problem
              ||
              (rowsInBicluster == rowsQuorum && rowsQuorum != 1)
              ||
              (rowsInBicluster == rowsQuorum && rowsQuorum == 1 && numberOfColumns == this.matrix.getNumberOfConditions())) {
            // INSERT BICLUSTER IN THE ARRAY SetOfBiclusters
            int[] rowsIndexes = new int[rowsInBicluster];
            boolean[] signChanges = new boolean[rowsInBicluster];
            Iterator<Row_SignChange> i = rows_signChanges.values().iterator();
            boolean hasTrue = false;
            boolean hasFalse = false;
            int p = 0;
            while (i.hasNext()) {
              Row_SignChange element = i.next();
              rowsIndexes[p] = element.getRow();
              signChanges[p] = element.getSignChange();

              // !!!
              // I WILL ALWAYS CREATE CCC_BICLUSTERS_WITH_SIGN_CHANGES (EVEN WHEN THE BICLUSTER DISCOVER IS JUST A CCC-BICLUSTER)
              // SINCE ITS PVALUE MUST BE COMPUTED USING P(PATTERN)+ P(OPPOSED PATTERN) BECAUSE WE USED BOTH PATTERNS TO PERFORM THE
              // SEARCH FOR PATTERNS IN THE ALGORITHM
              // !!!
              //
              if (signChanges[p] == true) {
                hasTrue = true;
              }
              else {
                hasFalse = true;
              }
              p++;
            }
            boolean hasSignChanges = hasTrue && hasFalse;

            // CREATE OBJECT BICLUSTER
            CCC_Bicluster_SignChanges b = new CCC_Bicluster_SignChanges(this,
                this.getBiclusters().size() + 1, rowsIndexes, columnsIndexes, signChanges);
            if (hasSignChanges) {
              // !!!
              // CHECK IF hasSignChanged IS NOT WRONG
              // FOR INSTANCE IF THE BICLUSTER PATTERN IS NN IT MIGHT HAVE COME FROM NODES WITH SIGN-CHANGES BUT
              // IT IT NOT REALLY A SIGN CHANGE
              // !!!
              if (this.hasReallySignChanges(b.getBiclusterSymbolicMatrix())) {
                this.keepBicluster(b, biclustersWithoutRepetitions);
              }
              else {
                boolean nodeWasMaximalBeforeSignChanges = ((InternalNodeNoErrors_Extended) node).getIsMaximal();
                if (nodeWasMaximalBeforeSignChanges) {
                  //CCC_Bicluster b1 = new CCC_Bicluster(this,this.getBiclusters().size() +1,rowsIndexes,columnsIndexes);
                  //this.keepBicluster(b1, biclustersWithoutRepetitions);
                  this.keepBicluster(b, biclustersWithoutRepetitions);
                }
              }
            }
            else { // CHECK IF NODE WAS MAXIMAL BEFORE SIGN-CHANGES
              boolean wasMaximalBeforeSignChanges = ((InternalNodeNoErrors_Extended) node).getIsMaximal();
              if (wasMaximalBeforeSignChanges) {
                //CCC_Bicluster b = new CCC_Bicluster(this,this.getBiclusters().size() +1, rowsIndexes,columnsIndexes);
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
                                   biclustersWithoutRepetitions, rowsQuorum,
                                   columnsQuorum);
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
          int patternLength = cccBiclustersSuffixTree.computeNumberOfColumns(node);
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
          int rowNumber = cccBiclustersSuffixTree.computeRowNumber( (
              LeafNodeNoErrors) node);
          if (rowNumber > this.matrix.getNumberOfGenes()) { // SIGN CHANGE
            if (rowNumber == 2 * this.matrix.getNumberOfGenes()) { //last gene
              rowsIndexes[0] = this.matrix.getNumberOfGenes();
            }
            else {
              rowsIndexes[0] = rowNumber % this.matrix.getNumberOfGenes();
            }
          }
          else {
            rowsIndexes[0] = rowNumber;
          }

          // INSERT BICLUSTER IN THE ARRAY SetOfBiclusters
          // NO SIGN CHANGE --> CREATE CCC_BICLUSTER ? --> NO!! ALWAYS CREATE CCC_BICLUSTER_WITH_SIGN_CHANGES
          boolean[] signChanges = {false};
          CCC_Bicluster_SignChanges b = new CCC_Bicluster_SignChanges(this,
                                              this.getBiclusters().size() + 1,
                                              rowsIndexes, columnsIndexes, signChanges);
          // CHECK IF BICLUSTER ALREADY EXISTS AND STORE IT IF IT DOESN'T
          int hashCode = b.hashCode();
          if (biclustersWithoutRepetitions.containsKey(hashCode)) {
            CCC_Bicluster_SignChanges object = biclustersWithoutRepetitions.get(hashCode); // get object that has this key
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
  //######################################################################################################################

  /**
   *
   * @param biclusterSymbolicMatrix char[][]
   * @return boolean
   */
  private boolean hasReallySignChanges(char[][] biclusterSymbolicMatrix) {
    for (int j = 0; j < biclusterSymbolicMatrix[0].length; j++) {
      char firstChar = biclusterSymbolicMatrix[0][j];
      for (int i = 1; i < biclusterSymbolicMatrix.length; i++) {
        if (biclusterSymbolicMatrix[i][j] != firstChar) {
          return true;
        }
      }
    }
    return false;
  }

  //######################################################################################################################
  //######################################################################################################################


  /**
   * Inserts the indexes of rows of the maximal ccc-bicluster in its array RowIndexes.
   * @param cccBiclustersSuffixTree SuffixTreeNoErrors
   * @param node NodeInterface
   * @param rows_signChanges TreeMap
   */
  private void getRows(SuffixTreeNoErrors cccBiclustersSuffixTree,
                       NodeInterface node, TreeMap<Integer, Row_SignChange>
      rows_signChanges) {
    if (node instanceof InternalNodeNoErrors_Extended) {
      // GO DOWN
      this.getRows(cccBiclustersSuffixTree,
                   ( (InternalNodeNoErrors_Extended) node).getFirstChild(),
                   rows_signChanges);
      // GO RIGHT
      if (node.getRightSybling() != null) {
        this.getRows(cccBiclustersSuffixTree, node.getRightSybling(),
                     rows_signChanges);
      }
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        boolean signChanges = false;
        //INSERT ROW INDEX
        int rowNumber = cccBiclustersSuffixTree.computeRowNumber((LeafNodeNoErrors) node);
        if (rowNumber > this.matrix.getNumberOfGenes()) { // SIGN CHANGE
          signChanges = true;
          if (rowNumber == 2 * this.matrix.getNumberOfGenes()) { //last gene
            rowNumber = this.matrix.getNumberOfGenes();
          }
          else {
            rowNumber = rowNumber % this.matrix.getNumberOfGenes();
          }
        }

        Row_SignChange entry = rows_signChanges.get(rowNumber);
        if (entry == null) {
          rows_signChanges.put(rowNumber,
                               new Row_SignChange(rowNumber, signChanges));
        }

        // GO RIGHT
        if (node.getRightSybling() != null) {
          this.getRows(cccBiclustersSuffixTree, node.getRightSybling(),
                       rows_signChanges);
        }
      }
    }
  }
}
