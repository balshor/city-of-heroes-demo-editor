/*
 * CostumeImportExportWizard.java
 *
 * Created on June 28, 2005, 6:04 PM
 */

package cohdemoeditor.wizards;

import java.awt.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.DemoReference;
import cohdemoeditor.FilterList;

/**
 * This wizard allows the import and export of costumes from one demo to
 * another. A costume is defined as all COSTUME, NPC, and PARTSNAME commands for
 * a specified reference at a specified time.
 * 
 * @author Darren Lee
 */
public class CostumeImportExportWizard extends DemoWizardDialog {

	private static final int NUM_STEPS = 6;

	private DemoCommandListChooserPanel dclcp;
	private DemoReferenceChooserPanel drcp;
	private DemoCommandChooserPanel dccp;

	/** Creates a new instance of CostumeImportExportWizard */
	public CostumeImportExportWizard() {
		super();
		dclcp = new DemoCommandListChooserPanel();
		drcp = new DemoReferenceChooserPanel();
		dccp = new DemoCommandChooserPanel();
	}

	/**
	 * Returns the correct component for each step.
	 * <ul>
	 * <li>Step 0: demo chooser for source</li>
	 * <li>Step 1: reference chooser for source</li>
	 * <li>Step 2: NEW command chooser for source</li>
	 * <li>Step 3: demo chooser for target</li>
	 * <li>Step 4: reference chooser for target</li>
	 * <li>Step 5: NEW command chooser for target</li>
	 * </ul>
	 */
	protected Component getComponentForStep(int stepNum) {
		if (stepNum == 0) {
			dclcp.refreshDemoEditor();
			dclcp.setInstructionText("Please select the source demo.");
			return dclcp;
		}
		if (stepNum == 1) {
			drcp.setDemoReferenceList(sourceList.getDemoReferenceList());
			drcp.setInstructionText("Please select the source reference");
			return drcp;
		}
		if (stepNum == 2) {
			dccp.setDemoCommandList(sourceList);
			dccp.setInstructionText("Please select the source NEW command.");
			return dccp;
		}
		if (stepNum == 3) {
			dclcp.refreshDemoEditor();
			dclcp.setInstructionText("Please select the target demo.");
			return dclcp;
		}
		if (stepNum == 4) {
			drcp.setDemoReferenceList(targetList.getDemoReferenceList());
			drcp.setInstructionText("Please select the target reference");
			return drcp;
		}
		if (stepNum == 5) {
			dccp.setDemoCommandList(targetList);
			dccp.setInstructionText("Please select the target NEW command.");
			return dccp;
		}
		return null;
	}

	private DemoCommandList sourceList;
	private FilterList sourceFilters;
	private DemoReference sourceRef;
	private DemoCommand sourceCmd;
	private DemoCommandList targetList;
	private FilterList targetFilters;
	private DemoReference targetRef;
	private DemoCommand targetCmd;

