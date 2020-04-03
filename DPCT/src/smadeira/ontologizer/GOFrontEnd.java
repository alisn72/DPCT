package smadeira.ontologizer;

import ontologizer.ByteString;
import ontologizer.PopulationSet;
import ontologizer.association.AssociationContainer;
import ontologizer.association.AssociationParser;
import ontologizer.association.Gene2Associations;
import ontologizer.calculation.AbstractGOTermProperties;
import ontologizer.calculation.CalculationRegistry;
import ontologizer.calculation.EnrichedGOTermsResult;
import ontologizer.calculation.ICalculation;
import ontologizer.go.*;
import ontologizer.statistics.AbstractTestCorrection;
import ontologizer.statistics.IResampling;
import ontologizer.statistics.TestCorrectionRegistry;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * This class is a simple front end to the GO ontology.
 * Uses the ontologizer package and this code is based on its
 * OntologizerCore class.
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
 * @author Andr� L. Martins
 * @author Joana P. Gon�alves
 */
public class GOFrontEnd {

  /*
   * OntologizerCore.Arguments arguments = new OntologizerCore.Arguments();
    arguments.goTermsOBOFile = pathTo_GeneOntologyFile;
    arguments.associationFile = pathTo_GeneAssociationFile;
    arguments.populationFile = new String("POPULATION.txt");
    arguments.studySet = pathTo_DirectoryWithBiclusters;
    arguments.calculationName = new String("Term-For-Term");
    arguments.correctionName = new String("Bonferroni");
    arguments.filterOutUnannotatedGenes = false;
   */

  private ICalculation _calculation = null;
  private AbstractTestCorrection _correction = null;
  //private Bonferroni _correction_bonferroni = null;
  private PopulationSet _population;
  private TermContainer _goTerms;
  private GOGraph _goGraph;
  private AssociationContainer _goAssociations;
  private boolean _filterByCorrectedPValue = false;
  private boolean _filterByTermNamespace = false;
  private Namespace _namespace;

  private Hashtable<Term, GOTermInfo> _depthTable = null;

  /**
   *
   * @return GOGraph
   */
  public GOGraph getGoGraph() {
    return this._goGraph;
  }

  /**
   *
   * @return GOTermContainer
   */
  public TermContainer getGoTerms() {
    return this._goTerms;
  }

  /**
   *
   * @return AssociationContainer
   */
  public AssociationContainer getGoAssociations() {
    return this._goAssociations;
  }

  /**
   *
   * @param filename String
   */
  private void parseOBOFile(String filename) throws IOException, OBOParserException {
    OBOParser oboParser = new OBOParser(filename);
    oboParser.doParse();
    _goTerms = new TermContainer(oboParser.getTermMap(),
                                 oboParser.getFormatVersion(),
                                 oboParser.getDate());
    _goGraph = new GOGraph(_goTerms);
  }

  /**
   *
   * @param population String[]
   * @param OBOfile String
   * @param geneAssociationFile String
   */
  public GOFrontEnd(String[] population, String OBOfile,
                    String geneAssociationFile) throws IOException, OBOParserException {
    _population = new StringPopulationSet(population, "GoFrontEnd:Population");
    parseOBOFile(OBOfile);

    _calculation = CalculationRegistry.getDefault();
    _correction = TestCorrectionRegistry.getCorrectionByName("Bonferroni");

    AssociationParser ap = new AssociationParser(geneAssociationFile, _goTerms,
                                                 _population.getAllGeneNames());
    _goAssociations = new AssociationContainer(ap.getAssociations(),
                                               ap.getSynonym2gene(),
                                               ap.getDbObject2gene());
  }

  /**
   *
   * @param name String
   */
  public void setCalculationMethod(String name) {
    ICalculation calculation = CalculationRegistry.getCalculationByName(name);
    if (calculation == null) {
      throw new IllegalArgumentException("Unknown calculation name <" + name +
                                         ">.");
    }
    this._calculation = calculation;
  }

  /**
   *
   * @param name String
   */
  public void setCorrectionMethod(String name) {
    AbstractTestCorrection testCorrection = TestCorrectionRegistry.
        getCorrectionByName(name);
    if (testCorrection == null) {
      throw new IllegalArgumentException("Unknown test correction name <" +
                                         name + ">.");
    }

    /*Empty cache for resampling based MTCs and set number of sampling steps */
    if (testCorrection instanceof IResampling) {
      throw new IllegalArgumentException(
          "For resampling methods, the number of steps must also be supplied.");
    }

    _correction = testCorrection;
  }

