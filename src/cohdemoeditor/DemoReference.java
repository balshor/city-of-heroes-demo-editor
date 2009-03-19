/*
 * DemoReference.java
 *
 * Created on June 20, 2005, 11:08 PM
 */

package cohdemoeditor;

import java.util.*;

/**
 * A DemoReference tracks all DemoCommands for a given reference number. Methods
 * should obtain locks on the commands, names, player, and listener objects. If
 * multiple locks are necessary, locks should be obtained in that order. Lock on
 * the listener list to r/w any of the listener properties.
 * 
 * @author Darren Lee
 */
public class DemoReference implements DemoCommandListener,
		EventsEnabledListener {
	private final int refNum;
	private List<DemoCommand> commands;
	private List<String> names;
	private Integer player = new Integer(0);

	/**
	 * Create a new DemoReference. The first command determines the reference
	 * number. It cannot be null.
	 * 
	 * @param firstCommand
	 *            the first command to insert into this DemoReference
	 */
	public DemoReference(DemoCommand firstCommand) {
		if (firstCommand == null)
			throw new IllegalArgumentException(
					"Cannot create a new DemoReference with a null first command.");
		commands = new LinkedList<DemoCommand>();
		names = new ArrayList<String>(1);
		refNum = firstCommand.getReference();
		addCommand(firstCommand);
	}

	/**
	 * Returns the commands for this reference as a new DemoCommandList.
	 * 
	 * @return
	 */
	public DemoCommandList getCommands() {
		DemoCommandList dcl = new DemoCommandList();
		for (DemoCommand dc : commands)
			dcl.addCommand(dc);
		return dcl;
	}

	/**
	 * Returns the number of players in this demo.
	 * 
	 * @return
	 */
	public int getPlayerCount() {
		return player;
	}

	/**
	 * Returns the reference number of this demo. This is an immutable quantity.
	 * 
	 * @return
	 */
	public final int getReferenceNumber() {
		return refNum;
	}

	/**
	 * Creates a shallow copy of this DemoReference.
	 */
	public DemoReference clone() {
		DemoReference clone = new DemoReference(commands.get(0));
		for (int i = 1; i < commands.size(); i++)
			clone.addCommand(commands.get(i));
		return clone;
	}

	public static final String NEW_CMD = "NEW";
	public static final String PLAYER_CMD = "Player";

	/**
	 * Adds a new DemoCommand to this DemoReference. The reference numbers must
	 * match.
	 * 
	 * @param cmd
	 */
	public void addCommand(DemoCommand cmd) {
		if (cmd == null) {
			throw new IllegalArgumentException(
					"Cannot add a null command to a DemoReference.");
		}
		if (cmd.getReference() != refNum) {
			throw new IllegalArgumentException(
					"Mismatched reference numbers: cannot add DemoCommand \""
							+ cmd + "\"to DemoReference " + refNum);
		}
		if (commands.contains(cmd))
			return;
		if (cmd.getReference() != refNum)
			throw new IllegalArgumentException(
					"Mismatched reference numbers: cannot add DemoCommand \""
							+ cmd + "\" to DemoReference " + refNum);
		commands.add(cmd);
		cmd.addListener(this);
		if (NEW_CMD.equals(cmd.getCommand()) && cmd.getArgumentCount() > 0) {
			names.add(cmd.getArgument(0));
			fireDemoReferenceListeners(true);
		} else {
			if (PLAYER_CMD.equals(cmd.getCommand())) {
				player++;

			}
			fireDemoReferenceListeners(false);
		}
	}

	/**
	 * Counts the number of player commands in this reference.
	 * 
	 * @return
	 */
	protected int countPlayerCommands() {
		int player = 0;
		for (Iterator<DemoCommand> it = commands.iterator(); it.hasNext();) {
			if (it.next().getCommand().equals(PLAYER_CMD))
				player++;
		}
		if (this.player != player) {
			this.player = player;
			fireDemoReferenceListeners(true);
		}
		return player;
	}

	/**
	 * Removes the given DemoCommand. Does nothing if the command is not found.
	 * 
	 * @param cmd
	 */
	public void removeCommand(DemoCommand cmd) {
		if (cmd == null)
			return;
		final boolean removed;
		removed = commands.remove(cmd);
		if (!removed)
			return;
		cmd.removeListener(this);
		if (removed && NEW_CMD.equals(cmd.getCommand())
				&& cmd.getArgumentCount() > 0) {
			names.remove(cmd.getArgument(0));
			fireDemoReferenceListeners(true);
		} else if (PLAYER_CMD.equals(cmd.getCommand())) {
			player--;
			fireDemoReferenceListeners(true);
		} else {
			fireDemoReferenceListeners(false);
		}
	}

	/**
	 * Returns the number of names (ie, NEW commands) are in this reference
	 * 
	 * @return
	 */
	public int getNameCount() {
		return names.size();
	}

	/**
	 * Returns an array containing the names of this reference
	 * 
	 * @return
	 */
	public String[] getNames() {
		String[] rtnArray = new String[names.size()];
		return names.toArray(rtnArray);
	}

	/**
	 * Returns the number of commands in this reference
	 * 
	 * @return
	 */
	public int getCommandCount() {
		return commands.size();
	}

	// listener variables -- obtain a lock on listeners before r/w any of these
	private List<DemoReferenceListener> listeners = new ArrayList<DemoReferenceListener>();
	private List<DemoReferenceListener> toAdd = null;
	private List<DemoReferenceListener> toRemove = null;
	private boolean listenersFiring = false;

	/**
	 * Add a new DemoReferenceListener
	 * 
	 * @param l
	 */
	public void addDemoReferenceListener(DemoReferenceListener l) {
		if (listenersFiring) {
			if (toAdd == null)
				toAdd = new ArrayList<DemoReferenceListener>(1);
			toAdd.add(l);
		} else {
			listeners.add(l);
		}
	}

	/**
	 * Remove a DemoReferenceListener
	 * 
	 * @param l
	 */
	public void removeDemoReferenceListener(DemoReferenceListener l) {
		if (listenersFiring) {
			if (toRemove == null)
				toRemove = new ArrayList<DemoReferenceListener>(1);
			toRemove.add(l);
		} else {
			listeners.remove(l);
		}
	}

	/**
	 * Fire DemoReferenceListeners. This indicates that the commands contained
	 * in this DemoReference have changed. If the names have additionally been
	 * changed, nameChanged should be true.
	 * 
	 * @param nameChanged
	 */
	protected void fireDemoReferenceListeners(final boolean nameChanged) {
		if (listeners.size() == 0)
			return;
		if (!eventsEnabled) {
			haveListenersFired = true;
			hasNameChanged |= nameChanged;
			return;
		}
		listenersFiring = true;
		for (DemoReferenceListener l : listeners)
			l.demoReferenceChanged(DemoReference.this, nameChanged);
		listenersFiring = false;
		if (toAdd != null && toAdd.size() > 0) {
			for (DemoReferenceListener l : toAdd)
				addDemoReferenceListener(l);
			toAdd.clear();
		}
		if (toRemove != null && toRemove.size() > 0) {
			for (DemoReferenceListener l : toRemove)
				removeDemoReferenceListener(l);
			toRemove.clear();
		}
	}

	/**
	 * Refreshes the names list.
	 */
	private void refreshNames() {
		names.clear();
		for (DemoCommand cmd : commands) {
			if (cmd.getReference() == refNum && cmd.getCommand().equals("NEW")
					&& cmd.getArgumentCount() > 0) {
				names.add(cmd.getArgument(0));
			}
		}
		fireDemoReferenceListeners(true);
	}

	/**
	 * Listener method. When a contained DemoCommand is changed, we need to
	 * check the following: (1) if the reference number changed, remove it (2)
	 * if the command column changed, refresh names and player count (3) if the
	 * arguments changed and we have a NEW command, refresh names
	 * 
	 * @param cmd
	 *            the source DemoCommand
	 * @param column
	 *            the column that was changed
	 */
	public void demoCommandChanged(DemoCommand cmd, int column, Object oldValue) {
		if (column == DemoCommandListFilter.REF_COL
				&& ((Integer) oldValue) == refNum
				&& cmd.getReference() != refNum) {
			removeCommand(cmd);
		} else if (column == DemoCommandListFilter.CMD_COL
				|| (column == DemoCommandListFilter.ARG_COL && cmd.getCommand()
						.equals("NEW"))) {
			refreshNames();
		} else if (column == DemoCommandListFilter.CMD_COL) {
			refreshNames();
			countPlayerCommands();
		}
	}

	private boolean eventsEnabled = true;
	private boolean haveListenersFired = false;
	private boolean hasNameChanged = false;

	/**
	 * Enables or disables whether events are fired. When events are re-enabled,
	 * fires an event if necessary.
	 */
	@Override
	public void eventsEnabledChanged(boolean areEventsEnabled) {
		if (eventsEnabled && !areEventsEnabled) {
			eventsEnabled = false;
			hasNameChanged = false;
		} else if (!eventsEnabled && areEventsEnabled) {
			eventsEnabled = true;
			if (haveListenersFired) {
				fireDemoReferenceListeners(hasNameChanged);
			}
		}
	}

}