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
 * Run Machine for XMotifs algorithm (2003).
 * 
 * For its execution, a shared library is needed (lib/linux/libxmotifs.so for
 * Linux).
 * 
 */

public class RunMachine_XMotifs extends RunMachine {

	// ===========================================================================
	public RunMachine_XMotifs() {
	}

	public RunMachine_XMotifs(BicatGui o) {
		owner = o;
	}

	// ===========================================================================
	public native void run_xMotifs(double[] data, int total_gs, int total_cs,
			int seed, double max_p_value, double alpha, int n_s, int n_d,
			int s_d, int max_length, int logarithm, int max_size,
			String out_file);

	static {
		System.out.println(System.getProperty("java.library.path"));
		System.out.println("xmotifs library loaded");
		System.loadLibrary("xmotifs"); // should distinguish between the
		// platforms ...
	}

	public synchronized void runBiclustering(final ArgumentsXMotifs xma) {
		
		final SwingWorker worker = new SwingWorker() {

			public Object construct() {

				run_xMotifs(xma.getData(), xma.getGeneNumber(), xma
						.getChipNumber(), xma.getSeed(), xma.getMaxPValue(),
						xma.getAlpha(), xma.getNS(), xma.getND(), xma.getSD(),
						xma.getMaxLength(), xma.getLogarithm(), xma
								.getMaxSize(), xma.getOutputFile());

				if (owner.debug)
					System.out.println("Started xMotifs in a new thread!\n");

				return null;
			}

			public void finished() {

				System.out.println("Getting results from xMotifs... "
						+ owner.currentDirectoryPath + "/output.xMotifs");

				computedBiclusters = getOutputBiclusters(owner.currentDirectoryPath
						+ "/output.xMotifs");

				outputBiclusters = computedBiclusters; // is a LinkedList of
				// BitSet
				// representated-BCs
				// (bicat.runMachine.Bicluster)
				transferList();

				owner.finishUpRun(xma.getDatasetIdx(), outputBiclusters, xma
						.getPreprocessOptions(), "xMotif");
				JOptionPane
						.showMessageDialog(
								null,
								"Calculations finished.\nThe results can be found in the bicluster results section of the current dataset.");

			}
		};
		worker.start();
	}

}