//  ZDT3.java
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

package jmetal.problems.singleObjective;

import jmetal.core.Matrix;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.wrapper.XReal;
import math3.stat.correlation.PearsonsCorrelation;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Class representing problem Biclustering3
 */
public class Biclustering4SingleObjective extends Problem {

    public ArrayList<ArrayList<String>> bench;


    public Biclustering4SingleObjective(String solutionType, String exp_Path, double epsilon) throws IOException {
        geneExpPath = exp_Path;
        numberOfObjectives_ = 1;
        numberOfConstraints_ = 0;
        problemName_ = "Biclustering4SingleObjective";
        geneExpressionData = new Matrix();
        //geneExpressionData.read_Gene_Expression(geneExpPath);
        geneExpressionData.Discretize(geneExpPath, epsilon);
        geneExpressionData.read_Gene_Expression_Descretized(geneExpPath + "_d.txt");
        sizeOfGenePart = geneExpressionData.sizeOfProteins;
        sizeOfConditionPart = geneExpressionData.sizeOfConditions;
        numberOfVariables_ = 1;
//        System.out.print("Biclustering4SingleObjective  started!!\n");
//        System.out.print("each bicluster have(in max) " + sizeOfGenePart + " Genes and ");
//        System.out.print(+sizeOfConditionPart + " Conditions\n");
        length_ = new int[numberOfVariables_];
        length_[0] = sizeOfGenePart + sizeOfConditionPart;
        solutionType_ = new BinarySolutionType(this);
        //for cpx based evaluations
//        bench = new ArrayList<ArrayList<String>>();
//        String PPI_path = "DIP_static_ppi.txt";
//        String GED_Path1 = "DIP_expression_data.txt";
//
//        Matrix static_PPI = new Matrix();
//        static_PPI.read_PPI_Filter_By_GED(PPI_path, geneExpressionData.lookuptb);
//        Generate_clear_CYC2008(bench,static_PPI.lookuptb);


        //end cpx based evaluations
        if (solutionType.compareTo("Binary") == 0)
            solutionType_ = new BinarySolutionType(this);
        else {
            System.out.println("OneMax: solution type " + solutionType + " invalid");
            System.exit(-1);
        }
    }


    /**
     * Evaluates a solution
     *
     * @param solution The solution to evaluate
     * @throws JMException
     */
    public void evaluate(Solution solution) throws JMException {
        double[] f = new double[numberOfObjectives_];

        //f[0] = 1;
        //f[0] = MSR_binary(solution)+1;
        //f[0] = VAR_binary(solution)+CompPenalty(solution);
        //f[0] = AC_binary(solution)+CompPenalty(solution) ;
        //f[0] = CpxBasedEvaluation(solution);
        f[0] = DiscretizedMeasure(solution);
        solution.setObjective(0, f[0]);
        //System.out.print(f[0]+"\n");
        //solution.setObjective(1,f[1]);
        //solution.setObjective(2,f[2]);

    } //evaluate


    public double MSR_binary(Solution solution) throws JMException {
        Binary binSolution = (Binary) solution.getDecisionVariables()[0].deepCopy();
        double mean = 0, msr = 0;
        ArrayList<Integer> validgIndex = new ArrayList<Integer>();
        ArrayList<Integer> validcIndex = new ArrayList<Integer>();

        for (int i = 0; i < sizeOfGenePart; i++) {
            if (binSolution.bits_.get(i)) validgIndex.add(i);
        }
        for (int i = sizeOfGenePart; i < sizeOfGenePart + sizeOfConditionPart; i++) {
            if (binSolution.bits_.get(i)) validcIndex.add(i - sizeOfGenePart);
        }
        double[] gene_mean = new double[validgIndex.size()];
        double[] con_mean = new double[validcIndex.size()];
        for (int j = 0; j < validgIndex.size(); j++) {
            for (int k = 0; k < validcIndex.size(); k++) {
                gene_mean[j] += geneExpressionData.mat[validgIndex.get(j)][validcIndex.get(k)];
                mean += geneExpressionData.mat[validgIndex.get(j)][validcIndex.get(k)];
            }
            gene_mean[j] /= validcIndex.size();
        }

        for (int j = 0; j < validcIndex.size(); j++) {
            for (int k = 0; k < validgIndex.size(); k++) {
                con_mean[j] += geneExpressionData.mat[validgIndex.get(k)][validcIndex.get(j)];
            }
            con_mean[j] /= validgIndex.size();
        }

        mean /= validcIndex.size() * validgIndex.size();

        for (int j = 0; j < validgIndex.size(); j++) {
            for (int k = 0; k < validcIndex.size(); k++) {
                msr += Math.pow(geneExpressionData.mat[validgIndex.get(j)][validcIndex.get(k)] - gene_mean[j] - con_mean[k] + mean, 2);
            }
        }
        msr /= validcIndex.size() * validgIndex.size();
        return msr;
    }

