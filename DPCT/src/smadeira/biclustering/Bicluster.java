package smadeira.biclustering;

import ontologizer.calculation.EnrichedGOTermsResult;
import ontologizer.go.Namespace;
import ontologizer.go.OBOParserException;
import smadeira.ontologizer.GOFrontEnd;
import smadeira.ontologizer.GeneSetResult;
import smadeira.utils.BiologicalRelevanceSummary;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import java.io.*;
import java.util.BitSet;

/**
 * <p>Title: Bicluster</p>
 *
 * <p>Description: Defines a bicluster.</p>
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
public class Bicluster
    implements Cloneable, Serializable, Comparable, NodeObjectInterface {
  public static final long serialVersionUID = -9199570643148022643L;

  // FIELDS

  /**
   * Object Biclustering where the bicluster was generated.
   */
  protected Biclustering biclusterSource;

  /**
   * Bicluster ID.
   */
  protected int ID;

  /**
   * Indexes start at 1 at not at 0.
   */
  protected int[] rowsIndexes;

  /**
   * Indexes start at 1 at not at 0.
   */
  protected int[] columnsIndexes;

//###############################################################################################################################
//###############################################################################################################################

  /**
   *
   */
  protected Bicluster() {
  }

  /**
   *
   * @param biclusterSource Biclustering
   * @param ID int
   * @param rows int[]
   * @param columns int[]
   */
  public Bicluster(Biclustering biclusterSource, int ID, int[] rows,
                   int[] columns) {
    this.biclusterSource = biclusterSource;
    this.rowsIndexes = rows;
    this.columnsIndexes = columns;
    this.ID = ID;
  }

