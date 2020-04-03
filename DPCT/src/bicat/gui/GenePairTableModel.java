package bicat.gui;

import javax.swing.table.AbstractTableModel;


public class GenePairTableModel extends AbstractTableModel {

  Object[][] data;
  private String[] columnNames = {"Gene 1", "Gene 2", "GenePair Score"}; //, "Graph Distance"};

  // ===========================================================================
  public GenePairTableModel() { }
  public GenePairTableModel(Object[][] d) {
    data = d;
  }

  // ===========================================================================
  public Object getValueAt(int row, int col) {
    return data[row][col];
  }

  // ===========================================================================
  public int getColumnCount() {
      return columnNames.length;
  }

  // ===========================================================================
  public int getRowCount() {
    return data.length;
  }

  // ===========================================================================
  public String getColumnName(int col) {
    return columnNames[col];
  }

  // ===========================================================================
  /*
   * JTable uses this method to determine the default renderer/
   * editor for each cell.  If we didn't implement this method,
   * then the last column would contain text ("true"/"false"),
   * rather than a check box.
   */
  public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
  }

  // ===========================================================================
  /*
   * Don't need to implement this method unless your table's editable.
   */
  public boolean isCellEditable(int row, int col) {
      //Note that the data/cell address is constant,
      //no matter where the cell appears onscreen.
      if (col < 2) {
          return false;
      } else {
          return true;
      }
  }

  private boolean DEBUG = false;
  // ===========================================================================
  /*
   * Don't need to implement this method unless your table's data can change.
   */
  public void setValueAt(Object value, int row, int col) {
      if (DEBUG) {
          System.out.println("Setting value at " + row + "," + col
                             + " to " + value
                             + " (an instance of "
                             + value.getClass() + ")");
      }

      data[row][col] = value;
      fireTableCellUpdated(row, col);

      if (DEBUG) {
          System.out.println("New value of data:");
          // printDebugData();
      }
  }

}
