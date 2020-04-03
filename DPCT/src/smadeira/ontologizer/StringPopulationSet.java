package smadeira.ontologizer;

import ontologizer.*;

/**
 * <p>Title: String Population Set </p>
 *
 * <p>Description: A population set.</p>
 *
 * <p>Copyright:   Copyright (C) 2007  Joana P. Gon�alves, Sara C. Madeira
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
 * @author Joana P. Gon�alves
 *
 * @version 1.0
 */
public class StringPopulationSet
    extends PopulationSet {

  public StringPopulationSet(String[] population, String populationName) {
    super(populationName);
    /* simply add all the genes with an empty description */
    for (String gene : population) {
      this.addGene(new ByteString(gene), "");
    }
  }

  public StringPopulationSet(ByteString[] population, String populationName) {
    super(populationName);
    /* simply add all the genes with an empty description */
    for (ByteString gene : population) {
      this.addGene(gene, "");
    }
  }
}
