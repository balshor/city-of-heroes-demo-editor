/*
 * DemoWizard.java
 *
 * Created on June 17, 2005, 12:49 PM
 *
 */

package cohdemoeditor;

import cohdemoeditor.swing.DemoEditor;

/**
 * A small program to automate some task in a <code>DemoEditor</code>.
 * @author Balshor
 */
public abstract class DemoWizard implements java.lang.Comparable<DemoWizard> {

    /**
     * Runs the <code>DemoWizard</code>.
     * @param source the <code>DemoEditor</code> that is running this wizard
     */
    public abstract void runWizard(DemoEditor source);
    
    /**
     * Returns the name of this <code>DemoWizard</code>.
     * @return the name of this <code>DemoWizard</code>
     */
    public abstract String getName();
    
    /**
     * Calls <code>getName</code> to return the name of this <code>DemoWizard</code>.
     * @return the name of this <code>DemoWizard</code>
     */
    public String toString() { return getName(); }
    
    /**
     * Creates a natural sorting of <code>DemoWizard</code>s according to their names.  Note that this ordering is incompatable with equals unless there is a guaranteed one-to-one mapping from wizards to names.
     * @param w The <code>DemoWizard</code> to compare this one to.
     * @return zero if this object has the same name as the argument; a positive value if this object has a name coming after the argument's name; and a negative value if this object has a name coming before the argument's name
     */
    public int compareTo(DemoWizard w) {
        if (this.equals(w)) return 0;
        return getName().compareTo(w.getName());     
    }
    
    /**
     * This method returns a short description for the end user of what the wizard will do.  This description will be displayed in a simple message dialog, so length and line breaks should be chosen appropriately.
     * @return a short description of what the <code>DemoWizard</code> does
     */
    public abstract String getDescription();
    
}
