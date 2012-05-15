package org.esigate.http;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.esigate.ConfigurationException;
import org.esigate.DriverConfiguration;
import org.esigate.ResourceFactory;

public class ResourceFactoryCreator {

	public static ResourceFactory create(DriverConfiguration config) {
		// Create and initialize scheme registry
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, null, null);
			SSLSocketFactory sslSocketFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
			Scheme https = new Scheme("https", 443, sslSocketFactory);
			schemeRegistry.register(https);
		} catch (NoSuchAlgorithmException e) {
			throw new ConfigurationException(e);
		} catch (KeyManagementException e) {
			throw new ConfigurationException(e);
		}
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		// Create an HttpClient with the ThreadSafeClientConnManager.
		// This connection manager must be used if more than one thread will
		// be using the HttpClient.
		ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(schemeRegistry);
		connectionManager.setMaxTotal(config.getMaxConnectionsPerHost());
		connectionManager.setDefaultMaxPerRoute(config.getMaxConnectionsPerHost());
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, config.getConnectTimeout());
		HttpConnectionParams.setSoTimeout(httpParams, config.getSocketTimeout());
		httpParams.setBooleanParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient(connectionManager, httpParams);
		defaultHttpClient.setRedirectStrategy(new RedirectStrategy());
		HttpClient httpClient = defaultHttpClient;

		// Proxy settings
		if (config.getProxyHost() != null) {
			if (config.getProxyUser() != null) {
				defaultHttpClient.getCredentialsProvider().setCredentials(new AuthScope(config.getProxyHost(), config.getProxyPort()),
						new UsernamePasswordCredentials(config.getProxyUser(), config.getProxyPassword()));
			}
			HttpHost proxy = new HttpHost(config.getProxyHost(), config.getProxyPort(), "http");
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		// Cache
		if (config.isUseCache()) {

			// TODO set these as parameters in driver.properties
			boolean heuristicCachingEnabled = true;
			// TODO add these parameters to cache-control header in every request
			long staleWhileRevalidate = 3600; // seconds
			long staleIfError = 86400; // seconds

			// TODO If-Match,If-Modified-Since,If-None-Match,If-Range, If-Unmodified-Since should be copied to request
			// Update documentation and unit tests

			CacheConfig cacheConfig = new CacheConfig();
			cacheConfig.setHeuristicCachingEnabled(heuristicCachingEnabled);
			if (config.getCacheMaxFileSize() > 0)
				cacheConfig.setMaxObjectSizeBytes(config.getCacheMaxFileSize());
			else
				cacheConfig.setMaxObjectSizeBytes(Integer.MAX_VALUE);
			cacheConfig.setSharedCache(true);
			httpClient = new CachingHttpClient(httpClient, cacheConfig);

		}
		ResourceFactory result = new HttpResourceFactory(httpClient);
		return result;
	}

	private ResourceFactoryCreator() {
	}

}