//###############################################################################################################################
//###############################################################################################################################


  /**
   *
   * @return float
   */
  public float getMissingValue(){
  return ((DiscretizedExpressionMatrix)this.biclusterSource.matrix)
      .getMissingValue();
}
  /**
   *
   * @return int
   */
  public int getID() {
    return (this.ID);
  }

  /**
   *
   * @return Biclustering
   */
  public Biclustering getBiclusterSource() {
    return (this.biclusterSource);
  }

  /**
   *
   * @return int
   */
  public int getNumberOfGenes() {
    return (this.rowsIndexes.length);
  }

  /**
   *
   * @return int
   */
  public int getNumberOfConditions() {
    return (this.columnsIndexes.length);
  }

  /**
   *
   * @return int[]
   */
  public int[] getRowsIndexes() {
    return (this.rowsIndexes);
  }

  /**
   *
   * @return String[]
   */
  public String[] getGenesNames() {
    String[] genesNames = new String[this.getNumberOfGenes()];
    try{
        for (int i = 0; i < this.getNumberOfGenes(); i++) {
          genesNames[i] = ( (IMatrix)this.biclusterSource.getMatrix()).
              getGenesNames()[this.getRowsIndexes()[i] ];
        }
    }catch (Exception e){
        e.printStackTrace();
    }

    return (genesNames);
  }

  /**
   *
   * @return int[]
   */
  public int[] getColumnsIndexes() {
    return (this.columnsIndexes);
  }

  /**
   *
   * @return String[]
   */
  public String[] getConditionsNames() {
    String[] conditionsNames = new String[this.getNumberOfConditions()];
    for (int j = 0; j < this.getNumberOfConditions(); j++) {
      conditionsNames[j] = ( (IMatrix)this.biclusterSource.getMatrix()).
          getConditionsNames()[this.getColumnsIndexes()[j] - 1];
    }
    return (conditionsNames);
  }

  /**
   *
   * @return float[][]
   */
  public float[][] getBiclusterExpressionMatrix() {
    float[][] matrix = new float[this.getNumberOfGenes()][this.
        getNumberOfConditions()];
    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        matrix[i][j] = ( (IMatrix)this.biclusterSource.getMatrix()).
            getGeneExpressionMatrix()[this.getRowsIndexes()[i] -
            1][this.getColumnsIndexes()[j] - 1];
      }
    }
    return (matrix);
  }

  /**
   *
   * @return int
   */
  public int computeSIZE() {
    return (this.getNumberOfConditions() * this.getNumberOfGenes());
  }

  /**
   *
   * @param geneOntologyTerms GeneSetResult
   * @return double
   */
  public double computeBest_GO_pValue(GeneSetResult geneOntologyTerms) {
    double best_GO_pValue = 1.0;

    for (int i = 0; i < geneOntologyTerms.getNumberOfTerms(); i++) {
      if (geneOntologyTerms.getPValue()[i] < best_GO_pValue) {
        best_GO_pValue = geneOntologyTerms.getPValue()[i];
      }
    }
    return (best_GO_pValue);
  }

  /**
   *
   * @param geneOntologyTerms GeneSetResult
   * @return double
   */
  public double computeBest_GO_BonferroniCorrected_pValue(GeneSetResult
      geneOntologyTerms) {
    double best_GO_BonferroniCorrected_pValue = 1.0;
    for (int i = 0; i < geneOntologyTerms.getNumberOfTerms(); i++) {
      if (geneOntologyTerms.getCorrectedPValue()[i] <
          best_GO_BonferroniCorrected_pValue) {
        best_GO_BonferroniCorrected_pValue = geneOntologyTerms.
            getCorrectedPValue()[i];
      }
    }
    return (best_GO_BonferroniCorrected_pValue);
  }

  /**
   * Number of (not corrected) pValues, computed using the hypergeometric distribution and the Gene Ontology,
   * that are statistical significant (CCC-Bicluster is biologically significant). That is, number of
   * pValues < user predefined threshold.
   *
   * @param geneOntologyTerms GeneSetResult
   * @param GO_pValues_userThreshold float
   * @return int
   */
  public int computeSignificant_GO_new_measure(GeneSetResult
      geneOntologyTerms, float GO_pValues_userThreshold) {

    int significant_GO_pValues_userThreshold = 0;
    for (int i = 0; i < geneOntologyTerms.getNumberOfTerms(); i++) {
      if (geneOntologyTerms.getPValue()[i] < GO_pValues_userThreshold) {
        significant_GO_pValues_userThreshold++;
      }
    }
    return (significant_GO_pValues_userThreshold);
  }

    public int computeSignificant_GO_pValues_UserThreshold(GeneSetResult
                                                                   geneOntologyTerms, float GO_pValues_userThreshold) {

        int significant_GO_pValues_userThreshold = 0;
        for (int i = 0; i < geneOntologyTerms.getNumberOfTerms(); i++) {
            if (geneOntologyTerms.getPValue()[i] < GO_pValues_userThreshold) {
                significant_GO_pValues_userThreshold++;
            }
        }
        return (significant_GO_pValues_userThreshold);
    }

  /**
   * Number of (Bonferroni corrected) pValues, computed using the hypergeometric distribution and the Gene Ontology,
   * that are statistical significant (CCC-Bicluster is biologically significant). That is, number of
   * pValues < user predefined threshold.
   *
   * @param geneOntologyTerms GeneSetResult
   * @param GO_pValues_userThreshold float User provided threshold used to consider a term as significantly enriched.
   * @return int
   */
  public int computeSignificant_GO_BonferroniCorrected_pValues_UserThreshold(
      GeneSetResult geneOntologyTerms, float GO_pValues_userThreshold) {
    int significant_GO_BonferroniCorrected_pValues_userThreshold = 0;
    for (int i = 0; i < geneOntologyTerms.getNumberOfTerms(); i++) {
            //System.out.print(i+"\n");تغییر دادم
            if ( geneOntologyTerms.getCorrectedPValue().clone()[i] < GO_pValues_userThreshold) {
                try {
                    significant_GO_BonferroniCorrected_pValues_userThreshold++;
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }


    }
    return (significant_GO_BonferroniCorrected_pValues_userThreshold);
  }

  /**
   * Number of Not Corrected pValues < 0.01.
   *
   * @param geneOntologyTerms GeneSetResult
   * @return int
   */
  public int computeHighlySignificant_GO_pValues(GeneSetResult
                                                 geneOntologyTerms) {
    int significant_GO_pValues = 0;
    for (int i = 0; i < geneOntologyTerms.getNumberOfTerms(); i++) {
      if (geneOntologyTerms.getPValue()[i] < 0.01) {
        significant_GO_pValues++;
      }
    }
    return (significant_GO_pValues);
  }

  /**
   * Number of (Bonferroni Corrected) pValues < 0.01.
   *
   * @param geneOntologyTerms GeneSetResult
   * @return int
   */
  public int computeHighlySignificant_GO_BonferroniCorrected_pValues(
      GeneSetResult geneOntologyTerms) {
    int significant_GO_pValues = 0;
    for (int i = 0; i < geneOntologyTerms.getNumberOfTerms(); i++) {
      if (geneOntologyTerms.getCorrectedPValue()[i] < 0.01) {
        significant_GO_pValues++;
      }
    }
    return (significant_GO_pValues);
  }

  /**
   * Number of (not corrected) pValues, computed using the hypergeometric distribution and the Gene Ontology,
   * that are statistical significant (CCC-Bicluster is biologically significant). That is, number of
   * 0.01 <= pValues < 0.05 user predefined threshold
   *
   * @param geneOntologyTerms GeneSetResult
   * @return int
   */
  public int computeSignificant_GO_pValues(GeneSetResult geneOntologyTerms) {
    int significant_GO_pValues = 0;
    for (int i = 0; i < geneOntologyTerms.getNumberOfTerms(); i++) {
      if (geneOntologyTerms.getPValue()[i] >= 0.01 &&
          geneOntologyTerms.getPValue()[i] < 0.05) {
        significant_GO_pValues++;
      }
    }
    return (significant_GO_pValues);
  }

  /**
   * Number of (Bonferroni corrected) pValues, computed using the hypergeometric distribution and the Gene Ontology,
   * that are statistical significant (CCC-Bicluster is biologically significant). That is, number of
   * 0.01 <= pValues < 0.05 user predefined threshold.
   *
   * @param geneOntologyTerms GeneSetResult
   * @return int
   */
  public int computeSignificant_GO_BonferroniCorrected_pValues(GeneSetResult
      geneOntologyTerms) {
    int significant_GO_pValues = 0;
    for (int i = 0; i < geneOntologyTerms.getNumberOfTerms(); i++) {
      if (geneOntologyTerms.getCorrectedPValue()[i] >= 0.01 &&
          geneOntologyTerms.getCorrectedPValue()[i] < 0.05) {
        significant_GO_pValues++;
      }
    }
    return (significant_GO_pValues);
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * Computes bicluster variance.
   *
   * @return float
   */
  public float computeVAR() {
    float a_IJ;
    float sum_a_ij = 0;
    float VAR = 0;
    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        sum_a_ij = sum_a_ij + this.getBiclusterExpressionMatrix()[i][j];
      }
    }
    a_IJ = sum_a_ij / (this.getNumberOfGenes() * this.getNumberOfConditions());

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        VAR = VAR +
            (float) Math.pow(this.getBiclusterExpressionMatrix()[i][j] - a_IJ,
                             2);
      }
    }
    return (VAR / (this.getNumberOfGenes() * this.getNumberOfConditions()));
  }

