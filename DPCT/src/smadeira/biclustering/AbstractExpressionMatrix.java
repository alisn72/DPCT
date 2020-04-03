package smadeira.biclustering;

import ontologizer.calculation.EnrichedGOTermsResult;
import smadeira.ontologizer.GOFrontEnd;
import smadeira.ontologizer.GeneSetResult;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import java.io.*;

/**
 * <p>Title: Abstract Expression Matrix</p>
 *
 * <p>Description: Defines an abstract expression matrix.</p>
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
public abstract class AbstractExpressionMatrix
    implements IMatrix, Cloneable, Serializable, NodeObjectInterface {
  public static final long serialVersionUID = -9066290990679020970L;

  // FIELDS

  /**
   * Gene names.
   */
  protected String[] genesNames;

  /**
   * Condition names.
   */
  protected String[] conditionsNames;

  /**
   * Gene expression matrix: real valued matrix with N rows (genes)
   * and M columns (conditions) (NxM).
   */
  protected float[][] geneExpressionMatrix;

  /**
   * True if the gene expression matrix has missing values.
   */
  protected boolean hasMissingValues;

  /**
   * Value used to fill the missing values
   */
  protected float missingValue;

  /**
   *
   */
  protected boolean GoTermsComputed;

  /** Object of class GeneSetResult
     geneOntologyTerms.getNumberOfTerms() --> number of GO terms annotating the genes in the bicluster
     geneOntologyTerms.termID[i] --> ID of term i
     geneOntologyTerms.termName[] --> name of term i
     geneOntologyTermspValue[] --> pValue of term i (computed using the hyper-geometric distribution)
     geneOntologyTerms.correctedPValue[] --> corrected pValue of term i (corrected using Bonferroni correction for multiple testing)
   */
  protected GeneSetResult geneOntologyTerms;

  // #####################################################################################################################

  /**
   * Default constructor.
   */
  public AbstractExpressionMatrix() {
    this.geneExpressionMatrix = new float[0][0];
    this.genesNames = new String[0];
    this.conditionsNames = new String[0];
    this.hasMissingValues = false;
  }

  // #####################################################################################################################

  /**
   *
   * @param expressionMatrix float[][]
   * @param missingValue int
   */
  public AbstractExpressionMatrix(float[][] expressionMatrix,
                                  float missingValue) {
    this.geneExpressionMatrix = expressionMatrix;
    this.missingValue = missingValue;
    this.hasMissingValues = true;
    this.genesNames = new String[this.getNumberOfGenes()];
    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      this.genesNames[i] = new String("Gene_" + (i + 1));
    }
    this.conditionsNames = new String[this.getNumberOfConditions()];
    for (int i = 0; i < this.getNumberOfConditions(); i++) {
      this.conditionsNames[i] = new String("Condition_" + (i + 1));
    }
  }

  //###############################################################################################################################
