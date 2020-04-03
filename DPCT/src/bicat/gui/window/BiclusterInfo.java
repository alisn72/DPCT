package bicat.gui.window;

import bicat.biclustering.Bicluster;
import bicat.gui.BicatGui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author D. Frick, A. Prelic
 * @version 1.0
 *
 **/

/**
 * Pop up window that provides information on one bicluster.
 */
public class BiclusterInfo implements ActionListener {

	/** Hook to governings <code>BiacGUI</code>. */
	BicatGui owner;

	/** Bicluster that this window shows information on. */
	Bicluster bc;

	/** Dialog window. */
	JDialog dialog;

	// ===========================================================================
	/**
	 * Basic constructor, requires a handle to governing frame.
	 * 
	 * @param o
	 *            handle to governing frame
	 * 
	 */
	public BiclusterInfo(BicatGui o, Bicluster bcluster) {
		owner = o;
		bc = bcluster;
	}

	// ===========================================================================
	/**
	 * For <code>ActionListener</code> interface, reacts to user selections
	 * and button clicks in this search window.
	 * 
	 */
	public void actionPerformed(ActionEvent e) {

		if ("close_info".equals(e.getActionCommand())) {
			// close window without doing anything
			dialog.setVisible(false);
			dialog.dispose();
		} else if ("save_info".equals(e.getActionCommand())) {

			JFileChooser jfc = new JFileChooser(owner.currentDirectoryPath); // open
			// a
			// file
			// chooser
			// dialog
			// window
			jfc.setDialogTitle("Save bicluster information:");
			File file;
			int returnVal = jfc.showOpenDialog(owner);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					file = jfc.getSelectedFile();
					FileWriter fw = new FileWriter(file);
					String writeBuffer = new String(bc.toString()
							+ "\n\nGenes:\n");
					int[] genes = bc.getGenes();
					int[] chips = bc.getChips();
					for (int i = 0; i < genes.length; i++)
						writeBuffer += owner.currentDataset
								.getGeneName(genes[i])
								+ "\n";
					// pre.getGeneName(genes[i]) + "\n";
					writeBuffer += "\n\nChips:\n";
					for (int i = 0; i < chips.length; i++)
						writeBuffer += owner.currentDataset
								.getWorkingChipName(chips[i])
								+ "\n";
					// pre.getChipName(chips[i]) + "\n";
					writeBuffer += "\n";

					fw.write(writeBuffer);
					fw.close();

					// close window
					dialog.setVisible(false);
					dialog.dispose();
				} catch (IOException ioe) {
					System.err.println(ioe);
				}
			} else
				System.out.println("unknown event: " + e.paramString());

		}
	}

	// ===========================================================================
	/**
	 * Creates and shows the window.
	 * 
	 */
	public void makeWindow() {

		int[] genes = bc.getGenes();
		int[] chips = bc.getChips();

		// new dialog window with general information on bicluster
		dialog = new JDialog(owner, "Bicluster information");

		// create top panel with general information on bicluster (SHOULD
		// CORRECT THIS... nije idealno!.. 300404)
		JPanel topPanel = new JPanel(new FlowLayout());
		topPanel.add(new JLabel(bc.toString() + " [" + bc.getGenes().length
				+ " genes and " + bc.getChips().length + " chips]"));

		// create gene subpanel

		JTextPane geneTextPane = new JTextPane();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < genes.length; i++)
			sb.append((owner.currentDataset.getGeneName(genes[i])) + "\n");
		// gPanel.setText((owner.currentDataset.getGeneName(genes[i])));
		// //pre.getGeneName(genes[i])));
		geneTextPane.setBorder(BorderFactory.createTitledBorder("Genes:"));
		geneTextPane.setText(sb.toString());

		// create chip subpanel
		JTextPane cPane = new JTextPane(); // new GridLayout(0,1) );
		sb = new StringBuffer();
		for (int i = 0; i < chips.length; i++)
			sb.append((owner.currentDataset.getWorkingChipName(chips[i]))
					+ "\n");
		// gPanel.setText((owner.currentDataset.getGeneName(genes[i])));
		// //pre.getGeneName(genes[i])));
		cPane.setBorder(BorderFactory.createTitledBorder("Chips:"));
		cPane.setText(sb.toString());

		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton closeButton = new JButton("Close");
		// closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.setActionCommand("close_info");
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);

		JButton saveButton = new JButton("Save");
		// closeButton.setMnemonic(KeyEvent.VK_S);
		saveButton.setActionCommand("save_info");
		saveButton.addActionListener(this);
		buttonPanel.add(saveButton);

		// main panel of dialog window
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(topPanel, BorderLayout.NORTH);
		contentPanel.add(geneTextPane, BorderLayout.WEST);
		contentPanel.add(cPane, BorderLayout.EAST);
		contentPanel.add(buttonPanel, BorderLayout.NORTH);
		contentPanel.setOpaque(true);
		dialog.setContentPane(contentPanel);

		// set size, location and make visible
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
	}

	// ===========================================================================
}
