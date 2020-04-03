package bicat.gui.window;

import bicat.biclustering.Bicluster;
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

public class RunInfo implements ActionListener {

  private String runLabel = ""; //null;
  private String runLabelInfo = "";

  /** Hook to governings <code>BiacGUI</code>. */
  BicatGui owner;

  /** Bicluster that this window shows information on. */
  Bicluster bc;

  /** Dialog window. */
  JDialog dialog;

  // ===========================================================================
  public RunInfo() { }

  public RunInfo(BicatGui o, String label, String info) {
    owner = o;
    runLabel = label;
    runLabelInfo = info;
  }

  // ===========================================================================
  /**
   * For <code>ActionListener</code> interface, reacts to user selections and
   * button clicks in this search window.
   *
   * */
  public void actionPerformed(ActionEvent e) {

    if("close_info".equals(e.getActionCommand())) {
      // close window without doing anything
      dialog.setVisible(false);
      dialog.dispose();
    }
    /*else if("save_info".equals(e.getActionCommand())) {

      JFileChooser jfc = new JFileChooser(); // open a file chooser dialog window
      jfc.setDialogTitle("Save bicluster information:");
      File file;
      int returnVal = jfc.showOpenDialog(owner);

      if(returnVal == JFileChooser.APPROVE_OPTION) {
        try{
          file = jfc.getSelectedFile();
          FileWriter fw = new FileWriter(file);
          String writeBuffer = new String(bc.toString() + "\n\nGenes:\n");
          int[] genes = bc.getGenes();
          int[] chips = bc.getChips();
          for(int i=0; i<genes.length; i++)
            writeBuffer += owner.pre.getGeneName(genes[i]) + "\n";
          writeBuffer += "\n\nChips:\n";
          for(int i=0; i<chips.length; i++)
            writeBuffer += owner.pre.getChipName(chips[i]) + "\n";
          writeBuffer += "\n";

          fw.write(writeBuffer);
          fw.close();

          // close window
          dialog.setVisible(false);
          dialog.dispose();
        }
        catch(IOException ioe) { System.err.println(ioe); }
      }*/

    else System.out.println("unknown event: "+e.paramString());

  }


  // ===========================================================================
  /**
   * Creates and shows the window.
   *
   * */
  public void makeWindow() {

    //int[] genes = bc.getGenes();
    //int[] chips = bc.getChips();

    // new dialog window with general information on bicluster
    dialog = new JDialog(owner, "Run "+runLabel+" information");

   // create top panel with general information on bicluster (SHOULD CORRECT THIS... nije idealno!.. 300404)
    JPanel topPanel = new JPanel(new FlowLayout() );

    JTextArea ta = new JTextArea();
    // ta.setText(runLabel+"\n"); // PROBA
    ta.setText(runLabelInfo);
    ta.setEditable(false);
    topPanel.add(ta, BorderLayout.CENTER);

/* topPanel.add(new JLabel(bc.toString()+" ["+bc.getGenes().length + " genes and "+ bc.getChips().length + " chips]"));

    // create gene subpanel
    JPanel gPanel = new JPanel (new GridLayout(0,1) );
    for(int i=0; i<genes.length; i++)
      gPanel.add(new JLabel(owner.pre.getGeneName(genes[i])));
    gPanel.setBorder(BorderFactory.createTitledBorder("Genes:"));

    // create chip subpanel
    JPanel cPanel = new JPanel (new GridLayout(0,1) );
    for(int i=0; i<chips.length; i++)
      cPanel.add(new JLabel(owner.pre.getChipName(chips[i])));
    cPanel.setBorder(BorderFactory.createTitledBorder("Chips:"));
*/

    JPanel buttonPanel = new JPanel( new FlowLayout() );

    JButton closeButton = new JButton("Close");
    // closeButton.setMnemonic(KeyEvent.VK_C);
    closeButton.setActionCommand("close_info");
    closeButton.addActionListener(this);
    buttonPanel.add(closeButton);

/*
    JButton saveButton = new JButton("Save");
    // closeButton.setMnemonic(KeyEvent.VK_S);
    saveButton.setActionCommand("save_info");
    saveButton.addActionListener(this);
    buttonPanel.add(saveButton);
*/
    // main panel of dialog window
    JPanel contentPane = new JPanel(new BorderLayout());
    contentPane.add(topPanel, BorderLayout.NORTH);
    //contentPane.add(gPanel,   BorderLayout.WEST);
    //contentPane.add(cPanel,   BorderLayout.CENTER);
    contentPane.add(buttonPanel, BorderLayout.SOUTH);
    contentPane.setOpaque(true);
    dialog.setContentPane(contentPane);

    // set size, location and make visible
    dialog.pack();
    dialog.setLocationRelativeTo(owner);
    dialog.setVisible(true);
  }

}

