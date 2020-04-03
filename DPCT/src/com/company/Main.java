package com.company;

import bicat.algorithms.bimax.BiMax;
import bicat.preprocessor.FileOffsetException;
import bicat.preprocessor.PreprocessOption;
import bicat.preprocessor.Preprocessor;
import jmetal.core.*;
import jmetal.metaheuristics.singleObjective.geneticAlgorithm.GA_main;
import smadeira.biclustering.*;
import smadeira.biclustering.Bicluster;
import java.io.*;
import java.util.*;


public class Main {

    public static void main(String[] args) throws Exception {
        long initTime = System.currentTimeMillis();

        double alpha = 0.3, beta = 0.5, epsilon = 0.6;
        int zeta = 30;
        String PPI_path = "biogrid_yeast_physical_WEIGHTED.txt";
//        String PPI_path = "DIP_static_ppi_WEIGHTED.txt";
        String benchmark = "MIPS";
//        String benchmark = "CYC2008";

        System.out.println("Parameters:\t\tAlpha:" + alpha + "   Beta:" + beta + "   Epsilon:" + epsilon + "   Zeta:" + zeta);
        System.out.println("PPI Network:\t" + PPI_path);
        System.out.println("Benchmark:\t\t" + benchmark + "\n");
        System.out.println("Starting DPCT.....\n");

        GA_main memetic_main = new GA_main();
        memetic_main.main(epsilon, zeta);
        main_Bimax_Biclustering(PPI_path, benchmark, alpha, beta);
        long totalTime = System.currentTimeMillis() - initTime;
        System.out.println("\nTotal execution time: " + totalTime);
    }

    public static void main_Bimax_Biclustering(String PPI_path, String benchmark, double alpha, double beta) throws Exception {
        ExpressionMatrix expressionMatrix = new ExpressionMatrix();
        String GED_Path1 = "DIP_expression_data.txt";
        expressionMatrix.readExpressionMatrixFromFileTXT(GED_Path1, '\t');
        CC_Biclustering cc_biclustering = new CC_Biclustering(expressionMatrix, 0.1f, .1f, 1, 1000);

        Matrix static_PPI = new Matrix();
        Matrix geneExpressionData1 = new Matrix();
        geneExpressionData1.read_Gene_Expression(GED_Path1);
        static_PPI.read_PPI_Filter_By_GED(PPI_path, geneExpressionData1.lookuptb);
        String subNetsPath = "";
        cc_biclustering.convert_From_VAR_File_Binary();
        subNetsPath = System.getProperty("user.dir") + "\\GED\\";
        Create_Sub_networks_based_Biclusters2(cc_biclustering.getBiclusters(), static_PPI, geneExpressionData1, .85, subNetsPath);


        //detects complexes
        Matrix subNet = null;
        ArrayList<String> seeds = new ArrayList<String>();
        ArrayList<ArrayList<String>> cores = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> initCpx = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> unionCpx = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < cc_biclustering.getNumberOfBiclusters(); i++) {
            //CAMWI
            subNet = new Matrix();
            subNet.read_PPI(subNetsPath + "subnetwork" + String.valueOf(i) + ".txt");
            seeds = subNet.SeedGeneration(alpha);
            cores = subNet.CoreFinding(seeds);
            initCpx = subNet.CoreGrowing(cores, beta);
            for (int j = 0; j < initCpx.size(); j++) {
                unionCpx.add(initCpx.get(j));
            }
        }
        unionCpx = Filtering(unionCpx, 3);
        ArrayList<ArrayList<String>> bench = new ArrayList<ArrayList<String>>();

