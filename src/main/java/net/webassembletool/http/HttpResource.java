package net.webassembletool.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.webassembletool.RequestContext;
import net.webassembletool.output.Output;
import net.webassembletool.output.StringOutput;
import net.webassembletool.resource.Resource;
import net.webassembletool.resource.ResourceUtils;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.ConnectionPoolTimeoutException;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.client.authentication.AttributePrincipal;

/**
 * Resource implementation pointing to a resource on an external application.
 * 
 * @author Fran�ois-Xavier Bonnet
 */
public class HttpResource extends Resource {
    private final static Log LOG = LogFactory.getLog(HttpResource.class);
    private HttpMethodBase httpMethod;
    private int statusCode;
    private String statusText;
    private final RequestContext target;
    private String url;
    private Exception exception;

    /** This method builds post request adding WAT specific parameters */
    private void buildFormPostMethod() {
        url = ResourceUtils.getHttpUrl(target);
        PostMethod postMethod = new PostMethod(url);
        postMethod.getParams().setContentCharset(target.getOriginalRequest().getCharacterEncoding());
        Map<String, String> parameters = target.getParameters();
        if (target.getUserContext() != null)
            for (Map.Entry<String, String> temp : target.getUserContext().getParameterMap().entrySet())
                postMethod.addParameter(new NameValuePair(temp.getKey(), temp.getValue()));
        if (parameters != null)
            for (Map.Entry<String, String> temp : parameters.entrySet())
                postMethod.addParameter(new NameValuePair(temp.getKey(), temp.getValue()));
        if (target.getOriginalRequest() != null)
            for (Object obj : target.getOriginalRequest().getParameterMap().entrySet()) {
                @SuppressWarnings("unchecked")
                Entry<String, String[]> entry = (Entry<String, String[]>) obj;
                for (int i = 0; i < entry.getValue().length; i++)
                    postMethod.addParameter(new NameValuePair(entry.getKey(), entry.getValue()[i]));
            }
        httpMethod = postMethod;
    }

    /**
     * This builds a postMethod forwarding content and content type without modification.
     * 
     * @throws IOException
     *             if problem getting the request
     */
    private void buildRawPostMethod() throws IOException {
        url = ResourceUtils.getHttpUrl(target);
        if (target.getOriginalRequest().getQueryString() != null)
            url = url + '?' + target.getOriginalRequest().getQueryString();
        PostMethod postMethod = new PostMethod(url);
        HttpServletRequest req = target.getOriginalRequest();
        postMethod.setRequestEntity(new InputStreamRequestEntity(req.getInputStream(), req.getContentLength(), req.getContentType()));
        httpMethod = postMethod;
    }

    private void buildGetMethod() {
        url = ResourceUtils.getHttpUrlWithQueryString(target);
        httpMethod = new GetMethod(url);
    }

