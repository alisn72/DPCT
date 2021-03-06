package bicat.biclustering;

import java.util.BitSet;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 **/

public class Bicluster implements Comparable {

  /** States that the <code>Bicluster</code> in question consists of ones. */
  public static boolean ONES = true;

  /** States that the <code>Bicluster</code> in question consists of zeroes. */
  public static boolean ZEROES = false;

  /** Unique ID number. */
  public int id;

  /** Either <code>ONES</code> or <code>ZEROES</code> */
  boolean type;

  /** Array containing the indices of all genes that are contained in the bicluster. */
  public int[] genes;

  /** Array containing the indices of all chips that are contained in the bicluster. */
  public int[] chips;

  /** Size of the bicluster (number of genes multiplied by number of chips). */
  public int size;

  /** Mean square residual score of biclusters. */
  float msrs;

  public int error = 0; // number of zero cells in a BC

  public int gd;
  public int cd;


  // ===========================================================================
  public Bicluster() {}

  /**
   * Default constructor, initializes all values.
   *
   * The only thing that is calculated is the size (dimensions, 29.04.04),
   * which is obtained as a product of the gene and chip counts.
   *
   * @param identification unique ID number that is to be assigned to the bicluster
   * @param g array of indices of genes contained in bicluster
   * @param c array of indices of chips contained in bicluster
   * @param rawData ...
   * @param binData ...
   *
   * */
  public Bicluster(int identification, int[] g, int[] c, float[][] rawData,
                   int[][] binData) {
    id = identification;
    genes = g;
    chips = c;

    //  print_arr(g);
    //  print_arr(c);

    // new:
    gd = g.length;
    cd = c.length;

    size = gd * cd;

    // find out whether bicluster consists of one or zero values
    type = (0 == binData[genes[0]][chips[0]]) ? ZEROES : ONES;

    // compute mean squared residue score of this BC
    float sum;
    float bigSum = 0; // normed sum of all values in BC
    float[] normedGeneRows = new float[gd]; // sums of each row of genes
    float[] normedChipRows = new float[cd]; // sums of each row of chips

    // get sums of each gene row in BC
    for (int k = 0; k < gd; k++) {
      sum = 0;
      for (int j = 0; j < cd; j++)
        sum += rawData[genes[k]][chips[j]];
      normedGeneRows[k] = (float) (sum / cd);
      bigSum += sum;
    }

    bigSum = (bigSum / gd); // compute normalized total sum of all values in BC

    // get sums of each chip row in BC
    for (int k = 0; k < cd; k++) {
      sum = 0;
      for (int j = 0; j < gd; j++)
        sum += rawData[genes[j]][chips[k]];
      normedChipRows[k] = (float) (sum / gd);
    }

    sum = 0;
    msrs = 0;

    // finally, compute the MSRS
    for (int k = 0; k < gd; k++) {
      for (int j = 0; j < cd; j++)
        sum += (rawData[genes[k]][chips[j]] + normedGeneRows[k] + normedChipRows[j] + bigSum);
      msrs += (sum*sum);
    }
    msrs = (msrs / (gd + cd)); //????
  }

  // ===========================================================================
  /**
   * Default constructor, initializes all values.
   *
   * The only thing that is calculated is the size (dimensions, 29.04.04),
   * which is obtained as a product of the gene and chip counts.
   *
   * @param identification unique ID number that is to be assigned to the bicluster
   * @param g array of indices of genes contained in bicluster
   * @param c array of indices of chips contained in bicluster
   *
   * */
  public Bicluster(int identification, int[] g, int[] c) {

    id = identification;
    genes = g;
    chips = c;

    gd = g.length;
    cd = c.length;

    size = gd * cd;

    msrs = 0;

  }

  // ===========================================================================
  public Bicluster(int identification, int[] g, int[] c, float[][] rawData,
                   int[][] binData, boolean extended) { // true: extended SAMBA version!
    id = identification;
    genes = g;
    chips = c;

    // new:
    gd = g.length;
    cd = c.length;
    size = gd * cd;

    msrs = 0;
  }

  // ===========================================================================
  // ===========================================================================
  // ===========================================================================

  public int compareTo(Object o) {
    Bicluster other = (Bicluster)o;
    if(gd*cd > other.gd*other.cd) return -1;
    else if(gd*cd < other.gd*other.cd) return 1;
    else return 0;
  }


  // ===========================================================================
  public void addToError(int err) {
    error += err;
  }

  static BitSet gene;
  static BitSet chip;

