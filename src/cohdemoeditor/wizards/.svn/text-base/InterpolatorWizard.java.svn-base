/*
 * InterpolatorWizard.java
 *
 * Created on July 27, 2007, 11:56 AM
 */

package cohdemoeditor.wizards;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import cohdemoeditor.DemoCommandList;

/**
 * This wizard provides for path interpolation along a linear, parabolic, or
 * circular path. In a future update, the AutoPOSWizard will be incorporated as
 * a parametric option.
 * 
 * @author Darren Lee
 */
public class InterpolatorWizard extends DemoWizardDialog {

	private JComboBox selectionComboBox;

	private String[] interpolationTypes = { "Linear", "Parabolic", "Circular",
			"Parametric (Advanced)" };
	private int currentInterpolationType;

	private InterpolatorPanelLinear linearPanel;
	private InterpolatorPanelParabolic parabolicPanel;
	private InterpolatorPanelCircular circularPanel;
	private InterpolatorPanelParametric parametricPanel;

	/** Creates a new instance of InterpolatorWizard */
	public InterpolatorWizard() {
		super();
		selectionComboBox = new JComboBox(interpolationTypes);
		selectionComboBox.setBorder(new EmptyBorder(5, 5, 5, 5));

		linearPanel = new InterpolatorPanelLinear(dialog);
		parabolicPanel = new InterpolatorPanelParabolic(dialog);
		circularPanel = new InterpolatorPanelCircular(dialog);
		parametricPanel = new InterpolatorPanelParametric(dialog);
	}

	/**
	 * Returns the name of the wizard.
	 */
	public String getName() {
		return "Interpolation Wizard";
	}

	/**
	 * Returns a brief description of the wizard.
	 */
	public String getDescription() {
		return "This wizard is used to create POS and PYR sequences.";
	}

	/**
	 * Returns 2, the number of steps in this wizard.
	 */
	public int getNumberOfSteps() {
		return 2;
	}

	/**
	 * Returns the appropriate component for each step.
	 */
	public Component getComponentForStep(int stepNum) {
		if (stepNum == 0)
			return selectionComboBox;
		if (stepNum == 1) {
			switch (currentInterpolationType) {
			case 0:
				return linearPanel;
			case 1:
				return parabolicPanel;
			case 2:
				return circularPanel;
			case 3:
				return parametricPanel;
			}
		}
		return null;
	}

	/**
	 * There isn't any validation necessary for this wizard, so this method just
	 * delegates to one of the doXInterpolation methods.
	 */
	public boolean validateCurrentStep() {
		int currentStep = getCurrentStep();
		if (currentStep == 0) {
			currentInterpolationType = selectionComboBox.getSelectedIndex();
			return true;
		}
		if (currentStep == 1) {
			switch (currentInterpolationType) {
			case 0:
				return doLinearInterpolation();
			case 1:
				return doParabolicInterpolation();
			case 2:
				return doCircularInterpolation();
			case 3:
				return doParametricInterpolation();
			}
		}
		JOptionPane.showMessageDialog(dialog,
				"No interpolation type selected.  How did you get here?",
				"Error", JOptionPane.ERROR_MESSAGE);
		return false;
	}

	/**
	 * Performs a linear interpolation by delegating to
	 * InterpolatorPanelLinear.generateCommands();
	 * 
	 * @return true if successful, false otherwise
	 */
	private boolean doLinearInterpolation() {
		DemoCommandList dcl = linearPanel.generateCommands();
		if (dcl == null)
			return false;
		getDemoEditor().addDemo(dcl);
		return true;
	}

	/**
	 * Performs a linear interpolation by delegating to
	 * InterpolatorPanelParabolic.generateCommands();
	 * 
	 * @return true if successful, false otherwise
	 */
	private boolean doParabolicInterpolation() {
		DemoCommandList dcl = parabolicPanel.generateCommands();
		if (dcl == null)
			return false;
		getDemoEditor().addDemo(dcl);
		return true;
	}

	/**
	 * Performs a linear interpolation by delegating to
	 * InterpolatorPanelCircular.generateCommands();
	 * 
	 * @return true if successful, false otherwise
	 */
	private boolean doCircularInterpolation() {
		DemoCommandList dcl = circularPanel.generateCommands();
		if (dcl == null)
			return false;
		getDemoEditor().addDemo(dcl);
		return true;
	}

	/**
	 * Returns true. Parametric Interpolation is not currently implemented in
	 * this wizard -- see the AutoPOSWizard
	 * 
	 * @return true
	 */
	private boolean doParametricInterpolation() {
		return true;
	}

}
