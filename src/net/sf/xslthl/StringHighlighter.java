package net.sf.xslthl;

import java.util.List;

class StringHighlighter extends Highlighter {

    private String start, escape;
    private boolean doubleEscapes;

    StringHighlighter(Params params) {
	start = params.getParam("string");
	escape = params.getParam("escape", null);
	doubleEscapes = params.isSet("doubleEscapes");
    }

    @Override
    boolean startsWith(CharIter in) {
	if (in.startsWith(start)) {
	    return true;
	}
	return false;
    }

    @Override
    boolean highlight(CharIter in, List<Block> out) {
	in.moveNext(start.length()); // skip start
	boolean wasEscape = false;
	while (!in.finished()) {
	    if (in.startsWith(start) && !wasEscape) {
		if (doubleEscapes && in.startsWith(start, start.length())) {
		    in.moveNext();
		} else {
		    break;
		}
	    } else if (escape != null && in.startsWith(escape) && !wasEscape) {
		wasEscape = true;
	    } else {
		wasEscape = false;
	    }
	    in.moveNext();
	}
	if (!in.finished()) {
	    in.moveNext();
	}
	out.add(in.markedToStyledBlock("string"));
	return true;
    }

}
