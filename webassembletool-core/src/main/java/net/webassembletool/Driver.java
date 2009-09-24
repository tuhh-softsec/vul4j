package net.webassembletool;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.webassembletool.cache.Cache;
import net.webassembletool.cache.MemoryOutput;
import net.webassembletool.cache.MemoryResource;
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
import net.webassembletool.resource.ResourceUtils;
import net.webassembletool.tags.BlockRenderer;
import net.webassembletool.tags.TemplateRenderer;
import net.webassembletool.xml.XpathRenderer;
import net.webassembletool.xml.XsltRenderer;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Main class used to retrieve data from a provider application using HTTP requests. Data can be retrieved as binary streams or as String for text data. To improve performance, the Driver uses a cache
 * that can be configured depending on the needs.
 * 
 * @author Francois-Xavier Bonnet
 */
public class Driver {
    private static final Log LOG = LogFactory.getLog(Driver.class);
    private final DriverConfiguration config;
    private final Cache cache;
    private final HttpClient httpClient;

    public Driver(String name, Properties props) {
        config = new DriverConfiguration(name, props);
        // Remote application settings
        if (config.getBaseURL() != null) {
            MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
            connectionManager.getParams().setDefaultMaxConnectionsPerHost(config.getMaxConnectionsPerHost());
            httpClient = new HttpClient(connectionManager);
            httpClient.getParams().setSoTimeout(config.getTimeout());
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(config.getTimeout());
            httpClient.getParams().setConnectionManagerTimeout(config.getTimeout());
            httpClient.getParams().setBooleanParameter(
                    "http.protocol.allow-circular-redirects", true);
        } else
            httpClient = null;
        // Proxy settings
        if (config.getProxyHost() != null) {
            HostConfiguration conf = httpClient.getHostConfiguration();
            conf.setProxy(config.getProxyHost(), config.getProxyPort());
        }
        // Cache
        if (config.isUseCache())
            cache = new Cache(config.getCacheRefreshDelay());
        else
            cache = null;
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
     * Returns the base URL used to retrieve contents from the provider application.
     * 
     * @return the base URL as a String
     */
    public final String getBaseURL() {
        return config.getBaseURL();
    }

    /**
     * Indicates whether 'jsessionid' filtering enabled
     * 
     * @return flag indicating whether 'filterJsessionid' option is turned on in configuration
     */
    public final boolean isFilterJsessionid() {
        return config.isFilterJsessionid();
    }

    /**
     * Retrieves a page from the provider application, evaluates XPath expression if exists, applies XSLT transformation and writes result to a Writer.
     * 
     * @param source
     *            external page used for inclusion
     * @param template
     *            path to the XSLT template (may be <code>null</code>) will be evaluated against current web application context
     * @param out
     *            Writer to write the block to
     * @param originalRequest
     *            original client request
     * @throws IOException
     *             If an IOException occurs while writing to the writer
     * @throws HttpErrorPage 
     *             If an Exception occurs while retrieving the block
     */
	public final void renderXml(String source, String template,
			Writer out, HttpServletRequest originalRequest)
			throws IOException, HttpErrorPage {
		render(source, null, out, originalRequest, false, new XsltRenderer(
				template, originalRequest.getSession().getServletContext()));
	}

    /**
     * Retrieves a page from the provider application, evaluates XPath expression if exists, applies XSLT transformation and writes result to a Writer.
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
	public final void renderXpath(String source, String xpath, Writer out,
			HttpServletRequest originalRequest)
			throws IOException, HttpErrorPage {
		render(source, null, out, originalRequest, false, new XpathRenderer(
				xpath));
	}

    /**
     * Retrieves a block from the provider application and writes it to a Writer. Block can be defined in the provider application using HTML comments.<br />
     * eg: a block name "myblock" should be delimited with "&lt;!--$beginblock$myblock$--&gt;" and "&lt;!--$endblock$myblock$--&gt;
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
     * @param propagateJsessionId
     *            indicates whether <code>jsessionid</code> should be propagated or just removed from generated output
     * @param copyOriginalRequestParameters
     *            indicates whether the original request parameters should be copied in the new request
     * @throws IOException
     *             If an IOException occurs while writing to the writer
     * @throws HttpErrorPage
     *             If an Exception occurs while retrieving the block
     */
    public final void renderBlock(String page, String name, Writer writer, HttpServletRequest originalRequest, Map<String, String> replaceRules, Map<String, String> parameters, boolean propagateJsessionId,
            boolean copyOriginalRequestParameters) throws IOException, HttpErrorPage {
        render(page, parameters, writer, originalRequest, propagateJsessionId, new BlockRenderer(name, page), new ReplaceRenderer(replaceRules));
    }

    /**
     * Retrieves a template from the provider application and renders it to the writer replacing the parameters with the given map. If "name" param is null, the whole page will be used as the
     * template.<br />
     * eg: The template "mytemplate" can be delimited in the provider page by comments "&lt;!--$begintemplate$mytemplate$--&gt;" and "&lt;!--$endtemplate$mytemplate$--&gt;".<br />
     * Inside the template, the parameters can be defined by comments.<br />
     * eg: parameter named "myparam" should be delimited by comments "&lt;!--$beginparam$myparam$--&gt;" and "&lt;!--$endparam$myparam$--&gt;"
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
     *            indicates whether <code>jsessionid</code> should be propagated or just removed from generated output
     * @throws IOException
     *             If an IOException occurs while writing to the writer
     * @throws HttpErrorPage
     *             If an Exception occurs while retrieving the template
     */
    public final void renderTemplate(String page, String name, Writer writer, HttpServletRequest originalRequest, Map<String, String> params, Map<String, String> replaceRules,
            Map<String, String> parameters, boolean propagateJsessionId) throws IOException, HttpErrorPage {
		render(page, parameters, writer, originalRequest, propagateJsessionId,
				new TemplateRenderer(name, params, page), new ReplaceRenderer(replaceRules));
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
     * @param propagateJsessionId
     *            indicates whether <code>jsessionid</code> should be propagated or just removed from generated output
	 * @param renderers
	 *            the renderers to use to transform the output
     * @throws IOException
     *             If an IOException occurs while writing to the writer
     * @throws HttpErrorPage
     *             If an Exception occurs while retrieving the template
	 */
	public final void render(String page, Map<String, String> parameters, Writer writer, HttpServletRequest originalRequest, boolean propagateJsessionId, Renderer... renderers) throws IOException, HttpErrorPage {
        RequestContext target = new RequestContext(this, page, parameters, originalRequest, propagateJsessionId, false);
        StringOutput stringOutput = getResourceAsString(target);
		String currentValue = stringOutput.toString();
		for(Renderer renderer : renderers){
			StringWriter stringWriter = new StringWriter();
			renderer.render(currentValue, stringWriter);
			currentValue = stringWriter.toString();
		}
		writer.write(currentValue);
	}

	/**
	 * Retrieves a resource from the provider application and transforms it
	 * using the Renderer passed as a parameter.
	 * 
	 * On of the Renderers that can be used is the AggregateRenderer that parses
	 * the html pages to find tags to be replaced by contents from other
	 * providers. Sample syntax used for includes :
	 * <ul>
	 * <li>&lt;!--$includeblock$provider$page$blockname$--&gt;</li>
	 * <li>&lt;!--$beginincludetemplate$provider$page$templatename$--&gt;</li>
	 * <li>&lt;!--$beginput$name$--&gt;</li>
	 * </ul>
	 * Sample syntax used inside included contents for template and block
	 * definition :
	 * <ul>
	 * <li>&lt;!--$beginblock$name$--&gt;</li>
	 * <li>&lt;!--$begintemplate$name$--&gt;</li>
	 * <li>&lt;!--$beginparam$name$--&gt;</li>
	 * </ul>
	 * Aggregation is always in "proxy mode" that means cookies or parameters
	 * from the original request are transmitted to the target server. <br/>
	 * <b>NB: Cookies and parameters are not transmitted to templates or blocks
	 * invoked by the page</b>.
	 * 
	 * 
	 * 
	 * @param relUrl
	 *            the relative URL to the resource
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param propagateJsessionId
	 *            indicates whether <code>jsessionid</code> should be propagated
	 *            or just removed from generated output
	 * @param renderers 
	 *            the renderers to use to transform the output
	 * @throws IOException
	 *             If an IOException occurs while writing to the response
	 * @throws HttpErrorPage
	 *             If the page contains incorrect tags
	 */
	public final void proxy(String relUrl, HttpServletRequest request,
			HttpServletResponse response, boolean propagateJsessionId,
			Renderer... renderers) throws IOException, HttpErrorPage {
		RequestContext requestContext = new RequestContext(this, relUrl, null,
				request, propagateJsessionId, true);
		request.setCharacterEncoding(config.getUriEncoding());
		requestContext.setProxyMode(true);
		if (renderers.length == 0) {
			renderResource(requestContext,
					new ResponseOutput(request, response));
		} else {
			// Directly stream out non text data
			TextOnlyStringOutput textOutput = new TextOnlyStringOutput(request, response);
			renderResource(requestContext, textOutput);
			// If data was binary, no text buffer is available and no rendering
			// is needed.
			if (!textOutput.hasTextBuffer()) {
				LOG.debug("'" + relUrl
						+ "' is binary : was forwarded without modification.");
				return;
			}
			LOG.debug("'" + relUrl + "' is text : will apply renderers.");
			String currentValue = textOutput.toString();
			for(Renderer renderer : renderers){
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
			} else
				// Even if Content-type header has been set, some containers like
				// Jetty need the charsetName to be set, if not it will take
				// default value ISO-8859-1
				response.setCharacterEncoding(charsetName);
				response.getWriter().write(currentValue);
		}
	}
	
    
    private final void renderResource(RequestContext target, Output output) {
        String httpUrl = ResourceUtils.getHttpUrlWithQueryString(target);
        MultipleOutput multipleOutput = new MultipleOutput();
        multipleOutput.addOutput(output);
        MemoryResource cachedResource = null;
        HttpResource httpResource = null;
        FileResource fileResource = null;
        MemoryOutput memoryOutput = null;
        FileOutput fileOutput = null;
        try {
            if (config.isUseCache() && target.isCacheable()) {
                // Try to load the resource from cache
                cachedResource = cache.get(httpUrl);
                if (cachedResource == null || cachedResource.isStale()) {
                    // Resource not in cache or stale, prepare a memoryOutput to
                    // collect the new version
                    memoryOutput = new MemoryOutput(config.getCacheMaxFileSize());
                    multipleOutput.addOutput(memoryOutput);
                } else if (cachedResource.isEmpty() || cachedResource.isError()) {
                    // Empty resource in cache because it was too big, or error
                    // we have to reload it
                } else {
                    // Resource in cache, not empty and not stale with no error,
                    // we can render it and return
                    cachedResource.render(multipleOutput);
                    return;
                }
            }
            // Try to load it from HTTP
            if (config.getBaseURL() != null && (cachedResource == null || cachedResource.isStale() || cachedResource.isEmpty())) {
                // Prepare a FileOutput to store the result on the file system
                if (config.isPutInCache() && target.isCacheable()) {
                	fileOutput = new FileOutput(ResourceUtils.getFileUrl(config.getLocalBase(), target));
                    multipleOutput.addOutput(fileOutput);
                }
                httpResource = new HttpResource(httpClient, target);
                if (!httpResource.isError()) {
                    httpResource.render(multipleOutput);
                    return;
                }
            }
            // Resource could not be loaded from HTTP, let's use the expired
            // cache entry if not empty and not error.
            if (cachedResource != null && !cachedResource.isEmpty() && !cachedResource.isError()) {
                cachedResource.render(multipleOutput);
                return;
            }
            // Resource could not be loaded neither from HTTP, nor from the
            // cache, let's try from the file system
            if (config.getLocalBase() != null && target.isCacheable()) {
                fileResource = new FileResource(config.getLocalBase(), target);
                if (!fileResource.isError()) {
                    fileResource.render(multipleOutput);
                    return;
                }
            }
            // Not valid response could be found, let's render the response even
            // if it is an error
            if (httpResource != null) {
                httpResource.render(multipleOutput);
                return;
            } else if (cachedResource != null) {
                cachedResource.render(multipleOutput);
                return;
            } else if (fileResource != null) {
                fileResource.render(multipleOutput);
                return;
            } else
                // Resource could not be loaded at all
                new NullResource().render(multipleOutput);
        } catch (Throwable t) {
			// In case there was a problem during rendering (client abort for
			// exemple), all the output
			// should have been gracefully closed in the render method but we
			// must discard the entry inside the cache or the file system
			// because it is not complete
            if (memoryOutput != null) {
                cache.cancelUpdate(httpUrl);
                memoryOutput = null;
            }
            if (fileOutput !=null)
            	fileOutput.delete();
            throw new ResponseException(httpUrl + " could not be retrieved", t);
        } finally {
            // Free all the resources
            if (cachedResource != null)
                cachedResource.release();
            if (memoryOutput != null)
                cache.put(httpUrl, memoryOutput.toResource());
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
	protected StringOutput getResourceAsString(RequestContext target)
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
