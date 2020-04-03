package bicat.util;

import bicat.gui.BicatGui;

import javax.swing.*;

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

public class BicatError {

	static BicatGui owner;

	// ===========================================================================
	public BicatError(BicatGui o) {
		owner = o;
	}

	// ===========================================================================
	public static void errorMessage(Exception e) {
		if (owner.debug)
			e.printStackTrace();
		JOptionPane.showMessageDialog(owner, "Exception Raised!\n\n"
				+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	}

	// ===========================================================================
	public static void errorMessage(Exception e, BicatGui o) {
		if (owner.debug)
			e.printStackTrace();
		JOptionPane.showMessageDialog(o, "Exception Raised!\n\n"
				+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	}

	// ===========================================================================
	public static void errorMessage(Exception e, BicatGui o, boolean ext,
			String extStr) {
		if (owner.debug)
			e.printStackTrace();

		if (ext)
			JOptionPane.showMessageDialog(o, extStr, "Error",
					JOptionPane.ERROR_MESSAGE);
		else
			JOptionPane.showMessageDialog(o, "3. Exception Raised!\n\n"
					+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	}

	// =============== Offset Error =======================
	public static void offsetError() {
		JOptionPane.showMessageDialog(null, "The offset must be at least 1",
				"alert", JOptionPane.ERROR_MESSAGE);
	}

	public static void wrongOffsetError() {
		JOptionPane
				.showMessageDialog(
						null,
						"Wrong offset!\nThe problem is probably that your file contains additional\ncolumns with annotations at the beginning of the data set.\nTo keep the gene names, delete all other columns that do not\ncontain expression values (e.g. with MS Excel) and set\nthe column offset to one.\nIf the problem persists, check if there are missing\nvalues in your data set and set those to zero or\ndelete the according lines.",
						"alert", JOptionPane.ERROR_MESSAGE);
	}

	// =============== Space Error =======================
	public static void spaceError() {
		JOptionPane.showMessageDialog(null,
				"Gene and chip names must not contain spaces", "alert",
				JOptionPane.ERROR_MESSAGE);
	}

}
