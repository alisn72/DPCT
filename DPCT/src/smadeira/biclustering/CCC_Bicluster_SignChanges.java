package smadeira.biclustering;

import java.io.*;
import java.util.*;

import javax.swing.text.*;
import javax.swing.text.html.*;

/**
 * <p>Title: CCC Bicluster with Sign Changes</p>
 *
 * <p>Description: Contiguous Column Coherent Bicluster with Sign Changes
 *                 (anticorrelated patterns).</p>
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
public class CCC_Bicluster_SignChanges
    extends CCC_Bicluster implements IBicluster_SignChanges, Cloneable,
    Serializable, Comparator, NodeObjectInterface {
  public static final long serialVersionUID = -6844611427925838009L;

  /**
   * true --> sign change
   * false --> no sign change
   */
  private boolean[] signChanges;

  // CONSTRUCTORS

  public CCC_Bicluster_SignChanges(BiclusteringInDiscretizedMatrix biclusterSource, int ID,
                                   int[] rows, int[] columns,
                                   boolean[] signChanges) {
    super(biclusterSource, ID, rows, columns);
    this.signChanges = signChanges;
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @return boolean[]
   */
  public boolean[] getSignChanges() {
    return this.signChanges;
  }

  //###############################################################################################################################

  /**
   *
   * @return char[]
   */
  public char[] getBiclusterExpressionPattern_SignChanges() {
    char[] pattern_SignChanges = new char[this.
        getBiclusterExpressionPatternLength()];
    for (int j = 0; j < this.getBiclusterExpressionPatternLength(); j++) {
      char currentSymbol = this.getBiclusterExpressionPattern()[j];
      boolean currentSymbolFound = false;
      int position = 0;
      while (!currentSymbolFound && position < this.getAlphabet().length) {
        if (this.getAlphabet()[position] == currentSymbol) {
          currentSymbolFound = true;
        }
        else {
          position++;
        }
      }
      char opposedSymbol;
      if (position == this.getAlphabet().length / 2) {
        opposedSymbol = currentSymbol;
      }
      else {
        opposedSymbol = this.getAlphabet()[this.getAlphabet().length - position -
            1];
      }
      pattern_SignChanges[j] = opposedSymbol;
    }
    return pattern_SignChanges;
  }

//###############################################################################################################################
//###############################################################################################################################

  /**
   * p = P (biclusterExpressionPattern)
   * FOR EXAMPLE:
   * P(U D N) = SUM OF THE PROBABILITY OF THE PATTERN WITH SIGN CHANGES
   * P(U D N) = P(U D N) + P(D U N)
   * WHERE
   * P(U D N) = P(U) * P(U->D) * P(D->N)
   * P(D U N) = P(D) * P(D->U) * P(U->N)
   *
   * @param markovChainOrder int
   * @return double
   */
  protected double compute_BiclusterPattern_Probability(int markovChainOrder) {
//      char[][] symbolicExpressionMatrix = ((CCC_Biclustering_SignChanges) this.biclusterSource).getDiscretizedMatrixWithSignChanges();
      char[][] symbolicExpressionMatrix = ( (CCC_Biclustering_SignChanges)this.
                                           biclusterSource).getSymbolicExpressionMatrix();
      // PATTERN
      double p = this.compute_Pattern_Probability(
          symbolicExpressionMatrix, this.getBiclusterExpressionPattern(),
          markovChainOrder);
      // PATTERN WITH SIGN CHANGES
      p = p +
          this.compute_Pattern_Probability(symbolicExpressionMatrix,
          this.getBiclusterExpressionPattern_SignChanges(), markovChainOrder);
      return p;
  }

//###############################################################################################################################
//###############################################################################################################################

  /**
   * p = P (biclusterExpressionPattern)
   * FOR EXAMPLE:
   * P(U1 D2 N3) = SUM OF THE PROBABILITY OF THE PATTERN WITH SIGN CHANGES
   * P(U1 D2 N3) = P(U1 D2 N3) + P(D1 U2 N3)
   * WHERE
   * P(U1 D2 N3) = P(U1) * P(U1->D2) * P(D2->N3)
   * P(D1 U2 N3) = P(D1) * P(D1->U2) * P(U2->N3)
   *
   * @param markovChainOrder int
   * @return double
   */
  protected double compute_BiclusterPatternWithColumns_Probability(int markovChainOrder) {
      char[][] symbolicMatrix = ( (CCC_Biclustering_SignChanges)this.
                                 biclusterSource).getSymbolicExpressionMatrix();
      // PATTERN
      double p = this.compute_PatternWithColumns_Probability(
          symbolicMatrix, this.getBiclusterExpressionPattern(), this.getFirstColumn() - 1, markovChainOrder);
      // PATTERN WITH SIGN CHANGES
      p = p +
          this.compute_PatternWithColumns_Probability(symbolicMatrix,
          this.getBiclusterExpressionPattern_SignChanges(),
          this.getFirstColumn() - 1, markovChainOrder);
      return p;
    }

  //###############################################################################################################################
  //###############################################################################################################################


  /**
   *
   * @return Object
   */
  public Object clone() {
    CCC_Bicluster_SignChanges o = (CCC_Bicluster_SignChanges)super.clone();
    return o;
  }

  /**
   *
   * @param o1 Object
   * @return int
   */
  public int compareTo(Object o1) {
    return ( ( (Integer) (this.ID)).compareTo( ( (Integer) ( (
        CCC_Bicluster_SignChanges) o1).ID)));
  }

  /**
   *
   * @param o1 Object
   * @param o2 Object
   * @return int
   */
  public int compare(Object o1, Object o2) {
    return ( ( (Integer) ( (CCC_Bicluster_SignChanges) o1).ID).compareTo( ( (
        Integer) ( (CCC_Bicluster_SignChanges) o2).ID)));
  }

  /**
   *
   * @param o Object
   * @return boolean
   */
  public boolean equals(Object o) {
    if (this == null || o == null || ! (o instanceof CCC_Bicluster_SignChanges)) {
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
        "PATTERN=" + new String(this.getBiclusterExpressionPattern()) +
        " OR PATTERN= " +
        new String(this.getBiclusterExpressionPattern_SignChanges()) + "\n";
    char[][] biclusterSymbolicMatrix = this.getBiclusterSymbolicMatrix();
    for (int g = 0; g < this.getNumberOfGenes(); g++) {
      s = s + this.getGenesNames()[g] + "\t" +
          new String(biclusterSymbolicMatrix[g]) + "\n";
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
         "PATTERN=" + new String(this.getBiclusterExpressionPattern()) +
         " OR PATTERN= " +
         new String(this.getBiclusterExpressionPattern_SignChanges()) + "\n";
     return s;
   }


  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * TO BE IMPLEMENTED !!!!!!
   *
   * @return StyledDocument
   * @todo
   */
  public StyledDocument info() {

    StyledDocument doc = new DefaultStyledDocument();
    StyledDocument doc2 = new HTMLDocument();

    // ...

    return (doc);
  }

} // END CLASS
