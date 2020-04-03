package bicat.run_machine;

import bicat.gui.BicatGui;

import javax.swing.*;

/**
 * <p>
 * Title: BicAT Tool
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @author Simon Barkow
 * @version 1.0
 * 
 * Run Machine for (native) bimax algorithm (2000).
 * 
 * For its execution, a shared library is needed (lib/linux/libcc.so for Linux).
 * 
 */

public class RunMachine_BiMax extends RunMachine {

	// ===========================================================================
	public RunMachine_BiMax(BicatGui o) {
		owner = o;
	}

	// ===========================================================================
	// public native void displayHelloWorld(); // Native Testmethode

	public native void run_bimax(int[] data, int total_gs, int total_cs,
			int minGenes, int minChips);

	// get the results from a .temp file in the current directory (this file
	// will be removed)

	static {
		//System.loadLibrary("bimax");
        System.load("E:\\Dr jalili\\paper 3\\paper3.2\\src\\bicat\\bimax.dll");
		System.out.println("bimax library loaded");
	}

	public synchronized void runBiclustering(final ArgumentsBiMax bima) {

		// Before calling the Native Method (bimax), prepare the calling
		// arguments
		// the parameters needed for bimax can be retrieved by the owner:

		// =======================================================================

		// Sa SwingWorker of BiMax ??
		// start bimax in a separate thread

		final SwingWorker worker = new SwingWorker() {

			public Object construct() {
				if (BicatGui.debug)
					System.out.println("Number of genes: "
							+ bima.getGeneNumber() + " number of Chips : "
							+ bima.getChipNumber() + " MinGenes: " + bima.getMinGenes()+" MinChips: "+ bima.getMinChips());
				run_bimax(bima.getBinaryDataAsVector(), bima.getGeneNumber(),
						bima.getChipNumber(), bima.getMinGenes(), bima
								.getMinChips());

				if (owner.debug) {
					System.out.println("Started bimax in a new thread!\n");
				}

				return null;
			}

			public void finished() {

				System.out.println("Getting results from bimax... "
						+ owner.currentDirectoryPath + "/bimax.out");

				computedBiclusters = getOutputBiclusters(owner.currentDirectoryPath
						+ "/bimax.out");

				outputBiclusters = computedBiclusters;
				// is a LinkedList of
				// BitSet
				// representated-BCs
				// (bicat.runMachine.Bicluster_bitset)

				transferList();

				owner.finishUpRun(bima.getDatasetIdx(), outputBiclusters, bima
						.getPreprocessOptions(), "BiMax");
				JOptionPane
						.showMessageDialog(
								null,
								"Calculations finished.\nThe results can be found in the bicluster results section of the current dataset.");

			}
		};
		worker.start();
	}
}
