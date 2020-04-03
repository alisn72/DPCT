package bicat.gui.window;

import bicat.gui.BicatGui;
import bicat.run_machine.ArgumentsBiMax;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

//import javax.swing.ProgressMonitor;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 **/


public class RunDialog_BiMax implements ActionListener {

  static String RUN_BIMAX = "run_bimax";
  static String RUN_BIMAX_DIALOG_CANCEL = "cancel";

  static int DEFAULT_GENES_VALUE = 2;
  static int DEFAULT_CHIPS_VALUE = 2;

  static BicatGui owner;
  static JDialog dialog;

  JFormattedTextField genes_f;
  JFormattedTextField chips_f;

  ArgumentsBiMax bmxa;

  // ===========================================================================
  public RunDialog_BiMax() { }
  public RunDialog_BiMax(BicatGui o) { owner = o; }
  public RunDialog_BiMax(BicatGui o, ArgumentsBiMax args) { owner = o;  bmxa = args; }

  // ===========================================================================
  public void makeWindow() {

    // user-defined parameters: seed, delta, alpha, n
    dialog = new JDialog(owner, "Run BiMax");

    JPanel parameters = new JPanel( new GridLayout(0,1) );

    // ........................................................................

    JPanel p0 = new JPanel( new FlowLayout() );
    JPanel parameter_values = new JPanel( new GridLayout(0,2) );

    JLabel genes_l = new JLabel("Specify minimum number of genes: ");
    genes_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    parameter_values.add(genes_l);

    genes_f = new JFormattedTextField(NumberFormat.getNumberInstance());
    genes_f.setValue(new Integer(DEFAULT_GENES_VALUE));
    genes_f.addActionListener(this);
    parameter_values.add(genes_f);

    // ....
    JLabel chips_l = new JLabel("Specify minimum number of chips: ");
    chips_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    parameter_values.add(chips_l);

    chips_f = new JFormattedTextField(NumberFormat.getNumberInstance());
    chips_f.setValue(new Integer(DEFAULT_CHIPS_VALUE));
    chips_f.addActionListener(this);
    parameter_values.add(chips_f);
    p0.add(parameter_values);

    // ........................................................................

    JPanel p1 = new JPanel( new FlowLayout() );

    JButton okButton = new JButton("Run BiMax");
    okButton.setActionCommand(RUN_BIMAX);
    okButton.addActionListener(this);
    p1.add(okButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand(RUN_BIMAX_DIALOG_CANCEL);
    cancelButton.addActionListener(this);
    p1.add(cancelButton);

    // ........................................................................

    parameters.add(p0); //parameter_values;
    parameters.add(p1);

    dialog.setContentPane(parameters);

    // ........................................................................
    dialog.pack();
    dialog.setLocationRelativeTo(owner);
    dialog.setVisible(true);
  

  }

  // ===========================================================================
  /**
   * For <code>ActionListener</code> interface, reacts to user selections and
   * button clicks in this search window.
   *
   * */
  public void actionPerformed(ActionEvent e) {

    if(RUN_BIMAX.equals(e.getActionCommand())) {

      try {
        int genes = DEFAULT_GENES_VALUE;
        int chips = DEFAULT_CHIPS_VALUE;

        genes = ( new Integer(genes_f.getText()).intValue());
        chips = ( new Integer(chips_f.getText()).intValue());

        bmxa.setMinGenes(genes);
        bmxa.setMinChips(chips);

        //ProgressMonitor pm = new ProgressMonitor(owner, "Biclustering data...", null, 0,0); // creates a progress monitor
        dialog.setVisible(false);
        dialog.dispose();
        JOptionPane.showMessageDialog(null, "Bimax algorithm is running... \nThe calculations may take some time");
        for (int i = 0; i < 300000000; i++) {} // wait a bit
		owner.runMachineBiMax.runBiclustering(bmxa);

        }
      catch(NumberFormatException nfe) { nfe.printStackTrace(); }
    }

    else if(RUN_BIMAX_DIALOG_CANCEL.equals(e.getActionCommand())) {
      dialog.setVisible(false);
      dialog.dispose();
    }
  }
}