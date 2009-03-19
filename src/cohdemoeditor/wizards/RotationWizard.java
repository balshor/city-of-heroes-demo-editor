package cohdemoeditor.wizards;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.swing.JDoubleTextField;

public class RotationWizard extends DemoWizardDialog {

	private final JPanel mainPanel;
	private final JDoubleTextField centerXField, centerYField, rotationField;
	private final JCheckBox posBox, pyrBox;
	private final DemoCommandListChooserPanel dclcp;
	private DemoCommandList target;

	@SuppressWarnings("serial")
	public RotationWizard() {
		dclcp = new DemoCommandListChooserPanel();
		dclcp.setInstructionText("Please select the target demo.");

		final Action firstAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int size = target.getVisibleCommandCount();
				for(int i = 0; i < size; i++) {
					final DemoCommand cmd = target.getVisibleCommand(i);
					if ("POS".equals(cmd.getCommand()) && cmd.getArgumentCount() == 3) {
						try {
							final double x = Double.parseDouble(cmd.getArgument(0));
							final double y = Double.parseDouble(cmd.getArgument(2));
							centerXField.setText(Double.toString(x));
							centerYField.setText(Double.toString(y));
							return;
						} catch (NumberFormatException nfe) {
							continue;
						}
					}
				}
				showErrorMessage("Could not find a usable POS command.");
			}
		};
		firstAction.putValue(Action.NAME, "Use first POS");

		final Action lastAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int size = target.getVisibleCommandCount();
				for(int i = size-1; i >= 0; i--) {
					final DemoCommand cmd = target.getVisibleCommand(i);
					if ("POS".equals(cmd.getCommand()) && cmd.getArgumentCount() == 3) {
						try {
							final double x = Double.parseDouble(cmd.getArgument(0));
							final double y = Double.parseDouble(cmd.getArgument(2));
							centerXField.setText(Double.toString(x));
							centerYField.setText(Double.toString(y));
							return;
						} catch (NumberFormatException nfe) {
							continue;
						}
					}
				}
				showErrorMessage("Could not find a usable POS command.");
			}
		};
		lastAction.putValue(Action.NAME, "Use last POS");
		
		mainPanel = new JPanel();
		centerXField = new JDoubleTextField();
		centerYField = new JDoubleTextField();
		rotationField = new JDoubleTextField();
		posBox = new JCheckBox("Rotate POS", true);
		pyrBox = new JCheckBox("Rotate PYR", true);
		mainPanel.setLayout(new GridLayout(6,2));
		mainPanel.add(new JLabel("Center coordinates (x,y):"));
		mainPanel.add(new JPanel());
		mainPanel.add(new JLabel("X:"));
		mainPanel.add(centerXField);
		mainPanel.add(new JLabel("Y:"));
		mainPanel.add(centerYField);
		mainPanel.add(new JButton(firstAction));
		mainPanel.add(new JButton(lastAction));
		mainPanel.add(new JLabel("Rotation amount (deg):"));
		mainPanel.add(rotationField);
		mainPanel.add(posBox);
		mainPanel.add(pyrBox);
	}

	@Override
	protected Component getComponentForStep(int stepNum) {
		switch (stepNum) {
		case 0:
			dclcp.refreshDemoEditor();
			return dclcp;
		case 1:
			return mainPanel;
		default:
			return null;
		}
	}

	@Override
	public String getName() {
		return "Rotation Wizard";
	}

	@Override
	protected int getNumberOfSteps() {
		return 2;
	}

	@Override
	protected boolean validateCurrentStep() {
		final int currentStep = getCurrentStep();
		if (currentStep == 0) {
			target = dclcp.getSelection();
			if (target == null) {
				showErrorMessage("Please select a demo.");
				return false;
			}
			return true;
		} else if (currentStep == 1) {
			final boolean pos = posBox.isSelected();
			final boolean pyr = pyrBox.isSelected();
			if (!pos && !pyr) {
				showErrorMessage("Nothing to rotate!");
				return false;
			}
			final double x = centerXField.getDouble();
			final double y = centerYField.getDouble();
			final double rot = rotationField.getDouble();

			doRotation(x, y, rot, pos, pyr);
			return true;
		}
		throw new RuntimeException(
				"This wizard only has 2 steps.  There is no step with index "
						+ currentStep);
	}

	/**
	 * This method performs the transformation on the POS commands.
	 * 
	 * @param x
	 * @param y
	 * @param rot
	 */

	// TODO handle non-origin center of rotation
	private void doRotation(final double x, final double y, final double rot,
			final boolean pos, final boolean pyr) {
		final int numCmds = target.getVisibleCommandCount();
		final double R = rot * Math.PI / 180;
		final double cosR = Math.cos(R);
		final double sinR = Math.sin(R);
		for (int i = 0; i < numCmds; i++) {
			final DemoCommand cmd = target.getVisibleCommand(i);
			if (pos && cmd.getCommand().equals("POS")) {
				if (cmd.getArgumentCount() == 3) {
					final double cmdX, cmdY;
					try {
						cmdX = Double.parseDouble(cmd.getArgument(0));
						cmdY = Double.parseDouble(cmd.getArgument(2));
					} catch (NumberFormatException nfe) {
						continue;
					}
					cmd.setArgument(0, truncate((cmdX - x) * cosR + (cmdY - y)
							* sinR + x));
					cmd.setArgument(2, truncate((cmdY - y) * cosR - (cmdX - x)
							* sinR + y));
				}
			}
			if (pyr && cmd.getCommand().equals("PYR")) {
				if (cmd.getArgumentCount() == 3) {
					double yaw;
					try {
						yaw = Double.parseDouble(cmd.getArgument(1));
					} catch (NumberFormatException nfe) {
						continue;
					}
					yaw += R;
					while (yaw < 0) {
						yaw += 2 * Math.PI;
					}
					cmd.setArgument(1, truncate(yaw));
				}
			}
		}
	}

	@Override
	public String getDescription() {
		return "This wizard will automatically rotate all visible POS and/or PYR commands by a specified amount.  It only handles horizontal rotations.";
	}

	@Override
	public void resetWizard() {
		super.resetWizard();
		posBox.setSelected(true);
		pyrBox.setSelected(true);
		centerXField.setText("0");
		centerYField.setText("0");
		rotationField.setText("0");
	}

}
