package smadeira.biclustering;

import java.io.*;
import java.util.*;
import java.util.Iterator;

import javax.swing.text.*;
import javax.swing.text.html.*;

import smadeira.suffixtrees.*;
import javax.swing.JProgressBar;
import smadeira.utils.TaskPercentages;

/**
 * <p>Title: CCC Biclustering</p>
 *
 * <p>Description: CCC-Biclustering algorithm.</p>
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
public class CCC_Biclustering
    extends BiclusteringInDiscretizedMatrix implements Cloneable, Serializable {
  public static final long serialVersionUID = 1681370715135530363L;

  // CONSTRUCTORS

  /**
   *
   * @param m DiscretizedExpressionMatrix
   */
  public CCC_Biclustering(DiscretizedExpressionMatrix m) {
    super(m);
  }

//######################################################################################################################
//############################################ CCC-BICLUSTERING ALGORITHM ##############################################
//######################################################################################################################

  /**
   * <p>
   * Constructs a generalized-suffix tree using the discretized transformed matrix and computes all the information needed
   * in its nodes to identify the maximal ccc-biclusters.
   *<\p><p>
   *  EXAMPLE:
   *<\p><p>
   * DISCRETIZED MATRIX
   *<\p><p>
   * N   U   D   U   N
   * <\p><p>
   * D   U   D   U   D
   * <\p><p>
   * N   N   N   U   N
   * <\p><p>
   * U   U   D   U   U
   *<\p><p>
   * MATRIX TO COMPUTE SUFFIX TREE:
   *<\p><p>
   * Matrix computed as folows:
   * <\p><p>
   * WHEN alphabet = {D, N, U}
   * <\p><p>
   * compute a int value representing the characters {D,N,U} concatenated with
   * a negative vaue computed as folows: - (numGenes-(rowNumber-1))
   * Due to optimization issues the terminators are added in inverse-order.
   * <\p><p>
   * D = 68
   * <\p><p>
   * N = 78
   * <\p><p>
   * U = 85
   * <\p><p>
   * EXAMPLE:
   * <\p><p>
   * 781   852   683   854   785  -4
   * <\p><p>
   * 681   852   683   854   685  -3
   * <\p><p>
   * 781   782   783   854   785  -2
   * <\p><p>
   * 851   852   683   854   855  -1
   * <\p><p>
   * SUFFIX TREE:
   * <\p>
   *<node root>
   *  <leaf label=855 -1  IsMaximal = false numberOfRows = 1 NumberOfColumns = 2  pos=22>
   *  <node label=854  numberOfRows = 4 NumberOfColumns = 1 >
   *    <leaf label=855 -1  IsMaximal = false numberOfRows = 1 NumberOfColumns = 3  pos=21>
   *    <node label=785  numberOfRows = 2 NumberOfColumns = 2 >
   *      <leaf label=-4  IsMaximal = false numberOfRows = 1 NumberOfColumns = 3  pos=3>
   *      <leaf label=-2  IsMaximal = false numberOfRows = 1 NumberOfColumns = 3  pos=15>
   *    </node>
   *    <leaf label=685 -3  IsMaximal = false numberOfRows = 1 NumberOfColumns = 3  pos=9>
   *  </node>
   *  <node label=852 683 854  numberOfRows = 3 NumberOfColumns = 3 >
   *    <leaf label=855 -1  IsMaximal = false numberOfRows = 1 NumberOfColumns = 5  pos=19>
   *    <leaf label=785 -4  IsMaximal = false numberOfRows = 1 NumberOfColumns = 5  pos=1>
   *    <leaf label=685 -3  IsMaximal = false numberOfRows = 1 NumberOfColumns = 5  pos=7>
   *  </node>
   *  <leaf label=851 852 683 854 855 -1  numberOfRows = 1 NumberOfColumns = 6  pos=18, 18>
   *  <node label=785  IsMaximal = false numberOfRows = 2 NumberOfColumns = 1 >
   *    <leaf label=-4  IsMaximal = false numberOfRows = 1 NumberOfColumns = 2  pos=4>
   *    <leaf label=-2  IsMaximal = false numberOfRows = 1 NumberOfColumns = 2  pos=16>
   *  </node>
   *  <leaf label=783 854 785 -2  IsMaximal = false numberOfRows = 1 NumberOfColumns = 4  pos=14>
   *  <leaf label=782 783 854 785 -2  IsMaximal = false numberOfRows = 1 NumberOfColumns = 5  pos=13>
   *  <node label=781  numberOfRows = 2 NumberOfColumns = 1 >
   *    <leaf label=852 683 854 785 -4  numberOfRows = 1 NumberOfColumns = 6  pos=0>
   *    <leaf label=782 783 854 785 -2  numberOfRows = 1 NumberOfColumns = 6  pos=12>
   *  </node>
   *  <leaf label=685 -3  IsMaximal = false numberOfRows = 1 NumberOfColumns = 2  pos=10>
   *  <node label=683 854  IsMaximal = false numberOfRows = 3 NumberOfColumns = 2 >
   *    <leaf label=855 -1  IsMaximal = false numberOfRows = 1 NumberOfColumns = 4  pos=20>
   *    <leaf label=785 -4  IsMaximal = false numberOfRows = 1 NumberOfColumns = 4  pos=2>
   *    <leaf label=685 -3  IsMaximal = false numberOfRows = 1 NumberOfColumns = 4  pos=8>
   *  </node>
   *  <leaf label=681 852 683 854 685 -3  numberOfRows = 1 NumberOfColumns = 6  pos=6, 6>
   *  <leaf label=-4  IsMaximal = false numberOfRows = 1 NumberOfColumns = 1  pos=5>
   *  <leaf label=-3  IsMaximal = false numberOfRows = 1 NumberOfColumns = 1  pos=11>
   *  <leaf label=-2  IsMaximal = false numberOfRows = 1 NumberOfColumns = 1  pos=17>
   *  <leaf label=-1  IsMaximal = false numberOfRows = 1 NumberOfColumns = 1  pos=23>
   *</node>
   *
   * <p>
   * #NODES = 31
   * <\p><p>
   * #INTERNAL NODES = 6
   * <\p><p>
   * #LEAF NODES = 24
   * <\p><p>
   * #MAXIMAL CCC-BICLUSTERS = 8
   * <\p><p>
   * #MAXIMAL NON-TRIVIAL CCC-BICLUSTERS = 2
   * <\p>
   *
   * @return int[][] matrix used to construct the suffix tree.
   */
  protected SuffixTreeNoErrors constructSuffixTree() {
      int numColumns = this.matrix.getNumberOfConditions() + 1; // one for the string terminator
      SuffixTreeNoErrors cccBiclustersSuffixTree = new SuffixTreeNoErrors(
          numColumns);
      SuffixTreeNoErrorsBuilder builder = new SuffixTreeNoErrorsBuilder(
          cccBiclustersSuffixTree);

      // COMPUTE MATRIX TO CONSTRUCT SUFFIX TREE
      int[][] matrixToConstructSuffixTrees = this.getMatrixToConstructSuffixTrees();
      this.incrementPercentDone();

      // Build generalized suffix tree.
      this.setSubtaskValues(matrixToConstructSuffixTrees.length);
      for (int i = 0; i < matrixToConstructSuffixTrees.length; i++) {
        //      System.out.println("Adding Row = " + i);
        builder.addToken(matrixToConstructSuffixTrees[i]);
        this.incrementSubtaskPercentDone();
      }
      this.updatePercentDone();

      //    System.out.println("Adding path length");
      cccBiclustersSuffixTree.addPathLength();
      this.incrementPercentDone();

      //    System.out.println("Adding number of leaves");
      cccBiclustersSuffixTree.addNumberOfLeaves();
      this.incrementPercentDone();

      //    System.out.println("Marking non-maximal nodes");
      cccBiclustersSuffixTree.addIsNotLeftMaximal();
      this.incrementPercentDone();

    return (cccBiclustersSuffixTree);
  }

