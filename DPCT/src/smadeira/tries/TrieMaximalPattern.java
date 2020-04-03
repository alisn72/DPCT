package smadeira.tries;

/**
 * This class implements a Trie used in the second step of the E-CCC-Biclustering algorithm:
 * remove from the stored models those:
 * 1) not representing *RIGHT* maximal e-CCC-Biclusters, in case the patterns are inserted in the trie;
 * 2) not representing *LEFT* maximal e-CCC-Biclusters, in case the *REVERSED* patterns are inserted in the trie;
 *
 * <P>Each node contains data encapsulated in an object of class <CODE>TrieMaximalPatternNodeInfo</CODE> instance.
 * Each edge spells out an integer and each path from the root represents an array of integers (model)
 * described by the integers labelling the traversed edges. Moreover, for each model represented, there is a unique
 * path from the root.</P>
 *
 * <P>The trie of the following example represents the models [681], [851], [781], [681 782], [851 682],
 * [851 782], [851 852], [851 852 783] and [851 852 683].</P>
 *
 * <CODE><BLOCKQUOTE><PRE>
 *            [0]
 *       ------+-----
 *      /      |     \
 *  681/    851|      \781
 *   [1]      [2]     [3]
 *    |      --+--
 *    | 682/ 782| \852
 * 782|   /     |  \
 *   [4] [5]  [6]  [7]
 *                --+--
 *               /     \
 *           783/       \683
 *            [8]       [9]
 * </PRE></BLOCKQUOTE></CODE>
 *
 * <P>It is easy to see that models with common prefixes will branch off from each other
 * at the first distinguishing integer. </P>
 *
 * <P>In this implementation, each node is an is an instance of classe <CODE>TrieMaximalPatternNode</CODE>.</P>
 *
 * <P>There are many ways to implement a trie. To avoid wasting space with
 * multiple pointers at each node, this implementation uses an approach with a linked list
 * of siblings. Each node actually contains a pointer to one of its children and a pointer
 * to one of its siblings only. Together with the pointers, each node also stores the
 * integer that labels the edge to the pointed node.<P>
 *
 * <CODE><BLOCKQUOTE><PRE>
 *            [0]
 *       ------+-----
 *      /      |     \
 *  681/    851|      \781
 *   [1]      [2]     [3]
 *    |      --+--
 *    | 682/ 782| \852
 * 782|   /     |  \
 *   [4] [5]  [6]  [7]
 *                --+--
 *               /     \
 *           783/       \683
 *            [8]       [9]
 * </PRE></BLOCKQUOTE></CODE>

 *
 * <CODE><BLOCKQUOTE><PRE>
 *   [0]
 *    |
 * 681|  851     781
 *   [1]-----[2]-----[3]
 *    |       |
 * 782|    682|  782     852
 *   [4]     [5]-----[6]-----[7]
 *                            |
 *                         783|  683
 *                           [8]-----[9]
 * </PRE></BLOCKQUOTE></CODE>
 *
 * <P>In this way, a trie is similar to a binary tree. Although this implementation is
 * more efficient in terms of space, the search for a label with a given integer leaving
 * a node <CODE>v</CODE> is no more constant but proportional to the number of children of
 * <CODE>v</CODE>. </P>
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
public class TrieMaximalPattern {
  /**
   * Root.
   */
  protected TrieMaximalPatternNode root;

  /**
   * Creates a new empty trie node.
   */
  public TrieMaximalPattern() {
    this.root = new TrieMaximalPatternNode(new TrieMaximalPatternNodeInfo());
  }

  /**
   * Returns the root of this Trie.
   *
   * @return TrieMaximalPatternNode Root of this Trie.
   */
  public TrieMaximalPatternNode getRoot() {
    return this.root;
  }

