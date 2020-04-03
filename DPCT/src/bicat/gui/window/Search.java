package bicat.gui.window;

import bicat.gui.BicatGui;
import bicat.util.BicatUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author D. Frick, A. Prelic
 * @version 1.0
 *
 **/

/**
 * Pop-up window that prompts user for search parameters.
 * 
 * Gets user input for a search of the bicluster list.
 * 
 */
public class Search implements ActionListener {

	JDialog dialog;

	BicatGui owner;

	public final String SEARCH_WINDOW_APPLY = "start_search";

	public final String SEARCH_WINDOW_CANCEL = "cancel_search";

	public final String SEARCH_WINDOW_SEARCH_AND = "search_and";

	public final String SEARCH_WINDOW_SEARCH_OR = "search_or";

	public final String SEARCH_WINDOW_SELECT_BC_LIST = "select_BC_list";

	static int list_idx = 0;

	static int which_list = -1; // 0 def BCs, 1 def Search Results, ...

	static int which_data = 0;

	boolean andSearch;

	JTextField geneNameField;

	JTextField chipNameField;

	// ===========================================================================
	/**
	 * Basic constructor, requires a handle to governing frame.
	 * 
	 */
	public Search(BicatGui o) {
		owner = o;
	}

	// ===========================================================================
	private void updateLabel_ListSelection(String item) {

		int[] info = BicatUtil.getListAndIdx(item);
		which_data = info[2];
		which_list = info[0];
		list_idx = info[1];

		if (info[0] == -1) {
			//System.out.println("Select something! (Canceling)");
			dialog.setVisible(false);
			dialog.dispose();
		} else {
			owner.updateCurrentDataset(which_data);
		}
	}

	// ===========================================================================
	/**
	 * For <code>ActionListener</code> interface, reacts to user selections
	 * and button clicks in this search window.
	 * 
	 */
	public void actionPerformed(ActionEvent e) {

		if (SEARCH_WINDOW_SEARCH_AND.equals(e.getActionCommand())) {
			andSearch = true;
		}

		else if (SEARCH_WINDOW_SEARCH_OR.equals(e.getActionCommand())) {
			andSearch = false;
		}

		else if (SEARCH_WINDOW_SELECT_BC_LIST.equals(e.getActionCommand())) {
			JComboBox box = (JComboBox) e.getSource();
			String item = (String) box.getSelectedItem();
			updateLabel_ListSelection(item);
		}

		else if (SEARCH_WINDOW_CANCEL.equals(e.getActionCommand())) {
			dialog.setVisible(false);
			dialog.dispose();
		}

		else if (SEARCH_WINDOW_APPLY.equals(e.getActionCommand())) {

			String geneStr = geneNameField.getText();
			String chipStr = chipNameField.getText();

			if (which_list == -1) {
				JOptionPane
						.showMessageDialog(null,
								"Please choose a bicluster list\nin which you would like to search");
			} else {
				owner.search(which_data, which_list, list_idx, geneStr,
						chipStr, andSearch);

				dialog.setVisible(false);
				dialog.dispose();
			}
		}
	}

	// ===========================================================================
	/**
	 * Creates the visible pop up window with all buttons, labels and fields.
	 * 
	 */
	public void makeWindow() {

		dialog = new JDialog(owner, "Search Setup Dialog ");

		// //////////////////////////////////////////////////////////////////////////

		JPanel listChoice = new JPanel(new FlowLayout());
		listChoice.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.black), "Choose list of biclusters"));

		Vector names = owner.getListNamesAll();
		JComboBox cb = new JComboBox(names);
		cb.setActionCommand(SEARCH_WINDOW_SELECT_BC_LIST);
		cb.setSelectedIndex(0);
		cb.addActionListener(this);
		listChoice.add(cb);

		// //////////////////////////////////////////////////////////////////////////

		JPanel buttonPanel = new JPanel(new GridBagLayout());

		JButton okayButton = new JButton("OK");
		okayButton.setMnemonic(KeyEvent.VK_K);
		okayButton.setActionCommand(SEARCH_WINDOW_APPLY);
		okayButton.addActionListener(this);
		buttonPanel.add(okayButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.setActionCommand(SEARCH_WINDOW_CANCEL);
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);

		// //////////////////////////////////////////////////////////////////////////

		JPanel top = new JPanel(new FlowLayout());

		JPanel labelPanel;
		JPanel fieldPane;

		// create gene, chip name panel
		JPanel gcPanel = new JPanel(new BorderLayout());
		gcPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.black), "Gene & Chip constraints"));

		// 1st subpane in gene/chip subpanel containing field descriptions
		labelPanel = new JPanel(new GridLayout(0, 1));
		labelPanel.add(new JLabel("Gene name: "));
		labelPanel.add(new JLabel("Chip name: "));
		labelPanel.add(new JLabel("Choose Search mode: "));
		labelPanel.add(new JLabel(" "));
		gcPanel.add(labelPanel, BorderLayout.CENTER);

		// 2nd subpane in gene/chip subpanel with name entry fields and AND/OR
		// radio buttons
		fieldPane = new JPanel(new GridLayout(0, 1));

		geneNameField = new JTextField(32);
		fieldPane.add(geneNameField);
		chipNameField = new JTextField(32);
		fieldPane.add(chipNameField);

		// radio buttons that let user specify whether to search for BCs
		// containing all names, or all BCs that contain at least one name
		ButtonGroup radioButtonGroup = new ButtonGroup();

		JRadioButton radioButton = new JRadioButton("AND");
		radioButton.setActionCommand(SEARCH_WINDOW_SEARCH_AND);
		radioButton.addActionListener(this);
		radioButton.setSelected(true);
		andSearch = true;
		fieldPane.add(radioButton);
		radioButtonGroup.add(radioButton);

		radioButton = new JRadioButton("OR");
		radioButton.setActionCommand(SEARCH_WINDOW_SEARCH_OR);
		radioButton.addActionListener(this);
		fieldPane.add(radioButton);
		radioButtonGroup.add(radioButton);
		gcPanel.add(fieldPane, BorderLayout.LINE_END);

		top.add(gcPanel);

		// //////////////////////////////////////////////////////////////////////////
		JPanel contentPane = new JPanel(new GridLayout(0, 1));

		contentPane.add(listChoice);
		contentPane.add(top);
		contentPane.add(buttonPanel);

		contentPane.setOpaque(true);
		dialog.setContentPane(contentPane);

		// set size, location and make visible
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
	}

}
