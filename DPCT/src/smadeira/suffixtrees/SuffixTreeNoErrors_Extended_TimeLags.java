package smadeira.suffixtrees;

import java.io.*;
import java.util.*;

/**
 * <p>Title: Suffix Tree No Errors Time Lags</p>
 *
 * <p>Description: Builder of a Suffix Tree extended for identifying
 *                 biclusters with exact patterns (CCC Biclusters)
 *                 and time lags (time lagged patterns) - STEP 1.</p>
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
public class SuffixTreeNoErrors_Extended_TimeLags
    extends SuffixTreeNoErrors implements Serializable {

  /**
   * Extension is:
   * sign-Changes
   **/

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
  public SuffixTreeNoErrors_Extended_TimeLags(int numberOfConditions,
                                              int numberOfGenes) {
    super();
    //!!!
    this.root = new InternalNodeNoErrors_Extended_TimeLags();
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
      if (node instanceof InternalNodeNoErrors_Extended_TimeLags) {
        // get number of rows of the node (numberOfLeaves)
        // get number of rows of its suffix link
        NodeInterface suffixLink = ( (InternalNodeNoErrors_Extended_TimeLags) node).getSuffixLink();
        if (suffixLink.getNumberOfLeaves() <= ( (InternalNodeNoErrors_Extended_TimeLags) node).getNumberOfLeaves()) {
          ( (InternalNodeNoErrors_Extended_TimeLags) suffixLink).setIsMaximal_After_TimeLags(false);
        }
        // go down
        NodeInterface firstChild = ((InternalNodeNoErrors_Extended_TimeLags) node).getFirstChild();
        boolean hasOriginalGeneDown = this.addIsNotLeftMaximal_After_TimeLags(firstChild);
        if (!hasOriginalGeneDown) {
          ( (InternalNodeNoErrors_Extended_TimeLags) node).setIsMaximal_After_TimeLags(false);
        }
        //go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          boolean hasOriginalGeneRight = this.addIsNotLeftMaximal_After_TimeLags(rightSybling);
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

  //##########################################################################################################
  //#################### MARK THE NODES THAT CORRESPOND TO NON-MAXIMAL CCC-BICLUSTERS AFTER EXTENSIONS## #####
  //##########################################################################################################

  /**
   *
   */
  public void addIsNotLeftMaximal_After_Extensions() {
    // the root must will be marked as "maximal" although it does not represent any ccc-bicluster
    this.addIsNotLeftMaximal_After_Extensions(this.root.getFirstChild());
  }

  /**
   *
   * @param node NodeInterface
   * @return boolean
   */
  protected boolean addIsNotLeftMaximal_After_Extensions(NodeInterface node) {
    if (node instanceof LeafNodeNoErrors) {
      return this.addIsNotLeftMaximal_After_Extensions_LeafNode( (
          LeafNodeNoErrors) node);
    }
    else {
      if (node instanceof InternalNodeNoErrors_Extended_TimeLags) {
        // get number of rows of the node (numberOfLeaves)
        // get number of rows of its suffix link
        NodeInterface suffixLink = ( (InternalNodeNoErrors_Extended_TimeLags)node).getSuffixLink();
        if (suffixLink.getNumberOfLeaves() <= ( (InternalNodeNoErrors_Extended_TimeLags) node).getNumberOfLeaves()) {
          ( (InternalNodeNoErrors_Extended_TimeLags) suffixLink).setIsMaximal_After_Extensions(false);
        }
        // go down
        NodeInterface firstChild = ( (InternalNodeNoErrors_Extended_TimeLags)node).getFirstChild();
        boolean hasOriginalGeneDown = this.addIsNotLeftMaximal_After_Extensions(
            firstChild);
        if (!hasOriginalGeneDown) {
          ( (InternalNodeNoErrors_Extended_TimeLags) node).
              setIsMaximal_After_Extensions(false);
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

  /**
   *
   * @param node NodeInterface
   * @return boolean
   */
  protected boolean addIsNotLeftMaximal_After_Extensions_LeafNode(LeafNodeNoErrors node) {
    boolean isOriginalGene = true;
    int rowNumber = this.computeRowNumber(node);
    if (node.getPathLength() - 1 != numberOfConditions
        ||
        node.getLength() == 1
        ||
        rowNumber > this.numberOfGenes) { // signChange OR gene-shift
      node.setIsMaximal(false);
    }
    if (rowNumber > this.numberOfGenes) {
      isOriginalGene = false;
      node.setIsMaximal(false);
    }
    // go right
    NodeInterface rightSybling = node.getRightSybling();
    if (rightSybling != null) {
      boolean hasOriginalGeneRight = this.addIsNotLeftMaximal_After_Extensions(
          rightSybling);
      return (isOriginalGene || hasOriginalGeneRight);
    }
    else {
      return (isOriginalGene);
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
        rowNumber > this.numberOfGenes) { // signChange OR gene-shift
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
