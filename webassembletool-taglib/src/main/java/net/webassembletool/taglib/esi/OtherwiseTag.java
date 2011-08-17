package net.webassembletool.taglib.esi;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class OtherwiseTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	public int doAfterBody() throws JspTagException {
		// find parent
		ChooseTag parent = (ChooseTag) findAncestorWithClass(this,
				ChooseTag.class);
		if (parent == null) {
			throw new JspTagException("Otherwise tag is not inside Choose tag");
		}

		if (!parent.isCondition()) {
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
		ChooseTag parent = (ChooseTag) findAncestorWithClass(this,
				ChooseTag.class);
		if (parent == null) {
			throw new JspTagException("Otherwise tag is not inside Choose tag");
		}

		// handle test condition
		if (!parent.hasCondition()) {
			throw new JspTagException("Otherwise tag must come before When tag");
		}

		return EVAL_BODY_BUFFERED;
	}

}
