/* A CT actor that outputs a sine wave.

 Copyright (c) 1997-1999 The Regents of the University of California.
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
@ProposedCodeRate red (liuj@eecs.berkeley.edu)
*/

package ptolemy.domains.ct.lib;
import ptolemy.domains.ct.kernel.*;
import ptolemy.kernel.*;
import ptolemy.kernel.util.*;
import ptolemy.actor.*;
import ptolemy.data.*;
import ptolemy.data.expr.*;
import java.util.Enumeration;

//////////////////////////////////////////////////////////////////////////
//// CTSin
/**
A timed sin wave source: output = A*Sin(w*t + phi) 
Note: Seems not replacable by the domain polymrphic Sin.
@author Jie Liu
@version $Id$
*/
public class CTSin extends CTActor {
    /** Construct the CTSin actor; the default magnitude is 1;
     *  the defalt angle frequency is 2*pi and the default phase is 0.
     * @param container TypedCompositeActor this actor belongs to
     * @param name The name
     * @exception NameDuplicationException Other star already had this name
     * @exception IllegalActionException internal problem
     */
    public CTSin(TypedCompositeActor container, String name)
            throws NameDuplicationException, IllegalActionException  {
        super(container, name);
        output = new TypedIOPort(this, "output");
        output.setInput(false);
        output.setOutput(true);
        output.setTypeEquals(DoubleToken.class);
        _magnitude = 1.0;
        paramMagnitude =  new Parameter(this, "Magnitude",
                new DoubleToken(_magnitude));
        _angleFrequency = 2*Math.PI;
        paramAngleFrequency = new Parameter(this, "AngleFrequency",
                new DoubleToken(_angleFrequency));
        _phase = 0.0;
        paramPhase =  new Parameter(this, "Phase",
                new DoubleToken(_phase));
    }


    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /** Output a token according to the shape of the ramp and the
     *  the current time.
     *
     *  @exception IllegalActionException If there's no director or no
     *        input token when needed.
     */
    public void fire() throws IllegalActionException{
        CTDirector dir = (CTDirector) getDirector();
        if(dir == null) {
            throw new IllegalActionException( this, " Has no director.");
        }
        double now = dir.getCurrentTime();
        double out = _magnitude * Math.sin(_angleFrequency*now + _phase);
        output.broadcast(new DoubleToken(out));
    }

    /** Update parameters
     *  @exception IllegalActionException Never thrown.
     */
    public void updateParameters() throws IllegalActionException {
        _magnitude = ((DoubleToken)paramMagnitude.getToken()).doubleValue();
        _angleFrequency = ((DoubleToken)
                paramAngleFrequency.getToken()).doubleValue();
        _phase = ((DoubleToken)paramPhase.getToken()).doubleValue();
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public variables                  ////

    /** The single output port with type double
     */
    public TypedIOPort output;

    /** Parameter for the manitude; type is double; default value is 1.0.
     */
    public Parameter paramMagnitude;

    /** Parameter for the angle frequency; the type is double, the default
     *  value is 2*pi.
     */
    public Parameter paramAngleFrequency;
    
    /** Parameter for the phase; the type is double; the default value is 0.0.
     */
    public Parameter paramPhase;
    

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    // Private variables should not have doc comments, they should
    // have regular C++ comments.
    private double _magnitude;
    private double _angleFrequency;
    private double _phase;
}
