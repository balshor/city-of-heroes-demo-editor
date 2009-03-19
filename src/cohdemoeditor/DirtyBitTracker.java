/*
 * TracksDirtyBit.java
 *
 * Created on June 13, 2005, 4:52 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package cohdemoeditor;

/**
 * This interface defines an object that possesses a dirty bit. It provides
 * methods to set and get the dirty bit as well as methods to add and remove
 * listeners that wish to be informed when the dirty bit is changed.
 * 
 * @author Darren Lee
 */
public interface DirtyBitTracker {

	public void addDirtyBitListener(DirtyBitListener dbl);

	public void removeDirtyBitListener(DirtyBitListener dbl);

	public boolean isDirty();

	public void setDirty(boolean b);

}
