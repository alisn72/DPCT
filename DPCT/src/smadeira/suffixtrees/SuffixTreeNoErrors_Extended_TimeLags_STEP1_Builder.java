package smadeira.suffixtrees;

import java.util.*;

/**
 * <p>Title: Suffix Tree No Errors Time Lags Builder - STEP 1</p>
 *
 * <p>Description: Builder of a Suffix Tree extended for identifying
 *                 biclusters with exact patterns (CCC Biclusters)
 *                 and time lags (time lagged patterns) - STEP 1.</p>
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
public class SuffixTreeNoErrors_Extended_TimeLags_STEP1_Builder
    extends SuffixTreeNoErrorsBuilder {

  /**
   *
   * @param tree SuffixTreeNoErrors_Extended
   */
  public SuffixTreeNoErrors_Extended_TimeLags_STEP1_Builder(
      SuffixTreeNoErrors_Extended_TimeLags tree) {
    super(tree);
  }

  /**
   * @postconditions returns true if the extension was explicit
   *
   * @param phase int
   * @param extension int
   * @return boolean
   */
  protected boolean extend(int phase, int extension) {
    boolean isExplicit = false;
    int phaseChar = this.token[phase - this.startPos];
    /**
     * NOTE: string gamma is S[j..i], where i is the index of the previous phase
     * since index i is not the last character in the current string, it cannot be
     * the string terminator, therefore, the node where it ends cannot be a LeafNodeNoErrors
     * of one of the previously inserted strings
     * moreover, each LeafNodeNoErrors of the current string has the rightIndex = to i+1, the index
     * of the current phase, which makes it impossible for the string S[j..i]
     * to end at it unless i = -1 ! meaning that I am in phase zero of the first string
     */
    if (gammaLen == 0) {
      /**
       * find the start char of the branch to first child, if there is one
       */
      NodeInterface temp = ( (InternalNodeNoErrors_Extended_TimeLags)this.
                            current.getLast()).getFirstChild();
      if (temp == null) {
        LeafNodeNoErrors t = new LeafNodeNoErrors(this.startPos, this.tokenLen, null);
        ( (InternalNodeNoErrors_Extended_TimeLags)this.current.getLast()).
            setFirstChild(t);
        isExplicit = true;
      }
      else {
        int[] branch = this.myTree.getSubArrayInt(temp.getLeftIndex(),
                                                  temp.getLength());
        int start = branch[0];
        NodeInterface tempP = temp;
        int startP = start;
        boolean goRight = (phaseChar <= start) && (temp.getRightSybling() != null);
        while (goRight) {
          tempP = temp;
          startP = start;
          temp = temp.getRightSybling();
          if (temp == null) {
            goRight = false;
          }
          else {
            /**
             * check if temp is a LeafNodeNoErrors of the current string
             */
            int branchStart = temp.getLeftIndex();
            int branchLen = 0;
            if (branchStart < this.startPos) {
              branchLen = temp.getLength();
            }
            else {
              branchLen = phase - branchStart + 1;
            }
            branch = this.myTree.getSubArrayInt(branchStart, branchLen);
            start = branch[0];
            goRight = (phaseChar <= start);
          }
        }
        if (startP == phaseChar) { // REGRA 3
          /**
           * rule 3: do nothing
           * actually, this is where we have to check if the currently
           * inserted string ends at this leaf node;
           * if it does, than we have to insert a new set of coordinates,
           * without creating a new leaf node
           */
          Token t = new Token();
          t.setToken(this.token);
          int[] str1 = t.getSubArrayInt(phase - this.startPos);
          int[] str2 = this.myTree.getSubArrayInt(tempP.getLeftIndex(),
                                                  tempP.getLength());
          if (Arrays.equals(str1, str2)) {
            ( (LeafNodeNoErrors) tempP).addCoordinates(extension);
            isExplicit = true;
          }
          else {
            isExplicit = false;
          }
        }
        else { // REGRA 2
          LeafNodeNoErrors t = new LeafNodeNoErrors(phase,
              this.tokenLen - (phase - this.startPos), null);
          t.addCoordinates(extension);
          if (phaseChar < startP) {
            /**
             * tempP holds the node after which I have to insert the new LeafNodeNoErrors
             * rule 2, without creating a new internal node
             */
            t.setRightSybling(tempP.getRightSybling());
            if (tempP instanceof LeafNodeNoErrors) {
              ( (LeafNodeNoErrors) tempP).setRightSybling(t);
            }
            else {
              ( (InternalNodeNoErrors_Extended_TimeLags) tempP).setRightSybling(
                  t);
            }
          }
          else {
            /**
             * tempP is the first child of this.current.getLast()
             * and t must be inserted before it
             */
            t.setRightSybling(tempP);
            ( (InternalNodeNoErrors_Extended_TimeLags)this.current.getLast()).
                setFirstChild(t);
          }
          isExplicit = true;
        }
      }
    }
    else {
      /**
       * string gamma ends somewhere on the branch between the first
       * and the last node of the current list
       * it does not matter if the last element is a leaf node
       * in case it is, and in case it is a leaf node of the current string
       * all I need is the next character after the end of the gamma string
       * I am not interested in any other characters, so the fact that my
       * branch is not yet completed, since I didn't get
       * to the last phase, doesn't affect the algorithm
       */
      NodeInterface down = this.current.getLast();
      int[] branch = this.myTree.getSubArrayInt(down.getLeftIndex(),
                                                down.getLength());
      int nextChar = branch[gammaLen];
      if (nextChar == phaseChar) { //REGRA 3
        /**
         * do nothing; extension rule 3
         * remove the left sybling in case there is one
         */
        if (this.current.size() > 2) {
          this.current.remove(1);
        }
        Token t = new Token();
        t.setToken(this.token);
        int[] str1 = t.getSubArrayInt(phase - this.startPos);
        int[] str2 = this.myTree.getSubArrayInt(down.getLeftIndex() + gammaLen,
                                                down.getLength() - gammaLen);
        if (Arrays.equals(str1, str2)) {
          ( (LeafNodeNoErrors) down).addCoordinates(extension);
          isExplicit = true;
        }
        else {
          isExplicit = false;
        }
      }
      else { // REGRA 2 - INSERCAO DE UM NO INTERNO A MEIO DE UM RAMO + FOLHA
        /**
         * insert a new internal node on the branch, and add a new leaf too
         * split the branch;
         * the down's node rightSybling always becomes the rightSybling
         * of the new internal node
         */

        InternalNodeNoErrors_Extended_TimeLags tempI = new
            InternalNodeNoErrors_Extended_TimeLags(down.getLeftIndex(),
            gammaLen, down.getRightSybling(), null);

        if (down instanceof InternalNodeNoErrors_Extended_TimeLags) {
          ( (InternalNodeNoErrors_Extended_TimeLags) down).setLeftIndex(down.
              getLeftIndex() + gammaLen);
          ( (InternalNodeNoErrors_Extended_TimeLags) down).setLength(down.
              getLength() - gammaLen);
          ( (InternalNodeNoErrors_Extended_TimeLags) down).setRightSybling(null);
        }
        else {
          ( (LeafNodeNoErrors) down).setLeftIndex(down.getLeftIndex() +
                                                  gammaLen);
          ( (LeafNodeNoErrors) down).setLength(down.getLength() - gammaLen);
          ( (LeafNodeNoErrors) down).setRightSybling(null);
        }
        /**
         * create new leaf node, but do not setup the links yet
         */
        LeafNodeNoErrors tempL = new LeafNodeNoErrors(phase,
            this.tokenLen - (phase - this.startPos), null);
        tempL.addCoordinates(extension);
        /**
         * if the branch on which we perform insertion is not the first
         * branch, the middle element of this.current will have tempI
         * as new rightSybling
         * otherwise, the first element of this.current will have tempI
         * as new first child
         */
        if (this.current.size() > 2) {
          Object removed = this.current.remove(1);
          if (removed instanceof InternalNodeNoErrors_Extended_TimeLags) {
            ( (InternalNodeNoErrors_Extended_TimeLags) (removed)).
                setRightSybling(tempI);
          }
          else {
            ( (LeafNodeNoErrors) (removed)).setRightSybling(tempI);
          }
        }
        else {
          ( (InternalNodeNoErrors_Extended_TimeLags)this.current.getFirst()).
              setFirstChild(tempI);
        }
        /**
         * now setup the children of new internal node tempI
         */
        if (nextChar > phaseChar) {
          tempI.setFirstChild( (NodeInterface)this.current.getLast());
          Object last = this.current.getLast();
          if (last instanceof LeafNodeNoErrors) {
            ( (LeafNodeNoErrors)this.current.getLast()).setRightSybling(tempL);
          }
          else {
            ( (InternalNodeNoErrors_Extended_TimeLags)this.current.getLast()).
                setRightSybling(tempL);
          }
        }
        else {
          tempI.setFirstChild(tempL);
          tempL.setRightSybling( (NodeInterface)this.current.getLast());
        }
        /**
         * replace this.current last element with  newly created internal
         * node, since it represents the end of substring S[j..i]
         * needed for the next extension performed, and for
         * suffix link setup
         */
        this.current.removeLast();
        this.current.addLast(tempI);
        /**
         * sice I am creating a new internal node at the end of gamma
         * set gamma to empty string
         */
        this.gamma = new int[0];
        this.gammaLen = 0;
        /**
         * extension rule 2 with creation of a new internal node
         */
        isExplicit = true;
      }
    }
    /**
     * setup suffix link if it is necessary; note that there is no
     * suffix link to add in the case previous does not contain any element
     * which happens when we are inserting a string into an already grown tree
     */
    if ( (this.previous.size() != 0) &&
        (previous.getLast() instanceof InternalNodeNoErrors_Extended_TimeLags)) {
      if ( ( ( (InternalNodeNoErrors_Extended_TimeLags) previous.getLast()).
            getSuffixLink() == null) &&
          ( (InternalNodeNoErrors_Extended_TimeLags)this.previous.getLast() !=
           this.myTree.getRoot())) {
        ( (InternalNodeNoErrors_Extended_TimeLags)this.previous.getLast()).
            setSuffixLink( (
                InternalNodeNoErrors_Extended_TimeLags)this.current.getLast());
      }
    }

    if (isExplicit) {
      this.lastExtPhase = phase;
    }
    return isExplicit;
  }

}
