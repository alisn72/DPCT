package smadeira.tries;

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
public class TrieMaximalPatternNode{
  /**
   * A pointer to the first of this node's children.
   */
  protected TrieMaximalPatternNode firstChild;

  /**
   * The character that labels the edge from this node to the child node pointer by
   * <CODE>son</CODE>.
   */
  protected int to_firstChild;

  /**
   * A pointer to this node's next sibling.
   */
  protected TrieMaximalPatternNode rightSibling;

  /**
   * The character that labels the edge from this node to the sibling pointer by
   * <CODE>rightSibling</CODE>.
   */
  protected int to_rightSibling;

  /**
   * This node's stored data.
   */
  protected TrieMaximalPatternNodeInfo info;

  /**
   * Creates a new trie node with the specified data. This constructor is typically used
   * by the client only once to instantiate the root node. After that, all new nodes are
   * implicitly instantiated by the <CODE>add</CODE> method.
   *
   * @param info TrieMaximalPatternNodeInfo The info that will be associated with the new node
   */
  public TrieMaximalPatternNode(TrieMaximalPatternNodeInfo info) {
    this.firstChild = null;
    this.rightSibling = null;
    this.info = info;
  }

  /**
   * Returns the info associated with this node.
   *
   * @return TrieWithErrorsLeftMaximalNodeInfo Info associated with this node
   */
  public TrieMaximalPatternNodeInfo getInfo() {
    return this.info;
  }

  /**
   * Updates the info associated with this node.
   *
   * @param info TrieMaximalPatternNodeInfo Info associated with this node.
   */
  public void setInfo(TrieMaximalPatternNodeInfo info) {
    this.info = info;
  }

  /**
   * Adds a new child to this node. The new node will be implicitly instantiated with
   * the <CODE>info</CODE> argument, and the edge from this node to the new node will be
   * labelled by the character argument. If this node already have an edge labelled with
   * this integer, an exception is raised. Otherwise, the new node created and
   * returned.
   *
   * <P>If this node have no child, a new node is created straight away. Otherwise, the
   * task is assigned to its first child that will add the new node as a sibling.</P>
   *
   * @param info TrieMaximalPatternNodeInfo The info that will be associated with the new node
   * @param token int The integer that will label the edge from this node to the new node
   * @return TrieMaximalPatternNode The added node or the node to whom the <CODE>token</CODE> takes if it already exists.
   */
  public TrieMaximalPatternNode add(TrieMaximalPatternNodeInfo info, int token) {
    if (this.firstChild == null) {
      this.firstChild = new TrieMaximalPatternNode(info);
      this.to_firstChild = token;
//                        System.out.println("this.to_firstChild = " + this.to_firstChild);
      return this.firstChild;
    }
    else {
      if (this.to_firstChild != token) {
        return this.firstChild.addRightSibling(info, token);
      }
      else {
        // duplicate int
//                                System.out.println("ALREADY IN this.to_firstChild = " + this.to_firstChild);
        return this.firstChild;
      }
    }
  }

  /**
   * Adds a sibling to this node. The new node will be implicitly instantiated with
   * the <CODE>info</CODE> argument, and the edge from this node to the new node will be
   * labelled by the character argument. If this node already have a sibling with this
   * character, an exception is raised. Otherwise, the new node is created and returned.
   *
   * <P>If this node have no direct sibling, a new node is created straight away.
   * Otherwise, the task is assigned to its next sibling.</P>
   *
   * @param info TrieWithErrorsLeftMaximalNodeInfo The info that will be associated with the new node
   * @param token int The integer that will label the edge from this node to the new node
   * @return TrieWithErrorsLeftMaximalNode The added node or the node to whom the <CODE>token</CODE> takes if it already existed.
   */
  public TrieMaximalPatternNode addRightSibling(
      TrieMaximalPatternNodeInfo info, int token) {
    if (this.rightSibling == null) {
      this.rightSibling = new TrieMaximalPatternNode(info);
      this.to_rightSibling = token;
//                        System.out.println("this.to_rightSibling = " + this.to_rightSibling);
      return this.rightSibling;
    }
    else {
      if (this.to_rightSibling != token) {
        return this.rightSibling.addRightSibling(info, token);
      }
      else {
        // duplicate int
//                                System.out.println("ALREADY IN this.to_rightSibling = " + this.to_rightSibling);
        return this.rightSibling;
      }
    }
  }

  /**
   * Follows a path from this node to one of its children by spelling the character
   * supplied as an argument. If there is no such a path, <CODE>null</CODE> is returned.
   * Otherwise, the reached child node is returned.
   *
   * <P>If this node's direct child is reached with an edge labelled by the character,
   * it is returned straight away. Otherwise, it is assigned the task of finding another
   * sibling labelled with that character.</P>
   *
   * @param token int The integer that labels the path to be followed to this node's child
   * @return TrieMaximalPatternNode The child node reached by traversing the edge labelled by <CODE>token</CODE>
   */
  public TrieMaximalPatternNode spellDown(int token) {
    if (this.firstChild == null) {
      return null;
    }

    if (this.to_firstChild == token) {
      return this.firstChild;
    }
    else {
      return this.firstChild.spellRight(token);
    }
  }

  /**
   * Follows a path from this node to one of its sibling by spelling the character
   * supplied as an argument. If there is no such a path, <CODE>null</CODE> is returned.
   * Otherwise, the reached sibling node is returned.
   *
   * <P>If this node's direct sibling is reached with an edge labelled by the character,
   * it is returned straight away. Otherwise, it is assigned the task of finding another
   * sibling labelled with that character.</P>
   *
   * @param token int The integer that labels the path to be followed to the sibling
   * @return TrieWithErrorsLeftMaximalNode The right sibling node reached by traversing the edge labelled by <CODE>token</CODE>
   */
  public TrieMaximalPatternNode spellRight(int token) {
    if (this.rightSibling == null) {
      return null;
    }
    if (this.to_rightSibling == token) {
      return this.rightSibling;
    }
    else {
      return this.rightSibling.spellRight(token);
    }
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

    s = s + "this.to_firstChild = " + this.to_firstChild + "\t";

    if (this.rightSibling != null) {
      s = s + "this.rightSibling = NOT null" + "\t";
    }
    else {
      s = s + "this.rightSibling = " + null +"\t";
    }

    s = s + "this.to_rightSibling = " + this.to_rightSibling + "\t";

    if (this.info != null) {
      s = s + "this.info = " + this.info.toString() + "\t";
    }
    else {
      s = s + "this.info = " + null +"\t";
    }
    return s;
  }
}
