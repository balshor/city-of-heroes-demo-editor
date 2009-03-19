/*
 * TimeShiftDialog.java
 *
 * Created on June 24, 2005, 10:15 AM
 */

package cohdemoeditor.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import cohdemoeditor.DemoCommandList;

/**
 * This dialog provides an interface for the user to perform time shifts on all
 * visible demo commands.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class TimeShiftDialog extends JCenteringDialog {

	private JSpinner spinner;
	private DemoCommandList cmdList;

	/**
	 * Creates a new TimeShiftDialog
	 * 
	 * @param cmdList
	 *            the DemoCommandList to timeshift
	 * @param editor
	 *            the DemoEditor with which to register the undoable commands
	 */
	public TimeShiftDialog(DemoCommandList cmdList) {
		super(DemoEditor.getEditor(), "Time Shift Number", true);
		this.cmdList = cmdList;
		setLayout(new BorderLayout(0, 0));
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				reset();
			}
		});
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setBorder(new javax.swing.border.EmptyBorder(5, 5, 5, 5));
		centerPanel.add(new JLabel("Amount to adjust time:"));
		spinner = new JSpinner();
		centerPanel.add(spinner);
		add(centerPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("Okay");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!validateInput())
					return;
				doTimeShift();
				setVisible(false);
				reset();
			}
		});
		buttonPanel.add(okButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				reset();
			}
		});
		buttonPanel.add(cancelButton);
		JButton helpButton = new JButton("Help");
		helpButton.setEnabled(false); // delete this line when help is
		// implemented
		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				; // open the documentation to the appropriate place
			}
		});
		buttonPanel.add(helpButton);
		add(buttonPanel, BorderLayout.SOUTH);

		pack();
		Dimension parentSize = getParent().getSize();
		Dimension mySize = getSize();
		Point parentLocation = getParent().getLocation();
		setLocation(parentLocation.x + (parentSize.width - mySize.width) / 2,
				parentLocation.y + (parentSize.height - mySize.height) / 2);
	}

	/**
	 * Validates the user input. Displays an error message if the spinner cannot
	 * commit the edit.
	 * 
	 * @return true if the input was valid, false otherwise.
	 */
	private boolean validateInput() {
		try {
			spinner.commitEdit();
		} catch (java.text.ParseException e) {
			JOptionPane
					.showMessageDialog(
							DemoEditor.getEditor(),
							"Could not parse your input.  You must enter integers for each field.",
							"Parse Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * Performs the time shift by delegating to DemoCommandList.offSetTimes
	 */
	private void doTimeShift() {
		int value = (Integer) spinner.getValue();
		DemoEditor.getEditor().addUndoableEdit(cmdList.offSetTimes(value));
	}

	/**
	 * Resets the UI to default values.
	 */
	private void reset() {
		spinner.setValue(0);
		spinner.requestFocus();
	}

}
