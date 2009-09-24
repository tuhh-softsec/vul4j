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
public class BlockRenderer implements Renderer {
	private final static Log LOG = LogFactory.getLog(BlockRenderer.class);
	private final static Pattern PATTERN = Pattern
			.compile("<!--\\$[^>]*\\$-->");

	private final String page;
	private final String name;

	public BlockRenderer(String name, String page) {
		this.name = name;
		this.page = page;
	}

	/** {@inheritDoc} */
	public void render(String content, Writer out) throws IOException,
			HttpErrorPage {
		LOG.debug("Rendering block " + name + " in page " + page);
		if (content == null)
			return;
		if (name == null) {
			out.write(content);
		} else {
			Parser parser = new Parser(PATTERN, BlockElement.TYPE);
			parser.setAttribute("name", name);
			parser.parse(content, out, false);
		}
	}

}
