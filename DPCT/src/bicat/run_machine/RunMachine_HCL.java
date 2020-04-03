package bicat.run_machine;

import bicat.algorithms.clustering.HCL;
import bicat.gui.BicatGui;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 * Run Machine for Hierarchical Clustering algorithm. This Java implementation
 * allows the parametrization similar to the corresponding MATLAB implementation.
 *
 **/

public class RunMachine_HCL extends RunMachine {

  // ===========================================================================
  public RunMachine_HCL() { }
  public RunMachine_HCL(BicatGui o) { owner = o; }

  // ===========================================================================
  public synchronized void runClustering(final ArgumentsHCL args) {

    final HCL hcl = new HCL(args.getNumberClusters(),
                            args.getDistanceMetric(),
                            args.getLinkage(), 0);

    final SwingWorker worker = new SwingWorker() {

      public Object construct() {

        if(owner.debug)
          System.out.println("Started HCL in a new thread!\n");

        hcl.runHCL(args.getMyData(),args.getDistanceMetric());
        return null;
      }

      public void finished() {

        System.out.println("Getting results from HCL...");

        outputBiclusters = null;
          // I commented it
        //outputBiclusters = hcl.getClusters();

//        owner.finishUpClusterRun(args.getDatasetIdx(), outputBiclusters, args.getPreprocessOptions(), "HCL");
//        JOptionPane.showMessageDialog(null, "Calculations finished.\nThe results can be found in the cluster results section of the current dataset.");

      }
    } ;
    worker.start();
  }

}
