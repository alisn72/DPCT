package smadeira.biclustering;

import java.io.*;
import java.util.*;
import java.util.Iterator;

import javax.swing.text.*;
import javax.swing.text.html.*;

import smadeira.suffixtrees.*;
import smadeira.tries.*;
import smadeira.utils.*;

/**
 * <p>Title: e-CCC Biclustering</p>
 *
 * <p>Description: e-CCC Biclustering algorithm. Identifies contiguous
 *                 column coherent biclusters with approximate patterns.</p>
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
public class E_CCC_Biclustering
    extends BiclusteringInDiscretizedMatrix implements Cloneable, Serializable {
  public static final long serialVersionUID = 4594548478895617944L;

  // FIELDS

  /**
   * Number of errors allowed.
   */
  protected int maxNumberOfErrors;

//######################################################################################################################
//######################################################################################################################

  /**
   *
   * @return int
   */
  public int getMaxNumberOfErrors() {
    return this.maxNumberOfErrors;
  }

  /**
   *
   * @param m DiscretizedExpressionMatrix
   * @param maxNumberOfErrors int
   * @throws Exception
   */
  public E_CCC_Biclustering(DiscretizedExpressionMatrix m,
                            int maxNumberOfErrors) throws Exception {
    super(m);
    if (maxNumberOfErrors < 0 ||
        maxNumberOfErrors > this.matrix.getNumberOfConditions()) {
      throw new Exception(
          "maxNumberOfErrors in [0,  this.matrix.getNumberOfConditions()]");
    }
    this.maxNumberOfErrors = maxNumberOfErrors;
  }

//######################################################################################################################
//################################ PRIVATE METHODS USED IN SPELLMODELS #################################################
//######################################################################################################################

  /**
   * Computes alpbabet that will be used in the construction of the suffix trees in lexicographic order
   * (The terminators are not stored).
   * Alphabet = {symbolDownExpression, symbolNoExpression, symbolUpExpression}
   * IF Alphabet = {D, N, U}
   * Alphabet with column positions:
   * {D1,...,DnumConditions,N1,...,NnumConditions,U1,...,UnumConditions}
   * int Values for the characters:
   * D = 68
   * N = 78
   * U = 85
   * Alphabet converted to ints:
   * {681,..., 68numConditions, 781,..., 78numConditions, 851, ... , 85numConditions}
   *
   * @return int[]
   */
  protected int[] computeAlphabetLexicographicOrder() {
    int alphabetLength = this.matrix.getAlphabet().length;
    int[] alphabet = new int[alphabetLength * this.matrix.getNumberOfConditions()];
    int positionInAlphabet = 0;
    for (int i = 0; i < alphabetLength; i++) {
      int intSymbol = (int)this.matrix.getAlphabet()[i];
      for (int j = 1; j <= this.matrix.getNumberOfConditions(); j++) {
        String c = new String();
        String p = new String();
        String s = new String();
        c = Integer.toString(intSymbol);
        p = Integer.toString(j);
        s = new String(c + p);
        alphabet[positionInAlphabet] = (int) Integer.parseInt(s);
        positionInAlphabet++;
      }
    }
    return (alphabet);
  }

//######################################################################################################################

  /**
   * Computes the possible alphabet for a given level.
   * For example,
   * If this.matrix.getAlphabet() = {D,N,U}
   * and level = 1
   * alphabet = {D1,N1,U1}
   * @return int[]
   * @param level int
   * @return int[]
   */
  protected int[] computeAlphabetLexicographicOrderAtGivenLevel(int level) {
    int[] alphabet = new int[this.getAlphabet().length];
    int positionInAlphabet = 0;
    for (int i = 0; i < this.getAlphabet().length; i++) {
      int intSymbol = (int) matrix.getAlphabet()[i];
      String c = new String();
      String p = new String();
      String s = new String();
      c = Integer.toString(intSymbol);
      p = Integer.toString(level);
      s = new String(c + p);
      alphabet[positionInAlphabet] = (int) Integer.parseInt(s);
      positionInAlphabet++;
    }
    return (alphabet);
  }

//######################################################################################################################
//######################################################################################################################

  /**
   * Suffix tree that will be used by the method spellmodels to find out the models that correspond to right maximal
   * ccc-biclusters with errors.
   *
   * @deprecated
   * @param matrixToConstructSuffixTree int[][]
   * @return SuffixTreeWithErrorsRightMaximal
   */
  protected SuffixTreeWithErrorsRightMaximal constructSuffixTreeRightMaximalBiclusters_old(int[][] matrixToConstructSuffixTree) {

    int numRows = matrixToConstructSuffixTree.length;
    int numColumns = matrixToConstructSuffixTree[0].length;
    SuffixTreeWithErrorsRightMaximal suffixTreeRightMaximalBiclusters = new
        SuffixTreeWithErrorsRightMaximal(numColumns);
    SuffixTreeWithErrorsRightMaximalBuilder builder = new SuffixTreeWithErrorsRightMaximalBuilder(suffixTreeRightMaximalBiclusters);
    // Build generalized suffix tree.
    for (int i = 0; i < numRows; i++) {
      builder.addToken(matrixToConstructSuffixTree[i]);
    }

    // add path length
    suffixTreeRightMaximalBiclusters.addPathLength();
    //add number of leaves
    suffixTreeRightMaximalBiclusters.addNumberOfLeaves();
    return (suffixTreeRightMaximalBiclusters);
  }

  //######################################################################################################################

  /**
   * Suffix tree that will be used by the method spellmodels to find out the models that correspond to right maximal
   * ccc-biclusters with errors.
   *
   * @return SuffixTreeWithErrorsRightMaximal
   */
  protected SuffixTreeWithErrorsRightMaximal constructSuffixTreeRightMaximalBiclusters() {
     //##########################################################################
    // BUILD GENERALIZED SUFFIX TREE
    //##########################################################################
    SuffixTreeWithErrorsRightMaximal suffixTreeRightMaximalBiclusters = null;

/*    try {
      PrintWriter pw = new PrintWriter("e_ccc_0_constructST.txt");
      long time = System.currentTimeMillis();
      /*      suffixTreeRightMaximalBiclusters =
          this.buildSuffixTreeRightMaximalBiclusters(this.
          getMatrixToConstructSuffixTrees());
       */
//      this.getProgressBar().setString("GET MATRIX TO CONSTRUCT SUFFIX TREE");
      int[][] matrixToConstructSuffixTree = this.getMatrixToConstructSuffixTrees();
      this.incrementPercentDone();
//      pw.println("GET MATRIX TO CONSTRUCT SUFFIX TREE: " + (System.currentTimeMillis() - time));

/*      time = System.currentTimeMillis();
      this.getProgressBar().setString("ADD TOKENS");
*/
      suffixTreeRightMaximalBiclusters =
          this.buildSuffixTreeRightMaximalBiclusters(matrixToConstructSuffixTree);
//      pw.println("ADD TOKENS: " + (System.currentTimeMillis() - time));


      //##########################################
      // ADD INFORMATION TO THE NODES IN THE TREE
      //##########################################

//      time = System.currentTimeMillis();
      //IF maxNumberOfErrors >= 0 --> ADD NUMBER OF LEAVES
//      this.getProgressBar().setString("ADD NUMBER OF LEAVES");
      suffixTreeRightMaximalBiclusters.addNumberOfLeaves();
      this.incrementPercentDone();
//      pw.println("ADD NUM LEAVES: " + (System.currentTimeMillis() - time));

/*      time = System.currentTimeMillis();
      this.getProgressBar().setString("ADD COLOR ARRAY");
 */
      //IF maxNumberOfErrors > 0 --> ADD COLOR ARRAY
      if (this.maxNumberOfErrors > 0) {
        suffixTreeRightMaximalBiclusters.addColorArray();
      }
      this.incrementPercentDone();
/*      pw.println("ADD COLOR ARRAY: " + (System.currentTimeMillis() - time));
      pw.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
 */

    return (suffixTreeRightMaximalBiclusters);
  }

  //######################################################################################################################

  /**
   * Builds the generalized suffix tree using the set of int[] passed as parameter.
   *
   * @param matrixToConstructSuffixTree int[][]
   * @return SuffixTreeWithErrorsRightMaximal
   */
  protected SuffixTreeWithErrorsRightMaximal buildSuffixTreeRightMaximalBiclusters(int[][] matrixToConstructSuffixTree) {
    SuffixTreeWithErrorsRightMaximal suffixTreeRightMaximalBiclusters = null;
      int numRows = matrixToConstructSuffixTree.length;
        this.setSubtaskValues(numRows);
      int numColumns = matrixToConstructSuffixTree[0].length;
      suffixTreeRightMaximalBiclusters = new
          SuffixTreeWithErrorsRightMaximal(numColumns);
      SuffixTreeWithErrorsRightMaximalBuilder builder = new
          SuffixTreeWithErrorsRightMaximalBuilder(suffixTreeRightMaximalBiclusters);
      // Build generalized suffix tree.
      for (int i = 0; i < numRows; i++) {
        builder.addToken(matrixToConstructSuffixTree[i]);
          this.incrementSubtaskPercentDone();
      }
        this.updatePercentDone();
    return suffixTreeRightMaximalBiclusters;
  }

