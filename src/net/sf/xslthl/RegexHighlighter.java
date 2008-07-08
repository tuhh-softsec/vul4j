package net.sf.xslthl;

import java.util.List;
import java.util.regex.Pattern;

class RegexHighlighter extends WholeHighlighter {

    Pattern pattern;
    String style;

    RegexHighlighter(Params params) {
	super(params);
	pattern = Pattern.compile(params.getParam("pattern"));
	style = params.getParam("style");
    }

    @Override
    boolean highlight(CharIter in, List<Block> out) {
	in.createMatcher(pattern);
	while (in.find()) {
	    if (in.isMarked()) {
		out.add(in.markedToBlock());
	    }
	    in.markMatched();
	    out.add(in.markedToStyledBlock(style));
	}
	if (in.isMarked()) {
	    out.add(in.markedToBlock());
	}
	return false;
    }
}