//###########################################################################################################################
//###########################################################################################################################


  /**
   * USED TO MARK MODELS AS NON-RIGHT MAXIMAL !!
   *
   * @param model int[]
   * @param numberOfGenes int
   * @return TrieMaximalPatternNode
   */
  public TrieMaximalPatternNode addModel(int[] model, int numberOfGenes) {

    if (model.length == 0) {
      return null;
    }

    TrieMaximalPatternNode currentNode = this.root;
    for (int i = 0; i < model.length; i++) {
      currentNode = currentNode.add(null, model[i]);
    }
    currentNode.setInfo(new TrieMaximalPatternNodeInfo(numberOfGenes, true)); // update last node info (end of model)
    return currentNode;
  }

  //###########################################################################################################################


  /**
   * USED TO MARK MODELS AS NON-LEFT MAXIMAL !!
   *
   * @param model int[]
   * @param numberOfGenes int
   * @return TrieMaximalPatternNode
   */
  public TrieMaximalPatternNode addInvertedModel(int[] model,
      int numberOfGenes) {

    if (model.length == 0) {
      return null;
    }

    TrieMaximalPatternNode currentNode = this.root;
    for (int i = model.length - 1; i >= 0; i--) {
      currentNode = currentNode.add(null, model[i]);
    }
    currentNode.setInfo(new TrieMaximalPatternNodeInfo(numberOfGenes, true)); // update last node info (end of model)
    return currentNode;
  }

  //###########################################################################################################################
  //###########################################################################################################################


  /**
   *
   */
  public void markNodesAsNonMaximalPattern() {
    // the root is non left-maximal already
    this.markNodesAsNonMaximalPattern(this.root.firstChild);
  }

  //###########################################################################################################################

  /**
   * Used recurvivelly.
   *
   * @param currentNode TrieMaximalPatternNode
   * @return int
   */
  protected int markNodesAsNonMaximalPattern(TrieMaximalPatternNode currentNode) {
    if (currentNode == null) {
      return 0;
    }
    if (currentNode.firstChild == null) { //leaf node
      currentNode.getInfo().setIsMaximalPattern(true);
      if (currentNode.rightSibling == null) {
        return currentNode.getInfo().getNumberOfGenes();
      }
      else { // go right
        return Math.max(currentNode.getInfo().getNumberOfGenes(),
                        this.markNodesAsNonMaximalPattern(currentNode.rightSibling));
      }
    }
    else { // internal node
      // go down
      int maxNumberOfGenesInSubTree = this.markNodesAsNonMaximalPattern(
          currentNode.firstChild);
      if (currentNode.getInfo() != null) { // node corresponds to an inserted model
        if (maxNumberOfGenesInSubTree == currentNode.getInfo().getNumberOfGenes()) {
          currentNode.getInfo().setIsMaximalPattern(false);
        }
        if (currentNode.rightSibling != null) { // go right
          return Math.max(currentNode.getInfo().getNumberOfGenes(),
                          this.markNodesAsNonMaximalPattern(currentNode.rightSibling));
        }
        else {
          return Math.max(maxNumberOfGenesInSubTree, currentNode.getInfo().getNumberOfGenes());
        }
      }
      else { // node does not correspond to an inserted model
        if (currentNode.rightSibling != null) { // go right
          return this.markNodesAsNonMaximalPattern(currentNode.rightSibling);
        }
        else {
          return maxNumberOfGenesInSubTree;
        }
      }
    }
  }

//###########################################################################################################################
//###########################################################################################################################

  /**
   *
   * @param model int[]
   * @return TrieMaximalPatternNode
   */
  public TrieMaximalPatternNode findModel(int[] model) {
    if (model.length == 0) {
      return null;
    }

    TrieMaximalPatternNode currentNode = this.root;
    int i = 0;
    while (currentNode != null && i < model.length) {
      currentNode = currentNode.spellDown(model[i]);
      i++;
    }
    return currentNode;
  }

