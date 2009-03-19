/*
 * FixGhostsWizard.java
 *
 * Created on June 24, 2005, 6:13 PM
 */

package cohdemoeditor.wizards;

import java.awt.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.DemoReference;
import cohdemoeditor.DemoReferenceList;
import cohdemoeditor.FilterList;

/**
 * This wizard removes "ghost" commands that remain after a reference's DEL
 * command.
 * 
 * @author Darren Lee
 */
public class FixGhostsWizard extends DemoWizardDialog {

	private DemoCommandListChooserPanel dclcp;

	/** Creates a new instance of FixGhostsWizard */
	public FixGhostsWizard() {
		super();
		dclcp = new DemoCommandListChooserPanel();
	}

	/**
	 * There is one step, which uses a DemoCommandListChooserPanel
	 */
	protected Component getComponentForStep(int stepNum) {
		if (stepNum == 0) {
			dclcp.refreshDemoEditor();
			return dclcp;
		}
		return null;
	}

	/**
	 * Validates input and performs ghost removal by calling fixGhosts on each
	 * reference number
	 */
	protected boolean validateCurrentStep() {
		DemoCommandList target = dclcp.getSelection();
		if (target == null) {
			showErrorMessage("Please select a demo.");
			return false;
		}
		FilterList currentFilters = target.getFilterList();
		FilterList oldFilters = currentFilters.clone();
		DemoReferenceList drl = target.getDemoReferenceList();
		for (DemoReference ref : drl) {
			int refNum = ref.getReferenceNumber();
			if (refNum > 0) {
				fixGhosts(refNum, target);
			}
		}
		currentFilters.clear();
		currentFilters.add(oldFilters);
		return true;
	}

	/**
	 * Helper method that actually fixes the ghosts.
	 * 
	 * @param refNum
	 * @param target
	 */
	private void fixGhosts(int refNum, DemoCommandList target) {
		FilterList currentFilters = target.getFilterList();
		currentFilters.clear();
		DemoCommandListFilter filter = new DemoCommandListFilter();
		filter.addReference(refNum);
		currentFilters.add(filter);
		int numNEWActive = 0;
		for (int i = 0; i < target.getVisibleCommandCount(); i++) {
			DemoCommand cmd = target.getVisibleCommand(i);
			String cmdString = cmd.getCommand();
			if (cmdString.equals("NEW")) {
				if (numNEWActive > 0) {
					DemoCommand delcmd = new DemoCommand(cmd.getTime(), cmd
							.getReference(), "DEL", null);
					target.addCommand(i, delcmd);
					numNEWActive--;
					continue;
				} else {
					numNEWActive++;
					continue;
				}
			} else if (cmdString.equals("DEL")) {
				if (numNEWActive > 0) {
					numNEWActive--;
					continue;
				} else {
					target.removeCommand(i);
					i--;
				}
			} else if (numNEWActive < 1 && !cmdString.equals("Player")) {
				target.removeCommand(i);
				i--;
			} else {

			}
		}
	}

	/**
	 * We have nothing to reset, so this method does nothing.
	 */
	protected void resetWizard() {
		super.resetWizard();
	}

	/**
	 * Returns 1, the number of steps.
	 */
	protected int getNumberOfSteps() {
		return 1;
	}

	/**
	 * Returns the name of this wizard.
	 */
	public String getName() {
		return "Fix Ghosts Wizard";
	}

	/**
	 * Returns a brief description of this wizard.
	 */
	public String getDescription() {
		return "This wizard will strip out all commands that are not preceeded by a valid NEW command.  It only processes numbered references greater than zero.";
	}

}
