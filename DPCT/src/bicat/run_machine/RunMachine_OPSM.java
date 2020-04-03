//package bicat.runMachine;
//
//import bicat.gui.BicatGui;
//
//public class RunMachine_OPSM extends RunMachine{
//
//public RunMachine_OPSM(BicatGui gui) {
//		// TODO Auto-generated constructor stub
//	}
//
//public void runBiclustering(Arguments_OPSM opsma) {
//	// TODO Auto-generated method stub
//	
//}
//
//}

package bicat.run_machine;

import bicat.algorithms.opsm.BendorReloadedAlgorithm;
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
 * Run Machine for BiMax algorithm. The code for the BiMax algorithm is kept
 * separate, in bicat.runMachine.bimax subpackage.
 * 
 */

public class RunMachine_OPSM extends RunMachine {

	// ===========================================================================
	public RunMachine_OPSM() {
	}

	public RunMachine_OPSM(BicatGui o) {
		owner = o;
	}

	// ===========================================================================
	/**
	 * Takes a dataset, stores it, runs <code>OPSM</code> on it Its completion
	 * is marked by a function call in the <code>finished</code> method in the
	 * local <code>SwingWorker</code> object.
	 * 
	 */
	public synchronized void runBiclustering(final ArgumentsOPSM args) {

		nrGenes = args.getNumberGenes();
		nrChips = args.getNumberChips();

		// create new OPSM object
		final int l = args.getl();
		final BendorReloadedAlgorithm opsm = new BendorReloadedAlgorithm();
		final float[][] matrix = args.getMyData();

		// start BiMax in a separate thread
		final SwingWorker worker = new SwingWorker() {

			public Object construct() {
				if (owner.debug)
					System.out.println("Started OPSM in a new thread!\n"
							+ "Matrix dimensions are: " + matrix.length + " / "
							+ matrix[0].length);
				opsm.run(matrix, l);
				return null;
			}

			public void finished() {
				System.out.println("Getting results from OPSM...");
				computedBiclusters = opsm.getBiclusters();
				outputBiclusters = computedBiclusters;
				owner.finishUpRun(args.getDatasetIdx(), outputBiclusters, args
						.getPreprocessOptions(), "OPSM");
				JOptionPane.showMessageDialog(null, "Calculations finished.\nThe results can be found in the bicluster results section of the current dataset.");

			}
		};
		worker.start();
	}
}
