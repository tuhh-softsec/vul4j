package net.sf.xslthl;

import java.util.LinkedList;
import java.util.List;

class MainHighlighter {

    List<Highlighter> highlighters = new LinkedList<Highlighter>();
    List<WholeHighlighter> wholehighlighters = new LinkedList<WholeHighlighter>();

    void add(Highlighter h) {
	highlighters.add(h);
    }

    void addWhole(WholeHighlighter h) {
	wholehighlighters.add(h);
    }

    List<Block> highlight(String source) {
	CharIter in = new CharIter(source);
	List<Block> out = new LinkedList<Block>();

	if (highlighters.size() > 0) {
	    while (!in.finished()) {
		boolean found = false;
		for (Highlighter h : highlighters) {
		    if (h.startsWith(in)) {
			int oldMark = -2;
			Block preBlock = null;
			if (in.isMarked()) {
			    oldMark = in.getMark();
			    preBlock = in.markedToBlock();
			    out.add(preBlock);
			}
			found = h.highlight(in, out);
			if (found) {
			    break;
			} else {
			    // undo last action when it was a false positive
			    if (preBlock != null) {
				out.remove(preBlock);
			    }
			    if (oldMark != -2) {
				in.setMark(oldMark);
			    }
			}
		    }
		}
		if (!found) {
		    in.moveNext();
		}
	    }
	} else {
	    in.moveToEnd();
	}

	if (in.isMarked()) {
	    out.add(in.markedToBlock());
	}

	if (wholehighlighters.size() > 0) {
	    for (WholeHighlighter h : wholehighlighters) {
		List<Block> oldout = out;
		out = new LinkedList<Block>();
		for (Block b : oldout) {
		    if (b.isStyled()
			    && (h.appliesOnAllStyles() || h
				    .appliesOnStyle(((StyledBlock) b)
					    .getStyle())) || !b.isStyled()
			    && h.appliesOnEmptyStyle()) {
			h.highlight(new CharIter(b.getText()), out);
		    } else {
			out.add(b);
		    }
		}
	    }
	}

	return out;
    }

}
