package smadeira.tries;

import java.util.ArrayList;
import java.util.TreeMap;

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
public class TrieMaximalPattern_TimeLags_Node{

  /**
   * A pointer to the first of this node's children.
   */
  protected TrieMaximalPattern_TimeLags_Node firstChild;

  /**
   * The character that labels the edge from this node to the child node pointer by
   * <CODE>son</CODE>.
   */
  protected int to_firstChild;

  /**
   * A pointer to this node's next sibling.
   */
  protected TrieMaximalPattern_TimeLags_Node rightSibling;

  /**
   * The character that labels the edge from this node to the sibling pointer by
   * <CODE>rightSibling</CODE>.
   */
  protected int to_rightSibling;

  /**
   *
   */
  protected TrieMaximalPattern_TimeLags_NodeInfo info;

  /**
   *
   * @param genes_patternFirstColumns_Map TreeMap
   */
  public TrieMaximalPattern_TimeLags_Node(TreeMap<Integer, Integer> genes_patternFirstColumns_Map) {
    this.firstChild = null;
    this.rightSibling = null;
    if (genes_patternFirstColumns_Map != null){
      this.info = new TrieMaximalPattern_TimeLags_NodeInfo(genes_patternFirstColumns_Map);
    }
  }

  /**
   *
   * @return TrieMaximalPattern_TimeLags_NodeInfo
   */
  public TrieMaximalPattern_TimeLags_NodeInfo getInfo() {
    return this.info;
  }

  /**
   *
   * @param info TrieMaximalPattern_TimeLags_NodeInfo
   */
  public void setInfo(TrieMaximalPattern_TimeLags_NodeInfo info) {
    this.info = info;;
  }

  /**
   *
   * @return int
   */
  public int numberOfReferencesToModelsAndOccurrences(){
    return  this.info.getModelAndOccurrencesAtDifferentTimeLags().size();
  }

  /**
   *
   * @return boolean
   */
  public boolean hasNodesWithSameNumberOfGenesInSubTree(){
    return this.info.hasNodesWithSameNumberOfGenesInSubTree;
  }


  /**
   *
   * @param token char
   * @param genes_patternFirstColumns_Map TreeMap
   * @return Genes_PatternFirstColumns_PatternIsMaximal
   */
  public TrieMaximalPattern_TimeLags_Node add(int token, TreeMap<Integer, Integer> genes_patternFirstColumns_Map) {
    if (this.firstChild == null) { //LEAF NODE
      this.firstChild = new TrieMaximalPattern_TimeLags_Node(genes_patternFirstColumns_Map);
      this.to_firstChild = token;
      return this.firstChild;
    }
    else {
      if (this.to_firstChild != token) {
        return this.firstChild.addRightSibling(token, genes_patternFirstColumns_Map);
      }
      else {
        // PATTERN ALREADY EXISTS
        //--> ADD referenceToModelAndOccurrences TO ITS occurrencesAtDifferentTimeLags IN this.info
        if(genes_patternFirstColumns_Map != null){
//          System.out.println(token);
//          System.out.println("NOT NULL FIRST CHILD");
          if(this.firstChild.getInfo() != null){
            this.firstChild.getInfo().addOccurrencesAtDifferentTimeLags(genes_patternFirstColumns_Map); // AT FIRST POSITION !!!
          }
          else{
            this.firstChild.setInfo(new TrieMaximalPattern_TimeLags_NodeInfo(genes_patternFirstColumns_Map));
          }
        }
        return this.firstChild;
      }
    }
  }

  /**
   *
   * @param token char
   * @param genes_patternFirstColumns_Map TreeMap
   * @return Genes_PatternFirstColumns_PatternIsMaximal
   */
  public TrieMaximalPattern_TimeLags_Node addRightSibling(int token, TreeMap<Integer, Integer> genes_patternFirstColumns_Map) {
    if (this.rightSibling == null) {
      this.rightSibling = new TrieMaximalPattern_TimeLags_Node(genes_patternFirstColumns_Map);
      this.to_rightSibling = token;
      return this.rightSibling;
    }
    else {
      if (this.to_rightSibling != token) {
        return this.rightSibling.addRightSibling(token, genes_patternFirstColumns_Map);
      }
      else {
        // PATTERN ALREADY EXISTS
        //--> ADD referenceToModelAndOccurrences TO ITS occurrencesAtDifferentTimeLags IN this.info
        if(genes_patternFirstColumns_Map != null){
//          System.out.println(token);
//          System.out.println("NOT NULL RIGHT SYBLING");
          if(this.rightSibling.info != null){
            this.rightSibling.getInfo().addOccurrencesAtDifferentTimeLags(genes_patternFirstColumns_Map);
          }
          else{
            this.rightSibling.setInfo(new TrieMaximalPattern_TimeLags_NodeInfo(genes_patternFirstColumns_Map));
          }
        }
        return this.rightSibling;
      }
    }
  }

  /**
   *
   * @param token int
   * @return TrieMaximalPattern_TimeLags_Node
   */
  public TrieMaximalPattern_TimeLags_Node spellDown(int token) {
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
   *
   * @param token int
   * @return TrieMaximalPatternNode
   */
  public TrieMaximalPattern_TimeLags_Node spellRight(int token) {
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
}
