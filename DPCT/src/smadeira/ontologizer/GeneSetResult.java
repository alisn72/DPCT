package smadeira.ontologizer;

import java.io.*;
import java.util.*;

/**
 * <p>Title: Gene Set Result</p>
 *
 * <p>Description: Results of term-for-term analysis for a gene set.</p>
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
 * @author André L. Martins
 * @author Sara C. Madeira
 *
 * @version 1.0
 */
public class GeneSetResult
    implements Serializable {

  /**
   *
   */
  private int[] iTermID;

  /**
   *
   */
  private String[] termID;

  /**
   *
   */
  private String[] termName;

  /**
   *
   */
  private int[] genesInPopulationAnnotatedWithTerm; // Sara C. Madeira

  /**
   *
   */
  private int[] genesInBiclusterAnnotatedWithTerm; // Sara C. Madeira

  /**
   *
   */
  private double[] pValue;

  /**
   *
   */
  private double[] correctedPValue;

  /**
   *
   */
  private int numberOfTerms;

  /**
   *
   */
  private int populationGeneCount;

  /**
   * For each GOTerm, a bit in the BitSet is
   * set to true if the original study gene set
   * gene with that bit's index is annotated by
   * the GOTerm.
   */
  private BitSet[] annotatedStudyGeneMap;

  /**
   *
   */
  private boolean hasDepthInfo;

  /**
   *
   */
  private int[] minDepth;

  /**
   *
   */
  private int[] maxDepth;

  /**
   *
   * @param numberOfTerms int
   * @param populationGeneCount int
   * @param goTermAssocs If true, GeneSetResult will contain a BitSet for
   * each GOTerm, marking the genes it annotates.
   */
  public GeneSetResult(int numberOfTerms, int populationGeneCount,
                       boolean goTermAssocs) {
    this.iTermID = new int[numberOfTerms];
    this.termID = new String[numberOfTerms];
    this.termName = new String[numberOfTerms];
    this.genesInBiclusterAnnotatedWithTerm = new int[numberOfTerms];
    this.genesInPopulationAnnotatedWithTerm = new int[numberOfTerms];
    this.pValue = new double[numberOfTerms];
    this.correctedPValue = new double[numberOfTerms];
    this.populationGeneCount = populationGeneCount;
    this.numberOfTerms = numberOfTerms;
    this.minDepth = new int[numberOfTerms];
    this.maxDepth = new int[numberOfTerms];
    if (goTermAssocs) {
      this.annotatedStudyGeneMap = new BitSet[numberOfTerms];
      for (int i = 0; i < numberOfTerms; ++i) {
        this.annotatedStudyGeneMap[i] = new BitSet(populationGeneCount);
      }
    }
  }

  /**
   *
   * @return int[]
   */
  public int[] getITermID() {
    return this.iTermID;
  }

  /**
   *
   * @return String[]
   */
  public String[] getTermID() {
    return this.termID;
  }

  /**
   *
   * @return String[]
   */
  public String[] getTermName() {
    return this.termName;
  }

  /**
   *
   * @return int[]
   */
  public int[] getGenesInPopulationAnnotatedWithTerm() {
    return this.genesInPopulationAnnotatedWithTerm;
  }

  /**
   *
   * @return int[]
   */
  public int[] getGenesInBiclusterAnnotatedWithTerm() {
    return this.genesInBiclusterAnnotatedWithTerm;
  }

  /**
   *
   * @return double[]
   */
  public double[] getPValue() {
    return this.pValue;
  }

  /**
   *
   * @return double[]
   */
  public double[] getCorrectedPValue() {
    return this.correctedPValue;
  }

  /**
   *
   * @return BitSet[]
   */
  public BitSet[] getAnnotatedStudyGeneMap() {
    return this.annotatedStudyGeneMap;
  }

  /**
   *
   * @return boolean
   */
  public boolean getHasDepthInfo() {
    return this.hasDepthInfo;
  }

  /**
   *
   * @param hasDepthInfo boolean
   */
  public void setHasDepthInfo(boolean hasDepthInfo) {
    this.hasDepthInfo = hasDepthInfo;
  }

  /**
   *
   * @return int[]
   */
  public int[] getMinDepth() {
    return this.minDepth;
  }

  /**
   *
   * @return int[]
   */
  public int[] getMaxDepth() {
    return this.maxDepth;
  }

  public int getNumberOfTerms() {
    return this.numberOfTerms;
  }

  public int getPopulationGeneCount() {
    return this.populationGeneCount;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < this.numberOfTerms; ++i) {
      builder.append('\t');
      builder.append(termID[i]);
      builder.append(' ');
      builder.append(termName[i]);
      builder.append('\n');
      builder.append("Population Genes: ");
      builder.append(this.genesInPopulationAnnotatedWithTerm[i]);
      builder.append('\n');
      builder.append("Study Set Genes: ");
      builder.append(this.genesInPopulationAnnotatedWithTerm[i]);
      builder.append('\n');
      builder.append("\tP-value: ");
      builder.append(pValue[i]);
      builder.append('\n');
      builder.append("\tCorrected P-value: ");
      builder.append(correctedPValue[i]);
      builder.append('\n');
    }
    return builder.toString();
  }

  /**
   * Sara C.Madeira
   *
   * @return GeneSetResult
   */
  public GeneSetResult clone() {
    GeneSetResult g = new GeneSetResult(this.numberOfTerms,
                                        this.populationGeneCount, true);
    g.iTermID = this.iTermID.clone();
    g.termID = this.termID.clone();
    g.termName = this.termName.clone();
    g.pValue = this.pValue.clone();
    g.correctedPValue = this.correctedPValue.clone();
    if (this.genesInPopulationAnnotatedWithTerm != null) {
      g.genesInPopulationAnnotatedWithTerm = this.
          genesInPopulationAnnotatedWithTerm.clone();
    }
    if (this.genesInBiclusterAnnotatedWithTerm != null) {
      g.genesInBiclusterAnnotatedWithTerm = this.
          genesInBiclusterAnnotatedWithTerm.clone();
    }
    if (this.annotatedStudyGeneMap != null) {
      g.annotatedStudyGeneMap = this.annotatedStudyGeneMap.clone();
    }
    g.hasDepthInfo = this.hasDepthInfo;
    if (this.maxDepth != null) {
      g.maxDepth = this.maxDepth.clone();
    }
    if (this.minDepth != null) {
      g.minDepth = this.minDepth.clone();
    }
    g.numberOfTerms = this.numberOfTerms;
    g.populationGeneCount = this.populationGeneCount;
    return (g);
  }
}
