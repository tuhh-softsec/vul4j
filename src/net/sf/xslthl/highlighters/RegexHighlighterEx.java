/**
 * 
 */
package net.sf.xslthl.highlighters;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.xslthl.Block;
import net.sf.xslthl.CharIter;
import net.sf.xslthl.Highlighter;
import net.sf.xslthl.HighlighterConfigurationException;
import net.sf.xslthl.Params;

/**
 * Regular expression highlighter. Accepted parameters:
 * <dl>
 * <dt>pattern</dt>
 * <dd>The regular expression pattern to be matched.</dd>
 * <dt>caseInsensitive, dotAll, multiLine, unicodeCase, literal, unixLines,
 * comments, canonEQ</dt>
 * <dd>Flags, see javadoc</dd>
 * </dl>
 */
public class RegexHighlighterEx extends Highlighter {

    protected Pattern pattern;

    /**
     * Matcher used to find locations
     */
    protected Matcher matcher;

    /**
     * If true this matcher is done
     */
    protected boolean finished;

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#init(net.sf.xslthl.Params)
     */
    @Override
    public void init(Params params) throws HighlighterConfigurationException {
	super.init(params);
	int flags = 0;
	if (params.isSet("caseInsensitive")) {
	    flags += Pattern.CASE_INSENSITIVE;
	}
	if (params.isSet("dotAll")) {
	    flags += Pattern.DOTALL;
	}
	if (params.isSet("multiLine")) {
	    flags += Pattern.MULTILINE;
	}
	if (params.isSet("unicodeCase")) {
	    flags += Pattern.UNICODE_CASE;
	}
	if (params.isSet("literal")) {
	    flags += Pattern.LITERAL;
	}
	if (params.isSet("unixLines")) {
	    flags += Pattern.UNIX_LINES;
	}
	if (params.isSet("comments")) {
	    flags += Pattern.COMMENTS;
	}
	if (params.isSet("canonEQ")) {
	    flags += Pattern.CANON_EQ;
	}
	String pat = params.getParam("pattern");
	if (params.isSet("pattern")) {
	    try {
		pattern = Pattern.compile(pat, flags);
	    } catch (PatternSyntaxException e) {
		throw new HighlighterConfigurationException(e.getMessage(), e);
	    }
	}
	if (pattern == null) {
	    throw new HighlighterConfigurationException(
		    "Required parameter 'pattern' is not set.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#getDefaultStyle()
     */
    @Override
    public String getDefaultStyle() {
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#reset()
     */
    @Override
    public void reset() {
	matcher = null;
	finished = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#startsWith(net.sf.xslthl.CharIter)
     */
    @Override
    public boolean startsWith(CharIter in) {
	if (matcher == null) {
	    matcher = in.createMatcher(pattern);
	    finished = !matcher.find();
	}
	if (!finished) {
	    return matcher.start() == in.getPosition();
	}
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#highlight(net.sf.xslthl.CharIter,
     * java.util.List)
     */
    @Override
    public boolean highlight(CharIter in, List<Block> out) {
	if (!finished) {
	    in.moveNext(matcher.end() - matcher.start());
	    out.add(in.markedToStyledBlock(styleName));
	    finished = !matcher.find();
	    return true;
	}
	return false;
    }

}
