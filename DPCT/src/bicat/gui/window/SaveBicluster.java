package bicat.gui.window;

import bicat.gui.BicatGui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author D. Frick, A. Prelic
 * @version 1.0
 *
 **/

public class SaveBicluster implements ActionListener {

  /** Dialog window. */
  JDialog dialog;
  /** Hook to governing <code>BicaGUI</code>. */
  BicatGui owner;

  boolean ok;
  int gene_offset;
  int chip_offset;

  // ===========================================================================
  public SaveBicluster() { }

  // ===========================================================================
  public SaveBicluster(BicatGui o, int go, int co) {
    owner = o; // ok = false;
    gene_offset = go;
    chip_offset = co;
  }

  // ===========================================================================
  public void makeWindow() {

    dialog = new JDialog(owner, "Do You want to save the current list of BCs? ");

    // create bottom subpanel with cancel and OK buttons
    JPanel closePanel = new JPanel( new FlowLayout() );

    JButton okayButton = new JButton("Ok");
    // okayButton.setMnemonic(KeyEvent.VK_K);
    okayButton.setActionCommand("ok_save");
    okayButton.addActionListener(this);
    closePanel.add(okayButton);

    JButton cancelButton = new JButton("Cancel");
    // cancelButton.setMnemonic(KeyEvent.VK_C);
    cancelButton.setActionCommand("nok_save");
    cancelButton.addActionListener(this);
    closePanel.add(cancelButton);

    // create bicluster subpanel
    JPanel bcDimPanel = new JPanel( new BorderLayout() );
    // bcDimPanel.setBorder( BorderFactory.createTitledBorder("... dimensions"));

    // 1st subpane in BC subpanel with labels containing field descriptions
    // JPanel labelPanel = new JPanel( new GridLayout(0,1) );
    // labelPanel.add( new JLabel("... will result in "+nrBCs+" BCs in the list of biclusters. Are You sure You want to continue? "));

    //labelPanel.add( new JLabel("Minimum gene count: "));
    //labelPanel.add( new JLabel("Minimum chip count: "));
    // bcDimPanel.add(labelPanel, BorderLayout.CENTER);

    // 2nd subpane in BC subpanel with number entry fields
    /*JPanel fieldPane = new JPanel( new GridLayout(0,1));

    minGeneField = new JFormattedTextField(NumberFormat.getIntegerInstance());
    minGeneField.setValue(new Integer(0));
    minGeneField.setColumns(6);
    fieldPane.add(minGeneField);

    minChipField = new JFormattedTextField(NumberFormat.getIntegerInstance());
    minChipField.setValue(new Integer(0));
    minChipField.setColumns(6);
    fieldPane.add(minChipField);

    bcDimPanel.add(fieldPane, BorderLayout.LINE_END);
*/

    // main panel of dialog window
    JPanel contentPane = new JPanel(new BorderLayout());
    contentPane.add(bcDimPanel, BorderLayout.CENTER);
    // contentPane.add(gcPanel, BorderLayout.CENTER);
    contentPane.add(closePanel, BorderLayout.PAGE_END);
    contentPane.setOpaque(true);
    dialog.setContentPane(contentPane);

    // set size, location and make visible
    dialog.pack();
    dialog.setLocationRelativeTo(owner);
    dialog.setVisible(true);

  }

  // ===========================================================================
  /**
   * For <code>ActionListener</code> interface, reacts to user selections and
   * button clicks in this search window.
   *
   * */
  public void actionPerformed(ActionEvent e) {

    // NE FERCERA!!!

    if("ok_save".equals(e.getActionCommand())) {

      System.out.println("am here!");
      // System.out.println("Starting filter...");

      // gene_offset = ((Number)minGeneField.getValue()).intValue();
      // chip_offset = ((Number)minChipField.getValue()).intValue();

      //owner.post.getFilteredBiclusters(gene_offset,chip_offset);

      // owner.post.testFilterBiclusters(gene_offset,chip_offset);
      // ....
      // owner.post.filterBiclusters(gene_offset, chip_offset);
      // owner.buildTree();

      // close window
      // ok = true;

      // boolean proceed = fcw.getProceed();
      //if(proceed) {

      // ask if wants to save the BC list?
      //SaveBCsWindow sbw = new SaveBCsWindow(owner);
      //if(sbw.getOK())

      // BILO BEFORE: owner.writeBiclusters(owner.post.bcs_add);

      // and do the thing.
      // owner.post.filterBiclusters(gene_offset, chip_offset);
      // }
      // */

      dialog.setVisible(false);
      dialog.dispose();
    }
    else if("nok_save".equals(e.getActionCommand())) {
      // close window without doing anything
      dialog.setVisible(false);
      dialog.dispose();
    } else {
      // unknown!
      System.out.println("unknown event: "+e.paramString());
    }
  }

  // ===========================================================================
  public boolean getOK() { return ok; }

}

