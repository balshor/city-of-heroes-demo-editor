/*
 * LargeFileFilterWizard.java
 *
 * Created on June 17, 2005, 2:44 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package cohdemoeditor.wizards;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.FilterList;
import cohdemoeditor.swing.FilterPanel;

/**
 * This wizard is designed for filtering files without actually loading the
 * whole thing into memory at once. Thus, it is useful for processing large demo
 * files.
 * 
 * @author Darren Lee
 */
public class LargeFileFilterWizard extends DemoWizardDialog {

	private JPanel[] steps;
	private JTextField sourceField, destField;
	private FilterList filterList;
	private File infile, outfile;
	private FilterPanel filters;

	private static final int NUMBER_OF_STEPS = 3;

	/** Creates a new instance of LargeFileFilterWizard */
	@SuppressWarnings("serial")
	public LargeFileFilterWizard() {
		filterList = new FilterList();
		steps = new JPanel[NUMBER_OF_STEPS];

		steps[0] = new JPanel();
		steps[0].setLayout(new BoxLayout(steps[0], BoxLayout.Y_AXIS));
		steps[0]
				.add(new JLabel("Please enter the name of the file to filter."));
		sourceField = new JTextField();
		steps[0].add(sourceField);
		JButton sourceButton = new JButton("Browse");
		sourceButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = getDemoEditor().getDemoFileChooser()
						.showOpenDialog(dialog);
				if (returnVal == JFileChooser.APPROVE_OPTION)
					sourceField.setText(getDemoEditor().getDemoFileChooser()
							.getSelectedFile().getAbsolutePath());
			}
		});
		steps[0].add(sourceButton);

		steps[1] = new JPanel();
		steps[1].setLayout(new BoxLayout(steps[1], BoxLayout.Y_AXIS));
		steps[1].add(new JLabel(
				"Where would you like to save the filtered file?"));
		destField = new JTextField();
		steps[1].add(destField);
		JButton destButton = new JButton("Browse");
		destButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = getDemoEditor().getDemoFileChooser();
				int returnVal = chooser.showSaveDialog(dialog);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					destField.setText(chooser.getSelectedFile()
							.getAbsolutePath());
				}
			}
		});
		steps[1].add(destButton);

		steps[2] = new JPanel();
		steps[2].setLayout(new BorderLayout(0, 0));
		steps[2].add(new JLabel("Please choose the filters to apply"),
				BorderLayout.NORTH);
		filters = new FilterPanel(filterList, dialog);
		steps[2].add(filters, BorderLayout.CENTER);

	}

	/**
	 * Returns the appropriate component for each step.
	 */
	protected Component getComponentForStep(int stepNum) {
		if (stepNum < 0 || stepNum >= getNumberOfSteps())
			return null;
		if (stepNum == 2)
			filters
					.setFilterListManager(getDemoEditor()
							.getFilterListManager());
		return steps[stepNum];
	}

	/**
	 * Validates the input for each step and performs the actual filtering by
	 * delegating to doFiltering.
	 */
	protected boolean validateCurrentStep() {
		int stepNum = getCurrentStep();

		if (stepNum == 0) {
			infile = new File(sourceField.getText());
			if (!infile.exists() || !infile.isFile() || !infile.canRead()) {
				showErrorMessage("Cannot find file " + infile.getName());
				return false;
			}
		} else if (stepNum == 1) {
			outfile = new File(destField.getText());
			if (outfile.equals(infile)) {
				showErrorMessage("Source and destination files must be different.");
				return false;
			}
			if (outfile.exists()) {
				int returnVal = JOptionPane.showConfirmDialog(dialog,
						"File exists!  Overwrite?", "Confirm File Overwrite",
						JOptionPane.YES_NO_OPTION);
				if (!outfile.isFile() || returnVal != JOptionPane.YES_OPTION) {
					return false;
				}
			}
			if (outfile.exists() && !outfile.canWrite()) {
				showErrorMessage("Cannot write to " + outfile.getName());
				return false;
			}
		} else if (stepNum == 2) {
			try {
				doFiltering();
			} catch (FileNotFoundException fnfe) {
				showErrorMessage("Cannot find file.");
			} catch (IOException ioe) {
				showErrorMessage("I/O error");
			}
		}
		return true;
	}

	/**
	 * Does the actual filtering.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void doFiltering() throws FileNotFoundException, IOException {
		ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(
				steps[1], "Filtering file...", new FileInputStream(infile));

		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				pmis));
		final BufferedWriter writer = new BufferedWriter(
				new FileWriter(outfile));
		final FilterList filterList = this.filterList;

		Thread thread = new Thread() {
			public void run() {
				try {
					int timeOffset = 0;

					for (String str = reader.readLine(); str != null; str = reader
							.readLine()) {
						if (str.trim().equals("")) {
							writer.write(str);
							continue;
						}
						DemoCommand dcmd = DemoCommand.parseDemoCommand(str);
						if (dcmd == null) {
							if (reader != null)
								reader.close();
							if (writer != null)
								writer.close();
							showErrorMessage(infile.getName()
									+ " is not a valid .cohdemo file.");
							return;
						}

						// offset time
						timeOffset += dcmd.getTime();

						if (filterList.isVisible(dcmd)) {
							dcmd.setTime(timeOffset);
							writer.write(dcmd.toString() + "\n");
							timeOffset = 0;
						}
					}
					reader.close();
					writer.close();
				} catch (InterruptedIOException iioe) {
					showErrorMessage("Filtering cancelled!");
				} catch (IOException ioe) {
					showErrorMessage("I/O Exception");
				}
			}
		};
		thread.start();
	}

	/**
	 * Resets the UI to its initial values.
	 */
	protected void resetWizard() {
		super.resetWizard();
		sourceField.setText("");
		destField.setText("");
		filterList.clear();
	}

	/**
	 * Returns the name of the wizard.
	 */
	public String getName() {
		return "Large-File Filter Wizard";
	}

	/**
	 * Returns the number of steps in the wizard.
	 */
	protected int getNumberOfSteps() {
		return NUMBER_OF_STEPS;
	}

	/**
	 * Returns a brief description of the wizard.
	 */
	public String getDescription() {
		return "This wizard allows the user to extract commands from very large demo files without having to load the entire file into memory at once.";
	}

}
