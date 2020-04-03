
package jmetal.problems;

import jmetal.core.*;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;

import java.util.ArrayList;

/**
 * Class representing problem Biclustering2
 */
public class Biclustering2 extends Problem {




    public Biclustering2(String solutionType, String exp_Path) {
        geneExpPath = exp_Path;
        numberOfObjectives_ = 2;
        numberOfConstraints_ = 0;
        problemName_ = "Biclustering2";
        geneExpressionData = new Matrix();
        geneExpressionData.read_Gene_Expression(geneExpPath);

        numberOfGeneVariables_ = (int) Math.ceil(Math.sqrt(geneExpressionData.sizeOfProteins));
        numberOfConditionVariables_ = (int) Math.ceil(Math.sqrt(geneExpressionData.sizeOfConditions));
        numberOfVariables_ = numberOfGeneVariables_ + numberOfConditionVariables_;
        upperLimit_ = new double[numberOfVariables_];
        lowerLimit_ = new double[numberOfVariables_];

        for (int var = 0; var < numberOfGeneVariables_; var++) {
            lowerLimit_[var] = -1;
            upperLimit_[var] = geneExpressionData.sizeOfProteins;
        }
        for (int var = numberOfGeneVariables_; var < numberOfVariables_; var++) {
            lowerLimit_[var] = -1;
            upperLimit_[var] = geneExpressionData.sizeOfConditions;
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
        solution.sizeOfBiclusters_ = ComputeNumberOfBiclustersInSolution(solution);
//        UpdateSolution(solution);
        double [] f = new double[numberOfObjectives_] ;

        Bicluster[] biclusters = ExtractBiclusters(solution);
        f[0] = MSR(biclusters) ;
        f[1] = VAR(biclusters) ;

        solution.setObjective(0,f[0]);
        solution.setObjective(1,f[1]);
    } //evaluate

    @Override
    public int GetNumberOfOnes(Solution solution) throws JMException {
        return 0;
    }


    private int ComputeNumberOfBiclustersInSolution(Solution solution) throws JMException {
        XReal x = new XReal(solution) ;
        int gCenters = 0,cCenters=0;
        for(int i=0; i<numberOfGeneVariables_;i++)
            if( (int)x.getValue(i) != -1)
                gCenters++;
        for(int i=numberOfGeneVariables_; i<numberOfVariables_;i++)
            if( (int)x.getValue(i) != -1)
                cCenters++;
        return gCenters*cCenters;

    }

    private Bicluster[] ExtractBiclusters(Solution solution) throws JMException {
        XReal x = new XReal(solution) ;
        ArrayList<Bicluster> biclusters = new ArrayList<Bicluster>();

        int gCenterIndex,cCenterIndex;
        for(int j=0; j<numberOfGeneVariables_;j++) {
            if ((int) x.getValue(j) == -1) continue;
            gCenterIndex = /*(int) x.getValue(j)*/j;
            for (int k = numberOfGeneVariables_; k < numberOfVariables_; k++) {
                if ((int) x.getValue(k) == -1) continue;
                cCenterIndex = /*(int) x.getValue(k)*/k;
                biclusters.add(ExtractBicluster(x, gCenterIndex, cCenterIndex));
            }
        }
        Bicluster[] biclustersArray = new Bicluster[biclusters.size()];
        biclusters.toArray(biclustersArray);
        return  biclustersArray;
    }

//    private void UpdateSolution(Solution solution) throws JMException {
//        XReal x = new XReal(solution) ;
//        double[][] gDistanceMatrix = gDistance1(x);
//        double[][] cDistanceMatrix = cDistance1(x);
//        int gCenterIndex,cCenterIndex;
//        for(int i=0; i<numberOfGeneVariables_;i++){
//            if((int)x.getValue(i) == -1)continue;
//            gCenterIndex = /*(int)x.getValue(i)*/i;
//            for(int j=numberOfGeneVariables_; j<numberOfVariables_;j++) {
//                if((int)x.getValue(j) == -1)continue;;
//                cCenterIndex = /*(int) x.getValue(j);*/j;
//                UpdateBicluster(solution,gCenterIndex,cCenterIndex);
//            }
//
//        }
//
//    }

//    private void UpdateBicluster(Solution solution, int gCenterIndex, int cCenterIndex) throws JMException {
//        XReal x = new XReal(solution);
//        Bicluster b = ExtractBicluster(x,gCenterIndex,cCenterIndex);
//        for(int i=0;i<numberOfGeneVariables_;i++)
//            x.setValue(i,b.geneIndexes.get(i));
//    }

    private Bicluster ExtractBicluster(XReal x, int gCenterIndex, int cCenterIndex) throws JMException {
        double[][] gDistanceMatrix = gDistance1(x);
        double[][] cDistanceMatrix = cDistance1(x);

        ArrayList<Integer> geneList = new ArrayList<Integer>();
        for(int gIndex=0;gIndex<geneExpressionData.sizeOfProteins;gIndex++) {
            double min = 1000000;int minIndex = 0;
            for (int gcIndex = 0; gcIndex < numberOfGeneVariables_; gcIndex++) {
                if((int)x.getValue(gcIndex)==-1) continue;
                if (gDistanceMatrix[gIndex][(int)x.getValue(gcIndex)] < min) {
                    min = gDistanceMatrix[gIndex][(int)x.getValue(gcIndex)];
                    minIndex = gIndex;
                }
            }
            if(minIndex==gCenterIndex)
                geneList.add(minIndex);
        }


        ArrayList<Integer> conList = new ArrayList<Integer>();
        for(int cIndex=0;cIndex<geneExpressionData.sizeOfConditions;cIndex++) {
            double min = 1000000;int minIndex = 0;
            for (int ccIndex = numberOfGeneVariables_; ccIndex < numberOfVariables_; ccIndex++) {
                if((int)x.getValue(ccIndex)==-1) continue;
                if (cDistanceMatrix[cIndex][(int)x.getValue(ccIndex)] < min) {
                    min = cDistanceMatrix[cIndex][(int)x.getValue(ccIndex)];
                    minIndex = cIndex;
                }
            }
            if(minIndex==cCenterIndex)
                conList.add(minIndex);
        }
        Bicluster bicluster = new Bicluster();
        bicluster.geneIndexes = geneList;
        bicluster.conIndexes  = conList;
        return bicluster;
    }

    private double[][] gDistance1(XReal x) throws JMException {


        double[][] distanceM = new double[geneExpressionData.sizeOfProteins][geneExpressionData.sizeOfProteins];
        //-> Calculate the euclidean distance
        for (int geneIndex = 0; geneIndex < geneExpressionData.sizeOfProteins;geneIndex++){
            for(int gcenterIndex=0;gcenterIndex<numberOfGeneVariables_;gcenterIndex++) {
                if((int)x.getValue(gcenterIndex)==-1)continue;
                double distance = 0.0;
                double diff;    //Auxiliar var
                for(int k=0;k<geneExpressionData.sizeOfConditions;k++) {
                    diff = geneExpressionData.mat[geneIndex][k] - geneExpressionData.mat[(int)x.getValue(gcenterIndex)][k];
                    distance += Math.pow(diff, 2.0);
                }
                distanceM[geneIndex][gcenterIndex] = Math.sqrt(distance);
            }
        } // for

        //Return the euclidean distance
        return distanceM;
    }
    private double[][] cDistance1(XReal x) throws JMException {


        double[][] distanceM = new double[geneExpressionData.sizeOfConditions][geneExpressionData.sizeOfConditions];
        //-> Calculate the euclidean distance
        for (int conIndex = 0; conIndex < geneExpressionData.sizeOfConditions;conIndex++){
            for(int ccenterIndex=numberOfGeneVariables_;ccenterIndex<numberOfGeneVariables_;ccenterIndex++) {
                if((int)x.getValue(ccenterIndex)==-1)continue;
                double distance = 0.0;
                double diff;    //Auxiliar var
                for(int k=0;k<geneExpressionData.sizeOfProteins;k++) {
                    diff = geneExpressionData.mat[k][conIndex] - geneExpressionData.mat[k][(int)x.getValue(ccenterIndex)];
                    distance += Math.pow(diff, 2.0);
                }
                distanceM[conIndex][ccenterIndex] = Math.sqrt(distance);
            }
        } // for

        //Return the euclidean distance
        return distanceM;
    }

    public double MSR(Bicluster[] ch) {
        double ave_MSR = 0;
        for(Bicluster bb : ch){
            double mean=0,msr=0;
            double[] gene_mean = new double[bb.geneIndexes.size()];
            double[] con_mean = new double[bb.conIndexes.size()];
            for(int i=0;i<bb.geneIndexes.size();i++) {
                for (int j=0;j<bb.conIndexes.size();j++) {
                    gene_mean[i] += geneExpressionData.mat[bb.geneIndexes.get(i)][bb.conIndexes.get(j)];
                    mean += geneExpressionData.mat[bb.geneIndexes.get(i)][bb.conIndexes.get(j)];
                }
                gene_mean[i] /= bb.conIndexes.size();
            }
            mean /= bb.conIndexes.size()*bb.geneIndexes.size();
            for(int j=0;j<bb.conIndexes.size();j++) {
                for (int i=0;i<bb.geneIndexes.size();i++)
                    con_mean[j] += geneExpressionData.mat[bb.geneIndexes.get(i)][bb.conIndexes.get(j)];
                con_mean[j] /= bb.geneIndexes.size();
            }
            for(int i=0;i<bb.geneIndexes.size();i++)
                for (int j=0;j<bb.conIndexes.size();j++)
                    msr += Math.pow(geneExpressionData.mat[bb.geneIndexes.get(i)][bb.conIndexes.get(j)] - gene_mean[i] - con_mean[j] + mean, 2);
            msr /= bb.conIndexes.size()*bb.geneIndexes.size();
            ave_MSR += msr;
        }
        return ave_MSR/ch.length;
    }

    public double VAR(Bicluster[] ch) {
        double ave_VAR = 0;
        for(Bicluster bb : ch){
            double var = 0, mean=0;
            for(int i=0;i<bb.geneIndexes.size();i++)
                for (int j=0;j<bb.conIndexes.size();j++)
                    mean += geneExpressionData.mat[bb.geneIndexes.get(i)][bb.conIndexes.get(j)];
            mean /= bb.conIndexes.size()*bb.geneIndexes.size();
            for(int i:bb.geneIndexes)
                for(int j:bb.conIndexes)
                    var += Math.pow(geneExpressionData.mat[bb.geneIndexes.get(i)][bb.conIndexes.get(j)]-mean,2);
            ave_VAR += var;
        }
        ave_VAR /= ch.length;
        return ave_VAR;

    }

} // Biclustering2
