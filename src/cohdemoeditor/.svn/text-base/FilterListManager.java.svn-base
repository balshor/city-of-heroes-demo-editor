/*
 * FilterListManager.java
 *
 * Created on June 21, 2005, 9:54 PM
 */

package cohdemoeditor;

import java.util.*;

import javax.swing.AbstractListModel;

/**
 * A FilterListManager stores and retrieves FilterLists for future use. It
 * maintains an indexed list of FilterLists (for display purposes, mostly) and a
 * Map of FilterLists by name (for retrieval purposes).
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class FilterListManager extends AbstractListModel implements
		FilterListListener, Iterable<FilterList> {

	private Map<String, FilterList> mapOfLists;
	private List<FilterList> listOfLists;

	/** Creates a new instance of FilterListManager */
	public FilterListManager() {
		mapOfLists = new HashMap<String, FilterList>();
		listOfLists = new ArrayList<FilterList>();
	}

	/**
	 * Add a new FilterList to this FilterListManager. Only a shallow copy into
	 * the manager is performed.
	 * 
	 * @param list
	 */
	public void addFilterList(FilterList list) {
		FilterList f = mapOfLists.put(list.getName(), list);
		if (f != null) {
			int index = listOfLists.indexOf(f);
			listOfLists.remove(index);
			fireIntervalRemoved(this, index, index);
		}
		listOfLists.add(list);
		list.addFilterListListener(this);
		fireIntervalAdded(this, listOfLists.size() - 1, listOfLists.size() - 1);
	}

	/**
	 * Removes a FilterList from this FilterListManager
	 * 
	 * @param list
	 */
	public void removeFilterList(FilterList list) {
		FilterList f = mapOfLists.remove(list.getName());
		list.removeFilterListListener(this);
		if (f != null) {
			int index = listOfLists.indexOf(f);
			listOfLists.remove(index);
			fireIntervalRemoved(this, index, index);
		}
	}

	/**
	 * Returns the FilterList with the given name.
	 * 
	 * @param name
	 * @return
	 */
	public FilterList getFilterList(String name) {
		return mapOfLists.get(name);
	}

	/**
	 * Returns the FilterList at the given index.
	 * 
	 * @param index
	 * @return
	 */
	public FilterList getFilterList(int index) {
		return listOfLists.get(index);
	}

	/**
	 * Returns an iterator over the FilterLists.
	 */
	public Iterator<FilterList> iterator() {
		return listOfLists.iterator();
	}

	/**
	 * Returns the number of FilterLists contained in this FilterListManager.
	 * 
	 * @return
	 */
	public int size() {
		return listOfLists.size();
	}

	/**
	 * Same as size(), required for ListModel interface
	 */
	public int getSize() {
		return size();
	}

	/**
	 * Returns the name of the FilterList at the given row number. Used for the
	 * ListModel interface and intended for display purposes.
	 */
	public Object getElementAt(int row) {
		return listOfLists.get(row).getName();
	}

	/**
	 * Listener method. When a FilterList has its name changed, it must be
	 * re-inserted into the map.
	 */
	public void filterListNameChanged(String oldName, String newName) {
		FilterList list = mapOfLists.get(oldName);
		if (list == null)
			return;
		removeFilterList(list);
		addFilterList(list);
	}
}
