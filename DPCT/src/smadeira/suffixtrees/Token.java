package smadeira.suffixtrees;

/**
 * <p>Title: Token</p>
 *
 * <p>Description: A token of a suffix tree..</p>
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
public class Token {

  /**
   * Stores an array of int values represented a gene after alphabet transformation.
   * EXAMPLE: N1 U2 D3 U4 N5 $6
   */
  private int[] token;

  /**
   * @stereotype constructor
   */
  public Token() {
    this.token = null;
  }

  /**
   *
   * @param token int[]
   */
  public Token(int[] token) {
    this.token = token;
  }

  /**
   *
   * @param token int[]
   */
  public void setToken(int[] token) {
    this.token = token;
  }

  /**
   *
   * @return int
   */
  public int getLen() {
    return this.token.length;
  }

  /**
   *
   * @return int[]
   */
  public int[] getToken() {
    return this.token;
  }

  /**
   * Returns a new array of ints that is a sub-array of this.token.
   * The sub-array begins at the specified beginIndex and extends to the int at index endIndex-1.
   *
   * @param beginIndex int
   * @param endIndex int
   * @return int[]
   */
  public int[] getSubArrayInt(int beginIndex, int endIndex) {
    int arrayIntLength = this.token.length;
    if (beginIndex < 0 || endIndex >= arrayIntLength) {
      throw new IndexOutOfBoundsException();
    }
    int lengthSubArrayInt = endIndex - beginIndex;
    int[] subArrayInt = new int[lengthSubArrayInt];
    for (int i = 0; i < lengthSubArrayInt; i++) {
      subArrayInt[i] = this.token[beginIndex + i];
    }
    return (subArrayInt);
  }

  /**
   * Returns a new array of ints that is a sub-array of this.token.
   * The sub-array begins at the specified beginIndex and extends to the last position of this.token.
   *
   * @param beginIndex int
   * @return int[]
   */
  public int[] getSubArrayInt(int beginIndex) {
    int arrayIntLength = this.token.length;
    if (beginIndex < 0 || beginIndex >= arrayIntLength) {
      throw new IndexOutOfBoundsException();
    }
    int lengthSubArrayInt = arrayIntLength - beginIndex;
    int[] subArrayInt = new int[lengthSubArrayInt];
    for (int i = 0; i < lengthSubArrayInt; i++) {
      subArrayInt[i] = this.token[beginIndex + i];
    }
    return (subArrayInt);
  }

  public String toString() {
    String s = new String();
    for (int i = 0; i < this.token.length; i++) {
      s = s + this.token[i] + "\t";
    }
    return (s);
  }
}
