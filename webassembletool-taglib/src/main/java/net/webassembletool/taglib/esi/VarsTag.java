package net.webassembletool.taglib.esi;

import java.io.IOException;

import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class VarsTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	public int doAfterBody() {
		BodyContent bc = getBodyContent();
		String body = bc.getString();
		String hb = TagsUtils.processVars(body, pageContext);

		try {
			bc.getEnclosingWriter().print(hb);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return SKIP_BODY;
	}

}
