package smadeira.biclustering;

import java.io.*;

import javax.swing.text.*;
import javax.swing.text.html.*;

import smadeira.ontologizer.*;

/**
 * <p>Title: PreProcessed Expression Matrix</p>
 *
 * <p>Description: Defines a preprocessed expression matrix and methods
 *                 for preprocessing expression matrices.</p>
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
public class PreProcessedExpressionMatrix
    extends AbstractExpressionMatrix implements Cloneable, Serializable,
    NodeObjectInterface {
  public static final long serialVersionUID = -8287340381533392908L;

  // FIELDS

  /**
   * Gene expression matrix from which this pre-processed expression matrix was created
   * (can either be an object from two classes: ExpressionMatrix and PreProcessedExpressionMatrix ).
   */
  private AbstractExpressionMatrix originalExpressionMatrix;

  // CONSTRUCTORS

  /**
   *
   * @param originalMatrix AbstractExpressionMatrix
   */
  public PreProcessedExpressionMatrix(AbstractExpressionMatrix originalMatrix) {
    this.originalExpressionMatrix = originalMatrix;
    this.genesNames = this.originalExpressionMatrix.getGenesNames();
    this.conditionsNames = this.originalExpressionMatrix.getConditionsNames();
    this.hasMissingValues = this.originalExpressionMatrix.hasMissingValues();
    this.missingValue = this.originalExpressionMatrix.getMissingValue();
    this.geneExpressionMatrix = new float[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        this.geneExpressionMatrix[i][j] = this.originalExpressionMatrix.
            getGeneExpressionMatrix()[i][j];
      }
    }
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @param pathTo_GeneOntologyFile String
   * @param pathTo_GeneAssociationFile String
   * @param goTermAssocs boolean
   * @return GOFrontEnd
   * @throws Exception
   */
  public GOFrontEnd computeGeneOntologyTerms(String pathTo_GeneOntologyFile,
                                             String pathTo_GeneAssociationFile,
                                             boolean goTermAssocs) throws
      Exception {

    if (!this.GoTermsComputed) {
      if (this.getNumberOfGenes() ==
          this.originalExpressionMatrix.getNumberOfGenes()) {
        if (!this.originalExpressionMatrix.GoTermsComputed) {
          GOFrontEnd go = this.originalExpressionMatrix.
              computeGeneOntologyTerms(pathTo_GeneOntologyFile,
                                       pathTo_GeneAssociationFile,
                                       goTermAssocs);
          this.geneOntologyTerms = this.originalExpressionMatrix.
              getGeneOntologyProperties();
          this.GoTermsComputed = true;
          return go;
        }
        else {
          this.geneOntologyTerms = this.originalExpressionMatrix.
              getGeneOntologyProperties();
          this.GoTermsComputed = true;
          return null;
        }
      }
      else {
        GOFrontEnd go = new GOFrontEnd(this.genesNames, pathTo_GeneOntologyFile,
                                       pathTo_GeneAssociationFile);
        this.computeGeneOntologyTerms(go, goTermAssocs);
        return go;
      }
    }
    else {
      return null;
    }
  }

  //###############################################################################################################################
  //###############################################################################################################################



  /**
   *
   * @return AbstractExpressionMatrix
   */
  public AbstractExpressionMatrix getOriginalExpressionMatrix() {
    return (this.originalExpressionMatrix);
  }

  //###############################################################################################################################

  /**
   *
   * @return String
   */
  public String getOrganism() {
    if (this.originalExpressionMatrix instanceof ExpressionMatrix) {
      return ( ( (ExpressionMatrix)this.originalExpressionMatrix).getOrganism());
    }
    else {
      if (this.originalExpressionMatrix instanceof PreProcessedExpressionMatrix) {
        return ( ( (PreProcessedExpressionMatrix)this.originalExpressionMatrix).
                getOrganism());
      }
      else {
        return null;
      }
    }
  }

  // #####################################################################################################################

  /**
   *
   * @return boolean
   */
  public boolean hasMissingValues() {
    return this.hasMissingValues;
  }

  //###############################################################################################################################

  /**
   *
   * @return String
   */
  public String getGeneIdentifier() {
    if (this.originalExpressionMatrix instanceof ExpressionMatrix) {
      return ( ( (ExpressionMatrix)this.originalExpressionMatrix).
              getGeneIdentifier());
    }
    else {
      if (this.originalExpressionMatrix instanceof PreProcessedExpressionMatrix) {
        return ( ( (PreProcessedExpressionMatrix)this.originalExpressionMatrix).
                getGeneIdentifier());
      }
      else {
        return null;
      }
    }
  }

  //###############################################################################################################################

  /**
   *
   * @return String
   */
  public String getDataType() {
    if (this.originalExpressionMatrix instanceof ExpressionMatrix) {
      return ( ( (ExpressionMatrix)this.originalExpressionMatrix).getDataType());
    }
    else {
      if (this.originalExpressionMatrix instanceof PreProcessedExpressionMatrix) {
        return ( ( (PreProcessedExpressionMatrix)this.originalExpressionMatrix).
                getDataType());
      }
      else {
        return null;
      }
    }
  }

  //############################################# SMOOTHING ############################################################

  /**
   * <p>
   * Performs smoothing on the gene expression matrix using a window (performed gene by gene).
   * <p><\p>
   * Let X_n = a_ij
   * <p><\p>
   * For example if window size = 5.
   * <p><\p>
   * X_n = a_n-2 * X_n-2 + a_n-1 * X_n-1 + a_n * X_n + a_n+1 * X_n+1 + a_n+2 * X_n+2
   * <p><\p>
   * For a window size W:
   * <p><\p>
   * X_n = a_(n-W/2) * X_(n-W/2) + ... + a_n * X_n + ... + a_(n+W/2) * X_(n+W/2)
   * <p>
   *
   * @param window int Window size: W.
   * @param weights float[] Array with set of weights {a_(n-W/2),..., a_n, a_(n+W/2)}
   *
   * @throws Exception
   */
  public void computeSmoothedExpressionMatrix(int window, float[] weights) throws
      Exception {

    //EXCEPTIONS!!!
    // 1) window must be odd number and >= 3
    // 2) window = weigths.length
    // 3) window > this.getNumberOfConditions()
    // 4) missing values
    // 5) weights must sum 1

    if (window % 2 == 0) {
      throw new Exception("INVALID WINDOW: window must be an odd number");
    }

    if (window != weights.length) {
      throw new Exception(
          "INVALID NUMBER OF WEIGHTS: window must be equal to number of weights");
    }

    if (window < 3 ||
        window > this.originalExpressionMatrix.getNumberOfConditions()) {
      throw new Exception(
          "INVALID WINDOW: window must be an odd number between 3 and " +
          this.getNumberOfConditions());
    }

    if (this.hasMissingValues) {
      throw new Exception("CANNOT PERFORM SMOOTHING: matrix has missing values");
    }

    float weights_sum = 0;
    for (int i = 0; i < weights.length; i++) {
      weights_sum = weights_sum + weights[i];
    }

    if (weights_sum != 1) {
      throw new Exception("INVALID WEIGHTS: weights must sum 1");
    }

    //.................................

    float[][] smoothedExpressionMatrix = new float[this.getNumberOfGenes()][this.
        getNumberOfConditions()];

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        // test if the window can be applied completely
        if (j - (int) (window / 2) >= 0 &&
            j + (int) (window / 2) < this.getNumberOfConditions()) {
          int weightPosition = 0;
          float smoothed_a_ij = 0;

          // TEST IF THERE ARE MISSING AND NEED SOME CORRECTION
          float correction = 1;
          float weightSum = 0;
          int numberOfMissings = 0;
          for (int w = j - (int) (window / 2); w <= j + (int) (window / 2); w++) {
            if (this.geneExpressionMatrix[i][w] == this.missingValue) {
              weightSum = weightSum + weights[w];
              numberOfMissings++;
            }
          }
          if (numberOfMissings != 0) {
            correction = 1 / (1 - weightSum);
          }
          //----------------------------------------

          for (int k = j - (int) (window / 2); k <= j + (int) (window / 2); k++) {
            smoothed_a_ij = smoothed_a_ij + correction * // correction included due to potential missings
                weights[weightPosition] * this.geneExpressionMatrix[i][k];
            weightPosition++;
          }
          smoothedExpressionMatrix[i][j] = smoothed_a_ij;
        }
        else {
          // window cannot be applied at the left of a_ij but can be applied at right
          if (j - (int) (window / 2) < 0 &&
              j + (int) (window / 2) < this.getNumberOfConditions()) {
            float correction = 1;
            float weightSum = 0;
            for (int w = 0; w < Math.abs(j - (int) (window / 2)); w++) {
              weightSum = weightSum + weights[w];
            }

            // CORRECTION FOR POTENTIAL MISSING VALUES
            int weightPosition = Math.abs(j - (int) (window / 2));
            for (int k = 0; k <= j + (int) (window / 2); k++) {
              if (this.geneExpressionMatrix[i][k] != this.missingValue) {
                weightSum = weightSum + weights[weightPosition];
              }
            }
            //----------------------------------------

            correction = 1 / (1 - weightSum);

            weightPosition = Math.abs(j - (int) (window / 2));
            float smoothed_a_ij = 0;
            for (int k = 0; k <= j + (int) (window / 2); k++) {
              if (this.geneExpressionMatrix[i][k] != this.missingValue) {
                smoothed_a_ij = smoothed_a_ij +
                    correction * weights[weightPosition] *
                    this.geneExpressionMatrix[i][k];
              }
              weightPosition++;
            }
            smoothedExpressionMatrix[i][j] = smoothed_a_ij;
          }
          else {
            // window cannot be applied at the right of a_ij but can be applied at left
            if (j - (int) (window / 2) >= 0 &&
                j + (int) (window / 2) >= this.getNumberOfConditions()) {
              float correction = 0;
              float weightSum = 0;
              for (int w = window - 1;
                   w >=
                   this.getNumberOfConditions() -
                   ( (j + (int) (window / 2)) - (this.getNumberOfConditions() - 1));
                   w--) {
                weightSum = weightSum + weights[w];
              }

              // CORRECTION FOR POTENTIAL MISSING VALUES
              int weightPosition = 0;
              for (int k = j - (int) (window / 2);
                   k < this.getNumberOfConditions(); k++) {
                if (this.geneExpressionMatrix[i][k] != this.missingValue) {
                  weightSum = weightSum + weights[weightPosition];
                }
              }
              //----------------------------------------
              correction = 1 / (1 - weightSum);

              weightPosition = 0;
              float smoothed_a_ij = 0;
              for (int k = j - (int) (window / 2);
                   k < this.getNumberOfConditions(); k++) {
                if (this.geneExpressionMatrix[i][k] != this.missingValue) {
                  smoothed_a_ij = smoothed_a_ij +
                      correction * weights[weightPosition] *
                      this.geneExpressionMatrix[i][k];
                }
                weightPosition++;
              }
              smoothedExpressionMatrix[i][j] = smoothed_a_ij;
            }
          }
        }
      }
    }
    this.geneExpressionMatrix = smoothedExpressionMatrix;
  }

  /**
   * Computes smoothing weights given a window.
   *
   * USED IN:
   *
   * public void computeSmoothedExpressionMatrix(int window);
   *
   * IF window = 3 => weigths = [0.25, 0.5, 0.25]
   *
   * IF window = 5 => weigths = [0.05, 0.2, 0.5, 0.2, 0.05]
   *
   * IF window = 7 => weigths = [0.25, 0.075, 0.15, 0.5, 0.15, 0.075, 0,25]
   *
   * IF window > 7 => weigths = [0.5/(window-1), ..., 0.5/(window-1), 0.5, 0.5/(window-1), ..., 0.5/(window-1)]
   *
   * @param window int
   * @return float[]
   */
  private float[] computeSmoothingWeights(int window) {
    float[] weigths = new float[window];

    if (window == 3) {
      weigths[0] = (float) 0.25;
      weigths[1] = (float) 0.5;
      weigths[2] = (float) 0.25;
    }
    else {
      if (window == 5) {
        weigths[0] = (float) 0.05;
        weigths[1] = (float) 0.2;
        weigths[2] = (float) 0.5;
        weigths[3] = (float) 0.2;
        weigths[4] = (float) 0.05;
      }
      else {
        if (window == 7) {
          weigths[0] = (float) 0.025;
          weigths[1] = (float) 0.075;
          weigths[2] = (float) 0.15;
          weigths[3] = (float) 0.5;
          weigths[4] = (float) 0.15;
          weigths[5] = (float) 0.075;
          weigths[6] = (float) 0.025;
        }
        else {
          for (int i = 0; i < window; i++) {
            weigths[i] = (float) 0.5 / (window - 1);
          }
          weigths[window / 2] = (float) 0.5;
        }
      }
    }
    return (weigths);
  }

  /**
   * <p>
   * Performs smoothing on the gene expression matrix using a window (performed gene by gene).
   * <p><\p>
   * Let X_n = a_ij
   * <p><\p>
   * For example if window size = 5.
   * <p><\p>
   * X_n = a_n-2 * X_n-2 + a_n-1 * X_n-1 + a_n * X_n + a_n+1 * X_n+1 + a_n+2 * X_n+2
   * <p><\p>
   * For a window size W:
   * <p><\p>
   * X_n = a_(n-W/2) * X_(n-W/2) + ... + a_n * X_n + ... + a_(n+W/2) * X_(n+W/2)
   * <p>
   *
   * THE SMOOTHING WEIGHTS ARE COMPUTED AUTOMATICALLY!
   *
   * @param window int Window size: W.
   * @throws Exception
   */
  public void computeSmoothedExpressionMatrix(int window) throws Exception {
    this.computeSmoothedExpressionMatrix(window,
                                         this.computeSmoothingWeights(window));
  }

  // ############################# REMOVE GENES WITH MISSING VALUES #################################################

  /**
   * Remove genes with missing values.
   */
  public void removeGenesWithMissingValues() {

    int numberOfGenesWithMissingValues = 0;

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        if (this.geneExpressionMatrix[i][j] == missingValue) {
          numberOfGenesWithMissingValues++;
          j = this.getNumberOfConditions(); // no need to see the rest of the row
        }
      }
    }

    float[][] newExpressionMatrix = new float[this.getNumberOfGenes() -
        numberOfGenesWithMissingValues][this.getNumberOfConditions()];

    String[] newGeneNames = new String[this.getNumberOfGenes() -
        numberOfGenesWithMissingValues];
    int numberOfCopiedGenes = 0;
    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      int foundMissingValue = 0;
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        if (this.geneExpressionMatrix[i][j] == this.missingValue) {
          foundMissingValue = 1;
          j = this.getNumberOfConditions(); // no need to see the rest of the row
        }
      }
      if (foundMissingValue == 0) {
        newExpressionMatrix[numberOfCopiedGenes] = this.geneExpressionMatrix[i]; // copy gene i
        newGeneNames[numberOfCopiedGenes] = this.genesNames[i];
        numberOfCopiedGenes++;
      }
    }
    this.geneExpressionMatrix = newExpressionMatrix;
    this.genesNames = newGeneNames;
    this.hasMissingValues = false;
  }

  // ############################# REMOVE GENES WITH MISSING VALUES #################################################

  /**
   * Remove genes with missing values.
   *
   * @param missingValue float
   * @param geneExpressionMatrix float[][]
   * @return float[][]
   */
  public static float[][] removeGenesWithMissingValues(float missingValue,
      float[][] geneExpressionMatrix) {

    int numberOfGenesWithMissingValues = 0;
    int numberOfGenes = geneExpressionMatrix.length;
    int numberOfConditions = geneExpressionMatrix[0].length;

    for (int i = 0; i < numberOfGenes; i++) {
      for (int j = 0; j < numberOfConditions; j++) {
        if (geneExpressionMatrix[i][j] == missingValue) {
          numberOfGenesWithMissingValues++;
          j = numberOfConditions; // no need to see the rest of the row
        }
      }
    }

    float[][] newExpressionMatrix = new float[numberOfGenes -
        numberOfGenesWithMissingValues][numberOfConditions];
    int numberOfCopiedGenes = 0;
    for (int i = 0; i < numberOfGenes; i++) {
      int foundMissingValue = 0;
      for (int j = 0; j < numberOfConditions; j++) {
        if (geneExpressionMatrix[i][j] == missingValue) {
          foundMissingValue = 1;
          j = numberOfConditions; // no need to see the rest of the row
        }
      }
      if (foundMissingValue == 0) {
        newExpressionMatrix[numberOfCopiedGenes] = geneExpressionMatrix[i]; // copy gene i
        numberOfCopiedGenes++;
      }
    }
    return (newExpressionMatrix);
  }

  // ############################# REMOVE GENES WITH MORE THAN X% OF MISSING VALUES #############################################

  /**
   * Remove genes with more than a given percentage of missing values.
   * @param percentage float Percentage (between 1 and 100).
   */
  public void removeGenesWithMoreThanAPercentageOfMissingValues(float
      percentage) {

    int numberOfGenesToBeRemoved = 0;

    int allowedMissings = Math.round(percentage / 100 *
                                     this.getNumberOfConditions());

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      int numberOfMissingValues = 0;
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        if (this.geneExpressionMatrix[i][j] == missingValue) {
          numberOfMissingValues++;
        }
      }
      if (numberOfMissingValues > allowedMissings) {
        numberOfGenesToBeRemoved++;
      }
    }

    float[][] newExpressionMatrix = new float[this.getNumberOfGenes() -
        numberOfGenesToBeRemoved][this.getNumberOfConditions()];

    String[] newGeneNames = new String[this.getNumberOfGenes() -
        numberOfGenesToBeRemoved];
    int numberOfCopiedGenes = 0;
    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      int numberOfMissingValues = 0;
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        if (this.geneExpressionMatrix[i][j] == this.missingValue) {
          numberOfMissingValues++;
        }
      }
      if (numberOfMissingValues <= allowedMissings) {
        newExpressionMatrix[numberOfCopiedGenes] = this.geneExpressionMatrix[i]; // copy gene i
        newGeneNames[numberOfCopiedGenes] = this.genesNames[i];
        numberOfCopiedGenes++;
      }
    }

    this.geneExpressionMatrix = newExpressionMatrix;
    this.genesNames = newGeneNames;
    this.hasMissingValues = this.checkIfMatrixHasMissingValues(this.
        missingValue);
  }

  // ############################# FILL MISSING VALUES WITH A GIVEN VALUE #############################################

  /**
   * Fills missing values with a given value.
   *
   * @param value float
   */
  public void fillMissingValuesWithValue(float value) {

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        if (this.geneExpressionMatrix[i][j] == missingValue) {
          this.geneExpressionMatrix[i][j] = value;
        }
      }
    }
    this.hasMissingValues = false;
    this.missingValue = value;
  }

  // ############################# FILL MISSING VALUES WITH AvERAGE GENE EXPRESSION #############################################

  /**
   * Fills missing values with average gene expression.
   */
  public void fillMissingsAverageGeneExpression() {

    float[] averageGeneExpression = new float[this.getNumberOfGenes()];
    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      int count = 0;
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        if (this.geneExpressionMatrix[i][j] != this.missingValue) {
          averageGeneExpression[i] = averageGeneExpression[i] +
              this.geneExpressionMatrix[i][j];
          count++;
        }
      }
      averageGeneExpression[i] = averageGeneExpression[i] / count;
    }

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        if (this.geneExpressionMatrix[i][j] == missingValue) {
          this.geneExpressionMatrix[i][j] = averageGeneExpression[i];
        }
      }
    }
    this.hasMissingValues = false;
  }

  // ############################# FILL MISSING VALUES WITH AVERAGE GENE EXPRESSION OF K NEIGHBORS #############################################

  /**
   *
   * @param numberOfNeighbors int
   */
  public void fillMissingsNeighborsAverageGeneExpression(int numberOfNeighbors) {

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        if (this.geneExpressionMatrix[i][j] == missingValue) {
          int numberOfLeftNeighbors = j;
          int numberOfRightNeighbors = this.getNumberOfConditions() - j - 1;
          float expressionSum = 0;
          int neighbors = 0;
          if (numberOfLeftNeighbors == 0) {
            for (int k = j + 1;
                 k <= j + numberOfNeighbors && k < this.getNumberOfConditions();
                 k++) {
              if (this.geneExpressionMatrix[i][k] != this.missingValue) {
                expressionSum = expressionSum + this.geneExpressionMatrix[i][k];
                neighbors++;
              }
            }
          }
          else {
            if (numberOfRightNeighbors == 0) {
              for (int k = j - 1; k >= j - numberOfNeighbors && k >= 0; k--) {
                if (this.geneExpressionMatrix[i][k] != this.missingValue) {
                  expressionSum = expressionSum +
                      this.geneExpressionMatrix[i][k];
                  neighbors++;
                }
              }
            }
            else {
              // GO LEFT
              int left = 0;
              for (int k = j - 1;
                   k >= j - Math.round(numberOfNeighbors / 2) && k >= 0; k--) {
                if (this.geneExpressionMatrix[i][k] != this.missingValue) {
                  expressionSum = expressionSum +
                      this.geneExpressionMatrix[i][k];
                  left++;
                  neighbors++;
                }
              }
              // GO RIGHT
              for (int k = j + 1;
                   k <= j + (numberOfNeighbors - left) &&
                   k < this.getNumberOfConditions();
                   k++) {
                if (this.geneExpressionMatrix[i][k] != this.missingValue) {
                  expressionSum = expressionSum +
                      this.geneExpressionMatrix[i][k];
                  neighbors++;
                }
              }
            }
          }
          this.geneExpressionMatrix[i][j] = expressionSum / neighbors;
        }
      }
    }
    this.hasMissingValues = false;
  }

  // ############################# NORMALIZE MATRIX TO ZERO MEAN AND UNIT STANDARD DEVIATION ############################################

  /**
   * Normalizes gene expression values to zero mean and unit standard deviation (standardization).
   * @throws Exception
   */
  public void normalizeGeneExpressionMatrix() {

    float expressionSum = 0;
    int numberOfNonMissings = 0;
    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        if (this.geneExpressionMatrix[i][j] != this.missingValue) {
          expressionSum = expressionSum + this.geneExpressionMatrix[i][j];
          numberOfNonMissings++;
        }
      }
    }

    if (numberOfNonMissings != 0) {
      float mean = expressionSum / numberOfNonMissings;

      double deviationSum = 0;
      for (int i = 0; i < this.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.getNumberOfConditions(); j++) {
          if (this.geneExpressionMatrix[i][j] != this.missingValue) {
            deviationSum = deviationSum +
                Math.pow(this.geneExpressionMatrix[i][j] - mean, 2);
          }
        }
      }
      float std = (float) Math.sqrt(deviationSum / numberOfNonMissings);

      for (int i = 0; i < this.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.getNumberOfConditions(); j++) {
          if (this.geneExpressionMatrix[i][j] != this.missingValue) {
            if (std != 0) {
              this.geneExpressionMatrix[i][j] = (this.geneExpressionMatrix[i][j] -
                                                 mean) / std;
            }
            else {
              this.geneExpressionMatrix[i][j] = this.geneExpressionMatrix[i][j] -
                  mean;
            }
          }
        }
      }
    }
  }

  // ############################# COMPUTE ORIGINAL EXPRESSION MATRIX NORMALIZE TO ZERO MEAN AND UNIT STANDARD DEVIATION - BY GENE ############################################

  /**
   * Normalizes a gene expression natrix by gene.
   *
   * @param geneExpressionMatrix float[][]
   * @param missingValue float
   * @return float[][]
   */
  public static float[][] normalizeGeneExpressionMatrixByGene(float[][]
      geneExpressionMatrix, float missingValue) {

    float a_iJ[] = new float[geneExpressionMatrix.length]; // mean of row i
    float std_iJ[] = new float[geneExpressionMatrix.length]; // standard deviation of row i

    for (int i = 0; i < geneExpressionMatrix.length; i++) {
      float sum_a_iJ = 0;
      int numberOfNonMissings_i = 0;
      for (int j = 0; j < geneExpressionMatrix[0].length; j++) {
        if (geneExpressionMatrix[i][j] != missingValue) {
          sum_a_iJ = sum_a_iJ + geneExpressionMatrix[i][j];
          numberOfNonMissings_i++;
        }
      }
      if (numberOfNonMissings_i != 0) {
        a_iJ[i] = sum_a_iJ / numberOfNonMissings_i;
      }
      else {
        a_iJ[i] = missingValue;
      }
    }

    for (int i = 0; i < geneExpressionMatrix.length; i++) {
      double deviation_sum_i = 0;
      int numberOfNonMissings_i = 0;
      for (int j = 0; j < geneExpressionMatrix[0].length; j++) {
        if (geneExpressionMatrix[i][j] != missingValue) {
          deviation_sum_i = deviation_sum_i +
              Math.pow(geneExpressionMatrix[i][j] - a_iJ[i], 2);
          numberOfNonMissings_i++;
        }
      }
      if (numberOfNonMissings_i != 0) {
        std_iJ[i] = (float) Math.sqrt(deviation_sum_i / numberOfNonMissings_i);
      }
      else {
        std_iJ[i] = 0;
      }
    }

    float[][] normalizedMatrix =
        new float[geneExpressionMatrix.length][geneExpressionMatrix[0].length];
    for (int i = 0; i < geneExpressionMatrix.length; i++) {
      for (int j = 0; j < geneExpressionMatrix[0].length; j++) {
        if (geneExpressionMatrix[i][j] != missingValue) {
          if (std_iJ[i] != 0) {
            normalizedMatrix[i][j] = (geneExpressionMatrix[i][j] - a_iJ[i]) /
                std_iJ[i];
          }
          else {
            normalizedMatrix[i][j] = geneExpressionMatrix[i][j] - a_iJ[i];
          }
        }
      }
    }
    return (normalizedMatrix);
  }

  // ############################# NORMALIZE MATRIX TO ZERO MEAN AND UNIT STANDARD DEVIATION - BY GENE ############################################

  /**
   * Normalizes gene expression values to zero mean and unit standard deviation BY GENE (standardization by gene).
   *
   * @throws Exception
   */
  public void normalizeGeneExpressionMatrixByGene() {

    float a_iJ[] = new float[this.getNumberOfGenes()]; // mean of row i
    float std_iJ[] = new float[this.getNumberOfGenes()]; // standard deviation of row i

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      float sum_a_iJ = 0;
      int numberOfNonMissings = 0;
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        if (this.geneExpressionMatrix[i][j] != this.missingValue) {
          sum_a_iJ = sum_a_iJ + this.geneExpressionMatrix[i][j];
          numberOfNonMissings++;
        }
      }
      if (numberOfNonMissings != 0) {
        a_iJ[i] = sum_a_iJ / numberOfNonMissings;
      }
      else {
        a_iJ[i] = this.getMissingValue();
      }
    }

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      double deviation_sum_i = 0;
      int numberOfNonMissings = 0;
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        if (this.geneExpressionMatrix[i][j] != this.missingValue) {
          deviation_sum_i = deviation_sum_i +
              Math.pow(this.geneExpressionMatrix[i][j] - a_iJ[i], 2);
          numberOfNonMissings++;
        }
      }
      if (numberOfNonMissings != 0) {
        std_iJ[i] = (float) Math.sqrt(deviation_sum_i / numberOfNonMissings);
      }
      else {
        std_iJ[i] = 0;
      }
    }

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        if (this.geneExpressionMatrix[i][j] != this.missingValue) {
          if (std_iJ[i] != 0) {
            this.geneExpressionMatrix[i][j] = (this.geneExpressionMatrix[i][j] -
                                               a_iJ[i]) / std_iJ[i];
          }
          else {
            this.geneExpressionMatrix[i][j] = this.geneExpressionMatrix[i][j] -
                a_iJ[i];
          }
        }
      }
    }
  }

  // ############################# NORMALIZE MATRIX TO A GIVEN MEAN AND STANDARD DEVIATION ############################################

  /**
   * Normalizes gene expression values to a given mean and standard deviation.
   *
   * @param mean float
   * @param std float
   * @throws Exception
   */
  public void normalizeGeneExpressionMatrix(float mean, float std) throws
      Exception {

    if (this.hasMissingValues) {
      throw new Exception("CANNOT NORMALIZE MATRIX: matrix has missing values");
    }

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        if (this.geneExpressionMatrix[i][j] != this.missingValue) {
          if (std != 0) {
            this.geneExpressionMatrix[i][j] = (this.geneExpressionMatrix[i][j] -
                                               mean) / std;
          }
          else {
            this.geneExpressionMatrix[i][j] = this.geneExpressionMatrix[i][j] -
                mean;
          }
        }
      }
    }
  }

  // ############################# LOG-TRANSFORMATION ############################################

  /**
   * Transforms gene expression values a_ij to log(a_ij)
   * @throws Exception
   */
  public void logTransform() throws Exception {

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        if (this.geneExpressionMatrix[i][j] != this.missingValue) {
          if (this.geneExpressionMatrix[i][j] < 0) {
            this.geneExpressionMatrix[i][j] = (float) Math.log( -this.
                geneExpressionMatrix[i][j]);
          }
          else {
            this.geneExpressionMatrix[i][j] = (float) Math.log(this.
                geneExpressionMatrix[i][j]);
          }
        }
      }
    }
  }

  /**
   *
   * @return Object
   */
  public Object clone() {
    PreProcessedExpressionMatrix o = (PreProcessedExpressionMatrix)super.clone();
    o.originalExpressionMatrix = (AbstractExpressionMatrix)this.
        originalExpressionMatrix.clone();
    return o;

  }

  //.......................................................................................................................

  /**
   *
   * TO BE IMPLEMENTED !!!!!!
   *
   * @return StyledDocument
   */
  public StyledDocument info() {
    StyledDocument doc = new DefaultStyledDocument();
    StyledDocument doc2 = new HTMLDocument();

    return (doc);
  }
} // END CLASS
