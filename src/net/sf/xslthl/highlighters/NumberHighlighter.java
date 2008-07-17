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
 * Recognizes numbers. Accepted parameters:
 * <dl>
 * <dt>point</dt>
 * <dd>character used for the decimal point. If not declared no decimal points
 * are accepted.</dd>
 * <dt>thousands</dt>
 * <dd>thousand separator</dd>
 * <dt>exponent</dt>
 * <dd>the string used for recognizing the exponent part of a floating point</dd>
 * <dt>pointStarts</dt>
 * <dd>a switch, when set the value defined as point can also be used to start a
 * number. For example ".1234" would also be accepted as a number.</dd>
 * <dt>prefix</dt>
 * <dd>required start of a number, can be useful in the hexnumber highlighter to
 * define how a hexadecimal number is started</dd>
 * <dt>suffix</dt>
 * <dd>an optional string that can be found after a number, can be define
 * multiple times. This is often used to set the "size" of a integer or floating
 * point.</dd>
 * <dt>ignoreCase</dt>
 * <dd>all strings parameters are case insensitive</dd>
 * </dl>
 */
public class NumberHighlighter extends Highlighter {

    /**
     * The decimal point
     */
    protected String decimalPoint;

    /**
     * Thousand seperator
     */
    protected String thousandSep;

    /**
     * The character to use to start the exponent
     */
    protected String exponent;

    /**
     * Opional suffixes
     */
    protected List<String> suffix;

    /**
     * Required prefix
     */
    protected String prefix;

    /**
     * Ignore case when looking for exponent and flags
     */
    protected boolean ignoreCase;

    /**
     * If true a number can start with the decimal point
     */
    protected boolean pointStarts;

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#init(net.sf.xslthl.Params)
     */
    @Override
    public void init(Params params) throws HighlighterConfigurationException {
	super.init(params);
	decimalPoint = params.getParam("point");
	thousandSep = params.getParam("thousands");
	exponent = params.getParam("exponent");
	pointStarts = decimalPoint != null && params.isSet("pointStarts");
	ignoreCase = params.isSet("ignoreCase");
	suffix = new ArrayList<String>();
	params.getMutliParams("suffix", suffix);
	prefix = params.getParam("prefix");
    }

    /**
     * Return true if it is an ascii digit
     * 
     * @param ch
     * @return
     */
    protected boolean isDigit(char ch) {
	return ch >= '0' && ch <= '9';
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#getDefaultStyle()
     */
    @Override
    public String getDefaultStyle() {
	return "number";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#startsWith(net.sf.xslthl.CharIter)
     */
    @Override
    public boolean startsWith(CharIter in) {
	if (in.getPosition() > 0 && Character.isLetter(in.prev())) {
	    return false;
	}
	if (prefix != null) {
	    return in.startsWith(prefix, ignoreCase)
		    && isDigit(in.next(prefix.length()));
	}
	if (pointStarts && in.startsWith(decimalPoint, ignoreCase)
		&& isDigit(in.next(decimalPoint.length()))) {
	    return true;
	}
	return isDigit(in.current());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#highlight(net.sf.xslthl.CharIter,
     * java.util.List)
     */
    @Override
    public boolean highlight(CharIter in, List<Block> out) {
	boolean hadPoint = false;
	boolean hadExponent = false;
	if (prefix != null && in.startsWith(prefix, ignoreCase)) {
	    in.moveNext(prefix.length());
	}
	if (pointStarts && in.startsWith(decimalPoint, ignoreCase)) {
	    in.moveNext(decimalPoint.length());
	    hadPoint = true;
	}
	while (!in.finished()) {
	    if (!hadPoint) {
		if (decimalPoint != null
			&& in.startsWith(decimalPoint, ignoreCase)) {
		    hadPoint = true;
		    in.moveNext(decimalPoint.length());
		    continue;
		} else if (thousandSep != null
			&& in.startsWith(thousandSep, ignoreCase)) {
		    in.moveNext(thousandSep.length());
		    continue;
		}
	    }
	    if (!hadExponent) {
		if (exponent != null && in.startsWith(exponent, ignoreCase)) {
		    hadPoint = true;
		    hadExponent = true;
		    in.moveNext(exponent.length());
		    if (in.current().equals('-') || in.current().equals('+')) {
			in.moveNext();
		    }
		    continue;
		}
	    }
	    if (!isDigit(in.current())) {
		break;
	    }
	    in.moveNext();
	}
	for (String suf : suffix) {
	    if (in.startsWith(suf, ignoreCase)) {
		in.moveNext(suf.length());
		break;
	    }
	}
	if (!in.finished() && Character.isLetter(in.current())) {
	    return false;
	}
	out.add(in.markedToStyledBlock(styleName));
	return true;
    }

}
