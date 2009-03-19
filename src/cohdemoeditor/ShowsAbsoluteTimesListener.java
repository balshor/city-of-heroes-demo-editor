/*
 * ShowsAbsoluteTimeListener.java
 *
 * Created on June 14, 2005, 11:55 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package cohdemoeditor;

/**
 * Interface for objects that want notifications of when the time mode of a
 * <code>DemoCommandList</code> changes between absolute and relative.
 * 
 * @author Darren Lee
 */
public interface ShowsAbsoluteTimesListener {
	/**
	 * This method is called whenever the <code>showsAbsoluteTimesChanged</code>
	 * flag is changed on the object listened to.
	 * 
	 * @param source
	 *            The object that called this method.
	 * @param newValue
	 *            The new value of the <code>showsAbsoluteTimesChanged</code>
	 *            flag for <code>source</code>.
	 */
	public void showsAbsoluteTimesChanged(DemoCommandList source,
			boolean newValue);
}
