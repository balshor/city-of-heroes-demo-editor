/*
 * DirtyBitListener.java
 *
 * Created on June 13, 2005, 4:53 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package cohdemoeditor;

/**
 * This interface defines an object that wishes to receive notification when the
 * dirty bit of a DirtyBitTracker is changed.
 * 
 * @author Darren Lee
 */
public interface DirtyBitListener {
	public void dirtyChanged(DirtyBitTracker source, boolean newValue);
}
