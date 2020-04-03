package smadeira.tries;

import java.util.TreeMap;
import java.util.ArrayList;
import smadeira.utils.ModelOccurrences;

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
public class TrieMaximalPattern_TimeLags_NodeInfo {

  /**
   *
   */
  protected int numberOfGenes;

  /**
   *
   */
  protected boolean hasNodesWithSameNumberOfGenesInSubTree;

  /**
   *
   */
  protected ArrayList<Genes_PatternFirstColumns_PatternIsMaximal> occurrencesAtDifferentTimeLags;

  /**
   *
   * @param genes_patternFirstColumns_Map TreeMap
   */
  public TrieMaximalPattern_TimeLags_NodeInfo(TreeMap<Integer, Integer> genes_patternFirstColumns_Map) {
    this.numberOfGenes = genes_patternFirstColumns_Map.size();
    this.hasNodesWithSameNumberOfGenesInSubTree = false;
    this.occurrencesAtDifferentTimeLags = new ArrayList<Genes_PatternFirstColumns_PatternIsMaximal>(1);
    this.occurrencesAtDifferentTimeLags.add(0, new Genes_PatternFirstColumns_PatternIsMaximal(genes_patternFirstColumns_Map));
  }

  /**
   *
   * @return int
   */
  public int getNumberOfGenes() {
    return this.numberOfGenes;
  }

  /**
   *
   * @return boolean
   */
  public boolean getHasNodesWithSameNumberOfGenesInSubTree () {
    return this.hasNodesWithSameNumberOfGenesInSubTree;
  }


  /**
   * Sets this flag to true.
   */
  public void setHasNodesWithSameNumberOfGenesInSubTree () {
    this.hasNodesWithSameNumberOfGenesInSubTree = true;
  }

  /**
   *
   * @return ArrayList<Genes_PatternFirstColumns_PatternIsMaximal>
   */
  public ArrayList<Genes_PatternFirstColumns_PatternIsMaximal> getModelAndOccurrencesAtDifferentTimeLags(){
    return this.occurrencesAtDifferentTimeLags;
  }

  /**
   * Adds a new object of class <CODE>Genes_PatternFirstColumns_PatternIsMaximal<\CODE> to this.occurrencesAtDifferentTimeLags
   * at first position.
   * @param genes_patternFirstColumns_Map TreeMap
   * @return Genes_PatternFirstColumns_PatternIsMaximal
   */
  public Genes_PatternFirstColumns_PatternIsMaximal addOccurrencesAtDifferentTimeLags (TreeMap<Integer, Integer> genes_patternFirstColumns_Map){
    if (genes_patternFirstColumns_Map != null){
      Genes_PatternFirstColumns_PatternIsMaximal occurrencesAtDifferentTimeLags = new
          Genes_PatternFirstColumns_PatternIsMaximal(genes_patternFirstColumns_Map);
      this.occurrencesAtDifferentTimeLags.add(0, occurrencesAtDifferentTimeLags); // !!! AT FIRST POSITION
      return occurrencesAtDifferentTimeLags;
    }
    else{
      return null;
    }
  }

  /**
   *
   * @return String
   */
  public String toString() {
    String s = new String();

    s = s + "<NODE_INFO" + "\t";
    s = s + "this.numberOfGenes = " + this.numberOfGenes + "\t";
    s = s + "this.hasNodesWithSameNumberOfGenesInSubTree = " + this.hasNodesWithSameNumberOfGenesInSubTree + "\t";
    s = s + "this.occurrencesAtDifferentTimeLags.size() = " + this.occurrencesAtDifferentTimeLags.size() + ">";
    s = s + "</NODE_INFO>";
    return s;
  }

}
