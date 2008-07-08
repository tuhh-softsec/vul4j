package net.sf.xslthl;

import java.io.*;
import java.util.*;

// Saxon 6 specific imports
import com.icl.saxon.*;
import com.icl.saxon.expr.*;
import com.icl.saxon.om.*;
import com.icl.saxon.pattern.*;
import com.icl.saxon.tree.*;

public class ConnectorSaxon6 {

	private static void blockToSaxon6Node(Block b, Builder builder, NamePool pool, Config config) throws Exception {
		if (b.isStyled()) {
			AttributeCollection emptyAtts = new AttributeCollection(pool);
			int elemId = pool.allocate(config.prefix, config.uri, ((StyledBlock)b).getStyle());
			builder.startElement(elemId, emptyAtts, new int[0], 0);
			builder.characters(b.getText().toCharArray(), 0, b.getText().length());
			builder.endElement(elemId);
		} else {
			builder.characters(b.getText().toCharArray(), 0, b.getText().length());
		}
	}

	protected static NodeEnumeration highlight(Context c, String source, MainHighlighter hl, Config config) throws Exception {
		Builder builder = c.getController().makeBuilder();
		NamePool pool = c.getController().getNamePool();
		builder.startDocument();

		List<Block> l = hl.highlight(source);
		for (Block b : l) {
			blockToSaxon6Node(b, builder, pool, config);
		}

		builder.endDocument();
		DocumentInfo doc = builder.getCurrentDocument();
		return doc.getEnumeration(Axis.CHILD, AnyNodeTest.getInstance());
	}

	public static NodeEnumeration highlight(Context context, String hlCode, String source) throws Exception {
		try {
			Config c = Config.getInstance();
			MainHighlighter hl = c.getMainHighlighter(hlCode);
			return highlight(context, source, hl, c);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
