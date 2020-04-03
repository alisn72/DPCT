package bicat.run_machine;

import bicat.gui.BicatGui;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedList;

/**
 * <p>
 * Title: BicAT Tool
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * @author Amela Prelic
 * @version 1.0
 * 
 * Run Machine that is the super class of specific RunMachine's managing
 * different algorithms integrated into BicAT.
 * 
 */

public class RunMachine {

	public static int HCL_ID = 0;

	public static int KMEANS_ID = 1;

	public static int BIMAX_ID = 0;

	public static int ISA_ID = 1;

	public static int CC_ID = 2;

	public static int XMOTIF_ID = 3;

	public static int OPSM_ID = 4;

	// this super-class contains the fields and methods common to different
	// Run-Machines (BiMax, CC, ISA, xMotif,OPSM)
	// ===========================================================================
	public RunMachine() {
	}

	// fields ...

	static BicatGui owner;

	int nrGenes = -1;

	int nrChips = -1;

	public LinkedList computedBiclusters; // "raw" biclusters computed by algorithm.

	LinkedList outputBiclusters; // transferred from bimax... run.

	boolean cullDegenerate; // remove the "line" BCs?

	// ===========================================================================
	/**
	 * 
	 * Retrieve the list of <code>bimax.Bicluster</code> objects computed by
	 * <code>bimax.Bimax</code> to the local representation,
	 * <code>bicat.biclustering.Bicluster_Bitset</code> objects. <br>
	 * 
	 * The transferred list is automatically sorted (by Size).
	 * <p>
	 * 
	 */
	void transferList() {

		// diese BC typ ist auch generell(er) ...
		bicat.run_machine.Bicluster_bitset bIn; // Bicluster object from bcs
		// list that will be made into a
		// Bicluster
		bicat.biclustering.Bicluster bOut; // new Bicluster object that will be
		// added to the outputBiclusters
		// list

		int[] genes, chips; // aux. arrays for gene sets and chips sets of a bc

		outputBiclusters = new LinkedList();

		for (int i = 0; i < computedBiclusters.size(); i++) {

			bIn = (bicat.run_machine.Bicluster_bitset) computedBiclusters
					.get(i);
			genes = bIn.getGenes();
			chips = bIn.getChips();

			if (cullDegenerate)
				if ((genes.length <= 1) || (chips.length <= 1))
					continue;
			
			bOut = new bicat.biclustering.Bicluster(i, genes, chips);
			outputBiclusters.add(bOut);
			// System.out.println("outputBiclusters:
			// "+outputBiclusters.toString());
		}

		// SORT works?
		Collections.sort(outputBiclusters);
	}

	// ===========================================================================
	LinkedList getOutputBiclusters(String file) {

		try {
			FileReader fr = new FileReader(file);
			LineNumberReader lnr = new LineNumberReader(fr);

			String s = lnr.readLine();

			if (s == null)
				return new LinkedList();
			if (null != s && 0 == s.length())
				return new LinkedList(); // special case, when no BCs are
			// returned.

			String[] fragments = s.split(" ", -1);
			BitSet genes = convertIntoBitSet(fragments); // new BitSet();

			s = lnr.readLine();
			fragments = s.split(" ", -1);
			BitSet chips = convertIntoBitSet(fragments);

			LinkedList biclusters = new LinkedList();

			Bicluster_bitset bc = new Bicluster_bitset(genes, chips);
			biclusters.add(bc);

			s = lnr.readLine();
			boolean is_gene = true;

			while ((null != s) && (0 != s.length())) {
				fragments = s.split(" ", -1);
				if (is_gene) {
					genes = convertIntoBitSet(fragments);
					is_gene = false;
				} else {
					chips = convertIntoBitSet(fragments);
					is_gene = true;

					bc = new Bicluster_bitset(genes, chips);
					biclusters.add(bc);
				}
				s = lnr.readLine();
			}
			return biclusters;
		} catch (FileNotFoundException e) {
			System.err.println(e);
		} // System.exit(1);
		catch (IOException e) {
			System.err.println(e);
		} // System.exit(1); }

		return null;
	}

	// ===========================================================================
	BitSet convertIntoBitSet(String[] bits) {

		BitSet r = new BitSet(bits.length);
		for (int i = 0; i < bits.length; i++) {
			int v = new Integer(bits[i]).intValue();
			if (1 == v)
				r.set(i);
		}

		return r;
	}

}