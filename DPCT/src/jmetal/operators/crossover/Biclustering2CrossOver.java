package jmetal.operators.crossover;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.*;
import jmetal.encodings.variable.Binary;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class allows to apply a Single Point crossover operator using two parent
 * solutions.
 */
public class Biclustering2CrossOver extends Crossover {
    /**
     * Valid solution types to apply this operator
     */
    private static final List VALID_TYPES = Arrays.asList(RealSolutionType.class,
            ArrayRealSolutionType.class) ;

    private Double crossoverProbability_ = null;

    /**
     * Constructor
     * Creates a new instance of the single point crossover operator
     */
    public Biclustering2CrossOver(HashMap<String, Object> parameters) {
        super(parameters) ;
        if (parameters.get("probability") != null)
            crossoverProbability_ = (Double) parameters.get("probability") ;
    } // SinglePointCrossover


    /**
     * Constructor
     * Creates a new instance of the single point crossover operator
     */
    //public SinglePointCrossover(Properties properties) {
    //    this();
    //} // SinglePointCrossover

    /**
     * Perform the crossover operation.
     * @param probability Crossover probability
     * @param parent1 The first parent
     * @param parent2 The second parent
     * @return An array containig the two offsprings
     * @throws JMException
     */
    public Solution[] doCrossover(double probability,
                                  Solution parent1,
                                  Solution parent2) throws JMException {
        Solution[] offSpring = new Solution[2];
        offSpring[0] = new Solution(parent1);
        offSpring[1] = new Solution(parent2);
        try {
            if (PseudoRandom.randDouble() < probability) {

                int gCrossoverPoint = PseudoRandom.randInt(0, parent1.numberOfGeneVariables()-1);
                int valueX1;
                int valueX2;
                for (int i = gCrossoverPoint; i < parent1.numberOfGeneVariables(); i++) {
                    valueX1 = (int) parent1.getDecisionVariables()[i].getValue();
                    valueX2 = (int) parent2.getDecisionVariables()[i].getValue();
                    offSpring[0].getDecisionVariables()[i].setValue(valueX2);
                    offSpring[1].getDecisionVariables()[i].setValue(valueX1);
                } // for
                int cCrossoverPoint = PseudoRandom.randInt(parent1.numberOfGeneVariables(), parent1.numberOfVariables()-1);
                for (int i = cCrossoverPoint; i < parent1.numberOfVariables(); i++) {
                    valueX1 = (int) parent1.getDecisionVariables()[i].getValue();
                    valueX2 = (int) parent2.getDecisionVariables()[i].getValue();
                    offSpring[0].getDecisionVariables()[i].setValue(valueX2);
                    offSpring[1].getDecisionVariables()[i].setValue(valueX1);
                } // for

            }
        } catch (ClassCastException e1) {
            Configuration.logger_.severe("SinglePointCrossover.doCrossover: Cannot perfom " +
                    "SinglePointCrossover");
            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".doCrossover()");
        }
        return offSpring;
    } // doCrossover

    /**
     * Executes the operation
     * @param object An object containing an array of two solutions
     * @return An object containing an array with the offSprings
     * @throws JMException
     */
    public Object execute(Object object) throws JMException {
        Solution[] parents = (Solution[]) object;

        if (!(VALID_TYPES.contains(parents[0].getType().getClass())  &&
                VALID_TYPES.contains(parents[1].getType().getClass())) ) {

            Configuration.logger_.severe("SinglePointCrossover.execute: the solutions " +
                    "are not of the right type. The type should be 'Binary' or 'Int', but " +
                    parents[0].getType() + " and " +
                    parents[1].getType() + " are obtained");

            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".execute()");
        } // if

        if (parents.length < 2) {
            Configuration.logger_.severe("SinglePointCrossover.execute: operator " +
                    "needs two parents");
            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".execute()");
        }

        Solution[] offSpring;
        offSpring = doCrossover(crossoverProbability_,
                parents[0],
                parents[1]);

        //-> Update the offSpring solutions
        for (int i = 0; i < offSpring.length; i++) {
            offSpring[i].setCrowdingDistance(0.0);
            offSpring[i].setRank(0);
        }
        return offSpring;
    } // execute
} // SinglePointCrossover
