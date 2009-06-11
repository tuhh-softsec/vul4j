package net.webassembletool.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;
import org.apache.commons.logging.LogFactory;

/**
 * 'defaultError' JSP tag.
 * <p>
 * Displays the body's content of retrieved resource instead of the error message in case when error happens while retrieving resource.
 * 
 * @author Cedric BRANDES
 */
public class DefaultErrorTag extends BodyTagSupport {
    private static final long serialVersionUID = -4466967947717753242L;

    @Override
    public int doAfterBody() throws JspException {
        ErrorManageableTag parent = getFirstAvailableParent();
        if (parent == null) {
            LogFactory.getLog(ErrorDefinitionTag.class).warn("No parent found, skipping tag");
            return Tag.SKIP_BODY;
        }
        String value = getBodyContent().getString();
        parent.setDefaultMessage(value);
        return Tag.SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        if (getBodyContent() == null) {
            ErrorManageableTag parent = getFirstAvailableParent();
            if (parent == null) {
                LogFactory.getLog(ErrorDefinitionTag.class).warn("No parent found, skipping tag");
                return Tag.SKIP_BODY;
            }
            parent.setDefaultMessage("");
        }
        return Tag.SKIP_BODY;
    }

    private ErrorManageableTag getFirstAvailableParent() {
        ErrorManageableTag parent = null;
        if (getParent() instanceof ErrorManageableTag)
            parent = (ErrorManageableTag) getParent();
        else if (getParent() instanceof IfTag && getParent().getParent() instanceof ErrorManageableTag)
            parent = (ErrorManageableTag) getParent().getParent();
        return parent;
    }
}
