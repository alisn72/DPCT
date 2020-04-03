package bicat.run_machine;

import bicat.gui.BicatGui;

import javax.swing.*;

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
 * Run Machine for Cheng & Church algorithm (2000).
 * 
 * For its execution, a shared library is needed (lib/linux/libcc.so for Linux).
 * 
 */

public class RunMachine_CC extends RunMachine {

	// ===========================================================================
	// public RunMachine_CC() { }
	public RunMachine_CC(BicatGui o) {
		owner = o;
	}

	// ===========================================================================
	// public native void displayHelloWorld(); // Native Testmethode

	// public native void runCC();
	public native void run_CC(double[] data, int total_gs, int total_cs,
	// String[] g_names, String[] c_names,
			int seed, double delta, double alpha, double p_rand_1,
			double p_rand_2, double p_rand_3, int logarithm, int randomize,
			int n, String out_file);

	// get the results from a .temp file in the current directory (this file
	// will be removed)
	// public native Object[] get_biclusters_CC();

	static {
		System.loadLibrary("cc");
		System.out.println("cc library loaded");
	}

	public synchronized void runBiclustering(final ArgumentsCC cca) {

		// Before calling the Native Method (CC), prepare the calling arguments
		// the parameters needed for CC can be retrieved by the owner:
		// data, tg, tc, seed, delta, alpha, pr1-3, logarithm, randomize, n,
		// out_file

		// =======================================================================
		// ???
		// Sa SwingWorker of BiMax ??
		// start CC in a separate thread

		final SwingWorker worker = new SwingWorker() {

			public Object construct() {
				// new RunMachineCC(null).runCC();
				if (BicatGui.debug)System.out.println("gene number: "+ cca.getGeneNumber() +" Chip number: "+  cca.getChipNumber());
				
				run_CC(cca.getData(), cca.getGeneNumber(), cca.getChipNumber(),
						cca.getSeed(), cca.getDelta(), cca.getAlpha(), cca
								.getPR1(), cca.getPR2(), cca.getPR3(), cca
								.getLogarithm(), cca.getRandomize(),
						cca.getN(), cca.getOutputFile());

				if (owner.debug) {
					System.out.println("Started CC in a new thread!\n");
				}

				return null;
			}

			public void finished() {

				System.out.println("Getting results from CC... "
						+ owner.currentDirectoryPath + "/output.cc");

				computedBiclusters = getOutputBiclusters(owner.currentDirectoryPath
						+ "/output.cc");
								
				outputBiclusters = computedBiclusters; // is a LinkedList of
				// BitSet
				// representated-BCs
				// (bicat.runMachine.Bicluster_bitset)
				
				transferList();

				owner.finishUpRun(cca.getDatasetIdx(), outputBiclusters, cca
						.getPreprocessOptions(), "CC");
				JOptionPane
						.showMessageDialog(
								null,
								"Calculations finished.\nThe results can be found in the bicluster results section of the current dataset.");

			}
		};
		worker.start();
	}
}
