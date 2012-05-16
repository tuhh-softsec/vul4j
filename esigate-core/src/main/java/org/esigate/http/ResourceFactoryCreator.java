package org.esigate.http;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.cache.HttpCacheStorage;
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
import org.esigate.cache.CacheConfigHelper;
import org.esigate.extension.Extension;

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

			// TODO set all as parameters in driver.properties

			// TODO make a wrapper for each storage type that can be instanced with properties configuration only
			String cacheStorageClass = "org.esigate.cache.BasicCacheStorage";
			// org.esigate.cache.BasicCacheStorage
			// org.esigate.cache.EhcacheCacheStorage
			// org.esigate.cache.ManagedCacheStorage
			// org.esigate.cache.MemcachedCacheStorage
			Object cacheStorage;
			try {
				cacheStorage = Class.forName(cacheStorageClass).newInstance();
			} catch (Exception e) {
				throw new ConfigurationException("Could not instantiate cacheStorageClass", e);
			}
			if (!(cacheStorage instanceof Extension) || !(cacheStorage instanceof HttpCacheStorage))
				throw new ConfigurationException("Cache storage class must implement Extension and HttpCacheStorage interfaces");
			((Extension) cacheStorage).init(config.getProperties());

			// TODO wrap backend http client for responses and incoming requests : cache errors, add stale management, force cache
			// TODO add these parameters to cache-control header in every request

			// TODO Update cache.xml xdoc
			
			// TODO support load balancing combined with cache
			// TODO replace "clustering" by "load balancing" in documentation

			CacheConfig cacheConfig = CacheConfigHelper.createCacheConfig(config.getProperties());
			cacheConfig.setSharedCache(true);
			httpClient = new CachingHttpClient(httpClient, (HttpCacheStorage) cacheStorage, cacheConfig);

		}
		ResourceFactory result = new HttpResourceFactory(httpClient);
		return result;
	}

	private ResourceFactoryCreator() {
	}

}
