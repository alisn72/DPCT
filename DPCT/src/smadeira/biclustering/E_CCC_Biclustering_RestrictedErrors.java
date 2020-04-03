package smadeira.biclustering;

import java.io.*;
import java.util.*;

import smadeira.suffixtrees.*;
import smadeira.utils.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;

public class E_CCC_Biclustering_RestrictedErrors
    extends E_CCC_Biclustering
    implements IRestrictedErrors, Cloneable, Serializable {
  public static final long serialVersionUID = -6043056360623678576L;

  /**
   *
   */
  private int numberOfNeighborsInRestrictedErrors;

//######################################################################################################################
//######################################################################################################################

  /**
   *
   * @param m DiscretizedExpressionMatrix
   * @param numberOfErrors int
   * @param numberOfNeighborsInRestrictedErrors int
   * @throws Exception
   */
  public E_CCC_Biclustering_RestrictedErrors(DiscretizedExpressionMatrix m,
                                             int numberOfErrors,
      int numberOfNeighborsInRestrictedErrors) throws Exception {
    super(m, numberOfErrors);
    this.numberOfNeighborsInRestrictedErrors =
        numberOfNeighborsInRestrictedErrors;
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   *
   * @return int
   */
  public int getNumberOfNeighborsInRestrictedErrors() {
    return this.numberOfNeighborsInRestrictedErrors;
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   * Used when restrictErrors = true to check if a character is a valid error to another given character alpha.
   * Only the this.numberOfNeighbors characters lexicographically before and after the given character in the alphabet are valid errors.
   * @param alpha char
   * @param character char
   * @return boolean
   */
  protected boolean testIfCharacterIsValidError(char alpha, char character) {

    if (alpha == character) {
      return true;
    }

    // find alpha in the alphabet
    int p = 0;
    boolean alphaFound = false;
    while (p < this.matrix.getAlphabet().length && alphaFound == false) {
      if (this.matrix.getAlphabet()[p] == alpha) {
        alphaFound = true;
      }
      else {
        p++;
      }
    }

    if (alphaFound == true) {
      int i = 1;
      // CHECK AT LEFT
      while (p - i > 0 && i <= this.numberOfNeighborsInRestrictedErrors) {
        if (this.matrix.getAlphabet()[p - i] == character) {
          return true;
        }
        i++;
      }
      // CHECK AT RIGHT
      i = 1;
      while (p + i < this.matrix.getAlphabet().length &&
             i <= this.numberOfNeighborsInRestrictedErrors) {
        if (this.matrix.getAlphabet()[p + i] == character) {
          return true;
        }
        i++;
      }
    }
    return false;
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   *
   * @param suffixTreeRightMaximalBiclusters SuffixTreeWithErrorsRightMaximal
   * @param occurrence NodeOccurrence
   * @param branchToExamine NodeInterface
   * @param alpha int
   * @return boolean
   */
  protected boolean extendFromBranchWithErrorsIsPossible(SuffixTreeWithErrorsRightMaximal suffixTreeRightMaximalBiclusters,
      NodeOccurrence occurrence, NodeInterface branchToExamine, int alpha){

        int leftIndex = branchToExamine.getLeftIndex(); // left index of branch b between x and x'
        int firstCharacter = suffixTreeRightMaximalBiclusters.getSubArrayInt(leftIndex, 1)[0];

        // when occurrence is the root
        //test if character is at level 1 => ?1
        boolean testExtension = true;
        // test if character is a string terminator
        boolean characterIsAterminator = false;
        if (occurrence.getX() instanceof
            InternalNodeWithErrorsRightMaximal) {
          if (firstCharacter < 0) {
            characterIsAterminator = true;
          }
          else {
            if (occurrence.getX().getPathLength() == 0) { // X = root
              int columnNumberFirstCharacter = Integer.parseInt(
                  Integer.toString(firstCharacter).substring(2));
              int columnNumberAlpha = Integer.parseInt(Integer.
                  toString(alpha).substring(2));
              if (columnNumberFirstCharacter != columnNumberAlpha) {
                testExtension = false;
              }
            }
          }
        }

        //____________________________
        // RESTRICT ERRORS
        boolean firstCharacterIsValidError = true;
        // WHEN restrictErrors = false ALL CHARACTERS CAN BE ERRORS
        // WHEN restrictErrors = true ONLY THE ERRORS LEXICOGRAPHICALLY BEFORE AND AFTER ALPHA
        //IN THE ALPHABET ARE ALLOWED
        if (firstCharacter < 0) { // terminator
          firstCharacterIsValidError = false;
        }
        else {
          char alphaAsChar = (char) Integer.parseInt(new Integer(alpha).toString().substring(0, 2));
          char firstCharacterAsChar =
              (char) Integer.parseInt(new Integer(firstCharacter).toString().substring(0, 2));
          firstCharacterIsValidError = this.testIfCharacterIsValidError(alphaAsChar,
                                          firstCharacterAsChar);
        }
        //____________________________

        // branch b leaving node x except the one labelled alpha if it exists
        return (firstCharacter != alpha && testExtension == true &&
            characterIsAterminator == false &&
            firstCharacterIsValidError == true);
  }

  //######################################################################################################################
  //######################################################################################################################

  /**
   *
   * @param suffixTreeRightMaximalBiclusters SuffixTreeWithErrorsRightMaximal
   * @param occurrence NodeOccurrence
   * @param branchToExamine NodeInterface
   * @param alpha int
   * @return boolean
   */
  protected boolean extendFromNodeWithErrorsIsPossible(SuffixTreeWithErrorsRightMaximal suffixTreeRightMaximalBiclusters,
      NodeOccurrence occurrence, NodeInterface branchToExamine, int alpha){

      int leftIndex = branchToExamine.getLeftIndex(); // left index of branch b between x and x'
      int characterAtPositionPplusOne = suffixTreeRightMaximalBiclusters.getSubArrayInt(leftIndex, occurrence.getP() + 1)[occurrence.getP()];

      if (characterAtPositionPplusOne != alpha) {
        //##########################################################################
        //IF the extension will reach another node x'
        //branchToExamine.getLength() == occurrence.getP() + 1
        //##########################################################################

        //____________________________
        // RESTRICT ERRORS
        boolean characterAtPositionPplusOneIsValidError = true;

        // WHEN restrictErrors = false ALL CHARACTERS CAN BE ERRORS
        // WHEN restrictErrors = true ONLY THE ERRORS LEXICOGRAPHICALLY IN THE
        // numberOfNeighborsInRestrictErrors BEFORE AND AFTER ALPHA
        //IN THE ALPHABET ARE ALLLOWED
        if (characterAtPositionPplusOne < 0) { // terminator
          characterAtPositionPplusOneIsValidError = false;
        }
        else {
          char alphaAsChar = (char) Integer.parseInt(new Integer(
              alpha).toString().substring(0, 2));
          char characterAtPositionPplusOneAsChar =
              (char) Integer.parseInt(new Integer(characterAtPositionPplusOne).toString().substring(0, 2));
          characterAtPositionPplusOneIsValidError = this.testIfCharacterIsValidError(alphaAsChar,characterAtPositionPplusOneAsChar);
        }
        return (characterAtPositionPplusOneIsValidError == true);
      }
      else{
        return false;
      }
    }

  //#########################################################################################################
  //#########################################################################################################

  /**
   * Create an object of E_CCC_Bicluster_RestrictedErrors and stores it in this.setOfBiclusters.
   *
   * @param ID int
   * @param rows int[]
   * @param columns int[]
   * @param biclusterPattern char[]
   * @param maxNumberOfErrors int
   */
  protected void store_E_CCC_Bicluster(int ID, int[] rows,
                                       int[] columns, char[] biclusterPattern,
                                       int maxNumberOfErrors) {
    this.setOfBiclusters.add(new E_CCC_Bicluster_RestrictedErrors(
        this, ID, rows,
        columns, biclusterPattern, maxNumberOfErrors));
  }

  //#####################################################################################################################
  //#####################################################################################################################

  /**
   *
   * @return Object
   */
  public Object clone() {
    E_CCC_Biclustering_RestrictedErrors o = (E_CCC_Biclustering_RestrictedErrors)super.clone();
    o.numberOfNeighborsInRestrictedErrors = this.numberOfNeighborsInRestrictedErrors;
    return o;
  }

  //#####################################################################################################################
  //#####################################################################################################################

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
}