//######################################################################################################################
//######################################################################################################################

  /**
   * Computes set of genes in a node occurrences list (TO USE WHEN maxNumberOfErrors > 0)
   *
   * @param Occ_m NodeOccurrenceList
   *
   * @param suffixTreeRightMaximalBiclusters SuffixTreeWithErrorsRightMaximal
   * (NEEDED WHEN maxNumberOfErrors = 0 !!!)
   *
   * @return  BitSet
   */
  protected BitSet computeGenesInNodeOccurrences(ArrayList<NodeOccurrence>
      Occ_m, SuffixTreeWithErrorsRightMaximal suffixTreeRightMaximalBiclusters) {
    BitSet genes = new BitSet();
    if (Occ_m != null) {
      //_______________________________________________________
      // If maxNumberOfErrors = 0 => Occ_m has only one occurrence
      if (this.maxNumberOfErrors == 0) {
        genes = suffixTreeRightMaximalBiclusters.computeGenes(Occ_m.get(0).getX());
      }
      //________________
      // IF maxNumberOfErrors > 0
      else {
        for (int i = 0; i < Occ_m.size(); i++) {
          NodeInterface node = Occ_m.get(i).getX();
          if (node instanceof LeafNodeWithErrorsRightMaximal) {
            genes.set( ( (LeafNodeWithErrorsRightMaximal) node).getColor(), true);
          }
          else {
            if (node instanceof InternalNodeWithErrorsRightMaximal) {
              genes.or( ( (InternalNodeWithErrorsRightMaximal) node).getColors());
            }
          }
        }
      }
    }
    return genes;
  }

//######################################################################################################################
//######################################################################################################################

  /**
   * Computes number of genes in a node occurrences list.
   *
   * @param genes BitSet
   * @return int
   */
  protected int computeNumberOfGenesInNodeOccurrences(BitSet genes) {
    if (genes == null) {
      return 0;
    }
    else {
      return (genes.cardinality());
    }
  }

//######################################################################################################################
//######################################################################################################################

  /**
   * Checks if the father of the current model corresponds to a right-maximal bicluster.
   * IF the number of genes in the node occurrences of current model is equal to the
   * number of genes in the node occurrences of the father of the current model, the father of the current node does
   * not correspond to a right-maximal bicluster. As such, the object corresponding to it is deleted from the list
   * this.modelsAndOccurrences and the model is stored in the list this.deletedModels so that the algorithm does not
   * try to remove it again when analysing other of its children models.
   *
   * @param modelsAndOccurrences ArrayList
   * @param genesOcc_m BitSet
   * @param father_m Model
   * @param numberOfGenes_in_Occ_father_m int
   *
   * @deprecated
   */
  protected void checkRightMaximality_old(ArrayList<ModelOccurrences> modelsAndOccurrences,
      BitSet genesOcc_m, Model father_m, int numberOfGenes_in_Occ_father_m) {
    if (father_m.getMotif().length != 0) { // father_m is not the root
      int numberOfGenes_in_Occ_m = this.computeNumberOfGenesInNodeOccurrences(genesOcc_m);
      if (numberOfGenes_in_Occ_m == numberOfGenes_in_Occ_father_m) {
        // THE MODEL CAN BE EXTENDED TO THE RIGHT WITHOUT LOOSING GENES
        //THROW AWAY MODEL FATHER - IT DOES NOT CORRESPOND TO RIGHT MAXIMAL BICLUSTERS
        /*
                System.out.print("DELETING MODEL: ");
                for (int i = 0; i < father_m.getMotif().length; i++)
                  System.out.print(father_m.getMotif()[i] + "\t");
                System.out.println(" --> NOT RIGHT MAXIMAL");
         */
        int position = modelsAndOccurrences.size() - 1;
        boolean modelFound = false;
        while (position >= 0 && modelFound == false) {
          ModelOccurrences currentModelAndOccurrences = modelsAndOccurrences.
              get(position);
          Model model = currentModelAndOccurrences.getModel();
          if (model.equals(father_m)) {
            modelsAndOccurrences.remove(position);
            modelFound = true;
          }
          position--;
        }
      }
    }
  }

//######################################################################################################################
//###################################### SPELLMODELS ###################################################################
//######################################################################################################################


  /**
   *
   * Keeps a model and their node occurrences in the list this.modelsAndOccurrences.
   * In the end of spellModels this list will contain all models corresponding to all right-maximal biclusters.
   * THROWS AWAY THE FATHER OF THE CURRENT MODEL (ALREADY KEPT)
   * WHEN IT DOES NOT CORRESPOND TO A RIGHT-MAXIMAL BICLUSTER
   *
   * @param modelsAndOccurrences ArrayList
   * @param suffixTreeRightMaximalBiclusters SuffixTreeWithErrorsRightMaximal
   * @param m Model
   * @param Occ_m ArrayList
   * @param father_m Model
   * @param numberOfGenes_in_Occ_father_m int
   * @param rowsQuorum int
   * @param columnsQuorum int
   */
  protected void keepModel(ArrayList<ModelOccurrences> modelsAndOccurrences, SuffixTreeWithErrorsRightMaximal suffixTreeRightMaximalBiclusters,
      Model m, ArrayList<NodeOccurrence> Occ_m, Model father_m, int numberOfGenes_in_Occ_father_m, int rowsQuorum, int columnsQuorum) {

    // THE MODEL CORRESPONDING TO THE ROOT MUST NEVER BE KEPT.
    //if (m.getMotif().length == 0)  => "DESCENDING FROM THE ROOT!"
    if (m.getMotif().length > 0) {
      ModelOccurrences modelKept = null;
      // IF this.matrix.getIsVBTP() = true
      // => m.getMotif().length = L REPRESENT A BICLUSTER WITH L+1 COLUMNS
      // => BICLUSTER IS NEVER TRIVIAL BECAUSE IT HAS ALWAYS AT LEAST 2 COLUMNS
      if ((m.getMotif().length >= columnsQuorum && !this.matrix.getIsVBTP())
          ||
          (m.getMotif().length + 1 >= columnsQuorum && this.matrix.getIsVBTP())) {
        // COMPUTE GENES IN MODEL OCCURRENCES
        BitSet genesOcc_m = this.computeGenesInNodeOccurrences(Occ_m, suffixTreeRightMaximalBiclusters);
        int numberOfGenesOcc_m = this.computeNumberOfGenesInNodeOccurrences(genesOcc_m);

        // CHECK ROWS QUORUM
        if (numberOfGenesOcc_m >= rowsQuorum) {
          // !!!
          // !!! VER SE SE PODE GUARDAR ESTE MAXIMO A MEDIDA QUE SE INSEREM AS OCORRENCIAS PARA EVITAR O CICLO!!!
          // !!!
          // compute maximum number of errors in the model occurences (USEFUL LATER TO CREATE THE BICLUSTER WITH THIS INFO)
          int maxErrorsInOcc_m = 0;
          for (int i = 0; i < Occ_m.size(); i++) {
            if (Occ_m.get(i).getXErr() > maxErrorsInOcc_m) {
              maxErrorsInOcc_m = Occ_m.get(i).getXErr();
            }
          }
          modelKept = new ModelOccurrences(m, genesOcc_m, numberOfGenesOcc_m, maxErrorsInOcc_m);
          modelsAndOccurrences.add(modelKept);

//!!!
//          System.out.print("#BIC="+modelsAndOccurrences.size() + "\t");

          //##########################################################################
          // THROW AWAY THE FATHER OF THE CURRENT MODEL (IF IT WAS ALREADY KEPT)
          // WHEN IT DOES NOT CORRESPOND TO A RIGHT-MAXIMAL BICLUSTER
          //##########################################################################
          if ( (father_m.getMotif().length >= columnsQuorum && !this.matrix.getIsVBTP())
              ||
              (father_m.getMotif().length + 1 >= columnsQuorum && this.matrix.getIsVBTP())) {
            this.checkRightMaximality(modelsAndOccurrences, numberOfGenesOcc_m, father_m, numberOfGenes_in_Occ_father_m);
          }
        }
      }
    }
  }

  //######################################################################################################################

  /**
   * Checks if the father of the current model corresponds to a right-maximal bicluster.
   * IF the number of genes in the node occurrences of current model is equal to the
   * number of genes in the node occurrences of the father of the current model, the father of the current node does
   * not correspond to a right-maximal bicluster. As such, the object corresponding to it is deleted from the list
   * this.modelsAndOccurrences and the model is stored in the list this.deletedModels so that the algorithm does not
   * try to remove it again when analysing other of its children models.
   *
   * @param modelsAndOccurrences ArrayList
   * @param numberOfGenesOcc_m int
   * @param father_m Model
   * @param numberOfGenes_in_Occ_father_m int
   */
  protected void checkRightMaximality(ArrayList<ModelOccurrences> modelsAndOccurrences,
      int numberOfGenesOcc_m, Model father_m, int numberOfGenes_in_Occ_father_m) {
    if (father_m.getMotif().length != 0) { // father_m is not the root
      if (numberOfGenesOcc_m == numberOfGenes_in_Occ_father_m) {
        // THE MODEL CAN BE EXTENDED TO THE RIGHT WITHOUT LOOSING GENES
        //THROW AWAY MODEL FATHER - IT DOES NOT CORRESPOND TO RIGHT MAXIMAL BICLUSTERS
        ListIterator<ModelOccurrences> i = modelsAndOccurrences.listIterator(modelsAndOccurrences.size());
        boolean modelFound = false;
        while (i.hasPrevious() && !modelFound){
          ModelOccurrences current = i.previous();
          if(current.getModel().equals(father_m)){
            modelFound = true;
            i.remove();
          }
        }
      }
    }
  }

  //######################################################################################################################

  /**
   *
   * @param maxGenes int
   * @param minGenes int
   * @param Colors_m_alpha BitSet
   * @param rowsQuorum int
   * @return boolean
   */
  protected boolean modelHasQuorum(int maxGenes, int minGenes,
                                   BitSet Colors_m_alpha, int rowsQuorum) {

    if (maxGenes < rowsQuorum) {
      return false; // the model has no quorum already NO HOPE to extend it with quorum
    }
    else { // maxGenes >= quorum
      if (this.maxNumberOfErrors == 0) { // NO ERRORS ALLOWED
        return true;
      }
      else { // ERRORS ALLOWED
        if (minGenes >= rowsQuorum) {
          return true;
        }
        else {
          if (Colors_m_alpha.cardinality() >= rowsQuorum) {
            return true;
          }
          else {
            return false;
          }
        }
      }
    }
  }

