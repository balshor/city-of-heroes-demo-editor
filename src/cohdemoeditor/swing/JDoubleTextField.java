/*
 * JDoubleTextField.java
 *
 * Created on July 9, 2005, 11:21 PM
 */

package cohdemoeditor.swing;

/**
 * This is a modified JTextField used for double input.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class JDoubleTextField extends javax.swing.JTextField {

	/** Creates new form BeanForm */
	public JDoubleTextField() {
		initComponents();
		setHorizontalAlignment(RIGHT);
		setText("0");
	}

	protected javax.swing.text.Document createDefaultModel() {
		return new DoubleDocument();
	}

	public double getDouble() {
		String str = getText();
		if (str.equals("") || str.equals("-")) {
			setText("0");
			return 0;
		}
		return Double.parseDouble(getText());
	}

	public void setDouble(double d) {
		setText(Double.toString(d));
	}

	private static class DoubleDocument extends javax.swing.text.PlainDocument {

		public DoubleDocument() {
			super();
		}

		public void insertString(int offs, String str,
				javax.swing.text.AttributeSet a)
				throws javax.swing.text.BadLocationException {
			if (str == null) {
				return;
			}
			String proposedText = getText(0, offs) + str
					+ getText(offs, getLength() - offs);
			try {
				Double.parseDouble(proposedText);
				super.insertString(offs, str, a);
			} catch (NumberFormatException nfe) {
				if (proposedText.equals("-"))
					super.insertString(offs, "-", a);
				else if (proposedText.equals(""))
					super.insertString(offs, "0", a);
				return;
			}

		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {

	}
	// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	// End of variables declaration//GEN-END:variables

}
