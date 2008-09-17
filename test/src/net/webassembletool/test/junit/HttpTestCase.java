package net.webassembletool.test.junit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains methods for http request testing.
 * 
 * @author Omar BENHAMID
 */
public abstract class HttpTestCase extends TestCase {
    private HttpClient httpClient;
    private HttpMethod httpMethod;
    private String referenceFilesPath;
    private final static Log log = LogFactory.getLog(HttpTestCase.class);

    /**
     * Protected constructor. In fact, this class is essentially meant to be
     * used through static methods
     */
    protected HttpTestCase() {
	// Private
    }

    @Override
    protected void setUp() {
	referenceFilesPath = System.getenv("referenceFilesPath");
	if (!referenceFilesPath.endsWith(File.separator))
	    referenceFilesPath += File.separator;
	httpClient = new HttpClient();
	httpMethod = null;
    }

    @Override
    protected void tearDown() throws Exception {
	httpClient = null;
	httpMethod = null;
    }

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
     * Ensures the given method responds the given status Assertion on
     * HttpClient's HttpMethod objects.
     * <p>
     * If the given HttpMethod has not been executed it is executed in a new
     * HttpClient.
     * </p>
     * 
     * Exemple : assertStatus(new GetMethod("http://www.google.com/",200);
     * //Ensures google works fine
     * 
     * @param status the requested status
     * @throws Exception In case of a problem
     */
    protected void assertStatus(int status) throws Exception {
	Assert.assertEquals("Http status for " + getMethodURI() + ":", status,
		httpMethod.getStatusCode());
    }

    /**
     * <p>
     * Ensures the given method's response has the right header
     * </p>
     * <p>
     * If the given HttpMethod has not been executed it is executed in a new
     * HttpClient.
     * </p>
     * 
     * Exemple : assertHeader(new
     * GetMethod("http://www.google.com/","ContentType","text/.*"); //Enusre
     * google works fine
     * 
     * @param header the name of the header
     * @param valueRegex if null only existance of header is checked, else the
     *            header value must match the regex.
     * @throws Exception In case of a problem
     */
    protected void assertStatus(String header, String valueRegex)
	    throws Exception {
	Header headerObject = httpMethod.getResponseHeader(header);
	Assert.assertNotNull("Header " + header + " of " + getMethodURI()
		+ " should exist ", headerObject);
	if (valueRegex != null)
	    Assert.assertTrue("Header " + header + " of " + getMethodURI()
		    + " must match regex :", headerObject.getValue().matches(
		    valueRegex));
    }

    /**
     * <p>
     * Ensures response body matches the given regex
     * </p>
     * <p>
     * If the given HttpMethod has not been executed it is executed in a new
     * HttpClient.
     * </p>
     * 
     * @param regex the regex to check
     * @throws Exception In case of a problem
     */
    protected void assertBodyMatch(String regex) throws Exception {
	Assert.assertTrue("Body of " + getMethodURI() + "must match regex ",
		httpMethod.getResponseBodyAsString().matches(regex));
    }

    /**
     * <p>
     * Ensures response body does not match the given regex
     * </p>
     * <p>
     * If the given HttpMethod has not been executed it is executed in a new
     * HttpClient.
     * </p>
     * 
     * @param regex the regex to check
     * @throws Exception In case of a problem
     */
    protected void assertBodyNotMatch(String regex) throws Exception {
	Assert.assertFalse("Body of " + getMethodURI()
		+ " must not match regex ", httpMethod
		.getResponseBodyAsString().matches(regex));
    }

    /**
     * <p>
     * Ensures response body does contain the given string
     * </p>
     * <p>
     * If the given HttpMethod has not been executed it is executed in a new
     * HttpClient.
     * </p>
     * 
     * @param fragment the fragment to check
     * @throws Exception In case of a problem
     */
    protected void assertBodyContains(String fragment) throws Exception {
	Assert.assertTrue("Body of " + getMethodURI()
		+ " must contain fragment ", httpMethod
		.getResponseBodyAsString().indexOf(fragment) >= 0);
    }

    /**
     * <p>
     * Ensures response body does not contain the given string
     * </p>
     * <p>
     * If the given HttpMethod has not been executed it is executed in a new
     * HttpClient.
     * </p>
     * 
     * @param fragment the fragment to check
     * @throws Exception In case of a problem
     */
    protected void assertBodyNotContains(String fragment) throws Exception {
	Assert.assertFalse("Body of " + getMethodURI()
		+ "  must not contain fragment ", httpMethod
		.getResponseBodyAsString().indexOf(fragment) >= 0);
    }

    /**
     * <p>
     * Ensures response body is equal to the given byte array
     * </p>
     * <p>
     * If the given HttpMethod has not been executed it is executed in a new
     * HttpClient.
     * </p>
     * 
     * @param reference the reference data bytes
     * @throws Exception In case of a problem
     */
    protected void assertBodyEqualsData(byte[] reference) throws Exception {
	Assert.assertTrue("Body of " + getMethodURI()
		+ "  must be equal to reference", Arrays.equals(httpMethod
		.getResponseBody(), reference));
    }

    /**
     * <p>
     * Checks wether the given method body equals body of the result of the
     * supplied url Exampel : assertBodyEqualsURLBody(new
     * GetMethod("http://www.google.com/","file:///mydata/exameplefile.html");
     * </p>
     * <p>
     * If the given HttpMethod has not been executed it is executed in a new
     * HttpClient.
     * </p>
     * 
     * @param url URL to compare the method result with
     * @throws Exception In case of a problem
     */
    protected void assertBodyEqualsURLBody(String url) throws Exception {
	log.info("Open file " + url);
	URLConnection conn = new URL(url).openConnection();
	byte[] data;
	int len = conn.getContentLength();
	if (len > 0) {
	    data = new byte[len];
	    int read = 0;
	    InputStream in = conn.getInputStream();
	    while (read < len) {
		read += in.read(data, read, len - read);
	    }
	} else {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    InputStream stream = conn.getInputStream();
	    byte[] buffer = new byte[1024];
	    while (stream.available() > 0) {
		int read = stream.read(buffer);
		baos.write(buffer, 0, read);
	    }
	    data = baos.toByteArray();
	}
	assertBodyEqualsData(data);
    }

    /**
     * Recurse directory and queries each serverPrefix+filePrefix+<filename>
     * ressource with GET and compares result body with provided body. Method
     * skips files with pattern ".*"
     * 
     * @param directory File representing the directory
     * @param serverPrefix server prefix ending with "/"
     * @param filePrefix relative path : not starting nor ending with "/", it
     *            can be null to check direct children of directory
     * @throws Exception In case of a problem
     */
    private void recurseAndValidateFiles(File directory, String relativePath)
	    throws Exception {
	String[] files = directory.list();
	for (int i = 0; i < files.length; i++) {
	    if (files[i].startsWith("."))
		continue; // Skip .XXXXX
	    File f = new File(directory, files[i]);
	    String relPath = relativePath + "/" + files[i];

	    if (f.isDirectory()) {
		recurseAndValidateFiles(f, relPath);
		continue;
	    }
	    doGet(relPath);
	    assertBodyEqualsURLBody(f.toURI().toString());
	}
    }

    /**
     * <p>
     * Browses files in a directory and queries the same file on server and
     * ensure resources match. A file whose path in local directory is x/y/z is
     * compared with get result of GET call on <serverprefix>/x/y/z
     * 
     * Files whose name start with . are ignored (such as .CVS/.SVN ...)
     * </p>
     * 
     * @param relativePath Relative path of a folder on the server
     * 
     * @throws Exception In case of a problem
     */
    protected void assertBodyGetEqualsLocalFile(String relativePath)
	    throws Exception {
	String directoryPrefix = getReferenceFilePath(relativePath);
	File file = new File(directoryPrefix);
	if (!file.isDirectory())
	    throw new Exception("Supplied prefix : " + directoryPrefix
		    + " is not a directory");
	recurseAndValidateFiles(file, relativePath);
    }

    /**
     * Converts a relative url to a url on the test server.
     * 
     * @param relativeURL the relative URL to append to server name
     * @return returns the absolute URL as a string
     */
    private String getAbsoluteURL(String relativeURL) {
	return getServerURLPrefix()
		+ (relativeURL.startsWith("/") ? relativeURL.substring(1)
			: relativeURL);
    }

    /**
     * Get the server part of url to this container
     * 
     * @return url ending with /
     */
    protected String getServerURLPrefix() {
	return "http://localhost:8080/";
    }

    protected void doGet(String relativeURL) throws Exception {
	String absoluteUrl = getAbsoluteURL(relativeURL);
	log.info("GET " + absoluteUrl);
	httpMethod = new GetMethod(absoluteUrl);
	try {
	    httpClient.executeMethod(httpMethod);
	    httpMethod.getResponseBody();
	} finally {
	    httpMethod.releaseConnection();
	}
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

}