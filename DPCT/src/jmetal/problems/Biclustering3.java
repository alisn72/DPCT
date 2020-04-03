
package jmetal.problems;

import jmetal.core.Matrix;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.wrapper.XReal;
import math3.stat.correlation.PearsonsCorrelation;

import java.util.ArrayList;

/**
 * Class representing problem Biclustering3
 */
public class Biclustering3 extends Problem {




    public Biclustering3(String solutionType, String exp_Path,int numberOfRow_) {
        geneExpPath = exp_Path;
        numberOfObjectives_ = 1;
        numberOfConstraints_ = 0;
        problemName_ = "Biclustering3";
        geneExpressionData = new Matrix();
        geneExpressionData.read_Gene_Expression(geneExpPath);
        sizeOfGenePart = /*geneExpressionData.sizeOfProteins*/900;
        sizeOfConditionPart = geneExpressionData.sizeOfConditions;
        numberOfRow = numberOfRow_;
        numberOfVariables_ = numberOfRow*(sizeOfConditionPart+sizeOfGenePart);
        System.out.print("NSGA-based biclustering started!!\n");
        System.out.print("each bicluster have(in max) "+sizeOfGenePart+" Genes and ");
        System.out.print(+sizeOfConditionPart+" Conditions\n");
        System.out.print("number of bicluster in each chromosome: "+numberOfRow+"\n");
//        upperLimit_ = new double[numberOfVariables_];
//        lowerLimit_ = new double[numberOfVariables_];

        upperLimit_ = new double[numberOfVariables_];
        lowerLimit_ = new double[numberOfVariables_];
        for(int row=0;row<numberOfRow;row++) {
            int c = row*(sizeOfConditionPart+sizeOfGenePart);
            for (int var = 0; var < sizeOfGenePart; var++) {
                lowerLimit_[c+var] = 0;
                upperLimit_[c+var] = geneExpressionData.sizeOfProteins - 1;
            }
            for (int var = sizeOfGenePart; var < sizeOfGenePart + sizeOfConditionPart; var++) {
                lowerLimit_[c+var] = 0;
                upperLimit_[c+var] = geneExpressionData.sizeOfConditions - 1;
            }
        }
//        if (solutionType.compareTo("BinaryReal") == 0)
//            solutionType_ = new BinaryRealSolutionType(this);
//        else if (solutionType.compareTo("Real") == 0)
//            solutionType_ = new RealSolutionType(this);
//        else
        if (solutionType.compareTo("ArrayReal") == 0)
            solutionType_ = new ArrayRealSolutionType(this);
        else {
            System.out.println("Error: solution type " + solutionType + " invalid");
            System.exit(-1);
        }

    }


    /**
     * Evaluates a solution
     * @param solution The solution to evaluate
     * @throws JMException
     */
    public void evaluate(Solution solution) throws JMException {
        double [] f = new double[numberOfObjectives_] ;


        f[0] = MSR(solution)+VAR(solution);
        //f[1] = VAR(solution) ;
        //f[2] = AC(solution) ;

        solution.setObjective(0,f[0]);
        //solution.setObjective(1,f[1]);
        //solution.setObjective(2,f[2]);

    } //evaluate

    @Override
    public int GetNumberOfOnes(Solution solution) throws JMException {
        return 0;
    }

    public double MSR(Solution solution) throws JMException {
        XReal x = new XReal(solution) ;
        double ave_MSR = 0;
        for(int row=0;row<numberOfRow;row++){
            double mean=0,msr=0;
            ArrayList<Integer> validgIndex = new ArrayList<Integer>();
            ArrayList<Integer> validcIndex = new ArrayList<Integer>();

            int column = row*(sizeOfGenePart+sizeOfConditionPart);

            for(int i=column;i<column+sizeOfGenePart;i++) {
                int t = (int)x.getValue(i);
                if ( t != -1 && !validgIndex.contains(t)) validgIndex.add(t);
            }
            for(int i=column+sizeOfGenePart;i<column+sizeOfGenePart+sizeOfConditionPart;i++) {
                int t = (int) x.getValue(i);
                if ( t!= -1 && !validcIndex.contains(t)) validcIndex.add(t);
            }
            double[] gene_mean = new double[validgIndex.size()];
            double[] con_mean = new double[validcIndex.size()];
            for(int j=0;j<validgIndex.size();j++) {
                for (int k = 0; k < validcIndex.size(); k++) {
                    gene_mean[j] += geneExpressionData.mat[validgIndex.get(j)][validcIndex.get(k)];
                    mean += geneExpressionData.mat[validgIndex.get(j)][validcIndex.get(k)];
                }
                gene_mean[j] /= validcIndex.size();
            }

            for(int j=0;j<validcIndex.size();j++) {
                for (int k = 0; k < validgIndex.size(); k++) {
                    con_mean[j] += geneExpressionData.mat[validgIndex.get(k)][validcIndex.get(j)];
                }
                con_mean[j] /= validgIndex.size();
            }

            mean /= validcIndex.size()*validgIndex.size();

            for(int j=0;j<validgIndex.size();j++) {
                for (int k = 0; k < validcIndex.size(); k++) {
                    msr += Math.pow(geneExpressionData.mat[validgIndex.get(j)][validcIndex.get(k)]- gene_mean[j] - con_mean[k] + mean,2);
                }
            }
            msr /= validcIndex.size()*validgIndex.size();
            ave_MSR += msr;
        }
        return ave_MSR/numberOfRow;
    }

