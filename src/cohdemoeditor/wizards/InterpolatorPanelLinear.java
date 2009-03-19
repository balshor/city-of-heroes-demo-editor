/*
 * InterpolatorPanelLinear.java
 *
 * Created on July 27, 2007, 9:11 PM
 */

package cohdemoeditor.wizards;

import java.awt.*;
import javax.swing.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;

/**
 * This class defines a JPanel containing components that allow the user to
 * input information about a linear POS/PYR path. The method
 * <code>generateCommands</code> creates a new <code>DemoCommandList</code>
 * containing the requested path.
 * 
 * @author Balshor
 */
@SuppressWarnings("serial")
public class InterpolatorPanelLinear extends javax.swing.JPanel {

	private Dialog parent;

	/**
	 * Creates new form InterpolatorPanelLinear The parent <code>Dialog</code>
	 * is used only as the parent of feedback messages in
	 * <code>JOptionPane</code>s.
	 */
	public InterpolatorPanelLinear(Dialog parent) {
		this.parent = parent;
		initComponents();
	}

	/**
	 * Validates the input data. Creates a new <code>DemoCommandList</code>
	 * containing the requested linear movement path. Returns <code>null</code>
	 * if there are any errors in the input data.
	 */
	public DemoCommandList generateCommands() {
		final int startTime, timePerStep, numSteps;
		double startX = 0, startZ = 0, startY = 0, endX = 0, endZ = 0, endY = 0;
		double startPitch = 0, startYaw = 0, startRoll = 0, endPitch = 0, endYaw = 0, endRoll = 0;
		try {
			startTime = (Integer) startTimeSpinner.getValue();
		} catch (ClassCastException cce) {
			JOptionPane.showMessageDialog(parent, "Error reading start time.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (startTime < 1) {
			JOptionPane.showMessageDialog(parent,
					"Please enter a positive start time.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		try {
			timePerStep = (Integer) timePerStepSpinner.getValue();
		} catch (ClassCastException cce) {
			JOptionPane.showMessageDialog(parent,
					"Error reading time per step.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (timePerStep < 1) {
			JOptionPane.showMessageDialog(parent,
					"Please enter a positive amount of time per step.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		try {
			numSteps = (Integer) numStepsSpinner.getValue();
		} catch (ClassCastException cce) {
			JOptionPane.showMessageDialog(parent,
					"Error reading number of steps.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (numSteps < 1) {
			JOptionPane.showMessageDialog(parent,
					"You must have at least one step.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}

		final boolean doPOS = posCheckBox.isSelected();
		final boolean doPYR = pyrCheckBox.isSelected();
		if (!posCheckBox.isSelected() && !pyrCheckBox.isSelected()) {
			JOptionPane.showMessageDialog(parent,
					"Nothing to generate!  Please select POS and/or PYR.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		if (doPOS && !hasPOSInput()) {
			JOptionPane.showMessageDialog(parent,
					"Cannot generate POS commands without start/end points.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		if (doPYR && !hasPYRInput()) {
			JOptionPane
					.showMessageDialog(
							parent,
							"Cannot generate PYR commands without start/end orientations.",
							"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		if (doPOS) {
			startX = startXField.getDouble();
			startZ = startZField.getDouble();
			startY = startYField.getDouble();
			endX = endXField.getDouble();
			endZ = endZField.getDouble();
			endY = endYField.getDouble();
		}

		if (doPYR) {
			startPitch = startPitchField.getDouble();
			startYaw = startYawField.getDouble();
			startRoll = startRollField.getDouble();
			endPitch = endPitchField.getDouble();
			endYaw = endYawField.getDouble();
			endRoll = endRollField.getDouble();
		}

		String refString = refField.getText();
		if (refString.equals("")) {
			JOptionPane.showMessageDialog(parent,
					"Please enter a reference number.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (refString.contains(" ")) {
			JOptionPane.showMessageDialog(parent, "Could not parse reference.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		int refNumber = DemoCommand.getRefNumFor(refString);

		DemoCommandList outputList = new DemoCommandList();
		DemoCommand dcmd;
		String str;
		int time = startTime;
		for (double i = 0; i <= numSteps; i++) {
			if (doPOS) {
				double x, z, y;
				x = truncate(startX + i / (numSteps) * (endX - startX));
				z = truncate(startZ + i / (numSteps) * (endZ - startZ));
				y = truncate(startY + i / (numSteps) * (endY - startY));
				str = "" + x + " " + z + " " + y;
				dcmd = new DemoCommand(time, refNumber, "POS", str);
				outputList.addCommand(dcmd);
			}
			if (doPYR) {
				double p, y, r;
				p = truncate(startPitch + i / (numSteps)
						* (endPitch - startPitch));
				y = truncate(startYaw + i / (numSteps) * (endYaw - startYaw));
				r = truncate(startRoll + i / (numSteps) * (endRoll - startRoll));
				str = "" + p + " " + y + " " + r;
				dcmd = new DemoCommand(time, refNumber, "PYR", str);
				outputList.addCommand(dcmd);
			}
			time += timePerStep;
		}

		return outputList;
	}

	/**
	 * Helper method to determine if the JTextField is empty
	 * 
	 * @param field
	 * @return
	 */
	private boolean isEmpty(JTextField field) {
		return field.getText().equals("");
	}

	/**
	 * Helper method to determine if all POS fields are non-empty
	 * 
	 * @return
	 */
	private boolean hasPOSInput() {
		return !(isEmpty(startXField) || isEmpty(endXField)
				|| isEmpty(startZField) || isEmpty(endZField)
				|| isEmpty(startYField) || isEmpty(endYField));
	}

	/**
	 * Helper method to determine if all PYR fields are non-empty
	 * 
	 * @return
	 */
	private boolean hasPYRInput() {
		return !(isEmpty(startPitchField) || isEmpty(endPitchField)
				|| isEmpty(startYawField) || isEmpty(endYawField)
				|| isEmpty(startRollField) || isEmpty(endRollField));
	}

	// Helper function so we get a max of 6 decimal places.
	private double truncate(double d) {
		return Math.floor(d * 1000000) / 1000000;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		northLabel = new javax.swing.JLabel();
		centerPanel = new javax.swing.JPanel();
		timeAndRefPanel = new javax.swing.JPanel();
		startTimeLabel = new javax.swing.JLabel();
		startTimeSpinner = new javax.swing.JSpinner(new SpinnerNumberModel(1,
				1, Integer.MAX_VALUE, 1));
		timePerStepLabel = new javax.swing.JLabel();
		timePerStepSpinner = new javax.swing.JSpinner(new SpinnerNumberModel(1,
				1, Integer.MAX_VALUE, 1));
		numStepsLabel = new javax.swing.JLabel();
		numStepsSpinner = new javax.swing.JSpinner(new SpinnerNumberModel(2, 2,
				Integer.MAX_VALUE, 1));
		refLabel = new javax.swing.JLabel();
		refField = new javax.swing.JTextField();
		posPanel = new javax.swing.JPanel();
		posCheckBox = new javax.swing.JCheckBox();
		xLabel = new javax.swing.JLabel();
		zLabel = new javax.swing.JLabel();
		yLabel = new javax.swing.JLabel();
		startPointLabel = new javax.swing.JLabel();
		startXField = new cohdemoeditor.swing.JDoubleTextField();
		startZField = new cohdemoeditor.swing.JDoubleTextField();
		startYField = new cohdemoeditor.swing.JDoubleTextField();
		endPointLabel = new javax.swing.JLabel();
		endXField = new cohdemoeditor.swing.JDoubleTextField();
		endZField = new cohdemoeditor.swing.JDoubleTextField();
		endYField = new cohdemoeditor.swing.JDoubleTextField();
		pyrPanel = new javax.swing.JPanel();
		pyrCheckBox = new javax.swing.JCheckBox();
		pitchLabel = new javax.swing.JLabel();
		yawLabel = new javax.swing.JLabel();
		rollLabel = new javax.swing.JLabel();
		startPYRLabel = new javax.swing.JLabel();
		startPitchField = new cohdemoeditor.swing.JDoubleTextField();
		startYawField = new cohdemoeditor.swing.JDoubleTextField();
		startRollField = new cohdemoeditor.swing.JDoubleTextField();
		endPYRLabel = new javax.swing.JLabel();
		endPitchField = new cohdemoeditor.swing.JDoubleTextField();
		endYawField = new cohdemoeditor.swing.JDoubleTextField();
		endRollField = new cohdemoeditor.swing.JDoubleTextField();

		setLayout(new java.awt.BorderLayout());

		northLabel.setFont(new java.awt.Font("Tahoma", 1, 14));
		northLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		northLabel.setText("Linear Interpolation");
		northLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
		add(northLabel, java.awt.BorderLayout.NORTH);

		centerPanel.setLayout(new javax.swing.BoxLayout(centerPanel,
				javax.swing.BoxLayout.Y_AXIS));

		centerPanel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 0, 5, 0)));
		timeAndRefPanel.setLayout(new java.awt.GridLayout(4, 2, 0, 5));

		timeAndRefPanel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
		startTimeLabel.setText("Start Time");
		startTimeLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(0, 5, 0, 5)));
		timeAndRefPanel.add(startTimeLabel);

		startTimeSpinner.setToolTipText("Enter the time of the first command.");
		timeAndRefPanel.add(startTimeSpinner);

		timePerStepLabel.setText("Time per Step");
		timePerStepLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(0, 5, 0, 5)));
		timeAndRefPanel.add(timePerStepLabel);

		timePerStepSpinner
				.setToolTipText("Enter the number of ms between commands.");
		timeAndRefPanel.add(timePerStepSpinner);

		numStepsLabel.setText("Number of Steps");
		numStepsLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(0, 5, 0, 5)));
		timeAndRefPanel.add(numStepsLabel);

		numStepsSpinner
				.setToolTipText("Enter the number of commands to generate.");
		timeAndRefPanel.add(numStepsSpinner);

		refLabel.setText("Reference");
		refLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(0, 5, 0, 5)));
		timeAndRefPanel.add(refLabel);

		refField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		refField.setToolTipText("Enter the reference number to use.");
		timeAndRefPanel.add(refField);

		centerPanel.add(timeAndRefPanel);

		posPanel.setLayout(new java.awt.GridLayout(3, 4, 0, 5));

		posPanel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
		posCheckBox.setSelected(true);
		posCheckBox.setText("Create POS");
		posCheckBox.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				posCheckBoxItemStateChanged(evt);
			}
		});

