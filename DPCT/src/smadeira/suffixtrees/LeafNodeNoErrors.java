package smadeira.suffixtrees;

/**
 * <p>Title: Leaf Node No Errors</p>
 *
 * <p>Description: Leaf node of a suffix tree suited for identifying
 *                 biclusters with exact expression patterns.</p>
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
public class LeafNodeNoErrors
    extends LeafNode {

  /**
   * Stores true if the nodes corresponds to a maximal ccc-bicluster (without errors) and false otherwise
   * (always true in case of leaf nodes).
   */
  boolean isMaximal;

  /**
   * @stereotype constructor
   */
  public LeafNodeNoErrors() {
    super();
    this.isMaximal = true;
  }

  /**
   * @stereotype constructor
   *
   * @param leftIndex int
   * @param length int
   * @param rightSybling Object
   */
  public LeafNodeNoErrors(int leftIndex, int length, NodeInterface rightSybling) {
    super(leftIndex, length, rightSybling);
    this.isMaximal = true;
  }

  /**
   *
   * @param isMaximal boolean
   */
  public void setIsMaximal(boolean isMaximal) {
    this.isMaximal = isMaximal;
  }

  /**
   *
   * @return isMaximal boolean
   */
  public boolean getIsMaximal() {
    return (this.isMaximal);
  }

  /**
   * Prints the information stored in the node (only the one added to it after the tree construction).
   * @return String
   */
  public String printInfo() {
    return ("numberOfLeaves = " + this.numberOfLeaves + " " + "pathLength = " +
            this.pathLength + " " +
            "isMaximal = " + this.isMaximal);
  }

  /**
   *
   * @return String
   */
  public String toString() {
    String s = new String("LEAF: \n");
    s = s + "LEFT INDEX = " + this.leftIndex + "\n";
    s = s + "BRANCH LENGTH = " + this.length + "\n";
    s = s + "PATH LENGTH = " + this.pathLength + "\n";
    s = s + "IS MAXIMAL = " + this.isMaximal + "\n";
    return (s);
  }

  /**
   *
   * @param tree SuffixTree
   * @return String
   */
  public String toString(SuffixTreeNoErrors tree) {
    String s = new String("LEAF: \n");
    s = s + "LEFT INDEX = " + this.leftIndex + "\n";
    s = s + "BRANCH LENGTH = " + this.length + "\n";
    s = s + "PATH LENGTH = " + this.pathLength + "\n";
    String path = new String("");
    if (this.pathLength != 0) {
      int[] pathFromRoot = tree.getSubArrayInt(this.leftIndex -
                                               (this.pathLength - this.length),
                                               this.pathLength - 1);
      for (int i = 0; i < pathFromRoot.length; i++) {
        path = path + pathFromRoot[i] + "\t";
      }
    }
    s = s + "PATH = " + path + "\n";
    s = s + "IS MAXIMAL = " + isMaximal + "\n";
    return (s);
  }

}
