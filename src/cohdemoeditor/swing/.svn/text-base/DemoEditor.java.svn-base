/*
 * Editor.java
 *
 * Created on June 13, 2005, 12:48 PM
 */

package cohdemoeditor.swing;

import cohdemoeditor.DemoCommand;
import cohdemoeditor.DemoCommandComparator;
import cohdemoeditor.DemoCommandList;
import cohdemoeditor.DemoCommandListFilter;
import cohdemoeditor.DemoWizard;
import cohdemoeditor.DemoWizardManager;
import cohdemoeditor.DirtyBitListener;
import cohdemoeditor.DirtyBitTracker;
import cohdemoeditor.FilterList;
import cohdemoeditor.FilterListManager;
import cohdemoeditor.config.*;
import cohdemoeditor.wizards.*;
import java.io.*;
import java.util.*;
import java.awt.Point;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import javax.help.*;
import java.net.URL;
import javax.xml.bind.*;
import java.awt.datatransfer.*;

/**
 * The DemoEditor class extends JFrame to provide the window that the program
 * runs in. It is also the primary entry point to the program. It is responsible
 * for managing the DemoCommandListEditor windows (InternalFrames, really) and
 * coordinating all of the pieces that act on the program as a whole (as opposed
 * to acting on a single demo). This includes things like loading the
 * configuration file, managing the help system, launching demos on request,
 * loading wizards, etc.
 * 
 * @author Darren Lee
 */
