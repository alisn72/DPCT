package smadeira.suffixtrees;

import java.io.*;

/**
 * <p>Title: Suffix Tree No Errors</p>
 *
 * <p>Description: A Suffix Tree for identifying
 *                 biclusters with exact patterns (CCC Biclusters).</p>
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
public class SuffixTreeNoErrors
    extends SuffixTree implements Serializable {

  /**
   * Number of maximal ccc-biclusters.
   */
  protected int numberOfMaximalCccBiclusters;

  /**
   * Number of maximal non-trivial ccc-biclusters.
   */
  protected int numberOfMaximalNonTrivialCccBiclusters;

  /**
   *
   */
  protected SuffixTreeNoErrors() {
    super();
    this.root = new InternalNodeNoErrors();
  }

  /**
   *
   * @param numberOfConditions int
   */
  public SuffixTreeNoErrors(int numberOfConditions) {
    super();
    this.root = new InternalNodeNoErrors();
    this.tokens = new ListOfTokensOfSameSize(numberOfConditions);
  }

  /**
   * Compute number of maximal ccc-biclusters.
   */
  public void computeNumberOfMaximalCccBiclusters() {
    //initialize counters
    this.numberOfMaximalCccBiclusters = 0;
    // the root is probably marked as "maximal" although it does not represent any ccc-bicluster
    // the root must not be counted !!!
    if (this.numberOfNodes <= 1) {
      this.computeNumberOfNodes();
      this.computeNumberOfMaximalCccBiclusters(this.root.getFirstChild());
    }
    else {
      this.computeNumberOfMaximalCccBiclusters(this.root.getFirstChild());
    }
  }

  /**
   * Computes the number of maximal ccc-biclusters.
   *
   * @param node NodeInterface
   */
  private void computeNumberOfMaximalCccBiclusters(NodeInterface node) {
    if (node == null) {
      return;
    }
    else {
      if (node instanceof InternalNodeNoErrors) {
        // check if the node is maximal and count it if it is
        if ( ( (InternalNodeNoErrors) node).getIsMaximal() == true) {
          this.numberOfMaximalCccBiclusters++;
        }
        // go down
        this.computeNumberOfMaximalCccBiclusters( ( (InternalNodeNoErrors) node).
                                                 getFirstChild());
        // go right
        this.computeNumberOfMaximalCccBiclusters(node.getRightSybling());
      }
      else {
        if (node instanceof LeafNodeNoErrors) {
          // check if the node is maximal and count it if it is.
          if ( ( (LeafNodeNoErrors) node).getIsMaximal() == true) {
            this.numberOfMaximalCccBiclusters++;
          }
          // go right
          this.computeNumberOfMaximalCccBiclusters(node.getRightSybling());
        }
      }
    }
  }

  /**
   * Compute number of maximal non-trivial ccc-biclusters.
   */
  public void computeNumberOfMaximalNonTrivialCccBiclusters() {
    //initialize counters
    this.numberOfMaximalNonTrivialCccBiclusters = 0;

    // the root must not be counted
    if (this.numberOfNodes <= 1) {
      this.computeNumberOfNodes();
      this.computeNumberOfMaximalCccBiclusters(this.root.getFirstChild());
      this.computeNumberOfMaximalNonTrivialCccBiclusters(this.root);
    }
    else {
      this.computeNumberOfMaximalNonTrivialCccBiclusters(this.root);
    }
  }

  /**
   * Computes the number of maximal non-trivial ccc-biclusters.
   *
   * @param node NodeInterface
   */
  private void computeNumberOfMaximalNonTrivialCccBiclusters(NodeInterface node) {
    if (node == null) {
      return;
    }
    else {
      if (node instanceof InternalNodeNoErrors) {
        if ( ( (InternalNodeNoErrors) node).getIsMaximal() == true &&
            ( (InternalNodeNoErrors) node).getPathLength() > 1) {
          this.numberOfMaximalNonTrivialCccBiclusters++;
        }
        // go down
        this.computeNumberOfMaximalNonTrivialCccBiclusters( ( (
            InternalNodeNoErrors) node).getFirstChild());
        // go right
        this.computeNumberOfMaximalNonTrivialCccBiclusters(node.getRightSybling());
      }
      else {
        if (node instanceof LeafNodeNoErrors) {
          // the maximal ccc-biclusters in the leaves are always trivial (only have one row)
          // go right
          this.computeNumberOfMaximalNonTrivialCccBiclusters(node.
              getRightSybling());
        }
        else {
          return;
        }
      }
    }
  }

  /**
   * Compute number of nodes (total, leaves and internal), number of maximal ccc-biclusters and
   * number of maximal non-trivial ccc-biclusters.
   */
  public void computeSuffixTreeStatistics() {
    //initialize counters
    this.numberOfNodes = 1; // root
    this.numberOfLeafNodes = 0;
    this.numberOfInternalNodes = 0;
    this.numberOfMaximalCccBiclusters = 0;
    this.numberOfMaximalNonTrivialCccBiclusters = 0;
    this.computeSuffixTreeStatistics(this.root.getFirstChild());
  }

  /**
   * Used recursively.
   *
   * @param node NodeInterface
   */
  private void computeSuffixTreeStatistics(NodeInterface node) {
    if (node == null) {
      return;
    }
    else {
      this.numberOfNodes++;
      // check if it's an internal node
      if (node instanceof InternalNodeNoErrors) {
        this.numberOfInternalNodes++;
        //  check if its maximal
        if ( ( (InternalNodeNoErrors) node).getIsMaximal() == true) {
          this.numberOfMaximalCccBiclusters++;
          // check if it's non-trivial
          if ( ( (InternalNodeNoErrors) node).getPathLength() > 1) {
            this.numberOfMaximalNonTrivialCccBiclusters++;
          }
        }
        // go down
        this.computeSuffixTreeStatistics( ( (InternalNodeNoErrors) node).
                                         getFirstChild());
        // go right
        this.computeSuffixTreeStatistics(node.getRightSybling());
      }
      else {
        if (node instanceof LeafNodeNoErrors) {
          this.numberOfLeafNodes++;
          // the maximal ccc-biclusters in the leaves are always trivial (only have one row)
          //  check if its maximal
          if ( ( (LeafNodeNoErrors) node).getIsMaximal() == true) {
            this.numberOfMaximalCccBiclusters++;
          }
          // go right
          this.computeSuffixTreeStatistics(node.getRightSybling());
        }
        else {
          return;
        }
      }
    }
  }

  // ###################################################################################################################
  // ############################################# METHODS TO ADD INFO TO THE NODES ####################################
  // ###################################################################################################################

  // ###################### MARK THE NODES THAT CORRESPOND TO NON-MAXIMAL CCC-BICLUSTERS
  /**
   * Method to mark the nodes that are not maximal biclusters.
   * All the nodes are "right-maximal" but only the ones that are "rigth and left maximal" are true maximal biclusters.
   */
  public void addIsNotLeftMaximal() {
    InternalNodeNoErrors root = (InternalNodeNoErrors)this.getRoot();
    // the root must will be marked as "maximal" although it does not represent any ccc-bicluster
    this.addIsNotLeftMaximal(root.getFirstChild());
  }

  /**
   * Marks the nodes that correspond to non-maximal ccc-biclusters (no errors).
   *
   * Used recursively!!
   *
   * The pathlength of each node and the number of leaves of each internal node must be computed first!!
   *
   * @param node NodeInterface
   */
  protected void addIsNotLeftMaximal(NodeInterface node) {
    if (node == null) {
      return;
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        this.addIsNotLeftMaximal_LeafNode( (LeafNodeNoErrors) node);
      }
      else {
        if (node instanceof InternalNodeNoErrors) {
          // get number of rows of the node (numberOfLeaves)
          // get number of rows of its suffix link
          InternalNodeNoErrors suffixLink = (InternalNodeNoErrors) ( (
              InternalNodeNoErrors) node).getSuffixLink();
          if ( ( (InternalNodeNoErrors) suffixLink).getNumberOfLeaves() <=
              ( (InternalNodeNoErrors) node).getNumberOfLeaves()) {
            ( (InternalNodeNoErrors) suffixLink).setIsMaximal(false);
          }
          // go down
          this.addIsNotLeftMaximal( ( (InternalNodeNoErrors) node).
                                   getFirstChild());
          // go right
          NodeInterface rightSybling = node.getRightSybling();
          if (rightSybling != null) {
            this.addIsNotLeftMaximal(rightSybling);
          }
        }
      }
    }
  }

  /**
   *
   * @param node LeafNodeNoErrors
   */
  protected void addIsNotLeftMaximal_LeafNode(LeafNodeNoErrors node) {
    // get number of columns of the node (pathLength)
    // pathLength-1 because the last character is the string terminator
    if (node.getPathLength() - 1 != this.getNumberOfConditions() ||
        node.getLength() == 1) {
      node.setIsMaximal(false);
    }
    //go right
    NodeInterface rightSybling = node.getRightSybling();
    if (rightSybling != null) {
      this.addIsNotLeftMaximal(rightSybling);
    }
  }

  // ###################################################################################################################
  // ############################################# USEFUL METHODS ######################################################
  // ###################################################################################################################

  // ############# COMPUTE THE FIRST COLUMN OF THE CCC-BICLUSTER (WITHOUT ERRORS) CORRESPONDING TO A GIVEN NODE

  /**
   * Computes the first column of the ccc-bicluster represented by the node passed as parameter.
   *
   * @param node NodeInterface
   * @return int
   */
  public int computeFirstColumn(NodeInterface node) {
    int firstColumn = -1;
    int pathLength = 0;
    if (node instanceof InternalNodeNoErrors) {
      pathLength = ( (InternalNodeNoErrors) node).getPathLength();
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        pathLength = ( (LeafNodeNoErrors) node).getPathLength();
      }
    }
    int nodeLength = node.getLength();
    int[] intAtLeftIndex = this.getSubArrayInt(node.getLeftIndex(), 1);
    // if intAtLeftIndex = 7810 first firstColumn = 10
    if (pathLength == nodeLength) {
      firstColumn = Integer.parseInt(Integer.toString(intAtLeftIndex[0]).
                                     substring(2));
    }
    else { // pathLength > nodeLength => node is subnode of at least one node
      firstColumn = Integer.parseInt(Integer.toString(intAtLeftIndex[0]).
                                     substring(2)) - (pathLength - nodeLength);
    }
    return (firstColumn);
  }

  // CLASS METHOD
  /**
   * Computes the first column of the ccc-bicluster represented by the node passed as parameter.
   *
   * @param tree SuffixTree
   * @param node NodeInterface
   * @return int
   */
  public static int computeFirstColumn(SuffixTreeNoErrors tree,
                                       NodeInterface node) {
    int firstColumn = -1;
    int pathLength = 0;
    if (node instanceof InternalNodeNoErrors) {
      pathLength = ( (InternalNodeNoErrors) node).getPathLength();
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        pathLength = ( (LeafNodeNoErrors) node).getPathLength();
      }
    }
    int nodeLength = node.getLength();
    int[] intAtLeftIndex = tree.getSubArrayInt(node.getLeftIndex(), 1);
    // if intAtLeftIndex = 7810 first firstColumn = 10
    if (pathLength == nodeLength) {
      firstColumn = Integer.parseInt(Integer.toString(intAtLeftIndex[0]).
                                     substring(2));
    }
    else { // pathLength > nodeLength => node is subnode of at least one node
      firstColumn = Integer.parseInt(Integer.toString(intAtLeftIndex[0]).
                                     substring(2)) - (pathLength - nodeLength);
    }
    return (firstColumn);
  }

  // ############# COMPUTE THE LAST COLUMN OF THE CCC-BICLUSTER (WITHOUT ERRORS) CORRESPONDING TO A GIVEN NODE

  /**
   * Computes the last column of the ccc-bicluster represented by the node passed as parameter.
   *
   * @param node NodeInterface
   * @return int
   */
  public int computeLastColumn(NodeInterface node) {
    int lastColumn = -1;
    if (node instanceof InternalNodeNoErrors) {
      lastColumn = this.computeFirstColumn(node) +
          ( (InternalNodeNoErrors) node).getPathLength() - 1;
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        lastColumn = this.computeFirstColumn(node) +
            ( (InternalNodeNoErrors) node).getPathLength() - 2;
      }
    }
    return (lastColumn);
  }

  // CLASS METHOD
  /**
   * Computes the last column of the ccc-bicluster represented by the node passed as parameter in the given tree.
   *
   * @param tree SuffixTree
   * @param node NodeInterface
   * @return int
   */
  public static int computeLastColumn(SuffixTreeNoErrors tree,
                                      NodeInterface node) {
    int lastColumn = -1;
    if (node instanceof InternalNodeNoErrors) {
      lastColumn = tree.computeFirstColumn(node) +
          ( (InternalNodeNoErrors) node).getPathLength() - 1;
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        lastColumn = tree.computeFirstColumn(node) +
            ( (InternalNodeNoErrors) node).getPathLength() - 2;
      }
    }
    return (lastColumn);
  }

  /**
   * Computes the last column of the ccc-bicluster represented by the node passed as parameter given its first column.
   *
   * @param node NodeInterface
   * @param firstColumn int
   * @return int
   */
  public int computeLastColumn(NodeInterface node, int firstColumn) {
    int lastColumn = -1;
    if (node instanceof InternalNodeNoErrors) {
      lastColumn = firstColumn + ( (InternalNodeNoErrors) node).getPathLength() -
          1;
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        lastColumn = firstColumn + ( (InternalNodeNoErrors) node).getPathLength() -
            2;
      }
    }
    return (lastColumn);
  }

  // CLASS METHOD
  /**
   * Computes the last column of the ccc-bicluster represented by the node passed as parameter given its first column.
   *
   * @param node NodeInterface
   * @param firstColumn int
   * @return int
   */
  public static int computeLastColumnGivenFirst(NodeInterface node,
                                                int firstColumn) {
    int lastColumn = -1;
    if (node instanceof InternalNodeNoErrors) {
      lastColumn = firstColumn + ( (InternalNodeNoErrors) node).getPathLength() -
          1;
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        lastColumn = firstColumn + ( (LeafNodeNoErrors) node).getPathLength() -
            2;
      }
    }
    return (lastColumn);
  }

  // ######## COMPUTE ROW (GENE) THAT CORRESPONDS TO A GIVEN LEAF IN THE TREE.
  /**
   * Returns the row (gene) that corresponds to a given leaf in the suffix tree.
   *
   * @param node LeafNodeNoErrors
   * @return int
   */
  public int computeRowNumber(LeafNodeNoErrors node) {
    return (this.getIndex( ( (LeafNodeNoErrors) node).getCoordinates().getPosition()) + 1);
  }

  // CLASS METHOD
  /**
   * Returns the row (gene) that corresponds to a given leaf in the given suffix tree.
   *
   * @param tree SuffixTree
   * @param node LeafNodeNoErrors
   * @return int
   */
  public static int computeRowNumber(SuffixTreeNoErrors tree, LeafNodeNoErrors node) {
    return (tree.getIndex( ( (LeafNodeNoErrors) node).getCoordinates().getPosition()) + 1);
  }

  // ########## COMPUTE NUMBER OF COLUMNS OF THE CCC-BICLUSTER CORRESPONDING TO A GIVEN NODE IN THE TREE

  /**
   * Returns the number of columns of the ccc-bicluster that corresponds to a given node in the suffix tree.
   *
   * @param node NodeInterface
   * @return int
   */
  public int computeNumberOfColumns(NodeInterface node) {
    int numberOfColumns = -1;
    if (node instanceof InternalNodeNoErrors) {
      numberOfColumns = ( (InternalNodeNoErrors) node).getPathLength();
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        numberOfColumns = ( (LeafNodeNoErrors) node).getPathLength() - 1; // last int is the terminator
      }
    }
    return (numberOfColumns);
  }

  // CLASS METHOD
  /**
   * Returns the number of columns of the ccc-bicluster that corresponds to a given node in a given suffix tree.
   *
   * @param node LeafNodeNoErrors
   * @return int
   */
  public static int computeBiclusterNumberOfColumns(NodeInterface node) {
    int numberOfColumns = -1;
    if (node instanceof InternalNodeNoErrors) {
      numberOfColumns = ( (InternalNodeNoErrors) node).getPathLength();
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        numberOfColumns = ( (LeafNodeNoErrors) node).getPathLength() - 1; // last int is the terminator
      }
    }
    return (numberOfColumns);
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
    if (node instanceof InternalNodeNoErrors) {
      numberOfRows = ( (InternalNodeNoErrors) node).getNumberOfLeaves();
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        numberOfRows = 1;
      }
    }
    return (numberOfRows);
  }

  //CLASS METHOD
  /**
   * Returns the number of rows of the ccc-bicluster that corresponds to a given node in a given suffix tree.
   *
   * @param node NodeInterface
   * @return int
   */
  public static int computeBiclusterNumberOfRows(NodeInterface node) {
    int numberOfRows = -1;
    if (node instanceof InternalNodeNoErrors) {
      numberOfRows = ( (InternalNodeNoErrors) node).getNumberOfLeaves();
    }
    else {
      if (node instanceof LeafNodeNoErrors) {
        numberOfRows = 1;
      }
    }
    return (numberOfRows);
  }

  // COMPUTE THE NUMBER OF COLUMNS (CONDITIONS) IN THE GENE EXPRESSION MATRIX USED TO CONSTRUCT THE SUFFIX TREE
  /**
   * Returns the number of conditions in the expression matrix used to construct this suffix tree.
   * @return int
   */
  public int getNumberOfConditions() {
    return ( ( (ListOfTokensOfSameSize)this.tokens).getTokenSize() - 1);
  }

  // COMPUTE THE NUMBER OF ROWS (GENES) IN THE GENE EXPRESSION MATRIX USED TO CONSTRUCT THE SUFFIX TREE
  /**
   * Returns the number of genes in the expression matrix used to construct this suffix tree.
   * @return int
   */
  public int getNumberOfGenes() {
    return (this.tokens.getTokenOfArrays().size());
  }

} // END CLASS
