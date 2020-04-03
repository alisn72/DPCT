package smadeira.biclustering;

/**
 * <p>Title: Interface Discretized Matrix</p>
 *
 * <p>Description: Defines the interface for discretized matrices.</p>
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
public interface IDiscretizedMatrix
    extends IMatrix {

  /**
   *
   * @return char[][]
   */
  public char[][] getSymbolicExpressionMatrix();

  /**
   *
   * @return char[]
   */
  public char[] getAlphabet();

  /**
   *
   * @return boolean
   */
  public boolean getIsVBTP();

  /**
   *
   * @return char
   */
  public char getMissingValue();
}
