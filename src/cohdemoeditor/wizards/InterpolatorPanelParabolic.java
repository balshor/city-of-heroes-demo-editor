/*
 * InterpolatorPanelParabolic.java
 *
 * Created on July 27, 2007, 10:49 PM
 */

package cohdemoeditor.wizards;

import java.awt.*;
import javax.swing.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;

/**
 * This panel provides the user interface and logic necessary to do a parabolic
 * interpolation.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class InterpolatorPanelParabolic extends javax.swing.JPanel {

	Dialog parent;

	/** Creates new form InterpolatorPanelParabolic */
	public InterpolatorPanelParabolic(Dialog parent) {
		this.parent = parent;
		initComponents();
	}

	/**
	 * Perform the actual parabolic interpolation
	 * 
	 * @return a DemoCommandList with the generated commands
	 */
	public DemoCommandList generateCommands() {
		final int startTime, timePerStep, numSteps;
		double startX = 0, startZ = 0, startY = 0, endX = 0, endZ = 0, endY = 0;
		double height;
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

		if (!hasInput()) {
			JOptionPane
					.showMessageDialog(
							parent,
							"Cannot generate POS commands without apex height and start/end points.",
							"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		startX = startXField.getDouble();
		startZ = startZField.getDouble();
		startY = startYField.getDouble();
		endX = endXField.getDouble();
		endZ = endZField.getDouble();
		endY = endYField.getDouble();
		height = heightField.getDouble();

		if ((height < Math.max(startZ, endZ) && height > Math.min(startZ, endZ))) {
			JOptionPane
					.showMessageDialog(
							parent,
							"Apex height cannot be between the starting and ending heights.",
							"Error", JOptionPane.ERROR_MESSAGE);
			return null;
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
		double x, z, y;
		height = height - startZ;
		double horizontalOffset = Math.sqrt((endX - startX) * (endX - startX)
				+ (endY - startY) * (endY - startY));
		double verticalOffset = endZ - startZ;
		double b = 2 * height / horizontalOffset
				* (1 + Math.sqrt(1 - verticalOffset / height));
		double a = -b * b / (4 * height);
		double xStepSize = (endX - startX) / (numSteps);
		double yStepSize = (endY - startY) / (numSteps);
		double stepLength = Math.sqrt(xStepSize * xStepSize + yStepSize
				* yStepSize);
		double xOffset, lengthOffset;

		StringBuilder builder = new StringBuilder();
		for (double i = 0; i <= numSteps; i++) {
			xOffset = i * xStepSize;
			lengthOffset = i * stepLength;
			x = startX + xOffset;
			y = startY + i * yStepSize;
			z = startZ + a * lengthOffset * lengthOffset + b * lengthOffset;
			str = builder.append(truncate(x)).append(" ").append(truncate(z)).append(" ").append(truncate(y)).toString(); 

			dcmd = new DemoCommand(time, refNumber, "POS", str);
			outputList.addCommand(dcmd);

			time += timePerStep;
			builder.setLength(0);
		}

		return outputList;
	}

	/**
	 * Helper method to determine if a text field is empty
	 * 
	 * @param field
	 * @return
	 */
	private boolean isEmpty(JTextField field) {
		return field.getText().equals("");
	}

	/**
	 * Helper method to truncate a double to six decimal places
	 * 
	 * @param d
	 * @return
	 */
	private double truncate(double d) {
		return Math.floor(d * 1000000) / 1000000;
	}

	/**
	 * Helper method to determine if all fields are non-empty
	 * 
	 * @return
	 */
	private boolean hasInput() {
		return !(isEmpty(startXField) || isEmpty(endXField)
				|| isEmpty(startZField) || isEmpty(endZField)
				|| isEmpty(startYField) || isEmpty(endYField) || isEmpty(heightField));
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
		spacerPanel = new javax.swing.JPanel();
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
		heightLabel = new javax.swing.JLabel();
		heightField = new cohdemoeditor.swing.JDoubleTextField();

		setLayout(new java.awt.BorderLayout());

		northLabel.setFont(new java.awt.Font("Tahoma", 1, 14));
		northLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		northLabel.setText("Parabolic Interpolation");
		northLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5,
				5, 5));
		add(northLabel, java.awt.BorderLayout.NORTH);

		centerPanel.setLayout(new javax.swing.BoxLayout(centerPanel,
				javax.swing.BoxLayout.Y_AXIS));

		timeAndRefPanel.setLayout(new java.awt.GridLayout(4, 2, 0, 5));

		timeAndRefPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(
				5, 5, 5, 5));
		startTimeLabel.setText("Start Time");
		startTimeLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				5, 0, 5));
		timeAndRefPanel.add(startTimeLabel);

		startTimeSpinner.setToolTipText("Enter the time of the first command.");
		timeAndRefPanel.add(startTimeSpinner);

		timePerStepLabel.setText("Time per Step");
		timePerStepLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(
				0, 5, 0, 5));
		timeAndRefPanel.add(timePerStepLabel);

		timePerStepSpinner
				.setToolTipText("Enter the number of ms between commands.");
		timeAndRefPanel.add(timePerStepSpinner);

		numStepsLabel.setText("Number of Steps");
		numStepsLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				5, 0, 5));
		timeAndRefPanel.add(numStepsLabel);

		numStepsSpinner
				.setToolTipText("Enter the number of commands to generate.");
		timeAndRefPanel.add(numStepsSpinner);

		refLabel.setText("Reference");
		refLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0,
				5));
		timeAndRefPanel.add(refLabel);

		refField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		refField.setToolTipText("Enter the reference number to use.");
		timeAndRefPanel.add(refField);

		centerPanel.add(timeAndRefPanel);

		posPanel.setLayout(new java.awt.GridLayout(4, 4, 0, 5));

		posPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5,
				5));
		posPanel.add(spacerPanel);

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
		startPointLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(
				0, 5, 0, 5));
		posPanel.add(startPointLabel);

		posPanel.add(startXField);

		posPanel.add(startZField);

		posPanel.add(startYField);

		endPointLabel.setText("End POS");
		endPointLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				5, 0, 5));
		posPanel.add(endPointLabel);

		posPanel.add(endXField);

		posPanel.add(endZField);

		posPanel.add(endYField);

		heightLabel.setText("Apex Heigh (Max Z)");
		heightLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5,
				0, 5));
		posPanel.add(heightLabel);

		posPanel.add(heightField);

		centerPanel.add(posPanel);

		add(centerPanel, java.awt.BorderLayout.CENTER);

	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel centerPanel;
	private javax.swing.JLabel endPointLabel;
	private cohdemoeditor.swing.JDoubleTextField endXField;
	private cohdemoeditor.swing.JDoubleTextField endYField;
	private cohdemoeditor.swing.JDoubleTextField endZField;
	private cohdemoeditor.swing.JDoubleTextField heightField;
	private javax.swing.JLabel heightLabel;
	private javax.swing.JLabel northLabel;
	private javax.swing.JLabel numStepsLabel;
	private javax.swing.JSpinner numStepsSpinner;
	private javax.swing.JPanel posPanel;
	private javax.swing.JTextField refField;
	private javax.swing.JLabel refLabel;
	private javax.swing.JPanel spacerPanel;
	private javax.swing.JLabel startPointLabel;
	private javax.swing.JLabel startTimeLabel;
	private javax.swing.JSpinner startTimeSpinner;
	private cohdemoeditor.swing.JDoubleTextField startXField;
	private cohdemoeditor.swing.JDoubleTextField startYField;
	private cohdemoeditor.swing.JDoubleTextField startZField;
	private javax.swing.JPanel timeAndRefPanel;
	private javax.swing.JLabel timePerStepLabel;
	private javax.swing.JSpinner timePerStepSpinner;
	private javax.swing.JLabel xLabel;
	private javax.swing.JLabel yLabel;
	private javax.swing.JLabel zLabel;
	// End of variables declaration//GEN-END:variables

}
