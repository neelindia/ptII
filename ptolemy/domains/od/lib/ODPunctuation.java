/* 

 Copyright (c) 1997-1998 The Regents of the University of California.
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

@ProposedRating Red (davisj@eecs.berkeley.edu)

*/

package ptolemy.domains.od.lib;

import ptolemy.domains.od.kernel.*;
import ptolemy.kernel.util.*;
import ptolemy.actor.*;
import ptolemy.data.*;
import collections.LinkedList;

//////////////////////////////////////////////////////////////////////////
//// ODPunctuation
/** 


@author John S. Davis II
@version @(#)ODPunctuation.java	1.1	11/12/98
*/
public class ODPunctuation extends ODStringSource {

    /** 
     */
    public ODPunctuation(CompositeActor container, String name)
            throws IllegalActionException, NameDuplicationException {
        super(container, name);
        _punctuation = new LinkedList();
    }
 
    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /** Return the current time of this actor.
     */
    public LinkedList setUpStrings() {
        
        _punctuation.insertLast( new StringTime( 2.5, "!") ); 
       
        _punctuation.insertLast( new StringTime( 3.0, "!" ) );
        
        _punctuation.insertLast( new StringTime( 3.5, " " ) );
        
        _punctuation.insertLast( new StringTime( 5000.0, ";" ) );
        
        // Up to "Hello!! The"
        
        return _punctuation;
    }
    
    ///////////////////////////////////////////////////////////////////
    ////                        private variables                  ////

    private LinkedList _punctuation;
    
}




















