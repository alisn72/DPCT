package smadeira.tries;

import java.util.TreeMap;
import smadeira.utils.ModelOccurrences;
import java.util.ArrayList;
import java.util.Iterator;

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
public class TrieMaximalPattern_TimeLags{

  /**
   * Root.
   */
  protected TrieMaximalPattern_TimeLags_Node root;

  /**
   * Creates a new empty trie node.
   */
  public TrieMaximalPattern_TimeLags() {
    this.root = new TrieMaximalPattern_TimeLags_Node(null);
  }

  /**
   * Returns the root of this Trie.
   *
   * @return TrieMaximalPattern_TimeLags_Node
   */
  public TrieMaximalPattern_TimeLags_Node getRoot() {
    return this.root;
  }

  //###########################################################################################################################
  //###########################################################################################################################

  /**
   * USED TO MARK MODELS AS NON-RIGHT MAXIMAL !!
   *
   * @param model int[]
   * @param genes_patternFirstColumns_Map TreeMap
   * @return Genes_PatternFirstColumns_PatternIsMaximal
   */
  public Genes_PatternFirstColumns_PatternIsMaximal addModel(int[] model,
      TreeMap<Integer, Integer> genes_patternFirstColumns_Map) {
    TrieMaximalPattern_TimeLags_Node currentNode = this.root;
    for (int i = 0; i < model.length; i++) {
      if (i == model.length - 1) { // LAST CHARACTER --> STORE NODE INFO
        currentNode = currentNode.add(model[i], genes_patternFirstColumns_Map);
      }
      else {
        currentNode.add(model[i], null);
      }
    }
    return currentNode.info.occurrencesAtDifferentTimeLags.get(0); // IN CASE OF MULTIPLE OCCURRENCES LAST OCCURRENCE INSERTED IS AT FIRST POSITION
  }

  //###########################################################################################################################


  /**
   * USED TO MARK MODELS AS NON-LEFT MAXIMAL !!
   *
   * @param model int[]
   * @param genes_patternFirstColumns_Map TreeMap
   * @return Genes_PatternFirstColumns_PatternIsMaximal
   */
  public Genes_PatternFirstColumns_PatternIsMaximal addInvertedModel(int[]
      model, TreeMap<Integer, Integer> genes_patternFirstColumns_Map) {
    TrieMaximalPattern_TimeLags_Node currentNode = this.root;
    for (int i = model.length - 1; i >= 0; i--) {
      if (i == 0) { // LAST CHARACTER --> STORE NODE INFO
        currentNode = currentNode.add(model[i], genes_patternFirstColumns_Map);
      }
      else {
        currentNode.add(model[i], null);
      }
    }
    return currentNode.info.occurrencesAtDifferentTimeLags.get(0); // IN CASE OF MULTIPLE OCCURRENCES LAST OCCURRENCE INSERTED IS AT FIRST POSITION
  }

  //###########################################################################################################################
  //###########################################################################################################################


  /**
   *
   */
  public void markNodesHavingNodesWithSameNumberOfGenesInSubTree() {
    this.markNodesHavingNodesWithSameNumberOfGenesInSubTree(this.root.firstChild);
  }

  //###########################################################################################################################

