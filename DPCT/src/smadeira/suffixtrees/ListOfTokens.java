package smadeira.suffixtrees;

import java.io.*;
import java.util.*;

/**
 * <p>Title: List of Tokens</p>
 *
 * <p>Description: List of tokens of a suffix tree.</p>
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
public abstract class ListOfTokens
    implements Serializable {

  /**
   *
   * Vector of size #genes where each element is ab array of ints representing gene i.
   */
  ArrayList<Token> vectorOfArrays;

  public ListOfTokens() {
    this.vectorOfArrays = new ArrayList<Token> ();
  }

  /**
   * Inserts token (gene) as arrays of ints in the vector with all the genes already inserted in the suffix tree
   * if the token is not already there. Returns token position.
   *
   * @param token int[]
   * @return int
   */
  public abstract int insertToken(int[] token);

  /**
   * Returns the subarray at the position "position", where position is counted
   * from the first int of the first token (cumulative position).
   *
   * @param leftIndex int
   * @param length int
   * @return int[]
   */
  public abstract int[] getSubArrayInt(int leftIndex, int length);

  /**
   * Returns the subarray at the position "position", to the end of the
   * array,  where position is counted from the first int of the first token (cumulative position).
   *
   * @param leftIndex int
   * @return int[]
   */
  public abstract int[] getSubArrayInt(int leftIndex);

  /**
   * Returns the starting index of the array of int stored in index "position":
   * Example: if we insert a1 = {0,1,2,3}, a2 = {4,5,6,7,8}, a3 = {9}
   * getStart(0) = 0
   * getStart(1) = 4
   * getStart(2) = 9
   *
   * @param position int
   * @return int
   */
  public abstract int getStart(int position);

  /**
   * Returns the index of the array of int that contains position.
   * Returns the starting index of the array of int stored in index "position":
   * Example: if we insert a1 = {0,1,2,3}, a2 = {4,5,6,7,8}, a3 = {9}
   * getIndex(8) = 1 (positions 4-8 belong to array stored at index 1).
   *
   * @param position int
   * @return int
   */
  public abstract int getIndex(int position);

  /**
   * Returns the total length of the array of int contained in this list.
   * @return int
   */
  public abstract int getTotalLength();

  /**
   *
   * @return ArrayList
   */
  public ArrayList getTokenOfArrays() {
    return (this.vectorOfArrays);
  }

}
