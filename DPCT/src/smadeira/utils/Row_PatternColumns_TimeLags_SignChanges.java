package smadeira.utils;

import java.util.*;

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
public class Row_PatternColumns_TimeLags_SignChanges
    extends Row_PatternColumns_TimeLags implements Comparable {

  /**
   *
   */
  private ArrayList<Boolean> signChanges;

  /**
   *
   * @param row int
   */
  public Row_PatternColumns_TimeLags_SignChanges(int row) {
    super(row);
    this.signChanges = new ArrayList<Boolean> (0);
  }

  /**
   *
   * @param columns int[]
   * @param timeLag int
   * @param signChange boolean
   */
  public void add_PatternColumns_TimeLag_SignChange(int[] columns, int timeLag,
      boolean signChange) {
    this.columns.add(columns);
    this.timeLags.add(timeLag);
    this.signChanges.add(signChange);
  }

  /**
   *
   * @return ArrayList
   */
  public ArrayList<Boolean> getSignChanges() {
    return this.signChanges;
  }

  /**
   *
   * @param o Object
   * @return boolean
   */
  public boolean equals(Object o) {
    return (o instanceof Row_PatternColumns_TimeLags_SignChanges &&
            this.
            timeLags.equals( ( (Row_PatternColumns_TimeLags_SignChanges) o).timeLags) &&
            this.row == ( (Row_PatternColumns_TimeLags_SignChanges) o).row &&
            this.
            columns.equals( ( (Row_PatternColumns_TimeLags_SignChanges) o).columns) &&
            this.signChanges ==
            ( (Row_PatternColumns_TimeLags_SignChanges) o).signChanges);
  }

  /**
   *
   * @param o1 Object
   * @param o2 Object
   * @return int
   */
  public int compare(Object o1, Object o2) {
    /*
         Integer o1_hash = ((Row_PatternColumns_TimeLags)o1).hashCode();
         Integer o2_hash = ((Row_PatternColumns_TimeLags)o2).hashCode();
         return(o1_hash.compareTo(o2_hash));
     */
    Integer o1_row = ( (Row_PatternColumns_TimeLags_SignChanges) o1).row;
    Integer o2_row = ( (Row_PatternColumns_TimeLags_SignChanges) o2).row;
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
    hash = 37 * hash + (null == this.columns ? 0 : this.columns.hashCode());
    hash = 37 * hash + (null == this.timeLags ? 0 : this.timeLags.hashCode());
    hash = 37 * hash +
        (null == this.signChanges ? 0 : this.signChanges.hashCode());
    return hash;
  }
}