//###############################################################################################################################

  /**
   * Computes bicluster average row variance.
   *
   * @return float
   */
  public float computeARV() {
    float[] a_iJ = new float[this.getNumberOfGenes()];
    float ARV = 0;

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      a_iJ[i] = 0;
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        a_iJ[i] = a_iJ[i] + this.getBiclusterExpressionMatrix()[i][j];
      }
      a_iJ[i] = a_iJ[i] / this.getNumberOfConditions();
    }

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        ARV = ARV +
            (float) Math.pow(this.getBiclusterExpressionMatrix()[i][j] - a_iJ[i],
                             2);
      }
    }
    return (ARV / (this.getNumberOfGenes() * this.getNumberOfConditions()));
  }

//###############################################################################################################################

  /**
   * Computes bicluster average column variance.
   *
   * @return float
   */
  public float computeACV() {
    float[] a_Ij = new float[this.getNumberOfConditions()];
    float ACV = 0;

    for (int j = 0; j < this.getNumberOfConditions(); j++) {
      a_Ij[j] = 0;
      for (int i = 0; i < this.getNumberOfGenes(); i++) {
        a_Ij[j] = a_Ij[j] + this.getBiclusterExpressionMatrix()[i][j];
      }
      a_Ij[j] = a_Ij[j] / this.getNumberOfGenes();
    }

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        ACV = ACV +
            (float) Math.pow(this.getBiclusterExpressionMatrix()[i][j] - a_Ij[j],
                             2);
      }
    }
    return (ACV / (this.getNumberOfGenes() * this.getNumberOfConditions()));
  }

