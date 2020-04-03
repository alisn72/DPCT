package smadeira.suffixtrees;

/**
 * <p>Title: Suffix Tree With Errors Right Maximal Jumping Missing Values</p>
 *
 * <p>Description: Suffix tree with errors right maximal jumping missing values.</p>
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
public class SuffixTreeWithErrorsRightMaximal_Jumping_MissingValues
    extends SuffixTreeWithErrorsRightMaximal {

  /**
   * Number of genes in the gene expression matrix.
   */
  protected int numberOfGenes;

  /**
   *
   * @param numberOfGenes int
   */
  public SuffixTreeWithErrorsRightMaximal_Jumping_MissingValues(int numberOfGenes) {
    super();
    this.root = new InternalNodeWithErrorsRightMaximal();
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
  public int computeRowNumber(LeafNodeWithErrorsRightMaximal node) {
    int[] label = this.getSubArrayInt(node.getLeftIndex(), node.getLength());
    int terminator = label[label.length - 1];
    int rowNumber = terminator + this.numberOfGenes + 1;
    return (rowNumber);
  }

} // END CLASS
