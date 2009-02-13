package net.webassembletool.parse;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.AggregationSyntaxException;
import net.webassembletool.Driver;
import net.webassembletool.RenderingException;
import net.webassembletool.RetrieveException;

/**
 * Represents a <code>template</code> region which is located between
 * <code>&lt;!--$includetemplate...$--></code> and
 * <code>&lt;!--$endincludetemplate$--></code> tags.
 * <p>
 * Aggregates a template to the output writer. Searches for tags:
 * <ul>
 * <li>&lt;!--$beginput$name$--&gt;</li>
 * <li>&lt;!--$endput$--&gt;</li>
 * </ul>
 * 
 * @see Driver#renderTemplate(String, String, Writer, HttpServletRequest, Map,
 *      Map, Map, boolean)
 * @author Stanislav Bernatskyi
 */
public class IncludeTemplateRegion extends AbstractIncludeRegion {
    private final String templateBody;

    public IncludeTemplateRegion(String provider, String page, String name,
            boolean propagateJsessionId, String templateBody) {
        super(provider, page, name, propagateJsessionId);
        this.templateBody = templateBody;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws RetrieveException If an Exception occurs while retrieving the
     *             template
     */
    public void process(Writer out, HttpServletRequest request)
            throws IOException, RenderingException {
        Map<String, String> params = parseParameters(templateBody);
        getDriver().renderTemplate(page, name, out, request, params, null,
                null, propagateJsessionId);
    }

    /**
     * Extracts parameters from provided <code>content</code>.
     * <p>
     * Searches for tags:
     * <ul>
     * <li>&lt;!--$beginput$name$--&gt;</li>
     * <li>&lt;!--$endput$--&gt;</li>
     * </ul>
     */
    protected Map<String, String> parseParameters(String content)
            throws AggregationSyntaxException {
        Map<String, String> result = new HashMap<String, String>();
        Tag openTag = Tag.find("beginput", content);
        while (openTag != null) {
            if (openTag.countTokens() != 2)
                throw new AggregationSyntaxException("Invalid syntax: "
                        + openTag);
            Tag closeTag = Tag.findNext("endput", content, openTag);
            if (closeTag == null)
                throw new AggregationSyntaxException("Tag not closed: "
                        + openTag);
            String paramName = openTag.getToken(1);
            result.put(paramName, content.substring(openTag.getEndIndex(),
                    closeTag.getBeginIndex()));
            openTag = Tag.findNext("beginput", content, closeTag);
        }
        return result;
    }

}
