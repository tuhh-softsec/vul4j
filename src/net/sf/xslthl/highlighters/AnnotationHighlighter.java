/**
 * 
 */
package net.sf.xslthl.highlighters;

import java.util.List;

import net.sf.xslthl.Block;
import net.sf.xslthl.CharIter;
import net.sf.xslthl.Highlighter;
import net.sf.xslthl.HighlighterConfigurationException;
import net.sf.xslthl.Params;

/**
 * Recognizes annotations. Accepted parameters:
 * <dl>
 * <dt>start</dt>
 * <dd>How the annotation starts. <b>Required.</b></dd>
 * <dt>end</dt>
 * <dd>How the annotation ends. Optional.</dd>
 * <dt>valueStart</dt>
 * <dd>String used to start the value section of annotations</dd>
 * <dt>valueEnd</dt>
 * <dd>String used to end the value section of annotations</dd>
 * </dl>
 */
public class AnnotationHighlighter extends Highlighter {

    protected String start, end, valueStart, valueEnd;

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#init(net.sf.xslthl.Params)
     */
    public void init(Params params) throws HighlighterConfigurationException {
	super.init(params);
	start = params.getParam("start");
	end = params.getParam("end");
	valueStart = params.getParam("valueStart");
	valueEnd = params.getParam("valueEnd");
	if (start == null || start.length() == 0) {
	    throw new HighlighterConfigurationException(
		    "Required parameter 'start' is not set.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#getDefaultStyle()
     */
    @Override
    public String getDefaultStyle() {
	return "annotation";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#startsWith(net.sf.xslthl.CharIter)
     */
    @Override
    public boolean startsWith(CharIter in) {
	if (in.startsWith(start)) {
	    return Character.isJavaIdentifierStart(in.next(start.length()));
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
	in.moveNext(start.length()); // skip start
	int valueCnt = 0;
	boolean hadEnd = false;
	while (!in.finished()) {
	    if (valueStart != null && in.startsWith(valueStart)) {
		++valueCnt;
	    } else if (valueEnd != null && in.startsWith(valueEnd)) {
		--valueCnt;
	    } else if (valueCnt == 0) {
		if (end != null && valueCnt == 0 && in.startsWith(end)) {
		    in.moveNext(end.length());
		    hadEnd = true;
		    break;
		} else if (end == null && Character.isWhitespace(in.current())) {
		    hadEnd = true;
		    break;
		}
	    }
	    in.moveNext();
	}
	if (!hadEnd) {
	    return false;
	}
	out.add(in.markedToStyledBlock(styleName));
	return true;
    }

}
