package smadeira.biclustering;

import java.io.*;
import java.util.*;

import smadeira.suffixtrees.*;
import smadeira.utils.TaskPercentages;

/**
 * <p>Title: CCC Biclustering Sign Changes and Jumping over Missing Values</p>
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
public class CCC_Biclustering_SignChanges_Jumping_MissingValues
    extends CCC_Biclustering_SignChanges implements IMissingValuesJumped, Cloneable, Serializable {
  public static final long serialVersionUID = 7791982511086485115L;

  /**
   *
   * @param m DiscretizedExpressionMatrix
   */
  public CCC_Biclustering_SignChanges_Jumping_MissingValues(
      DiscretizedExpressionMatrix m) {
    super(m);
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
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
   * MATRIX WITH SIGN-CHANGES
   *
   * _ D U D N
   * U D _ D _
   * _ _ _ D N
   * D _ U D D
   *
   * ALPHABET TRANSFORMATION
   *
   *  _ D2 U3 D4 N5
   * U1 D2  _ D4 _
   *  _  _  _ D4 N5
   * D1  _ U3 D4 D5
   *
   * MATRIX TO CONSTRUCT SUFFIX TREE
   *
   * 852 683 854 785 -10
   * 681 852 -9
   * 854 -9
   * 854 785 -8
   * 851 -7
   * 683 854 855 -7
   * 682 853 684 785 -6
   * 851 682 -9
   * 684 -9
   * 684 785 -8
   * 681 -7
   * 853 784 85 -7
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
    ArrayList<ArrayList<Integer>>
        matrixToConstructSuffixTrees = new ArrayList<ArrayList<Integer>> (0);
    //ORIGINAL SYMBOLIC MATRIX
    for (int i = 0; i < numberOfRows / 2; i++) {
      String c = new String();
      String p = new String();
      String s = new String();
      int element;
      ArrayList<Integer> row = new ArrayList<Integer> (0);
      for (int j = 0; j < this.matrix.getNumberOfConditions(); j++) {
        if (this.matrix.getSymbolicExpressionMatrix()[i][j] !=
            this.matrix.getMissingValue()) {
          c = Integer.toString( (int)this.matrix.getSymbolicExpressionMatrix()[
                               i][j]);
          p = Integer.toString(j + 1);
          s = new String(c + p);
          element = Integer.parseInt(s);
          row.add(element);
          if ( (j < this.matrix.getNumberOfConditions() - 1 &&
                this.matrix.getSymbolicExpressionMatrix()[i][j + 1] ==
                this.matrix.getMissingValue())
              || (j == this.matrix.getNumberOfConditions() - 1)) {
            row.add( - (numberOfRows - i)); // add terminator
            matrixToConstructSuffixTrees.add(row);
            row = new ArrayList<Integer> (0);
          }
        }
      }
    }

    //SYMBOLIC MATRIX WITH SIGN CHANGES
    for (int i = numberOfRows / 2; i < numberOfRows; i++) {
      String c = new String();
      String p = new String();
      String s = new String();
      int element;
      ArrayList<Integer> row = new ArrayList<Integer> (0);
      for (int j = 0; j < this.matrix.getNumberOfConditions(); j++) {
        if (this.matrix.getSymbolicExpressionMatrix()[i - numberOfRows / 2][j] !=
            this.matrix.getMissingValue()) {
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
          row.add(element);
          if ( (j < this.matrix.getNumberOfConditions() - 1
                &&
                this.matrix.getSymbolicExpressionMatrix()[i -
                numberOfRows / 2][j + 1] == this.matrix.getMissingValue())
              ||
              (j == this.matrix.getNumberOfConditions() - 1)) {
            row.add( - (numberOfRows - i)); // add terminator
            matrixToConstructSuffixTrees.add(row);
            row = new ArrayList<Integer> (0);
          }
        }
      }
    }

    int[][] matrixToConstructSuffixTreesAsArray = new int[
        matrixToConstructSuffixTrees.size()][];
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
  //######################################################################################################################

  /**
   *
   * @return SuffixTreeNoErrors
   */
  protected SuffixTreeNoErrors constructSuffixTree() {
    int numColumns = this.matrix.getNumberOfConditions() + 1; // one for the string terminator
    // !!!
    SuffixTreeNoErrors_Extended_Jumping_MissingValues cccBiclustersSuffixTree =
        new SuffixTreeNoErrors_Extended_Jumping_MissingValues(numColumns,
        this.matrix.getNumberOfGenes());
    // COMPUTE MATRIX TO CONSTRUCT SUFFIX TREE

/*    try {
      PrintWriter pw = new PrintWriter("ccc_sign_changes_times_0.txt");
      long time = System.currentTimeMillis();
 */
      int[][] matrixToConstructSuffixTrees = this.
          getMatrixToConstructSuffixTrees();
      this.incrementPercentDone();
/*      pw.println("GET MATRIX TO CONSTRUCT SUFFIX TREE: " +
                 (System.currentTimeMillis() - time));
 */

      // Build generalized suffix tree.

      // ADD ORIGINAL MATRIX
      this.setSubtaskValues(this.matrix.getNumberOfGenes());
      SuffixTreeNoErrors_Extended_STEP1_Builder builder1 = new
          SuffixTreeNoErrors_Extended_STEP1_Builder(cccBiclustersSuffixTree);
//      time = System.currentTimeMillis();
      for (int i = 0; i < this.matrix.getNumberOfGenes(); i++) {
        //      System.out.println("Adding Row = " + i);
        builder1.addToken(matrixToConstructSuffixTrees[i]);
        this.incrementSubtaskPercentDone();
      }
      this.updatePercentDone();
//      pw.println("ADD TOKENS: " + (System.currentTimeMillis() - time));

      //    System.out.println("Adding path length");
      cccBiclustersSuffixTree.addPathLength();
      this.incrementPercentDone();
//      pw.println("ADD PATH LENGTH: " + (System.currentTimeMillis() - time));

      //    System.out.println("Adding number of leaves");
      cccBiclustersSuffixTree.addNumberOfLeaves();
      this.incrementPercentDone();
//      pw.println("ADD NUM LEAVES: " + (System.currentTimeMillis() - time));

      //    System.out.println("Marking non-maximal nodes");
      // !!! BEFORE SIGN-CHANGES
      cccBiclustersSuffixTree.addIsNotLeftMaximal();
      this.incrementPercentDone();
//      pw.println("ADD NON LEFT MAXIMAL: " + (System.currentTimeMillis() - time));

      // ADD ORIGINAL MATRIX WITH SIGN.CHANGES
      this.setSubtaskValues(matrixToConstructSuffixTrees.length-this.matrix.getNumberOfGenes());
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

      //    System.out.println("Adding path length");
      cccBiclustersSuffixTree.addPathLength();
      this.incrementPercentDone();
      //    System.out.println("Adding number of leaves");
//      pw.println("ADD PATH LENGTH: " + (System.currentTimeMillis() - time));

      cccBiclustersSuffixTree.addNumberOfLeaves();
      this.incrementPercentDone();
//      pw.println("ADD NUM LEAVES: " + (System.currentTimeMillis() - time));

      //    System.out.println("Marking non-maximal nodes");
      // !!! AFTER SIGN-CHANGES
      cccBiclustersSuffixTree.addIsNotLeftMaximal_After_Extensions();
      this.incrementPercentDone();
/*      pw.println("ADD NON LEFT MAXIMAL: " + (System.currentTimeMillis() - time));
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
   * @param rowsQuorum int
   * @param columnsQuorum int
   * @throws Exception
   */
  public void computeBiclusters(int rowsQuorum, int columnsQuorum) throws Exception{

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
                                 biclustersWithoutRepetitions, rowsQuorum, columnsQuorum);
  }
}
