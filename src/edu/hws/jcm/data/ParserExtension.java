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
 * A ParserExtension can be defined to add new capabilities to a
 * standard Parser.  Examples include user-defined functions and
 * summations (using a notation of the form "sum(i, 0, n, x^n/i!)").
 *   A ParserExtension is a MathObject, so it has a name and can be
 * registered with a Parser.  When the Parser encounters the name
 * in a string, it turns control of the parsing process over to
 * the ParserExtension, which must parse any necessary arguments 
 * and generate any ExpressionProgram commands.
 *
 */
public interface ParserExtension extends MathObject {
   /**
    * Parses the part of an expression string associated with this ParserExtension.
    * This method must add commands to context.prog that will generate exactly ONE
    * number on the stack when they are executed.  Parsing routines from the Parse class,
    * such as parseFactor and parseExpression, can be called
    * to parse sub-parts of the string.  The name of the command
    * has already been read from the ParseContext when doParse() is called. 
    *    (At the time this is called, context.tokenString is the 
    * name under which this ParserExtension was registered with the 
    * Parser.  This makes it possible to register the same ParserExtension
    * under several names, with each name represnting a different
    * meaning.)
    */
   public void doParse(Parser parser, ParserContext context);
}
