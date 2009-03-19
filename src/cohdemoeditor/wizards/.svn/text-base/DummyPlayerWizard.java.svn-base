/*
 * PuddlePlayerWizard.java
 *
 * Created on August 30, 2005, 2:32 AM
 */

package cohdemoeditor.wizards;

import java.awt.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.FilterList;

/**
 * A demo only renders references in a certain radius around the "Player"
 * reference. (The radius is determined by graphics settings.) This wizard
 * creates an invisible dummy player (using NPC Pet_NoCollision), sets it as the
 * player, and has it shadow the camera around.
 * 
 * @author Darren Lee
 */
public class DummyPlayerWizard extends DemoWizardDialog {

	private static final int NUM_STEPS = 1;
	private final DemoCommandListFilter PLAYER_FILTER = new DemoCommandListFilter();
	private final DemoCommandListFilter CAM_POS_FILTER = new DemoCommandListFilter();

	private DemoCommandListChooserPanel dclcp;

	/** Creates a new instance of PuddlePlayerWizard */
	public DummyPlayerWizard() {
		super();
		PLAYER_FILTER.addCommand("Player");
		CAM_POS_FILTER.addReference(DemoCommand.CAM_INDEX);
		CAM_POS_FILTER.addCommand("POS");
		dclcp = new DemoCommandListChooserPanel();
		dclcp.setInstructionText("Please select the target demo.");
	}

	/**
	 * Retrieves the correct component to display at each step.
	 */
	protected Component getComponentForStep(int step) {
		dclcp.refreshDemoEditor();
		return dclcp;
	}

	/**
	 * Validates user input and performs the actual addition of the dummy player. 
	 */
	protected boolean validateCurrentStep() {
		DemoCommandList target = dclcp.getSelection();
		FilterList filters = target.getFilterList();
		FilterList oldFilters = filters.clone();
		filters.clear();
		filters.add(CAM_POS_FILTER);
		if (target.getVisibleCommandCount() == 0) {
			showErrorMessage("Could not find any POS commands for the camera.");
			return false;
		}
		filters.clear();
		filters.add(PLAYER_FILTER);
		target.removeVisibleCommands();
		filters.clear();
		filters.add(CAM_POS_FILTER);
		int ref = target.getDemoReferenceList().getUnusedReferenceNumber();
		int time = target.getVisibleCommand(0).getTime();
		target.addCommand(new DemoCommand(time, ref, "Player", ""));
		target.addCommand(new DemoCommand(time, ref, "NEW", "Dummy_Player"));
		target.addCommand(new DemoCommand(time, ref, "NPC", "Pet_NoCollision"));
		for (int i = 0; i < target.getVisibleCommandCount(); i++) {
			DemoCommand cmd = target.getVisibleCommand(i);
			if (cmd == null || !cmd.getCommand().equals("POS")
					|| cmd.getArgumentCount() != 3)
				continue;
			target.addCommand(new DemoCommand(cmd.getTime(), ref, "POS", cmd
					.getArguments()));
		}
		filters.clear();
		filters.add(oldFilters);
		target.resort();
		return true;
	}

	/**
	 * Returns the name of this wizard.
	 */
	public String getName() {
		return "Dummy Player Wizard";
	}

	/**
	 * Returns a brief description of this wizard.
	 */
	public String getDescription() {
		return "This wizard will create a Pet_NoCollision, label it as the player, and add appropriate POS commands to have it follow the camera around.";
	}

	/**
	 * Returns the number of steps in this wizard.
	 */
	protected int getNumberOfSteps() {
		return NUM_STEPS;
	}

}
