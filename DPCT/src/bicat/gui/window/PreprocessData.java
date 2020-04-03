package bicat.gui.window;

import bicat.gui.BicatGui;
import bicat.preprocessor.PreprocessOption;

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
 * @author D. Frick, A. Prelic
 * @version 1.0
 * 
 */

public class PreprocessData implements ActionListener {

	BicatGui owner;

	JDialog dialog;

	// options to pass to my owner
	static PreprocessOption preprocessOption;

	// i need these labels, since i will accordingly enable and disable them!
	JFormattedTextField log_base;

	JLabel log_label;

	JLabel n_label;

	JComboBox n_combo;

	JFormattedTextField d_field_thr;

	JFormattedTextField d_field_perc;

	JComboBox d_combo_discr_scheme;

	JComboBox d_combo_perc;

	JLabel d_label_0_perc;

	JLabel d_label_1_thr;

	JLabel d_label_2_discr_scheme;

	// to have a clean interface:
	static final String PREPROCESS_DATA_WINDOW_USE_DEFAULT = "default_preprocessing";

	static final String PREPROCESS_DATA_WINDOW_APPLY = "apply_preprocessing";

	static final String PREPROCESS_DATA_WINDOW_CANCEL = "cancel_preprocessing";

	static final String PREPROCESS_DATA_WINDOW_RATIO = "compute_ratio";

	static final String PREPROCESS_DATA_WINDOW_LOGARITHM = "compute_logarithm";

	static final String PREPROCESS_DATA_WINDOW_SET_LOGARITHM_BASE = "set_log_base";

	static final String PREPROCESS_DATA_WINDOW_NORMALIZE_GENES = "normalize_genes";

	static final String PREPROCESS_DATA_WINDOW_NORMALIZE_CHIPS = "normalize_chips";

	static final String PREPROCESS_DATA_WINDOW_CHOOSE_NORMALIZATION_SCHEME = "choose_norm_scheme";

	static final String PREPROCESS_DATA_WINDOW_DISCRETIZE = "discretize";

	static final String PREPROCESS_DATA_WINDOW_ONES_PERCENTAGE = "ones_percentage";

	static final String PREPROCESS_DATA_WINDOW_CHOOSE_DISCRETIZATION_SCHEME = "choose_discr_scheme";

	boolean norm_gs;

	boolean norm_cs;

	private JCheckBox cb_thr;

	private JCheckBox cb_perc;

	// ===========================================================================
	public PreprocessData() {
	}

	public PreprocessData(BicatGui o) {
		owner = o;
		preprocessOption = new PreprocessOption("default");
	}

	// ===========================================================================
	protected void updateLabel_normalization(String item) {
		/*
		 * normalize_items.add("Mean centring (0,1)");
		 * normalize_items.add("Huber norm"); normalize_items.add("Philip Z.
		 * norm"); normalize_items.add("Binarization 1 - Mixture model");
		 * normalize_items.add("Binarization 2 - Norm, InfoTheory");
		 */

		if (item.equals("Mean centring (0,1)"))
			preprocessOption
					.setNormalizationScheme(PreprocessOption.PREPROCESS_OPTIONS_NORMALIZATION_MEAN_CENTRING);

		// else if(item.equals("Huber norm"))
		// preprocessOption.setNormalizationScheme(PreprocessOption.PREPROCESS_OPTIONS_NORMALIZATION_HUBER);

		// else if(item.equals("Philip Z. norm"))
		// preprocessOption.setNormalizationScheme(PreprocessOption.PREPROCESS_OPTIONS_NORMALIZATION_PHILIP);

		else if (item.equals("Binarization 1 - Mixture Model"))
			preprocessOption
					.setNormalizationScheme(PreprocessOption.PREPROCESS_OPTIONS_NORMALIZATION_MIXTURE);

		else if (item.equals("Binarization 2 - Norm, InfoTheory"))
			preprocessOption
					.setNormalizationScheme(PreprocessOption.PREPROCESS_OPTIONS_NORMALIZATION_IT);
	}

