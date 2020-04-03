package bicat.gui.window;

import bicat.gui.BicatGui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 **/

public class About implements ActionListener {

  /** Hook to governings <code>BiacGUI</code>. */
  BicatGui owner;

  /** Dialog window. */
  JDialog dialog;

  // ===========================================================================
  public About(BicatGui o) {
    owner = o;
  }

  // ===========================================================================
  /**
   * For <code>ActionListener</code> interface, reacts to user selections and
   * button clicks in this search window.
   *
   * */
  public void actionPerformed(ActionEvent e) {

    if ("close_info".equals(e.getActionCommand())) {
      // close window without doing anything
      dialog.setVisible(false);
      dialog.dispose();
    }
    else
      System.out.println("Unknown event! " + e.paramString());
  }

  // ===========================================================================
  /**
   * Creates and shows the window.
   *
   * */
  public void makeWindow() {

    // pane with 2 tabs: Authors, Licensing...
    dialog = new JDialog(owner, "About BicAT");

    // create top panel with general information on bicluster (SHOULD CORRECT THIS... nije idealno!.. 300404)
    JPanel topPanel = new JPanel(new FlowLayout());

    // here add some text.
    JPanel labelPanel = new JPanel(new GridLayout(0, 1));
    labelPanel.add(new JLabel("Biclustering Analysis Toolbox V2.2\n\n"));
    labelPanel.add(new JLabel("Copyright 2005: Systems Optimization group, ETH Zurich, Switzerland\n"));
    labelPanel.add(new JLabel("Developers: Simon Barkow, Amela Prelic, Stefan Bleuler\n"));
    labelPanel.add(new JLabel(
    "Email: barkow@tik.ee.ethz.ch\n"));
    labelPanel.add(new JLabel(
    "Website: http://www.tik.ee.ethz.ch/sop/bicat/\n"));
    topPanel.add(labelPanel, BorderLayout.CENTER);

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */

    JPanel buttonPanel = new JPanel(new FlowLayout());

    JButton closeButton = new JButton("Close");
    // closeButton.setMnemonic(KeyEvent.VK_C);
    closeButton.setActionCommand("close_info");
    closeButton.addActionListener(this);
    buttonPanel.add(closeButton);

    // main panel of dialog window
    JPanel contentPane = new JPanel(new BorderLayout());
    contentPane.add(topPanel, BorderLayout.NORTH);
    contentPane.add(buttonPanel, BorderLayout.SOUTH);
    contentPane.setOpaque(true);
    dialog.setContentPane(contentPane);

    // set size, location and make visible
    dialog.pack();
    dialog.setLocationRelativeTo(owner);
    dialog.setVisible(true);
  }
}
