package net.webassembletool.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.ResourceContext;
import net.webassembletool.ResourceFactory;
import net.webassembletool.resource.Resource;

import org.apache.http.client.HttpClient;

public class HttpResourceFactory implements ResourceFactory {
	private final HttpClient httpClient;

	public HttpResourceFactory(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public Resource getResource(ResourceContext resourceContext) throws HttpErrorPage {
		try {
			return new HttpResource(httpClient, resourceContext);
		} catch (IOException e) {
			StringWriter out = new StringWriter();
			e.printStackTrace(new PrintWriter(out));
			HttpErrorPage httpErrorPage = new HttpErrorPage(HttpServletResponse.SC_BAD_GATEWAY, e.getMessage(), out.toString());
			httpErrorPage.initCause(e);
			throw httpErrorPage;
		}
	}
}
