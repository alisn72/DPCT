package smadeira.suffixtrees;

import java.io.*;

/**
 * <p>Title: Suffix Tree No Errors Extended</p>
 *
 * <p>Description: A Suffix Tree extended for identifying
 *                 biclusters with exact patterns (CCC Biclusters)
 *                 and special additional characteristics, such as
 *                 gene shifts (scaled patterns), sign changes
 *                 (anticorrelated patterns) or time lags (time
 *                 lagged patterns).</p>
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
public class SuffixTreeNoErrors_Extended
    extends SuffixTreeNoErrors implements Serializable {

  /**
   * Extension can be:
   * gene-shifts
   * sign-changes
   */

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
   * @param numberOfConditions int
   * @param numberOfGenes int
   */
  public SuffixTreeNoErrors_Extended(int numberOfConditions, int numberOfGenes) {
    super();
    this.root = new InternalNodeNoErrors_Extended();
    this.tokens = new ListOfTokensOfSameSize(numberOfConditions);
    this.numberOfConditions = numberOfConditions - 1;
    this.numberOfGenes = numberOfGenes;
  }

  // COMPUTE THE NUMBER OF COLUMNS (CONDITIONS) IN THE GENE EXPRESSION MATRIX USED TO CONSTRUCT THE SUFFIX TREE
  /**
   * Returns the number of conditions in the expression matrix used to construct this suffix tree.
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

  //######################################################################################################################
  //#################### MARK THE NODES THAT CORRESPOND TO NON-MAXIMAL CCC-BICLUSTERS AFTER SIGN-CHANGES/GENE-SHIFTS #####
  //######################################################################################################################

  /**
   *
   */
  public void addIsNotLeftMaximal_After_Extensions() {
    // the root must will be marked as "maximal" although it does not represent any ccc-bicluster
    this.addIsNotLeftMaximal_After_Extensions(this.root.getFirstChild());
  }

  //######################################################################################################################

  /**
   *
   * @param node NodeInterface
   * @return boolean
   */
  protected boolean addIsNotLeftMaximal_After_Extensions(NodeInterface node) {
    if (node instanceof LeafNodeNoErrors) {
      return this.addIsNotLeftMaximal_After_Extensions_LeafNode( (LeafNodeNoErrors) node);
    }
    else {
      if (node instanceof InternalNodeNoErrors_Extended) {
        // get number of rows of the node (numberOfLeaves)
        // get number of rows of its suffix link
        InternalNodeNoErrors_Extended suffixLink = (
            InternalNodeNoErrors_Extended) ( (InternalNodeNoErrors_Extended) node).getSuffixLink();
        if (suffixLink.getNumberOfLeaves() ==
            ( (InternalNodeNoErrors_Extended) node).getNumberOfLeaves()) {
          suffixLink.setIsMaximal_After_Extensions(false);
        }
        // go down
        NodeInterface firstChild = ( (InternalNodeNoErrors_Extended) node).
            getFirstChild();
        boolean hasOriginalGeneDown = this.addIsNotLeftMaximal_After_Extensions(
            firstChild);
        if (!hasOriginalGeneDown) {
          ( (InternalNodeNoErrors_Extended) node).setIsMaximal_After_Extensions(false);
        }
        //go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          boolean hasOriginalGeneRight = this.addIsNotLeftMaximal_After_Extensions(rightSybling);
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

  //######################################################################################################################

  /**
   *
   * @param node LeafNodeNoErrors
   * @return boolean
   */
  protected boolean addIsNotLeftMaximal_After_Extensions_LeafNode(LeafNodeNoErrors node) {
    boolean isOriginalGene = true;
    int rowNumber = this.computeRowNumber(node);
    if (node.getPathLength() - 1 != this.getNumberOfConditions()
        ||
        node.getLength() == 1
        ||
        rowNumber > this.numberOfGenes) { // signChange
      node.setIsMaximal(false);
    }
    if (rowNumber > this.numberOfGenes) {
      isOriginalGene = false;
    }
    // go right
    NodeInterface rightSybling = node.getRightSybling();
    if (rightSybling != null) {
      boolean hasOriginalGeneRight = this.addIsNotLeftMaximal_After_Extensions(rightSybling);
      return (isOriginalGene || hasOriginalGeneRight);
    }
    else {
      return (isOriginalGene);
    }
  }

} // END CLASS
