package smadeira.suffixtrees;

import java.util.*;

/**
 * <p>Title: List of Tokens of Different Size</p>
 *
 * <p>Description: List of tokens of different size for a suffix tree.</p>
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
public class ListOfTokensOfDifferentSize
    extends ListOfTokens {

  /**
   * USE TO CREATE GENERALIZED SUFFIX TREES WHERE THE STRINGS HAVE ARBITRARY SIZE!!
   */

  /**
   * Sizes of the array representing a token (number of columns + 1 for the terminator)
   */
  ArrayList<Integer> tokenSizes;

  /**
   * @stereotype constructor
   */
  public ListOfTokensOfDifferentSize() {
    this.tokenSizes = new ArrayList<Integer> (0);
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
    this.tokenSizes.add(new Integer(token.length));
    return (this.vectorOfArrays.size() - 1);
  }

  /**
   * Compute string index when the strings inserted in the tree may not have the same size.
   *
   * @param leftIndex int
   * @return int
   */
  private int getStringIndex(int leftIndex) {
    // compute string index
    boolean stringIndexFound = false;
    int position = 0;
    int stringIndex = 0;
    int sumStringSizes = 0;
    while (stringIndexFound == false && position < this.vectorOfArrays.size()) {
      sumStringSizes = sumStringSizes +
          ( (Integer)this.tokenSizes.get(position)).intValue();
      if (leftIndex < sumStringSizes) {
        stringIndexFound = true;
        stringIndex = position;
      }
      position++;
    }
    return (stringIndex);
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
    int stringIndex = this.getStringIndex(leftIndex);
    Token current = vectorOfArrays.get(stringIndex);
    int[] subArrayInt = new int[length];

    int totalLengthTokens = 0;
    for (int i = 0; i < stringIndex; i++) {
      totalLengthTokens = totalLengthTokens + this.tokenSizes.get(i).intValue();
    }

    for (int i = 0; i < length; i++) {
      subArrayInt[i] = current.getToken()[ (leftIndex - totalLengthTokens) + i];
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
    int stringIndex = this.getStringIndex(leftIndex);
    Token current = vectorOfArrays.get(stringIndex);
    int arrayIntLength = current.getToken().length;

    int totalLengthTokens = 0;
    for (int i = 0; i < stringIndex; i++) {
      totalLengthTokens = totalLengthTokens + this.tokenSizes.get(i).intValue();
    }

    int lengthSubArrayInt = arrayIntLength - (leftIndex - totalLengthTokens);
    int[] subArrayInt = new int[lengthSubArrayInt];
    for (int i = 0; i < lengthSubArrayInt; i++) {
      subArrayInt[i] = current.getToken()[ (leftIndex - totalLengthTokens) + i];
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
    int sumSizes = 0;
    for (int i = 0; i < position; i++) {
      sumSizes = sumSizes + this.tokenSizes.get(i).intValue();
    }
    return (sumSizes);
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
    return (this.getStringIndex(position));
  }

  /**
   * Returns the total length of the array of int contained in this list.
   * @return int
   */
  public int getTotalLength() {
    int totalLength = 0;
    int tokenSizesSize = this.tokenSizes.size();
    for (int i = 0; i < tokenSizesSize; i++) {
      totalLength = totalLength + this.tokenSizes.get(i).intValue();
    }
    return (totalLength);
  }

  /**
   *
   * @return ArrayList
   */
  public ArrayList getTokenOfArrays() {
    return (this.vectorOfArrays);
  }

  /**
   *
   * @return ArrayList
   */
  public ArrayList<Integer> getTokenSizes() {
    return (this.tokenSizes);
  }

// #################################### MAIN ########################################


  public static void main(String[] args) {

    ListOfTokensOfDifferentSize lt = new ListOfTokensOfDifferentSize();
    int t1[] = {
        0, 1, 2, 3};
    lt.insertToken(t1); //insere no fim da lista!!

    int t2[] = {
        4, 5, 6};
    lt.insertToken(t2);

    int t3[] = {
        8, 9};
    lt.insertToken(t3);

/*    System.out.println("VECTOR OF ARRAYS = ");
    for (int i = 0; i < lt.vectorOfArrays.size(); i++) {
      System.out.println(lt.vectorOfArrays.get(i).toString());
    }
    System.out.println();

    System.out.println("TOKENSIZES = ");
    for (int i = 0; i < lt.tokenSizes.size(); i++) {
      System.out.println(lt.tokenSizes.get(i).intValue() + "\t");
    }
    System.out.println();

    System.out.println("lt.getStart(0) = " + lt.getStart(0));
    System.out.println("lt.getStart(1) = " + lt.getStart(1));
    System.out.println("lt.getStart(2) = " + lt.getStart(2));
    System.out.println();

    // token 0
    System.out.println("lt.getindex(0) = " + lt.getIndex(0));
    System.out.println("lt.getindex(2) = " + lt.getIndex(2));
    System.out.println("lt.getindex(3) = " + lt.getIndex(3));
    System.out.println();

    // token 1
    System.out.println("lt.getindex(4) = " + lt.getIndex(4));
    System.out.println("lt.getindex(5) = " + lt.getIndex(5));
    System.out.println("lt.getindex(6) = " + lt.getIndex(6));
    System.out.println();

    // token 2
    System.out.println("lt.getindex(7) = " + lt.getIndex(7));
    System.out.println("lt.getindex(8) = " + lt.getIndex(8));
    System.out.println();
*/

    // token 0 --> 1 2 3
    int a[] = lt.getSubArrayInt(1);
/*    for (int i = 0; i < a.length; i++) {
      System.out.print(a[i] + "\t");
    }
    System.out.println();
*/

    // token 1 --> 5 6
    int b[] = lt.getSubArrayInt(5);
/*    for (int i = 0; i < b.length; i++) {
      System.out.print(b[i] + "\t");
    }
    System.out.println();
*/

    // token 2 --> 8 9
    int c[] = lt.getSubArrayInt(7);
/*    for (int i = 0; i < c.length; i++) {
      System.out.print(c[i] + "\t");
    }
    System.out.println();
*/

    // token 0 --> 0 1
    int d[] = lt.getSubArrayInt(0, 2);
/*    for (int i = 0; i < d.length; i++) {
      System.out.print(d[i] + "\t");
    }
    System.out.println();
*/

    // token 1 --> 5
    int e[] = lt.getSubArrayInt(5, 1);
/*    for (int i = 0; i < e.length; i++) {
      System.out.print(e[i] + "\t");
    }
    System.out.println();
*/

    // token 2 --> 9
    int f[] = lt.getSubArrayInt(8, 1);
/*    for (int i = 0; i < f.length; i++) {
      System.out.print(f[i] + "\t");
    }
    System.out.println();
*/

  }
}
