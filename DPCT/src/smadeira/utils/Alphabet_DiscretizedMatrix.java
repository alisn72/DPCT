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
public class Alphabet_DiscretizedMatrix {

  /**
   *
   */
  private char[] alphabet;

  /**
   *
   */
  private char[][] symbolicMatrix;

  /**
   *
   */
  public Alphabet_DiscretizedMatrix() {
  }

  /**
   *
   * @param a char[]
   * @param m char[][]
   */
  public Alphabet_DiscretizedMatrix(char[] a, char[][] m) {
    this.alphabet = a;
    this.symbolicMatrix = m;
  }

  /**
   *
   * @param a char[]
   */
  public void setAlphabet(char[] a) {
    this.alphabet = a;
  }

  /**
   *
   * @param m char[][]
   */
  public void setSymbolicMatrix(char[][] m) {
    this.symbolicMatrix = m;
  }

  /**
   *
   * @return char[]
   */
  public char[] getAlphabet() {
    return this.alphabet;
  }

  /**
   *
   * @return char[][]
   */
  public char[][] getSymbolicMatrix() {
    return this.symbolicMatrix;
  }
}
