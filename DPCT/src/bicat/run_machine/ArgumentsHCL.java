package bicat.run_machine;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 * Arguments for Hierarchical clustering algorithm. Linkage modus and the
 * distance metric to be employed, as well as n (the number of resulting clusters)
 * are user-specified.
 *
 **/

public class ArgumentsHCL extends Arguments {

  int what_linkage;
  int what_distance;

  int nr_clusters;

  float[][] myData;

  // ===========================================================================
  public ArgumentsHCL() { }

  // ===========================================================================
  public void setLinkage(int l)        { what_linkage = l; }
  public void setDistanceMetric(int d) { what_distance = d; }
  public void setNumberClusters(int n) { nr_clusters = n; }
  public void setMyData(float[][] d)   { myData = d; }

  // ===========================================================================
  public int getLinkage()        { return what_linkage; }
  public int getDistanceMetric() { return what_distance; }
  public int getNumberClusters() { return nr_clusters; }
  public float[][] getMyData()   { return myData; }

}