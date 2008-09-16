package net.webassembletool.test.junit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.webassembletool.test.jetty.JettyRunner;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains methods for http request testing.
 * 
 * @author Omar BENHAMID
 */
public class WebTestCase extends TestCase {
    private static final Log log = LogFactory.getLog(WebTestCase.class);

    /**
     * Protected constructor. In fact, this class is essentially meant to be
     * used through static methods
     */
    protected WebTestCase() {
	// Private
    }

    /**
     * Executes the given method in a new HttpClient if it has not already been
     * executed
     * 
     * @param m
     */
    private static void ensureMethodExecuted(HttpMethod m) {
	HttpClient hc = new HttpClient();
	if (m.isRequestSent())
	    return;
	try {
	    hc.executeMethod(m);
	    m.getResponseBody();
	    m.releaseConnection();
	} catch (Exception e) {
	    throw new RuntimeException("Http query failed", e);
	}
    }

    /**
     * Safely read method URI
     * 
     * @param m the method whos URI is extracted
     * @return a string of the URL of the method or null in case of error
     */
    private static String readMethodURI(HttpMethod m) {
	try {
	    return m.getURI().toString();
	} catch (Exception ex) {
	    log.warn("Failed to retrieve URI for method", ex);
	}
	return null;
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
     * //Enusre google works fine
     * 
     * @param method any HttpClient HttpMethod object
     * @param status the requested status
     */
    public static void assertStatus(HttpMethod method, int status) {
	ensureMethodExecuted(method);
	Assert.assertEquals("Http status for " + readMethodURI(method) + ":",
		status, method.getStatusCode());
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
     * @param method any HttpClient HttpMethod object
     * @param header the name of the header
     * @param valueRegex if null only existance of header is checked, else the
     *            header value must match the regex.
     */
    public static void assertStatus(HttpMethod method, String header,
	    String valueRegex) {
	ensureMethodExecuted(method);
	Header headerObject = method.getResponseHeader(header);
	Assert.assertNotNull("Header " + header + " of "
		+ readMethodURI(method) + " should exist ", headerObject);
	if (valueRegex != null)
	    Assert.assertTrue("Header " + header + " of "
		    + readMethodURI(method) + " must match regex :",
		    headerObject.getValue().matches(valueRegex));
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
     * @param method the method providing the body
     * @param regex the regex to check
     */
    public static void assertBodyMatch(HttpMethod method, String regex) {
	ensureMethodExecuted(method);
	try {
	    Assert.assertTrue("Body of " + readMethodURI(method)
		    + "must match regex ", method.getResponseBodyAsString()
		    .matches(regex));
	} catch (IOException e) {
	    throw new RuntimeException("Unable to extract response body", e);
	}
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
     * @param method the method providing the body
     * @param regex the regex to check
     */
    public static void assertBodyNotMatch(HttpMethod method, String regex) {
	ensureMethodExecuted(method);
	try {
	    Assert.assertFalse("Body of " + readMethodURI(method)
		    + " must not match regex ", method
		    .getResponseBodyAsString().matches(regex));
	} catch (IOException e) {
	    throw new RuntimeException("Unable to extract response body", e);
	}
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
     * @param method the method providing the body
     * @param fragment the fragment to check
     */
    public static void assertBodyContains(HttpMethod method, String fragment) {
	ensureMethodExecuted(method);
	try {
	    Assert.assertTrue("Body of " + readMethodURI(method)
		    + " must contain fragment ", method
		    .getResponseBodyAsString().indexOf(fragment) >= 0);
	} catch (IOException e) {
	    throw new RuntimeException("Unable to extract response body", e);
	}
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
     * @param method the method providing the body
     * @param fragment the fragment to check
     */
    public static void assertBodyNotContains(HttpMethod method, String fragment) {
	ensureMethodExecuted(method);
	try {
	    Assert.assertFalse("Body of " + readMethodURI(method)
		    + "  must not contain fragment ", method
		    .getResponseBodyAsString().indexOf(fragment) >= 0);
	} catch (IOException e) {
	    throw new RuntimeException("Unable to extract response body", e);
	}
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
     * 
     * TODO Method javadoc
     * 
     * @param method the method whose body is to check
     * @param reference the reference data bytes
     */
    public static void assertBodyEqualsData(HttpMethod method, byte[] reference) {
	ensureMethodExecuted(method);
	try {
	    Assert.assertTrue("Body of " + readMethodURI(method)
		    + "  must be equal to reference", Arrays.equals(method
		    .getResponseBody(), reference));
	} catch (IOException e) {
	    throw new RuntimeException("Unable to extract response body", e);
	}
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
     * @param method The request
     * @param url URL to compare the method result with
     */
    public static void assertBodyEqualsURLBody(HttpMethod method, String url) {
	try {
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
	    assertBodyEqualsData(method, data);
	} catch (Exception e) {
	    throw new RuntimeException(
		    "Unable to open reference data form url : " + url, e);
	}

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
     */
    private static void recurseAndValidateFiles(File directory,
	    String serverPrefix, String filePrefix) {
	String[] files = directory.list();
	if (filePrefix != null)
	    filePrefix += "/";
	else
	    filePrefix = "";

	for (int i = 0; i < files.length; i++) {
	    if (files[i].startsWith("."))
		continue; // Skip .XXXXX
	    File f = new File(directory, files[i]);
	    String relPath = filePrefix + files[i];

	    if (f.isDirectory()) {
		recurseAndValidateFiles(f, serverPrefix, relPath);
		continue;
	    }

	    assertBodyEqualsURLBody(new GetMethod(serverPrefix + relPath), f
		    .toURI().toString());
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
     * @param serverPrefix the prefix to prepend to files
     * @param directoryPrefix the directory to browse to find reference files.
     */
    public static void assertBodyGetEqualsLocalFile(String serverPrefix,
	    String directoryPrefix) {
	File f = new File(directoryPrefix);
	if (!f.isDirectory())
	    throw new IllegalStateException("Supplied prefix : "
		    + directoryPrefix + " is not a directory");
	if (!serverPrefix.endsWith("/"))
	    serverPrefix += "/";
	recurseAndValidateFiles(f, serverPrefix, null);
    }

    @Override
    protected void setUp() {
	try {
	    JettyRunner.startJetty();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    protected void tearDown() throws Exception {
	try {
	    JettyRunner.stopJetty();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Converts a relative url to a url on the test server.
     * 
     * @param relativeURL the relative URL to append to server name
     * @return returns the absolute URL as a string
     */
    public String getAbsoluteURL(String relativeURL) {
	return getServerURLPrefix()
		+ (relativeURL.startsWith("/") ? relativeURL.substring(1)
			: relativeURL);
    }

    /**
     * Get the server part of url to this container
     * 
     * @return url ending with /
     */
    public String getServerURLPrefix() {
	return "http://localhost:8080/";
    }
}