package smadeira.utils;

import java.util.*;

import smadeira.biclustering.*;

/**
 * <p>Title: Comparator with hash table for sorting biclusters by their
 *           bicluster pattern p-value in ascending order.</p>
 *
 * <p>Description: This <code>Comparator</code> stores an hash table with
 *                 the bicluster pattern p-value for each bicluster
 *                 in a group of biclusters. Allows comparison operations
 *                 between two biclusters by that metric. When passed as an
 *                 argument to <code>Arrays</code> or <code>Collections</code>
 *                 <code>sort</code> methods, this <code>Comparator</code>
 *                 allows to sort biclusters by their pattern p-value in
 *                 ascending order.
 * </p>
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
public class Comparator_HASH_biclusterPattern_pValue_ASC
    extends AbstractComparator_HASH<Double> {
  /**
   * Creates a new <code>Comparator</code> which stores an hash table with
   * the pattern p-value for each bicluster in <code>biclustering</code>.
   *
   * @param biclustering the group of biclusters to initialize the
   *        metric table from
   *
   * @param markovChainOrder int
   */
  public Comparator_HASH_biclusterPattern_pValue_ASC(
      BiclusteringInDiscretizedMatrix biclustering, int markovChainOrder) {
    super(biclustering);
    metrics = new Hashtable<Integer, Double> ();
    ArrayList<Bicluster> biclusters = biclustering.getBiclusters();

    this.biclustering.setSubtaskValues(this.biclustering.getNumberOfBiclusters());
    for (Bicluster bicluster : biclusters) {
      metrics.put(bicluster.getID(),
                  ( (CCC_Bicluster) bicluster).getBiclusterPattern_pValue(
          markovChainOrder));
      this.biclustering.incrementSubtaskPercentDone();
    }
    this.biclustering.updatePercentDone();
    this.biclustering.setSubtaskValues(this.estimatedNumberOfComparisons);
  }

  /**
   * Compares biclusters <code>o1</code> and <code>o2</code> for order.
   * Returns a negative integer, zero, or a positive integer as the
   * bicluster pattern p-value of the first bicluster is
   * less than, equal to, or greater than the bicluster pattern p-value
   * of the second.
   *
   * @param o1 the first bicluster <code>Object</code> to compare
   * @param o2 the second bicluster <code>Object</code> to compare
   * @return int the comparison's result
   */
  public int compare(Object o1, Object o2) {
    this.biclustering.incrementSubtaskPercentDone();
    return ( (metrics.get( ( (Bicluster) o1).getID())).
            compareTo(metrics.get( ( (Bicluster) o2).getID())));
  }
}
