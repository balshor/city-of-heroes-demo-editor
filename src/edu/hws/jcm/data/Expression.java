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
 * An Expression represents a mathematical expression such as "x+1" or
 * "3" or "sin(x*ln(x)-3*abs(x/4))".  An expression has a value, which
 * can depend on the values of variables that occur in the expression.
 * An expression can be differenetiated with respect to a variable.  It has
 * a print string representation.  This interface is implemented by
 * the classes Constant, Variable, and ExpressionProgram, for example.  
 * The Expression interface
 * represents all the properties of expressions that you are likely to need
 * to know about, unless you want to write a new kind of ExpressionCommand.
 */
public interface Expression extends Value {

   // The method "public double getVal()" is inherited from the Value interface.
   // It returns the current value of this expression.

   /**
    * Compute and return the value of this expression.  If cases is non-null,
    * then data is stored in cases that can be used to check for possible
    * discontinuities between one evaluation and the next.  See the class
    * Cases for more information.
    */
   public double getValueWithCases(Cases cases);

   /**
    * Return an Expression that represents the derivative of
    * this expression with respect to the variable wrt.
    * (Note that if the expression contains other variables
    * besides wrt, this is actually a partial derivative.)
    */
   public Expression derivative(Variable wrt);

   /**
    * Checks whether the expression has any dependence on the variable x.
    */
   public boolean dependsOn(Variable x);

   /**
    * Get a print string representation of this expression.  (Of course,
    * every object defines toString().  It is included here to remind you
    * that classes that implement the Expression interface should have
    * a print string representation that looks like a mathematical
    * expression.)
    */
   public String toString();

}
