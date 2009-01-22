package net.webassembletool.parse;

import java.util.LinkedList;
import java.util.List;

import net.webassembletool.AggregationSyntaxException;

/**
 * {@linkplain IRegionParser} parser implementation used internally in
 * {@linkplain AggregateRenderer}.
 * <p>
 * Parses content to find tags to be replaced by contents from other providers.
 * Sample syntax used for includes :
 * <ul>
 * <li>&lt;!--$includeblock$provider$page$blockname$--&gt;</li>
 * <li>&lt;!--$beginincludetemplate$provider$page$templatename$--&gt;</li>
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
public class AggregateRendererRegionParser implements IRegionParser {

    /** {@inheritDoc} */
    public List<IRegion> parse(String content)
	    throws AggregationSyntaxException {
	List<IRegion> result = new LinkedList<IRegion>();
	Result found = find(content, 0);
	while (found != null) {
	    result.add(found.getRegion());
	    found = find(content, found.getPos());
	}
	return result;
    }

    /**
     * Finds next {@linkplain IRegion} in content starting from provided
     * <code>position</code>.
     * 
     * @return {@linkplain Result} object with next found {@linkplain IRegion}
     *         and its end position for further parsing or <code>null</code> if
     *         no more regions could not be found.
     */
    protected Result find(String content, int position)
	    throws AggregationSyntaxException {
	if (position >= content.length()) {
	    return null;
	}
	// look for includeBlock or includeTemplate markers
	Tag openTag = Tag.find("include", content, position);
	if (openTag == null) {
	    return new Result(new UnmodifiableRegion(content, position, content
		    .length()), content.length());
	} else if (openTag.getBeginIndex() > position) {
	    return new Result(new UnmodifiableRegion(content, position, openTag
		    .getBeginIndex()), openTag.getBeginIndex());
	} else { // start with 'include'
	    if (openTag.countTokens() < 3 || openTag.countTokens() > 4)
		throw new AggregationSyntaxException("Invalid syntax: "
			+ openTag);
	    String provider = openTag.getToken(1);
	    String page = openTag.getToken(2);
	    String blockOrTemplate = (openTag.countTokens() == 4) ? openTag
		    .getToken(3) : null;
	    if ("includeblock".equals(openTag.getToken(0))) {
		Tag closeTag = Tag.findNext("", content, openTag);
		if (closeTag == null
			|| !"endincludeblock".equals(closeTag.getToken(0))) {
		    closeTag = openTag;
		}
		return new Result(new IncludeBlockRegion(provider, page,
			blockOrTemplate), closeTag.getEndIndex());
	    } else if ("includetemplate".equals(openTag.getToken(0))) {
		Tag closeTag = Tag.findNext("endincludetemplate", content,
			openTag);
		return new Result(new IncludeTemplateRegion(provider, page,
			blockOrTemplate, content.substring(openTag
				.getEndIndex(), closeTag.getBeginIndex())),
			closeTag.getEndIndex());
	    } else {
		// False alert, wrong tag
		throw new AggregationSyntaxException("Unknown tag: " + openTag);
	    }
	}
    }

    protected static class Result {
	private final IRegion region;
	private final int pos;

	public Result(IRegion region, int pos) {
	    this.region = region;
	    this.pos = pos;
	}

	public IRegion getRegion() {
	    return region;
	}

	public int getPos() {
	    return pos;
	}
    }
}
