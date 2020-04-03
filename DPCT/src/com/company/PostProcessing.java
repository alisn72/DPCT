//Created by Ali SabziNezhad
package com.company;

import jmetal.core.Matrix;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.company.Main.Evaluate;

public class PostProcessing {

    public static Matrix static_PPI;
    private static double OS = 0.25;
    private String p;

    public static void postProcess(Matrix GE, Matrix ppi, ArrayList<ArrayList<String>> pred) throws IOException {
        Matrix geneExpressionData1 = GE;
        static_PPI = ppi;
        ArrayList<ArrayList<String>> bench = new ArrayList<ArrayList<String>>();
        Main.Generate_clear_CYC2008(bench, static_PPI.lookuptb);
        ArrayList<ArrayList<String>> final_complex = new ArrayList<ArrayList<String>>();


        for (int i = 0; i < pred.size(); i++) {
            ArrayList<String> t = pred.get(i);
            if (t.size() < 5) {
                final_complex.add(t);
                pred.remove(t);
            }
        }

        ArrayList<ArrayList<ArrayList<String>>> workArea = new ArrayList<ArrayList<ArrayList<String>>>();
        while (pred.size() >0) {
            ArrayList<String> t = pred.get(0);
            if (isUnique(t, pred)) {
                final_complex.add(t);
                pred.remove(t);
            } else {
                ArrayList<ArrayList<String>> temp = getAllSimilar(t, pred);
                workArea.add(temp);
                for (int j = 0; j<temp.size(); j++){
                    pred.remove(temp.get(j));
                }
            }
        }

        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < workArea.size(); i++) {
            temp = workArea.get(i);
            ArrayList<String> derived = new ArrayList<String>();
            ArrayList<String> all = new ArrayList<String>();
            getAllProteinsOfSet(temp, all);
            for (int j = 0; j < all.size(); j++) {
                String p = all.get(j);
                if (isAllComplexesHasProtein(temp, p)) {
                    derived.add(p);
                    removeProteinFromSet(temp, p);
                }
            }
            for (int k = 0; k < temp.size(); k++) {
                if (temp.get(k).size() == 0) temp.remove(k);
            }

            //****************************************************************  R2
            if (temp.size() > 0) {
                all = new ArrayList<String>();
                getAllProteinsOfSet(temp, all);
                ArrayList<String> proteins = new ArrayList<>();
                for (int j = 0; j < all.size(); j++) {
                    String p = all.get(j);
                    int count = 0;

                    for (int k = 0; k < temp.size(); k++) {
                        if (temp.get(k).contains(p)) {
                            count++;
                        }
                    }
                    if (count >= temp.size() / 2) {
                        proteins.add(p);
                    }
                }
                addGroupToComplex(derived, proteins, static_PPI);
                for (int k = 0; k < proteins.size(); k++)
                    removeProteinFromSet(temp, proteins.get(k));
                for (int k = 0; k < temp.size(); k++)
                    if (temp.get(k).size() == 0) temp.remove(k);

                //***************************************************************************************** R3
                if (temp.size() > 0) {
                    ArrayList<ArrayList<String>> r3derived = new ArrayList<ArrayList<String>>();
                    for (int j = 0; j < temp.size(); j++) {
                        ArrayList<String> tt = (ArrayList<String>) derived.clone();
                        tt.addAll(temp.get(j));

                        double w1 = (double) Wdensity(derived, static_PPI);
                        double w3 = (double) Wdensity(tt, static_PPI);
                        if (w3 > w1) {
                            r3derived.add(tt);
                        }
                    }

                    final_complex.add(derived);
                    final_complex.addAll(r3derived);
                }

            }
            temp = new ArrayList<ArrayList<String>>();
        }
        System.out.println("Final Results:");
        Evaluate(final_complex, bench, OS);

    }

    private static boolean isUnique(ArrayList<String> complex, ArrayList<ArrayList<String>> complexArray) throws IOException {
        double th = 0.0;
        int a = complex.size() - 1;
        int bb = complex.size() + 1;
        th = 0.8;//(double) a / bb;
        for (int i = 0; i < complexArray.size(); i++) {
            ArrayList<String> b = complexArray.get(i);
            if (b.size() > 5) {
                if (Main.OScore2(b, complex) >= th && !b.equals(complex)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static ArrayList<ArrayList<String>> getAllSimilar(ArrayList<String> complex, ArrayList<ArrayList<String>> complexArray) throws IOException {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        double th = 0.0;
        int a = complex.size() - 1;
        int bb = complex.size() + 1;
        th = 0.8;//(double) a / bb;
        for (int i = 0; i < complexArray.size(); i++) {
            ArrayList<String> b = complexArray.get(i);
            if (Main.OScore2(b, complex) >= th && !b.equals(complex)) {
                res.add(b);
//                complex.remove(b);
            }
        }
        return res;
    }

    private static boolean isSimilar(ArrayList<String> c1, ArrayList<String> c2) {
        double th = 0.0;
        int a = c1.size() - 1;
        int bb = c2.size() + 1;
        th = (double) a / bb;
        if (Main.OScore2(c1, c2) >= th) {
            return true;
        }
        return false;
    }

    private static void sortComplexList(ArrayList<ArrayList<String>> c) {
        for (int j = 0; j < c.size(); j++) {
            Collections.sort(c.get(j), String.CASE_INSENSITIVE_ORDER);
        }
        Collections.sort(c, new Comparator<List<String>>() {
            @Override
            public int compare(List<String> a, List<String> b) {
                return a.get(1).compareTo(b.get(1));
            }
        });
    }

    public static void EvaluateByConplexSize(ArrayList<ArrayList<String>> pred, ArrayList<ArrayList<String>> bench) {
        for (int size = 3; size < 12; size++) {
            ArrayList<ArrayList<String>> predTmp = new ArrayList<ArrayList<String>>();
            for (int i = 0; i < pred.size(); i++) {
                ArrayList<String> t = pred.get(i);
                if (size == 11) {
                    if (t.size() > 10) {
                        predTmp.add(t);
                    }
                } else if (t.size() == size)
                    predTmp.add(t);
            }
            System.out.print((size == 11 ? ">10=C: " : size + "=\tC: ") + predTmp.size() + "\t");
            Evaluate(predTmp, bench, OS);
        }
    }

    public static boolean evaluateSingleComplex(ArrayList<String> c, ArrayList<ArrayList<String>> bench) {
        Float maxScore = new Float(0);
        int TPb = 0;
        for (int i = 0; i < bench.size(); i++) {
            Float t = Main.OScore2(bench.get(i), c);
            if (t >= OS) {
                TPb++;
                if (t > maxScore) maxScore = t;
            }
        }
        if (TPb == 0) {
            System.out.print("Not Matched\n");
            return false;
        } else {
            System.out.print("Matched with: " + TPb + "\t MaxScore: " + String.format("%.2f", maxScore) + "\n");
            return true;
        }
    }

    private static void getAllProteinsOfSet(ArrayList<ArrayList<String>> pred, ArrayList<String> result) {
        for (int j = 0; j < pred.size(); j++) {
            ArrayList<String> t = pred.get(j);
            for (int k = 0; k < t.size(); k++) {
                String p = t.get(k);
                if (!result.contains(p)) {
                    result.add(p);
                }
            }
        }
    }

    private static boolean isAllComplexesHasProtein(ArrayList<ArrayList<String>> pred, String p) {
        for (int k = 0; k < pred.size(); k++) {
            if (!pred.get(k).contains(p)) {
                return false;
            }
        }
        return true;
    }

    private static void removeProteinFromSet(ArrayList<ArrayList<String>> pred, String p) {
        for (int k = 0; k < pred.size(); k++) {
            pred.get(k).remove(p);
        }
    }

    public static double Wdensity(ArrayList<String> temp, Matrix m) {
        if (m == null) {
            m = static_PPI;
        }
        double wden = 0;
        int c = 0, a, b;
        for (int i = 0; i < temp.size(); i++)
            for (int j = i + 1; j < temp.size(); j++) {
                a = m.lookuptb.get(temp.get(i));
                b = m.lookuptb.get(temp.get(j));
                if (m.mat[a][b] > 0) {
                    wden += m.mat[m.lookuptb.get(temp.get(i))][m.lookuptb.get(temp.get(j))];
                    c++;
                }
            }
        return c > 0 ? ((2 * wden) / (temp.size() * (temp.size() - 1))) : 0;
    }

    private static int getCountOfConnection(ArrayList<String> complex, String p, Matrix m) {
        int count = 0, a, b;
        b = m.lookuptb.get(p);
        for (int i = 0; i < complex.size(); i++) {
            a = m.lookuptb.get(complex.get(i));
            if (m.mat[a][b] > 0) {
                count++;
            }
        }
        return count;
    }

    private static void addGroupToComplex(ArrayList<String> complex, ArrayList<String> proteins, Matrix m) {
        complex.addAll(proteins);
        for (int i = 0; i < proteins.size(); i++) {
            String p = proteins.get(i);
            complex.remove(p);
        }
    }
}
