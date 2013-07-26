package de.intevation.lada.test;

import junit.framework.Assert;
import org.junit.Test;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

public class RestEasyClient {

public static String baseURL = "https://bfs-lada.intevation.de/lada/server/rest/";

public ClientResponse<String> getResponse(String url, boolean header){
	ClientRequest request = new ClientRequest(url);
	if(header)
		request.header("Authorization", "Basic dGVzdGVpbnM6TjVKOENmSm5iOA==");
	ClientResponse<String> response = null;
	try {
		response = request.get(String.class);
	}
	catch(Exception e) {
		e.printStackTrace();
	}
	return response;
}

public void checkResponse(ClientResponse<String> response){
	Assert.assertEquals(true, response.getEntity().contains("\"message\":\"200\""));
}

public void testHttpOK(String url) {
	ClientResponse<String> response = getResponse(url, true);
	Assert.assertNotNull("Response shouldnot be null", response);
	Assert.assertEquals(200, response.getStatus());
	checkResponse(response);
}

public void testHttpForbidden(String url) {
	ClientResponse<String> response = getResponse(url, false);
	Assert.assertNotNull("Response shouldnot be null", response);
	Assert.assertEquals(401, response.getStatus());
}

@Test
public void testLOrtService(){
	testHttpOK(baseURL + "ort?probeId=000007587685X");
	testHttpForbidden(baseURL + "ort");
	ClientResponse<String> response = getResponse(baseURL + "ort/000007587685X", true);
	Assert.assertNotNull(response);
	Assert.assertEquals(404, response.getStatus());
}
@Test
public void testLMessKommentarService() {
	testHttpOK(baseURL + "messkommentare?probeId=000007587685X&messungsId=1");
	testHttpForbidden(baseURL + "messkommentare");
	ClientResponse<String> response = getResponse(baseURL + "messkommentare/000007587685X", true);
	Assert.assertNotNull(response);
	Assert.assertEquals(404, response.getStatus());
}
@Test
public void testLKommentarService() {
	testHttpOK(baseURL + "kommentare?probeId=000007587685X");
	testHttpForbidden(baseURL + "kommentare");
	ClientResponse<String> response = getResponse(baseURL + "kommentare/000007587685X", true);
	Assert.assertNotNull(response);
	Assert.assertEquals(404, response.getStatus());
}
@Test
public void testMessungService() {
	testHttpOK(baseURL + "messung?probeId=000007587685X");
	testHttpForbidden(baseURL + "messung");
	ClientResponse<String> response = getResponse(baseURL + "messung/000007587685X", true);
	Assert.assertNotNull(response);
	Assert.assertEquals(404, response.getStatus());
}
@Test
public void testLMesswertService() {
	testHttpOK(baseURL + "messwert?probeId=000007587685X&messungsId=1");
	testHttpForbidden(baseURL + "messwert");
	ClientResponse<String> response = getResponse(baseURL + "messungwert/000007587685X", true);
	Assert.assertNotNull(response);
	Assert.assertEquals(404, response.getStatus());
}
@Test
public void testLProbenService() {
	testHttpOK(baseURL + "proben?mstId=06010&umwId=N24");
	testHttpForbidden(baseURL + "proben?mstId=06010&umwId=N24");
	ClientResponse<String> response = getResponse(baseURL + "proben/000007587685X", true);
	Assert.assertNotNull(response);
	Assert.assertEquals(200, response.getStatus());
	checkResponse(response);
}
@Test
public void testLStatusService() {
	testHttpOK(baseURL + "status?probeId=000007587685X&messungsId=1");
	testHttpForbidden(baseURL + "status");
	ClientResponse<String> response = getResponse(baseURL + "status/000007587685X", true);
	Assert.assertNotNull(response);
	Assert.assertEquals(404, response.getStatus());
}
@Test
public void testLZusatzwertService() {
	testHttpOK(baseURL + "zusatzwert?probeId=000007587685X");
	testHttpForbidden(baseURL + "zusatzwert");
	ClientResponse<String> response = getResponse(baseURL + "zusatzwert/000007587685X", true);
	Assert.assertNotNull(response);
	Assert.assertEquals(404, response.getStatus());
}
}