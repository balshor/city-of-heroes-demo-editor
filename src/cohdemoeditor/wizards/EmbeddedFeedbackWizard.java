/*
 * EmbeddedFeedbackWizard.java
 *
 * Created on August 30, 2005, 1:43 PM
 */

package cohdemoeditor.wizards;

import java.awt.*;
import javax.swing.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.FilterList;

/**
 * It can be difficult determining information about a demo by watching it. This
 * wizard adds chat spam to a demo so a viewer can easily determine reference
 * numbers and the time.  The wizard can also remove this chat spam.
 * 
 * @author Darren Lee
 */
public class EmbeddedFeedbackWizard extends DemoWizardDialog {

	private static final int NUM_STEPS = 3;
	private static final String DEFAULT_ID_STRING = "(embed)";
	private static final int LOCAL_CHAT = 10;

	private DemoCommandListChooserPanel dclcp;

	private JPanel addOrRemovePanel, addPanel, removePanel;
	private JRadioButton addButton, removeButton;
	private JSpinner freqSpinner;
	private JTextField idAddTextField, idRemoveTextField;
	private JCheckBox addTimeBox, addRefBox;
	private JTable refTable;

	private DemoCommandList target;

	/** Creates a new instance of EmbeddedFeedbackWizard */
	public EmbeddedFeedbackWizard() {
		super();
		dclcp = new DemoCommandListChooserPanel();
		dclcp.setInstructionText("Please choose the target demo.");

		addOrRemovePanel = new JPanel();
		ButtonGroup bg = new ButtonGroup();
		addButton = new JRadioButton("Add embedded chat");
		addButton.setSelected(true);
		bg.add(addButton);
		addOrRemovePanel.add(addButton);
		removeButton = new JRadioButton("Remove embedded chat");
		bg.add(removeButton);
		addOrRemovePanel.add(removeButton);

		addPanel = new JPanel();
		addPanel.setLayout(new BorderLayout(0, 0));
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		Box panel2 = Box.createHorizontalBox();
		addTimeBox = new JCheckBox("Display Time");
		addTimeBox.setSelected(true);
		panel2.add(addTimeBox);
		addRefBox = new JCheckBox("Display Reference Number");
		addRefBox.setSelected(true);
		panel2.add(addRefBox);
		panel.add(panel2);
		panel2 = Box.createHorizontalBox();
		JLabel label = new JLabel("Chat ID ");
		label
				.setToolTipText("The Chat ID is used to distinguish between the embedded messages and legitimate chat in the demo.");
		panel2.add(label);
		idAddTextField = new JTextField();
		idAddTextField.setText(DEFAULT_ID_STRING);
		panel2.add(idAddTextField);
		panel.add(panel2);
		panel2 = Box.createHorizontalBox();
		panel2.add(new JLabel("Time (in ms) between chat messages"));
		freqSpinner = new JSpinner(new SpinnerNumberModel(1000, 1,
				Integer.MAX_VALUE, 1));
		panel2.add(freqSpinner);
		panel.add(panel2);
		addPanel.add(panel, BorderLayout.SOUTH);
		refTable = new JTable();
		refTable.setMinimumSize(new Dimension(150, 150));
		refTable
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		addPanel.add(new JScrollPane(refTable,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER),
				BorderLayout.CENTER);
		removePanel = new JPanel();
		removePanel.setLayout(new BoxLayout(removePanel, BoxLayout.X_AXIS));
		removePanel.add(new JLabel("Chat ID to remove "));
		idRemoveTextField = new JTextField();
		removePanel.add(idRemoveTextField);
	}

	/**
	 * Resets the UI to its initial state.
	 */
	protected void resetWizard() {
		super.resetWizard();
		addButton.setSelected(true);
		removeButton.setSelected(false);
		addTimeBox.setSelected(true);
		addRefBox.setSelected(true);
	}

	/**
	 * Retrieves the correct component for each step.
	 */
	protected Component getComponentForStep(int stepNum) {
		if (stepNum == 0) {
			dclcp.refreshDemoEditor();
			return dclcp;
		} else if (stepNum == 1) {
			return addOrRemovePanel;
		} else if (stepNum == 2) {
			if (addButton.isSelected()) {
				refTable.setModel(target.getDemoReferenceList());
				return addPanel;
			} else {
				idRemoveTextField.setText(idAddTextField.getText());
				return removePanel;
			}
		}
		return null;
	}