    public double VAR_binary(Solution solution) throws JMException {
        Binary binSolution = (Binary) solution.getDecisionVariables()[0].deepCopy();
        double mean = 0;
        ArrayList<Integer> validgIndex = new ArrayList<Integer>();
        ArrayList<Integer> validcIndex = new ArrayList<Integer>();

        for (int i = 0; i < sizeOfGenePart; i++) {
            if (binSolution.bits_.get(i)) validgIndex.add(i);
        }
        for (int i = sizeOfGenePart; i < sizeOfGenePart + sizeOfConditionPart; i++) {
            if (binSolution.bits_.get(i)) validcIndex.add(i - sizeOfGenePart);
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
        return var;
    }

    public double AC_binary(Solution solution) throws JMException {
        Binary binSolution = (Binary) solution.getDecisionVariables()[0].deepCopy();
        double ACi = 0;

        ArrayList<Integer> validgIndex = new ArrayList<Integer>();
        ArrayList<Integer> validcIndex = new ArrayList<Integer>();

        for (int i = 0; i < sizeOfGenePart; i++) {
            if (binSolution.bits_.get(i)) validgIndex.add(i);
        }
        for (int i = sizeOfGenePart; i < sizeOfGenePart + sizeOfConditionPart; i++) {
            if (binSolution.bits_.get(i)) validcIndex.add(i - sizeOfGenePart);
        }
        double[][] biclusterExpressionsTransposed = new double[validcIndex.size()][validgIndex.size()];
        for (int i = 0; i < validgIndex.size(); i++)
            for (int j = 0; j < validcIndex.size(); j++)
                biclusterExpressionsTransposed[j][i] = geneExpressionData.mat[validgIndex.get(i)][validcIndex.get(j)];
        double[][] PCC = new double[validgIndex.size()][validgIndex.size()];
        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        PCC = pearsonsCorrelation.computeCorrelationMatrix(biclusterExpressionsTransposed).getData();
        for (int i = 0; i < validgIndex.size() - 1; i++) {
            for (int j = i + 1; j < validgIndex.size(); j++)
                ACi += PCC[i][j];
        }
        ACi /= validgIndex.size() * (validgIndex.size() - 1) * .5;
        return ACi;
    }

    public int GetNumberOfOnes(Solution solution) throws JMException {
        Binary binSolution = (Binary) solution.getDecisionVariables()[0].deepCopy();
        ArrayList<Integer> validgIndex = new ArrayList<Integer>();
        ArrayList<Integer> validcIndex = new ArrayList<Integer>();
        for (int i = 0; i < sizeOfGenePart; i++)
            if (binSolution.bits_.get(i)) validgIndex.add(i);
        for (int i = sizeOfGenePart; i < sizeOfGenePart + sizeOfConditionPart; i++)
            if (binSolution.bits_.get(i)) validcIndex.add(i - sizeOfGenePart);
        int count = 0;
        for (int j : validcIndex)
            for (int i : validgIndex)
                if (geneExpressionData.dmat[i][j] == '1')
                    count++;
        return count;
    }

    public double DiscretizedMeasure(Solution solution) throws JMException {
        Binary binSolution = (Binary) solution.getDecisionVariables()[0].deepCopy();
        ArrayList<Integer> validgIndex = new ArrayList<Integer>();
        ArrayList<Integer> validcIndex = new ArrayList<Integer>();
        for (int i = 0; i < sizeOfGenePart; i++)
            if (binSolution.bits_.get(i)) validgIndex.add(i);
        for (int i = sizeOfGenePart; i < sizeOfGenePart + sizeOfConditionPart; i++)
            if (binSolution.bits_.get(i)) validcIndex.add(i - sizeOfGenePart);

        int count = 0;
        double score = 0.0;

        //calculating column
//        double[] colScores = new double[sizeOfConditionPart];
//        for (int j : validcIndex) {
//            count = 0;
//            for (int i : validgIndex) {
//                if (geneExpressionData.dmat[i][j] == '1')
//                    count++;
//            }
//            score = (double) count / validgIndex.size();
//            if (count < (validgIndex.size() / 2))
//                score *= -1;
////            score = 1.0 - (2.0 * x) / validgIndex.size();
//            colScores[j] = score;
//        }

        //calculating row
        double[] rowScores = new double[sizeOfGenePart];
        for (int j : validgIndex) {
//            count = 0;
            for (int i : validcIndex)
                if (geneExpressionData.dmat[j][i] == '1')
                    count++;

//            score = (double) count / validcIndex.size();
//            if (score < 0.5)
//                score -= 1;
//
//            rowScores[j] = score;
        }

//        for (int i = 0; i < sizeOfGenePart - 1; i++)
//            rowScores[i + 1] += rowScores[i];

//        double rowSumScore = (double) rowScores[sizeOfGenePart - 1];
//        double res1 = (double) (rowScores[sizeOfGenePart - 1] + (1 / Math.abs(6 - validcIndex.size())));
//        double res = (double) (rowScores[sizeOfGenePart - 1] / validgIndex.size());
        double resT = (double) validgIndex.size()*validcIndex.size();
        double res = (double) (count / resT);
        return res;
    }

    private double CompPenalty(Solution solution) throws JMException {
        Binary binSolution = (Binary) solution.getDecisionVariables()[0].deepCopy();
        ArrayList<Integer> validgIndex = new ArrayList<Integer>();
        ArrayList<Integer> validcIndex = new ArrayList<Integer>();

        for (int i = 0; i < sizeOfGenePart; i++) {
            if (binSolution.bits_.get(i)) validgIndex.add(i);
        }
        for (int i = sizeOfGenePart; i < sizeOfGenePart + sizeOfConditionPart; i++) {
            if (binSolution.bits_.get(i)) validcIndex.add(i - sizeOfGenePart);
        }
        double penalty = 0, pf1 = 0, pf2 = 0, pf3 = 0;
//        pf1 = 10*(500-validgIndex.size());
//        pf2 = 10*(validgIndex.size()-1000);
        if (validcIndex.size() == 1)
            pf3 = 1000;
        penalty = pf1 + pf2 + pf3;
        return penalty;
    }

    private double CpxBasedEvaluation(Solution solution) {
        Matrix subNet = null;
        ArrayList<String> seeds = new ArrayList<String>();
        ArrayList<ArrayList<String>> cores = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> initCpx = new ArrayList<ArrayList<String>>();
        subNet = new Matrix();
        subNet.ReadFromBiclusterSolution(solution, geneExpressionData);
        seeds = subNet.SeedGeneration(.6);
        cores = subNet.CoreFinding(seeds);
        initCpx = subNet.CoreGrowing(cores, 0.2);
        initCpx = Filtering(initCpx, 3);
        double eval = Evaluate(initCpx, bench, 0.25);
        return eval;
    }


    public void evaluateConstraints(Solution solution) throws JMException {
        XReal x = new XReal(solution);
        for (int row = 0; row < numberOfRow; row++) {
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
                    if (t == -1) {
                        x.setValue(i, PseudoRandom.randInt(0, geneExpressionData.sizeOfConditions - 1));
                        break;
                    }
                }
            }
        }
    }

    public static void Generate_clear_CYC2008(ArrayList<ArrayList<String>> bench, Hashtable<String, Integer> lookuptb) {
        ArrayList<ArrayList<String>> bench2 = new ArrayList<ArrayList<String>>();
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream("CYC2008.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        StringTokenizer st;
        ArrayList<String> cpx;
        String p;
        try {
            br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while ((line = br.readLine()) != null) {
                cpx = new ArrayList<String>();
                while (!line.isEmpty()) {
                    st = new StringTokenizer(line);
                    p = st.nextToken();
                    if (lookuptb.containsKey(p))
                        cpx.add(p);
                    line = br.readLine();
                }
                bench2.add(cpx);
                //System.out.print(line+"\n");
            }
            for (int i = 0; i < bench2.size(); i++)
                if (bench2.get(i).size() > 2)
                    bench.add(bench2.get(i));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<ArrayList<String>> Filtering(ArrayList<ArrayList<String>> cores, int size) {
        ArrayList<ArrayList<String>> Filtered = new ArrayList<ArrayList<String>>();
        if (cores.size() > 0) {
            //Filtered.add(cores.get(0));
            for (int i = 0; i < cores.size(); i++) {
                int j = 0;
                for (; j < Filtered.size(); j++) {
                    if (Filtered.get(j).containsAll(cores.get(i)))
                        break;
                }
                if (j == Filtered.size() && cores.get(i).size() >= size)
                    Filtered.add(cores.get(i));
            }
        }
        return (ArrayList<ArrayList<String>>) Filtered.clone();
    }

    public double Evaluate(ArrayList<ArrayList<String>> pred, ArrayList<ArrayList<String>> bench, double os) {
        Double precision = new Double(0);
        Double recall = new Double(0);
        Double fmeasure = new Double(0);
        //double os = 0.2;
        int TPb = 0, TPp = 0;
        for (int i = 0; i < bench.size(); i++) {
            for (int j = 0; j < pred.size(); j++)
                if (OScore2(bench.get(i), pred.get(j)) >= os) {
                    TPb++;
                    break;
                }
        }
        for (int i = 0; i < pred.size(); i++) {
            for (int j = 0; j < bench.size(); j++)
                if (OScore2(pred.get(i), bench.get(j)) >= os) {
                    TPp++;
                    break;
                }
        }
        //System.out.print("#predicted complex : "+ pred.size()+"\n");
        precision = Double.valueOf((double) TPp / pred.size());
        recall = Double.valueOf((double) TPb / bench.size());
        fmeasure = Double.valueOf((double) (2 * precision * recall) / (precision + recall));
//        System.out.print(precision + "  " + recall + "  " + fmeasure + "\n");
//        System.out.print("**PR**\n");
//        double [][] PRij = new double[pred.size()][bench.size()];
//        double [] PRCi = new double[pred.size()];
//        double [] PRGj = new double[bench.size()];
//        for(int i=0; i<pred.size();i++) {
//            for (int j = 0; j < bench.size(); j++) {
//                PRij[i][j] = Math.pow(SizeOfIntersectTwoArray(pred.get(i), bench.get(j)), 2) / (pred.get(i).size() * bench.get(j).size());
//            }
//            PRCi[i] = Find_max(PRij,i,true);
//        }
//        for(int j=0; j<bench.size();j++)
//            PRGj[j] = Find_max(PRij,j,false);
//        double PRC=0,size_p=0;
//        for(int i=0; i<pred.size();i++){
//            PRC += pred.get(i).size()*PRCi[i];
//            size_p += pred.get(i).size();
//        }
//        double PRG=0,size_g=0;
//        for(int j=0; j<bench.size();j++){
//            PRG += bench.get(j).size()*PRGj[j];
//            size_g += bench.get(j).size();
//        }
//        PRC  /= size_p;
//        PRG /= size_g;
//        double PR = (2*PRC*PRG)/(PRC+PRG);
//        System.out.print(" PR : "+PR);
        return fmeasure;
    }

    private static float OScore(ArrayList one, ArrayList two) {

        return (float) (Math.pow(SizeOfIntersectTwoArray(one, two), 2) / (one.size() * two.size()));
    }

    private static float OScore2(ArrayList one, ArrayList two) {
        return (((float) SizeOfIntersectTwoArray(one, two)) / SizeOfUnionTwoArray(one, two));
    }

    private static int SizeOfUnionTwoArray(ArrayList<Integer> One, ArrayList<Integer> Two) {
        ArrayList<Integer> union = (ArrayList<Integer>) One.clone();
        for (int i = 0; i < Two.size(); i++)
            if (!union.contains(Two.get(i)))
                union.add(Two.get(i));
        return union.size();
    }

    private static int SizeOfIntersectTwoArray(ArrayList<String> One, ArrayList<String> Two) {
        int a = 0;
        for (int i = 0; i < One.size(); i++)
            if (Two.contains(One.get(i)))
                a++;
        return a;
    }


} // Biclustering3
