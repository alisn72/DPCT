package smadeira.tries;

import java.util.ArrayList;
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
public class TrieDifferentCombinations {
  /**
   * Root.
   */
  protected TrieDifferentCombinationsNode root;

  /**
   * Creates a new empty trie node.
   */
  public TrieDifferentCombinations() {
    this.root = new TrieDifferentCombinationsNode();
  }

  /**
   * Returns the root of this Trie.
   *
   * @return TrieWithErrorsLeftMaximalNode Root of this Trie.
   */
  public TrieDifferentCombinationsNode getRoot() {
    return this.root;
  }

//###########################################################################################################################
//###########################################################################################################################

  /**
   *
   * @param occurrences ArrayList
   */
  public void addOccurrences (ArrayList<Integer> occurrences){
    if(occurrences == null || occurrences.size() == 0){
      return;
    }
    this.addOccurrences(this.root, occurrences);
  }

  /**
   *
   * @param currentNode TrieDifferentCombinationsNode
   * @param occurrences ArrayList
   */
  private void addOccurrences (TrieDifferentCombinationsNode currentNode, ArrayList<Integer> occurrences){

    if (currentNode == null){
      return;
    }

    if (currentNode.firstChild != null){ // INTERNAL NODE
      // GO DOWN
      this.addOccurrences(currentNode.firstChild, occurrences);
      // GO RIGHT
      if (currentNode.rightSibling != null){
        this.addOccurrences(currentNode.rightSibling, occurrences);
      }
    }
    else{ // LEAF NODE
      // INSERT FIRST CHILD
      TrieDifferentCombinationsNode firstChild = new TrieDifferentCombinationsNode();
      firstChild.setCombination(occurrences.get(0));
      currentNode.firstChild = firstChild;

      // ADD OCCURRENCES AS RIGHT SYBLINGS
      this.addRightSybling(firstChild, occurrences, 1);

      // GO RIGHT
      if(currentNode.rightSibling != null){
        this.addOccurrences(currentNode.rightSibling, occurrences);
      }
    }
  }

  /**
   *
   * @param currentNode TrieDifferentCombinationsNode
   * @param combinations ArrayList
   * @param index int
   */
  private void addRightSybling (TrieDifferentCombinationsNode currentNode, ArrayList<Integer> combinations, int index){
    if (index >= combinations.size()){
      return;
    }

    TrieDifferentCombinationsNode newRightSybling = new TrieDifferentCombinationsNode();
    newRightSybling.setCombination(combinations.get(index));
    currentNode.rightSibling = newRightSybling;
    this.addRightSybling(currentNode.rightSibling, combinations, index+1);
  }


//###########################################################################################################################
//###########################################################################################################################

  /**
   *
   * @return ArrayList
   */
  public ArrayList<ArrayList<Integer>> computeCombinations(){
    ArrayList<ArrayList<Integer>> combinations = new ArrayList<ArrayList<Integer>>(0);
    this.computeCombinations(this.root.firstChild, new ArrayList<Integer>(0), combinations);
    return combinations;
  }

  /**
   *
   * @param currentNode TrieDifferentCombinationsNode
   * @param parentCombinations BitSet
   * @param combinations ArrayList
   */
  private void computeCombinations (TrieDifferentCombinationsNode currentNode, ArrayList<Integer> parentCombinations, ArrayList<ArrayList<Integer>> combinations){
    if (currentNode == null){
      return;
    }

    if (currentNode.firstChild != null){ // INTERNAL NODE
      // GO DOWN
      ArrayList<Integer> currentCombinations = new ArrayList<Integer>(0);
      currentCombinations.addAll(parentCombinations);
      currentCombinations.add(currentNode.combination);
      this.computeCombinations(currentNode.firstChild, currentCombinations, combinations);
      // GO RIGHT
      if(currentNode.rightSibling != null){
        this.computeCombinations(currentNode.rightSibling, parentCombinations, combinations);
      }
    }
    else{ // LEAF NODE
      ArrayList<Integer> currentCombinations = new ArrayList<Integer>(0);
      currentCombinations.addAll(parentCombinations);
      currentCombinations.add(currentNode.combination);
      // ADD COMBINATIONS TO ARRAYLIST
      combinations.add(currentCombinations);
      // GO RIGHT
      if(currentNode.rightSibling != null){
        this.computeCombinations(currentNode.rightSibling, parentCombinations, combinations);
      }
    }

  }

//###########################################################################################################################
//###########################################################################################################################


