/**
 * 
 */
package net.webassembletool.resource;

import java.io.IOException;

import net.webassembletool.ouput.Output;

public class NullResource implements Resource {

    public int getStatusCode() {
	return 404;
    }

    public void release() {
	// Nothing to do
    }

    public void render(Output output) throws IOException {
	output.open();
	try {
	    output.setStatus(404, "Not found");
	} finally {
	    output.close();
	}
    }

}
