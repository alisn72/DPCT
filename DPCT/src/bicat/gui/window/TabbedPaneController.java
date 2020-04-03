package bicat.gui.window;


import javax.swing.*;
import java.awt.*;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 **/

public class TabbedPaneController {

  JPanel tabbedPanel = null;
  JTabbedPane tabbedPane;
  JPanel emptyFilePanel = null;
  JTextArea emptyFileArea = null;
  //FileAndTextTransferHandler transferHandler;
  TablePreviewHandler tablePreviewHandler;
  boolean noFiles = true;
  String fileSeparator;

  // ===========================================================================
  public TabbedPaneController() { }

  public TabbedPaneController(JTabbedPane tb, JPanel tp) {
    tabbedPane = tb;
    tabbedPanel = tp;
    tablePreviewHandler = new TablePreviewHandler(this);
    // transferHandler = new FileAndTextTransferHandler(this);
    fileSeparator = System.getProperty("file.separator");

    //The split method in the String class uses
    //regular expressions to define the text used for
    //the split.  The forward slash "\" is a special
    //character and must be escaped.  Some look and feels,
    //such as Microsoft Windows, use the forward slash to
    //delimit the path.
    if ("\\".equals(fileSeparator)) {
      fileSeparator = "\\\\";
    }
    init();
    // makeTextPanel("see","see-2");
  }

  // ===========================================================================
  public JTextArea addTab(String filename) {
    if (noFiles) {
      tabbedPanel.remove(emptyFilePanel);
      tabbedPanel.add(tabbedPane, BorderLayout.CENTER);
      noFiles = false;
    }
    String[] str = filename.split(fileSeparator);
    return makeTextPanel(str[str.length-1], filename);
  }

  // ===========================================================================
  //Remove all tabs and their components, then put the default file area back.
  public void clearAll() {
    if (noFiles == false) {
      tabbedPane.removeAll();
      tabbedPanel.remove(tabbedPane);
    }
    init();
  }

  // ===========================================================================
  private void init() {
    String defaultText = "Click on the upper-leftmost data value in the table";
    noFiles = true;
    if (emptyFilePanel == null) {
      emptyFileArea = new JTextArea(20,15);
      emptyFileArea.setEditable(false);
      // emptyFileArea.setDragEnabled(true);
      // emptyFileArea.setTransferHandler(previewHandler);
      emptyFileArea.setTransferHandler(tablePreviewHandler);
      emptyFileArea.setMargin(new Insets(5,5,5,5));
      JScrollPane fileScrollPane = new JScrollPane(emptyFileArea);
      emptyFilePanel = new JPanel(new BorderLayout(), false);
      emptyFilePanel.add(fileScrollPane, BorderLayout.CENTER);
    }
    tabbedPanel.add(emptyFilePanel, BorderLayout.CENTER);
    tabbedPanel.repaint();
    emptyFileArea.setText(defaultText);
  }

  // ===========================================================================
  protected JTextArea makeTextPanel(String name, String toolTip) {
    JTextArea fileArea = new JTextArea(20,15);
    // fileArea.setDragEnabled(true);
    // fileArea.setTransferHandler(previewHandler);
    fileArea.add(new JTable(2,3));

    fileArea.setMargin(new Insets(5,5,5,5));
    JScrollPane fileScrollPane = new JScrollPane(fileArea);
    tabbedPane.addTab(name, null, (Component)fileScrollPane, toolTip);
    tabbedPane.setSelectedComponent((Component)fileScrollPane);
    return fileArea;
  }

}
