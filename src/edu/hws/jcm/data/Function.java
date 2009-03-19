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
 * A Function is a mathematical real-valued function of zero or more
 * real-valued arguments.  The number of arguments is called the arity
 * of the function.
 */
abstract public interface Function extends java.io.Serializable {

   /**
    * Return the number of arguments of this function.  This must
    * be a non-negative integer.
    */
   public int getArity();

   /**
    * Find the value of the function at the argument values
    * given by arguments[0], arguments[1], ...  The length
    * of the array, arguments, should be equal to the arity of
    * the function.
    */
   public double getVal( double[] arguments );

   /**
    * Find the value of the function at the argument values
    * given by arguments[0], arguments[1], ...  The length
    * of the array argument should be equal to the arity of
    * the function.  Information about "cases" is stored in
    * the Cases parameter, if it is non-null.  See the Cases
    * class for more information.
    */
   public double getValueWithCases( double[] arguments, Cases cases );

   /**
    * Return the derivative of the function with repect to
    * argument number wrt.  For example, derivative(1) returns
    * the derivative function with respedt to the first argument.
    * Note that argements are numbered starting from 1.
    */
   public Function derivative(int wrt);
       
   /**
    * Return the derivative of the function with respect to the
    * variable x.  This will be non-zero only if x occurs somehow in
    * the definition of x: For example, f(y) = sin(x*y);
    * (This routine is required for the general function-differentiating
    * code in the class FunctionParserExtension.)
    */
   public Function derivative(Variable x);
       
   /**
    * Return true if the defintion of this function depends 
    * in some way on the variable x.  If not, it's assumed
    * that the derivative w.r.t. x of the function, applied to any
    * arguments that do not themselves depend on x,  is zero.
    * (This routine is required for the general function-differentiating
    * code in the class FunctionParserExtension.)
    */
   public boolean dependsOn(Variable x);

}
