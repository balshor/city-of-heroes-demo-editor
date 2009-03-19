/*
 * AutoPYRWizard.java
 *
 * Created on July 9, 2005, 11:16 AM
 */

package cohdemoeditor.wizards;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.DemoReference;
import cohdemoeditor.FilterList;
import cohdemoeditor.swing.JCenteringDialog;
import cohdemoeditor.swing.JDoubleTextField;

/**
 * This wizard automatically generates PYR commands so a character is either (1)
 * always facing direction of travel or (2) always facing a specific other
 * character. Fixed offsets can be added to the computed angles.
 * 
 * @author Darren Lee
 */
public class AutoPYRWizard extends DemoWizardDialog {

	private static final int NUM_STEPS = 3;

	private DemoCommandListChooserPanel dclcp;
	private DemoReferenceChooserPanel drcp1, drcp2;
	private JPanel posSelectionPanel, pyrSelfPanel, pyrTargetPanel,
			pyrSelectionPanel;
	private JCheckBox restrictTimeBox, camBox, delPYRBox;
	private JRadioButton pGenButton, pFixButton, yGenButton, yFixButton,
			rGenButton, rFixButton;
	private JSpinner startTimeSpinner, endTimeSpinner;
	private JDoubleTextField pSelfOffset, ySelfOffset, rSelfOffset,
			pTargetOffset, yTargetOffset, rTargetOffset, XTargetOffset,
			ZTargetOffset, YTargetOffset;
	private JComboBox pyrTypeComboBox;

	private DemoCommandList target;
	private DemoReference targetRef;

	private FilterList originalFilters = null;
	private DemoCommandListFilter baseFilter, posFilter, pyrFilter;

	private static final String SELFPANEL = "Orient according to movement path";
	private static final String TARGETPANEL = "Orient on another reference";
	private static final String[] PANELNAMES = { SELFPANEL, TARGETPANEL };

