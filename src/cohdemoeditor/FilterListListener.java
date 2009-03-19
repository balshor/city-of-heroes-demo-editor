/*
 * FilterListListener.java
 *
 * Created on June 21, 2005, 9:51 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package cohdemoeditor;

/**
 * This interface defines objects that wish to be notified when a FilterList has
 * its name changed.
 * 
 * @author Darren Lee
 */
public interface FilterListListener {
	/**
	 * This method will be called when the FilterList has its name changed
	 * 
	 * @param oldName
	 * @param newName
	 */
	public void filterListNameChanged(String oldName, String newName);
}
