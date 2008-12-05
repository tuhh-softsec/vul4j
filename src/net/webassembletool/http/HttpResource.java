package net.webassembletool.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.security.Principal;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.RequestContext;
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
import org.jasig.cas.client.authentication.AttributePrincipal;

/**
 * Resource implementation pointing to a resource on an external application.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class HttpResource extends Resource {
    private final static Log LOG = LogFactory.getLog(Resource.class);
    private HttpMethodBase httpMethod;
    private int statusCode;
    private String statusText;
    private final RequestContext target;
    private String url;

    private Exception exception;

    private void buildHttpMethod() {
	// CAS support
	Principal principal = target.getOriginalRequest().getUserPrincipal();
	if (principal != null && principal instanceof AttributePrincipal) {
	    AttributePrincipal casPrincipal = (AttributePrincipal) principal;
	    LOG.debug("User logged in CAS as: " + casPrincipal.getName());
	    url = ResourceUtils.getHttpUrl(target);
	    String casProxyTicket = casPrincipal.getProxyTicketFor(url);
	    LOG.debug("Proxy ticket retrieved: " + casPrincipal.getName()
		    + " for service: " + url);
	    target.getParameters().put("ticket", casProxyTicket);
	}
	if ("GET".equalsIgnoreCase(target.getMethod()) || !target.isProxyMode()) {
	    url = ResourceUtils.getHttpUrlWithQueryString(target);
	    httpMethod = new GetMethod(url);
	} else if ("POST".equalsIgnoreCase(target.getMethod())) {
	    url = ResourceUtils.getHttpUrl(target);
	    PostMethod postMethod = new PostMethod(url);
	    postMethod.getParams().setContentCharset(
		    target.getOriginalRequest().getCharacterEncoding());
	    Map<String, String> parameters = target.getParameters();
	    if (target.getUserContext() != null) {
		for (Map.Entry<String, String> temp : target.getUserContext()
			.getParameterMap().entrySet()) {
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
    public HttpResource(HttpClient httpClient, RequestContext target) {
	this.target = target;
	// Retrieve session and other cookies
	HttpState httpState = null;
	if (target.getUserContext() != null)
	    httpState = target.getUserContext().getHttpState();
	buildHttpMethod();
	try {
	    if (LOG.isDebugEnabled())
		LOG.debug(toString());
	    httpClient.executeMethod(httpClient.getHostConfiguration(),
		    httpMethod, httpState);
	    statusCode = httpMethod.getStatusCode();
	    statusText = httpMethod.getStatusText();
	    if (isError())
		LOG.warn("Problem retrieving URL: " + url + ": " + statusCode
			+ " " + statusText);
	} catch (ConnectTimeoutException e) {
	    exception = e;
	    statusCode = HttpServletResponse.SC_GATEWAY_TIMEOUT;
	    statusText = "Connect timeout retrieving URL: " + url;
	    LOG.warn("Connect timeout retrieving URL: " + url);
	} catch (SocketTimeoutException e) {
	    exception = e;
	    statusCode = HttpServletResponse.SC_GATEWAY_TIMEOUT;
	    statusText = "Socket timeout retrieving URL: " + url;
	    LOG.warn("Socket timeout retrieving URL: " + url);
	} catch (HttpException e) {
	    exception = e;
	    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	    statusText = "Error retrieving URL: " + url;
	    LOG.error("Error retrieving URL: " + url, e);
	} catch (IOException e) {
	    exception = e;
	    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	    statusText = "Error retrieving URL: " + url;
	    LOG.error("Error retrieving URL: " + url, e);
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
	    location = location.replaceFirst(target.getDriver().getBaseURL(),
		    originalBase);
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

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuilder result = new StringBuilder();
	result.append(target.getMethod());
	result.append(" ");
	result.append(ResourceUtils.getHttpUrlWithQueryString(target));
	result.append("\n");
	if (target.getUserContext() != null)
	    result.append(target.getUserContext().toString());
	return result.toString();
    }
}
