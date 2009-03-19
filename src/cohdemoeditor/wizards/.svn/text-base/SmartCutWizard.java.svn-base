/*
 * SmartCutWizard.java
 *
 * Created on June 22, 2005, 3:00 PM
 */

package cohdemoeditor.wizards;

import java.awt.*;

import javax.swing.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.DemoReference;
import cohdemoeditor.DemoReferenceList;
import cohdemoeditor.FilterList;
import cohdemoeditor.swing.ProgressMonitoringSwingWorker;

/**
 * This wizard provides "Smart Cuts" -- ie, it cuts out a period of time from a
 * demo while maintaining consistency between "paired" commands. eg, NEW/DEL,
 * FX/FXDESTROY, etc.
 * 
 * @author Darren Lee
 */
public class SmartCutWizard extends DemoWizardDialog {

	private static final int NUM_STEPS = 2;

	private JSpinner startSpinner, endSpinner;
	private JCheckBox posBox, movBox;
	private JPanel panel;
	private DemoCommandListChooserPanel dclcp;
	private DemoCommandList target;

	/** Creates a new instance of SmartCutWizard */
	public SmartCutWizard() {
		super();

		dclcp = new DemoCommandListChooserPanel();
		dclcp.setInstructionText("Select target demo.");

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JPanel timePanel = new JPanel();
		timePanel.setLayout(new GridLayout(2, 2));
		timePanel.add(new JLabel("Start time"));
		startSpinner = new JSpinner();
		timePanel.add(startSpinner);
		timePanel.add(new JLabel("End time"));
		endSpinner = new JSpinner();
		timePanel.add(endSpinner);
		panel.add(timePanel);
		posBox = new JCheckBox("Keep last POS/PYR");
		panel.add(posBox);
		movBox = new JCheckBox("Keep last MOV");
		panel.add(movBox);
	}

	/**
	 * Returns the appropriate component for each of the two steps. The first
	 * step selects the demo, the second step selects the time range to cut.
	 */
	protected Component getComponentForStep(int stepNum) {
		if (stepNum == 0) {
			dclcp.refreshDemoEditor();
			return dclcp;
		}
		if (stepNum == 1)
			return panel;
		return null;
	}

	/**
	 * Validates each step. After the second step is validated, starts a
	 * SwingWorker thread to perform the actual smart cut.
	 */
	public boolean validateCurrentStep() {
		int stepNum = getCurrentStep();
		if (stepNum == 0) {
			target = dclcp.getSelection();
			if (target == null) {
				showErrorMessage("Please select a demo.");
				return false;
			}
			return true;
		}

		if (stepNum == 1) {
			final int startTime, endTime;
			try {
				startSpinner.commitEdit();
				endSpinner.commitEdit();
				startTime = (Integer) startSpinner.getValue();
				endTime = (Integer) endSpinner.getValue();
			} catch (java.text.ParseException e) {
				showErrorMessage("Could not parse start/end times.");
				return false;
			} catch (ClassCastException e) {
				showErrorMessage("Error reading start/end time spinners.");
				return false;
			}
			if (startTime > endTime) {
				showErrorMessage("The starting time is after the ending time.");
				return false;
			}

			target.eventsEnabledChanged(false);
			final SmartCutWorker worker = new SmartCutWorker(target, startTime,
					endTime, posBox.isSelected(), movBox.isSelected());
			worker.execute();
		}
		return true;
	}

	/**
	 * Given the time that a FXDESTROY command appears, find the previous FX
	 * create command.
	 * 
	 * @param destroyTime
	 *            the time that the FXDESTROY appears
	 * @param refNum
	 *            the reference number
	 * @param FXNum
	 *            the FX number
	 * @return the time of the FX command, or -1 if none is found
	 */
	private static int findFXCreateTime(DemoCommandList target,
			int destroyTime, int refNum, int FXNum) {
		DemoCommand cmd = target.findFXCreate(destroyTime, refNum, FXNum);
		if (cmd == null)
			return -1;
		return cmd.getTime();
	}

