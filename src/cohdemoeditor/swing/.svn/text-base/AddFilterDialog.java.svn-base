/*
 * AddFilterDialog.java
 *
 * Created on June 12, 2005, 9:52 PM
 */

package cohdemoeditor.swing;

import javax.swing.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.FilterList;

/**
 * This class provides a user interface for creating new
 * <code>DemoCommandListFilter</code> objects for adding to a
 * <code>FilterList</code>.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class AddFilterDialog extends JCenteringDialog {

	private FilterList target;
	private DemoEditor editor;
	private ReferenceLookupDialog lookupDialog;

	/**
	 * Creates a new AddFilterDialog.
	 * 
	 * @param parent
	 *            the parent <code>Frame</code> for which this dialog is
	 *            displayed
	 * @param modal
	 *            <code>true</code> for a modal dialog, <code>false</code> for
	 *            one that allows other windows to be active at the same time
	 * @param target
	 *            the <code>FilterList</code> to which the new filter will be
	 *            added. Cannot be null
	 */
	public AddFilterDialog(DemoEditor parent, boolean modal, FilterList target) {
		super(parent, modal);
		if (target == null)
			throw new IllegalArgumentException(
					"target cannot be null in AddFilterDialog constructor");
		this.target = target;
		editor = parent;
		initComponents();
	}

	/**
	 * Creates a new AddFilterDialog.
	 * 
	 * @param parent
	 *            the parent <code>Dialog</code> for which this dialog is
	 *            displayed
	 * @param modal
	 *            <code>true</code> for a modal dialog, <code>false</code> for
	 *            one that allows other windows to be active at the same time
	 * @param target
	 *            the <code>FilterList</code> to which the new filter will be
	 *            added. Cannot be null
	 */
	public AddFilterDialog(java.awt.Dialog parent, boolean modal,
			FilterList target) {
		super(parent, modal);
		if (target == null)
			throw new IllegalArgumentException(
					"target cannot be null in AddFilterDialog constructor");
		this.target = target;
		initComponents();
	}

	/**
	 * Returns the FilterList to which the new filter will be added.
	 * 
	 * @return the FilterList to add the new filter to
	 */
	public FilterList getTarget() {
		return target;
	}

	/**
	 * A regular expression representing valid entries for the time range field.
	 */
	public static final String timeRegEx = "(([0-9]+)-([0-9]+)|[ ])+";

	private boolean validateTimeField() {
		String s = timeField.getText();
		if (s.matches("[ \t\n\f\r]*"))
			return true;
		if (!s.matches(timeRegEx)) {
			return false;
		}
		try {
			String[] tokens = s.split(" ");
			for (String token : tokens) {
				String[] range = token.split("-");
				if (Integer.valueOf(range[1]) < Integer.valueOf(range[0]))
					return false;
			}
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (IndexOutOfBoundsException ioobe) {
			ioobe.printStackTrace();
		}
		return true;
	}

	private boolean validateRefField() {
		String s = refField.getText();
		if (s.matches("[ \t\n\f\r]*"))
			return true;
		String[] tokens = s.split(" ");
		for (String token : tokens) {
			if (!DemoCommand.hasRefNumFor(token.trim()))
				return false;
		}
		return true;
	}

	private void resetInputs() {
		timeField.setText("");
		refField.setText("");
		cmdField.setText("");
		argField.setText("");
		filterTypeButtonGroup.setSelected(showButton.getModel(), true);
	}

	private DemoCommandListFilter getFilter() {
		try {
			DemoCommandListFilter f = new DemoCommandListFilter(hideButton
					.isSelected());
			String timeString = timeField.getText();
			if (!timeString.matches("[ \t\n\f\r]*")) {
				String[] tokens = timeString.split(" ");
				for (String token : tokens) {
					if (!token.equals("")) {
						String[] range = token.split("-");
						f.addTimeRange(Integer.valueOf(range[0]), Integer
								.valueOf(range[1]));
					}
				}
			}
			String refString = refField.getText();
			if (!refString.matches("[ \t\n\f\r]*")) {
				String[] tokens = refString.split(" ");
				for (String token : tokens) {
					if (!token.equals("")) {
						f.addReference(DemoCommand.getRefNumFor(token));
					}
				}
			}
			String cmdString = cmdField.getText().trim();
			if (!cmdString.matches("[ \t\n\f\r]*")) {
				String[] tokens = cmdString.split(" ");
				for (String token : tokens) {
					f.addCommand(token.trim());
				}
			}
			String argString = argField.getText().trim();
			if (!argString.matches("[ \t\n\f\r]*")) {
				String[] strarray = argString.split("\"");
				for (int i = 0; i < strarray.length; i++) {
					if (strarray[i] == "")
						continue;
					if ((i % 2 == 0)) {
						String[] strarray2 = strarray[i].trim().split(" ");
						for (String str : strarray2) {
							if (!str.trim().equals(""))
								f.addArgument(str.trim());
						}
					} else {
						if (!strarray[i].trim().equals(""))
							f.addArgument(strarray[i].trim());
					}
				}
			}
			if (!f.isEmpty())
				return f;
			return null;
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			return null;
		} catch (IndexOutOfBoundsException ioobe) {
			ioobe.printStackTrace();
			return null;
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
		filterTypeButtonGroup = new javax.swing.ButtonGroup();
		southPanel = new javax.swing.JPanel();
		addButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		helpButton = new javax.swing.JButton();
		centerPanel = new javax.swing.JPanel();
		inputPanel = new javax.swing.JPanel();
		timeLabel = new javax.swing.JLabel();
		timeField = new javax.swing.JTextField();
		refButton = new javax.swing.JButton();
		refField = new javax.swing.JTextField();
		cmdLabel = new javax.swing.JLabel();
		cmdField = new javax.swing.JTextField();
		argLabel = new javax.swing.JLabel();
		argField = new javax.swing.JTextField();
		filterTypePanel = new javax.swing.JPanel();
		showButton = new javax.swing.JRadioButton();
		hideButton = new javax.swing.JRadioButton();

		setTitle("Add New Filter");
		setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		setModal(true);
		addButton.setMnemonic('a');
		addButton.setText("Add");
		addButton
				.setToolTipText("Add a new filter with the options entered above");
		addButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addButtonActionPerformed(evt);
			}
		});

		southPanel.add(addButton);

		cancelButton.setMnemonic('c');
		cancelButton.setText("Cancel");
		cancelButton.setToolTipText("Exit without adding a new filter");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		southPanel.add(cancelButton);

		helpButton.setMnemonic('h');
		helpButton.setText("Help");
		helpButton.setToolTipText("Help");
		helpButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				helpButtonActionPerformed(evt);
			}
		});

		southPanel.add(helpButton);

		getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

		centerPanel.setLayout(new java.awt.BorderLayout());

		centerPanel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
		inputPanel.setLayout(new java.awt.GridLayout(4, 2, 5, 0));

		timeLabel.setText("Time Ranges");
		inputPanel.add(timeLabel);

		timeField.setToolTipText("Enter time ranges in the format xxx-yyy.");
		timeField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				timeFieldActionPerformed(evt);
			}
		});

		inputPanel.add(timeField);

		refButton.setText("References");
		refButton.setToolTipText("Toggles the reference search dialog.");
		refButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		refButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				refButtonActionPerformed(evt);
			}
		});

		inputPanel.add(refButton);

		refField.setToolTipText("Enter references here.");
		refField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				refFieldActionPerformed(evt);
			}
		});

		inputPanel.add(refField);

		cmdLabel.setText("Commands");
		cmdLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
		inputPanel.add(cmdLabel);

		cmdField.setToolTipText("Enter commands here");
		cmdField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cmdFieldActionPerformed(evt);
			}
		});

		inputPanel.add(cmdField);

		argLabel.setText("Arguments");
		inputPanel.add(argLabel);

		argField
				.setToolTipText("Enter arguments here.  Multi-word arguments should be enclosed in quotes.");
		argField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				argFieldActionPerformed(evt);
			}
		});

		inputPanel.add(argField);

		centerPanel.add(inputPanel, java.awt.BorderLayout.CENTER);

		filterTypeButtonGroup.add(showButton);
		showButton.setSelected(true);
		showButton.setText("Show Matching Commands");
		showButton
				.setToolTipText("Only show lines that match at least one parameter in each category above.");
		filterTypePanel.add(showButton);

		filterTypeButtonGroup.add(hideButton);
		hideButton.setText("Hide Matching Commands");
		hideButton
				.setToolTipText("Hide any commands that match at least one parameter in each box above.");
		filterTypePanel.add(hideButton);

		centerPanel.add(filterTypePanel, java.awt.BorderLayout.SOUTH);

		getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

		pack();
	}

	// </editor-fold>//GEN-END:initComponents

	private void refButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-
		// FIRST
		// :
		// event_refButtonActionPerformed
		if (lookupDialog == null) {
			lookupDialog = new ReferenceLookupDialog(this, null, refField,
					false);
		}
		if (editor == null) {
			JOptionPane.showMessageDialog(this,
					"This Add Filter dialog is not attached to a demo editor.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!lookupDialog.isVisible()) {
			int numDCLE = editor.getDemoCommandListEditorCount();
			if (numDCLE == 0) {
				JOptionPane.showMessageDialog(this,
						"Could not find any open demos.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			lookupDialog.setDemoReferenceList(editor.getDemoCommandListEditor(
					numDCLE - 1).getDemoCommandList().getDemoReferenceList());
			lookupDialog.setLocation(this.getX(), this.getY()
					+ this.getHeight());
			lookupDialog.setVisible(true);
		} else {
			lookupDialog.setVisible(false);
		}
	}// GEN-LAST:event_refButtonActionPerformed

	private void argFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-
		// FIRST
		// :
		// event_argFieldActionPerformed
		addButton.requestFocusInWindow();
	}// GEN-LAST:event_argFieldActionPerformed

	private void cmdFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-
		// FIRST
		// :
		// event_cmdFieldActionPerformed
		argField.requestFocusInWindow();
	}// GEN-LAST:event_cmdFieldActionPerformed

	private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_helpButtonActionPerformed
		JOptionPane
				.showMessageDialog(
						this,
						"Please separate multiple entries with spaces.  Encapsulate multi-word arguments in double quotes.  Time ranges should be entered as xxx-yyy.",
						"Temporary Help Message",
						JOptionPane.INFORMATION_MESSAGE);
	}// GEN-LAST:event_helpButtonActionPerformed

	private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-
		// FIRST
		// :
		// event_addButtonActionPerformed
		if (target == null) {
			setVisible(false);
			resetInputs();
			return;
		}
		if (!validateTimeField()) {
			JOptionPane
					.showMessageDialog(
							this,
							"Could not parse time ranges.  Please see HELP for instructions.",
							"Input Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!validateRefField()) {
			JOptionPane
					.showMessageDialog(
							this,
							"Could not parse references.  Please see HELP for instructions.",
							"Input Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		DemoCommandListFilter f = getFilter();
		if (f == null) {
			setVisible(false);
			resetInputs();
			return;
		}
		target.add(f);
		setVisible(false);
		resetInputs();
		return;
	}// GEN-LAST:event_addButtonActionPerformed

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_cancelButtonActionPerformed
		resetInputs();
		setVisible(false);
	}// GEN-LAST:event_cancelButtonActionPerformed

	private void refFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-
		// FIRST
		// :
		// event_refFieldActionPerformed
		if (!validateRefField()) {
			JOptionPane
					.showMessageDialog(
							this,
							"Could not parse references.  Please see HELP for instructions.",
							"Input Error", JOptionPane.ERROR_MESSAGE);
		}
		cmdField.requestFocusInWindow();
	}// GEN-LAST:event_refFieldActionPerformed

	private void timeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-
		// FIRST
		// :
		// event_timeFieldActionPerformed
		if (!validateTimeField()) {
			JOptionPane
					.showMessageDialog(
							this,
							"Could not parse time ranges.  Please see HELP for instructions.",
							"Input Error", JOptionPane.ERROR_MESSAGE);
		}
		refField.requestFocusInWindow();
	}// GEN-LAST:event_timeFieldActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton addButton;
	private javax.swing.JTextField argField;
	private javax.swing.JLabel argLabel;
	private javax.swing.JButton cancelButton;
	private javax.swing.JPanel centerPanel;
	private javax.swing.JTextField cmdField;
	private javax.swing.JLabel cmdLabel;
	private javax.swing.ButtonGroup filterTypeButtonGroup;
	private javax.swing.JPanel filterTypePanel;
	private javax.swing.JButton helpButton;
	private javax.swing.JRadioButton hideButton;
	private javax.swing.JPanel inputPanel;
	private javax.swing.JButton refButton;
	private javax.swing.JTextField refField;
	private javax.swing.JRadioButton showButton;
	private javax.swing.JPanel southPanel;
	private javax.swing.JTextField timeField;
	private javax.swing.JLabel timeLabel;
	// End of variables declaration//GEN-END:variables

}
