/*
 * DemoWizardDialog.java
 *
 * Created on June 13, 2005, 12:44 PM
 */

package cohdemoeditor.wizards;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoReference;
import cohdemoeditor.DemoReferenceList;
import cohdemoeditor.DemoWizard;
import cohdemoeditor.swing.DemoEditor;

/**
 * <p>
 * This abstract class provides a basic UI framework for a wizard. It is
 * designed for the paradigm of wizards that have a fixed order and number of
 * steps through which the user can move forward and backwards until reaching
 * the end.
 * 
 * <p>
 * At each step, the wizard will display the component returned by
 * <code>getComponentForStep</code> in a non-modal dialog with buttons to
 * proceed to the previous or next step as well as a cancel button. Before
 * proceeding to the next step, the wizard will attempt to validate the user's
 * input using <code>validateCurrentStep</code>. This method is responsible for
 * checking the user input, storing or processing this input, and displaying any
 * necessary error messages. The user cannot proceed to the next step until
 * <code>validateCurrentStep</code> returns <code>true</code>.
 * 
 * <p>
 * Subclasses should implement or override the following six methods:
 * <ul>
 * <li><code>getComponentForStep</code></li>
 * <li><code>validateCurrentStep</code></li>
 * <li><code>resetWizard</code></li>
 * <li><code>getName</code></li>
 * <li><code>getNumberOfSteps</code></li>
 * <li><code>getDescription</code></li>
 * </ul>
 * <p>
 * They may also optionally override the <code>cancelWizard</code> method.
 * 
 * @author Darren Lee
 */
public abstract class DemoWizardDialog extends DemoWizard {

	/**
	 * The default component to display when <code>getComponentForStep</code>
	 * does not provide one.
	 */
	protected static final Component DEFAULT_COMPONENT = new DefaultWizardComponent();
	/**
	 * Demo wizards default to not being undoable, and so they normally reset
	 * the <code>UndoManager</code> before exiting. Set this boolean to
	 * <code>true</code> to override this behavior.
	 */
	protected static final boolean canUndo = false;

	private DemoEditor source = null;
	private int currentStep = -1;
	/**
	 * The parent <code>Dialog</code> of this wizard. Useful as the parent of
	 * dialogs openned by this wizard.
	 */
	protected final JDialog dialog;
	private JPanel buttonPanel;
	private Component wizardPanel = null;
	private JButton prevButton, nextButton, finishButton, cancelButton;

