/*
 * DemoCommandListener.java
 *
 * Created on June 11, 2005, 7:19 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package cohdemoeditor;

import java.util.*;

/**
 * This interface describes an object that wishes to receive events when a demo
 * command is changed.
 * 
 * @author Darren Lee
 */
public interface DemoCommandListener extends EventListener {
	public void demoCommandChanged(DemoCommand dc, int column, Object oldValue);
}
