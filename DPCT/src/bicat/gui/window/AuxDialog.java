package bicat.gui.window;

import bicat.gui.BicatGui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author D. Frick, A. Prelic
 * @version 1.0
 *
 **/

public class AuxDialog  implements ActionListener {

  JDialog dialog;
  bicat.gui.BicatGui owner;

  int wl = 0;  // default: is ALWAYS the first list of biclusters!
  int wi = 0;

  // ===========================================================================
  public AuxDialog() { }
  public AuxDialog(BicatGui o) { owner = o; }
  public AuxDialog(BicatGui o, int l, int i) { owner = o; wl = l; wi = i; }

  // ===========================================================================
  /**
   * For <code>ActionListener</code> interface, reacts to user selections and
   * button clicks in this search window.
   *
   * */
  public void actionPerformed(ActionEvent e) {

    // ...... Filtering procedures ...
    if(AUX_DIALOG_FBHD_APPLY.equals(e.getActionCommand())) {

      int maxErr = ( new Integer(maxErrorField.getText()).intValue());

      //owner.post.filterByHammingDistanceExtension(owner.currentDataset, maxErr, wl, wi,
      //                                            (owner.pre.preOption.discretizationScheme == PreprocessOption.PREPROCESS_OPTIONS_DISCRETIZATION_COEXPRESSED));
      owner.updateFilterMenu();
      owner.buildTree();

      dialog.setVisible(false);
      dialog.dispose();
    }

    // ...... Gene Pair Analysis ... DO LATER, sada:

    else if(AUX_DIALOG_GPA_COOCURRENCE_APPLY.equals(e.getActionCommand())) {

      /* try { if(null != input) gpScore = Integer.parseInt(input); }
            catch(NumberFormatException nfe) {
        JOptionPane.showMessageDialog(this, "Invalid input, Gene Pair Distance Threshold is still "+gpScore);
      } */

      int gpScore = ( new Integer(minGPScoreField.getText()).intValue());

      //owner.post.gpaByCoocurrence(owner.currentDataset, gpScore, wl, wi);
      String res = owner.post.gpaWriteResults();

      owner.updateAnalysisMenu();
      // owner.writeGPA(res); // ne razumijem: zasto mi ovo treba???
      owner.buildTree();

      owner.refreshAnalysisPanel();

      dialog.setVisible(false);
      dialog.dispose();
    }

    else if(AUX_DIALOG_GPA_COMMONCHIPS_APPLY.equals(e.getActionCommand())) {

      /*      try { if(null != input) gpDistanceThreshold = Integer.parseInt(input); }
             catch(NumberFormatException helloIAmYourNumberFormatExceptionForTodayHowMayIAnnoyYou) {
        JOptionPane.showMessageDialog(this, "Invalid input, GP distance is still "+gpDistanceThreshold);
             }
       */
      int gpScore = ( new Integer(minGPScoreField.getText()).intValue());

      //owner.post.gpaByCommonChips(owner.currentDataset, gpScore, wl, wi);
      String res = owner.post.gpaWriteResults();
      // temp:
      owner.updateAnalysisMenu();
      // owner.writeGPA(res); // zasto?
      owner.buildTree();

      owner.refreshAnalysisPanel();

      dialog.setVisible(false);
      dialog.dispose();
    }

    else if(AUX_DIALOG_CANCEL.equals(e.getActionCommand())) {
      dialog.setVisible(false);
      dialog.dispose();
    }

    else System.out.println("unknown event: "+e.paramString());

  }

  // ===========================================================================
  // AUX-DIALOG: Filter By Size

  static final String AUX_DIALOG_FBS_APPLY = "ad_fbs_ok";

  //static JFormattedTextField bcSizeField;
  //static JFormattedTextField geneSizeField;
  //static JFormattedTextField chipSizeField;

  // ===========================================================================
  // AUX-DIALOG: Filter By No Overlap

  //static final String AUX_DIALOG_FBNO_APPLY = "ad_fbno_ok";
  //static JFormattedTextField maxNumberOfBCsField;
  //static JFormattedTextField maxOverlapThresholdField;
  static JFormattedTextField maxErrorField;

  // ===========================================================================
  // AUX-DIALOG: Filter By New Area
  static final String AUX_DIALOG_FBNA_APPLY = "ad_fbna_ok";
  static final String AUX_DIALOG_FBHD_APPLY = "ad_fbhd_ok";
  static final String AUX_DIALOG_CANCEL = "ad_fbna_cancel";

  static JFormattedTextField minNewAreaThresholdField;

  // ===========================================================================
  // AUX-DIALOG: Filter By Hamming Distance

