package bicat.run_machine;

import bicat.preprocessor.PreprocessOption;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 * Super-class for the arguments-passing mechanism within BicAT tool.
 *
 * A standard Arguments class contains necessarily the data matrix, as well as the
 * information about to which dataset this data matrix belongs to.
 *
 **/

public class Arguments {

  // ===========================================================================
  public Arguments() {  }

  // ===========================================================================
  double[] data;

  int total_genes;
  int total_chips;

  int seed;
  int logarithm;

  String outputFileName;

  int datasetIdx;
  PreprocessOption preOpts;

  // ===========================================================================
  public void setData(double[] d) {
    data = new double[d.length];
    for(int i = 0; i< d.length; i++) data[i] = d[i];
  }

  public void setGeneNumber(int tg) { total_genes = tg; }
  public void setChipNumber(int tc) { total_chips = tc; }

  public void setSeed(int s) { seed = s; }
  public void setLogarithm(int l) { logarithm = l; }

  public void setOutputFile(String of) { outputFileName = of; }

  public void setDatasetIdx(int idx) { datasetIdx = idx; }
  public void setPreprocessOptions(PreprocessOption po) { preOpts = po; }

  // ===========================================================================
  public double[] getData() { return data; }

  public int getGeneNumber() { return total_genes; }
  public int getChipNumber() { return total_chips; }

  public int getSeed() { return seed; }
  public int getLogarithm() { return logarithm; }

  public String getOutputFile() { return outputFileName; }

  public int getDatasetIdx() { return datasetIdx; }
  public PreprocessOption getPreprocessOptions() { return preOpts; }

}