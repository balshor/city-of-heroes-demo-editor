/*
 * CAMCoordinateWizard.java
 *
 * Created on June 18, 2005, 1:19 AM
 */

package cohdemoeditor.wizards;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.swing.DemoCommandListEditor;

/**
 * This wizard switches the PYR coordinates of the CAM object between the usual
 * backwards coordinates and the standard ones used by all other objects.
 * 
 * @author Darren Lee
 */
public class CAMCoordinateWizard extends DemoWizardDialog {

	private static final int NUM_STEPS = 1;

	private JPanel panel;
	private JList list;

	/** Creates a new instance of CAMCoordinateWizard */
	public CAMCoordinateWizard() {
		panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
		labelPanel
				.add(new JLabel(
						"The wizard converts all CAM PYR commands to standard coordinates and back."));
		labelPanel.add(new JLabel("Please select the target demo:"));
		panel.add(labelPanel, BorderLayout.NORTH);
		JButton loadButton = new JButton("Load Demo");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (getDemoEditor() != null) {
					getDemoEditor().loadDemo();
					dialog.toFront();
					list.setSelectedIndex(getDemoEditor().getSelectedIndex());
				}
			}
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(loadButton);
		panel.add(buttonPanel, BorderLayout.EAST);
		list = new JList();
		list.setPreferredSize(new Dimension(150, 150));
		panel.add(list, BorderLayout.CENTER);
	}

	/**
	 * Returns the <code>Component</code> that should be displayed for the given
	 * step, or <code>null</code> if no <code>Component</code> has been
	 * specified.
	 * 
	 * @param stepNum
	 *            the number of the step to get the component for
	 * @return the <code>Component</code> to display, or <code>null</code> if
	 *         none is specified
	 */
	protected Component getComponentForStep(int stepNum) {
		if (getCurrentStep() == 0) {
			list.setModel(getDemoEditor().getListModel());
			list.setSelectedIndex(getDemoEditor().getSelectedIndex());
			return panel;
		}
		return null;
	}

	/**
	 * Checks the input from the current step and updates the internal state of
	 * the wizard in preparation for continuing to the next step.
	 * 
	 * @return <code>true</code> if the wizard is ready to proceed to the next
	 *         step, <code>false</code> otherwise
	 */
	protected boolean validateCurrentStep() {
		if (getDemoEditor() == null || list.getSelectedIndex() < 0)
			return false;

		DemoCommandListEditor dcle = getDemoEditor().getDemoCommandListEditor(
				list.getSelectedIndex());
		DemoCommandList dcl = dcle.getDemoCommandList();

		DemoCommandListFilter filter = new DemoCommandListFilter(
				DemoCommandListFilter.SHOW_THESE);
		filter.addReference(DemoCommand.CAM_INDEX);
		filter.addCommand("PYR");

		int numCmds = dcl.getCommandCount();
		for (int i = 0; i < numCmds; i++) {
			DemoCommand cmd = dcl.getCommand(i);
			if (filter.isVisible(cmd)) {
				if (cmd.getArgumentCount() != 3) {
					showErrorMessage("The CAM PYR command \"" + cmd
							+ "\" should have 3 arguments, but has "
							+ cmd.getArgumentCount());
					return false;
				}
				String pArg = cmd.getArgument(0);
				try {
					double d = Double.valueOf(pArg);
					d *= -1;
					String newArg = Double.toString(d);
					cmd.setArgument(0, newArg);
				} catch (NumberFormatException nfe) {
					showErrorMessage("The CAM PYR command \"" + cmd
							+ "\" has a malformed pitch argument " + pArg);
					return false;
				}
				String arg = cmd.getArgument(1);
				try {
					double d = Double.valueOf(arg);
					d += Math.PI;
					if (d > Math.PI)
						d -= 2 * Math.PI;
					String newArg = truncate(d);
					cmd.setArgument(1, newArg);
				} catch (NumberFormatException nfe) {
					showErrorMessage("The CAM PYR command \"" + cmd
							+ "\" has a malformed yaw argument " + arg);
					return false;
				}
				dcl.setDirty(true);
			}
		}
		return true;
	}

	/**
	 * Resets the wizard's internal state.
	 */
	protected void resetWizard() {
		super.resetWizard();
	}

	/**
	 * Returns the number of steps in this wizard.
	 * 
	 * @return the number of steps in this wizard
	 */
	protected int getNumberOfSteps() {
		return NUM_STEPS;
	}

	/**
	 * Returns the name of the wizard.
	 * 
	 * @return the wizard's name
	 */
	public String getName() {
		return "CAM Coordinate Wizard";
	}

	/**
	 * Returns a brief description of this wizard.
	 * 
	 * @return a brief description of this wizard
	 */
	public String getDescription() {
		return "This wizard converts the CAM yaw coordinates either to or from the regular coordinate system.\nBecause this conversion is self-inverting, you can use this wizard to convert in either direction.";
	}

}
