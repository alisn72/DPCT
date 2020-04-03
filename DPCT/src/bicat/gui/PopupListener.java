package bicat.gui;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 **/

public class PopupListener extends MouseAdapter {

  JPopupMenu popup;

  // ===========================================================================
  public PopupListener(JPopupMenu popupMenu) { popup = popupMenu; }

  // ===========================================================================
  public void mousePressed(MouseEvent e) { maybeShowPopup(e); }

  // TRY???
  // public void mouseClicked(MouseEvent e) { maybeShowPopup(e);}

  // ===========================================================================
  public void mouseReleased(MouseEvent e) { maybeShowPopup(e); }

  // ===========================================================================
  private void maybeShowPopup(MouseEvent e) {
    if(e.isPopupTrigger()) popup.show(e.getComponent(), e.getX(), e.getY());
  }

  // ===========================================================================
  public PopupListener() { }
}
