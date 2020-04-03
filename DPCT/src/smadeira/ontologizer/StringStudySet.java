package smadeira.ontologizer;

/**
 * <p>Title: String Study Set </p>
 *
 * <p>Description: A study set.</p>
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
 * @version 1.0
 */
import ontologizer.*;

public class StringStudySet
    extends StudySet {

  public StringStudySet(String[] geneSet, String setName) {
        super(setName);
    /* simply add all the genes with an empty description */
        for (String gene : geneSet) {
            this.addGene(new ByteString(gene), "");
        }
    }
    public StringStudySet(String[] geneSet) {
        super();
    /* simply add all the genes with an empty description */
        for (String gene : geneSet) {
            this.addGene(new ByteString(gene), "");
        }
    }

  public StringStudySet(ByteString[] geneSet, String setName) {
    super(setName);
    /* simply add all the genes with an empty description */
    for (ByteString gene : geneSet) {
      this.addGene(gene, "");
    }
  }
}