  // ===========================================================================
  public void makeWindow_FilterByHammingDistance() {

    dialog = new JDialog((BicatGui)owner, "Filter By Hamming Distance ");

    JPanel top = new JPanel( new GridLayout(0,1) );

    JPanel dimPanel = new JPanel( new GridLayout(0,2) );
    dimPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.black), "Specify Error Tollerance "));

    JPanel labelPanel = new JPanel( new GridLayout(0,1) );
    labelPanel.add(new JLabel("Error Threshold must be smaller than (%)   "));
    dimPanel.add(labelPanel);

    // 2nd subpane in gene/chip subpanel with name entry fields and AND/OR radio buttons
    JPanel fieldPane = new JPanel( new GridLayout(0,1) );

    maxErrorField = new JFormattedTextField(NumberFormat.getIntegerInstance());
    maxErrorField.setValue(new Integer(50));
    maxErrorField.setAlignmentX(JFormattedTextField.RIGHT_ALIGNMENT);
    fieldPane.add(maxErrorField);
    dimPanel.add(fieldPane);

    top.add(dimPanel, BorderLayout.CENTER);

    // create bottom subpanel with cancel and OK buttons
    JPanel closePanel = new JPanel( new FlowLayout() );

    JButton okayButton = new JButton("OK");
    okayButton.setMnemonic(KeyEvent.VK_K);
    okayButton.setActionCommand(AUX_DIALOG_FBHD_APPLY);
    okayButton.addActionListener(this);
    closePanel.add(okayButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.setMnemonic(KeyEvent.VK_C);
    cancelButton.setActionCommand(AUX_DIALOG_CANCEL);
    cancelButton.addActionListener(this);
    closePanel.add(cancelButton);

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
    JPanel contentPane = new JPanel(new BorderLayout());
    contentPane.add(top);
    contentPane.add(closePanel, BorderLayout.PAGE_END);
    contentPane.setOpaque(true);
    dialog.setContentPane(contentPane);

    // set size, location and make visible
    dialog.pack();
    dialog.setLocationRelativeTo((BicatGui)owner);
    dialog.setVisible(true);

  }

  // ===========================================================================
  // AUX-DIALOG: GenePair Analysis By Co-ocurrence

  JFormattedTextField minGPScoreField;
  static final String AUX_DIALOG_GPA_COOCURRENCE_APPLY = "gpa_cooc_apply";

  // ===========================================================================
  public void makeWindow_GenePairAnalysis_ByCoocurrence() {

    dialog = new JDialog((BicatGui)owner, "Gene-Pair Analysis By Co-ocurrence ");

    JPanel top = new JPanel( new GridLayout(0,1) );

    JPanel dimPanel = new JPanel( new GridLayout(0,2) );
    dimPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.black), "Specify Lower Score Threshold"));

    JPanel labelPanel = new JPanel( new GridLayout(0,1) );
    labelPanel.add(new JLabel("Minimum GP score    "));
    dimPanel.add(labelPanel);

    // 2nd subpane in gene/chip subpanel with name entry fields and AND/OR radio buttons
    JPanel fieldPane = new JPanel( new GridLayout(0,1) );

    minGPScoreField = new JFormattedTextField(NumberFormat.getIntegerInstance());
    minGPScoreField.setValue(new Integer(5));
    minGPScoreField.setAlignmentX(JFormattedTextField.RIGHT_ALIGNMENT);
    fieldPane.add(minGPScoreField);
    dimPanel.add(fieldPane);

    top.add(dimPanel);

    // create bottom subpanel with cancel and OK buttons
    JPanel closePanel = new JPanel( new FlowLayout() );

    JButton okayButton = new JButton("OK");
    okayButton.setMnemonic(KeyEvent.VK_K);
    okayButton.setActionCommand(AUX_DIALOG_GPA_COOCURRENCE_APPLY);
    okayButton.addActionListener(this);
    closePanel.add(okayButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.setMnemonic(KeyEvent.VK_C);
    cancelButton.setActionCommand(AUX_DIALOG_CANCEL);
    cancelButton.addActionListener(this);
    closePanel.add(cancelButton);

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
    JPanel contentPane = new JPanel(new BorderLayout());
    contentPane.add(top);
    contentPane.add(closePanel, BorderLayout.PAGE_END);
    contentPane.setOpaque(true);
    dialog.setContentPane(contentPane);

    // set size, location and make visible
    dialog.pack();
    dialog.setLocationRelativeTo((BicatGui)owner);
    dialog.setVisible(true);
  }

  // ===========================================================================
  // AUX-DIALOG: GenePair Analysis By Common Chips

  static final String AUX_DIALOG_GPA_COMMONCHIPS_APPLY = "gpa_chips_apply";

  // ===========================================================================
  public void makeWindow_GenePairAnalysis_ByCommonChips() {

    dialog = new JDialog((BicatGui)owner, "Gene-Pair Analysis By Common Chips ");

    JPanel top = new JPanel( new GridLayout(0,1) );

    JPanel dimPanel = new JPanel( new GridLayout(0,2) );
    dimPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.black), "Specify Lower Score Threshold"));

    JPanel labelPanel = new JPanel( new GridLayout(0,1) );
    labelPanel.add(new JLabel("Minimum GP score    "));
    dimPanel.add(labelPanel);

    // 2nd subpane in gene/chip subpanel with name entry fields and AND/OR radio buttons
    JPanel fieldPane = new JPanel( new GridLayout(0,1) );

    minGPScoreField = new JFormattedTextField(NumberFormat.getIntegerInstance());
    minGPScoreField.setValue(new Integer(5));
    minGPScoreField.setAlignmentX(JFormattedTextField.RIGHT_ALIGNMENT);
    fieldPane.add(minGPScoreField);
    dimPanel.add(fieldPane);

    top.add(dimPanel);

    // create bottom subpanel with cancel and OK buttons
    JPanel closePanel = new JPanel( new FlowLayout() );

    JButton okayButton = new JButton("OK");
    okayButton.setMnemonic(KeyEvent.VK_K);
    okayButton.setActionCommand(AUX_DIALOG_GPA_COMMONCHIPS_APPLY);
    okayButton.addActionListener(this);
    closePanel.add(okayButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.setMnemonic(KeyEvent.VK_C);
    cancelButton.setActionCommand(AUX_DIALOG_CANCEL);
    cancelButton.addActionListener(this);
    closePanel.add(cancelButton);

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */
    JPanel contentPane = new JPanel(new BorderLayout());
    contentPane.add(top);
    contentPane.add(closePanel, BorderLayout.PAGE_END);
    contentPane.setOpaque(true);
    dialog.setContentPane(contentPane);

    // set size, location and make visible
    dialog.pack();
    dialog.setLocationRelativeTo((BicatGui)owner);
    dialog.setVisible(true);
  }

}
