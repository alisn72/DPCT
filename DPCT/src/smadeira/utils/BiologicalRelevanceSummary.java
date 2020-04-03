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
public class BiologicalRelevanceSummary {

  /**
   * Best p-value
   */
  private double bestPvalue;

  /**
   * Best (Bonferroni corrected) p-value
   */
  private double bestCorrectedPvalue;

  /**
   * # significant terms --> 0.01 <= (Bonferroni corrected) p-value < 0.05
   */
  private int numberOfSignificantTerms;

  /**
   * # highly significant terms --> (Bonferroni corrected)p-value < 0.01
   */
  private int numberOfHighlySignificantTerms;

  /**
   * Threshold provided by the user to consider a term as significant.
   */
  private float significanceUserThreshold;

  /**
   * # significant terms according to user threshold --> (Bonferroni corrected) p-value < GO_pValues_userThreshold
   */
  private int numberOfSignificantTermsUserThreshold;

  /**
   * Constructor
   *
    * @param bestPvalue double
    * @param bestCorrectedPvalue double
    * @param numberOfSignificantCorrectedTerms int
    * @param numberOfHighlySignificantCorrectedTerms int
    * @param significanceUserThreshold float
    * @param numberOfUserThresholdCorrectedSignificantTerms int
    */
   public BiologicalRelevanceSummary(double bestPvalue,
                                    double bestCorrectedPvalue,
                                    int numberOfSignificantCorrectedTerms,
                                    int numberOfHighlySignificantCorrectedTerms,
                                    float significanceUserThreshold,
                                    int numberOfUserThresholdCorrectedSignificantTerms) {
    this.bestPvalue = bestPvalue;
    this.bestCorrectedPvalue = bestCorrectedPvalue;
    this.numberOfSignificantTerms = numberOfSignificantCorrectedTerms;
    this.numberOfHighlySignificantTerms = numberOfHighlySignificantCorrectedTerms;
    this.significanceUserThreshold = significanceUserThreshold;
    this.numberOfSignificantTermsUserThreshold = numberOfUserThresholdCorrectedSignificantTerms;
  }

  /**
   * Best p-value
   *
   * @return double
   */
  public double getBestPvalue() {
    return this.bestPvalue;
  }

  /**
   * Best (Bonferroni corrected) p-value
   *
   * @return double
   */
  public double getBestCorrectedPvalue() {
    return this.bestCorrectedPvalue;
  }

  /**
   * # significant terms --> 0.01 <= (Bonferroni corrected) p-value < 0.05
   *
   * @return int
   */
  public int getNumberOfSignificantCorrectedTerms() {
    return this.numberOfSignificantTerms;
  }

  /**
   * # highly significant terms --> (Bonferroni corrected)p-value < 0.01
   *
   * @return int
   */
  public int getNumberOfHighlySignificantCorrectedTerms() {
    return this.numberOfHighlySignificantTerms;
  }

  /**
   * Threshold provided by the user to consider a term as significant.
   *
   * @return float
   */
  public float getSignificanceUserThreshold() {
    return this.significanceUserThreshold;
  }

  /**
   * # significant terms according to user threshold --> (Bonferroni corrected) p-value < GO_pValues_userThreshold
   *
   * @return int
   */
  public int getNumberOfUserThresholdSignificantCorrectedTerms() {
    return this.numberOfSignificantTermsUserThreshold;
  }

}
