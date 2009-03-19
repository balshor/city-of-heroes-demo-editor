/*
 * AutoPOSWizard.java
 *
 * Created on July 20, 2005, 2:52 PM
 */

package cohdemoeditor.wizards;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import edu.hws.jcm.data.*;

/**
 * The AutoPOSWizard allows the user to create POS and PYR methods
 * parametrically. It uses the edu.hws.jcm.data package written by David Eck to
 * parse the formulas.
 * 
 * @author Darren Lee
 */
public class AutoPOSWizard extends DemoWizardDialog {

	private static final int NUM_STEPS = 1;

	private static final String POS_CMD = "POS";
	private static final String PYR_CMD = "PYR";

	private Parser parser;
	private Variable nVar, tVar;

	private JPanel mainPanel;
	private JSpinner startTimeSpinner, numStepsSpinner, endTimeSpinner,
			stepLengthSpinner, refSpinner;
	private JTextField xField, zField, yField, PField, YField, RField;
	private JCheckBox posBox, pyrBox;

	/** Creates a new instance of AutoPOSWizard */
	public AutoPOSWizard() {
		parser = new Parser();
		nVar = new Variable("n");
		tVar = new Variable("t");
		parser.add(nVar);
		parser.add(tVar);

		JPanel panel;

		/**
		 * This ChangeListener synchronizes the various spinners to keep the
		 * start time, end time, number of steps, and step length consistent.
		 */
		ChangeListener cl = new ChangeListener() {
			private boolean changing = false;

			public void stateChanged(ChangeEvent ce) {
				if (changing)
					return;
				changing = true;
				Object source = ce.getSource();
				if (source == null) {
					return;
				}
				int start = (Integer) startTimeSpinner.getValue();
				int end = (Integer) endTimeSpinner.getValue();
				int numSteps = (Integer) numStepsSpinner.getValue();
				int stepLength = (Integer) stepLengthSpinner.getValue();
				if (end - start != (numSteps - 1) * stepLength) {
					if (source.equals(startTimeSpinner)) {
						endTimeSpinner.setValue(start + (numSteps - 1)
								* stepLength);
					} else if (source.equals(endTimeSpinner)) {
						numSteps = Math.max((end - start) / stepLength + 1, 1);
						numStepsSpinner.setValue(numSteps);
						endTimeSpinner.setValue(start + (numSteps - 1)
								* stepLength);
					} else if (source.equals(numStepsSpinner)) {
						endTimeSpinner.setValue(start + (numSteps - 1)
								* stepLength);
					} else if (source.equals(stepLengthSpinner)) {
						endTimeSpinner.setValue(start + (numSteps - 1)
								* stepLength);
						((SpinnerNumberModel) endTimeSpinner.getModel())
								.setStepSize(stepLength);
					}
				}
				changing = false;
			}
		};

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		// the time panel allows the user to set the start time, end time,
		// number of steps, and time per step
		JPanel timePanel = new JPanel();
		timePanel.setLayout(new GridLayout(4, 2));
		timePanel.add(new JLabel("Start Time"));
		startTimeSpinner = new JSpinner(new SpinnerNumberModel(1,
				Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		startTimeSpinner.addChangeListener(cl);
		timePanel.add(startTimeSpinner);
		timePanel.add(new JLabel("End Time"));
		endTimeSpinner = new JSpinner(new SpinnerNumberModel(1,
				Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		endTimeSpinner.addChangeListener(cl);
		timePanel.add(endTimeSpinner);
		timePanel.add(new JLabel("Number of Steps"));
		numStepsSpinner = new JSpinner(new SpinnerNumberModel(1, 1,
				Integer.MAX_VALUE, 1));
		numStepsSpinner.addChangeListener(cl);
		timePanel.add(numStepsSpinner);
		timePanel.add(new JLabel("Time per Step"));
		stepLengthSpinner = new JSpinner(new SpinnerNumberModel(1, 1,
				Integer.MAX_VALUE, 1));
		stepLengthSpinner.addChangeListener(cl);
		timePanel.add(stepLengthSpinner);
		mainPanel.add(timePanel);

		// the reference panel allows the user to select which reference to
		// generate commands for
		JPanel refPanel = new JPanel();
		refPanel.setLayout(new GridLayout(1, 2));
		refPanel.add(new JLabel("Reference Number (CAM=-1)"));
		refSpinner = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE,
				Integer.MAX_VALUE, 1));
		refPanel.add(refSpinner);
		mainPanel.add(refPanel);

		// the POS panel allows the user to enter formulas for POS commands
		JPanel posPanel = new JPanel();
		posPanel.setLayout(new BorderLayout());
		posBox = new JCheckBox("Generate POS commands", true);
		posBox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean isSelected = posBox.isSelected();
				xField.setEnabled(isSelected);
				zField.setEnabled(isSelected);
				yField.setEnabled(isSelected);
			}
		});
		posPanel.add(posBox, BorderLayout.NORTH);
		panel = new JPanel();
		panel.setLayout(new GridLayout(3, 2));
		panel.add(new JLabel("x (East to West)"));
		xField = new JTextField();
		panel.add(xField);
		panel.add(new JLabel("z (Down to Up)"));
		zField = new JTextField();
		panel.add(zField);
		panel.add(new JLabel("y (North to South)"));
		yField = new JTextField();
		panel.add(yField);
		posPanel.add(panel, BorderLayout.CENTER);
		mainPanel.add(posPanel);

