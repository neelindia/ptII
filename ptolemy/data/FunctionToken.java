/* Token that contains a function.

 Copyright (c) 2002-2003 The Regents of the University of California.
 All rights reserved.
 Permission is hereby granted, without written agreement and without
 license or royalty fees, to use, copy, modify, and distribute this
 software and its documentation for any purpose, provided that the above
 copyright notice and the following two paragraphs appear in all copies
 of this software.

 IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
 FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
 ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
 PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 ENHANCEMENTS, OR MODIFICATIONS.

                                        PT_COPYRIGHT_VERSION_2
                                        COPYRIGHTENDKEY

@ProposedRating Yellow (liuxj@eecs.berkeley.edu)
@AcceptedRating Red (liuxj@eecs.berkeley.edu)
*/

package ptolemy.data;

import ptolemy.kernel.util.IllegalActionException;
import ptolemy.data.type.BaseType;
import ptolemy.data.type.Type;
import ptolemy.data.type.TypeLattice;
import ptolemy.graph.CPO;

import java.io.Serializable;
import java.util.List;

//////////////////////////////////////////////////////////////////////////
//// FunctionToken
/**
A token that contains a function. The function takes a fixed number of
arguments, supplied as a list of tokens.

Currently, no operations between function tokens (add, multiply, etc.)
are supported.

@author Xiaojun Liu
@version $Id$
@since Ptolemy II 2.1
*/
public class FunctionToken extends Token {

	public FunctionToken(Function f) {
		_function = f;
	}

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

	public Token apply(List args) throws IllegalActionException {
		return _function.apply(args);
	}

	/** Return the number of arguments of the function.
	 *  @return The number of arguments of the function.
	 */
	public int getNumberOfArguments() {
		return _function.getNumberOfArguments();
	}

    /** Return the type of this token.
	 *  FIXME: before a function type is implemented, use BaseType.GENERAL.
     *  @return BaseType.GENERAL
     */
    public Type getType() {
        return BaseType.GENERAL;
    }

	///////////////////////////////////////////////////////////////////
    ////                         private fields                    ////

	// The object that implements the function.
	private Function _function;

    ///////////////////////////////////////////////////////////////////
    ////                         inner classes                     ////

	/** The interface for functions contained by function tokens.
	 */
	public interface Function {

		/** Apply the function to the list of arguments, which are tokens.
		 *  @param arguments The list of arguments.
		 *  @return The result of applying the function to the given
		 *   arguments.
		 *  @exception IllegalActionException If thrown during evaluating
		 *   the function.
		 */
		public Token apply(List args) throws IllegalActionException;

		/** Return the number of arguments of the function.
		 *  @return The number of arguments of the function.
		 */
		public int getNumberOfArguments();
	}

}

