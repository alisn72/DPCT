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
public class TrieMaximalPatternNodeInfo {

  /**
   *
   */
  protected int numberOfGenes;

  /**
   *
   */
  protected boolean isMaximalPattern;

  /**
   *
   */
  public TrieMaximalPatternNodeInfo() {
    this.isMaximalPattern = false;
    this.numberOfGenes = 0;
  }

  /**
   *
   * @param numberOfGenes int
   * @param isMaximalPattern boolean
   */
  public TrieMaximalPatternNodeInfo(int numberOfGenes,
                                           boolean isMaximalPattern) {
    this.isMaximalPattern = isMaximalPattern;
    this.numberOfGenes = numberOfGenes;
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
   * @param numberOfGenes int
   */
  public void setNumberOfGenes(int numberOfGenes) {
    this.numberOfGenes = numberOfGenes;
  }

  /**
   *
   * @return boolean
   */
  public boolean getIsMaximalPattern() {
    return this.isMaximalPattern;
  }

  /**
   *
   * @param isMaximalPattern boolean
   */
  public void setIsMaximalPattern(boolean isMaximalPattern) {
    this.isMaximalPattern = isMaximalPattern;
  }

  /**
   *
   * @return String
   */
  public String toString() {
    String s = new String();

    s = s + "<NODE_INFO" + "\t";
    s = s + "this.numberOfGenes = " + this.numberOfGenes + "\t";
    s = s + "this.isMaximalPattern = " + this.isMaximalPattern + ">";
    s = s + "</NODE_INFO>";

    return s;
  }

}
