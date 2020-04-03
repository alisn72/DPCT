package smadeira.utils;

import java.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: Used to implement CCC Biclustering with errors</p>
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
public class ModelExtensionSet {

  /**
   * Ordered set of possible extensions (int values).
   */
  private SortedSet<Integer> extensions;

  /**
   * Default constructor.
   */
  public ModelExtensionSet() {
    this.extensions = new TreeSet();
  }

  /**
   * Constructor.
   * @param extensions SortedSet
   */
  public ModelExtensionSet(SortedSet<Integer> extensions) {
    this.extensions = new TreeSet<Integer> ();
    this.extensions = extensions;
  }

  /**
   * Constructor.
   * @param set int[]
   */
  public ModelExtensionSet(int[] set) {
    this.extensions = new TreeSet<Integer> ();
    this.addAll(set);
  }

  /**
   * Return number of possible extensions.
   * @return int
   */
  public int size() {
    return (this.extensions.size());
  }

  /**
   * Adds a possible extension (int) to the set of extensions.
   * Uses add from SortedSet.
   * @param ext int
   */
  public void add(int ext) {
    this.extensions.add(ext);
  }

  /**
   * Adds am array of possible extension (ints) to the set of extensions.
   * Uses add from SortedSet.
   *
   * @param extensions int[]
   */
  public void addAll(int[] extensions) {
    for (int i = 0; i < extensions.length; i++) {
      this.extensions.add(extensions[i]);

    }
  }

  /**
   * Checks if the set of extensions already contains a given extension (int).
   * Uses contains from SortedSet.
   * @param ext int
   * @return boolean
   */
  public boolean contains(int ext) {
    Integer o = new Integer(ext);
    return (this.extensions.contains(o));
  }

  /**
   * Checks if the set of extensions already contains a given set of extensions (ints).
   * Uses contains from SortedSet.
   *
   * @param extensions int[]
   * @return boolean
   */
  public boolean containsAll(int[] extensions) {
    for (int i = 0; i < extensions.length; i++) {
      Integer o = new Integer(extensions[i]);
      if (!this.extensions.contains(o)) {
        return (false);
      }
    }
    return (true);
  }

  /**
   * Checks if the set of extensions already contains a given set of extensions (ModelExtensionSet).
   * Uses contains from SortedSet.
   *
   * @param extSet ModelExtensionSet
   * @return boolean
   */
  public boolean containsAll(ModelExtensionSet extSet) {
    return (this.extensions.containsAll(extSet.extensions));
  }

  /**
   * Returns the first extension (lower int) in the set of extensions.
   * Uses first from SortedSet.
   * @return int
   */
  public int first() {
    return (this.extensions.first().intValue());
  }

  /**
   * Returns the last extension (higher int) in the set of extensions.
   * Uses last from SortedSet.
   *
   * @return int
   */
  public int last() {
    return (this.extensions.last().intValue());
  }

  /**
   * Removes a given extension (int) from the set of extensions (if the set contains it).
   * Uses contains and remove from SortedSet.
   *
   * @param ext int
   */
  public void remove(int ext) {
    this.extensions.remove(ext);
  }

  /**
   * Removes a given set of extensions (ints) from the set of extensions (if the set contains them).
   * Uses contains and remove from SortedSet.
   *
   * @param extArray int[]
   */
  public void removeAll(int[] extArray) {
    for (int i = 0; i < extArray.length; i++) {
      this.extensions.remove(extArray[i]);
    }
  }

  /**
   * Removes a given set of extensions (ModelExtensionSet) from the set of extensions (if the set contains them).
   * Uses contains and remove from SortedSet.
   *
   * @param extSet ModelExtensionSet
   */
  public void removeAll(ModelExtensionSet extSet) {
    this.extensions.removeAll(extSet.extensions);
  }

  /**
   * Returns an iterator over the elements in this set. The elements are returned in ascending order.
   * @return Iterator
   */
  public Iterator<Integer> iterator() {
    return (this.extensions.iterator());
  }

  public boolean equals(Object o) {
    return (o instanceof ModelExtensionSet &&
            this.extensions.equals( ( (ModelExtensionSet) o).extensions));
  }

  public String toString(){
    return this.extensions.toString();
  }

}
