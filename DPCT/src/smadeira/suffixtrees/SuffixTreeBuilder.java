package smadeira.suffixtrees;

import java.util.*;

/**
 * <p>Title: Suffix Tree Builder</p>
 *
 * <p>Description: A suffix tree builder.</p>
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
public abstract class SuffixTreeBuilder {

  /**
   * The current node: it is always the node where the subArrayInt S[j..i] ends,
   * where j is the index of the previous phase).
   */
  protected LinkedList<NodeInterface> current;

  /**
   * The tree in which the array of ints (row of the symbolic expression matrix) is added.
   */
  protected SuffixTree myTree;

  /**
   * If the current internal node was just inserted, it does not have
   * a suffix link attached to it.
   * In this case, we have to remember the array of ints that labels the branch leading to this node
   * (gamma will be followed down from the node at the end of the suffix link
   * starting form the parent of this node).
   */
  protected int[] gamma;

  /**
   * The index of the current token:
   * it is assigned in the order of insertion of the array of ints (row of the symbolic expression matrix) in the tree.
   */
  protected int tokenIndex;

  /**
   * Preserves the internal node that was previously added to the tree, as well as its parent.
   * This is necessary when the chain of explicit extensions ends in the current phase,
   * so that we can restore the starting point for the next phase.
   */
  protected LinkedList previous;

  /**
   * Records the last extension phase, so that we know where to start from
   * when inserting a new suffix in the tree.
   */
  protected int lastExtPhase;

  /**
   * The array of ints (row of the symbolic expression matrix) that is currently inserted.
   */
  protected int[] token;

  /**
   * The index of the first int of this array of ints (row of the symbolic expression matrix), relatively to the first
   * int of the first array of ints (row of the symbolic expression matrix) inserted in the tree.
   */
  protected int startPos;

  /**
   * Stores the token length, so that we don't have to call token.length() each time (expensive).
   */
  protected int tokenLen;

  /**
   * Stores the gamma length, so that we don't have to call gamma.length() each time (expensive).
   */
  protected int gammaLen;

  /**
   * Initialized always at the root of the tree, each time a new array of ints (row of the symbolic expression matrix)
   * is added to the tree.
   * @stereotype constructor
   * @input one array of ints (row of the symbolic expression matrix) that is to be added to the given tree.
   * @output no output; constructs the tree, or adds to its structure.
   * @preconditions a tree has to be constructed first.
   * @postconditions the tree has all the suffixes of the given array of ints (row of the symbolic expression matrix).
   *
   * @param tree SuffixTreeNoErrors
   */
  public SuffixTreeBuilder(SuffixTree tree) {
    this.current = new LinkedList<NodeInterface> ();
    this.previous = new LinkedList<NodeInterface> ();
    /**
     * initialize the previous and current list of nodes
     */
    this.current.addFirst(tree.getRoot());
    this.previous.addFirst(tree.getRoot());
    this.myTree = tree;
    this.token = null;
    this.tokenLen = 0;
    /**
     * always initialize the last extension phase to -1, since no phase was
     * done
     */
    this.lastExtPhase = -1;
    this.gamma = new int[0];
    this.gammaLen = 0;
  }

  /**
   * currentSuffix is the j index of the current phase, and the last explicit extension index + 1.
   * @preconditions the previous extension was explicit
   *
   * @param currentSuffix int
   * @param currentRightEnd int
   */
  protected void goUp(int currentSuffix, int currentRightEnd) {
    /**
     * first save current position in case next extension will be done
     * by the rule 3, and to be able to add the sufix link
     */
    this.previous = (LinkedList<NodeInterface>)this.current.clone();
    if (this.current.getLast() instanceof InternalNode) {
      /**
       * the current node does not have a suffix link attached to it
       */
      if ( ( (InternalNode)this.current.getLast()).getSuffixLink() == null) {
        /**
         * this happens only after at least one explicit extension
         * with InternalNode creation was made in this phase,
         * otherwise I would have had a suffix link;
         * note that if the first node on the current list is the root,
         * we cannot follow any suffix link
         */
        if ( (InternalNode)this.current.getFirst() !=
            this.myTree.getRoot()) {
          int leftIndex = ( (InternalNode)this.current.getLast()).
              getLeftIndex();
          int length = ( (InternalNode)this.current.getLast()).
              getLength();
          this.gamma = this.myTree.getSubArrayInt(leftIndex, length);
          this.gammaLen = length;
          this.current.removeLast();
          this.current.addLast( ( (InternalNode)this.current.getFirst()).
                               getSuffixLink());
          this.current.removeFirst();
        }
        else {
          int leftIndex = currentSuffix;
          int length = currentRightEnd - currentSuffix;
          this.gamma = this.myTree.getSubArrayInt(leftIndex, length);
          this.gammaLen = length;
          if (this.current.size() > 1) {
            this.current.removeLast();
          }
        }
      }
      /**
       * the current node has a suffix link attached to it;
       * note that this node has a path S[last-explicit-extension-index...phase-of-last-explicit-1]
       * gamma in this case is S[phase-of-last-explicit...current-phase-1]
       */
      else {
        this.gamma = this.myTree.getSubArrayInt(this.lastExtPhase,
                                                currentRightEnd -
                                                this.lastExtPhase);
        this.gammaLen = currentRightEnd - this.lastExtPhase;
        if (this.current.size() > 1) {
          this.current.removeFirst();
        }
        this.current.addLast( ( (InternalNode)this.current.getFirst()).
                             getSuffixLink());
        this.current.removeFirst();
      }
    }
    else {
      /**
       * the last node of the current list is a LeafNode; this happens
       * when, in the previous extension I went down, the current gamma
       * ended on the branch to a leaf node, and I did not perform any
       * explicit extension
       */
      int leftIndex = currentSuffix;
      int length = currentRightEnd - currentSuffix;
      this.gamma = this.myTree.getSubArrayInt(leftIndex, length);
      this.gammaLen = length;
      this.current.removeLast();
      InternalNode next = (InternalNode) ( (
          InternalNode)this.current.getFirst()).getSuffixLink();
      /**
       * in case the parent of this leaf is not the root, follow the
       * suffix link
       */
      if (next != null) {
        this.current.addLast(next);
        this.current.removeLast();
      }
    }
  }

  /**
   * walks down from node s(v) along the path gamma, using skip/count trick
   */
  protected void goDown() {
    boolean where = true;
    while ( (where) && (gammaLen > 0)) {
      int branch = this.gamma[0];
      where = this.findChild(branch);
    }
  }

  /**
   * Finds the child of the current node that has branchStart as its first character;
   * this function will succeed in finding the child since the string S[j-1..i]
   * is already in the tree returns true if there are more branches to be followed.
   *
   * @param branchStart int
   * @return boolean
   */
  protected boolean findChild(int branchStart) {
    /**
     * NOTE: findChild is called only when gamma is not empty; this means
     * that the curent node is an InternalNode
     * also, since the substring S[j..i] is already in the tree,
     * we are bounded to find a child that starts with the first char of gamma.
     */
    NodeInterface temp = ( (InternalNode)this.current.
                          getLast()).getFirstChild();
    /**
     * I need to preserve the previous node in my search, in case my gamma ends
     * on a branch, and that branch is not to the first child
     */
    NodeInterface tempP = temp;
    /**
     * here the fact that a child node can be a leaf does not influence the result,
     * since if gamma's first symbol is found on a branch of a LeafNode
     * of the current string, gamma's length will be less than
     * the real current rightEnd of that branch
     */
    int[] branch = this.myTree.getSubArrayInt(temp.getLeftIndex(),
                                              temp.getLength());
    int start = branch[0];
    /**
     * find the child whos branch starts with the first char of gamma
     */
    while (start > branchStart) {
      tempP = temp;
      temp = temp.getRightSybling();
      branch = this.myTree.getSubArrayInt(temp.getLeftIndex(), temp.getLength());
      start = branch[0];
    }
    /**
     * the this.current list has only one element when the last explicit
     * extension was made from root
     */
    if (this.current.size() > 1) {
      this.current.removeFirst();
    }
    this.current.addLast(temp);
    /**
     * if gamma's length is greater than the length of the branch, return true
     * and set gamma to the remaining string;
     * if gamma's length is equal to the branch length, return false, and set gamma to
     * empty string; string S[j..i] finishes at current node
     * if gamma's length is less than the branch legth, string S[j..i] ends
     * somewhere inside this branch, leave gamma as it is, and return false
     */
    if (gammaLen > (temp.getLength())) {
      Token t = new Token();
      t.setToken(this.gamma);
      this.gamma = t.getSubArrayInt(temp.getLength());
      this.gammaLen = this.gamma.length;
      return true;
    }
    else {
      if (gammaLen == (temp.getLength())) {
        this.gamma = new int[0];
        this.gammaLen = 0;
        return false;
      }
      else {
        /**
         * add both the node whos branch starts with first char of gammma
         * and its left sybling, because we need it to update its right sybling
         * link
         * if the node is the first child, it has no left sybling,
         * so we leave only two nodes in the current list
         */
        if (tempP != temp) {
          this.current.add(1, tempP);
        }
        return false;
      }
    }
  }

  /**
   * just before ending the current phase, after an implicit extension
   * was made
   */
  protected void goToLastExplicit() {
    this.current = (LinkedList)this.previous.clone();
  }
}
