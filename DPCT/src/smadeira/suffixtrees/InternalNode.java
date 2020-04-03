package smadeira.suffixtrees;

/**
 * <p>Title: Internal Node of a Suffix Tree</p>
 *
 * <p>Description: Internal node of a suffix tree.</p>
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
public class InternalNode
    implements NodeInterface {

  /**
   * The starting index of the branch that leads to this node.
   * Note that the index is calculated relatively to the first char of
   * the first string inserted in the tree.
   */
  protected int leftIndex;

  /**
   * The length of the branch leading to this node.
   */
  protected int length;

  /**
   * Link to the right sybling of this node (if there is one).
   */
  protected NodeInterface rightSybling;

  /**
   * Link to the first child of this node.  There are always at
   * least two children to this node.  The access to the rest of the
   * children is done following the right sybling(s) of the first
   * child.
   */
  protected NodeInterface firstChild;

  /**
   * An internal node always has a sufix link.
   */
  protected NodeInterface suffixLink;

  /**
   * Number of leaves of the node (always equal to 1 for leaf nodes).
   */
  protected int numberOfLeaves;

  /**
   * Length of the path label from the root to the node.
   */
  protected int pathLength;

  /**
   * @stereotype constructor
   */
  public InternalNode() {
    this.leftIndex = 0;
    this.length = 0;
    this.rightSybling = null;
    this.firstChild = null;
    this.suffixLink = null;
    this.numberOfLeaves = 0;
    this.pathLength = 0;
  }

  /**
   * @stereotype constructor
   *
   * @param leftIndex int
   * @param length int
   * @param rightSybling Object
   * @param firstChild Object
   */
  public InternalNode(int leftIndex, int length, NodeInterface rightSybling,
                      NodeInterface firstChild) {
    this.leftIndex = leftIndex;
    this.length = length;
    this.rightSybling = rightSybling;
    this.firstChild = firstChild;
    this.suffixLink = null;
    this.numberOfLeaves = 0;
    this.pathLength = 0;
  }

  /**
   *
   * @return Object
   */
  public NodeInterface getFirstChild() {
    return this.firstChild;
  }

  /**
   *
   * @param firstChild Object
   */
  public void setFirstChild(NodeInterface firstChild) {
    this.firstChild = firstChild;
  }

  /**
   *
   * @return Object
   */
  public NodeInterface getSuffixLink() {
    return this.suffixLink;
  }

  /**
   *
   * @param suffixLink Object
   */
  public void setSuffixLink(NodeInterface suffixLink) {
    this.suffixLink = suffixLink;
  }

  /**
   *
   * @return int
   */
  public final int getLeftIndex() {
    return this.leftIndex;
  }

  /**
   *
   * @param leftIndex int
   */
  public void setLeftIndex(int leftIndex) {
    this.leftIndex = leftIndex;
  }

  /**
   *
   * @return int
   */
  public final int getLength() {
    return this.length;
  }

  /**
   *
   * @param length int
   */
  public void setLength(int length) {
    this.length = length;
  }

  /**
   *
   * @return Object
   */
  public final NodeInterface getRightSybling() {
    return this.rightSybling;
  }

  /**
   *
   * @param rightSybling Object
   */
  public void setRightSybling(NodeInterface rightSybling) {
    this.rightSybling = rightSybling;
  }

  /**
   *
   * @param leaves int
   */
  public void setNumberOfLeaves(int leaves) {
    this.numberOfLeaves = leaves;
  }

  /**
   *
   * @return leaves int
   */
  public final int getNumberOfLeaves() {
    return (this.numberOfLeaves);
  }

  /**
   *
   * @return pathLength int
   */
  public final int getPathLength() {
    return (this.pathLength);
  }

  /**
   *
   * @param pathLength int
   */
  public void setPathLength(int pathLength) {
    this.pathLength = pathLength;
  }

  /**
   * Prints the information stored in the node (only the one added to it after the tree construction).
   * @return String
   */
  public String printInfo() {
    return ("numberOfLeaves = " + this.numberOfLeaves + " " + "pathLength = " +
            this.pathLength);
  }

  /**
   *
   * @return String
   */
  public String toString() {
    String s = new String("INTERNAL NODE: \n");
    s = s + "LEFT INDEX = " + leftIndex + "\n";
    s = s + "BRANCH LENGTH = " + length + "\n";
    s = s + "PATH LENGTH = " + pathLength + "\n";
    s = s + "#LEAVES = " + this.numberOfLeaves + "\n";
    return (s);
  }

  /**
   *
   * @param tree SuffixTreeNoErrors
   * @return String
   */
  public String toString(SuffixTreeWithErrorsLeftMaximal tree) {
    String s = new String("INTERNAL NODE: \n");
    s = s + "LEFT INDEX = " + leftIndex + "\n";
    s = s + "BRANCH LENGTH = " + length + "\n";
    s = s + "PATH LENGTH = " + pathLength + "\n";
    String path = new String("");
    if (this.pathLength != 0) {
      int[] pathFromRoot = tree.getSubArrayInt(this.leftIndex -
                                               (this.pathLength - this.length),
                                               this.pathLength);
      for (int i = 0; i < pathFromRoot.length; i++) {
        path = path + pathFromRoot[i] + "\t";
      }
    }
    s = s + "PATH = " + path + "\n";
    s = s + "#LEAVES = " + this.numberOfLeaves + "\n";
    return (s);
  }

}
