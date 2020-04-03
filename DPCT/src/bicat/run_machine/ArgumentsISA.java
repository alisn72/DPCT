package bicat.run_machine;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 * Arguments for Iterative Signature Algorithm (2002). Arguments specific for ISA
 * are the gene and chip similarity thresholds, \t_g and \t_c.
 *
 **/

public class ArgumentsISA extends Arguments {

  int n_fix;
  int max_size;

  double t_g;
  double t_c;

  // ===========================================================================
  public ArgumentsISA() {
  }

  // ===========================================================================
  public double getTG() { return t_g; }
  public double getTC() { return t_c; }

  public int getNFix() { return n_fix; }
  public int getMaxSize() { return max_size; }

  // ===========================================================================
  public void setTG(double v) { t_g = v; }
  public void setTC(double v) { t_c = v; }

  public void setNFix(int v) { n_fix = v; }
  public void setMaxSize(int v) { max_size = v; }
}