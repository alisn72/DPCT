package smadeira.suffixtrees;

import java.util.*;

/**
 * <p>Title: Suffix Tree With Errors Right Maximal</p>
 *
 * <p>Description: Suffix tree with errors right maximal.</p>
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
public class SuffixTreeWithErrorsRightMaximal
    extends SuffixTree {

  /**
   *
   */
  protected SuffixTreeWithErrorsRightMaximal() {
    super();
    this.root = new InternalNodeWithErrorsRightMaximal();
  }

  /**
   *
   * @param tokenSize int
   */
  public SuffixTreeWithErrorsRightMaximal(int tokenSize) {
    super();
    this.root = new InternalNodeWithErrorsRightMaximal();
    this.tokens = new ListOfTokensOfSameSize(tokenSize);
  }

  // ###################################################################################################################
  // ############################################# METHODS TO ADD INFO TO THE NODES ####################################
  // ###################################################################################################################

  // ########################################## ADD COLOR ARRAY #######################################################

  /**
   * Adds color to the nodes in the suffix tree.
   */
  public void addColorArray() {
    InternalNodeWithErrorsRightMaximal node = (InternalNodeWithErrorsRightMaximal)this.root;
    node.setColors(this.addColorArray(node));
  }

  /**
   * Used recursively !!!
   *
   * @param node NodeInterface
   * @return BitSet
   */
  protected BitSet addColorArray(NodeInterface node) {
    if (node instanceof InternalNodeWithErrorsRightMaximal) {
      // initialize color array
      ( (InternalNodeWithErrorsRightMaximal) node).setColors(new BitSet());
      // get first child and right symbling
      NodeInterface firstChild = ( (InternalNodeWithErrorsRightMaximal) node).
          getFirstChild();
      NodeInterface rightSybling = node.getRightSybling();
      // go down
      BitSet colorArray = this.addColorArray(firstChild);
      ( (InternalNodeWithErrorsRightMaximal) node).getColors().or(colorArray);
      // go right
      if (rightSybling != null) {
        colorArray.or(this.addColorArray(rightSybling));
        return (colorArray);
      }
      else {
        return (colorArray);
      }
    }
    else {
      if (node instanceof LeafNodeWithErrorsRightMaximal) {
        // get right symbling
        NodeInterface rightSybling = node.getRightSybling();
        // get row number
        int rowNumber = this.computeRowNumber( (LeafNodeWithErrorsRightMaximal)node); // number in {1,#genes}
        // set color = row number
        ( (LeafNodeWithErrorsRightMaximal) node).setColor(rowNumber - 1); // color in {0,#genes-1}
//          System.out.println("Color = " + ((LeafNodeWithErrorsRightMaximal)node).getColor());
        // construct color array to be returned
        BitSet colorArray = new BitSet();
        // set color corresponding to the leaf
        colorArray.set(rowNumber - 1); // indexes in bit array go in {0,#genes-1}
//          System.out.println("Colors = " + colorArray.toString());
        if (rightSybling != null) {
          colorArray.or(addColorArray(rightSybling));
          return (colorArray);
        }
        else {
          return (colorArray);
        }
      }
      else {
        return (null);
      }
    }
  }

  // ###################################################################################################################
  // ############################################# USEFUL METHODS ######################################################
  // ###################################################################################################################

  // ######## COMPUTE ROW (GENE) THAT CORRESPONDS TO A GIVEN LEAF IN THE TREE.
  /**
   * Returns the row (gene) that corresponds to a given leaf in the suffix tree.
   *
   * @param node LeafNodeWithErrorsRightMaximal
   * @return int
   */
  public int computeRowNumber(LeafNodeWithErrorsRightMaximal node) {
    return (this.getIndex( ( (LeafNodeWithErrorsRightMaximal) node).
                          getCoordinates().getPosition()) + 1);
  }

  // ########## COMPUTE NUMBER OF ROWS OF THE CCC-BICLUSTER CORRESPONDING TO A GIVEN NODE IN THE TREE
  /**
   * Returns the number of rows of the ccc-bicluster that corresponds to a given node in the suffix tree.
   *
   * @param node NodeInterface
   * @return int
   */
  public int computeNumberOfRows(NodeInterface node) {
    int numberOfRows = -1;
    if (node instanceof InternalNodeWithErrorsRightMaximal) {
      numberOfRows = ( (InternalNodeWithErrorsRightMaximal) node).getNumberOfLeaves();
    }
    else {
      if (node instanceof LeafNodeWithErrorsRightMaximal) {
        numberOfRows = 1;
      }
    }
    return (numberOfRows);
  }

  /**
   * Returns the number of rows in the expression matrix used to construct this suffix tree.
   * @return int
   */
  public int getNumberOfGenes() {
    return (this.tokens.getTokenOfArrays().size());
  }

  /**
   *
   * @param node NodeInterface
   * @return BitSet
   */
  public BitSet computeGenes(NodeInterface node) {
    BitSet genes = new BitSet();
    if (node instanceof LeafNodeWithErrorsRightMaximal) {
      int rowNumber = this.computeRowNumber( (LeafNodeWithErrorsRightMaximal)node);
      genes.set(rowNumber - 1, true);
    }
    else {
      if (node instanceof InternalNodeWithErrorsRightMaximal) {
        computeGenes(node, genes);
      }
    }
    return genes;
  }

  /**
   *
   * @param node NodeInterface
   * @param genes BitSet
   * @return BitSet
   */
  public BitSet computeGenes(NodeInterface node, BitSet genes) {
    if (node instanceof InternalNodeWithErrorsRightMaximal) {
      // go down
      return (computeGenes( ( (
          InternalNodeWithErrorsRightMaximal) node).getFirstChild(), genes));
    }
    else {
      if (node instanceof LeafNodeWithErrorsRightMaximal) {
        int rowNumber = this.computeRowNumber( (LeafNodeWithErrorsRightMaximal)
                                              node);
        genes.set(rowNumber - 1, true);
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          return (computeGenes(rightSybling, genes));
        }
        else {
          return genes;
        }
      }
      else {
        return genes;
      }
    }
  }
} // END CLASS
