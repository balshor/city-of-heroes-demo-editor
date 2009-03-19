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
 * A standard stack of values of type double, which can grow to arbitrary size.
 */
public class StackOfDouble implements java.io.Serializable {

   private double[] data;  // Contents of stack.
   private int top;        // Number of items on stack.

   /**
    * Create an initially empty stack.  It initially has space allocated for one item.
    */
   public StackOfDouble() {
    data = new double[1];
   }

   /**
    * Create an empty stack that initially has space for initialSize items pre-allocated.
    * If initialSize <= 0, an initialSize of 1 is used.
    */
   public StackOfDouble(int initialSize) {
    data = new double[initialSize > 0 ? initialSize : 1];
   }

   /**
    * Add x to top of stack.
    */
   public void push(double x) {
    if (top >= data.length) {
       double[] temp = new double[2*data.length];
       System.arraycopy(data,0,temp,0,data.length);
       data = temp;
    }
    data[top++] = x;
   }

   /**
    * Remove and return the top item on the stack.
    * Will throw an exception of type java.util.EmptyStackException
    * if the stack is empty when pop() is called.
    */
   public double pop() {
    if (top == 0)
       throw new java.util.EmptyStackException();
    return data[--top];
   }

   /**
    * Return true if and only if the stack contains no items.
    */
   public boolean isEmpty() {
    return (top == 0);
   }

   /**
    * Clear all items from the stack.
    */
   public void makeEmpty() {
    top = 0;
   }
   
   /**
    * Return the number of items on the stack.
    */
   public int size() {
    return top;
   }

} // end class StackOfDouble
