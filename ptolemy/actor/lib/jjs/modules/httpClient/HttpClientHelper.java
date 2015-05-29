/* A JavaScript helper for HttpClient.

   Copyright (c) 2014 The Regents of the University of California.
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
package ptolemy.actor.lib.jjs.modules.httpClient;

import java.util.Map;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;

import ptolemy.actor.lib.jjs.modules.VertxHelperBase;
import ptolemy.actor.lib.jjs.JSUtils;

///////////////////////////////////////////////////////////////////////
//// HttpClientHelper

/**
   A helper class for the HttpClient module in JavaScript.
   
   @author Marten Lohstroh, Edward A. Lee
   @version $Id: HttpClientHelper.java 71938 2015-04-08 21:55:19Z hokeunkim $
   @since Ptolemy II 11.0
   @Pt.ProposedRating Yellow (eal)
   @Pt.AcceptedRating Red (cxh)
 */
public class HttpClientHelper extends VertxHelperBase {

    ///////////////////////////////////////////////////////////////////
    ////                     public methods                        ////

    /** Create a HttpClientHelper instance for the specified JavaScript
     *  Socket instance for the client side of the socket.
     *  @param currentObj The JavaScript instance of the Socket.
     *  @return A new HttpClientHelper instance.
     */
    public static HttpClientHelper createHttpClient(
            ScriptObjectMirror currentObj, Map<String, Object> options) {
        return new HttpClientHelper(currentObj, options);
    }

    /** End a request. */
    public void end() {
        if (_request != null) {
            _request.end();
        }
    }

    ///////////////////////////////////////////////////////////////////
    ////                     private constructors                   ////

    /** Private constructor for WebSocketHelper to open a client-side web socket.
     *  Open an internal web socket using Vert.x.
     *  @param currentObj The JavaScript instance of Socket that this helps.
     *  @param address The URL of the WebSocket host with an optional port number
     *   (e.g. 'ws://localhost:8000'). If no port number is given, 80 is used.
     */
    private HttpClientHelper(ScriptObjectMirror currentObj,
            Map<String, Object> options) {
        _currentObj = currentObj;
        HttpClient client = _vertx.createHttpClient();
        // FIXME: Why does Vert.x require setHost and setPort and also a URI?

        Object obj;
        if ((obj = JSUtils.castSafely(options.get("host"), String.class, false)) != null) {
            client.setHost((String) obj);
        }

        if ((obj = JSUtils
                .castSafely(options.get("port"), Integer.class, false)) != null) {
            client.setPort((int) obj);
        }

        client.exceptionHandler(new HttpClientExceptionHandler());

        if ((obj = JSUtils.castSafely(options.get("keepAlive"), Boolean.class,
                false)) != null) {
            client.setKeepAlive((boolean) obj);
        }

        // FIXME: Add timeout to the options in httpClient.js
        if ((obj = JSUtils.castSafely(options.get("timeout"), Integer.class,
                false)) != null) {
            client.setConnectTimeout((int) obj);
        }

        String query = "";
        if ((obj = JSUtils
                .castSafely(options.get("query"), String.class, false)) != null) {
            query = (String) obj;

            if (!query.startsWith("?")) {
                query = "?" + query;
            }
        }

        String protocol = "http"; // NOTE: http is the default protocol
        if ((obj = JSUtils.castSafely(options.get("protocol"), String.class,
                false)) != null) {
            protocol = (String) obj;
        }

        String path = "";
        if ((obj = JSUtils.castSafely(options.get("path"), String.class, false)) != null) {
            path = (String) obj;
        }

        String uri = protocol + "://" + client.getHost() + ":"
                + client.getPort() + path + query;

        if (protocol.equals("https")) {
            client.setSSL(true);
        }

        if ((obj = JSUtils.castSafely(options.get("trustAll"), Boolean.class,
                false)) != null) {
            if ((boolean) obj == true) {
                client.setTrustAll(true);
            }
        }

        String method = "GET"; // NOTE: GET is the default method
        if ((obj = JSUtils.castSafely(options.get("method"), String.class,
                false)) != null) {
            method = (String) obj;
        }

        _request = client.request(method, uri, new HttpClientResponseHandler());

        // Handle the headers.

        if ((obj = JSUtils.castSafely(options.get("headers"), Map.class, false)) != null) {
            Map<String, Object> headers = (Map<String, Object>) obj;
            for (Object key : headers.keySet()) {
                Object value = headers.get(key);
                if (value instanceof String) {
                    _request.putHeader((String) key, (String) value);
                } else if (value instanceof Iterable) {
                    _request.putHeader((String) key, (Iterable<String>) value);
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////
    ////                     private fields                        ////

    /** The current instance of the ClientRequest JavaScript object. */
    private ScriptObjectMirror _currentObj;

    /** The request built in the constructor. */
    private HttpClientRequest _request;

    ///////////////////////////////////////////////////////////////////
    ////                     inner classes                         ////

    /** The event handler that is triggered when an error occurs in the HTTP connection.
     */
    private class HttpClientExceptionHandler implements Handler<Throwable> {
        @Override
        public void handle(Throwable throwable) {
            synchronized (HttpClientHelper.this) {
                _currentObj.callMember("emit", "error", throwable.getMessage());
            }
        }
    }

    /** The event handler that is triggered when a response arrives from the server.
     */
    private class HttpClientResponseHandler implements
            Handler<HttpClientResponse> {
        @Override
        public void handle(HttpClientResponse response) {
            // The response is not yet complete. Provide a handler to handle when it
            // is complete. Note that large response could fill up memory here, since
            // we do not chunk the response!
            HttpClientBodyHandler bodyHandler = new HttpClientBodyHandler(
                    response);
            response.bodyHandler(bodyHandler);
        }
    }

    /** The body handler that is triggered when a complete response body
     *  has arrived from the server.
     */
    private class HttpClientBodyHandler implements Handler<Buffer> {
        public HttpClientBodyHandler(HttpClientResponse response) {
            _response = response;
        }

        @Override
        public void handle(Buffer body) {
            synchronized (HttpClientHelper.this) {
                // FIXME: This assumes the body is a string encoded in UTF-8.
                _currentObj.callMember("_response", _response, body.toString());
            }
        }

        private HttpClientResponse _response;
    }
}