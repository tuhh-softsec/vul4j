/**
 * 
 */
package net.sf.xslthl.highlighters;

import java.util.ArrayList;
import java.util.List;

import net.sf.xslthl.Block;
import net.sf.xslthl.CharIter;
import net.sf.xslthl.Highlighter;
import net.sf.xslthl.HighlighterConfigurationException;
import net.sf.xslthl.Params;

/**
 * Highlights the list of elements as they are encounterd. No special
 * processing. This is a slow highlighter. Accepted parameters:
 * <dl>
 * <dt>word</dt>
 * <dd>The word it recognizes, can be used multiple times.</dd>
 * <dt>ignoreCase</dt>
 * <dd>the words are recognized case insensitive</dd>
 * </dl>
 */
public class WordHighlighter extends Highlighter {

    /**
     * The words to recognize
     */
    protected List<String> words;

    /**
     * If true ingore the case
     */
    protected boolean caseInsensitive;

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#init(net.sf.xslthl.Params)
     */
    @Override
    public void init(Params params) throws HighlighterConfigurationException {
	super.init(params);
	words = new ArrayList<String>();
	params.getMutliParams("word", words);
	caseInsensitive = params.isSet("ignoreCase");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#startsWith(net.sf.xslthl.CharIter)
     */
    @Override
    public boolean startsWith(CharIter in) {
	for (String string : words) {
	    if (in.startsWith(string, caseInsensitive)) {
		return true;
	    }
	}
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#getDefaultStyle()
     */
    @Override
    public String getDefaultStyle() {
	// no default style
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#highlight(net.sf.xslthl.CharIter,
     * java.util.List)
     */
    @Override
    public boolean highlight(CharIter in, List<Block> out) {
	for (String string : words) {
	    if (in.startsWith(string, caseInsensitive)) {
		in.moveNext(string.length());
		if (in.finished() || Character.isWhitespace(in.current())) {
		    out.add(in.markedToStyledBlock(styleName));
		    return true;
		}
	    }
	}
	return false;
    }

}
