package smadeira.biclustering;

import flanagan.analysis.Stat;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import java.io.File;
import java.io.Serializable;
import java.util.BitSet;

/**
 * <p>Title: CCC Bicluster</p>
 *
 * <p>Description: Contiguous Column Coherent Bicluster.</p>
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
public class CCC_Bicluster
    extends Bicluster implements Cloneable, Serializable, Comparable,
    NodeObjectInterface {
  public static final long serialVersionUID = -3140684013276102465L;

  // CONSTRUCTORS

  /**
   *
   */
  protected CCC_Bicluster() {
  }

  /**
   *
   * @param biclusterSource Biclustering
   * @param ID int
   * @param rows int[]
   * @param columns int[]
   */
  public CCC_Bicluster(BiclusteringInDiscretizedMatrix biclusterSource, int ID,
                       int[] rows, int[] columns) {
    super(biclusterSource, ID, rows, columns);
  }

//##############################################################################################################################

  /**
   *
   * @return int
   */
  public int getFirstColumn() {
    return this.columnsIndexes[0];
  }

  /**
   *
   * @return int
   */
  public int getLastColumn() {
    return this.columnsIndexes[this.getNumberOfConditions() - 1];
  }

  /**
   *
   * @return String[]
   */
  public String[] getConditionsNames() {
    String[] conditionsNames = new String[this.getNumberOfConditions()];
    for (int j = 0; j < this.getNumberOfConditions(); j++) {
      conditionsNames[j] = ( (DiscretizedExpressionMatrix)this.biclusterSource.
                            getMatrix()).getOriginalExpressionMatrix().getConditionsNames()[this.getColumnsIndexes()[j] - 1];
    }
    return (conditionsNames);
  }

  /**
   *
   * @return int
   */
  public int getBiclusterExpressionPatternLength() {
    if ( ( (DiscretizedExpressionMatrix)this.getBiclusterSource().getMatrix()).
        getIsVBTP()) {
      return (this.getNumberOfConditions() - 1);
    }
    else {
      return (this.getNumberOfConditions());
    }
  }

  /**
   *
   * @return char[]
   */
  public char[] getBiclusterExpressionPattern() {
    char[] pattern = new char[this.getBiclusterExpressionPatternLength()];
    // THE PATTERN IS THE SAME FOR ALL GENES
    for (int j = 0; j < this.getBiclusterExpressionPatternLength(); j++) {
      pattern[j] = ( (DiscretizedExpressionMatrix)this.getBiclusterSource().
                    getMatrix()).getSymbolicExpressionMatrix()
          [this.rowsIndexes[0] - 1][this.columnsIndexes[j] - 1];
    }
    return (pattern);
  }

  /**
   *
   * @return char[][]
   */
  public char[][] getBiclusterSymbolicMatrix() {
    char[][] matrix = new char[this.getNumberOfGenes()][this.
        getBiclusterExpressionPatternLength()];
    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getBiclusterExpressionPatternLength(); j++) {
        matrix[i][j] = ( (IDiscretizedMatrix)this.biclusterSource.getMatrix()).
            getSymbolicExpressionMatrix()[this.getRowsIndexes()[i]-1][this.getColumnsIndexes()[j]-1];
      }
    }
    return (matrix);
  }

  /**
   *
   * @return char[]
   */
  public char[] getAlphabet() {
    return ( ( (IDiscretizedMatrix)this.biclusterSource.getMatrix()).
            getAlphabet());
  }

  /**
   * Compute bicluster pValue using the tail of the binomial distribution and the probability of the bicluster expression pattern.
   * <p>
   * pValue computed with binomialProb(p, |R|-1, |I|-1) WHERE:
   * <\p><p>
   * p = P (biclusterExpressionPattern) computed using Markov Chain
   * <\p><p>
   * FOR EXAMPLE:
   * <\p><p>
   * IF biclusterExpressionPattern = NUUDU AND markovChainOrder = 1
   * <\p><p>
   * P(N U U D U) = P(N) * P(NU|U) * P(UU|U) * P(UD|U) * P(DU|U)
   * <\p><p>
   * |R| = number of rows in the gene expression matrix
   * <\p><p>
   * |I| = number of rows in the bicluster
   * <\p>
   *
   * @param markovChainOrder int
   * @return double
   */
  public double getBiclusterPattern_pValue(int markovChainOrder) {
    return (this.compute_BiclusterPattern_pValue(markovChainOrder));
  }

  /**
   * Compute bicluster pValue using the tail of the binomial distribution and the probability of the bicluster expression pattern
   * with respective columns.
   * <p>
   * pValue computed with binomialProb(p, |R|-1, |I|-1) WHERE:
   * <\p><p>
   * p = P (biclusterExpressionPattern with respective conditions)
   * <\p><p>
   * FOR EXAMPLE:
   * <\p><p>
   * IF biclusterExpressionPattern = N1 U2 U3 D4 U5 AND markovChainOrder = 1
   * <\p><p>
   * P(N1 U2 U3 D4 U5) = P(N1) * P(N1U2|N1) * P(U2U3|U2) * P(U3D4|U3) * P(D4U5|D4)
   * <\p><p>
   * |R| = number of rows in the gene expression matrix
   * <\p><p>
   * |I| = number of rows in the bicluster
   * <\p><p>
   * @param markovChainOrder int
   * @return double
   */
  public double getBiclusterPatternWithColumns_pValue(int markovChainOrder) {
    return (this.compute_BiclusterPatternWithColumns_pValue(markovChainOrder));
  }

  /**
   *
   * @param markovChainOrder int
   * @return double
   */
  public double getBiclusterPattern_BonferroniCorrected_pValue(int
      markovChainOrder) {
    double pvalue = this.getBiclusterPattern_pValue(markovChainOrder);
    int numberOfBiclustersTested = this.biclusterSource.getNumberOfBiclusters();
    return (pvalue * numberOfBiclustersTested);
  }

  /**
   *
   * @param markovChainOrder int
   * @return double
   */
  public double getBiclusterPatternWithColumns_BonferroniCorrected_pValue(int
      markovChainOrder) {
    double pvalue = this.getBiclusterPatternWithColumns_pValue(markovChainOrder);
    int numberOfBiclustersTested = this.biclusterSource.getNumberOfBiclusters();
    return (pvalue * numberOfBiclustersTested);

  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * Computes the pvalue of the bicluster using the probability of a pattern with the size of the bicluster pattern
   * generated using an alphabet sigma where all symbols have the same probability (equally distributed symbols)
   * Compute pValue using BinomialProb (p, |R|-1, |I|-1)
   * WHERE p = |alphabet| to the power of -|J|
   * |R| = number of rows in the gene expression matrix
   * |I| = number of rows in the bicluster
   * |J| = number of columns in the bicluster
   *
   * RETURNS 1 WHEN |I| = 1
   *
   * @param R int
   * @param sigma int
   * @return double
   */
  public double compute_PatternWithEquallyDistributedSymbols_pValue(int R,
      int sigma) {
    double p = Math.pow(sigma, -this.getNumberOfConditions());
    if (this.getNumberOfGenes() == 1) {
      return (1);
    }
    else {
      if (p > 1) { // due to precision problems
        p = 1;
      }
      else {
        if (p < 0) {
          p = 0;
        }
      }

      double pValue = Stat.binomialProb(p, R - 1, this.getNumberOfGenes() - 1);
      if (pValue > 1) { // due to precision problems
        return (1);
      }
      else {
        if (pValue < 0) {
          return (0);
        }
        else {
          return (pValue);
        }
      }
    }
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * <p>
   * Computes the pValue with binomialProb(p, |R|-1, |I|-1) WHERE:
   * <\p><p>
   * p = P (biclusterExpressionPattern)
   * <\p><p>
   * FOR EXAMPLE:
   * <\p><p>
   * IF biclusterExpressionPattern = N U U D U
   * <\p><p>
   * P(N U U D U) = P(N) * P(N->U) * P(U->U) * P(U->D) * P(D->U) = P(N) * P(NU|N) * P(UU|U) * P(UD|U) * P(DU|U)
   * <\p><p>
   * |R| = number of rows in the gene expression matrix
   * <\p><p>
   * |I| = number of rows in the bicluster
   * <\p><p>
   * RETURNS 1 WHEN |I| = 1
   * <\p>
   *
   * @param markovChainOrder int
   * @return double
   */
  protected double compute_BiclusterPattern_pValue(int markovChainOrder) {
    if (this.getNumberOfGenes() == 1) {
      return (1);
    }
    else {
      double p = this.compute_BiclusterPattern_Probability(markovChainOrder);
      if (p > 1) { // due to precision problems
        p = 1;
      }
      else {
        if (p < 0) {
          p = 0;
        }
      }
      double biclusterPattern_pValue = (float) Stat.binomialProb(p,
          ((IDiscretizedMatrix)this.biclusterSource.getMatrix()).getNumberOfGenes()-1,
          this.getNumberOfGenes() - 1);
      if (biclusterPattern_pValue > 1) { // due to precision problems
        return (1);
      }
      else {
        if (biclusterPattern_pValue < 0) {
          return (0);
        }
        else {
          return (biclusterPattern_pValue);
        }
      }
    }
  }

  //###############################################################################################################################

  /**
   * <p>
   * Computes the probability of the bicluster pattern.
   * <\p><p>
   * FOR EXAMPLE:
   * <\p><p>
   * IF biclusterExpressionPattern = N U U D U
   * <\p><p>
   * WHEN K = 1:
   * <\p><p>
   * P(N U U D U) = P(N) * P(N->U) * P(U->U) * P(U->D) * P(D->U)
   * <\p><p>
   * WHEN K = 2:
   * <\p><p>
   * P(N U U D U) = P(N) * P(N->U) * P(NU->U) * P(NU->D) * P(UD->U)
   * <\p><p>
   * WHEN K = 3:
   * <\p><p>
   * P(N U U D U) = P(N) * P(N->U) * P(NU->U) * P(NUU->D) * P(UUD->U)
   * <\p><p>
   * WHEN K = 4
   * <\p><p>
   * ...
   * <\p>
   *
   * @param markovChainOrder int
   * @return double
   */
  protected double compute_BiclusterPattern_Probability(int markovChainOrder) {
    char[][] symbolicMatrix = ( (BiclusteringInDiscretizedMatrix)this.
                               biclusterSource).getMatrix().getSymbolicExpressionMatrix();
    double p = this.compute_Pattern_Probability(
      symbolicMatrix,this.getBiclusterExpressionPattern(), markovChainOrder);
    return p;
  }

  //###############################################################################################################################
  //###############################################################################################################################
  /**
   * <p>
   * Computes the probability of expression pattern passed as parameter.
   * <\p><p>
   * FOR EXAMPLE:
   * <\p><p>
   * IF biclusterExpressionPattern = N U U D U
   * <\p><p>
   * WHEN K = 1:
   * <\p><p>
   * P(N U U D U) = P(N) * P(N->U) * P(U->U) * P(U->D) * P(D->U)
   * <\p><p>
   * WHEN K = 2:
   * <\p><p>
   * P(N U U D U) = P(N) * P(N->U) * P(NU->U) * P(NU->D) * P(UD->U)
   * <\p><p>
   * WHEN K = 3:
   * <\p><p>
   * P(N U U D U) = P(N) * P(N->U) * P(NU->U) * P(NUU->D) * P(UUD->U)
   * <\p><p>
   * WHEN K = 4
   * <\p><p>
   * ...
   * <\p>
   *
   * @param symbolicExpressionMatrix char[][]
   * @param biclusterPattern char[] NOT NEEDED HERE!! (ADDED SO THAT METHOD CAN BE USED DIRECTLY IN THE CASE WITH ERRORS !!)
   * @param markovChainOrder int
   * @return double
   */
  protected double compute_Pattern_Probability(char[][]
      symbolicExpressionMatrix, char[] biclusterPattern, int markovChainOrder) {
    //System.out.println("## COMPUTING PROBABILITY OF PATTERN");
    //System.out.println("markovChainOrder = " + markovChainOrder);
    //System.out.println("PATTERN = " + new String(biclusterPattern));

    double p = 1;
    char[] pattern = new char[1];
    pattern[0] = biclusterPattern[0];
    //System.out.println("FIRST SYMBOL --> p("+  new String(pattern) + ")");
    p = p * this.compute_Pattern_Probability(symbolicExpressionMatrix, pattern);

    for (int endPosition = 1; endPosition < biclusterPattern.length;
         endPosition++) {
      int startPosition = endPosition - markovChainOrder;
      if (startPosition < 0) {
        startPosition = 0;
      }
      //System.out.println("startPosition = " + startPosition);
      //System.out.println("endPosition = " + endPosition);
      pattern = new String(biclusterPattern).substring(startPosition,
          endPosition + 1).toCharArray();
      //System.out.println("p("+  new String(pattern) + ")");
      p = p *
          this.compute_Pattern_Probability(symbolicExpressionMatrix, pattern);
    }
    //System.out.println("p = " + p);
    return (p);
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * IF p=U
   * return P(U)
   * IF p = UD
   * return P(UD)/P(U)
   * IF p = UDU
   * return P(UDU) / P(UD)
   * IF p = UDUN
   * return P(UDUN) / P(UDU)
   * ...
   *
   * @param symbolicExpressionMatrix char[][]
   * @param pattern char[]
   * @return double
   */
  private double compute_Pattern_Probability(char[][] symbolicExpressionMatrix,
                                             char[] pattern) {
    double p_pattern; //probability
    int o_pattern = 0; //occurrences
    int numberOfRows = symbolicExpressionMatrix.length;
    int numberOfColumns = symbolicExpressionMatrix[0].length;

    // IF PATTERN.LENGTH = 1
    if (pattern.length == 1) {
      for (int i = 0; i < symbolicExpressionMatrix.length; i++) {
        for (int j = 0; j < symbolicExpressionMatrix[0].length; j++) {
          if (pattern[0] == symbolicExpressionMatrix[i][j]) {
            o_pattern++;
          }
        }
      }
      //System.out.println(o_pattern + "/" + (numberOfRows*numberOfColumns));
      if (o_pattern > 0) {
        return (o_pattern / ( (double) numberOfRows * numberOfColumns));
      }
      else {
        return (0);
      }
    }

    // IF PATTERN.LENGTH > 1
    //count occurrences of pattern
    for (int i = 0; i < symbolicExpressionMatrix.length; i++) {
      for (int j = 0; j <= symbolicExpressionMatrix[0].length - pattern.length;
           j++) {
        boolean match = true;
        int p = 0;
        while (p < pattern.length && match == true) {
          if (pattern[p] == symbolicExpressionMatrix[i][j + p]) { //match
            p++;
          }
          else { //mismatch
            match = false;
          }
        }
        if (match == true) {
          o_pattern++;
        }
      }
    }
    //System.out.println(o_pattern + "/" + (numberOfRows*(numberOfColumns -(pattern.length-1))));
    if (o_pattern == 0) {
      return (0);
    }
    p_pattern = o_pattern /
        ( (double) numberOfRows * (numberOfColumns - (pattern.length - 1)));

    //count occurrences of pattern without last symbol
    double p_patternWithoutLastSymbol; //probability
    int o_patternWithoutLastSymbol = 0; //occurences

    for (int i = 0; i < symbolicExpressionMatrix.length; i++) {
      for (int j = 0;
           j <= symbolicExpressionMatrix[0].length - (pattern.length - 1); j++) {
        boolean match = true;
        int p = 0;
        while (p < pattern.length - 1 && match == true) {
          if (pattern[p] == symbolicExpressionMatrix[i][j + p]) { //match
            p++;
          }
          else { //mismatch
            match = false;
          }
        }
        if (match == true) {
          o_patternWithoutLastSymbol++;
        }
      }
    }
    //System.out.println(o_patternWithoutLastSymbol + "/" + numberOfRows*(numberOfColumns -(pattern.length-2)));
    p_patternWithoutLastSymbol = o_patternWithoutLastSymbol /
        ( (double) numberOfRows * (numberOfColumns - (pattern.length - 2)));
    return (p_pattern / p_patternWithoutLastSymbol);
  }

  //###############################################################################################################################
  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * <p>
   * Computes the pValue with binomialProb(p, |R|-1, |I|-1) WHERE:
   * <\p><p>
   * p = P (biclusterExpressionPattern with respective colulumns)
   * <\p><p>
   * FOR EXAMPLE:
   * <\p><p>
   * IF biclusterExpressionPattern = N1 U2 U3 D4 U5
   * <\p><p>
   * P(N1 U2 U3 D4 U5) = P(N1) * P(N1->U2) * P(U2->U3) * P(U3->D4) * P(D4->U5) = P(N1) * P(N1U2|N1) * P(U2U3|U2) * P(U3D4|U3) * P(D4U5|U4)
   * <\p><p>
   * |R| = number of rows in the gene expression matrix
   * <\p><p>
   * |I| = number of rows in the bicluster
   * <\p><p>
   * RETURNS 1 WHEN |I| = 1
   * <\p>
   *
   * @param markovChainOrder int
   * @return double
   */
  protected double compute_BiclusterPatternWithColumns_pValue(int markovChainOrder) {
    if (this.getNumberOfGenes() == 1) {
      return (1);
    }
    else {
      // COMPUTE BICLUSTER PATTERN PROBABILITY
      double p = this.compute_BiclusterPatternWithColumns_Probability(markovChainOrder);
      if (p > 1) { // due to precision problems
        p = 1;
      }
      else {
        if (p < 0) {
          p = 0;
        }
      }
      double biclusterPatternWithColumns_pValue = (float) Stat.binomialProb(p,
          ((IDiscretizedMatrix)this.biclusterSource.getMatrix()).getNumberOfGenes()-1,
          this.getNumberOfGenes() - 1);
      if (biclusterPatternWithColumns_pValue > 1) { // due to precision problems
        return (1);
      }
      else {
        if (biclusterPatternWithColumns_pValue < 0) {
          return (0);
        }
        else {
          return (biclusterPatternWithColumns_pValue);
        }
      }
    }
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * <p>
   * Computes the probability of the bicluster expression pattern.
   * <\p><p>
   * FOR EXAMPLE:
   * <\p><p>
   * IF biclusterExpressionPattern = N U U D U
   * <\p><p>
   * WHEN K = 1:
   * <\p><p>
   * P(N U U D U) = P(N) * P(N->U) * P(U->U) * P(U->D) * P(D->U)
   * <\p><p>
   * WHEN K = 2:
   * <\p><p>
   * P(N U U D U) = P(N) * P(N->U) * P(NU->U) * P(NU->D) * P(UD->U)
   * <\p><p>
   * WHEN K = 3:
   * <\p><p>
   * P(N U U D U) = P(N) * P(N->U) * P(NU->U) * P(NUU->D) * P(UUD->U)
   * <\p><p>
   * WHEN K = 4
   * <\p><p>
   * ...
   * <\p>
   *
   * @param markovChainOrder int
   * @return double
   */
  protected double compute_BiclusterPatternWithColumns_Probability(int markovChainOrder) {
    char[][] symbolicMatrix = ( (BiclusteringInDiscretizedMatrix)this.
                               biclusterSource).getMatrix().getSymbolicExpressionMatrix();
    double p = this.compute_PatternWithColumns_Probability(
          symbolicMatrix, this.getBiclusterExpressionPattern(),
          this.getFirstColumn() - 1, markovChainOrder);
    return p;
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * <p>
   * Computes the probability of the expression pattern passed as parameter.
   * <\p><p>
   * FOR EXAMPLE:
   * <\p><p>
   * IF biclusterExpressionPattern = N U U D U
   * <\p><p>
   * WHEN K = 1:
   * <\p><p>
   * P(N U U D U) = P(N) * P(N->U) * P(U->U) * P(U->D) * P(D->U)
   * <\p><p>
   * WHEN K = 2:
   * <\p><p>
   * P(N U U D U) = P(N) * P(N->U) * P(NU->U) * P(NU->D) * P(UD->U)
   * <\p><p>
   * WHEN K = 3:
   * <\p><p>
   * P(N U U D U) = P(N) * P(N->U) * P(NU->U) * P(NUU->D) * P(UUD->U)
   * <\p><p>
   * WHEN K = 4
   * <\p><p>
   * ...
   * <\p>
   *
   *
   * @param symbolicExpressionMatrix char[][]
   * @param biclusterPattern char[] NOT NEEDED HERE!! (ADDED SO THAT METHOD CAN BE USED DIRECTLY IN THE CASE WITH ERRORS !!)
   * @param firstColumnIndex int NOT NEEDED HERE!! (ADDED SO THAT METHOD CAN BE USED DIRECTLY IN THE CASE WITH ERRORS !!)
   * @param markovChainOrder int
   * @return double
   */
  protected double compute_PatternWithColumns_Probability(char[][]
      symbolicExpressionMatrix, char[] biclusterPattern, int firstColumnIndex, int markovChainOrder) {
    //System.out.println("## COMPUTING PROBABILITY OF PATTERN WITH COLUMNS");
    //System.out.println("markovChainOrder = " + markovChainOrder);
    //System.out.println("PATTERN = " + new String(biclusterPattern));
    double p = 1;
    char[] pattern = new char[1];
    pattern[0] = biclusterPattern[0];
    //System.out.println("FIRST SYMBOL --> p("+  new String(pattern) + ")");
    p = p *
        this.compute_PatternWithColumns_Probability(symbolicExpressionMatrix,
        pattern, firstColumnIndex);
    for (int endPosition = firstColumnIndex + 1;
         endPosition < firstColumnIndex + biclusterPattern.length; endPosition++) {
      int startPosition = endPosition - markovChainOrder;
      if (startPosition < firstColumnIndex) {
        startPosition = firstColumnIndex;
      }
      //System.out.println("startPosition = " + startPosition);
      //System.out.println("endPosition = " + endPosition);
      pattern = new String(biclusterPattern).substring(startPosition -
          firstColumnIndex, endPosition + 1 - firstColumnIndex).toCharArray();
      //System.out.println("p("+   new String(pattern) + ")");
      p = p *
          this.compute_PatternWithColumns_Probability(symbolicExpressionMatrix,
          pattern, startPosition);
    }
    //System.out.println("p = " + p);
    return (p);
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * IF p = U1
   * return P(U1)
   * IF p = U1D2
   * return P(U1D2)/P(U1)
   * IF p = U1D2U3
   * return P(U1D2U3) / P(U1D2)
   * IF p = U1D2U3N4
   * return P(U1D2U3N4) / P(U1D2U3) = OCCURRENCES(U1D2U3N4)/OCCURRENCES(U1D2U3)
   * ...
   *
   * @param symbolicExpressionMatrix char[][]
   * @param pattern char[]
   * @param firstColumnIndex int
   * @return double
   */
  private double compute_PatternWithColumns_Probability(char[][]
      symbolicExpressionMatrix, char[] pattern, int firstColumnIndex) {
    int o_pattern = 0; //occurrences
    int numberOfRows = symbolicExpressionMatrix.length;

    // IF PATTERN.LENGTH = 1
    if (pattern.length == 1) {
      for (int i = 0; i < symbolicExpressionMatrix.length; i++) {
        if (pattern[0] == symbolicExpressionMatrix[i][firstColumnIndex]) {
          o_pattern++;
        }
      }
      //System.out.println(o_pattern + "/" + numberOfRows);
      if (o_pattern > 0) {
        return ( (double) o_pattern / numberOfRows);
      }
      else {
        return (0);
      }
    }
    // IF PATTERN.LENGTH > 1
    //count occurrences of pattern
    for (int i = 0; i < symbolicExpressionMatrix.length; i++) {
      boolean match = true;
      int p = 0;
      while (p < pattern.length && match == true) {
        if (pattern[p] == symbolicExpressionMatrix[i][firstColumnIndex + p]) { //match
          p++;
        }
        else { //mismatch
          match = false;
        }
      }
      if (match == true) {
        o_pattern++;
      }
    }
    if (o_pattern == 0) {
      return (0);
    }
    //count occurrences of pattern without last symbol
    int o_patternWithoutLastSymbol = 0; //occurences
    for (int i = 0; i < symbolicExpressionMatrix.length; i++) {
      boolean match = true;
      int p = 0;
      while (p < pattern.length - 1 && match == true) {
        if (pattern[p] == symbolicExpressionMatrix[i][firstColumnIndex + p]) { //match
          p++;
        }
        else { //mismatch
          match = false;
        }
      }
      if (match == true) {
        o_patternWithoutLastSymbol++;
      }
    }
    //System.out.println(o_pattern + "/" + o_patternWithoutLastSymbol);
    return ( (double) o_pattern / o_patternWithoutLastSymbol);
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @param o Object
   * @return boolean
   */
  public boolean equals(Object o) {
    if (this == null || o == null || ! (o instanceof CCC_Bicluster)) {
      return false;
    }

    if (!this.biclusterSource.equals( ( (CCC_Bicluster) o).biclusterSource)) {
      return false;
    }

    if (this.getNumberOfGenes() != ( (CCC_Bicluster) o).getNumberOfGenes()) {
      return false;
    }
    if (this.getNumberOfConditions() !=
        ( (CCC_Bicluster) o).getNumberOfConditions()) {
      return false;
    }
    if (this.getFirstColumn() != ( (CCC_Bicluster) o).getFirstColumn() ||
        this.getLastColumn() != ( (CCC_Bicluster) o).getLastColumn()) {
      return false;
    }
    BitSet genes = new BitSet(this.getNumberOfGenes());
    BitSet genes_o = new BitSet(this.getNumberOfGenes());
    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      genes.set(this.rowsIndexes[i]);
      genes_o.set( ( (CCC_Bicluster) o).rowsIndexes[i]);
    }
    return (genes.equals(genes_o));
  }

  /**
   *
   * @param o1 Object
   * @return int
   */
  public int compareTo(Object o1) {
    return ( ( (Integer) (this.ID)).compareTo( ( (Integer) ( (CCC_Bicluster) o1).
                                                ID)));
  }

  /**
   *
   * @return int
   */
  public int hashCode() {
    int hash = 17;
    hash = 37 * hash + this.getFirstColumn();
    hash = 37 * hash + this.getLastColumn();
    if (this.rowsIndexes != null) {
      BitSet genes = new BitSet(this.getNumberOfGenes());
      for (int i = 0; i < this.getNumberOfGenes(); i++) {
        genes.set(this.rowsIndexes[i]);
      }
      hash = 37 * hash + genes.hashCode();
    }
    return hash;
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @return String
   */
  public String toString() {
    String s = new String();
    s = s + "#BICLUSTER_" + this.getID() + "\t" +
        "CONDITIONS= " + this.getNumberOfConditions() + "\t" +
        "FIRST-LAST= " + this.columnsIndexes[0] + "-" +
        this.columnsIndexes[this.getNumberOfConditions() - 1] +
        "\t" +
        "GENES= " + this.getNumberOfGenes() + "\t" +
        "PATTERN=" + new String(this.getBiclusterExpressionPattern()) + "\n";

    char[][] biclusterSymbolicMatrix = this.getBiclusterSymbolicMatrix();

    for (int g = 0; g < this.getNumberOfGenes(); g++) {
      s = s + this.getGenesNames()[g] + "\t" + new String(biclusterSymbolicMatrix[g]) + "\n";
    }
    return s;
  }

  /**
    *
    * @return String
    */
   public String summaryToString() {
     String s = new String();
     s = s + "#BICLUSTER_" + this.getID() + "\t" +
         "CONDITIONS= " + this.getNumberOfConditions() + "\t" +
         "FIRST-LAST= " + this.columnsIndexes[0] + "-" +
         this.columnsIndexes[this.getNumberOfConditions() - 1] +
         "\t" +
         "GENES= " + this.getNumberOfGenes() + "\t" +
         "PATTERN=" + new String(this.getBiclusterExpressionPattern()) + "\n";
      return s;
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @param pathToDirectory String
   * @param filename String
   * @param width int
   * @param height int
   * @param legend boolean
   */
  public void printColorChartBiclusterExpressionPattern_ToFile(String pathToDirectory, String filename, int width, int height, boolean legend){
    File fileOut;
    if (pathToDirectory != null) {
      // CREATE DIRECTORY TO STORE THE RESULTS
      File dirForResults = new File(pathToDirectory);
      boolean exists = (dirForResults).exists();
      if (exists) {
//        System.out.println("THE DIRECTORY TO STORE THE RESULTS ALREADY EXISTS!");
      }
      else {
        System.out.println("CREATING THE DIRECTORY TO STORE THE RESULTS...");
        boolean success = (new File(pathToDirectory)).mkdirs();
        if (!success) {
          System.out.println(
              "CREATION OF DIRECTORY TO STORE THE RESULTS FAILED!");
        }
      }
      fileOut = new File(pathToDirectory, filename);
    }
    else {
      fileOut = new File(filename);
    }

    //PanelExpressionChart panel = null;
    boolean miniature = false;
    try {
      //panel = PanelExpressionChart.createExpressionBiclusterPatternChart(this, width, height, miniature, legend);
      //ChartUtilities.saveChartAsPNG(fileOut, panel.getChart(), width, height);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


  //###############################################################################################################################
  //###############################################################################################################################

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
