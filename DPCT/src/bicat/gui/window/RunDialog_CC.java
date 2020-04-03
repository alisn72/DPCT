package bicat.gui.window;

import bicat.gui.BicatGui;
import bicat.run_machine.ArgumentsCC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 **/

public class RunDialog_CC implements ActionListener {

  BicatGui owner;
  JDialog dialog;

  static String DEFAULT_PARAMETERS = "set_defaults";
  static String RUN_CC = "run_cc";
  static String RUN_CC_DIALOG_CANCEL = "cancel";

  static String DEFAULT_SEED_VALUE = "13";
  static String DEFAULT_NUMBER_BICLUSTERS_VALUE = "10";
  static String DEFAULT_ALPHA_VALUE = "1.2";
  static String DEFAULT_DELTA_VALUE = "0.5";

  static JTextField seed_f;
  static JTextField delta_f;
  static JTextField alpha_f;
  static JTextField number_BCs_f;

  ArgumentsCC cca;

  // ===========================================================================
  public RunDialog_CC() { }
  public RunDialog_CC(BicatGui o, ArgumentsCC args) {
    owner = o;
    cca = args;
  }

  // ===========================================================================
  public void makeWindow() {

    // user-defined parameters: seed, delta, alpha, n
    dialog = new JDialog(owner, "Run CC");

    JPanel parameters = new JPanel( new GridLayout(0,1) );

    // ....

    JPanel p0 = new JPanel( new FlowLayout() );
    JPanel parameter_values = new JPanel( new GridLayout(0,2) );

    JLabel seed_l = new JLabel("Set seed for random number generator: ");
    seed_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    parameter_values.add(seed_l);

    seed_f = new JTextField();
	seed_f.setText(DEFAULT_SEED_VALUE);
	seed_f.addActionListener(this);
	parameter_values.add(seed_f);
    // ....

    JLabel delta_l = new JLabel("Set delta: ");
    delta_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    parameter_values.add(delta_l);

    delta_f = new JTextField();
    delta_f.setText(DEFAULT_DELTA_VALUE);
    delta_f.addActionListener(this);
    parameter_values.add(delta_f);

    JLabel alpha_l = new JLabel("Set alpha: ");
    alpha_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    parameter_values.add(alpha_l);

    alpha_f = new JTextField();
    alpha_f.setText(DEFAULT_ALPHA_VALUE);
    alpha_f.addActionListener(this);
    parameter_values.add(alpha_f);

    // ....

    JLabel number_BCs_l = new JLabel("Set the number of output biclusters: ");
    number_BCs_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    parameter_values.add(number_BCs_l);

    number_BCs_f = new JTextField();
    number_BCs_f.setText(DEFAULT_NUMBER_BICLUSTERS_VALUE);
    number_BCs_f.addActionListener(this);
    parameter_values.add(number_BCs_f);

    p0.add(parameter_values);

    // ........................................................................

    JPanel p1 = new JPanel( new GridBagLayout() );

    JButton okButton = new JButton("Run CC");
    okButton.setActionCommand(RUN_CC);
    okButton.addActionListener(this);
    p1.add(okButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand(RUN_CC_DIALOG_CANCEL);
    cancelButton.addActionListener(this);
    p1.add(cancelButton);

    // ........................................................................

    parameters.add(p0);
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

    if(RUN_CC.equals(e.getActionCommand())) {

      try {
        int seed = ( new Integer(seed_f.getText()).intValue());
        double alpha = ( new Double(alpha_f.getText()).doubleValue());
        double delta = ( new Double(delta_f.getText()).doubleValue());
        int number_BCs = ( new Integer(number_BCs_f.getText()).intValue());

        cca.setSeed(seed);
        cca.setAlpha(alpha);
        cca.setDelta(delta);
        cca.setN(number_BCs);          // number of output biclusters
        
        JOptionPane.showMessageDialog(null, "CC algorithm is running... \nThe calculations may take some time");
        for (int i = 0; i < 500000000; i++) {} //wait
        owner.runMachine_CC.runBiclustering(cca);

        dialog.setVisible(false);
        dialog.dispose();
        
      }
      catch(NumberFormatException nfe) { nfe.printStackTrace(); }
    }

    else if(RUN_CC_DIALOG_CANCEL.equals(e.getActionCommand())) {
      dialog.setVisible(false);
      dialog.dispose();
    }

      }

}