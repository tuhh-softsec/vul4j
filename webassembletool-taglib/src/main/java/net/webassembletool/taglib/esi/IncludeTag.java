package net.webassembletool.taglib.esi;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import net.webassembletool.taglib.DriverUtils;

public class IncludeTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;
	private String alt = null;
	private final String errorMessage = "An ESI Processor can fetch neither the 'src' nor the 'alt' sources";
	private String onerror = null;
	private String src;

	@Override
	public int doEndTag() {
		return EVAL_PAGE;
	}

	@Override
	public int doStartTag() {
		try {
			// try to load 'src' source
			// pageContext.getOut().print(TagsUtils.loadContent(src));
			DriverUtils.renderEsi(null, src, pageContext);

		} catch (Exception e) {
			try {
				// try to load 'alt' source
				if (alt != null && !alt.equals("")) {
					// pageContext.getOut().print(TagsUtils.loadContent(alt));
					DriverUtils.renderEsi(null, alt, pageContext);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				try {
					if (onerror == null || !onerror.equals("continue")) {
						((HttpServletResponse) pageContext.getResponse())
								.sendError(404, errorMessage);
						return SKIP_BODY;
					}
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}

			e.printStackTrace();
			try {
				Tag parent = getParent();
				if (parent instanceof AttemptTag) {
					TryTag tryTag = (TryTag) parent.getParent();
					tryTag.setIncludeInside(true);
				} else if (onerror == null || !onerror.equals("continue")) {
					((HttpServletResponse) pageContext.getResponse())
							.sendError(404, errorMessage);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return SKIP_BODY;
	}

	public void setAlt(String alt) {
		this.alt = TagsUtils.processVars(alt, pageContext);
	}

	public void setOnerror(String onerror) {
		this.onerror = onerror;
	}

	public void setSrc(String src) {
		this.src = TagsUtils.processVars(src, pageContext);
	}

}
