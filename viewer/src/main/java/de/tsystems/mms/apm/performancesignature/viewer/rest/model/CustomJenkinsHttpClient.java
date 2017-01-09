/*
 * Copyright (c) 2014 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tsystems.mms.apm.performancesignature.viewer.rest.model;

import com.offbytwo.jenkins.client.JenkinsHttpClient;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import de.tsystems.mms.apm.performancesignature.viewer.model.CustomProxy;
import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.protocol.BasicHttpContext;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

public class CustomJenkinsHttpClient extends JenkinsHttpClient {
    private static final Logger LOGGER = Logger.getLogger(CustomJenkinsHttpClient.class.getName());

    public CustomJenkinsHttpClient(final URI uri, final String username, final String password, final boolean verifyCertificate, final CustomProxy customProxy) {

        super(uri, addAuthentication(createHttpClientBuilder(verifyCertificate, customProxy), uri, username, password));
        if (StringUtils.isNotBlank(username)) {
            BasicHttpContext httpContext = new BasicHttpContext();
            httpContext.setAttribute("preemptive-auth", new BasicScheme());
            super.setLocalContext(httpContext);
        }
    }

    private static HttpClientBuilder createHttpClientBuilder(final boolean verifyCertificate, final CustomProxy customProxy) {

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.useSystemProperties();
        if (!verifyCertificate) {
            SSLContextBuilder builder = new SSLContextBuilder();
            try {
                builder.loadTrustMaterial(null, new TrustStrategy() {
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        return true;
                    }
                });
                httpClientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactory(builder.build()));
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
            }

        }
        if (customProxy != null) {
            Jenkins jenkins = PerfSigUIUtils.getInstance();
            if (customProxy.isUseJenkinsProxy() && jenkins.proxy != null) {
                final ProxyConfiguration proxyConfiguration = jenkins.proxy;
                if (StringUtils.isNotBlank(proxyConfiguration.name) && proxyConfiguration.port > 0) {
                    httpClientBuilder.setProxy(new HttpHost(proxyConfiguration.name, proxyConfiguration.port));
                    if (StringUtils.isNotBlank(proxyConfiguration.getUserName())) {
                        CredentialsProvider credsProvider = new BasicCredentialsProvider();
                        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(proxyConfiguration.getUserName(),
                                proxyConfiguration.getUserName());
                        credsProvider.setCredentials(new AuthScope(proxyConfiguration.name, proxyConfiguration.port), usernamePasswordCredentials);
                        httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
                        httpClientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
                    }
                }
            } else {
                httpClientBuilder.setProxy(new HttpHost(customProxy.getProxyServer(), customProxy.getProxyPort()));
                if (StringUtils.isNotBlank(customProxy.getProxyUser()) && StringUtils.isNotBlank(customProxy.getProxyPassword())) {
                    CredentialsProvider credsProvider = new BasicCredentialsProvider();
                    UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(customProxy.getProxyUser(), customProxy.getProxyPassword());
                    credsProvider.setCredentials(new AuthScope(customProxy.getProxyServer(), customProxy.getProxyPort()), usernamePasswordCredentials);
                    httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
                    httpClientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
                }
            }
        }
        return httpClientBuilder;
    }
}
