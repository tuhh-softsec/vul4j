package net.webassembletool.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import net.webassembletool.Driver;

/**
 * Retrieves an HTML fragment from the provider application and inserts it into
 * the page. Extends AbstractReplaceableTag, so a ReplaceTag can be used inside
 * this tag.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class IncludeBlockTag extends AbstractReplaceableTag {
    private String name;
    private String page;
    private String provider;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public int doEndTag() throws JspException {
	try {
	    Driver.getInstance(provider).renderBlock(page, name, pageContext,
		    replaceRules);
	} catch (IOException e) {
	    throw new JspException(e);
	}

	return EVAL_PAGE;
    }

    public String getPage() {
	return page;
    }

    public void setPage(String page) {
	this.page = page;
    }

    String getProvider() {
	return provider;
    }

    void setProvider(String provider) {
	this.provider = provider;
    }
}
