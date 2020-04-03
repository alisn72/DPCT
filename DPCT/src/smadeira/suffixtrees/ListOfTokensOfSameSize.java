package smadeira.suffixtrees;

import java.io.*;

/**
 * <p>Title: List of Tokens of Same Size</p>
 *
 * <p>Description: List of tokens of equal size for a suffix tree.</p>
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
public class ListOfTokensOfSameSize
    extends ListOfTokens implements Serializable {

  /**
   * USED TO CREATE GENERALIZED SUFFIX TREES WHERE THE STRINGS HAVE ALL THE SAME SIZE!!!
   */

  /**
   * Size of the array representing a gene (number of columns + 1 for the terminator)
   */
  int tokenSize;

  /**
   *
   * @param tokenSize int
   */
  public ListOfTokensOfSameSize(int tokenSize) {
    this.tokenSize = tokenSize;
  }

  /**
   * Inserts token (gene) as arrays of ints in the vector with all the genes already inserted in the suffix tree
   * if the token is not already there. Returns token position.
   *
   * @param token int[]
   * @return int
   */
  public int insertToken(int[] token) {
    Token t = new Token(token);
    this.vectorOfArrays.add(t);
    return (this.vectorOfArrays.size() - 1);
  }

  /**
   * Returns the subarray at the position "position", where position is counted
   * from the first int of the first token (cumulative position).
   *
   * @param leftIndex int
   * @param length int
   * @return int[]
   */
  public int[] getSubArrayInt(int leftIndex, int length) {
    int stringIndex = (int) (leftIndex / tokenSize);
    Token current = vectorOfArrays.get(stringIndex);
    int[] subArrayInt = new int[length];
    for (int i = 0; i < length; i++) {
      subArrayInt[i] = current.getToken()[ (leftIndex -
                                            (stringIndex * this.tokenSize)) + i];
    }
    return (subArrayInt);
  }

  /**
   * Returns the subarray at the position "position", to the end of the
   * array,  where position is counted from the first int of the first token (cumulative position).
   *
   * @param leftIndex int
   * @return int[]
   */
  public int[] getSubArrayInt(int leftIndex) {
    int stringIndex = (int) (leftIndex / tokenSize);
    Token current = vectorOfArrays.get(stringIndex);
    int arrayIntLength = current.getToken().length;
    int lengthSubArrayInt = arrayIntLength -
        (leftIndex - (stringIndex * this.tokenSize));
    int[] subArrayInt = new int[lengthSubArrayInt];
    for (int i = 0; i < lengthSubArrayInt; i++) {
      subArrayInt[i] = current.getToken()[ (leftIndex -
                                            (stringIndex * this.tokenSize)) + i];
    }
    return (subArrayInt);
  }

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
  public int getStart(int position) {
    return (position * tokenSize);
  }

  /**
   * Returns the index of the array of int that contains position.
   * Returns the starting index of the array of int stored in index "position":
   * Example: if we insert a1 = {0,1,2,3}, a2 = {4,5,6,7,8}, a3 = {9}
   * getIndex(8) = 1 (positions 4-8 belong to array stored at index 1).
   *
   * @param position int
   * @return int
   */
  public int getIndex(int position) {
    return ( (int) (position / tokenSize));
  }

  /**
   * Returns the total length of the array of int contained in this list.
   * @return int
   */
  public int getTotalLength() {
    return (vectorOfArrays.size() * tokenSize);
  }

  /**
   *
   * @return int
   */
  public int getTokenSize() {
    return (this.tokenSize);
  }

}
