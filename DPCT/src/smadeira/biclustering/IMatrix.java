package smadeira.biclustering;

import smadeira.ontologizer.*;

/**
 * <p>Title: Interface Matrix</p>
 *
 * <p>Description: Defines the interface for expression matrices.</p>
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
 * @version 1.0
 */
public interface IMatrix
    extends NodeObjectInterface {

  /**
   *
   * @return float[][]
   */
  public float[][] getGeneExpressionMatrix();

  /**
   *
   * @return int
   */
  public int getNumberOfGenes();

  /**
   *
   * @return int
   */
  public int getNumberOfConditions();

  /**
   *
   * @return GeneSetResult
   */
  public GeneSetResult getGeneOntologyProperties();

  /**
   *
   * @return String[]
   */
  public String[] getGenesNames();

  /**
   *
   * @return String[]
   */
  public String[] getConditionsNames();

  /**
   *
   * @return Object
   */
  public Object clone();

  /**
   *
   * @return boolean
   */
  public boolean hasMissingValues();
}
