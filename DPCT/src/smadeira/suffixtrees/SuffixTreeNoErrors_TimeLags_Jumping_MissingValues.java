package smadeira.suffixtrees;

import java.io.*;
import java.util.*;

/**
 * <p>Title: Suffix Tree No Errors Time Lags Jumping Missing Values</p>
 *
 * <p>Description: A Suffix Tree for identifying
 *                 biclusters with time lagged exact patterns (CCC
 *                 Biclusters with time lags) while jumping over
 *                 missing values.</p>
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
public class SuffixTreeNoErrors_TimeLags_Jumping_MissingValues
    extends SuffixTreeNoErrors_TimeLags implements Serializable {

  /**
   *
   */
  protected int maxTimeLag;

  /**
   *
   * @param numberOfConditions int
   * @param numberOfGenes int
   * @param maxTimeLag int
   */
  public SuffixTreeNoErrors_TimeLags_Jumping_MissingValues(int
      numberOfConditions, int numberOfGenes, int maxTimeLag) {
    super(numberOfConditions, numberOfGenes);
    this.maxTimeLag = maxTimeLag;
  }

  //######################################################################################################################

  /**
   * Returns the row (gene) that corresponds to a given leaf in the suffix tree.
   *
   * @param node LeafNodeNoErrors
   * @return int
   */
  public int computeRowNumber(LeafNodeNoErrors node) {
    int[] label = this.getSubArrayInt(node.getLeftIndex(), node.getLength());
    int terminator = label[label.length - 1];
    int rowNumber = this.numberOfGenes + 2* this.maxTimeLag * this.numberOfGenes + terminator + 1;
    return (rowNumber);
  }

  //######################################################################################################################

  /**
   *
   * @param node LeafNodeNoErrors
   */
  protected void addIsNotLeftMaximal_LeafNode(LeafNodeNoErrors node) {
    int index = this.getIndex(node.getCoordinates().getPosition());
    ArrayList<Token> rowsInserted = this.tokens.vectorOfArrays;
    int totalSubStringLenght = rowsInserted.get(index).getLen();
    if (node.getLength() == 1 // just terminator
        ||
        node.getPathLength() < totalSubStringLenght) {
      node.setIsMaximal(false);
    }
    //go right
    NodeInterface rightSybling = node.getRightSybling();
    if (rightSybling != null) {
      this.addIsNotLeftMaximal(rightSybling);
    }
  }

  //######################################################################################################################

  /**
   *
   * @param node LeafNodeNoErrors
   * @return boolean
   */
  protected boolean addIsNotLeftMaximal_After_TimeLags_LeafNode(
      LeafNodeNoErrors node) {
    boolean isOriginalGene = true;
    int rowNumber = this.computeRowNumber(node);
    int index = this.getIndex(node.getCoordinates().getPosition());
    ArrayList<Token> rowsInserted = this.tokens.vectorOfArrays;
    int totalSubStringLenght = rowsInserted.get(index).getLen();
    if (node.getPathLength() < totalSubStringLenght
        ||
        node.getLength() == 1
        ||
        rowNumber > this.numberOfGenes) { // time-lag
      node.setIsMaximal(false);
    }
    if (rowNumber > this.numberOfGenes) {
      isOriginalGene = false;
      node.setIsMaximal(false);
    }
    // go right
    NodeInterface rightSybling = node.getRightSybling();
    if (rightSybling != null) {
      boolean hasOriginalGeneRight = this.addIsNotLeftMaximal_After_TimeLags(
          rightSybling);
      return (isOriginalGene || hasOriginalGeneRight);
    }
    else {
      return (isOriginalGene);
    }
  }
} // END CLASS
