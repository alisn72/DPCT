package bicat.run_machine;

import bicat.gui.BicatGui;

import javax.swing.*;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 * Run Machine for Iterative Signature algorithm (2002).
 *
 * For its execution, a shared library is needed (lib/linux/libisa.so for Linux).
 *
 **/

public class RunMachine_ISA extends RunMachine {

  // ===========================================================================
  public RunMachine_ISA() { }
  public RunMachine_ISA(BicatGui o) { owner = o; }

  // ===========================================================================
  public native void run_ISA(double[] data, int total_gs, int total_cs,
                             int seed,
                             double t_g, double t_c,
                             int n_fix,
                             int logarithm,
                             int max_size,
                             String out_file);

  static {
//	  System.loadLibrary("libgslcblas"); //do these libs have to be loaded?
//	  System.loadLibrary("libgsl");
//      System.setProperty( "java.library.path", "E:\\Dr jalili\\paper 3\\Intell.PAPER3\\src\\bicat\\" );
	  System.loadLibrary("isa");
      //System.load("E:\\Dr jalili\\paper 3\\Intell.PAPER3\\src\\bicat\\isa.dll");
      System.out.println("isa library loaded ");
}

  public synchronized void runBiclustering(final ArgumentsISA isaa) {

    final SwingWorker worker = new SwingWorker() {

      public Object construct() {

        run_ISA(isaa.getData(), isaa.getGeneNumber(), isaa.getChipNumber(),
                isaa.getSeed(),
                isaa.getTG(), isaa.getTC(),
                isaa.getNFix(),
                isaa.getLogarithm(),
                isaa.getMaxSize(), isaa.getOutputFile());

        if(owner.debug) System.out.println("Started ISA in a new thread!\n");

        return null;
      }

      public void finished() {

        System.out.println("Getting results from ISA... " + owner.currentDirectoryPath+"/output.isa");

        computedBiclusters = getOutputBiclusters(owner.currentDirectoryPath+"/output.isa"); //cca.getOutputFile());

        outputBiclusters = computedBiclusters; // is a LinkedList of BitSet representated-BCs (bicat.runMachine.Bicluster)
        transferList();

        owner.finishUpRun(isaa.getDatasetIdx(), outputBiclusters, isaa.getPreprocessOptions(), "ISA");
        JOptionPane.showMessageDialog(null, "Calculations finished.\nThe results can be found in the bicluster results section of the current dataset.");

      }
    };
    worker.start();
  }
}