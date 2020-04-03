package bicat.run_machine;

import java.util.BitSet;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 * Data structure describing a bicluster. Essentially, set of genes and chips.
 *
 *
 **/

public class Bicluster_bitset implements Comparable {

  BitSet genes;
  BitSet chips;

  // new - optimization related
  BitSet geneStarsOrigins;
  BitSet chipStarsOrigins;

  // ===========================================================================
  public Bicluster_bitset() {  }

  // ===========================================================================
  public Bicluster_bitset(BitSet g, BitSet c) {
    genes = g;
    chips = c;
  }

  // ===========================================================================
  public Bicluster_bitset(BitSet g, BitSet c, BitSet gs, BitSet cs) {
    genes = g;
    chips = c;
    if (gs.cardinality() > 0) geneStarsOrigins = gs;
    if (cs.cardinality() > 0) chipStarsOrigins = cs;

    //??
    g = null;
    c = null;
    gs = null;
    cs = null;
  }

  // ===========================================================================
  public int compareTo(Object o) {

    if(genes.cardinality()*chips.cardinality() > ((Bicluster_bitset)o).genes.cardinality()*((Bicluster_bitset)o).chips.cardinality())
      return -1;
    else if(genes.cardinality()*chips.cardinality() > ((Bicluster_bitset)o).genes.cardinality()*((Bicluster_bitset)o).chips.cardinality())
      return 1;
    else return 0;
  }

  // ===========================================================================
  BitSet getGenesBitSet() { return genes; }

  // ===========================================================================
  BitSet getChipsBitSet() { return chips; }

  // ===========================================================================
  public int[] getGenes() {
    int card = genes.cardinality();
    int[] arr = new int[card];
    int idx = 0;
    for(int i=genes.nextSetBit(0); i>=0; i=genes.nextSetBit(i+1))
      if (genes.get(i)) {
        arr[idx] = i;
        idx++;
      }
    return arr;
  }

  // ===========================================================================
  public int[] getChips() {
    int card = chips.cardinality();
    int[] arr = new int[card];
    int idx = 0;
    for(int i=chips.nextSetBit(0); i>=0; i=chips.nextSetBit(i+1))
      if (chips.get(i)) {
        arr[idx] = i;
        idx++;
      }
    return arr;
  }

  // ====
  public int get_gene_dim() { return genes.cardinality(); }
  public int get_chip_dim() { return chips.cardinality(); }

  // ===========================================================================
  BitSet getGeneStarsOrigins() { return geneStarsOrigins; }

  // ===========================================================================
  BitSet getChipStarsOrigins() { return chipStarsOrigins; }

  // ===========================================================================
  public void printOut() {
    System.out.println("BC "+genes.cardinality()+" "+chips.cardinality());
    for (int i = genes.nextSetBit(0); i>=0; i=genes.nextSetBit(i+1))
      System.out.print((i+1)+" ");
    System.out.println();
    for (int i = chips.nextSetBit(0); i>=0; i=chips.nextSetBit(i+1))
      System.out.print((i+1)+" ");
    System.out.println();
  }

}
