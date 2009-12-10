package net.webassembletool.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * If tag
 * 
 * @author Cedric Brandes
 */
public class IfTag extends BodyTagSupport {

    private static final long serialVersionUID = 7042532778875011712L;
    private boolean test;

    public void setTest(boolean test) {
        this.test = test;
    }

    @Override
    public int doStartTag() throws JspException {
        if (test)
            return EVAL_BODY_INCLUDE;
        else
            return SKIP_BODY;
    }

}
