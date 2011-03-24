package net.webassembletool.taglib.esi;

import javax.servlet.jsp.tagext.BodyTagSupport;

public class RemoveTag extends BodyTagSupport {

	@Override
	public int doAfterBody() {
		return SKIP_BODY;
	}

}
