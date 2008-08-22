package net.webassembletool.taglib;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Retrieves a template from the provider application and inserts it into the
 * page replacing the parameters. Extends AbstractReplaceableTag, so a
 * ReplaceTag can be used inside this tag.
 * 
 * @author François-Xavier Bonnet
 * 
 * 
 */
public class IncludeTemplateTag extends BodyTagSupport implements
	IReplaceableTag, IParameterTag {
    private static final long serialVersionUID = 1L;
    private String name = null;
    private String page = null;
    private String provider;
    private Map<String, String> params = new HashMap<String, String>();
    private Map<String, String> replaceRules = new HashMap<String, String>();
    private Map<String, String> parameters = new HashMap<String, String>();
    Properties prop = null;

    public int doEndTag() throws JspException {
	DriverUtils.renderTemplate(provider, page, name, pageContext, params,
		replaceRules, parameters);
	name = null;
	page = null;
	params = new HashMap<String, String>();
	replaceRules = new HashMap<String, String>();
	parameters = new HashMap<String, String>();
	return EVAL_PAGE;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getPage() {
	return page;
    }

    public void setPage(String page) {
	this.page = page;
    }

    public Map<String, String> getParams() {
	return params;
    }

    public void setParams(Map<String, String> params) {
	this.params = params;
    }

    String getProvider() {
	return provider;
    }

    void setProvider(String provider) {
	this.provider = provider;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.webassembletool.taglib.IReplaceableTag#getReplaceRules()
     */
    public Map<String, String> getReplaceRules() {
	return replaceRules;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.webassembletool.taglib.IParameterTag#getParameters()
     */
    public Map<String, String> getParameters() {
	return parameters;
    }
}
