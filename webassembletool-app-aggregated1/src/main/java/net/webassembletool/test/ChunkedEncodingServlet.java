package net.webassembletool.test;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ChunkedEncodingServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/plain");
		Writer writer = resp.getWriter();
		writer.write("Bonjour ");
		writer.flush();
		resp.flushBuffer();
		writer.write("Monde !");
		writer.flush();
		writer.close();
		resp.flushBuffer();
	}
}
