/* An actor that reads a string from a file or URL, parses it assuming it is defining a record, and outputs the record.

 Copyright (c) 2003-2010 The Regents of the University of California.
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
package ptolemy.actor.lib.io;

import java.util.ArrayList;
import java.util.List;

import ptolemy.actor.TypedIOPort;
import ptolemy.data.Token;
import ptolemy.data.expr.ASTPtRootNode;
import ptolemy.data.expr.Parameter;
import ptolemy.data.expr.ParseTreeEvaluator;
import ptolemy.data.expr.PtParser;
import ptolemy.data.expr.StringParameter;
import ptolemy.data.type.BaseType;
import ptolemy.data.type.MonotonicFunction;
import ptolemy.data.type.Type;
import ptolemy.data.type.TypeLattice;
import ptolemy.graph.CPO;
import ptolemy.graph.InequalityTerm;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

///////////////////////////////////////////////////////////////////
//// TokenReader

/**
 An actor that reads a string expression from a file or URL upon receiving a 
 signal on its <i>trigger</i> input port, and outputs a token that is the 
 result of evaluating the read string. The file or URL is specified by the 
 <i>FileOrURL</i> parameter or set using the <i>FileOrURL</i> port. If the 
 file or URL cannot be read, the expression cannot be parsed successfully,
 or the resulting token does not match the type constraint of the output port,
 the value of the <i>errorHandlingStrategy</i> parameter determines the 
 behavior of this actor.
 TODO: describe automatic port constraint setting 
 FIXME: More here. Particularly, document output type handling.

 @author Edward A. Lee
 @author Marten Lohstroh
 @version $Id$
 @since Ptolemy II 9.0
 @Pt.ProposedRating Yellow (eal)
 @Pt.AcceptedRating Red (reviewmoderator)
 */
public class TokenReader extends FileReader {
    
