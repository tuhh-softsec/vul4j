package net.webassembletool;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.xpath.XPathExpressionException;
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
import net.webassembletool.parse.AggregateRenderer;
import net.webassembletool.parse.BlockRenderer;
import net.webassembletool.parse.Renderer;
import net.webassembletool.parse.TemplateRenderer;
import net.webassembletool.parse.XsltRenderer;
import net.webassembletool.resource.NullResource;
import net.webassembletool.resource.ResourceUtils;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Main class used to retrieve data from a provider application using HTTP requests. Data can be retrieved as binary streams or as String for text data. To improve performance, the Driver uses a cache
 * that can be configured depending on the needs.
 * 
 * @author François-Xavier Bonnet
 */
public class Driver {
    // TODO: write a tokenizer class to avoid String.indexOf usage in the
    // driver.
    // TODO: proxy mode option for taglibs, aggregator and proxy, recursive or
    // not for aggregator
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
    public boolean isFilterJsessionid() {
        return config.isFilterJsessionid();
    }

    /**
     * Retrieves a page from the provider application, evaluates XPath expression if exists, applies XSLT transformation and writes result to a Writer.
     * 
     * @param source
     *            external page used for inclusion
     * @param xpath
     *            XPath expression (may be <code>null</code>)
     * @param template
     *            path to the XSLT template (may be <code>null</code>) will be evaluated against current web application context
     * @param out
     *            Writer to write the block to
     * @param originalRequest
     *            original client request
     * @param ctx
     *            hosting web application context
     * @throws IOException
     *             If an IOException occurs while writing to the writer
     * @throws RenderingException
     *             If an Exception occurs while retrieving the block
     */
    public void renderXml(String source, String xpath, String template, Writer out, HttpServletRequest originalRequest, ServletContext ctx) throws IOException, RenderingException {
        RequestContext target = new RequestContext(this, source, null, originalRequest);
        StringOutput stringOutput = getResourceAsString(target);
        try {
            Renderer renderer = XsltRenderer.builder().xpath(xpath).template(template, ctx).result();
            renderer.render(stringOutput, out, null);
        } catch (XPathExpressionException e) {
            throw new ProcessingFailedException("failed to compile XPath expression", e);
        } catch (TransformerConfigurationException e) {
            throw new ProcessingFailedException("failed to create XSLT template", e);
        }
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
     * @throws RenderingException
     *             If an Exception occurs while retrieving the block
     */
    public void renderBlock(String page, String name, Writer writer, HttpServletRequest originalRequest, Map<String, String> replaceRules, Map<String, String> parameters, boolean propagateJsessionId,
            boolean copyOriginalRequestParameters) throws IOException, RenderingException {
        RequestContext target = new RequestContext(this, page, parameters, originalRequest, propagateJsessionId, copyOriginalRequestParameters);
        StringOutput stringOutput = getResourceAsString(target);
        Renderer renderer = new BlockRenderer(name, page);
        renderer.render(stringOutput, writer, replaceRules);
    }

    public void renderBlock(String page, String name, Writer writer, HttpServletRequest originalRequest, Map<String, String> replaceRules, Map<String, String> parameters, boolean propagateJsessionId)
            throws IOException, RenderingException {
        renderBlock(page, name, writer, originalRequest, replaceRules, parameters, propagateJsessionId, false);
    }

    public void renderBlock(String page, String name, Writer writer, HttpServletRequest originalRequest, Map<String, String> replaceRules, Map<String, String> parameters) throws IOException,
            RenderingException {
        renderBlock(page, name, writer, originalRequest, replaceRules, parameters, false, false);
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
     *            Parameters to be added to the request (TODO: unused???)
     * @param propagateJsessionId
     *            indicates whether <code>jsessionid</code> should be propagated or just removed from generated output
     * @throws IOException
     *             If an IOException occurs while writing to the writer
     * @throws RenderingException
     *             If an Exception occurs while retrieving the template
     */
    public void renderTemplate(String page, String name, Writer writer, HttpServletRequest originalRequest, Map<String, String> params, Map<String, String> replaceRules,
            Map<String, String> parameters, boolean propagateJsessionId) throws IOException, RenderingException {
        RequestContext target = new RequestContext(this, page, parameters, originalRequest, propagateJsessionId);
        StringOutput stringOutput = getResourceAsString(target);
        Renderer renderer = new TemplateRenderer(name, params, page);
        renderer.render(stringOutput, writer, replaceRules);
    }

    /**
     * Retrieves a resource from the provider application as binary data and writes it to the response.
     * 
     * @param relUrl
     *            the relative URL to the resource
     * @param request
     *            the request
     * @param response
     *            the response
     * @param parameters
     *            Additional parameters that will be added to the request
     * @param propagateJsessionId
     *            indicates whether <code>jsessionid</code> should be propagated or just removed from generated output
     * @throws IOException
     *             If an IOException occurs while rendering the response
     */
    public final void proxy(String relUrl, HttpServletRequest request, HttpServletResponse response, Map<String, String> parameters, boolean propagateJsessionId) throws IOException {
        RequestContext requestContext = new RequestContext(this, relUrl, parameters, request, propagateJsessionId);
        request.setCharacterEncoding(config.getUriEncoding());
        requestContext.setProxyMode(true);
        renderResource(requestContext, new ResponseOutput(request, response));
    }

    public final void proxy(String relUrl, HttpServletRequest request, HttpServletResponse response, Map<String, String> parameters) throws IOException {
        proxy(relUrl, request, response, parameters, false);
    }

    /**
     * Retrieves a resource from the provider application and parses it to find tags to be replaced by contents from other providers. Sample syntax used for includes :
     * <ul>
     * <li>&lt;!--$includeblock$provider$page$blockname$--&gt;</li>
     * <li>&lt;!--$beginincludetemplate$provider$page$templatename$--&gt;</li>
     * <li>&lt;!--$beginput$name$--&gt;</li>
     * </ul>
     * Sample syntax used inside included contents for template and block definition :
     * <ul>
     * <li>&lt;!--$beginblock$name$--&gt;</li>
     * <li>&lt;!--$begintemplate$name$--&gt;</li>
     * <li>&lt;!--$beginparam$name$--&gt;</li>
     * </ul>
     * Aggregation is always in "proxy mode" that means cookies or parameters from the original request are transmitted to the target server. <br/>
     * <b>NB: Cookies and parameters are not transmitted to templates or blocks invoked by the page</b>.
     * 
     * @param relUrl
     *            the relative URL to the resource
     * @param request
     *            the request
     * @param response
     *            the response
     * @param propagateJsessionId
     *            indicates whether <code>jsessionid</code> should be propagated or just removed from generated output
     * @throws IOException
     *             If an IOException occurs while writing to the response
     * @throws RenderingException
     *             If the page contains incorrect tags
     */
    public final void aggregate(String relUrl, HttpServletRequest request, HttpServletResponse response, boolean propagateJsessionId) throws IOException, RenderingException {
        RequestContext target = new RequestContext(this, relUrl, null, request, propagateJsessionId, true);
        request.setCharacterEncoding(config.getUriEncoding());
        target.setProxyMode(true);
        // Directly stream out non text data
        TextOnlyStringOutput textOutput = new TextOnlyStringOutput(new ResponseOutput(request, response));
        renderResource(target, textOutput);
        // If data was binary, no text buffer is available and no rendering is
        // needed.
        if (!textOutput.hasTextBuffer()) {
            Driver.LOG.debug("'" + relUrl + "' is binary : was forwarded without aggregation.");
            return;
        }
        Driver.LOG.debug("'" + relUrl + "' is text : will be aggregated.");
        Renderer renderer = new AggregateRenderer(response, target.getOriginalRequest(), propagateJsessionId);
        renderer.render(textOutput, null, null);
    }

    private final void renderResource(RequestContext target, Output output) throws IOException {
        String httpUrl = ResourceUtils.getHttpUrlWithQueryString(target);
        MultipleOutput multipleOutput = new MultipleOutput();
        multipleOutput.addOutput(output);
        MemoryResource cachedResource = null;
        HttpResource httpResource = null;
        FileResource fileResource = null;
        MemoryOutput memoryOutput = null;
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
                if (config.isPutInCache() && target.isCacheable())
                    multipleOutput.addOutput(new FileOutput(ResourceUtils.getFileUrl(config.getLocalBase(), target)));
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
     * @throws IOException
     */
    protected StringOutput getResourceAsString(RequestContext target) throws IOException {
        StringOutput stringOutput = new StringOutput();
        renderResource(target, stringOutput);
        return stringOutput;
    }

    private final String getContextKey() {
        return UserContext.class.getName() + "#" + config.getInstanceName();
    }
}
