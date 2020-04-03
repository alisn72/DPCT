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
public class Model {
  /**
   * Motif.
   */
  private int[] motif;

  // METHODS

  public Model() {
    this.motif = new int[0];
  }

  public Model(int length) {
    this.motif = new int[length];
  }

  public Model(int[] motif) {
    this.motif = motif;
  }

  public Model(Model m) {
    this.motif = new int[m.motif.length];
    for (int i = 0; i < m.motif.length; i++) {
      this.motif[i] = m.motif[i];
    }
  }

  /**
   * Return length of the motif.
   * @return int
   */
  public int length() {
    return (this.motif.length);
  }

  /**
   * Return element at position i;
   * @param i int
   * @return int
   */
  public int getElementAt(int i) {
    return (this.motif[i]);
  }

  /**
   * Set position i with x.
   * @param i int
   * @param x int
   */
  public void setElementAt(int i, int x) {
    this.motif[i] = x;
  }

  /**
   * Appends an element to the motif.
   *
   * @param x int
   */
  public void append(int x) {
    this.motif[this.motif.length - 1] = x;
  }

  /**
   * Inserts an array in the begining of themotif.
   *
   * @param x int
   */
  public void insert(int[] x) {
    if (x.length > this.motif.length) {
      for (int i = 0; i < this.motif.length; i++) {
        this.motif[i] = x[i];
      }
    }
    else {
      for (int i = 0; i < x.length; i++) {
        this.motif[i] = x[i];
      }
    }
  }

  /**
   *
   * @return int[]
   */
  public int[] getMotif() {
    return (this.motif);
  }

  /**
   *
   * @param motif int[]
   */
  public void setMotif(int[] motif) {
    this.motif = motif;
  }

  /**
   *
   * @return String
   */
  public String toString() {
    String s = new String("MODEL =");
    for (int i = 0; i < this.motif.length; i++) {
      s = s + this.motif[i] + "\t";
    }
    return (s);
  }

  public boolean equals(Object m) {
    return (m instanceof Model &&
            Arrays.equals(this.motif, ( (Model) m).getMotif()));
  }

  public static void main(String[] args) {
    int[] a = {
        681, 682};
    int[] b = {
        681, 682};
    Model m1 = new Model();
    Model m2 = new Model();
    m1.setMotif(a);
    m2.setMotif(b);
//    System.out.println(m1.equals(m2));
  }
}
