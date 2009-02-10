package net.webassembletool.parse;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

/**
 * Represents a static content in template which should be processed to the
 * output 'as is'.
 * 
 * @author Stanislav Bernatskyi
 */
public class UnmodifiableRegion implements IRegion {
    private final CharSequence content;
    private final int start;
    private final int end;

    public UnmodifiableRegion(CharSequence content, int start, int end) {
	this.content = content;
	this.start = start;
	this.end = end;
    }

    /** {@inheritDoc} */
    public void process(Writer out, HttpServletRequest request)
	    throws IOException {
	out.append(content, start, end);
    }

}
