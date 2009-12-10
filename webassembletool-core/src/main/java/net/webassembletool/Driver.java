package net.webassembletool;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.webassembletool.authentication.AuthenticationHandler;
import net.webassembletool.cache.Cache;
import net.webassembletool.cache.CacheOutput;
import net.webassembletool.cache.CachedResponse;
import net.webassembletool.cache.Rfc2616;
import net.webassembletool.file.FileOutput;
import net.webassembletool.file.FileResource;
import net.webassembletool.http.HttpResource;
import net.webassembletool.http.ResponseOutput;
import net.webassembletool.output.MultipleOutput;
import net.webassembletool.output.Output;
import net.webassembletool.output.StringOutput;
import net.webassembletool.output.TextOnlyStringOutput;
import net.webassembletool.regexp.ReplaceRenderer;
import net.webassembletool.resource.NullResource;
import net.webassembletool.resource.Resource;
import net.webassembletool.resource.ResourceUtils;
import net.webassembletool.tags.BlockRenderer;
import net.webassembletool.tags.TemplateRenderer;
import net.webassembletool.xml.XpathRenderer;
import net.webassembletool.xml.XsltRenderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

/**
 * Main class used to retrieve data from a provider application using HTTP
 * requests. Data can be retrieved as binary streams or as String for text data.
 * To improve performance, the Driver uses a cache that can be configured
 * depending on the needs.
 * 
 * @author Francois-Xavier Bonnet
 */
public class Driver {
	private static final Log LOG = LogFactory.getLog(Driver.class);
	private final DriverConfiguration config;
	private final Cache cache;
	private final HttpClient httpClient;
	private AuthenticationHandler authenticationHandler;

	public AuthenticationHandler getAuthenticationHandler() {
		return authenticationHandler;
	}

