package smadeira.suffixtrees;

/**
 * <p>Title: Suffix Coordinates</p>
 *
 * <p>Description:  Holds the coordinates of this leaf can have more than one set of
 * coordinates, since this suffix can be suffix to more than one array of ints.
 * </p>
 *
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
public class SuffixCoordinates {

  /**
   * The position of the suffix associated with this leaf node
   * Note that the position is calculated relatively to the first int of the
   * first array of int added to the tree.
   */

  private int position;

  /**
   * The next coordinate object.
   */

  private SuffixCoordinates nextCoordinates;

  /**
   * @stereotype constructor
   */

  public SuffixCoordinates(int position, SuffixCoordinates nextCoordinates) {
    this.position = position;
    this.nextCoordinates = nextCoordinates;
  }

  /**
   * returns the position of the suffix
   */

  public int getPosition() {
    return position;
  }

  /**
   * returns the next coordinate object
   */

  public SuffixCoordinates getNext() {
    return nextCoordinates;
  }

  public String toString() {

    if (nextCoordinates == null) {
      return Integer.toString(position);
    }
    else {
      return position + ", " + nextCoordinates.toString();
    }
  }

}
