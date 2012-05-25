/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.esigate.http;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

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
import org.esigate.Parameters;
import org.esigate.ResourceFactory;
import org.esigate.cache.CacheAdapter;
import org.esigate.cache.CacheConfigHelper;
import org.esigate.extension.Extension;

public class ResourceFactoryCreator {

	/**
	 * Creates a ResourceFactory on top of a backend HttpClient by adding a cache on it.
	 * 
	 * @param config
	 * @param backend
	 * @return A ResourceFactory
	 */
	static ResourceFactory create(Properties properties, HttpClient backend) {
		HttpClient httpClient;
		boolean useCache = Parameters.USE_CACHE.getValueBoolean(properties);
		if (useCache) {
			httpClient = addCache(properties, backend);
		} else {
			httpClient = backend;
		}
		ResourceFactory result = new HttpResourceFactory(httpClient);
		return result;
	}

	/**
	 * Creates a ResourceFactory on top of a default backend HttpClient by adding a cache on it.
	 * 
	 * @return A ResourceFactory
	 */
	public static ResourceFactory create(Properties properties) {
		DefaultHttpClient backend = createDefaultHttpClient(properties);
		return create(properties, backend);
	}

	private static DefaultHttpClient createDefaultHttpClient(Properties properties) {
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
		connectionManager.setMaxTotal(Parameters.MAX_CONNECTIONS_PER_HOST.getValueInt(properties));
		connectionManager.setDefaultMaxPerRoute(Parameters.MAX_CONNECTIONS_PER_HOST.getValueInt(properties));
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, Parameters.CONNECT_TIMEOUT.getValueInt(properties));
		HttpConnectionParams.setSoTimeout(httpParams, Parameters.SOCKET_TIMEOUT.getValueInt(properties));
		httpParams.setBooleanParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient(connectionManager, httpParams);
		defaultHttpClient.setRedirectStrategy(new RedirectStrategy());

		// Proxy settings
		String proxyHost = Parameters.PROXY_HOST.getValueString(properties);
		if (proxyHost != null) {
			int proxyPort = Parameters.PROXY_PORT.getValueInt(properties);
			String proxyUser = Parameters.PROXY_USER.getValueString(properties);
			if (proxyUser != null) {
				String proxyPassword = Parameters.PROXY_PASSWORD.getValueString(properties);
				defaultHttpClient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(proxyUser, proxyPassword));
			}
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		return defaultHttpClient;
	}

	private static HttpClient addCache(Properties properties, HttpClient backend) {
		String cacheStorageClass = Parameters.CACHE_STORAGE.getValueString(properties);
		// TODO add a test with conditional request where resource is supposed to be transformed and not in cache yet. We should send back the Not modified response (responsibility of the application.
		Object cacheStorage;
		try {
			cacheStorage = Class.forName(cacheStorageClass).newInstance();
		} catch (Exception e) {
			throw new ConfigurationException("Could not instantiate cacheStorageClass", e);
		}
		if (!(cacheStorage instanceof Extension) || !(cacheStorage instanceof HttpCacheStorage))
			throw new ConfigurationException("Cache storage class must implement Extension and HttpCacheStorage interfaces");
		((Extension) cacheStorage).init(properties);
		CacheConfig cacheConfig = CacheConfigHelper.createCacheConfig(properties);
		cacheConfig.setSharedCache(true);
		CacheAdapter cacheAdapter = new CacheAdapter();
		cacheAdapter.init(properties);
		HttpClient cachingHttpClient = cacheAdapter.wrapBackendHttpClient(backend);
		cachingHttpClient = new CachingHttpClient(cachingHttpClient, (HttpCacheStorage) cacheStorage, cacheConfig);
		cachingHttpClient = cacheAdapter.wrapCachingHttpClient(cachingHttpClient);
		return cachingHttpClient;
	}

	private ResourceFactoryCreator() {
	}

}
