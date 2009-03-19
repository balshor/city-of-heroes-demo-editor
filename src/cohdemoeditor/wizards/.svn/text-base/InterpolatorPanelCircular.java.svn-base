/*
 * InterpolatorPanelCircular.java
 *
 * Created on July 27, 2007, 11:02 PM
 */

package cohdemoeditor.wizards;

import java.awt.*;
import javax.swing.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;

/**
 * This panel provides the user interface and logic necessary to perform
 * circular interpolation.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class InterpolatorPanelCircular extends javax.swing.JPanel {

	private Dialog parent;

	/** Creates new form InterpolatorPanelCircular */
	public InterpolatorPanelCircular(Dialog parent) {
		this.parent = parent;
		initComponents();
	}

	/**
	 * Generates the actual circular interpolation commands
	 * 
	 * @return a DemoCommandList of the interpolated commands
	 */
	public DemoCommandList generateCommands() {
		final int startTime, timePerStep, numSteps;
		double startX = 0, startZ = 0, startY = 0, centerX = 0, centerZ = 0, centerY = 0, revolutions = 0;
		boolean isVertical = verticalOrientationButton.isSelected();
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
							"Cannot generate POS commands without start and center points.",
							"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		startX = startXField.getDouble();
		startZ = startZField.getDouble();
		startY = startYField.getDouble();
		centerX = centerXField.getDouble();
		centerZ = centerZField.getDouble();
		centerY = centerYField.getDouble();

		if (startX == centerX && startY == centerY) {
			JOptionPane
					.showMessageDialog(
							parent,
							"This wizard cannot create circular paths starting directly above or below the center.\nWe recommend that you either use parametric interpolation or start somewhere else on the circle, then delete any unwanted commands.",
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

		revolutions = revolutionsField.getDouble();
		if (revolutions == 0) {
			JOptionPane.showMessageDialog(parent,
					"Please enter a non-zero number of revolutions.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}

		DemoCommandList outputList = new DemoCommandList();
		DemoCommand dcmd;
		String str = "";
		int time = startTime;
		double x, z, y;
		double stepSize = 2 * Math.PI * revolutions / numSteps;
		final double radius = Math.sqrt(square(startX - centerX)
				+ square(startZ - centerZ) + square(startY - centerY));
		final double hRadius = Math.sqrt(square(startX - centerX)
				+ square(startY - centerY));
		final double cosPhi = (startX - centerX) / hRadius;
		final double sinPhi = (startY - centerY) / hRadius;

		if (!isVertical) {
			if (clockwiseButton.isSelected()) {
				stepSize *= -1;
			}
			for (double i = 0; i <= numSteps; i++) {
				double r1 = hRadius * Math.cos(i * stepSize);
				double r2 = radius * Math.sin(i * stepSize);

				x = r1 * cosPhi - r2 * sinPhi;
				z = (startZ - centerZ) * Math.cos(i * stepSize);
				y = r1 * sinPhi + r2 * cosPhi;

				str = "" + truncate(x) + " " + truncate(z) + " " + truncate(y);

				dcmd = new DemoCommand(time, refNumber, "POS", str);
				outputList.addCommand(dcmd);
				time += timePerStep;
			}
		} else {
			if (downButton.isSelected()) {
				stepSize *= -1;
			}
			final double cosTheta = hRadius / radius;
			final double sinTheta = (startZ - centerZ) / radius;
			for (double i = 0; i <= numSteps; i++) {
				double a1 = radius * Math.cos(i * stepSize);
				double a2 = radius * Math.sin(i * stepSize);
				double horizontal = a1 * cosTheta - a2 * sinTheta;

				x = horizontal * cosPhi;
				z = a1 * sinTheta + a2 * cosTheta;
				y = horizontal * sinPhi;

				str = "" + truncate(x) + " " + truncate(z) + " " + truncate(y);

				dcmd = new DemoCommand(time, refNumber, "POS", str);
				outputList.addCommand(dcmd);
				time += timePerStep;
			}
		}

		return outputList;
	}

	/**
	 * Convenience method to square a double
	 * 
	 * @param d
	 * @return
	 */
	private double square(double d) {
		return d * d;
	}

	/**
	 * Convenience method to truncate a double to six decimal places
	 * 
	 * @param d
	 * @return
	 */
	private double truncate(double d) {
		return Math.floor(d * 1000000) / 1000000;
	}

	/**
	 * Convenience method to determine if a text field is empty
	 * 
	 * @param field
	 * @return
	 */
	private boolean isEmpty(JTextField field) {
		return field.getText().equals("");
	}

	/**
	 * Convenience method to determine if all text fields have input
	 * 
	 * @return
	 */
	private boolean hasInput() {
		return !(isEmpty(startXField) || isEmpty(centerXField)
				|| isEmpty(startZField) || isEmpty(centerZField)
				|| isEmpty(startYField) || isEmpty(centerYField));
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		orientationButtonGroup = new javax.swing.ButtonGroup();
		horizontalButtonGroup = new javax.swing.ButtonGroup();
		verticalButtonGroup = new javax.swing.ButtonGroup();
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
		revolutionsLabel = new javax.swing.JLabel();
		revolutionsField = new cohdemoeditor.swing.JDoubleTextField();
		posPanel = new javax.swing.JPanel();
		spacerPanel = new javax.swing.JPanel();
		xLabel = new javax.swing.JLabel();
		zLabel = new javax.swing.JLabel();
		yLabel = new javax.swing.JLabel();
		startPointLabel = new javax.swing.JLabel();
		startXField = new cohdemoeditor.swing.JDoubleTextField();
		startZField = new cohdemoeditor.swing.JDoubleTextField();
		startYField = new cohdemoeditor.swing.JDoubleTextField();
		centerLabel = new javax.swing.JLabel();
		centerXField = new cohdemoeditor.swing.JDoubleTextField();
		centerZField = new cohdemoeditor.swing.JDoubleTextField();
		centerYField = new cohdemoeditor.swing.JDoubleTextField();
		southPanel = new javax.swing.JPanel();
		orientationPanel = new javax.swing.JPanel();
		orientationLabel = new javax.swing.JLabel();
		horizontalOrientationButton = new javax.swing.JRadioButton();
		verticalOrientationButton = new javax.swing.JRadioButton();
		directionPanel = new javax.swing.JPanel();
		horizontalDirectionPanel = new javax.swing.JPanel();
		horizontalDirectionLabel = new javax.swing.JLabel();
		clockwiseButton = new javax.swing.JRadioButton();
		counterClockwiseButton = new javax.swing.JRadioButton();
		verticalDirectionPanel = new javax.swing.JPanel();
		verticalDirectionLabel = new javax.swing.JLabel();
		upButton = new javax.swing.JRadioButton();
		downButton = new javax.swing.JRadioButton();

		setLayout(new java.awt.BorderLayout());

		northLabel.setFont(new java.awt.Font("Tahoma", 1, 14));
		northLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		northLabel.setText("Circular Interpolation");
		northLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
		add(northLabel, java.awt.BorderLayout.NORTH);

		centerPanel.setLayout(new javax.swing.BoxLayout(centerPanel,
				javax.swing.BoxLayout.Y_AXIS));

		centerPanel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
		timeAndRefPanel.setLayout(new java.awt.GridLayout(5, 2, 0, 5));

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

		revolutionsLabel.setText("Number of Revolutions");
		revolutionsLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(0, 5, 0, 5)));
		timeAndRefPanel.add(revolutionsLabel);

		revolutionsField.setText("1");
		timeAndRefPanel.add(revolutionsField);

		centerPanel.add(timeAndRefPanel);

		posPanel.setLayout(new java.awt.GridLayout(3, 4, 0, 5));

		posPanel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
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
		startPointLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(0, 5, 0, 5)));
		posPanel.add(startPointLabel);

		startXField.setText("0");
		posPanel.add(startXField);

		startZField.setText("0");
		posPanel.add(startZField);

		startYField.setText("0");
		posPanel.add(startYField);

		centerLabel.setText("Center POS");
		centerLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(0, 5, 0, 5)));
		posPanel.add(centerLabel);

		centerXField.setText("0");
		posPanel.add(centerXField);

		centerZField.setText("0");
		posPanel.add(centerZField);

		centerYField.setText("0");
		posPanel.add(centerYField);

		centerPanel.add(posPanel);

		southPanel.setLayout(new java.awt.GridLayout(1, 0));

		orientationPanel.setLayout(new java.awt.GridLayout(3, 0));

		orientationPanel.setBorder(new javax.swing.border.CompoundBorder(
				new javax.swing.border.EmptyBorder(new java.awt.Insets(4, 4, 4,
						4)), new javax.swing.border.LineBorder(
						new java.awt.Color(0, 0, 0))));
		orientationLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		orientationLabel.setText("Orientation of Circle");
		orientationLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
		orientationPanel.add(orientationLabel);

		orientationButtonGroup.add(horizontalOrientationButton);
		horizontalOrientationButton.setSelected(true);
		horizontalOrientationButton.setText("Horizontal");
		horizontalOrientationButton
				.addItemListener(new java.awt.event.ItemListener() {
					public void itemStateChanged(java.awt.event.ItemEvent evt) {
						horizontalOrientationButtonItemStateChanged(evt);
					}
				});

		orientationPanel.add(horizontalOrientationButton);

		orientationButtonGroup.add(verticalOrientationButton);
		verticalOrientationButton.setText("Vertical");
		orientationPanel.add(verticalOrientationButton);

		southPanel.add(orientationPanel);

		directionPanel.setLayout(new java.awt.CardLayout());

		directionPanel.setBorder(new javax.swing.border.CompoundBorder(
				new javax.swing.border.EmptyBorder(new java.awt.Insets(4, 4, 4,
						4)), new javax.swing.border.LineBorder(
						new java.awt.Color(0, 0, 0))));
		horizontalDirectionPanel.setLayout(new java.awt.GridLayout(3, 0));

		horizontalDirectionLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		horizontalDirectionLabel.setText("Direction of Motion");
		horizontalDirectionLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
		horizontalDirectionPanel.add(horizontalDirectionLabel);

		horizontalButtonGroup.add(clockwiseButton);
		clockwiseButton.setSelected(true);
		clockwiseButton.setText("Clockwise");
		horizontalDirectionPanel.add(clockwiseButton);

		horizontalButtonGroup.add(counterClockwiseButton);
		counterClockwiseButton.setText("Counter-Clockwise");
		horizontalDirectionPanel.add(counterClockwiseButton);

		directionPanel.add(horizontalDirectionPanel, "horizontalPanel");

		verticalDirectionPanel.setLayout(new java.awt.GridLayout(3, 0));

		verticalDirectionLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		verticalDirectionLabel.setText("Direction of Motion");
		verticalDirectionLabel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
		verticalDirectionPanel.add(verticalDirectionLabel);

		verticalButtonGroup.add(upButton);
		upButton.setSelected(true);
		upButton.setText("Up");
		verticalDirectionPanel.add(upButton);

		verticalButtonGroup.add(downButton);
		downButton.setText("Down");
		verticalDirectionPanel.add(downButton);

		directionPanel.add(verticalDirectionPanel, "verticalPanel");

		southPanel.add(directionPanel);

		centerPanel.add(southPanel);

		add(centerPanel, java.awt.BorderLayout.CENTER);

	}

	// </editor-fold>//GEN-END:initComponents

	private void horizontalOrientationButtonItemStateChanged(
			java.awt.event.ItemEvent evt) {// GEN-FIRST:
		// event_horizontalOrientationButtonItemStateChanged
		CardLayout cl = (CardLayout) directionPanel.getLayout();
		if (horizontalOrientationButton.isSelected()) {
			cl.show(directionPanel, "horizontalPanel");
		} else {
			cl.show(directionPanel, "verticalPanel");
		}
	}// GEN-LAST:event_horizontalOrientationButtonItemStateChanged

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JLabel centerLabel;
	private javax.swing.JPanel centerPanel;
	private cohdemoeditor.swing.JDoubleTextField centerXField;
	private cohdemoeditor.swing.JDoubleTextField centerYField;
	private cohdemoeditor.swing.JDoubleTextField centerZField;
	private javax.swing.JRadioButton clockwiseButton;
	private javax.swing.JRadioButton counterClockwiseButton;
	private javax.swing.JPanel directionPanel;
	private javax.swing.JRadioButton downButton;
	private javax.swing.ButtonGroup horizontalButtonGroup;
	private javax.swing.JLabel horizontalDirectionLabel;
	private javax.swing.JPanel horizontalDirectionPanel;
	private javax.swing.JRadioButton horizontalOrientationButton;
	private javax.swing.JLabel northLabel;
	private javax.swing.JLabel numStepsLabel;
	private javax.swing.JSpinner numStepsSpinner;
	private javax.swing.ButtonGroup orientationButtonGroup;
	private javax.swing.JLabel orientationLabel;
	private javax.swing.JPanel orientationPanel;
	private javax.swing.JPanel posPanel;
	private javax.swing.JTextField refField;
	private javax.swing.JLabel refLabel;
	private cohdemoeditor.swing.JDoubleTextField revolutionsField;
	private javax.swing.JLabel revolutionsLabel;
	private javax.swing.JPanel southPanel;
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
	private javax.swing.JRadioButton upButton;
	private javax.swing.ButtonGroup verticalButtonGroup;
	private javax.swing.JLabel verticalDirectionLabel;
	private javax.swing.JPanel verticalDirectionPanel;
	private javax.swing.JRadioButton verticalOrientationButton;
	private javax.swing.JLabel xLabel;
	private javax.swing.JLabel yLabel;
	private javax.swing.JLabel zLabel;
	// End of variables declaration//GEN-END:variables

}
