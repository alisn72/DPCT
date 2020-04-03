package smadeira.biclustering;

import java.io.*;
import java.util.*;
import java.util.Iterator;

import javax.swing.text.*;
import javax.swing.text.html.*;

import smadeira.utils.*;

/**
 * <p>Title: e-CCC Bicluster Restricted Errors</p>
 *
 * <p>Description: e-CCC Bicluster with restricted errors.</p>
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
public class E_CCC_Bicluster_RestrictedErrors
    extends E_CCC_Bicluster implements Cloneable, Serializable, Comparator,
    NodeObjectInterface {
  public static final long serialVersionUID = 2128782240169297244L;

  // CONSTRUCTORS

  /**
   *
   * @param biclusterSource E_CCC_Biclustering_RestrictedErrors
   * @param ID int
   * @param rows int[]
   * @param columns int[]
   * @param biclusterPattern char[]
   * @param maxNumberOfErrors int
   */
  public E_CCC_Bicluster_RestrictedErrors(E_CCC_Biclustering biclusterSource,
                                          int ID, int[] rows,
                                          int[] columns,
                                          char[] biclusterPattern,
                                          int maxNumberOfErrors) {
    super(biclusterSource, ID, rows, columns, biclusterPattern,
          maxNumberOfErrors);
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * Errors restricted the number of neighbors
   * used in the E_CCC_Biclustering algorithm with restricted errors lexicographically before or after a given caracter in the this.getAlphabet().
   *
   * @param pattern char[]
   * @param alphabet char[]
   * @return ArrayList
   */
  protected ArrayList<char[]> Eneighborhood(char[] pattern, char[] alphabet) {

    ArrayList<char[]> neighbors = new ArrayList<char[]> (0);
    int numNeighbors = 0;

    // 0 errors
    neighbors.add(0, pattern.clone());
    numNeighbors = 1;

    if (this.getMaxNumberOfErrors() == 0) {
      return (neighbors);
    }

    // generate all combinations of the indexes 0..s.length-1, taken numErrors at a time
    // = places where the errors will take place
    String[] elements = new String[pattern.length];
    for (int i = 0; i < pattern.length; i++) {
      elements[i] = new String(new Integer(i).toString());
    }

    for (int e = 1; e <= this.getMaxNumberOfErrors(); e++) {
      //System.out.println("####################### e = " + e);
      if (elements.length >= e) {
        CombinationGenerator x = new CombinationGenerator(elements.length, e); // C_elements.length_e

        while (x.hasMore()) {
          int[] indexes = x.getNext(); // places where the errors will take place

          /*System.out.print("Errors Indexes = ");
                     for (int i = 0; i < indexes.length; i++)
            System.out.print(indexes[i] + "\t");
                     System.out.println();*/

          // find s[indexes[0]]
          int p0 = 0;
          boolean found = false;
          while (p0 < alphabet.length && found == false) {
            if (pattern[indexes[0]] == alphabet[p0]) {
              found = true;
            }
            else {
              p0++;
            }
          }

          if (e == 1) {
            for (int i = 1;
                 i <
                 ( (IRestrictedErrors)this.biclusterSource).getNumberOfNeighborsInRestrictedErrors();
                 i++) {
              if (p0 - i > 0) {
                char[] neighbor = (char[]) pattern.clone();
                neighbor[indexes[0]] = alphabet[p0 - i];
                neighbors.add(numNeighbors, neighbor);
                //System.out.println(new String( (char[]) neighbors.get(numNeighbors)));
                numNeighbors++;
              }

              if (p0 + i < this.getAlphabet().length) {
                char[] neighbor = (char[]) pattern.clone();
                neighbor[indexes[0]] = alphabet[p0 + i];
                neighbors.add(numNeighbors, neighbor);
                //System.out.println(new String( (char[]) neighbors.get(numNeighbors)));
                numNeighbors++;
              }
            }
          }
          else {
            if (e == 2) {
              // find s[indexes[1]]
              int p1 = 0;
              boolean foundAgain = false;
              while (p1 < alphabet.length && foundAgain == false) {
                if (pattern[indexes[1]] == alphabet[p1]) {
                  foundAgain = true;
                }
                else {
                  p1++;
                }
              }

              for (int i = 1;
                   i <
                   ( (IRestrictedErrors)this.biclusterSource).getNumberOfNeighborsInRestrictedErrors();
                   i++) {
                if (p0 - i > 0 && p1 - i > 0) {
                  char[] neighbor = (char[]) pattern.clone();
                  neighbor[indexes[0]] = alphabet[p0 - i];
                  neighbor[indexes[1]] = alphabet[p1 - i];
                  neighbors.add(numNeighbors, neighbor);
                  //System.out.println(new String( (char[]) neighbors.get(numNeighbors)));
                  numNeighbors++;
                }

                if (p0 - i > 0 && p1 + i < alphabet.length) {
                  char[] neighbor = (char[]) pattern.clone();
                  neighbor[indexes[0]] = alphabet[p0 - i];
                  neighbor[indexes[1]] = alphabet[p1 + i];
                  neighbors.add(numNeighbors, neighbor);
                  //System.out.println(new String( (char[]) neighbors.get(numNeighbors)));
                  numNeighbors++;
                }

                if (p0 + i < alphabet.length && p1 - i > 0) {
                  char[] neighbor = (char[]) pattern.clone();
                  neighbor[indexes[0]] = alphabet[p0 + i];
                  neighbor[indexes[1]] = alphabet[p1 - i];
                  neighbors.add(numNeighbors, neighbor);
                  //System.out.println(new String( (char[]) neighbors.get(numNeighbors)));
                  numNeighbors++;
                }

                if (p0 + i < alphabet.length &&
                    p1 + i < alphabet.length) {
                  char[] neighbor = (char[]) pattern.clone();
                  neighbor[indexes[0]] = alphabet[p0 + i];
                  neighbor[indexes[1]] = alphabet[p1 + i];
                  neighbors.add(numNeighbors, neighbor);
                  //System.out.println(new String( (char[]) neighbors.get(numNeighbors)));
                  numNeighbors++;
                }
              }
            }
            else { // e > 2
              char[][] tempNeighbors = new char[ (int) Math.pow( ( (
                  E_CCC_Biclustering_RestrictedErrors)this.biclusterSource).
                  getNumberOfNeighborsInRestrictedErrors(), e)][pattern.length];
              for (int t = 0; t < tempNeighbors.length; t++) {
                tempNeighbors[t] = (char[]) pattern.clone();
              }
              for (int ne = 1; ne <= e; ne++) {
                int numberOfSymbolRepetitions = (int) Math.pow( ( (
                    E_CCC_Biclustering_RestrictedErrors)this.biclusterSource).
                    getNumberOfNeighborsInRestrictedErrors(), e - ne);
                int t = 0;
                // find symbol in s[indexes[ne - 1]]
                boolean symbolFound = false;
                int a = 0;
                while (a < alphabet.length && symbolFound == false) {
                  if (pattern[indexes[ne - 1]] == alphabet[a]) {
                    symbolFound = true;
                  }
                  else {
                    a++;
                  }
                }

                while (t < tempNeighbors.length) {
                  int numberOfInsertions = 0;
                  for (int i = 1;
                       i <
                       ((IRestrictedErrors)this.biclusterSource).getNumberOfNeighborsInRestrictedErrors();
                       i++) {
                    if (a - i > 0) {
                      for (int j = 0; j < numberOfSymbolRepetitions; j++) {
                        tempNeighbors[t + j][indexes[ne - 1]] = alphabet[a - i];
                        numberOfInsertions++;
                      }
                      t = t + numberOfInsertions;
                    }
                  }

                  numberOfInsertions = 0;
                  for (int i = 1;
                       i <
                       ((IRestrictedErrors)this.biclusterSource).getNumberOfNeighborsInRestrictedErrors();
                       i++) {
                    if (a + i < this.getAlphabet().length) {
                      for (int j = 0; j < numberOfSymbolRepetitions; j++) {
                        tempNeighbors[t + j][indexes[ne - 1]] = alphabet[a + i];
                        numberOfInsertions++;
                      }
                      t = t + numberOfInsertions;
                    }
                  }
                }
              }

              // remove repetitions
              TreeSet set = new TreeSet();
              for (int t = 0; t < tempNeighbors.length; t++) {
                set.add(new String(tempNeighbors[t]));
              }

              Iterator i = set.iterator();
              while (i.hasNext()) {
                neighbors.add(numNeighbors, ( (String) i.next()).toCharArray());
                //System.out.println(new String( (char[]) neighbors.get(numNeighbors)));
                numNeighbors++;
              }
            }
          }
        }
      }
      else {
        //System.out.println("PATTERN LENGTH = " + s.length + " INFERIOR TO NUMBER OF ERRORS = " + e);
        //System.out.println("CANNOT COMPUTE COMBINATIONS OF PATTERN INDEXES TAKING NUMBER OF ERRORS EACH TIME");
      }
    }
    return neighbors;
  }

//###############################################################################################################################
//###############################################################################################################################

  /**
   *
   * @param o Object
   * @return boolean
   */
  public boolean equals(Object o) {
    return (super.equals(o));
  }

  //###############################################################################################################################
  //###############################################################################################################################


  /**
   *
   * @param o1 Object
   * @param o2 Object
   * @return int
   */
  public int compare(Object o1, Object o2) {
    return super.compare(o1, o2);
  }

  /**
   *
   * @return Object
   */
  public Object clone() {
    return super.clone();
  }

  //................................................................

  /**
   *
   * TO BE IMPLEMENTED !!!!!!
   *
   * @return StyledDocument
   */
  public StyledDocument info() {

    StyledDocument doc = new DefaultStyledDocument();
    StyledDocument doc2 = new HTMLDocument();

    // ...

    return (doc);
  }

} // END CLASS
