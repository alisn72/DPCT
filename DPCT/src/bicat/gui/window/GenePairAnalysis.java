package bicat.gui.window;

import bicat.gui.BicatGui;
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

public class GenePairAnalysis implements ActionListener {

	private BicatGui owner;

	private JDialog dialog;

	public static final String GENE_PAIR_ANALYSIS_WINDOW_APPLY = "gpa_apply";

	public static final String GENE_PAIR_ANALYSIS_WINDOW_CANCEL = "gpa_cancel";

	public static final String GENE_PAIR_ANALYSIS_WINDOW_BY_COOCURRENCE = "gpa_by_coocurrence";

	public static final String GENE_PAIR_ANALYSIS_WINDOW_BY_COMMON_CHIPS = "gpaby_common_chips";

	public static final String GENE_PAIR_ANALYSIS_WINDOW_SELECT_BC_LIST = "gpa_select_bc_list";

	public static final int GENE_PAIR_ANALYSIS_WINDOW_DEFAULT_SCORE_VALUE = 1;

	int which_list = -1;

	int list_idx = -1;

	int which_data = -1;

	JFormattedTextField scoreCoocurrenceField;

	JFormattedTextField scoreCommonChipsField;

	JCheckBox coocLabel;

	JCheckBox commLabel;
	
	private boolean bcListSelected = false;

	// ===========================================================================
	public GenePairAnalysis() {
	}

	public GenePairAnalysis(BicatGui o) {
		owner = o;
	}

	boolean byCooc = true;

