package smadeira.biclustering;

import java.io.*;
import java.util.*;

import javax.swing.text.*;
import javax.swing.text.html.*;

import smadeira.utils.*;

/**
 * <p>Title: e-CCC Bicluster</p>
 *
 * <p>Description: e-CCC Bicluster (CCC Bicluster with errors).</p>
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
public class E_CCC_Bicluster
    extends CCC_Bicluster implements Cloneable, Serializable, Comparator,
    NodeObjectInterface {
  public static final long serialVersionUID = 8821506628656184455L;

  /**
   *
   */
  protected int maxNumberOfErrors;

  /**
   * Bicluster pattern: "perfect" pattern (no errors) used to find the e-ccc-bicluster).
   * FOR EXAMPLE:
   * <\p><p>
   * D - down regulation
   * <\p><p>
   * N - no regulation
   * <\p><p>
   * U - up regulation
   * <\p>
   */
  protected char[] biclusterExpressionPattern;

//###############################################################################################################################

  // CONSTRUCTORS

  /**
   *
   * @param biclusterSource E_CCC_Biclustering
   * @param ID int
   * @param rows int[]
   * @param columns int[] Columns of pattern with tima-lag = 0.
   * @param biclusterPattern char[] E-CCC-Biclusters expression pattern (motif). All patterns of the genes in the bicluster are in the e-neighbouhood of this motif.
   * @param maxNumberOfErrors int Maximum number of errors found in the pattern of the genes in the bicluster relatively to the bicluster pattern (motif).
   */
  public E_CCC_Bicluster(E_CCC_Biclustering biclusterSource, int ID, int[] rows,
                         int[] columns, char[] biclusterPattern,
                         int maxNumberOfErrors) {
    this.biclusterSource = biclusterSource;
    this.ID = ID;
    this.columnsIndexes = columns;
    this.rowsIndexes = rows;
    this.biclusterExpressionPattern = biclusterPattern;
    this.maxNumberOfErrors = maxNumberOfErrors;
  }

