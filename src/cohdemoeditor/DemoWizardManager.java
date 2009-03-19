/*
 * DemoWizardManager.java
 *
 * Created on June 13, 2005, 12:43 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package cohdemoeditor;

import java.util.*;
import java.io.*;
import javax.swing.*;

import cohdemoeditor.swing.DemoEditor;

/**
 * A DemoWizardManager keeps track of the currently loaded wizards and provides
 * a method for loading additional wizards at run-time. It is an
 * AbstractListModel, so it is displayable in a JList or other similar
 * component.
 * 
 * Wizards are stored both indexed in the list and by name in a Map.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class DemoWizardManager extends AbstractListModel {

	private HashMap<String, DemoWizard> wizards;
	private ArrayList<String> wizardNames;
	private DemoEditor editor;
	private WizardClassLoader loader;

	/** Creates a new instance of DemoWizardManager */
	public DemoWizardManager(DemoEditor editor) {
		this.editor = editor;
		loader = new WizardClassLoader();
		wizards = new HashMap<String, DemoWizard>();
		wizardNames = new ArrayList<String>();
	}

	/**
	 * Returns the number of wizards currently stored.
	 */
	public int getSize() {
		return wizardNames.size();
	}

	/**
	 * Returns the name of the wizard at the given index. Used for display
	 * purposes.
	 */
	public Object getElementAt(int index) {
		return wizardNames.get(index);
	}

	/**
	 * Simple getter
	 * 
	 * @return
	 */
	public DemoEditor getEditor() {
		return editor;
	}

	/**
	 * Adds a new DemoWizard to this DemoWizardManager
	 * 
	 * @param wizard
	 */
	public void addDemoWizard(DemoWizard wizard) {
		if (wizard == null || wizards.containsValue(wizard))
			return;
		wizards.put(wizard.getName(), wizard);
		if (wizardNames.contains(wizard.getName()))
			return;
		wizardNames.add(wizard.getName());
		fireIntervalAdded(this, wizardNames.size() - 1, wizardNames.size() - 1);
	}

	/**
	 * Attempts to load a new wizard from the file. Delegates to the loader.
	 * 
	 * @param wizardFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassFormatError
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws LinkageError
	 */
	@SuppressWarnings("unchecked")
	public DemoWizard loadDemoWizard(File wizardFile)
			throws FileNotFoundException, IOException, ClassFormatError,
			InstantiationException, IllegalAccessException, LinkageError {
		Class wizardClass = loader.loadWizard(wizardFile);
		if (wizardClass == null)
			throw new ClassFormatError(
					"invalid class file, not a subclass of DemoWizard");
		DemoWizard wizard = (DemoWizard) wizardClass.newInstance();
		addDemoWizard(wizard);
		return wizard;
	}

	/**
	 * Returns an array of the names of the demo wizards.
	 * 
	 * @return
	 */
	public String[] getDemoWizardNames() {
		return (String[]) wizardNames.toArray();
	}

	/**
	 * Runs the wizard with the specified name.
	 * 
	 * @param name
	 */
	public void runWizard(String name) {
		DemoWizard wizard = wizards.get(name);
		if (wizard == null)
			return;
		wizards.get(name).runWizard(editor);
	}

	/**
	 * Retrieves the description of the wizard with the specified name.
	 * 
	 * @param name
	 * @return
	 */
	public String getDescriptionFor(String name) {
		DemoWizard wizard = wizards.get(name);
		if (wizard == null)
			return null;
		return wizard.getDescription();
	}

	/**
	 * This WizardClassLoader is used to load new Wizards at run-time. Its
	 * primary limitation is that the wizard must be contained in a single
	 * class.
	 * 
	 * @author Darren Lee
	 * 
	 */
	private static class WizardClassLoader extends ClassLoader {

		public WizardClassLoader() {
			super();
		}

		private static final String WIZARD_CLASS_NAME = "cohdemoeditor.DemoWizard";

		@SuppressWarnings("unchecked")
		public Class loadWizard(File file) throws FileNotFoundException,
				IOException, ClassFormatError, LinkageError {
			try {
				BufferedInputStream instream = new BufferedInputStream(
						new FileInputStream(file));
				byte[] b = new byte[instream.available()];
				int numBytesRead = instream.read(b);
				assert instream.available() == 0;
				Class loadedClass = defineClass(null, b, 0, numBytesRead);
				if (Class.forName(WIZARD_CLASS_NAME).isAssignableFrom(
						loadedClass)) {
					resolveClass(loadedClass);
					return loadedClass;
				}
			} catch (ClassNotFoundException cnfe) {
				assert false : "Could not find the class " + WIZARD_CLASS_NAME
						+ "??";
			}
			// indicates is a valid .class file but not a subclass of
			// DemoWizard.
			return null;
		}

	}

}
