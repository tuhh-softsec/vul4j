package org.esigate.http;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.esigate.ConfigurationException;
import org.esigate.DriverConfiguration;
import org.esigate.HttpErrorPage;
import org.esigate.ResourceContext;
import org.esigate.ResourceFactory;
import org.esigate.ResponseException;
import org.esigate.cache.Cache;
import org.esigate.cache.CacheOutput;
import org.esigate.cache.CacheStorage;
import org.esigate.cache.CachedResponse;
import org.esigate.file.FileOutput;
import org.esigate.file.FileResource;
import org.esigate.output.MultipleOutput;
import org.esigate.output.Output;
import org.esigate.resource.NullResource;
import org.esigate.resource.Resource;
import org.esigate.resource.ResourceUtils;
import org.esigate.util.Rfc2616;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachedHttpResourceFactory implements ResourceFactory {
	private static final Logger log = LoggerFactory.getLogger(CachedHttpResourceFactory.class);

	private final ResourceFactory parent;
	private final DriverConfiguration config;
	private final Cache cache;

	public CachedHttpResourceFactory(ResourceFactory parent, DriverConfiguration config) {
		this.parent = parent;
		this.config = config;
		this.cache = new Cache();
		try {
			CacheStorage cacheStorage = this.config.getCacheStorageClass().newInstance();
			cacheStorage.init(config.getProperties());
			cache.setStorage(cacheStorage);
		} catch (Exception e) {
			throw new ConfigurationException("Error during initialization CacheStorage", e);
		}
	}

	public Resource getResource(ResourceContext resourceContext) throws HttpErrorPage {
		String httpUrl = ResourceUtils.getHttpUrlWithQueryString(resourceContext);

		ResourceProxyWithContext ret = new ResourceProxyWithContext();
		ret.setResourceContext(resourceContext);
		ret.setCache(cache);
		ret.setHttpUrl(httpUrl);

		try {
			tryLoadFromCache(ret);
			if (!ret.isReady()) {
				tryLoadFromHttp(ret);
			}
			if (!ret.isReady()) {
				tryLoadFromExpiredCache(ret);
			}
			if (!ret.isReady()) {
				tryLoadFromFileSystem(ret);
			}
			if (!ret.isReady()) {
				loadWithError(ret);
			}
			return ret;
		} catch (Throwable t) {
			// In case there was a problem during rendering (client abort for
			// exemple), all the output
			// should have been gracefully closed in the render method but we
			// must discard the entry inside the cache or the file system
			// because it is not complete
			ret.setMemoryOutput(null);

			if (ret.getFileOutput() != null) {
				ret.getFileOutput().delete();
			}
			throw new ResponseException(httpUrl + " could not be retrieved", t);
		}
	}

	private void loadWithError(ResourceProxyWithContext ret) {
		// No valid response could be found, let's render the response even
		// if it is an error
		if (ret.getHttpResource() != null) {
			ret.setProxyResource(ret.getHttpResource());
			ret.setReady(true);
		} else if (ret.getCachedResource() != null) {
			ret.setProxyResource(ret.getHttpResource());
			ret.setReady(true);
		} else if (ret.getFileResource() != null) {
			ret.setProxyResource(ret.getFileResource());
			ret.setReady(true);
		} else {
			ret.setProxyResource(new NullResource());
			ret.setReady(true);
		}
	}

	private void tryLoadFromFileSystem(ResourceProxyWithContext ret)
			throws IOException {
		// Resource could not be loaded neither from HTTP, nor from the
		// cache, let's try from the file system
		if (config.getLocalBase() != null
				&& Rfc2616.isCacheable(ret.getResourceContext())) {
			ret.setFileResource(ResourceUtils.createFileResource(
					config.getLocalBase(), ret.getResourceContext()));
			if (!ret.getFileResource().isError()) {
				// reinit the FileOutput not to overwrite itself
				ret.setFileOutput(null);
				ret.setMemoryOutput(new CacheOutput(config
						.getCacheMaxFileSize()));
				ret.setProxyResource(ret.getFileResource());
				ret.setReady(true);
				log.info("Using filesystem for: " + ret.getHttpUrl());
			}
		}
	}

	private void tryLoadFromExpiredCache(ResourceProxyWithContext ret) {
		// Resource could not be loaded from HTTP, let's use the expired
		// cache entry if not empty and not error.
		if (ret.getCachedResource() != null
				&& !ret.getCachedResource().isError()) {
			ret.setProxyResource(ret.getCachedResource());
			ret.setReady(true);
			log.info("Reusing expired cache entry for: " + ret.getHttpUrl());
		}
	}

	private void tryLoadFromHttp(ResourceProxyWithContext ret)
			throws Exception, HttpErrorPage {
		// Try to load it from HTTP
		if (config.getBaseURL() != null) {
			// Prepare a FileOutput to store the result on the file system
			if (config.isPutInCache()
					&& Rfc2616.isCacheable(ret.getResourceContext())) {
				ret.setFileOutput(ResourceUtils.createFileOutput(
						config.getLocalBase(), ret.getResourceContext()));
			}

			Map<String, String> validators = cache.getValidators(
					ret.getResourceContext(), ret.getCachedResource());
			// ResourceContext resourceContext =
			// ret.getResourceContext().clone();
			ResourceContext resourceContext = ret.getResourceContext();
			Map<String, String> originalValidators = resourceContext
					.getValidators();
			try {
				resourceContext.setValidators(validators);
				ret.setHttpResource(parent
						.getResource(resourceContext));
			} finally {
				resourceContext.setValidators(originalValidators);
			}
			ret.setHttpResource(cache.select(ret.getResourceContext(),
					ret.getCachedResource(), ret.getHttpResource()));

			// If there is an error, we will try to reuse an old cache entry
			if (!ret.getHttpResource().isError()) {
				ret.setProxyResource(ret.getHttpResource());
				ret.setReady(true);
			}
		}
	}

	private void tryLoadFromCache(ResourceProxyWithContext ret) {
		if (!Rfc2616.isCacheable(ret.getResourceContext())) {
			return;
		}

		// Try to load the resource from cache
		ret.setCachedResource(cache.get(ret.getResourceContext()));
		boolean needsValidation = true;
		if (ret.getCachedResource() != null) {
			needsValidation = false;
			if (config.getCacheRefreshDelay() > 0) {
				// Forced expiration delay
				needsValidation = (Rfc2616.requiresRefresh(ret.getResourceContext())
						|| Rfc2616.getAge(ret.getCachedResource()) > config.getCacheRefreshDelay() * 1000L);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Needs validation={} cacheRefreshDelay={} cachedResource={}",
					new Object[] {needsValidation, config.getCacheRefreshDelay(), ret.getCachedResource()});
		}
		if (needsValidation) {
			// Resource not in cache or stale, or refresh was forced by
			// the user (hit refresh in the browser so the browser sent
			// a pragma:no-cache header or something similar), prepare a
			// memoryOutput to collect the new version
			ret.setMemoryOutput(new CacheOutput(config.getCacheMaxFileSize()));
			if (log.isInfoEnabled()) {
				if (ret.getCachedResource() == null) {
					log.info("Not in cache: " + ret.getHttpUrl());
				} else {
					StringBuilder validators = new StringBuilder();
					Set<Entry<String, String>> entrySet = cache.getValidators(
							ret.getResourceContext(), ret.getCachedResource()).entrySet();
					for (Entry<String, String> validator : entrySet) {
						validators.append(validator.getKey())
								.append(": ")
								.append(validator.getValue())
								.append(";");
					}
					log.info("Cache entry needs validation: {}; validators: {}", ret.getHttpUrl(), validators);
				}
			}
		} else {
			// Resource in cache, does not need validation, just render
			// it
			log.info("Reusing cache entry for: " + ret.getHttpUrl());
			ret.setProxyResource(ret.getCachedResource());
			ret.setReady(true);
		}
	}

	private static class ResourceProxyWithContext extends Resource {
		private Resource resource;
		private CachedResponse cachedResource = null;
		private Resource httpResource = null;
		private FileResource fileResource = null;
		private CacheOutput memoryOutput = null;
		private FileOutput fileOutput = null;
		private ResourceContext resourceContext;
		private Cache cache = null;
		private String httpUrl = null;
		private boolean ready = false;

		public String getHttpUrl() {
			return httpUrl;
		}

		public void setHttpUrl(String httpUrl) {
			this.httpUrl = httpUrl;
		}

		public boolean isReady() {
			return ready;
		}

		public void setReady(boolean ready) {
			this.ready = ready;
		}

		public CachedResponse getCachedResource() {
			return cachedResource;
		}

		public void setProxyResource(Resource resource) {
			this.resource = resource;
		}

		public void setCachedResource(CachedResponse cachedResource) {
			this.cachedResource = cachedResource;
		}

		public Resource getHttpResource() {
			return httpResource;
		}

		public void setHttpResource(Resource httpResource) {
			this.httpResource = httpResource;
		}

		public FileResource getFileResource() {
			return fileResource;
		}

		public void setFileResource(FileResource fileResource) {
			this.fileResource = fileResource;
		}

		public void setMemoryOutput(CacheOutput memoryOutput) {
			this.memoryOutput = memoryOutput;
		}

		public FileOutput getFileOutput() {
			return fileOutput;
		}

		public void setFileOutput(FileOutput fileOutput) {
			this.fileOutput = fileOutput;
		}

		public ResourceContext getResourceContext() {
			return resourceContext;
		}

		public void setResourceContext(ResourceContext resourceContext) {
			this.resourceContext = resourceContext;
		}

		public void setCache(Cache cache) {
			this.cache = cache;
		}

		@Override
		public void render(Output output) throws IOException {
			MultipleOutput multipleOutput = new MultipleOutput();
			multipleOutput.addOutput(output);
			if (null != this.memoryOutput) {
				multipleOutput.addOutput(this.memoryOutput);
			}
			if (null != this.fileOutput) {
				multipleOutput.addOutput(this.fileOutput);
			}
			resource.render(multipleOutput);
		}

		@Override
		public void release() {
			// Free all the resources

			if (cachedResource != null) {
				cachedResource.release();
			}
			if (memoryOutput != null && cache != null) {
				cache.put(resourceContext, memoryOutput.toResource());
			}
			if (httpResource != null) {
				httpResource.release();
			}
			if (fileResource != null) {
				fileResource.release();
			}
		}

		@Override
		public int getStatusCode() {
			return resource.getStatusCode();
		}

		@Override
		public String getHeader(String name) {
			return resource.getHeader(name);
		}

		@Override
		public Collection<String> getHeaders(String name) {
			return resource.getHeaders(name);
		}

		@Override
		public Collection<String> getHeaderNames() {
			return resource.getHeaderNames();
		}

		@Override
		public boolean isError() {
			return resource.isError();
		}

		@Override
		public int hashCode() {
			return resource.hashCode();
		}

		@Override
		public String getRequestHeader(String name) {
			return resource.getRequestHeader(name);
		}

		@Override
		public boolean hasResponseBody() {
			return resource.hasResponseBody();
		}

		@Override
		public Date getLocalDate() {
			return resource.getLocalDate();
		}

		@Override
		public boolean equals(Object obj) {
			return resource.equals(obj);
		}

		@Override
		public String toString() {
			return resource.toString();
		}

		@Override
		public String getStatusMessage() {
			return resource.getStatusMessage();
		}

	}
}
