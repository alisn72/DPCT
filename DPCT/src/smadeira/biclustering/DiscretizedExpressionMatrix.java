package smadeira.biclustering;

import java.io.*;
import java.util.*;

import javax.swing.text.*;
import javax.swing.text.html.*;

import smadeira.ontologizer.*;
import smadeira.utils.*;

/**
 * <p>Title: Discretized Expression Matrix</p>
 *
 * <p>Description: Discretized Expression Matrix.</p>
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
public class DiscretizedExpressionMatrix
    implements IDiscretizedMatrix, Cloneable, Serializable, NodeObjectInterface {
  public static final long serialVersionUID = 55206701787598732L;

  // FIELDS

  /**
   * Gene expression matrix from which this discretized expression matrix was created
   * (can either be an object from two classes: ExpressionMatrix and PreProcessedExpressionMatrix ).
   */
  private AbstractExpressionMatrix originalExpressionMatrix;

  /**
   * Alphabet used to discretize the expression matrix in lexicographic order (WHEN GENESHIFTING = FALSE).
   * Example:
   * {D,N,U} - {down-regulation, no-change, up-regulation}
   * {N,R} - {no-change, regulation (up/down)}
   * {0,1} - {no-change, regulation (up/down)}
   *
   **/
  private char[] alphabet;

  /**
   * Discretized gene expression matrix: real valued matrix with N rows (genes)
   * and M columns (conditions) (NxM).
   */
  private char[][] symbolicExpressionMatrix;

  /**
   * True if the discretization used is performed using variations between time points (VBTP)
   * => #columns in symbolicExpressionMatrix = # #columns in originalExpressionMatrix - 1
   * (false by default).
   */
  private boolean isVBTP;

  // #####################################################################################################################
  // CONSTRUCTORS
  // #####################################################################################################################

  /**
   *
   * @param originalExpressionMatrix AbstractExpressionMatrix
   */
  public DiscretizedExpressionMatrix(AbstractExpressionMatrix
                                     originalExpressionMatrix) {
    this.originalExpressionMatrix = originalExpressionMatrix;
    this.isVBTP = false;
  }

  // #####################################################################################################################

  /**
   *
   * @param originalExpressionMatrix AbstractExpressionMatrix
   * @param symbolicMatrix char[][]
   * @param alphabet char[]
   * @param isVBTP boolean
   * @throws Exception
   */
  public DiscretizedExpressionMatrix(AbstractExpressionMatrix
                                     originalExpressionMatrix,
                                     char[][] symbolicMatrix, char[] alphabet,
                                     boolean isVBTP) throws Exception {
    this(originalExpressionMatrix);
    this.symbolicExpressionMatrix = symbolicMatrix;
    this.isVBTP = isVBTP;
    this.alphabet = alphabet;
    if (symbolicMatrix.length != this.getNumberOfGenes()
        ||
        (symbolicMatrix[0].length !=
         originalExpressionMatrix.getNumberOfConditions() && !isVBTP)
        ||
        (symbolicMatrix[0].length !=
         originalExpressionMatrix.getNumberOfConditions() - 1 && isVBTP)) {
      throw new Exception("Wrong matrices' sizes");
    }

    // CONVERT ALPHABET AND SYMBOLIC MATRIX TO UPPER-CASE (IN THE ALGORITHMS I ASSUME THAT ASCII CODE IS ONLY TWO SYMBOLS
    // AND LOWER CASE CHARACTERS HAVE THREE)
    for (int i = 0; i < alphabet.length; i++) {
      alphabet[i] = Character.toUpperCase(alphabet[i]);
    }
    for (int i = 0; i < symbolicMatrix.length; i++) {
      for (int j = 0; j < symbolicMatrix[i].length; j++) {
        symbolicMatrix[i][j] = Character.toUpperCase(symbolicMatrix[i][j]);
      }
    }
  }

  // #####################################################################################################################
  // #####################################################################################################################

  /**
   *
   * @return boolean
   */
  public boolean hasMissingValues() {
    return this.originalExpressionMatrix.hasMissingValues();
  }

  // #####################################################################################################################

  /**
   *
   * @return char
   */
  public char getMissingValue() {
    char missing = '_';
    return missing;
  }

  // #####################################################################################################################

  /**
   *
   * @return boolean
   */
  public boolean getGoTermsComputed() {
    return this.originalExpressionMatrix.getGoTermsComputed();
  }

  // #####################################################################################################################

  /**
   *
   * @return GeneSetResult
   */
  public GeneSetResult getGeneOntologyProperties() {
    return this.originalExpressionMatrix.getGeneOntologyProperties();
  }

  // #####################################################################################################################

  /**
   *
   * @return int
   */
  public int getNumberOfGenes() {
    return (this.originalExpressionMatrix.getNumberOfGenes());
  }

  // #####################################################################################################################

  /**
   *
   * @return int
   */
  public int getNumberOfConditions() {
    return (this.symbolicExpressionMatrix[0].length);
//    return(this.originalExpressionMatrix.getNumberOfConditions());
  }

  // #####################################################################################################################

  /**
   *
   * @return String[]
   */
  public String[] getGenesNames() {
    return (this.originalExpressionMatrix.getGenesNames());
  }

  // #####################################################################################################################

  /**
   *
   * @return String[]
   */
  public String[] getConditionsNames() {
    int numberOfConditions = this.originalExpressionMatrix.getConditionsNames().
        length;
    if (this.isVBTP) {
      String[] conditionNames = new String[numberOfConditions - 1];
      for (int i = 0; i < numberOfConditions - 1; i++) {
        conditionNames[i] = new String("TP" + (i + 1) + "_TP" + (i + 2));
      }
      return (conditionNames);
    }
    else {
      return (this.originalExpressionMatrix.getConditionsNames());
    }
  }

  // #####################################################################################################################

  /**
   *
   * @return char[][]
   */
  public char[][] getSymbolicExpressionMatrix() {
    return (this.symbolicExpressionMatrix);
  }

  /**
   *
   * @return AbstractExpressionMatrix
   */
  public AbstractExpressionMatrix getOriginalExpressionMatrix() {
    return (this.originalExpressionMatrix);
  }

  // #####################################################################################################################

  /**
   *
   * @return float[][]
   */
  public float[][] getGeneExpressionMatrix() {
    return (this.originalExpressionMatrix.getGeneExpressionMatrix());
  }

  // #####################################################################################################################

  /**
   *
   * @return char[]
   */
  public char[] getAlphabet() {
    return (this.alphabet);
  }

  // #####################################################################################################################

  /**
   *
   * @return boolean
   */
  public boolean getIsVBTP() {
    return (this.isVBTP);
  }

  // #####################################################################################################################

  /**
   *
   * @param vbtp boolean
   */
  public void setIsVBTP(boolean vbtp) {
    this.isVBTP = vbtp;
  }

  // #####################################################################################################################

  /**
   *
   * @return String
   */
  public String getGeneIdentifier() {
    if (this.originalExpressionMatrix instanceof ExpressionMatrix) {
      return ( ( (ExpressionMatrix)this.originalExpressionMatrix).
              getGeneIdentifier());
    }
    else {
      if (this.originalExpressionMatrix instanceof PreProcessedExpressionMatrix) {
        return ( ( (PreProcessedExpressionMatrix)this.originalExpressionMatrix).
                getGeneIdentifier());
      }
      else {
        return null;
      }
    }
  }

  // #####################################################################################################################

  /**
   *
   * @return String
   */
  public String getDataType() {
    if (this.originalExpressionMatrix instanceof ExpressionMatrix) {
      return ( ( (ExpressionMatrix)this.originalExpressionMatrix).getDataType());
    }
    else {
      if (this.originalExpressionMatrix instanceof PreProcessedExpressionMatrix) {
        return ( ( (PreProcessedExpressionMatrix)this.originalExpressionMatrix).
                getDataType());
      }
      else {
        return null;
      }
    }
  }

  // #####################################################################################################################

  public void setAlphabet(char[] a) {
    this.alphabet = a;
  }

  // #####################################################################################################################

  /**
   * Writes the symbolic expression matrix to a .txt file. The values in the expression matrix are separated
   * by one of the following characters: '\t' (tab), ';' ',' or ' '.
   *
   * @param fileName String Name of the file to whom the gene expression matrix will be written.
   *
   * @param separator Character used to separate the values in the symbolic matrix.
   *
   * @throws Exception
   */
  public void writeSymbolicMatrixToFileTXT(String fileName, char separator) throws
      Exception {

    // EXCEPTION!!! separator can only be '\t', ';' ',' or ' '
    // the file type is .txt

    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
        fileName)));

    // First line = condition name
    if (this.originalExpressionMatrix.conditionsNames != null) {
      out.print(separator);
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        out.print(this.originalExpressionMatrix.conditionsNames[j] + separator);
      }
      out.println();
    }

    if (this.symbolicExpressionMatrix != null &&
        this.originalExpressionMatrix.genesNames != null) {

      for (int i = 0; i < this.symbolicExpressionMatrix.length; i++) {
        out.print(this.originalExpressionMatrix.genesNames[i] + separator);
        for (int j = 0; j < this.symbolicExpressionMatrix[0].length; j++) {
          Character c = new Character(this.symbolicExpressionMatrix[i][j]);
          out.print(c.toString() + separator);
        }
        out.println();
      }
    }
    out.close();
  }

  // #####################################################################################################################

  /**
   * Writes the symbolic expression matrix to a .txt file. The values in the expression matrix are separated
   * by one of the following characters: '\t' (tab), ';' ',' or ' '.
   *
   * @param fileName String Name of the file to whom the gene expression matrix will be written.
   *
   * @param separator Character used to separate the values in the symbolic matrix.
   *
   * @param symbolicMatrix char[][] Symbolic matrix.
   *
   * @throws IOException
   */
  public static void writeSymbolicMatrixToFileTXT(char[][] symbolicMatrix,
                                                  char separator,
                                                  String fileName) throws
      IOException {

    // EXCEPTION!!! separator can only be '\t', ';' ',' or ' '
    // the file type is .txt

    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
        fileName)));

    int numberOfConditions = symbolicMatrix[0].length;
    int numberOfGenes = symbolicMatrix.length;

    for (int i = 0; i < numberOfGenes; i++) {
      for (int j = 0; j < numberOfConditions; j++) {
        Character c = new Character(symbolicMatrix[i][j]);
        out.print(c.toString() + separator);
      }
      out.println();
    }
    out.close();
  }

  // #####################################################################################################################

  /**
   *
   * @param symbolicMatrix char[][]
   * @param separator char
   */
  public static void writeSymbolicMatrix(char[][] symbolicMatrix,
                                         char separator) {

    for (int i = 0; i < symbolicMatrix.length; i++) {
      for (int j = 0; j < symbolicMatrix[i].length; j++) {
        Character c = new Character(symbolicMatrix[i][j]);
        System.out.print(c.toString() + separator);
      }
      System.out.println();
    }
  }

  //###############################################################################################################################

  /**
   *
   * @return Object
   */
  public Object clone() {
    DiscretizedExpressionMatrix o = null;
    try {
      o = (DiscretizedExpressionMatrix)super.clone();
    }
    catch (CloneNotSupportedException e) {
      System.err.println("AbstractExpressionMatrix can't clone");
    }
    o.alphabet = this.alphabet;
    o.isVBTP = this.isVBTP;
    o.symbolicExpressionMatrix = this.symbolicExpressionMatrix.clone();
    return (o);
  }

  //###############################################################################################################################

  /**
   * Read ExpressionMatrix from a gene expression matrix in a txt file.
   * First line = condition names.
   * First column of each line (except the first) = gene name.
   *
   * @param file String
   * @param separator char
   *
   * @throws Exception
   */
  public void readSymbolicMatrixFromFileTXT(String file, char separator) throws
      Exception {

    // EXCEPTION!!!
    // 1) separator can only be '\t', ';' ',' or ' '
    // 2) the file type is .txt

    if (separator != '\t' && separator != ' ' && separator != ';') {
      throw new Exception(
          "INVALID SEPARATOR: separator can only be '\t', ';' ',' or ' '");
    }

    if (!file.toLowerCase().endsWith(".txt")) {
      throw new Exception("INVALID FILE TYPE: file type can only be '.txt'");
    }

    // FileNotFoundException
    BufferedReader br = new BufferedReader(new FileReader(file));

    // read first line = condition names
    String line = br.readLine();

    // store condition names
    StringTokenizer st = new StringTokenizer(line,
                                             new Character(separator).toString());
    int numberOfConditions = st.countTokens() - 1;

    // EXCEPTION!!
    if (numberOfConditions == 0) { //separator != file separator
      throw new Exception("INVALID SEPARATOR: separator found in the file is different from the one passed as parameter");
    }

    this.originalExpressionMatrix.conditionsNames = new String[
        numberOfConditions];
    st.nextToken(); // skip string on the first column
    for (int j = 0; j < numberOfConditions; j++) {
      this.originalExpressionMatrix.conditionsNames[j] = st.nextToken();
    }

    int numberOfGenes = 0;
    // count number of genes
    while ( (line = br.readLine()) != null) {
      numberOfGenes++;
    }

    br.close();

    // re-open file
    br = new BufferedReader(new FileReader(file));

    // in each line: read name and expression values
    this.originalExpressionMatrix.genesNames = new String[numberOfGenes];
    this.originalExpressionMatrix.geneExpressionMatrix = new float[
        numberOfGenes][numberOfConditions];
    this.symbolicExpressionMatrix = new char[numberOfGenes][numberOfConditions];

    // skip first line = condition names
    line = br.readLine();

    this.originalExpressionMatrix.hasMissingValues = false;
    this.originalExpressionMatrix.missingValue = Integer.MIN_VALUE;

    for (int i = 0; i < numberOfGenes; i++) {
      line = br.readLine();
      st = new StringTokenizer(line, new Character(separator).toString());
      // store gene name = first column
      this.originalExpressionMatrix.genesNames[i] = st.nextToken();
      for (int j = 0; j < numberOfConditions; j++) {
        this.symbolicExpressionMatrix[i][j] = st.nextToken().charAt(0);
      }
    }
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
    /*if (this.originalExpressionMatrix instanceof ExpressionMatrix){
      ...
         }
         else{
     if (this.originalExpressionMatrix instanceof PreProcessedExpressionMatrix){
          ...
      }
      else{
        return null;
      }
     */
    return (doc);
  }

  // #####################################################################################################################
  // ############################################ DISCRETIZATION THECNIQUES ##############################################
  // #####################################################################################################################

  // #######################################################################################################################
  // ################################################### TWO SYMBOLS #######################################################
  // #######################################################################################################################

  //################## MEAN (OVERALL MATRIX / BY ROW / BY COLUMNS)

  /**
   * Discretizes the expression matrix using mean(overall matrix).
   *
   * @param symbolExpression char
   * @param symbolNoExpression char
   *
   * @throws Exception
   */
  public void computeDiscretizedMatrix_Mean_OverallMatrix(char
      symbolNoExpression, char symbolExpression) throws Exception {

    if (symbolNoExpression >= symbolExpression) {
      throw new Exception(
          "INVALID ALPHABET: symbolNoExpression < symbolExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[2];

    alphabet[0] = symbolNoExpression;
    alphabet[1] = symbolExpression;

    float a_IJ = 0; // average value in the matrix

    float sum_a_ij = 0;
    int numberOfNonMissings = 0;
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          sum_a_ij = sum_a_ij +
              this.originalExpressionMatrix.geneExpressionMatrix[i][j];
          numberOfNonMissings++;
        }
      }
    }

    if (numberOfNonMissings != 0) {
      a_IJ = sum_a_ij / numberOfNonMissings;
    }
    else {
      a_IJ = this.originalExpressionMatrix.getMissingValue();
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] > a_IJ) {
            symbolicMatrix[i][j] = symbolExpression;
          }
          else {
            symbolicMatrix[i][j] = symbolNoExpression;
          }
        }
      }
    }

    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  // #######################################################################################################################

  /**
   * Discretizes the expression matrix using mean(condition j).
   *
   * @param symbolExpression char
   * @param symbolNoExpression char
   *
   * @throws Exception
   */
  public void computeDiscretizedMatrix_Mean_ByCondition(char
      symbolNoExpression, char symbolExpression) throws Exception {

    if (symbolNoExpression >= symbolExpression) {
      throw new Exception(
          "INVALID ALPHABET: symbolNoExpression < symbolExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[2];

    alphabet[0] = symbolNoExpression;
    alphabet[1] = symbolExpression;

    float a_Ij[] = new float[this.originalExpressionMatrix.
        getNumberOfConditions()]; // mean of column j

    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      float sum_a_Ij = 0;
      int numberOfNonMissings_Ij = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          sum_a_Ij = sum_a_Ij +
              this.originalExpressionMatrix.geneExpressionMatrix[i][j];
          numberOfNonMissings_Ij++;
        }
      }
      if (numberOfNonMissings_Ij != 0) {
        a_Ij[j] = sum_a_Ij / numberOfNonMissings_Ij;
      }
      else {
        a_Ij[j] = this.originalExpressionMatrix.getMissingValue();
      }
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][i] = this.getMissingValue();
        }
        else {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] > a_Ij[j]) {
            symbolicMatrix[i][j] = symbolExpression;
          }
          else {
            symbolicMatrix[i][j] = symbolNoExpression;
          }
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  // #######################################################################################################################

  /**
   * Discretizes the expression matrix using mean(gene i).
   *
   * @param symbolExpression char
   * @param symbolNoExpression char
   *
   * @throws Exception
   */
  public void computeDiscretizedMatrix_Mean_ByGene(char
      symbolNoExpression, char symbolExpression) throws Exception {

    if (symbolNoExpression >= symbolExpression) {
      throw new Exception(
          "INVALID ALPHABET: symbolNoExpression < symbolExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[2];

    alphabet[0] = symbolNoExpression;
    alphabet[1] = symbolExpression;

    float a_iJ[] = new float[this.originalExpressionMatrix.getNumberOfGenes()]; // mean of row i

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      float sum_a_iJ = 0;
      int numberOfNonMissings_iJ = 0;
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          sum_a_iJ = sum_a_iJ +
              this.originalExpressionMatrix.geneExpressionMatrix[i][j];
          numberOfNonMissings_iJ++;
        }
      }
      if (numberOfNonMissings_iJ != 0) {
        a_iJ[i] = sum_a_iJ / numberOfNonMissings_iJ;
      }
      else {
        a_iJ[i] = this.originalExpressionMatrix.getMissingValue();
      }
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] > a_iJ[i]) {
            symbolicMatrix[i][j] = symbolExpression;
          }
          else {
            symbolicMatrix[i][j] = symbolNoExpression;
          }
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  //################## MID-RANGED (OVERALL MATRIX / BY ROW / BY COLUMNS)

  /**
   * Discretizes the expression matrix using the mid-ranged value (overall matrix).
   *
   * @param symbolExpression char
   * @param symbolNoExpression char
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_MidRanged_OverallMatrix(char symbolNoExpression,
      char symbolExpression) throws Exception {

    if (symbolNoExpression >= symbolExpression) {
      throw new Exception(
          "INVALID ALPHABET: symbolNoExpression < symbolExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[2];

    alphabet[0] = symbolNoExpression;
    alphabet[1] = symbolExpression;

    float min_a_ij = Integer.MAX_VALUE;
    float max_a_ij = Integer.MIN_VALUE;
    float midRanged_a_ij;

    int numberOfNonMissings = 0;
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          numberOfNonMissings++;
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <
              min_a_ij) {
            min_a_ij = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
          }
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              max_a_ij) {
            max_a_ij = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
          }
        }
      }
    }

    if (numberOfNonMissings != 0) {
      midRanged_a_ij = (max_a_ij - min_a_ij) / 2;
    }
    else {
      midRanged_a_ij = this.originalExpressionMatrix.getMissingValue();
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              midRanged_a_ij) {
            symbolicMatrix[i][j] = symbolExpression;
          }
          else {
            symbolicMatrix[i][j] = symbolNoExpression;
          }
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  // #######################################################################################################################

  /**
   * Discretizes the expression matrix using the mid-ranged value (by condition).
   *
   * @param symbolExpression char
   * @param symbolNoExpression char
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_MidRanged_ByCondition(char symbolNoExpression,
      char symbolExpression) throws Exception {

    if (symbolNoExpression >= symbolExpression) {
      throw new Exception(
          "INVALID ALPHABET: symbolNoExpression < symbolExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[2];

    alphabet[0] = symbolNoExpression;
    alphabet[1] = symbolExpression;

    float[] min_a_Ij = new float[this.originalExpressionMatrix.
        getNumberOfConditions()];
    float[] max_a_Ij = new float[this.originalExpressionMatrix.
        getNumberOfConditions()];
    float[] midRanged_a_Ij = new float[this.originalExpressionMatrix.
        getNumberOfConditions()];

    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      min_a_Ij[j] = Integer.MAX_VALUE;
      max_a_Ij[j] = Integer.MIN_VALUE;
      int numberOfNonMissings_j = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          numberOfNonMissings_j++;
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <
              min_a_Ij[j]) {
            min_a_Ij[j] = this.originalExpressionMatrix.geneExpressionMatrix[i][
                j];
          }
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              max_a_Ij[j]) {
            max_a_Ij[j] = this.originalExpressionMatrix.geneExpressionMatrix[i][
                j];
          }
        }
      }
      if (numberOfNonMissings_j != 0) {
        midRanged_a_Ij[j] = (max_a_Ij[j] - min_a_Ij[j]) / 2;
      }
      else {
        midRanged_a_Ij[j] = this.originalExpressionMatrix.getMissingValue();
      }
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              midRanged_a_Ij[j]) {
            symbolicMatrix[i][j] = symbolExpression;
          }
          else {
            symbolicMatrix[i][j] = symbolNoExpression;
          }
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  // #######################################################################################################################

  /**
   * Discretizes the expression matrix using the mid-ranged value (by gene).
   *
   * @param symbolExpression char
   * @param symbolNoExpression char
   *
   * @throws Exception
   */
  public void computeDiscretizedMatrix_MidRanged_ByGene(char
      symbolNoExpression, char symbolExpression) throws Exception {

    if (symbolNoExpression >= symbolExpression) {
      throw new Exception(
          "INVALID ALPHABET: symbolNoExpression < symbolExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[2];

    alphabet[0] = symbolNoExpression;
    alphabet[1] = symbolExpression;

    float[] min_a_iJ = new float[this.originalExpressionMatrix.getNumberOfGenes()];
    float[] max_a_iJ = new float[this.originalExpressionMatrix.getNumberOfGenes()];
    float[] midRanged_a_iJ = new float[this.originalExpressionMatrix.
        getNumberOfGenes()];

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      min_a_iJ[i] = Integer.MAX_VALUE;
      max_a_iJ[i] = Integer.MIN_VALUE;
      int numberOfNonMissings_i = 0;
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          numberOfNonMissings_i++;
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <
              min_a_iJ[i]) {
            min_a_iJ[i] = this.originalExpressionMatrix.geneExpressionMatrix[i][
                j];
          }
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              max_a_iJ[i]) {
            max_a_iJ[i] = this.originalExpressionMatrix.geneExpressionMatrix[i][
                j];
          }
        }
      }
      if (numberOfNonMissings_i != 0) {
        midRanged_a_iJ[i] = (max_a_iJ[i] - min_a_iJ[i]) / 2;
      }
      else {
        midRanged_a_iJ[i] = this.originalExpressionMatrix.getMissingValue();
      }

    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              midRanged_a_iJ[i]) {
            symbolicMatrix[i][j] = symbolExpression;
          }
          else {
            symbolicMatrix[i][j] = symbolNoExpression;
          }
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  //################## MAX - X% MAX (OVERALL MATRIX / BY ROW / BY COLUMNS)

  /**
   * Discretizes the expression matrix using the max - X% max (overall matrix).
   *
   * @param X float
   * @param symbolExpression char
   * @param symbolNoExpression char
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_MaxMinusPercentMax_OverallMatrix(float X,
      char symbolNoExpression, char symbolExpression) throws Exception {

    if (symbolNoExpression >= symbolExpression) {
      throw new Exception(
          "INVALID ALPHABET: symbolNoExpression < symbolExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[2];

    alphabet[0] = symbolNoExpression;
    alphabet[1] = symbolExpression;

    float max_a_ij = Integer.MIN_VALUE;
    float maxMinusPercentMax = 0;
    int numberOfNonMissings = 0;

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          numberOfNonMissings++;
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              max_a_ij) {
            max_a_ij = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
          }
        }
      }
    }

    if (numberOfNonMissings != 0) {
      maxMinusPercentMax = max_a_ij - (X / 100) * max_a_ij;
    }
    else {
      maxMinusPercentMax = this.originalExpressionMatrix.getMissingValue();
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              maxMinusPercentMax) {
            symbolicMatrix[i][j] = symbolExpression;
          }
          else {
            symbolicMatrix[i][j] = symbolNoExpression;
          }
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  // #######################################################################################################################

  /**
   * Discretizes the expression matrix using the max - X% max (by condition).
   *
   * @param X float
   * @param symbolExpression char
   * @param symbolNoExpression char
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_MaxMinusPercentMax_ByCondition(float X,
      char symbolNoExpression, char symbolExpression) throws Exception {

    if (symbolNoExpression >= symbolExpression) {
      throw new Exception(
          "INVALID ALPHABET: symbolNoExpression < symbolExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[2];

    alphabet[0] = symbolNoExpression;
    alphabet[1] = symbolExpression;

    float[] max_a_Ij = new float[this.originalExpressionMatrix.
        getNumberOfConditions()];
    float[] maxMinusPercentMax_j = new float[this.originalExpressionMatrix.
        getNumberOfConditions()];

    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      max_a_Ij[j] = Integer.MIN_VALUE;
      int numberOfNonMissings_j = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          numberOfNonMissings_j++;
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              max_a_Ij[j]) {
            max_a_Ij[j] = this.originalExpressionMatrix.geneExpressionMatrix[i][
                j];
          }
        }
      }
      if (numberOfNonMissings_j != 0) {
        maxMinusPercentMax_j[j] = max_a_Ij[j] - (X / 100) * max_a_Ij[j];
      }
      else {
        maxMinusPercentMax_j[j] = this.originalExpressionMatrix.getMissingValue();
      }

    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              maxMinusPercentMax_j[j]) {
            symbolicMatrix[i][j] = symbolExpression;
          }
          else {
            symbolicMatrix[i][j] = symbolNoExpression;
          }
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  // #######################################################################################################################

  /**
   * Discretizes the expression matrix using the max - X% max (by gene).
   *
   * @param X float
   * @param symbolExpression char
   * @param symbolNoExpression char
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_MaxMinusPercentMax_ByGene(float X,
      char symbolNoExpression, char symbolExpression) throws Exception {

    if (symbolNoExpression >= symbolExpression) {
      throw new Exception(
          "INVALID ALPHABET: symbolNoExpression < symbolExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[2];

    alphabet[0] = symbolNoExpression;
    alphabet[1] = symbolExpression;

    float[] max_a_iJ = new float[this.originalExpressionMatrix.getNumberOfGenes()];
    float[] maxMinusPercentMax_i = new float[this.originalExpressionMatrix.
        getNumberOfGenes()];

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      max_a_iJ[i] = Integer.MIN_VALUE;
      int numberOfNonMissings_i = 0;
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          numberOfNonMissings_i++;
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              max_a_iJ[i]) {
            max_a_iJ[i] = this.originalExpressionMatrix.geneExpressionMatrix[i][
                j];
          }
        }
      }
      if (numberOfNonMissings_i != 0) {
        maxMinusPercentMax_i[i] = max_a_iJ[i] - (X / 100) * max_a_iJ[i];
      }
      else {
        maxMinusPercentMax_i[i] = this.originalExpressionMatrix.getMissingValue();
      }
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              maxMinusPercentMax_i[i]) {
            symbolicMatrix[i][j] = symbolExpression;
          }
          else {
            symbolicMatrix[i][j] = symbolNoExpression;
          }
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  //################## X% MAX (OVERALL MATRIX / BY ROW / BY COLUMNS)

  /**
   * Discretizes the expression matrix using the X% max (overall matrix).
   *
   * @param X float
   * @param symbolExpression char
   * @param symbolNoExpression char
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_PercentMax_OverallMatrix(float X,
      char symbolNoExpression, char symbolExpression) throws Exception {

    // (X/100) * this.originalExpressionMatrix.getNumberOfGenes() * this.originalExpressionMatrix.getNumberOfConditions() should be >= 1

    if (symbolNoExpression >= symbolExpression) {
      throw new Exception(
          "INVALID ALPHABET: symbolNoExpression < symbolExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[2];

    alphabet[0] = symbolNoExpression;
    alphabet[1] = symbolExpression;

    ArrayList<Float> geneExpressionMatrix_WithoutMissings_AsArray = new
        ArrayList<Float> (0);
    int numberOfNonMissings = 0;

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          geneExpressionMatrix_WithoutMissings_AsArray.add(this.
              originalExpressionMatrix.geneExpressionMatrix[i][j]);
          numberOfNonMissings++;
        }
        else {
          geneExpressionMatrix_WithoutMissings_AsArray.add(Float.MIN_VALUE); //missings
        }
      }
    }

    Collections.sort(geneExpressionMatrix_WithoutMissings_AsArray); // sorts in ascendent order => max values are at the end

    int numberOfValuesInPercentMax = 0;
    float expressionValueInLowestPercentMax = Float.MIN_VALUE; // missing
    if (numberOfNonMissings != 0) {
      numberOfValuesInPercentMax = (int) Math.round( (X / 100) *
          numberOfNonMissings);
      expressionValueInLowestPercentMax =
          geneExpressionMatrix_WithoutMissings_AsArray.get(
              this.originalExpressionMatrix.getNumberOfGenes() *
              this.originalExpressionMatrix.getNumberOfConditions() -
              numberOfValuesInPercentMax - 1);
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              expressionValueInLowestPercentMax) {
            symbolicMatrix[i][j] = symbolExpression;
          }
          else {
            symbolicMatrix[i][j] = symbolNoExpression;
          }
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  // #######################################################################################################################

  /**
   * Discretizes the expression matrix using the X% max (by condition).
   *
   * @param X float
   * @param symbolExpression char
   * @param symbolNoExpression char
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_PercentMax_ByCondition(float X,
      char symbolNoExpression, char symbolExpression) throws Exception {

    // (X/100) * this.originalExpressionMatrix.getNumberOfGenes() should be >= 1


    if (symbolNoExpression >= symbolExpression) {
      throw new Exception(
          "INVALID ALPHABET: symbolNoExpression < symbolExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[2];

    alphabet[0] = symbolNoExpression;
    alphabet[1] = symbolExpression;

    ArrayList<Float>
        expressionValueInLowestPercentMax_j = new ArrayList<Float> (0);

    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      //column j
      ArrayList<Float> sortedColumn_j = new ArrayList<Float> (0);
      int numberOfNonMissings_j = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          sortedColumn_j.add(this.originalExpressionMatrix.geneExpressionMatrix[
                             i][j]);
          numberOfNonMissings_j++;
        }
        else {
          sortedColumn_j.add(Float.MIN_VALUE); //missing
        }
      }
      int numberOfValuesInPercentMax_j = 0;
      if (numberOfNonMissings_j != 0) {
        numberOfValuesInPercentMax_j = (int) (Math.round( (X / 100) *
            numberOfNonMissings_j));
        Collections.sort(sortedColumn_j);
        expressionValueInLowestPercentMax_j.add(sortedColumn_j.get(this.
            getNumberOfConditions() - numberOfValuesInPercentMax_j - 1));
      }
      else {
        expressionValueInLowestPercentMax_j.add(Float.MIN_VALUE);
      }
    }

    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              expressionValueInLowestPercentMax_j.get(j)) {
            symbolicMatrix[i][j] = symbolExpression;
          }
          else {
            symbolicMatrix[i][j] = symbolNoExpression;
          }
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  // #######################################################################################################################

  /**
   * Discretizes the expression matrix using the X% max (by gene).
   *
   * @param X float
   * @param symbolExpression char
   * @param symbolNoExpression char
   *
   * @throws Exception
   */
  public void computeDiscretizedMatrix_PercentMax_ByGene(float
      X, char symbolNoExpression, char symbolExpression) throws Exception {

    // (X/100) * this.originalExpressionMatrix.getNumberOfConditions() should be >= 1

    if (symbolNoExpression >= symbolExpression) {
      throw new Exception(
          "INVALID ALPHABET: symbolNoExpression < symbolExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[2];

    alphabet[0] = symbolNoExpression;
    alphabet[1] = symbolExpression;

    ArrayList<Float>
        expressionValueInLowestPercentMax_i = new ArrayList<Float> (0);

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      // get row i
      ArrayList<Float> sortedRow_i = new ArrayList<Float> (0);
      int numberOfNonMissings_i = 0;
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          sortedRow_i.add(this.originalExpressionMatrix.geneExpressionMatrix[i][
                          j]);
          numberOfNonMissings_i++;
        }
        else {
          sortedRow_i.add(Float.MIN_VALUE); // missing
        }
      }
      int numberOfValuesInPercentMax_i = 0;
      if (numberOfNonMissings_i != 0) {
        numberOfValuesInPercentMax_i = (int) (Math.round( (X / 100) *
            numberOfNonMissings_i));
        Collections.sort(sortedRow_i);
        expressionValueInLowestPercentMax_i.add(sortedRow_i.get(this.
            getNumberOfGenes() - numberOfValuesInPercentMax_i - 1));
      }
      else {
        expressionValueInLowestPercentMax_i.add(Float.MIN_VALUE);
      }
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              expressionValueInLowestPercentMax_i.get(i)) {
            symbolicMatrix[i][j] = symbolExpression;
          }
          else {
            symbolicMatrix[i][j] = symbolNoExpression;
          }
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  // #######################################################################################################################
  // ################################################### THREE SYMBOLS #####################################################
  // #######################################################################################################################

  //################## MEAN +- STANDARD DEVIATION (OVERALL MATRIX / BY ROW / BY COLUMNS)
  /**
   * Discretizes the expression matrix using mean(overall matrix) +- alpha * std(overall matrix).
   *
   * @param alpha double
   * @param symbolDownExpression char
   * @param symbolNoExpression char
   * @param symbolUpExpression char
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_MeanAndStd_OverallMatrix(double alpha,
      char symbolDownExpression, char symbolNoExpression,
      char symbolUpExpression) throws Exception {

    if (this.originalExpressionMatrix.hasMissingValues) {
      throw new Exception("CANNOT DISCRETIZE MATRIX: matrix has missing values");
    }

    if (symbolDownExpression > symbolNoExpression ||
        symbolNoExpression > symbolUpExpression) {
      throw new Exception("INVALID ALPHABET: symbolDownExpression > symbolNoExpression OR symbolNoExpression > symbolUpExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[3];

    alphabet[0] = symbolDownExpression;
    alphabet[1] = symbolNoExpression;
    alphabet[2] = symbolUpExpression;

    float a_IJ = 0; // average value in the matrix
    float std; // standard deviation of the values in the matrix

    float sum_a_ij = 0;
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        sum_a_ij = sum_a_ij +
            this.originalExpressionMatrix.geneExpressionMatrix[i][j];
      }
    }
    a_IJ = sum_a_ij /
        (this.originalExpressionMatrix.getNumberOfGenes() *
         this.originalExpressionMatrix.getNumberOfConditions());

    double deviation_sum = 0;
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        deviation_sum = deviation_sum +
            Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                     a_IJ, 2);
      }
    }
    std = (float) Math.sqrt(deviation_sum /
                            (this.originalExpressionMatrix.getNumberOfGenes() *
                             this.originalExpressionMatrix.
                             getNumberOfConditions()));

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <
            a_IJ - alpha * std) {
          symbolicMatrix[i][j] = symbolDownExpression;
        }
        else
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
            a_IJ + alpha * std) {
          symbolicMatrix[i][j] = symbolUpExpression;
        }
        else {
          symbolicMatrix[i][j] = symbolNoExpression;
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  /**
   * Discretizes the expression matrix using mean(condition j) +- alpha * std(condition j).
   *
   * @param alpha double
   * @param symbolDownExpression char
   * @param symbolNoExpression char
   * @param symbolUpExpression char
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_MeanAndStd_ByCondition(double alpha,
      char symbolDownExpression, char symbolNoExpression,
      char symbolUpExpression) throws Exception {

    if (this.originalExpressionMatrix.hasMissingValues) {
      throw new Exception("CANNOT DISCRETIZE MATRIX: matrix has missing values");
    }

    if (symbolDownExpression > symbolNoExpression ||
        symbolNoExpression > symbolUpExpression) {
      throw new Exception("INVALID ALPHABET: symbolDownExpression > symbolNoExpression OR symbolNoExpression > symbolUpExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[3];

    alphabet[0] = symbolDownExpression;
    alphabet[1] = symbolNoExpression;
    alphabet[2] = symbolUpExpression;

    float a_Ij[] = new float[this.originalExpressionMatrix.
        getNumberOfConditions()]; // mean of column j
    float std_Ij[] = new float[this.originalExpressionMatrix.
        getNumberOfConditions()]; // standard deviation of column j

    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      float sum_a_Ij = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        sum_a_Ij = sum_a_Ij +
            this.originalExpressionMatrix.geneExpressionMatrix[i][j];
      }
      a_Ij[j] = sum_a_Ij / this.originalExpressionMatrix.getNumberOfGenes();
    }

    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      double deviation_sum_j = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        deviation_sum_j = deviation_sum_j +
            Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                     a_Ij[j], 2);
      }
      std_Ij[j] = (float) Math.sqrt(deviation_sum_j /
                                    this.originalExpressionMatrix.
                                    getNumberOfGenes());
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <
            a_Ij[j] - alpha * std_Ij[j]) {
          symbolicMatrix[i][j] = symbolDownExpression;
        }
        else
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
            a_Ij[j] + alpha * std_Ij[j]) {
          symbolicMatrix[i][j] = symbolUpExpression;
        }
        else {
          symbolicMatrix[i][j] = symbolNoExpression;
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  /**
   * Discretizes the expression matrix using mean(gene i) +- alpha * std(gene i).
   *
   * @param alpha double
   * @param symbolDownExpression char
   * @param symbolNoExpression char
   * @param symbolUpExpression char
   *
   * @throws Exception
   */
  public void computeDiscretizedMatrix_MeanAndStd_ByGene(double alpha,
      char symbolDownExpression, char symbolNoExpression,
      char symbolUpExpression) throws Exception {

    if (this.originalExpressionMatrix.hasMissingValues) {
      throw new Exception("CANNOT DISCRETIZE MATRIX: matrix has missing values");
    }

    if (symbolDownExpression > symbolNoExpression ||
        symbolNoExpression > symbolUpExpression) {
      throw new Exception("INVALID ALPHABET: symbolDownExpression > symbolNoExpression OR symbolNoExpression > symbolUpExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[3];

    alphabet[0] = symbolDownExpression;
    alphabet[1] = symbolNoExpression;
    alphabet[2] = symbolUpExpression;

    float a_iJ[] = new float[this.originalExpressionMatrix.getNumberOfGenes()]; // mean of row i
    float std_iJ[] = new float[this.originalExpressionMatrix.getNumberOfGenes()]; // standard deviation of row i

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      float sum_a_iJ = 0;
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        sum_a_iJ = sum_a_iJ +
            this.originalExpressionMatrix.geneExpressionMatrix[i][j];
      }
      a_iJ[i] = sum_a_iJ / this.originalExpressionMatrix.getNumberOfConditions();
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      double deviation_sum_i = 0;
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        deviation_sum_i = deviation_sum_i +
            Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                     a_iJ[i], 2);
      }
      std_iJ[i] = (float) Math.sqrt(deviation_sum_i /
                                    this.originalExpressionMatrix.
                                    getNumberOfConditions());
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <
            a_iJ[i] - alpha * std_iJ[i]) {
          symbolicMatrix[i][j] = symbolDownExpression;
        }
        else
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
            a_iJ[i] + alpha * std_iJ[i]) {
          symbolicMatrix[i][j] = symbolUpExpression;
        }
        else {
          symbolicMatrix[i][j] = symbolNoExpression;
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  // #######################################################################################################################
  // ################################################### K SYMBOLS #########################################################
  // #######################################################################################################################

  /**
   *
   * @param numberOfSymbols int
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_EqualFrequency_K_Symbols_OverallMatrix(int
      numberOfSymbols) throws Exception {

    if (numberOfSymbols < 2 || numberOfSymbols > 20) {
      throw new Exception(
          "INVALID ALPHABET: numberOfSymbols < 2 OR numberOfSymbols > 20");
    }

    if (this.originalExpressionMatrix.getNumberOfGenes() *
        this.originalExpressionMatrix.getNumberOfConditions() < numberOfSymbols) {
      throw new Exception("INVALID ALPHABET: too many symbols");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[numberOfSymbols];

    if (numberOfSymbols == 2) {
      alphabet[0] = 'D';
      alphabet[1] = 'U';
    }
    else {
      if (numberOfSymbols == 3) {
        alphabet[0] = 'D';
        alphabet[1] = 'N';
        alphabet[1] = 'U';
      }
      else {
        int firstSymbol = 65;
        for (int s = 0; s < numberOfSymbols; s++) {
          alphabet[s] = (char) (firstSymbol + s);
        }
      }
    }

    int numberOfNonMissings = 0;
    ArrayList<Float> geneExpressionMatrixAsArray = new ArrayList<Float> (0);

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          geneExpressionMatrixAsArray.add(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j]);
          numberOfNonMissings++;
        }
        else {
          geneExpressionMatrixAsArray.add(Float.MIN_VALUE); // missing
        }
      }
    }

    float[] expressionValuesInSplitPoints = new float[numberOfSymbols - 1];
    int numberOfValuesForEachSymbol = 0;
    if (numberOfNonMissings != 0) {
      numberOfValuesForEachSymbol = (int) (numberOfNonMissings /
                                           numberOfSymbols);
      Collections.sort(geneExpressionMatrixAsArray); // sorts in ascendent order => max values are at the end
      for (int k = 1; k < numberOfSymbols; k++) {
        expressionValuesInSplitPoints[k -
            1] = geneExpressionMatrixAsArray.get(k *
                                                 numberOfValuesForEachSymbol -
                                                 1);
      }
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          boolean symbolFound = false;
          for (int k = 0; k < numberOfSymbols - 1 && symbolFound == false; k++) {
            if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <=
                expressionValuesInSplitPoints[k]) {
              symbolicMatrix[i][j] = alphabet[k];
              symbolFound = true;
            }
          }
          if (symbolFound == false) {
            symbolicMatrix[i][j] = alphabet[numberOfSymbols - 1];
          }
        }
      }
    }

    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  // #######################################################################################################################

  /**
   *
   * @param numberOfSymbols int
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_EqualFrequency_K_Symbols_ByCondition(int
      numberOfSymbols) throws Exception {

    if (numberOfSymbols < 2 || numberOfSymbols > 20) {
      throw new Exception(
          "INVALID ALPHABET: numberOfSymbols < 2 OR numberOfSymbols > 20");
    }

    if (this.originalExpressionMatrix.getNumberOfGenes() *
        this.originalExpressionMatrix.getNumberOfConditions() < numberOfSymbols) {
      throw new Exception("INVALID ALPHABET: too many symbols");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[numberOfSymbols];

    if (numberOfSymbols == 2) {
      alphabet[0] = 'D';
      alphabet[1] = 'U';
    }
    else {
      if (numberOfSymbols == 3) {
        alphabet[0] = 'D';
        alphabet[1] = 'N';
        alphabet[1] = 'U';
      }
      else {
        int firstSymbol = 65;
        for (int s = 0; s < numberOfSymbols; s++) {
          alphabet[s] = (char) (firstSymbol + s);
        }
      }
    }

    float[][] expressionValuesInSplitPoints_j = new float[this.
        originalExpressionMatrix.getNumberOfConditions()][numberOfSymbols - 1];

    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      //column j
      float[] sortedColumn_j = new float[this.originalExpressionMatrix.
          getNumberOfGenes()];
      int numberOfNonMissings_j = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          sortedColumn_j[i] = this.originalExpressionMatrix.
              geneExpressionMatrix[i][
              j];
          numberOfNonMissings_j++;
        }
        else {
          sortedColumn_j[i] = this.getMissingValue();
        }
      }

      int numberOfValuesForEachSymbol = 0;
      if (numberOfNonMissings_j != 0) {
        Arrays.sort(sortedColumn_j);
        numberOfValuesForEachSymbol = (int) (numberOfNonMissings_j /
                                             numberOfSymbols);
        for (int k = 1; k < numberOfSymbols; k++) {
          expressionValuesInSplitPoints_j[j][k -
              1] = sortedColumn_j[k * numberOfValuesForEachSymbol - 1];
        }
      }
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        boolean symbolFound = false;
        for (int k = 0; k < numberOfSymbols - 1 && symbolFound == false; k++) {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
              this.originalExpressionMatrix.getMissingValue()) {
            symbolicMatrix[i][j] = this.getMissingValue();
          }
          else {
            if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <=
                expressionValuesInSplitPoints_j[j][k]) {
              symbolicMatrix[i][j] = alphabet[k];
              symbolFound = true;
            }
          }
          if (symbolFound == false) {
            symbolicMatrix[i][j] = alphabet[numberOfSymbols - 1];
          }
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  // #######################################################################################################################

  /**
   *
   * @param numberOfSymbols int
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_EqualFrequency_K_Symbols_ByGene(int
      numberOfSymbols) throws Exception {

    if (numberOfSymbols < 2 || numberOfSymbols > 20) {
      throw new Exception(
          "INVALID ALPHABET: numberOfSymbols < 2 OR numberOfSymbols > 20");
    }

    if (this.originalExpressionMatrix.getNumberOfGenes() *
        this.originalExpressionMatrix.getNumberOfConditions() < numberOfSymbols) {
      throw new Exception("INVALID ALPHABET: too many symbols");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[numberOfSymbols];

    if (numberOfSymbols == 2) {
      alphabet[0] = 'D';
      alphabet[1] = 'U';
    }
    else {
      if (numberOfSymbols == 3) {
        alphabet[0] = 'D';
        alphabet[1] = 'N';
        alphabet[1] = 'U';
      }
      else {
        int firstSymbol = 65;
        for (int s = 0; s < numberOfSymbols; s++) {
          alphabet[s] = (char) (firstSymbol + s);
        }
      }
    }

    float[][] expressionValuesInSplitPoints_i = new float[this.
        originalExpressionMatrix.getNumberOfGenes()][numberOfSymbols - 1];

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      //row i
      float[] sortedRow_i = new float[this.originalExpressionMatrix.
          getNumberOfConditions()];
      int numberOfNonMissings_i = 0;
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          sortedRow_i[j] = this.originalExpressionMatrix.geneExpressionMatrix[i][
              j];
          numberOfNonMissings_i++;
        }
        else {
          sortedRow_i[j] = Float.MIN_VALUE; // missing
        }
      }

      int numberOfValuesForEachSymbol_i = 0;
      if (numberOfNonMissings_i != 0) {
        numberOfValuesForEachSymbol_i = (int) (numberOfNonMissings_i /
                                               numberOfSymbols);
        Arrays.sort(sortedRow_i);
        for (int k = 1; k < numberOfSymbols; k++) {
          expressionValuesInSplitPoints_i[i][k -
              1] = sortedRow_i[k * numberOfValuesForEachSymbol_i - 1];
        }
      }
    }
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        boolean symbolFound = false;
        for (int k = 0; k < numberOfSymbols - 1 && symbolFound == false; k++) {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
              this.originalExpressionMatrix.getMissingValue()) {
            symbolicMatrix[i][j] = this.getMissingValue();
          }
          else {
            if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <=
                expressionValuesInSplitPoints_i[i][k]) {
              symbolicMatrix[i][j] = alphabet[k];
              symbolFound = true;
            }
          }
          if (symbolFound == false) {
            symbolicMatrix[i][j] = alphabet[numberOfSymbols - 1];
          }
        }
      }
    }

    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  // ######################## EQUAL WIDTH

  /**
   *
   * @param numberOfSymbols int
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_EqualWidth_K_Symbols_OverallMatrix(int
      numberOfSymbols) throws Exception {

    if (numberOfSymbols < 2 || numberOfSymbols > 20) {
      throw new Exception(
          "INVALID ALPHABET: numberOfSymbols < 2 OR numberOfSymbols > 20");
    }

    if (this.originalExpressionMatrix.getNumberOfGenes() *
        this.originalExpressionMatrix.getNumberOfConditions() < numberOfSymbols) {
      throw new Exception("INVALID ALPHABET: too many symbols");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[numberOfSymbols];

    if (numberOfSymbols == 2) {
      alphabet[0] = 'D';
      alphabet[1] = 'U';
    }
    else {
      if (numberOfSymbols == 3) {
        alphabet[0] = 'D';
        alphabet[1] = 'N';
        alphabet[1] = 'U';
      }
      else {
        int firstSymbol = 65;
        for (int s = 0; s < numberOfSymbols; s++) {
          alphabet[s] = (char) (firstSymbol + s);
        }
      }
    }

    float min_a_ij = Float.MAX_VALUE;
    float max_a_ij = Float.MIN_VALUE;
    int numberOfNonMissings = 0;
    float width_a_ij = 0;

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          numberOfNonMissings++;
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <
              min_a_ij) {
            min_a_ij = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
          }
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              max_a_ij) {
            max_a_ij = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
          }
        }
      }
    }

    if (numberOfNonMissings != 0) {
      width_a_ij = (max_a_ij - min_a_ij) / numberOfSymbols;
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        boolean symbolFound = false;
        for (int k = 1; k < numberOfSymbols && symbolFound == false; k++) {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
              this.originalExpressionMatrix.getMissingValue()) {
            symbolicMatrix[i][j] = this.getMissingValue();
          }
          else {
            if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <=
                min_a_ij + k * width_a_ij) {
              symbolicMatrix[i][j] = alphabet[k - 1];
              symbolFound = true;
            }
            if (symbolFound == false) {
              symbolicMatrix[i][j] = alphabet[numberOfSymbols - 1];
            }
          }
        }
      }
    }
  }

  // #######################################################################################################################

  /**
   *
   * @param numberOfSymbols int
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_EqualWidth_K_Symbols_ByCondition(int
      numberOfSymbols) throws Exception {

    if (numberOfSymbols < 2 || numberOfSymbols > 20) {
      throw new Exception(
          "INVALID ALPHABET: numberOfSymbols < 2 OR numberOfSymbols > 20");
    }

    if (this.originalExpressionMatrix.getNumberOfGenes() *
        this.originalExpressionMatrix.getNumberOfConditions() < numberOfSymbols) {
      throw new Exception("INVALID ALPHABET: too many symbols");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[numberOfSymbols];

    if (numberOfSymbols == 2) {
      alphabet[0] = 'D';
      alphabet[1] = 'U';
    }
    else {
      if (numberOfSymbols == 3) {
        alphabet[0] = 'D';
        alphabet[1] = 'N';
        alphabet[1] = 'U';
      }
      else {
        int firstSymbol = 65;
        for (int s = 0; s < numberOfSymbols; s++) {
          alphabet[s] = (char) (firstSymbol + s);
        }
      }
    }

    float[] min_a_Ij = new float[this.originalExpressionMatrix.
        getNumberOfConditions()];
    float[] max_a_Ij = new float[this.originalExpressionMatrix.
        getNumberOfConditions()];
    float[] width_a_Ij = new float[this.originalExpressionMatrix.
        getNumberOfConditions()];

    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      int numberOfNonMissings_j = 0;
      min_a_Ij[j] = Float.MAX_VALUE;
      max_a_Ij[j] = Float.MIN_VALUE;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          numberOfNonMissings_j++;
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <
              min_a_Ij[j]) {
            min_a_Ij[j] = this.originalExpressionMatrix.geneExpressionMatrix[i][
                j];
          }
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              max_a_Ij[j]) {
            max_a_Ij[j] = this.originalExpressionMatrix.geneExpressionMatrix[i][
                j];
          }
        }
      }

      if (numberOfNonMissings_j != 0) {
        width_a_Ij[j] = (max_a_Ij[j] - min_a_Ij[j]) / numberOfSymbols;
      }
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          boolean symbolFound = false;
          for (int k = 1; k < numberOfSymbols && symbolFound == false; k++) {
            if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <=
                min_a_Ij[j] + k * width_a_Ij[j]) {
              symbolicMatrix[i][j] = alphabet[k - 1];
              symbolFound = true;
            }
            if (symbolFound == false) {
              symbolicMatrix[i][j] = alphabet[numberOfSymbols - 1];
            }
          }
        }
      }
    }

    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  // #######################################################################################################################

  /**
   *
   * @param numberOfSymbols int
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_EqualWidth_K_Symbols_ByGene(int
      numberOfSymbols) throws Exception {

    if (numberOfSymbols < 2 || numberOfSymbols > 20) {
      throw new Exception(
          "INVALID ALPHABET: numberOfSymbols < 2 OR numberOfSymbols > 20");
    }

    if (this.originalExpressionMatrix.getNumberOfGenes() *
        this.originalExpressionMatrix.getNumberOfConditions() < numberOfSymbols) {
      throw new Exception("INVALID ALPHABET: too many symbols");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[numberOfSymbols];

    if (numberOfSymbols == 2) {
      alphabet[0] = 'D';
      alphabet[1] = 'U';
    }
    else {
      if (numberOfSymbols == 3) {
        alphabet[0] = 'D';
        alphabet[1] = 'N';
        alphabet[1] = 'U';
      }
      else {
        int firstSymbol = 65;
        for (int s = 0; s < numberOfSymbols; s++) {
          alphabet[s] = (char) (firstSymbol + s);
        }
      }
    }

    float[] min_a_iJ = new float[this.originalExpressionMatrix.getNumberOfGenes()];
    float[] max_a_iJ = new float[this.originalExpressionMatrix.getNumberOfGenes()];
    float[] width_a_iJ = new float[this.originalExpressionMatrix.
        getNumberOfGenes()];

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      min_a_iJ[i] = Float.MAX_VALUE;
      max_a_iJ[i] = Float.MIN_VALUE;
      int numberOfNonMissings_j = 0;
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          numberOfNonMissings_j++;
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <
              min_a_iJ[i]) {
            min_a_iJ[i] = this.originalExpressionMatrix.geneExpressionMatrix[i][
                j];
          }
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              max_a_iJ[i]) {
            max_a_iJ[i] = this.originalExpressionMatrix.geneExpressionMatrix[i][
                j];
          }
        }
      }

      if (numberOfNonMissings_j != 0) {
        width_a_iJ[i] = (max_a_iJ[i] - min_a_iJ[i]) / numberOfSymbols;
      }
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          boolean symbolFound = false;
          for (int k = 1; k < numberOfSymbols && symbolFound == false; k++) {
            if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <=
                min_a_iJ[i] + k * width_a_iJ[i]) {
              symbolicMatrix[i][j] = alphabet[k - 1];
              symbolFound = true;
            }
            if (symbolFound == false) {
              symbolicMatrix[i][j] = alphabet[numberOfSymbols - 1];
            }
          }
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  // ###################################################################################################################
  // ################ USING EXPRESSION VARIATION BETWEEN TIME POINTS ###################################################
  // ###################################################################################################################

  /**
   * Discretizes the expression matrix using transitional state discrimination (TSD) discretization
   * (z-scores computed using mean and standard deviation by gene).
   * a_ij - a_i(j-1) >= 0 --> 1
   * a_ij - a_i(j-1) < 0  --> 0
   *
   * @param symbolExpression char
   * @param symbolNoExpression char
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_VariationsBetweenTimePoints_TSD(char
      symbolNoExpression, char symbolExpression) throws Exception {

    if (symbolNoExpression >= symbolExpression) {
      throw new Exception(
          "INVALID ALPHABET: symbolNoExpression < symbolExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions() -
        1];
    char[] alphabet = new char[2];

    alphabet[0] = symbolNoExpression;
    alphabet[1] = symbolExpression;

    // standardize gene expression matrix using z-score
    float[][] standarlizedExpressionMatrix = new float[this.
        originalExpressionMatrix.getNumberOfGenes()][this.
        originalExpressionMatrix.
        getNumberOfConditions()];

    float a_iJ[] = new float[this.originalExpressionMatrix.getNumberOfGenes()]; // mean of row i
    float std_iJ[] = new float[this.originalExpressionMatrix.getNumberOfGenes()]; // standard deviation of row i

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      float sum_a_iJ = 0;
      int numberOfNonMissings_i = 0;
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          sum_a_iJ = sum_a_iJ +
              this.originalExpressionMatrix.geneExpressionMatrix[i][j];
          numberOfNonMissings_i++;
        }
      }

      if (numberOfNonMissings_i != 0) {
        a_iJ[i] = sum_a_iJ / numberOfNonMissings_i;
      }
      else {
        a_iJ[i] = this.originalExpressionMatrix.getMissingValue();
      }
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      double deviation_sum_i = 0;
      int numberOfNonMissings_i = 0;
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          deviation_sum_i = deviation_sum_i +
              Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       a_iJ[i], 2);
          numberOfNonMissings_i++; ;
        }
      }
      if (numberOfNonMissings_i != 0) {
        std_iJ[i] = (float) Math.sqrt(deviation_sum_i / numberOfNonMissings_i);
      }
      else {
        std_iJ[i] = 0;
      }
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] !=
            this.originalExpressionMatrix.getMissingValue()) {
          standarlizedExpressionMatrix[i][j] = (this.originalExpressionMatrix.
                                                geneExpressionMatrix[i][j] -
                                                a_iJ[i]) / std_iJ[i];
        }
      }
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 1; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          float previousValue = standarlizedExpressionMatrix[i][j - 1];
          if (previousValue != this.originalExpressionMatrix.getMissingValue()) { // previous values not missing
            if (standarlizedExpressionMatrix[i][j] - previousValue > 0) {
              symbolicMatrix[i][j - 1] = symbolExpression;
            }
            else {
              symbolicMatrix[i][j - 1] = symbolNoExpression;
            }
          }
          else { // find closest non-missing value
            boolean foundNonMissing = false;
            int k = j - 1;
            while (k >= 0 && !foundNonMissing) {
              if (this.originalExpressionMatrix.geneExpressionMatrix[i][k] !=
                  this.originalExpressionMatrix.missingValue) {
                foundNonMissing = true;
                previousValue = this.originalExpressionMatrix.
                    geneExpressionMatrix[i][k];
              }
              k--;
            }

            if (foundNonMissing) {
              if (standarlizedExpressionMatrix[i][j] - previousValue > 0) {
                symbolicMatrix[i][j - 1] = symbolExpression;
              }
              else {
                symbolicMatrix[i][j - 1] = symbolNoExpression;
              }
            }
            else {
              symbolicMatrix[i][j - 1] = this.getMissingValue();
            }
          }
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
    this.isVBTP = true;
  }

  // ###################################################################################################################

  /**
   * Discretizes the expression matrix using variations between time points (Erdal et al.).
   * t = std (time point 0) * alpha
   * a_ij - a_i(j-1 >= t --> 1
   * a_ij - a_i(j-1 < t  --> 0
   *
   * @param alpha float
   * @param symbolExpression char
   * @param symbolNoExpression char
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_VariationsBetweenTimePoints(float alpha,
      char symbolNoExpression, char symbolExpression) throws Exception {

    if (symbolNoExpression >= symbolExpression) {
      throw new Exception(
          "INVALID ALPHABET: symbolNoExpression < symbolExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions() -
        1];
    char[] alphabet = new char[2];

    alphabet[0] = symbolNoExpression;
    alphabet[1] = symbolExpression;

    float a_i0; // average value in time point 0
    float std_0; // standard deviation of the values in time point 0
    int numberOfNonMissing = 0;

    float sum_a_i0 = 0;
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      if (this.originalExpressionMatrix.geneExpressionMatrix[i][0] !=
          this.originalExpressionMatrix.getMissingValue()) {
        sum_a_i0 = sum_a_i0 +
            this.originalExpressionMatrix.geneExpressionMatrix[i][0];
        numberOfNonMissing++;
      }
    }

    if (numberOfNonMissing != 0) {
      a_i0 = sum_a_i0 / numberOfNonMissing;
      double deviation_sum = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        deviation_sum = deviation_sum +
            Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][0] -
                     a_i0, 2);
      }
      std_0 = (float) Math.sqrt(deviation_sum / numberOfNonMissing);
    }
    else {
      a_i0 = Float.MIN_VALUE; // missing
      std_0 = 0;
    }

    float t = alpha * std_0;

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 1; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          float previousValue = this.originalExpressionMatrix.
              geneExpressionMatrix[i][j - 1];
          if (previousValue != this.originalExpressionMatrix.getMissingValue()) { // previous value not missing
            if (Math.abs(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] - previousValue) >= t) {
              symbolicMatrix[i][j - 1] = symbolExpression;
            }
            else {
              symbolicMatrix[i][j - 1] = symbolNoExpression;
            }
          }
          else { // find closest non-missing value
            boolean foundNonMissing = false;
            int k = j - 1;
            while (k >= 0 && !foundNonMissing) {
              if (this.originalExpressionMatrix.geneExpressionMatrix[i][k] !=
                  this.originalExpressionMatrix.missingValue) {
                foundNonMissing = true;
                previousValue = this.originalExpressionMatrix.
                    geneExpressionMatrix[i][k];
              }
              k--;
            }

            if (foundNonMissing) {
              if (Math.abs(this.originalExpressionMatrix.geneExpressionMatrix[i][
                           j] - previousValue) >= t) {
                symbolicMatrix[i][j - 1] = symbolExpression;
              }
              else {
                symbolicMatrix[i][j - 1] = symbolNoExpression;
              }
            }
            else {
              symbolicMatrix[i][j - 1] = this.getMissingValue();
            }

          }
        }
      }
    }
    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
    this.isVBTP = true;
  }

  // ###################################################################################################################

  /**
   * Discretizes the expression matrix using variations between time points (Ji and Tan).
   *
   * @param t float
   * @param symbolUpExpression char
   * @param symbolNoExpression char
   * @param symbolDownExpression char
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_VariationsBetweenTimePoints(float t,
      char symbolDownExpression,
      char symbolNoExpression, char symbolUpExpression) throws Exception {

    if (symbolDownExpression > symbolNoExpression ||
        symbolNoExpression > symbolUpExpression) {
      throw new Exception("INVALID ALPHABET: symbolDownExpression > symbolNoExpression OR symbolNoExpression > symbolUpExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions() -
        1];
    char[] alphabet = new char[3];

    alphabet[0] = symbolDownExpression;
    alphabet[1] = symbolNoExpression;
    alphabet[2] = symbolUpExpression;

    float[][] expressionVariations = new float[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions() -
        1];

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0;
           j < this.originalExpressionMatrix.getNumberOfConditions() - 1; j++) {

        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j + 1] ==
            this.originalExpressionMatrix.getMissingValue()) {
          expressionVariations[i][j] = this.originalExpressionMatrix.
              getMissingValue();
        }
        else {

          float previousValue = this.originalExpressionMatrix.
              geneExpressionMatrix[i][j];
          boolean foundNonMissing = false;
          if (previousValue == this.originalExpressionMatrix.missingValue) {
            int k = j - 1;
            while (k >= 0 && !foundNonMissing) {
              if (this.originalExpressionMatrix.geneExpressionMatrix[i][k] !=
                  this.originalExpressionMatrix.missingValue) {
                foundNonMissing = true;
                previousValue = this.originalExpressionMatrix.
                    geneExpressionMatrix[i][k];
              }
              k--;
            }
          }
          else {
            foundNonMissing = true;
          }

          if (!foundNonMissing) {
            expressionVariations[i][j] = this.originalExpressionMatrix.
                getMissingValue();
          }
          else {
            if (previousValue != 0) {
              expressionVariations[i][j] = (this.originalExpressionMatrix.
                                            geneExpressionMatrix[i][j + 1] -
                                            previousValue) /
                  Math.abs(previousValue);
            }
            else { // this.originalExpressionMatrix.geneExpressionMatrix[i][j] = 0
              if (this.originalExpressionMatrix.geneExpressionMatrix[i][j + 1] >
                  0) {
                expressionVariations[i][j] = 1;
              }
              else {
                if (this.originalExpressionMatrix.geneExpressionMatrix[i][j + 1] <
                    0) {
                  expressionVariations[i][j] = -1;
                }
                else { // this.originalExpressionMatrix.geneExpressionMatrix[i][j + 1] = 0
                  expressionVariations[i][j] = 0;
                }
              }
            }
          }
        }
      }
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0;
           j < this.originalExpressionMatrix.getNumberOfConditions() - 1; j++) {
        if (expressionVariations[i][j] ==
            this.originalExpressionMatrix.getMissingValue()) {
          symbolicMatrix[i][j] = this.getMissingValue();
        }
        else {
          if (expressionVariations[i][j] >= t) {
            symbolicMatrix[i][j] = symbolUpExpression;
          }
          else {
            if (expressionVariations[i][j] <= -t) {
              symbolicMatrix[i][j] = symbolDownExpression;
            }
            else {
              symbolicMatrix[i][j] = symbolNoExpression;
            }
          }
        }
      }
    }

    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
    this.isVBTP = true;
  }

//########################################################################################################################
//##########################################################################################################################
//##########################################################################################################################
//##########################################################################################################################
//########################################  MIXTURE MODELS
//##########################################################################################################################
//##########################################################################################################################
//########################################################################################################################
//##########################################################################################################################


  // ################## 1 GAUSSIAN

  /**
   * Compute a mixture model with one Gaussian with mean on the mean of the expression matrix and estimated
   * standard deviation.
   *
   * @return MixtureModelOneGaussian
   */
  public MixtureModelOneGaussian computeMixtureModel_OneGaussian_OverallMatrix() {

    System.out.println("COMPUTING MIXTURE MODEL - ONE GAUSSIAN");

    double mean_1;
    double std_1;

    // compute mean of all values in the matrix
    double sum_a_ij = 0;
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        sum_a_ij = sum_a_ij +
            this.originalExpressionMatrix.geneExpressionMatrix[i][j];
      }
    }
    mean_1 = sum_a_ij /
        (this.originalExpressionMatrix.getNumberOfGenes() *
         this.originalExpressionMatrix.getNumberOfConditions());

    // compute standard deviation of all values in the matrix = initial value for std_1
    double deviation_sum = 0;
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        deviation_sum = deviation_sum +
            Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                     mean_1, 2);
      }
    }
    std_1 = Math.sqrt(deviation_sum /
                      (this.originalExpressionMatrix.getNumberOfGenes() *
                       this.originalExpressionMatrix.getNumberOfConditions()));

    // EM
    int numIteractions = 0;
    boolean convergence = false;
    double[][] E_Z_i1 = new double[this.originalExpressionMatrix.
        getNumberOfGenes()][this.
        originalExpressionMatrix.getNumberOfConditions()];
    double new_std_1 = 0;

    while (numIteractions <= 500 && convergence == false) {
      // STEP 1
      // compute E [Z_i1]
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          E_Z_i1[i][j] = Math.exp( -1 / (2 * Math.pow(std_1, 2)) *
                                  Math.pow(this.originalExpressionMatrix.
                                           geneExpressionMatrix[i][j] - mean_1,
                                           2));
        }
      }

      // STEP 2
      // new std_1
      double sum_E_Z_i1 = 0;
      double sum_E_Z_i1_x_i_mean_1 = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i1_x_i_mean_1 = sum_E_Z_i1_x_i_mean_1 +
              Math.pow(E_Z_i1[i][j] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       mean_1, 2);
          sum_E_Z_i1 = sum_E_Z_i1 + E_Z_i1[i][j];
        }
      }
      new_std_1 = Math.sqrt(sum_E_Z_i1_x_i_mean_1 / sum_E_Z_i1);

      // update stopping conditions
      convergence = Math.abs(std_1 - new_std_1) <= 0.00001;
      numIteractions++;