	/**
	 * Given the time that a FX command appears, find the next FXDESTROY command
	 * 
	 * @param startTime
	 *            the time that the FX command appears
	 * @param refNum
	 *            the reference number
	 * @param FXNum
	 *            the FX number
	 * @return the time of the FXDESTROY command, or -1 if none is found
	 */
	private static int findFXDestroyTime(DemoCommandList target, int startTime,
			int refNum, int FXNum) {
		DemoCommand cmd = target.findFXDestroy(startTime, refNum, FXNum);
		if (cmd == null)
			return -1;
		return cmd.getTime();
	}

	/**
	 * Given a DEL command, returns the time of the corresponding NEW command,
	 * or -1 if no corresponding NEW is found
	 * 
	 * @param endTime
	 *            the time of the DEL command
	 * @param refNum
	 *            the reference number
	 * @return the time of the NEW command, or -1 if none is found
	 */
	private static int findNewTime(DemoCommandList target, int endTime,
			int refNum) {
		DemoCommand cmd = target.findNEW(endTime, refNum);
		if (cmd == null)
			return -1;
		return cmd.getTime();
	}

	/**
	 * Given a NEW command, returns the time of the corresponding DEL command,
	 * or -1 if no corresponding DEL is found
	 * 
	 * @param startTime
	 *            the time of the NEW command
	 * @param refNum
	 *            the reference number
	 * @return the time of the DEL command, or -1 if none is found
	 */
	private static int findDelTime(DemoCommandList target, int startTime,
			int refNum) {
		DemoCommand cmd = target.findDEL(startTime, refNum);
		if (cmd == null)
			return -1;
		return cmd.getTime();
	}

	/**
	 * Resets the UI to its initial values.
	 */
	public void resetWizard() {
		super.resetWizard();
		startSpinner.setValue(0);
		endSpinner.setValue(0);
		posBox.setSelected(true);
		movBox.setSelected(true);
	}

	/**
	 * Returns the name of the wizard
	 */
	public String getName() {
		return "Smart Cut Wizard";
	}

	/**
	 * Returns the number of steps in this wizard
	 */
	public int getNumberOfSteps() {
		return NUM_STEPS;
	}

	/**
	 * Returns a description of this wizard.
	 */
	public String getDescription() {
		return "This wizard will allow you to easily cut out a block of time from a demo.\nNecessary commands will be kept, unnecessary ones deleted, and times adjusted appropriately.\nNote that you must start with a correctly sorted demo.";
	}

	private class SmartCutWorker extends ProgressMonitoringSwingWorker<Boolean> {

		final int startTime, endTime;
		final DemoCommandListFilter timeFilter;
		final DemoCommandList target;
		final FilterList commandsToDelete;
		final boolean doPOS, doMOV;

		public SmartCutWorker(final DemoCommandList target,
				final int startTime, final int endTime, final boolean doPOS,
				final boolean doMOV) {
			super(getDemoEditor());
			if (target == null)
				throw new IllegalArgumentException(
						"Cannot create a SmartCutWorker with a null target.");
			this.startTime = startTime;
			this.endTime = endTime;
			this.target = target;
			this.doPOS = doPOS;
			this.doMOV = doMOV;
			commandsToDelete = new FilterList();
			timeFilter = new DemoCommandListFilter(
					DemoCommandListFilter.SHOW_THESE);
			timeFilter.addTimeRange(startTime, endTime);
			setTitle("Smart Cut Analysis Progress");
		}

