package smadeira.biclustering;

import java.io.*;
import java.util.*;

import javax.swing.text.*;
import javax.swing.text.html.*;

import flanagan.analysis.*;
import smadeira.utils.*;

/**
 * <p>Title: CCC Bicluster with Gene Shifts</p>
 *
 * <p>Description: Contiguous Column Coherent Bicluster with Gene Shifts
 *                 (scaled patterns).</p>
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
public class CCC_Bicluster_GeneShifts
    extends CCC_Bicluster implements Cloneable, Serializable, Comparable,
    NodeObjectInterface {
  public static final long serialVersionUID = -8718744655172532879L;


  // CONSTRUCTORS

  /**
   *
   * @param biclusterSource Biclustering
   * @param ID int
   * @param rows int[]
   * @param columns int[]
   */
  public CCC_Bicluster_GeneShifts(BiclusteringInDiscretizedMatrix
                                  biclusterSource, int ID, int[] rows,
                                  int[] columns) {
    super(null, ID, rows, columns);
    this.biclusterSource = biclusterSource;
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @return ArrayList
   */
  public ArrayList<char[]> getGeneExpressionPatternWithGeneShifts() {
    return Shift_Pattern.shiftPattern(this.getBiclusterExpressionPattern(),
                                      ( (IDiscretizedMatrix)this.
                                       getBiclusterSource().getMatrix()).
                                      getAlphabet(),
                                      ( (CCC_Biclustering_GeneShifts)this.
                                       getBiclusterSource()).getNumberOfShifts());
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @param markovChainOrder int
   * @return double
   */
  protected double compute_BiclusterPattern_Probability(int markovChainOrder) {
      char[][] symbolicMatrix = ( (CCC_Biclustering_GeneShifts)this.biclusterSource).getDiscretizedMatrixAfterGeneShifts();
      char[] alphabet = ( (CCC_Biclustering_GeneShifts)this.biclusterSource).getAlphabetAfterGeneShifts();
      int numberOfShifts = ( (CCC_Biclustering_GeneShifts)this.biclusterSource).getNumberOfShifts();
      double p = this.compute_Pattern_Probability(symbolicMatrix,
          this.getBiclusterExpressionPattern(), markovChainOrder);
      ArrayList<char[]>patternShiftedDownAndUp = Shift_Pattern.shiftPattern(this.getBiclusterExpressionPattern(),
          alphabet, numberOfShifts);
      for (int s = 0; s < patternShiftedDownAndUp.size(); s++) {
        p = p + this.compute_Pattern_Probability(
            symbolicMatrix,
            patternShiftedDownAndUp.get(s),
            markovChainOrder);
      }
      return p;
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @param markovChainOrder int
   * @return double
   */
  protected double compute_BiclusterPatternWithColumns_Probability(int markovChainOrder) {
      char[][] symbolicMatrix = ( (CCC_Biclustering_GeneShifts)this.
                                 biclusterSource).
          getDiscretizedMatrixAfterGeneShifts();
      char[] alphabet = ( (CCC_Biclustering_GeneShifts)this.biclusterSource).getAlphabetAfterGeneShifts();
      int numberOfShifts = ( (CCC_Biclustering_GeneShifts)this.biclusterSource).
          getNumberOfShifts();
      double p = this.compute_PatternWithColumns_Probability(
          symbolicMatrix, this.getBiclusterExpressionPattern(),
          this.columnsIndexes[0] - 1, markovChainOrder);
      ArrayList<char[]> patternShiftedDownAndUp = Shift_Pattern.shiftPattern(this.
          getBiclusterExpressionPattern(),
          alphabet, numberOfShifts);
      for (int s = 0; s < patternShiftedDownAndUp.size(); s++) {
        p = p + this.compute_PatternWithColumns_Probability(
            symbolicMatrix,
            patternShiftedDownAndUp.get(s),
            this.columnsIndexes[0] - 1,
            markovChainOrder);
      }
      return p;
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @param o Object
   * @return boolean
   */
  public boolean equals(Object o) {
    if (this == null || o == null || ! (o instanceof CCC_Bicluster_GeneShifts)) {
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
    return ( ( (Integer) (this.ID)).compareTo( ( (Integer) ( (
        CCC_Bicluster_GeneShifts) o1).ID)));
  }

  /**
   *
   * @return Object
   */
  public Object clone() {
    CCC_Bicluster_GeneShifts o = (CCC_Bicluster_GeneShifts)super.clone();
    return o;
  }

  //................................................................

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
