package smadeira.suffixtrees;

import java.io.*;

/**
 * <p>Title: Suffix Tree</p>
 *
 * <p>Description: A suffix tree.</p>
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
public abstract class SuffixTree
    implements Serializable {

  /**
   * The root of the tree.
   */
  protected InternalNode root;

  /**
   * The arrays of ints (rows of the symbolic expression matrix) in the tree.
   */
  protected ListOfTokens tokens;

  /**
   * Total number of nodes in the tree.
   */
  protected int numberOfNodes;

  /**
   * Total number of internal nodes in the tree.
   */
  protected int numberOfInternalNodes;

  /**
   * Total number of leaf nodes in the tree.
   */
  protected int numberOfLeafNodes;

  /**
   *
   */
  public SuffixTree() {
    this.root = new InternalNode();
  }

  /**
   * Adds a new row of the symbolic expression matrix (array of ints) to the suffix tree.
   *
   * @param token int[]
   * @return int
   */
  public int addToken(int[] token) {
    return this.tokens.insertToken(token);
  }

  /**
   * Returns the root of the tree.
   *
   * @return InternalNode
   */
  public InternalNode getRoot() {
    return (this.root);
  }

  /**
   * Returns the sub-array of ints starting at leftIndex and having the specified length
   * from the array of ints collection (rows of the symbolic expression matrix already in the tree).
   *
   * @param leftIndex int
   * @param length int
   * @return int[]
   */
  public int[] getSubArrayInt(int leftIndex, int length) {
    return (this.tokens.getSubArrayInt(leftIndex, length));
  }

  /**
   * Returns the suffix starting at leftIndex.
   *
   * @param leftIndex int
   * @return int[]
   */
  public int[] getSubArrayInt(int leftIndex) {
    return (this.tokens.getSubArrayInt(leftIndex));
  }

  /**
   * Returns the starting index of the token at indexToken.
   *
   * @param indexToken int
   * @return int
   */
  public int getStart(int indexToken) {
    return (this.tokens.getStart(indexToken));
  }

  /**
   * Returns the index of the token that contains position.
   *
   * @param position int
   * @return int
   */
  public int getIndex(int position) {
    return (this.tokens.getIndex(position));
  }

  /**
   * Returns the total length of the arrays of ints (rows of the symbolic expresion matrix) in the tree.
   *
   * @return int
   */
  public int getTotalLength() {
    return (this.tokens.getTotalLength());
  }

  /**
   * Returns the total number of nodes in the tree.
   *
   * @return int
   */
  public int getNumberOfNodes() {
    if (this.numberOfNodes == 1) { // fields not yet initialized
      this.computeNumberOfNodes();
    }
    return (this.numberOfNodes);
  }

  /**
   * Returns the total number of internal nodes in the tree.
   *
   * @return int
   */
  public int getNumberOfInternalNodes() {
    if (this.numberOfNodes == 1) { // fields not yet initialized
      this.computeNumberOfNodes();
    }
    return (this.numberOfInternalNodes);
  }

  /**
   * Returns the total number of leaf nodes in the tree.
   *
   * @return int
   */
  public int getNumberOfLeafNodes() {
    if (this.numberOfNodes == 1) { // fields not yet initialized
      this.computeNumberOfNodes();
    }
    return (this.numberOfLeafNodes);
  }

  /**
   * Computes the total number of nodes, internal nodes and leaf nodes of the tree and updates the fields:
   * numberOfNodes, numberOfInternalNodes, numberOfLeafNodes.
   */
  public void computeNumberOfNodes() {
    //initialize counters
    this.numberOfNodes = 1; // root
    this.numberOfInternalNodes = 0;
    this.numberOfLeafNodes = 0;
    this.computeNumberOfNodes( ( (InternalNode)this.root).getFirstChild());
  }

  /**
   * Computes the total number of nodes, internal nodes and leaf nodes of the tree and updates the fields:
   * numberOfNodes, numberOfInternalNodes, numberOfLeafNodes.
   *
   * @param node NodeInterface
   */
  private void computeNumberOfNodes(NodeInterface node) {
    if (node == null) {
      return;
    }
    else {
      this.numberOfNodes++;
      if (node instanceof LeafNode) {
        this.numberOfLeafNodes++;
        // go right
        this.computeNumberOfNodes(node.getRightSybling());
      }
      else {
        if (node instanceof InternalNode) {
          this.numberOfInternalNodes++;
          // go down
          this.computeNumberOfNodes( ( (InternalNode) node).getFirstChild());
          // go down
          this.computeNumberOfNodes(node.getRightSybling());
        }
      }
    }
  }

  /**
   * Computes the total number of nodes, internal nodes and leaf nodes of the tree and updates the fields:
   * numberOfNodes, numberOfInternalNodes, numberOfLeafNodes of the tree passed as parameter.
   *
   * @param tree SuffixTree
   */
  public static void computeNumberOfNodes(SuffixTree tree) {
    //initialize counters
    tree.numberOfNodes = 1;
    tree.numberOfInternalNodes = 0;
    tree.numberOfLeafNodes = 0;
    SuffixTree.computeNumberOfNodes(tree,
                                    ( ( (InternalNode) tree.getRoot()).getFirstChild()));
  }

  /**
   * Computes the total number of nodes, internal nodes and leaf nodes of the tree and updates the fields:
   * numberOfNodes, numberOfInternalNodes, numberOfLeafNodes of the tree passed as parameter.
   *
   * @param tree SuffixTree
   * @param node NodeInterface
   */
  private static void computeNumberOfNodes(SuffixTree tree,
                                           NodeInterface node) {
    if (node == null) {
      return;
    }
    else {
      tree.numberOfNodes++;
      if (node instanceof LeafNode) {
        tree.numberOfLeafNodes++;
        // go right
        SuffixTree.computeNumberOfNodes(tree, node.getRightSybling());
      }
      else {
        if (node instanceof InternalNode) {
          tree.numberOfInternalNodes++;
          // go down
          SuffixTree.computeNumberOfNodes(tree, ( (InternalNode)
                                                 node).getFirstChild());
          // go right
          SuffixTree.computeNumberOfNodes(tree, node.getRightSybling());
        }
      }
    }
  }

  // ###################################################################################################################
  // ############################################# METHODS TO ADD INFO TO THE NODES ####################################
  // ###################################################################################################################

  // ################################## ADD NUMBER OF LEAVES ##########################################################
  /**
   * Method to decorate the tree with the number of leaves of each node.
   * (number of rows in the bicluster that each of them represents).
   */
  public void addNumberOfLeaves() {
    this.addNumberOfLeaves(this.getRoot());
  }

  /**
   * Decorate the tree with the number of leaves of each internal node.
   * (number of rows in the bicluster that each of them represents).
   *
   * Used recursively !!!
   *
   * @param node NodeInterface
   * @return int
   */
  private int addNumberOfLeaves(NodeInterface node) {
    if (node instanceof LeafNode) {
      NodeInterface rightSybling = node.getRightSybling();
      // the number of rows of a bicluster represented by a leaf is one
      ( (LeafNode) node).setNumberOfLeaves(1);
      if (rightSybling != null) {
        return (1 + this.addNumberOfLeaves(rightSybling));
      }
      else {
        return (1);
      }
    }
    else {
      if (node instanceof InternalNode) {
        NodeInterface firstChild = ( (InternalNode) node).getFirstChild();
        NodeInterface rightSybling = node.getRightSybling();
        // go down
        int numberOfLeaves = this.addNumberOfLeaves(firstChild);
        ( (InternalNode) node).setNumberOfLeaves(numberOfLeaves);
        if (rightSybling != null) {
          return (numberOfLeaves + this.addNumberOfLeaves(rightSybling));
        }
        else {
          return (numberOfLeaves);
        }
      }
      else {
        return (0);
      }
    }
  }

  // ###################################### ADD PATHLENGTH ####################################################
  /**
   * Method to decorate a tree with the pathLength information at each node.
   *
   */
  public void addPathLength() {
    InternalNode root = (InternalNode)this.getRoot();
    addPathLength(0, root.getFirstChild());
  }

  /**
   * Method to decorate a tree with the pathLength information at each node.
   *
   * Used recursively !!!
   *
   * @param prefix int
   * @param node NodeInterface
   */
  private void addPathLength(int prefix, NodeInterface node) {
    int pathLength = prefix + node.getLength();
    if (node instanceof InternalNode) {
      //add pathlength
      ( (InternalNode) node).setPathLength(pathLength);
      // go down
      addPathLength(pathLength,
                    ( (InternalNode) node).getFirstChild());
      // go right
      NodeInterface rightSybling = node.getRightSybling();
      if (rightSybling != null) {
        addPathLength(prefix, rightSybling);
      }
    }
    else {
      if (node instanceof LeafNode) {
        //add pathlength
        ( (LeafNode) node).setPathLength(pathLength);
        // go right
        NodeInterface rightSybling = node.getRightSybling();
        if (rightSybling != null) {
          addPathLength(prefix, rightSybling);
        }
      }
    }
  }

  // ###################################################################################################################
  // ############################################# USEFUL METHODS ######################################################
  // ###################################################################################################################

  // ############################ COMPUTE PATHLABEL #######################

  /**
   * Returns the path label of the specified node as string.
   *
   * @param node NodeInterface
   * @return String
   */
  public String computeNodePathLabel(NodeInterface node) {
    String label = new String();
    int[] subArrayInt = this.getSubArrayInt(node.getLeftIndex(), node.getLength());
    int subArrayIntLength = subArrayInt.length;
    for (int i = 0; i < subArrayIntLength; i++) {
      label = label + new Integer(subArrayInt[i]).toString() + " ";
    }
    return (label);
  }

} // END CLASS
