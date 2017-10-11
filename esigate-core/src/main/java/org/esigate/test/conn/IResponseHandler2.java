package org.esigate.test.conn;

import org.apache.http.HttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.HttpContext;

/**
 * A response handler with support for receiving connection information.
 * 
 * Can be used to test Http route behavior.
 * 
 * @author Nicolas Richeton
 */
public interface IResponseHandler2 extends IResponseHandler {
    public void connect(HttpClientConnection conn, HttpRoute route, int connectTimeout, HttpContext context);
}
