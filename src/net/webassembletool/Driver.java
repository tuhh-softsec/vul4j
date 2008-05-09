package net.webassembletool;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import net.webassembletool.ouput.FileOutput;
import net.webassembletool.ouput.MemoryOutput;
import net.webassembletool.ouput.MultipleOutput;
import net.webassembletool.ouput.Output;
import net.webassembletool.ouput.ResponseOutput;
import net.webassembletool.ouput.StringOutput;
import net.webassembletool.resource.FileResource;
import net.webassembletool.resource.HttpResource;
import net.webassembletool.resource.MemoryResource;
import net.webassembletool.resource.ResourceNotFoundException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * Main class used to retrieve data from a provider application using HTTP
 * requests. Data can be retrieved as binary streams or as String for text data.
 * To improve performance, the Driver uses a cache that can be configured
 * depending on the needs.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public final class Driver {
    // TODO improve last-modified management
    private final static Log log = LogFactory.getLog(Driver.class);
    private static HashMap<String, Driver> instances;
    private boolean useCache = true;
    private int cacheRefreshDelay = 0;
    private int cacheMaxFileSize = 0;
    private int timeout = 1000;
    private String baseURL;
    private String localBase;
    private boolean putInCache = false;
    private GeneralCacheAdministrator cache = new GeneralCacheAdministrator();
    private HttpClient httpClient;

    static {
	// Load default settings
	configure();
    }

    @SuppressWarnings("unused")
    private Driver() {
	// Not to be used
    }

    public Driver(Properties props) {
	// Remote application settings
	baseURL = props.getProperty("remoteUrlBase");
	if (baseURL != null) {
	    MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	    int maxConnectionsPerHost = 20;
	    if (props.getProperty("maxConnectionsPerHost") != null)
		maxConnectionsPerHost = Integer.parseInt(props
			.getProperty("maxConnectionsPerHost"));
	    connectionManager.getParams().setDefaultMaxConnectionsPerHost(
		    maxConnectionsPerHost);
	    httpClient = new HttpClient(connectionManager);
	    if (props.getProperty("timeout") != null) {
		timeout = Integer.parseInt(props.getProperty("timeout"));
		httpClient.getParams().setSoTimeout(
			timeout);
		//httpClient.getHttpConnectionManager().getParams().setSoTimeout(
		//	timeout);

		httpClient.getHttpConnectionManager().getParams()
			.setConnectionTimeout(timeout);
	    }
	}
	// Cache settings
	if (props.getProperty("cacheRefreshDelay") != null)
	    cacheRefreshDelay = Integer.parseInt(props
		    .getProperty("cacheRefreshDelay"));
	if (props.getProperty("cacheMaxFileSize") != null)
	    cacheMaxFileSize = Integer.parseInt(props
		    .getProperty("cacheMaxFileSize"));
	// Local file system settings
	localBase = props.getProperty("localBase");
	if (props.getProperty("putInCache") != null)
	    putInCache = Boolean.parseBoolean(props.getProperty("putInCache"));
	if (props.getProperty("useCache") != null)
	    useCache = Boolean.parseBoolean(props.getProperty("useCache"));
    }

    /**
     * Retrieves the default instance of this class that is configured according
     * to the properties file (driver.properties)
     * 
     * @return the default instance
     */
    public final static Driver getInstance() {
	return getInstance("default");
    }

    /**
     * Retrieves the default instance of this class that is configured according
     * to the properties file (driver.properties)
     * 
     * @param instanceName
     *                The name of the instance (corresponding to the prefix in
     *                the driver.properties file)
     * 
     * @return the named instance
     */
    public final static Driver getInstance(String instanceName) {
	if (instances == null)
	    throw new ConfigurationException(
		    "Driver has not been configured and driver.properties file was not found");
	if (instanceName == null)
	    instanceName = "default";
	Driver instance = instances.get(instanceName);
	if (instance == null)
	    throw new ConfigurationException(
		    "No configuration properties found for factory : "
			    + instanceName);
	return instance;
    }

    /**
     * Loads all the instances according to the properties parameter
     * 
     * @param props
     *                properties to use for configuration
     */
    public final static void configure(Properties props) {
	instances = new HashMap<String, Driver>();
	HashMap<String, Properties> driversProps = new HashMap<String, Properties>();
	for (Enumeration enumeration = props.propertyNames(); enumeration
		.hasMoreElements();) {
	    String propertyName = (String) enumeration.nextElement();
	    String prefix;
	    String name;
	    if (propertyName.indexOf(".") < 0) {
		prefix = "default";
		name = propertyName;
	    } else {
		prefix = propertyName.substring(0, propertyName
			.lastIndexOf("."));
		name = propertyName
			.substring(propertyName.lastIndexOf(".") + 1);
	    }
	    Properties driverProperties = driversProps.get(prefix);
	    if (driverProperties == null) {
		driverProperties = new Properties();
		driversProps.put(prefix, driverProperties);
	    }
	    driverProperties.put(name, props.getProperty(propertyName));
	}
	for (Iterator<String> iterator = driversProps.keySet().iterator(); iterator
		.hasNext();) {
	    String name = iterator.next();
	    Properties driverProperties = driversProps.get(name);
	    instances.put(name, new Driver(driverProperties));
	}

    }

    /**
     * Loads all the instances according to default configuration file
     */
    public final static void configure() {
	try {
	    InputStream inputStream = Driver.class
		    .getResourceAsStream("driver.properties");
	    if (inputStream != null) {
		Properties props = new Properties();
		props.load(inputStream);
		configure(props);
	    }
	} catch (IOException e) {
	    throw new ConfigurationException(e);
	}
    }

    /**
     * Retrieves a block from the provider application and writes it to a
     * Writer. Block can be defined in the provider application using HTML
     * comments.<br />
     * eg: a block name "myblock" should be delimited with
     * "&lt;!--$beginblock$myblock$--&gt;" and "&lt;!--$endblock$myblock$--&gt;
     * 
     * @param page
     * @param name
     * @param writer
     * @param context
     * @throws IOException
     */
    public final void renderBlock(String page, String name, Writer writer,
	    Context context) throws IOException {
	String content = getResourceAsString(page, context);
	String beginString = "<!--$beginblock$" + name + "$-->";
	String endString = "<!--$endblock$" + name + "$-->";
	int begin = content.indexOf(beginString);
	int end = content.indexOf(endString);
	if (begin == -1 || end == -1) {
	    log.warn("Block not found: page=" + page + " block=" + name);
	} else {
	    log.debug("Serving block: page=" + page + " block=" + name);
	    writer.write(content, begin, end - begin);
	}
    }

    /**
     * Retrieves a resource from the provider application as binary data and
     * writes it to the response.
     * 
     * @param relUrl
     *                the relative URL to the resource
     * @param request
     *                the request
     * @param response
     *                the response
     * @throws IOException
     */
    public final void renderResource(String relUrl, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {
	try {
	    renderResource(relUrl, new ResponseOutput(request, response),
		    getContext(request));
	} catch (ResourceNotFoundException e) {
	    response.sendError(HttpServletResponse.SC_NOT_FOUND,
		    "Page not found: " + relUrl);
	}
    }

    private final void renderResource(String relUrl, Output output,
	    Context context) throws IOException, ResourceNotFoundException {
	String httpUrl = getUrlForHttpResource(relUrl, context);
	String fileUrl = getUrlForFileResource(relUrl, context);
	MultipleOutput multipleOutput = new MultipleOutput();
	multipleOutput.addOutput(output);
	MemoryResource cachedResource = null;
	HttpResource httpResource = null;
	FileResource fileResource = null;
	try {
	    if (useCache) {
		// Load the resource from cache even if not up to date
		cachedResource = (MemoryResource) cache.getFromCache(httpUrl);
		cachedResource = (MemoryResource) cache.getFromCache(httpUrl,
			cacheRefreshDelay);
	    }
	    if (cachedResource == null)
		throw new NeedsRefreshException(null);
	    cachedResource.render(multipleOutput);
	} catch (NeedsRefreshException e) {
	    boolean cacheUpdated = false;
	    try {
		MemoryOutput memoryOutput = null;
		if (baseURL != null)
		    httpResource = getResourceFromHttp(httpUrl);
		if (httpResource != null) {
		    if (useCache) {
			memoryOutput = new MemoryOutput(cacheMaxFileSize);
			multipleOutput.addOutput(memoryOutput);
		    }
		    if (putInCache)
			multipleOutput.addOutput(new FileOutput(fileUrl));
		    httpResource.render(multipleOutput);
		} else if (cachedResource != null) {
		    cachedResource.render(multipleOutput);
		} else {
		    fileResource = getResourceFromLocal(fileUrl);
		    if (fileResource == null)
			throw new ResourceNotFoundException(relUrl);
		    if (useCache) {
			memoryOutput = new MemoryOutput(cacheMaxFileSize);
			multipleOutput.addOutput(memoryOutput);
		    }
		    fileResource.render(multipleOutput);
		}
		if (memoryOutput != null) {
		    cachedResource = memoryOutput.toResource();
		    cache.putInCache(httpUrl, cachedResource);
		    cacheUpdated = true;
		    if (cachedResource != null)
			cachedResource.release();
		    if (httpResource != null)
			httpResource.release();
		    if (fileResource != null)
			fileResource.release();
		}
	    } finally {
		// The resource was not found in cache so osCache has locked
		// this key. We have to remove the lock.
		if (useCache && !cacheUpdated)
		    cache.cancelUpdate(httpUrl);
	    }
	}
    }

    private final HttpResource getResourceFromHttp(String url)
	    throws HttpException, IOException {
	HttpResource httpResource = new HttpResource(httpClient, url);
	if (httpResource.exists())
	    return httpResource;
	else {
	    httpResource.release();
	    return null;
	}
    }

    private final FileResource getResourceFromLocal(String relUrl) {
	FileResource fileResource = new FileResource(relUrl);
	if (fileResource.exists())
	    return fileResource;
	else {
	    fileResource.release();
	    return null;
	}
    }

    private final String getUrlForFileResource(String relUrl, Context context) {
	StringBuilder url = new StringBuilder("");
	if (localBase != null && relUrl != null
		&& (localBase.endsWith("/") || localBase.endsWith("\\"))
		&& relUrl.startsWith("/")) {
	    url.append(localBase.substring(0, localBase.length() - 1)).append(
		    relUrl);
	} else {
	    url.append(localBase).append(relUrl);
	}

	int index = url.indexOf("?");
	if (index > -1) {
	    url = new StringBuilder(url.substring(0, index));
	}
	if (context != null) {
	    // Append queryString hashcode to supply different cache filenames
	    addContextToQueryString(url, context, true);
	}
	return url.toString();
    }

    private final String getUrlForHttpResource(String relUrl, Context context) {
	StringBuilder url = new StringBuilder();
	if (baseURL != null && relUrl != null && baseURL.endsWith("/")
		&& relUrl.startsWith("/")) {
	    url.append(baseURL.substring(0, baseURL.length() - 1)).append(
		    relUrl);
	} else {
	    url.append(baseURL).append(relUrl);
	}

	if (context != null) {
	    addContextToQueryString(url, context, false);
	}
	return url.toString();
    }

    private final void addContextToQueryString(StringBuilder url,
	    Context context, boolean isFile) {
	StringBuilder queryString = new StringBuilder("");
	for (Map.Entry<String, String> temp : context.getParameterMap()
		.entrySet()) {
	    queryString.append(temp.getKey()).append("=").append(
		    temp.getValue()).append("&");
	}
	if (isFile)
	    url.append("_").append(queryString.toString().hashCode());
	else
	    url.append("?").append(
		    queryString.substring(0, queryString.length() - 1));
    }

    /**
     * Retrieves a template from the provider application and renders it to the
     * writer replacing the parameters with the given map. If "page" param is
     * null, the whole page will be used as the template.<br />
     * eg: The template "mytemplate" can be delimited in the provider page by
     * comments "&lt;!--$begintemplate$mytemplate$--&gt;" and
     * "&lt;!--$endtemplate$mytemplate$--&gt;".<br />
     * Inside the template, the parameters can be defined by comments.<br />
     * eg: parameter named "myparam" should be delimited by comments
     * "&lt;!--$beginparam$myparam$--&gt;" and "&lt;!--$endparam$myparam$--&gt;"
     * 
     * @param page
     * @param name
     * @param writer
     * @param context
     * @param params
     * @throws IOException
     */
    public final void renderTemplate(String page, String name, Writer writer,
	    Context context, Map<String, String> params) throws IOException {
	String content = getResourceAsString(page, context);
	StringBuilder sb = new StringBuilder();
	Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
	if (content != null) {
	    if (name != null) {
		String beginString = "<!--$begintemplate$" + name + "$-->";
		String endString = "<!--$endtemplate$" + name + "$-->";
		int begin = content.indexOf(beginString);
		int end = content.indexOf(endString);
		if (begin == -1 || end == -1) {
		    log.warn("Template not found: page=" + page + " template="
			    + name);
		} else {
		    log.debug("Serving template: page=" + page + " template="
			    + name);
		    sb.append(content, begin + beginString.length(), end);
		}
	    } else {
		log.debug("Serving template: page=" + page);
		sb.append(content);
	    }
	    while (it.hasNext()) {
		Map.Entry<String, String> pairs = it.next();
		String key = pairs.getKey();
		String value = pairs.getValue();
		String beginString = "<!--$beginparam$" + key + "$-->";
		String endString = "<!--$endparam$" + key + "$-->";
		int begin = sb.indexOf(beginString);
		int end = sb.indexOf(endString);
		if (!(begin == -1 || end == -1)) {
		    sb = sb.replace(begin + beginString.length(), end, value);
		}
	    }

	} else {
	    while (it.hasNext()) {
		Map.Entry<String, String> pairs = it.next();
		String value = pairs.getValue();
		sb = sb.append(value);
	    }
	}
	writer.append(sb);
    }

    /**
     * This method returns the content of an url. We check before in the cache
     * if the content is here. If yes, we return the content of the cache. If
     * not, we get it via an HTTP connection and put it in the cache.
     * 
     * @throws IOException
     * @throws HttpException
     */
    private final String getResourceAsString(String relUrl, Context context)
	    throws HttpException, IOException {
	StringOutput stringOutput = new StringOutput();
	try {
	    renderResource(relUrl, stringOutput, context);
	    return stringOutput.toString();
	} catch (ResourceNotFoundException e) {
	    log.error("Page not found: " + relUrl);
	    return "";
	}
    }

    /**
     * Returns the base URL used to retrieve contents from the provider
     * application.
     * 
     * @return the base URL as a String
     */
    public final String getBaseURL() {
	return baseURL;
    }

    private final String getContextKey() {
	return Context.class.getName() + "#" + this.hashCode();
    }

    public final Context getContext(HttpServletRequest request) {
	HttpSession session = request.getSession(false);
	if (session != null)
	    return (Context) session.getAttribute(getContextKey());
	return null;
    }

    public final Context getContext(PageContext pageContext) {
	return getContext((HttpServletRequest) pageContext.getRequest());
    }

    public final void setContext(Context context, HttpServletRequest request) {
	HttpSession session = request.getSession();
	session.setAttribute(getContextKey(), context);
    }

    public final void setContext(Context context, PageContext pageContext,
	    String provider) {
	HttpSession session = ((HttpServletRequest) pageContext.getRequest())
		.getSession();
	session.setAttribute(getContextKey(), context);
    }

    public final void renderBlock(String page, String name,
	    PageContext pageContext) throws IOException {
	renderBlock(page, name, pageContext.getOut(), getContext(pageContext));
    }

    public final void renderTemplate(String page, String name,
	    PageContext pageContext, Map<String, String> params)
	    throws IOException {
	renderTemplate(page, name, pageContext.getOut(),
		getContext(pageContext), params);
    }
}
