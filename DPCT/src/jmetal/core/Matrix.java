package jmetal.core;

import java.io.*;
import java.util.*;

import bicat.algorithms.bimax.BiMax;
import bicat.preprocessor.FileOffsetException;
import bicat.preprocessor.PreprocessOption;
import bicat.preprocessor.Preprocessor;
import bicat.util.PreUtil;
import jmetal.encodings.variable.Binary;
import math3.stat.correlation.PearsonsCorrelation;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class Matrix {
    public int sizeOfProteins;
    public int sizeOfConditions;
    public double[][] mat;
    public char[][] dmat;//discretized expression matrix
    public double[] mean;
    public double[] var;
    public double[][] wmat;
    public double[][] ECC;
    public double[][] CorrMat;
    public String[] geneName;
    double [] active_Th;
    public ArrayList<Interaction> interactions;
    public Hashtable<String, Integer> lookuptb;
    public Hashtable<Integer, String> lookuptb2;
    public Matrix(){
        lookuptb = new Hashtable<String, Integer>();
        lookuptb2 = new Hashtable<Integer, String>();
        interactions = new ArrayList<Interaction>();
    }
    //    public void setSize(int size1){
//        size = size1;
//    }
    public  void read_Gene_Expression(String path){
        FileInputStream fstream = null;
        try {
            int conditions=0;
            fstream = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line,gene;
            StringTokenizer st;
            int j=-1;
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                st = new StringTokenizer(line);
                gene = st.nextToken();
                conditions = st.countTokens();
                if(!lookuptb.containsKey(gene)) {
                    lookuptb.put(gene, ++j);
                    lookuptb2.put(j, gene);
                }
            }
            fstream.close();
            fstream = new FileInputStream(path);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            sizeOfProteins = lookuptb.size();
            sizeOfConditions = conditions;
            mat = new double[sizeOfProteins][sizeOfConditions];
            CorrMat = new double[sizeOfProteins][sizeOfProteins];
            mean = new double[sizeOfProteins];
            var = new double[sizeOfProteins];
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                conditions=0;
                st = new StringTokenizer(line);
                gene = st.nextToken();
                while (st.hasMoreTokens()) {
                    mat[lookuptb.get(gene)][conditions++] = Double.parseDouble(st.nextToken());
                }
            }
            for (int i = 0; i < sizeOfProteins; i++) {
                for (j = 0; j < sizeOfConditions; j++)
                    mean[i] += mat[i][j];
            }
            for (int i = 0; i < sizeOfProteins; i++)
                mean[i] /= sizeOfConditions;

            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //computes active thresholds
        active_Th = new double[sizeOfProteins];
        //compute_Active_Thresholds(0.9);
        Generate_correlationMatrix();
    }
    public  void read_PPI(String path){
        FileInputStream fstream = null;
        StringTokenizer st;
        String gene1, gene2,line;
        DataInputStream in;
        BufferedReader br;
        try{
            fstream = new FileInputStream(path);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            int j = -1, Cline = 0;
            while ((line = br.readLine()) != null) {
                Cline++;
                st = new StringTokenizer(line);
                if (st.countTokens() < 2) {
                    System.out.print("Error in line : " + String.valueOf(Cline));
                }
                else {
                    gene1 = st.nextToken();
                    gene2 = st.nextToken();
//                    if(gene1.contains("-")) {
//                        st2 = new StringTokenizer(gene1, "-");
//                        gene1 = st2.nextToken();
//                    }
//                    if(gene2.contains("-")) {
//                        st2 = new StringTokenizer(gene2, "-");
//                        gene2 = st2.nextToken();
//                    }
                    if (!lookuptb.containsKey(gene1)) {
                        lookuptb.put(gene1, ++j);
                        lookuptb2.put(j, gene1);
                    }
                    if (!lookuptb.containsKey(gene2)) {
                        lookuptb.put(gene2, ++j);
                        lookuptb2.put(j, gene2);
                    }
                }
            }
            fstream.close();double w;Interaction i;
            sizeOfProteins = lookuptb.size();
            mat = new double[sizeOfProteins][sizeOfProteins];
            geneName = new String[sizeOfProteins];
            fstream = new FileInputStream(path);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while ((line = br.readLine()) != null) {
                st = new StringTokenizer(line);
                gene1 = st.nextToken();
                gene2 = st.nextToken();
                if (st.hasMoreTokens())
                    w = Double.parseDouble(st.nextToken());
                else
                    w = 1;
                mat[lookuptb.get(gene1)][lookuptb.get(gene2)] = w;
                mat[lookuptb.get(gene2)][lookuptb.get(gene1)] = w;
                i = new Interaction(gene1,gene2,w);
                interactions.add(i);
            }
            fstream.close();
            for(int k=0;k<lookuptb2.size();k++)
                geneName[k] = lookuptb2.get(k);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
    private  void Generate_correlationMatrix(){
        PearsonsCorrelation corrInstance1 = new PearsonsCorrelation();
        CorrMat = corrInstance1.computeCorrelationMatrix(transpose()).getData();
    }
    public  boolean containInteraction(Interaction interaction){
        return (lookuptb.containsKey(interaction.p1)&& lookuptb.containsKey(interaction.p2));
    }
    public void writeToFileInteractions(String path){
        FileOutputStream fstream = null;
        DataOutputStream out;
        BufferedWriter bw;
        try {
            fstream = new FileOutputStream(path);
            out = new DataOutputStream(fstream);
            bw = new BufferedWriter(new OutputStreamWriter(out));
            for(int i=0; i<interactions.size();i++) {
                bw.write(interactions.get(i).p1+"\t"+interactions.get(i).p2+"\n");
            }
            fstream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public double getCorrelationOfInteraction(Interaction i){
        return  CorrMat[lookuptb.get(i.p1)][lookuptb.get(i.p2)];
    }
    public boolean is_Active(Interaction interaction,int column){
        return (mat[lookuptb.get(interaction.p1)][column] > active_Th[lookuptb.get(interaction.p1)] && mat[lookuptb.get(interaction.p2)][column] > active_Th[lookuptb.get(interaction.p2)]);
    }
    private void compute_Active_Thresholds(double a){
        double[] ave = new double[sizeOfProteins];
        for(int i=0; i<sizeOfProteins;i++){
            for(int j=0; j<sizeOfConditions; j++)
                ave[i] += mat[i][j];
            ave[i] /=  sizeOfConditions;
            active_Th[i] = a*ave[i];
        }
    }
    private double[][] transpose(){
        double[][] temp = new double[mat[0].length][mat.length];
        for (int i = 0; i < mat.length; i++)
            for (int j = 0; j < mat[0].length; j++)
                temp[j][i] = mat[i][j];
        return temp;
    }
    private double[][] normalize(double[][] data){
        double[][]result = new double[data.length][data[0].length];
        double[] row_means = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            int sum = 0;
            for (int j = 0; j < data[0].length; j++)
                sum += data[i][j];
            row_means[i] = sum / data[0].length;
        }
        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data[0].length; j++)
                data[i][j] -= row_means[i];

        // compute the variance
        double[] row_vars = new double[data.length];
        for (int i = 0; i < data.length; i++)
            row_vars[i] = PreUtil.computeVariance(data[i]);

        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data[0].length; j++)
                if (row_vars[i] != 0)
                    result[i][j] = data[i][j] / row_vars[i];
        return  result;
    }


    public ArrayList<String> SeedGeneration(double alpha) {
        ArrayList<String> seeds = new ArrayList<String>();
        float num = 0, denum = 0;
        int Cnbr = 0; float wcc;
        for (int i = 0; i < sizeOfProteins; i++) {
            num = 0;
            Cnbr = 0;
            wcc=0;
            for (int j = 0; j < sizeOfProteins; j++) {
                if (mat[i][j] > 0) {
                    Cnbr += 1;
                    for (int k = 0; k < sizeOfProteins; k++) {
                        if (k != j && mat[i][k] > 0)
                            num += mat[j][k];
                    }
                }
            }
            if (Cnbr > 1) {
                denum = Cnbr * (Cnbr - 1);
                wcc = num/denum;
            }
            if( wcc >= alpha )
                seeds.add(lookuptb2.get(i));

        }
        return seeds;
    }

    public ArrayList<ArrayList<String>> CoreFinding(ArrayList<String> seeds) {
        ArrayList<ArrayList<String>> cores = new ArrayList<ArrayList<String>>();
        ArrayList<String> core, temp;
        String Cver;
        for (int i = 0; i < seeds.size(); i++) {
            core = new ArrayList<String>();
            core.add(seeds.get(i));
            ArrayList<String> InducedSG = InduceSubGraph(seeds.get(i));
            Cver = MaxWdeg(InducedSG, core);
            temp = new ArrayList<String>();
            temp = (ArrayList<String>) core.clone();
            temp.add(Cver);
            while (Cver != null && (Wdensity(temp) >= Wdensity(core))) {
                //System.out.print("ok");
                core.add(Cver);
                Cver = MaxWdeg(InducedSG, core);
                temp.add(Cver);
            }
            cores.add(core);
        }
        return (cores);
    }

    private double Wdensity(ArrayList<String> temp) {
        double wden = 0;
        int c = 0;
        for (int i = 0; i < temp.size(); i++)
            for (int j = i + 1; j < temp.size(); j++)
                if (mat[lookuptb.get(temp.get(i))][lookuptb.get(temp.get(j))] > 0) {
                    wden += mat[lookuptb.get(temp.get(i))][lookuptb.get(temp.get(j))];
                    c++;
                }
        if (c > 0)
            return ((2*wden) / (temp.size() * (temp.size() - 1)));
        else
            return 0;

    }

    private String MaxWdeg(ArrayList<String> inducedSG, ArrayList<String> core) {
        float Cmax = 0;
        String index = null;
        float cur;
        for (int i = 0; i < inducedSG.size(); i++) {
            if (!core.contains(inducedSG.get(i))) {
                cur = 0;
                for (int j = 0; j < core.size(); j++)
                    cur += mat[lookuptb.get(inducedSG.get(i))][lookuptb.get(core.get(j))];
                if (cur > Cmax) {
                    Cmax = cur;
                    index = inducedSG.get(i);
                }
            }
        }
        return index;
    }

    private ArrayList<String> InduceSubGraph(String s) {
        ArrayList<String> ISG = new ArrayList<String>();
        for (int i = 0; i < mat.length; i++)
            if (mat[lookuptb.get(s)][i] > 0) ISG.add(lookuptb2.get(i));
        return (ArrayList<String>) ISG.clone();
    }

    public ArrayList<ArrayList<String>> CoreGrowing(ArrayList<ArrayList<String>> cores, double beta) {
        ArrayList<ArrayList<String>> InitCpx = new ArrayList<ArrayList<String>>();
        ArrayList<String> nbrG;
        for (int i = 0; i < cores.size(); i++) {
            InitCpx.add(cores.get(i));
            for (int j = 0; j < cores.get(i).size(); j++) {
                ArrayList<String> nbrList = InduceSubGraph(cores.get(i).get(j));
                if (nbrList.size() > 0) {
                    for (int k = 0; k < nbrList.size(); k++) {
                        if (!cores.get(i).contains(nbrList.get((k)))) {
                            nbrG = new ArrayList<String>();
                            nbrG = InduceSubGraph(nbrList.get((k)));
                            if (SizeOfIntersectTwoArray(nbrG, InitCpx.get(i)) > beta * InitCpx.get(i).size()) {
                                InitCpx.get(i).add(nbrList.get(k));
                                //System.out.print("!\n");
                            }
                        }
                    }
                }
            }
        }
        //return Filtering(InitCpx);
        return  InitCpx;

    }

    private double SizeOfIntersectTwoArray(ArrayList<String> one, ArrayList<String> two) {
        int a = 0;
        for (int i = 0; i < one.size(); i++)
            if (two.contains(one.get(i)))
                a++;
        return a;


    }
    private  void PPI_Compute_ECC(){
        //computes Edge clustering coefficients
        ECC = new double[sizeOfProteins][sizeOfProteins];
        int nbr, deg_i,deg_j;
        for(int i=0;i<interactions.size();i++){
            Interaction I = interactions.get(i);
            nbr = 0;deg_i=0;deg_j=0;
            for(int k=0;k<sizeOfProteins;k++) {
                if (mat[k][lookuptb.get(I.p1)] > 0 && mat[k][lookuptb.get(I.p2)] > 0)
                    nbr++;
                if(mat[lookuptb.get(I.p1)][k]>0) deg_i++;
                if(mat[lookuptb.get(I.p2)][k]>0) deg_j++;
            }
            if(nbr==0) ECC[lookuptb.get(I.p1)][lookuptb.get(I.p2)] = 0;
            else
                ECC[lookuptb.get(I.p1)][lookuptb.get(I.p2)] = nbr/((deg_i-1) * (deg_j-1));
        }
    }
    public  void PPI_Weighting(Matrix geneExpressionData){
        PPI_Compute_ECC();

        wmat = new double[sizeOfProteins][sizeOfProteins];
        for(int i=0; i<interactions.size();i++){
            Interaction I = interactions.get(i);
            if(geneExpressionData.containInteraction(I))
                wmat[lookuptb.get(I.p1)][lookuptb.get(I.p2)] = /*ECC[lookuptb.get(I.p1)][lookuptb.get(I.p2)] + */geneExpressionData.getCorrelationOfInteraction(I);
        }
    }
    public ArrayList<String> weighted_SeedGeneration(int size) {
        ArrayList<String> seeds = new ArrayList<String>();
        double []w = new double[sizeOfProteins];
        for(int i=0;i<sizeOfProteins;i++){
            w[i]=0;
            for(int j=0;j<sizeOfProteins;j++){
                if(mat[i][j]>0){
                    w[i] += wmat[i][j];
                }
            }
        }
        OBJECT[] w_Sorted = new OBJECT[sizeOfProteins];
        for(int i=0;i<sizeOfProteins;i++)
            w_Sorted[i] = new OBJECT(i,w[i]);
        for(int i=0;i<sizeOfProteins-1;i++)
            for(int j=sizeOfProteins-1;j>i;j--)
                if(w_Sorted[j].value > w_Sorted[j-1].value){
                    OBJECT t = w_Sorted[j];
                    w_Sorted[j] = w_Sorted[j-1];
                    w_Sorted[j-1] = t;
                }
        for(int i=0;i<size;i++)
            seeds.add(lookuptb2.get(w_Sorted[i].key));
        return seeds;
    }

    public void read_PPI_Filter_By_GED(String ppi_path, Hashtable<String, Integer> lookuptb) {
        FileInputStream fstream = null;
        StringTokenizer st,st2;
        String gene1, gene2,line;
        DataInputStream in;
        BufferedReader br;
        try{
            fstream = new FileInputStream(ppi_path);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            int j = -1, Cline = 0;
            while ((line = br.readLine()) != null) {
                Cline++;
                st = new StringTokenizer(line);
                if (st.countTokens() < 2) {
                    System.out.print("Error in line : " + String.valueOf(Cline));
                }
                else {
                    gene1 = st.nextToken();
                    gene2 = st.nextToken();
                    if ((!this.lookuptb.containsKey(gene1)) && lookuptb.containsKey(gene1)) {
                        this.lookuptb.put(gene1, ++j);
                        this.lookuptb2.put(j, gene1);
                    }
                    if ((!this.lookuptb.containsKey(gene2)) &&lookuptb.containsKey(gene2)) {
                        this.lookuptb.put(gene2, ++j);
                        this.lookuptb2.put(j, gene2);
                    }
                }
            }
            fstream.close();double w;Interaction i;
            sizeOfProteins = lookuptb.size(); //????? ? /???? ?????????
            mat = new double[sizeOfProteins][sizeOfProteins];
            fstream = new FileInputStream(ppi_path);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while ((line = br.readLine()) != null) {
                st = new StringTokenizer(line);
                gene1 = st.nextToken();
                gene2 = st.nextToken();
                if (st.hasMoreTokens())
                    w = Double.parseDouble(st.nextToken());
                else
                    w = 1;
                if(this.lookuptb.containsKey(gene1)&& this.lookuptb.containsKey(gene2)){
                    mat[this.lookuptb.get(gene1)][this.lookuptb.get(gene2)] += w;
                    mat[this.lookuptb.get(gene2)][this.lookuptb.get(gene1)] += w;
                    i = new Interaction(gene1,gene2,w);
                    interactions.add(i);
                }
            }
            fstream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
    public double Expression_index(String gene, int column){
        return (mat[lookuptb.get(gene)][column]);
    }

    public  void ReadFromBiclusterSolution(Solution solution, Matrix geneExpressionData) {
        Binary binSolution = (Binary) solution.getDecisionVariables()[0].deepCopy();
        ArrayList<Integer> validgIndex = new ArrayList<Integer>();
        ArrayList<Integer> validcIndex = new ArrayList<Integer>();

        for(int i=0;i<geneExpressionData.sizeOfProteins;i++) {
            if ( binSolution.bits_.get(i) ) validgIndex.add(i);
        }

        String gene1, gene2;
        int p = -1;
        Interaction Inctn;
        mat = new double[validgIndex.size()][validgIndex.size()];
        for (int i = 0; i < geneExpressionData.sizeOfProteins - 1; i++) {
            if (binSolution.bits_.get(i)) {
                for (int j = 0; j < geneExpressionData.sizeOfProteins; j++) {
                    if (binSolution.bits_.get(j)) {
                        gene1 = geneExpressionData.lookuptb2.get(i);
                        gene2 = geneExpressionData.lookuptb2.get(j);
                        if (!lookuptb.containsKey(gene1)) {
                            lookuptb.put(gene1, ++p);
                            lookuptb2.put(p, gene1);
                        }
                        if (!lookuptb.containsKey(gene2)) {
                            lookuptb.put(gene2, ++p);
                            lookuptb2.put(p, gene2);
                        }
                        Inctn = new Interaction(gene1, gene2,1);
                        interactions.add(Inctn);
                        mat[lookuptb.get(gene1)][lookuptb.get(gene2)] = 1;
                    }
                }
            }
        }
    }

    public void read_Gene_Expression_Descretized(String path) {
        FileInputStream fstream = null;
        try {
            int conditions=0;
            fstream = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line,gene;
            StringTokenizer st;
            int j=-1;
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                st = new StringTokenizer(line);
                gene = st.nextToken();
                conditions = st.countTokens();
                if(!lookuptb.containsKey(gene)) {
                    lookuptb.put(gene, ++j);
                    lookuptb2.put(j, gene);
                }
            }
            fstream.close();
            fstream = new FileInputStream(path);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            sizeOfProteins = lookuptb.size();
            sizeOfConditions = conditions;
            dmat = new char[sizeOfProteins][sizeOfConditions];
//            CorrMat = new double[sizeOfProteins][sizeOfProteins];
//            mean = new double[sizeOfProteins];
//            var = new double[sizeOfProteins];
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                conditions=0;
                st = new StringTokenizer(line);
                gene = st.nextToken();
                while (st.hasMoreTokens()) {
                    dmat[lookuptb.get(gene)][conditions++] = st.nextToken().charAt(0);
                }
            }
            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void Discretize(String path, double gamma) throws IOException {
        Preprocessor preprocessor = new Preprocessor();
        File file = new File(path);
        try {
            preprocessor.readMainDataFile(file,1,1);
            PreprocessOption preprocessOption = new PreprocessOption();//default  preprocess options
            preprocessOption.do_compute_logarithm =false;
            preprocessOption.do_normalize = true;
            preprocessOption.do_normalize_genes =true;
            //preprocessOption.do_normalize_chips = true;
            preprocessOption.do_discretize = true;
//            preprocessOption.discretizationMode = "threshold";
            preprocessOption.discretizationMode = "nominal";
//            double alpha = .6;
//            preprocessOption.discretizationMode = "nominal";
//            preprocessOption.discretizationMode = "onesPercentage";
//            preprocessOption.discretizationScheme = 7;//PREPROCESS_OPTIONS_DISCRETIZATION_GENE_MID_RANGE
            preprocessOption.discretizationScheme = 8;//REPROCESS_OPTIONS_DISCRETIZATION_GENE_EXPRESSION_MEAN_STD
//            preprocessOption.discretizationScheme = 6;//PREPROCESS_OPTIONS_DISCRETIZATION_GENE_EXPRESSION_AVERAGE
//            preprocessOption.discretizationScheme = 10;//PREPROCESS_OPTIONS_DISCRETIZATION_VARIATION_BETWEEN_TIME_POINTS_TWO_SYMBOLS
//            preprocessOption.discretizationScheme = 11 ;//PREPROCESS_OPTIONS_DISCRETIZATION_VARIATION_BETWEEN_TIME_POINTS_THREE_SYMBOLS
//            preprocessOption.discretizationScheme = 12 ;//PREPROCESS_OPTIONS_DISCRETIZATION_VARIATION_BETWEEN_TIME_POINTS_THREE_SYMBOLS_NEW
            //preprocessOption.discretizationScheme = 13; //PREPROCESS_OPTIONS_DISCRETIZATION_By_Active_Threshold
            preprocessor.preprocessData(preprocessOption, gamma);
            int[][] discreteData = preprocessor.getDiscreteData();
            FileOutputStream fstream = null;
            DataOutputStream out;
            BufferedWriter bw;
            fstream = new FileOutputStream(path + "_d.txt");
            out = new DataOutputStream(fstream);
            bw = new BufferedWriter(new OutputStreamWriter(out));
            bw.newLine();char ch;
            for(int i=0;i<discreteData.length;i++) {
                bw.write(preprocessor.geneNames[i]+"\t");
                for (int j = 0; j < discreteData[0].length; j++) {
                    if(discreteData[i][j]==1)
                        ch = '1';
                    else
                        ch = '0';
                    bw.write(ch+"\t");

                }
                bw.write("\n");
            }
            bw.close();out.close();
        } catch (FileOffsetException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
