package net.webassembletool.parse;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.AggregationSyntaxException;
import net.webassembletool.Context;
import net.webassembletool.Driver;
import net.webassembletool.DriverFactory;
import net.webassembletool.RenderingException;
import net.webassembletool.RetrieveException;
import net.webassembletool.ouput.StringOutput;

/**
 * Retrieves a resource from the provider application and parses it to find tags
 * to be replaced by contents from other providers.
 * 
 * Sample syntax used for includes :
 * <ul>
 * <li>&lt;!--$includeblock$provider$page$blockname$--&gt;</li>
 * <li>&lt;!--$beginincludetemplate$provider$page$templatename$--&gt;</li>
 * <li>&lt;!--$beginput$name$--&gt;</li>
 * </ul>
 * 
 * Sample syntax used inside included contents for template and block definition
 * :
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
    private final Context context;

    public AggregateRenderer(HttpServletResponse response, Context context) {
	this.response = response;
	this.context = context;
    }

    /** {@inheritDoc} */
    public void render(StringOutput stringOutput, Map<String, String> unused)
	    throws IOException, RenderingException {
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
	response.setCharacterEncoding(stringOutput.getCharsetName());
	Writer writer = response.getWriter();
	Tag previousCloseTag = null;
	// look for includeBlock or includeTemplate markers
	Tag openTag = Tag.find("include", content);
	Tag closeTag = null;
	if (openTag != null) {
	    if ("includeblock".equals(openTag.getTokens()[0])) {
		closeTag = Tag.find("endincludeblock", content);
		if (closeTag == null)
		    closeTag = openTag;
	    } else if ("includetemplate".equals(openTag.getTokens()[0]))
		closeTag = Tag.find("endincludetemplate", content);
	    else
		// False alert, wrong tag
		openTag = null;
	}
	while (openTag != null) {
	    if (closeTag == null)
		throw new AggregationSyntaxException("Tag not closed: "
			+ openTag);
	    if (previousCloseTag != null)
		writer.append(content, previousCloseTag.getEndIndex(), openTag
			.getBeginIndex());
	    else
		writer.append(content, 0, openTag.getBeginIndex());
	    if (openTag.countTokens() != 3 && openTag.countTokens() != 4)
		throw new AggregationSyntaxException("Invalid syntax: "
			+ openTag);
	    String tagName = openTag.getTokens()[0];
	    String provider = openTag.getTokens()[1];
	    String page = openTag.getTokens()[2];
	    String blockOrTemplate;
	    if (openTag.countTokens() == 4)
		blockOrTemplate = openTag.getTokens()[3];
	    else
		blockOrTemplate = null;
	    try {
		if ("includeblock".equals(tagName)) {
		    DriverFactory.getInstance(provider).renderBlock(page,
			    blockOrTemplate, writer, context, null, null);
		} else {
		    aggregateTemplate(page, blockOrTemplate, content.substring(
			    openTag.getEndIndex(), closeTag.getBeginIndex()),
			    writer, DriverFactory.getInstance(provider));
		}
	    } catch (RetrieveException e) {
		writer.append(e.getStatusCode() + " " + e.getStatusMessage());
	    }
	    openTag = Tag.findNext("include", content, closeTag);
	    previousCloseTag = closeTag;
	    if (openTag != null) {
		if ("includeblock".equals(openTag.getTokens()[0])) {
		    closeTag = Tag.find("endincludeblock", content);
		    if (closeTag == null)
			closeTag = openTag;
		} else if ("includetemplate".equals(openTag.getTokens()[0]))
		    closeTag = Tag.find("endincludetemplate", content);
		else
		    // False alert, wrong tag
		    openTag = null;
	    }
	}
	if (previousCloseTag != null)
	    writer.append(content, previousCloseTag.getEndIndex(), content
		    .length());
	else
	    writer.append(content, 0, content.length());
    }

    /**
     * Aggregates a template to the output writer </ul>
     * 
     * Searches for tags :
     * <ul>
     * <li>&lt;!--$beginput$name$--&gt;</li>
     * <li>&lt;!--$endput$--&gt;</li>
     * </ul>
     * 
     * @throws RetrieveException If an Exception occurs while retrieving the
     *             template
     * 
     */
    private final void aggregateTemplate(String page, String template,
	    String content, Writer writer, Driver driver) throws IOException,
	    RenderingException {
	Tag openTag = Tag.find("beginput", content);
	Tag closeTag = null;
	if (openTag != null)
	    closeTag = Tag.findNext("endput", content, openTag);
	HashMap<String, String> params = new HashMap<String, String>();
	while (openTag != null) {
	    // look for includeBlock or includeTemplate markers
	    if (openTag.countTokens() != 2)
		throw new AggregationSyntaxException("Invalid syntax: "
			+ openTag);
	    if (closeTag == null)
		throw new AggregationSyntaxException("Tag not closed: "
			+ openTag);
	    String name = openTag.getTokens()[1];
	    params.put(name, content.substring(openTag.getEndIndex(), closeTag
		    .getBeginIndex()));
	    openTag = Tag.findNext("beginput", content, closeTag);
	    if (openTag != null)
		closeTag = Tag.findNext("endput", content, openTag);
	}
	driver.renderTemplate(page, template, writer, context, params, null,
		null);
    }
}
