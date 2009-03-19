/*
 * DemoCommand.java
 *
 * Created on June 10, 2005, 1:18 PM
 *
 * Note: this class has a natural ordering that is inconsistent with equals.
 */

package cohdemoeditor;

import java.util.*;

/**
 * A DemoCommand represents one line in a demo. Each line can be broken down
 * into four parts (in order): time, reference, command, and arguments. In the
 * saved demo files, the time is relative; a time of five indicates 5 ms have
 * passed between the previous command and this command. For clarity, we always
 * use absolute times in internal storage, so a time of five indicates that the
 * command occurs five ms after the start of the demo.
 * 
 * A reference is usually a number; however, there are at least three Strings
 * allowed as reference numbers: CAM, DYNGROUPS, and SKYFILE. These are assigned
 * negative numbers for internal purposes and automatically converted to and
 * from their String equivalents. We allow for the possibility of more
 * non-numeric reference numbers by automatically adding them to our conversion
 * list. This prevents updates to the demo file format from breaking the editor,
 * as happened when DYNGROUPS and SKYFILE references were added.
 * 
 * A DemoCommand is StateEditable, so we can easily manage undo/redo through the
 * javax.swing.undo package.
 * 
 * @author Darren Lee
 */
public class DemoCommand implements javax.swing.undo.StateEditable,
		EventsEnabledListener {

	public static final int TIME_DIGITS = 3;
	public static final int TARGET_DIGITS = 3;
	public static final int CAM_INDEX = -1;
	public static final String CAM_ID = "CAM";
	public static final int DYNGROUPS_INDEX = -2;
	public static final String DYNGROUPS_ID = "DYNGROUPS";
	public static final int SKYFILE_INDEX = -3;
	public static final String SKYFILE_ID = "SKYFILE";
	private static final ArrayList<String> objectStrings;

	private int time;
	private int reference;
	private String command;
	private final ArrayList<String> arguments;
	private int id = -1;

	private boolean listenersFiring = false;
	private Set<DemoCommandListener> toRemove = null;
	private Set<DemoCommandListener> toAdd = null;
	private Set<DemoCommandListener> listeners = new HashSet<DemoCommandListener>();
	private boolean eventsEnabled = true;
	private Object[] oldValues = new Object[DemoCommandListFilter.NUM_COLS];

	private static final List<String> commandOrder;
	private static final String[] commandOrderArray = { "Version", "Map",
			"Time", "SKY", "Player", "NEW", "NPC", "COSTUME", "PARTSNAME",
			"FX", "FXSCALE", "ORIGIN", "TARGET", "HPMAX", "HP", "Chat",
			"floatdmg", "float", "POS", "PYR", "MOV", "EntRagdoll",
			"FXDESTROY", "DEL" };
	
	/**
	 * Static initializer block. Initializes the default object strings for the
	 * CAM, DYNGROUPS, and SKYFILE references. Initializes the command order.
	 */
	static {
		objectStrings = new ArrayList<String>();
		objectStrings.add(0, "");
		objectStrings.add(-CAM_INDEX, CAM_ID);
		objectStrings.add(-DYNGROUPS_INDEX, DYNGROUPS_ID);
		objectStrings.add(-SKYFILE_INDEX, SKYFILE_ID);
		commandOrder = new ArrayList<String>(commandOrderArray.length);
		for(String str : commandOrderArray) commandOrder.add(str);
	}

	/** Creates a new instance of DemoCommand */
	public DemoCommand(int time, int objNum, String command, String args) {
		this.time = time;
		this.reference = objNum;
		this.command = command;
		arguments = new ArrayList<String>(3);
		setArguments(args);
	}

	/**
	 * If index < 0 and there is a definition for index, returns the definition.
	 * Otherwise, converts the index into a string.
	 */
	public static String getIDFor(int refNum) {
		if (refNum < 0 && -refNum < objectStrings.size()) {
			return objectStrings.get(-refNum);
		}
		return "" + refNum;
	}

	/**
	 * Determines if the given String corresponds to a reference number. If the
	 * String can be parsed to a non-negative integer, returns true. If the
	 * String is in the list of object strings, returns true. Otherwise, returns
	 * false.
	 * 
	 * @param referenceName
	 * @return
	 */
	public static boolean hasRefNumFor(String referenceName) {
		try {
			int integer = Integer.valueOf(referenceName);
			return integer >= 0;
		} catch (NumberFormatException nfe) {
			final int objSize = objectStrings.size();
			for (int i = 0; i < objSize; i++) {
				if (objectStrings.get(i).equals(referenceName))
					return true;
			}
			return false;
		}
	}

	/**
	 * First, if Integer.valueOf(referenceName) returns an integer, that integer
	 * will be returned. Otherwise, if an index has previously been created for
	 * the referenceName, returns the index. Otherwise, defines a new index
	 * number as the given referenceName and returns the new index.
	 * 
	 * Note that is the referenceName is parsed to a negative integer, the
	 * negative integer will be returned. However, this is not a legal reference
	 * number in the demo file format.
	 */
	public static int getRefNumFor(String referenceName) {
		try {
			int index = Integer.valueOf(referenceName);
			return index;
		} catch (NumberFormatException nfe) {
			final int objSize = objectStrings.size();
			for (int i = 0; i < objSize; i++) {
				if (objectStrings.get(i).equals(referenceName))
					return -i;
			}
			objectStrings.add(objSize, referenceName);
			return -objSize;
		}
	}

	public static final int MIN_TOKENS = 3;

	/**
	 * Parses a single line to a DemoCommand. The different tokens are divided
	 * by spaces or tabs (for import from a spreadsheet).
	 * 
	 * @param str
	 * @return
	 */
	public static DemoCommand parseDemoCommand(String str) {
		String[] tokens = str.split("[ \t]+");
		if (tokens.length < MIN_TOKENS)
			return null;
		int time, reference;
		try {
			time = Integer.valueOf(tokens[0]);
		} catch (NumberFormatException nfe) {
			return null;
		}
		try {
			if (CAM_ID.equals(tokens[1]))
				reference = CAM_INDEX;
			else if (DYNGROUPS_ID.equals(tokens[1]))
				reference = DYNGROUPS_INDEX;
			else if (SKYFILE_ID.equals(tokens[1]))
				reference = SKYFILE_INDEX;
			else
				reference = Integer.valueOf(tokens[1]);
		} catch (NumberFormatException nfe) {
			reference = getRefNumFor(tokens[1]);
		}
		String command = tokens[2];
		String args = "";
		for (int i = 3; i < tokens.length; i++)
			args += " " + tokens[i];
		args = args.trim();
		return new DemoCommand(time, reference, command, args);
	}

	/**
	 * Creates a copy of this DemoCommand.
	 */
	public DemoCommand clone() {
		DemoCommand copy = new DemoCommand(time, reference, command, null);
		copy.arguments.addAll(arguments);
		return copy;
	}

	/**
	 * Simple getter for time.
	 * 
	 * @return
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Setter for time.
	 * 
	 * @param time
	 */
	public void setTime(int time) {
		if (this.time == time)
			return;
		final int oldTime = this.time;
		this.time = time;
		fireListeners(DemoCommandListFilter.TIME_COL, oldTime);
	}

	/**
	 * Setter for time, where the input is parsed to an Integer.
	 * 
	 * @param s
	 * @return true if successful, false if there was a NumberFormatException
	 */
	public boolean setTime(String s) {
		int time;
		try {
			time = Integer.valueOf(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		setTime(time);
		return true;
	}

	/**
	 * Simple getter for reference. Returns the numerical reference, so negative
	 * numbers should be retranslated elsewhere.
	 * 
	 * @return
	 */
	public int getReference() {
		return reference;
	}

	/**
	 * Sets the reference to the given number.
	 * 
	 * @param objNum
	 */
	public void setReference(int objNum) {
		if (this.reference == objNum)
			return;
		final int oldRef = this.reference;
		this.reference = objNum;
		fireListeners(DemoCommandListFilter.REF_COL, oldRef);
	}

	/**
	 * Sets the reference to the given string.
	 * 
	 * @param s
	 * @return
	 */
	public boolean setReference(String s) {
		setReference(getRefNumFor(s.trim()));
		return true;
	}

	/**
	 * Returns the String representation of the reference.
	 * 
	 * @return
	 */
	public String getReferenceString() {
		if (reference == CAM_INDEX)
			return CAM_ID;
		if (reference == DYNGROUPS_INDEX)
			return DYNGROUPS_ID;
		if (reference == SKYFILE_INDEX)
			return SKYFILE_ID;
		return DemoCommand.getIDFor(reference);
	}

	/**
	 * Returns the command.
	 * 
	 * @return
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Sets the command.
	 * 
	 * @param command
	 */
	public void setCommand(String command) {
		if (this.command.equals(command))
			return;
		final String oldCmd = this.command;
		this.command = command;
		fireListeners(DemoCommandListFilter.CMD_COL, oldCmd);
	}

	/**
	 * Returns the number of arguments.
	 * 
	 * @return
	 */
	public int getArgumentCount() {
		return arguments.size();
	}

	/**
	 * Determines if this DemoCommand contains the given argument
	 * 
	 * @param arg
	 * @return
	 */
	public boolean hasArg(String arg) {
		return arguments.contains(arg);
	}

	/**
	 * Returns the argument at the given index. Returns an empty string if no
	 * argument exists at that index.
	 * 
	 * @param index
	 * @return
	 */
	public String getArgument(int index) {
		if (index < arguments.size())
			return arguments.get(index);
		return "";
	}

	/**
	 * Gets a String representation of all of the arguments.
	 * 
	 * @return
	 */
	public String getArguments() {
		if (arguments.size() == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for (String arg : arguments) {
			if (arg == null) {
				sb.append("\"\"");
			} else if (arg.contains(" ")) {
				sb.append(" \"").append(arg).append("\"");
			} else {
				sb.append(" ").append(arg);
			}
		}
		return sb.toString().trim();
	}

	/**
	 * Sets the argument at the given index to the given argument.
	 * 
	 * @param index
	 * @param arg
	 */
	public void setArgument(int index, String arg) {
		if (arg == null || arg.equals(getArgument(index)))
			return;
		final String oldArgs = getArguments();
		while (arguments.size() < index + 1) {
			arguments.add("");
		}
		arguments.set(index, arg.trim());
		fireListeners(DemoCommandListFilter.ARG_COL, oldArgs);
	}

	/**
	 * Sets the arguments to the given string.
	 * 
	 * @param args
	 */
	public void setArguments(String args) {
		if (args == null || args.equals(getArguments()))
			return;
		final String oldArgs = getArguments();
		arguments.clear();
		if (args != null) {
			String[] strarray = args.split("\"");
			for (int i = 0; i < strarray.length; i++) {
				if (strarray[i] == "")
					arguments.add("");
				if ((i % 2 == 0)) {
					String[] strarray2 = strarray[i].trim().split(" ");
					for (String str : strarray2) {
						if (!str.trim().equals(""))
							arguments.add(str.trim());
					}
				} else {
					if (!strarray[i].trim().equals(""))
						arguments.add(strarray[i].trim());
				}
			}
		}
		fireListeners(DemoCommandListFilter.ARG_COL, oldArgs);
	}

	/**
	 * Returns a String representation of the DemoCommand as it would be written
	 * to a demo file. The parameter currenttime is used to indicate the time of
	 * the previous demo command.
	 * 
	 * @param currenttime
	 * @return
	 */
	public String toString(int currenttime) {
		String timeStr = "" + (time - currenttime);
		while (timeStr.length() < TIME_DIGITS)
			timeStr += " ";
		String objStr;
		if (CAM_INDEX == reference)
			objStr = CAM_ID;
		else if (DYNGROUPS_INDEX == reference)
			objStr = DYNGROUPS_ID;
		else if (SKYFILE_INDEX == reference)
			objStr = SKYFILE_ID;
		else
			objStr = "" + reference;
		while (objStr.length() < TARGET_DIGITS)
			objStr += " ";
		String str = timeStr + " " + objStr + " " + command;
		if (arguments.size() > 0) {
			str += " " + getArguments();
		}
		return str;
	}

	/**
	 * Equivalent to toString(0)
	 */
	public String toString() {
		return toString(0);
	}

	/**
	 * Adds a new listener to this DemoCommand.
	 * 
	 * @param l
	 */
	public void addListener(DemoCommandListener l) {
		if (listenersFiring) {
			if (toAdd == null) {
				toAdd = new HashSet<DemoCommandListener>();
			}
			toAdd.add(l);
		} else {
			listeners.add(l);
		}
	}

	/**
	 * Removes the specified listener from this DemoCommand.
	 * 
	 * @param l
	 */
	public void removeListener(DemoCommandListener l) {
		if (listenersFiring) {
			if (toRemove == null) {
				toRemove = new HashSet<DemoCommandListener>();
			}
			toRemove.add(l);
		} else {
			listeners.remove(l);
		}
	}

	/**
	 * Fires the listeners.
	 * 
	 * @param column
	 *            the column that was changed
	 */
	protected void fireListeners(final int column, final Object oldValue) {
		if (listeners.size() == 0)
			return;
		if (!eventsEnabled) {
			if (oldValues[column] == null)
				oldValues[column] = oldValue;
			return;
		}
		listenersFiring = true;
		for (DemoCommandListener l : listeners) {
			l.demoCommandChanged(DemoCommand.this, column, oldValue);
		}
		listenersFiring = false;
		if (toAdd != null && toAdd.size() > 0) {
			for (DemoCommandListener l : toAdd) {
				addListener(l);
			}
			toAdd.clear();
		}
		if (toRemove != null && toRemove.size() > 0) {
			for (DemoCommandListener l : toRemove) {
				removeListener(l);
			}
			toRemove.clear();
		}
	}

	/**
	 * Enables or disables events. When events are re-enabled, fires updates on
	 * all columns that have changed.
	 */
	@Override
	public void eventsEnabledChanged(boolean areEventsEnabled) {
		if (areEventsEnabled && !eventsEnabled) {
			eventsEnabled = true;
			for (int i = 0; i < DemoCommandListFilter.NUM_COLS; i++) {
				if (oldValues[i] != null)
					fireListeners(i, oldValues[i]);
			}
		} else if (!areEventsEnabled && eventsEnabled) {
			eventsEnabled = false;
			for (int i = 0; i < DemoCommandListFilter.NUM_COLS; i++) {
				oldValues[i] = null;
			}
		}
	}

	/**
	 * Implements StateEditable.restoreState to restore the state of this
	 * DemoCommand from the given hashtable.
	 */
	@Override
	public void restoreState(Hashtable<?, ?> state) {
		try {
			Integer time = (Integer) state.get("time");
			if (time != null) {
				final int oldTime = this.time;
				this.time = time;
				fireListeners(DemoCommandListFilter.TIME_COL, oldTime);
			}
		} catch (ClassCastException e) {
			;
		}
		try {
			Integer ref = (Integer) state.get("ref");
			if (ref != null) {
				final int oldRef = this.reference;
				this.reference = ref;
				fireListeners(DemoCommandListFilter.REF_COL, oldRef);
			}
		} catch (ClassCastException e) {
			;
		}
		try {
			String cmd = (String) state.get("cmd");
			if (cmd != null) {
				final String oldCmd = this.command;
				this.command = cmd;
				fireListeners(DemoCommandListFilter.CMD_COL, oldCmd);
			}
		} catch (ClassCastException e) {
			;
		}
		try {
			String args = (String) state.get("args");
			if (args != null) {
				final String oldArgs = getArguments();
				setArguments(args);
				fireListeners(DemoCommandListFilter.ARG_COL, oldArgs);
			}
		} catch (ClassCastException e) {
			;
		}
	}

	/**
	 * Implements StateEditable.storeState to store this DemoCommand in the
	 * given hashtable
	 */
	public void storeState(Hashtable<Object, Object> state) {
		state.put("time", time);
		state.put("ref", reference);
		state.put("cmd", command);
		state.put("args", getArguments());
	}

	/**
	 * Simple setter for id. Id numbers are the fourth sort criteria: first is
	 * by time, second by reference, third by command, fourth by id, and fifth
	 * by argument.
	 * 
	 * @param id
	 *            the new id to set
	 */
	public final void setId(int id) {
		this.id = id;
	}

	/**
	 * Simple getter for id.
	 */
	public final int getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for(String arg : arguments) {
			result = prime * result + arg.hashCode();
		}
		result = prime * result + ((command == null) ? 0 : command.hashCode());
		result = prime * result + id;
		result = prime * result + reference;
		result = prime * result + time;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DemoCommand other = (DemoCommand) obj;
		if (time != other.time)
			return false;
		if (reference != other.reference)
			return false;
		if (command == null) {
			if (other.command != null)
				return false;
		} else if (!command.equals(other.command))
			return false;
		if (id != other.id)
			return false;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (other.arguments == null) {
			return false;
		} else {
			if (arguments.size() != other.arguments.size()) {
				return false;
			}
			final int size = arguments.size();
			for (int i = 0; i < size; i++) {
				final String thisarg = arguments.get(i);
				final String otherarg = other.arguments.get(i);
				if ((thisarg == null && otherarg != null)
						|| (thisarg != null && otherarg == null)) {
					return false;
				}
				if (!thisarg.equals(otherarg)) {
					return false;
				}
			}
		}
		return true;
	}

}