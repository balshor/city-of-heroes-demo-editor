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
 * Represents a syntax error that is found while a string is being parsed.
 */
public class ParseError extends RuntimeException {

   /**
    * The parsing context that was in effect
    * at the time the error occurred.  This includes
    * the string that was being processed and the
    * position in the string where the error occured.
    * These values are context.data and context.pos.
    */
   public ParserContext context;
      
   /**
    * Create a new ParseError with a given error message and parsing context.
    */
   public ParseError(String message, ParserContext context) { 
      super(message);
      this.context = context;
   }

}