	/**
	 * Validates the selection on each step and performs the actual copy by
	 * calling doCostumeImport.
	 */
	protected boolean validateCurrentStep() {
		int stepNum = getCurrentStep();
		if (stepNum == 0) {
			sourceList = dclcp.getSelection();
			if (sourceList == null) {
				showErrorMessage("Please make a selection.");
				return false;
			}
			sourceFilters = sourceList.getFilterList().clone();
			return true;
		}
		if (stepNum == 1) {
			sourceRef = drcp.getSelection();
			if (sourceRef == null) {
				showErrorMessage("Please make a selection.");
				return false;
			}
			FilterList filters = sourceList.getFilterList();
			filters.clear();
			DemoCommandListFilter filter = new DemoCommandListFilter();
			filter.addCommand("NEW");
			filter.addReference(sourceRef.getReferenceNumber());
			filters.add(filter);
			if (sourceList.getVisibleCommandCount() == 0) {
				showErrorMessage("Could not find any NEW commands for the reference"
						+ targetRef.getReferenceNumber() + ".");
				filters.clear();
				return false;
			}
			return true;
		}
		if (stepNum == 2) {
			sourceCmd = dccp.getSelection();
			if (sourceCmd == null) {
				showErrorMessage("Please make a selection");
				return false;
			}
		}
		if (stepNum == 3) {
			targetList = dclcp.getSelection();
			if (targetList == null)
				return false;
			if (targetList != sourceList)
				targetFilters = targetList.getFilterList().clone();
			return true;
		}
		if (stepNum == 4) {
			targetRef = drcp.getSelection();
			if (targetRef == null) {
				showErrorMessage("Please make a selection.");
				return false;
			}
			FilterList filters = targetList.getFilterList();
			filters.clear();
			DemoCommandListFilter filter = new DemoCommandListFilter();
			filter.addCommand("NEW");
			filter.addReference(targetRef.getReferenceNumber());
			filters.add(filter);
			if (targetList.getVisibleCommandCount() == 0) {
				showErrorMessage("Could not find any NEW commands for the reference"
						+ targetRef.getReferenceNumber() + ".");
				filters.clear();
				return false;
			}
			return true;
		}
		if (stepNum == 5) {
			targetCmd = dccp.getSelection();
			if (targetCmd == null) {
				showErrorMessage("Please make a selection");
				return false;
			}
			doCostumeImport();
		}
		return true;
	}

	/**
	 * Does the actual costume import. Applies filters and copies commands,
	 * editing the time and reference number appropriately.
	 */
	private void doCostumeImport() {
		FilterList sourceFilterList = sourceList.getFilterList();
		FilterList targetFilterList = targetList.getFilterList();

		DemoCommandListFilter filter = new DemoCommandListFilter();
		filter.addTimeRange(sourceCmd.getTime(), sourceCmd.getTime());
		filter.addReference(sourceRef.getReferenceNumber());
		filter.addCommand("COSTUME");
		filter.addCommand("NPC");
		filter.addCommand("PARTSNAME");
		sourceFilterList.clear();
		sourceFilterList.add(filter);

		DemoCommandList costumeCmds = sourceList.exportVisible();
		costumeCmds.editVisibleTimes(targetCmd.getTime());
		costumeCmds.editVisibleRefs(targetRef.getReferenceNumber());

		filter = new DemoCommandListFilter();
		filter.addTimeRange(targetCmd.getTime(), targetCmd.getTime());
		filter.addReference(targetRef.getReferenceNumber());
		filter.addCommand("COSTUME");
		filter.addCommand("NPC");
		filter.addCommand("PARTSNAME");
		targetFilterList.clear();
		targetFilterList.add(filter);
		targetList.removeVisibleCommands();
		targetFilterList.clear();

		int index = targetList.visibleIndexOf(targetCmd) + 1;
		for (int i = costumeCmds.getVisibleCommandCount() - 1; i >= 0; i--) {
			targetList.addCommand(index, costumeCmds.getVisibleCommand(i));
		}

		sourceFilterList.clear();
		sourceFilterList.add(sourceFilters);
		if (targetList != sourceList) {
			targetFilterList.clear();
			targetFilterList.add(targetFilters);
		}
	}

	/**
	 * Resets the wizard.
	 */
	protected void resetWizard() {
		super.resetWizard();
	}

	/**
	 * Gets the name of the wizard for display to the user.
	 */
	public String getName() {
		return "Costume Import/Export Wizard";
	}

	/**
	 * Returns 6, the number of steps.
	 */
	public int getNumberOfSteps() {
		return NUM_STEPS;
	}

	/**
	 * Returns a brief description of the wizard.
	 */
	public String getDescription() {
		return "This wizard will automatically grab the costume information of a character in one demo and transfer it to another character.\nNote that this wizard assumes that there is not more than one NEW command per reference number at any single time.";
	}
}
