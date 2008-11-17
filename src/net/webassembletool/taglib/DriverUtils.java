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
import net.webassembletool.DriverFactory;
import net.webassembletool.RenderException;

/**
 * Utility class used by all tags to access to the Driver
 * 
 * @author François-Xavier Bonnet
 */
public class DriverUtils {
    public final static String getBaseUrl(String provider) {
	return DriverFactory.getInstance(provider).getBaseURL();
    }

    public final static void renderBlock(String provider, String page,
	    String name, PageContext pageContext,
	    Map<String, String> replaceRules, Map<String, String> parameters)
	    throws JspException {

	try {
	    try {
		Driver driver = DriverFactory.getInstance(provider);
		driver.renderBlock(
			page,
			name,
			pageContext.getOut(),
			driver.getContext(
				(HttpServletRequest) pageContext.getRequest()),
			replaceRules, parameters);
	    } catch (RenderException e) {
		pageContext.getOut().write(
			e.getStatusCode() + " " + e.getStatusMessage());
	    }
	} catch (IOException e) {
	    throw new JspException(e);
	}
    }

    public final static void renderTemplate(String provider, String page,
	    String name, PageContext pageContext, Map<String, String> params,
	    Map<String, String> replaceRules, Map<String, String> parameters)
	    throws JspException {

	try {
	    try {
		Driver driver = DriverFactory.getInstance(provider);
		driver.renderTemplate(
			page,
			name,
			pageContext.getOut(),
			driver.getContext(
				(HttpServletRequest) pageContext.getRequest()),
			params, replaceRules, parameters);
	    } catch (RenderException e) {
		pageContext.getOut().write(
			e.getStatusCode() + " " + e.getStatusMessage());
	    }
	} catch (IOException e) {
	    throw new JspException(e);
	}
    }
}
