package bicat.gui.window;

import bicat.biclustering.Bicluster;
import bicat.gui.BicatGui;
import bicat.postprocessor.Postprocessor;
import bicat.preprocessor.Preprocessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.Vector;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 **/

public class HammingDistanceBC implements ActionListener {

  JDialog dialog;
  BicatGui owner;


  Bicluster bcluster;
  boolean extended = false;

  // ===========================================================================
  public HammingDistanceBC() { }
  public HammingDistanceBC(BicatGui o) { owner = o;  }

  public HammingDistanceBC(Bicluster bc, BicatGui o) {
    bcluster = bc;
    owner = o;
  }

  public HammingDistanceBC(Bicluster bc, boolean isExt, BicatGui o) {
    bcluster = bc;
    extended = isExt;
    owner = o;
  }

  static final String HAMMING_DISTANCE_BC_WINDOW_EXTEND_GENE_D = "hdbc_gene_d";
  static final String HAMMING_DISTANCE_BC_WINDOW_EXTEND_CHIP_D = "hdbc_chip_d";
  static final String HAMMING_DISTANCE_BC_WINDOW_APPLY = "hdbc_apply";
  static final String HAMMING_DISTANCE_BC_WINDOW_CANCEL = "hdbc_cancel";

  JFormattedTextField field;

  // ===========================================================================
  public void makeWindow() {

    dialog = new JDialog(owner, "BC Extension Setup Dialog ");

    JPanel top = new JPanel( new GridLayout(0,1) );
    top.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.black), ""));

    JPanel pp = new JPanel( new GridLayout(0,2));

    JRadioButton right = new JRadioButton("Extend in gene dimension");
    right.setActionCommand(HAMMING_DISTANCE_BC_WINDOW_EXTEND_GENE_D);
    right.addActionListener(this);
    pp.add(right);

    JRadioButton left = new JRadioButton("Extend in chip dimension"); //Absolute values ( 2 files )");
    left.setActionCommand(HAMMING_DISTANCE_BC_WINDOW_EXTEND_CHIP_D);
    left.addActionListener(this);
    pp.add(left);

    top.add(pp);

    JPanel pane = new JPanel( new GridLayout(0,2));

    JLabel label = new JLabel("Specify allowed error (%)");
    pane.add(label);

    field = new JFormattedTextField( NumberFormat.getIntegerInstance());
    field.setValue(new Integer(5));
    field.addActionListener(this);
    pane.add(field);

    top.add(pane,BorderLayout.CENTER);

    // create bottom subpanel with cancel and OK buttons
    JPanel closePanel = new JPanel( new FlowLayout() );

    JButton okayButton = new JButton("OK");
    okayButton.setMnemonic(KeyEvent.VK_K);
    okayButton.setActionCommand(HAMMING_DISTANCE_BC_WINDOW_APPLY);
    okayButton.addActionListener(this);
    closePanel.add(okayButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.setMnemonic(KeyEvent.VK_C);
    cancelButton.setActionCommand(HAMMING_DISTANCE_BC_WINDOW_CANCEL);
    cancelButton.addActionListener(this);
    closePanel.add(cancelButton);

    // ....
    // main panel of dialog window
    JPanel contentPane = new JPanel(new BorderLayout());
    contentPane.add(top);
    contentPane.add(closePanel, BorderLayout.PAGE_END);
    // contentPane.setOpaque(true);
    dialog.setContentPane(contentPane);

    // set size, location and make visible
    dialog.pack();
    dialog.setLocationRelativeTo(owner);
    dialog.setVisible(true);
  }

  boolean gene_d_selected = false;
  boolean chip_d_selected = false;

  // ===========================================================================
  /**
   * For <code>ActionListener</code> interface, reacts to user selections and
   * button clicks in this search window.
   *
   * */
  public void actionPerformed(ActionEvent e) {

    if(HAMMING_DISTANCE_BC_WINDOW_APPLY.equals(e.getActionCommand())) {

      int bcErr = ( new Integer(field.getText()).intValue());

      Bicluster extension;
      if(extended) extension = Postprocessor.getExtension(bcluster, bcErr, extended, Preprocessor.getDiscreteData(), gene_d_selected);
      else extension = Postprocessor.getExtension(bcluster, bcErr, extended, Preprocessor.getDiscreteData(), gene_d_selected);

      owner.pp.clearSelections();
      Vector[] currentBiclusterSelection = owner.pp.setTranslationTable(extension);

      owner.matrixScrollPane.repaint();
      owner.readjustPictureSize();
      owner.pp.repaint();

      owner.pp.biclusterSelected(currentBiclusterSelection[1], currentBiclusterSelection[2]);
      owner.refreshGraphicPanel();
      owner.pp.repaint();
      owner.gp.setGraphDataList(currentBiclusterSelection[0], currentBiclusterSelection[1], currentBiclusterSelection[2]);
      owner.gp.repaint();

      dialog.setVisible(false);
      dialog.dispose();
    }

    else if(HAMMING_DISTANCE_BC_WINDOW_CANCEL.equals(e.getActionCommand())) {
      // close window without doing anything
      dialog.setVisible(false);
      dialog.dispose();
    }

    else if(HAMMING_DISTANCE_BC_WINDOW_EXTEND_GENE_D.equals(e.getActionCommand())) {
      if(gene_d_selected) {
        gene_d_selected = false;
        chip_d_selected = true;
      }
      else {
        gene_d_selected = true;
        chip_d_selected = false;
      }
    }

    else if(HAMMING_DISTANCE_BC_WINDOW_EXTEND_CHIP_D.equals(e.getActionCommand())) {
      if(chip_d_selected) {
        chip_d_selected = false;
        gene_d_selected = true;
      }
      else {
        chip_d_selected = true;
        gene_d_selected = false;
      }

    }

    else System.out.println("unknown event: "+e.paramString());
  }
}
