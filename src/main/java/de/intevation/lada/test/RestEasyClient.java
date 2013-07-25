package de.intevation.lada.test;

import junit.framework.Assert;
import org.junit.Test;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

public class RestEasyClient {

	public static String baseURL = "https://bfs-lada.intevation.de/lada/server/rest/";
/*	
	@Test
	public void test2() {
		String url = "https://bfs-lada.intevation.de/lada/server/rest/proben?mstId=06010&umwId=N24";
		ClientRequest request = new ClientRequest(url);
		request.header("Authorization", "Basic dGVzdGVpbnM6TjVKOENmSm5iOA==");
		request.accept(MediaType.WILDCARD_TYPE);
		
		ClientResponse<String> response;
		boolean get_error = false;
		try {
			response = request.get(String.class);
			Assert.assertEquals(200,response.getStatus());
			System.out.println(response.getEntity(String.class));
		} catch (Exception e) {
			get_error = true;
			e.printStackTrace();
		}
		Assert.assertEquals(false, get_error);
	}
*/
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

public void testHttpOK(String url) {
	ClientResponse<String> response = getResponse(url, true);
	Assert.assertNotNull("Response shouldnot be null", response);
	Assert.assertEquals(200, response.getStatus());
}

public void testHttpForbidden(String url) {
	ClientResponse<String> response = getResponse(url, false);
	Assert.assertNotNull("Response shouldnot be null", response);
	Assert.assertEquals(401, response.getStatus());
}

@Test
public void testLOrtService(){
	testHttpOK(baseURL + "ort");
	testHttpForbidden(baseURL + "ort");
}
@Test
public void testLMessKommentarService() {
	testHttpOK(baseURL + "messkommentare");
	testHttpForbidden(baseURL + "messkommentare");
}
@Test
public void testLKommentarService() {
	testHttpOK(baseURL + "kommentare");
	testHttpForbidden(baseURL + "kommentare");
}
@Test
public void testMessungService() {
	testHttpOK(baseURL + "messung");
	testHttpForbidden(baseURL + "messung");
}
@Test
public void testLMesswertService() {
	testHttpOK(baseURL + "messwert");
	testHttpForbidden(baseURL + "messwert");
}
@Test
public void testLProbenService() {
	testHttpOK(baseURL + "proben?mstId=06010&umwId=N24");
	testHttpForbidden(baseURL + "proben?mstId=06010&umwId=N24");
}
@Test
public void testLStatusService() {
	testHttpOK(baseURL + "status");
	testHttpForbidden(baseURL + "status");
}
@Test
public void testLZusatzwertService() {
	testHttpOK(baseURL + "zusatzwert");
	testHttpForbidden(baseURL + "zusatzwert");
}

}
