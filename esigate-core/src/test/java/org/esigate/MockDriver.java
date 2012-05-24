package org.esigate;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Properties;

import org.esigate.output.Output;
import org.esigate.output.StringOutput;

public class MockDriver extends Driver {
	private final HashMap<String, StringOutput> resources = new HashMap<String, StringOutput>();

	public MockDriver(String name) {
		this(name, new Properties());
	}

	public MockDriver(String name, Properties props) {
		super(name, props);
		DriverFactory.put(name, this);
	}

	public void addResource(String relUrl, String content) throws IOException {
		StringOutput stringOutput = new StringOutput();
		stringOutput.setStatusCode(200);
		stringOutput.setStatusMessage("OK");
		stringOutput.setCharsetName("ISO-8859-1");
		stringOutput.open();
		stringOutput.write(content);
		resources.put(relUrl, stringOutput);
	}

	@Override
	protected StringOutput getResourceAsString(ResourceContext target)
			throws HttpErrorPage {
		StringOutput result = resources.get(target.getRelUrl());
		if (result == null) {
			throw new HttpErrorPage(404, "Not found", "The page: "
					+ target.getRelUrl() + " does not exist");
		}
		return result;
	}

	@Override
	protected void renderResource(ResourceContext target, Output output)
			throws HttpErrorPage, IOException {
		StringOutput result = resources.get(target.getRelUrl());
		if (result == null) {
			output.setStatus(404, "Not found");
			output.open();
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					output.getOutputStream());
			try {
				outputStreamWriter.write("Not found");
			} catch (IOException e) {
				// Should not happen
			}
			output.close();
		} else {
			output.setStatus(result.getStatusCode(), result.getStatusMessage());
			output.open();
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					output.getOutputStream());
			try {
				outputStreamWriter.write(result.toString());
			} catch (IOException e) {
				// Should not happen
			}
			output.close();
		}
	}

}
