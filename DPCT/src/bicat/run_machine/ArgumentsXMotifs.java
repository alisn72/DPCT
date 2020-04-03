package bicat.run_machine;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 * Arguments for XMotifs algorithm (2003).
 *
 **/

public class ArgumentsXMotifs extends Arguments {

  double max_p_value;
  double alpha;

  int n_s;
  int n_d;
  int s_d;

  int max_length;
  int max_size;

  // ===========================================================================
  public ArgumentsXMotifs() { }

  // ===========================================================================
  public double getAlpha() { return alpha; }
  public double getMaxPValue() { return max_p_value; }

  public int getND() { return n_d; }
  public int getNS() { return n_s; }
  public int getSD() { return s_d; }

  public int getMaxLength() { return max_length; }
  public int getMaxSize() { return max_size; }

  // ===========================================================================
  public void setAlpha(double a) { alpha = a; }
  public void setMaxPValue(double pv) {
		max_p_value = pv; }

  public void setNS(int ns) { n_s = ns; }
  public void setND(int nd) { n_d = nd; }
  public void setSD(int sd) { s_d = sd; }

  public void setMaxLength(int ml) { max_length = ml; }
  public void setMaxSize(int ms) { max_size = ms; }

}