package bicat.gui.window;

import bicat.gui.BicatGui;
import bicat.run_machine.ArgumentsOPSM;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class RunDialog_OPSM implements ActionListener {

	BicatGui owner;

	JDialog dialog;

	static String DEFAULT_PARAMETERS = "set_defaults";

	static String RUN_OPSM = "run_opsm";

	static String RUN_OPSM_DIALOG_CANCEL = "cancel";

	static int DEFAULT_L_VALUE = 10;

	static JFormattedTextField l_textfield;

	ArgumentsOPSM opsma;

	// ===========================================================================
	public RunDialog_OPSM() {
	}

	public RunDialog_OPSM(BicatGui o, ArgumentsOPSM args) {
		owner = o;
		opsma = args;
	}

	// ===========================================================================
	public void makeWindow() {

		dialog = new JDialog(owner, "Run OPSM");

		JPanel parameters = new JPanel(new GridLayout(0, 1));

		// ....

		JPanel p0 = new JPanel(new FlowLayout());
		JPanel parameter_values = new JPanel(new GridLayout(0, 2));

		JLabel l_label = new JLabel(
				"Set number of passed models for each iteration (l) ");
		l_label.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		parameter_values.add(l_label);

		l_textfield = new JFormattedTextField(NumberFormat.getNumberInstance());
		l_textfield.setValue(new Integer(DEFAULT_L_VALUE));
		l_textfield.addActionListener(this);
		parameter_values.add(l_textfield);
		p0.add(parameter_values);

		// ........................................................................

		JPanel p1 = new JPanel(new FlowLayout());

		JButton okButton = new JButton("Run OPSM");
		okButton.setActionCommand(RUN_OPSM);
		okButton.addActionListener(this);
		p1.add(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand(RUN_OPSM_DIALOG_CANCEL);
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

		if (RUN_OPSM.equals(e.getActionCommand())) {

			try {
				int parameter = (new Integer(l_textfield.getText()).intValue());

				opsma.setl(parameter); // number of passed models

				dialog.setVisible(false);
				dialog.dispose();
				JOptionPane
						.showMessageDialog(null,
								"OPSM algorithm is running... \nThe calculations may take some time");
				for (int i = 0; i < 50000000; i++) {} // wait a bit
				owner.runMachineOPSM.runBiclustering(opsma);
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
		}

		else if (RUN_OPSM_DIALOG_CANCEL.equals(e.getActionCommand())) {
			dialog.setVisible(false);
			dialog.dispose();
		}
	}
}
