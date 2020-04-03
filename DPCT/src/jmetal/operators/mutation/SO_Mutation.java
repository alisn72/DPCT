package jmetal.operators.mutation;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class implements a bit flip mutation operator.
 * NOTE: the operator is applied to binary or integer solutions, considering the
 * whole solution as a single encodings.variable.
 */
public class SO_Mutation extends Mutation {
    /**
     * Valid solution types to apply this operator
     */
    private static final List VALID_TYPES = Arrays.asList(BinarySolutionType.class,
            BinaryRealSolutionType.class,
            IntSolutionType.class);

    private Double mutationProbability_ = null;

    /**
     * Constructor
     * Creates a new instance of the Bit Flip mutation operator
     */
    public SO_Mutation(HashMap<String, Object> parameters) {
        super(parameters);
        if (parameters.get("probability") != null)
            mutationProbability_ = (Double) parameters.get("probability");
    } // BitFlipMutation

    /**
     * Perform the mutation operation
     *
     * @param probability Mutation probability
     * @param solution    The solution to mutate
     * @throws JMException
     */
    public void doMutation(double probability, Solution solution) throws JMException {
        try {
            //Gene part
            int length = ((Binary) solution.getDecisionVariables()[0]).getNumberOfBits();
            for (int j = 0; j < length - 12; j++) {
                if (PseudoRandom.randDouble() < probability) {
                    ((Binary) solution.getDecisionVariables()[0]).bits_.flip(j);
                }
            }
            //Condition Part
                if (PseudoRandom.randDouble() < 0.1) {
            for (int j = length - 12; j < length; j++) {
                    int c = 0;
                    for (int i = length - 12; i < length; i++) {
                        if (((Binary) solution.getDecisionVariables()[0]).bits_.get(i) ) {
                            c++;
                        }
                    }
                    if (c > 1)
                        ((Binary) solution.getDecisionVariables()[0]).bits_.flip(j);
                }
            }

            for (int i = 0; i < solution.getDecisionVariables().length; i++) {
                ((Binary) solution.getDecisionVariables()[i]).decode();
            }
        } catch (ClassCastException e1) {
            Configuration.logger_.severe("BitFlipMutation.doMutation: " +
                    "ClassCastException error" + e1.getMessage());
            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".doMutation()");
        }
    } // doMutation

    /**
     * Executes the operation
     *
     * @param object An object containing a solution to mutate
     * @return An object containing the mutated solution
     * @throws JMException
     */
    public Object execute(Object object) throws JMException {
        Solution solution = (Solution) object;

        if (!VALID_TYPES.contains(solution.getType().getClass())) {
            Configuration.logger_.severe("BitFlipMutation.execute: the solution " +
                    "is not of the right type. The type should be 'Binary', " +
                    "'BinaryReal' or 'Int', but " + solution.getType() + " is obtained");

            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".execute()");
        } // if

        doMutation(mutationProbability_, solution);
        return solution;
    } // execute
} // BitFlipMutation
