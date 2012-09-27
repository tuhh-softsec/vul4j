package org.esigate.esi;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.regex.Pattern;

import org.esigate.HttpErrorPage;
import org.esigate.Renderer;
import org.esigate.ResourceContext;
import org.esigate.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves a resource from the provider application and parses it to find ESI
 * tags to be replaced by contents from other applications.
 * 
 * For more information about ESI language specification, see <a
 * href="http://www.w3.org/TR/esi-lang">Edge Side Include</a>
 * 
 * @author Francois-Xavier Bonnet
 */
public class EsiRenderer implements Renderer, Appendable {
	private final static Logger LOG = LoggerFactory.getLogger(EsiRenderer.class);
	private final static Pattern PATTERN = Pattern.compile("(<esi:[^>]*>)|(</esi:[^>]*>)|(<!--esi)|(-->)");

	private final Parser parser = new Parser(PATTERN, IncludeElement.TYPE, Comment.TYPE, CommentElement.TYPE, RemoveElement.TYPE, VarsElement.TYPE, ChooseElement.TYPE, WhenElement.TYPE,
			OtherwiseElement.TYPE, TryElement.TYPE, AttemptElement.TYPE, ExceptElement.TYPE, InlineElement.TYPE, ReplaceElement.TYPE, FragmentElement.TYPE);
	private Writer out;
	private Map<String, CharSequence> fragmentsToReplace;
	private final String page;
	private final String name;
	private boolean write = true;

	public String getName() {
		return name;
	}

	public void setWrite(boolean write) {
		this.write = write;
	}

	/**
	 * Constructor used to render a complete page
	 */
	public EsiRenderer() {
		page = null;
		name = null;
	}

	/**
	 * Constructor used to render a fragment Retrieves a fragment inside a page.<br />
	 * 
	 * Extracts html between <code>&lt;esi:fragment name="myFragment"&gt;</code>
	 * and <code>&lt;/esi:fragment&gt;</code>
	 * 
	 * @param page
	 * @param name
	 */
	public EsiRenderer(String page, String name) {
		this.page = page;
		this.name = name;
		write = false;
	}

	public Map<String, CharSequence> getFragmentsToReplace() {
		return fragmentsToReplace;
	}

	public void setFragmentsToReplace(Map<String, CharSequence> fragmentsToReplace) {
		this.fragmentsToReplace = fragmentsToReplace;
	}

	/** {@inheritDoc} */
	public void render(ResourceContext requestContext, String content, Writer out) throws IOException, HttpErrorPage {
		if (name != null)
			LOG.debug("Rendering fragment " + name + " in page " + page);
		this.out = out;
		if (content == null) {
			return;
		}
		parser.setResourceContext(requestContext);
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

	public boolean isWrite() {
		return this.write;
	}

}