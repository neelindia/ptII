/* A class cannot be found during AST manipulation.

Copyright (c) 1998-2004 The Regents of the University of California.
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

*/

package ptolemy.backtrack.ast;

//////////////////////////////////////////////////////////////////////////
//// ASTClassNotFoundException
/**
   Thrown on an attempt to load a class that cannot be found in the class
   path, or a class that is not properly imported. It is caused only in AST
   manipulation procedures. Because those procedures are not explicitly
   declared to raise exceptions in the superclasses, this exception is
   subclassed from {@link RuntimeException}.

   @author Thomas Feng
   @version $Id$
   @since Ptolemy II 4.1
   @Pt.ProposedRating Red (tfeng)
*/
public class ASTClassNotFoundException extends ASTException {

    /** Construct an exception representing a failure occurred when
     *  trying to load a class during AST building or transformation.
     *  
     *  @param className The name of the class that causes the failure.
     */
    public ASTClassNotFoundException(String className) {
        super("Class \"" + className + "\" not found.");
    }
    
}
