/*
 * DemoLauncher.java
 *
 * Created on July 3, 2005, 1:19 PM
 */

package cohdemoeditor.swing;

import java.util.*;
import cohdemoeditor.config.DemoEditorConfigType;

import javax.swing.*;
import java.io.*;
import java.awt.*;

/**
 * A DemoLauncher provides a UI for the user to run the demo in the City of
 * Heroes client. It also contains the logic necessary to construct and execute
 * the launching command.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class DemoLauncher extends JCenteringDialog {

	private DemoEditor editor;
	private static final String defaultCoHPath = "c:\\Program Files\\City of Heroes\\";
	private static final String cohExeFile = "CityOfHeroes.exe";
	private File coh = null;

	/** Creates new form DemoLauncher */
	public DemoLauncher(final DemoEditor editor) {
		super(editor, true);
		this.editor = editor;
		final DemoEditorConfigType config = editor.getDemoEditorConfig();
		if (config != null) {
			setCoHPath(config.getDefaultCohPath());
		} else {
			setCoHPath(defaultCoHPath + cohExeFile);
		}
		initComponents();
	}

	/**
	 * Sets the visibility of this dialog. If visibility is set to true, updates
	 * the selection of the list of demos.
	 */
	public void setVisible(boolean b) {
		if (b) {
			demoList.setSelectedIndex(editor.getSelectedIndex());
			super.setVisible(b);
		} else {
			super.setVisible(b);
		}
	}

	/**
	 * Sets the path to the CoH executable.
	 * 
	 * @param path
	 */
	public void setCoHPath(String path) {
		File f = new File(path);
		try {
			coh = f.getCanonicalFile();
		} catch (IOException ioe) {
			coh = f;
		}
	}

	/**
	 * Gets the path to the CoH executable. If this has not yet been set,
	 * returns the default path of C:\Program Files\City of
	 * Heroes\CityofHeroes.exe
	 * 
	 * @return
	 */
	public File getCoHPath() {
		if (coh == null)
			setCoHPath(defaultCoHPath + cohExeFile);
		return coh;
	}

	/**
	 * Returns the directory containing the CoH executable.
	 * 
	 * @return
	 */
	public File getCoHDirectory() {
		return getCoHPath().getParentFile();
	}

	/**
	 * Performs the actual launch.
	 */
	private void doLaunch() {
		// check the user's selection
		int selection = demoList.getSelectedIndex();
		if (selection == -1) {
			JOptionPane.showMessageDialog(this,
					"Please select a demo to launch.",
					"Error: no demo selected", JOptionPane.ERROR_MESSAGE);
			return;
		}
		DemoCommandListEditor target = editor
				.getDemoCommandListEditor(selection);
		if (target.getDemoCommandList().isDirty()) {
			if (!editor.saveDemo(target)) {
				JOptionPane.showMessageDialog(this,
						"You must save your demo before it can be played.",
						"Error: demo not saved",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}

		// Create the executable string: first, determine the shell command and
		// path to the CoH executable
		ArrayList<String> cmds = new ArrayList<String>();
		String osName = System.getProperty("os.name");
		if (osName == null || !osName.contains("Windows")) {
			JOptionPane.showMessageDialog(this,
					"Cannot launch demoes in the non-Windows operating system "
							+ osName, "Error", JOptionPane.ERROR_MESSAGE);
			return;
		} else if (osName.equals("Windows 98") || osName.equals("Windows 95")) {
			cmds.add("command.com");
		} else if (osName.equals("Windows XP") || osName.equals("Windows 2000")
				|| osName.equals("Windows NT")
				|| osName.equals("Windows Vista")) {
			cmds.add("cmd.exe");
		} else {
			JOptionPane
					.showMessageDialog(
							this,
							"Unrecognized Windows version -- using cmd.exe as a command shell.",
							"Warning", JOptionPane.ERROR_MESSAGE);
			cmds.add("cmd.exe");
		}
		cmds.add("/C");
		cmds.add(getCoHPath().getName());

		// determine the flags to add
		if (disable2d.isSelected())
			cmds.add("-disable2d");
		cmds.add("-fullscreen");
		if (fullscreen.isSelected()) {
			cmds.add("1");
		} else {
			cmds.add("0");
		}
		if (customResolution.isSelected()) {
			try {
				int xRes = Integer.parseInt(xResolutionField.getText());
				int yRes = Integer.parseInt(yResolutionField.getText());
				if (!(xRes > 0 && yRes > 0)) {
					throw new NumberFormatException();
				}
				cmds.add("-screen");
				cmds.add("" + xRes);
				cmds.add("" + yRes);
			} catch (java.lang.NumberFormatException nfe) {
				JOptionPane
						.showMessageDialog(
								this,
								"Please select the default resolution or enter positive integers in the resolution fields.",
								"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		if (defaultFPS.isSelected()) {
			try {
				fpsSpinner.commitEdit();
				int fps = (Integer) fpsSpinner.getValue();
				cmds.add("-demofps");
				cmds.add("" + fps);
			} catch (java.text.ParseException pe) {
				JOptionPane.showMessageDialog(this,
						"Error reading the fps field", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		int mode = modeCombo.getSelectedIndex();
		if (mode == -1) {
			JOptionPane.showMessageDialog(this,
					"Please select a playback mode", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (mode == 1) {
			cmds.add("-demodump");
		} else if (mode == 2) {
			cmds.add("-demodumptga");
		}

		cmds.add("-demoplay");

		// save the demo if necessary, add the path to the saved demo file
		File saveFile = target.getDemoCommandList().getSaveFile();
		if (saveFile == null) {
			JFileChooser demoChooser = editor.getDemoFileChooser();
			int returnVal = demoChooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File f = demoChooser.getSelectedFile();
				try {
					target.getDemoCommandList().saveFile(f);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(this,
							"Error saving file, cannot launch.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			} else {
				JOptionPane.showMessageDialog(this,
						"File not saved, cannot launch.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			saveFile = target.getDemoCommandList().getSaveFile();
		}
		cmds.add(saveFile.getAbsolutePath());

		setVisible(false);

		// execute the launch command
		try {
			// wait for the save to complete

			ProcessBuilder pb = new ProcessBuilder(cmds.toArray(new String[cmds
					.size()]));
			Process p = pb.directory(getCoHDirectory()).start();
			StreamHandler errorStream = new StreamHandler(p.getErrorStream(),
					"ERROR");
			StreamHandler inStream = new StreamHandler(p.getInputStream(),
					"OUTPUT");
			errorStream.start();
			inStream.start();
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(this,
					"Error executing the system command:\n" + cmds.toString(),
					"Error", JOptionPane.ERROR_MESSAGE);
			ioe.printStackTrace();
		}
	}

	/**
	 * Sets the selected status of the Disable2D check box.
	 * 
	 * @param disable2d
	 */
	public void setDisable2D(boolean disable2d) {
		this.disable2d.setSelected(disable2d);
	}

	/**
	 * Gets whether the disable2d check box is selected or not.
	 * 
	 * @return
	 */
	public boolean isDisable2D() {
		return disable2d.isSelected();
	}

	/**
	 * Sets the value of the fps spinner. If a non-positive number is passed to
	 * this method, it will deselect the defaultFPS check box.
	 * 
	 * @param fps
	 */
	public void setDefaultFPS(int fps) {
		if (fps <= 0) {
			defaultFPS.setSelected(false);
			return;
		}
		defaultFPS.setSelected(true);
		fpsSpinner.setValue(fps);
	}

	/**
	 * Returns the default FPS setting, or -1 if the default FPS setting is
	 * disabled.
	 * 
	 * @return
	 */
	public int getDefaultFPS() {
		if (!defaultFPS.isSelected())
			return -1;
		return (Integer) fpsSpinner.getValue();
	}

	/**
	 * Sets the selected property of the fullscreen check box.
	 * 
	 * @param fullscreen
	 */
	public void setFullscreen(boolean fullscreen) {
		this.fullscreen.setSelected(fullscreen);
	}

	/**
	 * Gets the selected property of the fullscreen check box.
	 * 
	 * @return
	 */
	public boolean isFullscreen() {
		return fullscreen.isSelected();
	}

	/**
	 * Sets the values of the resolution check box and fields. If either x or y
	 * is non-positive, this method will disable custom resolution.
	 * 
	 * @param x
	 * @param y
	 */
	public void setResolution(int x, int y) {
		if (x <= 0 || y <= 0) {
			customResolution.setSelected(false);
			return;
		}
		customResolution.setSelected(true);
		xResolutionField.setValue(x);
		yResolutionField.setValue(y);
	}

	/**
	 * Returns the value of the x resolution field, or -1 if the resolution
	 * check box is not selected.
	 * 
	 * @return
	 */
	public int getXRes() {
		if (!customResolution.isSelected()) {
			return -1;
		}
		try {
			final int x = Integer.parseInt(xResolutionField.getText());
			return x;
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	/**
	 * Returns the value of the y resolution field, or -1 if the resolution
	 * check box is not selected.
	 * 
	 * @return
	 */
	public int getYRes() {
		if (!customResolution.isSelected()) {
			return -1;
		}
		try {
			final int y = Integer.parseInt(yResolutionField.getText());
			return y;
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	/**
	 * Sets the mode choice box.
	 * 
	 * @param mode
	 */
	public void setMode(int mode) {
		modeCombo.setSelectedIndex(mode);
	}

	/**
	 * Returns the index of the selected mode.
	 * 
	 * @return
	 */
	public int getMode() {
		return modeCombo.getSelectedIndex();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		javax.swing.JPanel centerPanel;
		javax.swing.JPanel configPanel;
		javax.swing.JLabel exeLabel;
		javax.swing.JPanel locationPanel;
		javax.swing.JLabel modeLabel;
		javax.swing.JPanel optionsPanel;
		javax.swing.JPanel resolutionPanel;
		javax.swing.JLabel xLabel;

		chooser = new javax.swing.JFileChooser();
		chooser
				.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
					public boolean accept(File f) {
						return f.isDirectory()
								|| f.getName().equals(cohExeFile);
					}

					public String getDescription() {
						return "City of Heroes Executable (" + cohExeFile + ")";
					}
				});
		chooser
				.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().endsWith(".exe");
					}

					public String getDescription() {
						return "All executable files (*.exe)";
					}
				});
		centerPanel = new javax.swing.JPanel();
		configPanel = new javax.swing.JPanel();
		optionsPanel = new javax.swing.JPanel();
		disable2d = new javax.swing.JCheckBox();
		fullscreen = new javax.swing.JCheckBox();
		defaultFPS = new javax.swing.JCheckBox();
		fpsSpinner = new javax.swing.JSpinner();
		customResolution = new javax.swing.JCheckBox();
		resolutionPanel = new javax.swing.JPanel();
		xResolutionField = new javax.swing.JFormattedTextField();
		xLabel = new javax.swing.JLabel();
		yResolutionField = new javax.swing.JFormattedTextField();
		modeLabel = new javax.swing.JLabel();
		modeCombo = new javax.swing.JComboBox();
		locationPanel = new javax.swing.JPanel();
		exeLabel = new javax.swing.JLabel();
		exePanel = new javax.swing.JPanel();
		exeTextField = new javax.swing.JTextField();
		exeBrowseButton = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		demoList = new javax.swing.JList();
		loadPanel = new javax.swing.JPanel();
		loadButton = new javax.swing.JButton();
		northLabel = new javax.swing.JLabel();
		southPanel = new javax.swing.JPanel();
		launchButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();

		chooser.setCurrentDirectory(new File(defaultCoHPath));

		getContentPane().setLayout(new java.awt.BorderLayout(5, 5));

		setTitle("Demo Launcher");
		centerPanel.setLayout(new java.awt.BorderLayout());

		centerPanel.setBorder(new javax.swing.border.EmptyBorder(
				new java.awt.Insets(5, 5, 5, 5)));
		configPanel.setLayout(new java.awt.BorderLayout());

		optionsPanel.setLayout(new java.awt.GridLayout(0, 2));

		disable2d.setText("disable2d");
		optionsPanel.add(disable2d);

		fullscreen.setSelected(true);
		fullscreen.setText("Full Screen");
		optionsPanel.add(fullscreen);

		defaultFPS.setText("Specify FPS");
		defaultFPS.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				defaultFPSItemStateChanged(evt);
			}
		});

		optionsPanel.add(defaultFPS);

		fpsSpinner.setEnabled(false);
		((SpinnerNumberModel) fpsSpinner.getModel()).setMinimum(new Integer(1));
		fpsSpinner.getModel().setValue(new Integer(30));
		optionsPanel.add(fpsSpinner);

		customResolution.setText("Specify Resolution");
		customResolution.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				customResolutionItemStateChanged(evt);
			}
		});

		optionsPanel.add(customResolution);

		resolutionPanel.setLayout(new javax.swing.BoxLayout(resolutionPanel,
				javax.swing.BoxLayout.X_AXIS));

		xResolutionField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		xResolutionField.setText(Integer.toString((int) Toolkit
				.getDefaultToolkit().getScreenSize().getWidth()));
		xResolutionField.setEnabled(false);
		resolutionPanel.add(xResolutionField);

		xLabel.setText(" x ");
		resolutionPanel.add(xLabel);

		yResolutionField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		yResolutionField.setText(Integer.toString((int) Toolkit
				.getDefaultToolkit().getScreenSize().getHeight()));
		yResolutionField.setEnabled(false);
		resolutionPanel.add(yResolutionField);

		optionsPanel.add(resolutionPanel);

		modeLabel.setText("Mode");
		optionsPanel.add(modeLabel);

		modeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"Standard Playback", "Output as Screenshots (JPG)",
				"Output as Screenshots (TGA)" }));
		optionsPanel.add(modeCombo);

		configPanel.add(optionsPanel, java.awt.BorderLayout.SOUTH);

		locationPanel.setLayout(new java.awt.BorderLayout());

		exeLabel.setText("City of Heroes Executable Location");
		locationPanel.add(exeLabel, java.awt.BorderLayout.NORTH);

		exePanel.setLayout(new javax.swing.BoxLayout(exePanel,
				javax.swing.BoxLayout.X_AXIS));

		exeTextField.setText(getCoHPath().getAbsolutePath());
		exePanel.add(exeTextField);

		exeBrowseButton.setText("Browse");
		exeBrowseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exeBrowseButtonActionPerformed(evt);
			}
		});

		exePanel.add(exeBrowseButton);

		locationPanel.add(exePanel, java.awt.BorderLayout.CENTER);

		configPanel.add(locationPanel, java.awt.BorderLayout.NORTH);

		centerPanel.add(configPanel, java.awt.BorderLayout.SOUTH);

		demoList.setModel(editor.getListModel());
		demoList
				.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		jScrollPane1.setViewportView(demoList);

		centerPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

		loadPanel.setLayout(new javax.swing.BoxLayout(loadPanel,
				javax.swing.BoxLayout.Y_AXIS));

		loadButton.setText("Load");
		loadButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loadButtonActionPerformed(evt);
			}
		});

		loadPanel.add(loadButton);

		centerPanel.add(loadPanel, java.awt.BorderLayout.EAST);

		northLabel.setText("Demo to Launch");
		centerPanel.add(northLabel, java.awt.BorderLayout.NORTH);

		getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

		launchButton.setText("Launch");
		launchButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				launchButtonActionPerformed(evt);
			}
		});

		southPanel.add(launchButton);

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		southPanel.add(cancelButton);

		getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

		pack();
	}

	// </editor-fold>//GEN-END:initComponents

	private void exeBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_exeBrowseButtonActionPerformed
		int rtnval = chooser.showOpenDialog(exePanel);
		if (rtnval == JFileChooser.APPROVE_OPTION) {
			try {
				setCoHPath(chooser.getSelectedFile().getCanonicalPath());
				exeTextField.setText(getCoHPath().getCanonicalPath());
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(this, "Error resolving file.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}// GEN-LAST:event_exeBrowseButtonActionPerformed

	private void defaultFPSItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-
		// FIRST
		// :
		// event_defaultFPSItemStateChanged
		boolean b = defaultFPS.isSelected();
		fpsSpinner.setEnabled(b);
	}// GEN-LAST:event_defaultFPSItemStateChanged

	private void customResolutionItemStateChanged(java.awt.event.ItemEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_customResolutionItemStateChanged
		boolean b = customResolution.isSelected();
		xResolutionField.setEnabled(b);
		yResolutionField.setEnabled(b);
	}// GEN-LAST:event_customResolutionItemStateChanged

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_cancelButtonActionPerformed
		setVisible(false);
	}// GEN-LAST:event_cancelButtonActionPerformed

	private void launchButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_launchButtonActionPerformed
		doLaunch();
	}// GEN-LAST:event_launchButtonActionPerformed

	private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN
		// -
		// FIRST
		// :
		// event_loadButtonActionPerformed
		if (editor != null) {
			editor.loadDemo();
			DemoLauncher.this.toFront();
			demoList.setSelectedIndex(editor.getSelectedIndex());
		}
	}// GEN-LAST:event_loadButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton cancelButton;
	private javax.swing.JFileChooser chooser;
	private javax.swing.JCheckBox customResolution;
	private javax.swing.JCheckBox defaultFPS;
	private javax.swing.JList demoList;
	private javax.swing.JCheckBox disable2d;
	private javax.swing.JButton exeBrowseButton;
	private javax.swing.JPanel exePanel;
	private javax.swing.JTextField exeTextField;
	private javax.swing.JSpinner fpsSpinner;
	private javax.swing.JCheckBox fullscreen;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JButton launchButton;
	private javax.swing.JButton loadButton;
	private javax.swing.JPanel loadPanel;
	private javax.swing.JComboBox modeCombo;
	private javax.swing.JLabel northLabel;
	private javax.swing.JPanel southPanel;
	private javax.swing.JFormattedTextField xResolutionField;
	private javax.swing.JFormattedTextField yResolutionField;

	// End of variables declaration//GEN-END:variables

	private class StreamHandler extends Thread {
		private InputStream is;
		private String type;

		StreamHandler(InputStream is, String type) {
			this.is = is;
			this.type = type;
			this.setPriority(Thread.NORM_PRIORITY);
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null)
					System.out.println(type + ">" + line);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

}
