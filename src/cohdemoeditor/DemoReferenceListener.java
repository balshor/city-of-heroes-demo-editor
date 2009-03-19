/*
 * DemoReferenceListener.java
 *
 * Created on June 20, 2005, 11:09 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package cohdemoeditor;

/**
 * This interface describes objects that wish to be informed when a
 * DemoReference has its name changed.
 * 
 * @author Darren Lee
 */
public interface DemoReferenceListener {
	public void demoReferenceChanged(DemoReference source, boolean nameChanged);
}