		posPanel.add(posCheckBox);

		xLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		xLabel.setText("X");
		posPanel.add(xLabel);

		zLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		zLabel.setText("Z");
		posPanel.add(zLabel);

		yLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		yLabel.setText("Y");
		posPanel.add(yLabel);

		startPointLabel.setText("Start POS");
		startPointLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(0, 5, 0, 5)));
		posPanel.add(startPointLabel);

		startXField.setText("0");
		posPanel.add(startXField);

		startZField.setText("0");
		posPanel.add(startZField);

		startYField.setText("0");
		posPanel.add(startYField);

		endPointLabel.setText("End POS");
		endPointLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(0, 5, 0, 5)));
		posPanel.add(endPointLabel);

		endXField.setText("0");
		posPanel.add(endXField);

		endZField.setText("0");
		posPanel.add(endZField);

		endYField.setText("0");
		posPanel.add(endYField);

		centerPanel.add(posPanel);

		pyrPanel.setLayout(new java.awt.GridLayout(3, 4, 0, 5));

		pyrPanel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
		pyrCheckBox.setSelected(true);
		pyrCheckBox.setText("Create PYR");
		pyrCheckBox.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				pyrCheckBoxItemStateChanged(evt);
			}
		});

		pyrPanel.add(pyrCheckBox);

		pitchLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		pitchLabel.setText("P");
		pyrPanel.add(pitchLabel);

		yawLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		yawLabel.setText("Y");
		pyrPanel.add(yawLabel);

		rollLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		rollLabel.setText("R");
		pyrPanel.add(rollLabel);

		startPYRLabel.setText("Start PYR");
		startPYRLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(0, 5, 0, 5)));
		pyrPanel.add(startPYRLabel);

		startPitchField.setText("0");
		pyrPanel.add(startPitchField);

		startYawField.setText("0");
		pyrPanel.add(startYawField);

		startRollField.setText("0");
		pyrPanel.add(startRollField);

		endPYRLabel.setText("End PYR");
		endPYRLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(0, 5, 0, 5)));
		pyrPanel.add(endPYRLabel);

		endPitchField.setText("0");
		pyrPanel.add(endPitchField);

		endYawField.setText("0");
		pyrPanel.add(endYawField);

		endRollField.setText("0");
		pyrPanel.add(endRollField);

		centerPanel.add(pyrPanel);

		add(centerPanel, java.awt.BorderLayout.CENTER);

	}

	// </editor-fold>//GEN-END:initComponents

	private void pyrCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_pyrCheckBoxItemStateChanged
		boolean b = pyrCheckBox.isSelected();
		startPitchField.setEnabled(b);
		startYawField.setEnabled(b);
		startRollField.setEnabled(b);
		endPitchField.setEnabled(b);
		endYawField.setEnabled(b);
		endRollField.setEnabled(b);
	}// GEN-LAST:event_pyrCheckBoxItemStateChanged

	private void posCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_posCheckBoxItemStateChanged
		boolean b = posCheckBox.isSelected();
		startXField.setEnabled(b);
		startZField.setEnabled(b);
		startYField.setEnabled(b);
		endXField.setEnabled(b);
		endZField.setEnabled(b);
		endYField.setEnabled(b);
	}// GEN-LAST:event_posCheckBoxItemStateChanged

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel centerPanel;
	private javax.swing.JLabel endPYRLabel;
	private cohdemoeditor.swing.JDoubleTextField endPitchField;
	private javax.swing.JLabel endPointLabel;
	private cohdemoeditor.swing.JDoubleTextField endRollField;
	private cohdemoeditor.swing.JDoubleTextField endXField;
	private cohdemoeditor.swing.JDoubleTextField endYField;
	private cohdemoeditor.swing.JDoubleTextField endYawField;
	private cohdemoeditor.swing.JDoubleTextField endZField;
	private javax.swing.JLabel northLabel;
	private javax.swing.JLabel numStepsLabel;
	private javax.swing.JSpinner numStepsSpinner;
	private javax.swing.JLabel pitchLabel;
	private javax.swing.JCheckBox posCheckBox;
	private javax.swing.JPanel posPanel;
	private javax.swing.JCheckBox pyrCheckBox;
	private javax.swing.JPanel pyrPanel;
	private javax.swing.JTextField refField;
	private javax.swing.JLabel refLabel;
	private javax.swing.JLabel rollLabel;
	private javax.swing.JLabel startPYRLabel;
	private cohdemoeditor.swing.JDoubleTextField startPitchField;
	private javax.swing.JLabel startPointLabel;
	private cohdemoeditor.swing.JDoubleTextField startRollField;
	private javax.swing.JLabel startTimeLabel;
	private javax.swing.JSpinner startTimeSpinner;
	private cohdemoeditor.swing.JDoubleTextField startXField;
	private cohdemoeditor.swing.JDoubleTextField startYField;
	private cohdemoeditor.swing.JDoubleTextField startYawField;
	private cohdemoeditor.swing.JDoubleTextField startZField;
	private javax.swing.JPanel timeAndRefPanel;
	private javax.swing.JLabel timePerStepLabel;
	private javax.swing.JSpinner timePerStepSpinner;
	private javax.swing.JLabel xLabel;
	private javax.swing.JLabel yLabel;
	private javax.swing.JLabel yawLabel;
	private javax.swing.JLabel zLabel;
	// End of variables declaration//GEN-END:variables

}
