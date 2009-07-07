package net.sf.xslthl.plugins;

import net.sf.xslthl.highlighters.OnelineCommentHighlighter;

public class AltOnelineComment extends OnelineCommentHighlighter {
	@Override
	public String getDefaultStyle() {
		return "doccomment";
	}
}
