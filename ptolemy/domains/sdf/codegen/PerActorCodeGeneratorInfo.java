/* An object that hold information used as an argument to a 
   code generator that operates on the actor level.

 Copyright (c) 1999-2000 The Regents of the University of California.
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

@ProposedRating Red (ctsay@eecs.berkeley.edu)
@AcceptedRating Red (ctsay@eecs.berkeley.edu)
*/

package ptolemy.domains.sdf.codegen;

import java.util.HashMap;
import java.util.Map;

public class PerActorCodeGeneratorInfo {
    public PerActorCodeGeneratorInfo() {}
    
    /** The number of times the actor appears disjointly in the schedule
     *  for one iteration. By "disjointly" we mean that each disjoint
     *  appearance contains the maximum number of ordinary consecutive 
     *  appearances. If disjointAppearances = 1, certain optimizations
     *  are possible.
     */
    public int disjointAppearances = 1;

    /** A map containing arrays of Strings indexed by channel, using
     *  the corresponding input ports as keys. The Strings
     *  in the array are the names of the buffers from which to read 
     *  a specified channel of a specified port.
     */
    public final Map inputInfoMap = new HashMap();
         
    /** A map containing BufferInfos which uses the corresponding output 
     *  ports as keys.
     */
    public final Map outputInfoMap = new HashMap();
    
    
    /** A map containing instances of TypedIOPort or SDFIOPort which uses
     *  port names as keys.
     */
    public final Map portNameToPortMap = new HashMap();
 
    /** The total number of times the actor is fired in one iteration. */    
    public int totalFirings = -1;   
}