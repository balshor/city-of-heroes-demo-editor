/*
 * CommandInfoPanel.java
 *
 * Created on June 12, 2005, 1:35 AM
 */

package cohdemoeditor.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.DemoCommandListener;
import cohdemoeditor.ShowsAbsoluteTimesListener;

/**
 * A CommandInfoPanel provides the UI for a user to see and edit the contents of
 * a command.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class CommandInfoPanel extends javax.swing.JPanel implements
		DemoCommandListener, ListSelectionListener, ShowsAbsoluteTimesListener {

	private DemoCommandListTable cmdPanel;
	private DemoCommand target;
	private JTextField[] fields;

	/** Creates new form CommandInfoPanel */
	public CommandInfoPanel(DemoCommandListTable cmdPanel) {
		this.cmdPanel = cmdPanel;
		cmdPanel.getDemoCommandList().addShowsAbsoluteTimesListener(this);
		if (cmdPanel != null)
			cmdPanel.getSelectionModel().addListSelectionListener(this);
		setPreferredSize(new Dimension(400, 200));
		initComponents();
		fields = new JTextField[DemoCommandListFilter.NUM_COLS];
		fields[DemoCommandListFilter.TIME_COL] = timeField;
		fields[DemoCommandListFilter.REF_COL] = refField;
		fields[DemoCommandListFilter.CMD_COL] = commandField;
		fields[DemoCommandListFilter.ARG_COL] = argField;
	}

	/**
	 * Listener method. The panel listens to the DemoCommandListTable for
	 * selection changes. When the selection changes, it displays the newly
	 * selected DemoCommand.
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (cmdPanel == null)
			return;
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		if (lsm.isSelectionEmpty()) {
			target = null;
			resetFields();
			return;
		}
		int index = lsm.getLeadSelectionIndex();
		if (index < 0
				|| index >= cmdPanel.getDemoCommandList()
						.getVisibleCommandCount())
			return;
		DemoCommand newTarget = cmdPanel.getDemoCommandList()
				.getVisibleCommand(index);
		if (target != null) {
			target.removeListener(this);
		}
		target = newTarget;
		if (newTarget != null) {
			newTarget.addListener(this);
		}
		restoreFields();
	}

	/**
	 * Listener method. The panel listens to the DemoCommand being displayed. On
	 * updates, it updates its own display.
	 */
	@Override
	public void demoCommandChanged(final DemoCommand source, final int column, final Object oldValue) {
		if (source == null) {
			resetFields();
			return;
		}
		restoreField(column);
	}

	/**
	 * Resets the panel's fields to empty.
	 */
	private void resetFields() {
		timeField.setText("");
		refField.setText("");
		commandField.setText("");
		argField.setText("");
	}

	/**
	 * Helper method that restores all four fields.
	 */
	private void restoreFields() {
		for (int i = 0; i < DemoCommandListFilter.NUM_COLS; i++)
			restoreField(i);
	}

	/**
	 * This method reloads the displayed text from the DemoCommand in the
	 * specified field.
	 * 
	 * @param fieldNum
	 *            the number of the field to reset.
	 */
	private void restoreField(final int fieldNum) {
		if (!(fieldNum < 4))
			return;
		if (target == null) {
			fields[fieldNum].setText("");
			return;
		}
		if (fieldNum == DemoCommandListFilter.TIME_COL) {
			DemoCommandList cmdList = cmdPanel.getDemoCommandList();
			if (showsAbsoluteTimes || cmdList.indexOf(target) == 0) {
				timeField.setText(Integer.toString(target.getTime()));
			} else {
				DemoCommand prevCmd = cmdList.getCommand(cmdList
						.indexOf(target) - 1);
				timeField.setText(Integer.toString(target.getTime()
						- prevCmd.getTime()));
			}
			return;
		}
		if (fieldNum == DemoCommandListFilter.REF_COL) {
			refField.setText(target.getReferenceString());
			return;
		}
		if (fieldNum == DemoCommandListFilter.CMD_COL) {
			commandField.setText(target.getCommand());
			return;
		}
		if (fieldNum == DemoCommandListFilter.ARG_COL) {
			argField.setText(target.getArguments());
			return;
		}
	}

	/**
	 * Commits any changes made in the text fields to the DemoCommand.
	 */
	private void commitFields() {
		ListSelectionModel clsm = cmdPanel.getSelectionModel();
		if (clsm.isSelectionEmpty())
			return;

		int timeInput;
		try {
			timeInput = Integer.valueOf(timeField.getText());
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(this,
					"Could not parse time.  Please see HELP for instructions.",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		int ref;
		try {
			String refString = refField.getText().trim();
			ref = DemoCommand.getRefNumFor(refString);
		} catch (NumberFormatException nfe) {
			JOptionPane
					.showMessageDialog(
							this,
							"Could not parse reference.  Please see HELP for instructions.",
							"Input Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String cmd = commandField.getText().trim();
		String args = argField.getText().trim();

		if (target.getTime() != timeInput)
			commitTime();
		if (target.getReference() != ref)
			commitRef();
		if (target.getCommand().compareTo(cmd) != 0)
			commitCommand();
		if (target.getArguments().compareTo(args) != 0)
			commitArgs();
	}

	/**
	 * Commits the time field to the DemoCommand.
	 */
	private void commitTime() {
		ListSelectionModel clsm = cmdPanel.getSelectionModel();
		if (clsm.isSelectionEmpty())
			return;

		boolean changed = false;
		int minIndex = clsm.getMinSelectionIndex();
		int maxIndex = clsm.getMaxSelectionIndex();
		if (minIndex == -1 || maxIndex == -1) {
			return;
		}

		CompoundEdit cEdit = new CompoundEdit();
		StateEdit edit;
		DemoCommand target;
		DemoCommandList cmdList = cmdPanel.getDemoCommandList();
		int timeInput;
		try {
			timeInput = Integer.valueOf(timeField.getText());
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(this,
					"Could not parse time.  Please see HELP for instructions.",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		final DemoCommandList dcl = cmdPanel.getDemoCommandList();
		for (int i = minIndex; i <= maxIndex; i++) {
			if (clsm.isSelectedIndex(i)) {
				target = dcl.getVisibleCommand(i);
				int time = timeInput;
				if (!showsAbsoluteTimes && cmdList.indexOf(target) != 0) {
					time += cmdList.getCommand(cmdList.indexOf(target) - 1)
							.getTime();
				}
				if (time != target.getTime()) {
					edit = new StateEdit(target);
					target.setTime(time);
					changed = true;
					edit.end();
					cEdit.addEdit(edit);
				}
			}
		}
		cEdit.end();
		if (changed) {
			DemoEditor.getEditor().addUndoableEdit(cEdit);
		}
	}

	/**
	 * Commits the reference field to the DemoCommand.
	 */
	private void commitRef() {
		ListSelectionModel clsm = cmdPanel.getSelectionModel();
		if (clsm.isSelectionEmpty())
			return;

		boolean changed = false;
		int minIndex = clsm.getMinSelectionIndex();
		int maxIndex = clsm.getMaxSelectionIndex();
		if (minIndex == -1 || maxIndex == -1) {
			return;
		}

		CompoundEdit cEdit = new CompoundEdit();
		StateEdit edit;
		DemoCommand target;
		int ref;
		try {
			String refString = refField.getText().trim();
			ref = DemoCommand.getRefNumFor(refString);
		} catch (NumberFormatException nfe) {
			JOptionPane
					.showMessageDialog(
							this,
							"Could not parse reference.  Please see HELP for instructions.",
							"Input Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		final DemoCommandList dcl = cmdPanel.getDemoCommandList();
		for (int i = minIndex; i <= maxIndex; i++) {
			target = dcl.getVisibleCommand(i);
			if (clsm.isSelectedIndex(i)) {
				if (target.getReference() != ref) {
					edit = new StateEdit(target);
					target.setReference(ref);
					changed = true;
					edit.end();
					cEdit.addEdit(edit);
				}
			}
		}
		cEdit.end();
		if (changed) {
			DemoEditor.getEditor().addUndoableEdit(cEdit);
		}
	}

	/**
	 * Commits the command field to the DemoCommand.
	 */
	private void commitCommand() {
		ListSelectionModel clsm = cmdPanel.getSelectionModel();
		if (clsm.isSelectionEmpty())
			return;

		boolean changed = false;
		int minIndex = clsm.getMinSelectionIndex();
		int maxIndex = clsm.getMaxSelectionIndex();
		if (minIndex == -1 || maxIndex == -1) {
			return;
		}

		CompoundEdit cEdit = new CompoundEdit();
		StateEdit edit;
		DemoCommand target;
		String cmd = commandField.getText().trim();

		final DemoCommandList dcl = cmdPanel.getDemoCommandList();
		for (int i = minIndex; i <= maxIndex; i++) {
			if (clsm.isSelectedIndex(i)) {
				target = dcl.getVisibleCommand(i);
				if (!target.getCommand().equals(cmd)) {
					edit = new StateEdit(target);
					target.setCommand(cmd);
					changed = true;
					edit.end();
					cEdit.addEdit(edit);
				}
			}
		}
		cEdit.end();
		if (changed) {
			DemoEditor.getEditor().addUndoableEdit(cEdit);
		}
	}

	/**
	 * Commits the argument field to the DemoCommand.
	 */
	private void commitArgs() {
		ListSelectionModel clsm = cmdPanel.getSelectionModel();
		if (clsm.isSelectionEmpty())
			return;

		boolean changed = false;
		int minIndex = clsm.getMinSelectionIndex();
		int maxIndex = clsm.getMaxSelectionIndex();
		if (minIndex == -1 || maxIndex == -1) {
			return;
		}

		CompoundEdit cEdit = new CompoundEdit();
		StateEdit edit;
		DemoCommand target;
		String args = argField.getText().trim();

		final DemoCommandList dcl = cmdPanel.getDemoCommandList();
		for (int i = minIndex; i <= maxIndex; i++) {
			if (clsm.isSelectedIndex(i)) {
				target = dcl.getVisibleCommand(i);
				if (!target.getArguments().equals(args)) {
					edit = new StateEdit(target);
					target.setArguments(argField.getText());
					changed = true;
					edit.end();
					cEdit.addEdit(edit);
				}
			}
		}
		cEdit.end();
		if (changed) {
			DemoEditor.getEditor().addUndoableEdit(cEdit);
		}
	}

	private boolean showsAbsoluteTimes = true;

	/**
	 * Listener method. The CommandInfoPanel changes its display depending on
	 * whether the DemoCommandList is in absolute time mode or relative time
	 * mode.
	 */
	public void showsAbsoluteTimesChanged(DemoCommandList source,
			boolean newValue) {
		timeLabel.setText(newValue ? "Abs. Time" : "Rel. Time");
		restoreField(DemoCommandListFilter.TIME_COL);
		showsAbsoluteTimes = newValue;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		titleLabel = new javax.swing.JLabel();
		centerPanel = new javax.swing.JPanel();
		timeLabel = new javax.swing.JLabel();
		timeField = new javax.swing.JTextField();
		refLabel = new javax.swing.JLabel();
		refField = new javax.swing.JTextField();
		commandLabel = new javax.swing.JLabel();
		commandField = new javax.swing.JTextField();
		argLabel = new javax.swing.JLabel();
		argField = new javax.swing.JTextField();
		buttonPanel = new javax.swing.JPanel();
		commitButton = new javax.swing.JButton();
		helpButton = new javax.swing.JButton();

		setLayout(new java.awt.BorderLayout());

		titleLabel.setFont(new java.awt.Font("Tahoma", 1, 14));
		titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		titleLabel.setText("Command Information");
		titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		add(titleLabel, java.awt.BorderLayout.NORTH);

		centerPanel.setLayout(new java.awt.GridLayout(4, 2));

		centerPanel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
		timeLabel.setText("Abs. Time");
		centerPanel.add(timeLabel);

		timeField.setComponentPopupMenu(DemoEditor.TEXT_FIELD_POPUP_MENU);
		timeField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				timeFieldActionPerformed(evt);
			}
		});
		timeField.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt) {
				timeFieldFocusLost(evt);
			}
		});

		centerPanel.add(timeField);

		refLabel.setText("Reference");
		centerPanel.add(refLabel);

		refField.setComponentPopupMenu(DemoEditor.TEXT_FIELD_POPUP_MENU);
		refField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				refFieldActionPerformed(evt);
			}
		});
		refField.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt) {
				refFieldFocusLost(evt);
			}
		});

		centerPanel.add(refField);

		commandLabel.setText("Command");
		centerPanel.add(commandLabel);

		commandField.setComponentPopupMenu(DemoEditor.TEXT_FIELD_POPUP_MENU);
		commandField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				commandFieldActionPerformed(evt);
			}
		});
		commandField.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt) {
				commandFieldFocusLost(evt);
			}
		});

		centerPanel.add(commandField);

		argLabel.setText("Arguments");
		centerPanel.add(argLabel);

		argField.setComponentPopupMenu(DemoEditor.TEXT_FIELD_POPUP_MENU);
		argField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				argFieldActionPerformed(evt);
			}
		});
		argField.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt) {
				argFieldFocusLost(evt);
			}
		});

		centerPanel.add(argField);

		add(centerPanel, java.awt.BorderLayout.CENTER);

		buttonPanel.setLayout(new java.awt.GridLayout(1, 0));

		commitButton.setText("Commit");
		commitButton
				.setToolTipText("Commits any changes to all selected commands.");
		commitButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				commitButtonActionPerformed(evt);
			}
		});

		buttonPanel.add(commitButton);

		helpButton.setText("Help");
		helpButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				helpButtonActionPerformed(evt);
			}
		});

		buttonPanel.add(helpButton);

		add(buttonPanel, java.awt.BorderLayout.SOUTH);

	}

	// </editor-fold>//GEN-END:initComponents

	private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_helpButtonActionPerformed
		JOptionPane.showMessageDialog(null,
				"Use this panel to edit the currently selected lines.",
				"Temporary Help Message", JOptionPane.INFORMATION_MESSAGE);
	}// GEN-LAST:event_helpButtonActionPerformed

	private void argFieldFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:
		// event_argFieldFocusLost
		commitArgs();
	}// GEN-LAST:event_argFieldFocusLost

	private void commandFieldFocusLost(java.awt.event.FocusEvent evt) {// GEN-
		// FIRST
		// :
		// event_commandFieldFocusLost
		commitCommand();
	}// GEN-LAST:event_commandFieldFocusLost

	private void refFieldFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:
		// event_refFieldFocusLost
		commitRef();
	}// GEN-LAST:event_refFieldFocusLost

	private void timeFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:
		// event_timeFieldFocusLost
		commitTime();
	}// GEN-LAST:event_timeFieldFocusLost

	private void argFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-
		// FIRST
		// :
		// event_argFieldActionPerformed
		commitArgs();
	}// GEN-LAST:event_argFieldActionPerformed

	private void commandFieldActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_commandFieldActionPerformed
		commitCommand();
	}// GEN-LAST:event_commandFieldActionPerformed

	private void refFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-
		// FIRST
		// :
		// event_refFieldActionPerformed
		commitRef();
	}// GEN-LAST:event_refFieldActionPerformed

	private void timeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-
		// FIRST
		// :
		// event_timeFieldActionPerformed
		commitTime();
	}// GEN-LAST:event_timeFieldActionPerformed

	private void commitButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_commitButtonActionPerformed
		commitFields();
	}// GEN-LAST:event_commitButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JTextField argField;
	private javax.swing.JLabel argLabel;
	private javax.swing.JPanel buttonPanel;
	private javax.swing.JPanel centerPanel;
	private javax.swing.JTextField commandField;
	private javax.swing.JLabel commandLabel;
	private javax.swing.JButton commitButton;
	private javax.swing.JButton helpButton;
	private javax.swing.JTextField refField;
	private javax.swing.JLabel refLabel;
	private javax.swing.JTextField timeField;
	private javax.swing.JLabel timeLabel;
	private javax.swing.JLabel titleLabel;
	// End of variables declaration//GEN-END:variables

}
