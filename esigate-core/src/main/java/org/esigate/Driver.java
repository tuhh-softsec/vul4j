/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.esigate;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;
import org.esigate.api.HttpStatusConstants;
import org.esigate.authentication.AuthenticationHandler;
import org.esigate.cookie.CustomCookieStore;
import org.esigate.extension.ExtensionFactory;
import org.esigate.filter.Filter;
import org.esigate.http.ResourceFactoryCreator;
import org.esigate.output.Output;
import org.esigate.output.StringOutput;
import org.esigate.output.TextOnlyStringOutput;
import org.esigate.regexp.ReplaceRenderer;
import org.esigate.renderers.ResourceFixupRenderer;
import org.esigate.resource.Resource;
import org.esigate.resource.ResourceUtils;
import org.esigate.tags.BlockRenderer;
import org.esigate.tags.TemplateRenderer;
import org.esigate.util.Rfc2616;
import org.esigate.vars.VariablesResolver;
import org.esigate.xml.XpathRenderer;
import org.esigate.xml.XsltRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class used to retrieve data from a provider application using HTTP
 * requests. Data can be retrieved as binary streams or as String for text data.
 * To improve performance, the Driver uses a cache that can be configured
 * depending on the needs.
 * 
 * @author Francois-Xavier Bonnet
 * @contributor Nicolas Richeton
 */
public class Driver {
	private static final Logger LOG = LoggerFactory.getLogger(Driver.class);
	private final DriverConfiguration config;
	private final AuthenticationHandler authenticationHandler;
	private final Filter filter;
	private final ResourceFactory resourceFactory;
	private final ExtensionFactory extension;

	public Driver(String name, Properties props) {
		LOG.info("Initializing instance: " + name);
		config = new DriverConfiguration(name, props);
		// Remote application settings
		
		this.resourceFactory = ResourceFactoryCreator.create(config);
		
		extension = new ExtensionFactory(config);

		// Authentication handler
		authenticationHandler = extension
				.getExtension(AuthenticationHandler.class);
		// Filter
		Filter f = extension.getExtension(Filter.class);
		filter = (f != null) ? f : Filter.EMPTY;
	}

	public AuthenticationHandler getAuthenticationHandler() {
		return authenticationHandler;
	}

	/**
	 * Get current user context in session or request. Context will be saved to
	 * session only if not empty.
	 * 
	 * @param request
	 *            http request
	 * 
	 * @return UserContext
	 */
	public final UserContext getUserContext(HttpRequest request) {
		String key = getContextKey();
		UserContext context = (UserContext) request.getAttribute(key);
		if (context == null) {
			org.esigate.api.HttpSession session = request.getSession(false);
			if (session != null) {
				context = (UserContext) session.getAttribute(key);
			}
			if (context == null) {
				context = createNewUserContext();
			}
			request.setAttribute(key, context);
		}
		return context;
	}

	/**
	 * Return a new and initialized user context.
	 * 
	 * @return UserContext
	 */
	private UserContext createNewUserContext() {
		UserContext context = new UserContext(
				extension.getExtension(CustomCookieStore.class));
		return context;
	}

