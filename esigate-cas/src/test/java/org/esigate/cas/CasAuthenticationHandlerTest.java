package org.esigate.cas;

import java.util.Properties;

import junit.framework.TestCase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.esigate.Driver;
import org.esigate.Parameters;
import org.esigate.cookie.CookieManager;
import org.esigate.extension.ExtensionFactory;
import org.esigate.http.HttpClientRequestExecutor;
import org.esigate.http.IncomingRequest;
import org.esigate.http.OutgoingRequest;
import org.esigate.impl.DriverRequest;
import org.esigate.test.TestUtils;
import org.esigate.test.conn.MockConnectionManager;
import org.jasig.cas.client.authentication.AttributePrincipalImpl;

/**
 * CasAuthenticationHandlerTest
 * <p/>
 * TODO : test the addCasAuthentication method
 */
public class CasAuthenticationHandlerTest extends TestCase {
    private Driver driver1;
    private Driver driver2;
    private CasAuthenticationHandler handler;
    private HttpClientRequestExecutor httpClientRequestExecutor;
    private MockConnectionManager mockConnectionManager;

    private HttpResponse createMockResponse(String entity) throws Exception {
        HttpResponse response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "OK");
        HttpEntity httpEntity = new StringEntity(entity);
        response.setEntity(httpEntity);
        return response;
    }

    @Override
    public void setUp() {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE, "http://localhost:8080");
        properties.put(CasAuthenticationHandler.LOGIN_URL_PROPERTY, "/loginurl");

        mockConnectionManager = new MockConnectionManager();

        driver1 =
                Driver.builder()
                        .setName("driver1")
                        .setProperties(properties)
                        .setRequestExecutorBuilder(
                                HttpClientRequestExecutor
                                        .builder()
                                        .setConnectionManager(mockConnectionManager)
                                        .setCookieManager(
                                                (CookieManager) ExtensionFactory.getExtension(properties,
                                                        Parameters.COOKIE_MANAGER, null))).build();

        driver2 =
                Driver.builder()
                        .setName("driver2")
                        .setProperties(properties)
                        .setRequestExecutorBuilder(
                                HttpClientRequestExecutor
                                        .builder()
                                        .setConnectionManager(mockConnectionManager)
                                        .setCookieManager(
                                                (CookieManager) ExtensionFactory.getExtension(properties,
                                                        Parameters.COOKIE_MANAGER, null))).build();

        httpClientRequestExecutor = (HttpClientRequestExecutor) driver1.getRequestExecutor();
        handler = new CasAuthenticationHandler();

        handler.init(driver1, properties);
    }

    public void testBeforeProxy() throws Exception {
        assertTrue(handler.beforeProxy(mockConnectionManager.getSentRequest()));
    }

    public void testNeedsNewRequest() throws Exception {
        HttpResponse httpResponse = createMockResponse("0");
        IncomingRequest incomingRequest = TestUtils.createIncomingRequest().build();
        DriverRequest driverRequest = TestUtils.createDriverRequest(driver1);
        OutgoingRequest outgoingRequest =
                httpClientRequestExecutor.createHttpRequest(driverRequest, "http://localhost:8080", true);

        assertFalse(handler.needsNewRequest(httpResponse, outgoingRequest, incomingRequest));
        incomingRequest.setAttribute(handler.driverSpecificName(driver1, CasAuthenticationHandler.SECOND_REQUEST),
                Boolean.TRUE);
        // Without location
        assertFalse(handler.needsNewRequest(httpResponse, outgoingRequest, incomingRequest));

        httpResponse.setHeader("Location", "http://localhost/loginurl");
        assertFalse(handler.needsNewRequest(httpResponse, outgoingRequest, incomingRequest));

        incomingRequest =
                TestUtils.createIncomingRequest().setUserPrincipal(new AttributePrincipalImpl("loggeduser")).build();
        incomingRequest.setAttribute(handler.driverSpecificName(driver1, CasAuthenticationHandler.SECOND_REQUEST),
                Boolean.TRUE);
        assertTrue(handler.needsNewRequest(httpResponse, outgoingRequest, incomingRequest));

        httpResponse.setHeader("Location", "http://localhost/another");
        assertFalse(handler.needsNewRequest(httpResponse, outgoingRequest, incomingRequest));

    }

    public void testPreRequest() throws Exception {

        IncomingRequest incomingRequest = TestUtils.createIncomingRequest().build();

        DriverRequest httpRequest = TestUtils.createDriverRequest(driver1);
        OutgoingRequest outgoingRequest =
                httpClientRequestExecutor.createHttpRequest(httpRequest, "http://localhost:8080", true);

        handler.preRequest(outgoingRequest, incomingRequest);
        Object sr =
                incomingRequest.getAttribute(handler.driverSpecificName(driver1,
                        CasAuthenticationHandler.SECOND_REQUEST));
        assertNotNull("SecondRequest should be set", sr);
        assertTrue("SecondRequest should be true", (Boolean) sr);

        sr = incomingRequest.getAttribute(handler.driverSpecificName(driver2, CasAuthenticationHandler.SECOND_REQUEST));
        assertNull("SecondRequest should not be set", sr);

    }
}
