package net.webassembletool.taglib.esi;

import javax.servlet.jsp.tagext.BodyTagSupport;

public class CommentTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	public int doEndTag() {
		return EVAL_PAGE;
	}

	@Override
	public int doStartTag() {
		return SKIP_BODY;
	}

	public void setText(String text) {
		// Just ignore
	}
}