	// ===========================================================================
	/**
	 * For <code>ActionListener</code> interface, reacts to user selections
	 * and button clicks in this search window.
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		
		if (GENE_PAIR_ANALYSIS_WINDOW_SELECT_BC_LIST.equals(e
				.getActionCommand())) {
			JComboBox box = (JComboBox) e.getSource();
			String item = (String) box.getSelectedItem();
			updateLabel_BiclusterListSelection(item);
			bcListSelected = true;
		}

		else if (GENE_PAIR_ANALYSIS_WINDOW_BY_COOCURRENCE.equals(e
				.getActionCommand())) {
			byCooc = !byCooc;
			if (byCooc) {
				coocLabel.setEnabled(true);
				coocLabel.setSelected(true);
				commLabel.setEnabled(false);
				commLabel.setSelected(false);
			} else {
				coocLabel.setEnabled(false);
				coocLabel.setSelected(false);
				commLabel.setEnabled(true);
				commLabel.setSelected(true);
			}
		}

		else if (GENE_PAIR_ANALYSIS_WINDOW_BY_COMMON_CHIPS.equals(e
				.getActionCommand())) {
			byCooc = !byCooc;
			if (byCooc) {
				coocLabel.setEnabled(true);
				coocLabel.setSelected(true);
				commLabel.setEnabled(false);
				commLabel.setSelected(false);
			} else {
				coocLabel.setEnabled(false);
				coocLabel.setSelected(false);
				commLabel.setEnabled(true);
				commLabel.setSelected(true);
			}
		}

		else if (GENE_PAIR_ANALYSIS_WINDOW_APPLY.equals(e.getActionCommand())) {

			if (bcListSelected == false)
				JOptionPane
						.showMessageDialog(null,
								"Please choose a bicluster list and\nthe counting mode.");
			else {
				int scoreCooc = (new Integer(scoreCoocurrenceField.getText()))
						.intValue();
				int scoreComm = (new Integer(scoreCommonChipsField.getText()))
						.intValue();

				owner.genePairAnalysis(which_data, which_list, list_idx,
						scoreCooc, scoreComm, byCooc);
				// JOptionPane.showMessageDialog(owner, "GPA started");
				dialog.setVisible(false);
				dialog.dispose();
				JOptionPane.showMessageDialog(null, "The GPA results can be found in the\nanalysis view of the corresponding dataset\n(please open the analysis result folder and click on the results)");  
				 
			}
		}

		else if (GENE_PAIR_ANALYSIS_WINDOW_CANCEL.equals(e.getActionCommand())) {
			dialog.setVisible(false);
			dialog.dispose();
		}

		else
			System.out.println("unknown event: " + e.paramString());
	}

	// ===========================================================================
	private void updateLabel_BiclusterListSelection(String item) {

		int[] info = BicatUtil.getListAndIdx(item);
		which_list = info[0];
		list_idx = info[1];
		which_data = info[2];

		if (info[0] == -1) {
			JOptionPane.showMessageDialog(null,
					"Please choose a bicluster list and\nthe counting mode.");
			//System.out.println("Select something! (Canceling)");
			dialog.setVisible(false);
			dialog.dispose();
		} else
			owner.updateCurrentDataset(which_data);
	}

	// ===========================================================================
	/**
	 * Creates the visible pop up window with all buttons, labels and fields.
	 * 
	 */
	public void makeWindow() {

		dialog = new JDialog(owner, "GPA Setup Dialog ");

		// //////////////////////////////////////////////////////////////////////////

		JPanel top = new JPanel(new FlowLayout());

		Vector names = owner.getListNamesAll();
		JComboBox cb = new JComboBox(names);
		cb.setActionCommand(GENE_PAIR_ANALYSIS_WINDOW_SELECT_BC_LIST);
		cb.setSelectedIndex(0);
		cb.setAlignmentX(JComboBox.CENTER_ALIGNMENT);
		cb.addActionListener(this);

		top.add(cb);

		// //////////////////////////////////////////////////////////////////////////

		JPanel byCooc = new JPanel(new GridLayout(0, 2));
		byCooc.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.black), "By coocurrence count"));

		coocLabel = new JCheckBox("Minimum for coocurrence score of genes",
				true);
		coocLabel.setActionCommand(GENE_PAIR_ANALYSIS_WINDOW_BY_COOCURRENCE);
		coocLabel.setEnabled(true);
		coocLabel.addActionListener(this);
		byCooc.add(coocLabel);

		scoreCoocurrenceField = new JFormattedTextField(NumberFormat
				.getIntegerInstance());
		scoreCoocurrenceField.setValue(new Integer(
				GENE_PAIR_ANALYSIS_WINDOW_DEFAULT_SCORE_VALUE));
		byCooc.add(scoreCoocurrenceField);

		// //////////////////////////////////////////////////////////////////////////

		JPanel byCommon = new JPanel(new GridLayout(0, 2));
		byCommon.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.black), "By common chips Count"));

		commLabel = new JCheckBox("Minimum for common chips score of genes",
				false); // new JLabel(" Lower Bound on the Common Chips Score
		// ");
		commLabel.setActionCommand(GENE_PAIR_ANALYSIS_WINDOW_BY_COMMON_CHIPS);
		commLabel.setEnabled(false);
		commLabel.addActionListener(this);
		byCommon.add(commLabel);

		scoreCommonChipsField = new JFormattedTextField(NumberFormat
				.getIntegerInstance());
		scoreCommonChipsField.setValue(new Integer(
				GENE_PAIR_ANALYSIS_WINDOW_DEFAULT_SCORE_VALUE));
		byCommon.add(scoreCommonChipsField);

		// //////////////////////////////////////////////////////////////////////////

		JPanel buttonPanel = new JPanel(new FlowLayout());

		JButton okayButton = new JButton("OK");
		okayButton.setMnemonic(KeyEvent.VK_K);
		okayButton.setActionCommand(GENE_PAIR_ANALYSIS_WINDOW_APPLY);
		okayButton.addActionListener(this);
		buttonPanel.add(okayButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.setActionCommand(GENE_PAIR_ANALYSIS_WINDOW_CANCEL);
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);

		// //////////////////////////////////////////////////////////////////////////

		JPanel contentPane = new JPanel(new GridLayout(0, 1)); // BorderLayout());
		contentPane.add(top);
		contentPane.add(byCooc);
		contentPane.add(byCommon);
		contentPane.add(buttonPanel);

		dialog.setContentPane(contentPane);

		// set size, location and make visible
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
	}

}
