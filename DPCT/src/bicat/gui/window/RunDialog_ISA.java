package bicat.gui.window;

import bicat.gui.BicatGui;
import bicat.run_machine.ArgumentsISA;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
 */

public class RunDialog_ISA implements ActionListener {

	BicatGui owner;

	JDialog dialog;

	static String DEFAULT_PARAMETERS = "set_defaults";

	static String RUN_ISA = "run_isa";

	static String RUN_ISA_DIALOG_CANCEL = "cancel";

	static String DEFAULT_SEED_VALUE = "13";

	static String DEFAULT_NUMBER_BICLUSTERS_VALUE = "100";

	static String DEFAULT_T_G_VALUE = "2.0";

	static String DEFAULT_T_C_VALUE = "2.0";

	static JTextField seed_f;

	static JTextField t_g_f;

	static JTextField t_c_f;

	static JTextField number_BCs_f;

	ArgumentsISA isaa;

	// ===========================================================================
	public RunDialog_ISA() {
	}

	public RunDialog_ISA(BicatGui o, ArgumentsISA args) {
		owner = o;
		isaa = args;
	}

	// ===========================================================================
	public void makeWindow() {

		// user-defined parameters: seed, delta, alpha, n
		dialog = new JDialog(owner, "Run ISA");

		JPanel parameters = new JPanel(new GridLayout(0, 1));

		// ....
		JPanel p0 = new JPanel(new FlowLayout());

		JPanel parameter_values = new JPanel(new GridLayout(0, 2));

		JLabel seed_l = new JLabel("Set seed for random number generator: ");
		seed_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		parameter_values.add(seed_l);

		seed_f = new JTextField();
		seed_f.setText(DEFAULT_SEED_VALUE);
		seed_f.addActionListener(this);
		parameter_values.add(seed_f);

		// ....

		JLabel t_g_l = new JLabel("Set t_g (threshold genes): ");
		t_g_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		parameter_values.add(t_g_l);

		t_g_f = new JTextField();
		t_g_f.setText(DEFAULT_T_G_VALUE);
		t_g_f.addActionListener(this);
		parameter_values.add(t_g_f);

		JLabel t_c_l = new JLabel("Set t_c (threshold chips): ");
		t_c_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		parameter_values.add(t_c_l);

		t_c_f = new JTextField();
		t_c_f.setText(DEFAULT_T_C_VALUE);
		t_c_f.addActionListener(this);
		parameter_values.add(t_c_f);

		// ....

		JLabel number_BCs_l = new JLabel(
				"Set the number of starting points: ");
		number_BCs_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		parameter_values.add(number_BCs_l);

		number_BCs_f = new JTextField();
		number_BCs_f.setText(DEFAULT_NUMBER_BICLUSTERS_VALUE);
		number_BCs_f.addActionListener(this);
		parameter_values.add(number_BCs_f);

		p0.add(parameter_values);

		// ........................................................................

		JPanel p1 = new JPanel(new GridBagLayout());

		JButton okButton = new JButton("Run ISA");
		okButton.setActionCommand(RUN_ISA);
		okButton.addActionListener(this);
		p1.add(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand(RUN_ISA_DIALOG_CANCEL);
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
	 * For <code>ActionListener</code> interface, reacts to user selections
	 * and button clicks in this search window.
	 * 
	 */
	public void actionPerformed(ActionEvent e) {

		if (RUN_ISA.equals(e.getActionCommand())) {
			try {
				int seed = (new Integer(seed_f.getText()).intValue());
				double t_g = (new Double(t_g_f.getText()).doubleValue());
				double t_c = (new Double(t_c_f.getText()).doubleValue());
				int number_BCs = (new Integer(number_BCs_f.getText())
						.intValue());

				isaa.setSeed(seed);
				isaa.setTG(t_g);
				isaa.setTC(t_c);
				isaa.setNFix(number_BCs); // number of starting points (max number of biclusters)
				isaa.setMaxSize(number_BCs);

				dialog.setVisible(false);
				dialog.dispose();
				
				JOptionPane
						.showMessageDialog(null,
								"ISA algorithm is running... \nThe calculations may take some time");

				for (int i = 0; i < 500000000; i++) {}
				owner.runMachine_ISA.runBiclustering(isaa);

				

			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
		}

		else if (RUN_ISA_DIALOG_CANCEL.equals(e.getActionCommand())) {
			dialog.setVisible(false);
			dialog.dispose();
		}
	}

}