//###########################################################################################################################
//###########################################################################################################################


  /**
   * Prints the Trie.
   */
  public void printWithErrorsLeftMaximal() {
    this.printWithErrorsLeftMaximal(this.root, "");
  }

  /**
   * Used recursivelly to print each node int the Trie.
   *
   * @param currentNode TrieMaximalPatternNode Current node.
   * @param currentNodeDepth String Depth of the current node in the Trie.
   */
  protected void printWithErrorsLeftMaximal(TrieMaximalPatternNode
                                          currentNode, String currentNodeDepth) {

    if (currentNode == null) {
      return;
    }

    String nodeString = new String();
    if (this.root == currentNode) {
      nodeString = nodeString + "_ROOT_";
    }

    if (currentNode.firstChild != null) { // Internal Node
      nodeString = nodeString + " " + currentNode.toString();
      System.out.println(currentNodeDepth + "<INTERNAL_NODE " + nodeString +
                         ">");
      // GO DOWN
      this.printWithErrorsLeftMaximal(currentNode.firstChild,
                                      currentNodeDepth + " ");
      System.out.println(currentNodeDepth + "</INTERNAL_NODE>");
      // GO RIGHT
      if (currentNode.rightSibling != null) {
        this.printWithErrorsLeftMaximal(currentNode.rightSibling,
                                        currentNodeDepth);
      }
    }
    else { // Leaf Node
      nodeString = nodeString + " " + currentNode.toString();
      System.out.println(currentNodeDepth + "<LEAF_NODE " + nodeString + ">");
      System.out.println(currentNodeDepth + "</LEAF_NODE>");
      // GO RIGHT
      if (currentNode.rightSibling != null) {
        this.printWithErrorsLeftMaximal(currentNode.rightSibling,
                                        currentNodeDepth);
      }
    }
  }

//###########################################################################################################################
//###########################################################################################################################

  public static void main(String[] args) throws Exception {

    int[] r_model_1 = {
        785, 854, 683, 852, 781};
    int[] r_model_2 = {
        685, 854, 683, 852, 681};
    int[] r_model_3 = {
        785, 854, 783, 782, 781};
    int[] r_model_4 = {
        855, 854, 683, 852, 851};
    int[] r_model_5 = {
        785, 854, 683, 852}; // NON-LEFT-MAXIMAL
    int[] r_model_6 = {
        685, 854, 683}; // NON-LEFT-MAXIMAL
    int[] r_model_7 = {
        785}; // NON-LEFT-MAXIMAL
    int[] r_model_8 = {
        855, 854}; // NON-LEFT-MAXIMAL

    // ADD MODEL

    TrieMaximalPattern trie = new TrieMaximalPattern();
    trie.addModel(r_model_1, 1);
    trie.addModel(r_model_2, 2);
    trie.addModel(r_model_3, 3);
    trie.addModel(r_model_4, 4);
    trie.addModel(r_model_5, 1);
    trie.addModel(r_model_6, 2);
    trie.addModel(r_model_7, 3);
    trie.addModel(r_model_8, 4);
    System.out.println();
    trie.printWithErrorsLeftMaximal();
    TrieMaximalPatternNode node = trie.findModel(r_model_7);
    if (node != null) {
      System.out.println("Found r_model_7");
      System.out.println(node.info.getNumberOfGenes());
    }
    else {
      System.out.println("DIDN'T Found r_model_7");
    }

    // ADD REVERSE MODELS

    int[] model_1 = {
        781, 852, 683, 854, 785};
    int[] model_2 = {
        681, 852, 683, 854, 685};
    int[] model_3 = {
        781, 782, 783, 854, 785};
    int[] model_4 = {
        851, 852, 683, 854, 855};
    int[] model_5 = {
        852, 683, 854, 785};
    int[] model_6 = {
        683, 854, 685};
    int[] model_7 = {
        785};
    int[] model_8 = {
        854, 855};

    TrieMaximalPattern trie2 = new TrieMaximalPattern();
    trie2.addInvertedModel(model_1, 1);
    trie2.addInvertedModel(model_2, 2);
    trie2.addInvertedModel(model_3, 3);
    trie2.addInvertedModel(model_4, 4);
    trie2.addInvertedModel(model_5, 1);
    trie2.addInvertedModel(model_6, 2);
    trie2.addInvertedModel(model_7, 3);
    trie2.addInvertedModel(model_8, 4);
    System.out.println();
    trie2.printWithErrorsLeftMaximal();

    // MARK NODES AS NON-LEFT MAXIMAL
    trie2.markNodesAsNonMaximalPattern();
    trie2.printWithErrorsLeftMaximal();

  }
}
