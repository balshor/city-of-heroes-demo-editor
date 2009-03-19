/*
 * EditPopupMenu.java
 *
 * Created on July 26, 2007, 1:56 PM
 */

package cohdemoeditor.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This menu provides the cut/copy/paste functionality.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class TextFieldPopupMenu extends JPopupMenu {

	private JTextField focus;
	private Action cutAction;
	private Action copyAction;
	private Action pasteAction;

	/** Creates a new instance of EditPopupMenu */
	public TextFieldPopupMenu() {
		super();

		cutAction = new AbstractAction("Cut") {
			public void actionPerformed(ActionEvent e) {
				if (focus == null)
					return;
				focus.cut();
			}
		};

		copyAction = new AbstractAction("Copy") {
			public void actionPerformed(ActionEvent e) {
				if (focus == null)
					return;
				focus.copy();
			}
		};

		pasteAction = new AbstractAction("Paste") {
			public void actionPerformed(ActionEvent e) {
				if (focus == null)
					return;
				focus.paste();
			}
		};

		add(cutAction);
		add(copyAction);
		add(pasteAction);
	}

	/**
	 * Sets the focus of this JPopupMenu
	 * 
	 * @param field
	 */
	public void setFocus(JTextField field) {
		setInvoker(field);
		focus = field;
	}

	/**
	 * Returns the focus of this JPopupMenu
	 * 
	 * @return
	 */
	public JTextField getFocus() {
		return focus;
	}

	/**
	 * Called to show the menu.
	 */
	public void show(Component invoker, int x, int y) {
		if (!(invoker instanceof JTextField)) {
			JOptionPane.showMessageDialog(this,
					"Bug: Incorrect context menu assigned to this component.",
					"Programming Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		focus = (JTextField) invoker;
		super.show(invoker, x, y);
	}

}
