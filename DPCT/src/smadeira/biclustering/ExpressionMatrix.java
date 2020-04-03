package smadeira.biclustering;

import java.io.*;
import java.util.*;

import javax.swing.text.*;
import javax.swing.text.html.*;

/**
 * <p>Title: Expression Matrix</p>
 *
 * <p>Description: Expression Matrix.</p>
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
public class ExpressionMatrix
    extends AbstractExpressionMatrix implements Cloneable, Serializable,
    NodeObjectInterface {
  public static final long serialVersionUID = -8630049008479680483L;


  // FIELDS

  /**
   * Organism.
   *
   * EXAMPLE:
   *
   * HUMAN, MOUSE, ...
   */
  protected String organism;

  /**
   * Gene expression data type.
   *
   * EXAMPLE:
   *
   * cDNA, ...
   */
  protected String dataType;

  /**
   * Gene Identifier used in the gene expression matrix.
   *
   * EXAMPLE:
   *
   * GENE ID, ORF ID, ...
   */
  protected String geneIdentifier;

  // CONSTRUCTORS

  /**
   * Default constructor.
   */
  public ExpressionMatrix() {
    super();
    this.organism = new String();
    this.geneIdentifier = new String();
    this.dataType = new String();
  }

  /**
   *
   * @param filename String
   * @param separator char
   * @param organism String
   * @param geneID String
   * @param dataType String
   * @throws Exception
   */
  public ExpressionMatrix(String filename, char separator, String organism,
                          String geneID, String dataType) throws Exception {
    this.readExpressionMatrixFromFileTXT(filename, separator);
    this.organism = organism;
    this.dataType = dataType;
    this.geneIdentifier = geneID;
  }

  /**
   *
   * @param expressionMatrix float[][]
   * @param missingValue float
   * @param organism String
   * @param geneID String
   * @param dataType String
   */
  public ExpressionMatrix(float[][] expressionMatrix, float missingValue,
                          String organism, String geneID, String dataType) {
    super(expressionMatrix, missingValue);
    this.organism = organism;
    this.dataType = dataType;
    this.geneIdentifier = geneID;
  }

  /**
   *
   * @param expressionMatrix float[][]
   * @param organism String
   * @param geneID String
   * @param dataType String
   */
  public ExpressionMatrix(float[][] expressionMatrix, String organism,
                          String geneID, String dataType) {
    this(expressionMatrix, Integer.MIN_VALUE, organism, geneID, dataType);
    this.hasMissingValues = false;
  }

  /**
   *
   * @param genesNames String[]
   * @param conditionsNames String[]
   * @param expressionMatrix float[][]
   * @param missingValue float
   * @param organism String
   * @param geneID String
   * @param dataType String
   */
  public ExpressionMatrix(String[] genesNames, String[] conditionsNames,
                          float[][] expressionMatrix, float missingValue,
                          String organism, String geneID, String dataType) {
    this(expressionMatrix, missingValue, organism, geneID, dataType);
    this.genesNames = genesNames;
    this.conditionsNames = conditionsNames;
  }

  /**
   *
   * @param genesNames String[]
   * @param conditionsNames String[]
   * @param expressionMatrix float[][]
   * @param organism String
   * @param geneID String
   * @param dataType String
   */
  public ExpressionMatrix(String[] genesNames, String[] conditionsNames,
                          float[][] expressionMatrix, String organism,
                          String geneID, String dataType) {
    this(genesNames, conditionsNames, expressionMatrix, Integer.MIN_VALUE,
         organism, geneID, dataType);
    this.hasMissingValues = false;
  }

  /**
   *
   * @param genesNames String[]
   * @param conditionsNames String[]
   * @param expressionMatrix float[][]
   */
  public ExpressionMatrix(String[] genesNames, String[] conditionsNames,
                          float[][] expressionMatrix) {
    this(genesNames, conditionsNames, expressionMatrix, Integer.MIN_VALUE,
         new String(), new String(), new String());
    this.hasMissingValues = false;
  }

  // #####################################################################################################################

  /**
   *
   * @return String
   */
  public String getOrganism() {
    return (this.organism);
  }

  // #####################################################################################################################

  /**
   *
   * @return String
   */
  public String getGeneIdentifier() {
    return (this.geneIdentifier);
  }

  // #####################################################################################################################

  /**
   *
   * @return String
   */
  public String getDataType() {
    return (this.dataType);
  }

// #####################################################################################################################

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
  public void readExpressionMatrixFromFileTXT(String file, char separator) throws
      Exception {

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

    this.conditionsNames = new String[numberOfConditions];
    st.nextToken(); // skip string on the first column
    for (int j = 0; j < numberOfConditions; j++) {
      this.conditionsNames[j] = st.nextToken();
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
    this.genesNames = new String[numberOfGenes];
    this.geneExpressionMatrix = new float[numberOfGenes][numberOfConditions];

    // skip first line = condition names
    line = br.readLine();

    this.hasMissingValues = false;
    this.missingValue = Integer.MIN_VALUE;

    for (int i = 0; i < numberOfGenes; i++) {
      line = br.readLine();
      //MISSING VALUES WILL BE IDENTIFIED BY @
      line = line.replaceAll("" + separator + separator + separator,
                             separator + "@" + separator + "@" + separator);
      line = line.replaceAll("" + separator + separator,
                             separator + "@" + separator);

      //TOKENIZER
      st = new StringTokenizer(line, new Character(separator).toString());
      // store gene name = first column
      this.genesNames[i] = st.nextToken();
      for (int j = 0; j < numberOfConditions; j++) {
        if (st.hasMoreTokens()) {
          String token = st.nextToken();
//          System.out.println("_" + token + "_");

          if (token.equals("@")) { //missing value
            this.hasMissingValues = true;
            this.geneExpressionMatrix[i][j] = this.missingValue;
          }
          else {
            this.geneExpressionMatrix[i][j] = Float.parseFloat(token);
          }
        }
        else {
          this.geneExpressionMatrix[i][j] = this.missingValue;
        }
      }
    }
  }

  /**
   *
   * @return Object
   */
  public Object clone() {
    ExpressionMatrix o = (ExpressionMatrix)super.clone();
    o.dataType = this.dataType;
    o.geneIdentifier = this.geneIdentifier;
    o.organism = this.organism;
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
