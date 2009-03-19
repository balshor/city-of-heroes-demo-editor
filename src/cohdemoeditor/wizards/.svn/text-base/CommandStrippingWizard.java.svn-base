/*
 * CommandStrippingWizard.java
 *
 * Created on June 18, 2005, 3:25 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package cohdemoeditor.wizards;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.FilterList;

/**
 * A CommandStrippingWizard can remove various types of information from a demo.
 * These types include chat, combat spam, floating text and numbers, damage
 * information, and character names.
 * 
 * @author Darren Lee
 */
public class CommandStrippingWizard extends DemoWizardDialog {

	private JPanel demoPanel, typePanel;
	private JList demoList;
	private JCheckBox chatBox, combatspamBox, floatBox, dmgBox, nameBox;

	private DemoCommandList dcl;
	private static final int NUM_STEPS = 2;

	/** Creates a new instance of CommandStrippingWizard */
	public CommandStrippingWizard() {
		demoPanel = new JPanel();
		demoPanel.setLayout(new BorderLayout(0, 0));
		demoList = new JList();
		JPanel panel = new JPanel();
		JButton loadButton = new JButton("Load Demo");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getDemoEditor() != null) {
					getDemoEditor().loadDemo();
					dialog.toFront();
					demoList.setSelectedIndex(getDemoEditor()
							.getSelectedIndex());
				}
			}
		});
		panel.add(loadButton);
		demoPanel.add(panel, BorderLayout.EAST);
		demoPanel.add(demoList, BorderLayout.CENTER);

		typePanel = new JPanel();
		chatBox = new JCheckBox("All Chat Messages");
		chatBox.setSelected(true);
		combatspamBox = new JCheckBox("Combat Chat Messages");
		combatspamBox.setSelected(true);
		floatBox = new JCheckBox("Floating Warnings");
		floatBox.setSelected(true);
		dmgBox = new JCheckBox("Damage and Healing Numbers");
		dmgBox.setSelected(true);
		nameBox = new JCheckBox("Player Names");
		nameBox.setSelected(true);
		typePanel.setLayout(new GridLayout(6, 1));
		typePanel
				.add(new JLabel("Please select the type of commands to strip."));
		typePanel.add(chatBox);
		typePanel.add(combatspamBox);
		typePanel.add(floatBox);
		typePanel.add(dmgBox);
		typePanel.add(nameBox);
	}

	/**
	 * Returns the correct component for each step.
	 */
	protected Component getComponentForStep(int stepNum) {
		if (stepNum == 0) {
			demoList.setModel(getDemoEditor().getListModel());
			demoList.setSelectedIndex(getDemoEditor().getSelectedIndex());
			return demoPanel;
		}
		if (stepNum == 1)
			return typePanel;
		return null;
	}

	/**
	 * Validates the current step. In the second step, it also performs the
	 * removal of commands.
	 */
	protected boolean validateCurrentStep() {
		int currentStep = getCurrentStep();

		if (currentStep == 0) {
			int index = demoList.getSelectedIndex();
			if (index == -1)
				return false;
			dcl = getDemoEditor().getDemoCommandListEditor(index)
					.getDemoCommandList();
			return true;
		}

		if (currentStep == 1) {
			Set<String> commands = new HashSet<String>();
			if (chatBox.isSelected()) {
				commands.add("Chat");
			}
			if (floatBox.isSelected()) {
				commands.add("float");
			}
			if (dmgBox.isSelected()) {
				commands.add("floatdmg");
			}
			if (!commands.isEmpty()) {
				DemoCommandList toRemove = dcl.getDCByCommands(commands);
				dcl.removeCommands(toRemove);
			}
			if (nameBox.isSelected()) {
				FilterList filters = dcl.getFilterList();
				FilterList oldFilters = filters.clone();
				filters.clear();
				DemoCommandListFilter filter = new DemoCommandListFilter();
				filter.addCommand("NEW");
				filters.add(filter);
				dcl.editVisibleArgs("");
				filters.clear();
				filters.add(oldFilters);
			}
			if (combatspamBox.isSelected()) {
				DemoCommandList toRemove = new DemoCommandList();
				final int numCmds = dcl.getCommandCount();
				for (int i = 0; i < numCmds; i++) {
					DemoCommand cmd = dcl.getCommand(i);
					if (cmd.getCommand().equals("Chat")
							&& cmd.getArgumentCount() > 2
							&& cmd.getArgument(1).equals("2")) {
						toRemove.addCommand(cmd);
					}
				}
				dcl.removeCommands(toRemove);
			}
			JOptionPane.showMessageDialog(dialog, "Removal complete!");
			return true;
		}

		assert false : "Step not defined in CommandStrippingWizard.";
		return true;
	}

	/**
	 * Resets the wizard.
	 */
	protected void resetWizard() {
		super.resetWizard();
	}

	/**
	 * Gets the name of this wizard.
	 */
	public String getName() {
		return "Command Stripping Wizard";
	}

	/**
	 * Returns 2, the number of steps in this wizard.
	 */
	protected int getNumberOfSteps() {
		return NUM_STEPS;
	}

	/**
	 * Returns a brief description of this wizard.
	 */
	public String getDescription() {
		return "This wizard can strip all chat messages, floating warnings, floating damage numbers, and/or player names from a demo.";
	}

}
