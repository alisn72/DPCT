package smadeira.tries;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
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
public interface ITrieMaximalPatternNode {

  public ITrieMaximalPatternNode getInfo();

  public void setInfo(TrieMaximalPatternNodeInfo info);

  public TrieMaximalPatternNodeInfo add(TrieMaximalPatternNodeInfo info, int token);

  public ITrieMaximalPatternNode addRightSibling(TrieMaximalPatternNodeInfo info, int token);

  public ITrieMaximalPatternNode spellDown(int token);

  public ITrieMaximalPatternNode spellRight(int token);

  public String toString();
}