        if (benchmark == "CYC2008")
            Generate_clear_CYC2008(bench, static_PPI.lookuptb);
        else
            Generate_clear_MIPS(bench, static_PPI.lookuptb);
        Evaluate(unionCpx, bench, 0.25);
        System.out.println("Analysis and Aggregation Phase Started");
        PostProcessing.postProcess(geneExpressionData1, static_PPI, unionCpx);
    }

    private static void Create_Sub_networks_based_Biclusters2(ArrayList<Bicluster> bcs, Matrix static_PPI, Matrix geneExpressionData, double th, String path) {
        Interaction interaction;
        //creates global and experiment-dependent stable PPIs
        ArrayList<Interaction> Stable_Interactions = new ArrayList<Interaction>();
        for (int i = 0; i < static_PPI.interactions.size(); i++) {
            //System.out.print(i+"\n");
            interaction = static_PPI.interactions.get(i);
            if (geneExpressionData.containInteraction(interaction) && geneExpressionData.getCorrelationOfInteraction(interaction) >
                    th)
                Stable_Interactions.add(interaction);
        }
        //create transient interactions
        ArrayList<ArrayList<Interaction>> subNetworks = new ArrayList<ArrayList<Interaction>>();
        ArrayList<Interaction> transients;
        //transients
        for (int j = 0; j < bcs.size(); j++) {
            transients = new ArrayList<Interaction>();
            for (int i = 0; i < static_PPI.interactions.size(); i++) {
                interaction = static_PPI.interactions.get(i);
                if (geneExpressionData.containInteraction(interaction)) {
                    if (Arrays.asList(bcs.get(j).getGenesNames()).contains(interaction.p1) && Arrays.asList(bcs.get(j).getGenesNames()).contains(interaction.p2)) {
                        transients.add(interaction);
                    }
                }
            }
            subNetworks.add(transients);
        }
        //adding stable(global, exp dependent) interactions to subNetworks
        for (int i = 0; i < subNetworks.size(); i++) {
            for (int j = 0; j < Stable_Interactions.size(); j++) {
                if (!subNetworks.get(i).contains(Stable_Interactions.get(j)))//??? ???
                    subNetworks.get(i).add(Stable_Interactions.get(j));
            }
        }
        //writes sub-networks
        write_Subnetworks(subNetworks, path);
    }


    private static void write_Subnetworks(ArrayList<ArrayList<Interaction>> exp_sunNetworks, String path) {
        FileOutputStream fstream = null;
        DataOutputStream out;
        BufferedWriter bw;
        for (int i = 0; i < exp_sunNetworks.size(); i++) {
            try {
                fstream = new FileOutputStream(path + "subnetwork" + String.valueOf(i) + ".txt");
                out = new DataOutputStream(fstream);
                bw = new BufferedWriter(new OutputStreamWriter(out));
                for (int j = 0; j < exp_sunNetworks.get(i).size(); j++) {
                    bw.write(exp_sunNetworks.get(i).get(j).p1 + "\t" + exp_sunNetworks.get(i).get(j).p2 + "\t" + exp_sunNetworks.get(i).get(j).weight + "\n");
                }
                bw.close();
                fstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public static void Generate_clear_MIPS(ArrayList<ArrayList<String>> bench, Hashtable<String, Integer> lookuptb) {
        ArrayList<ArrayList<String>> bench2 = new ArrayList<ArrayList<String>>();
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream("mips.txt");
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
            while ((line = br.readLine()) != null) {
                cpx = new ArrayList<String>();
                st = new StringTokenizer(line);
                while (st.hasMoreTokens()) {
                    p = st.nextToken();
                    if (lookuptb.containsKey(p))
                        cpx.add(p);
                }
                bench2.add(cpx);
                //System.out.print(line+"\n");
            }

//            System.out.print("\n#Benchmark size : " + bench2.size());
            for (int i = 0; i < bench2.size(); i++)
                if (bench2.get(i).size() > 2)
                    bench.add(bench2.get(i));
        } catch (IOException e) {
            e.printStackTrace();
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

    public static void Evaluate(ArrayList<ArrayList<String>> pred, ArrayList<ArrayList<String>> bench, double os) {
        Double precision = new Double(0);
        Double recall = new Double(0);
        Double fmeasure = new Double(0);
        //double os = 0.2;
        int TPb = 0, TPp = 0;
        for (int i = 0; i < bench.size(); i++) {
            for (int j = 0; j < pred.size(); j++)
                if (OScore(bench.get(i), pred.get(j)) >= os) {
                    TPb++;
                    break;
                }
        }
        for (int i = 0; i < pred.size(); i++) {
            for (int j = 0; j < bench.size(); j++)
                if (OScore(pred.get(i), bench.get(j)) >= os) {
                    TPp++;
                    break;
                }
        }
        System.out.print("#predicted complex: " + pred.size() + "\t");
        precision = Double.valueOf((double) TPp / pred.size());
        recall = Double.valueOf((double) TPb / bench.size());
        fmeasure = Double.valueOf((double) (2 * precision * recall) / (precision + recall));
        System.out.print("  precision:" + String.format("%.3f", precision) + "  recall:" + String.format("%.3f", recall) + "  f1:" + String.format("%.3f", fmeasure) + "\n");
    }

    private static double[][] transpose(double[][] mat) {
        double[][] temp = new double[mat[0].length][mat.length];
        for (int i = 0; i < mat.length; i++)
            for (int j = 0; j < mat[0].length; j++)
                temp[j][i] = mat[i][j];
        return temp;
    }

    private static float OScore(ArrayList one, ArrayList two) {
        return (float) (Math.pow(SizeOfIntersectTwoArray(one, two), 2) / (one.size() * two.size()));
    }

    public static float OScore2(ArrayList one, ArrayList two) {
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

    private static double Find_max(double[][] pr, int index, boolean row_based) {
        double max;
        if (row_based) {
            max = pr[index][0];
            for (int j = 1; j < pr[0].length; j++) {
                if (pr[index][j] > max)
                    max = pr[index][j];
            }
        } else {
            max = pr[0][index];
            for (int i = 1; i < pr.length; i++) {
                if (pr[i][index] > max)
                    max = pr[i][index];
            }
        }
        return max;
    }

    private static HashMap biClustering_biMax(String path, int n, int m, boolean do_normalize, String geneExpName, int min_gene_bicluster, int min_column_bicluster, double alpha, int ONE_symbol) {

        Preprocessor preprocessor = new Preprocessor();
        File file = new File(path);
        HashMap biClusters = new HashMap();
        HashMap biClusters_temp = new HashMap();
        try {
            preprocessor.readMainDataFile(file, 1, 1);
            PreprocessOption preprocessOption = new PreprocessOption();//default  preprocess options
            preprocessOption.do_compute_logarithm = false;
            preprocessOption.do_normalize = do_normalize;
            preprocessOption.do_normalize_genes = true;
            //preprocessOption.do_normalize_chips = true;
            preprocessOption.do_discretize = true;
            preprocessOption.discretizationMode = "threshold";
            preprocessor.preprocessData(preprocessOption, alpha);

            //int[][] discreteData = preprocessOption.getDiscreteMatrix();
            int[][] discreteData = preprocessor.getDiscreteData();
            for (int i = 0; i < discreteData.length; i++) {
                for (int j = 0; j < discreteData[0].length; j++) {
                    if (discreteData[i][j] == ONE_symbol)
                        discreteData[i][j] = 1;
                    else
                        discreteData[i][j] = 0;
                }
            }
            BiMax biMax = new BiMax();
            biClusters = biMax.run(discreteData, n, m, "", "pp", min_gene_bicluster, min_column_bicluster, "D:\\Dr jalili\\paper 3\\paper3.2\\biclusters_" + geneExpName + ".txt");
            System.out.print("\n Mode: " + ONE_symbol + " --> 1 , #clusters: " + biClusters.size() + "\n");
        } catch (FileOffsetException e) {
            e.printStackTrace();
        }
        return biClusters;
    }


    public static ArrayList<ArrayList<String>> ReadBIGGEST(String path) {
        ArrayList<ArrayList<String>> biclusters = new ArrayList<ArrayList<String>>();
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        StringTokenizer st;
        ArrayList<String> bicluster;
        String p;
        try {
            while ((line = br.readLine()) != null) {
                line = br.readLine();
                bicluster = new ArrayList<String>();
                st = new StringTokenizer(line);
                while (st.hasMoreTokens()) {
                    p = st.nextToken();
                    bicluster.add(p);
                }
                biclusters.add(bicluster);
                br.readLine();
            }
            br.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return biclusters;

    }


}
