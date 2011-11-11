package org.esigate.esi;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import org.esigate.HttpErrorPage;
import org.esigate.Renderer;
import org.esigate.ResourceContext;
import org.esigate.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves a fragment inside a page.<br />
 * 
 * Extracts html between <code>&lt;esi:fragment name="myFragment"&gt;</code> and <code>&lt;/esi:fragment&gt;</code>
 * 
 * @author Francois-Xavier Bonnet
 */
public class EsiFragmentRenderer implements Renderer, Appendable {
	private final static Logger LOG = LoggerFactory.getLogger(EsiFragmentRenderer.class);
	private final static Pattern PATTERN = Pattern.compile("(<esi:[^>]*>)|(</esi:[^>]*>)");

	private final Parser parser = new Parser(PATTERN, FragmentElement.TYPE);
	private final String page;
	private final String name;
	private boolean write;
	private Writer out;

	public void setWrite(boolean write) {
		this.write = write;
	}

	public String getName() {
		return name;
	}

	public EsiFragmentRenderer(String page, String name) {
		this.page = page;
		this.name = name;
		write = false;
	}

	/** {@inheritDoc} */
	public void render(ResourceContext requestContext, String content, Writer out) throws IOException, HttpErrorPage {
		LOG.debug("Rendering fragment " + name + " in page " + page);
		this.out = out;
		if (content == null) {
			return;
		}
		parser.parse(content, this);
	}

	public Appendable append(CharSequence csq) throws IOException {
		if (write) {
			out.append(csq);
		}
		return this;
	}

	public Appendable append(char c) throws IOException {
		if (write) {
			out.append(c);
		}
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		if (write) {
			out.append(csq, start, end);
		}
		return this;
	}

}