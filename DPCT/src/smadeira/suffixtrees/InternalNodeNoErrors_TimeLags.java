package smadeira.suffixtrees;

import java.io.*;

/**
 * <p>Title: Internal Node No Errors Time Lags</p>
 *
 * <p>Description: Internal node of a suffix tree extended for identifying
 *                 biclusters with time lags (time lagged patterns).</p>
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
public class InternalNodeNoErrors_TimeLags
    extends InternalNodeNoErrors implements Serializable {

  /**
   * Extensions can be:
   * gene-shifts
   * sign-changes
   * time-lags
   * sign-changes + time-lags
   */
  private boolean isMaximal_After_TimeLags;

  /**
   * @stereotype constructor
   */
  public InternalNodeNoErrors_TimeLags() {
    super();
    this.isMaximal_After_TimeLags = true;
  }

  /**
   * @stereotype constructor
   *
   * @param leftIndex int
   * @param length int
   * @param rightSybling Object
   * @param firstChild Object
   */
  public InternalNodeNoErrors_TimeLags(int leftIndex, int length,
                                       NodeInterface rightSybling,
                                       NodeInterface firstChild) {
    super(leftIndex, length, rightSybling, firstChild);
    this.isMaximal_After_TimeLags = true;
  }

  /**
   *
   * @param isMaximal_After_TimeLags boolean
   */
  public void setIsMaximal_After_TimeLags(boolean isMaximal_After_TimeLags) {
    this.isMaximal_After_TimeLags = isMaximal_After_TimeLags;
  }

  /**
   *
   * @return isMaximal boolean
   */
  public boolean getIsMaximal_After_TimeLags() {
    return (this.isMaximal_After_TimeLags);
  }

  /**
   * Prints the information stored in the node (only the one added to it after the tree construction).
   * @return String
   */
  public String printInfo() {
    return ("numberOfLeaves = " + this.numberOfLeaves + " " + "pathLength = " +
            this.pathLength + " " +
            "isMaximal = " + this.isMaximal + " " +
            "isMaximal_After_Time-Lags = " + this.isMaximal_After_TimeLags);
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
    s = s + "IS MAXIMAL After TIME-LAGS= " + this.isMaximal_After_TimeLags +
        "\n";
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
    s = s + "IS MAXIMAL After TIME-LAGS= " + this.isMaximal_After_TimeLags +
        "\n";
    s = s + "#LEAVES = " + this.numberOfLeaves + "\n";
    return (s);
  }

}
