package bicat.algorithms.clustering;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 **/

public class Util {

  // ===========================================================================
  public Util() { }

  // ===========================================================================
  public static double computeEuclideanDistance(float[] X, float[] Y) {
    double euclid = 0.0;
    for(int i = 0; i< X.length; i++)
      euclid += Math.pow((X[i] - Y[i]),2.0);
    return Math.sqrt(euclid);
  }

  // ===========================================================================
  public static double computePearsonCorrelationDistance(float[] X, float[] Y) {

    double sum_X = 0.0;
    double sum_Y = 0.0;
    double sum_X_sqr = 0.0;
    double sum_Y_sqr = 0.0;
    double sum_product_XY = 0.0;

    for(int i = 0; i < X.length; i++) {
      sum_X += (double)X[i];
      sum_X_sqr += Math.pow((double)X[i], 2.0);
    }
    for(int i = 0; i < Y.length; i++) {
      sum_Y += (double)Y[i];
      sum_Y_sqr += Math.pow((double)Y[i], 2.0);
    }
    for(int i = 0; i < X.length; i++) sum_product_XY += (double)X[i]*(double)Y[i];

    return (double)1.0 - (sum_product_XY - sum_X*sum_Y/X.length) /
        Math.sqrt((sum_X_sqr - sum_X_sqr/X.length)*(sum_Y_sqr - sum_Y_sqr/Y.length));
  }

  // ===========================================================================
  // (oder City-Block)
  public static double computeManhattanDistance(float[] X, float[] Y) {
    double manhattan = 0.0;
    for(int i = 0; i< X.length; i++)
      manhattan += Math.abs((X[i] - Y[i]));
    return manhattan;
  }

  // ===========================================================================
  public static double computeCosineDistance(float[] X, float[] Y) {
    double sum_X_sqr = 0.0;
    double sum_Y_sqr = 0.0;
    double sum_product_XY = 0.0;

    for(int i = 0; i< X.length; i++) sum_X_sqr += Math.pow((double)X[i], 2.0);
    for(int i = 0; i< Y.length; i++) sum_Y_sqr += Math.pow((double)Y[i], 2.0);
    for(int i = 0; i< X.length; i++) sum_product_XY += X[i]*Y[i];

    return 1 - sum_product_XY / Math.sqrt(sum_X_sqr*sum_Y_sqr);
  }

  // ===========================================================================
  public static double computeMinkowskiDistance(float[] X, float[] Y) {
    double minkowski = 0.0;
    for(int i = 0; i< X.length; i++)
      minkowski += Math.pow(Math.abs(X[i] - Y[i]),2.0);
    return Math.sqrt(minkowski);
  }

  /* double computeHammingDistance(float[] X, float[] Y) {
     double hamming = 0.0;
     for(int i = 0; i< X.length; i++)
      hamming += Math.pow((X[i] - Y[i]),2.0);
     return Math.sqrt(hamming);
   } */

}