	/** Creates a new instance of AutoPYRWizard */
	@SuppressWarnings("serial")
	public AutoPYRWizard() {
		dclcp = new DemoCommandListChooserPanel();
		dclcp.setInstructionText("Please select the target demo.");

		posSelectionPanel = new JPanel();
		posSelectionPanel.setLayout(new BorderLayout(0, 0));
		drcp1 = new DemoReferenceChooserPanel();
		posSelectionPanel.add(drcp1, BorderLayout.CENTER);
		JPanel timePanel = new JPanel();
		timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.X_AXIS));
		startTimeSpinner = new JSpinner();
		startTimeSpinner.setEnabled(false);
		endTimeSpinner = new JSpinner();
		endTimeSpinner.setEnabled(false);
		restrictTimeBox = new JCheckBox(new AbstractAction("Restrict times") {
			public void actionPerformed(java.awt.event.ActionEvent ae) {
				startTimeSpinner.setEnabled(restrictTimeBox.isSelected());
				endTimeSpinner.setEnabled(restrictTimeBox.isSelected());
			}
		});
		timePanel.add(restrictTimeBox);
		timePanel.add(startTimeSpinner);
		timePanel.add(new JLabel(" - "));
		timePanel.add(endTimeSpinner);
		posSelectionPanel.add(timePanel, BorderLayout.SOUTH);

		ButtonGroup bg;
		JPanel panel;
		Box box;

		pyrSelfPanel = new JPanel();
		pyrSelfPanel.setLayout(new BoxLayout(pyrSelfPanel, BoxLayout.Y_AXIS));

		box = Box.createHorizontalBox();
		pGenButton = new JRadioButton("generate pitch");
		pFixButton = new JRadioButton("fixed pitch");
		pFixButton.setSelected(true);
		bg = new ButtonGroup();
		bg.add(pGenButton);
		bg.add(pFixButton);
		box.add(pGenButton);
		box.add(pFixButton);
		final JLabel pLabel = new JLabel("Pitch value:");
		pLabel.setHorizontalAlignment(SwingConstants.LEFT);
		pFixButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
						if (pFixButton.isSelected()) {
							pLabel.setText("Pitch value:");
						} else {
							pLabel.setText("Pitch offset:");
						}
			}
		});
		pSelfOffset = new JDoubleTextField();
		pSelfOffset.setText("0");
		pSelfOffset.setMaximumSize(new Dimension(Integer.MAX_VALUE, pSelfOffset
				.getPreferredSize().height));
		pyrSelfPanel.add(box);
		pyrSelfPanel.add(pLabel);
		pyrSelfPanel.add(pSelfOffset);

		box = Box.createHorizontalBox();
		yGenButton = new JRadioButton("generate yaw");
		yFixButton = new JRadioButton("fixed yaw");
		yGenButton.setSelected(true);
		bg = new ButtonGroup();
		bg.add(yGenButton);
		bg.add(yFixButton);
		box.add(yGenButton);
		box.add(yFixButton);
		final JLabel yLabel = new JLabel("Yaw offset:");
		yLabel.setHorizontalAlignment(SwingConstants.LEFT);
		yFixButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (yFixButton.isSelected()) {
							yLabel.setText("Yaw value:");
						} else {
							yLabel.setText("Yaw offset:");
						}
					}
				});
			}
		});
		ySelfOffset = new JDoubleTextField();
		ySelfOffset.setText("0");
		ySelfOffset.setMaximumSize(new Dimension(Integer.MAX_VALUE, ySelfOffset
				.getPreferredSize().height));
		pyrSelfPanel.add(box);
		pyrSelfPanel.add(yLabel);
		pyrSelfPanel.add(ySelfOffset);

		box = Box.createHorizontalBox();
		rGenButton = new JRadioButton("generate rotation");
		rFixButton = new JRadioButton("fixed rotation");
		rFixButton.setSelected(true);
		bg = new ButtonGroup();
		bg.add(rGenButton);
		bg.add(rFixButton);
		box.add(rGenButton);
		box.add(rFixButton);
		final JLabel rLabel = new JLabel("Rotation value:");
		rLabel.setHorizontalAlignment(SwingConstants.LEFT);
		rFixButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (rFixButton.isSelected()) {
							rLabel.setText("Rotation value:");
						} else {
							rLabel.setText("Rotation offset:");
						}
					}
				});
			}
		});
		rSelfOffset = new JDoubleTextField();
		rSelfOffset.setText("0");
		rSelfOffset.setMaximumSize(new Dimension(Integer.MAX_VALUE, rSelfOffset
				.getPreferredSize().height));
		pyrSelfPanel.add(box);
		pyrSelfPanel.add(rLabel);
		pyrSelfPanel.add(rSelfOffset);

		pyrSelfPanel.add(Box.createVerticalGlue());

		pyrTargetPanel = new JPanel();
		pyrTargetPanel.setLayout(new BorderLayout(0, 0));
		drcp2 = new DemoReferenceChooserPanel();
		pyrTargetPanel.add(drcp2, BorderLayout.CENTER);

		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		panel = new JPanel();
		panel.setLayout(new GridLayout(6, 2));
		panel.add(new JLabel("Pitch offset"));
		pTargetOffset = new JDoubleTextField();
		pTargetOffset.setText("0");
		panel.add(pTargetOffset);
		panel.add(new JLabel("Yaw offset"));
		yTargetOffset = new JDoubleTextField();
		yTargetOffset.setText("0");
		panel.add(yTargetOffset);
		panel.add(new JLabel("Rotation offset"));
		rTargetOffset = new JDoubleTextField();
		rTargetOffset.setText("0");
		panel.add(rTargetOffset);
		panel.add(new JLabel("target X offset"));
		XTargetOffset = new JDoubleTextField();
		XTargetOffset.setText("0");
		panel.add(XTargetOffset);
		panel.add(new JLabel("target Z offset"));
		ZTargetOffset = new JDoubleTextField();
		ZTargetOffset.setText("0");
		panel.add(ZTargetOffset);
		panel.add(new JLabel("target Y offset"));
		YTargetOffset = new JDoubleTextField();
		YTargetOffset.setText("0");
		panel.add(YTargetOffset);
		panel2.add(panel);
		JButton heightButton = new JButton(
				"Calculate Z offset from target height.");
		heightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DemoReference dref = drcp2.getSelection();
				if (dref == null) {
					showErrorMessage("No Target Reference Selected.");
					return;
				}
				FilterList filters = target.getFilterList();
				FilterList backupFilters = filters.clone();
				filters.clear();
				DemoCommandListFilter filter = new DemoCommandListFilter();
				filter.addReference(dref.getReferenceNumber());
				filter.addCommand("COSTUME");
				filters.add(filter);
				if (target.getVisibleCommandCount() == 0) {
					showErrorMessage("Cannot find COSTUME command for reference number "
							+ dref.getReferenceNumber() + ".");
				} else {
					if (target.getVisibleCommandCount() > 1) {
						final JDialog dialog = new JCenteringDialog(
								AutoPYRWizard.this.dialog, true);
						dialog.setLayout(new BorderLayout(0, 0));
						final DemoCommandChooserPanel dccp = new DemoCommandChooserPanel();
						dccp.setDemoCommandList(target);
						dccp
								.setInstructionText("Please select the COSTUME command from which to get the target's height.");
						dialog.add(dccp, BorderLayout.CENTER);
						JPanel panel = new JPanel();
						panel.setLayout(new FlowLayout());
						final JButton okayButton = new JButton("Okay");
						final JButton cancelButton = new JButton("Cancel");
						ActionListener al = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								if (okayButton.equals(e.getSource())) {
									DemoCommand cmd = dccp.getSelection();
									if (cmd.getArgumentCount() < 3) {
										showErrorMessage("The selected COSTUME command does not have a height argument.");
									} else {
										try {
											double d = Double.parseDouble(cmd
													.getArgument(2));
											ZTargetOffset.setText(Double
													.toString(6 + d / 12));
										} catch (NumberFormatException nfe) {
											showErrorMessage("Could not parse the height argument of that COSTUME command.");
										}
									}
								}
								dialog.setVisible(false);
							}
						};
						panel.add(okayButton);
						okayButton.addActionListener(al);
						panel.add(cancelButton);
						cancelButton.addActionListener(al);
						dialog.add(panel, BorderLayout.SOUTH);
						dialog.pack();
						dialog.setVisible(true);
					} else { // should be exactly one command visible
						DemoCommand cmd = target.getVisibleCommand(0);
						if (cmd.getArgumentCount() < 3) {
							showErrorMessage("The reference's COSTUME command does not have a height argument.");
						} else {
							try {
								double d = Double.parseDouble(cmd
										.getArgument(2));
								ZTargetOffset.setText(Double
										.toString(6 + d / 12));
							} catch (NumberFormatException nfe) {
								showErrorMessage("Could not parse the height argument of that COSTUME command.");
							}
						}
					}
				}
				filters.clear();
				filters.add(backupFilters);
			}
		});
		panel2.add(heightButton);
		pyrTargetPanel.add(panel2, BorderLayout.EAST);

		final JPanel cardPanel = new JPanel();
		final CardLayout cl = new CardLayout();
		cardPanel.setLayout(cl);
		cardPanel.add(pyrSelfPanel, SELFPANEL);
		cardPanel.add(pyrTargetPanel, TARGETPANEL);

		JPanel checkPanel = new JPanel();
		checkPanel.setLayout(new BoxLayout(checkPanel, BoxLayout.Y_AXIS));
		camBox = new JCheckBox("Use CAM coordinates for yaw");
		delPYRBox = new JCheckBox("Delete existing PYR commands");
		delPYRBox.setSelected(true);
		checkPanel.add(camBox);
		checkPanel.add(delPYRBox);

		pyrSelectionPanel = new JPanel();
		pyrSelectionPanel.setLayout(new BorderLayout(0, 0));
		pyrTypeComboBox = new JComboBox(PANELNAMES);
		pyrTypeComboBox.setEditable(false);
		pyrTypeComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				cl.show(cardPanel, (String) pyrTypeComboBox.getSelectedItem());
				dialog.pack();
			}
		});
		pyrSelectionPanel.add(pyrTypeComboBox, BorderLayout.NORTH);
		pyrSelectionPanel.add(cardPanel, BorderLayout.CENTER);
		pyrSelectionPanel.add(checkPanel, BorderLayout.SOUTH);

		posFilter = new DemoCommandListFilter();
		posFilter.addCommand("POS");
		pyrFilter = new DemoCommandListFilter();
		pyrFilter.addCommand("PYR");

	}

	/**
	 * Step 1 is selecting a demo. Step 2 is selecting the reference. Step 3 is
	 * either selecting the movement path or the target to track and entering
	 * the offsets (if any)
	 */
	protected Component getComponentForStep(int stepNum) {
		if (stepNum == 0) {
			dclcp.refreshDemoEditor();
			return dclcp;
		}
		if (stepNum == 1) {
			drcp1.setDemoReferenceList(target.getDemoReferenceList());
			drcp1
					.setInstructionText("Please select the reference whose PYR commands you would like to generate.");
			return posSelectionPanel;
		}
		if (stepNum == 2) {
			drcp2.setDemoReferenceList(target.getDemoReferenceList());
			drcp2
					.setInstructionText("Please select the target reference to track.");
			camBox
					.setSelected(targetRef.getReferenceNumber() == DemoCommand.CAM_INDEX);
			return pyrSelectionPanel;
		}
		return null;
	}

	/**
	 * Does the work of each step. In the first two steps, just verifies input.
	 * In the last step, calculates the appropriate PYR commands.
	 */
	protected boolean validateCurrentStep() {
		final int stepNum = getCurrentStep();
		if (stepNum == 0) {
			target = dclcp.getSelection();
			if (target == null) {
				showErrorMessage("Please select a demo.");
				return false;
			}
			originalFilters = target.getFilterList().clone();
			return true;
		}
		if (stepNum == 1) {
			targetRef = drcp1.getSelection();
			if (targetRef == null) {
				showErrorMessage("Please make a selection.");
				return false;
			}
			// Filter the commands to show only POS commands for the given
			// reference number and time range.
			baseFilter = new DemoCommandListFilter();
			if (restrictTimeBox.isSelected()) {
				try {
					startTimeSpinner.commitEdit();
					endTimeSpinner.commitEdit();
				} catch (java.text.ParseException pe) {
					showErrorMessage("Could not parse start and end times.");
					return false;
				}
				baseFilter.addTimeRange((Integer) startTimeSpinner.getValue(),
						(Integer) endTimeSpinner.getValue());
			}
			baseFilter.addReference(targetRef.getReferenceNumber());
			FilterList filters = target.getFilterList();
			filters.clear();
			filters.add(baseFilter);
			filters.add(posFilter);
			return true;
		}
		if (stepNum == 2) {
			FilterList filters = target.getFilterList();
			if (delPYRBox.isSelected()) {
				// delete all PYR commands for the reference in the time range
				filters.clear();
				filters.add(baseFilter);
				filters.add(pyrFilter);
				target.removeVisibleCommands();
				filters.clear();
				filters.add(baseFilter);
				filters.add(posFilter);
			}
			double pitch, yaw, rotation;
			if (SELFPANEL.equals(pyrTypeComboBox.getSelectedItem())) {
				// generate commands based on movement path
				if (target.getVisibleCommandCount() < 2) {
					showErrorMessage("The reference "
							+ targetRef.getReferenceNumber()
							+ " must have at least two POS commands between the given times in order to orient based on movement.");
					filters.clear();
					return false;
				}
				try {
					pitch = Double.parseDouble(pSelfOffset.getText());
					yaw = Double.parseDouble(ySelfOffset.getText());
					rotation = Double.parseDouble(rSelfOffset.getText());
				} catch (NumberFormatException nfe) {
					showErrorMessage("Unable to read the pitch/yaw/rotation fields.");
					return false;
				}
				try {
					doSelfRePYRing(pitch, yaw, rotation);
				} catch (NumberFormatException nfe) {
					showErrorMessage("Malformed POS command -- stopping PYR generation.");
					return false;
				}
			} else {
				// generate commands based on target location
				try {
					pitch = Double.parseDouble(pTargetOffset.getText());
					yaw = Double.parseDouble(yTargetOffset.getText());
					rotation = Double.parseDouble(rTargetOffset.getText());
				} catch (NumberFormatException nfe) {
					showErrorMessage("Unable to read the pitch/yaw/rotation fields.");
					return false;
				}
				DemoReference ref = drcp2.getSelection();
				if (ref == null) {
					showErrorMessage("Please select the reference to track.");
					return false;
				}
				if (ref.getReferenceNumber() == targetRef.getReferenceNumber()) {
					showErrorMessage("Source reference is the same as the target reference!");
					return false;
				}
				filters.clear();
				DemoCommandListFilter filter = new DemoCommandListFilter();
				filter.addReference(ref.getReferenceNumber());
				filters.add(filter);
				filters.add(posFilter);
				if (target.getVisibleCommandCount() == 0) {
					showErrorMessage("The reference to track must contain at least one POS command.");
					return false;
				}
				try {
					doTargetRePYRing(target.exportVisible(), pitch, yaw,
							rotation);
				} catch (NumberFormatException nfe) {
					showErrorMessage("Malformed POS command -- stopping PYR generation.");
					return false;
				}
			}
			filters.clear();
			filters.add(originalFilters);
			JOptionPane.showMessageDialog(dialog, "AutoPYRing complete!",
					"Done", JOptionPane.INFORMATION_MESSAGE);
			return true;
		}
		return true;
	}

	/**
	 * This helper method does the actual math to compute the appropriate PYR
	 * commands for tracking a target.
	 * 
	 * @param trackPOS
	 *            a DemoCommandList of the target's POS commands
	 * @param pitch
	 *            the pitch offset
	 * @param yaw
	 *            the yaw offset
	 * @param rotation
	 *            the rotation offset
	 * @throws NumberFormatException
	 */
	private void doTargetRePYRing(DemoCommandList trackPOS, double pitch,
			double yaw, double rotation) throws NumberFormatException {
		FilterList filters = target.getFilterList();
		filters.clear();
		filters.add(baseFilter);
		filters.add(posFilter);
		trackPOS.resort();
		final int refNum = targetRef.getReferenceNumber();
		boolean trackDone = false, targetDone = false;
		DemoCommand currentTrackPOS = trackPOS.getVisibleCommand(0);
		int nextTrackPOSIndex = 0;
		DemoCommand currentPOS = target.getVisibleCommand(0);
		int nextPOSIndex = 0;
		while (true) {
			if (trackDone && targetDone)
				break;
			double xOffset = Double.parseDouble(currentTrackPOS.getArgument(0))
					- Double.parseDouble(currentPOS.getArgument(0))
					+ XTargetOffset.getDouble();
			double zOffset = Double.parseDouble(currentTrackPOS.getArgument(1))
					- Double.parseDouble(currentPOS.getArgument(1))
					+ ZTargetOffset.getDouble();
			double yOffset = Double.parseDouble(currentTrackPOS.getArgument(2))
					- Double.parseDouble(currentPOS.getArgument(2))
					+ YTargetOffset.getDouble();
			final int trackTime = currentTrackPOS.getTime();
			final int targetTime = currentPOS.getTime();
			String args = "";
			final boolean isCam = camBox.isSelected();
			double theta = (Math.atan2(zOffset, Math.sqrt(xOffset * xOffset
					+ yOffset * yOffset)) + pitch);
			if (isCam) {
				theta *= -1;
			}
			args += theta;
			theta = Math.atan2(xOffset, yOffset);
			if (isCam) {
				double newYaw = theta + yaw + Math.PI;
				if (newYaw > Math.PI)
					newYaw -= 2 * Math.PI;
				args += " " + (newYaw);
			} else {
				args += " " + (theta + yaw);
			}
			// no rotation
			args += " 0";
			int time;
			if (trackDone) {
				time = targetTime;
			} else if (targetDone) {
				time = trackTime;
			} else {
				time = Math.min(trackTime, targetTime);
			}
			DemoCommand pyrCommand = new DemoCommand(time, refNum, "PYR", args);
			target.addCommand(pyrCommand);
			if (!trackDone && (targetDone || trackTime <= targetTime)) {
				if (++nextTrackPOSIndex >= trackPOS.getVisibleCommandCount()) {
					trackDone = true;
				} else {
					currentTrackPOS = trackPOS
							.getVisibleCommand(nextTrackPOSIndex);
				}
			}
			if (!targetDone && (trackDone || trackTime >= targetTime)) {
				if (++nextPOSIndex >= target.getVisibleCommandCount()) {
					targetDone = true;
				} else {
					currentPOS = target.getVisibleCommand(nextPOSIndex);
				}
			}
		}
		target.resort();
	}

	/**
	 * This helper command does the actual mathematics for computing PYR
	 * commands for facing in the direction of movement.
	 * 
	 * @param pitch
	 *            the pitch offset
	 * @param yaw
	 *            the yaw offset
	 * @param rotation
	 *            the rotation offset
	 * @throws NumberFormatException
	 */
	private void doSelfRePYRing(double pitch, double yaw, double rotation)
			throws NumberFormatException {
		FilterList filters = target.getFilterList();
		filters.clear();
		filters.add(baseFilter);
		filters.add(posFilter);
		DemoCommand lastPOS = null, currentPOS = null, nextPOS = null;
		final int refNum = targetRef.getReferenceNumber();
		final int numCmds = target.getVisibleCommandCount();
		for (int i = 0; i < numCmds; i++) {
			// set the last, current, and next POS command
			if (i + 1 < target.getVisibleCommandCount()) {
				nextPOS = target.getVisibleCommand(i + 1);
			} else {
				nextPOS = null;
			}
			currentPOS = target.getVisibleCommand(i);
			if (i - 1 > 0) {
				lastPOS = target.getVisibleCommand(i - 1);
			} else {
				lastPOS = null;
			}
			String args = "";
			double xOffset, yOffset, zOffset;
			if (nextPOS != null) {
				xOffset = Double.parseDouble(nextPOS.getArgument(0))
						- Double.parseDouble(currentPOS.getArgument(0));
				zOffset = Double.parseDouble(nextPOS.getArgument(1))
						- Double.parseDouble(currentPOS.getArgument(1));
				yOffset = Double.parseDouble(nextPOS.getArgument(2))
						- Double.parseDouble(currentPOS.getArgument(2));
			} else {
				xOffset = Double.parseDouble(currentPOS.getArgument(0))
						- Double.parseDouble(lastPOS.getArgument(0));
				zOffset = Double.parseDouble(currentPOS.getArgument(1))
						- Double.parseDouble(lastPOS.getArgument(1));
				yOffset = Double.parseDouble(currentPOS.getArgument(2))
						- Double.parseDouble(lastPOS.getArgument(2));
			}
			if (pGenButton.isSelected()) {
				double theta = (Math.atan2(zOffset, Math.sqrt(xOffset * xOffset
						+ yOffset * yOffset)) + pitch);
				if (camBox.isSelected())
					theta *= -1;
				args += theta;
			} else {
				args += pitch;
			}
			double theta = Math.atan2(xOffset, yOffset);
			if (yGenButton.isSelected()) {
				if (camBox.isSelected()) {
					double newYaw = theta + yaw + Math.PI;
					if (newYaw > Math.PI)
						newYaw -= 2 * Math.PI;
					args += " " + (newYaw);
				} else {
					args += " " + (theta + yaw);
				}
			} else {
				if (camBox.isSelected()) {
					double newYaw = yaw + Math.PI;
					if (newYaw > Math.PI)
						newYaw -= 2 * Math.PI;
					args += " " + newYaw;
				} else {
					args += " " + yaw;
				}
			}
			if (rGenButton.isSelected() && lastPOS != null && nextPOS != null) {
				double lastX = Double.parseDouble(currentPOS.getArgument(0))
						- Double.parseDouble(lastPOS.getArgument(0));
				double lastZ = Double.parseDouble(currentPOS.getArgument(1))
						- Double.parseDouble(lastPOS.getArgument(1));
				double lastY = Double.parseDouble(currentPOS.getArgument(2))
						- Double.parseDouble(lastPOS.getArgument(2));
				double newY = Math.cos(theta) * lastY - Math.sin(theta) * lastX;
				double mag = Math.sqrt(newY * newY + lastZ * lastZ);
				double newRotation = Math.asin(lastZ / mag);
				newRotation += rotation;
				args += " " + newRotation;
			} else {
				args += " " + rotation;
			}
			DemoCommand pyrCmd = new DemoCommand(currentPOS.getTime(), refNum,
					"PYR", args);
			target.addCommandAfter(i, pyrCmd);
		}
	}

	/**
	 * Resets the UI to initial values.
	 */
	protected void resetWizard() {
		if (restrictTimeBox.isSelected()) {
			restrictTimeBox.doClick();
		}
		originalFilters = null;
		startTimeSpinner.setValue(0);
		endTimeSpinner.setValue(0);
		pFixButton.setSelected(true);
		yGenButton.setSelected(true);
		rFixButton.setSelected(true);
		pSelfOffset.setText("0");
		ySelfOffset.setText("0");
		rSelfOffset.setText("0");
		pTargetOffset.setText("0");
		yTargetOffset.setText("0");
		rTargetOffset.setText("0");
		XTargetOffset.setText("0");
		ZTargetOffset.setText("0");
		YTargetOffset.setText("0");
		camBox.setSelected(false);
		delPYRBox.setSelected(true);
	}

	/**
	 * Resets the filter lists of the target demo if the user cancels out of the wizard.
	 */
	protected void cancelWizard() {
		if (originalFilters != null && target != null) {
			target.getFilterList().clear();
			target.getFilterList().add(originalFilters);
		}
	}

	/**
	 * Returns the name of this wizard.
	 */
	public String getName() {
		return "Generate PYR relative to POS";
	}

	/**
	 * There are 3 steps.
	 */
	public int getNumberOfSteps() {
		return NUM_STEPS;
	}

	/**
	 * Returns a brief description of this wizard.
	 */
	public String getDescription() {
		return "This wizard will automatically create a PYR command for each POS command for a given reference.\nYou can use it to make a character or camera face the direction of travel or to track another reference.";
	}

}