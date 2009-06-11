package net.webassembletool.parse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.webassembletool.RenderingException;
import net.webassembletool.RetrieveException;
import net.webassembletool.output.StringOutput;

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
    private final HttpServletResponse response;
    private final HttpServletRequest request;
    private final boolean propagateJsessionId;

    public AggregateRenderer(HttpServletResponse response,
            HttpServletRequest request, boolean propagateJsessionId) {
        this.response = response;
        this.request = request;
        this.propagateJsessionId = propagateJsessionId;
    }

    /** {@inheritDoc} */
    public void render(StringOutput stringOutput, Writer unised1,
            Map<String, String> unused2) throws IOException, RenderingException {
        if (stringOutput.getStatusCode() == HttpServletResponse.SC_MOVED_PERMANENTLY
                || stringOutput.getStatusCode() == HttpServletResponse.SC_MOVED_TEMPORARILY) {
            response.setStatus(stringOutput.getStatusCode());
            response.setHeader("location", stringOutput.getLocation());
            return;
        }
        stringOutput.copyHeaders(response);
        String content = stringOutput.toString();
        if (content == null)
            return;

        OutputStream out = response.getOutputStream();
        Writer writer = new OutputStreamWriter(out, stringOutput.getCharsetName());

        IRegionParser parser = createParser();
        List<IRegion> parsed = parser.parse(content);
        for (IRegion region : parsed) {
            try {
                region.process(writer, request);
            } catch (RetrieveException e) {
                writer.append(e.getStatusCode() + " " + e.getStatusMessage());
            }
        }
        // Don't forget to flush the buffer as it is a home made writer, it is not flushed automatically.
        writer.flush();
    }

    protected IRegionParser createParser() {
        return new AggregateRendererRegionParser(propagateJsessionId);
    }
}
