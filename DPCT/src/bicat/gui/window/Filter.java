package bicat.gui.window;

import bicat.biclustering.Dataset;
import bicat.gui.BicatGui;
import bicat.util.BicatError;
import bicat.util.BicatUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
 * @author D. Frick, A. Prelic
 * @version 1.0
 * 
 */

public class Filter implements ActionListener {

	JDialog dialog;

	BicatGui owner;

	int which_list = -1;

	int list_idx = -1;

	int which_data = -1;

	JTextField minGsField;

	JTextField maxGsField;

	JTextField minCsField;

	JTextField maxCsField;

	JTextField bcNumber;

	JFormattedTextField overlap;

	public final String FILTER_WINDOW_DEFAULT_NR_OUTPUT_BICLUSTERS = "100";

	public final int FILTER_WINDOW_DEFAULT_ALLOWED_OVERLAP = 25;

	public final String FILTER_WINDOW_APPLY = "start_filter";

	public final String FILTER_WINDOW_CANCEL = "cancel_filter";

	public final String FILTER_WINDOW_SELECT_BC_LIST = "select_BC_list";

	// ===========================================================================
	public Filter() {
	}

	public Filter(BicatGui o) {
		owner = o;
	}

	void reset() {
		which_list = -1;
		list_idx = -1;
		which_data = -1;
	}

