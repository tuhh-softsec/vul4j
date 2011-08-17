package net.webassembletool.taglib.esi;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class WhenTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;
	private String test;

	@Override
	public int doAfterBody() throws JspTagException {
		// find parent
		ChooseTag parent = (ChooseTag) findAncestorWithClass(this,
				ChooseTag.class);
		if (parent == null) {
			throw new JspTagException("When tag is not inside Choose tag");
		}

		if (parent.isCondition()) {
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
			throw new JspTagException("When tag is not inside Choose tag");
		}

		// exclude others When tags if previous one was true
		if (parent.isCondition()) {
			return SKIP_BODY;
		}

		// handle test condition
		parent.setCondition(Operations.processOperators(test));

		return EVAL_BODY_BUFFERED;
	}

	public void setTest(String test) {
		this.test = TagsUtils.processVars(test, pageContext);
	}

}
