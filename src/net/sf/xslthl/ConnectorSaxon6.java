package net.sf.xslthl;

import java.util.List;

import com.icl.saxon.Context;
import com.icl.saxon.om.Axis;
import com.icl.saxon.om.AxisEnumeration;
import com.icl.saxon.om.Builder;
import com.icl.saxon.om.DocumentInfo;
import com.icl.saxon.om.NamePool;
import com.icl.saxon.om.NodeEnumeration;
import com.icl.saxon.om.NodeInfo;
import com.icl.saxon.pattern.AnyNodeTest;
import com.icl.saxon.tree.AttributeCollection;

public class ConnectorSaxon6 {

    private static void blockToSaxon6Node(Block b, Builder builder,
	    NamePool pool, Config config) throws Exception {
	if (b.isStyled()) {
	    AttributeCollection emptyAtts = new AttributeCollection(pool);
	    int elemId = pool.allocate(config.prefix, config.uri,
		    ((StyledBlock) b).getStyle());
	    builder.startElement(elemId, emptyAtts, new int[0], 0);
	    builder.characters(b.getText().toCharArray(), 0, b.getText()
		    .length());
	    builder.endElement(elemId);
	} else {
	    builder.characters(b.getText().toCharArray(), 0, b.getText()
		    .length());
	}
    }

    /**
     * Performs a deep copy of a node. This is needed to include non highlighted
     * tags in the return of the highlight function
     * 
     * @param builder
     * @param pool
     * @param node
     * @throws Exception
     */
    protected static void deepCopy(Builder builder, NamePool pool, NodeInfo node)
	    throws Exception {
	if (node.getNodeType() == NodeInfo.ELEMENT) {
	    AttributeCollection attrs = new AttributeCollection(pool);
	    NodeEnumeration ne = node.getEnumeration(Axis.ATTRIBUTE,
		    AnyNodeTest.getInstance());
	    while (ne.hasMoreElements()) {
		NodeInfo attr = ne.nextElement();
		attrs.addAttribute(attr.getPrefix(), attr.getURI(), attr
			.getLocalName(), "", attr.getStringValue());
	    }
	    int elm = pool.allocate(node.getPrefix(), node.getURI(), node
		    .getLocalName());
	    builder.startElement(elm, attrs, new int[0], 0);
	    ne = node.getEnumeration(Axis.CHILD, AnyNodeTest.getInstance());
	    while (ne.hasMoreElements()) {
		deepCopy(builder, pool, ne.nextElement());
	    }
	    builder.endElement(elm);
	} else if (node.getNodeType() == NodeInfo.TEXT) {
	    String s = node.getStringValue();
	    builder.characters(s.toCharArray(), 0, s.length());
	} else if (node.getNodeType() == NodeInfo.COMMENT) {
	    String s = node.getStringValue();
	    builder.comment(s.toCharArray(), 0, s.length());
	} else if (node.getNodeType() == NodeInfo.PI) {
	    builder.processingInstruction(node.getLocalName(), node
		    .getStringValue());
	} else {
	    System.err.println("Unknown node type in deepCopy: "
		    + node.getNodeType());
	}
    }

    public static NodeEnumeration highlight(Context context, String hlCode,
	    NodeEnumeration nodes) throws Exception {
	try {
	    Config c = Config.getInstance();
	    MainHighlighter hl = c.getMainHighlighter(hlCode);

	    Builder builder = context.getController().makeBuilder();
	    NamePool pool = context.getController().getNamePool();
	    builder.startDocument();

	    while (nodes.hasMoreElements()) {
		NodeInfo ni = nodes.nextElement();
		AxisEnumeration ae = ni.getEnumeration(Axis.CHILD, AnyNodeTest
			.getInstance());
		while (ae.hasMoreElements()) {
		    NodeInfo n2i = ae.nextElement();
		    if (n2i.getNodeType() == NodeInfo.TEXT) {
			if (hl != null) {
			    List<Block> l = hl.highlight(n2i.getStringValue());
			    for (Block b : l) {
				blockToSaxon6Node(b, builder, pool, c);
			    }
			} else {
			    String s = n2i.getStringValue();
			    builder.characters(s.toCharArray(), 0, s.length());
			}
		    } else {
			deepCopy(builder, pool, n2i);
		    }
		}
	    }
	    builder.endDocument();
	    DocumentInfo doc = builder.getCurrentDocument();
	    return doc.getEnumeration(Axis.CHILD, AnyNodeTest.getInstance());
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

}
