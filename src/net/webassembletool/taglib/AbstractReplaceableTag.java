package net.webassembletool.taglib;

import java.util.HashMap;

import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Abstract class that defines a tag in which the ReplaceTag can be used. The
 * replaceRules property represents the couple replace rules to be applied.
 * 
 * <hr>
 * 
 * @author Cédric Brandes, 27 juin 08
 */
public abstract class AbstractReplaceableTag extends BodyTagSupport {

    protected HashMap<String, String> replaceRules = new HashMap<String, String>();

    /**
     * @return replaceRules.
     */
    public HashMap<String, String> getReplaceRules() {
	return replaceRules;
    }

    /**
     * @param replaceRules
     */
    public void setReplaceRules(HashMap<String, String> replaceRules) {
	this.replaceRules = replaceRules;
    }
}
