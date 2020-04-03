package bicat.gui.window;

import bicat.gui.BicatGui;
import bicat.util.BicatError;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Properties;

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

public class LoadData implements ActionListener {

	BicatGui owner;

	JDialog dialog;

	private JFormattedTextField cfo_field;

	private JFormattedTextField rfo_field;

	private JTextField f_field;

	private File file;

	private static int DEFAULT_FILE_OFFSET = 1;

	// ===========================================================================

	public static final String LOAD_DATA_WINDOW_APPLY = "ld_apply";

	public static final String LOAD_DATA_WINDOW_CANCEL = "ld_cancel";

	public static final String LOAD_DATA_WINDOW_BROWSE_DATA_MATRIX_FILE = "ld_browse_file_main";

	// ===========================================================================
	public LoadData() {
	}

	public LoadData(BicatGui o) {
		owner = o;
	}

	final File propFile = new File(System.getProperty("user.home"),
			".bicat.lastfile");

	final String lastFilePropertyName = "last-file";

	// ===========================================================================
	public void makeWindow() {

		dialog = new JDialog(owner, "Load Data");

		// //////////////////////////////////////////////////////////////////////////

		JPanel panel_1 = new JPanel(new GridLayout(0, 2));
		panel_1.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.black), "Input File Format "));

		JLabel fo_label = new JLabel(
				"        Set column offset (column(s) with gene names): ");
		fo_label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		panel_1.add(fo_label);

		cfo_field = new JFormattedTextField(NumberFormat.getIntegerInstance());
		cfo_field.setValue(new Integer(DEFAULT_FILE_OFFSET));
		cfo_field.setColumns(6);
		cfo_field.addActionListener(this);
		panel_1.add(cfo_field);

		fo_label = new JLabel(
				"        Set row offset (row(s) with chip names): ");
		fo_label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		panel_1.add(fo_label);

		rfo_field = new JFormattedTextField(NumberFormat.getIntegerInstance());
		rfo_field.setValue(new Integer(DEFAULT_FILE_OFFSET));
		rfo_field.setColumns(6);
		rfo_field.addActionListener(this);
		panel_1.add(rfo_field);

		// //////////////////////////////////////////////////////////////////////////

		JPanel panel_2 = new JPanel(new FlowLayout());
		panel_2.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.black), "File Selection"));

		JLabel fileOne = new JLabel("Select data file: ");
		fileOne.setOpaque(true);
		panel_2.add(fileOne);

		// Show default file ""
		f_field = new JTextField(
				"C:/TEMP/100_20.txt",
				28);
		panel_2.add(f_field);

		JButton browse = new JButton("Browse...");
		browse.setActionCommand(LOAD_DATA_WINDOW_BROWSE_DATA_MATRIX_FILE);
		browse.addActionListener(this);
		panel_2.add(browse);

		// //////////////////////////////////////////////////////////////////////////

		JPanel lowest = new JPanel(new FlowLayout());

		JButton okButton = new JButton("Load data");
		okButton.setActionCommand(LOAD_DATA_WINDOW_APPLY);
		okButton.addActionListener(this);
		lowest.add(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand(LOAD_DATA_WINDOW_CANCEL);
		cancelButton.addActionListener(this);
		lowest.add(cancelButton);

		// //////////////////////////////////////////////////////////////////////////

		JPanel contentPane = new JPanel(new GridLayout(0, 1));

		contentPane.add(panel_1);
		contentPane.add(panel_2);
		contentPane.add(lowest);

		contentPane.setOpaque(true);
		dialog.setContentPane(contentPane);

		// ----
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

		if (LOAD_DATA_WINDOW_APPLY.equals(e.getActionCommand())) {

			try {
				int columnFileOffset = (new Integer(cfo_field.getText()))
						.intValue();
				int rowFileOffset = (new Integer(rfo_field.getText()))
						.intValue();

				if (null == file) {
					String fileName = f_field.getText();
					file = new File(fileName); // BicatGui.currentDirectoryPath
					// + "/" +
				}
				if (columnFileOffset > 0 & rowFileOffset > 0) {
					owner.loadData(file, columnFileOffset, rowFileOffset);
				} else {
					BicatError.offsetError();
				}
				dialog.setVisible(false);
				dialog.dispose();

			} catch (NumberFormatException ee) {
				BicatError.wrongOffsetError();
			} catch (Exception ee) {
				BicatError.errorMessage(ee, owner, true, "Gene and chip names must not contain spaces.\n\n");
//				dialog.setVisible(false);
//				dialog.dispose();
			}
		}

		else if (LOAD_DATA_WINDOW_CANCEL.equals(e.getActionCommand())) {
			dialog.setVisible(false);
			dialog.dispose();
		}

		else if (LOAD_DATA_WINDOW_BROWSE_DATA_MATRIX_FILE.equals(e
				.getActionCommand())) {

			if (BicatGui.debug)
				System.out.println("Current working directory is: "
						+ BicatGui.currentDirectoryPath);

			JFileChooser jfc = new JFileChooser(BicatGui.currentDirectoryPath);
			jfc.setMultiSelectionEnabled(true);
			jfc.setDragEnabled(true);
			jfc.setDialogTitle("Select main data file:");
			try {
				Properties prop = new Properties();
				prop.load(new FileInputStream(propFile));
				if (prop.containsKey(lastFilePropertyName)) {
					File selFile = new File(prop
							.getProperty(lastFilePropertyName));
					jfc.setSelectedFile(selFile);
				}
			} catch (IOException ex) {
				// could not fetch last file from properties file
				// ignore exception
				ex.printStackTrace();
			}

			if (jfc.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
				file = jfc.getSelectedFile();
				Properties prop = new Properties();
				prop.setProperty(lastFilePropertyName, file.getAbsolutePath());
				try {
					prop.store(new FileOutputStream(propFile),
							lastFilePropertyName);
				} catch (IOException ex) {
					// ignore
					ex.printStackTrace();
				}
				f_field.setText(file.getName());
			} else
				return; 
		}

		else {
			System.out
					.println("What action is this? (LoadData.actionPerformed())");
		}
	}

}