  /**
   *
   * @param name String
   * @param steps int
   */
  public void setCorrectionMethod(String name, int steps) {
    AbstractTestCorrection testCorrection = TestCorrectionRegistry.
        getCorrectionByName(name);
    if (testCorrection == null) {
      throw new IllegalArgumentException("Unknown test correction name <" +
                                         name + ">.");
    }

    /*Empty cache for resampling based MTCs and set number of sampling steps */
    if (testCorrection instanceof IResampling) {
      IResampling resampling = (IResampling) testCorrection;
      resampling.resetCache();
      if (steps > 0) {
        resampling.setNumberOfResamplingSteps(steps);
      }
    }
    _correction = testCorrection;
  }

  /**
   *
   * @param geneset String[]
   * @param name String
   * @return StudySetResult
   */
  public EnrichedGOTermsResult calculateStudySet(String[] geneset, String name) {
    /* verify that all the genes are in the population set */
    for (String gene : geneset) {
      if (!_population.contains(new ByteString(gene))) {
        throw new IllegalArgumentException(
            "Genes passed in the 'geneset' argument must exist in the original population: " +
            gene);
      }
    }

    StringStudySet studySet = new StringStudySet(geneset, name);
    EnrichedGOTermsResult studySetResult = _calculation.calculateStudySet(_goGraph,
        _goAssociations, _population, studySet, _correction);

/*    NewStudySetResult studySetResult = new NewStudySetResult(_calculation.calculateStudySet(_goGraph,
        _goAssociations, _population, studySet, _correction), _population.getGeneCount());
 */
/*    NewStudySetResult studySetResult = _calculation.calculateStudySet(_goGraph,
        _goAssociations, _population, studySet, _correction);
 */

    return studySetResult;
  }
    public EnrichedGOTermsResult calculateStudySet(String[] geneset) {
    /* verify that all the genes are in the population set */
        for (String gene : geneset) {
            if (!_population.contains(new ByteString(gene))) {
                throw new IllegalArgumentException(
                        "Genes passed in the 'geneset' argument must exist in the original population: " +
                                gene);
            }
        }

        StringStudySet studySet = new StringStudySet(geneset);
        EnrichedGOTermsResult studySetResult = _calculation.calculateStudySet(_goGraph,
                _goAssociations, _population, studySet, _correction);

/*    NewStudySetResult studySetResult = new NewStudySetResult(_calculation.calculateStudySet(_goGraph,
        _goAssociations, _population, studySet, _correction), _population.getGeneCount());
 */
/*    NewStudySetResult studySetResult = _calculation.calculateStudySet(_goGraph,
        _goAssociations, _population, studySet, _correction);
 */

        return studySetResult;
    }

  /**
   *
   * @return boolean
   */
  public boolean getFilterByCorrectedPValue() {
    return _filterByCorrectedPValue;
  }

  /**
   *
   * @param value boolean
   */
  public void setFilterByCorrectedPValue(boolean value) {
    _filterByCorrectedPValue = value;
  }

  /**
   *
   * @return boolean
   */
  public boolean getFilterByTermNamespace() {
    return _filterByTermNamespace;
  }

  /**
   *
   * @param namespace Namespace
   */
  public void setFilterByTermNamespace(Namespace namespace) {
    if (namespace == null) {
      _filterByTermNamespace = false;
    }
    else {
      _namespace = namespace;
      _filterByTermNamespace = true;
    }
  }

  /**
   *
   * @param termProperties AbstractGOTermProperties
   * @param pValueThreshold double
   * @return boolean
   */
  private boolean filterAccepts(AbstractGOTermProperties termProperties,
                                double pValueThreshold) {
    return (termProperties.p < pValueThreshold &&
            (!_filterByCorrectedPValue ||
             termProperties.p_adjusted < 5 * pValueThreshold) &&
            (!_filterByTermNamespace ||
             termProperties.goTerm.getNamespace() == _namespace));
  }

