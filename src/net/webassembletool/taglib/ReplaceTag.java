package net.webassembletool.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Only used inside a tag that implements IReplaceableTag. This tag add a
 * replace rule to the parent tag, which will be applied on render.
 * 
 * <hr>
 * 
 * @author Cedric Brandes, 27 juin 08
 */
public class ReplaceTag extends BodyTagSupport {
    private static final long serialVersionUID = 1L;
    private String expression;

    public String getExpression() {
	return expression;
    }

    /**
     * Sets the expression to replace.
     * 
     * @param expression Regular expression to be replaced by the tag body
     */
    public void setExpression(String expression) {
	this.expression = expression;
    }

    /**
     * Add a new replace rule to the parent tag. The expression to replace of
     * the rule is the expression parameter, the new value is the content of the
     * body
     * 
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doAfterBody()
     */
    @Override
    public int doAfterBody() throws JspException {
	IReplaceableTag replaceableTag = (IReplaceableTag) getParent();
	String newExpression = getBodyContent().getString();
	replaceableTag.getReplaceRules().put(expression, newExpression);
	return SKIP_BODY;
    }
}
