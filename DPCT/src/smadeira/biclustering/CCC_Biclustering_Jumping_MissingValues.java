package smadeira.biclustering;

import java.io.*;
import java.util.*;

import javax.swing.text.*;
import javax.swing.text.html.*;

import smadeira.suffixtrees.*;
import smadeira.utils.TaskPercentages;

/**
 * <p>Title: CCC Biclustering jumping over missing values</p>
 *
 * <p>Description: CCC-Biclustering algorithm extension for jumping
 *                 over missing values.</p>
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
public class CCC_Biclustering_Jumping_MissingValues
    extends CCC_Biclustering implements IMissingValuesJumped, Cloneable, Serializable {
  public static final long serialVersionUID = -7380709657244785364L;

  /**
   *
   * @param m DiscretizedExpressionMatrix
   * @throws Exception
   */
  public CCC_Biclustering_Jumping_MissingValues(DiscretizedExpressionMatrix m) throws
      Exception {
    super(m);
  }

//######################################################################################################################
//############################################ CCC-BICLUSTERING ALGORITHM ##############################################
//######################################################################################################################

  /**
   * Constructs a matrix with int values to be used in biclustering algorithms using suffix trees.
   * In this the usual string composed of character in the alphabet is replaced by an array of ints
   * where each int is inserted into the suffix tree instead of the usual char.
   * This matrix is obtained from matrixWithSymbolsAndColumns by transforming each of its
   * elements to ints (2 bytes) as folows:
   * compute a int value representing one of the characters {D,N,U} concatenated with its column position.
   *
   * SPECIAL ATTENTION IS GIVEN TO MISSING VALUES BY SPLITING THE ORIGINAL STRING WITH MISSING INTO SEVERAL
   * STRINGS WITHOUT MISSINGS
   *
   * A TERMINATOR IS ADDED TO EACH STRING
   * SUBSTRINGS OF THE SAME STRING SHARE THE SAME STRING (NOT PROBLEMATIC IN UKKONEN'S ALGORITHM SINCE THE
   * VALUES TO INSERT HAVE THE COLUMN CONCATENATED)
   *
   * FOR INTANCE:
   *
   * int Values for the characters:
   * D = 68
   * N = 78
   * U = 85
   *
   * ORIGINAL MATRIX
   *
   * _ U D U N
   * D U _ U _
   * _ _ _ U N
   * U _ D U U
   *
   * ALPHABET TRANSFORMATION
   *
   *  _ U2 D3 U4 N5
   * D1 U2  _ U4 _
   *  _  _  _ U4 N5
   * U1  _ D3 U4 U5
   *
   * is converted to
   *
   * 852 683 854 785 -4
   * 681 852 -3
   * 854 -3
   * 854 785 -2
   * 851 -1
   * 683 854 855 -1
   *
   * @return int[][]
   * @throws Exception
   */
  protected int[][] getMatrixToConstructSuffixTrees() {
    int numberOfRows = this.matrix.getSymbolicExpressionMatrix().length;
    int numberOfColumns = this.matrix.getSymbolicExpressionMatrix()[0].length;
    ArrayList<ArrayList<Integer>> matrixToConstructSuffixTrees = new ArrayList<ArrayList<Integer>> (0);
    for (int i = 0; i < numberOfRows; i++) {
      String c = new String();
      String p = new String();
      String s = new String();
      int element;
      ArrayList<Integer> row = new ArrayList<Integer> (0);
      for (int j = 0; j < numberOfColumns; j++) {
        if (this.matrix.getSymbolicExpressionMatrix()[i][j] != this.matrix.getMissingValue()) {
          c = Integer.toString( (int)this.matrix.getSymbolicExpressionMatrix()[i][j]);
          p = Integer.toString(j + 1);
          s = new String(c + p);
          element = Integer.parseInt(s);
          row.add(element);
          if ( (j < numberOfColumns - 1
               &&
               this.matrix.getSymbolicExpressionMatrix()[i][j + 1] == this.matrix.getMissingValue())
              ||
              (j == this.matrix.getNumberOfConditions() - 1)) {
            row.add( - (numberOfRows - i)); // add terminator
            matrixToConstructSuffixTrees.add(row);
            row = new ArrayList<Integer> (0);
          }
        }
      }
    }

    int[][] matrixToConstructSuffixTreesAsArray = new int[matrixToConstructSuffixTrees.size()][];
    for (int i = 0; i < matrixToConstructSuffixTrees.size(); i++) {
      int[] row = new int[matrixToConstructSuffixTrees.get(i).size()];
      for (int j = 0; j < matrixToConstructSuffixTrees.get(i).size(); j++) {
        row[j] = matrixToConstructSuffixTrees.get(i).get(j);
      }
      matrixToConstructSuffixTreesAsArray[i] = row;
    }
    return matrixToConstructSuffixTreesAsArray;
  }

  //######################################################################################################################

  /**
   *
   * @return SuffixTreeNoErrors
   */
  protected SuffixTreeNoErrors constructSuffixTree() {
    SuffixTreeNoErrors_Jumping_MissingValues cccBiclustersSuffixTree =
        new SuffixTreeNoErrors_Jumping_MissingValues(this.matrix.getNumberOfGenes());
    SuffixTreeNoErrorsBuilder builder = new SuffixTreeNoErrorsBuilder(
        cccBiclustersSuffixTree);

/*    try {
      PrintWriter pw = new PrintWriter("ccc_jump_times_0.txt");
      long time = System.currentTimeMillis();
 */
      // COMPUTE MATRIX TO CONSTRUCT SUFFIX TREE
      int[][] matrixToConstructSuffixTrees = this.getMatrixToConstructSuffixTrees();
      this.incrementPercentDone();
//      pw.println("GET MATRIX TO CONSTRUCT SUFFIX TREE: " + (System.currentTimeMillis()-time));

//      time = System.currentTimeMillis();
      this.setSubtaskValues(matrixToConstructSuffixTrees.length);
      // Build generalized suffix tree.
      for (int i = 0; i < matrixToConstructSuffixTrees.length; i++) {
        //      System.out.println("Adding Row = " + i);
        builder.addToken(matrixToConstructSuffixTrees[i]);
        this.incrementSubtaskPercentDone();
      }
      this.updatePercentDone();
//      pw.println("ADD TOKENS: " + (System.currentTimeMillis()-time));

//      time = System.currentTimeMillis();
      //    System.out.println("Adding path length");
      cccBiclustersSuffixTree.addPathLength();
      this.incrementPercentDone();
//      pw.println("ADD PATH LENGTH: " + (System.currentTimeMillis()-time));

//      time = System.currentTimeMillis();
      //    System.out.println("Adding number of leaves");
      cccBiclustersSuffixTree.addNumberOfLeaves();
      this.incrementPercentDone();
//      pw.println("ADD NUM LEAVES: " + (System.currentTimeMillis()-time));

//      time = System.currentTimeMillis();
      //    System.out.println("Marking non-maximal nodes");
      cccBiclustersSuffixTree.addIsNotLeftMaximal();
      this.incrementPercentDone();
/*      pw.println("ADD NON LEFT MAX: " + (System.currentTimeMillis()-time));
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
      if (rowsQuorum < 1 || columnsQuorum < 1 || rowsQuorum > this.matrix.getNumberOfGenes() || columnsQuorum > this.matrix.getNumberOfConditions()) {
        throw new Exception("rowsQuorum in [1, numberOfGenes] and columndQuorum in [1, numberOfConditions]");
      }

      this.setTaskPercentages(new TaskPercentages(this));
      // construct ccc-biclusters suffix-tree - LINEAR
      SuffixTreeNoErrors cccBiclustersSuffixTree = this.constructSuffixTree();

      // create set of objects of class CCC_Bicluster and initialize them with the information of each maximal
      // ccc-bicluster - NOT LINEAR
      // the root is not a bicluster
      NodeInterface firstChild = cccBiclustersSuffixTree.getRoot().getFirstChild();
/*      this.createMaximalBiclusters(cccBiclustersSuffixTree, firstChild,
       rowsQuorum, columnsQuorum);
 */
this.countAndCreateMaximalBiclusters(cccBiclustersSuffixTree, firstChild,
       rowsQuorum, columnsQuorum);
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
} // END CLASS
