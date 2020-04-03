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
public class Row_SignChange
    implements Comparable {

  /**
   *
   */
  private int row;

  /**
   *
   */
  private boolean signChange;

  /**
   *
   * @param row int
   * @param signChange boolean
   */
  public Row_SignChange(int row, boolean signChange) {
    this.row = row;
    this.signChange = signChange;
  }

  /**
   *
   * @return int
   */
  public int getRow() {
    return this.row;
  }

  /**
   *
   * @return boolean
   */
  public boolean getSignChange() {
    return this.signChange;
  }

  /**
   *
   * @param o Object
   * @return boolean
   */
  public boolean equals(Object o) {
    return (o instanceof Row_SignChange &&
            this.row == ( (Row_SignChange) o).row &&
            this.signChange == ( (Row_SignChange) o).signChange);
  }

  /**
   *
   * @param o1 Object
   * @param o2 Object
   * @return int
   */
  public int compare(Object o1, Object o2) {
    Integer o1_row = ( (Row_SignChange) o1).row;
    Integer o2_row = ( (Row_SignChange) o2).row;
    return (o1_row.compareTo(o2_row));
  }

  /**
   *
   * @param o1 Object
   * @return int
   */
  public int compareTo(Object o1) {
    return this.compare(this, o1);
  }

  /**
   *
   * @return int
   */
  public int hashCode() {
    int hash = 17;
    hash = 37 * hash + this.row;
    hash = 37 * hash + new Boolean(this.signChange).hashCode();
    return hash;
  }
}
