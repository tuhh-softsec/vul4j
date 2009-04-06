package net.webassembletool.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;
import org.apache.commons.logging.LogFactory;

/**
 * Only used inside a tag that implements IReplaceableTag. This tag add a replace rule to the parent tag, which will be applied on render.
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
     * @param expression
     *            Regular expression to be replaced by the tag body
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * Add a new replace rule to the parent tag. The expression to replace of the rule is the expression parameter, the new value is the content of the body
     * 
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doAfterBody()
     */
    @Override
    public int doAfterBody() throws JspException {
        ReplaceableTag parent = getFirstAvailableParent();
        if (parent == null) {
            LogFactory.getLog(ReplaceTag.class).warn("No parent found, skipping tag");
            return Tag.SKIP_BODY;
        }
        if (expression == null)
            expression = "";
        String newExpression = getBodyContent().getString();
        parent.getReplaceRules().put(expression, newExpression);
        return Tag.SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        if (getBodyContent() == null) {
            ReplaceableTag parent = getFirstAvailableParent();
            if (parent == null) {
                LogFactory.getLog(ReplaceTag.class).warn("No parent found, skipping tag");
                return Tag.SKIP_BODY;
            }
            parent.getReplaceRules().put(expression, "");
        }
        return Tag.SKIP_BODY;
    }

    private ReplaceableTag getFirstAvailableParent() {
        ReplaceableTag parent = null;
        if (getParent() instanceof ReplaceableTag)
            parent = (ReplaceableTag) getParent();
        else if (getParent() instanceof IfTag && getParent().getParent() instanceof ReplaceableTag)
            parent = (ReplaceableTag) getParent().getParent();
        return parent;
    }
}
