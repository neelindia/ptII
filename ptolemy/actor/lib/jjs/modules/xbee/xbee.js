/**
 * Module supporting XBee radios.
 *
 * @module xbee
 * @authors: Edward A. Lee
 */

var XBeeHelper = Java.type('ptolemy.actor.lib.jjs.modules.xbee.XBeeHelper');
var CommPortIdentifier = Java.type('gnu.io.CommPortIdentifier');
var EventEmitter = require('events').EventEmitter;

///////////////////////////////////////////////////////////////////////////////
//// defaultXBeeOptions

/** The default options for connections over XBee radio.
 */
var defaultXBeeOptions = {
    'baudRate': 9600,
    'receiveType': 'string',
    'sendType': 'string',
}

///////////////////////////////////////////////////////////////////////////////
//// hostSerialPorts

/** Return an array of serial port names or null if none are found.
 *  @return An array of port names.
 */
exports.hostSerialPorts = function() {
    var enumeration = CommPortIdentifier.getPortIdentifiers();
    var result = [];
    while (enumeration.hasMoreElements()) {
        var identifier = enumeration.nextElement();
        if (identifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
            var value = identifier.getName();
            result.push(value);
        }
    }
    return result;
};

///////////////////////////////////////////////////////////////////////////////
//// supportedReceiveTypes

/** Return an array of the types supported by the current host for
 *  the receiveType option.
 */
exports.supportedReceiveTypes = function() {
    return XBeeHelper.supportedReceiveTypes();
}

///////////////////////////////////////////////////////////////////////////////
//// supportedSendTypes

/** Return an array of the types supported by the current host for
 *  the sendType option.
 */
exports.supportedSendTypes = function() {
    return XBeeHelper.supportedSendTypes();
}

///////////////////////////////////////////////////////////////////////////////
//// XBee

/** Construct an XBee object, initialize it with the specified options.
 *  It is an event emitter that emits the following events:
 *  * 'data': Emitted when new data arrives on the radio.
 *
 *  The options argument is a JSON object that can include:
 *
 *  * receiveType: See below.
 *  * sendType: See below.
 *  * baudRate: The baud rate to communicate with the radio. This defaults to 9600.
 *
 *
 *  The send and receive types can be any of those returned by
 *  supportedReceiveTypes() and supportedSendTypes(), respectively.
 *  If both ends of the socket are known to be JavaScript clients,
 *  then you should use the 'number' data type for numeric data.
 *  If one end or the other is not JavaScript, then
 *  you can use more specified types such as 'float' or 'int', if they
 *  are supported by the host. In all cases, received numeric
 *  data will be converted to JavaScript 'number' when emitted.
 *  For sent data, this will try to convert a JavaScript number
 *  to the specified type. The type 'number' is equivalent
 *  to 'double'.
 *
 *  When type conversions are needed, e.g. when you send a double
 *  with sendType set to int, or an int with sendType set to byte,
 *  then a "primitive narrowing conversion" will be applied, as specified here:
 *  https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.1.3 .
 *
 *  For numeric types, you can also send an array with a single call
 *  to send(). The elements of the array will be sent in sequence all
 *  at once, and may be received in one batch. If both ends have
 *  rawBytes set to false (specifying message framing), then these
 *  elements will be emitted at the receiving end all at once in a single
 *  array. Otherwise, they will be emitted one at a time.
 *
 *  For strings, you can also send an array of strings in a single call,
 *  but these will be simply be concatenated and received as a single string.
 *
 *  @param portName The name of the port to open.
 *  @param options The options.
 *  @return An XBee interface.
 *  @throws If the port is in use or initializing the port fails.
 */
exports.XBee = function(portName, options) {
    // Fill in default values.
    this.options = options || {};
    this.options = util._extend(defaultXBeeOptions, this.options);

    this.helper = new XBeeHelper(this, portName, this.options);
}

util.inherits(exports.XBee, EventEmitter);

/** Close the port.
 */
exports.XBee.prototype.close = function() {
    this.helper.close();
};

/** Send data over the radio.
 *  @param data The data to send.
 */
exports.XBee.prototype.send = function(data) {
    if (Array.isArray(data)) {
        data = Java.to(data);
    }
    this.helper.send(data);
}
