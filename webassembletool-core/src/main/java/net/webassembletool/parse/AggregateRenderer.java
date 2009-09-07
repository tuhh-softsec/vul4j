package net.webassembletool.parse;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.Renderer;

/**
 * Retrieves a resource from the provider application and parses it to find tags
 * to be replaced by contents from other providers.
 * 
 * Sample syntax used for includes :
 * <ul>
 * <li>&lt;!--$includeblock$provider$page$blockname$--&gt;</li>
 * <li>&lt;!--$includetemplate$provider$page$templatename$--&gt;</li>
 * <li>&lt;!--$beginput$name$--&gt;</li>
 * </ul>
 * 
 * Sample syntax used inside included contents for template and block
 * definition:
 * <ul>
 * <li>&lt;!--$beginblock$name$--&gt;</li>
 * <li>&lt;!--$begintemplate$name$--&gt;</li>
 * <li>&lt;!--$beginparam$name$--&gt;</li>
 * </ul>
 * 
 * Aggregation is always in "proxy mode" that means cookies or parameters from
 * the original request are transmitted to the target server. <br/>
 * <b>NB: Cookies and parameters are not transmitted to templates or blocks
 * invoked by the page</b>.
 * 
 * @author Stanislav Bernatskyi
 */
public class AggregateRenderer implements Renderer {
    private final HttpServletRequest request;
    private final boolean propagateJsessionId;

	public AggregateRenderer(HttpServletRequest request,
			boolean propagateJsessionId) {
        this.request = request;
        this.propagateJsessionId = propagateJsessionId;
    }

    /** {@inheritDoc} */
	public void render(String content, Writer writer)
			throws IOException, HttpErrorPage {
        if (content == null)
            return;
        RegionParser parser = createParser();
        List<Region> parsed = parser.parse(content);
        for (Region region : parsed) {
            try {
                region.process(writer, request);
            } catch (HttpErrorPage e) {
                writer.append(e.getStatusCode() + " " + e.getStatusMessage());
            }
        }
        // Don't forget to flush the buffer as it is a home made writer, it is not flushed automatically.
        writer.flush();
    }

    private RegionParser createParser() {
        return new AggregateRendererRegionParser(propagateJsessionId);
    }
}