@SuppressWarnings( { "serial", "static-access" })
public class DemoEditor extends javax.swing.JFrame implements DirtyBitListener,
		InternalFrameListener {

	private final JFileChooser demoChooser;

	private List<DemoCommandListEditor> demoWindows;
	private DemoLauncher demoLauncher;
	private DemoWizardManager wizManager;
	private DemoEditorListModel listModel;
	private DemoWizardChooser wizardChooser;
	private FilterListManager flManager;
	private Action saveAction, newAction, loadAction, quitAction,
			cascadeAction, undoAction, redoAction, cutAction, copyAction,
			pasteAction;
	private ListSelectionListener cmdSelectionListener;
	private String defaultPath = "c:\\program files\\city of heroes\\client_demos\\";
	private boolean isPC = true;
	private DemoUndoManager undoManager;
	private HelpSet helpSet = null;
	private HelpBroker helpBroker = null;
	private List<String> wizardsToLoad;
	private Clipboard clipboard;
	private final DemoCommandComparator comparator;

	/**
	 * The default file filter shows directories and files that end with
	 * ".cohdemo"
	 */
	public static final javax.swing.filechooser.FileFilter DEFAULT_FILE_FILTER = new javax.swing.filechooser.FileFilter() {
		public boolean accept(File file) {
			return file.getName().endsWith(".cohdemo") || file.isDirectory();
		}

		public String getDescription() {
			return "City of Heroes Demo Files (*.cohdemo)";
		}
	};
	public static final TextFieldPopupMenu TEXT_FIELD_POPUP_MENU = new TextFieldPopupMenu();

	private static final String VERSION = "0.9.1";
	private static final int CASCADE_OFFSET = 25;
	private static final String HELPSET_FILE = "help/Helpset.hs";
	private static final String CONFIG_PATH = "config.xml";
	private static final String LINE_SEPARATOR = java.awt.Toolkit
			.getDefaultToolkit().getProperty("line.separator", "\n");

	private JAXBContext jc;
	private DemoEditorConfigType config;

	private static final DemoEditor editor;
	static {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		editor = new DemoEditor();
	}

	public static final DemoEditor getEditor() {
		return editor;
	}

	/**
	 * Creates new DemoEditor. Initializes the clipboard, filter list manager,
	 * actions, help system, Swing components, undo manager, demo launcher, and
	 * wizard launcher. Loads the configuration file.
	 */
	private DemoEditor() {
		try {
			SecurityManager sm = System.getSecurityManager();
			if (sm != null) {
				System.getSecurityManager().checkSystemClipboardAccess();
			}
			clipboard = java.awt.Toolkit.getDefaultToolkit()
					.getSystemClipboard();
		} catch (SecurityException se) {
			clipboard = new Clipboard("CoH Demo Editor Clipboard");
		}
		comparator = new DemoCommandComparator();
		flManager = new FilterListManager();
		wizardsToLoad = new ArrayList<String>();
		demoWindows = new ArrayList<DemoCommandListEditor>();
		listModel = new DemoEditorListModel();
		demoLauncher = new DemoLauncher(this);
		initActions();
		initHelp();
		loadConfig();
		initComponents();
		desktopPane.setBackground(java.awt.Color.WHITE);
		undoManager = new DemoUndoManager();
		wizManager = new DemoWizardManager(this);
		wizardChooser = new DemoWizardChooser(this, wizManager);
		demoChooser = new JFileChooser(defaultPath);
		demoChooser.addChoosableFileFilter(DEFAULT_FILE_FILTER);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		refreshDirty();
		addDefaultWizards();
	}

	/**
	 * Helper method that initializes the helper system in this DemoEditor.
	 */
	private void initHelp() {
		ClassLoader cl = DemoEditor.class.getClassLoader();
		helpSet = null;
		try {
			URL hsURL = HelpSet.findHelpSet(cl, HELPSET_FILE);
			helpSet = new HelpSet(cl, hsURL);
		} catch (HelpSetException hse) {
			hse.printStackTrace();
			JOptionPane.showMessageDialog(null, "Could not find help files.",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		if (helpSet != null) {
			helpBroker = helpSet.createHelpBroker();
		}
	}

	/**
	 * Helper method that initializes the various actions defined in this
	 * DemoEditor.
	 */
	private void initActions() {
		saveAction = new AbstractAction("Save") {
			public void actionPerformed(ActionEvent e) {
				if (getSelectedDemo() == null)
					return;
				saveDemo(getSelectedDemo());
			}
		};

		newAction = new AbstractAction("New") {
			public void actionPerformed(ActionEvent e) {
				newDemo();
			}
		};

		loadAction = new AbstractAction("Load") {
			public void actionPerformed(ActionEvent e) {
				loadDemo();
			}
		};

		quitAction = new AbstractAction("Quit") {
			public void actionPerformed(ActionEvent e) {
				close();
				int defCloseOp = getDefaultCloseOperation();
				if (defCloseOp == WindowConstants.EXIT_ON_CLOSE)
					System.exit(0);
			}
		};

		cascadeAction = new AbstractAction("Cascade Windows") {
			public void actionPerformed(ActionEvent e) {
				if (demoWindows == null || demoWindows.size() == 0)
					return;
				Point p = new Point(0, 0);
				for (DemoCommandListEditor dcle : demoWindows) {
					if (!dcle.isIcon()) {
						dcle.setLocation(p);
						p.translate(CASCADE_OFFSET, CASCADE_OFFSET);
						dcle.moveToFront();
					}
				}
			}
		};

		undoAction = new AbstractAction("Undo") {
			public void actionPerformed(ActionEvent e) {
				undoManager.undo();
			}
		};
		undoAction.setEnabled(false);

		redoAction = new AbstractAction("Redo") {
			public void actionPerformed(ActionEvent e) {
				undoManager.redo();
			}
		};
		redoAction.setEnabled(false);

		cutAction = new AbstractAction("Cut") {
			public void actionPerformed(ActionEvent e) {
				doCut();
			}
		};
		cutAction.setEnabled(false);

		copyAction = new AbstractAction("Copy") {
			public void actionPerformed(ActionEvent e) {
				doCopy();
			}
		};
		copyAction.setEnabled(false);

		pasteAction = new AbstractAction("Paste") {
			public void actionPerformed(ActionEvent e) {
				doPaste();
			}
		};
		pasteAction.setEnabled(clipboard
				.isDataFlavorAvailable(DataFlavor.stringFlavor));
		clipboard.addFlavorListener(new FlavorListener() {
			public void flavorsChanged(FlavorEvent fe) {
				pasteAction.setEnabled(clipboard
						.isDataFlavorAvailable(DataFlavor.stringFlavor));
			}
		});

		cmdSelectionListener = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (getSelectedDemo() == null)
					return;
				if (e.getSource().equals(
						getSelectedDemo().getListSelectionModel())) {
					refreshCutCopy();
				}
			}
		};
	}

	/**
	 * Executes the cut command. Copies the current selection to the clipboard,
	 * then deletes the current selection.
	 */
	public void doCut() {
		doCutCopy(true);
	}

	/**
	 * Executes the copy command. Copies the current selection to the clipboard.
	 */
	public void doCopy() {
		doCutCopy(false);
	}

	/**
	 * This helper method enables or disables the cut and copy actions depending
	 * on whether there is a selection on the current demo.
	 */
	private void refreshCutCopy() {
		boolean enabled = false;
		DemoCommandListEditor dcle = getSelectedDemo();
		if (dcle != null) {
			ListSelectionModel lsm = dcle.getListSelectionModel();
			if (!lsm.isSelectionEmpty()) {
				enabled = true;
			}
		}
		cutAction.setEnabled(enabled);
		copyAction.setEnabled(enabled);
	}

	/**
	 * Helper method to execute a cut or copy command. Use the parameter isCut
	 * to tell it which one.
	 * 
	 * @param isCut
	 *            true if we are performing a cut, false to perform a copy
	 */
	private void doCutCopy(final boolean isCut) {
		CompoundEdit ce = new CompoundEdit();
		String output = "";
		DemoCommandListEditor dcle = getSelectedDemo();
		if (dcle == null)
			return;
		DemoCommandList dcl = dcle.getDemoCommandList();
		ListSelectionModel lsm = dcle.getListSelectionModel();
		if (lsm.isSelectionEmpty())
			return;
		int start = lsm.getMinSelectionIndex();
		int end = lsm.getMaxSelectionIndex();
		for (int i = end; i >= start; i--) {
			if (lsm.isSelectedIndex(i)) {
				DemoCommand cmd = dcl.getVisibleCommand(i);
				int absIndex = dcl.indexOf(cmd);
				int offset = 0;
				if (absIndex > 0) {
					offset = dcl.getCommand(absIndex - 1).getTime();
				}
				output = cmd.toString(offset) + LINE_SEPARATOR + output;
				if (isCut)
					ce.addEdit(dcl.removeCommand(i));
			}
		}
		StringSelection ss = new StringSelection(output);
		clipboard.setContents(ss, ss);
		if (ce.isSignificant()) {
			undoManager.addEdit(ce);
		}
	}

	/**
	 * Pastes the current clipboard contents into the currently selected demo.
	 * If no demo is currently selected, creates a new demo and pastes into
	 * there.
	 * 
	 * The clipboard data must be standard demo text (stringFlavor).
	 */
	public void doPaste() {
		DemoCommandListEditor targetEditor = DemoEditor.this.getSelectedDemo();
		DemoCommandList target;
		ArrayList<String> badStrings = new ArrayList<String>();
		String str = null;
		CompoundEdit ce = new CompoundEdit();

		if (targetEditor == null) {
			target = new DemoCommandList();
			targetEditor = DemoEditor.this.addDemo(target);
		} else {
			target = targetEditor.getDemoCommandList();
		}
		try {
			str = (String) clipboard.getData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException ufe) {
			JOptionPane.showMessageDialog(DemoEditor.this,
					"The clipboard does not contain text.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(DemoEditor.this,
					"Error retrieving clipboard data type.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		String[] lines = str.split(java.awt.Toolkit.getDefaultToolkit()
				.getProperty("line.separator", "\n"));
		int targetline = target.getVisibleCommandCount();
		if (!targetEditor.getListSelectionModel().isSelectionEmpty()) {
			targetline = targetEditor.getListSelectionModel()
					.getAnchorSelectionIndex();
		}
		for (String line : lines) {
			if (line.trim().equals(""))
				continue;
			DemoCommand cmd = DemoCommand.parseDemoCommand(line);
			if (cmd != null) {
				if (targetline != 0) {
					DemoCommand prevCmd = target
							.getVisibleCommand(targetline - 1);
					cmd.setTime(prevCmd.getTime() + cmd.getTime());
				}
				ce.addEdit(target.addCommand(targetline, cmd));
				targetline++;
			} else {
				badStrings.add("  " + line);
			}
		}
		ce.end();
		if (ce.isSignificant())
			undoManager.addEdit(ce);
		if (badStrings.size() > 0) {
			badStrings.add(0, "The following lines could not be parsed:");
			JOptionPane.showMessageDialog(DemoEditor.this,
					badStrings.toArray(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Simple getter for the action that loads a new demo.
	 * 
	 * @return
	 */
	public Action getLoadAction() {
		return loadAction;
	}

	/**
	 * Simple getter for whether we are on a PC platform or not.
	 * 
	 * @return
	 */
	public boolean isPC() {
		return isPC;
	}

	/**
	 * Simple setter for whether we are on a PC platform or not.
	 * 
	 * @param pc
	 */
	public void setPC(boolean pc) {
		isPC = pc;
	}

	/**
	 * Adds the specified UndoableEdit to the UndoManager.
	 * 
	 * @param edit
	 */
	public void addUndoableEdit(UndoableEdit edit) {
		if (edit == null)
			return;
		undoManager.addEdit(edit);
	}

	/**
	 * Returns the UndoManager. Simple getter.
	 * 
	 * @return
	 */
	public UndoManager getUndoManager() {
		return undoManager;
	}

	/**
	 * Returns the FilterListManager. Simple getter.
	 * 
	 * @return
	 */
	public FilterListManager getFilterListManager() {
		return flManager;
	}

	/**
	 * Returns the index of the currently selected DemoCommandListEditor
	 * 
	 * @return
	 */
	public int getSelectedIndex() {
		return demoWindows.indexOf(desktopPane.getSelectedFrame());
	}

	/**
	 * Adds the default wizards. This is basically a place for me to
	 * programatically add wizards at compile-time instead of being restricted
	 * to using the configuration file for run-time loading.
	 */
	private void addDefaultWizards() {
		addWizard(new InterpolatorWizard());
		addWizard(new SmartCutWizard());
		addWizard(new CostumeImportExportWizard());
		addWizard(new RotationWizard());
		addWizard(new FixGhostsWizard());
		addWizard(new AutoPYRWizard());
		addWizard(new AutoPOSWizard());
		addWizard(new LargeFileFilterWizard());
		addWizard(new ExtractMovementPathWizard());
		addWizard(new CAMCoordinateWizard());
		addWizard(new CommandStrippingWizard());
		addWizard(new POSAnalysisWizard());
		addWizard(new DummyPlayerWizard());
		addWizard(new EmbeddedFeedbackWizard());
		addWizard(new VanityDemoWizard());
		String str = null;
		for (Iterator<String> i = wizardsToLoad.iterator(); i.hasNext();) {
			str = i.next();
			try {
				if (str != null) {
					addWizard(wizManager.loadDemoWizard(new File(str)));
				}
			} catch (Exception e) {
				i.remove();
			}
		}
	}

	/**
	 * Returns the ListModel that models the DemoCommandListEditors stored in
	 * this DemoEditor.
	 * 
	 * @return
	 */
	public ListModel getListModel() {
		return listModel;
	}

	/**
	 * Gets the number of DemoCommandListEditors currently in this DemoEditor.
	 * 
	 * @return
	 */
	public int getDemoCommandListEditorCount() {
		return demoWindows.size();
	}

	/**
	 * Returns the DemoCommandListEditor at the specified index.
	 * 
	 * @param index
	 * @return
	 */
	public DemoCommandListEditor getDemoCommandListEditor(int index) {
		if (index < 0 || index >= demoWindows.size()) {
			return null;
		}
		return demoWindows.get(index);
	}

	/**
	 * Loads the configuration using JAXB.
	 */
	@SuppressWarnings("unchecked")
	private void loadConfig() {
		try {
			jc = JAXBContext.newInstance("cohdemoeditor.config");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			config = ((JAXBElement<DemoEditorConfigType>) unmarshaller
					.unmarshal(new File(CONFIG_PATH))).getValue();
			Version version;
			try {
				version = Version.parseVersion(config.getVersion());
				if (version == null)
					version = Version.DEFAULT;
			} catch (Exception e) {
				version = Version.DEFAULT;
			}
			if (version.compareTo(new Version(0, 8, 4)) > 0) {
				int width = config.getWidth();
				int height = config.getHeight();
				setPreferredSize(new java.awt.Dimension(width, height));
			} else {
				setPreferredSize(new java.awt.Dimension(1000, 600));
			}
			int defaultX = config.getDefaultXPos();
			int defaultY = config.getDefaultYPos();
			setLocation(defaultX, defaultY);
			defaultPath = config.getDefaultDemoPath();
			isPC = config.isPc();
			List wizards = config.getWizard();
			if (wizards != null && wizards.size() > 0) {
				for (Object obj : wizards) {
					if (obj instanceof String) {
						wizardsToLoad.add((String) obj);
					}
				}
			}
			List filterLists = config.getFilterlists();
			if (filterLists != null && filterLists.size() > 0) {
				for (Object obj : filterLists) {
					flManager
							.addFilterList(parseConfigFilterList((Filterlist) obj));
				}
			}
			final Launcher launcher = config.getLauncher();
			if (launcher != null) {
				demoLauncher.setDisable2D(launcher.isDisable2D());
				demoLauncher.setFullscreen(launcher.isFullscreen());
				final Integer fps = launcher.getFps();
				if (fps != null) {
					demoLauncher.setDefaultFPS(fps);
				}
				final Integer xRes = launcher.getResolutionX();
				final Integer yRes = launcher.getResolutionY();
				if (xRes != null && yRes != null) {
					demoLauncher.setResolution(xRes, yRes);
				}
				final int mode = launcher.getMode();
				demoLauncher.setMode(mode);
			}
		} catch (JAXBException e) {
			Throwable t = e.getCause();
			if (t instanceof FileNotFoundException) {
				JOptionPane
						.showMessageDialog(
								this,
								"No config file found: using defaults.\nIf this is the first time you are running this program, this is normal.",
								"Config not found",
								JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this,
						"Error loading config file: using defaults", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			setLocation(0, 0);
			setPreferredSize(new java.awt.Dimension(1000, 600));
		}
	}

	/**
	 * Creates a FilterList from a Filterlist. Yes, that's a horrible naming
	 * convention. A FilterList (with a capital L) is used internally to
	 * represent a bunch of Filters. A Filterlist (lowercase l) is the
	 * JAXB-generated representation of a Filterlist object in the configuration
	 * file. So, this method takes the loaded &lt;filterlist&gt; node and
	 * creates a filterlist from it.
	 * 
	 * @param fl
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private FilterList parseConfigFilterList(Filterlist fl) {
		FilterList flist = new FilterList();
		flist.setName(fl.getName());
		List filters = fl.getFilter();
		for (Object obj1 : filters) {
			Filter f = (Filter) obj1;
			DemoCommandListFilter filter = new DemoCommandListFilter(f.isType());
			for (Object obj2 : f.getTimerange()) {
				Timerange tr = (Timerange) obj2;
				filter.addTimeRange(tr.getStarttime(), tr.getEndtime());
			}
			for (Object ref : f.getReference()) {
				filter.addReference((Integer) ref);
			}
			for (Object obj2 : f.getCommand()) {
				filter.addCommand((String) obj2);
			}
			for (Object obj2 : f.getArgument()) {
				filter.addArgument((String) obj2);
			}
			flist.add(filter);
		}
		return flist;
	}

	/**
	 * Saves the current settings to a configuration file.
	 */
	@SuppressWarnings("unchecked")
	private void saveConfig() {
		final ObjectFactory objFactory = new ObjectFactory();
		try {
			if (config == null) {
				config = new DemoEditorConfigType();
			}
			config.setVersion(VERSION);
			config.setDefaultXPos(getLocation().x);
			config.setDefaultYPos(getLocation().y);
			java.awt.Dimension size = getSize();
			config.setWidth(size.width);
			config.setHeight(size.height);
			config.setDefaultDemoPath(demoChooser.getCurrentDirectory()
					.getAbsolutePath());
			config.setDefaultCohPath(demoLauncher.getCoHPath()
					.getAbsolutePath());
			config.setPc(isPC);
			if (wizardsToLoad.size() > 0) {
				List wizards = config.getWizard();
				wizards.clear();
				wizards.addAll(wizardsToLoad);
			}
			final Launcher launcher = objFactory.createLauncher();
			launcher.setDisable2D(demoLauncher.isDisable2D());
			launcher.setFullscreen(demoLauncher.isFullscreen());
			launcher.setMode(demoLauncher.getMode());
			final int xRes = demoLauncher.getXRes();
			final int yRes = demoLauncher.getYRes();
			if (xRes > 0 && yRes > 0) {
				launcher.setResolutionX(xRes);
				launcher.setResolutionY(yRes);
			}
			final int fps = demoLauncher.getDefaultFPS();
			if (fps > 0) {
				launcher.setFps(fps);
			}
			config.setLauncher(launcher);
			if (flManager.getSize() > 0) {
				List flists = config.getFilterlists();
				flists.clear();
				for (FilterList flist : flManager) {
					Filterlist fl = objFactory.createFilterlist();
					fl.setName(flist.getName());
					List filters = fl.getFilter();
					for (DemoCommandListFilter filter : flist) {
						Filter f = objFactory.createFilter();
						f.setType(filter.doesFilterOut());
						List trs = f.getTimerange();
						trs.clear();
						for (DemoCommandListFilter.TimeRange tr : filter
								.getTimeRangeList()) {
							Timerange tr2 = objFactory.createTimerange();
							tr2.setStarttime(tr.start);
							tr2.setEndtime(tr.end);
							trs.add(tr2);
						}
						List refs = f.getReference();
						refs.clear();
						for (int ref : filter.getReferenceList()) {
							refs.add(ref);
						}
						List cmds = f.getCommand();
						cmds.clear();
						for (String cmd : filter.getCommandList()) {
							cmds.add(cmd);
						}
						List args = f.getArgument();
						args.clear();
						for (String arg : filter.getArgumentList()) {
							args.add(arg);
						}
						filters.add(f);
					}
					flists.add(fl);
				}
			}
			Marshaller marshaller = jc.createMarshaller();
			JAXBElement<DemoEditorConfigType> configElement = (new ObjectFactory())
					.createDemoEditorConfig(config);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					new Boolean(true));
			FileOutputStream out = new FileOutputStream(CONFIG_PATH);
			marshaller.marshal(configElement, out);
		} catch (JAXBException e) {
			JOptionPane.showMessageDialog(this, "Error writing config file",
					"Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Error writing config file",
					"Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Returns the DemoCommandComparator. Simple getter.
	 * 
	 * @return
	 */
	public DemoCommandComparator getComparator() {
		return comparator;
	}

	/**
	 * Returns the HelpBroker. Simple getter.
	 * 
	 * @return
	 */
	public HelpBroker getHelpBroker() {
		return helpBroker;
	}

	/**
	 * Returns the HelpSet. Simple getter.
	 * 
	 * @return
	 */
	public HelpSet getHelpSet() {
		return helpSet;
	}

	/**
	 * Returns the configuration loaded on program startup. Simple getter.
	 * 
	 * @return
	 */
	public DemoEditorConfigType getDemoEditorConfig() {
		return config;
	}

	/**
	 * Returns the file chooser used to save and load demos. This is primarily
	 * so wizards can save and load files. This method is a simple getter.
	 * 
	 * @return the JFileChooser used to save and load demos.
	 */
	public JFileChooser getDemoFileChooser() {
		return demoChooser;
	}

	/**
	 * Adds the specified wizard to the UI.
	 * 
	 * @param wizard
	 *            the wizard to add
	 */
	public void addWizard(final DemoWizard wizard) {
		wizManager.addDemoWizard(wizard);
		JMenuItem wizButton = new JMenuItem(wizard.getName());
		wizButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				wizard.runWizard(DemoEditor.this);
			}
		});
		wizardMenu.add(wizButton);
	}

	/**
	 * Adds the specified DemoCommandList to this editor.
	 * 
	 * @param cmdList
	 *            the DemoCommandList to add
	 * @return the DemoCommandListEditor created to hold the new addition
	 */
	public DemoCommandListEditor addDemo(final DemoCommandList cmdList) {
		final DemoCommandListEditor cmdEditor = new DemoCommandListEditor(
				cmdList);
		demoWindows.add(cmdEditor);
		desktopPane.add(cmdEditor);
		JInternalFrame prevSelection = desktopPane.getSelectedFrame();
		if (prevSelection != null && !prevSelection.isIcon()) {
			Point p = prevSelection.getLocation();
			p.translate(CASCADE_OFFSET, CASCADE_OFFSET);
			cmdEditor.setLocation(p);
		}
		cmdEditor.setVisible(true);
		cmdEditor.addInternalFrameListener(DemoEditor.this);
		cmdList.addDirtyBitListener(DemoEditor.this);
		desktopPane.setSelectedFrame(cmdEditor);
		addItemToWindowMenu(cmdEditor.getMenuItem());
		cmdEditor.getMenuItem().setSelected(true);
		cmdEditor.addUndoableEditListener(undoManager);
		cmdEditor.getListSelectionModel().addListSelectionListener(
				cmdSelectionListener);
		listModel.fireListeners(new ListDataEvent(listModel,
				ListDataEvent.INTERVAL_ADDED, demoWindows.size() - 1,
				demoWindows.size() - 1));
		refreshDirty();
		return cmdEditor;
	}

	/**
	 * Adds the specified menuItem to the windowMenu. If the
	 * noWindowsPlaceHolder is present, removes the placeholder.
	 * 
	 * @param menuItem
	 *            the menuItem to add. If null, the method does nothing.
	 */
	private void addItemToWindowMenu(JMenuItem menuItem) {
		if (menuItem == null)
			return;
		if (windowMenu.isMenuComponent(noWindowsPlaceHolder)) {
			windowMenu.remove(noWindowsPlaceHolder);
		}
		windowMenu.add(menuItem);
		windowButtonGroup.add(menuItem);
	}

	/**
	 * Removes the specified menuItem from the windowMenu. If the windowMenu
	 * would be empty, adds the default window item (noWindowsPlaceHolder) to
	 * the windowMenu.
	 * 
	 * @param menuItem
	 *            the menu item to remove. If null, the method does nothing.
	 */
	private void removeItemFromWindowMenu(JMenuItem menuItem) {
		if (menuItem == null)
			return;
		windowMenu.remove(menuItem);
		if (windowMenu.getItemCount() == 0)
			windowMenu.add(noWindowsPlaceHolder);
		windowButtonGroup.remove(menuItem);
	}

	/**
	 * Creates a new demo and adds it to this DemoEditor.
	 */
	public void newDemo() {
		DemoCommandList cmdList = new DemoCommandList();
		addDemo(cmdList);
	}

	/**
	 * Saves the specified DemoCommandListEditor. This prompts the user for a
	 * destination and calls the saveFile method in DemoCommandList if
	 * appropriate.
	 * 
	 * @param cmdListEditor
	 * @return
	 */
	public boolean saveDemo(DemoCommandListEditor cmdListEditor) {
		final DemoCommandList cmdList = cmdListEditor.getDemoCommandList();

		demoChooser.setSelectedFile(cmdList.getSaveFile());

		int returnVal = demoChooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = demoChooser.getSelectedFile();
			try {
				cmdList.saveFile(f);
			} catch (FileNotFoundException e) {
				showErrorMessage("Error finding the destination file.");
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				showErrorMessage("An I/O error has occured - could not save file.");
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Helper method that prompts the user to save the specified
	 * DemoCommandListEditor.
	 * 
	 * @param cmdListEditor
	 * @return
	 */
	private int promptToSave(DemoCommandListEditor cmdListEditor) {
		return JOptionPane.showConfirmDialog(this, "The demo \""
				+ cmdListEditor.getDemoCommandList().getName()
				+ "\" has been modified.  Do you want to save your changes?",
				"Question", JOptionPane.YES_NO_CANCEL_OPTION);
	}

	/**
	 * Helper method to call when the DemoEditor is closing. It iterates through
	 * the DemoCommandListEditors and prompts the user to save each one. If the
	 * user selects the CANCEL_OPTION at any point, the program cancels the
	 * exit. If not, this method calls saveConfig and sets the window close
	 * operation to EXIT.
	 */
	private void close() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		for (DemoCommandListEditor dcle : demoWindows) {
			DemoCommandList dcl = dcle.getDemoCommandList();
			if (dcl.isDirty()) {
				int returnVal = promptToSave(dcle);
				if (returnVal == JOptionPane.YES_OPTION) {
					if (!saveDemo(dcle)) {
						setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
						return;
					}
				}
				if (returnVal == JOptionPane.CANCEL_OPTION
						|| returnVal == JOptionPane.CLOSED_OPTION) {
					// cancel the program closing
					setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					return;
				}
			}
		}
		saveConfig();
	}

	/**
	 * Convenience method for showing an error message to the user using the
	 * static JOptionPane.showMessageDialog method.
	 * 
	 * Mostly here to save me some typing.
	 * 
	 * @param msg
	 *            the error message
	 */
	private void showErrorMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Action method. This method opens a JFileChooser to prompt the user for a
	 * file to open. If the user selects a file, it is forwarded to
	 * DemoCommandList.loadFile for the actual loading.
	 */
	public void loadDemo() {
		int returnVal = demoChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = demoChooser.getSelectedFile();
			DemoCommandList.loadFile(f, this);
		}
	}

	/**
	 * Listener method. The DemoEditor listens to each of its
	 * DemoCommandListEditors, and this method is fired whenever one of their
	 * dirty bits is changed. This method calls refreshDirty if the source is
	 * the currently selected demo.
	 */
	public void dirtyChanged(DirtyBitTracker source, boolean newValue) {
		if (source.equals(getSelectedDemo().getDemoCommandList())) {
			refreshDirty();
		}
	}

	/**
	 * Each DemoCommandListEditor keeps track of its own dirty bit. This method
	 * synchronizes the UI to the current editor's dirty bit.
	 */
	public void refreshDirty() {
		DemoCommandListEditor cmdEditor = getSelectedDemo();
		boolean dirty;
		if (cmdEditor == null) {
			dirty = false;
		} else
			dirty = cmdEditor.getDemoCommandList().isDirty();
		saveAction.setEnabled(dirty);
	}

	/**
	 * This convenience method returns the currently selected
	 * DemoCommandListEditor
	 * 
	 * @return
	 */
	public DemoCommandListEditor getSelectedDemo() {
		return (DemoCommandListEditor) desktopPane.getSelectedFrame();
	}

	/**
	 * This method is called whenever one of the internal DemoCommandListEditor
	 * windows has been activated. Generally, this will be when it gains focus
	 * from another internal window. This method updates the UI state to match.
	 */
	public void internalFrameActivated(InternalFrameEvent e) {
		DemoCommandListEditor dcle = (DemoCommandListEditor) e.getSource();
		dcle.getMenuItem().setSelected(true);
		refreshDirty();
		refreshCutCopy();
	}

	/**
	 * This method is called whenever one of the internal DemoCommandListEditor
	 * windows has actually closed. It updates the list of demoWindows, removes
	 * the item from the window menu, and refreshes the dirty bit.
	 */
	public void internalFrameClosed(InternalFrameEvent e) {
		final int index = demoWindows.indexOf(e.getSource());
		if (index != -1) {
			demoWindows.remove(index);
			listModel.fireListeners(new ListDataEvent(listModel,
					ListDataEvent.INTERVAL_REMOVED, index, index));
			removeItemFromWindowMenu(((DemoCommandListEditor) e.getSource())
					.getMenuItem());
			refreshDirty();
		}
	}

	/**
	 * This method is called whenever one of the internal DemoCommandListEditor
	 * windows is closing. It checks the dirty-bit and prompts for saving if
	 * necessary.
	 */
	public void internalFrameClosing(InternalFrameEvent e) {
		DemoCommandListEditor cmdEditor = (DemoCommandListEditor) e.getSource();
		DemoCommandList cmdList = cmdEditor.getDemoCommandList();
		int returnVal;
		if (cmdList.isDirty()) {
			returnVal = promptToSave(cmdEditor);
			if (returnVal == JOptionPane.YES_OPTION) {
				// save, then allow the closing to continue
				saveDemo(cmdEditor);
				cmdEditor
						.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
			}
			if (returnVal == JOptionPane.CANCEL_OPTION
					|| returnVal == JOptionPane.CLOSED_OPTION) {
				// cancel the frame closing
				cmdEditor
						.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
			}
			if (returnVal == JOptionPane.NO_OPTION) {
				// allow the closing to continue
				cmdEditor
						.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
			}
			assert false : "JOptionPane returned " + returnVal;
		}
	}

	/**
	 * Empty listener method.
	 */
	public void internalFrameDeactivated(InternalFrameEvent e) {

	}

	/**
	 * Empty listener method.
	 */
	public void internalFrameDeiconified(InternalFrameEvent e) {

	}

	/**
	 * Empty listener method.
	 */
	public void internalFrameIconified(InternalFrameEvent e) {

	}

	/**
	 * Empty listener method.
	 */
	public void internalFrameOpened(InternalFrameEvent e) {

	}

	// will be lazy-instantiated by the loadNewWizard method
	private JFileChooser wizardLoader = null;

	/**
	 * This method theoretically provides run-time loading of new wizards. It
	 * handles the UI portions of wizard loading but delegates the actual load
	 * to the wizard manager.
	 * 
	 * Locations of successfully loaded wizards are stored in the config file
	 * and will be automatically loaded on subsequent program starts.
	 * 
	 * In pratice, I expect anybody who writes new wizards will have direct
	 * access to the code and can just hardwire their wizard into the program.
	 */
	public void loadNewWizard() {
		if (wizardLoader == null) {
			wizardLoader = new JFileChooser(defaultPath);
			wizardLoader
					.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
						public boolean accept(File file) {
							return file.isDirectory()
									|| file.getName().endsWith(".demowizard");
						}

						public String getDescription() {
							return "Demo Editor Wizard Files (.demowizard)";
						}
					});
		}
		if (wizardLoader.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = wizardLoader.getSelectedFile();
			try {
				final DemoWizard wizard = wizManager.loadDemoWizard(f);
				if (wizard != null) {
					JMenuItem menuItem = new JMenuItem(wizard.getName());
					menuItem.addActionListener(new AbstractAction() {
						public void actionPerformed(ActionEvent ae) {
							wizard.runWizard(DemoEditor.this);
						}
					});
					wizardMenu.add(menuItem);
				}
			} catch (FileNotFoundException fnfe) {
				showErrorMessage("Could not find the file " + f.getName());
				return;
			} catch (IOException ioe) {
				showErrorMessage("I/O error openning " + f.getName());
				return;
			} catch (ClassFormatError cfe) {
				showErrorMessage("The file " + f.getName()
						+ " is not a valid Demo Wizard file.");
				return;
			} catch (InstantiationException ie) {
				showErrorMessage("The file " + f.getName()
						+ " is not a valid Demo Wizard file.");
				return;
			} catch (IllegalAccessException iae) {
				showErrorMessage("The file " + f.getName()
						+ " is not a valid Demo Wizard file.");
				return;
			} catch (LinkageError le) {
				showErrorMessage("Linkage Error: Either "
						+ f.getName()
						+ " has been improperly named or a wizard with the same name has already been loaded.");
				return;
			}
			if (!wizardsToLoad.contains(f.getAbsolutePath())) {
				wizardsToLoad.add(f.getAbsolutePath());
			}
		}
	}

	/**
	 * This ListModel models the list of internal demo windows represented by
	 * the demoWindows parameter. Note that the listeners must be fired manually
	 * whenever changes are made to the demoWindows list.
	 * 
	 * @author Darren Lee
	 * 
	 */
	private class DemoEditorListModel implements ListModel {
		public DemoEditorListModel() {
		}

		Set<ListDataListener> listeners;

		public void addListDataListener(ListDataListener l) {
			if (listeners == null)
				listeners = new HashSet<ListDataListener>();
			listeners.add(l);
		}

		public void removeListDataListener(ListDataListener l) {
			if (listeners == null)
				return;
			listeners.remove(l);
		}

		public DemoCommandListEditor getElementAt(int index) {
			return demoWindows.get(index);
		}

		public int getSize() {
			return demoWindows.size();
		}

		public void fireListeners(ListDataEvent e) {
			if (listeners == null)
				return;
			for (ListDataListener l : listeners) {
				int type = e.getType();
				if (type == ListDataEvent.CONTENTS_CHANGED)
					l.contentsChanged(e);
				if (type == ListDataEvent.INTERVAL_ADDED)
					l.intervalAdded(e);
				if (type == ListDataEvent.INTERVAL_REMOVED)
					l.intervalRemoved(e);
			}
		}
	}

	/**
	 * A standard UndoManager does not provide an easy way to determine when
	 * edits have been added, changed, etc. So, we create this subclass to
	 * update the status of the undoAction whenever a change is made to the
	 * UndoManager.
	 * 
	 * @author Darren Lee
	 * 
	 */
	private class DemoUndoManager extends UndoManager {
		public DemoUndoManager() {
			super();
		}

		public boolean addEdit(UndoableEdit edit) {
			boolean returnVal = super.addEdit(edit);
			updateUndoStatus();
			return returnVal;
		}

		private void updateUndoStatus() {
			if (undoAction.isEnabled() != undoManager.canUndo())
				undoAction.setEnabled(undoManager.canUndo());
			if (redoAction.isEnabled() != undoManager.canRedo())
				redoAction.setEnabled(undoManager.canRedo());
		}

		public void undo() {
			super.undo();
			updateUndoStatus();
		}

		public void redo() {
			super.redo();
			updateUndoStatus();
		}

		public void discardAllEdits() {
			super.discardAllEdits();
			updateUndoStatus();
		}

		public void end() {
			super.end();
			updateUndoStatus();
		}
	}

	/**
	 * This static inner class is used as a convenience for comparing program
	 * version information stored in the configuration file. It's essentially an
	 * ordered triplet representing the major, minor, and subminor version
	 * numbers.
	 * 
	 * @author Darren Lee
	 * 
	 */
	private static class Version implements Comparable<Version> {
		public int major, minor, subminor;
		public static final Version DEFAULT = new Version(0, 8, 4);

		public Version(int major, int minor, int subminor) {
			this.major = major;
			this.minor = minor;
			this.subminor = subminor;
		}

		public static Version parseVersion(String str) {
			String[] strarray = str.split("\\.");
			if (strarray.length != 3) {
				return null;
			}
			try {
				int major = Integer.parseInt(strarray[0]);
				int minor = Integer.parseInt(strarray[1]);
				int subminor = Integer.parseInt(strarray[2]);
				return new Version(major, minor, subminor);
			} catch (java.lang.NumberFormatException nfe) {
				return null;
			}
		}

		public int compareTo(Version v) {
			int compare = major - v.major;
			if (compare != 0)
				return compare;
			compare = minor - v.minor;
			if (compare != 0)
				return compare;
			compare = subminor - v.subminor;
			return compare;
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof Version))
				return false;
			Version v = (Version) obj;
			if (v.major == major && v.minor == minor && v.subminor == subminor)
				return true;
			return false;
		}
	}

	/**
	 * Entry point for running the CoH Demo Editor program. Creates and displays
	 * a new DemoEditor. If command line parameters are present, tries to load
	 * demos with those filenames. Also changes the look and feel to the system
	 * LaF.
	 * 
	 * @param args
	 *            demo files to load on start-up
	 */
	public static void main(final String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final DemoEditor editor = getEditor();
				editor.setVisible(true);
				for (String str : args) {
					final File f = new File(str);
					DemoCommandList.loadFile(f, editor);
				}
			}
		});
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		windowButtonGroup = new javax.swing.ButtonGroup();
		toobar = new javax.swing.JToolBar();
		newButton = new javax.swing.JButton();
		saveButton = new javax.swing.JButton();
		loadButton = new javax.swing.JButton();
		wizardButton = new javax.swing.JButton();
		launchButton = new javax.swing.JButton();
		scrollPane = new javax.swing.JScrollPane();
		desktopPane = new javax.swing.JDesktopPane();
		menubar = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		newMenuItem = new javax.swing.JMenuItem();
		saveMenuItem = new javax.swing.JMenuItem();
		loadMenuItem = new javax.swing.JMenuItem();
		jSeparator4 = new javax.swing.JSeparator();
		quitMenuItem = new javax.swing.JMenuItem();
		editMenu = new javax.swing.JMenu();
		undoMenuItem = new javax.swing.JMenuItem();
		redoMenuItem = new javax.swing.JMenuItem();
		jSeparator3 = new javax.swing.JSeparator();
		cutMenuItem = new javax.swing.JMenuItem();
		copyMenuItem = new javax.swing.JMenuItem();
		pasteMenuItem = new javax.swing.JMenuItem();
		wizardMenu = new javax.swing.JMenu();
		loadWizardMenuItem = new javax.swing.JMenuItem();
		jSeparator5 = new javax.swing.JSeparator();
		windowMenu = new javax.swing.JMenu();
		cascadeMenuItem = new javax.swing.JMenuItem();
		jSeparator2 = new javax.swing.JSeparator();
		noWindowsPlaceHolder = new javax.swing.JMenuItem();
		helpMenu = new javax.swing.JMenu();
		helpContentsMenuItem = new javax.swing.JMenuItem();
		helpSearchMenuItem = new javax.swing.JMenuItem();
		helpIndexMenuItem = new javax.swing.JMenuItem();
		jSeparator1 = new javax.swing.JSeparator();
		aboutButton = new javax.swing.JMenuItem();
		helpBroker.enableHelpOnButton(aboutButton, "about", helpSet);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("City of Heroes Demo Editor");
		toobar.setFloatable(false);
		newButton.setAction(newAction);
		toobar.add(newButton);

		saveButton.setAction(saveAction);
		toobar.add(saveButton);

		loadButton.setAction(loadAction);
		toobar.add(loadButton);

		wizardButton.setText("Wizards");
		wizardButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				wizardButtonActionPerformed(evt);
			}
		});

		toobar.add(wizardButton);

		launchButton.setText("Launch");
		if (!isPC)
			launchButton.setEnabled(false);

		launchButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				launchButtonActionPerformed(evt);
			}
		});

		toobar.add(launchButton);

		getContentPane().add(toobar, java.awt.BorderLayout.NORTH);

		scrollPane.setViewportView(desktopPane);

		getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);

		fileMenu.setMnemonic('F');
		fileMenu.setText("File");
		newMenuItem.setAction(newAction);
		newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_N,
				java.awt.event.InputEvent.CTRL_MASK));
		newMenuItem.setMnemonic('N');
		newMenuItem.setText("New Demo");
		fileMenu.add(newMenuItem);

		saveMenuItem.setAction(saveAction);
		saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_S,
				java.awt.event.InputEvent.CTRL_MASK));
		saveMenuItem.setMnemonic('S');
		saveMenuItem.setText("Save Demo");
		fileMenu.add(saveMenuItem);

		loadMenuItem.setAction(loadAction);
		loadMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_L,
				java.awt.event.InputEvent.CTRL_MASK));
		loadMenuItem.setMnemonic('L');
		loadMenuItem.setText("Load Demo");
		fileMenu.add(loadMenuItem);

		fileMenu.add(jSeparator4);

		quitMenuItem.setAction(quitAction);
		quitMenuItem.setMnemonic('Q');
		quitMenuItem.setText("Quit Editor");
		fileMenu.add(quitMenuItem);

		menubar.add(fileMenu);

		editMenu.setText("Edit");
		undoMenuItem.setAction(undoAction);
		undoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Z,
				java.awt.event.InputEvent.CTRL_MASK));
		undoMenuItem.setMnemonic('U');
		undoMenuItem.setText("Undo");
		editMenu.add(undoMenuItem);

		redoMenuItem.setAction(redoAction);
		redoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Y,
				java.awt.event.InputEvent.CTRL_MASK));
		redoMenuItem.setMnemonic('R');
		redoMenuItem.setText("Redo");
		editMenu.add(redoMenuItem);

		editMenu.add(jSeparator3);

		cutMenuItem.setAction(cutAction);
		cutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_X,
				java.awt.event.InputEvent.CTRL_MASK));
		cutMenuItem.setText("Cut");
		editMenu.add(cutMenuItem);

		copyMenuItem.setAction(copyAction);
		copyMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_C,
				java.awt.event.InputEvent.CTRL_MASK));
		copyMenuItem.setText("Copy");
		editMenu.add(copyMenuItem);

		pasteMenuItem.setAction(pasteAction);
		pasteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_V,
				java.awt.event.InputEvent.CTRL_MASK));
		pasteMenuItem.setText("Paste");
		editMenu.add(pasteMenuItem);

		menubar.add(editMenu);

		wizardMenu.setMnemonic('Z');
		wizardMenu.setText("Wizards");
		loadWizardMenuItem.setText("Load New Wizard");
		loadWizardMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						loadWizardMenuItemActionPerformed(evt);
					}
				});

		wizardMenu.add(loadWizardMenuItem);

		wizardMenu.add(jSeparator5);

		menubar.add(wizardMenu);

		windowMenu.setMnemonic('W');
		windowMenu.setText("Windows");
		cascadeMenuItem.setAction(cascadeAction);
		cascadeMenuItem.setMnemonic('C');
		cascadeMenuItem.setText("Cascade Windows");
		windowMenu.add(cascadeMenuItem);

		windowMenu.add(jSeparator2);

		noWindowsPlaceHolder.setText("(no windows)");
		noWindowsPlaceHolder.setEnabled(false);
		windowMenu.add(noWindowsPlaceHolder);

		menubar.add(windowMenu);

		helpMenu.setMnemonic('H');
		helpMenu.setText("Help");
		helpMenu.setEnabled(helpBroker != null);
		helpContentsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_F1, 0));
		helpContentsMenuItem.setText("Contents");
		helpContentsMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						helpContentsMenuItemActionPerformed(evt);
					}
				});

		helpMenu.add(helpContentsMenuItem);

		helpSearchMenuItem.setText("Search");
		helpSearchMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						helpSearchMenuItemActionPerformed(evt);
					}
				});

		helpMenu.add(helpSearchMenuItem);

		helpIndexMenuItem.setText("Index");
		helpIndexMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						helpIndexMenuItemActionPerformed(evt);
					}
				});

		helpMenu.add(helpIndexMenuItem);

		helpMenu.add(jSeparator1);

		aboutButton.setText("About");
		helpMenu.add(aboutButton);

		menubar.add(helpMenu);

		setJMenuBar(menubar);

		pack();
	}

	// </editor-fold>//GEN-END:initComponents

	private void helpIndexMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		// GEN-FIRST:event_helpIndexMenuItemActionPerformed
		helpBroker.setCurrentID("empty");
		helpBroker.setCurrentView("Index");
		try {
			helpBroker.setDisplayed(true);
		} catch (javax.help.UnsupportedOperationException uoe) {
			uoe.printStackTrace();
		}
	}// GEN-LAST:event_helpIndexMenuItemActionPerformed

	private void helpSearchMenuItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		// GEN-FIRST:event_helpSearchMenuItemActionPerformed
		helpBroker.setCurrentID("empty");
		helpBroker.setCurrentView("Search");
		try {
			helpBroker.setDisplayed(true);
		} catch (javax.help.UnsupportedOperationException uoe) {
			uoe.printStackTrace();
		}
	}// GEN-LAST:event_helpSearchMenuItemActionPerformed

	private void helpContentsMenuItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		// GEN-FIRST:event_helpContentsMenuItemActionPerformed
		helpBroker.setCurrentID("empty");
		helpBroker.setCurrentView("TOC");
		try {
			helpBroker.setDisplayed(true);
		} catch (javax.help.UnsupportedOperationException uoe) {
			uoe.printStackTrace();
		}
	}// GEN-LAST:event_helpContentsMenuItemActionPerformed

	private void launchButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// GEN-FIRST:event_launchButtonActionPerformed
		demoLauncher.setVisible(true);
	}// GEN-LAST:event_launchButtonActionPerformed

	private void wizardButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// GEN-FIRST:event_wizardButtonActionPerformed
		wizardChooser.setVisible(true);
	}// GEN-LAST:event_wizardButtonActionPerformed

	private void loadWizardMenuItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		// GEN-FIRST:event_loadWizardMenuItemActionPerformed
		loadNewWizard();
	}// GEN-LAST:event_loadWizardMenuItemActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JMenuItem aboutButton;
	private javax.swing.JMenuItem cascadeMenuItem;
	private javax.swing.JMenuItem copyMenuItem;
	private javax.swing.JMenuItem cutMenuItem;
	private javax.swing.JDesktopPane desktopPane;
	private javax.swing.JMenu editMenu;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JMenuItem helpContentsMenuItem;
	private javax.swing.JMenuItem helpIndexMenuItem;
	private javax.swing.JMenu helpMenu;
	private javax.swing.JMenuItem helpSearchMenuItem;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	private javax.swing.JSeparator jSeparator3;
	private javax.swing.JSeparator jSeparator4;
	private javax.swing.JSeparator jSeparator5;
	private javax.swing.JButton launchButton;
	private javax.swing.JButton loadButton;
	private javax.swing.JMenuItem loadMenuItem;
	private javax.swing.JMenuItem loadWizardMenuItem;
	private javax.swing.JMenuBar menubar;
	private javax.swing.JButton newButton;
	private javax.swing.JMenuItem newMenuItem;
	private javax.swing.JMenuItem noWindowsPlaceHolder;
	private javax.swing.JMenuItem pasteMenuItem;
	private javax.swing.JMenuItem quitMenuItem;
	private javax.swing.JMenuItem redoMenuItem;
	private javax.swing.JButton saveButton;
	private javax.swing.JMenuItem saveMenuItem;
	private javax.swing.JScrollPane scrollPane;
	private javax.swing.JToolBar toobar;
	private javax.swing.JMenuItem undoMenuItem;
	private javax.swing.ButtonGroup windowButtonGroup;
	private javax.swing.JMenu windowMenu;
	private javax.swing.JButton wizardButton;
	private javax.swing.JMenu wizardMenu;
	// End of variables declaration//GEN-END:variables

}
