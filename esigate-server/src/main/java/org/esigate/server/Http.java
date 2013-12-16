/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.esigate.server;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

/**
 * This is just for convenience. It's not safe.
 * <p>
 * From : http://open.bekk.no/embedded-jetty-7-webapp-executable-with-maven/
 * 
 * @author Ole Christian Rynning
 * 
 */
public final class Http {

    private Http() {
    }

    public static class Response {
        private final String body;
        private final int code;

        public Response(int code) {
            this(code, "");
        }

        public Response(int code, String body) {
            this.code = code;
            this.body = body;
        }
    }

    /**
     * Perform POST request.
     * 
     * @param uri
     *            target uri
     * @return response.
     */

    public static Response doGET(String uri) {
        return http("GET", uri);
    }

    static Response http(String method, String uri) {
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            Object content = conn.getContent();

            if (content instanceof InputStream) {
                return new Response(conn.getResponseCode(), IOUtils.toString((InputStream) content, "UTF-8"));
            } else if (content instanceof String) {
                return new Response(conn.getResponseCode(), (String) content);
            } else {
                return new Response(conn.getResponseCode(), "unknown");
            }

        } catch (SocketException e) {
            return new Response(SC_NOT_FOUND);
        } catch (IOException e) {
            return new Response(SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Perform POST request.
     * 
     * @param uri
     *            target uri
     * @return response.
     */
    public static Response doPOST(String uri) {
        return http("POST", uri);
    }

}