	// ===========================================================================
	public void makeWindow() {

		reset();

		dialog = new JDialog(owner, "Filter Setup Dialog ");

		// //////////////////////////////////////////////////////////////////////////
		JPanel top = new JPanel(new FlowLayout());
		top.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.black), ""));

		/** @todo CHECK IF THERE IS ANY */
		Vector names = owner.getListNamesAll();

		JComboBox cb = new JComboBox(names);
		cb.setActionCommand(FILTER_WINDOW_SELECT_BC_LIST);
		cb.setSelectedIndex(0);
		cb.setAlignmentX(JComboBox.CENTER_ALIGNMENT);
		cb.addActionListener(this);
		top.add(cb, BorderLayout.CENTER);

		// //////////////////////////////////////////////////////////////////////////

		int limitG = 0;
		int limitC = 0;

		// //////////////////////////////////////////////////////////////////////////
		JPanel bySize = new JPanel(new GridLayout(0, 2));
		bySize.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.black), "By Size"));

		JPanel aux = new JPanel(new GridLayout(1, 2));
		JLabel g = new JLabel(" Min gene cnt");
		aux.add(g);
		minGsField = new JTextField();
		minGsField.setText("0");
		aux.add(minGsField);
		;

		bySize.add(aux);
		aux = new JPanel(new GridLayout(1, 2));

		g = new JLabel(" Max gene cnt");
		aux.add(g);
		maxGsField = new JTextField();
		maxGsField.setText("0");
		aux.add(maxGsField);

		bySize.add(aux);
		aux = new JPanel(new GridLayout(1, 2));

		JLabel c = new JLabel(" Min chip cnt");
		aux.add(c);
		minCsField = new JTextField();
		minCsField.setText("0");
		aux.add(minCsField);

		bySize.add(aux);
		aux = new JPanel(new GridLayout(1, 2));

		c = new JLabel(" Max chip cnt");
		aux.add(c);
		maxCsField = new JTextField();
		maxCsField.setText("0");
		aux.add(maxCsField);

		bySize.add(aux);

		// //////////////////////////////////////////////////////////////////////////
		JPanel byOverlap = new JPanel(new GridLayout(0, 2));
		byOverlap.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.black), "By Overlap"));

		JLabel l = new JLabel(" Limit the number of biclusters ");
		byOverlap.add(l);

		bcNumber = new JTextField();
		bcNumber.setText(FILTER_WINDOW_DEFAULT_NR_OUTPUT_BICLUSTERS);
		byOverlap.add(bcNumber);

		l = new JLabel(" Limit the allowed overlap (%)  ");
		byOverlap.add(l);

		overlap = new JFormattedTextField(NumberFormat.getIntegerInstance());
		overlap.setValue(new Integer(FILTER_WINDOW_DEFAULT_ALLOWED_OVERLAP));
		byOverlap.add(overlap);

		// //////////////////////////////////////////////////////////////////////////
		JPanel buttonPanel = new JPanel(new FlowLayout());

		JButton okayButton = new JButton("OK");
		okayButton.setMnemonic(KeyEvent.VK_K);
		okayButton.setActionCommand(FILTER_WINDOW_APPLY);
		okayButton.addActionListener(this);
		buttonPanel.add(okayButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.setActionCommand(FILTER_WINDOW_CANCEL);
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);

		// //////////////////////////////////////////////////////////////////////////

		// main panel of dialog window
		JPanel contentPane = new JPanel(new GridLayout(0, 1));
		contentPane.add(top);
		contentPane.add(bySize);
		contentPane.add(byOverlap);
		contentPane.add(buttonPanel);

		dialog.setContentPane(contentPane);

		// set size, location and make visible
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
	}

	// ===========================================================================
	private void updateLabel_BiclusterListSelection(String item) {

		if (owner.debug)
			System.out.println("D, label selected: " + item);

		int[] info = BicatUtil.getListAndIdx(item);
		which_data = info[2];
		which_list = info[0];
		list_idx = info[1];

		if (info[0] == -1) {
			// System.out.println("Select something! (Canceling)");
			dialog.setVisible(false);
			dialog.dispose();
		} else {
			owner.updateCurrentDataset(which_data); // to do, or not?

			int limit_G = ((Dataset) owner.datasetList.get(which_data))
					.getGeneCount();
			int limit_C = ((Dataset) owner.datasetList.get(which_data))
					.getWorkingChipCount();

			maxGsField.setText(Integer.toString(limit_G));
			maxCsField.setText(Integer.toString(limit_C));

			if (owner.debug)
				System.out.println("D: data = " + which_data + ", list = "
						+ which_list + ", idx= " + list_idx);
		}
	}

	private int tries = 0;

	// ===========================================================================
	/**
	 * For <code>ActionListener</code> interface, reacts to user selections
	 * and button clicks in this search window.
	 * 
	 */
	public void actionPerformed(ActionEvent e) {

		if (FILTER_WINDOW_SELECT_BC_LIST.equals(e.getActionCommand())) {
			JComboBox box = (JComboBox) e.getSource();
			String item = (String) box.getSelectedItem();
			updateLabel_BiclusterListSelection(item);
		}

		else if (FILTER_WINDOW_APPLY.equals(e.getActionCommand())) {

			if (which_list == -1) {
				JOptionPane
						.showMessageDialog(null,
								"Please choose the bicluster list\nand values for the gene and chip count.");
				// System.out.println("Select something! (Canceling)");
				tries++;
				if (tries == 2) {
					dialog.setVisible(false);
					dialog.dispose();
				}
			}

			else {
				int minGs = (new Integer(minGsField.getText())).intValue();
				int maxGs = (new Integer(maxGsField.getText())).intValue();
				int minCs = (new Integer(minCsField.getText())).intValue();
				int maxCs = (new Integer(maxCsField.getText())).intValue();
				int nrBCs = (new Integer(bcNumber.getText())).intValue();
				int ovrlp = (new Integer(overlap.getText())).intValue();

				// catch too high numbers of genes and conditions
				int limit_G = ((Dataset) owner.datasetList.get(which_data))
						.getGeneCount();
				int limit_C = ((Dataset) owner.datasetList.get(which_data))
						.getWorkingChipCount();
				if (minGs > limit_G)
					JOptionPane.showMessageDialog(null,
							"The minimun gene count is too large");
				else if (minCs > limit_C)
					JOptionPane.showMessageDialog(null,
							"The minimun chip count is too large");
				else
					try {
						owner.filter(which_data, which_list, list_idx, minGs,
								maxGs, minCs, maxCs, nrBCs, ovrlp);
					} catch (NumberFormatException ee) {
						BicatError.errorMessage(ee);
					}

				dialog.setVisible(false);
				dialog.dispose();
			}
		}

		else if (FILTER_WINDOW_CANCEL.equals(e.getActionCommand())) {
			// close window without doing anything
			dialog.setVisible(false);
			dialog.dispose();
		}

		else
			System.out.println("unknown event: " + e.paramString());
	}
}
