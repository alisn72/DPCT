package bicat.util;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 **/

public class PreUtil {

  // ===========================================================================
  public PreUtil() { }

  // ===========================================================================
  public static float computeVariance(float[] data) {
      double var2 = 0;
    for(int i=0; i<data.length; i++)
      var2 += Math.pow(data[i],(double)2);

    return (float)Math.sqrt(var2/data.length);
  }
    public static float computeVariance(double[] data) {
        double var2 = 0;
        for(int i=0; i<data.length; i++)
            var2 += Math.pow(data[i],(double)2);

        return (float)Math.sqrt(var2/data.length);
    }
}