    private void addCasAuthentication(String location) {
        Principal principal = target.getOriginalRequest().getUserPrincipal();
        if (principal != null && principal instanceof AttributePrincipal) {
            AttributePrincipal casPrincipal = (AttributePrincipal) principal;
            HttpResource.LOG.debug("User logged in CAS as: " + casPrincipal.getName());
            String service = location;
            service = service.substring(service.indexOf("service=") + "service=".length());
            int ampersandPosition = service.indexOf('&');
            if (ampersandPosition > 0)
                service = service.substring(0, ampersandPosition);
            try {
                service = URLDecoder.decode(service, "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                // Should not happen
            }
            String casProxyTicket = casPrincipal.getProxyTicketFor(service);
            HttpResource.LOG.debug("Proxy ticket retrieved: " + casPrincipal.getName() + " for service: " + service + " : " + casProxyTicket);
            if (casProxyTicket != null)
                target.getParameters().put("ticket", casProxyTicket);
        }
    }

    private void buildHttpMethod() throws IOException {
        if ("GET".equalsIgnoreCase(target.getMethod()) || !target.isProxyMode())
            buildGetMethod();
        else if ("POST".equalsIgnoreCase(target.getMethod())) {
            String contentType = target.getOriginalRequest().getContentType();
            if (contentType != null && contentType.startsWith("application/x-www-form-urlencoded"))
                // Handle multipart forms adding WAT specific params
                buildFormPostMethod();
            else
                // Raw forward of the post
                buildRawPostMethod();
        } else
            throw new UnsupportedHttpMethodException(target.getMethod() + " " + ResourceUtils.getHttpUrl(target));
        if (target.isProxyMode())
            httpMethod.setFollowRedirects(false);
        else
            httpMethod.setFollowRedirects(true);
    }

    public HttpResource(HttpClient httpClient, RequestContext target) {
        this.target = target;
        // Retrieve session and other cookies
        HttpState httpState = null;
        if (target.getUserContext() != null)
            httpState = target.getUserContext().getHttpState();
        try {
            buildHttpMethod();
            if (HttpResource.LOG.isDebugEnabled())
                HttpResource.LOG.debug(toString());
            httpClient.executeMethod(httpClient.getHostConfiguration(), httpMethod, httpState);
            statusCode = httpMethod.getStatusCode();
            statusText = httpMethod.getStatusText();
            // CAS support
            String currentLocation = null;
            if (!target.isProxyMode())
                currentLocation = httpMethod.getPath() + "?" + httpMethod.getQueryString();
            else if (statusCode == HttpServletResponse.SC_MOVED_PERMANENTLY || statusCode == HttpServletResponse.SC_MOVED_TEMPORARILY)
                currentLocation = httpMethod.getResponseHeader("location").getValue();
            if (currentLocation != null && currentLocation.contains("/login")) {
                addCasAuthentication(currentLocation);
                // We must ensure that the connection is allways released, if
                // not
                // the connection manager's pool may be exhausted soon !
                httpMethod.releaseConnection();
                buildHttpMethod();
                HttpResource.LOG.debug(toString());
                httpClient.executeMethod(httpClient.getHostConfiguration(), httpMethod, httpState);
                statusCode = httpMethod.getStatusCode();
                statusText = httpMethod.getStatusText();
            }
            if (isError())
                HttpResource.LOG.warn("Problem retrieving URL: " + url + ": " + statusCode + " " + statusText);
        } catch (ConnectionPoolTimeoutException e) {
            exception = e;
            statusCode = HttpServletResponse.SC_GATEWAY_TIMEOUT;
            statusText = "Connect timeout retrieving URL: " + url;
            HttpResource.LOG.warn("Connect timeout retrieving URL, connection pool exhausted: " + url, e);
        } catch (ConnectTimeoutException e) {
            exception = e;
            statusCode = HttpServletResponse.SC_GATEWAY_TIMEOUT;
            statusText = "Connect timeout retrieving URL: " + url;
            HttpResource.LOG.warn("Connect timeout retrieving URL: " + url);
        } catch (SocketTimeoutException e) {
            exception = e;
            statusCode = HttpServletResponse.SC_GATEWAY_TIMEOUT;
            statusText = "Socket timeout retrieving URL: " + url;
            HttpResource.LOG.warn("Socket timeout retrieving URL: " + url);
        } catch (HttpException e) {
            exception = e;
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            statusText = "Error retrieving URL: " + url;
            HttpResource.LOG.error("Error retrieving URL: " + url, e);
        } catch (IOException e) {
            exception = e;
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            statusText = "Error retrieving URL: " + url;
            HttpResource.LOG.error("Error retrieving URL: " + url, e);
        }
    }

    private void copyHeaders(HttpMethod src, Output dest, String... headers) {
        for (String name : headers) {
            Header header = src.getResponseHeader(name);
            if (header != null)
                dest.addHeader(header.getName(), header.getValue());
        }
    }

    @Override
    public void render(Output output) throws IOException {
        output.setStatus(statusCode, statusText);
        copyHeaders(httpMethod, output, "Content-Type", "Content-Length", "Last-Modified", "ETag", "Expires", "Cache-control", "Content-length");
        // TODO: refactor this
        Header header = httpMethod.getResponseHeader("Location");
        if (header != null) {
            // Location header rewriting
            String location = header.getValue();
            String originalBase = target.getOriginalRequest().getScheme() + "://" + target.getOriginalRequest().getServerName() + ":" + target.getOriginalRequest().getServerPort()
                    + target.getOriginalRequest().getContextPath() + target.getOriginalRequest().getServletPath();
            if (target.getOriginalRequest().getPathInfo() != null)
                originalBase += target.getOriginalRequest().getPathInfo();
            int pos = originalBase.indexOf(target.getRelUrl());
            originalBase = originalBase.substring(0, pos + 1);
            location = location.replaceFirst(target.getDriver().getBaseURL(), originalBase);
            output.addHeader(header.getName(), location);
        }
        String charset = httpMethod.getResponseCharSet();
        if (charset != null)
            output.setCharsetName(charset);
        try {
            output.open();
            if (exception != null)
                output.write(statusText);
            else {
                Handler handler = createHandler();
                handler.handle(httpMethod.getResponseBodyAsStream(), output);
            }
        } finally {
            output.close();
        }
    }

    private Handler createHandler() {
        String jsessionid = null;
        if (target.getUserContext() != null && target.isFilterJsessionid()) {
            Cookie[] cookies = target.getUserContext().getHttpState().getCookies();
            for (Cookie cookie : cookies)
                if ("jsessionid".equalsIgnoreCase(cookie.getName())) {
                    jsessionid = cookie.getValue();
                    break;
                }
        }
        boolean textContentType = false;
        Header contentTypeHeader = httpMethod.getResponseHeader("Content-Type");
        if (contentTypeHeader != null)
            textContentType = ResourceUtils.isTextContentType(contentTypeHeader.getValue());
        if (jsessionid == null || !textContentType)
            return new OldHandler();
        else {
            String charset = httpMethod.getResponseCharSet();
            if (target.isPropagateJsessionId())
                return new ReplaceCookieHandler(charset, jsessionid);
            else
                return new RemoveCookieHandler(charset, jsessionid);
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

    interface Handler {
        void handle(InputStream src, Output dest) throws IOException;
    }

    static final class ReplaceCookieHandler extends CookieHandler {
        public ReplaceCookieHandler(String charset, String jsessionid) {
            super(charset, jsessionid);
        }

        /** Replaces <code>jsessionid</code>'s some value with new one */
        @Override
        protected String parseContent(String content) {
            String replacement = "jsessionid=" + jsessionid;
            return content.replaceAll("jsessionid=([^?#&'\"]+)", replacement);
        }
    }

    static final class RemoveCookieHandler extends CookieHandler {
        public RemoveCookieHandler(String charset, String jsessionid) {
            super(charset, jsessionid);
        }

        /** Replaces <code>jsessionid</code>'s some value with new one */
        @Override
        protected String parseContent(String content) {
            return content.replaceAll("[;]{0,1}jsessionid=([^?#&'\"]+)", "");
        }
    }

    static abstract class CookieHandler implements Handler {
        private final Handler defaultHandler = new OldHandler();
        private final String charset;
        protected final String jsessionid;

        protected CookieHandler(String charset, String jsessionid) {
            this.charset = charset;
            this.jsessionid = jsessionid;
        }

        public void handle(InputStream src, Output dest) throws IOException {
            StringOutput out = new StringOutput();
            out.setCharsetName(charset);
            defaultHandler.handle(src, out);
            // parse content;
            String parsed = parseContent(out.toString());
            // update content-length header
            if (dest.getHeader("Content-length") != null)
                dest.setHeader("Content-length", Integer.toString(parsed.length()));
            // put parsed content to destination
            defaultHandler.handle(new ByteArrayInputStream(parsed.getBytes(charset)), dest);
        }

        protected abstract String parseContent(String content);
    }

    static final class OldHandler implements Handler {
        public OldHandler() {
            // no op constructor
        }

        public void handle(InputStream src, Output dest) throws IOException {
            if (src != null)
                try {
                    OutputStream out = dest.getOutputStream();
                    if (out != null) {
                        // out may be null when NOT_MODIFIED return code
                        byte[] buffer = new byte[1024];
                        for (int len = -1; (len = src.read(buffer)) != -1;)
                            out.write(buffer, 0, len);
                    }
                } finally {
                    src.close();
                }
        }
    }
}
