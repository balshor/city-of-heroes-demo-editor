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
 *  A ValueMath object is an easy way to create Value objects that are computed
 *  from other Value objects.  For example, "new ValueMath(a,b,'+')" is an
 *  object whose value is obtained by adding the values of a and b.
 */
public class ValueMath implements Value {

   private Function f;  // If non-null, this is a value of the form f(params);
                        // If null, it's of the form x + y, x - y, ...
   private double[] param;
   private Value x,y;
   private char op;
   
   /**
    * Create a ValueMath object whose value is computed by applying an arithmetic
    * operator the values of x and y.
    * @param op The arithmetic operator that is to be applied to x and y.  This should
    *           be one of the characters '+', '-', '*', '/', or '^'.  (No error is
    *           thrown if another character is provided.  It will be treated as a '/').
    */
   public ValueMath(Value x, Value y, char op) {
      this.x = x;
      this.y = y;
      this.op = op;
   }
   
   /**
    * Create a ValueMath object whose value is computed as f(x).
    */
   public ValueMath(Function f, Value x) {
       if (f.getArity() != 1)
          throw new IllegalArgumentException("Internal Error:  The function in a ValueMath object must have arity 1.");
       this.f = f;
       this.x = x;
       param = new double[1];
   }
   
   /**
    *  Get the value of this object.
    */
   public double getVal() {
      if (f != null) {
         param[0] = x.getVal();
         return f.getVal(param);
      }
      else {
         double a = x.getVal();
         double b = y.getVal();
         switch (op) {
            case '+': return a+b;
            case '-': return a-b;
            case '*': return a*b;
            case '/': return a/b;
            case '^': return Math.pow(a,b);
            default:  throw new IllegalArgumentException("Internal Error:  Unknown math operator.");
         }
      }
   }

} // end class ValueMath