//###############################################################################################################################

  /**
   *
   * @return boolean
   */
  public boolean getGoTermsComputed() {
    return this.GoTermsComputed;
  }

  /**
   *
   * @return GeneSetResult
   */
  public GeneSetResult getGeneOntologyProperties() {
    return this.geneOntologyTerms;
  }

  /**
   *
   * @return String
   */
  public abstract String getOrganism();

  /**
   *
   * @return String
   */
  public abstract String getDataType();

  /**
   *
   * @return String
   */
  public abstract String getGeneIdentifier();

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @param pathTo_GeneOntologyFile String
   * @param pathTo_GeneAssociationFile String
   * @param goTermAssocs boolean
   * @return GOFrontEnd
   * @throws Exception
   */
  public GOFrontEnd computeGeneOntologyTerms(String pathTo_GeneOntologyFile,
                                             String pathTo_GeneAssociationFile,
                                             boolean goTermAssocs) throws
      Exception {
    if (!this.GoTermsComputed) {
      GOFrontEnd go = new GOFrontEnd(this.genesNames, pathTo_GeneOntologyFile,
                                     pathTo_GeneAssociationFile);
        //go.setCorrectionMethod("Bonferroni");
      this.computeGeneOntologyTerms(go, goTermAssocs);
      return go;
    }
    else {
      return null;
    }
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @param go GOFrontEnd
   * @param goTermAssocs boolean
   * @throws Exception
   */
  public void computeGeneOntologyTerms(GOFrontEnd go, boolean goTermAssocs) {

    if (!this.GoTermsComputed) {
      this.geneOntologyTerms = go.calculateGeneSet(this.genesNames,
           goTermAssocs);
      this.GoTermsComputed = true;
    }
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * Creates .dot file with GO graph with terms enriched after Bonferroni correction.
   *
   * @param go GOFrontEnd
   * @param pathToDirectoryForResults String
   * @param dotFileName String
   * @param GO_pValues_userThreshold float
   * @throws Exception
   */
  public void printGraphWithGeneOntologyTerms_ToFile(GOFrontEnd go,
      String pathToDirectoryForResults, String dotFileName,
      float GO_pValues_userThreshold) throws Exception {
    if (GO_pValues_userThreshold <= 0) {
      throw new Exception("GO_pValues_userThreshold must be in ]0,1]");
    }

    File dotOutFile;
    if (pathToDirectoryForResults != null) {
      // CREATE DIRECTORY TO STORE THE RESULTS
      File dirForResults = new File(pathToDirectoryForResults);
      boolean exists = (dirForResults).exists();
      if (!exists) {
        dirForResults.mkdirs();
      }
      dotOutFile = new File(pathToDirectoryForResults, dotFileName);
    }
    else {
      dotOutFile = new File(dotFileName);
    }

    if (this.getSignificant_GO_BonferroniCorrected_pValues_UserThreshold(
        GO_pValues_userThreshold) > 0) {
      EnrichedGOTermsResult studySetResult = go.calculateStudySet(this.getGenesNames(),
          "EXPRESSION MATRIX");
      studySetResult.writeDOT(go.getGoTerms(), go.getGoGraph(), dotOutFile,
                              GO_pValues_userThreshold, true, null);
    }
  }

  //###############################################################################################################################

  /**
   * Number of (Bonferroni corrected) pValues, computed using the hypergeometric distribution and the Gene Ontology,
   * that are statistical significant (CCC-Bicluster is biologically significant). That is, number of
   * pValues < user predefined threshold.
   *
   * @param GO_pValues_userThreshold float User provided threshold used to consider a term as significantly enriched.
   * @return int
   */
  public int getSignificant_GO_BonferroniCorrected_pValues_UserThreshold(float
      GO_pValues_userThreshold) {
    if (!this.GoTermsComputed) {
      System.out.println("GO terms not computed!");
      return 0;
    }
    int significant_GO_BonferroniCorrected_pValues_userThreshold = 0;
    for (int i = 0; i < this.geneOntologyTerms.getNumberOfTerms(); i++) {
      if (this.geneOntologyTerms.getCorrectedPValue()[i] <
          GO_pValues_userThreshold) {
        significant_GO_BonferroniCorrected_pValues_userThreshold++;
      }
    }
    return (significant_GO_BonferroniCorrected_pValues_userThreshold);
  }

  //###############################################################################################################################
  // #####################################################################################################################

  /**
   *
   * @param missingValue float
   * @return boolean
   */
  protected boolean checkIfMatrixHasMissingValues(float missingValue) {
    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        if (this.getGeneExpressionMatrix()[i][j] == missingValue) {
          return (true);
        }
      }
    }
    return (false);
  }

  // SETS

  /**
   *
   * @param genesNames String[]
   */
  public void setGenesNames(String[] genesNames) {
    this.genesNames = genesNames;
  }

  /**
   *
   * @param conditionsNames String[]
   */
  public void setConditionsNames(String[] conditionsNames) {
    this.conditionsNames = conditionsNames;
  }

  /**
   *
   * @param geneExpressionMatrix float[][]
   */
  public void setGeneExpressionMatrix(float[][] geneExpressionMatrix) {
    this.geneExpressionMatrix = geneExpressionMatrix;
  }

  /**
   *
   * @param missingFlag boolean
   */
  public void setHasMissingValues(boolean missingFlag) {
    this.hasMissingValues = missingFlag;
  }

  // GETS

  /**
   *
   * @return int
   */
  public int getNumberOfGenes() {
    return (this.geneExpressionMatrix.length);
  }

  /**
   *
   * @return int
   */
  public int getNumberOfConditions() {
    return (this.geneExpressionMatrix[0].length);
  }

  /**
   *
   * @return String[]
   */
  public String[] getGenesNames() {
    return (this.genesNames);
  }


  /**
   *
   * @return String[]
   */
  public String[] getConditionsNames() {
    return (this.conditionsNames);
  }

  /**
   *
   * @return float[][]
   */
  public float[][] getGeneExpressionMatrix() {
    return (this.geneExpressionMatrix);
  }

  /**
   *
   * @return boolean
   */
  public boolean hasMissingValues() {
    return (this.hasMissingValues);
  }

  /**
   *
   * @return float
   */
  public float getMissingValue() {
    return (this.missingValue);
  }

  // ################################################ WRITE AND READ EXPRESSION MATRIX #################################

  /**
   * Writes the gene expression matrix to a .txt file. The values in the expression matrix are separated
   * by one of the following characters: '\t' (tab), ';' ',' or ' '.
   *
   * @param fileName String Name of the file to whom the gene expression matrix will be written.
   *
   * @param separator Character used to separate the values in the expression matrix.
   *
   * @throws Exception
   */
  public void writeExpressionMatrixToFileTXT(String fileName, char separator) throws
      Exception {

    // EXCEPTION!!!
    // 1) separator can only be '\t', ';' ',' or ' '
    // 2) the file type is .txt

    if (separator != '\t' && separator != ' ' && separator != ';') {
      throw new Exception(
          "INVALID SEPARATOR: separator can only be '\t', ';' ',' or ' '");
    }

    if (!fileName.substring(fileName.length() - 4).equals(new String(".txt"))) {
      throw new Exception("INVALID FILE TYPE: file type can only be '.txt'");
    }

    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
        fileName)));

    // First line = condition name
    if (this.conditionsNames != null) {
      out.print("CONDITIONS");
      out.print(separator);
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        out.print(this.conditionsNames[j] + separator);
      }
      out.println();
    }

    if (this.geneExpressionMatrix != null && this.genesNames != null) {

      for (int i = 0; i < this.getNumberOfGenes(); i++) {
        out.print(this.genesNames[i] + separator);
        for (int j = 0; j < this.getNumberOfConditions(); j++) {
          if (this.geneExpressionMatrix[i][j] == Integer.MIN_VALUE) { //missing value
            out.print("" + separator + separator);
          }
          else {
            out.print("" + this.geneExpressionMatrix[i][j] + separator);
          }
        }
        out.println();
      }
    }
    out.close();
  }

  /**
   * Writes a gene expression matrix to a .txt file. The values in the expression matrix are separated
   * by one of the following characters: '\t' (tab), ';' ',' or ' '.
   *
   * @param fileName String Name of the file to whom the gene expression matrix will be written.
   * @param separator Character used to separate the values in the expression matrix.
   * @param geneExpressionMatrix float[][] Matrix with the expression values.
   * @param genesNames String[] Array with the gene names.
   * @param conditionsNames String[] Array with the condition names.
   *
   * @throws Exception
   */
  public static void writeExpressionMatrixToFileTXT(String fileName,
      char separator,
      float[][] geneExpressionMatrix, String[] genesNames,
      String[] conditionsNames) throws Exception {

    if (separator != '\t' && separator != ' ' && separator != ';') {
      throw new Exception(
          "INVALID SEPARATOR: separator can only be '\t', ';' ',' or ' '");
    }

    if (!fileName.substring(fileName.length() - 4).equals(new String(".txt"))) {
      throw new Exception("INVALID FILE TYPE: file type can only be '.txt'");
    }

    int numberOfGenes = genesNames.length;
    int numberOfConditions = conditionsNames.length;

    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
        fileName)));

    // First line = condition name
    if (conditionsNames != null) {
      out.print("CONDITIONS");
      out.print(separator);
      for (int j = 0; j < numberOfConditions; j++) {
        out.print(conditionsNames[j] + separator);
      }
      out.println();
    }

    if (geneExpressionMatrix != null && genesNames != null) {

      for (int i = 0; i < numberOfGenes; i++) {
        out.print(genesNames[i] + separator);
        for (int j = 0; j < numberOfConditions; j++) {
          if (geneExpressionMatrix[i][j] == Integer.MIN_VALUE) { //missing value
            out.print("" + separator + separator);
          }
          else {
            out.print("" + geneExpressionMatrix[i][j] + separator);
          }

        }
        out.println();
      }
    }
    out.close();
  }

  /**
   * Writes the gene expression matrix to a .pgm file. The values in the expression matrix are
   * separated by one of the following characters: '\t' (tab), ';' ',' or ' '. The values in the
   * expression matrix (real-valued) are converted intovalues in the interval [0,65536].
   * (65536 is the maximum gray value in ASCII decimal).
   *
   * @param fileName String Name of the file to whom the gene expression matrix will be written.
   *
   * @param separator Character used to separate the values in the expression matrix.
   *
   * @throws Exception
   */
  public void writeExpressionMatrixToFilePGM(String fileName, char separator) throws
      Exception {

    // EXCEPTION!!!
    // 1) separator can only be '\t', ';' ',' or ' '
    // 2) the file type is .txt
    // 3) the real values converted from the matrix must not be greater than 65536

    if (separator != '\t' || separator != ' ' || separator != ';') {
      throw new Exception(
          "INVALID SEPARATOR: separator can only be '\t', ';' ',' or ' '");
    }

    if (!fileName.substring(fileName.length() - 5).equals(new String(".txt"))) {
      throw new Exception("INVALID FILE TYPE: file type can only be '.txt'");
    }

    // P2 => ASCII VALUES
    // P5 => BINARY VALUES
    // # = COMMENTS
    /* PGM FILE EXAMPLE
        P2 #ASCII VALUES
        24 7 #NUMBER_OF_ROWS NUMBER_OF_COLUMNS
        15   #MAX_VALUE
        #CONDITION_NAMES = ...
        #GENE NAMES = ...
        0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0
        0  3  3  3  3  0  0  7  7  7  7  0  0 11 11 11 11  0  0 15 15 15 15  0
        0  3  0  0  0  0  0  7  0  0  0  0  0 11  0  0  0  0  0 15  0  0 15  0
        0  3  3  3  0  0  0  7  7  7  0  0  0 11 11 11  0  0  0 15 15 15 15  0
        0  3  0  0  0  0  0  7  0  0  0  0  0 11  0  0  0  0  0 15  0  0  0  0
        0  3  0  0  0  0  0  7  7  7  7  0  0 11 11 11 11  0  0 15  0  0  0  0
        0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0
     */

    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
        fileName)));

    out.println("P2\t#ASCII VALUES");
    out.println(this.getNumberOfGenes() + "\t" + this.getNumberOfConditions() +
                "\t#NUMBER_OF_ROWS NUMBER_OF_COLUMNS");
    out.println("65536\t#MAX_VALUE");

    out.print("#CONDITION_NAMES =");
    if (this.conditionsNames != null) {
      out.print(separator);
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        out.print(this.conditionsNames[j] + separator);
      }
      out.println();
    }
    out.print("#GENE NAMES =");
    if (this.genesNames != null) {
      out.print(separator);
      for (int i = 0; i < this.getNumberOfGenes(); i++) {
        out.print(this.genesNames[i] + separator);
      }
      out.println();
    }

    // EXPRESSION MATRIX

    // CONVERT FLOAT
    //!!!!!!!!
    if (this.geneExpressionMatrix != null) {
      // compute max_a_ij and min_a_ij
      float min_a_ij = this.geneExpressionMatrix[0][0];
      float max_a_ij = this.geneExpressionMatrix[0][0];
      for (int i = 0; i < this.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.getNumberOfConditions(); j++) {
          if (this.geneExpressionMatrix[i][j] > max_a_ij) {
            max_a_ij = this.geneExpressionMatrix[i][j];
          }
          if (this.geneExpressionMatrix[i][j] < min_a_ij) {
            min_a_ij = this.geneExpressionMatrix[i][j];
          }
        }
        out.println();
      }
      // transform a_ij to the interval [0,65536] and write values into the file
      int[][] new_geneExpressionMatrix = new int[this.getNumberOfGenes()][this.
          getNumberOfConditions()];
      for (int i = 0; i < this.getNumberOfGenes(); i++) {
        for (int j = 0; j < this.getNumberOfConditions(); j++) {
          new_geneExpressionMatrix[i][j] = (int)
              Math.round(
                  (this.geneExpressionMatrix[i][j] - min_a_ij) * 65536 /
                  (max_a_ij - min_a_ij));
          out.print(new_geneExpressionMatrix[i][j] + separator);
        }
        out.println();
      }
    }
  }

  /**
   * Writes the genes names to a file.
   *
   * @param fileName String Name of the file to whom the gene names will be written.
   *
   * @throws IOException
   */
  public void writeGenesToFileTXT(String fileName) throws IOException {

    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
        fileName)));

    if (this.genesNames != null) {

      for (int i = 0; i < this.getNumberOfGenes(); i++) {
        out.println("" + this.genesNames[i]);
      }
      out.close();
    }
  }

  //................................................................

  /**
   *
   * @return Object
   */
  public Object clone() {
    AbstractExpressionMatrix o = null;
    try {
      o = (AbstractExpressionMatrix)super.clone();
    }
    catch (CloneNotSupportedException e) {
      System.err.println("AbstractExpressionMatrix can't clone");
    }
    o.genesNames = this.genesNames.clone();
    o.conditionsNames = this.conditionsNames.clone();
    o.geneExpressionMatrix = this.geneExpressionMatrix.clone();
    o.hasMissingValues = this.hasMissingValues;
    o.missingValue = this.missingValue;
    return o;
  }

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
