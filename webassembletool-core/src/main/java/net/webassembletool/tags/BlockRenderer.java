package net.webassembletool.tags;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.Renderer;
import net.webassembletool.parser.Parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Block renderer.
 * <p>
 * Extracts data between <code>&lt;!--$beginblock$myblock$--&gt;</code> and
 * <code>&lt;!--$endblock$myblock$--&gt;</code> separators
 * 
 * @author Stanislav Bernatskyi
 * @author Francois-Xavier Bonnet
 */
public class BlockRenderer implements Renderer, Appendable {
	private final static Log LOG = LogFactory.getLog(BlockRenderer.class);
	private final static Parser PARSER = new Parser(Pattern
			.compile("<!--\\$[^>]*\\$-->"), BlockElement.TYPE);
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

	public BlockRenderer(String name, String page) {
		this.name = name;
		this.page = page;
		if (name == null)
			write = true;
		else
			write = false;
	}

	/** {@inheritDoc} */
	public void render(String content, Writer out) throws IOException,
			HttpErrorPage {
		LOG.debug("Rendering block " + name + " in page " + page);
		this.out = out;
		if (content == null)
			return;
		if (name == null)
			out.write(content);
		else
			PARSER.parse(content, this);
	}

	public Appendable append(CharSequence csq) throws IOException {
		if (write)
			out.append(csq);
		return this;
	}

	public Appendable append(char c) throws IOException {
		if (write)
			out.append(c);
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end)
			throws IOException {
		if (write)
			out.append(csq, start, end);
		return this;
	}

}