  /**
   * Results are first filtered by the pValueThreshold and, if FilterByCorrectedPValue is set,
   * by 5 times the pValueThreshold applied to the corrected p-value.
   * @param geneset String[]
   * @param name String
   * @param pValueThreshold double
   * @return GeneSetResult
   */
  public GeneSetResult calculateGeneSet(String[] geneset, String name,
                                        double pValueThreshold) {
    return calculateGeneSet(geneset, name, pValueThreshold, false);
  }

  /**
   * Results are first filtered by the pValueThreshold and, if FilterByCorrectedPValue is set,
   * by 5 times the pValueThreshold applied to the corrected p-value.
   * @param geneset String[]
   * @param name String
   * @param pValueThreshold double
   * @param goTermAssocs boolean
   * @return GeneSetResult
   */
  public GeneSetResult calculateGeneSet(String[] geneset, String name,
                                        double pValueThreshold,
                                        boolean goTermAssocs) {
    EnrichedGOTermsResult setResult = calculateStudySet(geneset, name);
//    StudySetResult setResult =
    Iterator<AbstractGOTermProperties> iter = setResult.iterator();

    int size = 0;

    /* count */
    while (iter.hasNext()) {
      AbstractGOTermProperties prop = iter.next();
      if (filterAccepts(prop, pValueThreshold)) {
        ++size;
      }
    }
//    GeneSetResult geneResult = new GeneSetResult(size);
    GeneSetResult geneResult = new GeneSetResult(size,
                                                 setResult.getPopulationGeneCount(),
                                                 goTermAssocs);
    if (_depthTable != null) {
      geneResult.setHasDepthInfo(true);
    }

    /* reset */
    iter = setResult.iterator();

    int idx = 0;
    while (iter.hasNext()) {
      AbstractGOTermProperties propAbstract = iter.next();

      if (filterAccepts(propAbstract, pValueThreshold)) {
        geneResult.getITermID()[idx] = propAbstract.goTerm.getID().id;
        geneResult.getTermID()[idx] = propAbstract.goTerm.getIDAsString();
        geneResult.getTermName()[idx] = propAbstract.goTerm.getName();
        geneResult.getPValue()[idx] = propAbstract.p;
        geneResult.getCorrectedPValue()[idx] = propAbstract.p_adjusted;
        geneResult.getGenesInPopulationAnnotatedWithTerm()[idx] = propAbstract.
            annotatedPopulationGenes;
        geneResult.getGenesInBiclusterAnnotatedWithTerm()[idx] = propAbstract.
            annotatedStudyGenes;

        if (_depthTable != null) {
          GOTermInfo info = _depthTable.get(propAbstract.goTerm);
          if (info == null) {
            System.out.println("Ooops: " + propAbstract.goTerm.getIDAsString() +
                               "# of ancestors=" +
                               _goGraph.getTermsAncestors(propAbstract.goTerm.
                getID()).size());
          }
          else {
            geneResult.getMinDepth()[idx] = info.minDepth;
            geneResult.getMaxDepth()[idx] = info.maxDepth;
          }
        }

        if (goTermAssocs) {
          BitSet goTermMap = geneResult.getAnnotatedStudyGeneMap()[idx];
          for (int i = 0; i < geneset.length; ++i) {
            if (termQueryFull(propAbstract.goTerm.getID().id, geneset[i])) {
              goTermMap.set(i);
            }
          }
        }

        ++idx;
      }
    }
    return geneResult;
  }

