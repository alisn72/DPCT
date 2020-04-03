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
public class ModelOccurrences {

  /**
   * Valid model.
   */
  private Model model;

  /**
   * BitSet with the different genes in occurrences.
   */
  private BitSet genesInOccurrences;

  /**
   * Number of different genes in occurrences.
   */
  private int numberOfGenesInOccurrences;

  /**
   * Maximum number of errors in the model occurences (USEFUL LATER TO CREATE THE BICLUSTER WITH THIS INFO)
   */
  private int maxErrorInOcc_m;

  /**
   *
   * @param model Model
   * @param genesInOccurrences BitSet
   * @param numberOfGenesInOccurrences int
   * @param maxErrorInOcc_m int
   */
  public ModelOccurrences(Model model, BitSet genesInOccurrences,
                          int numberOfGenesInOccurrences, int maxErrorInOcc_m) {
    this.model = model;
    this.genesInOccurrences = genesInOccurrences;
    this.numberOfGenesInOccurrences = numberOfGenesInOccurrences;
    this.maxErrorInOcc_m = maxErrorInOcc_m;
  }

  /**
   * @return Model
   */
  public Model getModel() {
    return (this.model);
  }

  /**
   * @return BitSet
   */
  public BitSet getGenesInOccurrences() {
    return (this.genesInOccurrences);
  }

  /**
   * @return int
   */
  public int getNumberOfGenesInOccurrences() {
    return (this.numberOfGenesInOccurrences);
  }

  /**
   *
   * @return int
   */
  public int getMaxErrorInOcc_m() {
    return this.maxErrorInOcc_m;
  }

  /**
   *
   * @param model Model
   * @param maxErrorInOcc_m int
   */
  public void updateModelOccurrences(Model model, int maxErrorInOcc_m){
    this.model = model;
    this.maxErrorInOcc_m = this.maxErrorInOcc_m;
  }
}
