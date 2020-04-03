package smadeira.utils;

import java.util.*;
import smadeira.biclustering.Biclustering;
import javax.swing.JProgressBar;

/**
 * <p>Title: Abstract Comparator for Biclusters</p>
 *
 * <p>Description: Allows comparison operations between biclusters of a
 *                 group of biclusters by specific metrics. The metric
 *                 values for each bicluster of the group are stored in
 *                 a hash table</p>, in which the key is the bicluster ID.
 *                 When comparing two biclusters, their metric values are
 *                 retrieved from the hash table and compared to produce
 *                 the comparison result.
 *
 * <p>Copyright:   Copyright (C) 2007  Joana Gonçalves, Sara Madeira
 *                 This program is free software; you can redistribute
 *                 it and/or modify it under the terms of the GNU General
 *                 Public License as published by the Free Software
 *                 Foundation; either version 2 of the License, or
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
 * @author Joana P. Gonçalves
 * @author Sara C. Madeira
 * @version 1.0
 */
public abstract class AbstractComparator_HASH<Number>
    implements Comparator {
  /**
   * The hash table which contains the metric values for each
   * bicluster of a biclusters' group. The hash key is the bicluster
   * ID and the value is the value of a specific metric for that
   * bicluster.
   */
  protected Hashtable<Integer, Number> metrics;
  protected Biclustering biclustering;
  protected int estimatedNumberOfComparisons;

  public AbstractComparator_HASH() {

  }

  public AbstractComparator_HASH(Biclustering biclustering) {
    this.biclustering = biclustering;
    estimatedNumberOfComparisons = (int)
        (Math.log(fact(biclustering.getNumberOfBiclusters())) * 1.0
         / Math.log(2));
  }

  /**
   * Compares biclusters <code>o1</code> and <code>o2</code> for order.
   * Returns a negative integer, zero, or a positive integer as the
   * metric of the first bicluster is less than, equal to, or greater
   * than the metric of the second.
   *
   * @param o1 the first bicluster <code>Object</code> to compare
   * @param o2 the second bicluster <code>Object</code> to compare
   * @return int the comparison's result
   */
  public abstract int compare(Object o1, Object o2);

  /**
   * Indicates whether some other object is "equal to" this Comparator.
   *
   * @param obj the <code>Object</code> to check equality
   * @return boolean the result of the equality test
   */
  public boolean equals(Object obj) {
    return (this.equals(obj));
  }

  /**
   * Returns the hash table with the bicluster IDs and their
   * corresponding values for a specific metric.
   *
   * @return Hashtable with bicluster IDs and their values for a
   *         specific metric
   */
  public Hashtable<Integer, Number> getMetrics() {
    return metrics;
  }

  /**
   * Sets the hash table with bicluster IDs and their corresponding
   * values for a specific metric.
   *
   * @param metrics the new <code>Hashtable</code>
   */
  public void setHashtable(Hashtable<Integer, Number> metrics) {
    this.metrics = metrics;
  }

  public static int fact(int n) {
    // Base Case:
    //    If n <= 1 then n! = 1.
    if (n <= 1) {
      return 1;
    }
    // Recursive Case:
    //    If n > 1 then n! = n * (n-1)!
    else {
      return n * fact(n-1);
    }
  }
}
