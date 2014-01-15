package org.esigate.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.UserContext;
import org.esigate.api.ContainerRequestContext;
import org.esigate.http.IncomingRequest;

public class DriverRequest implements HttpEntityEnclosingRequest {
    private final IncomingRequest wrappedRequest;
    private final Driver driver;
    private final UserContext userContext;
    private final URL baseUrl;
    private final boolean external;
    private String characterEncoding;

    public DriverRequest(IncomingRequest request, Driver driver, boolean external) throws HttpErrorPage {
        this.wrappedRequest = request;
        this.driver = driver;
        this.external = external;
        this.userContext = new UserContext(request, driver.getConfiguration().getInstanceName());
        try {
            this.baseUrl = new URL(driver.getConfiguration().getBaseUrlRetrieveStrategy().getBaseURL(request));
        } catch (MalformedURLException e) {
            throw new HttpErrorPage(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Internal server error", e);
        }

    }

    @Override
    public boolean expectContinue() {
        return wrappedRequest.expectContinue();
    }

    @Override
    public void setEntity(HttpEntity entity) {
        wrappedRequest.setEntity(entity);
    }

    @Override
    public RequestLine getRequestLine() {
        return wrappedRequest.getRequestLine();
    }

    @Override
    public HttpEntity getEntity() {
        return wrappedRequest.getEntity();
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return wrappedRequest.getProtocolVersion();
    }

    @Override
    public boolean containsHeader(String name) {
        return wrappedRequest.containsHeader(name);
    }

    @Override
    public Header[] getHeaders(String name) {
        return wrappedRequest.getHeaders(name);
    }

    @Override
    public Header getFirstHeader(String name) {
        return wrappedRequest.getFirstHeader(name);
    }

    @Override
    public Header getLastHeader(String name) {
        return wrappedRequest.getLastHeader(name);
    }

    @Override
    public Header[] getAllHeaders() {
        return wrappedRequest.getAllHeaders();
    }

    @Override
    public void addHeader(Header header) {
        wrappedRequest.addHeader(header);
    }

    @Override
    public void addHeader(String name, String value) {
        wrappedRequest.addHeader(name, value);
    }

    @Override
    public void setHeader(Header header) {
        wrappedRequest.setHeader(header);
    }

    @Override
    public void setHeader(String name, String value) {
        wrappedRequest.setHeader(name, value);
    }

    @Override
    public void setHeaders(Header[] headers) {
        wrappedRequest.setHeaders(headers);
    }

    @Override
    public void removeHeader(Header header) {
        wrappedRequest.removeHeader(header);
    }

    @Override
    public void removeHeaders(String name) {
        wrappedRequest.removeHeaders(name);
    }

    @Override
    public HeaderIterator headerIterator() {
        return wrappedRequest.headerIterator();
    }

    @Override
    public HeaderIterator headerIterator(String name) {
        return wrappedRequest.headerIterator(name);
    }

    @SuppressWarnings("deprecation")
    @Override
    public org.apache.http.params.HttpParams getParams() {
        return wrappedRequest.getParams();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setParams(org.apache.http.params.HttpParams params) {
        wrappedRequest.setParams(params);
    }

    public Driver getDriver() {
        return driver;
    }

    public UserContext getUserContext() {
        return userContext;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public ContainerRequestContext getContext() {
        return wrappedRequest.getContext();
    }

    public IncomingRequest getOriginalRequest() {
        return wrappedRequest;
    }

    public boolean isExternal() {
        return external;
    }

}
