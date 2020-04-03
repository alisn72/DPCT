package bicat.gui.window;

import bicat.gui.BicatGui;
import bicat.run_machine.ArgumentsXMotifs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

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

public class RunDialog_XMotifs implements ActionListener {

	BicatGui owner;

	JDialog dialog;

	static String DEFAULT_PARAMETERS = "set_defaults";

	static String RUN_XMOTIFS = "run_xmotifs";

	static String RUN_XMOTIFS_DIALOG_CANCEL = "cancel";

	static String DEFAULT_SEED_VALUE = "13";

	static String DEFAULT_MAX_P_VALUE = "1e-9"; // "0.0000000001" // 10 ^ -1

	static String DEFAULT_ALPHA_VALUE = "0.05"; // 2.0

	static String DEFAULT_N_S_VALUE = "10";

	static String DEFAULT_N_D_VALUE = "100";

	static String DEFAULT_S_D_VALUE = "4";

	static int DEFAULT_MAX_LENGTH_VALUE; // = 50;... etwa 75 % nr chips

	static JTextField seed_f;

	static JTextField max_p_value_f;

	static JTextField alpha_f;

	static JTextField n_s_f;

	static JTextField n_d_f;

	static JTextField s_d_f;

	static JFormattedTextField max_length_f;

	NumberFormat nf;

	ArgumentsXMotifs xma;

	// ===========================================================================
	public RunDialog_XMotifs() {
	}

	public RunDialog_XMotifs(BicatGui o, ArgumentsXMotifs args) {
		owner = o;
		xma = args;
		DEFAULT_MAX_LENGTH_VALUE = owner.pre.getWorkingChipCount();
	}

	// ===========================================================================
	public void makeWindow() {

		// user-defined parameters: seed, delta, alpha, n
		dialog = new JDialog(owner, "Run xMotifs");

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

		JLabel max_p_value_l = new JLabel("Set maximum P-value: ");
		max_p_value_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		parameter_values.add(max_p_value_l);

		// max_p_value_f = new JFormattedTextField(nf);
		// max_p_value_f.setValue(new Double(DEFAULT_MAX_P_VALUE));
		// max_p_value_f.addActionListener(this);
		// parameter_values.add(max_p_value_f);
		max_p_value_f = new JTextField();
		max_p_value_f.setText(DEFAULT_MAX_P_VALUE);
		max_p_value_f.addActionListener(this);
		parameter_values.add(max_p_value_f);

		JLabel alpha_l = new JLabel("Set alpha: ");
		alpha_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		parameter_values.add(alpha_l);

		alpha_f = new JTextField();
		alpha_f.setText(DEFAULT_ALPHA_VALUE);
		alpha_f.addActionListener(this);
		parameter_values.add(alpha_f);

		// ....

		JLabel n_s_l = new JLabel("Set the n_s parameter: ");
		n_s_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		parameter_values.add(n_s_l);

		n_s_f = new JTextField();
		n_s_f.setText(DEFAULT_N_S_VALUE);
		n_s_f.addActionListener(this);
		parameter_values.add(n_s_f);

		JLabel n_d_l = new JLabel("Set the n_d parameter: ");
		n_d_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		parameter_values.add(n_d_l);

		n_d_f = new JTextField();
		n_d_f.setText(DEFAULT_N_D_VALUE);
		n_d_f.addActionListener(this);
		parameter_values.add(n_d_f);

		JLabel s_d_l = new JLabel("Set the s_d parameter: ");
		s_d_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		parameter_values.add(s_d_l);

		s_d_f = new JTextField();
		s_d_f.setText(DEFAULT_S_D_VALUE);
		s_d_f.addActionListener(this);
		parameter_values.add(s_d_f);

		JLabel max_length_l = new JLabel("Set the max_length parameter: ");
		max_length_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		parameter_values.add(max_length_l);

		max_length_f = new JFormattedTextField(NumberFormat.getNumberInstance());
		DEFAULT_MAX_LENGTH_VALUE = Math.round((float) (0.75 * xma
				.getChipNumber()));
		max_length_f.setValue(new Integer(DEFAULT_MAX_LENGTH_VALUE));
		max_length_f.addActionListener(this);
		parameter_values.add(max_length_f);

		p0.add(parameter_values);

		// ........................................................................

		JPanel p1 = new JPanel(new GridBagLayout());

		JButton okButton = new JButton("Run xMotifs");
		okButton.setActionCommand(RUN_XMOTIFS);
		okButton.addActionListener(this);
		p1.add(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand(RUN_XMOTIFS_DIALOG_CANCEL);
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

		if (RUN_XMOTIFS.equals(e.getActionCommand())) {

			int seed = (new Integer(seed_f.getText()).intValue());

			// Double alpha_Double = ((Double) alpha_f.getValue());
			// double alpha = alpha_Double.doubleValue();
			double alpha = Double.parseDouble(alpha_f.getText());

			// Double max_p_value_Double = ((Double) max_p_value_f.getValue());
			// double max_p_value = max_p_value_Double.doubleValue();
			double max_p_value = Double.parseDouble(max_p_value_f.getText());
			// System.out.println("max_p_value * 2: " + max_p_value * 2);

			int n_s = (new Integer(n_s_f.getText()).intValue());
			int n_d = (new Integer(n_d_f.getText()).intValue());
			int s_d = (new Integer(s_d_f.getText()).intValue());
			int max_length = (new Integer(max_length_f.getText()).intValue());

			xma.setSeed(seed);
			xma.setAlpha(alpha);
			xma.setND(n_d);
			xma.setNS(n_s);
			xma.setSD(s_d);
			xma.setMaxLength(max_length);
			xma.setMaxPValue(max_p_value);

			xma.setMaxSize(n_d * n_s);

			dialog.setVisible(false);
			dialog.dispose();
			int numberOfChips = xma.getChipNumber();
			if (numberOfChips > 64) {
				JOptionPane
						.showMessageDialog(null,
								"Sorry, xMotifs does not run for data sets with more than 64 chips.");
			} else {
				JOptionPane
						.showMessageDialog(null,
								"XMotifs algorithm is running... \nThe calculations may take some time");
				for (int i = 0; i < 300000000; i++) {
				} // wait a bit
				owner.runMachine_XMotifs.runBiclustering(xma);
			}
		}

		else if (RUN_XMOTIFS_DIALOG_CANCEL.equals(e.getActionCommand())) {
			dialog.setVisible(false);
			dialog.dispose();
		}

	}
}