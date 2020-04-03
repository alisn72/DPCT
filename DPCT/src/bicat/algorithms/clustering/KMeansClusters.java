package bicat.algorithms.clustering;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 **/

public class KMeansClusters {

  int[] geneClusters;   // nr.genes -> assignment (\in \{1,...,k\})
  float[][] centroids;  // K, or weniger

  int k = 0;
  double sumValue = 0;

  // ===========================================================================
  public KMeansClusters() { }

  public KMeansClusters(int[] g2l, float[][] cs, double sumV, int currK) {
    geneClusters = new int[g2l.length];
    centroids = new float[cs.length][cs[0].length];
    for(int i = 0; i < g2l.length; i++) geneClusters[i] = g2l[i];
    for(int i = 0; i < cs.length; i++)
      for(int j = 0; j < cs[0].length; j++) centroids[i][j] = cs[i][j];
    sumValue = sumV;
    k = currK;
  }

  // ===========================================================================
  public boolean betterThan(KMeansClusters k) { return (sumValue < k.getSumValue()); }
  public double getSumValue() { return sumValue; }

  public boolean equalsTo(KMeansClusters k) {
    int[] kGeneClusters = k.getClusters();
    for(int i = 0; i < geneClusters.length; i++)
      if(geneClusters[i] != kGeneClusters[i]) return false;
    return true;
  }

  public int getNumberClusters() { return k; }

  // ===========================================================================
  public int[] getClusters() { return geneClusters; }
  public float[][] getCentroids() { return centroids; }

}
