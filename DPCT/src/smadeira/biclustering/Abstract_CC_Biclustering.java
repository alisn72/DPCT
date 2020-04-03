package smadeira.biclustering;

import java.io.*;
import java.util.*;

/**
 * <p>Title: Abstract CC (Coherent Column) Biclustering</p>
 *
 * <p>Description: Defines a coherent column group of biclusters.</p>
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
 *
 */
public abstract class Abstract_CC_Biclustering
    extends Biclustering<IMatrix>
    implements
    Cloneable, Serializable, NodeObjectInterface {
  public static final long serialVersionUID = -5179122054415647591L;

  /**
   * Current bicluster expression matrix.
   */
  protected float[][] A;

  /**
   * Indexes of the genes in A.
   */
  protected ArrayList<Integer> X;

  /**
   * Indexes of the conditions in A.
   */
  protected ArrayList<Integer> Y;

  /**
   * The maximum acceptable mean squared residue.
   */
  protected float delta;

  /**
   * A parameter for multiple node deletion.
   */
  protected float alpha;

  /**
   * Indexes of the genes in the bicluster (I,J).
   */
  protected ArrayList<Integer> I;

  /**
   * Indexes of the conditions in the bicluster (I,J).
   */
  protected ArrayList<Integer> J;

  /**
   * MSR(I,J)
   * Minimum Squared Residue
   */
  protected float H;

  /**
   * Mean of all the elements in the bicluster.
   */
  protected float a_IJ;

  /**
   * Mean of all the elements in each line
   */
  protected ArrayList<Float> a_iJ;

  /**
   * Mean of all the elemtents in each column
   */
  protected ArrayList<Float> a_Ij;

  /**
   * Number of biclusters to extract.
   */
  protected int numberOfBiclustersToExtract;

  /**
   *
   * Constructor
   *
   * @param m AbstractExpressionMatrix
   * @param delta float
   * @param alpha float
   * @param numberOfBiclustersToExtract int
   * @throws Exception
   */
  public Abstract_CC_Biclustering(AbstractExpressionMatrix m, float delta,
                                  float alpha, int numberOfBiclustersToExtract) throws
      Exception {

    if (m.hasMissingValues) {
      throw new Exception("This algorithm does not support missing values: \n please remove the genes with missing values or fill them before applying the algorithm.");
    }

    this.matrix = m;
    this.delta = delta;
    this.alpha = alpha;
    this.numberOfBiclustersToExtract = numberOfBiclustersToExtract;

    this.A = new float[this.matrix.getNumberOfGenes()][this.matrix
        .getNumberOfConditions()];

    for (int i = 0; i < this.matrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.matrix.getNumberOfConditions(); j++) {
        this.A[i][j] = this.matrix.getGeneExpressionMatrix()[i][j];
      }
    }
    this.X = new ArrayList(this.matrix.getNumberOfGenes());
    this.I = new ArrayList(this.matrix.getNumberOfGenes());
    this.Y = new ArrayList(this.matrix.getNumberOfConditions());
    this.J = new ArrayList(this.matrix.getNumberOfConditions());

    for (int i = 0; i < this.matrix.getNumberOfGenes(); i++) {
      X.add(i, new Integer(i));
      I.add(i, new Integer(i));
    }

    for (int j = 0; j < this.matrix.getNumberOfConditions(); j++) {
      Y.add(j, new Integer(j));
      J.add(j, new Integer(j));
    }
    // compute a_IJ
    this.compute_a_IJ();
    // compute a_iJ
    this.compute_a_iJ();
    // compute a_Ij
    this.compute_a_Ij();
    // compute MSR(I,J)
    this.update_H();
  }

  /**
   *
   * @return float
   */
  public float getDelta() {
    return this.delta;
  }

  /**
   *
   * @return float
   */
  public float getAlpha() {
    return this.alpha;
  }

  /**
   *
   * @return int
   */
  public int getnumberOfBiclustersToExtract() {
    return this.numberOfBiclustersToExtract;
  }

  /**
   *
   * Abstract methods
   *
   */

  protected abstract void insertRow();

  protected abstract void insertColumn();

  protected abstract void deleteColumn();

  /**
   * Mask bicluster with random values.
   */
  protected void replaceBiclusterWithRandomValues() {
    float min_a_ij;
    float max_a_ij;
    // uniform distribution between min(A_ij) and max(A_ij)
    min_a_ij = this.A[0][0];
    max_a_ij = this.A[0][0];
    for (int i = 0; i < this.A.length; i++) {
      for (int j = 0; j < this.A[0].length; j++) {
        if (this.A[i][j] < min_a_ij) {
          min_a_ij = this.A[i][j];
        }

        if (this.A[i][j] > max_a_ij) {
          max_a_ij = this.A[i][j];
        }
      }
    }
    // uniform distribution between 0 and 800 for Cheng and Church data
    // min_a_ij = 0;
    // max_a_ij = 800;
    // mask bicluster
    Random r = new Random(); // random number generator
    for (int i = 0; i < I.size(); i++) {
      for (int j = 0; j < J.size(); j++) {
        float randomNumber = (max_a_ij - min_a_ij) * r.nextFloat()
            + min_a_ij;
        this.A[I.get(i).intValue()][J.get(j).intValue()] = randomNumber;
      }
    }
  }

  /**
   * Computes MSR - mean squared residue (BRUTE FORCE!!).
   */
  protected void update_H() {
    this.H = 0;
    // compute a_IJ
    this.compute_a_IJ();
    // compute a_iJ
    this.compute_a_iJ();
    // compute a_Ij
    this.compute_a_Ij();
    // compute MSR
    for (int i = 0; i < this.I.size(); i++) {
      for (int j = 0; j < this.J.size(); j++) {
        float r_a_ij = this.A[this.I.get(i).intValue()][this.J.get(j)
            .intValue()]
            - a_iJ.get(i).floatValue()
            - a_Ij.get(j).floatValue()
            + a_IJ;
        this.H = this.H + (float) Math.pow(r_a_ij, 2);
      }
    }
    this.H = this.H / (this.I.size() * this.J.size());
  }

  /**
   * Computes a_IJ.
   */
  protected void compute_a_IJ() {
    float sum_a_ij = 0;
    for (int i = 0; i < this.I.size(); i++) {
      for (int j = 0; j < this.J.size(); j++) {
        sum_a_ij = sum_a_ij
            + this.A[this.I.get(i).intValue()][this.J.get(j)
            .intValue()];
      }
    }
    this.a_IJ = sum_a_ij / (this.I.size() * this.J.size());
  }

  /**
   * Compute a_iJ
   *
   */
  protected void compute_a_iJ() {
    this.a_iJ = new ArrayList(this.I.size());
    for (int i = 0; i < this.I.size(); i++) {
      this.a_iJ.add(i, new Float(0));
      for (int j = 0; j < this.J.size(); j++) {
        float a = ( (Float) a_iJ.get(i)).floatValue();
        float b = A[this.I.get(i).intValue()][this.J.get(j).intValue()];
        this.a_iJ.set(i, new Float(a + b));
      }
      this.a_iJ.set(i,
                    new Float(a_iJ.get(i).floatValue() / this.J.size()));
    }
  }

  /**
   * Computes a_Ij
   */
  protected void compute_a_Ij() {
    this.a_Ij = new ArrayList(this.J.size());
    for (int j = 0; j < this.J.size(); j++) {
      this.a_Ij.add(j, new Float(0));
      for (int i = 0; i < this.I.size(); i++) {
        float a = ( (Float) a_Ij.get(j)).floatValue();
        float b = this.A[this.I.get(i).intValue()][this.J.get(j)
            .intValue()];
        this.a_Ij.set(j, new Float(a + b));
      }
      this.a_Ij.set(j,
                    new Float(a_Ij.get(j).floatValue() / this.I.size()));
    }
  }

  /**
   * Multiple Row Deletion.
   *
   */
  protected void deleteRow() {
    ArrayList rowsToDelete = new ArrayList(0);

    for (int i = 0; i < this.I.size(); i++) {
//      System.out.println("TRYING TO DELETE ROW #"
//                         + ( (Integer)this.I.get(i)).intValue());
      // COMPUTE RATIO i
      float ratio_i;
      float sum_j = 0;
      for (int j = 0; j < this.J.size(); j++) {
        float a = this.A[this.I.get(i).intValue()][this.J.get(j)
            .intValue()];
        float b = this.a_iJ.get(i).floatValue();
        float c = this.a_Ij.get(j).floatValue();
        sum_j = sum_j + (float) Math.pow(a - b - c + this.a_IJ, 2);
      }
      ratio_i = (1 / this.H) * (1 / (float)this.J.size()) * sum_j;
      if (ratio_i > alpha) {
//        System.out.println("DELETING ROW #" + this.I.get(i).intValue());
        rowsToDelete.add(new Integer(i));
      }
    }
    this.I.removeAll(rowsToDelete);
  }

}