	/**
	 * Validates user input and inserts or removes chat spam.
	 */
	protected boolean validateCurrentStep() {
		int stepNum = getCurrentStep();
		if (stepNum == 0) {
			target = dclcp.getSelection();
			if (target == null) {
				showErrorMessage("Please select a demo.");
				return false;
			}
		} else if (stepNum == 1) {
			if (!addButton.isSelected() && !removeButton.isSelected()) {
				showErrorMessage("Error: neither add nor remove is selected.");
				return false;
			} else if (addButton.isSelected() && removeButton.isSelected()) {
				showErrorMessage("Error: both add and remove are selected.");
				return false;
			}
		} else if (stepNum == 2) {
			if (addButton.isSelected()) {
				if (refTable.getSelectedRowCount() == 0) {
					showErrorMessage("Please select at least one reference.");
					return false;
				}
				try {
					freqSpinner.commitEdit();
				} catch (java.text.ParseException pe) {
					showErrorMessage("Could not parse the time field.");
					return false;
				}
				if (!(freqSpinner.getValue() instanceof Integer)) {
					showErrorMessage("Could not parse an integer value from the time field.");
					return false;
				}
				if (!addTimeBox.isSelected() && !addRefBox.isSelected()) {
					showErrorMessage("Nothing to add!  Please select either the time or reference checkbox.");
					return false;
				}
				if (idAddTextField.getText().equals("")) {
					showErrorMessage("Please choose an id to add to the end of the embedded chat messages.\n(These will allow the wizard to remove them later.");
					return false;
				}
				doAddChat();
			} else {
				if (idRemoveTextField.getText().equals("")) {
					showErrorMessage("Please choose the id of the embedded chat messages.\n(This prevents legitimate chat messages from being removed.");
					return false;
				}
				doRemoveChat();
			}
		}
		return true;
	}

	/**
	 * Helper method that does the actual addition of chat spam.
	 */
	private void doAddChat() {
		final int[] selectedRows = refTable.getSelectedRows();
		final int finalTime = target.getLastTime();
		final boolean addTime = addTimeBox.isSelected();
		final boolean addRef = addRefBox.isSelected();
		final String id = idAddTextField.getText();
		DemoCommandListFilter newFilter = new DemoCommandListFilter();
		newFilter.addCommand("NEW");
		for (int row : selectedRows) {
			for (int time = 0; time < finalTime;) {
				final int refNum = target.getDemoReferenceList()
						.getRefNumForRow(row);
				FilterList filters = new FilterList();
				filters.add(newFilter);
				DemoCommandListFilter refFilter = new DemoCommandListFilter();
				refFilter.addReference(refNum);
				refFilter.addTimeRange(time, finalTime);
				filters.add(refFilter);
				DemoCommand cmd = target.findFirstCommand(filters);
				if (cmd == null)
					break;
				int startTime = cmd.getTime();
				cmd = target.findDEL(startTime, refNum);
				int endTime;
				if (cmd == null) {
					endTime = finalTime;
				} else {
					endTime = cmd.getTime();
				}
				time = endTime + 1;
				for (int i = startTime; i < endTime; i += (Integer) freqSpinner
						.getValue()) {
					String chatMsg = "" + LOCAL_CHAT + " 0 " + "\"";
					if (addTime)
						chatMsg += "TIME: " + i;
					if (addTime && addRef)
						chatMsg += " -- ";
					if (addRef)
						chatMsg += "REF: " + refNum;
					chatMsg += " -- EmbeddedFeedbackID: " + id + "\"";
					target.addCommand(new DemoCommand(i, refNum, "Chat",
							chatMsg));
				}
			}
		}
		target.resort();
	}

	/**
	 * Helper method that removes the chat spam.
	 */
	private void doRemoveChat() {
		int commandCount = target.getCommandCount();
		final String id = idRemoveTextField.getText();
		for (int i = 0; i < commandCount; i++) {
			DemoCommand cmd = target.getCommand(i);
			if (cmd.getCommand().equals("Chat")
					&& cmd.getArguments().endsWith(
							" -- EmbeddedFeedbackID: " + id + "\"")) {
				target.removeCommand(cmd);
				i--;
				commandCount--;
			}
		}
	}

	/**
	 * Returns the name of the wizard.
	 */
	public String getName() {
		return "Embedded Feedback Wizard";
	}

	/**
	 * Returns a brief description of the wizard.
	 */
	public String getDescription() {
		return "This wizard will add (and later remove) chat spam from a demo that indicates reference numbers and time marks.";
	}

	/**
	 * Returns the number of steps in this wizard.
	 */
	protected int getNumberOfSteps() {
		return NUM_STEPS;
	}

}
