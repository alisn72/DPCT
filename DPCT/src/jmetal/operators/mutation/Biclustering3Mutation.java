
package jmetal.operators.mutation;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.wrapper.XReal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class implements a uniform mutation operator.
 */
public class Biclustering3Mutation extends Mutation{
    /**
     * Valid solution types to apply this operator
     */
    private static final List VALID_TYPES = Arrays.asList(RealSolutionType.class,
            ArrayRealSolutionType.class) ;
    /**
     * Stores the value used in a uniform mutation operator
     */
    private Double perturbation_;

    private Double mutationProbability_ = null;

    /**
     * Constructor
     * Creates a new uniform mutation operator instance
     */
    public Biclustering3Mutation(HashMap<String, Object> parameters) {
        super(parameters) ;

        if (parameters.get("probability") != null)
            mutationProbability_ = (Double) parameters.get("probability") ;
        if (parameters.get("perturbation") != null)
            perturbation_ = (Double) parameters.get("perturbation") ;

    } // UniformMutation


    /**
     * Constructor
     * Creates a new uniform mutation operator instance
     */
    //public UniformMutation(Properties properties) {
    //  this();
    //} // UniformMutation


    /**
     * Performs the operation
     * @param probability Mutation probability
     * @param solution The solution to mutate
     * @throws JMException
     */
    public void doMutation(double probability, Solution solution) throws JMException {
        XReal x = new XReal(solution) ;
        int numberOfRows = solution.getProblem().numberOfRow;
        int sizeOfGenePart = solution.getProblem().sizeOfGenePart;
        int sizeOfConditionPart = solution.getProblem().sizeOfConditionPart;
        for(int row=0;row<numberOfRows;row++){
            int column = row * (sizeOfGenePart + sizeOfConditionPart);
            for(int i=column;i<column+sizeOfGenePart;i++) {
                if (PseudoRandom.randDouble() < probability) {
                    int rand = PseudoRandom.randInt(0, sizeOfGenePart - 1);
                    x.setValue(i, rand);
                } // if
            }
            for(int i=column+sizeOfGenePart;i<column+sizeOfGenePart+sizeOfConditionPart;i++) {
                if (PseudoRandom.randDouble() < probability) {
                    int rand = PseudoRandom.randInt(0, sizeOfConditionPart - 1);
                    x.setValue(i, rand);
                } // if
            }
        }
    } // doMutation

    /**
     * Executes the operation
     * @param object An object containing the solution to mutate
     * @throws JMException
     */
    public Object execute(Object object) throws JMException {
        Solution solution = (Solution )object;

        if (!VALID_TYPES.contains(solution.getType().getClass())) {
            Configuration.logger_.severe("UniformMutation.execute: the solution " +
                    "is not of the right type. The type should be 'Real', but " +
                    solution.getType() + " is obtained");

            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".execute()") ;
        } // if

        doMutation(mutationProbability_,solution);

        return solution;
    } // execute
} // UniformMutation
