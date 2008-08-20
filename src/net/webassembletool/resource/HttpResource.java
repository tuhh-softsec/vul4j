package net.webassembletool.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.Context;
import net.webassembletool.ouput.Output;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Resource implementation pointing to a resource on an external application.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class HttpResource implements Resource {
    private final static Log log = LogFactory.getLog(Resource.class);
    private GetMethod getMethod;
    private int statusCode;
    private String statusText;

    public HttpResource(HttpClient httpClient, String url, Context context) {
	if (context != null && context.getHttpState() != null)
	    httpClient.setState(context.getHttpState());
	getMethod = new GetMethod(url);
	getMethod.setFollowRedirects(false);
	try {
	    httpClient.executeMethod(getMethod);
	    statusCode = getMethod.getStatusCode();
	    statusText = getMethod.getStatusText();
	    if (statusCode != HttpServletResponse.SC_OK
		    && statusCode != HttpServletResponse.SC_MOVED_TEMPORARILY
		    && statusCode != HttpServletResponse.SC_MOVED_PERMANENTLY)
		log.warn("Problem retrieving URL: " + url + ": " + statusCode
			+ " " + statusText);
	    if (context != null)
		context.setHttpState(httpClient.getState());
	} catch (ConnectTimeoutException e) {
	    statusCode = HttpServletResponse.SC_GATEWAY_TIMEOUT;
	    statusText = "Connect timeout retrieving URL: " + url;
	    log.warn("Connect timeout retrieving URL: " + url);
	} catch (SocketTimeoutException e) {
	    statusCode = HttpServletResponse.SC_GATEWAY_TIMEOUT;
	    statusText = "Socket timeout retrieving URL: " + url;
	    log.warn("Socket timeout retrieving URL: " + url);
	} catch (HttpException e) {
	    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	    statusText = "Error retrieving URL: " + url;
	    log.error("Error retrieving URL: " + url, e);
	} catch (IOException e) {
	    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	    statusText = "Error retrieving URL: " + url;
	    log.error("Error retrieving URL: " + url, e);
	}
    }

    public void render(Output output) throws IOException {
	output.setStatus(statusCode, statusText);
	if (statusCode == HttpServletResponse.SC_OK
		|| statusCode == HttpServletResponse.SC_MOVED_TEMPORARILY
		|| statusCode == HttpServletResponse.SC_MOVED_PERMANENTLY) {
	    try {
		Header header = getMethod.getResponseHeader("Content-Type");
		if (header != null)
		    output.addHeader(header.getName(), header.getValue());
		header = getMethod.getResponseHeader("Content-Length");
		if (header != null)
		    output.addHeader(header.getName(), header.getValue());
		header = getMethod.getResponseHeader("Location");
		if (header != null)
		    output.addHeader(header.getName(), header.getValue());
		header = getMethod.getResponseHeader("Last-Modified");
		if (header != null)
		    output.addHeader(header.getName(), header.getValue());
		header = getMethod.getResponseHeader("ETag");
		if (header != null)
		    output.addHeader(header.getName(), header.getValue());
		String charset = getMethod.getResponseCharSet();
		if (charset != null)
		    output.setCharset(charset);
		output.open();
		byte[] buffer = new byte[1024];
		InputStream inputStream = getMethod.getResponseBodyAsStream();
		for (int len = -1; (len = inputStream.read(buffer)) != -1;) {
		    output.write(buffer, 0, len);
		}
	    } finally {
		output.close();
	    }
	}
    }

    public void release() {
	if (getMethod != null) {
	    getMethod.releaseConnection();
	    getMethod = null;
	}
    }

    public int getStatusCode() {
	return statusCode;
    }
}
