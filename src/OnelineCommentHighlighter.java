package net.sf.xslthl;

import java.util.*;

class OnelineCommentHighlighter extends Highlighter {

	private String start;
	
	OnelineCommentHighlighter(Params params) {
		start = params.getParam();
	}
	
	boolean startsWith(CharIter in) {
		if (in.startsWith(start)) {
			return true;
		}
		return false;
	}

	boolean highlight(CharIter in, List<Block> out) {
		in.moveNext(start.length()); // skip start
		int endIndex = in.indexOf("\n");
		if (endIndex == -1) {
			in.moveToEnd();
		} else {
			in.moveNext(endIndex);
			if (in.prev().equals('\r')) {
				in.moveNext(-1);
			}
		}
		out.add(in.markedToStyledBlock("comment"));
		return true;		
	}

}