//######################################################################################################################

  /**
   * Computes all models and occurrences corresponding to right-maximal biclusters and stores them in the list
   * this.modelsAndOccurrences.
   *
   * @param modelsAndOccurrences ArrayList
   * @param suffixTreeRightMaximalBiclusters SuffixTreeWithErrorsRightMaximal
   * @param length_m int
   * @param m Model
   * @param Occ_m ArrayList
   * @param Ext_m ModelExtensionSet
   * @param father_m Model
   * @param numberOfGenes_in_Occ_father_m int
   * @param rowsQuorum int
   * @param columnsQuorum int
   */
  protected void spellModels(ArrayList<ModelOccurrences> modelsAndOccurrences,
      SuffixTreeWithErrorsRightMaximal suffixTreeRightMaximalBiclusters,
      int length_m,
      Model m,
      ArrayList<NodeOccurrence> Occ_m,
      ModelExtensionSet Ext_m,
      Model father_m,
      int numberOfGenes_in_Occ_father_m,
      int rowsQuorum,
      int columnsQuorum, int[] numberOfModels) {

    //##########################################################################
    // KEEP CURRENT MODEL (CALL KEEPMODEL)
    //##########################################################################
    this.keepModel(modelsAndOccurrences, suffixTreeRightMaximalBiclusters, m,
                   Occ_m,
                   father_m, numberOfGenes_in_Occ_father_m,
                   rowsQuorum, columnsQuorum);
    this.incrementSubtaskPercentDone();

    //##########################################################################
    // length_m < numberOfConditions ==> EXTENSION IS POSSIBLE
    //##########################################################################

    if (length_m < this.matrix.getNumberOfConditions()) {
      //##########################################################################
      // FOR each symbol alpha in Ext_m
      //##########################################################################
      Iterator<Integer> extensions = Ext_m.iterator();
      while (extensions.hasNext()) {

        //initialize maxGenes
        int maxGenes = 0;

        // minGenes = infinit = higher than it could ever be
        int minGenes = Integer.MAX_VALUE;

        int alpha = extensions.next();

        // test if alpha is a string terminator
        boolean alphaIsAterminator = false;
        if (alpha < 0) {
          alphaIsAterminator = true;
        }

        // IF ALPHA is not a string terminator
        if (!alphaIsAterminator) {

          // IF maxNumberOfErrors = 0
          BitSet Colors_m_alpha = null;

          // IF maxNumberOfErrors > 0
          // Colors_m_alpha = no colors
          if (this.maxNumberOfErrors > 0) {
            Colors_m_alpha = new BitSet();
            //Creates a new bit set. All bits are initially false.
          }

          // Ext_m_alpha = empty set
          ModelExtensionSet Ext_m_alpha = new ModelExtensionSet();

          // Occ_m_alpha = empty list
          ArrayList<NodeOccurrence> Occ_m_alpha = new ArrayList<NodeOccurrence> (0);

          //##########################################################################
          // FOR each node occurrence (x, x_err, p) in Occ_m
          //##########################################################################

          ListIterator<NodeOccurrence> occurrences = Occ_m.listIterator();
          while (occurrences.hasNext()) {
            NodeOccurrence occurrence = occurrences.next();

            //##########################################################################
            // IF we are at a node x
            //##########################################################################
            if (occurrence.getP() == 0) {
              //##########################################################################
              // TRY EXTENSION WITHOUT ERRORS
              // IF there is a branch b leaving node x with a label starting with alpha
              //##########################################################################
              //##########################################################################
              // EXTEND MODEL
              // maxGene and minGene should be passed by reference since they are updated inside the method
              // Since Integer objects are always passed by value
              // extend is returning the updated values inside the array int[] maxGenes_minGenes
              int[] maxGenes_minGenes = new int[2];
              maxGenes_minGenes = this.extendFromNodeWithoutErrors(
                  suffixTreeRightMaximalBiclusters, occurrence, alpha,
                  Occ_m_alpha, Colors_m_alpha,
                  Ext_m_alpha, maxGenes, minGenes);
              maxGenes = maxGenes_minGenes[0];
              minGenes = maxGenes_minGenes[1];
              //##########################################################################

              //##########################################################################
              // IF THE MODEL CAN BE EXTENDED WITH ERRORS
              // occurrence.getXErr() < e
              //##########################################################################
              if (occurrence.getXErr() < this.maxNumberOfErrors) {
                //##########################################################################
                // EXTEND MODEL
                // maxGene and minGene should be passed by reference since they are updated inside the method
                // Since Integer objects are always passed by values
                // extend is returning the updated values inside the array int[] maxGenes_minGenes
                maxGenes_minGenes = new int[2];
                maxGenes_minGenes = this.extendFromNodeWithErrors(
                    suffixTreeRightMaximalBiclusters, occurrence, alpha,
                    Occ_m_alpha, Colors_m_alpha,
                    Ext_m_alpha, maxGenes, minGenes);
                maxGenes = maxGenes_minGenes[0];
                minGenes = maxGenes_minGenes[1];
                //##########################################################################
              } // END IF the model can be extended with errors
            } // END IF we are at a node x
            else {
              //##########################################################################
              // WE are inside a branch b between nodes father(x) and x at position p > 0
              // occurrence.getP() > 0
              //##########################################################################

              //##########################################################################
              // TRY EXTENSION WITHOUT ERRORS
              // IF the symbol in branch b in position p+1 is alpha
              //##########################################################################
              //##########################################################################
              // EXTEND MODEL
              // maxGene and minGene should be passed by reference since they are updated inside the method
              // Since Integer objects are always passed by values
              // extend is returning the updated values inside the array int[] maxGenes_minGenes
              int[] maxGenes_minGenes = new int[2];
              maxGenes_minGenes = this.extendFromBranchWithoutErrors(
                  suffixTreeRightMaximalBiclusters, occurrence, alpha,
                  Occ_m_alpha, Colors_m_alpha,
                  Ext_m_alpha, maxGenes, minGenes);
              maxGenes = maxGenes_minGenes[0];
              minGenes = maxGenes_minGenes[1];

              //##########################################################################
              //IF THE MODEL CAN BE EXTENDED WITH ERRORS
              // occurrence.getXErr() < this.maxNumberOfErrors
              //##########################################################################
              if (occurrence.getXErr() < this.maxNumberOfErrors) {
                //##########################################################################
                // IF the symbol in branch b in position p+1 is NOT alpha
                //##########################################################################
                //##########################################################################
                // EXTEND MODEL
                // maxGene and minGene should be passed by reference since they are updated inside the method
                // Since Integer objects are always passed by values
                // extend is returning the updated values inside the array int[] maxGenes_minGenes
                maxGenes_minGenes = this.extendFromBranchWithErrors(
                    suffixTreeRightMaximalBiclusters, occurrence, alpha,
                    Occ_m_alpha, Colors_m_alpha,
                    Ext_m_alpha, maxGenes, minGenes);
                maxGenes = maxGenes_minGenes[0];
                minGenes = maxGenes_minGenes[1];
                //##########################################################################
              } // END IF THE MODEL CAN BE EXTENDED WITH ERRORS

            } // END we are inside branch b between nodes father(x) and x at position p > 0

          } // END FOR each pair node occurrence (x, x_err, p) in Occ_m

          //##########################################################################
          // maxGenes < q
          //##########################################################################

          Model m_alpha = new Model();

          if (this.modelHasQuorum(maxGenes, minGenes, Colors_m_alpha, rowsQuorum)) { // LETS KEEP IT AND TRY TO EXTEND IT

            // IF minGenes >= q || number of bits at 1 in colors_m_alpha >= q

            m_alpha = new Model(m.getMotif().length + 1);
            m_alpha.insert(m.getMotif());
            m_alpha.append(alpha);

            Model father_m_alpha = new Model(m.getMotif().length); // = m
            father_m_alpha.insert(m.getMotif());

            // numberOfGenes_in_Occ_father_m_alpha = numberOfGenes_in_Occ_m
            BitSet genesOcc_m = this.computeGenesInNodeOccurrences(Occ_m,suffixTreeRightMaximalBiclusters);
            int numberOfGenes_in_Occ_m = this.computeNumberOfGenesInNodeOccurrences(genesOcc_m);

            this.spellModels(modelsAndOccurrences,
                             suffixTreeRightMaximalBiclusters, length_m + 1,
                             m_alpha,
                             Occ_m_alpha, Ext_m_alpha,
                             father_m_alpha,
                             numberOfGenes_in_Occ_m, rowsQuorum, columnsQuorum, numberOfModels);

          } // END IF minGenes >= q || number of bits at 1 in colors_m_alpha >= q

        } // END IF ALPHA is not a terminator

      } // END FOR each symbol alpha in Ext_alpha

    } // END IF extensionIsPossible

  } // END SPELL MODELS

  //#########################################################################################################
  //#########################################################################################################

  /**
   *
   * @param suffixTreeRightMaximalBiclusters SuffixTreeWithErrorsRightMaximal
   * @param occurrence NodeOccurrence
   * @param alpha int
   * @param Occ_m_alpha ArrayList
   * @param Colors_m_alpha BitSet
   * @param Ext_m_alpha ModelExtensionSet
   * @param maxGenes Integer
   * @param minGenes Integer
   * @param branchToExamine NodeInterface
   * @param numErrors int
   * @return int[] Updated values of maxGene and minGene
   */
  protected int[] extend(SuffixTreeWithErrorsRightMaximal
                         suffixTreeRightMaximalBiclusters,
                         NodeOccurrence occurrence,
                         int alpha,
                         ArrayList<NodeOccurrence> Occ_m_alpha,
      BitSet Colors_m_alpha,
      ModelExtensionSet Ext_m_alpha,
      int maxGenes,
      int minGenes,
      NodeInterface branchToExamine,
      int numErrors) {

    //##########################################################################
    // IF the extension will reach another node x'
    // branchToExamine.getLength() == occurrence.getP() + 1
    //##########################################################################
    if (branchToExamine.getLength() == occurrence.getP() + 1) {
      //UPDATE Occ_m_alpha
      Occ_m_alpha.add(new NodeOccurrence(branchToExamine,
                                         occurrence.getXErr() + numErrors, 0));

      int numberOfLeaves = branchToExamine.getNumberOfLeaves();

      //update maxGenes
      maxGenes = maxGenes + numberOfLeaves; // numberOfLeaves(x') = CSS_x'

      if (numberOfLeaves < minGenes) {
        // update minGenes
        minGenes = numberOfLeaves;
      }

      if (this.maxNumberOfErrors > 0) {
        //UPDATE Colors_m_alpha
        if (branchToExamine instanceof InternalNodeWithErrorsRightMaximal) {
          // if node is an InternalNodeWithErrorsRightMaximal
          Colors_m_alpha.or( ( (InternalNodeWithErrorsRightMaximal)
                              branchToExamine).getColors());
        }
        else {
          // if node is a LeafNodeWithErrorsRightMaximal
          if (branchToExamine instanceof LeafNodeWithErrorsRightMaximal) {
            Colors_m_alpha.set( ( (LeafNodeWithErrorsRightMaximal)
                                 branchToExamine).getColor(), true);
          }
        }
      }

      //UPDATE Ext_m_alpha
      if (occurrence.getXErr() == this.maxNumberOfErrors) { // NO MORE ERRORS ALLOWED
        if (branchToExamine instanceof InternalNodeWithErrorsRightMaximal) { // cannot extend if it is a leaf
          NodeInterface node = ( (InternalNodeWithErrorsRightMaximal) branchToExamine).getFirstChild();
          do {
            int leftIndex = node.getLeftIndex();
            int firstCharacter = suffixTreeRightMaximalBiclusters.getSubArrayInt(leftIndex, 1)[0];

            if (firstCharacter > 0) { // is not a terminator
              Ext_m_alpha.add(firstCharacter);
            }
            node = node.getRightSybling();
          }
          while (node != null);
        }
      }
      else { // ERRORS ARE STILL ALLOWED
        String alphaAsString = Integer.toString(alpha);
        int alphaConditionNumber = Integer.parseInt(alphaAsString.substring(2));
        if (alphaConditionNumber + 1 <= this.matrix.getNumberOfConditions()) {
          Ext_m_alpha.addAll(this.computeAlphabetLexicographicOrderAtGivenLevel(alphaConditionNumber + 1));
        }
      }
    } // END IF the extension will reach another node x'
    else {
      //##########################################################################
      // ELSE we will stay inside node x at position p+1 after the extension
      //##########################################################################

      // UPDATE Occ_m_alpha
      Occ_m_alpha.add(new NodeOccurrence(branchToExamine,
                                         occurrence.getXErr() + numErrors,
                                         occurrence.getP() + 1));

      int numberOfLeaves = branchToExamine.getNumberOfLeaves();

      // update maxGenes
      maxGenes = maxGenes + numberOfLeaves; // numberOfLeaves(x) = CSS_x

      if (numberOfLeaves < minGenes) {
        //update minGenes
        minGenes = numberOfLeaves;
      }

      if (this.maxNumberOfErrors > 0) {
        //UPDATE Colors_m_alpha
        if (branchToExamine instanceof InternalNodeWithErrorsRightMaximal) {
          // if node is an InternalNodeWithErrorsRightMaximal
          Colors_m_alpha.or( ( (InternalNodeWithErrorsRightMaximal)
                              branchToExamine).getColors());
        }
        else {
          if (branchToExamine instanceof LeafNodeWithErrorsRightMaximal) {
            // if Node is a LeafNodeWithErrorsRightMaximal
            Colors_m_alpha.set( ( (LeafNodeWithErrorsRightMaximal) branchToExamine).getColor(), true);
          }
        }
      }

      // UPDATE Ext_m_alpha
      if (occurrence.getXErr() == this.maxNumberOfErrors) { // NO MORE ERRORS ALLOWED
        int leftIndex = branchToExamine.getLeftIndex(); // left index of branch b
        int characterAtPositionPplusTwo =
            suffixTreeRightMaximalBiclusters.getSubArrayInt(leftIndex,
            occurrence.getP() + 2)[occurrence.getP() + 1]; // positions start at 0
        if (characterAtPositionPplusTwo > 0) { // is not a terminator
          // is not a terminator
          Ext_m_alpha.add(characterAtPositionPplusTwo);
        }
      }
      else { // ERRORS ARE STILL ALLOWED
        String alphaAsString = Integer.toString(alpha);
        int alphaConditionNumber = Integer.parseInt(alphaAsString.substring(2));
        if (alphaConditionNumber + 1 <= this.matrix.getNumberOfConditions()) {
          Ext_m_alpha.addAll(this.computeAlphabetLexicographicOrderAtGivenLevel(alphaConditionNumber + 1));
        }
      }
    } // END ELSE we will stay inside node x at position p+1 after the extension

    int[] maxGenes_minGenes = new int[2];
    maxGenes_minGenes[0] = maxGenes;
    maxGenes_minGenes[1] = minGenes;
    return maxGenes_minGenes;
  }

  //######################################################################################################################

  /**
   *
   * @param suffixTreeRightMaximalBiclusters SuffixTreeWithErrorsRightMaximal
   * @param occurrence NodeOccurrence
   * @param alpha int
   * @param Occ_m_alpha ArrayList
   * @param Colors_m_alpha BitSet
   * @param Ext_m_alpha ModelExtensionSet
   * @param maxGenes int
   * @param minGenes int
   * @return int[]
   */
  protected int[] extendFromNodeWithoutErrors(
      SuffixTreeWithErrorsRightMaximal suffixTreeRightMaximalBiclusters,
      NodeOccurrence occurrence,
      int alpha,
      ArrayList<NodeOccurrence> Occ_m_alpha,
      BitSet Colors_m_alpha,
      ModelExtensionSet Ext_m_alpha,
      int maxGenes,
      int minGenes) {

    int[] maxGenes_minGenes = new int[2];
    maxGenes_minGenes[0] = maxGenes;
    maxGenes_minGenes[1] = minGenes;

    //##########################################################################
    // IF there is a branch b leaving node x with a label starting with alpha
    //##########################################################################
    boolean branchStartingWithAlphaFound = false;
    NodeInterface branchToExamine = new InternalNodeWithErrorsRightMaximal();
    // there can only be a branch leaving x if x is an internal node
    if (occurrence.getX() instanceof
        InternalNodeWithErrorsRightMaximal) {
      branchToExamine = ( (InternalNodeWithErrorsRightMaximal)
                         occurrence.getX()).getFirstChild(); // x.firstSon = first son of node x = x'
      do {
        int leftIndex = branchToExamine.getLeftIndex(); // left index of branch b between x and x'
        int firstCharacter = suffixTreeRightMaximalBiclusters.
            getSubArrayInt(leftIndex, 1)[0];

        if (firstCharacter == alpha) {
          branchStartingWithAlphaFound = true;
        }
        else {
          branchToExamine = branchToExamine.getRightSybling();
        }
      }
      while (branchStartingWithAlphaFound == false && branchToExamine != null);
    }

    if (branchStartingWithAlphaFound == true) {
      // branchToExamine = x'
      //##########################################################################
      // EXTEND MODEL
      // maxGene and minGene should be passed by reference since they are updated inside the method
      // Since Integer objects are always passed by values
      // extend is returning the updated values inside the array int[] maxGenes_minGenes
      maxGenes_minGenes = this.extend(
          suffixTreeRightMaximalBiclusters, occurrence, alpha,
          Occ_m_alpha, Colors_m_alpha,
          Ext_m_alpha, maxGenes, minGenes, branchToExamine, 0);
      //##########################################################################
    }
    return maxGenes_minGenes;
  }

  //######################################################################################################################

  /**
   *
   * @param suffixTreeRightMaximalBiclusters SuffixTreeWithErrorsRightMaximal
   * @param occurrence NodeOccurrence
   * @param alpha int
   * @param Occ_m_alpha ArrayList
   * @param Colors_m_alpha BitSet
   * @param Ext_m_alpha ModelExtensionSet
   * @param maxGenes int
   * @param minGenes int
   * @return int[]
   */
  protected int[] extendFromNodeWithErrors(SuffixTreeWithErrorsRightMaximal
                                             suffixTreeRightMaximalBiclusters,
                                             NodeOccurrence occurrence,
                                             int alpha,
                                             ArrayList<NodeOccurrence>
      Occ_m_alpha,
      BitSet Colors_m_alpha,
      ModelExtensionSet Ext_m_alpha,
      int maxGenes,
      int minGenes) {

    int[] maxGenes_minGenes = new int[2];
    maxGenes_minGenes[0] = maxGenes;
    maxGenes_minGenes[1] = minGenes;

    //##########################################################################
    // FOR each branch b leaving node x except the one labelled alpha if it exists
    //##########################################################################
    NodeInterface branchToExamine = new InternalNodeWithErrorsRightMaximal();
    // there can only be a branch leaving x if x is an internal node
    if (occurrence.getX() instanceof InternalNodeWithErrorsRightMaximal) {
      branchToExamine = ( (InternalNodeWithErrorsRightMaximal) occurrence.getX()).getFirstChild(); // x.firstSon = first son of node x = x'
      do {
        // branch b leaving node x except the one labelled alpha if it exists
        if (this.extendFromNodeWithErrorsIsPossible(suffixTreeRightMaximalBiclusters, occurrence, branchToExamine, alpha)) {
          //##########################################################################
          // EXTEND MODEL
          // maxGene and minGene should be passed by reference since they are updated inside the method
          // Since Integer objects are always passed by values
          // extend is returning the updated values inside the array int[] maxGenes_minGenes
          maxGenes_minGenes = this.extend(
              suffixTreeRightMaximalBiclusters, occurrence, alpha,
              Occ_m_alpha, Colors_m_alpha,
              Ext_m_alpha, maxGenes_minGenes[0], maxGenes_minGenes[1],
              branchToExamine, 1);
          //##########################################################################
        } // END IF

        // go to next branch
        branchToExamine = branchToExamine.getRightSybling();
      }
      while (branchToExamine != null); // END FOR each branch b leaving node x except the one labelled alpha if it exists
    } // END there can only be a branch leaving x if x is an internal node

    return maxGenes_minGenes;
  }

  //######################################################################################################################

  /**
   *
   * @param suffixTreeRightMaximalBiclusters SuffixTreeWithErrorsRightMaximal
   * @param occurrence NodeOccurrence
   * @param branchToExamine NodeInterface
   * @param alpha int
   * @return boolean
   */
  protected boolean extendFromNodeWithErrorsIsPossible(SuffixTreeWithErrorsRightMaximal suffixTreeRightMaximalBiclusters,
      NodeOccurrence occurrence, NodeInterface branchToExamine, int alpha){

    int leftIndex = branchToExamine.getLeftIndex(); // left index of branch b between x and x'
    int firstCharacter = suffixTreeRightMaximalBiclusters.getSubArrayInt(leftIndex, 1)[0];

    // IF CHARACTER IS A TERMINATOR -> EXTENSION IS NOT POSSIBLE
    if (firstCharacter < 0) {
      return false;
    }

    // WHEN OCCURRENCE IS THE ROOT
    // TEST IF CHARACTER IS AT THE SAME LEVEL AS ALPHA
    if (occurrence.getX() instanceof InternalNodeWithErrorsRightMaximal) {
      if (occurrence.getX().getPathLength() == 0) { // X = root
        int columnNumberFirstCharacter = Integer.parseInt(Integer.toString(firstCharacter).substring(2));
        int columnNumberAlpha = Integer.parseInt(Integer.toString(alpha).substring(2));
        if (columnNumberFirstCharacter != columnNumberAlpha) {
          return false;
        }
      }
    }

    // IF CHARACTER != ALPHA -> EXTENSION WITH ERRORS IS POSSIBLE
    return (firstCharacter != alpha);
  }


  //######################################################################################################################
  //######################################################################################################################

  /**
   *
   * @param suffixTreeRightMaximalBiclusters SuffixTreeWithErrorsRightMaximal
   * @param occurrence NodeOccurrence
   * @param alpha int
   * @param Occ_m_alpha ArrayList
   * @param Colors_m_alpha BitSet
   * @param Ext_m_alpha ModelExtensionSet
   * @param maxGenes int
   * @param minGenes int
   * @return int[]
   */
  protected int[] extendFromBranchWithoutErrors(SuffixTreeWithErrorsRightMaximal
                                              suffixTreeRightMaximalBiclusters,
                                              NodeOccurrence occurrence,
                                              int alpha,
                                              ArrayList<NodeOccurrence>
      Occ_m_alpha,
      BitSet Colors_m_alpha,
      ModelExtensionSet Ext_m_alpha,
      int maxGenes,
      int minGenes) {

    int[] maxGenes_minGenes = new int[2];
    maxGenes_minGenes[0] = maxGenes;
    maxGenes_minGenes[1] = minGenes;

    //##########################################################################
    // IF the symbol in branch b in position p+1 is alpha
    //##########################################################################
    NodeInterface branchToExamine = occurrence.getX();
    int leftIndex = branchToExamine.getLeftIndex(); // left index of branch b between x and x'
    int characterAtPositionPplusOne =
        suffixTreeRightMaximalBiclusters.getSubArrayInt(leftIndex,
        occurrence.getP() + 1)[occurrence.getP()];

    if (characterAtPositionPplusOne == alpha) {
      //##########################################################################
      // EXTEND MODEL
      // maxGene and minGene should be passed by reference since they are updated inside the method
      // Since Integer objects are always passed by values
      // extend is returning the updated values inside the array int[] maxGenes_minGenes
      maxGenes_minGenes = this.extend(
          suffixTreeRightMaximalBiclusters, occurrence, alpha,
          Occ_m_alpha, Colors_m_alpha,
          Ext_m_alpha, maxGenes, minGenes, branchToExamine, 0);
      maxGenes = maxGenes_minGenes[0];
      minGenes = maxGenes_minGenes[1];
      //##########################################################################
    } // END IF the symbol in branch b in position p+1 is alpha
    return maxGenes_minGenes;
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   *
   * @param suffixTreeRightMaximalBiclusters SuffixTreeWithErrorsRightMaximal
   * @param occurrence NodeOccurrence
   * @param alpha int
   * @param Occ_m_alpha ArrayList
   * @param Colors_m_alpha BitSet
   * @param Ext_m_alpha ModelExtensionSet
   * @param maxGenes int
   * @param minGenes int
   * @return int[]
   */
  protected int[] extendFromBranchWithErrors(SuffixTreeWithErrorsRightMaximal
                                           suffixTreeRightMaximalBiclusters,
                                           NodeOccurrence occurrence,
                                           int alpha,
                                           ArrayList<NodeOccurrence>
      Occ_m_alpha,
      BitSet Colors_m_alpha,
      ModelExtensionSet Ext_m_alpha,
      int maxGenes,
      int minGenes) {

    int[] maxGenes_minGenes = new int[2];
    maxGenes_minGenes[0] = maxGenes;
    maxGenes_minGenes[1] = minGenes;

    //##########################################################################
    // IF the symbol in branch b in position p+1 is NOT alpha
    //##########################################################################
    NodeInterface branchToExamine = occurrence.getX();

    if (this.extendFromBranchWithErrorsIsPossible(suffixTreeRightMaximalBiclusters, occurrence, branchToExamine, alpha)) {
      //##########################################################################
      //IF the extension will reach another node x'
      //branchToExamine.getLength() == occurrence.getP() + 1
      //##########################################################################

      //##########################################################################
      // EXTEND MODEL
      // maxGene and minGene should be passed by reference since they are updated inside the method
      // Since Integer objects are always passed by values
      // extend is returning the updated values inside the array int[] maxGenes_minGenes
      maxGenes_minGenes = new int[2];
      maxGenes_minGenes = this.extend(
          suffixTreeRightMaximalBiclusters, occurrence, alpha,
          Occ_m_alpha, Colors_m_alpha,
          Ext_m_alpha, maxGenes, minGenes, branchToExamine, 1);
      maxGenes = maxGenes_minGenes[0];
      minGenes = maxGenes_minGenes[1];
      //##########################################################################
    }
    return maxGenes_minGenes;
  }

  //######################################################################################################################

  /**
   *
   * @param suffixTreeRightMaximalBiclusters SuffixTreeWithErrorsRightMaximal
   * @param occurrence NodeOccurrence
   * @param branchToExamine NodeInterface
   * @param alpha int
   * @return boolean
   */
  protected boolean extendFromBranchWithErrorsIsPossible(SuffixTreeWithErrorsRightMaximal suffixTreeRightMaximalBiclusters,
      NodeOccurrence occurrence, NodeInterface branchToExamine, int alpha){
    int leftIndex = branchToExamine.getLeftIndex(); // left index of branch b between x and x'
    int characterAtPositionPplusOne = suffixTreeRightMaximalBiclusters.getSubArrayInt(leftIndex, occurrence.getP() + 1)[occurrence.getP()];
    // IF CHARACTER IS A TERMINATOR -> EXTENSION IS NOT POSSIBLE
    if (characterAtPositionPplusOne < 0) {
      return false;
    }
    // IF CHARACTER != ALPHA -> EXTENSION WITH ERRORS IS POSSIBLE
    return (characterAtPositionPplusOne != alpha);
  }

  //#####################################################################################################################
  //#####################################################################################################################
  //#####################################################################################################################

  /**
   * Deletes from the list this.modelsAndOccurrences the models that do not correspond to *LEFT* maximal biclusters.
   * Uses a trie where the *reversed* models are inserted. Nodes corresponding to NON-LEFT MAXIMAL PATTERNS are marked.
   *
   * O((|R||C|^(2+e)|sigma|^e))
   *
   * @param modelsAndOccurrences ArrayList<ModelOccurrences>
   */
  protected void deleteModelsNotCorrespondingToLeftMaximalBiclusters(ArrayList<ModelOccurrences> modelsAndOccurrences) {

    //CREATE TRIE
    TrieMaximalPattern trieLeftMaximalPattern = new TrieMaximalPattern();
    // INSERT *INVERTED* MODELS IN THE TRIE
    // STORE REFERENCE TO NODE IN THE TRIE CORRESPONDING TO THE INSERTED MODEL
    // SO THAT THE MODELS STORED IN NODES MARKED AS NON-LEFT-MAXIMAL CAN BE DELETED

    ArrayList<TrieMaximalPatternNode>
        referenceToNodesCorrespondingToModels = new ArrayList<TrieMaximalPatternNode> (modelsAndOccurrences.size());
    this.setSubtaskValues(modelsAndOccurrences.size()*2);
    for (int i = 0; i < modelsAndOccurrences.size(); i++) {
      TrieMaximalPatternNode nodeCorrespondingToModel = trieLeftMaximalPattern.
          addInvertedModel(modelsAndOccurrences.get(i).getModel().getMotif(), // ADD INVERTED MODEL !!!
                           modelsAndOccurrences.get(i).getNumberOfGenesInOccurrences());
      referenceToNodesCorrespondingToModels.add(nodeCorrespondingToModel);
      this.incrementSubtaskPercentDone();
    }

    // MARK NODES THAT CORRESPOND TO NON MAXIMAL MODELS
    trieLeftMaximalPattern.markNodesAsNonMaximalPattern();

    // REMOVE FROM modelsAndOccurrences THE MODELS WHOSE NODES ARE MARKED AS NON MAXIMAL MODELS
    Iterator<ModelOccurrences> i = modelsAndOccurrences.iterator();
    int p = 0; // corresponding position in referenceToNodesCorrespondingToModels
    while (i.hasNext()) {
      ModelOccurrences currentModelAndOccurrences = i.next();
      boolean isLeftMaximalPattern = referenceToNodesCorrespondingToModels.get(p).getInfo().getIsMaximalPattern();
      if (!isLeftMaximalPattern) {
        i.remove();
      }
      p++;
      this.incrementSubtaskPercentDone();
    }
    this.updatePercentDone();
  }

//#####################################################################################################################
//#####################################################################################################################
//#####################################################################################################################

  /**
   * Deletes from the list this.modelsAndOccurrences the models that correspond to the same bicluster.
   *
   * @param modelsAndOccurrences ArrayList<ModelOccurrences>
   */
  protected void deleteModelsRepresentingTheSameBiclusters(ArrayList<ModelOccurrences> modelsAndOccurrences) {

    //CREATE HASHMAP (NEW IMPLEMENTATION OF HASHTREE)

    int numberOfModels = modelsAndOccurrences.size();

    //HashMap(int initialCapacity, float loadFactor)  
    HashMap<Integer, ModelOccurrences>
        hashtable = new LinkedHashMap(numberOfModels, (float) 0.75);

    int numberOfDeletedModels = 0;

//    System.out.println("BEGIN - CHECK IF THERE ARE MODELS REPRESENTING THE SAME BICLUSTER");

    Iterator<ModelOccurrences> i = modelsAndOccurrences.iterator();
    while (i.hasNext()) {
      ModelOccurrences currentModelAndOccurrences = i.next();

      /*      System.out.print("MODEL " + position + " --> MOTIF = ");
            for (int j = 0; j< motif.length; j++){
              System.out.print(motif[j] + "\t");
            }
            System.out.println();
       */

      int currentHashCode = this.computeHashCodeModelAndOccurrences(currentModelAndOccurrences);
      if (hashtable.containsKey(currentHashCode)) {
        ModelOccurrences object = hashtable.get(currentHashCode); // get object that has this key
        if (this.computeEqualsModelAndOccurrences(object, currentModelAndOccurrences)) {
          //System.out.println("DELETING MODEL --> THERE IS ALREADY A MODEL REPRESENTING THE SAME BICLUSTER");
          // THERE IS A MODEL IN THE HASHTREE THAT REPRESENTS THE SAME BICLUSTERS
          numberOfDeletedModels++;
          i.remove();
        }
        else {
          // THERE IS NOT A MODEL IN THE HASHTREE THAT REPRESENTS THE SAME BICLUSTERS
          hashtable.put(currentHashCode, currentModelAndOccurrences);
        }
      }
      else {
        // THERE IS NOT A MODEL IN THE HASHTREE THAT REPRESENTS THE SAME BICLUSTERS
        hashtable.put(currentHashCode, currentModelAndOccurrences);
      }
    }
//    System.out.println("END - CHECK IF THERE ARE MODELS REPRESENTING THE SAME BICLUSTER");
  }

  //######################################################################################################################

  /**
   * Uses genes and motif first and last column.
   *
    * @param modelAndOccurrences_1 ModelOccurrences
    * @param modelAndOccurrences_2 ModelOccurrences
    * @return boolean
    */
   protected boolean computeEqualsModelAndOccurrences(ModelOccurrences modelAndOccurrences_1, ModelOccurrences modelAndOccurrences_2) {
    if(modelAndOccurrences_1 == null || modelAndOccurrences_2 == null){
      return false;
    }
    if(modelAndOccurrences_1.getModel() == null || modelAndOccurrences_2.getModel() == null){
      return false;
    }
    int firstColumn_1 = Integer.parseInt( (new Integer(modelAndOccurrences_1.getModel().getMotif()[0]).toString()).substring(2));
    int lastColumn_1 = Integer.parseInt( (new Integer(modelAndOccurrences_1.getModel().getMotif()[ modelAndOccurrences_1.getModel().getMotif().length - 1]).toString()).substring(2));
    int firstColumn_2 = Integer.parseInt( (new Integer(modelAndOccurrences_2.getModel().getMotif()[0]).toString()).substring(2));
    int lastColumn_2 = Integer.parseInt( (new Integer(modelAndOccurrences_2.getModel().getMotif()[ modelAndOccurrences_2.getModel().getMotif().length - 1]).toString()).substring(2));
    return (firstColumn_1 == firstColumn_2
            &&
            lastColumn_1 == lastColumn_2
            &&
            modelAndOccurrences_1.getGenesInOccurrences().equals(modelAndOccurrences_2.getGenesInOccurrences()));
  }


  //######################################################################################################################

  /**
   * Uses genes and motif first and last column.
   *
   * @param modelAndOccurrences ModelOccurrences
   * @return int
   */
  protected int computeHashCodeModelAndOccurrences(ModelOccurrences modelAndOccurrences) {
    if(modelAndOccurrences.getModel() == null){
      return 0;
    }
    int firstColumn = Integer.parseInt( (new Integer(modelAndOccurrences.getModel().getMotif()[0]).toString()).substring(2));
    int lastColumn = Integer.parseInt( (new Integer(modelAndOccurrences.getModel().getMotif()[modelAndOccurrences.getModel().getMotif().length - 1]).toString()).substring(2));
    int hash = 17;
    hash = 37 * hash + firstColumn;
    hash = 37 * hash + lastColumn;
    hash = 37 * hash +
        (null == modelAndOccurrences.getGenesInOccurrences() ? 0 : modelAndOccurrences.getGenesInOccurrences().hashCode());
    return hash;
  }


//######################################################################################################################
//################################# CCC-BICLUSTERING WITH ERRORS ALGORITHM #############################################
//######################################################################################################################

  /**
   * Returns true if this.maxNumberOfErrors > 0.
   * @return boolean
   */
  protected boolean deleteModelsRepresentingTheSameBiclustersIsNeeded(){
    if (this.maxNumberOfErrors > 0)
      return true;
    else
      return false;
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   * Runs E-CCC_Biclustering.
   *
   * @param rowsQuorum int
   * @param columnsQuorum int
   * @return ArrayList
   */
  public ArrayList<ModelOccurrences> cccBiclusteringWithErrors(int rowsQuorum, int columnsQuorum) {

    ArrayList<ModelOccurrences> modelsAndOccurrences = new ArrayList<ModelOccurrences> (0);

/*    try {
      PrintWriter pw = new PrintWriter("e_ccc_2_deleteModels.txt");
      long time;
 */
      //#######################################################################################################
      // COMPUTE ALL MODELS THAT CORRESPOND TO RIGHT-MAXIMAL BICLUSTERS AND STORE THEM IN modelsAndOccurrences
      //#######################################################################################################
      this.computeModelsCorrespondingToRightMaximalBiclusters(modelsAndOccurrences,
          rowsQuorum, columnsQuorum);

//      time = System.currentTimeMillis();
      //#####################################################################################################
      // DELETE FROM modelsAndOccurrences ALL MODELS THAT DO NOT CORRESPOND TO LEFT-MAXIMAL BICLUSTERS
      //#####################################################################################################
//      System.out.println("DELETING NON LEFT MAXIMAL BICLUSTERS");
//      this.getProgressBar().setString("DELETE NON LEFT MAXIMAL");
      this.deleteModelsNotCorrespondingToLeftMaximalBiclusters(modelsAndOccurrences);
//      pw.println("DELETE NON LEFT MAXIMAL: " + (System.currentTimeMillis() - time));

      if (this.deleteModelsRepresentingTheSameBiclustersIsNeeded()) {
        //###################################################################################################
        // DELETE MODELS THAT REPRESENT THE SAME CCC-BICLUSTERS
        //###################################################################################################
//        time = System.currentTimeMillis();
//        System.out.println("DELETING REPEATED BICLUSTERS");
//        this.getProgressBar().setString("DELETE REPEATED");
        this.deleteModelsRepresentingTheSameBiclusters(modelsAndOccurrences);
/*        pw.println("DELETE REPEATED: " +
                   (System.currentTimeMillis() - time));
 */
      }
      this.incrementPercentDone();
/*      pw.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
 */
    return (modelsAndOccurrences);
  }

  //#####################################################################################################################
  //#####################################################################################################################
  //#####################################################################################################################
  /**
   * Computes all models and occurrences corresponding to right-maximal biclusters using this.spellModels.
   *
   * @param modelsAndOccurrences ArrayList
   * @param rowsQuorum int
   * @param columnsQuorum int
   */
  protected void computeModelsCorrespondingToRightMaximalBiclusters(
      ArrayList<ModelOccurrences> modelsAndOccurrences,
      int rowsQuorum, int columnsQuorum) {

/*    try{
      PrintWriter pw = new PrintWriter("e_ccc_1_computeModels.txt");
      long time = System.currentTimeMillis();
 */

      //##########################################################################
      // CONSTRUCT GENERALIZED SUFFIX TREE AND ADD INFORMATION TO THE NODES IN THE TREE
      //##########################################################################
      SuffixTreeWithErrorsRightMaximal suffixTreeRightMaximalBiclusters =
          this.constructSuffixTreeRightMaximalBiclusters();
//      pw.println("CONSTRUCT SUFFIX TREE: " + (System.currentTimeMillis() - time));

      //##########################################################################
      // INITIALIZING DATA STRUCTURES
      //##########################################################################

      // l
      int length_m = 0;
      // m
      Model m = new Model();
      // Occ_m
      ArrayList<NodeOccurrence> Occ_m = new ArrayList<NodeOccurrence> (0);
      Occ_m.add(new NodeOccurrence(suffixTreeRightMaximalBiclusters.getRoot(), 0, 0));
      // Ext_m
      ModelExtensionSet Ext_m = this.initialize_Ext_m(
          suffixTreeRightMaximalBiclusters);

      // father_m
      Model father_m = new Model();

      // number of genes in Occ_father_m
      int numberOfGenes_in_Occ_father_m = 0;

      //#############################################################################################################
      // CALL SPELLMODELS: STORES ALL MODELS THAT CORRESPOND TO RIGHT-MAXIMAL BICLUSTERS IN modelsAndOccurrences
      //#############################################################################################################

//      int[] numberOfModels = new int[]{0};
//      time = System.currentTimeMillis();
/*      this.j_countModels(modelsAndOccurrences, suffixTreeRightMaximalBiclusters,
                       length_m,
                       m, Occ_m, Ext_m,
                       father_m, numberOfGenes_in_Occ_father_m, rowsQuorum,
                       columnsQuorum, numberOfModels);
//      pw.println("COUNT MODELS: " + (System.currentTimeMillis()-time));

      PrintWriter pw1 = new PrintWriter("e_ccc_numModels.txt");
      pw1.println("NUM MODELS: " + numberOfModels[0]);
      pw1.println("THEORETICAL BOUND: " + (this.matrix.getNumberOfGenes()*
                         Math.pow(this.matrix.getNumberOfConditions(),
                                  1+this.maxNumberOfErrors)*
                         Math.pow(this.getAlphabet().length, this.maxNumberOfErrors)));
 */

//      long time = System.currentTimeMillis();
      int[] numberOfModels = new int[]{this.getTaskPercentages().computeSpellModelsTaskDivisor(
      this.matrix.getNumberOfGenes(), this.matrix.getNumberOfConditions(),
            this.maxNumberOfErrors, this.getAlphabet().length)};
/*      pw1.println("ESTIMATED BOUND: " + numberOfModels[0]);
      pw1.close();
 */

      this.setSubtaskValues(numberOfModels[0]);
//      this.getProgressBar().setString("SPELL MODELS");
      this.spellModels(modelsAndOccurrences, suffixTreeRightMaximalBiclusters,
                       length_m,
                       m, Occ_m, Ext_m,
                       father_m, numberOfGenes_in_Occ_father_m, rowsQuorum,
                       columnsQuorum, numberOfModels);
      this.updatePercentDone();
/*      pw.println("SPELL MODELS: " + (System.currentTimeMillis() - time));
      pw.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
 */
  }

  //#########################################################################################################
  //#########################################################################################################

  /**
   *
   * @param suffixTreeRightMaximalBiclusters SuffixTreeWithErrorsRightMaximal
   * @return ModelExtensionSet
   */
  protected ModelExtensionSet initialize_Ext_m(SuffixTreeWithErrorsRightMaximal suffixTreeRightMaximalBiclusters){
    ModelExtensionSet Ext_m = new ModelExtensionSet();

    //IF maxNumberOfErrors > 0
    if (this.maxNumberOfErrors > 0) {
      Ext_m.addAll(this.computeAlphabetLexicographicOrder());
    }
    else { // maxNumberOfErrors = 0 =>
      //Extensions = root children that are not string terminators and whose first character = ?1
      NodeInterface node = suffixTreeRightMaximalBiclusters.getRoot().getFirstChild();
      while (node != null) {
        int firstCharacter = suffixTreeRightMaximalBiclusters.getSubArrayInt(node.getLeftIndex(), 1)[0];
        if (firstCharacter > 0) {
          Ext_m.add(firstCharacter);
        }
        node = node.getRightSybling();
      }
    } // END ELSE maxNumberOfErrors = 0
    return Ext_m;
  }

//#########################################################################################################
//#########################################################################################################

  /**
   * Uses the attribute this.modelsAndOccurrences to initialize the attribute this.setOfBiclusters
   * with the set of CCC-biclusters extracted by the algorithm.
   *
   * @param modelsAndOccurrences ArrayList<ModelOccurrences>
   */
  protected void createMaximalBiclusters(ArrayList<ModelOccurrences>modelsAndOccurrences) {

    this.setOfBiclusters = new ArrayList<Bicluster> (0);

    this.setSubtaskValues(modelsAndOccurrences.size());
    for (int k = 0; k < modelsAndOccurrences.size(); k++) {
      ModelOccurrences currentModelAndOccurrences = modelsAndOccurrences.get(k);
      int numberOfConditions;
      int patternLength;
      if (this.matrix.getIsVBTP()) {
        numberOfConditions = currentModelAndOccurrences.getModel().getMotif().length + 1;
        patternLength = numberOfConditions - 1;
      }
      else {
        numberOfConditions = currentModelAndOccurrences.getModel().getMotif().length;
        patternLength = numberOfConditions;
      }
      int[] columnsIndexes = new int[numberOfConditions]; // column indexes start at 1
      int firstColumn = Integer.parseInt(new Integer(currentModelAndOccurrences.getModel().getMotif()[0]).toString().substring(2));
      for (int c = 0; c < numberOfConditions; c++) {
        columnsIndexes[c] = firstColumn + c;
      }

      char[] biclusterExpressionPattern = new char[patternLength]; // model
      for (int pt = 0; pt < patternLength; pt++) {
        biclusterExpressionPattern[pt] = (char) Integer.parseInt(new Integer(
            currentModelAndOccurrences.getModel().getMotif()[pt]).toString().substring(0, 2));
      }

      BitSet genes_m = currentModelAndOccurrences.getGenesInOccurrences();
      int[] rowsIndexes = new int[genes_m.cardinality()];
      int p = 0;
      for (int g = 0; g < genes_m.size(); g++) {
        if (genes_m.get(g) == true) {
          rowsIndexes[p] = g + 1;
          p++;
        }
      }

      //!!!
      //
      // !!! I'M NOT SURE WHETHER TO CREATE CCC-BICLUSTERS HERE !!!
      //
      // 22/11/2007: I WON'T DO THIS (SEE EXPLANATION BELOW IN (*)
      //!!!

      // CHECK IF NODE OCCURRENCE REALLY CORRESPONDS TO A E_CCC_BICLUSTERS
      //boolean is_E_CCC_Bicluster = false;
      // if (currentModelAndOccurrences.getMaxErrorInOcc_m() == 0){
      //   is_E_CCC_Bicluster = false;
      // }
      //
      // DOES NOT WORK !!!
      //
      // #BICLUSTER_7	CONDITIONS= 3	FIRST-LAST= 2-4	GENES= 3	PATTERN=UDN
      // G1	UDU
      // G2	UDU
      // G4	UDU
      //
      // maxErrorInOccurrences = 1 JUST BECAUSE THE MOTIF IS UDN AND NOT UDU (WHICH ALSO GENERATES A BICLUSTER)
      //
      // SOLUTION:
      //
      // COMPARE EXPRESSION PATTERN OF EACH GENE WHEN TWO DIFFERENT PATTERNS ARE FOUND SET is_E_CCC_Bicluster = true

/*      int i = 0;
      while (i < rowsIndexes.length - 1 && is_E_CCC_Bicluster == false) {
        char[] pattern_i = new char[biclusterExpressionPattern.length];
        char[] pattern_i_plus_1 = new char[biclusterExpressionPattern.length];
        for (int j = 0; j < biclusterExpressionPattern.length; j++) {
          pattern_i[j] = this.matrix.getSymbolicExpressionMatrix()[rowsIndexes[i]-1][columnsIndexes[j] - 1];
          pattern_i_plus_1[j] = this.matrix.getSymbolicExpressionMatrix()[
              rowsIndexes[i + 1] - 1][columnsIndexes[j] - 1];
          String s_pattern_i = new String(pattern_i);
          String s_pattern_i_plus_1 = new String(pattern_i_plus_1);
          if (!s_pattern_i.equals(s_pattern_i_plus_1)) {
            is_E_CCC_Bicluster = true;
          }
        }
        i++;
      }
*/
      // CREATE AND STORE BICLUSTER

//      if (is_E_CCC_Bicluster) {
        this.store_E_CCC_Bicluster((k + 1), rowsIndexes, columnsIndexes,
                                   biclusterExpressionPattern,
                                   currentModelAndOccurrences.getMaxErrorInOcc_m());

        // (*)
        // THIS MAX ERROR IS RELATIVELLY TO THE MOTIF FROM WHICH THE BICLUSTER WAS GENERATED
        // => IF I WANT TO CREATE CCC-BICLUSTER THAT IS, FIND OUT IF THE GENE PATTERNS HAS 0 ERROR BETWEEN THEM
        // I WOULD ALSO HAVE TO CHECK IF MaxErrorInOcc_m ALSO CORRESPONDS TO THE "REAL" MAXIMUM NUMBER OF ERRORS IN
        // GENE PATTERNS IN THE BICLUSTERS => COMPARE BICLUSTER GENE PATTERNS BETWWEN THEM
        // I DON'T WANT TO DO THIS !! I WILL CONSIDER THAT THE NUMBER OF ERRORS IN THE BICLUSTER IS THE MAX NUMBER OF ERRORS
        // RELATIVELLY TO THE MOTIF THAT GENERATED THE BICLUSTER !!!
//      }
//      else {
//        this.setOfBiclusters.add(new CCC_Bicluster(this, (k + 1), rowsIndexes,columnsIndexes));
//      }

        this.incrementSubtaskPercentDone();
    }
    this.updatePercentDone();
  }

  //#########################################################################################################

  /**
   * Create an object of <CODE>E_CCC_Bicluster</CODE> and stores it in <CODE>this.setOfBiclusters</CODE>.
   *
   * @param ID int
   * @param rows int[]
   * @param columns int[]
   * @param biclusterPattern char[]
   * @param maxNumberOfErrors int
   */
  protected void store_E_CCC_Bicluster(int ID, int[] rows,
                                       int[] columns, char[] biclusterPattern,
                                       int maxNumberOfErrors) {
    this.setOfBiclusters.add(new E_CCC_Bicluster(this, ID, rows,
                                                 columns, biclusterPattern,
                                                 maxNumberOfErrors));
  }


  //#########################################################################################################
  //#########################################################################################################

  /**
   * Runs E-CCC-biclustering and creates the set of E-CCC-Biclusters.
   * Trivial E-CCC-Biclusters are NOT reported.
   *
   * @throws Exception
   */
  public void computeBiclusters() throws Exception {
    this.computeBiclusters(true);
  }

  //#####################################################################################################################

  /**
   * Runs E-CCC-biclustering and creates the set of E-CCC-Biclusters.
   * Trivial E-CCC-Biclusters are reported provided they are maximal and the parameter filterTrivial = false.
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

  //#########################################################################################################

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

    if (rowsQuorum < 1 || columnsQuorum < 1 ||
        rowsQuorum > this.matrix.getNumberOfGenes() ||
        columnsQuorum > this.matrix.getNumberOfConditions()) {
      throw new Exception(
          "rowsQuorum in [1, numberOfGenes] and columndQuorum in [1, numberOfConditions]");
    }

    this.setTaskPercentages(new TaskPercentages(this));

    ArrayList<ModelOccurrences> modelsAndOccurrences =
        this.cccBiclusteringWithErrors(rowsQuorum, columnsQuorum);

/*    PrintWriter pw = new PrintWriter("e_ccc_3_createBiclusters.txt");
    long time = System.currentTimeMillis();
 */

    //COMPUTE SET OF CCC-BICLUSTERS
//    this.getProgressBar().setString("CREATE BICLUSTERS");
    this.createMaximalBiclusters(modelsAndOccurrences);
/*    pw.println("CREATE BICLUSTERS: " + (System.currentTimeMillis()-time));
    pw.close();
 */
  }

  /**
   *
   * @return Object
   */
  public Object clone() {
    E_CCC_Biclustering o = (E_CCC_Biclustering)super.clone();
    o.maxNumberOfErrors = this.maxNumberOfErrors;
    return o;
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
