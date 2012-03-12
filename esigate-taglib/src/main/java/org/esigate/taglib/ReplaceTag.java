package org.esigate.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.slf4j.LoggerFactory;

/**
 * Only used as a descendant of a tag that implements IReplaceableTag. This tag
 * add a replace rule to the first replaceable parent tag, which will be applied
 * on render.
 * <hr>
 * 
 * @author Cedric Brandes, 27 juin 08
 * @author Sylvain Sicard
 */
public class ReplaceTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	private String expression;

	/**
	 * Add a new replace rule to the first replaceable parent tag. The
	 * expression to replace of the rule is the expression parameter, the new
	 * value is the content of the body
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doAfterBody()
	 */
	@Override
	public int doAfterBody() throws JspException {
		ReplaceableTag parent = getFirstReplaceableParent();
		if (parent == null) {
			LoggerFactory.getLogger(ReplaceTag.class).warn(
					"No replaceable parent found, skipping tag");
			return Tag.SKIP_BODY;
		}
		if (expression == null) {
			expression = "";
		}
		String newExpression = getBodyContent().getString();
		parent.getReplaceRules().put(expression, newExpression);
		return Tag.SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		if (getBodyContent() == null) {
			ReplaceableTag parent = getFirstReplaceableParent();
			if (parent == null) {
				LoggerFactory.getLogger(ReplaceTag.class).warn(
						"No replaceable parent found, skipping tag");
				return Tag.SKIP_BODY;
			}
			parent.getReplaceRules().put(expression, "");
		}
		return Tag.SKIP_BODY;
	}

	public String getExpression() {
		return expression;
	}

	protected ReplaceableTag getFirstReplaceableParent() {
		Tag tag = getParent();
		while (!(tag instanceof ReplaceableTag)) {
			if (tag == null) {
				break;
			}
			tag = tag.getParent();
		}
		return (ReplaceableTag) tag;
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
}