//###############################################################################################################################

  /**
   * Computes bicluster mean squared residue.
   *
   * @return float
   */
  public float computeMSR() {
    float a_IJ;
    float sum_a_ij = 0;
    float[] a_iJ = new float[this.getNumberOfGenes()];
    float[] a_Ij = new float[this.getNumberOfConditions()];
    float MSR = 0;

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        sum_a_ij = sum_a_ij + this.getBiclusterExpressionMatrix()[i][j];
      }
    }
    a_IJ = sum_a_ij / (this.getNumberOfGenes() * this.getNumberOfConditions());

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      a_iJ[i] = 0;
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        a_iJ[i] = a_iJ[i] + this.getBiclusterExpressionMatrix()[i][j];
      }
      a_iJ[i] = a_iJ[i] / this.getNumberOfConditions();
    }

    for (int j = 0; j < this.getNumberOfConditions(); j++) {
      a_Ij[j] = 0;
      for (int i = 0; i < this.getNumberOfGenes(); i++) {
        a_Ij[j] = a_Ij[j] + this.getBiclusterExpressionMatrix()[i][j];
      }
      a_Ij[j] = a_Ij[j] / this.getNumberOfGenes();
    }

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      for (int j = 0; j < this.getNumberOfConditions(); j++) {
        float r_a_ij = this.getBiclusterExpressionMatrix()[i][j] - a_iJ[i] -
            a_Ij[j] +
            a_IJ;
        MSR = MSR + (float) Math.pow(r_a_ij, 2);
      }
    }
    MSR = MSR / (this.getNumberOfGenes() * this.getNumberOfConditions());
    return (MSR);
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * GO Terms are computed using the 3 ontologies: Biological Process,
   * @param pathToGeneOntologyFile String
   * @param pathToGeneAssociationFile String
   * @param goTermAssocs boolean Compute GOTermsAssociations (which genes are anotated by each GO term).
   * @return GeneSetResult
   * Object of class GeneSetResult
   *    geneOntologyTerms.getNumberOfTerms() --> number of GO terms annotating the genes in the bicluster
   *    geneOntologyTerms.termID[i] --> ID of term i
   *    geneOntologyTerms.termName[] --> name of term i
   *    geneOntologyTermspValue[] --> pValue of term i (computed using the hyper-geometric distribution)
   *    geneOntologyTerms.correctedPValue[] --> corrected pValue of term i (corrected using Bonferroni correction for multiple testing)
   */
  public GeneSetResult computeGeneOntologyTerms(String pathToGeneOntologyFile,
                                                String pathToGeneAssociationFile,
                                                boolean goTermAssocs)
      throws IOException, OBOParserException {
    GOFrontEnd go = new GOFrontEnd( ( (IMatrix)this.biclusterSource.getMatrix()).
                                   getGenesNames(),
                                   pathToGeneOntologyFile,
                                   pathToGeneAssociationFile);
    return this.computeGeneOntologyTerms(go, goTermAssocs);
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * Filter GO given a the specified ontology.
   *
   * @param pathTo_GeneOntologyFile String
   * @param pathTo_GeneAssociationFile String
   * @param goTermAssocs boolean Compute GOTermsAssociations (which genes are anotated by each GO term).
   * @param ontology char Filter gene ontology terms to a given ontology:
   * 'B' - BIOLOGICAL PROCESS
   * 'C' - CELLULAR COMPONENT
   * 'M' - METABOLIC FUNCTION
   *
   * @return GeneSetResult Object of class GeneSetResult
   *    geneOntologyTerms.getNumberOfTerms() --> number of GO terms annotating the genes in the bicluster
   *    geneOntologyTerms.termID[i] --> ID of term i
   *    geneOntologyTerms.termName[] --> name of term i
   *    geneOntologyTermspValue[] --> pValue of term i (computed using the hyper-geometric distribution)
   *    geneOntologyTerms.correctedPValue[] --> corrected pValue of term i (corrected using Bonferroni correction for multiple testing)
   *
   * @throws Exception
   */
  public GeneSetResult computeGeneOntologyTerms(String pathTo_GeneOntologyFile,
                                                String pathTo_GeneAssociationFile,
                                                boolean goTermAssocs, char ontology) throws Exception{

    if (ontology != 'B' && ontology != 'C' && ontology != 'M'){
      throw new Exception("WRONG PARAMETER: ontology CAN ONLY BE one of the following: \n 'B' - BIOLOGICAL PROCESS \n 'C' - CELLULAR COMPONENT or 'M' - METABOLIC FUNCTION");
    }

    GOFrontEnd go = new GOFrontEnd(((IMatrix)this.biclusterSource.getMatrix()).
                                   getGenesNames(),
                                   pathTo_GeneOntologyFile,
                                   pathTo_GeneAssociationFile);
    if(ontology == 'B'){
      go.setFilterByTermNamespace(new Namespace("biological_process"));
    }
    else{
      if(ontology == 'C'){
        go.setFilterByTermNamespace(new Namespace("cellular_component"));
      }
      else{
          go.setFilterByTermNamespace(new Namespace("molecular_function"));
      }
    }
    return this.computeGeneOntologyTerms(go, goTermAssocs);
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * Compute GO terms in the 3 ontologies.
   *
   * @param go GOFrontEnd
   * @param goTermAssocs boolean
   *
   * @return GeneSetResult Object of class GeneSetResult
   *    geneOntologyTerms.getNumberOfTerms() --> number of GO terms annotating the genes in the bicluster
   *    geneOntologyTerms.termID[i] --> ID of term i
   *    geneOntologyTerms.termName[] --> name of term i
   *    geneOntologyTermspValue[] --> pValue of term i (computed using the hyper-geometric distribution)
   *    geneOntologyTerms.correctedPValue[] --> corrected pValue of term i (corrected using Bonferroni correction for multiple testing)
   */
  public GeneSetResult computeGeneOntologyTerms(GOFrontEnd go, boolean goTermAssocs) {
      return (go.calculateGeneSet(this.getGenesNames(), goTermAssocs));
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * Filter GO not in the specified ontology.
   *
   * @param go GOFrontEnd
   * @param goTermAssocs boolean
   * @param ontology char Filter gene ontology terms to a given ontology:
   * 'B' - BIOLOGICAL PROCESS
   * 'C' - CELLULAR COMPONENT
   * 'M' - METABOLIC FUNCTION
   *
   * @return GeneSetResult Object of class GeneSetResult
   *    geneOntologyTerms.getNumberOfTerms() --> number of GO terms annotating the genes in the bicluster
   *    geneOntologyTerms.termID[i] --> ID of term i
   *    geneOntologyTerms.termName[] --> name of term i
   *    geneOntologyTermspValue[] --> pValue of term i (computed using the hyper-geometric distribution)
   *    geneOntologyTerms.correctedPValue[] --> corrected pValue of term i (corrected using Bonferroni correction for multiple testing)
   *
   * @throws Exception
   */
  public GeneSetResult computeGeneOntologyTerms(GOFrontEnd go,
                                                boolean goTermAssocs,
                                                char ontology) throws Exception{

    if (ontology != 'B' && ontology != 'C' && ontology != 'M'){
      throw new Exception("WRONG PARAMETER: ontology CAN ONLY BE one of the following: \n 'B' - BIOLOGICAL PROCESS \n 'C' - CELLULAR COMPONENT or 'M' - METABOLIC FUNCTION");
    }

    if(ontology == 'B'){
      go.setFilterByTermNamespace(new Namespace("biological_process"));
    }
    else{
      if(ontology == 'C'){
        go.setFilterByTermNamespace(new Namespace("cellular_component"));
      }
      else{
          go.setFilterByTermNamespace(new Namespace("molecular_function"));
      }
    }

    return (go.calculateGeneSet(this.getGenesNames(), goTermAssocs));
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * Computes the following information:
   * Bicluster ID
   * Best p-value
   * Best (Bonferroni corrected) p-value
   * # significant terms --> 0.01 <= (Bonferroni corrected) p-value < 0.05
   * # highly significant terms --> (Bonferroni corrected)p-value < 0.01
   * # significant terms according to user threshold --> (Bonferroni corrected) p-value < GO_pValues_userThreshold
   * @param geneOntologyTerms GeneSetResult
   * @param GO_pValues_userThreshold float
   * @return BiologicalRelevanceSummary
   */
  public BiologicalRelevanceSummary computeBiologicalRelevanceSummary(GeneSetResult geneOntologyTerms, float GO_pValues_userThreshold) {
    return new BiologicalRelevanceSummary(this.computeBest_GO_pValue(geneOntologyTerms),
                                          this.computeBest_GO_BonferroniCorrected_pValue(geneOntologyTerms),
                                          this.computeSignificant_GO_BonferroniCorrected_pValues(geneOntologyTerms),
                                          this.computeHighlySignificant_GO_BonferroniCorrected_pValues(geneOntologyTerms),
                                          GO_pValues_userThreshold,
                                          this.computeSignificant_GO_BonferroniCorrected_pValues_UserThreshold(geneOntologyTerms, GO_pValues_userThreshold));
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * Prints the following information:
   * Bicluster ID
   * Best p-value
   * Best (Bonferroni corrected) p-value
   * # significant terms --> 0.01 <= (Bonferroni corrected) p-value < 0.05
   * # highly significant terms --> (Bonferroni corrected)p-value < 0.01
   * # significant terms according to user threshold --> (Bonferroni corrected) p-value < GO_pValues_userThreshold
   *
   * @param geneOntologyTerms GeneSetResult
   * @param GO_pValues_userThreshold float
   */
  public void printBiologicalRelevanceSummary(GeneSetResult geneOntologyTerms, float GO_pValues_userThreshold) {
    BiologicalRelevanceSummary bio = this.computeBiologicalRelevanceSummary(geneOntologyTerms, GO_pValues_userThreshold);
    System.out.println("Bicluster ID" + "\t" + "Best p-value" + "\t" +
                       "Best (Bonferroni corrected) p-value" +
                       "# significant terms --> 0.01 <= (Bonferroni corrected) p-value < 0.05" +
                       "\t" +
                       "# highly significant terms --> (Bonferroni corrected)p-value < 0.01" +
                       "\t" +
                       "# significant terms according to user threshold --> (Bonferroni corrected) p-value < GO_pValues_userThreshold");
    System.out.println(this.ID + "\t" + bio.getBestPvalue() + "\t" +
                       bio.getBestCorrectedPvalue() + "\t" +
                       bio.getNumberOfSignificantCorrectedTerms() + "\t" +
                       bio.getNumberOfHighlySignificantCorrectedTerms() + "\t" +
                       bio.getNumberOfUserThresholdSignificantCorrectedTerms());
  }
  //###############################################################################################################################

  /**
   *
   * @param geneOntologyTerms GeneSetResult
   * @param GO_pValues_userThreshold float
   * @return String
   */
  public String getBiologicalRelevanceSummary(GeneSetResult geneOntologyTerms, float GO_pValues_userThreshold) {
    BiologicalRelevanceSummary bio = this.computeBiologicalRelevanceSummary(geneOntologyTerms,
                                          GO_pValues_userThreshold);
    return (this.ID + "\t" + bio.getBestPvalue() + "\t" +
                       bio.getBestCorrectedPvalue() + "\t" +
                       bio.getNumberOfSignificantCorrectedTerms() + "\t" +
                       bio.getNumberOfHighlySignificantCorrectedTerms() + "\t" +
                       bio.getNumberOfUserThresholdSignificantCorrectedTerms());
  }


  //###############################################################################################################################

  /**
   * Prints the following information to file:
   * Bicluster ID
   * Best p-value
   * Best (Bonferroni corrected) p-value
   * # significant terms --> 0.01 <= (Bonferroni corrected) p-value < 0.05
   * # highly significant terms --> (Bonferroni corrected)p-value < 0.01
   * # significant terms according to user threshold --> (Bonferroni corrected) p-value < GO_pValues_userThreshold
   *
   * @param pathToDirectory String
   * @param filename String
   * @param geneOntologyTerms GeneSetResult
   * @param GO_pValues_userThreshold float
   * @throws IOException
   */
  public void printBiologicalRelevanceSummary_ToFile(String pathToDirectory, String filename,
      GeneSetResult geneOntologyTerms, float GO_pValues_userThreshold) throws IOException{

    PrintWriter fileOut;
    if (pathToDirectory != null) {
      // CREATE DIRECTORY TO STORE THE RESULTS
      File dirForResults = new File(pathToDirectory);
      boolean exists = (dirForResults).exists();
      if (!exists) {
        dirForResults.mkdirs();
      }
      if(filename != null){
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new
            File(pathToDirectory, filename))), true);
      }
      else{
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new
            File(pathToDirectory, "GO_Summary.txt"))), true);
      }
    }
    else {
      if(filename != null){
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename))), true);
      }
      else{
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new File("GO_Summary.txt"))), true);
      }
    }

    BiologicalRelevanceSummary bio = this.computeBiologicalRelevanceSummary(geneOntologyTerms,GO_pValues_userThreshold);
    fileOut.println("ID" + "\t" + "Best p-value" + "\t" + "Best p-value (Bonferroni corrected)" + "\t" + "# significant terms --> 0.01 <= (Bonferroni corrected) p-value < 0.05" + "\t" +
                    "# highly significant terms --> (Bonferroni corrected)p-value < 0.01" + "\t" +
                    "# significant terms according to user threshold --> (Bonferroni corrected) p-value < GO_pValues_userThreshold");

    fileOut.println(this.ID + "\t" + bio.getBestPvalue() + "\t" +
                       bio.getBestCorrectedPvalue() + "\t" +
                       bio.getNumberOfSignificantCorrectedTerms() + "\t" +
                       bio.getNumberOfHighlySignificantCorrectedTerms() + "\t" +
                       bio.getNumberOfUserThresholdSignificantCorrectedTerms());
    fileOut.close();
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * Creates .dot file with GO graph with terms enriched after Bonferroni correction.
   *
   * @param pathToGeneOntologyFile String
   * @param pathToGeneAssociationFile String
   * @param pathToDirectory String
   * @param filename String
   * @param GO_pValues_userThreshold float
   * @throws Exception
   */
  public void printGraphWithGeneOntologyTerms_ToFile(String
      pathToGeneOntologyFile, String pathToGeneAssociationFile,
      String pathToDirectory, String filename, float GO_pValues_userThreshold) throws
      Exception {
    if (GO_pValues_userThreshold <= 0) {
      throw new Exception("GO_pValues_userThreshold must be in ]0,1]");
    }

    GOFrontEnd go = new GOFrontEnd( ( (IMatrix)this.biclusterSource.getMatrix()).
                                   getGenesNames(),
                                   pathToGeneOntologyFile,
                                   pathToGeneAssociationFile);
    GeneSetResult geneOntologyTerms = this.computeGeneOntologyTerms(go, false);

    if (this.computeSignificant_GO_BonferroniCorrected_pValues_UserThreshold(
        geneOntologyTerms, GO_pValues_userThreshold) > 0) {
      this.printGraphWithGeneOntologyTerms_ToFile(go,
                                                  pathToDirectory, filename,
                                                  GO_pValues_userThreshold);
    }
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * Creates .dot file with GO graph with terms enriched after Bonferroni correction.
   *
   * @param go GOFrontEnd
   * @param pathToDirectory String
   * @param filename String
   * @param GO_pValues_userThreshold float
   * @throws Exception
   */
  public void printGraphWithGeneOntologyTerms_ToFile(GOFrontEnd go,
      String pathToDirectory, String filename, float GO_pValues_userThreshold) throws Exception {
    if (GO_pValues_userThreshold <= 0) {
      throw new Exception("GO_pValues_userThreshold must be in ]0,1]");
    }

    File dotOutFile;
    if (pathToDirectory != null) {
      // CREATE DIRECTORY TO STORE THE RESULTS
      File dirForResults = new File(pathToDirectory);
      boolean exists = (dirForResults).exists();
      if (!exists) {
        dirForResults.mkdirs();
      }
      if(filename == null){
        dotOutFile = new File(pathToDirectory, "BICLUSTER_" + this.getID() + "_GO_Graph.dot");
      }
      else{
        dotOutFile = new File(pathToDirectory, filename);
      }
    }
    else {
      if(filename == null){
        dotOutFile = new File("BICLUSTER_" + this.getID() + "_GO_Graph.dot");
      }
      else{
        dotOutFile = new File(filename);
      }
    }

    GeneSetResult geneOntologyTerms = this.computeGeneOntologyTerms(go, false);
    if (this.computeSignificant_GO_BonferroniCorrected_pValues_UserThreshold(
        geneOntologyTerms, GO_pValues_userThreshold) > 0) {
      EnrichedGOTermsResult studySetResult = go.calculateStudySet(this.getGenesNames(),
          "BICLUSTER_" + this.ID);
      studySetResult.writeDOT(go.getGoTerms(), go.getGoGraph(), dotOutFile,
                              GO_pValues_userThreshold, true, null);
    }
  }

//###############################################################################################################################
//###############################################################################################################################

  /**
   * PRINTS TABLE WITH BIOLOGICAL FUNCTIONS AND P-VALUES COMPUTED WITH ONTOLOGIZER.
   *
   * @param geneOntologyTerms GeneSetResult
   */
  public void printTableWithGeneOntologyTerms(GeneSetResult geneOntologyTerms) {

    System.out.println("TERM ID" + "\t" +
                       "TERM NAME" + "\t" +
                       "#GENES IN POPULATION" + "\t" +
                       "GENES IN POPULATION ANNOTATED WITH THIS TERM" + "\t" +
                       "#GENES IN BICLUSTER" + "\t" +
                       "GENES IN BICLUSTER ANNOTATED WITH THIS TERM" + "\t" +
                       "HYPERGEOMETRIC P-VALUE" + "\t" +
                       "BONFERRONI CORRECTED HYPERGEOMETRIC P-VALUE");
    for (int i = 0; i < geneOntologyTerms.getNumberOfTerms(); i++) {
      System.out.println(geneOntologyTerms.getTermID()[i] + "\t" +
                         geneOntologyTerms.getTermName()[i] + "\t" +
                         geneOntologyTerms.getPopulationGeneCount() + "\t" +
                         geneOntologyTerms.
                         getGenesInPopulationAnnotatedWithTerm()[i] + "\t" +
                         getNumberOfGenes() + "\t" +
                         geneOntologyTerms.getGenesInBiclusterAnnotatedWithTerm()[
                         i] + "\t" +
                         geneOntologyTerms.getPValue()[i] + "\t" +
                         geneOntologyTerms.getCorrectedPValue()[i]);
    }
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * PRINTS TABLE WITH BIOLOGICAL FUNCTIONS AND P-VALUES COMPUTED WITH ONTOLOGIZER TO FILE.
   *
   * @param geneOntologyTerms GeneSetResult
   * @param pathToDirectory String
   * @param filename String
   * @param GO_pValues_userThreshold float
   * @param useCorrectedPValues boolean
   * @throws IOException
   */
  public void printTableWithGeneOntologyTerms_ToFile(GeneSetResult
      geneOntologyTerms, String pathToDirectory, String filename,
      float GO_pValues_userThreshold,
      boolean useCorrectedPValues) throws IOException {

    PrintWriter fileOut;
    if (pathToDirectory != null) {
      // CREATE DIRECTORY TO STORE THE RESULTS
      File dirForResults = new File(pathToDirectory);
      boolean exists = (dirForResults).exists();
      if (!exists) {
        dirForResults.mkdirs();
      }
      if(filename == null){
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new
            File(pathToDirectory, "BICLUSTER_" + this.ID + "_GO_Table.txt"))), true);
      }
      else{
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new
            File(pathToDirectory, filename))), true);
      }
    }
    else {
      if(filename == null){
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new File("BICLUSTER_" + this.ID + "_GO_Table.txt"))), true);
      }
      else{
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename))), true);
      }
    }

    fileOut.println("TERM ID" + "\t" +
                    "TERM NAME" + "\t" +
                    "#GENES IN POPULATION" + "\t" +
                    "GENES IN POPULATION ANNOTATED WITH THIS TERM" + "\t" +
                    "#GENES IN BICLUSTER" + "\t" +
                    "GENES IN BICLUSTER ANNOTATED WITH THIS TERM" + "\t" +
                    "HYPERGEOMETRIC P-VALUE" + "\t" +
                    "BONFERRONI CORRECTED HYPERGEOMETRIC P-VALUE");
    for (int i = 0; i < geneOntologyTerms.getNumberOfTerms(); i++) {
      if ( (geneOntologyTerms.getCorrectedPValue()[i] <
            GO_pValues_userThreshold && useCorrectedPValues) ||
          (geneOntologyTerms.getPValue()[i] < GO_pValues_userThreshold &&
           !useCorrectedPValues)) {
        fileOut.println(geneOntologyTerms.getTermID()[i] + "\t" +
                        geneOntologyTerms.getTermName()[i] + "\t" +
                        geneOntologyTerms.getPopulationGeneCount() + "\t" +
                        geneOntologyTerms.getGenesInPopulationAnnotatedWithTerm()[
                        i] + "\t" +
                        getNumberOfGenes() + "\t" +
                        geneOntologyTerms.getGenesInBiclusterAnnotatedWithTerm()[
                        i] + "\t" +
                        geneOntologyTerms.getPValue()[i] + "\t" +
                        geneOntologyTerms.getCorrectedPValue()[i]);
      }
    }
    fileOut.close();
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   * PRINTS GENES TO FILE.
   *
   * @param pathToDirectory String
   * @param filename String
   * @throws IOException
   */
  public void printGenes_ToFile(String pathToDirectory, String filename) throws
      IOException {
    PrintWriter fileOut;
    if (pathToDirectory != null) {
      // CREATE DIRECTORY TO STORE THE RESULTS
      File dirForResults = new File(pathToDirectory);
      boolean exists = (dirForResults).exists();
      if (exists) {
        System.out.println( "THE DIRECTORY TO STORE THE RESULTS ALREADY EXISTS!");
      }
      else {
//        System.out.println("CREATING THE DIRECTORY TO STORE THE RESULTS...");
        boolean success = (new File(pathToDirectory)).mkdirs();
        if (!success) {
          System.out.println(
              "CREATION OF DIRECTORY TO STORE THE RESULTS FAILED!");
        }
      }

      fileOut = new PrintWriter(new BufferedWriter(new FileWriter(new
          File(pathToDirectory, filename))), true);
    }
    else {
      fileOut = new PrintWriter(new BufferedWriter(new FileWriter(filename)), true);
    }

    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      fileOut.println(this.getGenesNames()[i]);
    }
    fileOut.close();
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @param pathToDirectory String
   * @param filename String
   * @param width int
   * @param height int
   * @param normalize boolean
   * @param legend boolean
   */
  public void printColorChartGeneExpressionAllConditions_ToFile(String pathToDirectory, String filename, int width, int height, boolean normalize, boolean legend){
  File fileOut;
    if (pathToDirectory != null) {
      // CREATE DIRECTORY TO STORE THE RESULTS
      File dirForResults = new File(pathToDirectory);
      boolean exists = (dirForResults).exists();
      if (exists) {
//        System.out.println("THE DIRECTORY TO STORE THE RESULTS ALREADY EXISTS!");
      }
      else {
        System.out.println("CREATING THE DIRECTORY TO STORE THE RESULTS...");
        boolean success = (new File(pathToDirectory)).mkdirs();
        if (!success) {
          System.out.println(
              "CREATION OF DIRECTORY TO STORE THE RESULTS FAILED!");
        }
      }
      if(filename != null){
        fileOut = new File(pathToDirectory, filename);
      }
      else{
        fileOut = new File(pathToDirectory, "BICLUSTER_" + this.ID + "_ExpressionPatternChart.png");
      }
    }
    else {
      if(filename != null){
        fileOut = new File(filename);
      }
      else{
        fileOut = new File("BICLUSTER_" + this.ID + "_ExpressionPatternChart.png");
      }
    }

    //PanelExpressionChart panel = null;
    try {
      //panel = PanelExpressionChart.createExpressionAllTimePointsChart(this, width, height, normalize, legend);
      //ChartUtilities.saveChartAsPNG(fileOut, panel.getChart(), width, height);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @param pathToDirectory String
   * @param filename String
   * @param width int
   * @param height int
   * @param normalize boolean
   * @param legend boolean
   */
  public void printColorChartGeneExpressionBiclusterConditions_ToFile(String pathToDirectory, String filename, int width, int height, boolean normalize, boolean legend){
    File fileOut;
    if (pathToDirectory != null) {
      // CREATE DIRECTORY TO STORE THE RESULTS
      File dirForResults = new File(pathToDirectory);
      boolean exists = (dirForResults).exists();
      if (exists) {
//        System.out.println("THE DIRECTORY TO STORE THE RESULTS ALREADY EXISTS!");
      }
      else {
        System.out.println("CREATING THE DIRECTORY TO STORE THE RESULTS...");
        boolean success = (new File(pathToDirectory)).mkdirs();
        if (!success) {
          System.out.println(
              "CREATION OF DIRECTORY TO STORE THE RESULTS FAILED!");
        }
      }
      fileOut = new File(pathToDirectory, filename);
    }
    else {
      fileOut = new File(filename);
    }

    //PanelExpressionChart panel = null;
    boolean miniature = false;
    try {
      //panel = PanelExpressionChart.createExpressionBiclusterTimePointsChart(this, width, height, normalize, miniature, legend);
      //ChartUtilities.saveChartAsPNG(fileOut, panel.getChart(), width, height);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  //###############################################################################################################################
  //###############################################################################################################################

  /**
   *
   * @param o Object
   * @return boolean
   */
  public boolean equals(Object o) {
    if (this == null || o == null || ! (o instanceof Bicluster)) {
      return false;
    }
    if (!this.biclusterSource.equals( ( (Bicluster) o).biclusterSource)) {
      return false;
    }
    if (this.getNumberOfGenes() != ( (Bicluster) o).getNumberOfGenes()) {
      return false;
    }
    if (this.getNumberOfConditions() != ( (Bicluster) o).getNumberOfConditions()) {
      return false;
    }
    BitSet conditions = new BitSet(this.getNumberOfConditions());
    BitSet conditions_o = new BitSet(this.getNumberOfConditions());
    for (int i = 0; i < this.getNumberOfConditions(); i++) {
      conditions.set(this.rowsIndexes[i]);
      conditions_o.set( ( (Bicluster) o).rowsIndexes[i]);
    }
    BitSet genes = new BitSet(this.getNumberOfGenes());
    BitSet genes_o = new BitSet(this.getNumberOfGenes());
    for (int i = 0; i < this.getNumberOfGenes(); i++) {
      genes.set(this.rowsIndexes[i]);
      genes_o.set( ( (Bicluster) o).rowsIndexes[i]);
    }
    return (conditions.equals(conditions_o) && genes.equals(genes_o));
  }

//###############################################################################################################################
  /**
   *
   * @return int
   */
  public int hashCode() {
    int hash = 17;
    if (this.rowsIndexes != null) {
      BitSet conditions = new BitSet(this.getNumberOfConditions());
      for (int i = 0; i < this.getNumberOfConditions(); i++) {
        conditions.set(this.rowsIndexes[i]);
      }
      hash = 37 * conditions.hashCode();
    }
    if (this.rowsIndexes != null) {
      BitSet genes = new BitSet(this.getNumberOfGenes());
      for (int i = 0; i < this.getNumberOfGenes(); i++) {
        genes.set(this.rowsIndexes[i]);
      }
      hash = 37 * genes.hashCode();
    }
    return hash;
  }

  //###############################################################################################################################

  /**
   *
   * @param o1 Object
   * @return int
   */
  public int compareTo(Object o1) {
    return ( ( (Integer) (this.ID)).compareTo( ( (Integer) ( (Bicluster) o1).ID)));
  }

  /**
   *
   * @return Object
   */
  public Object clone() {
    Bicluster o = null;
    try {
      o = (Bicluster)super.clone();
    }
    catch (CloneNotSupportedException e) {
      System.err.println("Bicluster can't clone");
    }
    o.ID = this.ID;
    return o;
  }

  //###############################################################################################################################
  //###############################################################################################################################

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
        "GENES= " + this.getNumberOfGenes() + "\n";

    float[][] biclusterExpressionMatrix = this.getBiclusterExpressionMatrix();
    for (int g = 0; g < this.getNumberOfGenes(); g++) {
      s = s + this.getGenesNames()[g] + "\t";
      for (int c = 0; c < this.getNumberOfConditions(); c++) {
        s = s + biclusterExpressionMatrix[g][c] + "\t";
      }
      s = s + "\n";
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
         "GENES= " + this.getNumberOfGenes() + "\n";
     return s;
   }


  //###############################################################################################################################
  //###############################################################################################################################

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
