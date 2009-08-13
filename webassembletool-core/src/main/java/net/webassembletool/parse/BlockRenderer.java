package net.webassembletool.parse;

import java.io.IOException;
import java.io.Writer;

import net.webassembletool.HttpErrorPage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Block renderer.
 * <p>
 * Extracts data between <code>&lt;!--$beginblock$myblock$--&gt;</code> and
 * <code>&lt;!--$endblock$myblock$--&gt;</code> separators
 * 
 * @author Stanislav Bernatskyi
 */
public class BlockRenderer implements Renderer {
    private final static Log LOG = LogFactory.getLog(BlockRenderer.class);

    private final String page;
    private final String name;

    public BlockRenderer(String name, String page) {
        this.name = name;
        this.page = page;
    }

    /** {@inheritDoc} */
    public void render(String content, Writer out) throws IOException,
            HttpErrorPage {
         if (content == null)
            return;
        if (name == null) {
            LOG.debug("Serving whole page: page=" + page);
            out.append(content);
        } else {
            Tag openTag = Tag.find("beginblock$" + name, content);
            Tag closeTag = Tag.find("endblock$" + name, content);
            if (openTag == null || closeTag == null) {
                LOG.warn("Block not found: page=" + page + " block=" + name);
            } else {
                LOG.debug("Serving block: page=" + page + " block=" + name);
				out.append(content.substring(openTag.getEndIndex(), closeTag
						.getBeginIndex()));
            }
        }
    }

}