    public double VAR(Solution solution) throws JMException {
        XReal x = new XReal(solution) ;
        double ave_VAR = 0;
        for(int row=0;row<numberOfRow;row++) {
            double mean = 0;
            ArrayList<Integer> validgIndex = new ArrayList<Integer>();
            ArrayList<Integer> validcIndex = new ArrayList<Integer>();

            int column = row*(sizeOfGenePart+sizeOfConditionPart);

            for(int i=column;i<column+sizeOfGenePart;i++) {
                int t = (int)x.getValue(i);
                if ( t != -1 && !validgIndex.contains(t)) validgIndex.add(t);
            }
            for(int i=column+sizeOfGenePart;i<column+sizeOfGenePart+sizeOfConditionPart;i++) {
                int t = (int) x.getValue(i);
                if ( t!= -1 && !validcIndex.contains(t)) validcIndex.add(t);
            }

            for (int j = 0; j < validgIndex.size(); j++) {
                for (int k = 0; k < validcIndex.size(); k++) {
                    mean += geneExpressionData.mat[validgIndex.get(j)][validcIndex.get(k)];
                }
            }
            mean /= validcIndex.size() * validgIndex.size();
            double var = 0;
            for (int j = 0; j < validgIndex.size(); j++) {
                for (int k = 0; k < validcIndex.size(); k++) {
                    var += Math.pow(geneExpressionData.mat[validgIndex.get(j)][validcIndex.get(k)] - mean, 2);
                }
            }
            ave_VAR += var;
        }
        ave_VAR /= numberOfRow;
        return ave_VAR;
    }

    public double AC(Solution solution) throws JMException {
        XReal x = new XReal(solution) ;
        double ave_AC = 0, ACi = 0;double[][] PCC;
        ArrayList<Integer> validgIndex;
        ArrayList<Integer> validcIndex;
        for(int row=0;row<numberOfRow;row++) {
            validgIndex = new ArrayList<Integer>();
            validcIndex = new ArrayList<Integer>();
            int column = row * (sizeOfGenePart + sizeOfConditionPart);
            for (int i = column; i < column + sizeOfGenePart; i++) {
                int t = (int) x.getValue(i);
                if (t != -1 && !validgIndex.contains(t)) validgIndex.add(t);
            }
            for (int i = column + sizeOfGenePart; i < column + sizeOfGenePart + sizeOfConditionPart; i++) {
                int t = (int) x.getValue(i);
                if (t != -1 && !validcIndex.contains(t)) validcIndex.add(t);
            }
            double[] meanRows = new double[validgIndex.size()];
            for (int i = 0; i < validgIndex.size(); i++)
                for (int j = 0; j < validcIndex.size(); j++) {
                    meanRows[i] += geneExpressionData.mat[validgIndex.get(i)][validcIndex.get(j)];
                }
            PCC = new double[validgIndex.size()][validgIndex.size()];
            double [][] biclusterExpressionsTransposed = new double[validcIndex.size()][validgIndex.size()];
            for(int i=0;i<validgIndex.size();i++)
                for(int j=0;j<validcIndex.size();j++)
                    biclusterExpressionsTransposed[j][i] = geneExpressionData.mat[validgIndex.get(i)][validcIndex.get(j)];
            PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
            PCC = pearsonsCorrelation.computeCorrelationMatrix(biclusterExpressionsTransposed).getData();


            ACi = 0;
            for(int i=0;i<validgIndex.size()-1;i++) {
                for (int j = i + 1; j < validgIndex.size(); j++)
                    ACi += PCC[i][j];
            }
            ACi /= validgIndex.size()*(validgIndex.size()-1)*.5;
            ave_AC += ACi;
        }
        return ave_AC/numberOfRow;
    }

    public void evaluateConstraints(Solution solution) throws JMException {
        XReal x = new XReal(solution);
        for(int row=0;row<numberOfRow;row++) {
            ArrayList<Integer> validgIndex = new ArrayList<Integer>();
            ArrayList<Integer> validcIndex = new ArrayList<Integer>();

            int column = row * (sizeOfGenePart + sizeOfConditionPart);

            for (int i = column; i < column + sizeOfGenePart; i++) {
                int t = (int) x.getValue(i);
                if (t != -1 && !validgIndex.contains(t)) validgIndex.add(t);
            }
            for (int i = column + sizeOfGenePart; i < column + sizeOfGenePart + sizeOfConditionPart; i++) {
                int t = (int) x.getValue(i);
                if (t != -1 && !validcIndex.contains(t)) validcIndex.add(t);
            }
            if (validgIndex.size() < 100) {
                for (int i = column; i < column + sizeOfGenePart; i++) {
                    int t = (int) x.getValue(i);
                    if (t == -1) x.setValue(i, PseudoRandom.randInt(-1, geneExpressionData.sizeOfProteins - 1));
                }
            }
            if (validcIndex.size() < 2) {
                for (int i = column + sizeOfGenePart; i < column + sizeOfGenePart + sizeOfConditionPart; i++) {
                    int t = (int) x.getValue(i);
                    if (t == -1){
                        x.setValue(i, PseudoRandom.randInt(0, geneExpressionData.sizeOfConditions - 1));
                        break;
                    }
                }
            }
        }
    }

} // Biclustering3