//######################################################################################################################
//######################################################################################################################

  /**
   * Applyes CCC-Biclustering algorithm and reports all maximal CCC-Biclusters.
   * Trivial CCC-Biclusters are reported provided they are maximal and the parameter filterTrivial = false.
   *
   * Constructs the ccc-biclusters suffix tree and inserts the maximal ccc-biclusters in the array SetOfBiclusters.
   * The computation of the suffix tree and the identification of the maximal biclusters is linear but the creation and
   * insertion of the objects of class CCC_Bicluster in the array is not linear since for each maximal ccc-bicluster
   * identifyed its subtree must be traversed in order to identify the bicluster rows.
   *
   * @param filterTrivial boolean
   * @throws Exception
   */
  public void computeBiclusters(boolean filterTrivial) throws Exception {
    int rowsQuorum;
    int columnsQuorum;
    if (filterTrivial){
      rowsQuorum = 2;
      columnsQuorum = 2;
    }
    else{
      rowsQuorum = 1;
      columnsQuorum = 1;
    }
    this.computeBiclusters(rowsQuorum, columnsQuorum);
  }

  //######################################################################################################################

  /**
   * Applyes CCC-Biclustering algorithm and reports all maximal CCC-Biclusters.
   * Trivial CCC-Biclusters are NOT reported.
   *
   * @throws Exception
   */
  public void computeBiclusters() throws Exception {
    this.computeBiclusters(true);
  }

  //######################################################################################################################

  /**
   * Applyes CCC-Biclustering algorithm and reports all maximal CCC-Biclusters having number of rows >= rowsQuorum and
   * number of columns >= columns quorum.
   *
   * Constructs the ccc-biclusters suffix tree and inserts the maximal ccc-biclusters in the array SetOfBiclusters.
   * The computation of the suffix tree and the identification of the maximal biclusters is linear but the creation and
   * insertion of the objects of class CCC_Bicluster in the array is not linear since for each maximal ccc-bicluster
   * identifyed its subtree must be traversed in order to identify the bicluster rows.
   *
   * @param rowsQuorum int Rows quorum (>= 1) required to retrieved the CCC-Bicluster
   * @param columnsQuorum int Columns (>= 1) quorum to retrieved the CCC-Bicluster
   * @throws Exception
   */
  public void computeBiclusters(int rowsQuorum, int columnsQuorum) throws Exception {

    if (this.matrix.hasMissingValues()) {
      throw new Exception("This algorithm does not support missing values: \n " +
                          "please remove the genes with missing values or " +
                          "fill them before applying the algorithm.");
    }

    if (rowsQuorum < 1 || columnsQuorum < 1 ||
        rowsQuorum > this.matrix.getNumberOfGenes() ||
        columnsQuorum > this.matrix.getNumberOfConditions()) {
      throw new Exception("rowsQuorum in [1, numberOfGenes] and " +
                          "columndQuorum in [1, numberOfConditions]");
    }

    this.setTaskPercentages(new TaskPercentages(this));
    // construct ccc-biclusters suffix-tree - LINEAR
    SuffixTreeNoErrors cccBiclustersSuffixTree = this.constructSuffixTree();

    // create set of objects of class CCC_Bicluster and initialize them with the information of each maximal
    // ccc-bicluster - NOT LINEAR
    // the root is not a bicluster
    NodeInterface firstChild = cccBiclustersSuffixTree.getRoot().getFirstChild();

    this.countAndCreateMaximalBiclusters(cccBiclustersSuffixTree, firstChild, rowsQuorum, columnsQuorum);

//    this.createMaximalBiclusters(cccBiclustersSuffixTree, firstChild, rowsQuorum, columnsQuorum);
  }

  //######################################################################################################################
  //######################################################################################################################

  private void countMaximalBiclusters(SuffixTreeNoErrors
                                         cccBiclustersSuffixTree,
                                         NodeInterface node, int rowsQuorum,
                                         int columnsQuorum,
                                         int[] numberOfBiclusters) {
    if (node == null) {
      return;
    }

    int numberOfColumns = 0;
    if (node instanceof InternalNodeNoErrors) {
      if ( ( (InternalNodeNoErrors) node).getIsMaximal()) {
        //compute number of columns
        int patternLength = cccBiclustersSuffixTree.computeNumberOfColumns(node);
        if (this.matrix.getIsVBTP()) { // **IF MATRIX WAS DISCRETIZED USING VBTP (VARIATIONS BETWEEN TIME-POINTS)**
          numberOfColumns = patternLength + 1;
        }
        else {
          numberOfColumns = patternLength;
        }
        // initialize ARRAY rowIndexes
        TreeSet<Integer> rows = new TreeSet<Integer> ();
        // go down
        this.getRows(cccBiclustersSuffixTree,
                     ( (InternalNodeNoErrors) node).getFirstChild(), rows);
        if (rows.size() >= rowsQuorum && numberOfColumns >= columnsQuorum) {
          numberOfBiclusters[0] = numberOfBiclusters[0] + 1;
        }
      }
      // GO DOWN
      this.countMaximalBiclusters(cccBiclustersSuffixTree,
                                   ( (InternalNodeNoErrors) node).getFirstChild(),
                                   rowsQuorum, columnsQuorum, numberOfBiclusters);
      // GO RIGHT
      if (node.getRightSybling() != null) {
        this.countMaximalBiclusters(cccBiclustersSuffixTree,
                                     node.getRightSybling(), rowsQuorum,
                                     columnsQuorum, numberOfBiclusters);
      }
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        if (( (LeafNodeNoErrors) node).getIsMaximal() && rowsQuorum == 1) { // the bicluster is maximal and trivial
          numberOfBiclusters[0] = numberOfBiclusters[0]+1;
        }
        // GO RIGHT
        if (node.getRightSybling() != null) {
          this.countMaximalBiclusters(cccBiclustersSuffixTree,
                                       node.getRightSybling(), rowsQuorum,
                                       columnsQuorum, numberOfBiclusters);
        }
      }
    }
  }

  protected void countAndCreateMaximalBiclusters(SuffixTreeNoErrors
                                         cccBiclustersSuffixTree,
                                         NodeInterface node,
                                         int rowsQuorum,
                                         int columnsQuorum) throws Exception {

/*    PrintWriter pw = new PrintWriter("ccc_times_1.txt");
    long time = System.currentTimeMillis();
 */
    int[] numberOfBiclusters = new int[1];
    numberOfBiclusters[0] = 0;

    countMaximalBiclusters(cccBiclustersSuffixTree, node, rowsQuorum,
                           columnsQuorum, numberOfBiclusters);
//    pw.println("COUNT MAXIMAL BICLUSTERS: " + (System.currentTimeMillis()-time));

//    time = System.currentTimeMillis();
    this.setSubtaskValues(numberOfBiclusters[0]);
    createMaximalBiclusters(cccBiclustersSuffixTree, node,
                                    rowsQuorum, columnsQuorum,
                                    numberOfBiclusters);
    this.updatePercentDone();
/*    pw.println("CREATE MAXIMAL BICLUSTERS: " + (System.currentTimeMillis()-time));
    pw.close();
 */
}

  protected void createMaximalBiclusters(
      SuffixTreeNoErrors cccBiclustersSuffixTree,
      NodeInterface node, int rowsQuorum,
      int columnsQuorum, int[] numberOfBiclusters) {

    if (node == null) {
      return;
    }

    int firstColumn;
    int numberOfColumns = 0;
    boolean thisNodeIsMaximalBicluster = false;
    if (node instanceof InternalNodeNoErrors) {
      thisNodeIsMaximalBicluster = ( (InternalNodeNoErrors) node).getIsMaximal();
      if (thisNodeIsMaximalBicluster) { // the bicluster is maximal
        // print bicluster identification together with its rows
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
        // initialize ARRAY columnIndexes
        int[] columnsIndexes = new int[numberOfColumns];
        for (int j = 0; j < numberOfColumns; j++) {
          columnsIndexes[j] = firstColumn + j;
        }
        // initialize ARRAY rowIndexes
        TreeSet<Integer> rows = new TreeSet<Integer> ();
        // go down
        this.getRows(cccBiclustersSuffixTree,
                     ( (InternalNodeNoErrors) node).getFirstChild(), rows);
        if (rows.size() >= rowsQuorum && numberOfColumns >= columnsQuorum) {
          // INSERT BICLUSTER IN THE ARRAY SetOfBiclusters
          int[] rowsIndexes = new int[rows.size()];
          Iterator<Integer> i = rows.iterator();
          int p = 0;
          while (i.hasNext()) {
            rowsIndexes[p] = i.next().intValue();
            p++;
          }
          this.setOfBiclusters.add(this.getNumberOfBiclusters(),
                                   new CCC_Bicluster(this,
              this.getBiclusters().size() + 1,
              rowsIndexes, columnsIndexes));
          updateBiclusterPercentDone(numberOfBiclusters);
        }
      }
      // GO DOWN
      this.createMaximalBiclusters(cccBiclustersSuffixTree,
                                   ( (InternalNodeNoErrors) node).getFirstChild(),
                                   rowsQuorum, columnsQuorum, numberOfBiclusters);
      // GO RIGHT
      if (node.getRightSybling() != null) {
        this.createMaximalBiclusters(cccBiclustersSuffixTree,
                                     node.getRightSybling(), rowsQuorum,
                                     columnsQuorum, numberOfBiclusters);
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
          rowsIndexes[0] = cccBiclustersSuffixTree.computeRowNumber((LeafNodeNoErrors) node);
          // INSERT BICLUSTER IN THE ARRAY SetOfBiclusters
          this.setOfBiclusters.add(this.getNumberOfBiclusters(),
                                   new CCC_Bicluster(this, this.getBiclusters().size() + 1, rowsIndexes, columnsIndexes));
          updateBiclusterPercentDone(numberOfBiclusters);
        }
        // GO RIGHT
        if (node.getRightSybling() != null) {
          this.createMaximalBiclusters(cccBiclustersSuffixTree,
                                       node.getRightSybling(), rowsQuorum,
                                       columnsQuorum, numberOfBiclusters);
        }
      }
    }
  }


  /**
   * Traverses the ccc-biclusters suffix tree and for each maximal ccc-bicluster
   * creates an object of class CCC_Bicluster and inserts it in the array SetOfBiclusters.
   *
   * @param cccBiclustersSuffixTree SuffixTreeNoErrors
   * @param node NodeInterface
   * @param rowsQuorum int Rows quorum (>= 1) required to retrieved the CCC-Bicluster
   * @param columnsQuorum int Columns (>= 1) quorum to retrieved the CCC-Bicluster
   */
  protected void createMaximalBiclusters(SuffixTreeNoErrors
                                         cccBiclustersSuffixTree,
                                         NodeInterface node,
                                         int rowsQuorum,
                                         int columnsQuorum){
    if (node == null) {
      return;
    }

    int firstColumn;
    int numberOfColumns = 0;
    boolean thisNodeIsMaximalBicluster = false;
    if (node instanceof InternalNodeNoErrors) {
      thisNodeIsMaximalBicluster = ( (InternalNodeNoErrors) node).getIsMaximal();
      if (thisNodeIsMaximalBicluster) { // the bicluster is maximal
        // print bicluster identification together with its rows
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
        // initialize ARRAY columnIndexes
        int[] columnsIndexes = new int[numberOfColumns];
        for (int j = 0; j < numberOfColumns; j++) {
          columnsIndexes[j] = firstColumn + j;
        }
        // initialize ARRAY rowIndexes
        TreeSet<Integer> rows = new TreeSet<Integer> ();
        // go down
        this.getRows(cccBiclustersSuffixTree,
                     ( (InternalNodeNoErrors) node).getFirstChild(), rows);
        if (rows.size() >= rowsQuorum && numberOfColumns >= columnsQuorum) {
          // INSERT BICLUSTER IN THE ARRAY SetOfBiclusters
          int[] rowsIndexes = new int[rows.size()];
          Iterator<Integer> i = rows.iterator();
          int p = 0;
          while (i.hasNext()) {
            rowsIndexes[p] = i.next().intValue();
            p++;
          }
          this.setOfBiclusters.add(this.getNumberOfBiclusters(),
                                   new CCC_Bicluster(this, this.getBiclusters().size() + 1, rowsIndexes, columnsIndexes));
        }
      }
      // GO DOWN
      this.createMaximalBiclusters(cccBiclustersSuffixTree,
                                   ( (InternalNodeNoErrors) node).getFirstChild(), rowsQuorum, columnsQuorum);
      // GO RIGHT
      if (node.getRightSybling() != null) {
        this.createMaximalBiclusters(cccBiclustersSuffixTree,
                                     node.getRightSybling(), rowsQuorum, columnsQuorum);
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
          rowsIndexes[0] = cccBiclustersSuffixTree.computeRowNumber((LeafNodeNoErrors) node);
          // INSERT BICLUSTER IN THE ARRAY SetOfBiclusters
          this.setOfBiclusters.add(this.getNumberOfBiclusters(),
                                   new CCC_Bicluster(this, this.getBiclusters().size() + 1, rowsIndexes, columnsIndexes));
        }
        // GO RIGHT
        if (node.getRightSybling() != null) {
          this.createMaximalBiclusters(cccBiclustersSuffixTree,
                                       node.getRightSybling(), rowsQuorum, columnsQuorum);
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
    if (node == null) {
      return;
    }
    if (node instanceof InternalNodeNoErrors) {
      // GO DOWN
      this.getRows(cccBiclustersSuffixTree,
                   ( (InternalNodeNoErrors) node).getFirstChild(), rows);
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
   * <p>
   * Computes and prints all maximal ccc-biclusters in linear time.
   *<\p><p>
   * EXAMPLE:
   *<\p><p>
   * DISCRETIZED MATRIX
   *<\p><p>
   * N   U   D   U   N
   * <\p><p>
   * D   U   D   U   D
   * <\p><p>
   * N   N   N   U   N
   * <\p><p>
   * U   U   D   U   U
   * <\p><p>
   * MATRIX TO COMPUTE SUFFIX TREE:
   * <\p><p>
   * 781   852   683   854   785  -4
   * <\p><p>
   * 681   852   683   854   685  -3
   * <\p><p>
   * 781   782   783   854   785  -2
   * <\p><p>
   * 851   852   683   854   855  -1
   * <\p><p>
   * MAXIMAL CCC-BICLUSTERS:
   * <\p>
   *
   * #BICLUSTER	1	4-4	4
   *         4
   *        #BICLUSTER	2	4-5	2
   *                1
   *                3
   *        2
   * #BICLUSTER	3	2-4	3
   *        4
   *        1
   *        2
   * #BICLUSTER	5	1-5	1
   * 4
   * #BICLUSTER	1	1-1	2
   *        #BICLUSTER	5	1-5	1
   *        1
   *        #BICLUSTER	5	1-5	1
   *        3
   * #BICLUSTER	5	1-5	1
   * 2
   */
  public void computeAndPrintMaximalBiclustersInLinearTime() {
    // construct ccc-biclusters suffix-tree
    SuffixTreeNoErrors cccBiclustersSuffixTree = this.constructSuffixTree();
    // print maximal ccc-biclusters in linear time
    SuffixTreeNoErrorsPrinter.printMaximalBiclustersInLinearTime(
        cccBiclustersSuffixTree,
        this.matrix.getIsVBTP(), this.matrix.getNumberOfGenes());
  }

  /**
   * Computes and prints all the maximal ccc-biclusters into a file in linear time.
   *
   * @param filename String
   * @throws IOException
   */
  public void computeAndPrintMaximalBiclustersInLinearTimeToFile(String
      filename) throws IOException {
    // construct ccc-biclusters suffix-tree
    SuffixTreeNoErrors cccBiclustersSuffixTree = this.constructSuffixTree();
    // print maximal ccc-biclusters in linear time to the file passed as parameter
    SuffixTreeNoErrorsPrinter.printMaximalBiclustersInLinearTimeToFile(
        cccBiclustersSuffixTree,
        filename, this.matrix.getIsVBTP(), this.matrix.getNumberOfGenes());
  }

  /**
   * <p>
   * Computes all maximal ccc-biclusters and prints them together with the gene names in linear time.
   * <\p><p>
   * EXAMPLE:
   * <\p><p>
   * DISCRETIZED MATRIX
   * <\p><p>
   * N   U   D   U   N
   * <\p><p>
   * D   U   D   U   D
   * <\p><p>
   * N   N   N   U   N
   * <\p><p>
   * U   U   D   U   U
   * <\p><p>
   * MATRIX TO COMPUTE SUFFIX TREE:
   * <\p><p>
   * 781   852   683   854   785  -4
   * <\p><p>
   * 681   852   683   854   685  -3
   * <\p><p>
   * 781   782   783   854   785  -2
   * <\p><p>
   * 851   852   683   854   855  -1
   * <\p><p>
   * MAXIMAL CCC-BICLUSTERS:
   * <\p>
   *
   * #BICLUSTER	1	4-4	4
   *        4 G4
   *        #BICLUSTER	2	4-5	2
   *                1 G1
   *                3 G3
   *        2 G2
   * #BICLUSTER	3	2-4	3
   *        4 G4
   *        1 G1
   *        2 G2
   * #BICLUSTER	6	1-6	1
   * 4 G4
   * #BICLUSTER	1	1-1	2
   *        #BICLUSTER	6	1-6	1
   *        1 G1
   *        #BICLUSTER	6	1-6	1
   *        3 G3
   * #BICLUSTER	6	1-6	1
   * 2 G2
   */
  public void computeAndPrintMaximalBiclustersWithGeneNamesInLinearTime() {
    // construct ccc-biclusters suffix-tree
    SuffixTreeNoErrors cccBiclustersSuffixTree = this.constructSuffixTree();
    // print maximal ccc-biclusters together with gene names in linear time
    SuffixTreeNoErrorsPrinter.printMaximalBiclustersWithGeneNamesInLinearTime(
        cccBiclustersSuffixTree,
        this.matrix.getGenesNames(), this.matrix.getIsVBTP(),
        this.matrix.getNumberOfGenes());
  }

  /**
   * Computes all maximal ccc-biclusters and prints them to the file passed as parameter together with the gene
   * names in linear time.
   *
   * @param filename String
   * @throws IOException
   */
  public void computeAndPrintMaximalBiclustersWithGeneNamesInLinearTimeToFile(
      String filename) throws IOException {
    // construct ccc-biclusters suffix-tree
    SuffixTreeNoErrors cccBiclustersSuffixTree = this.constructSuffixTree();
    // print maximal ccc-biclusters together with gene names in linear time to the file
    SuffixTreeNoErrorsPrinter.
        printMaximalBiclustersWithGeneNamesInLinearTimeToFile(
        cccBiclustersSuffixTree,
        filename, this.matrix.getGenesNames(), this.matrix.getIsVBTP(),
        this.matrix.getNumberOfGenes());
  }

  /**
   * <p>
   * Computes all maximal ccc-biclusters and prints them together with the gene names. The ccc-biclusters are computed
   * in linear time but the reporting is not linear.
   * <\p><p>
   * EXAMPLE:
   * <\p><p>
   * DISCRETIZED MATRIX
   * <\p><p>
   * N   U   D   U   N
   * <\p><p>
   * D   U   D   U   D
   * <\p><p>
   * N   N   N   U   N
   * <\p><p>
   * U   U   D   U   U
   * <\p><p>
   * MATRIX TO COMPUTE SUFFIX TREE:
   * <\p><p>
   * 781   852   683   854   785  -4
   * <\p><p>
   * 681   852   683   854   685  -3
   * <\p><p>
   * 781   782   783   854   785  -2
   * <\p><p>
   * 851   852   683   854   855  -1
   * <\p><p>
   * MAXIMAL CCC-BICLUSTERS:
   * <\p>
   *
   * #BICLUSTER	1	4-4
   * G4
   * G1
   * G3
   * G2
   * #BICLUSTER	2	4-5	2
   * G1
   * G3
   * #BICLUSTER	3	2-4	3
   * G4
   * G1
   * G2
   * #BICLUSTER	5	1-5	1
   * G4
   * #BICLUSTER	1	1-1	2
   * G1
   * G3
   * #BICLUSTER	5	1-5	1
   * G1
   * #BICLUSTER	5	1-5	1
   * G3
   * #BICLUSTER	5	1-5	1
   * G2
   */
  public void computeAndPrintMaximalBiclustersWithGeneNames() {
    // construct ccc-biclusters suffix-tree
    SuffixTreeNoErrors cccBiclustersSuffixTree = this.constructSuffixTree();
    // print maximal ccc-biclusters together with gene names
    SuffixTreeNoErrorsPrinter.printMaximalBiclustersWithGeneNames(
        cccBiclustersSuffixTree,
        this.matrix.getGenesNames(), this.matrix.getIsVBTP(),
        this.matrix.getNumberOfGenes());
  }

  /**
   * Computes all maximal ccc-biclusters and prints them together with the gene names to the file passed as parameter.
   * The ccc-biclusters are computed in linear time but the reporting is not linear.
   *
   * @param filename String
   * @throws IOException
   */
  public void computeAndPrintMaximalBiclustersWithGeneNamesToFile(String
      filename) throws IOException {
    // construct ccc-biclusters suffix-tree
    SuffixTreeNoErrors cccBiclustersSuffixTree = this.constructSuffixTree();
    // print maximal ccc-biclusters together with gene names to the file
    SuffixTreeNoErrorsPrinter.printMaximalBiclustersWithGeneNamesToFile(
        cccBiclustersSuffixTree, filename, this.matrix.
        getGenesNames(), this.matrix.getIsVBTP(), this.matrix.getNumberOfGenes());
  }

  /**
   * Computes all maximal ccc-biclusters and prints them together with the gene names and its pattern.
   * The ccc-biclusters are computed in linear time but the reporting is not linear.
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
   * MATRIX TO COMPUTE SUFFIX TREE:
   *
   * 781   852   683   854   785  -4
   * 681   852   683   854   685  -3
   * 781   782   783   854   785  -2
   * 851   852   683   854   855  -1
   *
   * MAXIMAL CCC-BICLUSTERS WITH GENE NAMES AND BICLUSTER PATTERN
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
   *#BICLUSTER	5	1-5	1
   * G2	DUDUD
   *
   * @throws IOException
   */
  public void computeAndPrintMaximalBiclustersWithGeneNamesAndPattern() {
    // construct ccc-biclusters suffix-tree
    SuffixTreeNoErrors cccBiclustersSuffixTree = this.constructSuffixTree();
    // print maximal ccc-biclusters together with gene names to the file
    SuffixTreeNoErrorsPrinter.printMaximalBiclustersWithGeneNamesAndPattern(
        cccBiclustersSuffixTree,
        this.matrix.getGenesNames(), this.matrix.getIsVBTP(),
        this.matrix.getNumberOfGenes(), this.getMatrixToConstructSuffixTrees());
  }

  /**
   * Computes all maximal ccc-biclusters and prints them together with the gene names and its pattern
   * to the file passed as parameter.
   * The ccc-biclusters are computed in linear time but the reporting is not linear.
   *
   * @param filename String
   * @throws IOException
   */
  public void computeAndPrintMaximalBiclustersWithGeneNamesAndPatternToFile(
      String filename) throws IOException {
    // construct ccc-biclusters suffix-tree
    SuffixTreeNoErrors cccBiclustersSuffixTree = this.constructSuffixTree();
    SuffixTreeNoErrorsPrinter.
        printMaximalBiclustersWithGeneNamesAndPatternToFile(
        cccBiclustersSuffixTree, filename,
        this.matrix.getGenesNames(), this.matrix.getIsVBTP(),
        this.matrix.getNumberOfGenes(), this.getMatrixToConstructSuffixTrees());
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

} // END CLASS