/*      System.out.println(
          "COMPUTING MIXTURE MODEL - ONE GAUSSIAN: Iteraction = " +
          numIteractions);
      System.out.println("mean_1 = " + mean_1);
      System.out.println("std_1 = " + std_1 + "\t" + "new_std_1 = " + new_std_1);
 */

      // prepare next iteraction
      std_1 = new_std_1;
    }

    MixtureModelOneGaussian model = new MixtureModelOneGaussian(this.
        originalExpressionMatrix.getNumberOfGenes() *
        this.originalExpressionMatrix.getNumberOfConditions(),
        mean_1, std_1, E_Z_i1);
    return (model);
  }

  /**
   * Compute a mixture model with one Gaussian for each gene with mean on the mean of the expression levels of each
   * gene and estimated standard deviation.
   *
   * @return MistureModelOneGaussian[] One model for each gene.
   */

  public MixtureModelOneGaussian[] computeMixtureModel_OneGaussian_ByGene() {

    System.out.println("COMPUTING MIXTURE MODELS BY GENE - ONE GAUSSIAN");

    MixtureModelOneGaussian[] modelsByGene = new MixtureModelOneGaussian[this.
        originalExpressionMatrix.getNumberOfGenes()];

    // compute mean for each gene
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      modelsByGene[i] = new MixtureModelOneGaussian();
      modelsByGene[i].setSampleSize(this.originalExpressionMatrix.
                                    getNumberOfGenes());
      double sum_a_iJ = 0;
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        sum_a_iJ = sum_a_iJ +
            this.originalExpressionMatrix.geneExpressionMatrix[i][j];
      }
      modelsByGene[i].setMean_1(sum_a_iJ /
                                this.originalExpressionMatrix.
                                getNumberOfConditions());
      modelsByGene[i].setSampleSize(this.originalExpressionMatrix.
                                    getNumberOfConditions());
    }

    // compute standard deviation of each gene -- initial values
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      double deviation_sum_i = 0;
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        deviation_sum_i = deviation_sum_i +
            Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                     modelsByGene[i].getMean_1(), 2);
      }
      modelsByGene[i].setStd_1(Math.sqrt(deviation_sum_i /
                                         this.originalExpressionMatrix.
                                         getNumberOfConditions()));
    }

    // EM

    int numIteractions = 0;
    boolean convergence = false;
    double[][] E_Z_i1 = new double[1][this.originalExpressionMatrix.
        getNumberOfConditions()];
    double new_std_1_i = 0;

    // FOR EACH GENE
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {

//      System.out.println("GENE = " + i);

      numIteractions = 0;
      convergence = false;

      // BEGIN EM ITERACTIONS
      while (numIteractions <= 500 && convergence == false) {

        // STEP 1
        // compute E [Z_i1]
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          E_Z_i1[0][j] = Math.exp( -1 /
                                  (2 * Math.pow(modelsByGene[i].getStd_1(), 2)) *
                                  Math.pow(this.originalExpressionMatrix.
                                           geneExpressionMatrix[i][j] -
                                           modelsByGene[i].getMean_1(), 2));
        }

        // STEP 2
        // new std_1
        double sum_E_Z_i1_i = 0;
        double sum_E_Z_i1_x_i_mean_1_i = 0;
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i1_x_i_mean_1_i = sum_E_Z_i1_x_i_mean_1_i +
              Math.pow(E_Z_i1[0][j] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByGene[i].getMean_1(), 2);
          sum_E_Z_i1_i = sum_E_Z_i1_i + E_Z_i1[0][j];
        }
        new_std_1_i = Math.sqrt(sum_E_Z_i1_x_i_mean_1_i / sum_E_Z_i1_i);

        // update stopping conditions
        convergence = Math.abs(modelsByGene[i].getStd_1() - new_std_1_i) <=
            0.0001;
        numIteractions++;
        /*
                System.out.println("COMPUTING MIXTURE MODELS BY GENE - ONE GAUSSIAN: Iteraction = " + numIteractions);
                System.out.println("mean_1 = " + modelsByGene[i].getMean_1());
                System.out.println("std_1 = " + modelsByGene[i].getStd_1() + "\t" + "new_std_1 = " + new_std_1_i);
         */
        // prepare next iteraction
        modelsByGene[i].setStd_1(new_std_1_i);

      } // END EM ITERATION

      modelsByGene[i].setE_Z_i1(E_Z_i1);
    }

    return (modelsByGene);
  }

  /**
   * Compute a mixture model with one Gaussian for each gene with mean on the mean of the expression levels of each
   * condition and estimated standard deviation.
   *
   * @return MistureModelOneGaussian[] One model for each condition.
   */

  public MixtureModelOneGaussian[] computeMixtureModel_OneGaussian_ByCondition() {

    System.out.println("COMPUTING MIXTURE MODELS BY CONDITION - ONE GAUSSIAN");

    MixtureModelOneGaussian[] modelsByCondition = new MixtureModelOneGaussian[this.
        originalExpressionMatrix.getNumberOfConditions()];

    // compute mean for each Condition
    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      modelsByCondition[j] = new MixtureModelOneGaussian();
      modelsByCondition[j].setSampleSize(this.originalExpressionMatrix.
                                         getNumberOfGenes());
      double sum_a_Ij = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        sum_a_Ij = sum_a_Ij +
            this.originalExpressionMatrix.geneExpressionMatrix[i][j];
      }
      modelsByCondition[j].setMean_1(sum_a_Ij /
                                     this.originalExpressionMatrix.
                                     getNumberOfGenes());
      modelsByCondition[j].setSampleSize(this.originalExpressionMatrix.
                                         getNumberOfGenes());
    }

    // compute standard deviation of each Condition - initial values
    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      double deviation_sum_j = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        deviation_sum_j = deviation_sum_j +
            Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                     modelsByCondition[j].getMean_1(), 2);
      }
      modelsByCondition[j].setStd_1(Math.sqrt(deviation_sum_j /
                                              this.originalExpressionMatrix.
                                              getNumberOfGenes()));
    }

    // EM

    int numIteractions = 0;
    boolean convergence = false;
    double[][] E_Z_i1 = new double[this.originalExpressionMatrix.
        getNumberOfGenes()][
        1];
    double new_std_1_j = 0;

    // FOR EACH CONDITION
    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {

//      System.out.println("CONDITION = " + j);

      numIteractions = 0;
      convergence = false;

      // BEGIN EM ITERACTIONS
      while (numIteractions <= 500 && convergence == false) {

        // STEP 1
        // compute E [Z_i1]
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          E_Z_i1[i][0] = Math.exp( -1 /
                                  (2 *
                                   Math.pow(modelsByCondition[j].getStd_1(), 2)) *
                                  Math.pow(this.originalExpressionMatrix.
                                           geneExpressionMatrix[i][j] -
                                           modelsByCondition[j].getMean_1(), 2));
        }

        // STEP 2
        // new std_1
        double sum_E_Z_i1_j = 0;
        double sum_E_Z_i1_x_i_mean_1_j = 0;
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          sum_E_Z_i1_x_i_mean_1_j = sum_E_Z_i1_x_i_mean_1_j +
              Math.pow(E_Z_i1[i][0] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByCondition[j].getMean_1(), 2);
          sum_E_Z_i1_j = sum_E_Z_i1_j + E_Z_i1[i][0];
        }
        new_std_1_j = Math.sqrt(sum_E_Z_i1_x_i_mean_1_j / sum_E_Z_i1_j);

        // update stopping conditions
        convergence = Math.pow(modelsByCondition[j].getStd_1() - new_std_1_j, 2) ==
            0;
        numIteractions++;
        /*
                  System.out.println("COMPUTING MIXTURE MODELS BY CONDITION - ONE GAUSSIAN: Iteraction = " + numIteractions);
         System.out.println("mean_1 = " + modelsByCondition[j].getMean_1());
                  System.out.println("std_1 = " + modelsByCondition[j].getStd_1() + "\t" + "new_std_1 = " + new_std_1_j);
         */
        // prepare next iteraction
        modelsByCondition[j].setStd_1(new_std_1_j);

      } // END EM ITERACTION

      modelsByCondition[j].setE_Z_i1(E_Z_i1);
    }

    return (modelsByCondition);
  }

  // ################## 2 GAUSSIANS

  /**
   * Compute a mixture model with two Gaussians.
   * One Gaussian has mean on the mean of the expression matrix and estimated standard deviation.
   * Other Gaussian has mean and standard deviation estimated using EM.
   *
   * @return MixtureModelTwoGaussians
   */
  public MixtureModelTwoGaussians
      computeMixtureModel_TwoGaussians_OverallMatrix() {

    System.out.println("COMPUTING MIXTURE MODEL - TWO GAUSSIANS");

    double mean_1; // fixed
    double mean_2;
    double std_1;
    double std_2;

    // compute mean of all values in the matrix
    double sum_a_ij = 0;
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        sum_a_ij = sum_a_ij +
            this.originalExpressionMatrix.geneExpressionMatrix[i][j];
      }
    }
    mean_1 = sum_a_ij /
        (this.originalExpressionMatrix.getNumberOfGenes() *
         this.originalExpressionMatrix.getNumberOfConditions());

    // compute initial values for mean_2, std_1 and std_2
    float min_a_ij = this.originalExpressionMatrix.geneExpressionMatrix[0][0];
    float max_a_ij = this.originalExpressionMatrix.geneExpressionMatrix[0][0];
    float midRanged_a_ij = 0;
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] < min_a_ij) {
          min_a_ij = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
        }
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] > max_a_ij) {
          max_a_ij = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
        }
      }
    }

    midRanged_a_ij = (max_a_ij + min_a_ij) / 2;
    if (mean_1 <= midRanged_a_ij) { // left Gaussian centered on mean_1
      mean_2 = (max_a_ij + midRanged_a_ij) / 2;
      double deviation_sum_valuesBelowMidRanged = 0;
      double deviation_sum_valuesAboveMidRanged = 0;
      int valuesBelowMidRanged = 0;
      int valuesAboveMidRanged = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <=
              midRanged_a_ij) {
            deviation_sum_valuesBelowMidRanged =
                deviation_sum_valuesBelowMidRanged +
                Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] - mean_1, 2);
            valuesBelowMidRanged++;
          }
          else {
            deviation_sum_valuesAboveMidRanged =
                deviation_sum_valuesAboveMidRanged +
                Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] - mean_2, 2);
            valuesAboveMidRanged++;
          }
        }
      }
      std_1 = Math.sqrt(deviation_sum_valuesBelowMidRanged /
                        valuesBelowMidRanged);
      std_2 = Math.sqrt(deviation_sum_valuesAboveMidRanged /
                        valuesAboveMidRanged);
    }
    else { // right Gaussian centered on mean_1
      mean_2 = (midRanged_a_ij + min_a_ij) / 2;
      double deviation_sum_valuesBelowMidRanged = 0;
      double deviation_sum_valuesAboveMidRanged = 0;
      int valuesBelowMidRanged = 0;
      int valuesAboveMidRanged = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <=
              midRanged_a_ij) {
            deviation_sum_valuesBelowMidRanged =
                deviation_sum_valuesBelowMidRanged +
                Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] - mean_2, 2);
            valuesBelowMidRanged++;
          }
          else {
            deviation_sum_valuesAboveMidRanged =
                deviation_sum_valuesAboveMidRanged +
                Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] - mean_1, 2);
            valuesAboveMidRanged++;
          }
        }
      }
      std_2 = Math.sqrt(deviation_sum_valuesBelowMidRanged /
                        valuesBelowMidRanged);
      std_1 = Math.sqrt(deviation_sum_valuesAboveMidRanged /
                        valuesAboveMidRanged);
    }

    // EM

    int numIteractions = 0;
    boolean convergence = false;
    double[][] E_Z_i1 = new double[this.originalExpressionMatrix.
        getNumberOfGenes()][this.
        originalExpressionMatrix.getNumberOfConditions()];
    double[][] E_Z_i2 = new double[this.originalExpressionMatrix.
        getNumberOfGenes()][this.
        originalExpressionMatrix.getNumberOfConditions()];
    double new_std_1 = 0;
    double new_mean_2 = 0;
    double new_std_2 = 0;

    while (numIteractions <= 500 && convergence == false) {

      // STEP 1
      // compute E [Z_i1]
      // compute E [Z_i2]
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          double num1 = Math.exp( -1 / (2 * Math.pow(std_1, 2)) *
                                 Math.pow(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j] -
                                          mean_1, 2));
          double num2 = Math.exp( -1 / (2 * Math.pow(std_2, 2)) *
                                 Math.pow(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j] -
                                          mean_2, 2));
          double den = Math.exp( -1 / (2 * Math.pow(std_1, 2)) *
                                Math.pow(this.originalExpressionMatrix.
                                         geneExpressionMatrix[i][j] -
                                         mean_1, 2)) +
              Math.exp( -1 / (2 * Math.pow(std_2, 2)) *
                       Math.pow(this.originalExpressionMatrix.
                                geneExpressionMatrix[i][j] - mean_2, 2));
          E_Z_i1[i][j] = num1 / den;
          E_Z_i2[i][j] = num2 / den;
        }
      }

      // STEP 2
      // new std_1
      double sum_E_Z_i1 = 0;
      double sum_E_Z_i1_x_i_mean_1 = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i1_x_i_mean_1 = sum_E_Z_i1_x_i_mean_1 +
              Math.pow(E_Z_i1[i][j] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       mean_1, 2);
          sum_E_Z_i1 = sum_E_Z_i1 + E_Z_i1[i][j];
        }
      }
      new_std_1 = Math.sqrt(sum_E_Z_i1_x_i_mean_1 / sum_E_Z_i1);
      // new mean_2
      double sum_E_Z_i2 = 0;
      double sum_E_Z_i2_x_i = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i2_x_i = sum_E_Z_i2_x_i +
              (E_Z_i2[i][j] *
               this.originalExpressionMatrix.geneExpressionMatrix[i][j]);
          sum_E_Z_i2 = sum_E_Z_i2 + E_Z_i2[i][j];
        }
      }
      new_mean_2 = sum_E_Z_i2_x_i / sum_E_Z_i2;
      // new std_2
      double sum_E_Z_i2_x_i_mean_2 = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i2_x_i_mean_2 = sum_E_Z_i2_x_i_mean_2 +
              Math.pow(E_Z_i2[i][j] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       mean_2, 2);
        }
      }
      new_std_2 = Math.sqrt(sum_E_Z_i2_x_i_mean_2 / sum_E_Z_i2);

      // update stopping conditions
      convergence = Math.abs(std_1 - new_std_1) <= 0.00001 &&
          Math.abs(mean_2 - new_mean_2) <= 0.00001 &&
          Math.abs(std_2 - new_std_2) <= 0.00001;
      numIteractions++;
      /*
            System.out.println("COMPUTING MIXTURE MODELS - TWO GAUSSIANS: Iteraction = " + numIteractions);
            System.out.println("mean_1 = " + mean_1);
       System.out.println("std_1 = " + std_1 + "\t" + "new_std_1 = " + new_std_1);
       System.out.println("mean_2 = " + mean_2 + "\t" + "new_mean_2 = " + new_mean_2);
       System.out.println("std_2 = " + std_2 + "\t" + "new_std_2 = " + new_std_2);
       */
      // prepare next iteraction
      std_1 = new_std_1;
      mean_2 = new_mean_2;
      std_2 = new_std_2;
    }

    // return model
    MixtureModelTwoGaussians model = new MixtureModelTwoGaussians(this.
        originalExpressionMatrix.getNumberOfGenes() *
        this.originalExpressionMatrix.getNumberOfConditions(),
        mean_1, std_1, E_Z_i1, mean_2, std_2, E_Z_i2);
    return (model);
  }

  /**
   * Compute a mixture model with two Gaussians per gene.
   * One Gaussian has mean on the mean of each gene and estimated standard deviation.
   * Other Gaussian has mean and standard deviation estimated for each gene using EM.
   *
   * @return MixtureModelTwoGaussians[] One for each gene.
   */
  public MixtureModelTwoGaussians[] computeMixtureModel_TwoGaussians_ByGene() {

    System.out.println("COMPUTING MIXTURE MODELS BY GENE - TWO GAUSSIANS");

    MixtureModelTwoGaussians[] modelsByGene = new MixtureModelTwoGaussians[this.
        originalExpressionMatrix.getNumberOfGenes()];

    // compute mean of all values by gene
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      modelsByGene[i] = new MixtureModelTwoGaussians();
      modelsByGene[i].setSampleSize(this.originalExpressionMatrix.
                                    getNumberOfConditions());
      double sum_a_iJ = 0;
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        sum_a_iJ = sum_a_iJ +
            this.originalExpressionMatrix.geneExpressionMatrix[i][j];
      }
      modelsByGene[i].setMean_1(sum_a_iJ /
                                this.originalExpressionMatrix.
                                getNumberOfConditions());
    }

    // compute initial values for mean_2, std_1 and std_2 by gene
    float[] min_a_iJ = new float[this.originalExpressionMatrix.getNumberOfGenes()];
    float[] max_a_iJ = new float[this.originalExpressionMatrix.getNumberOfGenes()];
    float[] midRanged_a_iJ = new float[this.originalExpressionMatrix.
        getNumberOfGenes()];
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      min_a_iJ[i] = this.originalExpressionMatrix.geneExpressionMatrix[i][0];
      max_a_iJ[i] = this.originalExpressionMatrix.geneExpressionMatrix[i][0];
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <
            min_a_iJ[i]) {
          min_a_iJ[i] = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
        }
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
            max_a_iJ[i]) {
          max_a_iJ[i] = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
        }
      }
      midRanged_a_iJ[i] = (max_a_iJ[i] + min_a_iJ[i]) / 2;
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      if (modelsByGene[i].getMean_1() <= midRanged_a_iJ[i]) { // left Gaussian centered on mean_1
        modelsByGene[i].setMean_2( (max_a_iJ[i] + midRanged_a_iJ[i]) / 2);
        double deviation_sum_valuesBelowMidRanged_i = 0;
        double deviation_sum_valuesAboveMidRanged_i = 0;
        int valuesBelowMidRanged_i = 0;
        int valuesAboveMidRanged_i = 0;
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <=
              midRanged_a_iJ[i]) {
            deviation_sum_valuesBelowMidRanged_i =
                deviation_sum_valuesBelowMidRanged_i +
                Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] - modelsByGene[i].getMean_1(), 2);
            valuesBelowMidRanged_i++;
          }
          else {
            deviation_sum_valuesAboveMidRanged_i =
                deviation_sum_valuesAboveMidRanged_i +
                Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] - modelsByGene[i].getMean_2(), 2);
            valuesAboveMidRanged_i++;
          }
        }
        modelsByGene[i].setStd_1(Math.sqrt(deviation_sum_valuesBelowMidRanged_i /
                                           valuesBelowMidRanged_i));
        modelsByGene[i].setStd_2(Math.sqrt(deviation_sum_valuesAboveMidRanged_i /
                                           valuesAboveMidRanged_i));
      }
      else { // right Gaussian centered on mean_1
        modelsByGene[i].setMean_2( (midRanged_a_iJ[i] + min_a_iJ[i]) / 2);
        double deviation_sum_valuesBelowMidRanged_i = 0;
        double deviation_sum_valuesAboveMidRanged_i = 0;
        int valuesBelowMidRanged_i = 0;
        int valuesAboveMidRanged_i = 0;
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <=
              midRanged_a_iJ[i]) {
            deviation_sum_valuesBelowMidRanged_i =
                deviation_sum_valuesBelowMidRanged_i +
                Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] - modelsByGene[i].getMean_2(), 2);
            valuesBelowMidRanged_i++;
          }
          else {
            deviation_sum_valuesAboveMidRanged_i =
                deviation_sum_valuesAboveMidRanged_i +
                Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] - modelsByGene[i].getMean_1(), 2);
            valuesAboveMidRanged_i++;
          }
        }
        modelsByGene[i].setStd_2(Math.sqrt(deviation_sum_valuesBelowMidRanged_i /
                                           valuesBelowMidRanged_i));
        modelsByGene[i].setStd_1(Math.sqrt(deviation_sum_valuesAboveMidRanged_i /
                                           valuesAboveMidRanged_i));
      }
    }

    // EM

    int numIteractions = 0;
    boolean convergence = false;
    double[][] E_Z_i1 = new double[1][this.originalExpressionMatrix.
        getNumberOfConditions()];
    double[][] E_Z_i2 = new double[1][this.originalExpressionMatrix.
        getNumberOfConditions()];
    double new_std_1_i = 0;
    double new_mean_2_i = 0;
    double new_std_2_i = 0;

    // FOR EACH GENE
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {

//      System.out.println("GENE = " + i);

      numIteractions = 0;
      convergence = false;

      // BEGIN EM ITERACTIONS
      while (numIteractions <= 500 && convergence == false) {

        // STEP 1
        // compute E [Z_i1]
        // compute E [Z_i2]
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          double num1 = Math.exp( -1 /
                                 (2 * Math.pow(modelsByGene[i].getStd_1(), 2)) *
                                 Math.pow(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j] -
                                          modelsByGene[i].getMean_1(), 2));
          double num2 = Math.exp( -1 /
                                 (2 * Math.pow(modelsByGene[i].getStd_2(), 2)) *
                                 Math.pow(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j] -
                                          modelsByGene[i].getMean_2(), 2));
          double den = Math.exp( -1 /
                                (2 * Math.pow(modelsByGene[i].getStd_1(), 2)) *
                                Math.pow(this.originalExpressionMatrix.
                                         geneExpressionMatrix[i][j] -
                                         modelsByGene[i].getMean_1(), 2)) +
              Math.exp( -1 / (2 * Math.pow(modelsByGene[i].getStd_2(), 2)) *
                       Math.pow(this.originalExpressionMatrix.
                                geneExpressionMatrix[i][j] -
                                modelsByGene[i].getMean_2(), 2));
          E_Z_i1[0][j] = num1 / den;
          E_Z_i2[0][j] = num2 / den;
        }

        // STEP 2
        // new std_1
        double sum_E_Z_i1_i = 0;
        double sum_E_Z_i1_x_i_mean_1_i = 0;
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i1_x_i_mean_1_i = sum_E_Z_i1_x_i_mean_1_i +
              Math.pow(E_Z_i1[0][j] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByGene[i].getMean_1(), 2);
          sum_E_Z_i1_i = sum_E_Z_i1_i + E_Z_i1[0][j];
        }
        new_std_1_i = Math.sqrt(sum_E_Z_i1_x_i_mean_1_i / sum_E_Z_i1_i);
        // new mean_2
        double sum_E_Z_i2_i = 0;
        double sum_E_Z_i2_x_i = 0;
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i2_x_i = sum_E_Z_i2_x_i +
              (E_Z_i2[0][j] *
               this.originalExpressionMatrix.geneExpressionMatrix[i][j]);
          sum_E_Z_i2_i = sum_E_Z_i2_i + E_Z_i2[0][j];
        }
        new_mean_2_i = sum_E_Z_i2_x_i / sum_E_Z_i2_i;
        // new std_2
        double sum_E_Z_i2_x_i_mean_2_i = 0;
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i2_x_i_mean_2_i = sum_E_Z_i2_x_i_mean_2_i +
              Math.pow(E_Z_i2[0][j] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByGene[i].getMean_2(), 2);
        }
        new_std_2_i = Math.sqrt(sum_E_Z_i2_x_i_mean_2_i / sum_E_Z_i2_i);

        // update stopping conditions
        convergence = Math.abs(modelsByGene[i].getStd_1() - new_std_1_i) <=
            0.00001 &&
            Math.abs(modelsByGene[i].getMean_2() - new_mean_2_i) <= 0.00001 &&
            Math.abs(modelsByGene[i].getStd_2() - new_std_2_i) <= 0.00001;
        numIteractions++;
        /*
                  System.out.println("COMPUTING MIXTURE MODELS BY GENE - TWO GAUSSIANS: Iteraction = " + numIteractions);
         System.out.println("mean_1 = " + modelsByGene[i].getMean_1());
                  System.out.println("std_1 = " + modelsByGene[i].getStd_1() + "\t" + "new_std_1 = " + new_std_1_i);
                  System.out.println("mean_2 = " + modelsByGene[i].getMean_2() + "\t" + "new_mean_2 = " + new_mean_2_i);
                  System.out.println("std_2 = " + modelsByGene[i].getStd_2() + "\t" + "new_std_2 = " + new_std_2_i);
         */
        // prepare next iteraction
        modelsByGene[i].setStd_1(new_std_1_i);
        modelsByGene[i].setMean_2(new_mean_2_i);
        modelsByGene[i].setStd_2(new_std_2_i);
      } // END EM ITERATION

      modelsByGene[i].setE_Z_i1(E_Z_i1);
      modelsByGene[i].setE_Z_i2(E_Z_i2);
    }
    return (modelsByGene);
  }

  /**
   * Compute a mixture model with two Gaussians per condition.
   * One Gaussian has mean on the mean of each condition and estimated standard deviation.
   * Other Gaussian has mean and standard deviation estimated for each gene using EM.
   *
   * @return MixtureModelTwoGaussians[] One for each condition.
   */
  public MixtureModelTwoGaussians[]
      computeMixtureModel_TwoGaussians_ByCondition() {

    System.out.println("COMPUTING MIXTURE MODELS BY CONDITION - TWO GAUSSIANS");

    MixtureModelTwoGaussians[] modelsByCondition = new MixtureModelTwoGaussians[this.
        originalExpressionMatrix.getNumberOfConditions()];

    // compute mean of all values by Condition
    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      modelsByCondition[j] = new MixtureModelTwoGaussians();
      modelsByCondition[j].setSampleSize(this.originalExpressionMatrix.
                                         getNumberOfGenes());
      double sum_a_Ij = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        sum_a_Ij = sum_a_Ij +
            this.originalExpressionMatrix.geneExpressionMatrix[i][j];
      }
      modelsByCondition[j].setMean_1(sum_a_Ij /
                                     this.originalExpressionMatrix.
                                     getNumberOfGenes());
    }

    // compute initial values for mean_2, std_1 and std_2 by Condition
    float[] min_a_Ij = new float[this.originalExpressionMatrix.
        getNumberOfConditions()];
    float[] max_a_Ij = new float[this.originalExpressionMatrix.
        getNumberOfConditions()];
    float[] midRanged_a_Ij = new float[this.originalExpressionMatrix.
        getNumberOfConditions()];
    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      min_a_Ij[j] = this.originalExpressionMatrix.geneExpressionMatrix[0][j];
      max_a_Ij[j] = this.originalExpressionMatrix.geneExpressionMatrix[0][j];
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <
            min_a_Ij[j]) {
          min_a_Ij[j] = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
        }
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
            max_a_Ij[j]) {
          max_a_Ij[j] = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
        }
      }
      midRanged_a_Ij[j] = (max_a_Ij[j] + min_a_Ij[j]) / 2;
    }

    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      if (modelsByCondition[j].getMean_1() <= midRanged_a_Ij[j]) { // left Gaussian centered on mean_1
        modelsByCondition[j].setMean_2( (max_a_Ij[j] + midRanged_a_Ij[j]) / 2);
        double deviation_sum_valuesBelowMidRanged_j = 0;
        double deviation_sum_valuesAboveMidRanged_j = 0;
        int valuesBelowMidRanged_j = 0;
        int valuesAboveMidRanged_j = 0;
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <=
              midRanged_a_Ij[j]) {
            deviation_sum_valuesBelowMidRanged_j =
                deviation_sum_valuesBelowMidRanged_j +
                Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] - modelsByCondition[j].getMean_1(), 2);
            valuesBelowMidRanged_j++;
          }
          else {
            deviation_sum_valuesAboveMidRanged_j =
                deviation_sum_valuesAboveMidRanged_j +
                Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] - modelsByCondition[j].getMean_2(), 2);
            valuesAboveMidRanged_j++;
          }
        }
        modelsByCondition[j].setStd_1(Math.sqrt(
            deviation_sum_valuesBelowMidRanged_j / valuesBelowMidRanged_j));
        modelsByCondition[j].setStd_2(Math.sqrt(
            deviation_sum_valuesAboveMidRanged_j / valuesAboveMidRanged_j));
      }
      else { // right Gaussian centered on mean_1
        modelsByCondition[j].setMean_2( (midRanged_a_Ij[j] + min_a_Ij[j]) / 2);
        double deviation_sum_valuesBelowMidRanged_j = 0;
        double deviation_sum_valuesAboveMidRanged_j = 0;
        int valuesBelowMidRanged_j = 0;
        int valuesAboveMidRanged_j = 0;
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <=
              midRanged_a_Ij[j]) {
            deviation_sum_valuesBelowMidRanged_j =
                deviation_sum_valuesBelowMidRanged_j +
                Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] - modelsByCondition[j].getMean_2(), 2);
            valuesBelowMidRanged_j++;
          }
          else {
            deviation_sum_valuesAboveMidRanged_j =
                deviation_sum_valuesAboveMidRanged_j +
                Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] - modelsByCondition[j].getMean_1(), 2);
            valuesAboveMidRanged_j++;
          }
        }
        modelsByCondition[j].setStd_2(Math.sqrt(
            deviation_sum_valuesBelowMidRanged_j / valuesBelowMidRanged_j));
        modelsByCondition[j].setStd_1(Math.sqrt(
            deviation_sum_valuesAboveMidRanged_j / valuesAboveMidRanged_j));
      }
    }

    // EM

    int numIteractions = 0;
    boolean convergence = false;
    double[][] E_Z_i1 = new double[this.originalExpressionMatrix.
        getNumberOfGenes()][
        1];
    double[][] E_Z_i2 = new double[this.originalExpressionMatrix.
        getNumberOfGenes()][
        1];
    double new_std_1_j = 0;
    double new_mean_2_j = 0;
    double new_std_2_j = 0;

    // FOR EACH CONDITION
    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {

//      System.out.println("CONDITION = " + j);

      numIteractions = 0;
      convergence = false;

      // BEGIN EM ITERACTIONS
      while (numIteractions <= 500 && convergence == false) {

        // STEP 1
        // compute E [Z_i1]
        // compute E [Z_i2]
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          double num1 = Math.exp( -1 /
                                 (2 *
                                  Math.pow(modelsByCondition[j].getStd_1(), 2)) *
                                 Math.pow(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j] -
                                          modelsByCondition[j].getMean_1(), 2));
          double num2 = Math.exp( -1 /
                                 (2 *
                                  Math.pow(modelsByCondition[j].getStd_2(), 2)) *
                                 Math.pow(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j] -
                                          modelsByCondition[j].getMean_2(), 2));
          double den = Math.exp( -1 /
                                (2 *
                                 Math.pow(modelsByCondition[j].getStd_1(), 2)) *
                                Math.pow(this.originalExpressionMatrix.
                                         geneExpressionMatrix[i][j] -
                                         modelsByCondition[j].getMean_1(), 2)) +
              Math.exp( -1 / (2 * Math.pow(modelsByCondition[j].getStd_2(), 2)) *
                       Math.pow(this.originalExpressionMatrix.
                                geneExpressionMatrix[i][j] -
                                modelsByCondition[j].getMean_2(), 2));
          E_Z_i1[i][0] = num1 / den;
          E_Z_i2[i][0] = num2 / den;
        }

        // STEP 2
        // new std_1
        double sum_E_Z_i1_j = 0;
        double sum_E_Z_i1_x_i_mean_1_j = 0;
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          sum_E_Z_i1_x_i_mean_1_j = sum_E_Z_i1_x_i_mean_1_j +
              Math.pow(E_Z_i1[i][0] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByCondition[j].getMean_1(), 2);
          sum_E_Z_i1_j = sum_E_Z_i1_j + E_Z_i1[i][0];
        }
        new_std_1_j = Math.sqrt(sum_E_Z_i1_x_i_mean_1_j / sum_E_Z_i1_j);
        // new mean_2
        double sum_E_Z_i2_j = 0;
        double sum_E_Z_i2_x_j = 0;
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          sum_E_Z_i2_x_j = sum_E_Z_i2_x_j +
              (E_Z_i2[i][0] *
               this.originalExpressionMatrix.geneExpressionMatrix[i][j]);
          sum_E_Z_i2_j = sum_E_Z_i2_j + E_Z_i2[i][0];
        }
        new_mean_2_j = sum_E_Z_i2_x_j / sum_E_Z_i2_j;
        // new std_2
        double sum_E_Z_i2_x_i_mean_2_j = 0;
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          sum_E_Z_i2_x_i_mean_2_j = sum_E_Z_i2_x_i_mean_2_j +
              Math.pow(E_Z_i2[i][0] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByCondition[j].getMean_2(), 2);
        }
        new_std_2_j = Math.sqrt(sum_E_Z_i2_x_i_mean_2_j / sum_E_Z_i2_j);

        // update stopping conditions
        convergence = Math.abs(modelsByCondition[j].getStd_1() - new_std_1_j) <=
            0.00001 &&
            Math.abs(modelsByCondition[j].getMean_2() - new_mean_2_j) <=
            0.00001 &&
            Math.abs(modelsByCondition[j].getStd_2() - new_std_2_j) <= 0.00001;
        numIteractions++;
        /*
                  System.out.println("COMPUTING MIXTURE MODELS BY CONDITION - TWO GAUSSIANS: Iteraction = " + numIteractions);
         System.out.println("mean_1 = " + modelsByCondition[j].getMean_1());
                  System.out.println("std_1 = " + modelsByCondition[j].getStd_1() + "\t" + "new_std_1 = " + new_std_1_j);
                  System.out.println("mean_2 = " + modelsByCondition[j].getMean_2() + "\t" + "new_mean_2 = " + new_mean_2_j);
                  System.out.println("std_2 = " + modelsByCondition[j].getStd_2() + "\t" + "new_std_2 = " + new_std_2_j);
         */
        // prepare next iteraction
        modelsByCondition[j].setStd_1(new_std_1_j);
        modelsByCondition[j].setMean_2(new_mean_2_j);
        modelsByCondition[j].setStd_2(new_std_2_j);
      } // END EM ITERACTION

      modelsByCondition[j].setE_Z_i1(E_Z_i1);
      modelsByCondition[j].setE_Z_i2(E_Z_i2);
    }

    return (modelsByCondition);
  }

  // ######################## THREE GAUSSIANS

  /**
   * Compute a mixture model with three Gaussians.
   * One Gaussian has mean on the mean of the expression matrix and estimated standard deviation.
   * Other two Gaussians have mean and standard deviation estimated using EM.
   *
   * @return MixtureModelThreeGaussians
   */

  public MixtureModelThreeGaussians
      computeMixtureModel_ThreeGaussians_OverallMatrix() {

    System.out.println("COMPUTING MIXTURE MODEL - THREE GAUSSIANS");

    double mean_1 = 0; // fixed
    double mean_2 = 0;
    double mean_3 = 0;
    double std_1 = 0;
    double std_2 = 0;
    double std_3 = 0;

    // compute mean of all values in the matrix
    double sum_a_ij = 0;
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        sum_a_ij = sum_a_ij +
            this.originalExpressionMatrix.geneExpressionMatrix[i][j];
      }
    }
    mean_1 = sum_a_ij /
        (this.originalExpressionMatrix.getNumberOfGenes() *
         this.originalExpressionMatrix.getNumberOfConditions());

    // compute initial values for mean_2, mean_3, std_1, std_2 and std_3
    float min_a_ij = this.originalExpressionMatrix.geneExpressionMatrix[0][0];
    float max_a_ij = this.originalExpressionMatrix.geneExpressionMatrix[0][0];
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] < min_a_ij) {
          min_a_ij = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
        }
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] > max_a_ij) {
          max_a_ij = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
        }
      }
    }

    mean_2 = (mean_1 + min_a_ij) / 2;
    mean_3 = (mean_1 + max_a_ij) / 2;

    double deviation_sum_valuesBelowMean1 = 0;
    double deviation_sum_allValues = 0;
    double deviation_sum_valuesAboveMean1 = 0;
    int valuesBelowMean1 = 0;
    int valuesAboveMean1 = 0;
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] < mean_1) {
          deviation_sum_valuesBelowMean1 =
              deviation_sum_valuesBelowMean1 +
              Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       mean_2, 2);
          valuesBelowMean1++;
        }
        else {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] > mean_1) {
            deviation_sum_valuesAboveMean1 = deviation_sum_valuesAboveMean1 +
                Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] - mean_3, 2);
            valuesAboveMean1++;
          }

          deviation_sum_allValues = deviation_sum_allValues +
              Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       mean_1, 2);
        }
      }

      std_1 = Math.sqrt(deviation_sum_allValues /
                        this.originalExpressionMatrix.getNumberOfGenes() *
                        this.originalExpressionMatrix.getNumberOfConditions());
      std_2 = Math.sqrt(deviation_sum_valuesAboveMean1 / valuesBelowMean1);
      std_3 = Math.sqrt(deviation_sum_valuesAboveMean1 / valuesAboveMean1);
    }

    // EM

    int numIteractions = 0;
    boolean convergence = false;
    double[][] E_Z_i1 = new double[this.originalExpressionMatrix.
        getNumberOfGenes()][this.
        originalExpressionMatrix.getNumberOfConditions()];
    double[][] E_Z_i2 = new double[this.originalExpressionMatrix.
        getNumberOfGenes()][this.
        originalExpressionMatrix.getNumberOfConditions()];
    double[][] E_Z_i3 = new double[this.originalExpressionMatrix.
        getNumberOfGenes()][this.
        originalExpressionMatrix.getNumberOfConditions()];
    double new_std_1 = 0;
    double new_mean_2 = 0;
    double new_std_2 = 0;
    double new_mean_3 = 0;
    double new_std_3 = 0;

    while (numIteractions <= 500 && convergence == false) {

      // STEP 1
      // compute E [Z_i1]
      // compute E [Z_i2]
      // compute E [Z_i3]
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          double num1 = Math.exp( -1 / (2 * Math.pow(std_1, 2)) *
                                 Math.pow(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j] - mean_1,
                                          2));
          double num2 = Math.exp( -1 / (2 * Math.pow(std_2, 2)) *
                                 Math.pow(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j] - mean_2,
                                          2));
          double num3 = Math.exp( -1 / (2 * Math.pow(std_3, 2)) *
                                 Math.pow(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j] - mean_3,
                                          2));
          double den =
              Math.exp( -1 / (2 * Math.pow(std_1, 2)) *
                       Math.pow(this.originalExpressionMatrix.
                                geneExpressionMatrix[i][j] - mean_1, 2)) +
              Math.exp( -1 / (2 * Math.pow(std_2, 2)) *
                       Math.pow(this.originalExpressionMatrix.
                                geneExpressionMatrix[i][j] - mean_2, 2)) +
              Math.exp( -1 / (2 * Math.pow(std_3, 2)) *
                       Math.pow(this.originalExpressionMatrix.
                                geneExpressionMatrix[i][j] - mean_3, 2));
          E_Z_i1[i][j] = num1 / den;
          E_Z_i2[i][j] = num2 / den;
          E_Z_i3[i][j] = num3 / den;
        }
      }

      // STEP 2
      // new std_1
      double sum_E_Z_i1 = 0;
      double sum_E_Z_i1_x_i_mean_1 = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i1_x_i_mean_1 = sum_E_Z_i1_x_i_mean_1 +
              Math.pow(E_Z_i1[i][j] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       mean_1, 2);
          sum_E_Z_i1 = sum_E_Z_i1 + E_Z_i1[i][j];
        }
      }
      new_std_1 = Math.sqrt(sum_E_Z_i1_x_i_mean_1 / sum_E_Z_i1);
      // new mean_2
      double sum_E_Z_i2 = 0;
      double sum_E_Z_i2_x_i = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i2_x_i = sum_E_Z_i2_x_i +
              (E_Z_i2[i][j] *
               this.originalExpressionMatrix.geneExpressionMatrix[i][j]);
          sum_E_Z_i2 = sum_E_Z_i2 + E_Z_i2[i][j];
        }
      }
      new_mean_2 = sum_E_Z_i2_x_i / sum_E_Z_i2;
      // new std_2
      double sum_E_Z_i2_x_i_mean_2 = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i2_x_i_mean_2 = sum_E_Z_i2_x_i_mean_2 +
              Math.pow(E_Z_i2[i][j] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       mean_2, 2);
        }
      }
      new_std_2 = Math.sqrt(sum_E_Z_i2_x_i_mean_2 / sum_E_Z_i2);
      // new mean_3
      double sum_E_Z_i3 = 0;
      double sum_E_Z_i3_x_i = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i3_x_i = sum_E_Z_i3_x_i +
              (E_Z_i3[i][j] *
               this.originalExpressionMatrix.geneExpressionMatrix[i][j]);
          sum_E_Z_i3 = sum_E_Z_i3 + E_Z_i3[i][j];
        }
      }
      new_mean_3 = sum_E_Z_i3_x_i / sum_E_Z_i3;
      // new std_3
      double sum_E_Z_i3_x_i_mean_3 = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i3_x_i_mean_3 = sum_E_Z_i3_x_i_mean_3 +
              Math.pow(E_Z_i3[i][j] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       mean_3, 2);
        }
      }
      new_std_3 = Math.sqrt(sum_E_Z_i3_x_i_mean_3 / sum_E_Z_i3);

      // update stopping conditions
      convergence = Math.abs(std_1 - new_std_1) <= 0.00001 &&
          Math.abs(mean_2 - new_mean_2) <= 0.00001 &&
          Math.abs(std_2 - new_std_2) <= 0.00001 &&
          Math.abs(mean_3 - new_mean_3) <= 0.00001 &&
          Math.abs(std_3 - new_std_3) <= 0.00001;

      numIteractions++;
      /*
            System.out.println("FITTING MIXTURE MODEL - THREE GAUSSIANS: Iteraction = " + numIteractions);
            System.out.println("mean_1 = " + mean_1);
       System.out.println("std_1 = " + std_1 + "\t" + "new_std_1 = " + new_std_1);
       System.out.println("mean_2 = " + mean_2 + "\t" + "new_mean_2 = " + new_mean_2);
       System.out.println("std_2 = " + std_2 + "\t" + "new_std_2 = " + new_std_2);
       System.out.println("mean_3 = " + mean_3 + "\t" + "new_mean_3 = " + new_mean_3);
       System.out.println("std_3 = " + std_3 + "\t" + "new_std_3 = " + new_std_3);
       */
      // prepare next iteraction
      std_1 = new_std_1;
      mean_2 = new_mean_2;
      std_2 = new_std_2;
      mean_3 = new_mean_3;
      std_3 = new_std_3;
    }

    // return model
    MixtureModelThreeGaussians model = new MixtureModelThreeGaussians(this.
        originalExpressionMatrix.getNumberOfGenes() *
        this.originalExpressionMatrix.getNumberOfConditions(),
        mean_1, std_1, E_Z_i1, mean_2, std_2, E_Z_i2,
        mean_3, std_3, E_Z_i3);
    return (model);
  }

  /**
   * Compute a mixture model with three Gaussians for each gene.
   * One Gaussian has mean on the mean of the gene and estimated standard deviation.
   * Other two Gaussians have mean and standard deviation estimated using EM for each gene.
   *
   * @return MixtureModelThreeGaussians[] One for each gene.
   */

  public MixtureModelThreeGaussians[] computeMixtureModel_ThreeGaussians_ByGene() {

    System.out.println("COMPUTING MIXTURE MODELS BY GENE - THREE GAUSSIANS");

    MixtureModelThreeGaussians[] modelsByGene = new MixtureModelThreeGaussians[this.
        originalExpressionMatrix.getNumberOfGenes()];

    // compute mean of all values by gene
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      modelsByGene[i] = new MixtureModelThreeGaussians();
      modelsByGene[i].setSampleSize(this.originalExpressionMatrix.
                                    getNumberOfConditions());
      double sum_a_iJ = 0;
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        sum_a_iJ = sum_a_iJ +
            this.originalExpressionMatrix.geneExpressionMatrix[i][j];
      }
      modelsByGene[i].setMean_1(sum_a_iJ /
                                this.originalExpressionMatrix.
                                getNumberOfConditions());
    }

    // compute initial values for mean_2, std_1 and std_2 by gene
    float[] min_a_iJ = new float[this.originalExpressionMatrix.getNumberOfGenes()];
    float[] max_a_iJ = new float[this.originalExpressionMatrix.getNumberOfGenes()];
    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      min_a_iJ[i] = this.originalExpressionMatrix.geneExpressionMatrix[i][0];
      max_a_iJ[i] = this.originalExpressionMatrix.geneExpressionMatrix[i][0];
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <
            min_a_iJ[i]) {
          min_a_iJ[i] = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
        }
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
            max_a_iJ[i]) {
          max_a_iJ[i] = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
        }
      }
    }

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
      modelsByGene[i].setMean_2( (modelsByGene[i].getMean_1() + min_a_iJ[i]) /
                                2);
      modelsByGene[i].setMean_3( (modelsByGene[i].getMean_1() + max_a_iJ[i]) /
                                2);
      double deviation_sum_valuesBelowMean1_i = 0;
      double deviation_sum_allValues_i = 0;
      double deviation_sum_valuesAboveMean1_i = 0;
      int valuesBelowMean1_i = 0;
      int valuesAboveMean1_i = 0;
      for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
           j++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <
            modelsByGene[i].getMean_1()) {
          deviation_sum_valuesBelowMean1_i =
              deviation_sum_valuesBelowMean1_i +
              Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByGene[i].getMean_2(), 2);
          valuesBelowMean1_i++;
        }
        else {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              modelsByGene[i].getMean_1()) {
            deviation_sum_valuesAboveMean1_i = deviation_sum_valuesAboveMean1_i +
                Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] -
                         modelsByGene[i].getMean_3(), 2);
            valuesAboveMean1_i++;
          }

          deviation_sum_allValues_i = deviation_sum_allValues_i +
              Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByGene[i].getMean_1(), 2);
        }
      }

      modelsByGene[i].setStd_1(Math.sqrt(deviation_sum_allValues_i /
                                         this.originalExpressionMatrix.
                                         getNumberOfConditions()));
      modelsByGene[i].setStd_2(Math.sqrt(deviation_sum_valuesAboveMean1_i /
                                         valuesBelowMean1_i));
      modelsByGene[i].setStd_3(Math.sqrt(deviation_sum_valuesAboveMean1_i /
                                         valuesAboveMean1_i));
    }

    // EM

    int numIteractions = 0;
    boolean convergence = false;
    double[][] E_Z_i1 = new double[1][this.originalExpressionMatrix.
        getNumberOfConditions()];
    double[][] E_Z_i2 = new double[1][this.originalExpressionMatrix.
        getNumberOfConditions()];
    double[][] E_Z_i3 = new double[1][this.originalExpressionMatrix.
        getNumberOfConditions()];
    double new_std_1_i = 0;
    double new_mean_2_i = 0;
    double new_std_2_i = 0;
    double new_mean_3_i = 0;
    double new_std_3_i = 0;

    // FOR EACH GENE

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {

//      System.out.println("GENE = " + i);

      numIteractions = 0;
      convergence = false;

      // BEGIN EM ITERACTIONS
      while (numIteractions <= 500 && convergence == false) {

        // STEP 1
        // compute E [Z_i1]
        // compute E [Z_i2]
        // compute E [Z_i3]
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          double num1 = Math.exp( -1 /
                                 (2 * Math.pow(modelsByGene[i].getStd_1(), 2)) *
                                 Math.pow(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j] -
                                          modelsByGene[i].getMean_1(), 2));
          double num2 = Math.exp( -1 /
                                 (2 * Math.pow(modelsByGene[i].getStd_2(), 2)) *
                                 Math.pow(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j] -
                                          modelsByGene[i].getMean_2(), 2));
          double num3 = Math.exp( -1 /
                                 (2 * Math.pow(modelsByGene[i].getStd_3(), 2)) *
                                 Math.pow(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j] -
                                          modelsByGene[i].getMean_3(), 2));
          double den =
              Math.exp( -1 / (2 * Math.pow(modelsByGene[i].getStd_1(), 2)) *
                       Math.pow(this.originalExpressionMatrix.
                                geneExpressionMatrix[i][j] -
                                modelsByGene[i].getMean_1(), 2)) +
              Math.exp( -1 / (2 * Math.pow(modelsByGene[i].getStd_2(), 2)) *
                       Math.pow(this.originalExpressionMatrix.
                                geneExpressionMatrix[i][j] -
                                modelsByGene[i].getMean_2(), 2)) +
              Math.exp( -1 / (2 * Math.pow(modelsByGene[i].getStd_3(), 2)) *
                       Math.pow(this.originalExpressionMatrix.
                                geneExpressionMatrix[i][j] -
                                modelsByGene[i].getMean_3(), 2));
          E_Z_i1[0][j] = num1 / den;
          E_Z_i2[0][j] = num2 / den;
          E_Z_i3[0][j] = num3 / den;
        }

        // STEP 2
        // new std_1
        double sum_E_Z_i1_i = 0;
        double sum_E_Z_i1_x_i_mean_1_i = 0;
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i1_x_i_mean_1_i = sum_E_Z_i1_x_i_mean_1_i +
              Math.pow(E_Z_i1[0][j] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByGene[i].getMean_1(), 2);
          sum_E_Z_i1_i = sum_E_Z_i1_i + E_Z_i1[0][j];
        }
        new_std_1_i = Math.sqrt(sum_E_Z_i1_x_i_mean_1_i / sum_E_Z_i1_i);
        // new mean_2
        double sum_E_Z_i2_i = 0;
        double sum_E_Z_i2_x_i = 0;
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i2_x_i = sum_E_Z_i2_x_i +
              (E_Z_i2[0][j] *
               this.originalExpressionMatrix.geneExpressionMatrix[i][j]);
          sum_E_Z_i2_i = sum_E_Z_i2_i + E_Z_i2[0][j];
        }
        new_mean_2_i = sum_E_Z_i2_x_i / sum_E_Z_i2_i;
        // new std_2
        double sum_E_Z_i2_x_i_mean_2_i = 0;
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i2_x_i_mean_2_i = sum_E_Z_i2_x_i_mean_2_i +
              Math.pow(E_Z_i2[0][j] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByGene[i].getMean_2(), 2);
        }
        new_std_2_i = Math.sqrt(sum_E_Z_i2_x_i_mean_2_i / sum_E_Z_i2_i);
        // new mean_3
        double sum_E_Z_i3_i = 0;
        double sum_E_Z_i3_x_i = 0;
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i3_x_i = sum_E_Z_i3_x_i +
              (E_Z_i3[0][j] *
               this.originalExpressionMatrix.geneExpressionMatrix[i][j]);
          sum_E_Z_i3_i = sum_E_Z_i3_i + E_Z_i3[0][j];
        }
        new_mean_3_i = sum_E_Z_i3_x_i / sum_E_Z_i3_i;
        // new std_3
        double sum_E_Z_i3_x_i_mean_3_i = 0;
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          sum_E_Z_i3_x_i_mean_3_i = sum_E_Z_i3_x_i_mean_3_i +
              Math.pow(E_Z_i3[0][j] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByGene[i].getMean_3(), 2);
        }
        new_std_3_i = Math.sqrt(sum_E_Z_i3_x_i_mean_3_i / sum_E_Z_i3_i);

        // update stopping conditions
        convergence = Math.abs(modelsByGene[i].getStd_1() - new_std_1_i) <=
            0.00001 &&
            Math.abs(modelsByGene[i].getMean_2() - new_mean_2_i) <= 0.00001 &&
            Math.abs(modelsByGene[i].getStd_2() - new_std_2_i) <= 0.00001 &&
            Math.abs(modelsByGene[i].getMean_3() - new_mean_3_i) <= 0.00001 &&
            Math.abs(modelsByGene[i].getStd_3() - new_std_3_i) <= 0.00001;
        numIteractions++;
        /*
                System.out.println("FITTING MIXTURE MODELS - THREE GAUSSIANS BY GENE: Iteraction = " + numIteractions);
                System.out.println("mean_1 = " + modelsByGene[i].getMean_1());
                System.out.println("std_1 = " + modelsByGene[i].getStd_1() + "\t" + "new_std_1 = " + new_std_1_i);
                System.out.println("mean_2 = " + modelsByGene[i].getMean_2() + "\t" + "new_mean_2 = " + new_mean_2_i);
                System.out.println("std_2 = " + modelsByGene[i].getStd_2() + "\t" + "new_std_2 = " + new_std_2_i);
                System.out.println("mean_3 = " + modelsByGene[i].getMean_3() + "\t" + "new_mean_3 = " + new_mean_3_i);
                System.out.println("std_3 = " + modelsByGene[i].getStd_3() + "\t" + "new_std_3 = " + new_std_3_i);
         */
        // prepare next iteraction
        modelsByGene[i].setStd_1(new_std_1_i);
        modelsByGene[i].setMean_2(new_mean_2_i);
        modelsByGene[i].setStd_2(new_std_2_i);
        modelsByGene[i].setMean_3(new_mean_3_i);
        modelsByGene[i].setStd_3(new_std_3_i);
      }
      modelsByGene[i].setE_Z_i1(E_Z_i1);
      modelsByGene[i].setE_Z_i2(E_Z_i2);
      modelsByGene[i].setE_Z_i3(E_Z_i3);
    }
    return (modelsByGene);
  }

  /**
   * Compute a mixture model with three Gaussians for each condition.
   * One Gaussian has mean on the mean of the condition and estimated standard deviation.
   * Other two Gaussians have mean and standard deviation estimated using EM for each gene.
   *
   * @return MixtureModelThreeGaussians[] One for each condition.
   */

  public MixtureModelThreeGaussians[]
      computeMixtureModel_ThreeGaussians_ByCondition() {

    System.out.println(
        "COMPUTING MIXTURE MODELS BY CONDITION - THREE GAUSSIANS");

    MixtureModelThreeGaussians[] modelsByCondition = new
        MixtureModelThreeGaussians[this.originalExpressionMatrix.
        getNumberOfConditions()];

    // compute mean of all values by Condition
    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      modelsByCondition[j] = new MixtureModelThreeGaussians();
      modelsByCondition[j].setSampleSize(this.originalExpressionMatrix.
                                         getNumberOfGenes());
      double sum_a_Ij = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        sum_a_Ij = sum_a_Ij +
            this.originalExpressionMatrix.geneExpressionMatrix[i][j];
      }
      modelsByCondition[j].setMean_1(sum_a_Ij /
                                     this.originalExpressionMatrix.
                                     getNumberOfGenes());
    }

    // compute initial values for mean_2, std_1 and std_2 by gene
    float[] min_a_Ij = new float[this.originalExpressionMatrix.
        getNumberOfConditions()];
    float[] max_a_Ij = new float[this.originalExpressionMatrix.
        getNumberOfConditions()];
    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      min_a_Ij[j] = this.originalExpressionMatrix.geneExpressionMatrix[0][j];
      max_a_Ij[j] = this.originalExpressionMatrix.geneExpressionMatrix[0][j];
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <
            min_a_Ij[j]) {
          min_a_Ij[j] = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
        }
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
            max_a_Ij[j]) {
          max_a_Ij[j] = this.originalExpressionMatrix.geneExpressionMatrix[i][j];
        }
      }
    }

    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {
      modelsByCondition[j].setMean_2( (modelsByCondition[j].getMean_1() +
                                       min_a_Ij[j]) / 2);
      modelsByCondition[j].setMean_3( (modelsByCondition[j].getMean_1() +
                                       max_a_Ij[j]) / 2);
      double deviation_sum_valuesBelowMean1_j = 0;
      double deviation_sum_allValues_j = 0;
      double deviation_sum_valuesAboveMean1_j = 0;
      int valuesBelowMean1_j = 0;
      int valuesAboveMean1_j = 0;
      for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
        if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] <
            modelsByCondition[j].getMean_1()) {
          deviation_sum_valuesBelowMean1_j =
              deviation_sum_valuesBelowMean1_j +
              Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByCondition[j].getMean_2(), 2);
          valuesBelowMean1_j++;
        }
        else {
          if (this.originalExpressionMatrix.geneExpressionMatrix[i][j] >
              modelsByCondition[j].getMean_1()) {
            deviation_sum_valuesAboveMean1_j = deviation_sum_valuesAboveMean1_j +
                Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][
                         j] -
                         modelsByCondition[j].getMean_3(), 2);
            valuesAboveMean1_j++;
          }

          deviation_sum_allValues_j = deviation_sum_allValues_j +
              Math.pow(this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByCondition[j].getMean_1(), 2);
        }
      }

      modelsByCondition[j].setStd_1(Math.sqrt(deviation_sum_allValues_j /
                                              this.originalExpressionMatrix.
                                              getNumberOfGenes()));
      modelsByCondition[j].setStd_2(Math.sqrt(deviation_sum_valuesAboveMean1_j /
                                              valuesBelowMean1_j));
      modelsByCondition[j].setStd_3(Math.sqrt(deviation_sum_valuesAboveMean1_j /
                                              valuesAboveMean1_j));
    }

    // EM

    int numIteractions = 0;
    boolean convergence = false;
    double[][] E_Z_i1 = new double[this.originalExpressionMatrix.
        getNumberOfGenes()][
        1];
    double[][] E_Z_i2 = new double[this.originalExpressionMatrix.
        getNumberOfGenes()][
        1];
    double[][] E_Z_i3 = new double[this.originalExpressionMatrix.
        getNumberOfGenes()][
        1];
    double new_std_1_j = 0;
    double new_mean_2_j = 0;
    double new_std_2_j = 0;
    double new_mean_3_j = 0;
    double new_std_3_j = 0;

    // FOR EACH CONDITION

    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {

//      System.out.println("CONDITION = " + j);

      numIteractions = 0;
      convergence = false;

      // BEGIN EM ITERACTIONS
      while (numIteractions <= 500 && convergence == false) {

        // STEP 1
        // compute E [Z_i1]
        // compute E [Z_i2]
        // compute E [Z_i3]
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          double num1 = Math.exp( -1 /
                                 (2 *
                                  Math.pow(modelsByCondition[j].getStd_1(), 2)) *
                                 Math.pow(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j] -
                                          modelsByCondition[j].getMean_1(), 2));
          double num2 = Math.exp( -1 /
                                 (2 *
                                  Math.pow(modelsByCondition[j].getStd_2(), 2)) *
                                 Math.pow(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j] -
                                          modelsByCondition[j].getMean_2(), 2));
          double num3 = Math.exp( -1 /
                                 (2 *
                                  Math.pow(modelsByCondition[j].getStd_3(), 2)) *
                                 Math.pow(this.originalExpressionMatrix.
                                          geneExpressionMatrix[i][j] -
                                          modelsByCondition[j].getMean_3(), 2));
          double den =
              Math.exp( -1 / (2 * Math.pow(modelsByCondition[j].getStd_1(), 2)) *
                       Math.pow(this.originalExpressionMatrix.
                                geneExpressionMatrix[i][j] -
                                modelsByCondition[j].getMean_1(), 2)) +
              Math.exp( -1 / (2 * Math.pow(modelsByCondition[j].getStd_2(), 2)) *
                       Math.pow(this.originalExpressionMatrix.
                                geneExpressionMatrix[i][j] -
                                modelsByCondition[j].getMean_2(), 2)) +
              Math.exp( -1 / (2 * Math.pow(modelsByCondition[j].getStd_3(), 2)) *
                       Math.pow(this.originalExpressionMatrix.
                                geneExpressionMatrix[i][j] -
                                modelsByCondition[j].getMean_3(), 2));
          E_Z_i1[i][0] = num1 / den;
          E_Z_i2[i][0] = num2 / den;
          E_Z_i3[i][0] = num3 / den;
        }

        // STEP 2
        // new std_1
        double sum_E_Z_i1_j = 0;
        double sum_E_Z_i1_x_i_mean_1_j = 0;
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          sum_E_Z_i1_x_i_mean_1_j = sum_E_Z_i1_x_i_mean_1_j +
              Math.pow(E_Z_i1[i][0] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByCondition[j].getMean_1(), 2);
          sum_E_Z_i1_j = sum_E_Z_i1_j + E_Z_i1[i][0];
        }
        new_std_1_j = Math.sqrt(sum_E_Z_i1_x_i_mean_1_j / sum_E_Z_i1_j);
        // new mean_2
        double sum_E_Z_i2_j = 0;
        double sum_E_Z_i2_x_j = 0;
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          sum_E_Z_i2_x_j = sum_E_Z_i2_x_j +
              (E_Z_i2[i][0] *
               this.originalExpressionMatrix.geneExpressionMatrix[i][j]);
          sum_E_Z_i2_j = sum_E_Z_i2_j + E_Z_i2[i][0];
        }
        new_mean_2_j = sum_E_Z_i2_x_j / sum_E_Z_i2_j;
        // new std_2
        double sum_E_Z_i2_x_i_mean_2_j = 0;
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          sum_E_Z_i2_x_i_mean_2_j = sum_E_Z_i2_x_i_mean_2_j +
              Math.pow(E_Z_i2[i][0] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByCondition[j].getMean_2(), 2);
        }
        new_std_2_j = Math.sqrt(sum_E_Z_i2_x_i_mean_2_j / sum_E_Z_i2_j);
        // new mean_3
        double sum_E_Z_i3_j = 0;
        double sum_E_Z_i3_x_j = 0;
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          sum_E_Z_i3_x_j = sum_E_Z_i3_x_j +
              (E_Z_i3[i][0] *
               this.originalExpressionMatrix.geneExpressionMatrix[i][j]);
          sum_E_Z_i3_j = sum_E_Z_i3_j + E_Z_i3[i][0];
        }
        new_mean_3_j = sum_E_Z_i3_x_j / sum_E_Z_i3_j;
        // new std_3
        double sum_E_Z_i3_x_i_mean_3_j = 0;
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          sum_E_Z_i3_x_i_mean_3_j = sum_E_Z_i3_x_i_mean_3_j +
              Math.pow(E_Z_i3[i][0] *
                       this.originalExpressionMatrix.geneExpressionMatrix[i][j] -
                       modelsByCondition[j].getMean_3(), 2);
        }
        new_std_3_j = Math.sqrt(sum_E_Z_i3_x_i_mean_3_j / sum_E_Z_i3_j);

        // update stopping conditions
        convergence = Math.abs(modelsByCondition[j].getStd_1() - new_std_1_j) <=
            0.00001 &&
            Math.abs(modelsByCondition[j].getMean_2() - new_mean_2_j) <=
            0.00001 &&
            Math.abs(modelsByCondition[j].getStd_2() - new_std_2_j) <= 0.00001 &&
            Math.abs(modelsByCondition[j].getMean_3() - new_mean_3_j) <=
            0.00001 &&
            Math.abs(modelsByCondition[j].getStd_3() - new_std_3_j) <= 0.00001;
        numIteractions++;
        /*
                System.out.println("FITTING MIXTURE MODEL - THREE GAUSSIANS BY CONDITION: Iteraction = " + numIteractions);
         System.out.println("mean_1 = " + modelsByCondition[j].getMean_1());
                System.out.println("std_1 = " + modelsByCondition[j].getStd_1() + "\t" + "new_std_1 = " + new_std_1_j);
                System.out.println("mean_2 = " + modelsByCondition[j].getMean_2() + "\t" + "new_mean_2 = " + new_mean_2_j);
                System.out.println("std_2 = " + modelsByCondition[j].getStd_2() + "\t" + "new_std_2 = " + new_std_2_j);
                System.out.println("mean_3 = " + modelsByCondition[j].getMean_3() + "\t" + "new_mean_3 = " + new_mean_3_j);
                System.out.println("std_3 = " + modelsByCondition[j].getStd_3() + "\t" + "new_std_3 = " + new_std_3_j);
         */
        // prepare next iteraction
        modelsByCondition[j].setStd_1(new_std_1_j);
        modelsByCondition[j].setMean_2(new_mean_2_j);
        modelsByCondition[j].setStd_2(new_std_2_j);
        modelsByCondition[j].setMean_3(new_mean_3_j);
        modelsByCondition[j].setStd_3(new_std_3_j);
      }
      modelsByCondition[j].setE_Z_i1(E_Z_i1);
      modelsByCondition[j].setE_Z_i2(E_Z_i2);
      modelsByCondition[j].setE_Z_i3(E_Z_i3);
    }
    return (modelsByCondition);
  }

  // ###################### EVALUATE MIXTURE MODELS USING BIC

  /**
   *
   * @param model1 MixtureModelOneGaussian
   * @param freeParametersModel1 int
   * @param model2 MixtureModelTwoGaussians
   * @param freeParametersModel2 int
   * @param model3 MixtureModelThreeGaussians
   * @param freeParametersModel3 int
   * @return int
   */
  public int evaluateMixtureModelsUsingBIC_OverallMatrix(
      MixtureModelOneGaussian model1, int freeParametersModel1,
      MixtureModelTwoGaussians model2, int freeParametersModel2,
      MixtureModelThreeGaussians model3, int freeParametersModel3) {
    double BIC_model1 = MixtureModelOneGaussian.computeBIC_OverallMatrix(model1,
        freeParametersModel1);
    double BIC_model2 = MixtureModelTwoGaussians.computeBIC_OverallMatrix(
        model2, freeParametersModel2);
    double BIC_model3 = MixtureModelThreeGaussians.computeBIC_OverallMatrix(
        model3, freeParametersModel3);

    System.out.println("COMPUTING BEST MIXTURE MODEL USING BIC");

    if (Math.max(BIC_model1, BIC_model2) == BIC_model1 &&
        Math.max(BIC_model1, BIC_model3) == BIC_model1) {
      return 1;
    }
    else {
      if (Math.max(BIC_model1, BIC_model2) == BIC_model2 &&
          Math.max(BIC_model2, BIC_model3) == BIC_model2) {
        return 2;
      }
      else {
        return 3;
      }
    }
  }

  /**
   *
   * @param modelsByGene1 MixtureModelOneGaussian[]
   * @param freeParametersModel1 int
   * @param modelsByGene2 MixtureModelTwoGaussians[]
   * @param freeParametersModel2 int
   * @param modelsByGene3 MixtureModelThreeGaussians[]
   * @param freeParametersModel3 int
   * @return int[]
   */
  public int[] evaluateMixtureModelsUsingBIC_ByGene(MixtureModelOneGaussian[]
      modelsByGene1, int freeParametersModel1,
      MixtureModelTwoGaussians[] modelsByGene2, int freeParametersModel2,
      MixtureModelThreeGaussians[] modelsByGene3, int freeParametersModel3) {
    System.out.println("COMPUTING BEST MIXTURE MODEL BY GENE USING BIC");
    int numberOfModels = modelsByGene1.length;
    int[] bestModels = new int[numberOfModels];

    // exception number of models must be equal
    double[] BIC_model1 = MixtureModelOneGaussian.computeBIC_ByGene(
        modelsByGene1, freeParametersModel1);
    double[] BIC_model2 = MixtureModelTwoGaussians.computeBIC_ByGene(
        modelsByGene2, freeParametersModel2);
    double[] BIC_model3 = MixtureModelThreeGaussians.computeBIC_ByGene(
        modelsByGene3, freeParametersModel3);

    for (int k = 0; k < numberOfModels; k++) {
      if (Math.max(BIC_model1[k], BIC_model2[k]) == BIC_model1[k] &&
          Math.max(BIC_model1[k], BIC_model3[k]) == BIC_model1[k]) {
        bestModels[k] = 1;
      }
      else {
        if (Math.max(BIC_model1[k], BIC_model2[k]) == BIC_model2[k] &&
            Math.max(BIC_model2[k], BIC_model3[k]) == BIC_model2[k]) {
          bestModels[k] = 2;
        }
        else {
          bestModels[k] = 3;
        }
      }
    }
    return (bestModels);
  }

  /**
   *
   * @param modelsByCondition1 MixtureModelOneGaussian[]
   * @param freeParametersModel1 int
   * @param modelsByCondition2 MixtureModelTwoGaussians[]
   * @param freeParametersModel2 int
   * @param modelsByCondition3 MixtureModelThreeGaussians[]
   * @param freeParametersModel3 int
   * @return int[]
   */
  public int[] evaluateMixtureModelsUsingBIC_ByCondition(
      MixtureModelOneGaussian[] modelsByCondition1, int freeParametersModel1,
      MixtureModelTwoGaussians[] modelsByCondition2, int freeParametersModel2,
      MixtureModelThreeGaussians[] modelsByCondition3, int freeParametersModel3) {
    System.out.println("COMPUTING BEST MIXTURE MODEL BY CONDITION USING BIC");
    int numberOfModels = modelsByCondition1.length;
    int[] bestModels = new int[numberOfModels];

    // exception number of models must be equal
    double[] BIC_model1 = MixtureModelOneGaussian.computeBIC_ByCondition(
        modelsByCondition1, freeParametersModel1);
    double[] BIC_model2 = MixtureModelTwoGaussians.computeBIC_ByCondition(
        modelsByCondition2, freeParametersModel2);
    double[] BIC_model3 = MixtureModelThreeGaussians.computeBIC_ByCondition(
        modelsByCondition3, freeParametersModel3);

    for (int k = 0; k < numberOfModels; k++) {
      if (Math.max(BIC_model1[k], BIC_model2[k]) == BIC_model1[k] &&
          Math.max(BIC_model1[k], BIC_model3[k]) == BIC_model1[k]) {
        bestModels[k] = 1;
      }
      else {
        if (Math.max(BIC_model1[k], BIC_model2[k]) == BIC_model2[k] &&
            Math.max(BIC_model2[k], BIC_model3[k]) == BIC_model2[k]) {
          bestModels[k] = 2;
        }
        else {
          bestModels[k] = 3;
        }
      }
    }
    return (bestModels);
  }

  // ####################### DISCRETIZE MATRIX USING MIXTURE MODELS

  /**
   * Computes three mixture models: one with one gaussian, one with two gaussians and one with three gaussians.
   * Evalutes the three models using BIC and discretizes the expression matrix using the best model.
   * @param symbolDownExpression char
   * @param symbolNoExpression char
   * @param symbolUpExpression char
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_MixtureModels_OverallMatrix(char
      symbolDownExpression, char symbolNoExpression, char symbolUpExpression) throws
      Exception {

    if (this.originalExpressionMatrix.hasMissingValues) {
      throw new Exception("CANNOT DISCRETIZE MATRIX: matrix has missing values");
    }

    if (symbolDownExpression > symbolNoExpression ||
        symbolNoExpression > symbolUpExpression) {
      throw new Exception("INVALID ALPHABET: symbolDownExpression > symbolNoExpression OR symbolNoExpression > symbolUpExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[3];

    alphabet[0] = symbolDownExpression;
    alphabet[1] = symbolNoExpression;
    alphabet[2] = symbolUpExpression;

    // ONE GAUSSIAN
    MixtureModelOneGaussian oneGaussian_OverallMatrix = this.
        computeMixtureModel_OneGaussian_OverallMatrix();
    // TWO GAUSSIANS
    MixtureModelTwoGaussians twoGaussians_OverallMatrix = this.
        computeMixtureModel_TwoGaussians_OverallMatrix();
    // THREE GAUSSIANS
    MixtureModelThreeGaussians threeGaussians_OverallMatrix = this.
        computeMixtureModel_ThreeGaussians_OverallMatrix();

    // EVALUATE MIXTURE MODELS
    int bestModel = this.evaluateMixtureModelsUsingBIC_OverallMatrix(
        oneGaussian_OverallMatrix, 1,
        twoGaussians_OverallMatrix, 3, threeGaussians_OverallMatrix, 5);

//    System.out.println("BEST MODEL = " + bestModel);

    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
        "bestMixtureModel_OverallMatrix.txt")));
    out.println("" + bestModel);

//    System.out.println("DISCRETIZING EXPRESSION MATRIX USING BEST MODEL");

    // DISCRETIZE MATRIX USING BEST MODEL
    if (bestModel == 1) { // BEST MODEL = ONE GAUSSIAN
      // discretizar os valores para tr�s bins, com iguais frequ�ncias.
      this.computeDiscretizedMatrix_EqualFrequency_K_Symbols_OverallMatrix(3);
      symbolicMatrix = this.symbolicExpressionMatrix;
    }

    if (bestModel == 2) { // BEST MODEL = TWO GAUSSIANS
      // GAUSSIAN 1 - GAUSSIAN 2
      // or
      // GAUSSIAN 2 - GAUSSIAN 1

      // GAUSSIAN 1 - GAUSSIAN 2
      if (twoGaussians_OverallMatrix.getMean_1() <=
          twoGaussians_OverallMatrix.getMean_2()) {
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          for (int j = 0;
               j < this.originalExpressionMatrix.getNumberOfConditions();
               j++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolUpExpression)
            if (twoGaussians_OverallMatrix.getE_Z_i1()[i][j] >=
                twoGaussians_OverallMatrix.getE_Z_i2()[i][j]) {
              symbolicMatrix[i][j] = symbolNoExpression;
            }
            else {
              symbolicMatrix[i][j] = symbolUpExpression;
            }
          }
        }
      } // END GAUSSIAN 1 - GAUSSIAN 2
      else { // GAUSSIAN 2 - GAUSSIAN 1
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          for (int j = 0;
               j < this.originalExpressionMatrix.getNumberOfConditions();
               j++) {
            // discretize using symbolNoExpression and symbolDownExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolDownExpression)
            if (twoGaussians_OverallMatrix.getE_Z_i1()[i][j] >=
                twoGaussians_OverallMatrix.getE_Z_i2()[i][j]) {
              symbolicMatrix[i][j] = symbolNoExpression;
            }
            else {
              symbolicMatrix[i][j] = symbolDownExpression;
            }
          }
        }
      } // END GAUSSIAN 2 - GAUSSIAN 1
    } // END IF BEST MODEL = 1 GAUSSIAN

    if (bestModel == 3) {
      // BEST MODEL = THREE GAUSSIANS
      // GAUSSIAN 1 - GAUSSIAN ? - GAUSSIAN ?
      // GAUSSIAN ? - GAUSSIAN 1 - GAUSSIAN ?
      // GAUSSIAN ? - GAUSSIAN ? - GAUSSIAN 1

      // GAUSSIAN 1 - GAUSSIAN ? - GAUSSIAN ?

      // GAUSSIAN 1 - GAUSSIAN 2 - GAUSSIAN 3
      if (threeGaussians_OverallMatrix.getMean_1() <=
          threeGaussians_OverallMatrix.getMean_2() &&
          threeGaussians_OverallMatrix.getMean_2() <=
          threeGaussians_OverallMatrix.getMean_3()) {
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          for (int j = 0;
               j < this.originalExpressionMatrix.getNumberOfConditions();
               j++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolDownExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolUpExpression)

            // GAUSSIAN 1
            if (threeGaussians_OverallMatrix.getE_Z_i1()[i][j] >=
                threeGaussians_OverallMatrix.getE_Z_i2()[i][j] &&
                threeGaussians_OverallMatrix.getE_Z_i1()[i][j] >=
                threeGaussians_OverallMatrix.getE_Z_i3()[i][j]) {
              symbolicMatrix[i][j] = symbolDownExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_OverallMatrix.getE_Z_i2()[i][j] >=
                  threeGaussians_OverallMatrix.getE_Z_i1()[i][j] &&
                  threeGaussians_OverallMatrix.getE_Z_i2()[i][j] >=
                  threeGaussians_OverallMatrix.getE_Z_i3()[i][j]) {
                symbolicMatrix[i][j] = symbolNoExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolUpExpression;
              }
            }
          }
        }
      } // END GAUSSIAN 1 - GAUSSIAN 2 - GAUSSIAN 3

      // GAUSSIAN 1 - GAUSSIAN 3 - GAUSSIAN 2
      if (threeGaussians_OverallMatrix.getMean_1() <=
          threeGaussians_OverallMatrix.getMean_3() &&
          threeGaussians_OverallMatrix.getMean_3() <=
          threeGaussians_OverallMatrix.getMean_2()) {
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          for (int j = 0;
               j < this.originalExpressionMatrix.getNumberOfConditions();
               j++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolDownExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolUpExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolNoExpression)

            // GAUSSIAN 1
            if (threeGaussians_OverallMatrix.getE_Z_i1()[i][j] >=
                threeGaussians_OverallMatrix.getE_Z_i2()[i][j] &&
                threeGaussians_OverallMatrix.getE_Z_i1()[i][j] >=
                threeGaussians_OverallMatrix.getE_Z_i3()[i][j]) {
              symbolicMatrix[i][j] = symbolDownExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_OverallMatrix.getE_Z_i2()[i][j] >=
                  threeGaussians_OverallMatrix.getE_Z_i1()[i][j] &&
                  threeGaussians_OverallMatrix.getE_Z_i2()[i][j] >=
                  threeGaussians_OverallMatrix.getE_Z_i3()[i][j]) {
                symbolicMatrix[i][j] = symbolUpExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolNoExpression;
              }
            }
          }
        }
      } // END GAUSSIAN 1 - GAUSSIAN 3 - GAUSSIAN 2

      // GAUSSIAN ? - GAUSSIAN 1 - GAUSSIAN ?

      // GAUSSIAN 2 - GAUSSIAN 1 - GAUSSIAN 3
      if (threeGaussians_OverallMatrix.getMean_2() <=
          threeGaussians_OverallMatrix.getMean_1() &&
          threeGaussians_OverallMatrix.getMean_1() <=
          threeGaussians_OverallMatrix.getMean_3()) {
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          for (int j = 0;
               j < this.originalExpressionMatrix.getNumberOfConditions();
               j++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolDownExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolUpExpression)

            // GAUSSIAN 1
            if (threeGaussians_OverallMatrix.getE_Z_i1()[i][j] >=
                threeGaussians_OverallMatrix.getE_Z_i2()[i][j] &&
                threeGaussians_OverallMatrix.getE_Z_i1()[i][j] >=
                threeGaussians_OverallMatrix.getE_Z_i3()[i][j]) {
              symbolicMatrix[i][j] = symbolNoExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_OverallMatrix.getE_Z_i2()[i][j] >=
                  threeGaussians_OverallMatrix.getE_Z_i1()[i][j] &&
                  threeGaussians_OverallMatrix.getE_Z_i2()[i][j] >=
                  threeGaussians_OverallMatrix.getE_Z_i3()[i][j]) {
                symbolicMatrix[i][j] = symbolDownExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolUpExpression;
              }
            }
          }
        }
      } // END GAUSSIAN 2 - GAUSSIAN 1 - GAUSSIAN 3

      // GAUSSIAN 3 - GAUSSIAN 1 - GAUSSIAN 2
      if (threeGaussians_OverallMatrix.getMean_3() <=
          threeGaussians_OverallMatrix.getMean_1() &&
          threeGaussians_OverallMatrix.getMean_1() <=
          threeGaussians_OverallMatrix.getMean_2()) {
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          for (int j = 0;
               j < this.originalExpressionMatrix.getNumberOfConditions();
               j++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolUpExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolDownExpression)

            // GAUSSIAN 1
            if (threeGaussians_OverallMatrix.getE_Z_i1()[i][j] >=
                threeGaussians_OverallMatrix.getE_Z_i2()[i][j] &&
                threeGaussians_OverallMatrix.getE_Z_i1()[i][j] >=
                threeGaussians_OverallMatrix.getE_Z_i3()[i][j]) {
              symbolicMatrix[i][j] = symbolNoExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_OverallMatrix.getE_Z_i2()[i][j] >=
                  threeGaussians_OverallMatrix.getE_Z_i1()[i][j] &&
                  threeGaussians_OverallMatrix.getE_Z_i2()[i][j] >=
                  threeGaussians_OverallMatrix.getE_Z_i3()[i][j]) {
                symbolicMatrix[i][j] = symbolUpExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolDownExpression;
              }
            }
          }
        }
      } // END GAUSSIAN 3 - GAUSSIAN 1 - GAUSSIAN 2

      // GAUSSIAN ? - GAUSSIAN ? - GAUSSIAN 1

      // GAUSSIAN 2 - GAUSSIAN 3 - GAUSSIAN 1
      if (threeGaussians_OverallMatrix.getMean_2() <=
          threeGaussians_OverallMatrix.getMean_3() &&
          threeGaussians_OverallMatrix.getMean_3() <=
          threeGaussians_OverallMatrix.getMean_1()) {
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          for (int j = 0;
               j < this.originalExpressionMatrix.getNumberOfConditions();
               j++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolUpExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolDownExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolNoExpression)

            // GAUSSIAN 1
            if (threeGaussians_OverallMatrix.getE_Z_i1()[i][j] >=
                threeGaussians_OverallMatrix.getE_Z_i2()[i][j] &&
                threeGaussians_OverallMatrix.getE_Z_i1()[i][j] >=
                threeGaussians_OverallMatrix.getE_Z_i3()[i][j]) {
              symbolicMatrix[i][j] = symbolUpExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_OverallMatrix.getE_Z_i2()[i][j] >=
                  threeGaussians_OverallMatrix.getE_Z_i1()[i][j] &&
                  threeGaussians_OverallMatrix.getE_Z_i2()[i][j] >=
                  threeGaussians_OverallMatrix.getE_Z_i3()[i][j]) {
                symbolicMatrix[i][j] = symbolDownExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolNoExpression;
              }
            }
          }
        }
      } // END GAUSSIAN 2 - GAUSSIAN 3 - GAUSSIAN 1

      // GAUSSIAN 3 - GAUSSIAN 2 - GAUSSIAN 1
      if (threeGaussians_OverallMatrix.getMean_3() <=
          threeGaussians_OverallMatrix.getMean_2() &&
          threeGaussians_OverallMatrix.getMean_2() <=
          threeGaussians_OverallMatrix.getMean_1()) {
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          for (int j = 0;
               j < this.originalExpressionMatrix.getNumberOfConditions();
               j++) {
            // discretize using symbolDownExpression, symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolUpExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolDownExpression)

            // GAUSSIAN 1
            if (threeGaussians_OverallMatrix.getE_Z_i1()[i][j] >=
                threeGaussians_OverallMatrix.getE_Z_i2()[i][j] &&
                threeGaussians_OverallMatrix.getE_Z_i1()[i][j] >=
                threeGaussians_OverallMatrix.getE_Z_i3()[i][j]) {
              symbolicMatrix[i][j] = symbolUpExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_OverallMatrix.getE_Z_i2()[i][j] >=
                  threeGaussians_OverallMatrix.getE_Z_i1()[i][j] &&
                  threeGaussians_OverallMatrix.getE_Z_i2()[i][j] >=
                  threeGaussians_OverallMatrix.getE_Z_i3()[i][j]) {
                symbolicMatrix[i][j] = symbolNoExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolDownExpression;
              }
            }
          }
        }
      } // END GAUSSIAN 3 - GAUSSIAN 1 - GAUSSIAN 2

    } // END BEST MODEL = 3
    out.close();
  }

  /**
   * Computes three mixture models by gene: one with one gaussian, one with two gaussians and one with three gaussians.
   * Evalutes the three models using BIC and discretizes the expression matrix using the best model for each gene.
   * @param symbolDownExpression char
   * @param symbolNoExpression char
   * @param symbolUpExpression char
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_MixtureModels_ByGene(char
      symbolDownExpression, char symbolNoExpression, char symbolUpExpression) throws
      Exception {

    if (this.originalExpressionMatrix.hasMissingValues) {
      throw new Exception("CANNOT DISCRETIZE MATRIX: matrix has missing values");
    }

    if (symbolDownExpression > symbolNoExpression ||
        symbolNoExpression > symbolUpExpression) {
      throw new Exception("INVALID ALPHABET: symbolDownExpression > symbolNoExpression OR symbolNoExpression > symbolUpExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[3];

    alphabet[0] = symbolDownExpression;
    alphabet[1] = symbolNoExpression;
    alphabet[2] = symbolUpExpression;

    // ONE GAUSSIAN
    MixtureModelOneGaussian[] oneGaussian_ByGene = this.
        computeMixtureModel_OneGaussian_ByGene();
    // TWO GAUSSIANS
    MixtureModelTwoGaussians[] twoGaussians_ByGene = this.
        computeMixtureModel_TwoGaussians_ByGene();
    // THREE GAUSSIANS
    MixtureModelThreeGaussians[] threeGaussians_ByGene = this.
        computeMixtureModel_ThreeGaussians_ByGene();

    // EVALUATE MIXTURE MODELS
    int[] bestModels = this.evaluateMixtureModelsUsingBIC_ByGene(
        oneGaussian_ByGene, 1,
        twoGaussians_ByGene, 3, threeGaussians_ByGene, 5);

/*    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
        "bestMixtureModels_ByGene.txt")));
 */

    for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {

//      System.out.println("BEST MODEL FOR GENE " + i + " = " + bestModels[i]);

//      out.println("" + bestModels[i]);

/*      System.out.println(
          "DISCRETIZING EXPRESSION MATRIX USING BEST MODEL FOR EACH GENE");
 */

      // DISCRETIZE MATRIX USING BEST MODEL
      if (bestModels[i] == 1) { // BEST MODEL = ONE GAUSSIAN

/*        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          out.print("" + oneGaussian_ByGene[i].getE_Z_i1()[0][j] + "\t");
        }
        out.println();
 */

        // discretizar os valores para tr�s bins, com iguais frequ�ncias.
        this.computeDiscretizedMatrix_EqualFrequency_K_Symbols_ByGene(3);
        symbolicMatrix = this.symbolicExpressionMatrix;
      }

      if (bestModels[i] == 2) { // BEST MODEL = TWO GAUSSIANS

/*        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          out.print("" + twoGaussians_ByGene[i].getE_Z_i1()[0][j] + "\t");
        }
        out.println();
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          out.print("" + twoGaussians_ByGene[i].getE_Z_i2()[0][j] + "\t");
        }
        out.println();
 */

        // GAUSSIAN 1 - GAUSSIAN 2
        // or
        // GAUSSIAN 2 - GAUSSIAN 1

        // GAUSSIAN 1 - GAUSSIAN 2
        if (twoGaussians_ByGene[i].getMean_1() <=
            twoGaussians_ByGene[i].getMean_2()) {
          for (int j = 0;
               j < this.originalExpressionMatrix.getNumberOfConditions();
               j++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolUpExpression)
            if (twoGaussians_ByGene[i].getE_Z_i1()[0][j] >=
                twoGaussians_ByGene[i].getE_Z_i2()[0][j]) {
              symbolicMatrix[i][j] = symbolNoExpression;
            }
            else {
              symbolicMatrix[i][j] = symbolUpExpression;
            }
          }
        } // END GAUSSIAN 1 - GAUSSIAN 2
        else { // GAUSSIAN 2 - GAUSSIAN 1
          for (int j = 0;
               j < this.originalExpressionMatrix.getNumberOfConditions();
               j++) {
            // discretize using symbolNoExpression and symbolDownExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolDownExpression)
            if (twoGaussians_ByGene[i].getE_Z_i1()[0][j] >=
                twoGaussians_ByGene[i].getE_Z_i2()[0][j]) {
              symbolicMatrix[i][j] = symbolNoExpression;
            }
            else {
              symbolicMatrix[i][j] = symbolDownExpression;
            }
          }
        } // END GAUSSIAN 2 - GAUSSIAN 1
      } // END IF BEST MODEL = 1 GAUSSIAN

      if (bestModels[i] == 3) {

/*        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          out.print("" + threeGaussians_ByGene[i].getE_Z_i1()[0][j] + "\t");
        }
        out.println();
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          out.print("" + threeGaussians_ByGene[i].getE_Z_i2()[0][j] + "\t");
        }
        out.println();
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          out.print("" + threeGaussians_ByGene[i].getE_Z_i3()[0][j] + "\t");
        }
        out.println();
 */

        // BEST MODEL = THREE GAUSSIANS
        // GAUSSIAN 1 - GAUSSIAN ? - GAUSSIAN ?
        // GAUSSIAN ? - GAUSSIAN 1 - GAUSSIAN ?
        // GAUSSIAN ? - GAUSSIAN ? - GAUSSIAN 1

        // GAUSSIAN 1 - GAUSSIAN ? - GAUSSIAN ?

        // GAUSSIAN 1 - GAUSSIAN 2 - GAUSSIAN 3
        if (threeGaussians_ByGene[i].getMean_1() <=
            threeGaussians_ByGene[i].getMean_2() &&
            threeGaussians_ByGene[i].getMean_2() <=
            threeGaussians_ByGene[i].getMean_3()) {
          for (int j = 0;
               j < this.originalExpressionMatrix.getNumberOfConditions();
               j++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolDownExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolUpExpression)

            // GAUSSIAN 1
            if (threeGaussians_ByGene[i].getE_Z_i1()[0][j] >=
                threeGaussians_ByGene[i].getE_Z_i2()[0][j] &&
                threeGaussians_ByGene[i].getE_Z_i1()[0][j] >=
                threeGaussians_ByGene[i].getE_Z_i3()[0][j]) {
              symbolicMatrix[i][j] = symbolDownExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_ByGene[i].getE_Z_i2()[0][j] >=
                  threeGaussians_ByGene[i].getE_Z_i1()[0][j] &&
                  threeGaussians_ByGene[i].getE_Z_i2()[0][j] >=
                  threeGaussians_ByGene[i].getE_Z_i3()[0][j]) {
                symbolicMatrix[i][j] = symbolNoExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolUpExpression;
              }
            }
          }
        } // END GAUSSIAN 1 - GAUSSIAN 2 - GAUSSIAN 3

        // GAUSSIAN 1 - GAUSSIAN 3 - GAUSSIAN 2
        if (threeGaussians_ByGene[i].getMean_1() <=
            threeGaussians_ByGene[i].getMean_3() &&
            threeGaussians_ByGene[i].getMean_3() <=
            threeGaussians_ByGene[i].getMean_2()) {
          for (int j = 0;
               j < this.originalExpressionMatrix.getNumberOfConditions();
               j++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolDownExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolUpExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolNoExpression)

            // GAUSSIAN 1
            if (threeGaussians_ByGene[i].getE_Z_i1()[0][j] >=
                threeGaussians_ByGene[i].getE_Z_i2()[0][j] &&
                threeGaussians_ByGene[i].getE_Z_i1()[0][j] >=
                threeGaussians_ByGene[i].getE_Z_i3()[0][j]) {
              symbolicMatrix[i][j] = symbolDownExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_ByGene[i].getE_Z_i2()[0][j] >=
                  threeGaussians_ByGene[i].getE_Z_i1()[0][j] &&
                  threeGaussians_ByGene[i].getE_Z_i2()[0][j] >=
                  threeGaussians_ByGene[i].getE_Z_i3()[0][j]) {
                symbolicMatrix[i][j] = symbolUpExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolNoExpression;
              }
            }
          }
        } // END GAUSSIAN 1 - GAUSSIAN 3 - GAUSSIAN 2

        // GAUSSIAN ? - GAUSSIAN 1 - GAUSSIAN ?

        // GAUSSIAN 2 - GAUSSIAN 1 - GAUSSIAN 3
        if (threeGaussians_ByGene[i].getMean_2() <=
            threeGaussians_ByGene[i].getMean_1() &&
            threeGaussians_ByGene[i].getMean_1() <=
            threeGaussians_ByGene[i].getMean_3()) {
          for (int j = 0;
               j < this.originalExpressionMatrix.getNumberOfConditions();
               j++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolDownExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolUpExpression)

            // GAUSSIAN 1
            if (threeGaussians_ByGene[i].getE_Z_i1()[0][j] >=
                threeGaussians_ByGene[i].getE_Z_i2()[0][j] &&
                threeGaussians_ByGene[i].getE_Z_i1()[0][j] >=
                threeGaussians_ByGene[i].getE_Z_i3()[0][j]) {
              symbolicMatrix[i][j] = symbolNoExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_ByGene[i].getE_Z_i2()[0][j] >=
                  threeGaussians_ByGene[i].getE_Z_i1()[0][j] &&
                  threeGaussians_ByGene[i].getE_Z_i2()[0][j] >=
                  threeGaussians_ByGene[i].getE_Z_i3()[0][j]) {
                symbolicMatrix[i][j] = symbolDownExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolUpExpression;
              }
            }
          }
        }
      } // END GAUSSIAN 2 - GAUSSIAN 1 - GAUSSIAN 3

      // GAUSSIAN 3 - GAUSSIAN 1 - GAUSSIAN 2
      if (threeGaussians_ByGene[i].getMean_3() <=
          threeGaussians_ByGene[i].getMean_1() &&
          threeGaussians_ByGene[i].getMean_1() <=
          threeGaussians_ByGene[i].getMean_2()) {
        for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
             j++) {
          // discretize using symbolNoExpression and symbolUpExpression
          // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolNoExpression)
          // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolUpExpression)
          // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolDownExpression)

          // GAUSSIAN 1
          if (threeGaussians_ByGene[i].getE_Z_i1()[0][j] >=
              threeGaussians_ByGene[i].getE_Z_i2()[0][j] &&
              threeGaussians_ByGene[i].getE_Z_i1()[0][j] >=
              threeGaussians_ByGene[i].getE_Z_i3()[0][j]) {
            symbolicMatrix[i][j] = symbolNoExpression;
          }
          else { // GAUSSIAN 2
            if (threeGaussians_ByGene[i].getE_Z_i2()[0][j] >=
                threeGaussians_ByGene[i].getE_Z_i1()[0][j] &&
                threeGaussians_ByGene[i].getE_Z_i2()[0][j] >=
                threeGaussians_ByGene[i].getE_Z_i3()[0][j]) {
              symbolicMatrix[i][j] = symbolUpExpression;
            }
            else { // GAUSSIAN 3
              symbolicMatrix[i][j] = symbolDownExpression;
            }
          }
        } // END GAUSSIAN 3 - GAUSSIAN 1 - GAUSSIAN 2

        // GAUSSIAN ? - GAUSSIAN ? - GAUSSIAN 1

        // GAUSSIAN 2 - GAUSSIAN 3 - GAUSSIAN 1
        if (threeGaussians_ByGene[i].getMean_2() <=
            threeGaussians_ByGene[i].getMean_3() &&
            threeGaussians_ByGene[i].getMean_3() <=
            threeGaussians_ByGene[i].getMean_1()) {
          for (int j = 0;
               j < this.originalExpressionMatrix.getNumberOfConditions();
               j++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolUpExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolDownExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolNoExpression)

            // GAUSSIAN 1
            if (threeGaussians_ByGene[i].getE_Z_i1()[0][j] >=
                threeGaussians_ByGene[i].getE_Z_i2()[0][j] &&
                threeGaussians_ByGene[i].getE_Z_i1()[0][j] >=
                threeGaussians_ByGene[i].getE_Z_i3()[0][j]) {
              symbolicMatrix[i][j] = symbolUpExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_ByGene[i].getE_Z_i2()[0][j] >=
                  threeGaussians_ByGene[i].getE_Z_i1()[0][j] &&
                  threeGaussians_ByGene[i].getE_Z_i2()[0][j] >=
                  threeGaussians_ByGene[i].getE_Z_i3()[0][j]) {
                symbolicMatrix[i][j] = symbolDownExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolNoExpression;
              }
            }
          }
        } // END GAUSSIAN 2 - GAUSSIAN 3 - GAUSSIAN 1

        // GAUSSIAN 3 - GAUSSIAN 2 - GAUSSIAN 1
        if (threeGaussians_ByGene[i].getMean_3() <=
            threeGaussians_ByGene[i].getMean_2() &&
            threeGaussians_ByGene[i].getMean_2() <=
            threeGaussians_ByGene[i].getMean_1()) {
          for (int j = 0;
               j < this.originalExpressionMatrix.getNumberOfConditions();
               j++) {
            // discretize using symbolDownExpression, symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolUpExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolDownExpression)

            // GAUSSIAN 1
            if (threeGaussians_ByGene[i].getE_Z_i1()[0][j] >=
                threeGaussians_ByGene[i].getE_Z_i2()[0][j] &&
                threeGaussians_ByGene[i].getE_Z_i1()[0][j] >=
                threeGaussians_ByGene[i].getE_Z_i3()[0][j]) {
              symbolicMatrix[i][j] = symbolUpExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_ByGene[i].getE_Z_i2()[0][j] >=
                  threeGaussians_ByGene[i].getE_Z_i1()[0][j] &&
                  threeGaussians_ByGene[i].getE_Z_i2()[0][j] >=
                  threeGaussians_ByGene[i].getE_Z_i3()[0][j]) {
                symbolicMatrix[i][j] = symbolNoExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolDownExpression;
              }
            }
          }
        } // END GAUSSIAN 3 - GAUSSIAN 1 - GAUSSIAN 2

      } // END BEST MODEL = 3
    }

