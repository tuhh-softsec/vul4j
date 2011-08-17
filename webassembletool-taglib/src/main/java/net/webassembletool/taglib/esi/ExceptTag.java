package net.webassembletool.taglib.esi;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ExceptTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	public int doAfterBody() throws JspTagException {
		// find parent
		TryTag parent = (TryTag) findAncestorWithClass(this, TryTag.class);
		if (parent == null) {
			throw new JspTagException("Expect tag is not inside Try tag");
		}

		if (parent.isIncludeInside()) {
			try {
				BodyContent bc = getBodyContent();
				bc.getEnclosingWriter().print(bc.getString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return SKIP_BODY;
	}

	@Override
	public int doEndTag() {
		return EVAL_PAGE;
	}

	@Override
	public int doStartTag() throws JspTagException {

		// find parent
		TryTag parent = (TryTag) findAncestorWithClass(this, TryTag.class);
		if (parent == null || !parent.hasCondition()) {
			throw new JspTagException("Expect tag is not inside Try tag");
		}

		return EVAL_BODY_BUFFERED;
	}

}
