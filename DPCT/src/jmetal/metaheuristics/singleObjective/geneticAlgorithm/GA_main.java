package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.singleObjective.Biclustering4SingleObjective;
import jmetal.problems.singleObjective.OneMax;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class runs a single-objective genetic algorithm (GA). The GA can be 
 * a steady-state GA (class ssGA), a generational GA (class gGA), a synchronous
 * cGA (class scGA) or an asynchronous cGA (class acGA). The OneMax
 * problem is used to test the algorithms.
 */
public class GA_main {

  public  void main(double epsilon, int zeta) throws JMException, ClassNotFoundException, IOException {
    Problem   problem   ;         // The problem to solve
    Algorithm algorithm ;         // The algorithm to use
    Operator  crossover ;         // Crossover operator
    Operator  mutation  ;         // Mutation operator
    Operator  selection ;         // Selection operator
            
    //int bits ; // Length of bit string in the OneMax problem
    HashMap  parameters ; // Operator parameters

    problem = new Biclustering4SingleObjective("Binary","DIP_expression_data.txt", epsilon);

    algorithm = new ssGA(problem); // Steady-state GA

    /* Algorithm parameters*/
    algorithm.setInputParameter("populationSize",400);
    algorithm.setInputParameter("maxEvaluations", 10000);

    // Mutation and Crossover for Binary codification 
    parameters = new HashMap() ;
    parameters.put("probability", 0.9) ;
    crossover = CrossoverFactory.getCrossoverOperator("SO_TwoPointCrossover", parameters);

    parameters = new HashMap() ;
    parameters.put("probability", 0.001 ) ;
    mutation = MutationFactory.getMutationOperator("SO_Mutation", parameters);

    /* Selection Operator */
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;
    
    /* Add the operators to the algorithm*/
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);
 
    /* Execute the Algorithm */
    SolutionSet population = algorithm.execute(zeta);

    /* Log messages */
    population.printObjectivesToFile("FUN");
    population.printVariablesToFile("VAR");
  } //main
} // GA_main
