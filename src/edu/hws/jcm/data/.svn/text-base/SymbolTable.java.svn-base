/*************************************************************************
*                                                                        *
*   1) This source code file, in unmodified form, and compiled classes   *
*      derived from it can be used and distributed without restriction,  *
*      including for commercial use.  (Attribution is not required       *
*      but is appreciated.)                                              *
*                                                                        *
*    2) Modified versions of this file can be made and distributed       *
*       provided:  the modified versions are put into a Java package     *
*       different from the original package, edu.hws;  modified          *
*       versions are distributed under the same terms as the original;   *
*       and the modifications are documented in comments.  (Modification *
*       here does not include simply making subclasses that belong to    *
*       a package other than edu.hws, which can be done without any      *
*       restriction.)                                                    *
*                                                                        *
*   David J. Eck                                                         *
*   Department of Mathematics and Computer Science                       *
*   Hobart and William Smith Colleges                                    *
*   Geneva, New York 14456,   USA                                        *
*   Email: eck@hws.edu          WWW: http://math.hws.edu/eck/            *
*                                                                        *
*************************************************************************/

package edu.hws.jcm.data;

import java.util.Hashtable;

/**
 * A symbol table contains MathObjects, associating them
 * with their names.  To support scoping (for example), a symbol
 * table can have a parent symbol table.  If a symbol is not found
 * in the table itself, the search procedes to its parent.
 * MathObjects in the parent are hidden by MathObjects of
 * the same name in a SymbolTable.
 *    Note that a NullPointerException error will occur if an
 * attempt is made to add a MathObject with a null name to a
 * SymbolTable.
 *   A MathObject should not be renamed while it is registered
 * in a SymbolTable.
 *   Note that a Parser has an associated SymbolTable.  I expect
 * SymbolTables to be used only through Parsers.
 */
public class SymbolTable implements java.io.Serializable {

  private Hashtable symbols;    // Objects are stored here and in the parent.
  private SymbolTable parent;   // Parent symbol table, possibly null.
  
   /**
    * Construct a symbol table with null parent.
    */
   SymbolTable() { 
     this(null);
   }

   /**
    * Construct a symbol table with specified parent.
    */
   SymbolTable(SymbolTable parent) {
     this.parent = parent;
     symbols = new Hashtable();
   }

   /**
    * Returns the parent symbol table of this symbol table.
    */
   SymbolTable getParent() {
     return parent;
   }

   /**
    * Look up the object with the given name, if any.
    * If not found, return null.  (If the name is not found
    * in the HashTable, symbols, the search is delegated to
    * the parent.)  If the name is null, then
    * null is returned.
    */
   synchronized public MathObject get(String name) {
     if (name == null)
        return null;
     Object sym = symbols.get(name);
     if (sym != null)
        return (MathObject)sym;
     else if (parent != null)
        return parent.get(name);
     else
        return null;
   }

   /**
    * Adds sym to the SymbolTable, associating it with its name.
    */
   synchronized public void add(MathObject sym) {
      if (sym == null)
        throw new NullPointerException("Can't put a null symbol in SymbolTable.");
      add(sym.getName(), sym);
   }

   /**
    * Adds the given MathObject, sym, to the symbol table,
    * associating it with the given name (which is probably
    * the name of the symbol or that name transformed to lower
    * case, but it doesn't have to be).
    *    If the same name is already in use in the HashTable
    * then the new object replaces the current object.
    * Note that if the name is defined in the parent
    * symbol table, then the old object is hidden, not
    * removed from the parent.  If sym is null or if
    * sym's name is null, than a NullPointerException is
    * thrown.
    */
   synchronized public void add(String name, MathObject sym) {
     if (sym == null)
        throw new NullPointerException("Can't put a null symbol in SymbolTable.");
     else if (name == null)
        throw new NullPointerException("Can't put unnamed MathObject in SymbolTable.");
     symbols.put(name,sym);
   }

   /**
    * Remove the object with the given name from the symbol table,
    * but NOT from the parent symbol table.  No error occurs
    * if the name is not in the table or if name is null.
    * If an object of the same name occurs in the parent,
    * this routine will un-hide it.
    */
   synchronized public void remove(String name) {
     if (name != null) 
        symbols.remove(name);
   }

} // end class SymbolTable

