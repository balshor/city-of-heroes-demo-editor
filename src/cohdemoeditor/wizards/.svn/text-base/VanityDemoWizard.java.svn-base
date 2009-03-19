/*
 * VanityDemoWizard.java
 *
 * Created on September 2, 2005, 8:48 PM
 */

package cohdemoeditor.wizards;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.DemoReference;
import cohdemoeditor.FilterList;

/**
 * This wizard pulls the costume data from a specified reference in a specified
 * demo and inserts it into a "Vanity Demo" consisting of the character standing
 * on the Atlas Plaza platform.
 * 
 * @author Darren Lee
 */
public class VanityDemoWizard extends DemoWizardDialog {

	private static final int NUM_STEPS = 2;

	private DemoCommandListChooserPanel dclcp;
	private DemoReferenceChooserPanel drcp;
	private DemoReference dref;
	private DemoCommandList target = null;
	private DemoCommandChooserPanel dccp;
	private JDialog cmdDialog;
	private FilterList oldFilters = null;

	/** Creates a new instance of VanityDemoWizard */
	public VanityDemoWizard() {
		super();

		// create the first panel to chose the demo
		dclcp = new DemoCommandListChooserPanel();
		dclcp
				.setInstructionText("Please select the demo containing the hero to model.");
		drcp = new DemoReferenceChooserPanel();
		dclcp
				.setInstructionText("Please select the reference of the hero to model.");

		// create the second panel to chose the character

		// this dialog will be used in the event that there are multiple NEW
		// lines for the chose character
		cmdDialog = new JDialog(dialog, true);
		cmdDialog.setLayout(new BorderLayout(0, 0));
		dccp = new DemoCommandChooserPanel();
		dccp
				.setInstructionText("Please select the COSTUME or NPC command of the character to model.");
		cmdDialog.add(dccp, BorderLayout.CENTER);
		final JButton okayButton = new JButton("Okay");
		final JButton cancelButton = new JButton("Cancel");
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (okayButton.equals(e.getSource())) {
					DemoCommand cmd = dccp.getSelection();
					if (cmd == null) {
						showErrorMessage("Please select the COSTUME or NPC command to model.");
						return;
					} else {
						int time = cmd.getTime();
						DemoCommandListFilter filter = new DemoCommandListFilter();
						if (cmd.getCommand().equals("COSTUME")) {
							filter.addCommand("COSTUME");
							filter.addCommand("PARTSNAME");
						} else if (cmd.getCommand().equals("NPC")) {
							filter.addCommand("NPC");
						} else {
							showErrorMessage("Please select the COSTUME or NPC command to model.");
							return;
						}
						filter.addTimeRange(time, time);
						filter.addReference(cmd.getReference());
						FilterList filters = target.getFilterList();
						filters.clear();
						filters.add(filter);
						createDemo();
					}
				}
				dialog.setVisible(false);
			}
		};
		okayButton.addActionListener(al);
		cancelButton.addActionListener(al);
		JPanel panel = new JPanel();
		panel.add(okayButton);
		panel.add(cancelButton);
		cmdDialog.add(panel, BorderLayout.SOUTH);
		cmdDialog.pack();
	}

	/**
	 * Returns the appropriate component for each of the two steps.
	 */
	protected Component getComponentForStep(int step) {
		if (step == 0) {
			dclcp.refreshDemoEditor();
			return dclcp;
		} else if (step == 1) {
			drcp.setDemoReferenceList(target.getDemoReferenceList());
			return drcp;
		}
		return null;
	}

	/**
	 * Validates the current step. Checks for a chosen selection in the first
	 * step. In the second step, checks for the chosen reference. If more than
	 * one reference appears, prompts the user for clarification. Computes and
	 * generates the vanity demo.
	 */
	protected boolean validateCurrentStep() {
		int step = getCurrentStep();
		if (step == 0) {
			target = dclcp.getSelection();
			if (target == null) {
				showErrorMessage("Please select the demo containing the hero to model.");
				return false;
			}
		} else if (step == 1) {
			dref = drcp.getSelection();
			if (dref == null) {
				showErrorMessage("Please select the reference of the hero to model.");
				return false;
			}
			FilterList filters = target.getFilterList();
			oldFilters = filters.clone();
			filters.clear();
			DemoCommandListFilter filter = new DemoCommandListFilter();
			filter.addReference(dref.getReferenceNumber());
			filter.addCommand("COSTUME");
			filter.addCommand("NPC");
			filters.add(filter);
			if (target.getVisibleCommandCount() == 0) {
				showErrorMessage("Cannot find COSTUME or NPC command for reference number "
						+ dref.getReferenceNumber() + ".");
				return false;
			} else if (target.getVisibleCommandCount() > 1) {
				dccp.setDemoCommandList(target);
				cmdDialog.setVisible(true);
			} else {
				DemoCommand cmd = target.getVisibleCommand(0);
				if (cmd.getCommand().equals("COSTUME")) {
					filters.clear();
					filter = new DemoCommandListFilter();
					filter.addReference(dref.getReferenceNumber());
					filter.addCommand("COSTUME");
					filter.addCommand("PARTSNAME");
					filters.add(filter);
				}
				createDemo();
			}
		}
		return true;
	}

	private static final int ANGLE_COUNT = 120;
	private static final int NUM_ROTATIONS = 2;
	private static final int ROTATION_TIME = 30000;
	private static final int TIME_OFFSET = ROTATION_TIME / ANGLE_COUNT;
	private static final double RADIUS = 15.0;
	private static final double CENTER_X = 128;
	private static final double CENTER_Z = 18.5;
	private static final double CENTER_Y = -215.5;

	/**
	 * Creates the new vanity demo, adding it to the current demo editor. The
	 * visible commands of the current DemoCommandList will be added as the
	 * character's definition (ie, COSTUME/PARTSNAME or NPC command).
	 */
	private void createDemo() {
		FilterList filters = target.getFilterList();
		int height = 6;
		if (target.getVisibleCommand(0).getCommand().equals("COSTUME")) {
			height += Double.parseDouble(target.getVisibleCommand(0)
					.getArgument(2)) / 12;
		}
		DemoCommandList vanity = new DemoCommandList();
		vanity.addCommand(new DemoCommand(1, 0, "Version", "2"));
		vanity.addCommand(new DemoCommand(1, 0, "Map",
				"maps/City_Zones/City_01_01/City_01_01.txt"));
		vanity.addCommand(new DemoCommand(1, 0, "Time", "12.000000"));
		vanity.addCommand(new DemoCommand(1, 1, "Player", ""));
		final int numVisible = target.getVisibleCommandCount();
		for (int i = 0; i < numVisible; i++) {
			DemoCommand cmd = target.getVisibleCommand(i);
			vanity.addCommand(new DemoCommand(1, 1, cmd.getCommand(), cmd
					.getArguments()));
		}
		vanity.addCommand(new DemoCommand(1, 1, "POS", "" + CENTER_X + " "
				+ CENTER_Z + " " + CENTER_Y));
		vanity.addCommand(new DemoCommand(1, 1, "PYR", "0 0 0"));
		vanity.addCommand(new DemoCommand(1, 1, "MOV", "READY"));
		for (int i = 0; i < NUM_ROTATIONS * ANGLE_COUNT; i++) {
			int time = 1 + i * TIME_OFFSET;
			double theta = 2 * i * Math.PI / ANGLE_COUNT;
			double x = CENTER_X + RADIUS * Math.cos(theta);
			double z = CENTER_Z + height;
			double y = CENTER_Y + RADIUS * Math.sin(theta);
			vanity.addCommand(new DemoCommand(time, DemoCommand.CAM_INDEX,
					"POS", "" + x + " " + z + " " + y));
			double yaw = Math.PI / 2 - theta;
			if (yaw > Math.PI)
				yaw -= 2 * Math.PI;
			vanity.addCommand(new DemoCommand(time, DemoCommand.CAM_INDEX,
					"PYR", "0 " + yaw + " 0"));
		}
		getDemoEditor().addDemo(vanity);
		filters.clear();
		filters.add(oldFilters);
	}

	/**
	 * Resets the wizard to its default settings.
	 */
	protected void resetWizard() {
		super.resetWizard();
		oldFilters = null;
		target = null;
	}

	/**
	 * Returns the name presented to the user for this wizard.
	 */
	public String getName() {
		return "Vanity Demo Creation Wizard";
	}

	/**
	 * Returns a brief description of this wizard.
	 */
	public String getDescription() {
		return "This wizard will create a demo of the desired hero standing at Ms. Liberty's pedestal with the camera rotating around them.";
	}

	/**
	 * Returns 2, the number of steps.
	 */
	protected int getNumberOfSteps() {
		return NUM_STEPS;
	}

}
