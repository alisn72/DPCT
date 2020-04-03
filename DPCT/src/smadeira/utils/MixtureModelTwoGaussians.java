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
public class MixtureModelTwoGaussians
    extends MixtureModelOneGaussian {

  protected double mean_2;
  protected double std_2;
  protected double[][] E_Z_i2;

  public MixtureModelTwoGaussians() {

  }

  public MixtureModelTwoGaussians(int sampleSize, double mean_1, double std_1,
                                  double[][] E_Z_i1,
                                  double mean_2, double std_2,
                                  double[][] E_Z_i2) {
    super(sampleSize, mean_1, std_1, E_Z_i1);
    this.mean_2 = mean_2;
    this.std_2 = std_2;
    this.E_Z_i2 = E_Z_i2;
  }

  public void setMean_2(double mean) {
    this.mean_2 = mean;
  }

  public void setStd_2(double std) {
    this.std_2 = std;
  }

  public void setE_Z_i2(double[][] E_Z_i2) {
    this.E_Z_i2 = E_Z_i2;
  }

  public double getMean_2() {
    return (this.mean_2);
  }

  public double getStd_2() {
    return (this.std_2);
  }

  public double[][] getE_Z_i2() {
    return (this.E_Z_i2);
  }

  /**
   *
   * @param model MixtureModelTwoGaussians
   * @param numFreeParameters int
   * @return double
   */
  public static double computeBIC_OverallMatrix(MixtureModelTwoGaussians model,
                                                int numFreeParameters) {
    double BIC;
    double L_m_K = 0;
    int upsilon_m_K = numFreeParameters;

    int numberOfGenes = model.E_Z_i1.length;
    int numberOfConditions = model.E_Z_i1[0].length;

    for (int i = 0; i < numberOfGenes; i++) {
      for (int j = 0; j < numberOfConditions; j++) {
        L_m_K = L_m_K + Math.log(Math.max(model.E_Z_i1[i][j], model.E_Z_i2[i][j]));
      }
    }
    // Math.log(double): Returns the natural logarithm (base e) of a double value.
    BIC = -2 * L_m_K +
        upsilon_m_K * Math.log(numberOfGenes * numberOfConditions);
    return (BIC);
  }

  /**
   *
   * @param setOfModels MixtureModelTwoGaussians[]
   * @param numFreeParameters int
   * @return double[]
   */
  public static double[] computeBIC_ByGene(MixtureModelTwoGaussians[]
                                           setOfModels, int numFreeParameters) {

    int numberOfGenes = setOfModels.length;
    int numberOfConditions = 0;

    double[] BIC_ByGene = new double[numberOfGenes];
    double[] L_m_K_i = new double[numberOfGenes];
    int upsilon_m_K = numFreeParameters;

    for (int i = 0; i < numberOfGenes; i++) {
      L_m_K_i[i] = 0;
      numberOfConditions = setOfModels[i].sampleSize;
      for (int j = 0; j < numberOfConditions; j++) {
        L_m_K_i[i] = L_m_K_i[i] +
            Math.log(Math.max(setOfModels[i].E_Z_i1[0][j],
                              setOfModels[i].E_Z_i2[0][j]));
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
   * @param setOfModels MixtureModelTwoGaussians[]
   * @param numFreeParameters int
   * @return double[]
   */
  public static double[] computeBIC_ByCondition(MixtureModelTwoGaussians[]
                                                setOfModels,
                                                int numFreeParameters) {

    int numberOfGenes = 0;
    int numberOfConditions = setOfModels.length;

    double[] BIC_ByCondition = new double[numberOfConditions];
    double[] L_m_K_j = new double[numberOfConditions];
    int upsilon_m_K = numFreeParameters;

    for (int j = 0; j < numberOfConditions; j++) {
      L_m_K_j[j] = 0;
      numberOfGenes = setOfModels[j].sampleSize;
      for (int i = 0; i < numberOfGenes; i++) {
        L_m_K_j[j] = L_m_K_j[j] +
            Math.log(Math.max(setOfModels[j].E_Z_i1[i][0],
                              setOfModels[j].E_Z_i2[i][0]));
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
