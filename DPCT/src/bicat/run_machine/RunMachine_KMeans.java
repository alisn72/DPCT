package bicat.run_machine;

import bicat.algorithms.clustering.EmptyClusterException;
import bicat.algorithms.clustering.KMeans;
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
 * Run Machine for K-Means Clustering algorithm. This Java implementation allows
 * the parametrization similar to the corresponding MATLAB implementation.
 * 
 */

public class RunMachine_KMeans extends RunMachine {

	// ===========================================================================
	public RunMachine_KMeans() {
	}

	public RunMachine_KMeans(BicatGui o) {
		owner = o;
	}

	// ===========================================================================
	public synchronized void runClustering(final ArgumentsKMeans args) {

		final KMeans km = new KMeans(args.getNumberClusters(), args
				.getDistanceMetric(), args.getNumberMaxIterations(), args
				.getNumberReplicates(), args.getStartList(), null, args
				.getEmptyAction());

		final SwingWorker worker = new SwingWorker() {

			public Object construct() {
				try {
					System.out.println("Started K-means in a new thread!\n");
					km.runKMeans(args.getMyData());
				} catch (EmptyClusterException e) {
					System.out
							.println("EmptyClusterException! / No K-means results expected. ");
					System.err.println(e.toString());
				}
				return null;
			}

			public void finished() {
				System.out.println("Getting results from K-means...");

				outputBiclusters = null;
				try {
					outputBiclusters = km.getClusters();
				} catch (EmptyClusterException e) {
					for (int i = 0; i < 800000000; i++) {} //wait
					JOptionPane.showMessageDialog(null, "Empty cluster found. No K-means results expected.\nReduce the number of clusters to be found.");
				}

				if (outputBiclusters != null) {
					owner.finishUpClusterRun(args.getDatasetIdx(),
							outputBiclusters, args.getPreprocessOptions(),
							"KMeans");
					JOptionPane
							.showMessageDialog(
									null,
									"Calculations finished.\nThe results can be found in the cluster results section of the current dataset.");

				}
			}
		};
		worker.start();
	}

}