  /**
   * Prints the Trie.
   */
  public void printTrieDifferentCombinations() {
    this.printTrieDifferentCombinations(this.root, "");
  }

  /**
   * Used recursivelly to print each node int the Trie.
   *
   * @param currentNode TrieWithErrorsLeftMaximalNode Current node.
   * @param currentNodeDepth String Depth of the current node in the Trie.
   */
  private void printTrieDifferentCombinations(TrieDifferentCombinationsNode currentNode, String currentNodeDepth) {

    String nodeString = new String();
    if (this.root == currentNode) {
      nodeString = nodeString + "_ROOT_";
    }

    if (currentNode.firstChild != null) { // Internal Node
      nodeString = nodeString + " " + currentNode.toString();
      System.out.println(currentNodeDepth + "<INTERNAL_NODE " + nodeString + ">");
      // GO DOWN
      this.printTrieDifferentCombinations(currentNode.firstChild, currentNodeDepth + " ");
      System.out.println(currentNodeDepth + "</INTERNAL_NODE>");
      // GO RIGHT
      if (currentNode.rightSibling != null) {
        this.printTrieDifferentCombinations(currentNode.rightSibling,
                                        currentNodeDepth);
      }
    }
    else { // Leaf Node
      nodeString = nodeString + " " + currentNode.toString();
      System.out.println(currentNodeDepth + "<LEAF_NODE " + nodeString + ">");
      System.out.println(currentNodeDepth + "</LEAF_NODE>");
      // GO RIGHT
      if (currentNode.rightSibling != null) {
        this.printTrieDifferentCombinations(currentNode.rightSibling,
                                        currentNodeDepth);
      }
    }
  }

//###########################################################################################################################
//###########################################################################################################################

  public static void main(String[] args) throws Exception {

    int[] o_1 = {0,5};
    int[] o_2 = {1,6,11};
    int[] o_3 = {2,7};
    int[] o_4 = {3,8};
    int[] o_5 = {4,9};

    ArrayList<Integer> i_1 = new ArrayList<Integer>(2);
    i_1.add(o_1[0]);
    i_1.add(o_1[1]);

    ArrayList<Integer> i_2 = new ArrayList<Integer>(3);
    i_2.add(o_2[0]);
    i_2.add(o_2[1]);
    i_2.add(o_2[2]);

    ArrayList<Integer> i_3 = new ArrayList<Integer>(2);
    i_3.add(o_3[0]);
    i_3.add(o_3[1]);

    ArrayList<Integer> i_4 = new ArrayList<Integer>(2);
    i_4.add(o_4[0]);
    i_4.add(o_4[1]);

    ArrayList<Integer> i_5 = new ArrayList<Integer>(2);
    i_5.add(o_5[0]);
    i_5.add(o_5[1]);

    TrieDifferentCombinations trie = new TrieDifferentCombinations();

    trie.addOccurrences(i_1);
    trie.addOccurrences(i_2);
    trie.addOccurrences(i_3);
    trie.addOccurrences(i_4);
    trie.addOccurrences(i_5);

    trie.printTrieDifferentCombinations();

    ArrayList<ArrayList<Integer>> combinations = trie.computeCombinations();
    for (int i = 0; i < combinations.size(); i++){
      System.out.println(combinations.get(i).toString());
    }



  }
}
