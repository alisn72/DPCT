package smadeira.biclustering;

import java.io.*;
import java.util.*;

import javax.swing.text.*;
import javax.swing.text.html.*;

import smadeira.suffixtrees.*;
import smadeira.utils.*;

/**
 * <p>Title: CCC Biclustering with Gene Shifts able to jump over missings</p>
 *
 * <p>Description: CCC-Biclustering algorithm extension for identifying
 *                 CCC Biclusters with gene shifts (scaled patterns).
 *                 Jumps over missing values.</p>
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
public class CCC_Biclustering_GeneShifts_Jumping_MissingValues
    extends CCC_Biclustering_GeneShifts implements IMissingValuesJumped, Cloneable, Serializable {
  public static final long serialVersionUID = -1628119311990009190L;

  //######################################################################################################################
  //######################################################################################################################

  /**
   * this.numberOfShifts = m.getAlphabet().length
   *
   * @param m DiscretizedExpressionMatrix
   * @throws Exception
   */
  public CCC_Biclustering_GeneShifts_Jumping_MissingValues(
      DiscretizedExpressionMatrix m) throws Exception {
    this(m, m.getAlphabet().length);
  }

  /**
   *
   * @param m DiscretizedExpressionMatrix
   * @param numberOfShifts int
   * @throws Exception
   */
  public CCC_Biclustering_GeneShifts_Jumping_MissingValues(
      DiscretizedExpressionMatrix m, int numberOfShifts) throws Exception {
    super(m);
    this.numberOfShifts = numberOfShifts;
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * Constructs a matrix with int values to be used in biclustering algorithms using suffix trees.
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
   * PATTERNS PER ROW:
   * GENE: 1
   * U2	D3	U4	N5
   * GENE: 2
   * D1	 U2
   * U4
   * GENE: 3
   * U4	 N5
   * GENE: 3
   * U1
   * D3	  U4	U5
   *
   * PATTERNS PER ROW SHIFTED 3 SYMBOLS DOWN AND UP:
   * GENE: 1
   * N2	  C3	N4	D5
   * D2   B3	D4	C5
   * C2	  A3	C4	B5
   * V2	  N3	V4	U5
   * W2	  U3	W4	V5
   * X2	  V3	X4	W5
   * GENE: 1
   * C1	 N2
   * B1	 D2
   * A1	 C2
   * N1	 V2
   * U1	 W2
   * V1	 X2
   * N4
   * D4
   * C4
   * V4
   * W4
   * X4
   * GENE: 2
   * N4	 D5
   * D4	C5
   * C4	B5
   * V4	U5
   * W4	V5
   * X4	W5
   * GENE: 3
   * N1
   * D1
   * C1
   * V1
   * W1
   * X1
   * C3	  N4	N5
   * B3   D4	D5
   * A3	  C4	C5
   * N3   V4	V5
   * U3	  W4	W5
   * V3	  X4	X5
   *
   * MATRIX TO CONSTRUCT SUFFIX TREE
   *
   * 852	683	854	785	-8
   * 681	852	-7
   * 854	-7
   * 854	785	-6
   * 851	-5
   * 683	854	855	-5
   * 782	673	784	685	-4
   * 682	663	684	675	-4
   * 672	653	674	665	-4
   * 862	783	864	855	-4
   * 872	853	874	865	-4
   * 882	863	884	875	-4
   * 671	782	-3
   * 661	682	-3
   * 651	672	-3
   * 781	862	-3
   * 851	872	-3
   * 861	882	-3
   * 784	-3
   * 684	-3
   * 674	-3
   * 864	-3
   * 874	-3
   * 884	-3
   * 784	685	-2
   * 684	675	-2
   * 674	665	-2
   * 864	855	-2
   * 874	865	-2
   * 884	875	-2
   * 781	-1
   * 681	-1
   * 671	-1
   * 861	-1
   * 871	-1
   * 881	-1
   * 673	784	785	-1
   * 663	684	685	-1
   * 653	674	675	-1
   * 783	864	865	-1
   * 853	874	875	-1
   * 863	884	885	-1
   *
   * @return int[][]
   * @throws Exception
   */
  protected int[][] getMatrixToConstructSuffixTrees() {
    int numberOfRows = this.matrix.getSymbolicExpressionMatrix().length;
    ArrayList<ArrayList<Integer>>
        matrixToConstructSuffixTrees = new ArrayList<ArrayList<Integer>> (0);
    char[] alphabetAfterGeneShifts = this.getAlphabetAfterGeneShifts();

    ArrayList[] patternsPerGene = new ArrayList[numberOfRows];
    ArrayList[] columnsOfPatternsPerGene = new ArrayList[numberOfRows];

    int terminator = 2 * numberOfRows;

    //INSERT ORIGINAL PATTERNS
    for (int i = 0; i < numberOfRows; i++) {
      String c = new String();
      String p = new String();
      String s = new String();
      int element;
      //!!!
      ArrayList<Integer> row_ints = new ArrayList<Integer> (0);
      ArrayList<Character> row_chars = new ArrayList<Character> (0);
      ArrayList<Integer> row_cols = new ArrayList<Integer> (0);
      patternsPerGene[i] = new ArrayList<ArrayList<Character>> (0);
      columnsOfPatternsPerGene[i] = new ArrayList<ArrayList<Integer>> (0);
      for (int j = 0; j < this.matrix.getNumberOfConditions(); j++) {
        if (this.matrix.getSymbolicExpressionMatrix()[i][j] != this.matrix.getMissingValue()) {
          //!!!
          row_chars.add(this.matrix.getSymbolicExpressionMatrix()[i][j]);
          row_cols.add(j + 1);
          c = Integer.toString( (int)this.matrix.getSymbolicExpressionMatrix()[i][j]);
          p = Integer.toString(j + 1);
          s = new String(c + p);
          element = Integer.parseInt(s);
          row_ints.add(element);
          if ( (j < this.matrix.getNumberOfConditions() - 1 &&
                this.matrix.getSymbolicExpressionMatrix()[i][j + 1] ==
                this.matrix.getMissingValue())
              ||
              (j == this.matrix.getNumberOfConditions() - 1)) {
            row_ints.add( -terminator); // add terminator
            matrixToConstructSuffixTrees.add(row_ints);
            //!!!
            patternsPerGene[i].add(row_chars);
            columnsOfPatternsPerGene[i].add(row_cols);
            row_ints = new ArrayList<Integer> (0);
            row_chars = new ArrayList<Character> (0);
            row_cols = new ArrayList<Integer> (0);
          }
        }
      }
      terminator--;
    }

    /*
        System.out.println("PATTERNS PER ROW:");
        for (int i = 0; i < numberOfRows; i++){
          System.out.println("GENE: " + i);
          for (int p = 0; p < patternsPerGene[i].size(); p++){
            for (int j = 0; j < ((ArrayList<ArrayList<Character>>)patternsPerGene[i].get(p)).size(); j++){
              System.out.print(((ArrayList<ArrayList<Character>>)patternsPerGene[i].get(p)).get(j));
              System.out.print(((ArrayList<ArrayList<Integer>>)columnsOfPatternsPerGene[i].get(p)).get(j) + "\t");
            }
            System.out.println();
          }
        }
     */

    ArrayList<char[]> [] patternsPerGeneShifted = new ArrayList[numberOfRows];
    ArrayList<int[]> [] columnsOfPatternsPerGeneShifted = new ArrayList[
        numberOfRows];
    for (int i = 0; i < numberOfRows; i++) {
      patternsPerGeneShifted[i] = new ArrayList<char[]> (0);
      columnsOfPatternsPerGeneShifted[i] = new ArrayList<int[]> (0);
      for (int p = 0; p < patternsPerGene[i].size(); p++) {
        ArrayList<Character> pattern = (ArrayList<Character>) patternsPerGene[i].
            get(p);
        char[] patternAsArray = new char[pattern.size()];
        ArrayList<Integer> columnsOfPattern = (ArrayList<Integer>)
            columnsOfPatternsPerGene[i].get(p);
        int[] columnsAsArray = new int[pattern.size()];
        for (int j = 0; j < pattern.size(); j++) {
          patternAsArray[j] = pattern.get(j);
          columnsAsArray[j] = columnsOfPattern.get(j);
        }
        ArrayList<char[]>
            shifts = Shift_Pattern.shiftPattern(patternAsArray, alphabetAfterGeneShifts,
                                                this.numberOfShifts);
        patternsPerGeneShifted[i].addAll(shifts);
        for (int s = 0; s < shifts.size(); s++) {
          columnsOfPatternsPerGeneShifted[i].add(columnsAsArray);
        }
      }
    }

    /*
        System.out.println("PATTERNS PER ROW SHIFTED:");
        for (int i = 0; i < numberOfRows; i++){
          System.out.println("GENE: " + i);
          for (int p = 0; p < patternsPerGeneShifted[i].size(); p++){
            for (int j = 0; j < patternsPerGeneShifted[i].get(p).length; j++){
              System.out.print(patternsPerGeneShifted[i].get(p)[j]);
     System.out.print(columnsOfPatternsPerGeneShifted[i].get(p)[j] + "\t");
            }
            System.out.println();
          }
        }
     */

    //INSERT SHIFTED PATTERNS
    for (int g = 0; g < numberOfRows; g++) {
      for (int i = 0; i < patternsPerGeneShifted[g].size(); i++) { // shifts DOWN + UP
        ArrayList<Integer> row = new ArrayList<Integer> (0);
        String c = new String();
        String p = new String();
        String s = new String();
        int element;
        for (int j = 0; j < patternsPerGeneShifted[g].get(i).length; j++) {
          c = Integer.toString( (int) patternsPerGeneShifted[g].get(i)[j]);
          p = Integer.toString(columnsOfPatternsPerGeneShifted[g].get(i)[j]);
          s = new String(c + p);
          element = Integer.parseInt(s);
          row.add(element);
        }
        row.add( -terminator);
        matrixToConstructSuffixTrees.add(row);
      }
      terminator--;
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
    // GENE-SHIFTS
    SuffixTreeNoErrors_Extended_Jumping_MissingValues cccBiclustersSuffixTree = new
        SuffixTreeNoErrors_Extended_Jumping_MissingValues(numColumns,
        this.matrix.getNumberOfGenes());

/*    try {
      PrintWriter pw = new PrintWriter("ccc_gene_shifts_jump_times_0.txt");
      long time = System.currentTimeMillis();
 */
      // COMPUTE MATRIX TO CONSTRUCT SUFFIX TREE
      int[][] matrixToConstructSuffixTrees = this.getMatrixToConstructSuffixTrees();
      this.incrementPercentDone();
//      pw.println("GET MATRIX TO CONSTRUCT SUFFIX TREES: " + (System.currentTimeMillis()-time));

      // INSERT MATRIX IN THE TREE

//      time = System.currentTimeMillis();
      this.setSubtaskValues(this.matrix.getNumberOfGenes());
      //INSERT ORIGINAL MATRIX
      SuffixTreeNoErrors_Extended_STEP1_Builder builder1 = new
          SuffixTreeNoErrors_Extended_STEP1_Builder(cccBiclustersSuffixTree);
      for (int i = 0; i < this.matrix.getNumberOfGenes(); i++) {
        //      System.out.println("Adding Row = " + i);
        builder1.addToken(matrixToConstructSuffixTrees[i]);
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
      // !!! BEFORE GENE-SHIFTS
      cccBiclustersSuffixTree.addIsNotLeftMaximal();
      this.incrementPercentDone();
//      pw.println("ADD NON LEFT MAX: " + (System.currentTimeMillis()-time));

      //INSERT MATRIX WITH GENE-SHIFTS
//      time = System.currentTimeMillis();
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
      // !!! AFTER GENE-SHIFTS
      cccBiclustersSuffixTree.addIsNotLeftMaximal_After_Extensions();
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
  //######################################################################################################################

  /**
   *
   * @param rowsQuorum int
   * @param columnsQuorum int
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
