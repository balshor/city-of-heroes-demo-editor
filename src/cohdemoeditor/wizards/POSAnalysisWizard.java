/*
 * POSAnalysisWizard.java
 *
 * Created on July 14, 2005, 8:28 PM
 */

package cohdemoeditor.wizards;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.FilterList;
import cohdemoeditor.swing.JCenteringDialog;

import java.lang.Math;

/**
 * This wizard scans all POS commands to find the extrema of each coordinate.
 * 
 * @author Darren Lee
 */
public class POSAnalysisWizard extends DemoWizardDialog {

	private static final int NUM_STEPS = 1;
	private DemoCommandListChooserPanel dclcp;
	private static DemoCommandListFilter posFilter = null;
	private DefaultTableModel results;
	private JCenteringDialog resultsDialog;

	/**
	 * Creates a new instance of POSAnalysisWizard
	 */
	@SuppressWarnings("serial")
	public POSAnalysisWizard() {

		// panel for the first step: choose a demo
		dclcp = new DemoCommandListChooserPanel();
		if (posFilter == null) {
			posFilter = new DemoCommandListFilter();
			posFilter.addCommand("POS");
		}

		// model for the results table
		results = new DefaultTableModel(4, 3) {
			public boolean isCellEditable(int row, int column) {
				return (row > 0 && column > 0);
			}
		};
		results.setValueAt("min", 0, 1);
		results.setValueAt("max", 0, 2);
		results.setValueAt("x", 1, 0);
		results.setValueAt("z", 2, 0);
		results.setValueAt("y", 3, 0);
		JTable table = new JTable(results);
		JScrollPane scroller = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		resultsDialog = new JCenteringDialog(new JFrame(),
				"POS Analysis Results", false);
		resultsDialog.add(scroller, BorderLayout.CENTER);
		JButton button = new JButton("Close");
		button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				resultsDialog.setVisible(false);
			}
		});
		resultsDialog.pack();
	}

	/**
	 * Returns the appropriate Component for the first (and only) step.
	 */
	protected Component getComponentForStep(int stepnum) {
		if (stepnum == 0) {
			dclcp.refreshDemoEditor();
			resultsDialog.setTargetContainer(getDemoEditor());
			return dclcp;
		}
		return null;
	}

	/**
	 * Performs the POS extrema calculation, inserts the results into the
	 * DefaultTableModel, and displays the results.
	 */
	protected boolean validateCurrentStep() {
		DemoCommandList target = dclcp.getSelection();
		if (target == null) {
			showErrorMessage("Please select a demo.");
			return false;
		}
		FilterList filters = target.getFilterList();
		FilterList oldFilters = filters.clone();
		filters.clear();
		filters.add(posFilter);
		final int numCmds = target.getVisibleCommandCount();
		if (numCmds == 0) {
			filters.clear();
			filters.add(oldFilters);
			showErrorMessage("This demo has no POS commands!");
			return false;
		}
		double maxX = 0, minX = 0, maxY = 0, minY = 0, maxZ = 0, minZ = 0;
		boolean init = false;
		int badlines = 0;
		for (int i = 0; i < numCmds; i++) {
			DemoCommand cmd = target.getVisibleCommand(i);
			if (cmd.getArgumentCount() != 3) {
				badlines++;
				continue;
			}
			double x, y, z;
			try {
				x = Double.parseDouble(cmd.getArgument(0));
				z = Double.parseDouble(cmd.getArgument(1));
				y = Double.parseDouble(cmd.getArgument(2));
			} catch (NumberFormatException nfe) {
				badlines++;
				continue;
			}
			if (init) {
				maxX = Math.max(x, maxX);
				minX = Math.min(x, minX);
				maxY = Math.max(y, maxY);
				minY = Math.min(y, minY);
				maxZ = Math.max(z, maxZ);
				minZ = Math.min(z, minZ);
			} else {
				init = true;
				maxX = minX = x;
				maxY = minY = y;
				maxZ = minZ = z;
			}
		}
		if (badlines > 0) {
			showErrorMessage("Found " + badlines + " malformed POS commands.");
		}
		if (!init) {
			showErrorMessage("Could not find any valid POS commands.");
			return false;
		}

		results.setValueAt(minX, 1, 1);
		results.setValueAt(maxX, 1, 2);
		results.setValueAt(minZ, 2, 1);
		results.setValueAt(maxZ, 2, 2);
		results.setValueAt(minY, 3, 1);
		results.setValueAt(maxY, 3, 2);

		resultsDialog.setVisible(true);
		filters.clear();
		filters.add(oldFilters);
		return true;
	}

	/**
	 * Resets the wizard.
	 */
	protected void resetWizard() {
		super.resetWizard();
	}

	/**
	 * Returns 1, the number of steps.
	 */
	protected int getNumberOfSteps() {
		return NUM_STEPS;
	}

	/**
	 * Returns the name of the wizard.
	 */
	public String getName() {
		return "POS Analysis Wizard";
	}

	/**
	 * Returns a short description of the wizard.
	 */
	public String getDescription() {
		return "This wizard will calculate the largest and smallest values used for each of the three arguments to the POS command.";
	}

}