  /**
   * Used recursivelly.
   *
   * @param currentNode TrieMaximalPatternNode
   * @return int
   */
  private int markNodesHavingNodesWithSameNumberOfGenesInSubTree(TrieMaximalPattern_TimeLags_Node currentNode) {
    if (currentNode == null) {
      return 0;
    }
    if (currentNode.firstChild == null) { //LEAF NODE --> ALWAYS MAXIMAL PATTERN
      if (currentNode.rightSibling == null) {
          return currentNode.getInfo().getNumberOfGenes();
      }
      else { // go right
          return Math.max(currentNode.getInfo().getNumberOfGenes(),
                          this.markNodesHavingNodesWithSameNumberOfGenesInSubTree(currentNode.rightSibling));
      }
    }
    else { // INTERNAL NODE
      // go down
      int maxNumberOfGenesInSubTree = this.markNodesHavingNodesWithSameNumberOfGenesInSubTree(currentNode.firstChild);
      if (currentNode.getInfo() != null) { // node corresponds to an inserted model
        if (maxNumberOfGenesInSubTree == currentNode.getInfo().getNumberOfGenes()) {
          // hasNodesWithSameNumberOfGenesInSubTree = TRUE
          currentNode.getInfo().setHasNodesWithSameNumberOfGenesInSubTree();
        }
        if (currentNode.rightSibling != null) { // go right
          return Math.max(currentNode.getInfo().getNumberOfGenes(),
                          this.markNodesHavingNodesWithSameNumberOfGenesInSubTree(currentNode.rightSibling));
        }
        else {
          return Math.max(maxNumberOfGenesInSubTree, currentNode.getInfo().getNumberOfGenes());
        }
      }
      else { // node does not correspond to an inserted model
        if (currentNode.rightSibling != null) { // go right
          return this.markNodesHavingNodesWithSameNumberOfGenesInSubTree(currentNode.rightSibling);
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
     */
    public void markNonRightMaximalPatterns() {
      this.markNonRightMaximalPatterns(this.root.firstChild);
    }

    //###########################################################################################################################

    /**
     * Used recursivelly.
     *
     * @param currentNode TrieMaximalPattern_TimeLags_Node
     */
    protected void markNonRightMaximalPatterns(TrieMaximalPattern_TimeLags_Node currentNode) {

      if (currentNode == null){
        return;
      }

      if (currentNode.firstChild == null) { // LEAF NODE
        // ALL LEAF NODE CORRESPOND TO RIGHT-MAXIMAL PATTERNS
        if (currentNode.rightSibling != null) {
          // GO RIGHT
          this.markNonRightMaximalPatterns(currentNode.rightSibling);
        }
      }
      else { // INTERNAL NODE
        // DO SOMETHING WITH THE NODE ??
        if (currentNode.getInfo() != null) { // NODE CORRESPONDS TO AN INSERTED MODEL !!!
          // CHECK NUMBER OF genes_patternFirstColumns_Maps
          if(currentNode.numberOfReferencesToModelsAndOccurrences() == 1){
            if (currentNode.hasNodesWithSameNumberOfGenesInSubTree()){
              currentNode.info.occurrencesAtDifferentTimeLags.get(0).setPatternIsMaximalToFalse();
            }
          }
          else{
            // GET ALL genes_patternFirstColumns_IN_Subtree
            ArrayList<TreeMap<Integer, Integer>> all_genes_patternFirstColumns_IN_Subtree = new ArrayList<TreeMap<Integer,Integer>>(0);
            this.get_ALL_genes_patternFirstColumns_IN_Subtree(currentNode, all_genes_patternFirstColumns_IN_Subtree);

//            System.out.println(all_genes_patternFirstColumns_IN_Subtree.size());

            Iterator<Genes_PatternFirstColumns_PatternIsMaximal> i = currentNode.info.occurrencesAtDifferentTimeLags.iterator();
            while (i.hasNext()){
              Genes_PatternFirstColumns_PatternIsMaximal currentInNode = i.next();
              TreeMap<Integer, Integer> currentInNode_Map = currentInNode.genes_patternFirstColumns_Map;
              int p=0;
              boolean found = false;
              while(p < all_genes_patternFirstColumns_IN_Subtree.size() && !found){
                TreeMap<Integer, Integer> currentInSubtree_Map = all_genes_patternFirstColumns_IN_Subtree.get(p);
                if (currentInNode_Map.equals(currentInSubtree_Map)){ // !!!
                  // PATTERN IS NOT MAXIMAL
//                  System.out.println("setPatternIsMaximalToFalse");
                  currentInNode.setPatternIsMaximalToFalse();
                  found = true;
                }
                p++;
              }
           }
          }
        }

        // GO DOWN
        this.markNonRightMaximalPatterns(currentNode.firstChild);
        // GO RIGHT
        if (currentNode.rightSibling != null) {
          this.markNonRightMaximalPatterns(currentNode.rightSibling);
        }
      }
    }

    //###########################################################################################################################

    /**
     *
     * @param currentNode TrieMaximalPattern_TimeLags_Node
     * @param all_genes_patternFirstColumns_IN_Subtree ArrayList
     */
    private void get_ALL_genes_patternFirstColumns_IN_Subtree(TrieMaximalPattern_TimeLags_Node currentNode, ArrayList<TreeMap<Integer, Integer>> all_genes_patternFirstColumns_IN_Subtree){

      if (currentNode.firstChild == null) { // LEAF NODE
       Iterator<Genes_PatternFirstColumns_PatternIsMaximal> i = currentNode.info.occurrencesAtDifferentTimeLags.iterator();
       while (i.hasNext()){
        TreeMap<Integer, Integer> current = i.next().genes_patternFirstColumns_Map;
        all_genes_patternFirstColumns_IN_Subtree.add(0, current);
      }

      if (currentNode.rightSibling != null) {
          // GO RIGHT
          this.get_ALL_genes_patternFirstColumns_IN_Subtree(currentNode.rightSibling, all_genes_patternFirstColumns_IN_Subtree);
        }
      }
      else { // INTERNAL NODE
        // DO SOMETHING WITH THE NODE ??
        if (currentNode.getInfo() != null) { // NODE CORRESPONDS TO AN INSERTED MODEL !!!
         Iterator<Genes_PatternFirstColumns_PatternIsMaximal> i = currentNode.info.occurrencesAtDifferentTimeLags.iterator();
         while (i.hasNext()){
           TreeMap<Integer, Integer> current = i.next().genes_patternFirstColumns_Map;
           all_genes_patternFirstColumns_IN_Subtree.add(0, current);
         }
        }
        // GO DOWN
        this.get_ALL_genes_patternFirstColumns_IN_Subtree(currentNode.firstChild, all_genes_patternFirstColumns_IN_Subtree);
        // GO RIGHT
        if (currentNode.rightSibling != null) {
          this.get_ALL_genes_patternFirstColumns_IN_Subtree(currentNode.rightSibling, all_genes_patternFirstColumns_IN_Subtree);
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
  public TrieMaximalPattern_TimeLags_Node findModel(int[] model) {
    if (model.length == 0) {
      return null;
    }

    TrieMaximalPattern_TimeLags_Node currentNode = this.root;
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
  private void printWithErrorsLeftMaximal(TrieMaximalPattern_TimeLags_Node
                                          currentNode,
                                          String currentNodeDepth) {
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
}