//    out.close();

    this.alphabet = alphabet;
    this.symbolicExpressionMatrix = symbolicMatrix;
  }

  /**
   * Computes three mixture models by condition: one with one gaussian, one with two gaussians and one with three gaussians.
   * Evalutes the three models using BIC and discretizes the expression matrix using the best model for each condition.
   * @param symbolDownExpression char
   * @param symbolNoExpression char
   * @param symbolUpExpression char
   *
   * @throws Exception
   */
  public void
      computeDiscretizedMatrix_MixtureModels_ByCondition(char
      symbolDownExpression, char symbolNoExpression, char symbolUpExpression) throws
      Exception {

    if (this.originalExpressionMatrix.hasMissingValues) {
      throw new Exception("CANNOT DISCRETIZE MATRIX: matrix has missing values");
    }

    if (symbolDownExpression > symbolNoExpression ||
        symbolNoExpression > symbolUpExpression) {
      throw new Exception("INVALID ALPHABET: symbolDownExpression > symbolNoExpression OR symbolNoExpression > symbolUpExpression");
    }

    char[][] symbolicMatrix = new char[this.originalExpressionMatrix.
        getNumberOfGenes()][this.originalExpressionMatrix.getNumberOfConditions()];
    char[] alphabet = new char[3];

    alphabet[0] = symbolDownExpression;
    alphabet[1] = symbolNoExpression;
    alphabet[2] = symbolUpExpression;

    // ONE GAUSSIAN
    MixtureModelOneGaussian[] oneGaussian_ByCondition = this.
        computeMixtureModel_OneGaussian_ByCondition();
    // TWO GAUSSIANS
    MixtureModelTwoGaussians[] twoGaussians_ByCondition = this.
        computeMixtureModel_TwoGaussians_ByCondition();
    // THREE GAUSSIANS
    MixtureModelThreeGaussians[] threeGaussians_ByCondition = this.
        computeMixtureModel_ThreeGaussians_ByCondition();

    // EVALUATE MIXTURE MODELS
    int[] bestModels = this.evaluateMixtureModelsUsingBIC_ByCondition(
        oneGaussian_ByCondition, 1,
        twoGaussians_ByCondition, 3, threeGaussians_ByCondition, 5);

/*    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
        "bestMixtureModels_ByCondition.txt")));
*/

    for (int j = 0; j < this.originalExpressionMatrix.getNumberOfConditions();
         j++) {

/*      System.out.println("BEST MODEL FOR CONDITION " + j + " = " + bestModels[j]);

      out.println("" + bestModels[j]);

      System.out.println(
          "DISCRETIZING EXPRESSION MATRIX USING BEST MODEL FOR EACH CONDITION");
*/

      // DISCRETIZE MATRIX USING BEST MODEL
      if (bestModels[j] == 1) { // BEST MODEL = ONE GAUSSIAN

/*        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          out.print("" + oneGaussian_ByCondition[j].getE_Z_i1()[i][0] + "\t");
        }
        out.println();
*/

        // discretizar os valores para tr�s bins, com iguais frequ�ncias.
        this.computeDiscretizedMatrix_EqualFrequency_K_Symbols_ByCondition(3);
        symbolicMatrix = this.symbolicExpressionMatrix;
      }

      if (bestModels[j] == 2) { // BEST MODEL = TWO GAUSSIANS

/*        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          out.print("" + twoGaussians_ByCondition[j].getE_Z_i1()[i][0] + "\t");
        }
        out.println();
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          out.print("" + twoGaussians_ByCondition[j].getE_Z_i2()[i][0] + "\t");
        }
        out.println();
*/

        // GAUSSIAN 1 - GAUSSIAN 2
        // or
        // GAUSSIAN 2 - GAUSSIAN 1

        // GAUSSIAN 1 - GAUSSIAN 2
        if (twoGaussians_ByCondition[j].getMean_1() <=
            twoGaussians_ByCondition[j].getMean_2()) {
          for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes();
               i++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolUpExpression)
            if (twoGaussians_ByCondition[j].getE_Z_i1()[i][0] >=
                twoGaussians_ByCondition[j].getE_Z_i2()[i][0]) {
              symbolicMatrix[i][j] = symbolNoExpression;
            }
            else {
              symbolicMatrix[i][j] = symbolUpExpression;
            }
          }
        } // END GAUSSIAN 1 - GAUSSIAN 2
        else { // GAUSSIAN 2 - GAUSSIAN 1
          for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes();
               i++) {
            // discretize using symbolNoExpression and symbolDownExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolDownExpression)
            if (twoGaussians_ByCondition[j].getE_Z_i1()[i][0] >=
                twoGaussians_ByCondition[j].getE_Z_i2()[i][0]) {
              symbolicMatrix[i][j] = symbolNoExpression;
            }
            else {
              symbolicMatrix[i][j] = symbolDownExpression;
            }
          }
        } // END GAUSSIAN 2 - GAUSSIAN 1
      } // END IF BEST MODEL = 1 GAUSSIAN

      if (bestModels[j] == 3) {

/*        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          out.print("" + threeGaussians_ByCondition[j].getE_Z_i1()[i][0] + "\t");
        }
        out.println();
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          out.print("" + threeGaussians_ByCondition[j].getE_Z_i2()[i][0] + "\t");
        }
        out.println();
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          out.print("" + threeGaussians_ByCondition[j].getE_Z_i3()[i][0] + "\t");
        }
        out.println();
*/

        // BEST MODEL = THREE GAUSSIANS
        // GAUSSIAN 1 - GAUSSIAN ? - GAUSSIAN ?
        // GAUSSIAN ? - GAUSSIAN 1 - GAUSSIAN ?
        // GAUSSIAN ? - GAUSSIAN ? - GAUSSIAN 1

        // GAUSSIAN 1 - GAUSSIAN ? - GAUSSIAN ?

        // GAUSSIAN 1 - GAUSSIAN 2 - GAUSSIAN 3
        if (threeGaussians_ByCondition[j].getMean_1() <=
            threeGaussians_ByCondition[j].getMean_2() &&
            threeGaussians_ByCondition[j].getMean_2() <=
            threeGaussians_ByCondition[j].getMean_3()) {
          for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes();
               i++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolDownExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolUpExpression)

            // GAUSSIAN 1
            if (threeGaussians_ByCondition[j].getE_Z_i1()[i][0] >=
                threeGaussians_ByCondition[j].getE_Z_i2()[i][0] &&
                threeGaussians_ByCondition[j].getE_Z_i1()[i][0] >=
                threeGaussians_ByCondition[j].getE_Z_i3()[i][0]) {
              symbolicMatrix[i][j] = symbolDownExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_ByCondition[j].getE_Z_i2()[i][0] >=
                  threeGaussians_ByCondition[j].getE_Z_i1()[i][0] &&
                  threeGaussians_ByCondition[j].getE_Z_i2()[i][0] >=
                  threeGaussians_ByCondition[j].getE_Z_i3()[i][0]) {
                symbolicMatrix[i][j] = symbolNoExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolUpExpression;
              }
            }
          }
        } // END GAUSSIAN 1 - GAUSSIAN 2 - GAUSSIAN 3

        // GAUSSIAN 1 - GAUSSIAN 3 - GAUSSIAN 2
        if (threeGaussians_ByCondition[j].getMean_1() <=
            threeGaussians_ByCondition[j].getMean_3() &&
            threeGaussians_ByCondition[j].getMean_3() <=
            threeGaussians_ByCondition[j].getMean_2()) {
          for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes();
               i++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolDownExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolUpExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolNoExpression)

            // GAUSSIAN 1
            if (threeGaussians_ByCondition[j].getE_Z_i1()[i][0] >=
                threeGaussians_ByCondition[j].getE_Z_i2()[i][0] &&
                threeGaussians_ByCondition[j].getE_Z_i1()[i][0] >=
                threeGaussians_ByCondition[j].getE_Z_i3()[i][0]) {
              symbolicMatrix[i][j] = symbolDownExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_ByCondition[j].getE_Z_i2()[i][0] >=
                  threeGaussians_ByCondition[j].getE_Z_i1()[i][0] &&
                  threeGaussians_ByCondition[j].getE_Z_i2()[i][0] >=
                  threeGaussians_ByCondition[j].getE_Z_i3()[i][0]) {
                symbolicMatrix[i][j] = symbolUpExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolNoExpression;
              }
            }
          }
        } // END GAUSSIAN 1 - GAUSSIAN 3 - GAUSSIAN 2

        // GAUSSIAN ? - GAUSSIAN 1 - GAUSSIAN ?

        // GAUSSIAN 2 - GAUSSIAN 1 - GAUSSIAN 3
        if (threeGaussians_ByCondition[j].getMean_2() <=
            threeGaussians_ByCondition[j].getMean_1() &&
            threeGaussians_ByCondition[j].getMean_1() <=
            threeGaussians_ByCondition[j].getMean_3()) {
          for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes();
               i++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolDownExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolUpExpression)

            // GAUSSIAN 1
            if (threeGaussians_ByCondition[j].getE_Z_i1()[i][0] >=
                threeGaussians_ByCondition[j].getE_Z_i2()[i][0] &&
                threeGaussians_ByCondition[j].getE_Z_i1()[i][0] >=
                threeGaussians_ByCondition[j].getE_Z_i3()[i][0]) {
              symbolicMatrix[i][j] = symbolNoExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_ByCondition[j].getE_Z_i2()[i][0] >=
                  threeGaussians_ByCondition[j].getE_Z_i1()[i][0] &&
                  threeGaussians_ByCondition[j].getE_Z_i2()[i][0] >=
                  threeGaussians_ByCondition[j].getE_Z_i3()[i][0]) {
                symbolicMatrix[i][j] = symbolDownExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolUpExpression;
              }
            }
          }
        }
      } // END GAUSSIAN 2 - GAUSSIAN 1 - GAUSSIAN 3

      // GAUSSIAN 3 - GAUSSIAN 1 - GAUSSIAN 2
      if (threeGaussians_ByCondition[j].getMean_3() <=
          threeGaussians_ByCondition[j].getMean_1() &&
          threeGaussians_ByCondition[j].getMean_1() <=
          threeGaussians_ByCondition[j].getMean_2()) {
        for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes(); i++) {
          // discretize using symbolNoExpression and symbolUpExpression
          // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolNoExpression)
          // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolUpExpression)
          // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolDownExpression)

          // GAUSSIAN 1
          if (threeGaussians_ByCondition[j].getE_Z_i1()[i][0] >=
              threeGaussians_ByCondition[j].getE_Z_i2()[i][0] &&
              threeGaussians_ByCondition[j].getE_Z_i1()[i][0] >=
              threeGaussians_ByCondition[j].getE_Z_i3()[i][0]) {
            symbolicMatrix[i][j] = symbolNoExpression;
          }
          else { // GAUSSIAN 2
            if (threeGaussians_ByCondition[j].getE_Z_i2()[i][0] >=
                threeGaussians_ByCondition[j].getE_Z_i1()[i][0] &&
                threeGaussians_ByCondition[j].getE_Z_i2()[i][0] >=
                threeGaussians_ByCondition[j].getE_Z_i3()[i][0]) {
              symbolicMatrix[i][j] = symbolUpExpression;
            }
            else { // GAUSSIAN 3
              symbolicMatrix[i][j] = symbolDownExpression;
            }
          }
        } // END GAUSSIAN 3 - GAUSSIAN 1 - GAUSSIAN 2

        // GAUSSIAN ? - GAUSSIAN ? - GAUSSIAN 1

        // GAUSSIAN 2 - GAUSSIAN 3 - GAUSSIAN 1
        if (threeGaussians_ByCondition[j].getMean_2() <=
            threeGaussians_ByCondition[j].getMean_3() &&
            threeGaussians_ByCondition[j].getMean_3() <=
            threeGaussians_ByCondition[j].getMean_1()) {
          for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes();
               i++) {
            // discretize using symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolUpExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolDownExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolNoExpression)

            // GAUSSIAN 1
            if (threeGaussians_ByCondition[j].getE_Z_i1()[i][0] >=
                threeGaussians_ByCondition[j].getE_Z_i2()[i][0] &&
                threeGaussians_ByCondition[j].getE_Z_i1()[i][0] >=
                threeGaussians_ByCondition[j].getE_Z_i3()[i][0]) {
              symbolicMatrix[i][j] = symbolUpExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_ByCondition[j].getE_Z_i2()[i][0] >=
                  threeGaussians_ByCondition[j].getE_Z_i1()[i][0] &&
                  threeGaussians_ByCondition[j].getE_Z_i2()[i][0] >=
                  threeGaussians_ByCondition[j].getE_Z_i3()[i][0]) {
                symbolicMatrix[i][j] = symbolDownExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolNoExpression;
              }
            }
          }
        } // END GAUSSIAN 2 - GAUSSIAN 3 - GAUSSIAN 1

        // GAUSSIAN 3 - GAUSSIAN 2 - GAUSSIAN 1
        if (threeGaussians_ByCondition[j].getMean_3() <=
            threeGaussians_ByCondition[j].getMean_2() &&
            threeGaussians_ByCondition[j].getMean_2() <=
            threeGaussians_ByCondition[j].getMean_1()) {
          for (int i = 0; i < this.originalExpressionMatrix.getNumberOfGenes();
               i++) {
            // discretize using symbolDownExpression, symbolNoExpression and symbolUpExpression
            // E_Z_i1 = probability that a_ij belongs to the gaussian 1 (symbolUpExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 2 (symbolNoExpression)
            // E_Z_i2 = probability that a_ij belongs to the gaussian 3 (symbolDownExpression)

            // GAUSSIAN 1
            if (threeGaussians_ByCondition[j].getE_Z_i1()[i][0] >=
                threeGaussians_ByCondition[j].getE_Z_i2()[i][0] &&
                threeGaussians_ByCondition[j].getE_Z_i1()[i][0] >=
                threeGaussians_ByCondition[j].getE_Z_i3()[i][0]) {
              symbolicMatrix[i][j] = symbolUpExpression;
            }
            else { // GAUSSIAN 2
              if (threeGaussians_ByCondition[j].getE_Z_i2()[i][0] >=
                  threeGaussians_ByCondition[j].getE_Z_i1()[i][0] &&
                  threeGaussians_ByCondition[j].getE_Z_i2()[i][0] >=
                  threeGaussians_ByCondition[j].getE_Z_i3()[i][0]) {
                symbolicMatrix[i][j] = symbolNoExpression;
              }
              else { // GAUSSIAN 3
                symbolicMatrix[i][j] = symbolDownExpression;
              }
            }
          }
        } // END GAUSSIAN 3 - GAUSSIAN 1 - GAUSSIAN 2

      } // END BEST MODEL = 3
    }
//    out.close();
  }

} // END CLASS
