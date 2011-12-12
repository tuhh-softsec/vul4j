package org.esigate.http;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.esigate.ConfigurationException;
import org.esigate.DriverConfiguration;
import org.esigate.ResourceFactory;

public class ResourceFactoryCreator {
	
	public static ResourceFactory create(DriverConfiguration config) {
		HttpClient httpClient = null;
		if (config.getBaseURL() != null) {
			// Create and initialize scheme registry
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			try {
				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, null, null);
				SSLSocketFactory sslSocketFactory = new SSLSocketFactory(
						sslContext, SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
				Scheme https = new Scheme("https", 443, sslSocketFactory);
				schemeRegistry.register(https);
			} catch (NoSuchAlgorithmException e) {
				throw new ConfigurationException(e);
			} catch (KeyManagementException e) {
				throw new ConfigurationException(e);
			}
			schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
					.getSocketFactory()));
			// Create an HttpClient with the ThreadSafeClientConnManager.
			// This connection manager must be used if more than one thread will
			// be using the HttpClient.
			ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(
					schemeRegistry);
			connectionManager.setMaxTotal(config.getMaxConnectionsPerHost());
			connectionManager.setDefaultMaxPerRoute(config
					.getMaxConnectionsPerHost());
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					config.getTimeout());
			HttpConnectionParams.setSoTimeout(httpParams, config.getTimeout());
			httpParams.setBooleanParameter(
					ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient(
					connectionManager, httpParams);
			defaultHttpClient.setRedirectStrategy(new RedirectStrategy());
			httpClient = defaultHttpClient;

			// Proxy settings
			if (config.getProxyHost() != null) {
				HttpHost proxy = new HttpHost(config.getProxyHost(), config.getProxyPort(), "http");
				httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
		}
		ResourceFactory result = new HttpResourceFactory(httpClient);
		// Cache		
		if (config.isUseCache()) {
			result = new CachedHttpResourceFactory(result, config);
		}
		
		return result;
	}

	private ResourceFactoryCreator() { }
	
}