	// ===========================================================================
	protected void updateLabel_discretization(String item) {

		/*
		 * discretize_items.add("Changed patterns"); //Expressed");
		 * discretize_items.add("Down-regulated patterns"); //Under-expressed
		 * only"); discretize_items.add("Up-regulated patterns");
		 * //Over-expressed only"); discretize_items.add("Complementary
		 * patterns"); //Co-expressed");
		 */
		if (item.equals("Changed patterns")) {
			if (BicatGui.debug)
				System.out.println("4, changed");
			preprocessOption
					.setDiscretizationScheme(PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_CHANGE);
		} else if (item.equals("Up-regulated patterns")) {
			if (BicatGui.debug)
				System.out.println("1, up");
			preprocessOption
					.setDiscretizationScheme(PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_UP);
		} else if (item.equals("Down-regulated patterns")) {
			if (BicatGui.debug)
				System.out.println("2, down");
			preprocessOption
					.setDiscretizationScheme(PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_DOWN);
		} else if (item.equals("Complementary patterns")) {
			if (BicatGui.debug)
				System.out.println("3, coex");
			preprocessOption
					.setDiscretizationScheme(PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_COEXPRESSED);
		}

	}

	// ===========================================================================
	/**
	 * For <code>ActionListener</code> interface, reacts to user selections
	 * and button clicks in this search window.
	 * 
	 */
	public void actionPerformed(ActionEvent e) {

		// if (PREPROCESS_DATA_WINDOW_USE_DEFAULT.equals(e.getActionCommand()))
		// {
		//
		// // preprocessOption = new PreprocessOptions("default");
		// log_label.setEnabled(true);
		// log_base.setEnabled(true);
		//
		// n_label.setEnabled(true);
		// n_combo.setEnabled(true);
		//
		// d_label_1.setEnabled(true);
		// d_field.setEnabled(true);
		// d_label_0.setEnabled(true); // Percentage
		// d_fieldP.setEnabled(true);
		// d_label_2.setEnabled(true);
		// d_combo.setEnabled(true);
		// }

		if (PREPROCESS_DATA_WINDOW_APPLY.equals(e.getActionCommand())) {

			if (d_label_1_thr.isEnabled() == false
					&& d_label_0_perc.isEnabled() == false) {
				preprocessOption.resetDiscretization();
			}

			if (d_label_1_thr.isEnabled() == true) {
				String discretizationThresholdString = d_field_thr.getText();
				discretizationThresholdString = discretizationThresholdString
						.replaceAll("'", "");
				double discretizationThreshold = Double
						.parseDouble(discretizationThresholdString);
				preprocessOption
						.setDiscretizationThreshold(discretizationThreshold);
				preprocessOption.setDiscretizationMode("threshold");
			}

			if (d_label_0_perc.isEnabled() == true) {
				String onesPercentageString = d_field_perc.getText();
				onesPercentageString = onesPercentageString.replaceAll("'", "");
				int onesPercentage = (int) Double
						.parseDouble(onesPercentageString);
				preprocessOption.setOnesPercentage(onesPercentage);
				preprocessOption.setDiscretizationMode("onesPercentage");
			}

			System.out.println("APPLY preprocessing");

			preprocessOption.setLogarithmBase(new Integer(log_base.getText())
					.intValue());

			owner.setPreprocessRun(preprocessOption);

			dialog.setVisible(false);
			dialog.dispose();
		}
		// }

		else if (PREPROCESS_DATA_WINDOW_CANCEL.equals(e.getActionCommand())) {
			dialog.setVisible(false);
			dialog.dispose();
		}

		else if (PREPROCESS_DATA_WINDOW_RATIO.equals(e.getActionCommand())) {
			if (preprocessOption.do_compute_ratio)
				preprocessOption.resetComputeRatio();
			else
				preprocessOption.setComputeRatio();
		}

		else if (PREPROCESS_DATA_WINDOW_LOGARITHM.equals(e.getActionCommand())) {
			if (preprocessOption.do_compute_logarithm) {
				preprocessOption.resetComputeLogarithm();

				// log_label.setEnabled(false);
				log_base.setEnabled(false);
			} else {
				// @todo check if the data is already logarithmized

				preprocessOption.setComputeLogarithm();

			}

			// log_label.setEnabled(true);
			log_base.setEnabled(true);

		}

		else if (PREPROCESS_DATA_WINDOW_SET_LOGARITHM_BASE.equals(e
				.getActionCommand())) {
			// preprocessOption.setLogarithmBase(new
			// Integer(log_base.getText()).intValue());
			preprocessOption.setLogarithmBase(((Integer) log_base.getValue())
					.intValue());
		}

		else if (PREPROCESS_DATA_WINDOW_NORMALIZE_GENES.equals(e
				.getActionCommand())) {

			if (preprocessOption.do_normalize) {

				if (norm_cs) {
					norm_gs = !norm_gs;
					if (norm_gs)
						preprocessOption.setGeneNormalization();
					else
						preprocessOption.resetGeneNormalization();
				} else {
					n_label.setEnabled(false);
					n_combo.setEnabled(false);

					norm_gs = false;
					preprocessOption.resetNormalization();
					preprocessOption.resetGeneNormalization();
				}
			} else {
				n_label.setEnabled(true);
				n_combo.setEnabled(true);

				norm_gs = true;
				preprocessOption.setNormalization();
				preprocessOption.setGeneNormalization();
			}
		}

		else if (PREPROCESS_DATA_WINDOW_NORMALIZE_CHIPS.equals(e
				.getActionCommand())) {

			if (preprocessOption.do_normalize) {
				if (norm_gs) {
					norm_cs = !norm_cs;
					if (norm_cs)
						preprocessOption.setChipNormalization();
					else
						preprocessOption.resetChipNormalization();
				} else {
					n_label.setEnabled(false);
					n_combo.setEnabled(false);

					norm_cs = false;
					preprocessOption.resetNormalization();
					preprocessOption.resetChipNormalization();
				}
			} else {
				n_label.setEnabled(true);
				n_combo.setEnabled(true);

				norm_cs = true;
				preprocessOption.setNormalization();
				preprocessOption.setChipNormalization();
			}
		}

		else if (PREPROCESS_DATA_WINDOW_CHOOSE_NORMALIZATION_SCHEME.equals(e
				.getActionCommand())) {
			JComboBox box = (JComboBox) e.getSource();
			String item = (String) box.getSelectedItem();
			updateLabel_normalization(item);
		}

		else if (PREPROCESS_DATA_WINDOW_DISCRETIZE.equals(e.getActionCommand())) {

			if (cb_perc.isSelected()) {
				cb_perc.setSelected(false);
				d_label_0_perc.setEnabled(false);
				d_field_perc.setEnabled(false);
				d_label_1_thr.setEnabled(true); // Percentage
				d_field_thr.setEnabled(true);
			} else {
				if (preprocessOption.do_discretize) {
					d_label_1_thr.setEnabled(false);
					d_field_thr.setEnabled(false);
					d_label_0_perc.setEnabled(false);
					d_field_perc.setEnabled(false);
					d_label_2_discr_scheme.setEnabled(false);
					d_combo_discr_scheme.setEnabled(false);
					preprocessOption.resetDiscretization();
				} else {
					preprocessOption.setDiscretizationTrue();
					d_label_1_thr.setEnabled(true);
					d_field_thr.setEnabled(true);
					d_label_2_discr_scheme.setEnabled(true);
					d_combo_discr_scheme.setEnabled(true);
				}

			}
		} else if (PREPROCESS_DATA_WINDOW_ONES_PERCENTAGE.equals(e
				.getActionCommand())) {

			if (cb_thr.isSelected()) {
				cb_thr.setSelected(false);
				d_label_1_thr.setEnabled(false);
				d_field_thr.setEnabled(false);
				d_label_0_perc.setEnabled(true); // Percentage
				d_field_perc.setEnabled(true);
			} else {
				if (preprocessOption.do_discretize) {
					d_label_1_thr.setEnabled(false);
					d_field_thr.setEnabled(false);
					d_label_0_perc.setEnabled(false);
					d_field_perc.setEnabled(false);
					d_label_2_discr_scheme.setEnabled(false);
					d_combo_discr_scheme.setEnabled(false);
					preprocessOption.resetDiscretization();
				} else {
					preprocessOption.setDiscretizationTrue();
					d_label_0_perc.setEnabled(true);
					d_field_perc.setEnabled(true);
					d_label_2_discr_scheme.setEnabled(true);
					d_combo_discr_scheme.setEnabled(true);
				}
			}
		}	
		
		else if (PREPROCESS_DATA_WINDOW_CHOOSE_DISCRETIZATION_SCHEME.equals(e
				.getActionCommand())) {
			JComboBox box = (JComboBox) e.getSource();
			String item = (String) box.getSelectedItem();
			updateLabel_discretization(item);
		}

	}