    /** Construct an actor with a name and a container.
     *  The container argument must not be null, or a NullPointerException 
     *  will be thrown.
     *  @param container The container.
     *  @param name The name of this actor.
     *  @exception IllegalActionException If the container is incompatible
     *   with this actor.
     *  @exception NameDuplicationException If the name coincides with
     *   an actor already in the container.
     */
    public TokenReader(CompositeEntity container, String name)
            throws IllegalActionException, NameDuplicationException {
        super(container, name);
        outputType = new Parameter(this, "outputType");
        
        errorHandlingStrategy = new StringParameter(this, "errorHandlingStrategy");
        errorHandlingStrategy.addChoice("Throw Exception");
        errorHandlingStrategy.addChoice("Do Nothing");
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public variables                  ////

    /** The strategy to use if:
     *  <ul>
     *  <li> the file or URL cannot be read,</li>
     *  <li> the data read from the file or the URL cannot be parsed,</li>
     *  <li> the parsed token cannot be converted into a token of the type
     *  given by <i>outputType</i>, if such a type is given, or</li>
     *  <li> the parsed token cannot
     *  be converted to a token of the resolved type of the output, if no <i>outputType</i>
     *  is given.</li>
     *  </ul>
     *  This is a string that has the following
     *  possible values: "Do Nothing" or "Throw Exception", where
     *  "Throw Exception" is the default.
     */
    public StringParameter errorHandlingStrategy;

    /** If this parameter has a value, then the value specifies the
     *  type of the output port. When the actor reads from the file
     *  or URL, it expects the data read to be a string that can be
     *  parsed into a token that is convertible to (or identical to)
     *  this output type. If it is not, then the action taken is
     *  specified by the <i>errorHandlingStrategy</i> parameter.
     *  If this parameter has no value (the default), then the
     *  output type will be set to match whatever is first read from the
     *  file or URL and will be updated on each subsequent firing if
     *  the data read from the file or URL cannot be converted to the
     *  type determined by the first read. 
     */
    public Parameter outputType;

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /** FIXME.
     *  @exception IllegalActionException FIXME.
     */
    public void fire() throws IllegalActionException {
        try {
            super.fire();
        } catch (IllegalActionException exception) {
            String errorHandlingStrategyValue = errorHandlingStrategy.stringValue();
            if (errorHandlingStrategyValue.equals("Throw Exception")) {
                throw exception;
            }
        }
    }
    
    /** Set the type according to the value of the outputType parameter.
     * 
     */
    public void preinitialize() throws IllegalActionException {
        super.preinitialize();
        Token outputTypeValue = outputType.getToken();
        if (outputTypeValue != null) {
            // An output type has been specified.
            // Force the output to this type.
            output.setTypeEquals(outputTypeValue.getType());
        } else {
            // Declare constraints that the output type must
            // be greater than or equal to the types of all the
            // destination ports.
            // 
            // FIXME: The base class sets the output type
            // to String. That may need to change. It seems that just
            // removing the string declaration in the base class doesn't
            // work because then default constraints result in the output
            // being greater than or equal to the trigger input, which is
            // boolean.            
            if (_outputTypeConstraintsFunction == null) {
                _outputTypeConstraintsFunction = new GLBFunction();
            }
            output.setTypeAtLeast(_outputTypeConstraintsFunction);
            
            // Next, need to add constraints for destination ports that have
            // fixed types.
        }
    }
    
    ///////////////////////////////////////////////////////////////////
    ////                      protected methods                    ////

    /** FIXME: Send the specified string to the output.
     *  @param fileContents The contents of the file or URL that was read.
     *  @throws IllegalActionException If sending the data fails.
     */
    protected void _handleFileData(String fileContents)
            throws IllegalActionException {
        
        if (_parser == null) {
            _parser = new PtParser();
        }
        ASTPtRootNode parseTree = _parser.generateParseTree(fileContents);

        if (_parseTreeEvaluator == null) {
            _parseTreeEvaluator = new ParseTreeEvaluator();
        }

        // FIXME: Evaluating a parse tree that comes from an untrusted
        // source creates a security problem.
        Token result = _parseTreeEvaluator.evaluateParseTree(parseTree);

        if (result == null) {
            throw new IllegalActionException(this,
                    "Expression yields a null result: " + fileContents);
        }
        
        if (!output.getType().isCompatible(result.getType())) {
            // Handle the error according to the error policy.
            // FIXME: Fall through to the broadcast, which will throw
            // an exception.
        }
        output.broadcast(result);
    }
    
    ///////////////////////////////////////////////////////////////////
    ////                         private members                   ////
    
    /** Monotonic function used for setting output type constraints. */
    private GLBFunction _outputTypeConstraintsFunction;
    
    /** The parser to use. */
    private PtParser _parser = null;

    /** The parse tree evaluator to use. */
    private ParseTreeEvaluator _parseTreeEvaluator = null;
    
    ///////////////////////////////////////////////////////////////////
    ////                         inner classes                     ////
    
    /** This class implements a monotonic function that returns the
     *  greatest lower bound (GLB) of its arguments. The arguments
     *  will be port type variables for all destination ports that this
     *  actor sends data to. We will use this function to define a
     *  type constraint asserting that the type of the output is
     *  greater than or equal to the GLB of the destinations.
     *  <p>
     *  NOTE: It may seem counterintuitive that the constraint is
     *  "greater than or equal to" rather than "less than or equal to."
     *  But the latter constraint is already implied by the connections,
     *  since the output port type is required to be less than or equal
     *  to each destination port type.  The combination of these
     *  constraints has the effect of setting the type of the output
     *  equal to the GLB of the types of the destination ports.
     *  This resolved type is, in fact, the most general type that
     *  satisfies the contraints of all the downstream ports.
     */
    private class GLBFunction extends MonotonicFunction {

        ///////////////////////////////////////////////////////////////
        ////                       public inner methods            ////
        
        /** Return the current value of this monotonic function.
         *  @return A Type.
         */
        public Object getValue() throws IllegalActionException {
            InequalityTerm[] variables = getVariables();
            Object[] types = new Type[variables.length + _cachedTypes.length];
            for (int i = 0; i < variables.length; i++) {
                types[i] = variables[i].getValue();
            }
            for (int i = 0; i < _cachedTypes.length; i++) {
                types[variables.length + i] = _cachedTypes[i];
            }
            // If there are no destination outputs at all, then set
            // the output type to general.
            if (types.length == 0) {
                return BaseType.GENERAL;
            }
            CPO lattice = TypeLattice.lattice();
            return lattice.greatestLowerBound(types);
        }

        /** Return the type variables for this function, which are
         *  the type variables for all the destination ports.
         *  @return An array of InequalityTerm.
         */
        public InequalityTerm[] getVariables() {
            if (workspace().getVersion() == _cachedTermsWorkspaceVersion) {
                return _cachedTerms;
            }
            ArrayList<InequalityTerm> portTypeTermList = new ArrayList<InequalityTerm>();
            ArrayList<Type> portTypeList = new ArrayList<Type>();
            List<TypedIOPort> destinations = output.sinkPortList();
            
            for (TypedIOPort destination : destinations) {
                InequalityTerm destinationTypeTerm = destination.getTypeTerm();
                if (destinationTypeTerm.isSettable()) {
                    portTypeTermList.add(destinationTypeTerm);
                } else {
                    portTypeList.add(destination.getType());
                }
            }
            _cachedTerms = portTypeTermList.toArray(new InequalityTerm[0]);
            _cachedTypes = portTypeList.toArray(new Type[0]);
            _cachedTermsWorkspaceVersion = workspace().getVersion();
            return _cachedTerms;
        }
        
        private Type[] _cachedTypes;
        private InequalityTerm[] _cachedTerms;
        private long _cachedTermsWorkspaceVersion = -1;
    }
}
