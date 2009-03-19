/*
 * JCenteringDialog.java
 *
 * Created on July 9, 2005, 10:37 PM
 */

package cohdemoeditor.swing;

import java.awt.*;

/**
 * This is a JDialog that automatically centers itself over its parent when its
 * visibility is set to true.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class JCenteringDialog extends javax.swing.JDialog {

	public JCenteringDialog() {
		super();
	}

	public JCenteringDialog(Dialog owner) {
		super(owner);
	}

	public JCenteringDialog(Dialog owner, boolean modal) {
		super(owner, modal);
	}

	public JCenteringDialog(Dialog owner, String title) {
		super(owner, title);
	}

	public JCenteringDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	public JCenteringDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
	}

	public JCenteringDialog(Frame owner) {
		super(owner);
	}

	public JCenteringDialog(Frame owner, boolean modal) {
		super(owner, modal);
	}

	public JCenteringDialog(Frame owner, String title) {
		super(owner, title);
	}

	public JCenteringDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	public JCenteringDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
	}

	private Container c = null;

	public void setVisible(boolean b) {
		Container parent;
		if (c == null) {
			parent = getParent();
		} else {
			parent = c;
		}
		if (b && parent != null && parent.isVisible()) {
			Dimension parentSize = parent.getSize();
			Dimension mySize = getSize();
			Point parentLocation = parent.getLocationOnScreen();
			setLocation(parentLocation.x + (parentSize.width - mySize.width)
					/ 2, parentLocation.y + (parentSize.height - mySize.height)
					/ 2);
		}
		super.setVisible(b);
	}

	public void setTargetContainer(Container c) {
		this.c = c;
	}

	public Container getTargetContainer() {
		if (c == null)
			return getParent();
		return c;
	}

}
