package smadeira.suffixtrees;

import java.util.*;

/**
 * <p>Title: Internal Node With Errors Right Maximal</p>
 *
 * <p>Description: Internal node of a suffix tree suited for identifying
 *                 biclusters with approximate expression patterns. It
 *                 identifies a right maximal model.</p>
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
public class InternalNodeWithErrorsRightMaximal
    extends InternalNode {

  /**
   * Bit vector used to store the leaves  of each node.
   * colors[i]= 1(true), if the node has a leaf corresponding to gene i
   * colors[i]= 0(false), otherwise
   */
  BitSet colors;

  /**
   * @stereotype constructor
   */
  public InternalNodeWithErrorsRightMaximal() {
    super();
    this.colors = new BitSet();
  }

  /**
   * @stereotype constructor
   *
   * @param leftIndex int
   * @param length int
   * @param rightSybling Object
   * @param firstChild Object
   */
  public InternalNodeWithErrorsRightMaximal(int leftIndex, int length,
                                            NodeInterface rightSybling,
                                            NodeInterface firstChild) {
    super(leftIndex, length, rightSybling, firstChild);
    this.colors = new BitSet();
  }

  /**
   *
   * @param colors BitSet
   */
  public void setColors(BitSet colors) {
    this.colors = colors;
  }

  /**
   *
   * @return BitSet
   */
  public BitSet getColors() {
    return (this.colors);
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
    s = s + "LEFT INDEX = " + this.leftIndex + "\n";
    s = s + "BRANCH LENGTH = " + this.length + "\n";
    s = s + "PATH LENGTH = " + this.pathLength + "\n";
    s = s + "#LEAVES = " + this.numberOfLeaves + "\n";
    s = s + "COLORS = " + this.colors.toString() + "\n";
    return (s);
  }

  /**
   *
   * @param tree SuffixTree
   * @return String
   */
  public String toString(SuffixTreeWithErrorsRightMaximal tree) {
    String s = new String("INTERNAL NODE: \n");
    s = s + "LEFT INDEX = " + this.leftIndex + "\n";
    s = s + "BRANCH LENGTH = " + this.length + "\n";
    s = s + "PATH LENGTH = " + this.pathLength + "\n";
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
    if (this.colors != null) {
      s = s + "COLORS = " + colors.toString();
    }
    return (s);
  }

}
