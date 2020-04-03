package smadeira.suffixtrees;

import java.io.*;

/**
 * <p>Title: Internal Node No Errors Extended</p>
 *
 * <p>Description: Internal node of a suffix tree extended for identifying
 *                 biclusters with special characteristics, such as gene
 *                 shifts (scaled patterns), sign changes (anticorrelated
 *                 patterns) or time lags (time lagged patterns).</p>
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
public class InternalNodeNoErrors_Extended
    extends InternalNodeNoErrors implements Serializable {

  /**
   * Extensions can be:
   * gene-shifts
   * sign-changes
   * time-lags
   * sign-changes + time-lags
   */
  protected boolean isMaximal_After_Extensions;

  /**
   * @stereotype constructor
   */
  public InternalNodeNoErrors_Extended() {
    super();
    this.isMaximal_After_Extensions = true;
  }

  /**
   * @stereotype constructor
   *
   * @param leftIndex int
   * @param length int
   * @param rightSybling Object
   * @param firstChild Object
   */
  public InternalNodeNoErrors_Extended(int leftIndex, int length,
                                       NodeInterface rightSybling,
                                       NodeInterface firstChild) {
    super(leftIndex, length, rightSybling, firstChild);
    this.isMaximal_After_Extensions = true;
  }

  /**
   *
   * @param isMaximal_After_Extensions boolean
   */
  public void setIsMaximal_After_Extensions(boolean isMaximal_After_Extensions) {
    this.isMaximal_After_Extensions = isMaximal_After_Extensions;
  }

  /**
   *
   * @return isMaximal boolean
   */
  public boolean getIsMaximal_After_Extensions() {
    return (this.isMaximal_After_Extensions);
  }

  /**
   * Prints the information stored in the node (only the one added to it after the tree construction).
   * @return String
   */
  public String printInfo() {
    return ("numberOfLeaves = " + this.numberOfLeaves + " " + "pathLength = " +
            this.pathLength + " " +
            "isMaximal_After_Extensions = " + this.isMaximal_After_Extensions +
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
    s = s + "IS MAXIMAL After EXTENSIONS= " + this.isMaximal_After_Extensions +
        "\n";
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
    s = s + "IS MAXIMAL After EXTENSIONS= " + this.isMaximal_After_Extensions +
        "\n";
    s = s + "#LEAVES = " + this.numberOfLeaves + "\n";
    return (s);
  }

}
