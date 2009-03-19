/*
 * LoadFilterListDialog.java
 *
 * Created on June 21, 2005, 10:47 PM
 */

package cohdemoeditor.swing;

import java.awt.*;
import javax.swing.*;

import cohdemoeditor.FilterList;
import cohdemoeditor.FilterListManager;

/**
 * This dialog provides the user interface for the user to load a previously
 * saved list of filters.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class LoadFilterListDialog extends JCenteringDialog {

	private FilterList target = null;
	private FilterListManager manager = null;

	/** Creates new form LoadFilterListDialog */
	public LoadFilterListDialog(DemoEditor editor) {
		super(editor, true);
		setPreferredSize(new Dimension(400, 200));
		initComponents();
	}

	/** Creates new form LoadFilterListDialog */
	public LoadFilterListDialog(Dialog parent) {
		super(parent, true);
		setPreferredSize(new Dimension(400, 200));
		initComponents();
		if (parent == null)
			return;
		Dimension parentSize = parent.getSize();
		Dimension mySize = getSize();
		Point parentLocation = parent.getLocation();
		setLocation(parentLocation.x + (parentSize.width - mySize.width) / 2,
				parentLocation.y + (parentSize.height - mySize.height) / 2);
	}

	/**
	 * Method used to show the LoadFilterListDialog
	 * 
	 * @param target
	 *            the FilterList where the loaded Filters will be placed
	 * @param manager
	 *            the FilterListManager containing the FilterLists that can be
	 *            loaded
	 */
	public void showLoadFilterList(FilterList target, FilterListManager manager) {
		if (target == null || manager == null)
			return;
		this.target = target;
		this.manager = manager;
		filterList.setModel(manager);
		pack();
		setVisible(true);
	}

	/**
	 * Method for when the Cancel button is pressed. Hides the dialog.
	 */
	private void doCancel() {
		setVisible(false);
	}

	/**
	 * Method for when the Delete button is pressed.
	 */
	private void doDelete() {
		if (target == null || manager == null) {
			JOptionPane
					.showMessageDialog(
							this,
							"Error: either the current filter list or the manager is null.",
							"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		int index = filterList.getMinSelectionIndex();
		if (index < 0 || index >= manager.size()) {
			JOptionPane.showMessageDialog(this, "Error: no filter selected.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		FilterList toDelete = manager.getFilterList(filterList
				.getMinSelectionIndex());
		manager.removeFilterList(toDelete);
	}

	/**
	 * Method for when the Load button is pressed.
	 */
	private void doLoad() {
		if (target == null || manager == null) {
			JOptionPane
					.showMessageDialog(
							this,
							"Error: either the current filter list or the manager is null.",
							"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		int index = filterList.getMinSelectionIndex();
		if (index < 0 || index >= manager.size()) {
			JOptionPane.showMessageDialog(this, "Error: no filter selected.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		FilterList toLoad = manager.getFilterList(filterList
				.getMinSelectionIndex());
		target.clear();
		target.add(toLoad);
		setVisible(false);
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
		loadButton = new javax.swing.JButton();
		deleteButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		titleLabel = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		filterList = new javax.swing.JList();

		setTitle("Load Filter List");
		loadButton.setMnemonic('l');
		loadButton.setText("Load");
		loadButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loadButtonActionPerformed(evt);
			}
		});

		southPanel.add(loadButton);

		deleteButton.setText("Delete");
		deleteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteButtonActionPerformed(evt);
			}
		});

		southPanel.add(deleteButton);

		cancelButton.setMnemonic('c');
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		southPanel.add(cancelButton);

		getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

		titleLabel.setText("Select the filter list to load.");
		titleLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
		getContentPane().add(titleLabel, java.awt.BorderLayout.NORTH);

		jScrollPane1
				.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		filterList
				.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		jScrollPane1.setViewportView(filterList);

		getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

		pack();
	}

	// </editor-fold>//GEN-END:initComponents

	private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_deleteButtonActionPerformed
		doDelete();
	}// GEN-LAST:event_deleteButtonActionPerformed

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_cancelButtonActionPerformed
		doCancel();
	}// GEN-LAST:event_cancelButtonActionPerformed

	private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_loadButtonActionPerformed
		doLoad();
	}// GEN-LAST:event_loadButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton cancelButton;
	private javax.swing.JButton deleteButton;
	private javax.swing.JList filterList;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JButton loadButton;
	private javax.swing.JPanel southPanel;
	private javax.swing.JLabel titleLabel;
	// End of variables declaration//GEN-END:variables

}
