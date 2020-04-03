package smadeira.utils;

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
public class HashKey_Pattern {

  private String pattern;
  private int firstColumn;
  private int lastColumn;

  public HashKey_Pattern(String s, int first, int last) {
    this.pattern = s;
    this.firstColumn = first;
    this.lastColumn = last;
  }

  public String getPattern() {
    return (this.pattern);
  }

  public int getFirstColumn() {
    return (this.firstColumn);
  }

  public int getLastColumn() {
    return (this.lastColumn);
  }

  public void setPattern(String p) {
    this.pattern = p;
  }

  public void setFirstColumn(int first) {
    this.firstColumn = first;
  }

  public void setLastColumn(int last) {
    this.lastColumn = last;
  }

  /**
   *
   * @return String
   */
  public String toString() {
    String s = new String("(" + this.pattern + ", " + this.firstColumn + ", " +
                          this.lastColumn + ")");
    return (s);
  }

  /**
   *
   * @param o Object
   * @return boolean
   */
  public boolean equals(Object o) {
    return (o instanceof HashKey_Pattern &&
            this.firstColumn == ( (HashKey_Pattern) o).firstColumn &&
            this.lastColumn == ( (HashKey_Pattern) o).lastColumn &&
            this.pattern.equals( ( (HashKey_Pattern) o).pattern));
  }

  /**
   *
   * @return int
   */
  public int hashCode() {
    int hash = 17;
    hash = 37 * hash + this.firstColumn;
    hash = 37 * hash + this.lastColumn;
    hash = 37 * hash + (null == this.pattern ? 0 : pattern.hashCode());
    return hash;
  }

}
