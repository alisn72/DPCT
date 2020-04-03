package bicat.run_machine;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 * Arguments for Cheng & Church (2000). CC-specific arguments are \delta, \alpha,
 * and n, the number of biclusters to compute.
 *
 *
 **/

public class ArgumentsCC extends Arguments {

  double delta;
  double alpha;

  int randomize;

  double p_rand_1;
  double p_rand_2;
  double p_rand_3;

  int n;

  // ===========================================================================
  public ArgumentsCC() { }

  // ===========================================================================
  public void setRandomize(int r) { randomize = r; }

  public void setDelta(double d) { delta = d; }
  public void setAlpha(double a) { alpha = a; }

  public void setPR1(double pr1) { p_rand_1 = pr1; }
  public void setPR2(double pr2) { p_rand_2 = pr2; }
  public void setPR3(double pr3) { p_rand_3 = pr3; }

  public void setN(int nn) { n = nn; }

  // ===========================================================================
  public int getRandomize() { return randomize; }

  public double getDelta() { return delta; }
  public double getAlpha() { return alpha; }

  public double getPR1() { return p_rand_1; }
  public double getPR2() { return p_rand_2; }
  public double getPR3() { return p_rand_3; }

  public int getN() { return n; }
}