//###############################################################################################################################

  /**
   *
   * @return int
   */
  public int getMaxNumberOfErrors() {
    return (this.maxNumberOfErrors);
  }

  /**
   *
   * @return int
   */
  public int getBiclusterExpressionPatternLength() {
    return (this.biclusterExpressionPattern.length);

  }

  /**
   *
   * @return char[]
   */
  public char[] getBiclusterExpressionPattern() {
    return (this.biclusterExpressionPattern);
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @param pattern char[]
   * @param alphabet char[]
   * @return ArrayList
   */
  protected ArrayList<char[]> Eneighborhood(char[] pattern, char[] alphabet) {
    // !!!!!!
    // I BELIEVE THE NEIGHBORHOOD OF THE PATTERN SHOULD CONSIDER THE MISSING VALUE !!! CHECK !!!
    // !!!!!!
    char[] alphabetClone = alphabet.clone();
    if (this.biclusterSource instanceof IMissingValuesAllowedAsErrors){
      alphabetClone = new char[alphabet.length+1];
      for (int i=0; i < alphabet.length; i++){
        alphabetClone[i] = alphabet[i];
      }
      alphabetClone[alphabet.length] =((IMissingValuesAllowedAsErrors)this.biclusterSource).getMissingValue();
    }
    // !!!!!!

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

          if (e == 1) {
            for (int c = 0; c < alphabetClone.length; c++) {
              if (alphabetClone[c] != pattern[indexes[0]]) {
                char[] neighbor = (char[]) pattern.clone();
                neighbor[indexes[0]] = alphabetClone[c];
                neighbors.add(numNeighbors, neighbor);
                numNeighbors++;
              }
            }
          }
          else {
            char[][] tempNeighbors = new char[ (int) Math.pow(alphabetClone.length-1,e)][pattern.length];
            for (int t = 0; t < tempNeighbors.length; t++) {
              tempNeighbors[t] = (char[]) pattern.clone();
            }

            if (e == 2) {
              int c1 = 0;
              for (int t = 0; t < tempNeighbors.length;
                   t = t + alphabetClone.length - 1, c1++) {
                if (alphabetClone[c1] == pattern[indexes[0]]) {
                  c1++;
                }
                int c2 = 0;
                for (int i = 0; i < alphabetClone.length - 1; i++, c2++) {
                  tempNeighbors[t + i][indexes[0]] = alphabetClone[c1];
                  if (alphabetClone[c2] == pattern[indexes[1]]) {
                    c2++;
                  }
                  tempNeighbors[t + i][indexes[1]] = alphabetClone[c2];
                }
              }
            }
            else { // e > 2
              for (int ne = 1; ne <= e; ne++) {
                int numberOfSymbolRepetitions = (int) Math.pow(alphabetClone.length-1,e - ne);
                int t = 0;
                while (t < tempNeighbors.length) {
                  for (int a = 0; a < alphabetClone.length; a++) {
                    if (pattern[indexes[ne - 1]] != alphabetClone[a]) {
                      int numberOfInsertions = 0;
                      for (int j = 0; j < numberOfSymbolRepetitions; j++) {
                        tempNeighbors[t + j][indexes[ne-1]] = alphabetClone[a];
                        numberOfInsertions++;
                      }
                      t = t + numberOfInsertions;
                    }
                  }
                }
              }
            }

            for (int t = 0; t < tempNeighbors.length; t++) {
              neighbors.add(numNeighbors, tempNeighbors[t]);
              //System.out.println(new String(neighbors[numNeighbors]));
              numNeighbors++;
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

  /**
   * p = P (biclusterExpressionPattern)
    * FOR EXAMPLE:
    * P(U D N) = SUM OF THE PROBABILITY OF ALL PATTERN IN ITS e-NEIGHBORHOOD (#ERRORS = 1)
    * P(U D N) = P(U D N) + P(U N N) + P(U U N) + P(D D N) + P(N D N) + P(U D D) + P(U D U)
    * WHERE
    * P(U D N) = P(N) * P(U->D) * P(D->N)
    * ...
    * P(U D U) = P(U) * P(U->D) * P(D->U)
    *
    * @param markovChainOrder int
    * @return double
    */
   protected double compute_BiclusterPattern_Probability(int markovChainOrder) {
     char[][] symbolicMatrix = ( (E_CCC_Biclustering)this.biclusterSource).getSymbolicExpressionMatrix();
     ArrayList<char[]> ENeighborhood = this.Eneighborhood(this.biclusterExpressionPattern, this.getAlphabet());
     double p = 0;
     for (int i = 0; i < ENeighborhood.size(); i++) {
       char[] neighbour = ENeighborhood.get(i);
       p = p +
           this.compute_Pattern_Probability(symbolicMatrix,
                                            neighbour, markovChainOrder);
     }
     return p;
   }


//###############################################################################################################################
//###############################################################################################################################

  /**
   * p = P (biclusterExpressionPattern)
   * FOR EXAMPLE:
   * IF biclusterExpressionPattern = N U U D U and #Errors = 2
   * P(U1 D2 N3) = SUM OF THE PROBABILITY OF ALL PATTERN IN ITS e-NEIGHBORHOOD
   * P(U1 D2 N3) = P(U1 D2 N3) + P(U1 N2 N3) + P(U1 U2 N3) + P(D1 D2 N3) + P(N1 D2 N3) + P(U1 D2 D3) + P(U1 D2 U3)
   * WHERE
   * P(U1 D2 N3) = P(U1) * P(U1->D2) * P(D2->N3)
   * ...
   * P(U D U) = P(U1) * P(U1->D2) * P(D2->U3)
   *
   * @param markovChainOrder int
   * @return double
   */
  protected double compute_BiclusterPatternWithColumns_Probability(int markovChainOrder) {
      char[][] symbolicMatrix = ( (E_CCC_Biclustering)this.biclusterSource).getSymbolicExpressionMatrix();
      ArrayList<char[]> ENeighborhood = this.Eneighborhood(this.biclusterExpressionPattern, this.getAlphabet());
      double p = 0;
      for (int i = 0; i < ENeighborhood.size(); i++) {
        char[] neighbour = ENeighborhood.get(i);
        p = p +
            this.compute_PatternWithColumns_Probability(symbolicMatrix,
            neighbour, this.columnsIndexes[0] - 1, markovChainOrder);
      }
      return p;
  }

//###############################################################################################################################
//###############################################################################################################################

  /**
   *
   * @param o Object
   * @return boolean
   */
  public boolean equals(Object o) {
    if (this == null || o == null || ! (o instanceof E_CCC_Bicluster)) {
      return false;
    }

    return (super.equals(this));
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
    return ( ( (Integer) ( (E_CCC_Bicluster) o1).ID).compareTo( ( (Integer) ( (
        E_CCC_Bicluster) o2).ID)));
  }

  /**
   *
   * @return Object
   */
  public Object clone() {
    E_CCC_Bicluster o = (E_CCC_Bicluster)super.clone();
    o.biclusterExpressionPattern = this.biclusterExpressionPattern.clone();
    return o;
  }

  /**
   *
   * @return String
   */
  public String toString() {
    String s = new String();
    s = s + "#BICLUSTER_" + this.getID() + "\t" +
        "CONDITIONS= " + this.getNumberOfConditions() + "\t" +
        "FIRST-LAST= " + this.columnsIndexes[0] + "-" +
        this.columnsIndexes[this.getNumberOfConditions() - 1] +
        "\t" +
        "GENES= " + this.getNumberOfGenes() + "\t" +
        "PATTERN=" + new String(this.biclusterExpressionPattern) + "\t" +
        "MAX ERRORS PER GENE = " + this.maxNumberOfErrors + "\n";

    char[][] biclusterSymbolicMatrix = this.getBiclusterSymbolicMatrix();

    for (int g = 0; g < this.getNumberOfGenes(); g++) {
      s = s + this.getGenesNames()[g] + "\t" + new String(biclusterSymbolicMatrix[g]) + "\n";
    }
    return s;
  }

  /**
    *
    * @return String
    */
   public String summaryToString() {
     String s = new String();
     s = s + "#BICLUSTER_" + this.getID() + "\t" +
         "CONDITIONS= " + this.getNumberOfConditions() + "\t" +
         "FIRST-LAST= " + this.columnsIndexes[0] + "-" +
         this.columnsIndexes[this.getNumberOfConditions() - 1] +
         "\t" +
         "GENES= " + this.getNumberOfGenes() + "\t" +
         "PATTERN=" + new String(this.biclusterExpressionPattern) + "\t" +
         "MAX ERRORS PER GENE = " + this.maxNumberOfErrors + "\n";
     return s;
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
