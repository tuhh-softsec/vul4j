package net.sf.xslthl;

import java.util.*;

class NestedMultilineCommentHighlighter extends MultilineCommentHighlighter {

   NestedMultilineCommentHighlighter(Params params) {
      super(params);
   }

   boolean highlight(CharIter in, List<Block> out) {
      in.moveNext(start.length()); // skip start

      int depth = 1;
      while (!in.finished()) {
         if (in.startsWith(end)) {
            in.moveNext(end.length());
            if (depth == 1) {
               break;
            } else {
               depth--;
            }
         } else if (in.startsWith(start)) {
            depth++;
            in.moveNext(start.length());
         } else {
            in.moveNext();
         }
      }

      out.add(in.markedToStyledBlock("comment"));
      return true;
   }

}