	public Driver(String name, Properties props) {
		LOG.debug("Initializing instance: " + name);
		config = new DriverConfiguration(name, props);
		// Remote application settings
		if (config.getBaseURL() != null) {
			// Create and initialize scheme registry
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			// Create an HttpClient with the ThreadSafeClientConnManager.
			// This connection manager must be used if more than one thread will
			// be using the HttpClient.
			HttpParams httpParams = new BasicHttpParams();
			httpParams.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS,
					config.getMaxConnectionsPerHost());
			httpParams.setLongParameter(ConnManagerPNames.TIMEOUT, config
					.getTimeout());
			httpParams.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
					config.getTimeout());
			httpParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, config
					.getTimeout());
			httpParams.setBooleanParameter(
					ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
			ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(
					httpParams, schemeRegistry);
			httpClient = new DefaultHttpClient(connectionManager, httpParams);
		} else
			httpClient = null;
		// Proxy settings
		if (config.getProxyHost() != null) {
			HttpHost proxy = new HttpHost(config.getProxyHost(), config
					.getProxyPort(), "http");
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
		}
		// Cache
		if (config.isUseCache())
			cache = new Cache();
		else
			cache = null;
		// Authentication handler
		LOG.debug("Using authenticationHandler: "
				+ config.getAuthenticationHandler());
		try {
			authenticationHandler = (AuthenticationHandler) Class.forName(
					config.getAuthenticationHandler()).newInstance();
		} catch (InstantiationException e) {
			throw new ConfigurationException(e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationException(e);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException(e);
		}
		authenticationHandler.init(config.getProperties());
	}

	public final UserContext getContext(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String key = getContextKey();
		UserContext context = (UserContext) session.getAttribute(key);
		if (context == null) {
			context = new UserContext();
			setContext(context, request);
		}
		return context;
	}

	public final void setContext(UserContext context, HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute(getContextKey(), context);
	}

	/**
	 * Returns the base URL used to retrieve contents from the provider
	 * application.
	 * 
	 * @return the base URL as a String
	 */
	public final String getBaseURL() {
		return config.getBaseURL();
	}

	/**
	 * Retrieves a page from the provider application, evaluates XPath
	 * expression if exists, applies XSLT transformation and writes result to a
	 * Writer.
	 * 
	 * @param source
	 *            external page used for inclusion
	 * @param template
	 *            path to the XSLT template (may be <code>null</code>) will be
	 *            evaluated against current web application context
	 * @param out
	 *            Writer to write the block to
	 * @param originalRequest
	 *            original client request
	 * @throws IOException
	 *             If an IOException occurs while writing to the writer
	 * @throws HttpErrorPage
	 *             If an Exception occurs while retrieving the block
	 */
	public final void renderXml(String source, String template, Appendable out,
			HttpServletRequest originalRequest) throws IOException,
			HttpErrorPage {
		render(source, null, out, originalRequest, new XsltRenderer(template,
				originalRequest.getSession().getServletContext()));
	}

	/**
	 * Retrieves a page from the provider application, evaluates XPath
	 * expression if exists, applies XSLT transformation and writes result to a
	 * Writer.
	 * 
	 * @param source
	 *            external page used for inclusion
	 * @param xpath
	 *            XPath expression (may be <code>null</code>)
	 * @param out
	 *            Writer to write the block to
	 * @param originalRequest
	 *            original client request
	 * @throws IOException
	 *             If an IOException occurs while writing to the writer
	 * @throws HttpErrorPage
	 *             If an Exception occurs while retrieving the block
	 */
	public final void renderXpath(String source, String xpath, Appendable out,
			HttpServletRequest originalRequest) throws IOException,
			HttpErrorPage {
		render(source, null, out, originalRequest, new XpathRenderer(xpath));
	}

	/**
	 * Retrieves a block from the provider application and writes it to a
	 * Writer. Block can be defined in the provider application using HTML
	 * comments.<br />
	 * eg: a block name "myblock" should be delimited with
	 * "&lt;!--$beginblock$myblock$--&gt;" and "&lt;!--$endblock$myblock$--&gt;
	 * 
	 * @param page
	 *            Page containing the block
	 * @param name
	 *            Name of the block
	 * @param writer
	 *            Writer to write the block to
	 * @param originalRequest
	 *            original client request
	 * @param replaceRules
	 *            the replace rules to be applied on the block
	 * @param parameters
	 *            Additional parameters
	 * @param copyOriginalRequestParameters
	 *            indicates whether the original request parameters should be
	 *            copied in the new request
	 * @throws IOException
	 *             If an IOException occurs while writing to the writer
	 * @throws HttpErrorPage
	 *             If an Exception occurs while retrieving the block
	 */
	public final void renderBlock(String page, String name, Appendable writer,
			HttpServletRequest originalRequest,
			Map<String, String> replaceRules, Map<String, String> parameters,
			boolean copyOriginalRequestParameters) throws IOException,
			HttpErrorPage {
		render(page, parameters, writer, originalRequest, new BlockRenderer(
				name, page), new ReplaceRenderer(replaceRules));
	}

	/**
	 * Retrieves a template from the provider application and renders it to the
	 * writer replacing the parameters with the given map. If "name" param is
	 * null, the whole page will be used as the template.<br />
	 * eg: The template "mytemplate" can be delimited in the provider page by
	 * comments "&lt;!--$begintemplate$mytemplate$--&gt;" and
	 * "&lt;!--$endtemplate$mytemplate$--&gt;".<br />
	 * Inside the template, the parameters can be defined by comments.<br />
	 * eg: parameter named "myparam" should be delimited by comments
	 * "&lt;!--$beginparam$myparam$--&gt;" and "&lt;!--$endparam$myparam$--&gt;"
	 * 
	 * @param page
	 *            Address of the page containing the template
	 * @param name
	 *            Template name
	 * @param writer
	 *            Writer where to write the result
	 * @param originalRequest
	 *            originating request object
	 * @param params
	 *            Blocks to replace inside the template
	 * @param replaceRules
	 *            The replace rules to be applied on the block
	 * @param parameters
	 *            Parameters to be added to the request
	 * @param propagateJsessionId
	 *            indicates whether <code>jsessionid</code> should be propagated
	 *            or just removed from generated output
	 * @throws IOException
	 *             If an IOException occurs while writing to the writer
	 * @throws HttpErrorPage
	 *             If an Exception occurs while retrieving the template
	 */
	public final void renderTemplate(String page, String name,
			Appendable writer, HttpServletRequest originalRequest,
			Map<String, String> params, Map<String, String> replaceRules,
			Map<String, String> parameters, boolean propagateJsessionId)
			throws IOException, HttpErrorPage {
		render(page, parameters, writer, originalRequest, new TemplateRenderer(
				name, params, page), new ReplaceRenderer(replaceRules));
	}

	/**
	 * @param page
	 *            Address of the page containing the template
	 * @param parameters
	 *            parameters to be added to the request
	 * @param writer
	 *            Writer where to write the result
	 * @param originalRequest
	 *            originating request object
	 * @param renderers
	 *            the renderers to use to transform the output
	 * @throws IOException
	 *             If an IOException occurs while writing to the writer
	 * @throws HttpErrorPage
	 *             If an Exception occurs while retrieving the template
	 */
	public final void render(String page, Map<String, String> parameters,
			Appendable writer, HttpServletRequest originalRequest,
			Renderer... renderers) throws IOException, HttpErrorPage {
		ResourceContext resourceContext = new ResourceContext(this, page,
				parameters, originalRequest);
		resourceContext.setPreserveHost(config.isPreserveHost());
		StringOutput stringOutput = getResourceAsString(resourceContext);
		String currentValue = stringOutput.toString();
		for (Renderer renderer : renderers) {
			StringWriter stringWriter = new StringWriter();
			renderer.render(currentValue, stringWriter);
			currentValue = stringWriter.toString();
		}
		writer.append(currentValue);
	}

	/**
	 * Retrieves a resource from the provider application and transforms it
	 * using the Renderer passed as a parameter.
	 * 
	 * @param relUrl
	 *            the relative URL to the resource
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param renderers
	 *            the renderers to use to transform the output
	 * @throws IOException
	 *             If an IOException occurs while writing to the response
	 * @throws HttpErrorPage
	 *             If the page contains incorrect tags
	 */
	public final void proxy(String relUrl, HttpServletRequest request,
			HttpServletResponse response, Renderer... renderers)
			throws IOException, HttpErrorPage {
		ResourceContext resourceContext = new ResourceContext(this, relUrl,
				null, request);
		request.setCharacterEncoding(config.getUriEncoding());
		resourceContext.setProxy(true);
		resourceContext.setPreserveHost(config.isPreserveHost());
		if (renderers.length == 0) {
			// As we don't have any transformation to apply, we don't even
			// have to retrieve the resource if it is already in browser's
			// cache. So we can use conditional a request like
			// "if-modified-since"
			resourceContext.setNeededForTransformation(false);
			renderResource(resourceContext, new ResponseOutput(response));
		} else {
			// Directly stream out non text data
			TextOnlyStringOutput textOutput = new TextOnlyStringOutput(response);
			renderResource(resourceContext, textOutput);
			// If data was binary, no text buffer is available and no rendering
			// is needed.
			if (!textOutput.hasTextBuffer()) {
				LOG.debug("'" + relUrl
						+ "' is binary : was forwarded without modification.");
				return;
			}
			LOG.debug("'" + relUrl + "' is text : will apply renderers.");
			String currentValue = textOutput.toString();
			for (Renderer renderer : renderers) {
				StringWriter stringWriter = new StringWriter();
				renderer.render(currentValue, stringWriter);
				currentValue = stringWriter.toString();
			}
			// Write the result to the OutpuStream
			String charsetName = textOutput.getCharsetName();
			if (charsetName == null) {
				// No charset was specified in the response, we assume this is
				// ISO-8859-1
				charsetName = "ISO-8859-1";
				// We do not use the Writer because the container may add some
				// unwanted headers like default content-type that may not be
				// consistent with the original response
				response.getOutputStream().write(
						currentValue.getBytes(charsetName));
			} else {
				// Even if Content-type header has been set, some containers
				// like
				// Jetty need the charsetName to be set, if not it will take
				// default value ISO-8859-1
				response.setCharacterEncoding(charsetName);
				response.getWriter().write(currentValue);
			}
		}
	}

	private final void renderResource(ResourceContext resourceContext,
			Output output) {
		String httpUrl = ResourceUtils
				.getHttpUrlWithQueryString(resourceContext);
		MultipleOutput multipleOutput = new MultipleOutput();
		multipleOutput.addOutput(output);
		CachedResponse cachedResource = null;
		Resource httpResource = null;
		FileResource fileResource = null;
		CacheOutput memoryOutput = null;
		FileOutput fileOutput = null;
		try {
			if (config.isUseCache() && Rfc2616.isCacheable(resourceContext)) {
				// Try to load the resource from cache
				cachedResource = cache.get(resourceContext);
				boolean needsValidation = true;
				if (cachedResource != null) {
					needsValidation = false;
					if (config.getCacheRefreshDelay() <= 0) {
						// Auto http cache
						if (Rfc2616.needsValidation(resourceContext,
								cachedResource))
							needsValidation = true;
					} else {
						// Forced expiration delay
						if (Rfc2616.requiresRefresh(resourceContext)
								|| Rfc2616.getAge(cachedResource) > config
										.getCacheRefreshDelay() * 1000)
							needsValidation = true;
					}
				}
				if (LOG.isDebugEnabled()) {
					String message = "Needs validation=" + needsValidation;
					message += " cacheRefreshDelay="
							+ config.getCacheRefreshDelay();
					if (cachedResource != null)
						message += " cachedResource=" + cachedResource;
					LOG.debug(message);
				}
				if (needsValidation) {
					// Resource not in cache or stale, or refresh was forced by
					// the user (hit refresh in the browser so the browser sent
					// a pragma:no-cache header or something similar), prepare a
					// memoryOutput to collect the new version
					memoryOutput = new CacheOutput(config.getCacheMaxFileSize());
					multipleOutput.addOutput(memoryOutput);
				} else {
					// Resource in cache, does not need validation, just render
					// it
					LOG.debug("Reusing cache entry for: " + httpUrl);
					Rfc2616.renderResource(cachedResource, multipleOutput);
					return;
				}
			}
			// Try to load it from HTTP
			if (config.getBaseURL() != null) {
				// Prepare a FileOutput to store the result on the file system
				if (config.isPutInCache()
						&& Rfc2616.isCacheable(resourceContext)) {
					fileOutput = new FileOutput(ResourceUtils.getFileUrl(config
							.getLocalBase(), resourceContext));
					multipleOutput.addOutput(fileOutput);
				}
				Map<String, String> validators = cache.getValidators(
						resourceContext, cachedResource);
				httpResource = new HttpResource(httpClient, resourceContext,
						validators);
				httpResource = cache.select(resourceContext, cachedResource,
						httpResource);
				// If there is an error, we will try to reuse an old cache entry
				if (!httpResource.isError()) {
					Rfc2616.renderResource(httpResource, multipleOutput);
					return;
				}
			}
			// Resource could not be loaded from HTTP, let's use the expired
			// cache entry if not empty and not error.
			if (cachedResource != null && !cachedResource.isError()) {
				Rfc2616.renderResource(cachedResource, multipleOutput);
				return;
			}
			// Resource could not be loaded neither from HTTP, nor from the
			// cache, let's try from the file system
			if (config.getLocalBase() != null
					&& Rfc2616.isCacheable(resourceContext)) {
				fileResource = new FileResource(config.getLocalBase(),
						resourceContext);
				if (!fileResource.isError()) {
					Rfc2616.renderResource(fileResource, multipleOutput);
					return;
				}
			}
			// No valid response could be found, let's render the response even
			// if it is an error
			if (httpResource != null) {
				Rfc2616.renderResource(httpResource, multipleOutput);
				return;
			} else if (cachedResource != null) {
				Rfc2616.renderResource(cachedResource, multipleOutput);
				return;
			} else if (fileResource != null) {
				Rfc2616.renderResource(fileResource, multipleOutput);
				return;
			} else
				// Resource could not be loaded at all
				Rfc2616.renderResource(new NullResource(), multipleOutput);
		} catch (Throwable t) {
			// In case there was a problem during rendering (client abort for
			// exemple), all the output
			// should have been gracefully closed in the render method but we
			// must discard the entry inside the cache or the file system
			// because it is not complete
			if (memoryOutput != null) {
				memoryOutput = null;
			}
			if (fileOutput != null)
				fileOutput.delete();
			throw new ResponseException(httpUrl + " could not be retrieved", t);
		} finally {
			// Free all the resources
			if (cachedResource != null)
				cachedResource.release();
			if (memoryOutput != null)
				cache.put(resourceContext, memoryOutput.toResource());
			if (httpResource != null)
				httpResource.release();
			if (fileResource != null)
				fileResource.release();
		}
	}

	/**
	 * This method returns the content of an url.
	 * 
	 * @param target
	 *            the target resource
	 * @return the content of the url
	 * @throws HttpErrorPage
	 */
	protected StringOutput getResourceAsString(ResourceContext target)
			throws HttpErrorPage {
		StringOutput stringOutput = new StringOutput();
		renderResource(target, stringOutput);
		if (stringOutput.getStatusCode() != HttpServletResponse.SC_OK) {
			throw new HttpErrorPage(stringOutput.getStatusCode(), stringOutput
					.getStatusMessage(), stringOutput.toString());
		}
		return stringOutput;
	}

	private final String getContextKey() {
		return UserContext.class.getName() + "#" + config.getInstanceName();
	}
}
