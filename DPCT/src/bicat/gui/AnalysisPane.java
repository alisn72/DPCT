package bicat.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Set;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author D. Frick, A. Prelic
 * @version 1.0
 *
 **/

public class AnalysisPane extends JPanel implements ActionListener {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

static BicatGui owner;

  static JSplitPane splitPane;
  static JScrollPane tableScrollPane;
  static JScrollPane graphScrollPane;

  //private static JTable table;

  // ===========================================================================
  public AnalysisPane() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  public AnalysisPane(BicatGui o) {
    owner = o;
  }

  // ===========================================================================
  /**
   * For <code>ActionListener</code> interface, could be used to react to user
   * input.
   *
   * */
  public void actionPerformed(ActionEvent e) { }

  // ===========================================================================
  /**
   * Hands the governing <code>BicaGUI</code> to this <code>GraphPane</code.>
   * */
  public void setOwner(BicatGui frame) { owner = frame; }

  // ===========================================================================
  public void setTable(HashMap gps) {

    setLayout(new BorderLayout());
    removeAll();

    Object[][] data =  new Object[gps.size()][4];

    Set keys = gps.keySet();
    Object[] ka = keys.toArray();
    for(int i= 0; i< ka.length; i++) {
      String key = (String)ka[i];
      String[] fragments = key.split("\t");
      data[i][0] = owner.currentDataset.getGeneName(new Integer(fragments[0]).intValue());//pre.getGeneName((new Integer(fragments[0]).intValue()));
      data[i][1] = owner.currentDataset.getGeneName(new Integer(fragments[1]).intValue());//pre.getGeneName((new Integer(fragments[1]).intValue()));
      data[i][2] = ((Object[])gps.get(key))[0];
      data[i][3] = ((Object[])gps.get(key))[1];
    }

    TableSorter ts = new TableSorter( new GenePairTableModel(data));
    JTable table = new JTable(ts);
    ts.setTableHeader(table.getTableHeader());

    //Set up tool tips for column headers.
    table.getTableHeader().setToolTipText("Click to specify sorting; Control-Click to specify secondary sorting");

    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    table.setColumnSelectionAllowed(true);
    table.setRowSelectionAllowed(true);

    table.setCellSelectionEnabled(true);

    tableScrollPane = new JScrollPane(table);
    tableScrollPane.setBackground(Color.WHITE);
    tableScrollPane.setOpaque(true);

    tableScrollPane.setMinimumSize(new Dimension(200,150));

    // ....
//    graphScrollPane = new JScrollPane();
 //   graphScrollPane.setMaximumSize(new Dimension(10,10));
//    graphScrollPane.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.black), "Display area for neighbor graph"));
//    graphScrollPane.add(new JLabel("Display area for the neighbor graph"));
//    graphScrollPane.setBackground(Color.WHITE);
//    graphScrollPane.setForeground(Color.WHITE);

//    graphScrollPane.setOpaque(true);
 //   graphScrollPane.setVisible(true);

    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, graphScrollPane);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(600);

    add(splitPane);
  }

  private void jbInit() throws Exception {
  }

}

