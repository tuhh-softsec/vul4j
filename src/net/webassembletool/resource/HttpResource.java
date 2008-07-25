package net.webassembletool.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

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
 * @author Fran�ois-Xavier Bonnet
 * 
 */
public class HttpResource implements Resource {
    private final static Log log = LogFactory.getLog(Resource.class);
    private GetMethod getMethod;
    private boolean exists = false;

    public HttpResource(HttpClient httpClient, String url, Context context) {   
	if (context != null && context.getHttpState() != null) httpClient.setState(context.getHttpState());
	getMethod = new GetMethod(url);
	try {
	    httpClient.executeMethod(getMethod);
	    if (context != null) context.setHttpState(httpClient.getState());
	    exists = (getMethod.getStatusCode() == 200);
	} catch (ConnectTimeoutException e) {
	    log.warn("Connect timeout retrieving URL: " + url);
	} catch (SocketTimeoutException e) {
	    log.warn("Socket timeout retrieving URL: " + url);
	} catch (HttpException e) {
	    log.error("Error retrieving URL: " + url, e);
	} catch (IOException e) {
	    log.error("Error retrieving URL: " + url, e);
	}    }

    public void render(Output output) throws IOException {
	try {
	    Header header = getMethod.getResponseHeader("Content-Type");
	    if (header != null)
		output.addHeader(header.getName(), header.getValue());
	    header = getMethod.getResponseHeader("Content-Length");
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

    public boolean exists() {
	return exists;
    }

    public void release() {
	if (getMethod != null) {
	    getMethod.releaseConnection();
	    getMethod = null;
	}
    }
}
