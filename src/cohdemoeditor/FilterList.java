/*
 * FilterList.java
 *
 * Created on June 17, 2005, 5:38 PM
 *
 */

package cohdemoeditor;

import java.util.*;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

/**
 * A FilterList contains a List of DemoCommandListFilters and is intended to be
 * a logical grouping of filters that should be applied together. FilterLists
 * can be named, saved, and retrieved with a FilterListManager.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class FilterList extends AbstractTableModel implements Cloneable,
		Iterable<DemoCommandListFilter>, EventsEnabledListener {

	private final List<DemoCommandListFilter> filters = new ArrayList<DemoCommandListFilter>();;
	private String name = null;
	private static int filterListCount = 0;

	/**
	 * Returns an iterator over the filters. 
	 */
	public Iterator<DemoCommandListFilter> iterator() {
		return filters.iterator();
	}

	/**
	 * Getter for the list's name. If the list's name has not yet been set, this
	 * method autogenerates a name.
	 * 
	 * @return
	 */
	public String getName() {
		if (name == null)
			name = "FilterList" + filterListCount++;
		return name;
	}

	/**
	 * Setter for the list's name. Fires listeners when called.
	 * 
	 * @param newName
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		fireListeners(oldName, newName);
	}

	/**
	 * Returns true if the list is empty, false otherwise
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return filters.size() == 0;
	}

	/**
	 * Add a filter to this FilterList
	 * 
	 * @param filter
	 */
	public void add(DemoCommandListFilter filter) {
		if (filter == null)
			return;
		filters.add(filter);
		fireTableRowsInserted(filters.size() - 1, filters.size() - 1);
	}

	/**
	 * Add all filters in the given FilterList to this FilterList
	 * 
	 * @param filterList
	 */
	public void add(FilterList filterList) {
		if (filterList == null)
			return;
		this.filters.addAll(filterList.filters);
		fireTableRowsInserted(this.filters.size() - filterList.size() - 1,
				this.filters.size() - 1);
	}

	/**
	 * Clears the current filters and adds all filters in the given FilterList to this FilterList
	 * 
	 * @param filterList
	 */
	public void set(FilterList filterList) {
		if (filterList == null) {
			clear();
		}
		this.filters.clear();
		this.filters.addAll(filterList.filters);
		fireTableChanged(new TableModelEvent(this));
	}
	
	
	/**
	 * Remove the given Filter from this FilterList
	 * 
	 * @param filter
	 */
	public void remove(DemoCommandListFilter filter) {
		int index = filters.indexOf(filter);
		if (index != -1) {
			remove(index);
		}
	}

	/**
	 * Remove a filter by index
	 * 
	 * @param index
	 */
	public void remove(int index) {
		filters.remove(index);
		fireTableRowsDeleted(index, index);
	}

	/**
	 * Remove all filters from this filterlist
	 */
	public void clear() {
		filters.clear();
		fireTableChanged(new javax.swing.event.TableModelEvent(this));
	}

	/**
	 * Exports the filters in this list as an array
	 * 
	 * @return
	 */
	public DemoCommandListFilter[] exportFilters() {
		DemoCommandListFilter[] list = new DemoCommandListFilter[filters.size()];
		return filters.toArray(list);
	}

	/**
	 * Determines if the given command is visible or not under the current list
	 * of filters. To be visible, a command must be visible under each
	 * individual filter in the list.
	 * 
	 * @param cmd
	 * @return
	 */
	public boolean isVisible(DemoCommand cmd) {
		if (filters.size() == 0)
			return true;
		for (DemoCommandListFilter filter : filters) {
			if (!filter.isVisible(cmd))
				return false;
		}
		return true;
	}

	/**
	 * Returns the number of filters in this list.
	 * 
	 * @return
	 */
	public int size() {
		return filters.size();
	}

	/**
	 * A FilterList is a TableModel for display purposes. Each row is a filter,
	 * and the columns correspond to time, reference, command, argument, and
	 * type of filter (Hide vs Show).
	 */
	@Override
	public Object getValueAt(int row, int column) {
		DemoCommandListFilter f = filters.get(row);
		if (column == DemoCommandListFilter.TIME_COL)
			return f.getTimeRanges();
		if (column == DemoCommandListFilter.REF_COL)
			return f.getReferences();
		if (column == DemoCommandListFilter.CMD_COL)
			return f.getCommands();
		if (column == DemoCommandListFilter.ARG_COL)
			return f.getArguments();
		if (column == DemoCommandListFilter.NUM_COLS)
			return (f.doesFilterOut() ? "Hide" : "Show");
		return null;
	}

	/**
	 * Returns the number of rows. One for each filter.
	 */
	public int getRowCount() {
		return filters.size();
	}

	/**
	 * Returns the number of columns, five.
	 */
	public int getColumnCount() {
		return DemoCommandListFilter.NUM_COLS + 1;
	}

	/**
	 * Returns the name of each column.
	 */
	public String getColumnName(int column) {
		if (column >= DemoCommandListFilter.COL_NAMES.length) {
			return "Type";
		}
		return DemoCommandListFilter.COL_NAMES[column];
	}

	/**
	 * Clones the current FilterList. This is a deep copy as each individual
	 * filter is also cloned.
	 */
	public FilterList clone() {
		FilterList copy = new FilterList();
		for (DemoCommandListFilter f : filters)
			copy.filters.add(f.clone());
		return copy;
	}

	private boolean listenersFiring = false;
	private Set<FilterListListener> toRemove = null;
	private Set<FilterListListener> toAdd = null;
	private Set<FilterListListener> listeners = new HashSet<FilterListListener>();;

	/**
	 * Adds a new FilterListListener
	 * 
	 * @param l
	 */
	public void addFilterListListener(FilterListListener l) {
		if (listenersFiring) {
			if (toAdd == null) {
				toAdd = new HashSet<FilterListListener>();
			}
			toAdd.add(l);
		} else {
			listeners.add(l);
		}
	}

	/**
	 * Removes a FilterListListener
	 * 
	 * @param l
	 */
	public void removeFilterListListener(FilterListListener l) {

		if (listenersFiring) {
			if (toRemove == null) {
				toRemove = new HashSet<FilterListListener>();
			}
			toRemove.add(l);
		} else {
			listeners.remove(l);
		}
	}

	/**
	 * Fires the listeners to signify that the FilterList's name has changed.
	 * 
	 * @param oldName
	 * @param newName
	 */
	protected void fireListeners(final String oldName, final String newName) {

		if (listeners.size() == 0)
			return;
		listenersFiring = true;
		for (FilterListListener l : listeners) {
			l.filterListNameChanged(oldName, newName);
		}
		listenersFiring = false;
		if (toAdd != null && toAdd.size() > 0) {
			for (FilterListListener l : toAdd) {
				addFilterListListener(l);
			}
			toAdd.clear();
		}
		if (toRemove != null && toRemove.size() > 0) {
			for (FilterListListener l : toRemove) {
				removeFilterListListener(l);
			}
			toRemove.clear();
		}
	}

	private boolean eventsEnabled = true;
	private boolean tableChanged = false;

	/**
	 * Enables or disables events. If events are re-enabled, fires a table
	 * update if necessary.
	 */
	@Override
	public void eventsEnabledChanged(boolean areEventsEnabled) {
		if (eventsEnabled && !areEventsEnabled) {
			eventsEnabled = false;
			tableChanged = false;
		}
		if (!eventsEnabled && areEventsEnabled) {
			eventsEnabled = true;
			if (tableChanged) {
				fireTableChanged(new TableModelEvent(this));
			}
		}
	}

	/**
	 * If events are enabled, fires normally. Otherwise, notes that the table
	 * has changed in some way so a general table update can be fired when
	 * events are re-enabled.
	 */
	@Override
	public void fireTableChanged(TableModelEvent tme) {
		if (eventsEnabled) {
			super.fireTableChanged(tme);
		} else {
			tableChanged = true;
		}
	}

}
