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

/**
 * A MathObject is just an object that has setName and getName methods.
 * MathObjects can be registered with a Parser (meaning that they are
 * stored in the SymbolTable associated with the Parser, and can 
 * be used in expressions parsed by the Parser).
 */
public interface MathObject extends java.io.Serializable {
   /**
    * Get the name of this object.
    */
   public String getName();
   
   /**
    * Set the name of this object.  This should not be done if
    * the MathObject is registered with a Parser.
    */
   public void setName(String name);
   
} 