	/**
	 * Creates a new reusable instance of this <code>DemoWizard</code>. Builds
	 * the dialog and control buttons in which to display the wizard's
	 * components.
	 */
	public DemoWizardDialog() {
		dialog = new JDialog((Frame) null, getName(), true);
		dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				resetWizard();
			}
		});
		JPanel panel = new JPanel();
		panel.setBorder(new javax.swing.border.EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new BorderLayout(0, 0));
		dialog.setContentPane(panel);

		wizardPanel = new JPanel();
		dialog.add(wizardPanel, BorderLayout.CENTER);

		buttonPanel = new JPanel();
		dialog.add(buttonPanel, BorderLayout.SOUTH);

		prevButton = new JButton("< Previous");
		addStepChangeListener(new StepChangeListener() {
			public void stepChanged(int newStep) {
				prevButton.setEnabled(newStep != 0);
			}
		});
		prevButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayStep(getCurrentStep() - 1);
			}
		});
		buttonPanel.add(prevButton);

		nextButton = new JButton("Next >");
		addStepChangeListener(new StepChangeListener() {
			public void stepChanged(int newStep) {
				nextButton.setEnabled(newStep != getNumberOfSteps() - 1);
			}
		});
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (validateCurrentStep()) {
					displayStep(getCurrentStep() + 1);
				}
			}
		});
		buttonPanel.add(nextButton);

		finishButton = new JButton("Finish");
		addStepChangeListener(new StepChangeListener() {
			public void stepChanged(int newStep) {
				finishButton.setEnabled(newStep == getNumberOfSteps() - 1);
			}
		});
		finishButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (validateCurrentStep()) {
					dialog.setVisible(false);
					if (!canUndo)
						source.getUndoManager().discardAllEdits();
				}
			}
		});
		buttonPanel.add(finishButton);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				cancelWizard();
			}
		});
		buttonPanel.add(cancelButton);
		dialog.pack();
	}

	/**
	 * Running a wizard from a <code>DemoEditor</code> causes this method to be
	 * called. It resets the wizard using <code>resetWizard</code> and displays
	 * the first step.
	 * 
	 * @param source
	 *            the calling <code>DemoEditor</code>
	 */
	public void runWizard(DemoEditor source) {
		if (source == null)
			return;
		resetWizard();
		this.source = source;
		displayStep(0);
		centerOnEditor();
		dialog.setVisible(true);
	}

	/**
	 * Helper method that centers this dialog on the editor.
	 */
	private void centerOnEditor() {
		DemoEditor editor = getDemoEditor();
		if (editor == null || !editor.isVisible())
			return;
		Dimension parentSize = getDemoEditor().getSize();
		Dimension mySize = dialog.getSize();
		Point parentLocation = getDemoEditor().getLocation();
		dialog
				.setLocation(parentLocation.x
						+ (parentSize.width - mySize.width) / 2,
						parentLocation.y + (parentSize.height - mySize.height)
								/ 2);
	}

	/**
	 * Displays the component for the given step (or a default component if none
	 * is provided) in the wizard.
	 * 
	 * @param stepNum
	 *            the number of the step to display
	 */
	protected void displayStep(int stepNum) {
		currentStep = stepNum;
		Component toAdd = getComponentForStep(stepNum);
		if (toAdd == null)
			toAdd = DEFAULT_COMPONENT;
		if (wizardPanel != null)
			dialog.remove(wizardPanel);
		dialog.add(toAdd, BorderLayout.CENTER);
		wizardPanel = toAdd;
		dialog.setTitle(getName() + " : Step " + (currentStep + 1) + " of "
				+ getNumberOfSteps());
		dialog.pack();
		dialog.repaint();
		centerOnEditor();
		fireStepChangeListeners();
	}

	/**
	 * Returns the number of the current step.
	 * 
	 * @return the number of the current step
	 */
	protected int getCurrentStep() {
		return currentStep;
	}

	/**
	 * Returns the <code>DemoEditor</code> on which the wizard is currently
	 * running.
	 * 
	 * @return the <code>DemoEditor</code> on which the wizard is currently
	 *         running
	 */
	protected DemoEditor getDemoEditor() {
		return source;
	}

	/**
	 * Resets the internal state of the wizard. This method will be called if
	 * the source editor changes. An overriding method should include a call to
	 * the superclass's version of the method: <br>
	 * <code>super.resetWizard()</code>
	 */
	protected void resetWizard() {
		currentStep = -1;
		source = null;
		dialog.remove(wizardPanel);
		wizardPanel = null;
	}

	/**
	 * Called when the cancel button is pressed. Override if you need to restore
	 * or undo anything before leaving the wizard.
	 */
	protected void cancelWizard() {
	}

	/**
	 * This helper method will use a <code>JOptionPane</code> to display the
	 * given error message.
	 * 
	 * @param message
	 *            the error message to display
	 */
	protected void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(dialog, message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Returns the <code>Component</code> to display for the given step. This
	 * component should contain the instructions, interface, etc. for the
	 * current step.
	 * 
	 * @param stepNum
	 *            the number of the step to get a <code>Component</code> for
	 * @return the <code>Component</code> to display, or <code>null</code> if
	 *         there is no component for that step
	 */
	protected abstract Component getComponentForStep(int stepNum);

	/**
	 * This method will be called on each step after the user has pressed the
	 * "Next >" Button. This method must have been called on all previous steps
	 * before being invoked on the current one. Subclasses should use this
	 * method to validate user input, update the internal state, and indicate
	 * whether they are ready to proceed to the next step.
	 * 
	 * Note that this method may be called multiple times on a single step in
	 * the event that a user uses the "< Back" button.
	 * 
	 * @return <code>true</code> if the input for the current step has been
	 *         successfully processed, <code>false</code> otherwise
	 */
	protected abstract boolean validateCurrentStep();

	/**
	 * Returns the total number of steps in this wizard.
	 * 
	 * @return the total number of steps in this wizard
	 */
	protected abstract int getNumberOfSteps();

	private HashSet<StepChangeListener> stepChangeListeners = null;

	/**
	 * Registers a <code>DemoWizard.StepChangeListener</code> to receive
	 * messages when the current step changes.
	 * 
	 * This is intended to be used primarily to keep the interface synchronized
	 * with the current step. In general, subclasses should not need to register
	 * as listeners as the <code>validateCurrentStep</code> method will be
	 * called whenever a user proceeds to the next step.
	 * 
	 * @param scl
	 *            the <code>DemoWizard.StepChangeListener</code> to add
	 */
	public void addStepChangeListener(StepChangeListener scl) {
		if (stepChangeListeners == null)
			stepChangeListeners = new HashSet<StepChangeListener>();
		stepChangeListeners.add(scl);
	}

	/**
	 * Unregisters a <code>DemoWizard.StepChangeListener</code> from receiving
	 * future notifications about the current step changing.
	 * 
	 * @param scl
	 *            the <code>DemoWizard.StepChangeListener</code> to remove
	 */
	public void removeStepChangeListener(StepChangeListener scl) {
		if (stepChangeListeners == null)
			return;
		stepChangeListeners.remove(scl);
	}

	/**
	 * Notifies all registered <code>DemoWizard.StepChangeListener</code>s that
	 * the current step has changed.
	 */
	protected void fireStepChangeListeners() {
		if (stepChangeListeners == null)
			return;
		for (StepChangeListener scl : stepChangeListeners) {
			scl.stepChanged(currentStep);
		}
	}

	/**
	 * Objects that wish to receive notification when the current step of this
	 * wizard changes should implement this interface.
	 */
	public interface StepChangeListener {
		/**
		 * This method is called on all registered listeners when the current
		 * step of a <code>DemoWizard</code> is changed.
		 * 
		 * @param newStep
		 *            the new current step
		 */
		public void stepChanged(int newStep);
	}

	/**
	 * Returns the name of this <code>DemoWizard</code>.
	 * 
	 * @return the name of this <code>DemoWizard</code>
	 */
	public abstract String getName();

	@SuppressWarnings("serial")
	private static class DefaultWizardComponent extends JTextArea {
		public DefaultWizardComponent() {
			super();
			setText("The creator of this wizard did not provide an interface for this step.");
			setEditable(false);
			setWrapStyleWord(true);
		}
	}

	/**
	 * <p>
	 * This class is a panel for selecting a <code>DemoWizard</code>. It is
	 * provided as a convenience to subclasses of <code>DemoWizardDialog</code>.
	 * 
	 * <p>
	 * The panel includes a list of the currently open
	 * <code>DemoCommandList</code>s and a button that allows the user to
	 * instruct the <code>DemoEditor</code> to open a new demo.
	 */
	@SuppressWarnings("serial")
	protected class DemoCommandListChooserPanel extends JPanel {

		private JLabel instructionLabel;
		private JButton loadButton;
		private JList demoList;

		/**
		 * Creates a new <code>DemoCommandListChooserPanel</code>
		 */
		public DemoCommandListChooserPanel() {
			super();
			setLayout(new BorderLayout());
			instructionLabel = new JLabel("Please choose a demo.");
			add(instructionLabel, BorderLayout.NORTH);

			demoList = new JList();
			demoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			demoList.setMinimumSize(new Dimension(150, 150));
			add(new JScrollPane(demoList,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER),
					BorderLayout.CENTER);

			loadButton = new JButton("Load Demo");
			loadButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent ae) {
					if (getDemoEditor() != null) {
						getDemoEditor().loadDemo();
						dialog.toFront();
						demoList.setSelectedIndex(getDemoEditor()
								.getSelectedIndex());
					}
				}
			});
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(loadButton);
			add(buttonPanel, BorderLayout.EAST);
		}

		/**
		 * Sets the current <code>DemoEditor</code>to model the list of
		 * <code>DemoCommandList</code>. Should be called in
		 * <code>getComponentForStep</code> to correctly display the list of
		 * open <code>DemoCommandList</code>s.
		 */
		public void refreshDemoEditor() {
			demoList.setModel(getDemoEditor().getListModel());
			demoList.setSelectedIndex(getDemoEditor().getSelectedIndex());
		}

		/**
		 * Returns the selected <code>DemoCommandList</code>, or
		 * <code>null</code> if none has been selected
		 * 
		 * @return the selected <code>DemoCommandList</code>, or
		 *         <code>null</code> if none has been selected
		 */
		public DemoCommandList getSelection() {
			if (demoList.getSelectedIndex() == -1)
				return null;
			return getDemoEditor().getDemoCommandListEditor(
					demoList.getSelectedIndex()).getDemoCommandList();
		}

		/**
		 * Sets the message to display on the JLabel at the top of this pane.
		 * Usually, this is a short instruction for the user to select a demo
		 * from the list.
		 * 
		 * @param text
		 *            the text to display
		 */
		public void setInstructionText(final String text) {
			instructionLabel.setText(text);
		}
	}

	/**
	 * This is a convenience class that defines a panel from which the user can
	 * select a single <code>DemoCommand</code> from a
	 * <code>DemoCommandList</code>.
	 */
	@SuppressWarnings("serial")
	protected class DemoCommandChooserPanel extends JPanel {
		private DemoCommandList target;
		private JTable cmdTable;
		private JLabel instructionLabel;

		/**
		 * Creates a new <code>DemoCommandChooserPanel</code>.
		 */
		public DemoCommandChooserPanel() {
			super();
			setLayout(new BorderLayout(0, 0));
			setBorder(new javax.swing.border.EmptyBorder(5, 5, 5, 5));

			instructionLabel = new JLabel("Please select a command.");
			add(instructionLabel, BorderLayout.NORTH);

			cmdTable = new JTable();
			cmdTable.setMinimumSize(new Dimension(150, 150));
			cmdTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			add(new JScrollPane(cmdTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER),
					BorderLayout.CENTER);

		}

		/**
		 * Specifies the <code>DemoCommandList</code> whose visible commands
		 * should be displayed.
		 * 
		 * @param target
		 *            the <code>DemoCommandList</code> to display
		 */
		public void setDemoCommandList(DemoCommandList target) {
			this.target = target;
			cmdTable.setModel(target);
			if (target.getRowCount() > 0)
				cmdTable.setRowSelectionInterval(0, 0);
		}

		/**
		 * Returns the selected <code>DemoCommand</code>, or <code>null</code>
		 * if none has been selected
		 * 
		 * @return the selected <code>DemoCommand</code>, or <code>null</code>
		 *         if none has been selected
		 */
		public DemoCommand getSelection() {
			int index = cmdTable.getSelectedRow();
			if (index == -1)
				return null;
			return target.getVisibleCommand(index);
		}

		/**
		 * Sets the message to display on the JLabel at the top of this pane.
		 * Usually, this is a short instruction for the user to select a command
		 * from the list.
		 * 
		 * @param text
		 *            the text to display
		 */
		public void setInstructionText(final String text) {
			instructionLabel.setText(text);
		}
	}

	/**
	 * This is a convenience class that defines a panel from which the user can
	 * select a single <code>DemoReference</code> from a
	 * <code>DemoReferenceList</code>.
	 */
	@SuppressWarnings("serial")
	protected class DemoReferenceChooserPanel extends JPanel {

		private DemoReferenceList refList;
		private JLabel instructionLabel;
		private JTable refTable;

		/**
		 * Creates a new <code>DemoReferenceChooserPanel</code>.
		 */
		public DemoReferenceChooserPanel() {
			super();
			setLayout(new BorderLayout(0, 0));

			instructionLabel = new JLabel("Please select a reference.");
			add(instructionLabel, BorderLayout.NORTH);

			refTable = new JTable();
			refTable.setMinimumSize(new Dimension(150, 150));
			refTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			add(new JScrollPane(refTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER),
					BorderLayout.CENTER);
		}

		/**
		 * Specifies the <code>DemoReferenceList</code> whose references should
		 * be displayed.
		 * 
		 * @param list
		 *            the <code>DemoReferenceList</code> to display
		 */
		public void setDemoReferenceList(DemoReferenceList list) {
			refList = list;
			refTable.setModel(list);
		}

		/**
		 * Returns the selected <code>DemoReference</code>, or <code>null</code>
		 * if none has been selected
		 * 
		 * @return the selected <code>DemoReference</code>, or <code>null</code>
		 *         if none has been selected
		 */
		public DemoReference getSelection() {
			int row = refTable.getSelectedRow();
			if (row == -1)
				return null;
			String refStr = (String) refTable.getValueAt(row, 0);
			refStr = refStr.split(" ")[0];
			int refNum = DemoCommand.getRefNumFor(refStr);
			return refList.getReferenceFor(refNum);
		}

		/**
		 * Sets the message to display on the JLabel at the top of this pane.
		 * Usually, this is a short instruction for the user to select a
		 * reference from the list.
		 * 
		 * @param text
		 *            the text to display
		 */
		public void setInstructionText(final String text) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					instructionLabel.setText(text);
				}
			});
		}
	}

	/**
	 * This is a convenience method used for converting doubles to Strings in
	 * demo files. It is intended for wizards that generate POS and PYR
	 * commands. Doubles are truncated to three decimal places.
	 * 
	 * @param d
	 * @return
	 */
	protected static final String truncate(double d) {
		d = Math.floor(1000*d)/1000;
		return Double.toString(d);
	}

}
