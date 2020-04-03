package smadeira.suffixtrees;

import java.io.*;

/**
 * <p>Title: Internal Node No Errors</p>
 *
 * <p>Description: Internal node of a suffix tree suited for identifying
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
public class InternalNodeNoErrors
    extends InternalNode implements Serializable {

  /**
   * Stores true if the node corresponds to a maximal ccc-bicluster (without errors) and false otherwise
   * (always true in case of leaf nodes).
   */
  protected boolean isMaximal;

  /**
   * @stereotype constructor
   */
  public InternalNodeNoErrors() {
    super();
    this.isMaximal = true;
  }

  /**
   * @stereotype constructor
   *
   * @param leftIndex int
   * @param length int
   * @param rightSybling Object
   * @param firstChild Object
   */
  public InternalNodeNoErrors(int leftIndex, int length,
                              NodeInterface rightSybling,
                              NodeInterface firstChild) {
    super(leftIndex, length, rightSybling, firstChild);
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
    String s = new String("INTERNAL NODE: \n");
    s = s + "LEFT INDEX = " + this.leftIndex + "\n";
    s = s + "BRANCH LENGTH = " + this.length + "\n";
    s = s + "PATH LENGTH = " + this.pathLength + "\n";
    s = s + "#LEAVES = " + this.numberOfLeaves + "\n";
    s = s + "IS MAXIMAL = " + this.isMaximal + "\n";
    return (s);
  }

  /**
   *
   * @param tree SuffixTree
   * @return String
   */
  public String toString(SuffixTreeNoErrors tree) {
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
    s = s + "IS MAXIMAL = " + isMaximal + "\n";
    s = s + "#LEAVES = " + this.numberOfLeaves + "\n";
    return (s);
  }

}
