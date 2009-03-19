/*
 * DemoReferenceList.java
 *
 * Created on June 20, 2005, 11:07 PM
 */

package cohdemoeditor;

import java.util.*;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

/**
 * A DemoReferenceList manages the DemoReference objects for a single
 * DemoCommandList. First, it provides lookup by reference number. Second, it is
 * a TableModel, so it can be displayed in a DemoReferenceListPanel.
 * 
 * Currently, the table model implementation is backed by a TreeMap, so lookup
 * by row number or DemoReference index is an O(n) operation. However, we expect
 * the total number of references to be small (<200 in most cases), so it
 * shouldn't be a big deal.
 * 
 * @author Darren Lee
 */
@SuppressWarnings("serial")
public class DemoReferenceList extends AbstractTableModel implements
		DemoReferenceListener, Iterable<DemoReference>, EventsEnabledListener {
	private TreeMap<Integer, DemoReference> references;

	/**
	 * Create a new DemoReferenceList
	 */
	public DemoReferenceList() {
		references = new TreeMap<Integer, DemoReference>();
	}

	/**
	 * Create a new DemoReferenceList containing all DemoReferences from the
	 * given list
	 * 
	 * @param list
	 *            the list containing all the reference with which this
	 *            DemoReferenceList will be initialized
	 */
	public DemoReferenceList(DemoReferenceList list) {
		references = new TreeMap<Integer, DemoReference>(list.references);
	}

	/**
	 * Removes all references from the list.
	 */
	public void clear() {
		references.clear();
		fireTableChanged(new javax.swing.event.TableModelEvent(
				DemoReferenceList.this));
	}

	/**
	 * Makes this DemoReferenceList into a copy of toCopy.
	 */
	public void copy(DemoReferenceList toCopy) {
		references.clear();
		for (DemoReference ref : toCopy.references.values()) {
			addDemoReference(ref);
		}
		fireTableChanged(new javax.swing.event.TableModelEvent(this));
	}

	/**
	 * Returns an interator over all contained DemoReferences
	 */
	public Iterator<DemoReference> iterator() {
		return references.values().iterator();
	}

	/**
	 * Adds a DemoReference
	 * 
	 * @param dr
	 *            the DemoReference to add
	 */
	private void addDemoReference(DemoReference dr) {
		references.put(dr.getReferenceNumber(), dr);
		dr.addDemoReferenceListener(this);
		fireTableChanged(new TableModelEvent(this));
	}

	/**
	 * Adds multiple DemoReferences
	 * 
	 * @param drl
	 *            a DemoReferenceList containing the DemoReferences to add
	 */
	public void addDemoReferences(DemoReferenceList drl) {
		for (DemoReference ref : drl)
			addDemoReference(ref);
	}

	/**
	 * Adds a DemoCommand. This method finds the correct DemoReference and adds
	 * the DemoCommand to that reference. If the reference is not found, a new
	 * DemoReference is created.
	 * 
	 * @param dc
	 *            the DemoCommand to add
	 * @return the DemoReference to which it was added
	 */
	public void addDemoCommand(DemoCommand dc, DemoCommandList list) {
		DemoReference reference = references.get(dc.getReference());
		if (reference == null) {
			reference = new DemoReference(dc);
			if (list != null) {
				list.addEventsEnabledListener(reference);
			}
			addDemoReference(reference);
		} else {
			reference.addCommand(dc);
		}
	}

	/**
	 * Removes a DemoReference
	 * 
	 * @param dr
	 *            the DemoReference to remove
	 */
	private void removeDemoReference(DemoReference dr) {
		if (!references.containsKey(dr.getReferenceNumber()))
			return;
		references.remove(dr.getReferenceNumber());
		dr.removeDemoReferenceListener(this);
		fireTableChanged(new TableModelEvent(this));
	}

	/**
	 * Removes a DemoCommand. This method finds the correct DemoReference and
	 * removes the DemoCommand from that reference. If the reference becomes
	 * empty, the reference is also removed.
	 * 
	 * @param dc
	 *            the DemoCommand to remove
	 */
	public void removeDemoCommand(DemoCommand dc, DemoCommandList list) {
		DemoReference reference = references.get(dc.getReference());
		if (reference == null)
			return;
		reference.removeCommand(dc);
		if (reference.getCommandCount() == 0) {
			removeDemoReference(reference);
			list.removeEventsEnabledListener(reference);
		}
	}

	/**
	 * Returns the number of DemoReferences in this DemoReferenceList
	 * 
	 * @return
	 */
	public int size() {
		return references.size();
	}

	/**
	 * Returns the DemoReference for the given reference number
	 * 
	 * @param refNum
	 *            a reference number
	 * @return the DemoReference with that number, or null if no such reference
	 *         is found
	 */
	public DemoReference getReferenceFor(int refNum) {
		return references.get(refNum);
	}

	/**
	 * Returns the reference number for the given row number (from the table
	 * model).
	 * 
	 * @param row
	 *            the row number
	 * @return the reference number of the DemoReference at that row
	 */
	public int getRefNumForRow(int row) {
		return getReferenceForRow(row).getReferenceNumber();
	}

	/**
	 * Returns the DemoReference for a given row number
	 * 
	 * @param row
	 *            the row number
	 * @return the DemoReference at that row
	 */
	public DemoReference getReferenceForRow(int row) {
		if (row < 0 || row >= references.size())
			throw new IndexOutOfBoundsException();
		Iterator<DemoReference> it = references.values().iterator();
		DemoReference reference = null;
		for (int i = 0; i < row + 1; i++) {
			reference = it.next();
		}
		if (reference == null)
			throw new NullPointerException("Null reference at row " + row);
		return reference;
	}

	/**
	 * Defines the contents of the table cells. The first column is the
	 * reference number with a " [P]" appended if that reference has a "Player"
	 * tag. The second column contains the names of the reference.
	 */
	public Object getValueAt(int row, int column) {
		if (row < 0 || row >= references.size())
			return null;
		Iterator<DemoReference> it = references.values().iterator();
		DemoReference reference = null;
		for (int i = 0; i < row + 1; i++) {
			reference = it.next();
		}
		if (reference == null)
			return null;
		if (column == 0) {
			String returnVal = DemoCommand.getIDFor(reference
					.getReferenceNumber());
			if (reference.getPlayerCount() > 0)
				returnVal += " [P]";
			return returnVal;
		}
		if (column == 1) {
			String[] names = reference.getNames();
			if (names.length == 0) {
				return DemoCommand.getIDFor(reference.getReferenceNumber());
			}
			String nameStr = "";
			for (String name : names) {
				nameStr += " " + name;
			}
			return nameStr.trim();
		}
		return null;
	}

	/**
	 * Finds an unused reference number. Useful for creating a new reference.
	 * 
	 * @return
	 */
	public int getUnusedReferenceNumber() {
		int n;
		for (n = 0; references.containsKey(n); n++)
			;
		return n;
	}

	/**
	 * The names of the columns.
	 */
	private static final String[] COLUMN_NAMES = { "Number", "Names" };

	/**
	 * Returns the names of the columns.
	 */
	public String getColumnName(int colnum) {
		if (colnum < 0 || colnum >= COLUMN_NAMES.length)
			return "";
		return COLUMN_NAMES[colnum];
	}

	/**
	 * There are two columns. Returns 2.
	 */
	public int getColumnCount() {
		return 2;
	}

	/**
	 * Returns the number of rows, which is equal to the number of references
	 */
	public int getRowCount() {
		return references.size();
	}

	/**
	 * Returns the row number for the given DemoReference. Returns -1 if the
	 * reference is not found.
	 * 
	 * @param ref
	 *            the reference to find
	 * @return the row number of the given reference, or -1 if the reference is
	 *         not found
	 */
	public int getRow(DemoReference ref) {
		if (ref == null)
			throw new NullPointerException(
					"null DemoReferences not allowed in a DemoReferenceList");
		if (!references.values().contains(ref))
			return -1;
		Iterator<DemoReference> it = references.values().iterator();
		int count;
		for (count = 0; it.hasNext() && !ref.equals(it.next()); count++)
			;
		return count;
	}

	/**
	 * Listener method called when one of the contained DemoReferences has a
	 * name change. Updates the table if the name has been changed and removes
	 * the DemoReference if it is empty.
	 */
	public void demoReferenceChanged(final DemoReference source,
			final boolean nameChanged) {
		if (nameChanged) {
			fireTableChanged(new javax.swing.event.TableModelEvent(this));
		}
		if (source.getCommandCount() == 0) {
			removeDemoReference(source);
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