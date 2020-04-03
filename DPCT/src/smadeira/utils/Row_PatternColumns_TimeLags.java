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
public class Row_PatternColumns_TimeLags
    implements Comparable {

  /**
   *
   */
  protected int row;

  /**
   *
   */
  protected ArrayList<int[]> columns;

  /**
   *
   */
  protected ArrayList<Integer> timeLags;

  /**
   *
   * @param row int
   */
  public Row_PatternColumns_TimeLags(int row) {
    this.row = row;
    this.columns = new ArrayList<int[]> (0);
    this.timeLags = new ArrayList<Integer> (0);
  }

  /**
   *
   * @param columns int[]
   * @param timeLag int
   */
  public void add_PatternColumns_TimeLag(int[] columns, int timeLag) {
    this.columns.add(columns);
    this.timeLags.add(timeLag);
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
   * @return ArrayList
   */
  public ArrayList<int[]> getColumns() {
    return this.columns;
  }

  /**
   *
   * @return ArrayList
   */
  public ArrayList<Integer> getTimeLags() {
    return this.timeLags;
  }

  /**
   *
   * @param o Object
   * @return boolean
   */
  public boolean equals(Object o) {
    return (o instanceof Row_PatternColumns_TimeLags &&
            this.timeLags.equals( ( (Row_PatternColumns_TimeLags) o).timeLags) &&
            this.row == ( (Row_PatternColumns_TimeLags) o).row &&
            this.columns.equals( ( (Row_PatternColumns_TimeLags) o).columns));
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
    Integer o1_row = ( (Row_PatternColumns_TimeLags) o1).row;
    Integer o2_row = ( (Row_PatternColumns_TimeLags) o2).row;
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
    return hash;
  }
}
