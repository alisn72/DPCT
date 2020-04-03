package smadeira.utils;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
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
public class MixtureModelOneGaussian {

  protected int sampleSize;

  protected double mean_1;
  protected double std_1;
  protected double[][] E_Z_i1;

  public MixtureModelOneGaussian() {

  }

  public MixtureModelOneGaussian(int sampleSize, double mean, double std,
                                 double[][] E_Z_i1) {
    this.sampleSize = sampleSize;
    this.mean_1 = mean;
    this.std_1 = std;
    this.E_Z_i1 = E_Z_i1;
  }

  public void setSampleSize(int sampleSize) {
    this.sampleSize = sampleSize;
  }

  public void setMean_1(double mean) {
    this.mean_1 = mean;
  }

  public void setStd_1(double std) {
    this.std_1 = std;
  }

  public void setE_Z_i1(double[][] E_Z_i1) {
    this.E_Z_i1 = E_Z_i1;
  }

  public int getSampleSize() {
    return (this.sampleSize);
  }

  public double getMean_1() {
    return (this.mean_1);
  }

  public double getStd_1() {
    return (this.std_1);
  }

  public double[][] getE_Z_i1() {
    return (this.E_Z_i1);
  }

  /**
   *
   * @param model MixtureModelOneGaussian
   * @param numFreeParameters int
   * @return double
   */
  public static double computeBIC_OverallMatrix(MixtureModelOneGaussian model,
                                                int numFreeParameters) {
    double BIC;
    double L_m_K = 0;
    int upsilon_m_K = numFreeParameters;

    int numberOfGenes = model.E_Z_i1.length;
    int numberOfConditions = model.E_Z_i1[0].length;

    for (int i = 0; i < numberOfGenes; i++) {
      for (int j = 0; j < numberOfConditions; j++) {
        L_m_K = L_m_K + Math.log(model.E_Z_i1[i][j]);
      }
    }
    // Math.log(double): Returns the natural logarithm (base e) of a double value.
    BIC = -2 * L_m_K +
        upsilon_m_K * Math.log(numberOfGenes * numberOfConditions);
    return (BIC);
  }

  /**
   *
   * @param setOfModels MixtureModelOneGaussian[]
   * @param numFreeParameters int
   * @return double[]
   */
  public static double[] computeBIC_ByGene(MixtureModelOneGaussian[]
                                           setOfModels, int numFreeParameters) {

    int numberOfGenes = setOfModels.length;
    int numberOfConditions;

    double[] BIC_ByGene = new double[numberOfGenes];
    double[] L_m_K_i = new double[numberOfGenes];
    int upsilon_m_K = numFreeParameters;

    for (int i = 0; i < numberOfGenes; i++) {
      L_m_K_i[i] = 0;
      numberOfConditions = setOfModels[i].sampleSize;
      for (int j = 0; j < numberOfConditions; j++) {
        L_m_K_i[i] = L_m_K_i[i] + Math.log(setOfModels[i].E_Z_i1[0][j]);
      }
    }
    // Math.log(double): Returns the natural logarithm (base e) of a double value.
    for (int i = 0; i < numberOfGenes; i++) {
      numberOfConditions = setOfModels[i].sampleSize;
      BIC_ByGene[i] = -2 * L_m_K_i[i] +
          upsilon_m_K * Math.log(numberOfConditions);
    }
    return (BIC_ByGene);
  }

  /**
   *
   * @param setOfModels MixtureModelOneGaussian[]
   * @param numFreeParameters int
   * @return double[]
   */
  public static double[] computeBIC_ByCondition(MixtureModelOneGaussian[]
                                                setOfModels,
                                                int numFreeParameters) {

    int numberOfConditions = setOfModels.length;
    int numberOfGenes = 0;

    double[] BIC_ByCondition = new double[numberOfConditions];
    double[] L_m_K_j = new double[numberOfConditions];
    int upsilon_m_K = numFreeParameters;

    for (int j = 0; j < numberOfConditions; j++) {
      L_m_K_j[j] = 0;
      numberOfGenes = setOfModels[j].sampleSize;
      for (int i = 0; i < numberOfGenes; i++) {
        L_m_K_j[j] = L_m_K_j[j] + Math.log(setOfModels[j].E_Z_i1[i][0]);
      }
    }
    // Math.log(double): Returns the natural logarithm (base e) of a double value.
    for (int j = 0; j < numberOfConditions; j++) {
      numberOfGenes = setOfModels[j].sampleSize;
      BIC_ByCondition[j] = -2 * L_m_K_j[j] +
          upsilon_m_K * Math.log(numberOfGenes);
    }
    return (BIC_ByCondition);
  }
}
