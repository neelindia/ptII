/* One line description of file.

 Copyright (c) 1999-2003 The Regents of the University of California.
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
@ProposedRating Red (acataldo@eecs.berkeley.edu)
@AcceptedRating Red (reviewmoderator@eecs.berkeley.edu)
*/

package ptolemy.apps.softwalls;

import ptolemy.actor.TypedAtomicActor;
import ptolemy.actor.TypedIOPort;
import ptolemy.data.DoubleToken;
import ptolemy.data.StringToken;
import ptolemy.data.expr.Parameter;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.data.expr.Parameter;

import java.util.StringTokenizer;

//////////////////////////////////////////////////////////////////////////
//// Bias
/**
This takes state input from the aircraft, and outputs information on
the implicit surface function, which represents the reachable set.
This function is positive outside the reacable set, negative inside
the reachable set, and 0 on the boundary of the reacable set.  Its
value represents its distance to the reachable set, and, for positive
values, the negative gradient points toward the shortest path the the
reachable set.  This outputs the function value, the x gradient, the y
gradient, and the theta gradient, given the current x, y, and theta
value.

The function file should be stored in
$PTII/ptolemy/apps/softwalls/surfaces.  Its name is a parameter.

@author Adam Cataldo
@version $Id$
@since Ptolemy II 2.0.1
*/
public class ImplicitSurface extends TypedAtomicActor {
    /** Constructs an actor in the specified container with the specified
     *  name.
     *  @param container The container.
     *  @param name The name of this actor within the container.
     *  @exception IllegalActionException If the actor cannot be contained
     *   by the proposed container.
     *  @exception NameDuplicationException If the name coincides with
     *   an actor already in the container.
     */
    public ImplicitSurface(CompositeEntity container, String name) 
	throws IllegalActionException, NameDuplicationException {

	super(container, name);

	// Create and configure ports
	x = new TypedIOPort(this, "x", true, false);
	y = new TypedIOPort(this, "y", true, false);
	heading = new TypedIOPort(this, "heading", true, false);
	functionValue = new TypedIOPort(this, "functionValue", false, true);
		
	// Create and configure parameters
	functionFile = 
            new Parameter(this, "functionFile", new StringToken("plane.data"));
    }

    ///////////////////////////////////////////////////////////////////
    ////                     ports and parameters                  ////

    /** File name for implicit surface function */
    public Parameter functionFile;
    
    /** Current x position */
    public TypedIOPort x;

    /** Current y position */
    public TypedIOPort y;

    /** Current heading angle */
    public TypedIOPort heading;

    /** Output functionValue */
    public TypedIOPort functionValue;

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /** Overrides the base class to output the implicit surface
     *  function value.
     *  @exception IllegalActionException Not thrown in this base class.
     */

    public void fire() throws IllegalActionException {
        double currentX, currentY, currentTheta, f;
        currentX = ((DoubleToken)x.get(0)).doubleValue();
        currentY = ((DoubleToken)y.get(0)).doubleValue();
        currentTheta = ((DoubleToken)heading.get(0)).doubleValue();
        f = _surfaceFunction.getValue(currentX, currentY, currentTheta);
 	functionValue.send(0, new DoubleToken(f));
    }
    
    /** Loads the implicit surface function and gradient function.
     *  @exception IllegalActionException If the super class throws
     *  it, or if an exception is generated by creating the 
     *  functions.
     */
     
    public void initialize() throws IllegalActionException {
        super.initialize();
        String ptII, name;
        String path = "/ptolemy/apps/softwalls/surfaces/";
        StringToken functionToken;

        /** Get the full path name for the implicit surface function
         * file.
         */
        ptII = System.getProperty("ptolemy.ptII.dir");
        functionToken = (StringToken)functionFile.getToken();
        name = functionToken.toString();
        StringTokenizer t = new StringTokenizer(name, "\"");
        name = t.nextToken();

        System.out.println(name);
        path = ptII.concat(path);
        name = path.concat(name);

	try {
	    _surfaceFunction = new ThreeDFunction(name);
	} 
	catch (IllegalActionException a) {
	    throw a;
	}

    }

    ///////////////////////////////////////////////////////////////////
    ////                      private variables                    ////
    
    /* Implicit Surface Function and it's gradient Function*/
    ThreeDFunction _surfaceFunction;
    ThreeDFunction _gradientFunction;


    ///////////////////////////////////////////////////////////////////
    ////                       private methods                     ////
    

}


