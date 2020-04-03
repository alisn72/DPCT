package smadeira.suffixtrees;

/**
 * <p>Title: Leaf Node</p>
 *
 * <p>Description: Leaf node of a suffix tree.</p>
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
public abstract class LeafNode
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
   * The coordinates of one of the suffixes ending at this leaf node.
   */
  protected SuffixCoordinates coordinates;

  /**
   * Number of leaves of the node (always equal to 1 for leaf nodes).
   */
  int numberOfLeaves;

  /**
   * Length of the path label from the root to the node.
   */
  int pathLength;

  /**
   * @stereotype constructor
   */
  public LeafNode() {
    this.leftIndex = 0;
    this.length = 0;
    this.rightSybling = null;
    this.coordinates = null;
    this.numberOfLeaves = 1;
    this.pathLength = 0;
  }

  /**
   * @stereotype constructor
   *
   * @param leftIndex int
   * @param length int
   * @param rightSybling Object
   */
  public LeafNode(int leftIndex, int length, NodeInterface rightSybling) {
    this.leftIndex = leftIndex;
    this.length = length;
    this.rightSybling = rightSybling;
    this.coordinates = null;
    this.numberOfLeaves = 1;
    this.pathLength = 0;
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
   * Returns the first coordinate object.
   *
   * @return SuffixCoordenates
   */
  public SuffixCoordinates getCoordinates() {
    return coordinates;
  }

  /**
   * Prepends the current coordinates (an object that contains the starting
   * index of the current added suffix) to the list of coordinates -
   * that in case two strings have the same suffix
   *
   * @param position int
   */
  public void addCoordinates(int position) {
    SuffixCoordinates newCoord;
    if (this.coordinates != null) {
      newCoord = new SuffixCoordinates(position, this.coordinates);
    }
    else {
      newCoord = new SuffixCoordinates(position, null);
    }
    this.coordinates = newCoord;
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
   * @param pathLength int
   */
  public void setPathLength(int pathLength) {
    this.pathLength = pathLength;
  }

  /**
   *
   * @return int
   */
  public final int getPathLength() {
    return (this.pathLength);
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
    String s = new String("LEAF: \n");
    s = s + "LEFT INDEX = " + leftIndex + "\n";
    s = s + "BRANCH LENGTH = " + length + "\n";
    s = s + "PATH LENGTH = " + pathLength + "\n";
    return (s);
  }

}