		// the PYR panel allows the user to enter formulas for PYR commands
		JPanel pyrPanel = new JPanel();
		pyrPanel.setLayout(new BorderLayout());
		pyrBox = new JCheckBox("Generate PYR commands", true);
		pyrBox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean isSelected = pyrBox.isSelected();
				PField.setEnabled(isSelected);
				YField.setEnabled(isSelected);
				RField.setEnabled(isSelected);
			}
		});
		pyrPanel.add(pyrBox, BorderLayout.NORTH);
		panel = new JPanel();
		panel.setLayout(new GridLayout(3, 2));
		panel.add(new JLabel("pitch"));
		PField = new JTextField();
		panel.add(PField);
		panel.add(new JLabel("yaw"));
		YField = new JTextField();
		panel.add(YField);
		panel.add(new JLabel("roll"));
		RField = new JTextField();
		panel.add(RField);
		pyrPanel.add(panel, BorderLayout.CENTER);
		mainPanel.add(pyrPanel);

		// the var panel simply has two labels that define the n and t variables
		JPanel varPanel = new JPanel();
		varPanel.setLayout(new BorderLayout());
		varPanel.add(new JLabel("n = step number, beginning at 0",
				SwingConstants.CENTER), BorderLayout.CENTER);
		varPanel.add(new JLabel("t = time", SwingConstants.CENTER),
				BorderLayout.SOUTH);
		mainPanel.add(varPanel);

	}

	/**
	 * There is only one step in this wizard, so we always return the mainPanel.
	 */
	protected Component getComponentForStep(int stepnum) {
		return mainPanel;
	}

	/**
	 * This method does the work. Grabs all the parameters from the UI, parses
	 * the formulas, then computes the DemoCommands and places them in a new
	 * DemoCommandList.
	 */
	protected boolean validateCurrentStep() {
		final boolean doPOS = posBox.isSelected();
		final boolean doPYR = pyrBox.isSelected();
		if (!doPOS && !doPYR) {
			showErrorMessage("Please select POS or PYR commands to generate.");
		}

		// parse the formulas
		ExpressionProgram xExp = null, zExp = null, yExp = null, PExp = null, YExp = null, RExp = null;
		if (doPOS) {
			try {
				xExp = parser.parse(xField.getText());
			} catch (ParseError pe) {
				showErrorMessage("Could not parse x-expression");
				return false;
			}
			try {
				zExp = parser.parse(zField.getText());
			} catch (ParseError pe) {
				showErrorMessage("Could not parse z-expression");
				return false;
			}
			try {
				yExp = parser.parse(yField.getText());
			} catch (ParseError pe) {
				showErrorMessage("Could not parse y-expression");
				return false;
			}
		}
		if (doPYR) {
			try {
				PExp = parser.parse(PField.getText());
			} catch (ParseError pe) {
				showErrorMessage("Could not parse pitch field");
				return false;
			}
			try {
				YExp = parser.parse(YField.getText());
			} catch (ParseError pe) {
				showErrorMessage("Could not parse yaw field");
				return false;
			}
			try {
				RExp = parser.parse(RField.getText());
			} catch (ParseError pe) {
				showErrorMessage("Could not parse roll field");
				return false;
			}
		}

		// get the time and reference parameters
		int numSteps = (Integer) numStepsSpinner.getValue();
		int time = (Integer) startTimeSpinner.getValue();
		int ref = (Integer) refSpinner.getValue();
		int stepLength = (Integer) stepLengthSpinner.getValue();

		// generate commands
		DemoCommandList dcl = new DemoCommandList();
		DemoCommand posCmd, pyrCmd;
		for (int i = 0; i < numSteps; i++) {
			nVar.setVal(i);
			tVar.setVal(time);
			if (doPOS) {
				final StringBuilder sb = new StringBuilder();
				sb.append(truncate(xExp.getVal())).append(" ").append(truncate(zExp.getVal())).append(" ").append(truncate(yExp.getVal()));
				posCmd = new DemoCommand(time, ref, POS_CMD, sb.toString());
				dcl.addCommand(posCmd);
			}
			if (doPYR) {
				final StringBuilder sb = new StringBuilder();
				sb.append(truncate(PExp.getVal())).append(" ").append(truncate(YExp.getVal())).append(" ").append(truncate(RExp.getVal()));
				pyrCmd = new DemoCommand(time, ref, PYR_CMD, sb.toString());
				dcl.addCommand(pyrCmd);
			}
			time += stepLength;
		}
		getDemoEditor().addDemo(dcl);
		return true;
	}

	/**
	 * Resets the wizard for reuse.
	 */
	protected void resetWizard() {
		super.resetWizard();
		startTimeSpinner.setValue(1);
		endTimeSpinner.setValue(1);
		numStepsSpinner.setValue(1);
		stepLengthSpinner.setValue(1);
		xField.setText("");
		zField.setText("");
		yField.setText("");
		PField.setText("");
		YField.setText("");
		RField.setText("");
		posBox.setSelected(true);
		pyrBox.setSelected(true);
	}

	/**
	 * This wizard has one step.
	 */
	protected int getNumberOfSteps() {
		return NUM_STEPS;
	}

	/**
	 * Returns the name displayed to the user for this wizard.
	 */
	public String getName() {
		return "Generate POS/PYR by forumla";
	}

	/**
	 * Returns a quick description of the wizard for the user.
	 */
	public String getDescription() {
		return "This wizard will automatically generate POS and PYR commands according to user-defined formulae.  The commands will be output into a new demo.";
	}
}