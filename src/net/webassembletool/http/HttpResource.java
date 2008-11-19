package net.webassembletool.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.Context;
import net.webassembletool.Target;
import net.webassembletool.ouput.Output;
import net.webassembletool.resource.Resource;
import net.webassembletool.resource.ResourceUtils;

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
public class HttpResource extends Resource {
    private final static Log log = LogFactory.getLog(Resource.class);
    private HttpMethodBase httpMethod;
    private int statusCode;
    private String statusText;
    private final Target target;
    private String url;
    private Exception exception;

    private void buildHttpMethod() {
	// TODO do not proxy all the time
	if ("GET".equalsIgnoreCase(target.getMethod()) || !target.isProxyMode()) {
	    url = ResourceUtils.getHttpUrlWithQueryString(target);
	    httpMethod = new GetMethod(url);
	} else if ("POST".equalsIgnoreCase(target.getMethod())) {
	    url = ResourceUtils.getHttpUrl(target);
	    PostMethod postMethod = new PostMethod(url);
	    postMethod.getParams().setContentCharset(
		    target.getOriginalRequest().getCharacterEncoding());
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
		    @SuppressWarnings("unchecked")
		    Entry<String, String[]> entry = (Entry<String, String[]>) obj;
		    for (int i = 0; i < entry.getValue().length; i++) {
			postMethod.addParameter(new NameValuePair(entry
				.getKey(), (entry.getValue())[i]));
		    }
		}
	    }
	    httpMethod = postMethod;
	} else {
	    throw new UnsupportedHttpMethodException(target.getMethod() + " "
		    + ResourceUtils.getHttpUrl(target));
	}
	if (target.isProxyMode()) {
	    httpMethod.setFollowRedirects(false);
	} else {
	    httpMethod.setFollowRedirects(true);
	}
    }

    // TODO handle multipart POST requests
    public HttpResource(HttpClient httpClient, Target target) {
	this.target = target;
	// Retrieve session and other cookies
	HttpState httpState = null;
	if (target.getContext() != null)
	    httpState = target.getContext().getHttpState();
	buildHttpMethod();
	try {
	    httpClient.executeMethod(httpClient.getHostConfiguration(),
		    httpMethod, httpState);
	    statusCode = httpMethod.getStatusCode();
	    statusText = httpMethod.getStatusText();
	    if (isError())
		log.warn("Problem retrieving URL: " + url + ": " + statusCode
			+ " " + statusText);
	} catch (ConnectTimeoutException e) {
	    exception = e;
	    statusCode = HttpServletResponse.SC_GATEWAY_TIMEOUT;
	    statusText = "Connect timeout retrieving URL: " + url;
	    log.warn("Connect timeout retrieving URL: " + url);
	} catch (SocketTimeoutException e) {
	    exception = e;
	    statusCode = HttpServletResponse.SC_GATEWAY_TIMEOUT;
	    statusText = "Socket timeout retrieving URL: " + url;
	    log.warn("Socket timeout retrieving URL: " + url);
	} catch (HttpException e) {
	    exception = e;
	    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	    statusText = "Error retrieving URL: " + url;
	    log.error("Error retrieving URL: " + url, e);
	} catch (IOException e) {
	    exception = e;
	    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	    statusText = "Error retrieving URL: " + url;
	    log.error("Error retrieving URL: " + url, e);
	}
    }

    @Override
    public void render(Output output) throws IOException {
	output.setStatus(statusCode, statusText);
	Header header = httpMethod.getResponseHeader("Content-Type");
	if (header != null)
	    output.addHeader(header.getName(), header.getValue());
	header = httpMethod.getResponseHeader("Content-Length");
	if (header != null)
	    output.addHeader(header.getName(), header.getValue());
	// TODO refactor this
	header = httpMethod.getResponseHeader("Location");
	if (header != null) {
	    // Location header rewriting
	    String location = header.getValue();
	    String originalBase = target.getOriginalRequest().getScheme()
		    + "://" + target.getOriginalRequest().getServerName() + ":"
		    + target.getOriginalRequest().getServerPort()
		    + target.getOriginalRequest().getContextPath()
		    + target.getOriginalRequest().getServletPath();
	    if (target.getOriginalRequest().getPathInfo() != null)
		originalBase += target.getOriginalRequest().getPathInfo();
	    int pos = originalBase.indexOf(target.getRelUrl());
	    originalBase = originalBase.substring(0, pos + 1);
	    location = location.replaceFirst(target.getBaseUrl(), originalBase);
	    output.addHeader(header.getName(), location);
	}
	header = httpMethod.getResponseHeader("Last-Modified");
	if (header != null)
	    output.addHeader(header.getName(), header.getValue());
	header = httpMethod.getResponseHeader("ETag");
	if (header != null)
	    output.addHeader(header.getName(), header.getValue());
	String charset = httpMethod.getResponseCharSet();
	if (charset != null)
	    output.setCharsetName(charset);
	try {
	    output.open();
	    if (exception != null) {
		output.write(statusText);
	    } else {
		byte[] buffer = new byte[1024];
		InputStream inputStream = httpMethod.getResponseBodyAsStream();
		if (inputStream != null) {
		    try {
			OutputStream out = output.getOutputStream();
			for (int len = -1; (len = inputStream.read(buffer)) != -1;) {
			    out.write(buffer, 0, len);
			}
		    } finally {
			inputStream.close();
		    }
		}
	    }
	} finally {
	    output.close();
	}
    }

    @Override
    public void release() {
	if (httpMethod != null) {
	    httpMethod.releaseConnection();
	    httpMethod = null;
	}
    }

    @Override
    public int getStatusCode() {
	return statusCode;
    }

}
