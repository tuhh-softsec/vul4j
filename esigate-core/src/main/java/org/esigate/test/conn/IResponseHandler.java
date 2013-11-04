package org.esigate.test.conn;

import java.io.IOException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/**
 * A response handler for {@link MockConnectionManager}.
 * 
 * @author Nicolas Richeton
 */
public interface IResponseHandler {

    /**
     * Implement this method to process the HTTP request and provide a custom reponse.
     * 
     * @param request
     * @return the response
     * @throws IOException
     */
    HttpResponse execute(HttpRequest request) throws IOException;
}