		/**
		 * This method updates the commandsToDelete FilterList so it shows all
		 * commands that need to be removed. As we are on a worker thread, we
		 * operate on the copy of the target. (The target is most likely hooked
		 * up to a bunch of UI components.) In the done() method (which runs on
		 * the event thread), we will perform the actual deletion. Because we
		 * are working on a copy, there are no problems cancelling the smart cut
		 * in this method.
		 */
		@Override
		protected Boolean doInBackground() throws Exception {

			// This is a convenience variable.
			final FilterList filters = target.getFilterList();

			// Back up current filters
			final FilterList oldFilters = filters.clone();

			filters.clear();
			target.refilter();
			commandsToDelete.add(timeFilter);
			filters.add(timeFilter);
			target.refilter();

			/**
			 * This is our estimate of the number of loops we will need to make.
			 * There will be four loops of some number of the visible commands,
			 * one loop per reference to keep POS and MOV commands. We keep the
			 * two values separate so we can easily revise our estimates later.
			 */
			final int initialVisibleCount = target.getVisibleCommandCount();
			int estimatedNumberOfLoops = 0;
			if (doPOS) {
				estimatedNumberOfLoops += target.getVisibleReferenceCount();
			}
			if (doMOV) {
				estimatedNumberOfLoops += target.getVisibleReferenceCount();
			}

			int completedLoops = 0;

			// exclude NEW, COSTUME, NPC, and PARTSNAME when they belong
			// to an object deleted after the timeframe.
			final DemoCommandListFilter newFilter = new DemoCommandListFilter(
					DemoCommandListFilter.SHOW_THESE);
			newFilter.addCommand("NEW");
			filters.add(newFilter);
			target.refilter();
			final int numNEW = target.getVisibleCommandCount();
			estimatedNumberOfLoops += numNEW;
			publish("Analyzing NEW Commands");
			for (int i = 0; i < numNEW; i++) {
				DemoCommand cmd = target.getVisibleCommand(i);
				int timeofdel = findDelTime(target, cmd.getTime(), cmd
						.getReference());
				if (timeofdel == -1 || timeofdel > endTime) {
					DemoCommandListFilter f = new DemoCommandListFilter(
							DemoCommandListFilter.HIDE_THESE);
					f.addTimeRange(cmd.getTime(), cmd.getTime());
					f.addReference(cmd.getReference());
					f.addCommand("NEW");
					f.addCommand("COSTUME");
					f.addCommand("NPC");
					f.addCommand("PARTSNAME");
					commandsToDelete.add(f);
				}
				setProgress((100 * (++completedLoops))
						/ (estimatedNumberOfLoops + 3 * initialVisibleCount));
			}

			// exclude DEL when it belongs to an object created before
			// the
			// timeframe.
			filters.clear();
			filters.add(timeFilter);
			target.refilter();
			final DemoCommandListFilter delFilter = new DemoCommandListFilter(
					DemoCommandListFilter.SHOW_THESE);
			delFilter.addCommand("DEL");
			filters.add(delFilter);
			target.refilter();
			final int numDEL = target.getVisibleCommandCount();
			estimatedNumberOfLoops += numDEL;
			publish("Analyzing DEL Commands");
			for (int i = 0; i < numDEL; i++) {
				DemoCommand cmd = target.getVisibleCommand(i);
				int timeofnew = findNewTime(target, cmd.getTime(), cmd
						.getReference());
				if (timeofnew == -1 || timeofnew < startTime) {
					DemoCommandListFilter f = new DemoCommandListFilter(
							DemoCommandListFilter.HIDE_THESE);
					f.addTimeRange(cmd.getTime(), cmd.getTime());
					f.addReference(cmd.getReference());
					f.addCommand("DEL");
					commandsToDelete.add(f);
				}
				setProgress((100 * (++completedLoops))
						/ (estimatedNumberOfLoops + 2 * initialVisibleCount));
			}

			// exclude FX, FXSCALE, ORIGIN, and TARGET commands when the
			// FXDESTROY comes after the end of the timeframe.
			filters.clear();
			filters.add(timeFilter);
			target.refilter();
			final DemoCommandListFilter fxFilter = new DemoCommandListFilter(
					DemoCommandListFilter.SHOW_THESE);
			fxFilter.addCommand("FX");
			filters.add(fxFilter);
			target.refilter();
			final int numFX = target.getVisibleCommandCount();
			estimatedNumberOfLoops += numFX;
			publish("Analyzing FX Commands");
			for (int i = 0; i < numFX; i++) {
				DemoCommand cmd = target.getVisibleCommand(i);
				if (cmd.getArgumentCount() < 2) {
					showErrorMessage("Missing argument to FX command:\n  "
							+ cmd);
					return false;
				}
				int timeofdel;
				try {
					timeofdel = findFXDestroyTime(target, cmd.getTime(), cmd
							.getReference(), Integer
							.valueOf(cmd.getArgument(1)));
				} catch (NumberFormatException nfe) {
					showErrorMessage("Malformed argument to FX command:\n  "
							+ cmd);
					return false;
				}
				if (timeofdel == -1 || timeofdel > endTime) {
					DemoCommandListFilter f = new DemoCommandListFilter(
							DemoCommandListFilter.HIDE_THESE);
					f.addTimeRange(cmd.getTime(), cmd.getTime());
					f.addReference(cmd.getReference());
					f.addCommand("FX");
					f.addCommand("FXSCALE");
					f.addCommand("ORIGIN");
					f.addCommand("TARGET");
					commandsToDelete.add(f);
				}
				setProgress((100 * (++completedLoops))
						/ (estimatedNumberOfLoops + initialVisibleCount));
			}

			// exclude FXDESTROY when the FX comes before the beginning
			// of the
			// timeframe.
			filters.clear();
			filters.add(timeFilter);
			target.refilter();
			final DemoCommandListFilter fxDestroyFilter = new DemoCommandListFilter(
					DemoCommandListFilter.SHOW_THESE);
			fxDestroyFilter.addCommand("FXDESTROY");
			filters.add(fxDestroyFilter);
			target.refilter();
			final int numFXDestroy = target.getVisibleCommandCount();
			estimatedNumberOfLoops += numFXDestroy;
			publish("Analyzing FXDestroy Commands");
			for (int i = 0; i < numFXDestroy; i++) {
				DemoCommand cmd = target.getVisibleCommand(i);
				int timeofnew;
				try {
					timeofnew = findFXCreateTime(target, cmd.getTime(), cmd
							.getReference(), Integer
							.valueOf(cmd.getArgument(0)));
				} catch (NumberFormatException nfe) {
					showErrorMessage("Malformed argument to FXDESTROY command:\n  "
							+ cmd);
					return false;
				}
				if (timeofnew == -1 || timeofnew < startTime) {
					DemoCommandListFilter f = new DemoCommandListFilter(
							DemoCommandListFilter.HIDE_THESE);
					f.addTimeRange(cmd.getTime(), cmd.getTime());
					f.addReference(cmd.getReference());
					f.addCommand("FXDESTROY");
					commandsToDelete.add(f);
				}
				setProgress((100 * (++completedLoops))
						/ (estimatedNumberOfLoops));
			}

			// do the POS/PYR and MOV keeps, if necessary
			if (doPOS || doMOV) {
				publish("Analyzing POS, PYR, and/or MOV Commands");
				filters.clear();
				filters.add(timeFilter);
				target.refilter();
				final DemoCommandListFilter posFilter = new DemoCommandListFilter();
				posFilter.addCommand("POS");
				final DemoCommandListFilter pyrFilter = new DemoCommandListFilter();
				pyrFilter.addCommand("PYR");
				final DemoCommandListFilter movFilter = new DemoCommandListFilter();
				movFilter.addCommand("MOV");
				final DemoReferenceList visibleRefs = new DemoReferenceList(
						target.getVisibleDemoReferenceList());
				for (DemoReference ref : visibleRefs) {
					DemoCommandListFilter refFilter = new DemoCommandListFilter(
							DemoCommandListFilter.SHOW_THESE);
					refFilter.addReference(ref.getReferenceNumber());
					filters.add(refFilter);

					if (doPOS) {
						/*
						 * exclude the last POS and PYR commands for each
						 * object, unless a DEL command for that object exists
						 * before the end of the timeframe.
						 */
						// keep last POS command
						filters.add(posFilter);
						target.refilter();
						if (target.getVisibleCommandCount() > 0) {
							DemoCommand lastPos = target
									.getVisibleCommand(target
											.getVisibleCommandCount() - 1);
							int time = findDelTime(target, lastPos.getTime(),
									lastPos.getReference());
							if (time > endTime) {
								DemoCommandListFilter toKeep = new DemoCommandListFilter(
										DemoCommandListFilter.HIDE_THESE);
								toKeep.addTimeRange(lastPos.getTime(), lastPos
										.getTime());
								toKeep.addReference(lastPos.getReference());
								toKeep.addCommand("POS");
								commandsToDelete.add(toKeep);
							}
						}
						filters.remove(posFilter);

						// keep last PYR command
						filters.add(pyrFilter);
						target.refilter();
						if (target.getVisibleCommandCount() > 0) {
							DemoCommand lastPyr = target
									.getVisibleCommand(target
											.getVisibleCommandCount() - 1);
							int time = findDelTime(target, lastPyr.getTime(),
									lastPyr.getReference());
							if (time > endTime) {
								DemoCommandListFilter toKeep = new DemoCommandListFilter(
										DemoCommandListFilter.HIDE_THESE);
								toKeep.addTimeRange(lastPyr.getTime(), lastPyr
										.getTime());
								toKeep.addReference(lastPyr.getReference());
								toKeep.addCommand("PYR");
								commandsToDelete.add(toKeep);
							}
						}
						filters.remove(pyrFilter);
						target.refilter();
						setProgress((100 * (++completedLoops))
								/ (estimatedNumberOfLoops));
					}
					if (doMOV) {
						// exclude the last MOV command for each object,
						// unless
						// a DEL command for that object exists before
						// the end
						// of the timeframe.
						filters.add(movFilter);
						target.refilter();
						if (target.getVisibleCommandCount() > 0) {
							DemoCommand lastMov = target
									.getVisibleCommand(target
											.getVisibleCommandCount() - 1);
							int time = findDelTime(target, lastMov.getTime(),
									lastMov.getReference());
							if (time > endTime) {
								DemoCommandListFilter toKeep = new DemoCommandListFilter(
										DemoCommandListFilter.HIDE_THESE);
								toKeep.addTimeRange(lastMov.getTime(), lastMov
										.getTime());
								toKeep.addReference(lastMov.getReference());
								toKeep.addCommand("MOV");
								commandsToDelete.add(toKeep);
							}
						}
						filters.remove(pyrFilter);
						target.refilter();
						setProgress((100 * (++completedLoops))
								/ (estimatedNumberOfLoops));
					}
					filters.remove(refFilter);
				}
			}

			// Delete!
			filters.set(commandsToDelete);
			target.refilter();
			target.removeVisibleCommands();

			// Adjust times of remaining commands
			filters.clear();
			filters.add(timeFilter);
			target.refilter();
			target.editVisibleTimes(startTime);

			// Time shift all subsequent commands
			filters.clear();
			DemoCommandListFilter tailFilter = new DemoCommandListFilter();
			tailFilter.addTimeRange(endTime+1, target.getLastTime());
			filters.add(tailFilter);
			target.refilter();
			target.offSetTimesPermanent(startTime - endTime);

			// Restore original filters
			filters.clear();
			filters.add(oldFilters);
			target.refilter();
			return true;
		}

		/**
		 * On completion of the scan, we do the actual deleting, provided that
		 * the worker completed successfully.
		 */
		protected void done() {
			super.done();
			JOptionPane.showMessageDialog(getDemoEditor(), "Done!");
			target.eventsEnabledChanged(true);
		}
	}
}
