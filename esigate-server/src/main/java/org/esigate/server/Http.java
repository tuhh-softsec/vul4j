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
public class Http {

	public static class Response {
		public final String body;
		public final int code;

		public Response(int code) {
			this(code, "");
		}

		public Response(int code, String body) {
			this.code = code;
			this.body = body;
		}
	}

	public static Response GET(String uri) {
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

	public static Response POST(String uri) {
		return http("POST", uri);
	}

}