	// ===========================================================================
	public void makeWindow() {

		preprocessOption = new PreprocessOption();

		dialog = new JDialog(owner, "Preprocess Setup Dialog");
		
		// *-*-*-* //

		JPanel normalize = new JPanel(new GridLayout(0, 1));
		normalize.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.black), "Normalization"));

		JPanel log_choice = new JPanel(new GridLayout(1, 2));

		JCheckBox cb = new JCheckBox("Compute log data with base ");
		cb.setAlignmentX(JCheckBox.LEFT_ALIGNMENT);
		cb.setActionCommand(PREPROCESS_DATA_WINDOW_LOGARITHM);
		cb.addActionListener(this);
		log_choice.add(cb);

		log_base = new JFormattedTextField(NumberFormat.getIntegerInstance());
		log_base.setValue(new Integer(2));
		log_base.setAlignmentX(JFormattedTextField.CENTER_ALIGNMENT);
		log_base.setEnabled(false);
		log_base.addActionListener(this);
		log_base.setActionCommand(PREPROCESS_DATA_WINDOW_SET_LOGARITHM_BASE);
		log_choice.add(log_base);

		normalize.add(log_choice);

		// *-*-*-* //

		cb = new JCheckBox("Normalize genes");
		cb.setActionCommand(PREPROCESS_DATA_WINDOW_NORMALIZE_GENES);
		cb.setAlignmentX(JCheckBox.LEFT_ALIGNMENT);
		cb.addActionListener(this);
		normalize.add(cb);

		cb = new JCheckBox("Normalize chips");
		cb.setActionCommand(PREPROCESS_DATA_WINDOW_NORMALIZE_CHIPS);
		cb.setAlignmentX(JCheckBox.LEFT_ALIGNMENT);
		cb.addActionListener(this);
		normalize.add(cb);

		norm_gs = false;
		norm_cs = false;

		JPanel norm_choice = new JPanel(new GridLayout(0, 2));

		n_label = new JLabel("      Set normalization scheme: ");
		n_label.setEnabled(false);
		n_label.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		norm_choice.add(n_label);

		Vector normalize_items = new Vector();
		normalize_items.add("Mean centring (0,1)");
		/**
		 * EVENTUELL: normalize_items.add("Binarization 1 - Mixture model");
		 * normalize_items.add("Binarization 2 - Norm, InfoTheory");
		 */

		n_combo = new JComboBox(normalize_items);
		n_combo.setSelectedIndex(0);
		n_combo
				.setActionCommand(PREPROCESS_DATA_WINDOW_CHOOSE_NORMALIZATION_SCHEME);
		n_combo.addActionListener(this);
		n_combo.setEnabled(false);
		norm_choice.add(n_combo);

		normalize.add(norm_choice);

		// *-*-*-* //

		JPanel discretize = new JPanel(new GridLayout(0, 1));
		discretize.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.black), "Discretization"));

		cb_thr = new JCheckBox("Discretize (to binary values) by threshold");
		cb_thr.setActionCommand(PREPROCESS_DATA_WINDOW_DISCRETIZE);
		cb_thr.setAlignmentX(JCheckBox.LEFT_ALIGNMENT);
		cb_thr.addActionListener(this);
		discretize.add(cb_thr);

		cb_perc = new JCheckBox("Discretize (to binary values) by percentage");
		cb_perc.setActionCommand(PREPROCESS_DATA_WINDOW_ONES_PERCENTAGE);
		cb_perc.setAlignmentX(JCheckBox.LEFT_ALIGNMENT);
		cb_perc.addActionListener(this);
		discretize.add(cb_perc);

		JPanel d_choice_1 = new JPanel(new GridLayout(2, 2, 8, 2));
		d_label_0_perc = new JLabel("      Set percentage of ones: ");
		d_label_0_perc.setEnabled(false);
		d_label_0_perc.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		d_choice_1.add(d_label_0_perc);

		d_field_perc = new JFormattedTextField(NumberFormat.getNumberInstance());
		d_field_perc.setValue(new Double(10.0));
		d_field_perc.addActionListener(this);
		d_field_perc.setEnabled(false);
		d_choice_1.add(d_field_perc);

		d_label_1_thr = new JLabel("      Set discretization threshold: ");
		d_label_1_thr.setEnabled(false);
		d_label_1_thr.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		d_choice_1.add(d_label_1_thr);

		d_field_thr = new JFormattedTextField(NumberFormat.getNumberInstance());
		d_field_thr.setValue(new Double(2.0));
		d_field_thr.addActionListener(this);
		d_field_thr.setEnabled(false);
		d_choice_1.add(d_field_thr);

		discretize.add(d_choice_1);

		JPanel d_choice_2 = new JPanel(new GridLayout(1, 2, 6, 2));

		d_label_2_discr_scheme = new JLabel(
				"      Select discretization scheme: ");
		d_label_2_discr_scheme.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		d_label_2_discr_scheme.setEnabled(false);
		d_choice_2.add(d_label_2_discr_scheme); // , BorderLayout.NORTH);

		Vector discretize_items = new Vector();
		discretize_items.add("Changed patterns"); // Expressed");
		discretize_items.add("Down-regulated patterns"); // Under-expressed
		// only");
		discretize_items.add("Up-regulated patterns"); // Over-expressed
		// discretize_items.add("Complementary patterns"); // Co-expressed");

		// only");

		d_combo_discr_scheme = new JComboBox(discretize_items);
		d_combo_discr_scheme
				.setActionCommand(PREPROCESS_DATA_WINDOW_CHOOSE_DISCRETIZATION_SCHEME);
		d_combo_discr_scheme.setSelectedIndex(2);
		d_combo_discr_scheme.addActionListener(this);
		d_combo_discr_scheme.setEnabled(false);
		d_choice_2.add(d_combo_discr_scheme); // , BorderLayout.SOUTH);

		discretize.add(d_choice_2);

		// *-*-*-* //

		JPanel p1 = new JPanel(new FlowLayout());

		JButton okButton = new JButton("Apply");
		okButton.setActionCommand(PREPROCESS_DATA_WINDOW_APPLY);
		okButton.addActionListener(this);
		p1.add(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand(PREPROCESS_DATA_WINDOW_CANCEL);
		cancelButton.addActionListener(this);
		p1.add(cancelButton);

		// *-*-*-* //

		JPanel contentPane = new JPanel(new FlowLayout()); // new
		// GridLayout(0,1));

		contentPane.add(normalize); // normalization panel,
		contentPane.add(discretize); // discretization panel
		contentPane.add(p1); // two end buttons

		contentPane.setPreferredSize(new Dimension(428, 300)); // 500,300));

		dialog.setContentPane(contentPane);

		dialog.setLocationRelativeTo(owner);
		dialog.pack();
		dialog.setSize(430,420);
		dialog.setVisible(true);

		// *-*-*-* //
	}

}
