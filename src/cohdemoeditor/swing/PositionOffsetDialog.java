/*
 * PositionOffsetDialog.java
 *
 * Created on June 24, 2005, 4:41 PM
 */

package cohdemoeditor.swing;

import javax.swing.*;

import cohdemoeditor.DemoCommandList;

/**
 * This dialog provides the user interface for a user to offset all POS commands
 * by a fixed amount.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class PositionOffsetDialog extends JCenteringDialog {

	private DemoCommandList cmdList;

	/**
	 * Creates new form PositionOffsetDialog
	 * 
	 * @param cmdList
	 *            the target DemoCommandList
	 * @param editor
	 *            the editor with which to register undoable commands
	 */
	public PositionOffsetDialog(DemoCommandList cmdList) {
		super(DemoEditor.getEditor(), false);
		this.cmdList = cmdList;
		initComponents();
	}

	/**
	 * Resets the UI to initial values.
	 */
	public void reset() {
		xField.setDouble(0);
		zField.setDouble(0);
		yField.setDouble(0);
	}

	/**
	 * Performs the offset by delegating to DemoCommandList.offSetPositions
	 */
	public void doOffset() {
		DemoEditor.getEditor().addUndoableEdit(cmdList.offSetPositions(xField.getDouble(),
				zField.getDouble(), yField.getDouble()));
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
		jLabel1 = new javax.swing.JLabel();
		centerPanel = new javax.swing.JPanel();
		xLabel = new javax.swing.JLabel();
		xField = new cohdemoeditor.swing.JDoubleTextField();
		zLabel = new javax.swing.JLabel();
		zField = new cohdemoeditor.swing.JDoubleTextField();
		yLabel = new javax.swing.JLabel();
		yField = new cohdemoeditor.swing.JDoubleTextField();
		southPanel = new javax.swing.JPanel();
		okayButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		helpButton = new javax.swing.JButton();

		setTitle("Position Offset");
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				formWindowClosing(evt);
			}
		});

		jLabel1
				.setText("Please Enter the Amout by which to Offset POS Commands");
		jLabel1.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
		getContentPane().add(jLabel1, java.awt.BorderLayout.NORTH);

		centerPanel.setLayout(new java.awt.GridLayout(3, 2, 5, 0));

		centerPanel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
		xLabel.setText("East/West (X) offset");
		centerPanel.add(xLabel);

		xField.setText("0");
		centerPanel.add(xField);

		zLabel.setText("Up/Down (Z) offset");
		centerPanel.add(zLabel);

		zField.setText("0");
		centerPanel.add(zField);

		yLabel.setText("North/South (Y) offset");
		centerPanel.add(yLabel);

		yField.setText("0");
		centerPanel.add(yField);

		getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

		okayButton.setMnemonic('O');
		okayButton.setText("Okay");
		okayButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				okayButtonActionPerformed(evt);
			}
		});

		southPanel.add(okayButton);

		cancelButton.setMnemonic('C');
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		southPanel.add(cancelButton);

		helpButton.setMnemonic('H');
		helpButton.setText("Help");
		helpButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				helpButtonActionPerformed(evt);
			}
		});

		southPanel.add(helpButton);

		getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

		pack();
	}

	// </editor-fold>//GEN-END:initComponents

	private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_helpButtonActionPerformed
		JOptionPane
				.showMessageDialog(
						this,
						"Enter the amounts to offset the (x,z,y) coordinates of every visible POS command.",
						"Temporary Help Message",
						JOptionPane.INFORMATION_MESSAGE);
	}// GEN-LAST:event_helpButtonActionPerformed

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_cancelButtonActionPerformed
		setVisible(false);
	}// GEN-LAST:event_cancelButtonActionPerformed

	private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:
		// event_formWindowClosing
		reset();
	}// GEN-LAST:event_formWindowClosing

	private void okayButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_okayButtonActionPerformed
		doOffset();
	}// GEN-LAST:event_okayButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton cancelButton;
	private javax.swing.JPanel centerPanel;
	private javax.swing.JButton helpButton;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JButton okayButton;
	private javax.swing.JPanel southPanel;
	private cohdemoeditor.swing.JDoubleTextField xField;
	private javax.swing.JLabel xLabel;
	private cohdemoeditor.swing.JDoubleTextField yField;
	private javax.swing.JLabel yLabel;
	private cohdemoeditor.swing.JDoubleTextField zField;
	private javax.swing.JLabel zLabel;
	// End of variables declaration//GEN-END:variables

}
