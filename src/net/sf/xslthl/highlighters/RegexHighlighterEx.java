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
 * <dt>flags</dt>
 * <dd>Flags, see javadoc (use the constant names AS IS)</dd>
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
		String[] flagString = params.getParam("flags", "").split("[;,]");
		for (String flag : flagString) {
			flag = flag.trim();
			if ("CASE_INSENSITIVE".equalsIgnoreCase(flag)) {
				flags += Pattern.CASE_INSENSITIVE;
			} else if ("DOTALL".equalsIgnoreCase(flag)) {
				flags += Pattern.DOTALL;
			} else if ("MULTILINE".equalsIgnoreCase(flag)) {
				flags += Pattern.MULTILINE;
			} else if ("UNICODE_CASE".equalsIgnoreCase(flag)) {
				flags += Pattern.UNICODE_CASE;
			} else if ("LITERAL".equalsIgnoreCase(flag)) {
				flags += Pattern.LITERAL;
			} else if ("UNIX_LINES".equalsIgnoreCase(flag)) {
				flags += Pattern.UNIX_LINES;
			} else if ("COMMENTS".equalsIgnoreCase(flag)) {
				flags += Pattern.COMMENTS;
			} else if ("CANON_EQ".equalsIgnoreCase(flag)) {
				flags += Pattern.CANON_EQ;
			}
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
		while (!finished && matcher.start() < in.getPosition()) {
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