  /**
   *
   * @param goTermID int
   * @param gene String
   * @return boolean
   */
  public boolean termQuery(int goTermID, String gene) {
    Gene2Associations assoc = _goAssociations.get(new ByteString(gene));
    if (assoc != null && assoc.getAssociations() != null) {
      for (TermID goTerm : assoc.getAssociations()) {
        if (goTerm.id == goTermID) {
          return true;
        }
      }
    }
    return false;
  }

// ########################################################################################################
// ########################################################################################################
//   EFFICIENT METHODS FOR OBTAINING THE SUBSET OF GENES OF A GIVEN
//   GENE SET ANNOTATED WITH A GIVEN TERM
// ########################################################################################################
// ########################################################################################################
  /**
   * Retrieves the genes annotated with the term with GO term ID
   * <code>goTermID</code>. Returns the genes in a <code>String</code>,
   * each line containing the name of an annotated gene. The number
   * of annotated genes is counted and returned in <code>numberOfAnnotatedGenes</code>.
   *
   * Joana P. Gon�alves
   *
   * @param goTermID <code>int</code> the GO term to search annotated genes for
   * @param genes <code>String[]</code> the names of the genes
   * @param numberOfAnnotatedGenes <code>int[]</code> the number of annotated genes
   * @return String the names of the genes annotated with the GO term with
   * the given <code>goTermID</code>
   */
  public String queryGOTermIDForGenes(int goTermID, String[] genes,
                                      int[] numberOfAnnotatedGenes) {
    HashSet<TermID> terms = findAllTerms(goTermID);
    String annotatedGenes = "";

    for (String gene : genes) {
      Gene2Associations associations = _goAssociations.get(new ByteString(gene));
      if (associations != null) {
        Iterator<TermID> annotations = associations.getAssociations().iterator();
        boolean found = false;
        while (annotations.hasNext() && !found) {
          TermID term = annotations.next();
          if (terms.contains(term)) {
            annotatedGenes += "\n" + gene;
            numberOfAnnotatedGenes[0] = numberOfAnnotatedGenes[0]+1;
            found = true;
          }
        }
      }
    }
    return annotatedGenes;
  }

  /**
   * Retrieves and returns the list of genes annotated with the term with
   * the given <code>goTermID</code>.
   *
   * Joana P. Gon�alves
   *
   * @param goTermID <code>int</code> the GO term to search annotated genes for
   * @param genes <code>String[]</code> the names of the genes
   * @return ArrayList the list of genes annotated with the term with the
   * given <code>goTermID</code>
   */
  public ArrayList<String> queryGOTermIDForGenes(int goTermID, String[] genes) {
    HashSet<TermID> terms = findAllTerms(goTermID);
    ArrayList<String> annotatedGenes = new ArrayList<String>();

    for (String gene : genes) {
      Gene2Associations associations = _goAssociations.get(new ByteString(gene));
      if (associations != null) {
        Iterator<TermID> annotations = associations.getAssociations().iterator();
        boolean found = false;
        while (annotations.hasNext() && !found) {
          TermID term = annotations.next();
          if (terms.contains(term)) {
            annotatedGenes.add(gene);
            found = true;
          }
        }
      }
    }
    return annotatedGenes;
  }

  /**
   * Extracts and returns all the descendants of the given
   * <code>GOTermID</code> in the GO graph, including itself, in a
   * <code>HashSet</code>.
   *
   * Joana P. Gon�alves
   *
   * @param goTermID <code>int</code> the id of the GO term
   * @return HashSet contains all the descendants of the GO term
   * with <code>goTermID</code>, including itself
   */
  public HashSet<TermID> findAllTerms (int goTermID) {
    /* walk down the graph */
    HashSet<TermID> _seenTerm = new HashSet<TermID> ();
    _seenTerm.add(new TermID(goTermID));

    Stack<Iterator<TermID>> stack = new Stack<Iterator<TermID>> ();
    Set<TermID>
        descendants = _goGraph.getTermsDescendants(new TermID(goTermID));
    stack.add(descendants.iterator());

    while (!stack.empty()) {
      Iterator<TermID> iter = stack.peek();
      boolean foundNext = false;
      TermID next = null;
      while (iter.hasNext()) {
        next = iter.next();
        if (!_seenTerm.contains(next)) {
          foundNext = true;
          break;
        }
      }
      if (foundNext) {
        _seenTerm.add(next);
        descendants = _goGraph.getTermsDescendants(next);
        stack.add(descendants.iterator());
      }
      else {
        stack.pop();
      }
    }
    return _seenTerm;
  }

// ########################################################################################################
// ########################################################################################################


  /**
   *
   * @param goTermID int
   * @param gene String
   * @return boolean
   */
  public boolean termQueryFull(int goTermID, String gene) {
    if (termQuery(goTermID, gene)) {
      return true;
    }

    /* walk down the graph */
    HashSet<TermID> _seenTerm = new HashSet<TermID> ();

    Stack<Iterator<TermID>> stack = new Stack<Iterator<TermID>> ();
    Set<TermID>
        descendants = _goGraph.getTermsDescendants(new TermID(goTermID));
    stack.add(descendants.iterator());

    while (!stack.empty()) {
      Iterator<TermID> iter = stack.peek();
      boolean foundNext = false;
      TermID next = null;
      while (iter.hasNext()) {
        next = iter.next();
        if (!_seenTerm.contains(next)) {
          foundNext = true;
          break;
        }
      }
      if (foundNext) {
        if (termQuery(next.id, gene)) {
          return true;
        }
        _seenTerm.add(next);
        descendants = _goGraph.getTermsDescendants(next);
        stack.add(descendants.iterator());
      }
      else {
        stack.pop();
      }
    }

    return false;
  }

