package bicat.run_machine;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 * Arguments for K-Means clustering algorithm. The distance metric to be employed,
 * as well as n (the number of resulting clusters) are user-specified.
 *
 * Additionally, the starting centroids modus also need to be specified.
 *
 **/

public class ArgumentsKMeans extends Arguments {

  int what_distance;
  int start_list;
  int empty_action;

  int nr_max_iterations;
  int nr_replicates;

  int nr_clusters;

  float[][] myData;

  // ===========================================================================
  public ArgumentsKMeans() { }

  // ===========================================================================
  public void setDistanceMetric(int d)      { what_distance = d; }
  public void setStartList(int s)           { start_list = s; }
  public void setEmptyAction(int e)         { empty_action = e; }
  public void setNumberMaxIterations(int n) { nr_max_iterations = n; }
  public void setNumberReplicates(int n)    { nr_replicates = n; }
  public void setNumberClusters(int n)      { nr_clusters = n; }
  public void setMyData(float[][] d)        { myData = d; }

  // ===========================================================================
  public int getDistanceMetric()      { return what_distance; }
  public int getStartList()           { return start_list; }
  public int getEmptyAction()         { return empty_action; }
  public int getNumberMaxIterations() { return nr_max_iterations; }
  public int getNumberReplicates()    { return nr_replicates; }
  public int getNumberClusters()      { return nr_clusters; }
  public float[][] getMyData()        { return myData; }

}