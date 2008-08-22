/**
 * 
 */
package net.webassembletool.taglib;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import net.webassembletool.Driver;

/**
 * TODO
 * 
 * @author FRBON, 21 août 2008
 */
public class DriverUtils {
    public final static String getBaseUrl(String provider) {
	return Driver.getInstance(provider).getBaseURL();
    }

    public final static void renderBlock(String provider, String page,
	    String name, PageContext pageContext,
	    Map<String, String> replaceRules, Map<String, String> parameters)
	    throws JspException {

	try {
	    Driver.getInstance(provider).renderBlock(
		    page,
		    name,
		    pageContext.getOut(),
		    Driver.getInstance(provider).getContext(
			    (HttpServletRequest) pageContext.getRequest()),
		    replaceRules, parameters);
	} catch (IOException e) {
	    throw new JspException(e);
	}
    }

    public final static void renderTemplate(String provider, String page,
	    String name, PageContext pageContext, Map<String, String> params,
	    Map<String, String> replaceRules, Map<String, String> parameters)
	    throws JspException {

	try {
	    Driver.getInstance(provider).renderTemplate(
		    page,
		    name,
		    pageContext.getOut(),
		    Driver.getInstance(provider).getContext(
			    (HttpServletRequest) pageContext.getRequest()),
		    params, replaceRules, parameters);
	} catch (IOException e) {
	    throw new JspException(e);
	}
    }
}
