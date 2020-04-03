package bicat.gui.window;

import bicat.algorithms.clustering.HCL;
import bicat.gui.BicatGui;
import bicat.run_machine.ArgumentsHCL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Vector;

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

public class RunDialog_HCL implements ActionListener {

	BicatGui owner;

	static JDialog dialog;

	static JFormattedTextField number_Cs_f;

	static int DEFAULT_NUMBER_CLUSTERS_VALUE = 5;

	static String RUN_HCL = "run_hcl";

	static String RUN_HCL_DIALOG_CANCEL = "cancel";

	static String SELECT_LINKAGE_LIST = "select_linkage";

	static String SELECT_DISTANCE_LIST = "select_distance";

	static int wL = -1; // what linkage mode?

	static int wD = -1; // what distance metric?

	static int nC = DEFAULT_NUMBER_CLUSTERS_VALUE;

	ArgumentsHCL hcla;

	// ===========================================================================
	public RunDialog_HCL() {
	}

	public RunDialog_HCL(BicatGui o) {
		owner = o;
	}

	public RunDialog_HCL(BicatGui o, ArgumentsHCL args) {
		owner = o;
		hcla = args;
	}

	// ===========================================================================
	public void makeWindow() {

		dialog = new JDialog(owner, "Run HCL");

		// ........................................................................
		JPanel p0 = new JPanel(new FlowLayout());
		JPanel parameter_values = new JPanel(new GridLayout(0, 2));

		JLabel number_Cs_l = new JLabel("Number of clusters: ");
		number_Cs_l.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		parameter_values.add(number_Cs_l);

		number_Cs_f = new JFormattedTextField(NumberFormat.getNumberInstance());
		number_Cs_f.setValue(new Integer(DEFAULT_NUMBER_CLUSTERS_VALUE));
		number_Cs_f.addActionListener(this);
		parameter_values.add(number_Cs_f);

		p0.add(parameter_values);

		// ....
		JPanel p1 = new JPanel(new GridLayout(0, 1)); // FlowLayout());
		Vector linkageOp = new Vector();
		linkageOp.add("Choose Linkage Mode ...");
		linkageOp.add(" Single linkage");
		linkageOp.add(" Complete linkage");
		linkageOp.add(" Average linkage");

		JComboBox cb = new JComboBox(linkageOp);
		cb.setActionCommand(SELECT_LINKAGE_LIST);
		cb.setSelectedIndex(0);
		cb.setAlignmentX(JComboBox.CENTER_ALIGNMENT);
		cb.addActionListener(this);

		p1.add(cb);

		// ....
		JPanel p2 = new JPanel(new GridLayout(0, 1)); // FlowLayout() );
		Vector distanceOp = new Vector();
		distanceOp.add("Choose Distance Metric ...");
		distanceOp.add(" Euclidean distance");
		distanceOp.add(" Pearson's correlation distance");
		distanceOp.add(" Manhattan (City-block) distance");
		distanceOp.add(" Minkowski distance (pow 2)");
		distanceOp.add(" Cosine distance");

		JComboBox cb2 = new JComboBox(distanceOp);
		cb2.setActionCommand(SELECT_DISTANCE_LIST);
		cb2.setSelectedIndex(0);
		cb2.setAlignmentX(JComboBox.CENTER_ALIGNMENT);
		cb2.addActionListener(this);

		p2.add(cb2);

		// ........................................................................

		JPanel p3 = new JPanel(new FlowLayout()); // GridBagLayout() );

		JButton okButton = new JButton("Run HCL");
		okButton.setActionCommand(RUN_HCL);
		okButton.addActionListener(this);
		p3.add(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand(RUN_HCL_DIALOG_CANCEL);
		cancelButton.addActionListener(this);
		p3.add(cancelButton);

		// parameters.add(p1);

		// ........................................................................

		JPanel parameters = new JPanel(new GridLayout(0, 1));
		parameters.add(p0); // arameter_values);
		parameters.add(p1);
		parameters.add(p2);
		parameters.add(p3);

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

		if (SELECT_LINKAGE_LIST.equals(e.getActionCommand())) {
			JComboBox box = (JComboBox) e.getSource();
			String item = (String) box.getSelectedItem();
			updateLabel_LinkageListSelection(item);
		}

		else if (SELECT_DISTANCE_LIST.equals(e.getActionCommand())) {
			JComboBox box = (JComboBox) e.getSource();
			String item = (String) box.getSelectedItem();
			updateLabel_DistanceListSelection(item);
		}

		else if (RUN_HCL.equals(e.getActionCommand())) {

			try {

				if (wL == -1 || wD == -1) {
					JOptionPane
							.showMessageDialog(null,
									"Please make a choice for Linkage mode\nand distance metric.");
				} else if (new Integer(number_Cs_f.getText()).intValue() >= hcla
						.getMyData().length) {
					JOptionPane
							.showMessageDialog(null,
									"The number of clusters must be smaller than the number of genes.");
				} else {

					nC = (new Integer(number_Cs_f.getText()).intValue());

					hcla.setDistanceMetric(wD);
					hcla.setLinkage(wL);
					hcla.setNumberClusters(nC);

					for (int i = 0; i < 500000000; i++) {
					} // wait
					owner.runMachineHCL.runClustering(hcla);

					dialog.setVisible(false);
					dialog.dispose();
					JOptionPane
							.showMessageDialog(null,
									"HCL algorithm is running... \nThe calculations may take some time");

				}
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
		}

		else if (RUN_HCL_DIALOG_CANCEL.equals(e.getActionCommand())) {
			dialog.setVisible(false);
			dialog.dispose();
		}

	}

	// ===========================================================================
	void updateLabel_LinkageListSelection(String item) {

		/*
		 * linkageOp.add("Choose Linkage Mode ..."); linkageOp.add(" Single
		 * linkage"); linkageOp.add(" Complete linkage"); linkageOp.add("
		 * Average linkage");
		 */

		if (item.equals(" Single linkage"))
			wL = HCL.SINGLE_LINKAGE;
		else if (item.equals(" Complete linkage"))
			wL = HCL.COMPLETE_LINKAGE;
		else if (item.equals(" Average linkage"))
			wL = HCL.AVERAGE_LINKAGE;

		// System.out.println("Select linkage method! (Canceling) / ... " +
		// item); }
	}

	// ===========================================================================
	void updateLabel_DistanceListSelection(String item) {

		/*
		 * distanceOp.add("Choose Distance Metric ..."); distanceOp.add("
		 * Euclidean distance"); distanceOp.add(" Pearson's correlation
		 * distance"); distanceOp.add(" Manhattan (City-block) distance");
		 * distanceOp.add(" Minkowski distance (pow 2)"); distanceOp.add("
		 * Cosine distance");
		 */

		if (item.equals(" Euclidean distance"))
			wD = HCL.EUCLIDEAN_DISTANCE;
		else if (item.equals(" Pearson's correlation distance"))
			wD = HCL.PEARSON_CORRELATION;
		else if (item.equals(" Manhattan (City-block) distance"))
			wD = HCL.MANHATTAN_DISTANCE;
		else if (item.equals(" Minkowski distance (pow 2)"))
			wD = HCL.MINKOWSKI_DISTANCE;
		else if (item.equals(" Cosine distance"))
			wD = HCL.COSINE_DISTANCE;

		else {
			System.out.println("Select distance metric! (Canceling) / ... "
					+ item);
		}
		if (BicatGui.debug)
			System.out.println("The selected distance metric is: " + wD);
	}

}