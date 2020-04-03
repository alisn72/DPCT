package smadeira.suffixtrees;

import java.io.*;
import java.util.*;

/**
 * <p>Title: Suffix Tree No Errors Jumping Missing Values</p>
 *
 * <p>Description: A suffix tree for identifying biclusters
 *                 with exact patterns (CCC Biclusters) and able to
 *                 jump over missing values.</p>
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
public class SuffixTreeNoErrors_Jumping_MissingValues
    extends SuffixTreeNoErrors implements Serializable {

  /**
   * Number of genes in the gene expression matrix.
   */
  protected int numberOfGenes;

  /**
   *
   * @param numberOfGenes int
   */
  public SuffixTreeNoErrors_Jumping_MissingValues(int numberOfGenes) {
    super();
    this.root = new InternalNodeNoErrors();
    //!!!
    this.tokens = new ListOfTokensOfDifferentSize();
    this.numberOfGenes = numberOfGenes;
  }

  /**
   * Returns the row (gene) that corresponds to a given leaf in the suffix tree.
   *
   * @param node LeafNodeNoErrors
   * @return int
   */
  public int computeRowNumber(LeafNodeNoErrors node) {
    int[] label = this.getSubArrayInt(node.getLeftIndex(), node.getLength());
    int terminator = label[label.length - 1];
    int rowNumber = terminator + this.numberOfGenes + 1;
    return (rowNumber);
  }

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

} // END CLASS
