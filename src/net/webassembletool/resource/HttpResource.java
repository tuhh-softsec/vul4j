package net.webassembletool.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.Context;
import net.webassembletool.ResourceUtils;
import net.webassembletool.Target;
import net.webassembletool.ouput.Output;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
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
    private HttpMethodBase httpMethod;
    private int statusCode;
    private String statusText;

    // TODO handle multipart POST requests
    public HttpResource(HttpClient httpClient, String baseUrl, Target target) {
	// Retrieve session and other cookies
	HttpState httpState = null;
	if (target.getContext() != null)
	    httpState = target.getContext().getHttpState();
	String url;
	if ("GET".equalsIgnoreCase(target.getMethod()) || !target.isProxyMode()) {
	    url = ResourceUtils.getHttpUrlWithQueryString(baseUrl, target);
	    httpMethod = new GetMethod(url);
	    
	   // TODO forward cookies in proxy mode
	} else if ("POST".equalsIgnoreCase(target.getMethod())) {
	    url = ResourceUtils.getHttpUrl(baseUrl, target);
	    PostMethod postMethod = new PostMethod(url);
	    Context context = target.getContext();
	    Map<String, String> parameters = target.getParameters();
	    if (context != null) {
		for (Map.Entry<String, String> temp : context.getParameterMap()
			.entrySet()) {
		    postMethod.addParameter(new NameValuePair(temp.getKey(),
			    temp.getValue()));
		}
	    }
	    if (parameters != null) {
		for (Map.Entry<String, String> temp : parameters.entrySet()) {
		    postMethod.addParameter(new NameValuePair(temp.getKey(),
			    temp.getValue()));
		}
	    }
	    if (target.getOriginalRequest() != null) {
		for (Object obj : target.getOriginalRequest().getParameterMap()
			.entrySet()) {
		    Entry<String, String[]> entry = (Entry<String, String[]>) obj;
		    for (int i = 0; i < entry.getValue().length; i++) {
			postMethod.addParameter(new NameValuePair(entry
				.getKey(), (entry.getValue())[i]));
		    }
		}
	    }
	    httpMethod = postMethod;
	} else {
	    throw new HttpMethodNotSupportedException(target.getMethod() + " "
		    + ResourceUtils.getHttpUrl(baseUrl, target));
	}
	httpMethod.setFollowRedirects(false);
	try {
	    httpClient.executeMethod(httpClient.getHostConfiguration(),
		    httpMethod, httpState);
	    statusCode = httpMethod.getStatusCode();
	    statusText = httpMethod.getStatusText();
	    if (statusCode != HttpServletResponse.SC_OK
		    && statusCode != HttpServletResponse.SC_MOVED_TEMPORARILY
		    && statusCode != HttpServletResponse.SC_MOVED_PERMANENTLY)
		log.warn("Problem retrieving URL: " + url + ": " + statusCode
			+ " " + statusText);
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
		Header header = httpMethod.getResponseHeader("Content-Type");
		if (header != null)
		    output.addHeader(header.getName(), header.getValue());
		header = httpMethod.getResponseHeader("Content-Length");
		if (header != null)
		    output.addHeader(header.getName(), header.getValue());
		header = httpMethod.getResponseHeader("Location");
		if (header != null)
		    output.addHeader(header.getName(), header.getValue());
		header = httpMethod.getResponseHeader("Last-Modified");
		if (header != null)
		    output.addHeader(header.getName(), header.getValue());
		header = httpMethod.getResponseHeader("ETag");
		if (header != null)
		    output.addHeader(header.getName(), header.getValue());
		String charset = httpMethod.getResponseCharSet();
		if (charset != null)
		    output.setCharset(charset);
		output.open();
		byte[] buffer = new byte[1024];
		InputStream inputStream = httpMethod.getResponseBodyAsStream();
		for (int len = -1; (len = inputStream.read(buffer)) != -1;) {
		    output.write(buffer, 0, len);
		}
	    } finally {
		output.close();
	    }
	}
    }

    public void release() {
	if (httpMethod != null) {
	    httpMethod.releaseConnection();
	    httpMethod = null;
	}
    }

    public int getStatusCode() {
	return statusCode;
    }
}
