package smadeira.biclustering;

import java.io.*;
import java.util.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.io.Serializable;
import smadeira.utils.TaskPercentages;


/**
 * <p>Title: CC-TSB Biclustering</p>
 *
 * <p>Description: CC-TSB algorithm.<br/>
 * Ya Zhang, Hongyuan Zha, Chao-Hisen Chu.<br/>
 * A Time-Series Biclustering Algorithm for Revealing Co-Regulated Genes,<br/>
 * in Proc. of IEEE International Conference on Information and Technology:
 * Coding and Computing, (ITCC 2005), pp. 32-37.
 * </p>
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
public class CC_TSB_Biclustering
    extends Abstract_CC_Biclustering implements Serializable {
  public static final long serialVersionUID =-5559023305187962448L;

  /**
   *
   */
  private int maxNumberOfIterations;

  /**
   * Constructor
   *
   * @param m AbstractExpressionMatrix
   * @param delta float
   * @param alpha float
   * @param numBiclustersToExtract int
   * @param maxIterations int
   * @throws Exception
   */
  public CC_TSB_Biclustering(AbstractExpressionMatrix m, float delta,
                             float alpha, int numBiclustersToExtract,
                             int maxIterations) throws Exception {

    super(m, delta, alpha, numBiclustersToExtract);

    this.maxNumberOfIterations = maxIterations;
  }

  /**
   *
   * @return int
   */
  public int getMaxNumberOfIterations() {
    return this.maxNumberOfIterations;
  }

  //######################################################################################################################
  //####################################### METHODS USED IN CC-TSB #######################################################
  //######################################################################################################################


  /**
   * Delete column.
   *
   */
  protected void deleteColumn() {
    // compute J_min and J_max
    // assuming J is ordered
    // try to delete first column
/*    System.out.println("TRYING TO DELETE COLUM #"
                       + this.J.get(0).intValue());
 */
    boolean remove_first_column = false;
    // COMPUTE RATIO j
    float ratio_j;
    float sum_i = 0;
    for (int i = 0; i < this.I.size(); i++) {
      float a = this.A[this.I.get(i).intValue()][this.J.get(0).intValue()];
      float b = a_iJ.get(i).floatValue();
      float c = a_Ij.get(0).floatValue();
      sum_i = sum_i + (float) Math.pow(a - b - c + a_IJ, 2);
    }
    ratio_j = (1 / this.H) * (1 / (float)this.I.size()) * sum_i;
    if (ratio_j > alpha) {
//      System.out.println("DELETING COLUM #" + this.J.get(0).intValue());
      remove_first_column = true;
    }
    // try to delete last column
/*    System.out.println("TRYING TO DELETE COLUM #"
                       + J.get(J.size() - 1).intValue());
 */
    boolean remove_last_column = false;
    // COMPUTE RATIO j
    sum_i = 0;
    for (int i = 0; i < this.I.size(); i++) {
      float a = this.A[this.I.get(i).intValue()][this.J.get(
          this.J.size() - 1).intValue()];
      float b = a_iJ.get(i).floatValue();
      float c = a_Ij.get(J.size() - 1).floatValue();
      sum_i = sum_i + (float) Math.pow(a - b - c + a_IJ, 2);
    }
    ratio_j = (1 / this.H) * (1 / (float)this.I.size()) * sum_i;
    if (ratio_j > alpha) {
/*      System.out.println("DELETING COLUM #"
                         + J.get(J.size() - 1).intValue());
 */
      remove_last_column = true;
    }
    if (remove_first_column) {
      this.J.remove(0);
    }
    if (remove_last_column) {
      this.J.remove(this.J.size() - 1);
    }
  }

  //######################################################################################################################

  /**
   * Insert column.
   *
   */
  protected void insertColumn() {
    // compute J_min-1 and J_max+1
    // if L_min = first column in A => left insertion cannot be done
    // if L_max = last column in A => rigth insertion cannot be done
    // if L_min = first AND if L_max = last column in A => insertion cannot be done
    // assuming J is ordered
    // TRYING TO EXTEND LEFT
    if (this.J.get(0).intValue() != 0) {
/*      System.out.println("TRYING TO ADD COLUMN #"
                         + (this.J.get(0).intValue() - 1));
 */
      // COMPUTE RATIO j
      float ratio_j;
      boolean insert_column_left = false;
      float sum_i = 0;
      for (int i = 0; i < I.size(); i++) {
        float a = A[I.get(i).intValue()][J.get(0).intValue() - 1];
        float b = a_iJ.get(i).floatValue();
        float c = a_Ij.get(0).floatValue();
        sum_i = sum_i + (float) Math.pow(a - b - c + a_IJ, 2);
      }
      ratio_j = (1 / this.H) * (1 / (float)this.I.size()) * sum_i;
      if (ratio_j <= alpha) {
/*        System.out.println("ADDING COLUMN #"
                           + (J.get(0).intValue() - 1));
 */
        insert_column_left = true;
      }
      if (insert_column_left) {
        this.J.add(0, new Integer(J.get(0).intValue() - 1));
      }
    }

    // TRYING TO EXTEND TO RIGHT
    if ( ( (Integer) J.get(J.size() - 1)).intValue() != this.A[0].length - 1) {

/*      System.out.println("TRYING TO ADD COLUMN #"
                         + (J.get(J.size() - 1).intValue() + 1));
 */
      // COMPUTE RATIO j
      boolean insert_column_right = false;
      float ratio_j;
      float sum_i = 0;
      for (int i = 0; i < I.size(); i++) {
        float a = this.A[I.get(i).intValue()][this.J.get(
            this.J.size() - 1).intValue() + 1];
        float b = this.a_iJ.get(i).floatValue();
        float c = this.a_Ij.get(J.size() - 1).floatValue();
        sum_i = sum_i + (float) Math.pow(a - b - c + a_IJ, 2);
      }
      ratio_j = (1 / this.H) * (1 / (float) I.size()) * sum_i;
      if (ratio_j < alpha) {
/*        System.out.println("ADDING COLUMN #"
                           + (J.get(J.size() - 1).intValue() + 1));
 */
        insert_column_right = true;
      }
      if (insert_column_right) {
        this.J.add(J.size(), new Integer(
            J.get(J.size() - 1).intValue() + 1));
      }
    }
  }

  //######################################################################################################################

  /**
   * Insert Row.
   */
  protected void insertRow() {
    ArrayList<Integer> X_I = (ArrayList<Integer>) X.clone();
    // candidates do insertion
    X_I.removeAll(I);
    ArrayList<Integer> I_i = new ArrayList<Integer> (0);
    for (int i = 0; i < X_I.size(); i++) {
//      System.out.println("TRYING TO ADD ROW #" + X_I.get(i).intValue());
      // COMPUTE RATIO i
      float ratio_i;
      float sum_j = 0;
      for (int j = 0; j < this.J.size(); j++) {
        float a = A[X_I.get(i).intValue()][J.get(j).intValue()];
        int position_i = a_iJ.size() - 1;
        float b = a_iJ.get(position_i).floatValue();
        float c = a_Ij.get(j).floatValue();
        sum_j = sum_j + (float) Math.pow(a - b - c + a_IJ, 2);
      }
      ratio_i = (1 / H) * (1 / (float)this.J.size()) * sum_j;
      if (ratio_i < alpha) {
//        System.out.println("ADDING ROW #" + X_I.get(i).intValue());
        I_i.add(new Integer(X_I.get(i).intValue()));
      }
    }
    this.I.addAll(I_i);
  }

  //######################################################################################################################
  //################################################## CC-TSB BICLUSTERING ALGORITHM #####################################
  //######################################################################################################################

  /**
   * Computes a set of biclusters.
   */
  public void computeBiclusters() {
    /*    System.out.println("#BICLUSTERS=" + this.numberOfBiclustersToExtract);
         System.out.println("#ALPHA=" + this.alpha);
         System.out.println("#DELTA=" + this.delta);
     */
    this.setOfBiclusters = new ArrayList<Bicluster> (0);
    // fazer depois excepcoes
    if (alpha < 1) {
      return;

    }
    if (delta < 0) {
      return;
    }

    this.setTaskPercentages(new TaskPercentages(this));
    this.setSubtaskValues(this.numberOfBiclustersToExtract);
    for (int k = 0; k < this.numberOfBiclustersToExtract; k++) {
/*      System.out.println("###################### COMPUTING BICLUSTER #"
                         + (k + 1) + "##################################");
 */
      // initialize the submatrix to be A
      this.I = (ArrayList<Integer>)this.X.clone();
      this.J = (ArrayList<Integer>)this.Y.clone();
/*      System.out.println("#I=" + this.I.size());
      System.out.println("#J=" + this.J.size());
 */

      // compute H
      this.update_H();

      // NODE DELETION
      boolean colsDeleted = true;
      boolean rowsDeleted = true;
      int iterations = 0;

      while (H > delta && (rowsDeleted == true || colsDeleted == true)
             && iterations <= this.maxNumberOfIterations) {

/*        System.out.println("######################## H > delta:" + H
                           + ">" + delta + " ==>  NODE DELETION");
        System.out.println("#I=" + this.I.size());
        System.out.println("#J=" + this.J.size());
        System.out.println("H=" + this.H);
        System.out.println("DELETING ROWS...");
 */

        int rowsBefore = this.I.size();

        this.deleteRow();

        int rowsAfter = this.I.size();

        if (rowsAfter < rowsBefore) {

          rowsDeleted = true;

/*          System.out
              .println("######################## UPDATING H...");
 */

          this.update_H();

/*          System.out.println("#I=" + this.I.size());
          System.out.println("#J=" + this.J.size());
          System.out.println("H=" + this.H);
 */
        }
        else {
          rowsDeleted = false;
        }

/*        System.out
            .println("######################## DELETING COLUMNS...");
 */

        int colsBefore = this.J.size();

        this.deleteColumn();

        int colsAfter = this.J.size();

        if (colsAfter < colsBefore) {

          colsDeleted = true;

/*          System.out
              .println("######################## UPDATING H...");
 */

          this.update_H();

/*          System.out.println("#I=" + this.I.size());
          System.out.println("#J=" + this.J.size());
          System.out.println("H=" + this.H);
 */
        }
        else {
          colsDeleted = false;
        }

        iterations++;
      }

      // NODE INSERTION
      boolean colInserted = false;
      boolean rowInserted = false;

      if (I.size() != 0 && J.size() != 0) {
        do {
          // insert column
          /*        System.out
                      .println("######################## INSERTING COLUMNS...");
           */

          int numColsBefore = this.J.size();

          this.insertColumn();

          // test if column insertion occurred
          int numColsAfter = this.J.size();

          if (numColsBefore < numColsAfter) {

            colInserted = true;

            /*          System.out
                          .println("######################## UPDATING H...");
             */

            this.update_H();

            /*          System.out.println("#I=" + this.I.size());
                      System.out.println("#J=" + this.J.size());
                      System.out.println("H=" + this.H);
             */
          }
          else {
            colInserted = false;
          }

          // insert row
          /*        System.out
                      .println("######################## INSERTING ROWS...");
           */

          int numRowsBefore = I.size();

          this.insertRow();

          int numRowsAfter = this.I.size();

          // test if row insertion occurred
          if (numRowsBefore < numRowsAfter) {

            rowInserted = true;

            /*          System.out
                          .println("######################## UPDATING H...");
             */

            this.update_H();

            /*          System.out.println("#I=" + this.I.size());
                      System.out.println("#J=" + this.J.size());
                      System.out.println("H=" + this.H);
             */
          }
          else {
            rowInserted = false;
          }

        }
        while (rowInserted == true || colInserted == true);

        /*      System.out.println("H = " + this.H);
              System.out
         .println("######################## STORING BICLUSTER IN AN OBJECT");
         */

        //STORE BICLUSTER IN AN OBJECT BiclusterWithSymbols SO THAT SOME METHODS OF THIS CLASS AND OF CLASS
        //BiclusteringWithSymbols CAN BE USED
        int[] rowIndexes = new int[I.size()];

        String[] genes = new String[I.size()];
        String[] conditions = new String[J.size()];

        for (int i = 0; i < I.size(); i++) {
          rowIndexes[i] = I.get(i).intValue() + 1;
          genes[i] = this.matrix.getGenesNames()[I.get(i).intValue()];
        }

        int[] columnIndexes = new int[J.size()];

        for (int j = 0; j < J.size(); j++) {
          columnIndexes[j] = J.get(j).intValue() + 1;
          conditions[j] = this.matrix.getConditionsNames()[J.get(j)
              .intValue()];
        }

        float[][] biclusterExpressionMatrix = new float[I.size()][J.size()];

        for (int i = 0; i < I.size(); i++) {
          for (int j = 0; j < J.size(); j++) {
            biclusterExpressionMatrix[i][j] = this.matrix
                .getGeneExpressionMatrix()[rowIndexes[i] - 1][columnIndexes[j] -
                1]; // original expression matrix
          }
        }

        this.setOfBiclusters.add(new Bicluster(this, k + 1,
                                               rowIndexes, columnIndexes));

        //Replace A(I,J) with random values
        /*      System.out
                  .println(
         "################################## MASKING BICLUSTER WITH RANDOM VALUES");
         */
        this.replaceBiclusterWithRandomValues();
      }
      this.incrementSubtaskPercentDone();
    } // END FOR
    this.updatePercentDone();
  } //END CC_TSC_ReturningArrayOfBiclusters

  //###########################################################################
  //####### REPORTING BICLUSTERS TO SCREEN OR FILE ############################
  //###########################################################################

  /**
   *
   * @return Object
   */
  public Object clone() {
    CC_TSB_Biclustering o = (CC_TSB_Biclustering)super.clone();
    o.matrix = (AbstractExpressionMatrix)this.matrix.clone();
    o.delta = this.delta;
    o.alpha = this.alpha;
    o.maxNumberOfIterations = this.maxNumberOfIterations;
    o.A = this.A.clone();
    o.a_Ij = (ArrayList<Float>)this.a_Ij;
    o.a_iJ = (ArrayList<Float>)this.a_iJ;
    o.H = this.H;
    o.I = (ArrayList<Integer>)this.I.clone();
    o.J = (ArrayList<Integer>)this.J.clone();
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
    /*if (this.originalExpressionMatrix instanceof ExpressionMatrix){
     ...
     }
     else{
     if (this.originalExpressionMatrix instanceof PreProcessedExpressionMatrix){
     ...
     }
     else{
     return null;
     }
     */
    return (doc);
  }

} // END CLASS
