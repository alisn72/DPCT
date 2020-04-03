package smadeira.suffixtrees;

/**
 * <p>Title: Node Interface</p>
 *
 * <p>Description: Interface of a node of a suffix tree.</p>
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

public interface NodeInterface {

  /**
   * @return the length on the branch leading to this node.
   */
  int getLeftIndex();

  /**
   * @return the length of the branch leading to this node.
   */
  int getLength();

  /**
   * @return the right sybling of this node, if there is one.
   */
  NodeInterface getRightSybling();

  /**
   * @return int The number leaves of the node. (Zero in case of leaf nodes)
   */
  int getNumberOfLeaves();

  /**
   * @return int The number of symbols from the root to the node.
   */
  int getPathLength();

  /**
   *
   * @return String
   */
  String toString();
}
