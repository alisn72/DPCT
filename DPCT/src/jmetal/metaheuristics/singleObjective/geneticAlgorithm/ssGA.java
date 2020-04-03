package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import jmetal.core.*;
import jmetal.encodings.variable.Binary;
import jmetal.operators.selection.WorstSolutionSelection;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.comparators.ObjectiveComparator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Class implementing a steady-state genetic algorithm
 */
public class ssGA extends Algorithm {

    /**
     * Constructor
     * Create a new SSGA instance.
     *
     * @param problem Problem to solve
     */
    public ssGA(Problem problem) {
        super(problem);
    } // SSGA

    /**
     * Execute the SSGA algorithm
     *
     * @throws JMException
     */
    public SolutionSet execute(int resultCount) throws JMException, ClassNotFoundException {
        int populationSize;
        int maxEvaluations;
        int evaluations;

        SolutionSet population;
        Operator mutationOperator;
        Operator crossoverOperator;
        Operator selectionOperator;

        Comparator comparator;

        comparator = new ObjectiveComparator(0, true); // Single objective comparator

        Operator findWorstSolution;
        HashMap parameters; // Operator parameters
        parameters = new HashMap();
        parameters.put("comparator", comparator);

        findWorstSolution = new WorstSolutionSelection(parameters);

        // Read the parameters
        populationSize = ((Integer) this.getInputParameter("populationSize")).intValue();
        maxEvaluations = ((Integer) this.getInputParameter("maxEvaluations")).intValue();

        // Initialize the variables
        population = new SolutionSet(populationSize * 500);
        evaluations = 0;

        // Read the operators
        mutationOperator = this.operators_.get("mutation");
        crossoverOperator = this.operators_.get("crossover");
        selectionOperator = this.operators_.get("selection");

        Solution newIndividual;
        for (int i = 0; i < populationSize; i++) {
            newIndividual = new Solution(problem_);
            problem_.evaluate(newIndividual);
            evaluations++;
            population.add(newIndividual);
        } //for

        while (evaluations < maxEvaluations) {
            Solution[] parents = new Solution[2];

            // Selection
            parents[0] = (Solution) selectionOperator.execute(population);
            parents[1] = (Solution) selectionOperator.execute(population);

            // Crossover
            Solution[] offspring = (Solution[]) crossoverOperator.execute(parents);

            // Mutation
            mutationOperator.execute(offspring[0]);
            mutationOperator.execute(offspring[1]);
            // Evaluation of the new individual
            problem_.evaluate(offspring[0]);
            problem_.evaluate(offspring[1]);

            evaluations++;

            int worstIndividual = (Integer) findWorstSolution.execute(population);

            population.remove(worstIndividual);
            population.add(offspring[0]);
            population.add(offspring[1]);

        } // while

        // Return a population with the best individual

        SolutionSet resultPopulation = new SolutionSet(resultCount);
        Binary converted;
        population.sort(comparator);
        try {
            double sum = 0.0;
            for (int i = 0; i < resultCount; i++) {
//                converted = (Binary) population.get(i).getDecisionVariables()[0].deepCopy();
                //System.out.print(i+" before correction: "+converted.getNumberOfTrueBits()+", fitness: "+population.get(i).getObjective(0));
                Solution temp = population.get(i);
//                converted = (Binary) temp.getDecisionVariables()[0].deepCopy();
                //System.out.print(i+" after correction: "+converted.getNumberOfTrueBits()+", fitness: "+population.get(i).getObjective(0) +"\n");


                String y = temp.getDecisionVariables()[0].toString();
                Binary binSolution = (Binary) temp.getDecisionVariables()[0].deepCopy();
                ArrayList<Integer> validgIndex = new ArrayList<Integer>();
                ArrayList<Integer> validcIndex = new ArrayList<Integer>();
                for (int k = 0; k < y.length() - 12; k++) {
                    if (binSolution.bits_.get(k)) validgIndex.add(k);
                }
                for (int k = y.length() - 12; k < y.length(); k++) {
                    if (binSolution.bits_.get(k)) validcIndex.add(k - y.length() - 12);
                }
                resultPopulation.add(temp);
            }
            sum /=resultCount;

        } catch (Exception e) {

        }
        return resultPopulation;
    } // execute

    private void doCorrection(Solution offSpring) throws JMException {
        Binary offSpring1 = (Binary) offSpring.getDecisionVariables()[0].deepCopy();
        Comparator comparator = new ObjectiveComparator(0, true); // Single objective comparator
        int sizeOfGenePart = offSpring.getProblem().sizeOfGenePart;
//        System.out.print(offSpring.getProblem().GetNumberOfOnes(offSpring));
        //offSpring[0] correction
        for (int i = 0; i < sizeOfGenePart; i++) {
            offSpring1.bits_.set(i, !offSpring1.bits_.get(i));
            Solution temp0 = new Solution(offSpring);
            temp0.getDecisionVariables()[0] = offSpring1;
            ((Binary) temp0.getDecisionVariables()[0]).decode();
            getProblem().evaluate(temp0);
            int nBase = offSpring.getProblem().GetNumberOfOnes(offSpring);
            int nNew = temp0.getProblem().GetNumberOfOnes(temp0);
            if (nBase > nNew)
                offSpring1.bits_.set(i, !offSpring1.bits_.get(i));
        }
        offSpring.getDecisionVariables()[0] = offSpring1;
        ((Binary) offSpring.getDecisionVariables()[0]).decode();
        getProblem().evaluate(offSpring);
//        System.out.print("  -- >  " + offSpring.getProblem().GetNumberOfOnes(offSpring) + "\n");
    }

} // ssGA