	/**
	 * Save user context to session only if not empty.
	 * 
	 * @param request
	 *            http request.
	 */
	public final void saveUserContext(HttpRequest request) {
		String key = getContextKey();
		UserContext context = (UserContext) request.getAttribute(key);
		if (context != null && !context.isEmpty()) {
			org.esigate.api.HttpSession session = request.getSession(true);
			Object sessionContext = session.getAttribute(key);
			if (sessionContext == null || sessionContext != context) {
				if (LOG.isInfoEnabled()) {
					LOG.info("Provider=" + config.getInstanceName()
							+ " saving context to session : "
							+ context.toString().replaceAll("\n", ""));
				}
				session.setAttribute(key, context);
			}
		}
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
	 * @param request
	 *            original client request
	 * @throws IOException
	 *             If an IOException occurs while writing to the writer
	 * @throws HttpErrorPage
	 *             If an Exception occurs while retrieving the block
	 */
	public final void renderXml(String source, String template, Appendable out,
			HttpRequest request, HttpResponse response) throws IOException, HttpErrorPage {
		LOG.info("renderXml provider=" + config.getInstanceName() + " source=" + source + " template=" + template);
		render(source, null, out, request, response,
				new XsltRenderer(template, request));
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
	 * @param request
	 *            original client request
	 * @throws IOException
	 *             If an IOException occurs while writing to the writer
	 * @throws HttpErrorPage
	 *             If an Exception occurs while retrieving the block
	 */
	public final void renderXpath(String source, String xpath, Appendable out,
			HttpRequest request, HttpResponse response) throws IOException, HttpErrorPage {
		LOG.info("renderXpath provider=" + config.getInstanceName()
				+ " source=" + source + " xpath=" + xpath);
		render(source, null, out,request, response,
				new XpathRenderer(xpath));
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
	 * @param request
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
			HttpRequest request,
			HttpResponse response,
			Map<String, String> replaceRules, Map<String, String> parameters,
			boolean copyOriginalRequestParameters) throws IOException,
			HttpErrorPage {
		LOG.info("renderBlock provider=" + config.getInstanceName() + " page=" + page + " name=" + name);
		render(page, parameters, writer, request, response,
				new BlockRenderer(name, page), new ReplaceRenderer(replaceRules));
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
	 * @param request
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
			Appendable writer, HttpRequest request,
			HttpResponse response, Map<String, String> params,
			Map<String, String> replaceRules, Map<String, String> parameters,
			boolean propagateJsessionId) throws IOException, HttpErrorPage {
		LOG.info("renderTemplate provider=" + config.getInstanceName()
				+ " page=" + page + " name=" + name);
		render(page, parameters, writer, request, response,
				new TemplateRenderer(name, params, page), new ReplaceRenderer(replaceRules));
	}

	public final void renderEsi(String page, Appendable writer,
			HttpRequest request, HttpResponse response) throws IOException, HttpErrorPage {
		render(page, null, writer, request, response);
	}

	public final void render(String page, Appendable writer, ResourceContext parent, Renderer... renderers)
			throws IOException, HttpErrorPage {
		render(page, parent.getParameters(), writer, parent.getOriginalRequest(), parent.getOriginalResponse(), renderers);
	}
	/**
	 * @param page
	 *            Address of the page containing the template
	 * @param parameters
	 *            parameters to be added to the request
	 * @param writer
	 *            Writer where to write the result
	 * @param request
	 *            originating request object
	 * @param renderers
	 *            the renderers to use to transform the output
	 * @throws IOException
	 *             If an IOException occurs while writing to the writer
	 * @throws HttpErrorPage
	 *             If an Exception occurs while retrieving the template
	 */
	protected final void render(String page, Map<String, String> parameters,
			Appendable writer, HttpRequest request, HttpResponse response, Renderer... renderers)
			throws IOException, HttpErrorPage {
		if (LOG.isInfoEnabled()) {
			List<String> rendererNames = new ArrayList<String>(renderers.length);
			for (Renderer renderer : renderers) {
				rendererNames.add(renderer.getClass().getName());
			}
			LOG.info("render provider={} page= {} renderers={}", new Object[] { config.getInstanceName(), page, rendererNames});
		}
		String resultingpage = VariablesResolver.replaceAllVariables(page, request);
		ResourceContext resourceContext = new ResourceContext(this,
				resultingpage, parameters, request, response);
		resourceContext.setPreserveHost(config.isPreserveHost());
		StringOutput stringOutput = getResourceAsString(resourceContext);
		String currentValue = stringOutput.toString();

		// Fix resources
		if (config.isFixResources()) {
			ResourceFixupRenderer fixup = new ResourceFixupRenderer(
					config.getBaseURL(), config.getVisibleBaseURL(), page,
					config.getFixMode());
			StringWriter stringWriter = new StringWriter();
			fixup.render(resourceContext, currentValue, stringWriter);
			currentValue = stringWriter.toString();
		}

		// Process all renderers
		for (Renderer renderer : renderers) {
			StringWriter stringWriter = new StringWriter();
			renderer.render(resourceContext, currentValue, stringWriter);
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
	public final void proxy(String relUrl,
			HttpRequest request, HttpResponse response,
			Renderer... renderers) throws IOException, HttpErrorPage {
		LOG.info("proxy provider={} relUrl={}", config.getInstanceName(), relUrl);

		ResourceContext resourceContext = new ResourceContext(this, relUrl, null, request, response);
		request.setCharacterEncoding(config.getUriEncoding());
		resourceContext.setProxy(true);
		resourceContext.setPreserveHost(config.isPreserveHost());
		if (!authenticationHandler.beforeProxy(resourceContext)) {
			return;
		}

		if (renderers.length == 0 && !config.isFixResources()) {
			// As we don't have any transformation to apply, we don't even
			// have to retrieve the resource if it is already in browser's
			// cache. So we can use conditional a request like
			// "if-modified-since"
			resourceContext.setNeededForTransformation(false);
			renderResource(resourceContext, new ResponseOutput(response));
		} else {
			// Directly stream out non text data
			TextOnlyStringOutput textOutput = new TextOnlyStringOutput(
					response, this.config.getParsableContentTypes());
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

			List<Renderer> listOfRenderers = new ArrayList<Renderer>(
					renderers.length + 1);
			if (config.isFixResources()) {
				ResourceFixupRenderer fixup = new ResourceFixupRenderer(
						config.getBaseURL(), config.getVisibleBaseURL(),
						relUrl, config.getFixMode());
				listOfRenderers.add(fixup);
			}
			listOfRenderers.addAll(Arrays.asList(renderers));

			for (Renderer renderer : listOfRenderers) {
				StringWriter stringWriter = new StringWriter();
				renderer.render(resourceContext, currentValue, stringWriter);
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

	protected void renderResource(ResourceContext resourceContext, Output output)
			throws HttpErrorPage {
		Resource resource = null;
		try {
			resource = this.resourceFactory.getResource(resourceContext);
			try {
				Rfc2616.renderResource(config, resource, output);
			} catch (ResponseOutputStreamException e) {
				if (LOG.isInfoEnabled()) {
					Throwable t = e.getCause();
					LOG.info("Client or network problem, ignoring: {} {}", t.getClass().getName(), t.getMessage());
				}
			} catch (IOException e) {
				StringWriter out = new StringWriter();
				e.printStackTrace(new PrintWriter(out));
				HttpErrorPage httpErrorPage = new HttpErrorPage(
						HttpStatusConstants.SC_BAD_GATEWAY, e.getMessage(),
						out.toString());
				httpErrorPage.initCause(e);
				throw httpErrorPage;
			}
		} finally {
			if (null != resource) {
				resource.release();
			}
		}

	}

	/**
	 * This method returns the content of an url as a StringOutput. The result
	 * is cached into the request scope in order not to send several requests if
	 * you need several blocks in the same page to build the final page.
	 * 
	 * @param target
	 *            the target resource
	 * @return the content of the url
	 * @throws HttpErrorPage
	 */
	protected StringOutput getResourceAsString(ResourceContext target) throws HttpErrorPage {
		StringOutput result = null;
		String url = ResourceUtils.getHttpUrlWithQueryString(target);
		org.esigate.api.HttpRequest request = target.getOriginalRequest();
		boolean cacheable = Rfc2616.isCacheable(target);
		if (cacheable) {
			result = (StringOutput) request.getAttribute(url);
		}
		if (result == null) {
			result = new StringOutput();
			renderResource(target, result);
			if (cacheable) {
				request.setAttribute(url, result);
			}
		}
		return result;
	}
	
	/**
	 * This method returns the content of an url as a String. The result
	 * is cached into the request scope in order not to send several requests if
	 * you need several blocks in the same page to build the final page.
	 * 
	 * @param page
	 *            Address of the page containing the template
	 * @param ctx
	 *           target resource
	 * @throws HttpErrorPage
	 *             If an Exception occurs while retrieving the template
	 * @return the content of the url
	 * @throws HttpErrorPage
	 */
	public String getResourceAsString(String page, ResourceContext ctx) throws HttpErrorPage{
		String actualPage = VariablesResolver.replaceAllVariables(page, ctx.getOriginalRequest());
		ResourceContext resourceContext = new ResourceContext(this, actualPage, 
				null, ctx.getOriginalRequest(), ctx.getOriginalResponse());
		resourceContext.setPreserveHost(getConfiguration().isPreserveHost());
		StringOutput stringOutput = getResourceAsString(resourceContext);
		String currentValue = stringOutput.toString();
		return currentValue;
		
	}
	
	private final String getContextKey() {
		return UserContext.class.getName() + "#" + config.getInstanceName();
	}

	/**
	 * Returns {@linkplain Filter} instance configured for this driver. Never
	 * returns <code>null</code>.
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * Get current driver configuration.
	 * <p>
	 * This method is not intended to get a WRITE access to the configuration.
	 * <p>
	 * This may be supported in future versions (testing is needed). For the
	 * time being, changing configuration settings after getting access through
	 * this method is <b>UNSUPPORTED</b> and <b>SHOULD NOT</b> be used.
	 * 
	 * @return current configuration
	 */
	public DriverConfiguration getConfiguration() {
		return config;
	}

}
