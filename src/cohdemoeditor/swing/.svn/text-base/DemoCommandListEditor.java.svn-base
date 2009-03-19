/*
 * DemoCommandListEditor.java
 *
 * Created on June 13, 2005, 10:58 AM
 */

package cohdemoeditor.swing;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.beans.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DirtyBitListener;
import cohdemoeditor.DirtyBitTracker;
import cohdemoeditor.SaveFileListener;

/**
 * A DemoCommandListEditor is an internal frame that holds the components for
 * displaying and editing a single demo. This includes a DemoReferenceListPanel,
 * a DemoCommandListTable, a CommandInfoPanel, and a FilterListPanel. It also
 * provides a toolbar of buttons and manages the dialogs that these buttons
 * open. It listens to the DemoCommandList for dirty bit and save file changes,
 * so it can update the UI appropriately.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class DemoCommandListEditor extends javax.swing.JInternalFrame implements
		SaveFileListener, DirtyBitListener {

	private DemoCommandList cmdList = null;
	private DemoCommandListTable cmdListPanel = null;
	private FilterPanel displayPanel = null;
	private CommandInfoPanel cmdInfoPanel = null;
	private JRadioButtonMenuItem menuItem = null;
	private ChangeAllDialog changeAllDialog = null;
	private TimeShiftDialog timeShiftDialog = null;
	private TimeScaleDialog timeScaleDialog = null;
	private PositionOffsetDialog posShiftDialog = null;
	private DemoReferenceListPanel refListPanel = null;

	/**
	 * Creates new form DemoCommandListEditor. Arguments should not be null. If
	 * the editor is null, the DCLE will be created; however, the object may
	 * throw NullPointerExceptions when actions are performed on it.
	 */
	public DemoCommandListEditor(DemoCommandList cmdList) {
		super(cmdList.getName(), true, true, true, true);
		cmdList.addDirtyBitListener(this);
		this.cmdList = cmdList;
		cmdList.addSaveFileListener(this);
		menuItem = new JRadioButtonMenuItem(cmdList.getName());
		menuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					DemoCommandListEditor.this.setSelected(true);
				} catch (java.beans.PropertyVetoException pve) {
					;
				}
			}
		});

		cmdListPanel = new DemoCommandListTable(cmdList);
		refListPanel = new DemoReferenceListPanel(cmdListPanel);
		JScrollPane scroller = new JScrollPane(cmdListPanel);
		/**
		 * scroller.addMouseListener(new MouseAdapter() { public void
		 * MouseClicked(MouseEvent e) {
		 * cmdListPanel.getSelectionModel().clearSelection(); } });
		 */
		cmdInfoPanel = new CommandInfoPanel(cmdListPanel);
		cmdInfoPanel.setPreferredSize(cmdInfoPanel.getMinimumSize());
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		displayPanel = new FilterPanel(cmdList.getFilterList());
		displayPanel.setFilterListManager(DemoEditor.getEditor().getFilterListManager());
		panel.add(displayPanel, BorderLayout.CENTER);
		JLabel filterLabel = new JLabel("Current Filters");
		filterLabel.setFont(new java.awt.Font("Tahoma", 1, 14));
		filterLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		panel.add(filterLabel, BorderLayout.NORTH);

		JSplitPane eastPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
				cmdInfoPanel, panel);
		eastPane.setResizeWeight(0);

		JSplitPane westPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				refListPanel, scroller);
		westPane.setResizeWeight(0);
		westPane.setOneTouchExpandable(true);

		JSplitPane centerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				true, westPane, eastPane);
		centerPane.setResizeWeight(1);
		centerPane.setOneTouchExpandable(true);
		add(centerPane, BorderLayout.CENTER);

		setPreferredSize(new Dimension(810, 400));
		initComponents();
		changeAllDialog = new ChangeAllDialog(cmdList);
		timeShiftDialog = new TimeShiftDialog(cmdList);
		timeScaleDialog = new TimeScaleDialog(cmdList);
		posShiftDialog = new PositionOffsetDialog(cmdList);
	}

	/**
	 * Each DemoCommandListEditor owns a menu item associated with an action
	 * that will select this DemoCommandListEditor. These are used by a
	 * DemoEditor in the program's menu to provide a selectable menu of windows
	 * to select.
	 * 
	 * @return
	 */
	public JRadioButtonMenuItem getMenuItem() {
		return menuItem;
	}

	/**
	 * Simple getter for the DemoCommandList this editor is displaying.
	 * 
	 * @return
	 */
	public DemoCommandList getDemoCommandList() {
		return cmdList;
	}

	/**
	 * Returns the ListSelectionModel for the DemoCommandListTable (JTable) that
	 * displays the DemoCommandList
	 * 
	 * @return
	 */
	public ListSelectionModel getListSelectionModel() {
		return cmdListPanel.getSelectionModel();
	}

	/**
	 * Listener method called when the save file of the DemoCommandList is
	 * changed. Updates the name of the menu item (because we use the save file
	 * as the demo name) and the dirty bit.
	 */
	public void saveFileChanged(DemoCommandList source) {
		menuItem.setText(source.getName());
		dirtyChanged(source, source.isDirty());
	}

	/**
	 * Converts this DCLE to a String. Equal to the command list's name.
	 */
	public String toString() {
		return cmdList.getName();
	}

	private boolean undoFiring = false;
	private Set<UndoableEditListener> undoableEditListeners = null;
	private Set<UndoableEditListener> toAddUEL = null;
	private Set<UndoableEditListener> toRemoveUEL = null;

	/**
	 * Adds a new UndoableEditListener to receive events when undoable edits are
	 * made.
	 * 
	 * @param uel
	 */
	public void addUndoableEditListener(UndoableEditListener uel) {
		if (undoableEditListeners == null)
			undoableEditListeners = new HashSet<UndoableEditListener>();
		if (undoFiring) {
			if (toAddUEL == null)
				toAddUEL = new HashSet<UndoableEditListener>();
			toAddUEL.add(uel);
		} else {
			undoableEditListeners.add(uel);
		}
	}

	/**
	 * Removes the specified UndoableEditListener
	 * 
	 * @param uel
	 */
	public void removeUndoableEditListener(UndoableEditListener uel) {
		if (undoableEditListeners == null)
			return;
		if (undoFiring) {
			if (toRemoveUEL == null)
				toRemoveUEL = new HashSet<UndoableEditListener>();
			toRemoveUEL.add(uel);
		} else {
			undoableEditListeners.remove(uel);
		}
	}

	/**
	 * Fires the undoable edit listeners when an undoable edit is made.
	 * 
	 * @param edit
	 */
	protected void fireUndoableEditListeners(UndoableEdit edit) {
		if (undoableEditListeners == null)
			return;
		undoFiring = true;
		UndoableEditEvent uee = new UndoableEditEvent(this, edit);
		for (UndoableEditListener uel : undoableEditListeners)
			uel.undoableEditHappened(uee);
		undoFiring = false;
		if (toAddUEL != null && toAddUEL.size() > 0) {
			for (UndoableEditListener uel : toAddUEL)
				addUndoableEditListener(uel);
			toAddUEL.clear();
		}
		if (toRemoveUEL != null && toRemoveUEL.size() > 0) {
			for (UndoableEditListener uel : toRemoveUEL)
				removeUndoableEditListener(uel);
			toRemoveUEL.clear();
		}
	}

	/**
	 * Listener method that sets the title of this internal frame whenever the
	 * dirty bit is changed. Dirty editors append a star to the end of their
	 * normal names.
	 */
	public void dirtyChanged(DirtyBitTracker source, boolean newValue) {
		String name = cmdList.getName();
		if (cmdList.isDirty())
			name += "*";
		setTitle(name);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		jButton1 = new javax.swing.JButton();
		toolbar = new javax.swing.JToolBar();
		insertButton = new javax.swing.JButton();
		deleteButton = new javax.swing.JButton();
		deleteAllButton = new javax.swing.JButton();
		changeIDButton = new javax.swing.JButton();
		timeShiftButton = new javax.swing.JButton();
		timeScaleButton = new javax.swing.JButton();
		posShiftButton = new javax.swing.JButton();
		upButton = new javax.swing.JButton();
		downButton = new javax.swing.JButton();
		importButton = new javax.swing.JButton();
		exportButton = new javax.swing.JButton();
		resortButton = new javax.swing.JButton();

		jButton1.setText("jButton1");

		setBackground(new java.awt.Color(255, 255, 255));
		setTitle(cmdList.getName());
		toolbar.setFloatable(false);
		insertButton.setText("Insert");
		insertButton.setToolTipText("inserts a new command");
		insertButton.setBorder(new javax.swing.border.BevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		insertButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				insertButtonActionPerformed(evt);
			}
		});

		toolbar.add(insertButton);

		deleteButton.setText("Delete");
		deleteButton.setToolTipText("deletes the selected command");
		deleteButton.setBorder(new javax.swing.border.BevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		deleteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteButtonActionPerformed(evt);
			}
		});

		toolbar.add(deleteButton);

		deleteAllButton.setText("Delete All");
		deleteAllButton
				.setToolTipText("deletes all currently displayed commands");
		deleteAllButton.setBorder(new javax.swing.border.BevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		deleteAllButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteAllButtonActionPerformed(evt);
			}
		});

		toolbar.add(deleteAllButton);

		changeIDButton.setText("Change All");
		changeIDButton
				.setToolTipText("change all visible commands to have the same time, reference, command, and/or arguments");
		changeIDButton.setBorder(new javax.swing.border.BevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		changeIDButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				changeIDButtonActionPerformed(evt);
			}
		});

		toolbar.add(changeIDButton);

		timeShiftButton.setText("Time Shift");
		timeShiftButton
				.setToolTipText("adjust the time of all visible commands by a certain amount");
		timeShiftButton.setBorder(new javax.swing.border.BevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		timeShiftButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				timeShiftButtonActionPerformed(evt);
			}
		});

		toolbar.add(timeShiftButton);

		timeScaleButton.setText("Time Scale");
		timeScaleButton.setBorder(new javax.swing.border.BevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		timeScaleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				timeScaleButtonActionPerformed(evt);
			}
		});

		toolbar.add(timeScaleButton);

		posShiftButton.setText("Translate POS");
		posShiftButton.setBorder(new javax.swing.border.BevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		posShiftButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				posShiftButtonActionPerformed(evt);
			}
		});

		toolbar.add(posShiftButton);

		upButton.setMnemonic('u');
		upButton.setText("Move Up");
		upButton.setToolTipText("moves the currect command one line up");
		upButton.setBorder(new javax.swing.border.BevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		upButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				upButtonActionPerformed(evt);
			}
		});

		toolbar.add(upButton);

		downButton.setMnemonic('d');
		downButton.setText("Move Down");
		downButton.setToolTipText("moves the current command one space down");
		downButton.setBorder(new javax.swing.border.BevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		downButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				downButtonActionPerformed(evt);
			}
		});

		toolbar.add(downButton);

		importButton.setText("Import Commands");
		importButton
				.setToolTipText("adds the visible commands from another demo to the end of this one");
		importButton.setBorder(new javax.swing.border.BevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		importButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				importButtonActionPerformed(evt);
			}
		});

		toolbar.add(importButton);

		exportButton.setText("Export Commands");
		exportButton
				.setToolTipText("exports the visible commands from this demo to a new one");
		exportButton.setBorder(new javax.swing.border.BevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		exportButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exportButtonActionPerformed(evt);
			}
		});

		toolbar.add(exportButton);

		resortButton.setText("Resort");
		resortButton
				.setToolTipText("sorts the commands in increasing chronological order");
		resortButton.setBorder(new javax.swing.border.BevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		resortButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resortButtonActionPerformed(evt);
			}
		});

		toolbar.add(resortButton);

		getContentPane().add(toolbar, java.awt.BorderLayout.NORTH);

		pack();
	}

	// </editor-fold>//GEN-END:initComponents

	private void posShiftButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_posShiftButtonActionPerformed
		posShiftDialog.setVisible(true);
	}// GEN-LAST:event_posShiftButtonActionPerformed

	private void timeScaleButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_timeScaleButtonActionPerformed
		timeScaleDialog.setVisible(true);
	}// GEN-LAST:event_timeScaleButtonActionPerformed

	private void deleteAllButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_deleteAllButtonActionPerformed
		UndoableEdit edit = cmdList.removeVisibleCommands();
		fireUndoableEditListeners(edit);
	}// GEN-LAST:event_deleteAllButtonActionPerformed

	private void timeShiftButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_timeShiftButtonActionPerformed
		timeShiftDialog.setVisible(true);
	}// GEN-LAST:event_timeShiftButtonActionPerformed

	private void changeIDButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_changeIDButtonActionPerformed
		changeAllDialog.setVisible(true);
	}// GEN-LAST:event_changeIDButtonActionPerformed

	private void resortButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_resortButtonActionPerformed
		UndoableEdit edit = cmdList.resort();
		fireUndoableEditListeners(edit);
	}// GEN-LAST:event_resortButtonActionPerformed

	private DemoCommandListChooser dclc = null;

	private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_importButtonActionPerformed
		if (dclc == null) {
			dclc = new DemoCommandListChooser(DemoEditor.getEditor());
			dclc.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent pce) {
					DemoCommandListEditor sourceEditor = ((DemoCommandListChooser) pce
							.getSource()).getValue();
					if (sourceEditor == null) {
						dclc.setVisible(false);
						return;
					}
					DemoCommandList source = sourceEditor.getDemoCommandList();
					int numVisible = source.getVisibleCommandCount();
					CompoundEdit edit = new CompoundEdit();
					for (int i = 0; i < numVisible; i++)
						edit.addEdit(cmdList.addCommand(source
								.getVisibleCommand(i).clone()));
					edit.end();
					cmdList.setDirty(true);
					fireUndoableEditListeners(edit);
				}
			});
		}
		dclc.setVisible(true);
	}// GEN-LAST:event_importButtonActionPerformed

	private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_exportButtonActionPerformed
		DemoCommandList exportCmdList = cmdList.exportVisible();
		exportCmdList.setDirty(true);
		DemoEditor.getEditor().addDemo(exportCmdList);
	}// GEN-LAST:event_exportButtonActionPerformed

	private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_deleteButtonActionPerformed
		ListSelectionModel lsm = cmdListPanel.getSelectionModel();
		int minIndex = lsm.getMinSelectionIndex();
		int maxIndex = lsm.getMaxSelectionIndex();
		if (minIndex == -1 || maxIndex == -1)
			return;
		CompoundEdit edit = new CompoundEdit();
		for (int index = maxIndex; index >= minIndex; index--) {
			UndoableEdit cEdit = cmdList.removeCommand(index);
			if (cEdit != null)
				edit.addEdit(cEdit);
		}
		edit.end();
		fireUndoableEditListeners(edit);
		if (minIndex < cmdList.getVisibleCommandCount()) {
			lsm.setSelectionInterval(minIndex, minIndex);
		} else {
			lsm.setSelectionInterval(cmdList.getVisibleCommandCount() - 1,
					cmdList.getVisibleCommandCount() - 1);
		}
	}// GEN-LAST:event_deleteButtonActionPerformed

	private void insertButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_insertButtonActionPerformed
		DemoCommand newCmd = new DemoCommand(0, 0, "NUL", "\"new command\"");
		int index = cmdListPanel.getSelectionModel().getMinSelectionIndex();
		UndoableEdit edit = cmdList.addCommand(index, newCmd);
		if (index >= 0)
			cmdListPanel.getSelectionModel().setSelectionInterval(index, index);
		fireUndoableEditListeners(edit);
	}// GEN-LAST:event_insertButtonActionPerformed

	private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_downButtonActionPerformed
		int selectedRow = cmdListPanel.getSelectedRow();
		if (selectedRow >= 0
				&& selectedRow < cmdList.getVisibleCommandCount() - 1) {
			UndoableEdit edit = cmdList.moveDemoCommandDown(selectedRow);
			cmdListPanel.setRowSelectionInterval(selectedRow + 1,
					selectedRow + 1);
			fireUndoableEditListeners(edit);
		}
	}// GEN-LAST:event_downButtonActionPerformed

	private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-
		// FIRST
		// :
		// event_upButtonActionPerformed
		int selectedRow = cmdListPanel.getSelectedRow();
		if (selectedRow > 0 && selectedRow < cmdList.getVisibleCommandCount()) {
			UndoableEdit edit = cmdList.moveDemoCommandUp(selectedRow);
			cmdListPanel.setRowSelectionInterval(selectedRow - 1,
					selectedRow - 1);
			fireUndoableEditListeners(edit);
		}
	}// GEN-LAST:event_upButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton changeIDButton;
	private javax.swing.JButton deleteAllButton;
	private javax.swing.JButton deleteButton;
	private javax.swing.JButton downButton;
	private javax.swing.JButton exportButton;
	private javax.swing.JButton importButton;
	private javax.swing.JButton insertButton;
	private javax.swing.JButton jButton1;
	private javax.swing.JButton posShiftButton;
	private javax.swing.JButton resortButton;
	private javax.swing.JButton timeScaleButton;
	private javax.swing.JButton timeShiftButton;
	private javax.swing.JToolBar toolbar;
	private javax.swing.JButton upButton;
	// End of variables declaration//GEN-END:variables

}
