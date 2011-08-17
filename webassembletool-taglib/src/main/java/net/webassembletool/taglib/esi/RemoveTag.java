package net.webassembletool.taglib.esi;

import javax.servlet.jsp.tagext.BodyTagSupport;

public class RemoveTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	public int doAfterBody() {
		return SKIP_BODY;
	}

}
