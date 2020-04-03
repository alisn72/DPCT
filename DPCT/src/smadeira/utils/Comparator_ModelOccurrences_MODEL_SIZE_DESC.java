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
public class Comparator_ModelOccurrences_MODEL_SIZE_DESC
    implements Comparator {

  public Comparator_ModelOccurrences_MODEL_SIZE_DESC() {
  }

  public int compare(Object o1, Object o2) {
    Integer size1 = ( (ModelOccurrences) o1).getModel().getMotif().length;
    Integer size2 = ( (ModelOccurrences) o2).getModel().getMotif().length;
    return ( -size1.compareTo(size2));
  }

  public boolean equals(Object obj) {
    return (this.equals(obj));
  }
}
