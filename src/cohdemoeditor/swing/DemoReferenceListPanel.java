/*
 * DemoReferenceListPanel.java
 *
 * Created on June 22, 2005, 12:39 PM
 */

package cohdemoeditor.swing;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.DemoReferenceList;

/**
 * A DemoReferenceListPanel appears on the left side of each
 * DemoCommandListEditor window and consists primary of a list of all references
 * in the given demo. It also provides a button to easily add a new filter for
 * the selected reference.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class DemoReferenceListPanel extends javax.swing.JPanel implements
		ListSelectionListener, TableModelListener {

	private DemoCommandListTable cmdListPanel;
	private DemoCommandList cmdList;
	private DemoReferenceList refList;
	private Action addFilterAction;
	private boolean showVisible = false;

	/** Creates new form DemoReferenceListPanel */
	public DemoReferenceListPanel(DemoCommandListTable cmdListPanel) {
		if (cmdListPanel == null)
			throw new NullPointerException(
					"Cannot create a DemoReferenceListPanel with a null DemoCommandListTable");
		this.cmdListPanel = cmdListPanel;
		cmdList = cmdListPanel.getDemoCommandList();
		cmdList.addTableModelListener(this);
		cmdListPanel.getSelectionModel().addListSelectionListener(this);
		refList = cmdList.getDemoReferenceList();
		addFilterAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				DemoCommandListFilter filter = new DemoCommandListFilter(
						DemoCommandListFilter.SHOW_THESE);
				int[] selectedRows = referenceTable.getSelectedRows();
				if (selectedRows.length == 0)
					return;
				for (int i = 0; i < selectedRows.length; i++) {
					String refStr = (String) DemoReferenceListPanel.this.refList
							.getValueAt(selectedRows[i], 0);
					String[] tokens = refStr.split(" ");
					filter.addReference(DemoCommand.getRefNumFor(tokens[0]));
				}
				DemoReferenceListPanel.this.cmdList.getFilterList().add(filter);
			}
		};
		initComponents();
	}

	/**
	 * Simple getter for showVisible
	 * 
	 * @return
	 */
	public boolean isShowVisible() {
		return showVisible;
	}

	/**
	 * Setter for showVisible. Changes the TableModel when changed.
	 * 
	 * @param showVisible
	 */
	public void setShowVisible(boolean showVisible) {
		if (this.showVisible == showVisible)
			return;
		this.showVisible = showVisible;
		if (showVisible)
			refList = cmdList.getVisibleDemoReferenceList();
		else
			refList = cmdList.getDemoReferenceList();
		referenceTable.setModel(refList);
	}

	/**
	 * Listener method for when the underlying table model is changed. This DRLP
	 * listens to a DemoReferenceList.
	 */
	public void tableChanged(TableModelEvent e) {
		if (e.getColumn() == 1) {
			refreshSelection(cmdListPanel.getSelectionModel());
		}
	}

	/**
	 * Listener method for when the underlying list model is changed. This DRLP
	 * listens to the row selection model of a DemoCommandListTable.
	 */
	public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel clsm = (ListSelectionModel) e.getSource();
		refreshSelection(clsm);
	}

	/**
	 * Helper method to change the selection when the selection on the
	 * DemoCommandListTable is changed. Ideally, we want the selection to move
	 * to the reference for the currently selected command.
	 * 
	 * @param clsm
	 */
	private void refreshSelection(ListSelectionModel clsm) {
		ListSelectionModel rlsm = referenceTable.getSelectionModel();
		rlsm.clearSelection();
		final int minIndex = clsm.getMinSelectionIndex();
		final int maxIndex = clsm.getMaxSelectionIndex();
		if (minIndex == -1 || maxIndex == -1) {
			return;
		}
		int refIndex;
		for (int i = minIndex; i <= maxIndex; i++) {
			if (clsm.isSelectedIndex(i)) {
				refIndex = refList.getRow(refList.getReferenceFor(cmdList
						.getVisibleCommand(i).getReference()));
				rlsm.addSelectionInterval(refIndex, refIndex);
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
		southPanel = new javax.swing.JPanel();
		filterButton = new javax.swing.JButton();
		scroller = new javax.swing.JScrollPane();
		referenceTable = new javax.swing.JTable();
		referenceTable
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		referenceTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						addFilterAction.setEnabled(referenceTable
								.getSelectedRowCount() > 0);
					}
				});
		northPanel = new javax.swing.JPanel();
		titleLabel = new javax.swing.JLabel();
		comboBox = new javax.swing.JComboBox();

		setLayout(new java.awt.BorderLayout());

		southPanel.setLayout(new java.awt.GridLayout(1, 0));

		filterButton.setAction(addFilterAction);
		filterButton.setText("Add Filter for Reference(s)");
		filterButton
				.setToolTipText("Adds a filter to show only the currently highlighted references.");
		southPanel.add(filterButton);

		add(southPanel, java.awt.BorderLayout.SOUTH);

		referenceTable.setModel(cmdList.getDemoReferenceList());
		scroller.setViewportView(referenceTable);

		add(scroller, java.awt.BorderLayout.CENTER);

		northPanel.setLayout(new java.awt.BorderLayout());

		titleLabel.setFont(new java.awt.Font("Tahoma", 1, 14));
		titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		titleLabel.setText("References");
		titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		northPanel.add(titleLabel, java.awt.BorderLayout.NORTH);

		comboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"Show All References", "Show Visible References" }));
		comboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				comboBoxActionPerformed(evt);
			}
		});

		northPanel.add(comboBox, java.awt.BorderLayout.CENTER);

		add(northPanel, java.awt.BorderLayout.NORTH);

	}// </editor-fold>//GEN-END:initComponents

	private void comboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-
		// FIRST
		// :
		// event_comboBoxActionPerformed
		setShowVisible(comboBox.getSelectedIndex() == 1);
	}// GEN-LAST:event_comboBoxActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JComboBox comboBox;
	private javax.swing.JButton filterButton;
	private javax.swing.JPanel northPanel;
	private javax.swing.JTable referenceTable;
	private javax.swing.JScrollPane scroller;
	private javax.swing.JPanel southPanel;
	private javax.swing.JLabel titleLabel;
	// End of variables declaration//GEN-END:variables

}