  /**
   * Computing GOTerm depth interval [min ... max]
   */
  public void buildDepthTable() {
    System.out.println("Building depth table ...");
    _depthTable = computeGOTermDepths(_goGraph.getRootGOTerm());
    System.out.println("done.");
  }

  public boolean hasDepthTable() {
    return (_depthTable != null);
  }

  private class GOTermInfo {
    public Term term;
    public int minDepth;
    public int maxDepth;

    public GOTermInfo(Term t) {
      this.term = t;
      this.minDepth = this.maxDepth = 0;
    }
  }

  /**
   *
   * @param root GOTerm
   * @return Hashtable
   */
  private Hashtable<Term, GOTermInfo> computeGOTermDepths(Term root) {
    Hashtable<Term, GOTermInfo> table = new Hashtable<Term, GOTermInfo> ();
    int currentDepth = 0;
    LinkedList<Term> queue = new LinkedList<Term> ();
    queue.add(null); // null is the marker that indicates that a level has been fully scanned

    // work around a bug in ontologizer
    // Cannot get descendents of the 'root' node
    // as it tries to apply an .equal between the termID and the string form of
    // the rootTermID in getTermsDescendants
    if (root == _goGraph.getRootGOTerm()) {
      Term term = _goTerms.get("GO:0008150"); /* biological process */
      GOTermInfo info = new GOTermInfo(term);
      queue.add(term);
      table.put(term, info);
      term = _goTerms.get("GO:0005575"); /* celluar component */
      info = new GOTermInfo(term);
      queue.add(term);
      table.put(term, info);
      term = _goTerms.get("GO:0003674"); /* molecular function */
      queue.add(term);
      info = new GOTermInfo(term);
      table.put(term, info);
    }
    else {
      queue.add(root);
      table.put(root, new GOTermInfo(root));
    }

    while (!queue.isEmpty()) {
      Term head = queue.poll();
      if (head == null) {
        if (!queue.isEmpty()) {
          queue.add(null);
          ++currentDepth;
        }
        System.out.println("Depth = " + currentDepth);
      }
      else {
        // walk descendents
        TermID termId = head.getID();
        if (_goTerms.get(termId) == null) {
          System.out.println("ooops");
        }
        Set<TermID> childs = _goGraph.getTermsDescendants(termId);
        for (TermID id : childs) {
          Term child = _goTerms.get(id);

          GOTermInfo info = table.get(child);
          if (info == null) {
            info = new GOTermInfo(child);
            info.minDepth = currentDepth;
            info.maxDepth = currentDepth;
            table.put(child, info);
          }
          else {
            if (info.minDepth > currentDepth) {
              info.minDepth = currentDepth;
            }
            if (info.maxDepth < currentDepth) {
              info.maxDepth = currentDepth;
            }
          }

          // add child to list
          queue.add(child);
        }
      }
    }

    return table;
  }

  /**
   * Naive implementation of LCA for DGAs
   *
   * @param masks Hashtable
   * @param createMasks boolean
   * @param maskIdx int
   * @param id GOTermID
   */
  private void updateMasks(Hashtable<TermID, BitSet> masks,
      boolean createMasks, int maskIdx, TermID id) {
    BitSet mask = masks.get(id);
    if (mask == null) {
      if (createMasks) {
        mask = new BitSet();
        mask.set(maskIdx);
        masks.put(id, mask);
      }
    }
    else {
      mask.set(maskIdx);
    }
  }