  public void updateGene(int g_idx) {
    gene.set(g_idx);
    genes = null;
    genes = new int[gene.cardinality()];
    int i = 0;
    int k = 0;
    for (i = gene.nextSetBit(i); i >= 0; i = gene.nextSetBit(i + 1)) {
      genes[k] = i;
      k++;
    }
    gd = genes.length;
  }

  public void updateChip(int c_idx) {
    chip.set(c_idx);
    chips = null;
    chips = new int[chip.cardinality()];
    int i = 0;
    int k = 0;
    for(i = chip.nextSetBit(i); i>=0; i=chip.nextSetBit(i+1)) {
      chips[k] = i; k++;
    }
    cd = chips.length;
  }


  // ===========================================================================
  /**
   * Checks if this <code>Bicluster</code> is degenerate.
   *
   * A bicluster is "degenerate" if it contains only one gene or only one chip.
   * If that is the case, it can be ignored.
   *
   * @return true if <code>Bicluster</code> is degenerate, false otherwise
   *
   * */
  public boolean isDegenerate() { return (gd == 1 || cd == 1); }

  // ===========================================================================
  /**
   * Gets the unique identification number of this <code>BicaGUI</code>.
   *
   * @return the unique ID number of this <code>Bicluster</code>
   *
   * */
  public int getId() { return id; }

  // ===========================================================================
  /**
   * Gets array containing indices of genes that are contained in this
   * <code>Bicluster</code>.
   *
   * @return array containing indicies of genes containing in this
   *         <code>Bicluster</code>
   *
   * */
  public int[] getGenes() { return genes; }
  public int getGenesSize() { return genes.length; }

  // ===========================================================================
  /**
   * Gets array containing indices of chips that are contained in this
   * <code>Bicluster</code>.
   *
   * @return array containing indicies of chips containing in this
   *         <code>Bicluster</code>
   *
   * */
  public int[] getChips() { return chips; }
  public int getChipsSize() { return chips.length; }

  // ===========================================================================
  /**
   * Gets the size (number of genes times number of chips) of this
   * <code>Bicluster<code>.
   *
   * @return the size of this <code>Bicluster</code>
   *
   *  */
  public int getSize() { return size; }

  // ===========================================================================
  public int[] getDimension() {
    int[] arr = new int[2];
    arr[0] = gd;
    arr[1] = cd;
    return arr;
  }

  // ===========================================================================
  /**
   * Gets the mean square residual score (MSRS) of this <code>Bicluster</code>.
   *
   * @return the mean square residual score of this <code>Bicluster</code>
   *
   *  */
  public float getMSRS() { return msrs; }

  // ===========================================================================
  /**
   * Checks whether this <code>Bicluster</code> consists of ones or zeroes.
   *
   * @return ONES if <code>Bicluster</code> consists of ones, ZEROES otherwise
   * @see #ONES
   * @see #ZEROES
   *
   *  */
  public boolean getType() { return type; }

  // ===========================================================================
  /**
   * Gets a string representation of this <code>Bicluster</code>.
   *
   * Used in tree display of biclusters.
   *
   * @return a string that consists of the <code>Bicluster</code> identification, size and MSRS
   *
   *  */
  public String toString() {
    // return new String("ID: "+id+" size: "+size+" MSRS: "+msrs);
    return new String("ID: "+id+", size: "+size+" ("+genes.length+","+chips.length+")");
  }

  // ===========================================================================
  /**
   * Checks if this <code>Bicluster</code> is equal to another.
   *
   * Two <code>Bicluster</code> objects are defined to be equal if they contain
   * the excat same genes and chips. By extension, size and MSRS are also equal,
   * but the identification need not be the same. This method is used to cull
   * duplicate results from the list.
   *
   * @param rival <code>Bicluster</code> that this one has to be compared to
   * @return <code>true</code> if <code>Bicluster</code> are identical, <code>false</code> otherwise
   *
   * */
  public boolean isEqual(Bicluster rival) {
    if(rival.getSize() == size) {
      int geneCheck = 0; // number of genes that are in both BCs
      int chipCheck = 0; // number of chips that are in both BCs

      int[] rivalGenes = rival.getGenes();
      int[] rivalChips = rival.getChips();

      for(int i=0; i<rivalGenes.length; i++)
        for (int j=0; j<gd; j++)
          if (rivalGenes[i] == genes[j]) geneCheck++;

      for(int i=0; i<rivalChips.length; i++)
        for (int j=0; j<cd; j++)
          if (rivalChips[i] == chips[j]) chipCheck++;

      // if all genes and chips are contained in the other BC, they are equal
      // this assumes that Recursive_Bica does not return BCs that are contained in other BCs (ie. maximal BCS)
      if((geneCheck == gd) && (chipCheck == cd)) return true;
    }
    return false;
  }

}
