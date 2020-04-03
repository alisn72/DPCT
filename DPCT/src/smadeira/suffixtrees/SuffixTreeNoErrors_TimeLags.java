package smadeira.suffixtrees;

import java.io.*;

/**
 * <p>Title: Suffix Tree No Errors Time Lags</p>
 *
 * <p>Description: A Suffix Tree extended for identifying
 *                 biclusters with exact patterns (CCC Biclusters)
 *                 and time lags (time lagged patterns).</p>
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
public class SuffixTreeNoErrors_TimeLags
    extends SuffixTreeNoErrors implements Serializable {

  /**
   *
   */
  protected int numberOfConditions;

  /**
   *
   */
  protected int numberOfGenes;

  /**
   *
   * @param numberOfConditions int without the terminator
   * @param numberOfGenes int
   */
  public SuffixTreeNoErrors_TimeLags(int numberOfConditions, int numberOfGenes) {
    super();
    this.root = new InternalNodeNoErrors_TimeLags();
    //!!!
    this.tokens = new ListOfTokensOfDifferentSize();
    this.numberOfConditions = numberOfConditions;
    this.numberOfGenes = numberOfGenes;
  }

  /**
   *
   * @return int
   */
  public int getNumberOfConditions() {
    return (this.numberOfConditions);
  }

  /**
   * Returns the number of genes in the original (without time-lags) expression matrix.
   * @return int
   */
  public int getNumberOfGenes() {
    return (this.numberOfGenes);
  }

  //##########################################################################################################
  //#################### MARK THE NODES THAT CORRESPOND TO NON-MAXIMAL CCC-BICLUSTERS AFTER TIME-LAGS #######
  //##########################################################################################################

  /**
   *
   */
  public void addIsNotLeftMaximal_After_TimeLags() {
    // the root must will be marked as "maximal" although it does not represent any ccc-bicluster
    this.addIsNotLeftMaximal_After_TimeLags(this.root.getFirstChild());
  }

  /**
   *
   * @param node NodeInterface
   * @return boolean
   */
  protected boolean addIsNotLeftMaximal_After_TimeLags(NodeInterface node) {
    if (node instanceof LeafNodeNoErrors) {
      return this.addIsNotLeftMaximal_After_TimeLags_LeafNode( (
          LeafNodeNoErrors) node);
    }
    else {
      if (node instanceof InternalNodeNoErrors_TimeLags) {
        // get number of rows of the node (numberOfLeaves)
        // get number of rows of its suffix link
        NodeInterface suffixLink = ( (InternalNodeNoErrors_TimeLags) node).
            getSuffixLink();
        if (suffixLink.getNumberOfLeaves() <=
            ( (InternalNodeNoErrors_TimeLags) node).getNumberOfLeaves()) {
          ( (InternalNodeNoErrors_TimeLags) suffixLink).
              setIsMaximal_After_TimeLags(false);
        }
        // go down
        NodeInterface firstChild = ( (InternalNodeNoErrors_TimeLags) node).
            getFirstChild();
        boolean hasOriginalGeneDown = this.addIsNotLeftMaximal_After_TimeLags(
            firstChild);
        if (!hasOriginalGeneDown) {
          ( (InternalNodeNoErrors_TimeLags) node).setIsMaximal_After_TimeLags(false);
        }
        //go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          boolean hasOriginalGeneRight = this.
              addIsNotLeftMaximal_After_TimeLags(rightSybling);
          return (hasOriginalGeneDown || hasOriginalGeneRight);
        }
        else {
          return (hasOriginalGeneDown);
        }
      }
      else {
        return false;
      }
    }
  }

  /**
   *
   * @param node LeafNodeNoErrors
   * @return boolean
   */
  protected boolean addIsNotLeftMaximal_After_TimeLags_LeafNode(
      LeafNodeNoErrors node) {
    boolean isOriginalGene = true;
    int rowNumber = this.computeRowNumber( (LeafNodeNoErrors) node);
    if ( ( (LeafNodeNoErrors) node).getPathLength() - 1 != numberOfConditions
        ||
        node.getLength() == 1
        ||
        rowNumber > this.numberOfGenes) {
      ( (LeafNodeNoErrors) node).setIsMaximal(false);
    }
    if (rowNumber > this.numberOfGenes) {
      isOriginalGene = false;
      ( (LeafNodeNoErrors) node).setIsMaximal(false);
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
