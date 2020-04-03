package smadeira.tries;

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
public class Genes_PatternFirstColumns_PatternIsMaximal {

  /**
   *
   */
  protected boolean patternIsMaximal;

  /**
   *
   */
  protected TreeMap<Integer, Integer> genes_patternFirstColumns_Map;


  /**
   *
   * @param genes_patternFirstColumns_Map TreeMap
   */
  public Genes_PatternFirstColumns_PatternIsMaximal (TreeMap<Integer, Integer> genes_patternFirstColumns_Map){
    this.patternIsMaximal = true;
    this.genes_patternFirstColumns_Map = genes_patternFirstColumns_Map;
  }

  /**
   *
   * @return boolean
   */
  public boolean getPatternIsMaximal(){
    return this.patternIsMaximal;
  }

  /**
   *
   * @return TreeMap
   */
  public TreeMap<Integer, Integer> getGenes_patternFirstColumns_Map(){
    return this.genes_patternFirstColumns_Map;
  }

  /**
   *
   */
  public void setPatternIsMaximalToFalse(){
  this.patternIsMaximal = false;
  }
}
