package net.webassembletool;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.webassembletool.aggregator.AggregationSyntaxException;
import net.webassembletool.cache.Cache;
import net.webassembletool.cache.MemoryOutput;
import net.webassembletool.cache.MemoryResource;
import net.webassembletool.file.FileOutput;
import net.webassembletool.file.FileResource;
import net.webassembletool.http.HttpResource;
import net.webassembletool.http.ResponseOutput;
import net.webassembletool.ouput.MultipleOutput;
import net.webassembletool.ouput.Output;
import net.webassembletool.ouput.StringOutput;
import net.webassembletool.resource.NullResource;
import net.webassembletool.resource.Resource;
import net.webassembletool.resource.ResourceUtils;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    // TODO write a tokenizer class to avoid String.indexOf usage in the driver.
    // TODO proxy mode option for taglibs, aggregator and proxy, recursive or
    // not for aggregator
    private final static Log log = LogFactory.getLog(Driver.class);
    private static HashMap<String, Driver> instances;
    private DriverConfiguration config;
    private Cache cache;
    private HttpClient httpClient;

    static {
	// Load default settings
	configure();
    }

    public Driver(String name, Properties props) {
	config = new DriverConfiguration(name, props);
	// Remote application settings
	if (config.getBaseURL() != null) {
	    MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	    connectionManager.getParams().setDefaultMaxConnectionsPerHost(
		    config.getMaxConnectionsPerHost());
	    httpClient = new HttpClient(connectionManager);
	    httpClient.getParams().setSoTimeout(config.getTimeout());
	    httpClient.getHttpConnectionManager().getParams()
		    .setConnectionTimeout(config.getTimeout());
	}
	// Proxy settings
	if (config.getProxyHost() != null) {
	    HostConfiguration conf = httpClient.getHostConfiguration();
	    conf.setProxy(config.getProxyHost(), config.getProxyPort());
	}
	// Cache
	if (config.isUseCache())
	    cache = new Cache(config.getCacheRefreshDelay());
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
     * @param instanceName The name of the instance (corresponding to the prefix
     *            in the driver.properties file)
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
     * @param props properties to use for configuration
     */
    public final static void configure(Properties props) {
	instances = new HashMap<String, Driver>();
	HashMap<String, Properties> driversProps = new HashMap<String, Properties>();
	for (Enumeration<?> enumeration = props.propertyNames(); enumeration
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
	    instances.put(name, new Driver(name, driverProperties));
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
     * comments.<br /> eg: a block name "myblock" should be delimited with
     * "&lt;!--$beginblock$myblock$--&gt;" and "&lt;!--$endblock$myblock$--&gt;
     * 
     * @param page Page containing the block
     * @param name Name of the block
     * @param writer Writer to write the block to
     * @param context User context
     * @param replaceRules the replace rules to be applied on the block
     * @param parameters Additional parameters
     * @throws IOException If an IOException occurs while writing to the writer
     */
    public final void renderBlock(String page, String name, Writer writer,
	    Context context, Map<String, String> replaceRules,
	    Map<String, String> parameters) throws IOException {
	Target target = new Target(page, context, parameters);
	String content = getResourceAsString(target).toString();
	if (content == null)
	    return;
	String beginString = "<!--$beginblock$" + name + "$-->";
	String endString = "<!--$endblock$" + name + "$-->";
	int begin = content.indexOf(beginString);
	int end = content.indexOf(endString);
	if (begin == -1 || end == -1) {
	    log.warn("Block not found: page=" + page + " block=" + name);
	} else {
	    log.debug("Serving block: page=" + page + " block=" + name);
	    writer.append(StringUtils.replace(content.substring(begin
		    + beginString.length(), end), replaceRules));
	}
    }

    /**
     * Retrieves a resource from the provider application as binary data and
     * writes it to the response.
     * 
     * @param relUrl the relative URL to the resource
     * @param request the request
     * @param response the response
     * @param parameters Additional parameters that will be added to the request
     * @throws IOException If an IOException occurs while rendering the response
     */
    public final void renderResource(String relUrl, HttpServletRequest request,
	    HttpServletResponse response, Map<String, String> parameters)
	    throws IOException {
	Target target = new Target(relUrl, getContext(request), parameters,
		request);
	request.setCharacterEncoding(config.getUriEncoding());
	target.setProxyMode(true);
	renderResource(target, new ResponseOutput(request, response));
    }

    private final void renderResource(Target target, Output output)
	    throws IOException {
	target.setBaseUrl(config.getBaseURL());
	String httpUrl = ResourceUtils.getHttpUrlWithQueryString(config
		.getBaseURL(), target);
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
		if (cachedResource != null && !cachedResource.isStale()) {
		    cachedResource.render(multipleOutput);
		    return;
		} else {
		    // Resource not in cache or stale
		    memoryOutput = new MemoryOutput(config
			    .getCacheMaxFileSize());
		    multipleOutput.addOutput(memoryOutput);
		}
	    }
	    // Resource not in cache or stale or cache not activated, try to
	    // load it from HTTP
	    if (config.getBaseURL() != null) {
		httpResource = new HttpResource(httpClient,
			config.getBaseURL(), target);
		if (httpResource.getStatusCode() == HttpServletResponse.SC_OK
			|| httpResource.getStatusCode() == HttpServletResponse.SC_MOVED_TEMPORARILY
			|| httpResource.getStatusCode() == HttpServletResponse.SC_MOVED_PERMANENTLY) {
		    if (config.isPutInCache() && target.isCacheable())
			multipleOutput.addOutput(new FileOutput(ResourceUtils
				.getFileUrl(config.getLocalBase(), target)));
		    httpResource.render(multipleOutput);
		    return;
		}
	    }
	    // Resource could not be loaded from HTTP, let's use the cache
	    if (cachedResource != null) {
		cachedResource.render(multipleOutput);
		return;
	    }
	    // Resource could not be loaded neither from HTTP, nor from the
	    // cache, let's try from the file system
	    if (config.isPutInCache() && target.isCacheable()) {
		fileResource = new FileResource(config.getLocalBase(), target);
		if (fileResource.getStatusCode() == 200) {
		    fileResource.render(multipleOutput);
		    return;
		}
	    }
	    // Resource could not be loaded at all
	    new NullResource().render(multipleOutput);
	} finally {
	    // Free all the resources
	    if (cachedResource != null)
		cachedResource.release();
	    if (memoryOutput != null) {
		Resource newCache = memoryOutput.toResource();
		if (newCache != null)
		    cache.put(httpUrl, memoryOutput.toResource());
		else
		    // if we cannot put the resource in cache, we must call
		    // cancelUpdate to release the lock on the key
		    cache.cancelUpdate(httpUrl);
	    }
	    if (httpResource != null)
		httpResource.release();
	    if (fileResource != null)
		fileResource.release();
	}
    }

    /**
     * Retrieves a template from the provider application and renders it to the
     * writer replacing the parameters with the given map. If "page" param is
     * null, the whole page will be used as the template.<br /> eg: The template
     * "mytemplate" can be delimited in the provider page by comments
     * "&lt;!--$begintemplate$mytemplate$--&gt;" and
     * "&lt;!--$endtemplate$mytemplate$--&gt;".<br /> Inside the template, the
     * parameters can be defined by comments.<br /> eg: parameter named
     * "myparam" should be delimited by comments
     * "&lt;!--$beginparam$myparam$--&gt;" and "&lt;!--$endparam$myparam$--&gt;"
     * 
     * @param page Address of the page containing the template
     * @param name Template name
     * @param writer Writer where to write the result
     * @param context User context
     * @param params Blocks to replace inside the template
     * @param replaceRules The replace rules to be applied on the block
     * @param parameters Parameters to be added to the request
     * @throws IOException If an IOException occurs while writing to the writer
     */
    public final void renderTemplate(String page, String name, Writer writer,
	    Context context, Map<String, String> params,
	    Map<String, String> replaceRules, Map<String, String> parameters)
	    throws IOException {
	Target target = new Target(page, context, parameters);
	String content = getResourceAsString(target).toString();
	StringBuilder sb = new StringBuilder();
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
	    if (params != null) {
		for (Entry<String, String> param : params.entrySet()) {
		    int lastIndexOfString = 0;
		    String key = param.getKey();
		    String value = param.getValue();
		    String beginString = "<!--$beginparam$" + key + "$-->";
		    String endString = "<!--$endparam$" + key + "$-->";
		    while (lastIndexOfString >= 0) {
			int begin = sb.indexOf(beginString, lastIndexOfString);
			int end = sb.indexOf(endString, lastIndexOfString);
			if (!(begin == -1 || end == -1)) {
			    sb
				    .replace(begin + beginString.length(), end,
					    value);
			}
			if (begin == -1 || end == -1) {
			    lastIndexOfString = -1;
			} else {
			    // New start search value to use
			    lastIndexOfString = begin + beginString.length()
				    + value.length() + endString.length();
			}
		    }
		}
	    }

	} else {
	    if (params != null) {
		for (Entry<String, String> param : params.entrySet()) {
		    sb.append(param.getValue());
		}
	    }
	}
	writer.append(StringUtils.replace(sb, replaceRules));
    }

    /**
     * This method returns the content of an url.
     * 
     * @param relUrl the target URL
     * @param context the context of the request
     * @param parameters the parameters of the request
     * @return the content of the url
     * @throws IOException
     */
    private final StringOutput getResourceAsString(Target target)
	    throws IOException {
	StringOutput stringOutput = new StringOutput();
	renderResource(target, stringOutput);
	return stringOutput;
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

    private final String getContextKey() {
	return Context.class.getName() + "#" + config.getInstanceName();
    }

    public final Context getContext(HttpServletRequest request) {
	HttpSession session = request.getSession(false);
	if (session != null)
	    return (Context) session.getAttribute(getContextKey());
	return null;
    }

    public final void setContext(Context context, HttpServletRequest request) {
	HttpSession session = request.getSession();
	session.setAttribute(getContextKey(), context);
    }

    /**
     * Retrieves a resource from the provider application and parses it to find
     * tags to be replaced by contents from other providers.
     * 
     * Sample syntax used for includes :
     * <ul>
     * <li>&lt;!--$includeblock$provider$page$blockname$--&gt;</li>
     * <li>&lt;!--$beginincludetemplate$provider$page$templatename$--&gt;</li>
     * <li>&lt;!--$beginput$name$--&gt;</li>
     * </ul>
     * 
     * Sample syntax used inside included contents for template and block
     * definition :
     * <ul>
     * <li>&lt;!--$beginblock$name$--&gt;</li>
     * <li>&lt;!--$begintemplate$name$--&gt;</li>
     * <li>&lt;!--$beginparam$name$--&gt;</li>
     * </ul>
     * 
     * Aggregation is always in "proxy mode" that means cookies or parameters
     * from the original request are transmitted to the target server. NB:
     * Cookies and parameters are not transmitted to templates or blocks invoked
     * by the page.
     * 
     * 
     * @param relUrl the relative URL to the resource
     * @param request the request
     * @param response the response
     * @throws IOException If an IOException occurs while writing to the
     *             response
     * @throws AggregationSyntaxException If the page contains incorrect tags
     */
    public final void aggregate(String relUrl, HttpServletRequest request,
	    HttpServletResponse response) throws IOException,
	    AggregationSyntaxException {
	Target target = new Target(relUrl, getContext(request), null, request);
	request.setCharacterEncoding(config.getUriEncoding());
	target.setProxyMode(true);
	StringOutput stringOutput = getResourceAsString(target);
	if (stringOutput.getStatusCode() == HttpServletResponse.SC_MOVED_PERMANENTLY
		|| stringOutput.getStatusCode() == HttpServletResponse.SC_MOVED_TEMPORARILY) {
	    response.setStatus(stringOutput.getStatusCode());
	    response.setHeader("location", stringOutput.getLocation());
	    return;
	}
	stringOutput.copyHeaders(response);
	String content = stringOutput.toString();
	if (content == null)
	    return;
	response.setCharacterEncoding(stringOutput.getCharset());
	Writer writer = response.getWriter();
	int currentPosition = 0;
	int previousPosition;
	int endPosition;
	String currentTag;
	StringTokenizer stringTokenizer;
	String tagName;
	String provider;
	String page;
	String blockOrTemplate;
	while (currentPosition > -1) {
	    // look for includeBlock or includeTemplate markers
	    previousPosition = currentPosition;
	    currentPosition = content.indexOf("<!--$include", currentPosition);
	    if (currentPosition > -1) {
		writer.append(content, previousPosition, currentPosition);
		endPosition = content.indexOf("-->", currentPosition);
		if (endPosition == -1)
		    throw new AggregationSyntaxException("Tag not closed");
		currentTag = content
			.substring(currentPosition + 4, endPosition);
		log.debug("Found tag: " + currentTag);
		currentPosition = endPosition + 3;
		stringTokenizer = new StringTokenizer(currentTag, "$");
		if (stringTokenizer.countTokens() < 3)
		    throw new AggregationSyntaxException("Invalid syntax: "
			    + currentTag);
		tagName = stringTokenizer.nextToken();
		provider = stringTokenizer.nextToken();
		page = stringTokenizer.nextToken();
		if (stringTokenizer.hasMoreTokens())
		    blockOrTemplate = stringTokenizer.nextToken();
		else
		    blockOrTemplate = null;
		if (stringTokenizer.hasMoreTokens())
		    throw new AggregationSyntaxException("Invalid syntax: "
			    + currentTag);
		if ("includeblock".equals(tagName)) {
		    Driver.getInstance(provider).renderBlock(page,
			    blockOrTemplate, writer, getContext(request), null,
			    null);
		} else if ("includetemplate".equals(tagName)) {
		    endPosition = content
			    .indexOf("<!--$endincludetemplate$-->");
		    if (endPosition < 0)
			throw new AggregationSyntaxException("Tag not closed: "
				+ currentTag);
		    Driver.getInstance(provider).aggregateTemplate(page,
			    blockOrTemplate,
			    content.substring(currentPosition, endPosition),
			    writer, request);
		    currentPosition = endPosition + 27;
		} else {
		    throw new AggregationSyntaxException("Invalid syntax: "
			    + currentTag);
		}
	    } else {
		writer.append(content, previousPosition, content.length());
	    }
	}
    }

    /**
     * Aggregates a template to the output writer </ul>
     * 
     * Searches for tags :
     * <ul>
     * <li>&lt;!--$beginput$name$--&gt;</li>
     * <li>&lt;!--$endput$--&gt;</li>
     * </ul>
     * 
     */
    private final void aggregateTemplate(String page, String template,
	    String content, Writer writer, HttpServletRequest request)
	    throws IOException, AggregationSyntaxException {
	int currentPosition = 0;
	int endPosition;
	String currentTag;
	StringTokenizer stringTokenizer;
	String name;
	HashMap<String, String> params = new HashMap<String, String>();
	while (currentPosition > -1) {
	    // look for includeBlock or includeTemplate markers
	    currentPosition = content.indexOf("<!--$beginput", currentPosition);
	    if (currentPosition > -1) {
		endPosition = content.indexOf("-->", currentPosition);
		if (endPosition == -1)
		    throw new AggregationSyntaxException("Tag not closed");
		currentTag = content
			.substring(currentPosition + 4, endPosition);
		log.debug("Found tag: " + currentTag);
		stringTokenizer = new StringTokenizer(currentTag, "$");
		if (stringTokenizer.countTokens() != 2)
		    throw new AggregationSyntaxException("Invalid syntax: "
			    + currentTag);
		stringTokenizer.nextToken();
		name = stringTokenizer.nextToken();
		currentPosition = endPosition;
		endPosition = content.indexOf("<!--$endput$-->",
			currentPosition);
		if (endPosition == -1)
		    throw new AggregationSyntaxException("Tag not closed: "
			    + currentTag);
		params.put(name, content.substring(currentPosition + 3,
			endPosition));
		currentPosition = endPosition;
	    }
	}
	renderTemplate(page, template, writer, getContext(request), params,
		null, null);
    }
}
