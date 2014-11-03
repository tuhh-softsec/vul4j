package org.esigate.cas;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.esigate.http.RedirectStrategy;

/**
 * @author Alexis Thaveau on 31/10/14.
 */
public class CASRedirectStrategy extends RedirectStrategy {

    private String loginURL;

    public CASRedirectStrategy() {
        super();
    }

    @Override
    public boolean isRedirected(final HttpRequest request, final HttpResponse response, final HttpContext context)
            throws ProtocolException {
        Args.notNull(request, "HTTP request");
        Args.notNull(response, "HTTP response");

        final int statusCode = response.getStatusLine().getStatusCode();
        final Header locationHeader = response.getFirstHeader("location");

        switch (statusCode) {
        case HttpStatus.SC_MOVED_TEMPORARILY:
        case HttpStatus.SC_MOVED_PERMANENTLY:
        case HttpStatus.SC_TEMPORARY_REDIRECT:
            return super.isRedirected(request, response, context) && !locationHeader.getValue().contains(loginURL);
        default:
            return super.isRedirected(request, response, context);
        } // end of switch
    }

    public void setLoginURL(String loginURL) {
        this.loginURL = loginURL;
    }

}
