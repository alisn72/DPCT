package smadeira.biclustering;

import java.util.*;
import java.io.Serializable;

/**
 * <p>Title: CC Biclustering</p>
 *
 * <p>Description: Implementation of the Cheng and Church algorithm.</p>
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
 * @author smcosta
 * @version 1.0
 */
public class CC_Biclustering
    extends Abstract_CC_Biclustering implements Serializable {
  public static final long serialVersionUID = -3280281848391407411L;

  /**
   * Threshold for the minimum number of columns (rows)
   */
  private int threshold;

  /**
   * Perform multiple node deletion on rows.
   */
  private boolean performMultipleNodeDeletionOnRows;

  /**
   * Perform multiple node deletion on columns.
   */
  private boolean performMultipleNodeDeletionOnColumns;

  /**
   *
   * Constructor
   *
   * @param m AbstractExpressionMatrix
   * @param delta float
   * @param alpha float
   * @param threshold int
   * @param numBiclustersToExtract int
   * @throws Exception
   */
  public CC_Biclustering(AbstractExpressionMatrix m, float delta,
                         float alpha, int threshold, int numBiclustersToExtract) throws
      Exception {

    super(m, delta, alpha, numBiclustersToExtract);

    this.threshold = threshold;
    this.performMultipleNodeDeletionOnColumns = true;
    this.performMultipleNodeDeletionOnRows = true;
  }

  /**
   * Multiple Column Deletion.
   *
   */
  protected void deleteColumn() {

    // the columns to delete
    ArrayList columnsToDelete = new ArrayList(0);

    for (int j = 0; j < this.J.size(); j++) {

/*      System.out.println("TRYING TO DELETE COLUMN #"
                         + ( (Integer)this.J.get(j)).intValue());
 */

      // Compute ratio j
      float ratio_j;

      float sum_i = 0;

      for (int i = 0; i < this.I.size(); i++) {
        float a = this.A[this.I.get(i).intValue()][this.J.get(j)
            .intValue()];
        float b = this.a_iJ.get(i).floatValue();
        float c = this.a_Ij.get(j).floatValue();
        sum_i = sum_i + (float) Math.pow(a - b - c + this.a_IJ, 2);
      }

      ratio_j = (1 / this.H) * (1 / (float)this.I.size()) * sum_i;

      if (ratio_j > alpha) {

//        System.out.println("DELETING ROW #" + this.J.get(j).intValue());

        columnsToDelete.add(new Integer(j));
      }
    }
    this.J.removeAll(columnsToDelete);
  }

  /**
   *
   * Single Node Deletion.
   *
   */
  private void singleNodeDeletion() {

    // the row with the largest di
    int rowLargestdi = 0;

    // the column with the largest dj
    int columnLargestdj = 0;

    // the largest di
    float largestDi = 0;

    // the largest dj
    float largestDj = 0;

    // Find the row with the largest di
    for (int i = 0; i < this.I.size(); i++) {
      float di;
      float sum_j = 0;
      for (int j = 0; j < this.J.size(); j++) {
        float a = this.A[this.I.get(i).intValue()][this.J.get(j)
            .intValue()];
        float b = this.a_iJ.get(i).floatValue();
        float c = this.a_Ij.get(j).floatValue();
        sum_j = sum_j + (float) Math.pow(a - b - c + this.a_IJ, 2);
      }
      di = (1 / (float)this.J.size()) * sum_j;

      if (di > largestDi) {
        largestDi = di;
        rowLargestdi = i;
      }
    }

    // Find the column with the largest dj
    for (int j = 0; j < this.J.size(); j++) {
      float dj;
      float sum_i = 0;
      for (int i = 0; i < this.I.size(); i++) {
        float a = this.A[this.I.get(i).intValue()][this.J.get(j)
            .intValue()];
        float b = this.a_iJ.get(i).floatValue();
        float c = this.a_Ij.get(j).floatValue();
        sum_i = sum_i + (float) Math.pow(a - b - c + this.a_IJ, 2);
      }
      dj = (1 / (float)this.I.size()) * sum_i;

      if (dj > largestDj) {
        largestDj = dj;
        columnLargestdj = j;
      }
    }

    if (largestDi > largestDj) {
      // remove the row
      this.I.remove(rowLargestdi);
    }
    else {
      // remove the column
      this.J.remove(columnLargestdj);
    }
  }

  /**
   * Insert Row.
   */
  protected void insertRow() {

    ArrayList<Integer> X_I = (ArrayList<Integer>) X.clone();

    // candidates to insertion
    X_I.removeAll(I);

    // rows to add
    ArrayList<Integer> I_i = new ArrayList<Integer> (0);

    for (int i = 0; i < X_I.size(); i++) {

//      System.out.println("TRYING TO ADD ROW #" + X_I.get(i).intValue());

      // Compute ratio i
      float ratio_i;

      float sum_j = 0;

      for (int j = 0; j < this.J.size(); j++) {
        float a = A[X_I.get(i).intValue()][J.get(j).intValue()];
        int position_i = a_iJ.size() - 1;
        float b = a_iJ.get(position_i).floatValue();
        float c = a_Ij.get(j).floatValue();
        sum_j = sum_j + (float) Math.pow(a - b - c + a_IJ, 2);
      }

      ratio_i = (1 / (float)this.J.size()) * sum_j;

      if (ratio_i <= H) {

//        System.out.println("ADDING ROW #" + X_I.get(i).intValue());

        I_i.add(new Integer(X_I.get(i).intValue()));
      }
    }
    this.I.addAll(I_i);
  }

  /**
   * Insert Inverse Row.
   */
  private void insertInverseRow() {

    ArrayList<Integer> X_I = (ArrayList<Integer>) X.clone();

    // candidates to insertion
    X_I.removeAll(I);

    // rows to add
    ArrayList<Integer> I_i = new ArrayList<Integer> (0);

    for (int i = 0; i < X_I.size(); i++) {

//      System.out.println("TRYING TO ADD ROW #" + X_I.get(i).intValue());

      // Compute ratio i
      float ratio_i;

      float sum_j = 0;

      for (int j = 0; j < this.J.size(); j++) {
        float a = ( -1) * A[X_I.get(i).intValue()][J.get(j).intValue()];
        int position_i = a_iJ.size() - 1;
        float b = a_iJ.get(position_i).floatValue();
        float c = a_Ij.get(j).floatValue();
        sum_j = sum_j + (float) Math.pow(a - b - c + a_IJ, 2);
      }

      ratio_i = (1 / (float)this.J.size()) * sum_j;

      if (ratio_i <= H) {

//        System.out.println("ADDING ROW #" + X_I.get(i).intValue());

        // "inverse" the row
        Iterator iter = J.iterator();
        while (iter.hasNext()) {
          Integer column = (Integer) iter.next();
          A[X.get(i)][Y.get(column.intValue())] *= -1;
        }

        I_i.add(new Integer(X_I.get(i).intValue()));
      }
    }
    this.I.addAll(I_i);
  }

  /**
   * Insert Column.
   */
  protected void insertColumn() {

    ArrayList<Integer> Y_J = (ArrayList<Integer>) Y.clone();

    // candidates to insertion
    Y_J.removeAll(J);

    // columns to add
    ArrayList<Integer> columnsToAdd = new ArrayList<Integer> (0);

    for (int j = 0; j < Y_J.size(); j++) {

//      System.out.println("TRYING TO ADD COLUMN #" + Y_J.get(j).intValue());

      // compute ratio j
      float ratio_j;
      float sum_i = 0;

      for (int i = 0; i < this.I.size(); i++) {
        float a = A[I.get(i).intValue()][Y_J.get(j).intValue()];
        int position_j = a_Ij.size() - 1;
        float b = a_iJ.get(i).floatValue();
        float c = a_Ij.get(position_j).floatValue();

        sum_i = sum_i + (float) Math.pow(a - b - c + a_IJ, 2);
      }

      ratio_j = (1 / (float)this.I.size()) * sum_i;

      if (ratio_j <= H) {

//        System.out.println("ADDING ROW #" + Y_J.get(j).intValue());

        columnsToAdd.add(new Integer(Y_J.get(j).intValue()));
      }
    }
    this.J.addAll(columnsToAdd);
  }

  /**
   *
   * Computes a set of biclusters using the Cheng and Church Algorithm
   *
   */
  public void computeBiclusters() {

/*    System.out.println("#BICLUSTERS=" + this.numberOfBiclustersToExtract);
    System.out.println("#ALPHA=" + this.alpha);
    System.out.println("#DELTA=" + this.delta);
    System.out.println("#THRESHOLD=" + this.threshold);
 */

    this.setOfBiclusters = new ArrayList<Bicluster> (0);

    if (alpha < 1) {
      return;
    }
    if (delta < 0) {
      return;
    }

    if (this.X.size() < this.threshold) {
      this.performMultipleNodeDeletionOnRows = false;
    }

    if (this.Y.size() < this.threshold) {
      this.performMultipleNodeDeletionOnColumns = false;
    }

    for (int k = 0; k < this.numberOfBiclustersToExtract; k++) {

/*      System.out.println("###################### COMPUTING BICLUSTER #"
                         + (k + 1) + "##################################");
 */

      // initialize the bicluster to be A
      this.I = (ArrayList<Integer>)this.X.clone();
      this.J = (ArrayList<Integer>)this.Y.clone();

/*      System.out.println("#I=" + this.I.size());
      System.out.println("#J=" + this.J.size());
      System.out.println("H=" + this.H);
 */

      // compute H
      this.update_H();

      // multiple node deletion
      boolean colsDeleted = true;
      boolean rowsDeleted = true;

      while (H > delta && (rowsDeleted == true || colsDeleted == true)) {
/*        System.out.println("######################## H > delta:" + H
                           + ">" + delta + " ==> MULTIPLE NODE DELETION");
        System.out.println("#I=" + this.I.size());
        System.out.println("#J=" + this.J.size());
        System.out.println("H=" + this.H);
        System.out.println("DELETING ROWS...");
 */

        int rowsBefore = this.I.size();

        if (this.performMultipleNodeDeletionOnRows) {
          this.deleteRow();
        }

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

        if (this.performMultipleNodeDeletionOnColumns) {
          this.deleteColumn();
        }

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
      }

      // update H
      this.update_H();

      // single node deletion
      colsDeleted = true;
      rowsDeleted = true;

      while (H > delta && (rowsDeleted == true || colsDeleted == true)) {

/*        System.out.println("######################## H > delta:" + H
                           + ">" + delta + " ==> SINGLE NODE DELETION");
        System.out.println("#I=" + this.I.size());
        System.out.println("#J=" + this.J.size());
        System.out.println("H=" + this.H);
 */

        int rowsBefore = this.I.size();
        int colsBefore = this.J.size();

        singleNodeDeletion();

        int rowsAfter = this.I.size();
        int colsAfter = this.J.size();

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
      }

      // Node addition
      boolean colInserted = false;
      boolean rowInserted = false;

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

      // STORE BICLUSTER IN AN OBJECT BiclusterWithSymbols SO THAT SOME
      // METHODS OF THIS CLASS AND OF CLASS
      // BiclusteringWithSymbols CAN BE USED
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
              1]; // original
          // expression
          // matrix
        }
      }

      this.setOfBiclusters.add(new Bicluster(this, k + 1, rowIndexes,
                                             columnIndexes));

      // Replace A(I,J) with random values
/*      System.out
          .println(
          "################################## MASKING BICLUSTER WITH RANDOM VALUES");
 */

      this.replaceBiclusterWithRandomValues();
    }
  }

  /**
   *
   * @return Object
   */
  public Object clone() {
    CC_Biclustering o = (CC_Biclustering)super.clone();
    o.matrix = (ExpressionMatrix)this.matrix.clone();
    o.threshold = this.threshold;
    o.performMultipleNodeDeletionOnColumns = this.
        performMultipleNodeDeletionOnColumns;
    o.performMultipleNodeDeletionOnRows = this.
        performMultipleNodeDeletionOnRows;
    return o;
  }
}
