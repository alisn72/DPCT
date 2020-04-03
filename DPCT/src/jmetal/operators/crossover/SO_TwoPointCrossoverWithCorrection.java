//  SinglePointCrossover.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.operators.crossover;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.comparators.ObjectiveComparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * This class allows to apply a Single Point crossover operator using two parent
 * solutions.
 */
public class SO_TwoPointCrossoverWithCorrection extends Crossover {
    /**
     * Valid solution types to apply this operator
     */
    private static final List VALID_TYPES = Arrays.asList(BinarySolutionType.class,
            BinaryRealSolutionType.class,
            IntSolutionType.class);

    private Double crossoverProbability_ = null;

    /**
     * Constructor
     * Creates a new instance of the single point crossover operator
     */
    public SO_TwoPointCrossoverWithCorrection(HashMap<String, Object> parameters) {
        super(parameters);
        if (parameters.get("probability") != null)
            crossoverProbability_ = (Double) parameters.get("probability");
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
     *
     * @param probability Crossover probability
     * @param parent1     The first parent
     * @param parent2     The second parent
     * @return An array containig the two offsprings
     * @throws JMException
     */
    public Solution[] doCrossover(double probability, Solution parent1, Solution parent2) throws JMException {
        Solution[] offSpring = new Solution[2];
        offSpring[0] = new Solution(parent1);
        offSpring[1] = new Solution(parent2);
        int min = 0, max = 0, min1 = 0, max1 = 0;
        try {
            if (PseudoRandom.randDouble() < probability) {
                int sizeOfGenePart = parent1.getProblem().sizeOfGenePart;
                int sizeOfConditionPart = parent1.getProblem().sizeOfConditionPart;

                Binary offSpring1, offSpring2;
                offSpring1 = (Binary) parent1.getDecisionVariables()[0].deepCopy();
                offSpring2 = (Binary) parent2.getDecisionVariables()[0].deepCopy();

                int gCrossoverPoint1 = PseudoRandom.randInt(0, sizeOfGenePart - 1);
                int gCrossoverPoint2 = PseudoRandom.randInt(0, sizeOfGenePart - 1);

                if (gCrossoverPoint1 < gCrossoverPoint2) {
                    min1 = gCrossoverPoint1;
                    max1 = gCrossoverPoint2;
                } else {
                    min1 = gCrossoverPoint2;
                    max1 = gCrossoverPoint1;
                }
                for (int i = min1; i <= max1; i++) {
                    boolean swap = offSpring1.bits_.get(i);
                    offSpring1.bits_.set(i, offSpring2.bits_.get(i));
                    offSpring2.bits_.set(i, swap);
                }
                int cCrossoverPoint1 = PseudoRandom.randInt(sizeOfGenePart, sizeOfGenePart + sizeOfConditionPart - 1);
                int cCrossoverPoint2 = PseudoRandom.randInt(sizeOfGenePart, sizeOfGenePart + sizeOfConditionPart - 1);
                if (cCrossoverPoint1 < cCrossoverPoint2) {
                    min = cCrossoverPoint1;
                    max = cCrossoverPoint2;
                } else {
                    min = cCrossoverPoint2;
                    max = cCrossoverPoint1;
                }
                for (int i = min; i <= max; i++) {
                    boolean swap = offSpring1.bits_.get(i);
                    offSpring1.bits_.set(i, offSpring2.bits_.get(i));
                    offSpring2.bits_.set(i, swap);
                }
                offSpring[0].getDecisionVariables()[0] = offSpring1;
                offSpring[1].getDecisionVariables()[0] = offSpring2;

                //7. Decode the results
                for (int i = 0; i < offSpring[0].getDecisionVariables().length; i++) {
                    ((Binary) offSpring[0].getDecisionVariables()[i]).decode();
                    ((Binary) offSpring[1].getDecisionVariables()[i]).decode();
                }

            }
        } catch (ClassCastException e1) {
            Configuration.logger_.severe("SinglePointCrossover.doCrossover: Cannot perfom " +
                    "SinglePointCrossover");
            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".doCrossover()");
        }
//        System.out.print("parent1 : " + parent1.getDecisionVariables()[0].toString()+"\n");
//        System.out.print("parent2 : "+parent2.getDecisionVariables()[0].toString()+"\n");
//        System.out.print("min, max :"+min1+" , "+max1+"\n");
//        System.out.print("offSpring1 : " + offSpring[0].getDecisionVariables()[0].toString()+"\n");
//        System.out.print("offSpring2 : " + offSpring[1].getDecisionVariables()[0].toString()+"\n");

        doCorrection(offSpring[0]);
        doCorrection(offSpring[1]);

        return offSpring;
    } // doCrossover

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
//            getProblem().evaluate(temp0);
            int nBase = offSpring.getProblem().GetNumberOfOnes(offSpring);
            int nNew = temp0.getProblem().GetNumberOfOnes(temp0);
            if (nBase > nNew)
                offSpring1.bits_.set(i, !offSpring1.bits_.get(i));
        }
        offSpring.getDecisionVariables()[0] = offSpring1;
        ((Binary) offSpring.getDecisionVariables()[0]).decode();
//        getProblem().evaluate(offSpring);
    }

    /**
     * Executes the operation
     *
     * @param object An object containing an array of two solutions
     * @return An object containing an array with the offSprings
     * @throws JMException
     */
    public Object execute(Object object) throws JMException {
        Solution[] parents = (Solution[]) object;

        if (!(VALID_TYPES.contains(parents[0].getType().getClass()) &&
                VALID_TYPES.contains(parents[1].getType().getClass()))) {

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
