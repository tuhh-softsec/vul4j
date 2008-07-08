package net.sf.xslthl;

import java.util.List;

class MultilineCommentHighlighter extends Highlighter {

    protected String start, end;

    MultilineCommentHighlighter(Params params) {
	start = params.getParam("start");
	end = params.getParam("end");
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
	int endIndex = in.indexOf(end);
	if (endIndex == -1) {
	    in.moveToEnd();
	} else {
	    in.moveNext(endIndex + end.length());
	}
	out.add(in.markedToStyledBlock("comment"));
	return true;
    }

}
