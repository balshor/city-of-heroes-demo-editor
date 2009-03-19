/*
 * DemoCommandList.java
 *
 * Created on June 11, 2005, 12:17 AM
 */

package cohdemoeditor;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.undo.*;

import cohdemoeditor.swing.DemoEditor;
import cohdemoeditor.swing.JCenteringDialog;
import cohdemoeditor.swing.ProgressMonitoringSwingWorker;

/**
 * A DemoCommandList is a list of DemoCommands and is the internal
 * representation of one demo file. It is also a TableModel for display
 * purposes, listens to its DemoCommands to fire TableChanged events, and tracks
 * the dirty bit for the demo.
 * 
 * A DemoCommandList not only contains the DemoCommands for a given demo, but it
 * also stores a DemoReferenceList and a FilterList. The DemoReferenceList is
 * used for easier access and display of the individual reference in the demo,
 * and the filter list is used to determine which commands are "visible" in the
 * table model. The visible commands are shallow-copied into a second list, and
 * the visible references (defined as having at least one visible command) are
 * copied to a second DemoReferenceList.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class DemoCommandList extends AbstractTableModel implements
		DemoCommandListener, DirtyBitTracker, EventsEnabledListener {

	private List<DemoCommand> commands;
	private DemoReferenceList references;
	private List<DemoCommand> visibleCmds;
	private DemoReferenceList visibleRefs;
	private FilterList currentFilters;

	private String name = null;
	private File saveFile = null;
	private final Set<SaveFileListener> saveFileListeners = new HashSet<SaveFileListener>();

	private boolean enableListeners = true;
	private boolean tableEventFired = false;
	private boolean dirtyBitFired = false;
	private boolean saveFileFired = false;
	private boolean satlFired = false;

	private boolean dirty;
	private boolean dirtyBitListenersFiring = false;
	private final Set<DirtyBitListener> dirtyBitListeners = new HashSet<DirtyBitListener>();
	private Set<DirtyBitListener> dblAdd = null;
	private Set<DirtyBitListener> dblRemove = null;

	private boolean showsAbsoluteTimes = true;
	private boolean sATLFiring = false;
	private final Set<ShowsAbsoluteTimesListener> showsAbsoluteTimesListeners = new HashSet<ShowsAbsoluteTimesListener>();
	private Set<ShowsAbsoluteTimesListener> toAddATL = null;
	private Set<ShowsAbsoluteTimesListener> toRemoveATL = null;

	private boolean eelFiring = false;
	private final Set<EventsEnabledListener> eventsEnabledListeners = new HashSet<EventsEnabledListener>();
	private Set<EventsEnabledListener> toAddEEL = null;
	private Set<EventsEnabledListener> toRemoveEEL = null;

	private static int cmdListCount = 0;

	/**
	 * Creates a new DemoCommandList
	 */
	public DemoCommandList() {
		commands = new ArrayList<DemoCommand>();
		references = new DemoReferenceList();
		visibleCmds = commands;
		visibleRefs = new DemoReferenceList(references);
		currentFilters = new FilterList();
		currentFilters.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent tme) {
				refilter();
			}
		});
		addEventsEnabledListener(currentFilters);
		addEventsEnabledListener(references);
		addEventsEnabledListener(visibleRefs);
		dirty = new Boolean(false);
	}

	/**
	 * Adds a <code>DemoCommand</code> to the end of this list.
	 * 
	 * @param dcmd
	 *            the <code>DemoCommand</code> to add
	 */
	public UndoableEdit addCommand(DemoCommand dcmd) {
		if (dcmd == null)
			throw new NullPointerException(
					"cannot add a null DemoCommand to a DemoCommandList");
		return addCommandHelper(commands.size(), visibleCmds.size(), dcmd);
	}

	/**
	 * Adds a <code>DemoCommand</code> at an index as specified in the visible
	 * list. The <code>DemoCommand</code> will be inserted just prior to the
	 * command previously at that index, and all subsequent commands will be
	 * moved one index down. If <code>visibleIndex</code> is larger than the
	 * number of visible commands then <code>dcmd</code> will be added as the
	 * last command.
	 * 
	 * @param visibleIndex
	 *            the index according to the visible commands at which to add
	 *            the <code>DemoCommand</code>
	 * @param dcmd
	 *            the <code>DemoCommand</code> to add
	 */
	public UndoableEdit addCommand(int visibleIndex, DemoCommand dcmd) {
		if (dcmd == null)
			throw new NullPointerException(
					"cannot add a null DemoCommand to a DemoCommandList");
		if (visibleIndex < 0) {
			return addCommandHelper(0, 0, dcmd);
		}
		int indexToAddAt;
		if (visibleIndex >= visibleCmds.size()) {
			return addCommand(dcmd);
		}
		indexToAddAt = commands.indexOf(visibleCmds.get(visibleIndex));
		return addCommandHelper(indexToAddAt, visibleIndex, dcmd);
	}

	/**
	 * Adds a command after the command at the specified visible index.
	 * 
	 * @param visibleIndex
	 * @param dcmd
	 * @return
	 */
	public UndoableEdit addCommandAfter(int visibleIndex, DemoCommand dcmd) {
		if (dcmd == null)
			throw new NullPointerException(
					"cannot add a null DemoCommand to a DemoCommandList");
		if (visibleIndex < 0) {
			return addCommandHelper(0, 0, dcmd);
		}
		int indexToAddAt;
		if (visibleIndex >= visibleCmds.size()) {
			return addCommand(dcmd);
		}
		indexToAddAt = commands.indexOf(visibleCmds.get(visibleIndex)) + 1;
		return addCommandHelper(indexToAddAt, visibleIndex + 1, dcmd);
	}

	/**
	 * Helper method to add a new command at the specified absolute and visible
	 * indices. Note that this method assumes that these indices are correctly
	 * computed by the caller.
	 * 
	 * @param absIndex
	 *            the absolute index for the addition
	 * @param visibleIndex
	 *            the visible index for the addition
	 * @param dcmd
	 *            the command to add
	 * @return
	 */
	private UndoableEdit addCommandHelper(final int absIndex,
			final int visibleIndex, final DemoCommand dcmd) {
		if (dcmd == null)
			throw new NullPointerException(
					"cannot add a null DemoCommand to a DemoCommandList");
		commands.add(absIndex, dcmd);
		dcmd.addListener(this);
		references.addDemoCommand(dcmd, this);
		if (currentFilters.isVisible(dcmd)) {
			if (commands != visibleCmds) {
				visibleCmds.add(visibleIndex, dcmd);
			}
			visibleRefs.addDemoCommand(dcmd, this);
		}
		addEventsEnabledListener(dcmd);
		fireTableRowsInserted(visibleIndex, visibleIndex);
		setDirty(true);

		UndoableEdit edit = new AbstractUndoableEdit() {
			final boolean wasDirty = isDirty();

			public void undo() {
				super.undo();
				commands.remove(absIndex);
				dcmd.removeListener(DemoCommandList.this);
				references.removeDemoCommand(dcmd, DemoCommandList.this);
				if (currentFilters.isVisible(dcmd)) {
					if (commands != visibleCmds) {
						visibleCmds.remove(visibleIndex);
					}
					visibleRefs.removeDemoCommand(dcmd, null);
					fireTableRowsDeleted(visibleIndex, visibleIndex);
				}
				removeEventsEnabledListener(dcmd);
				setDirty(wasDirty);
			}

			public void redo() {
				super.redo();
				commands.add(absIndex, dcmd);
				dcmd.addListener(DemoCommandList.this);
				references.addDemoCommand(dcmd, DemoCommandList.this);
				if (currentFilters.isVisible(dcmd)) {
					if (commands != visibleCmds) {
						visibleCmds.add(visibleIndex, dcmd);
					}
					visibleRefs.addDemoCommand(dcmd, DemoCommandList.this);
					fireTableRowsInserted(visibleIndex, visibleIndex);
				}
				addEventsEnabledListener(dcmd);
				setDirty(true);
			}
		};
		return edit;
	}

	/**
	 * Removes the command at the specified visible index.
	 * 
	 * @param visibleIndex
	 * @return
	 */
	public UndoableEdit removeCommand(int visibleIndex) {
		if (visibleIndex < 0 || visibleIndex >= visibleCmds.size())
			return null;
		return removeCommand(visibleCmds.get(visibleIndex));
	}

	/**
	 * Removes the specified DemoCommand.
	 * 
	 * @param dcmd
	 * @return
	 */
	public UndoableEdit removeCommand(final DemoCommand dcmd) {
		if (commands.indexOf(dcmd) == -1)
			return null;
		DemoCommandListEdit edit = new DemoCommandListEdit();
		if (visibleCmds.contains(dcmd)) {
			int index = visibleCmds.indexOf(dcmd);
			visibleCmds.remove(dcmd);
			visibleRefs.removeDemoCommand(dcmd, this);
			fireTableRowsDeleted(index, index);
		}
		final int index = commands.indexOf(dcmd);
		if (index != -1) {
			commands.remove(index);
			removeEventsEnabledListener(dcmd);
		}
		references.removeDemoCommand(dcmd, DemoCommandList.this);
		setDirty(true);
		edit.end();
		return edit;
	}

	/**
	 * Removes multiple demo commands.
	 * 
	 * @param dlist
	 * @return
	 */
	public UndoableEdit removeCommands(DemoCommandList dlist) {
		DemoCommandListEdit edit = new DemoCommandListEdit();
		for (DemoCommand dcmd : dlist.commands) {
			removeCommand(dcmd);
		}
		edit.end();
		return edit;
	}

	/**
	 * Removes all visible commands.
	 * 
	 * @return
	 */
	public UndoableEdit removeVisibleCommands() {
		DemoCommandListEdit edit = new DemoCommandListEdit();
		if (visibleCmds == commands) {
			commands.clear();
			references.clear();
		} else {
			/*
			 * Because the visible commands must be a sublist of the commands,
			 * we can use a more efficient algorithm
			 */
			final int numVisibleCmds = visibleCmds.size();
			for (int i = 0; i < numVisibleCmds; i++) {
				int j = 0;
				final DemoCommand visibleCommand = visibleCmds.get(i);
				while (true) {
					final DemoCommand cmd = commands.get(j);
					if (cmd.equals(visibleCommand)) {
						commands.remove(j);
						references.removeDemoCommand(cmd, DemoCommandList.this);
						removeEventsEnabledListener(cmd);
						break;
					}
					j++;
				}
			}
		}
		// The visible commands will be empty now.
		visibleCmds.clear();
		visibleRefs.clear();
		fireTableChanged(new TableModelEvent(this));
		setDirty(true);
		edit.end();
		return edit;
	}

	/**
	 * Returns the largest absolute time. Does not assume that the list is
	 * sorted, so this is a O(n) operation. If the list is sorted, just get the
	 * time of the last command in the list.
	 * 
	 * @return
	 */
	public int getLastTime() {
		int time = 0;
		for (DemoCommand cmd : commands) {
			if (cmd.getTime() > time)
				time = cmd.getTime();
		}
		return time;
	}

	/**
	 * Same as getLastTime, but for visible commands only
	 * 
	 * @return
	 */
	public int getLastVisibleTime() {
		int time = 0;
		for (DemoCommand cmd : visibleCmds) {
			if (cmd.getTime() > time)
				time = cmd.getTime();
		}
		return time;
	}

	/**
	 * Simple getter
	 * 
	 * @return
	 */
	public DemoReferenceList getDemoReferenceList() {
		return references;
	}

	/**
	 * Simple getter
	 * 
	 * @return
	 */
	public DemoReferenceList getVisibleDemoReferenceList() {
		return visibleRefs;
	}

	/**
	 * Moves a demo command up one row in the table. This places it one index
	 * prior to the index of the previous row in both the complete and the
	 * visible command lists.
	 * 
	 * @param visibleIndex
	 * @return
	 */
	public UndoableEdit moveDemoCommandUp(final int visibleIndex) {
		if (visibleIndex == 0)
			return null;
		DemoCommandListEdit edit = new DemoCommandListEdit();
		DemoCommand cmdToMoveBefore = visibleCmds.get(visibleIndex - 1);
		DemoCommand cmdToMove = visibleCmds.get(visibleIndex);
		int destIndex = commands.indexOf(cmdToMoveBefore);
		int sourceIndex = commands.indexOf(cmdToMove);
		if (destIndex < sourceIndex) {
			commands.remove(sourceIndex);
			commands.add(destIndex, cmdToMove);
		} else {
			commands.add(destIndex, cmdToMove);
			commands.remove(sourceIndex);
		}
		if (visibleCmds != commands) {
			visibleCmds.remove(cmdToMove);
			visibleCmds.add(visibleIndex - 1, cmdToMove);
		}
		edit.end();
		fireTableRowsUpdated(visibleIndex - 1, visibleIndex);
		setDirty(true);
		return edit;
	}

	/**
	 * Moves the specified row one row down. This places the demo command one
	 * index after the next visible command in both the complete and visible
	 * lists.
	 * 
	 * @param visibleIndex
	 * @return
	 */
	public UndoableEdit moveDemoCommandDown(int visibleIndex) {
		if (visibleIndex >= getVisibleCommandCount() - 1)
			return null;
		DemoCommandListEdit edit = new DemoCommandListEdit();
		DemoCommand cmdToMoveAfter = visibleCmds.get(visibleIndex + 1);
		DemoCommand cmdToMove = visibleCmds.get(visibleIndex);
		int destIndex = commands.indexOf(cmdToMoveAfter) + 1;
		int sourceIndex = commands.indexOf(cmdToMove);
		if (destIndex < sourceIndex) {
			commands.remove(sourceIndex);
			commands.add(destIndex, cmdToMove);
		} else {
			commands.add(destIndex, cmdToMove);
			commands.remove(sourceIndex);
		}
		if (visibleCmds != commands) {
			visibleCmds.remove(cmdToMove);
			visibleCmds.add(visibleIndex + 1, cmdToMove);
		}
		edit.end();
		fireTableRowsUpdated(visibleIndex, visibleIndex + 1);
		setDirty(true);
		return edit;
	}

	/**
	 * Gets the dirty bit
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Sets the dirty bit
	 */
	public void setDirty(boolean b) {
		if (dirty == b)
			return;
		dirty = b;
		fireDirtyBitListeners();
	}

	/**
	 * Adds a new dirty bit listener
	 */
	public void addDirtyBitListener(DirtyBitListener dbl) {
		if (dirtyBitListenersFiring) {
			if (dblAdd == null)
				dblAdd = new HashSet<DirtyBitListener>();
			dblAdd.add(dbl);
		} else {
			dirtyBitListeners.add(dbl);
		}
	}

	/**
	 * Removes the specified dirty bit listener
	 */
	public void removeDirtyBitListener(DirtyBitListener dbl) {
		if (dirtyBitListenersFiring) {
			if (dblRemove == null)
				dblRemove = new HashSet<DirtyBitListener>();
			dblRemove.add(dbl);
		} else {
			dirtyBitListeners.remove(dbl);
		}
	}

	/**
	 * Fires the dirty bit listeners. Firing listeners should always occur on
	 * the event thread so we can safely interact with non-thread-safe Swing
	 * classes.
	 */
	protected void fireDirtyBitListeners() {
		if (dirtyBitListeners.size() == 0)
			return;
		if (!enableListeners) {
			dirtyBitFired = true;
			return;
		}
		dirtyBitListenersFiring = true;
		for (DirtyBitListener dbl : dirtyBitListeners)
			dbl.dirtyChanged(DemoCommandList.this, isDirty());
		dirtyBitListenersFiring = false;
		if (dblAdd != null && dblAdd.size() > 0) {
			for (DirtyBitListener dbl : dblAdd)
				addDirtyBitListener(dbl);
			dblAdd.clear();
		}
		if (dblRemove != null && dblRemove.size() > 0) {
			for (DirtyBitListener dbl : dblRemove)
				removeDirtyBitListener(dbl);
			dblRemove.clear();
		}
	}

	/**
	 * Simple getter
	 * 
	 * @return
	 */
	public File getSaveFile() {
		return saveFile;
	}

	/**
	 * Simple setter
	 * 
	 * @param file
	 */
	public void setSaveFile(File file) {
		if ((saveFile == null && file == null) || saveFile != null
				&& saveFile.equals(file))
			return;
		saveFile = file;
		name = saveFile.getName();
		fireSaveFileListeners();
	}

	/**
	 * Adds a new save file listener
	 * 
	 * @param l
	 */
	public void addSaveFileListener(SaveFileListener l) {
		saveFileListeners.add(l);
	}

	/**
	 * Removes a save file listener
	 * 
	 * @param l
	 */
	public void removeSaveFileListener(SaveFileListener l) {
		saveFileListeners.remove(l);
	}

	/**
	 * Fires save file listeners. Note that due to the current listener and
	 * program design, save file listeners should not add or remove listeners in
	 * response to this method call. The saveFileListeners will fire on the
	 * event thread.
	 */
	protected void fireSaveFileListeners() {
		if (saveFileListeners.size() == 0)
			return;
		if (!enableListeners) {
			saveFileFired = true;
			return;
		}
		for (SaveFileListener l : saveFileListeners) {
			l.saveFileChanged(DemoCommandList.this);
		}
	}

	/**
	 * returns the name of this demo
	 * 
	 * @return
	 */
	public String getName() {
		if (name == null) {
			if (saveFile == null) {
				name = "Demo" + (++cmdListCount);
			} else {
				name = saveFile.getName();
			}
		}
		return name;
	}

	/**
	 * Returns the absolute time of the first visible command in the list.
	 * 
	 * @return the time of the first visible command in the list
	 */
	public int getFirstTime() {
		return visibleCmds.get(0).getTime();
	}

	/**
	 * Reorders the list to guarantee increasing chronological order of
	 * commands. The order of commands assigned to the same time for each
	 * reference will not be changed.
	 * 
	 * @return the UndoableEdit that can undo/redo this method
	 */
	public UndoableEdit resort() {
		UndoableEdit edit;
		final List<DemoCommand> oldCommands = new ArrayList<DemoCommand>(
				commands);

		Collections.sort(commands, DemoEditor.getEditor().getComparator());

		edit = new AbstractUndoableEdit() {
			private final boolean wasDirty = isDirty();
			private final List<DemoCommand> newCommands = new ArrayList<DemoCommand>(
					commands);

			public void undo() {
				commands.clear();
				commands.addAll(oldCommands);
				refilter();
				setDirty(wasDirty);
			}

			public void redo() {
				commands.clear();
				commands.addAll(newCommands);
				refilter();
				setDirty(true);
			}
		};

		setDirty(true);
		refilter();
		return edit;
	}

	/**
	 * Rebuilds the information on which commands are visible through the
	 * current filters. Does not change command ordering.
	 */
	public void refilter() {
		if (currentFilters.size() == 0) {
			visibleCmds = commands;
			visibleRefs.copy(references);
			fireTableChanged(new TableModelEvent(this));
			return;
		}
		if (visibleCmds != commands)
			visibleCmds.clear();
		else
			visibleCmds = new ArrayList<DemoCommand>();
		visibleRefs.clear();
		for (DemoCommand cmd : commands) {
			if (currentFilters.isVisible(cmd)) {
				visibleCmds.add(cmd);
				visibleRefs.addDemoCommand(cmd, this);
			}
		}
		fireTableChanged(new TableModelEvent(this));
	}

	/**
	 * Returns the filter list.
	 * 
	 * @return
	 */
	public FilterList getFilterList() {
		return currentFilters;
	}

	/**
	 * Returns the total number of objects referenced in the current list.
	 * 
	 * @return the total number of objects referenced in the current list
	 */
	public int getReferenceCount() {
		return references.size();
	}

	/**
	 * Returns the number of references visible in the current list.
	 * 
	 * @return the number of references visible in the current list
	 */
	public int getVisibleReferenceCount() {
		return visibleRefs.size();
	}

	/**
	 * Returns the total number of commands in the current list.
	 * 
	 * @return the total number of commands in the current list
	 */
	public int getCommandCount() {
		return commands.size();
	}

	/**
	 * Returns the number of visible commands
	 * 
	 * @return
	 */
	public int getVisibleCommandCount() {
		return visibleCmds.size();
	}

	/**
	 * Returns the first command visible through the given filter
	 * 
	 * @param filter
	 * @return
	 */
	public DemoCommand findFirstCommand(DemoCommandListFilter filter) {
		FilterList filters = new FilterList();
		filters.add(filter);
		return findFirstCommand(filters);
	}

	/**
	 * Returns the first command visible through the given filter list
	 * 
	 * @param filters
	 * @return
	 */
	public DemoCommand findFirstCommand(FilterList filters) {
		FilterList oldFilters = currentFilters.clone();
		currentFilters.clear();
		currentFilters.add(filters);
		refilter();
		DemoCommand rtn = null;
		if (getVisibleCommandCount() > 0)
			rtn = getVisibleCommand(0);
		currentFilters.clear();
		currentFilters.add(oldFilters);
		refilter();
		return rtn;
	}

	/**
	 * Finds the last command visible through the given filter
	 * 
	 * @param filter
	 * @return
	 */
	public DemoCommand findLastCommand(DemoCommandListFilter filter) {
		FilterList filters = new FilterList();
		filters.add(filter);
		return findLastCommand(filters);
	}

	/**
	 * Returns the last command visible through the given filter list
	 * 
	 * @param filters
	 * @return
	 */
	public DemoCommand findLastCommand(FilterList filters) {
		FilterList oldFilters = currentFilters.clone();
		currentFilters.clear();
		currentFilters.add(filters);
		refilter();
		DemoCommand rtn = null;
		if (getVisibleCommandCount() > 0)
			rtn = getVisibleCommand(getVisibleCommandCount() - 1);
		currentFilters.clear();
		currentFilters.add(oldFilters);
		refilter();
		return rtn;
	}

	/**
	 * Finds the last NEW command for the given reference that lies prior to the
	 * given endTime.
	 * 
	 * @param endTime
	 * @param refNum
	 * @return
	 */
	public DemoCommand findNEW(int endTime, int refNum) {
		DemoCommandListFilter filter = new DemoCommandListFilter(
				DemoCommandListFilter.SHOW_THESE);
		filter.addTimeRange(0, endTime);
		filter.addCommand("NEW");
		filter.addReference(refNum);
		return findLastCommand(filter);
	}

	/**
	 * Finds the next DEL command for the given reference that lies after the
	 * given start time.
	 * 
	 * @param startTime
	 * @param refNum
	 * @return
	 */
	public DemoCommand findDEL(int startTime, int refNum) {
		DemoCommandListFilter filter = new DemoCommandListFilter(
				DemoCommandListFilter.SHOW_THESE);
		filter.addTimeRange(startTime, getLastTime());
		filter.addCommand("DEL");
		filter.addReference(refNum);
		return findFirstCommand(filter);
	}

	/**
	 * Finds the previous FX command for the given reference and FX number that
	 * lies before the given destroy time.
	 * 
	 * @param destroyTime
	 * @param refNum
	 * @param FXNum
	 * @return
	 */
	public DemoCommand findFXCreate(int destroyTime, int refNum, int FXNum) {
		DemoCommandListFilter filter = new DemoCommandListFilter(
				DemoCommandListFilter.SHOW_THESE);
		filter.addTimeRange(0, destroyTime);
		filter.addCommand("FX");
		filter.addReference(refNum);
		filter.addArgument("" + FXNum);
		return findLastCommand(filter);
	}

	/**
	 * Finds the next FXDestroy command for the given reference and FX number
	 * that lies after the given create time
	 * 
	 * @param startTime
	 * @param refNum
	 * @param FXNum
	 * @return
	 */
	public DemoCommand findFXDestroy(int startTime, int refNum, int FXNum) {
		DemoCommandListFilter filter = new DemoCommandListFilter(
				DemoCommandListFilter.SHOW_THESE);
		filter.addTimeRange(startTime, getLastTime());
		filter.addCommand("FXDESTROY");
		filter.addReference(refNum);
		filter.addArgument("" + FXNum);
		return findFirstCommand(filter);
	}

	/**
	 * Returns the command at the given index
	 * 
	 * @param index
	 * @return
	 */
	public DemoCommand getCommand(int index) {
		return commands.get(index);
	}

	/**
	 * Returns the command at the given visible index
	 * 
	 * @param index
	 * @return
	 */
	public DemoCommand getVisibleCommand(int index) {
		if (index < 0 || index >= visibleCmds.size())
			throw new IndexOutOfBoundsException("index " + index
					+ " is out of bounds of the visible commands.  size = "
					+ visibleCmds.size());
		return visibleCmds.get(index);
	}

	/**
	 * Returns the (absolute) index of the given command
	 * 
	 * @param cmd
	 * @return
	 */
	public int indexOf(DemoCommand cmd) {
		return commands.indexOf(cmd);
	}

	/**
	 * Returns the visible index of the given command
	 * 
	 * @param cmd
	 * @return
	 */
	public int visibleIndexOf(DemoCommand cmd) {
		return visibleCmds.indexOf(cmd);
	}

	/**
	 * Returns a DemoCommandList containing all demo commands with the given
	 * reference number
	 * 
	 * @param refNum
	 * @return
	 */
	public DemoCommandList getDCByReference(int refNum) {
		DemoReference ref;
		ref = references.getReferenceFor(refNum);
		if (ref == null)
			return null;
		return ref.getCommands();
	}

	/**
	 * Similar to getDCByReference, except with multiple reference numbers
	 * 
	 * @param objs
	 * @return
	 */
	public DemoCommandList getDCByReferences(Set<Integer> objs) {
		DemoCommandList d;
		d = new DemoCommandList();
		for (DemoCommand dc : commands) {
			if (objs.contains(dc.getReference())) {
				d.addCommand(dc);
			}
		}
		return d;
	}

	/**
	 * Returns all demo commands with the given command
	 * 
	 * @param cmd
	 * @return
	 */
	public DemoCommandList getDCByCommand(String cmd) {
		DemoCommandList d = new DemoCommandList();
		for (DemoCommand dc : commands) {
			if (dc.getCommand().equals(cmd)) {
				d.addCommand(dc);
			}
		}
		return d;
	}

	/**
	 * Returns all demo commands with one of the given commands
	 */
	public DemoCommandList getDCByCommands(Set<String> cmds) {
		DemoCommandList d = new DemoCommandList();
		for (DemoCommand dc : commands) {
			if (cmds.contains(dc.getCommand())) {
				d.addCommand(dc);
			}
		}
		return d;
	}

	/**
	 * Exports the visible commands to a new DemoCommandList
	 * 
	 * @return
	 */
	public DemoCommandList exportVisible() {
		return extractByCurrentFilter();
	}

	/**
	 * Helper method that copies all visible commands to a new DemoCommandList
	 * 
	 * @return
	 */
	private DemoCommandList extractByCurrentFilter() {
		DemoCommandList d = new DemoCommandList();
		for (DemoCommand dcmd : visibleCmds)
			d.addCommand(dcmd.clone());
		return d;
	}

	/**
	 * Performs a "change all" on all visible commands with the specified oldID
	 * to the newID.
	 * 
	 * @param oldID
	 * @param newID
	 * @return
	 */
	public UndoableEdit changeRefId(int oldID, int newID) {
		CompoundEdit edit;
		if (visibleRefs.getReferenceFor(oldID) == null)
			return null;
		edit = new CompoundEdit();
		for (DemoCommand cmd : visibleCmds) {
			if (cmd.getReference() == oldID) {
				StateEdit sEdit = new StateEdit(cmd);
				cmd.setReference(newID);
				sEdit.end();
				edit.addEdit(sEdit);
			}
		}
		refilter();
		setDirty(true);
		edit.end();
		return edit;
	}

	/**
	 * Changes all visible commands to have the specified time.
	 * 
	 * @param newTime
	 * @return
	 */
	public UndoableEdit editVisibleTimes(int newTime) {
		CompoundEdit edit = new CompoundEdit();
		if (showsAbsoluteTimes) {
			for (DemoCommand dcmd : visibleCmds) {
				StateEdit sEdit = new StateEdit(dcmd);
				dcmd.setTime(newTime);
				sEdit.end();
				edit.addEdit(sEdit);
			}
		} else {
			for (DemoCommand dcmd : visibleCmds) {
				StateEdit sEdit = new StateEdit(dcmd);
				int index = commands.indexOf(dcmd);
				if (index == 0)
					dcmd.setTime(newTime);
				else
					dcmd.setTime(commands.get(index - 1).getTime() + newTime);
				sEdit.end();
				edit.addEdit(sEdit);
			}
		}
		edit.end();
		return edit;
	}

	/**
	 * Changes al visible commands to have the specified reference number
	 * 
	 * @param newRef
	 * @return
	 */
	public UndoableEdit editVisibleRefs(int newRef) {
		CompoundEdit edit = new CompoundEdit();
		for (DemoCommand dcmd : visibleCmds) {
			StateEdit sEdit = new StateEdit(dcmd);
			dcmd.setReference(newRef);
			sEdit.end();
			edit.addEdit(sEdit);
		}
		edit.end();
		return edit;
	}

	/**
	 * Changes all visible commands to have the specified command
	 * 
	 * @param newCmd
	 * @return
	 */
	public UndoableEdit editVisibleCmds(String newCmd) {
		if (newCmd == null)
			return null;
		CompoundEdit edit = new CompoundEdit();
		for (DemoCommand dcmd : visibleCmds) {
			StateEdit sEdit = new StateEdit(dcmd);
			dcmd.setCommand(newCmd);
			sEdit.end();
			edit.addEdit(sEdit);
		}
		edit.end();
		return edit;
	}

	/**
	 * Changes all visible commands to have the specified arguments
	 * 
	 * @param newArgs
	 * @return
	 */
	public UndoableEdit editVisibleArgs(String newArgs) {
		if (newArgs == null)
			return null;
		CompoundEdit edit = new CompoundEdit();
		for (DemoCommand dcmd : visibleCmds) {
			StateEdit sEdit = new StateEdit(dcmd);
			dcmd.setArguments(newArgs);
			sEdit.end();
			edit.addEdit(sEdit);
		}
		edit.end();
		return edit;
	}

	/**
	 * Changes all visible commands to change the argument with the specified
	 * index to the specified argument
	 * 
	 * @param argIndex
	 * @param newArg
	 * @return
	 */
	public UndoableEdit editVisibleArgs(int argIndex, String newArg) {
		if (newArg == null)
			return null;
		CompoundEdit edit = new CompoundEdit();
		for (DemoCommand dcmd : visibleCmds) {
			StateEdit sEdit = new StateEdit(dcmd);
			dcmd.setArgument(argIndex, newArg);
			sEdit.end();
			edit.addEdit(sEdit);
		}
		edit.end();
		return edit;
	}

	/**
	 * Adds the offset to the time of each visible command. Negative numbers
	 * subtract.
	 * 
	 * @param offset
	 *            the amount by which to offset each visible command's time
	 */
	public UndoableEdit offSetTimes(int offset) {
		boolean b = enableListeners;
		enableListeners = false;
		CompoundEdit edit = new CompoundEdit();
		for (DemoCommand cmd : visibleCmds) {
			StateEdit sEdit = new StateEdit(cmd);
			cmd.setTime(cmd.getTime() + offset);
			sEdit.end();
			edit.addEdit(sEdit);
		}
		edit.end();
		enableListeners = b;
		fireTableChanged(new TableModelEvent(this));
		return edit;
	}

	/**
	 * Same as offSetTimes, but not undoable.
	 * 
	 * @param offset
	 */
	public void offSetTimesPermanent(int offset) {
		boolean b = enableListeners;
		enableListeners = false;
		for (DemoCommand cmd : visibleCmds) {
			cmd.setTime(cmd.getTime() + offset);
		}
		enableListeners = b;
		fireTableChanged(new TableModelEvent(this));
	}

	/**
	 * Adds the offset to the position of all visible POS commands. Negative
	 * numbers subtract.
	 * 
	 * @param xoffset
	 * @param zoffset
	 * @param yoffset
	 * @return
	 */
	public UndoableEdit offSetPositions(double xoffset, double zoffset,
			double yoffset) {
		CompoundEdit edit = new CompoundEdit();
		for (DemoCommand cmd : visibleCmds) {
			if (cmd.getCommand().equals("POS") && cmd.getArgumentCount() >= 3) {
				try {
					StateEdit sEdit = new StateEdit(cmd);
					double x = Double.valueOf(cmd.getArgument(0));
					double z = Double.valueOf(cmd.getArgument(1));
					double y = Double.valueOf(cmd.getArgument(2));
					cmd.setArgument(0, "" + (x + xoffset));
					cmd.setArgument(1, "" + (z + zoffset));
					cmd.setArgument(2, "" + (y + yoffset));
					sEdit.end();
					edit.addEdit(sEdit);
				} catch (NumberFormatException e) {
					continue;
				}
			}
		}
		edit.end();
		return edit;
	}

	/**
	 * Rescales the times of all visible commands so that the first command
	 * occurs at <code>newFirstTime</code> and the last command occurs at
	 * <code>newLastTime</code>. The remaining commands have their times
	 * adjusted proportionally.
	 * 
	 * @param newFirstTime
	 *            the new time of the first command on the list
	 * @param newLastTime
	 *            the new time of the last command on the list
	 */
	public UndoableEdit scaleTimes(int newFirstTime, int newLastTime) {
		if (getVisibleCommandCount() == 0)
			return null;
		CompoundEdit edit = new CompoundEdit();
		int firstTime = getVisibleCommand(0).getTime();
		int lastTime = getVisibleCommand(getVisibleCommandCount() - 1)
				.getTime();
		double ratio = ((double) (newLastTime - newFirstTime))
				/ ((double) (lastTime - firstTime));
		UndoableEdit e;
		e = offSetTimes(newFirstTime - firstTime);
		if (e != null)
			edit.addEdit(e);
		if (lastTime != firstTime) {
			e = scaleTimes(ratio);
		}
		if (e != null)
			edit.addEdit(e);
		edit.end();
		return edit;
	}

	/**
	 * Rescales the times of all visible commands by the given ratio. The time
	 * of the first command is unchanged. All subsequent commands will be timed
	 * at
	 * <code>(time of first command) + ratio*(time of command - time of first command)</code>
	 * .
	 * 
	 * Note that due to rounding, the resulting times may be timed 1 ms earlier
	 * than expected.
	 * 
	 * @param ratio
	 *            the amount to scale the time of the visible commands
	 */
	public UndoableEdit scaleTimes(double ratio) {
		if (getVisibleCommandCount() == 0)
			return null;
		CompoundEdit edit;
		int firstTime = getVisibleCommand(0).getTime();
		edit = new CompoundEdit();
		for (DemoCommand cmd : visibleCmds) {
			double newTime = firstTime + ratio * (cmd.getTime() - firstTime);
			StateEdit sEdit = new StateEdit(cmd);
			cmd.setTime((int) Math.round(newTime));
			sEdit.end();
			edit.addEdit(sEdit);
		}
		edit.end();
		return edit;
	}

	/**
	 * Returns whether the demo command list is reporting absolute times or
	 * relative times
	 * 
	 * @return
	 */
	public boolean getShowsAbsoluteTimes() {
		return showsAbsoluteTimes;
	}

	/**
	 * Sets whether the demo command list should report absolute times or
	 * relative times
	 * 
	 * @param b
	 */
	public void setShowsAbsoluteTimes(boolean b) {
		if (showsAbsoluteTimes == b)
			return;
		showsAbsoluteTimes = b;
		fireTableDataChanged();
		fireShowsAbsoluteTimesListeners();
	}

	/**
	 * Adds a new ShowsAbsoluteTimesListener
	 * 
	 * @param satl
	 */
	public void addShowsAbsoluteTimesListener(ShowsAbsoluteTimesListener satl) {
		if (sATLFiring) {
			if (toAddATL == null)
				toAddATL = new HashSet<ShowsAbsoluteTimesListener>();
			toAddATL.add(satl);
		} else {
			showsAbsoluteTimesListeners.add(satl);
		}
	}

	/**
	 * Removes the specified ShowsAbsoluteTimesListener
	 * 
	 * @param satl
	 */
	public void removeShowsAbsoluteTimesListener(ShowsAbsoluteTimesListener satl) {
		if (sATLFiring) {
			if (toRemoveATL == null)
				toRemoveATL = new HashSet<ShowsAbsoluteTimesListener>();
			toRemoveATL.add(satl);
		} else {
			showsAbsoluteTimesListeners.remove(satl);
		}
	}

	/**
	 * Fires the ShowsAbsoluteTimesListeners on the event thread.
	 */
	protected void fireShowsAbsoluteTimesListeners() {
		if (showsAbsoluteTimesListeners.size() == 0)
			return;
		if (!enableListeners) {
			satlFired = true;
			return;
		}
		sATLFiring = true;
		for (ShowsAbsoluteTimesListener satl : showsAbsoluteTimesListeners)
			satl.showsAbsoluteTimesChanged(DemoCommandList.this,
					getShowsAbsoluteTimes());
		sATLFiring = false;
		if (toAddATL != null && toAddATL.size() > 0) {
			for (ShowsAbsoluteTimesListener satl : toAddATL)
				addShowsAbsoluteTimesListener(satl);
			toAddATL.clear();
		}
		if (toRemoveATL != null && toRemoveATL.size() > 0) {
			for (ShowsAbsoluteTimesListener satl : toRemoveATL)
				removeShowsAbsoluteTimesListener(satl);
			toRemoveATL.clear();
		}
	}

	/**
	 * This method will allow or block events from firing. It's useful for
	 * performing certain large operations on this DemoCommandList on another
	 * thread, as we won't get events firing on the wrong threads. When the
	 * TableModelEvents are re-enabled, an overall table update will be fired.
	 * 
	 * Disabling events on a DemoCommandList will also disable events on all
	 * filters, commands, and references that it contains. In particular,
	 * without events, the table will require manual refiltering when the filter
	 * list is modified.
	 * 
	 * @param eventsEnabled
	 */
	public void eventsEnabledChanged(boolean eventsEnabled) {
		if (!enableListeners && eventsEnabled) {
			enableListeners = true;
			if (tableEventFired)
				super.fireTableChanged(new TableModelEvent(this));
			if (dirtyBitFired)
				fireDirtyBitListeners();
			if (saveFileFired)
				fireSaveFileListeners();
			if (satlFired)
				fireShowsAbsoluteTimesListeners();
			fireEventsEnabledListeners();
		} else {
			enableListeners = eventsEnabled;
			tableEventFired = false;
			dirtyBitFired = false;
			saveFileFired = false;
			satlFired = false;
			fireEventsEnabledListeners();
		}
	}

	/**
	 * Adds a new dirty bit listener
	 */
	public void addEventsEnabledListener(EventsEnabledListener eel) {
		if (eelFiring) {
			if (toAddEEL == null)
				toAddEEL = new HashSet<EventsEnabledListener>();
			toAddEEL.add(eel);
		} else {
			eventsEnabledListeners.add(eel);
		}
	}

	/**
	 * Removes the specified dirty bit listener
	 */
	public void removeEventsEnabledListener(EventsEnabledListener eel) {
		if (eelFiring) {
			if (toRemoveEEL == null)
				toRemoveEEL = new HashSet<EventsEnabledListener>();
			toRemoveEEL.add(eel);
		} else {
			eventsEnabledListeners.remove(eel);
		}
	}

	/**
	 * Fires the dirty bit listeners. Firing listeners should always occur on
	 * the event thread so we can safely interact with non-thread-safe Swing
	 * classes.
	 */
	protected void fireEventsEnabledListeners() {
		if (eventsEnabledListeners.size() == 0)
			return;
		eelFiring = true;
		for (EventsEnabledListener eel : eventsEnabledListeners)
			eel.eventsEnabledChanged(enableListeners);
		dirtyBitListenersFiring = false;
		if (toAddEEL != null && toAddEEL.size() > 0) {
			for (EventsEnabledListener eel : toAddEEL)
				addEventsEnabledListener(eel);
			toAddEEL.clear();
		}
		if (toRemoveEEL != null && toRemoveEEL.size() > 0) {
			for (EventsEnabledListener eel : toRemoveEEL)
				removeEventsEnabledListener(eel);
			toRemoveEEL.clear();
		}
	}

	/**
	 * Overrides the AbstractTableModel method to allow us to enable or disable
	 * firing the listeners.
	 */
	@Override
	public void fireTableChanged(TableModelEvent e) {
		if (enableListeners) {
			super.fireTableChanged(e);
		} else {
			tableEventFired = true;
		}
	}

	/**
	 * TableModel method. Returns the name of each column in the table.
	 */
	@Override
	public String getColumnName(int column) {
		if (column == DemoCommandListFilter.TIME_COL)
			return showsAbsoluteTimes ? "Abs. Time" : "Rel. Time";
		return DemoCommandListFilter.COL_NAMES[column];
	}

	/**
	 * TableModel method. Returns the number of rows in the table.
	 */
	@Override
	public int getRowCount() {
		return visibleCmds.size();
	}

	/**
	 * TableModel method. Our table is editable.
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	/**
	 * TableModel method. Edits the cell at the specified index.
	 */
	@Override
	public void setValueAt(final Object obj, final int rowIndex,
			final int columnIndex) {
		final String str = (String) obj;
		final DemoCommand target = visibleCmds.get(rowIndex);
		switch (columnIndex) {
		case 0: // time
			target.setTime(str);
			break;
		case 1: // reference
			target.setReference(str);
			break;
		case 2: // command
			target.setCommand(str);
			break;
		case 3: // arguments
			target.setArguments(str);
			break;
		default:
			throw new RuntimeException(
					"How did I get here?  There should only be 4 columns in the table.");
		}
	}

	/**
	 * TableModel method. Returns the number of columns in the table.
	 */
	@Override
	public int getColumnCount() {
		return DemoCommandListFilter.NUM_COLS;
	}

	/**
	 * TableModel method. All cells are considered to be Strings.
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	/**
	 * TableModel method. Returns the value of the specified cell.
	 */
	@Override
	public Object getValueAt(int row, int column) {
		DemoCommand cmd;
		if (visibleCmds.size() == 0)
			return null;
		cmd = visibleCmds.get(row);
		if (column == DemoCommandListFilter.TIME_COL) {
			if (showsAbsoluteTimes || commands.indexOf(cmd) == 0)
				return cmd.getTime();
			return cmd.getTime()
					- commands.get(commands.indexOf(cmd) - 1).getTime();
		}
		if (column == DemoCommandListFilter.REF_COL) {
			return cmd.getReferenceString();
		}
		if (column == DemoCommandListFilter.CMD_COL)
			return cmd.getCommand();
		if (column == DemoCommandListFilter.ARG_COL)
			return cmd.getArguments();
		return null;
	}

	public static final int MIN_TOKENS = 3;

	/**
	 * Static method to load a new DemoCommandList, automatically adding it to
	 * the DemoEditor. Loads asynchronously on a SwingWorker thread.
	 * 
	 * @param file
	 * @param editor
	 */

	public static void loadFile(final File file, final DemoEditor editor) {
		ProgressMonitoringSwingWorker<DemoCommandList> worker = new ProgressMonitoringSwingWorker<DemoCommandList>(
				editor) {
			List<String> badlinesArray = new LinkedList<String>();

			@Override
			protected DemoCommandList doInBackground() throws Exception {
				final long length = file.length();
				long read = 0;
				DemoCommandList cmdList = new DemoCommandList();
				BufferedReader reader = new BufferedReader(new FileReader(file));
				int time = 0;
				for (String str = reader.readLine(); str != null; str = reader
						.readLine()) {
					read += str.length();
					if (str.trim().equals(""))
						continue;
					DemoCommand cmd = DemoCommand.parseDemoCommand(str);
					if (cmd != null) {
						time += cmd.getTime();
						cmd.setTime(time);
						cmdList.addCommand(cmd);
					} else {
						badlinesArray.add(str);
					}
					final int progress = (int) ((100 * read) / length);
					setProgress(progress);
				}
				reader.close();
				cmdList.setDirty(false);
				cmdList.setSaveFile(file);
				return cmdList;
			}

			@Override
			protected void done() {
				super.done();
				try {
					final DemoCommandList cmdList = get();
					if (badlinesArray.size() != 0) {
						final JDialog dialog = new JCenteringDialog(editor);
						dialog.setLayout(new BorderLayout());
						JTextArea lines = new JTextArea();
						for (String badline : badlinesArray) {
							lines.append(badline);
							lines.append("\n");
						}
						dialog.add(lines, BorderLayout.CENTER);
						dialog.add(new JLabel("Could not read the following "
								+ badlinesArray.size() + " lines:"),
								BorderLayout.NORTH);
						JPanel panel = new JPanel();
						panel.setLayout(new FlowLayout());
						Action action = new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) {
								dialog.setVisible(false);
								dialog.dispose();
							}
						};
						action.putValue(Action.NAME, "Okay");
						panel.add(new JButton(action));
						dialog.add(panel, BorderLayout.SOUTH);
						dialog.pack();
						dialog.setVisible(true);
					}
					editor.addDemo(cmdList);
				} catch (InterruptedException e) {
					JOptionPane.showMessageDialog(editor, "Loading cancelled",
							"Cancelled", JOptionPane.INFORMATION_MESSAGE);
				} catch (ExecutionException e) {
					Throwable throwable = e.getCause();
					if (throwable instanceof FileNotFoundException) {
						JOptionPane.showMessageDialog(editor,
								"Could not find the file " + file.getName(),
								"Error", JOptionPane.ERROR_MESSAGE);
					} else if (throwable instanceof IOException) {
						JOptionPane.showMessageDialog(editor,
								"Unknown I/O error", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else if (throwable instanceof OutOfMemoryError) {
						JOptionPane
								.showMessageDialog(
										editor,
										"JVM out of memory -- could not load file.\nYou may want to allocate more memory to the JVM and restart the program.",
										"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		};
		worker.execute();
	}

	/**
	 * Writes the contents of this DemoCommandList to the given file. This
	 * method saves synchronously; use saveFile(File, DemoEditor) to save
	 * asychronously.
	 * 
	 * @param file
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	public void saveFile(File file) throws FileNotFoundException, IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				file)));
		int time = 0;
		for (DemoCommand cmd : commands) {
			out.println(cmd.toString(time));
			time = cmd.getTime();
		}
		out.close();
		setDirty(false);
		setSaveFile(file);
	}

	/**
	 * Writes the contents of this DemoCommandList to the specified file, using
	 * a separate thread. Also scans for a Base line and warns that the save may
	 * be corrupted.
	 * 
	 * @param file
	 * @param editor
	 */

	public SwingWorker<Boolean, String> saveFile(final File file,
			final DemoEditor editor) {
		if (file.exists()) {
			int yesno = JOptionPane.showConfirmDialog(editor, file.getName()
					+ " already exists.  Overwrite?", "Overwrite file?",
					JOptionPane.YES_NO_OPTION);
			if (yesno == JOptionPane.NO_OPTION)
				return null;
		}

		ProgressMonitoringSwingWorker<Boolean> worker = new ProgressMonitoringSwingWorker<Boolean>(
				editor) {
			@Override
			protected Boolean doInBackground() throws Exception {
				boolean base = false;
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(file)));
				int time = 0;
				int lines = 0;
				final int totalLines = commands.size();
				for (DemoCommand cmd : commands) {
					out.println(cmd.toString(time));
					time = cmd.getTime();
					if (!base && cmd.getCommand().equals("Base"))
						base = true;
					setProgress((100 * ++lines) / totalLines);
				}
				out.close();
				setDirty(false);
				setSaveFile(file);
				return base;
			}

			@Override
			protected void done() {
				super.done();
				try {
					boolean base = get();
					if (base) {
						JOptionPane
								.showMessageDialog(
										editor,
										"Demo saved as "
												+ file.getName()
												+ ".  However, your base data may have been corrupted.\nWe recommend that you use a hex editor or similar program to copy the line \"0 0 Base ...\" from the original file.",
										"Save mostly successful",
										JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(editor, "Demo saved as "
								+ file.getName(), "Save successful",
								JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (InterruptedException e) {
					JOptionPane.showMessageDialog(editor, "Save cancelled.");
				} catch (ExecutionException e) {
					JOptionPane.showMessageDialog(editor,
							"There was an I/O error saving the file.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		};

		worker.execute();
		return worker;
	}

	/**
	 * Listener method for when a demo command changes.
	 */
	@Override
	public void demoCommandChanged(DemoCommand cmd, int column, Object oldValue) {
		boolean isVisible = visibleCmds.contains(cmd);
		if (column == DemoCommandListFilter.REF_COL) {
			// final DemoReference ref = references
			// .getReferenceFor((Integer) oldValue);
			// ref.removeCommand(cmd);
			references.addDemoCommand(cmd, this);
			if (isVisible) {
				// final DemoReference visibleRef = visibleRefs
				// .getReferenceFor((Integer) oldValue);
				// visibleRef.removeCommand(cmd);
				visibleRefs.addDemoCommand(cmd, this);
			}
		}
		if (isVisible) {
			fireTableCellUpdated(visibleCmds.indexOf(cmd), column);
		}
		setDirty(true);
	}

	/**
	 * This is a simple state-saving undoable edit.
	 * 
	 * @author Darren Lee
	 */
	private class DemoCommandListEdit extends AbstractUndoableEdit {
		boolean done = false;
		private ArrayList<DemoCommand> oldCommands;
		private DemoReferenceList oldReferences;
		private ArrayList<DemoCommand> oldVisibleCmds;
		private DemoReferenceList oldVisibleRefs;
		private ArrayList<DemoCommand> newCommands;
		private DemoReferenceList newReferences;
		private ArrayList<DemoCommand> newVisibleCmds;
		private DemoReferenceList newVisibleRefs;

		/**
		 * On creation, back up the current state
		 */
		public DemoCommandListEdit() {
			super();
			oldCommands = new ArrayList<DemoCommand>(commands);
			oldReferences = new DemoReferenceList(references);
			oldVisibleCmds = new ArrayList<DemoCommand>(visibleCmds);
			oldVisibleRefs = new DemoReferenceList(visibleRefs);
		}

		/**
		 * Back up the new state.
		 */
		public void end() {
			newCommands = new ArrayList<DemoCommand>(commands);
			newReferences = new DemoReferenceList(references);
			newVisibleCmds = new ArrayList<DemoCommand>(visibleCmds);
			newVisibleRefs = new DemoReferenceList(visibleRefs);
			done = true;
		}

		/**
		 * Restore the old state
		 */
		public void undo() {
			if (!done)
				throw new CannotUndoException();
			super.undo();
			for (DemoCommand dcmd : commands) {
				removeEventsEnabledListener(dcmd);
			}
			commands.clear();
			commands.addAll(oldCommands);
			for (DemoCommand dcmd : commands) {
				addEventsEnabledListener(dcmd);
			}
			for (DemoReference ref : references) {
				removeEventsEnabledListener(ref);
			}
			references.clear();
			references.addDemoReferences(oldReferences);
			for (DemoReference ref : references) {
				addEventsEnabledListener(ref);
			}
			if (visibleCmds != commands) {
				visibleCmds.clear();
				visibleCmds.addAll(oldVisibleCmds);
			}
			visibleRefs.clear();
			visibleRefs.addDemoReferences(oldVisibleRefs);
			fireTableChanged(new javax.swing.event.TableModelEvent(
					DemoCommandList.this));
		}

		/**
		 * Restore the new state
		 */
		public void redo() {
			if (!done)
				throw new CannotRedoException();
			super.redo();
			for (DemoCommand dcmd : commands) {
				removeEventsEnabledListener(dcmd);
			}
			commands.clear();
			commands.addAll(newCommands);
			for (DemoCommand dcmd : commands) {
				addEventsEnabledListener(dcmd);
			}
			for (DemoReference ref : references) {
				removeEventsEnabledListener(ref);
			}
			references.clear();
			references.addDemoReferences(newReferences);
			if (visibleCmds != commands) {
				visibleCmds.clear();
				visibleCmds.addAll(newVisibleCmds);
			}
			visibleRefs.clear();
			visibleRefs.addDemoReferences(newVisibleRefs);
			for (DemoReference ref : visibleRefs) {
				addEventsEnabledListener(ref);
			}
			fireTableChanged(new javax.swing.event.TableModelEvent(
					DemoCommandList.this));
		}
	}

}