  /**
   *
   * @param id GOTermID
   * @param masks Hashtable
   * @param createMasks boolean
   * @param maskIdx int
   */
  private void walkToSources(TermID id, Hashtable<TermID, BitSet> masks,
      boolean createMasks, int maskIdx) {
    HashSet<TermID> visited = new HashSet<TermID> ();

    /* Add al terms to the queue */
    LinkedList<TermID> queue = new LinkedList<TermID> ();
    queue.offer(id);
    visited.add(id);
    updateMasks(masks, createMasks, maskIdx, id);

    while (!queue.isEmpty()) {
      /* Remove head of the queue */
      TermID head = queue.poll();

      Set<TermID> ancestors = _goGraph.getTermsAncestors(head);

      for (TermID parent : ancestors) {
        if (!visited.contains(parent)) {
          visited.add(parent);

          updateMasks(masks, createMasks, maskIdx, id);
          queue.offer(parent);
        }
      }
    }
  }

  /**
   *
   * @param termIDs int[]
   * @return int
   */
  public int depthOfLCA(int[] termIDs) {
    if (!hasDepthTable()) {
      throw new IllegalStateException(
          "Cannot compute function until Depth table is computed.");
    }

    Hashtable<TermID, BitSet> nodeMasks = new Hashtable<TermID, BitSet> ();

    // Tag nodes
    TermID termID = new TermID(termIDs[0]);
    walkToSources(termID, nodeMasks, true, 0);

    for (int i = 1; i < termIDs.length; ++i) {
      termID = new TermID(termIDs[i]);

      walkToSources(termID, nodeMasks, false, i);
      // createMasks = false: if a node does not exist within the ancestors
      //   of the first node in the set, then it cannot be a common ancestor.
    }

    // Choose a node from within the tagged nodes
    int lcaDepth = 0;
    for (Entry<TermID, BitSet> mask : nodeMasks.entrySet()) {
      BitSet bitMask = mask.getValue();

      if (bitMask.nextClearBit(0) == termIDs.length) { //all bits are true
        int maxDepth = _depthTable.get(_goTerms.get(mask.getKey())).maxDepth;
        lcaDepth = (lcaDepth < maxDepth ? maxDepth : lcaDepth);
      }
    }

    return lcaDepth;
  }

  /**
   * SARA
   *
   * @param geneset String[]
   //* @param name String
   * @param goTermAssocs boolean
   * @return GeneSetResult
   */
  public GeneSetResult calculateGeneSet(String[] geneset,
                                        boolean goTermAssocs) {
    EnrichedGOTermsResult setResult = calculateStudySet(geneset);
    Iterator<AbstractGOTermProperties> iter = setResult.iterator();

    int size = 0;

    /* count */
    while (iter.hasNext()) {
      AbstractGOTermProperties prop = iter.next();
       ++size;
    }

    GeneSetResult geneResult = new GeneSetResult(size,
                                                 setResult.getPopulationGeneCount(),
                                                 goTermAssocs);
    if (_depthTable != null) {
      geneResult.setHasDepthInfo(true);
    }

    /* reset */
    iter = setResult.iterator();

    int idx = 0;
    while (iter.hasNext()) {
      AbstractGOTermProperties propAbstract = iter.next();

      geneResult.getITermID()[idx] = propAbstract.goTerm.getID().id;
      geneResult.getTermID()[idx] = propAbstract.goTerm.getIDAsString();
      geneResult.getTermName()[idx] = propAbstract.goTerm.getName();
      geneResult.getPValue()[idx] = propAbstract.p;
      geneResult.getCorrectedPValue()[idx] = propAbstract.p_adjusted;
      geneResult.getGenesInPopulationAnnotatedWithTerm()[idx] = propAbstract.
          annotatedPopulationGenes;
      geneResult.getGenesInBiclusterAnnotatedWithTerm()[idx] = propAbstract.
          annotatedStudyGenes;

      if (_depthTable != null) {
        GOTermInfo info = _depthTable.get(propAbstract.goTerm);
        if (info == null) {
          System.out.println("Ooops: " + propAbstract.goTerm.getIDAsString() +
                             "# of ancestors=" +
                             _goGraph.getTermsAncestors(propAbstract.goTerm.
              getID()).size());
        }
        else {
          geneResult.getMinDepth()[idx] = info.minDepth;
          geneResult.getMaxDepth()[idx] = info.maxDepth;
        }
      }

      if (goTermAssocs) {
        BitSet goTermMap = geneResult.getAnnotatedStudyGeneMap()[idx];
        for (int i = 0; i < geneset.length; ++i) {
          if (termQueryFull(propAbstract.goTerm.getID().id, geneset[i])) {
            goTermMap.set(i);
          }
        }
      }

      ++idx;
    }
    return geneResult;
  }

}
