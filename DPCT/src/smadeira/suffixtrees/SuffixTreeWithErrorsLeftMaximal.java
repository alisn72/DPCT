package smadeira.suffixtrees;

/**
 * <p>Title: Suffix Tree With Errors Left Maximal</p>
 *
 * <p>Description: Suffix tree with errors left maximal.</p>
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
public class SuffixTreeWithErrorsLeftMaximal extends SuffixTree {

  /**
   * @stereotype constructor
   *
   */
  public SuffixTreeWithErrorsLeftMaximal() {
    super();
    this.root = new InternalNodeWithErrorsLeftMaximal();
    this.tokens = new ListOfTokensOfDifferentSize();
  }
} // END CLASS
