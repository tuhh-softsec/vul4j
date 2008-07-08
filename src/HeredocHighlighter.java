package net.sf.xslthl;

import java.util.*;

class HeredocHighlighter extends Highlighter {
	
	private String start;
	
	HeredocHighlighter(Params params) {
		start  = params.getParam("start");
	}
	
	boolean startsWith(CharIter in) {
		if (in.startsWith(start)) {
			return true;
		}
		return false;
	}

	boolean highlight(CharIter in, List<Block> out) {
		in.moveNext(start.length()); // skip start
		String s = "";
		while (!in.finished() && !Character.isWhitespace(in.current())) {
			s += in.current();
			in.moveNext();
		}
		if (s.length() == 0) {
			return false;
		}
		int i = in.indexOf(s);
		if (i < 0) {
			in.moveToEnd();
		} else {
			in.moveNext(i + s.length());
		}
		out.add(in.markedToStyledBlock("string"));
		return true;		
	}

}
