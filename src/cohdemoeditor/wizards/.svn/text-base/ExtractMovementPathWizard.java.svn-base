/*
 * ExtractMovementPathWizard.java
 *
 * Created on June 15, 2005, 12:53 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package cohdemoeditor.wizards;

import java.awt.*;
import javax.swing.*;

import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.FilterList;

/**
 * This wizard extracts a movement path from a demo. The movement path is marked
 * by self-tells at the start and finish; the text of these tells can be
 * customized to allow for multiple extractions per demo.
 * 
 * @author Darren Lee
 */
public class ExtractMovementPathWizard extends DemoWizardDialog {

	private JPanel[] steps;
	private JList sourceList;
	private JTextField startField, endField;

	private DemoCommandList dcl = null;
	private FilterList oldFilters = null;
	private static final int NUMBER_OF_STEPS = 2;
	public static final String DEFAULT_START_STRING = "EMPW Start";
	public static final String DEFAULT_END_STRING = "EMPW End";
	public static final String[] DEFAULT_MOVEMENT_COMMANDS = { "POS", "PYR" };

	/**
	 * Create a new ExtractMovementPathWizard
	 */
	public ExtractMovementPathWizard() {
		steps = new JPanel[NUMBER_OF_STEPS];

		dialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				restoreFilters();
			}
		});

		steps[0] = new JPanel();
		steps[0].setLayout(new BorderLayout());
		steps[0].add(new JLabel("Please select the source demo."),
				BorderLayout.NORTH);
		sourceList = new JList();
		sourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sourceList.setPreferredSize(new Dimension(150, 150));
		steps[0].add(sourceList, BorderLayout.CENTER);
		JButton loadButton = new JButton("Load Demo");
		loadButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ae) {
				if (getDemoEditor() != null) {
					getDemoEditor().loadDemo();
					dialog.toFront();
					sourceList.setSelectedIndex(getDemoEditor()
							.getSelectedIndex());
				}
			}
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(loadButton);
		steps[0].add(buttonPanel, BorderLayout.EAST);

		steps[1] = new JPanel();
		steps[1].setLayout(new GridLayout(2, 2));
		steps[1].add(new JLabel("Tell to start at"));
		startField = new JTextField(DEFAULT_START_STRING);
		steps[1].add(startField);
		steps[1].add(new JLabel("Tell to end at"));
		endField = new JTextField(DEFAULT_END_STRING);
		steps[1].add(endField);
	}

	/**
	 * Resets the wizard.
	 */
	public void resetWizard() {
		super.resetWizard();
		dcl = null;
		oldFilters = null;
	}

	/**
	 * Gets the wizard's name.
	 */
	public String getName() {
		return "Extract Movement Path";
	}

	/**
	 * Gets the number of steps in this wizard
	 */
	public int getNumberOfSteps() {
		return NUMBER_OF_STEPS;
	}

	/**
	 * Gets the appropriate component for each step
	 */
	public Component getComponentForStep(int stepNumber) {
		if (stepNumber < 0 || stepNumber >= steps.length)
			return null;
		if (stepNumber == 0) {
			sourceList.setModel(getDemoEditor().getListModel());
			sourceList.setSelectedIndex(getDemoEditor().getSelectedIndex());
		}
		return steps[stepNumber];
	}

	/**
	 * Helper method to restore the target's filters to their original state
	 */
	private void restoreFilters() {
		if (oldFilters == null || dcl == null)
			return;
		dcl.getFilterList().clear();
		DemoCommandListFilter[] filters = oldFilters.exportFilters();
		for (DemoCommandListFilter f : filters) {
			dcl.getFilterList().add(f);
		}
	}

	/**
	 * Validates user input and performs the extraction
	 */
	public boolean validateCurrentStep() {
		int current = getCurrentStep();

		if (current == 0) {
			int index = sourceList.getSelectedIndex();
			if (index == -1)
				return false;
			dcl = getDemoEditor().getDemoCommandListEditor(index)
					.getDemoCommandList();
			return true;
		}

		if (current == 1) {
			if (startField.getText().equals("")
					|| endField.getText().equals("")) {
				showErrorMessage("You must enter the start and end self-tells that indicate when to begin and end extraction.");
				return false;
			}
			oldFilters = dcl.getFilterList().clone();
			FilterList filters = dcl.getFilterList();
			filters.clear();
			DemoCommandListFilter filter = new DemoCommandListFilter(
					DemoCommandListFilter.SHOW_THESE);
			filter.addCommand("Player");
			filters.add(filter);
			if (dcl.getVisibleCommandCount() == 0) {
				showErrorMessage("Cannot find a player reference in "
						+ dcl.getName());
				return false;
			}
			int playerReference = dcl.getVisibleCommand(0).getReference();
			filters.clear();
			filter = new DemoCommandListFilter(DemoCommandListFilter.SHOW_THESE);
			filter.addReference(playerReference);
			filter.addCommand("NEW");
			filters.add(filter);
			if (dcl.getVisibleCommandCount() == 0) {
				showErrorMessage("Cannot find a player name in "
						+ dcl.getName());
				return false;
			}
			String playerName = dcl.getVisibleCommand(0).getArgument(0);
			String startString = "[Tell]" + playerName + ": "
					+ startField.getText();
			String endString = "[Tell]" + playerName + ": "
					+ endField.getText();
			filters.clear();
			filter = new DemoCommandListFilter(DemoCommandListFilter.SHOW_THESE);
			filter.addReference(playerReference);
			filter.addCommand("Chat");
			filter.addArgument(startString);
			filters.add(filter);
			if (dcl.getVisibleCommandCount() == 0) {
				showErrorMessage("Could not find the starting tell \""
						+ startString + "\"");
				return false;
			}
			int startTime = dcl.getVisibleCommand(0).getTime();
			filters.clear();
			filter = new DemoCommandListFilter(DemoCommandListFilter.SHOW_THESE);
			filter.addReference(playerReference);
			filter.addCommand("Chat");
			filter.addArgument(endString);
			filters.add(filter);
			if (dcl.getVisibleCommandCount() == 0) {
				showErrorMessage("Could not find the ending tell \""
						+ endString + "\"");
				return false;
			}
			int endTime = dcl.getVisibleCommand(0).getTime();
			if (startTime >= endTime) {
				showErrorMessage("Error: ending tell preceeds starting tell.");
				return false;
			}
			filters.clear();
			filter = new DemoCommandListFilter(DemoCommandListFilter.SHOW_THESE);
			filter.addTimeRange(startTime, endTime);
			filter.addReference(playerReference);
			filter.addCommand("POS");
			filter.addCommand("PYR");
			filters.add(filter);
			getDemoEditor().addDemo(dcl.exportVisible());
		}

		return true;
	}

	/**
	 * Returns a brief description of this wizard.
	 */
	public String getDescription() {
		return "This wizard extracts the player's movements (POS/PYR) from a demo.\nThe wizard uses the player's self-tells to indicate where to begin and to end extraction.";
	}

}
