package net.webassembletool.test.junit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains methods for http request testing.
 * 
 * @author Omar BENHAMID
 */
public abstract class HttpTestCase extends TestCase {
    private final static Log log = LogFactory.getLog(HttpTestCase.class);
    private static final String SERVER_BASE = "http://localhost:8080/";
    private HttpClient httpClient;
    private HttpMethod httpMethod;
    private String referenceFilesPath;

    /**
     * Safely read method URI
     * 
     * @param m the method whos URI is extracted
     * @return a string of the URL of the method
     * @throws URIException
     */
    private String getMethodURI() throws URIException {
	return httpMethod.getURI().toString();
    }

    /**
     * Converts a relative url to a url on the test server.
     * 
     * @param relativeURL the relative URL to append to server name
     * @return returns the absolute URL as a string
     */
    private String getAbsoluteURL(String relativeURL) {
	return SERVER_BASE
		+ (relativeURL.startsWith("/") ? relativeURL.substring(1)
			: relativeURL);
    }

    /**
     * Gets absolute path to reference data for given relative path relative is
     * merged with reference prefix.
     * 
     * @param relativePath relative path
     */

    private String getReferenceFilePath(String relativePath) {
	return referenceFilesPath
		+ (relativePath.startsWith(File.separator) ? relativePath
			.substring(1) : relativePath);
    }

    @Override
    public void setUp() {
	referenceFilesPath = System.getenv("referenceFilesPath");
	if (!referenceFilesPath.endsWith(File.separator))
	    referenceFilesPath += File.separator;
	httpClient = new HttpClient();
	httpMethod = null;
    }

    @Override
    public void tearDown() throws Exception {
	httpClient = null;
	httpMethod = null;
    }

    public void doGet(String relativeURL) throws Exception {
	String absoluteUrl = getAbsoluteURL(relativeURL);
	log.info("GET " + absoluteUrl);
	httpMethod = new GetMethod(absoluteUrl);
	httpMethod.setFollowRedirects(false);
	try {
	    httpClient.executeMethod(httpMethod);
	    httpMethod.getResponseBody();
	} finally {
	    httpMethod.releaseConnection();
	}
    }

    public void doPost(String relativeURL, Map<String, String> params)
	    throws Exception {
	doPost(relativeURL, params, "ISO-8859-1");
    }

    public void doPost(String relativeURL, Map<String, String> params,
	    String charset) throws Exception {
	String absoluteUrl = getAbsoluteURL(relativeURL);
	log.info("GET " + absoluteUrl);
	httpMethod = new PostMethod(absoluteUrl);
	httpMethod.setFollowRedirects(false);
	httpMethod.getParams().setContentCharset(charset);
	String paramName;
	String paramValue;
	for (Iterator<Entry<String, String>> iterator = params.entrySet()
		.iterator(); iterator.hasNext();) {
	    Entry<String, String> entry = iterator.next();
	    paramName = entry.getKey();
	    paramValue = entry.getValue();
	    ((PostMethod) httpMethod).addParameter(paramName, paramValue);
	}
	try {
	    httpClient.executeMethod(httpMethod);
	    httpMethod.getResponseBody();
	} finally {
	    httpMethod.releaseConnection();
	}
    }

    /**
     * Ensures the given method responds the given status Assertion on
     * HttpClient's HttpMethod objects.
     * 
     * @param status the requested status
     * @throws Exception In case of a problem
     */
    public void assertStatus(int status) throws Exception {
	Assert.assertEquals("Http status for " + getMethodURI() + ":", status,
		httpMethod.getStatusCode());
    }

    /**
     * Ensures response body matches the given regex
     * 
     * @param regex the regex to check
     * @throws Exception In case of a problem
     */
    public void assertBodyMatch(String regex) throws Exception {
	Assert.assertTrue("Body of " + getMethodURI() + "must match regex ",
		httpMethod.getResponseBodyAsString().matches(regex));
    }

    /**
     * Ensures response body does not match the given regex
     * 
     * @param regex the regex to check
     * @throws Exception In case of a problem
     */
    public void assertBodyNotMatch(String regex) throws Exception {
	Assert.assertFalse("Body of " + getMethodURI()
		+ " must not match regex ", httpMethod
		.getResponseBodyAsString().matches(regex));
    }

    /**
     * Ensures response body does contain the given string
     * 
     * @param fragment the fragment to check
     * @throws Exception In case of a problem
     */
    public void assertBodyContains(String fragment) throws Exception {
	Assert.assertTrue("Body of " + getMethodURI()
		+ " must contain fragment ", httpMethod
		.getResponseBodyAsString().indexOf(fragment) >= 0);
    }

    /**
     * Ensures response body does not contain the given string
     * 
     * @param fragment the fragment to check
     * @throws Exception In case of a problem
     */
    public void assertBodyNotContains(String fragment) throws Exception {
	Assert.assertFalse("Body of " + getMethodURI()
		+ "  must not contain fragment ", httpMethod
		.getResponseBodyAsString().indexOf(fragment) >= 0);
    }

    /**
     * Compares a response to a file
     * 
     * @param relativePath Relative path of a folder on the server
     * @throws Exception In case of a problem
     */
    public void assertBodyEqualsLocalFile(String relativePath) throws Exception {
	String directoryPrefix = getReferenceFilePath(relativePath);
	File file = new File(directoryPrefix);
	log.info("Open file " + file.getPath());
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	InputStream stream = new FileInputStream(file);
	byte[] buffer = new byte[1024];
	while (stream.available() > 0) {
	    int read = stream.read(buffer);
	    baos.write(buffer, 0, read);
	}
	stream.close();
	byte[] data = baos.toByteArray();
	if (!Arrays.equals(httpMethod.getResponseBody(), data)) {
	    String expected = new String(data, "ISO-8859-1");
	    String actual = new String(httpMethod.getResponseBody(),
		    "ISO-8859-1");
	    int index = 0;
	    int size = Math.min(expected.length(), actual.length());
	    int line = 1;
	    int character = 1;
	    String expectedLine = "";
	    String actualLine = "";
	    while (index < size) {
		if (expected.charAt(index) == '\n'
			&& actual.charAt(index) == '\n') {
		    line++;
		    character = 1;
		    expectedLine = "";
		    actualLine = "";
		} else {
		    character++;
		    expectedLine += expected.charAt(index);
		    actualLine += actual.charAt(index);
		}
		if (expected.charAt(index) != actual.charAt(index)) {
		    String message = "Body of " + getMethodURI()
			    + " must be equal to reference\n";
		    message += "Error line " + line + " character " + character
			    + "\n";
		    // Read to end of line
		    index++;
		    while (expected.charAt(index) != '\n'
			    && actual.charAt(index) != '\n' && index < size) {
			expectedLine += expected.charAt(index);
			actualLine += actual.charAt(index);
			index++;
		    }
		    message += "Expected: " + expectedLine + "\n";
		    message += "Actual:   " + actualLine + "\n";
		    throw new AssertionFailedError(message);
		}
		index++;
	    }
	}
    }

    public void assertHeaderEquals(String name, String value) {
	String actualValue = httpMethod.getResponseHeader(name).getValue();
	assertEquals("Header \"" + name + "\" must be equal to \"" + value
		+ "\" actual value is \"" + actualValue + "\"", actualValue,
		value);
    }
}