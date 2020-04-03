package smadeira.utils;

import java.util.*;

import smadeira.biclustering.*;
import smadeira.ontologizer.*;

/**
 * <p>Title: Comparator with hash table for sorting biclusters by their
 *           number of significant GO Bonferroni corrected p-values
 *           in descending order.</p>
 *
 * <p>Description: This <code>Comparator</code> stores an hash table with
 *                 the number of significant GO Bonferroni corrected
 *                 p-values for each bicluster in a group of biclusters.
 *                 Allows comparison operations between two biclusters by
 *                 that metric. When passed as an argument to <code>Arrays
 *                 </code> or <code>Collections</code> <code>sort</code>
 *                 methods, this <code>Comparator</code> allows to sort
 *                 biclusters by their number of significant GO
 *                 Bonferroni corrected p-value in descending order.
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
public class Comparator_HASH_significant_GO_BonferroniCorrected_pValues_DESC
    extends AbstractComparator_HASH<Integer> {
  /**
   * Creates a new <code>Comparator</code> which stores an hash table with
   * the number of significant GO Bonferroni corrected p-values for
   * each bicluster in <code>biclustering</code>.
   *
   * @param biclustering the group of biclusters to initialize the
   *        metric table from
   *
   * @param go GOFrontEnd
   */
  public Comparator_HASH_significant_GO_BonferroniCorrected_pValues_DESC(
      Biclustering biclustering, GOFrontEnd go) {
    super(biclustering);
    metrics = new Hashtable<Integer, Integer> ();
    ArrayList<Bicluster> biclusters = biclustering.getBiclusters();

    this.biclustering.setSubtaskValues(this.biclustering.getNumberOfBiclusters());
    for (Bicluster bicluster : biclusters) {
      GeneSetResult result = bicluster.computeGeneOntologyTerms(go, false);
      metrics.put(bicluster.getID(),
                  bicluster.computeSignificant_GO_BonferroniCorrected_pValues(
          result));
      this.biclustering.incrementSubtaskPercentDone();
    }
    this.biclustering.updatePercentDone();
    this.biclustering.setSubtaskValues(this.estimatedNumberOfComparisons);
  }

  /**
   * Compares biclusters <code>o1</code> and <code>o2</code> for order.
   * Returns a negative integer, zero, or a positive integer as the
   * number of significant GO Bonferroni corrected p-values of
   * the first bicluster is greater than, equal to, or less than the
   * number of significant GO Bonferroni corrected
   * p-values of the second.
   *
   * @param o1 the first bicluster <code>Object</code> to compare
   * @param o2 the second bicluster <code>Object</code> to compare
   * @return int the comparison's result
   */
  public int compare(Object o1, Object o2) {
    this.biclustering.incrementSubtaskPercentDone();
    return ( - (metrics.get( ( (Bicluster) o1).getID())).
            compareTo(metrics.get( ( (Bicluster) o2).getID())));
  }
}
