package smadeira.tries;

import java.util.BitSet;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
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
public class TrieDifferentCombinationsNode {
  /**
   * A pointer to the first of this node's children.
   */
  protected TrieDifferentCombinationsNode firstChild;

  /**
   * A pointer to this node's next sibling.
   */
  protected TrieDifferentCombinationsNode rightSibling;

  /**
   *
   */
  protected Integer combination;

  /**
   *
   */
  public TrieDifferentCombinationsNode() {
    this.firstChild = null;
    this.rightSibling = null;
  }

  /**
   *
   * @param index Integer
   */
  public void setCombination (Integer combination){
    this.combination = combination;
  }

  /**
    * Prints information about this node.
    *
    * @return String
    */
   public String toString() {
     String s = new String();

     if (this.firstChild != null) {
       s = s + "this.firstChild = NOT null" + "\t";
     }
     else {
       s = s + "this.firstChild = " + null +"\t";
     }

     if (this.rightSibling != null) {
       s = s + "this.rightSibling = NOT null" + "\t";
     }
     else {
       s = s + "this.rightSibling = " + null +"\t";
     }

     if (this.combination != null) {
       s = s + "this.combination = " + this.combination.toString() + "\t";
     }
     else {
       s = s + "this.combination = " + null +"\t";
     }
     return s;
  }

}
