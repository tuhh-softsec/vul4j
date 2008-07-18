/*
 * xslthl - XSLT Syntax Highlighting
 * https://sourceforge.net/projects/xslthl/
 * Copyright (C) 2005-2008 Michal Molhanec, Jirka Kosek, Michiel Hendriks
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 * 
 * Michal Molhanec <mol1111 at users.sourceforge.net>
 * Jirka Kosek <kosek at users.sourceforge.net>
 * Michiel Hendriks <elmuerte at users.sourceforge.net>
 */
package net.sf.xslthl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.icl.saxon.Context;
import com.icl.saxon.expr.XPathException;
import com.icl.saxon.om.Axis;
import com.icl.saxon.om.AxisEnumeration;
import com.icl.saxon.om.Builder;
import com.icl.saxon.om.DocumentInfo;
import com.icl.saxon.om.NamePool;
import com.icl.saxon.om.NodeEnumeration;
import com.icl.saxon.om.NodeInfo;
import com.icl.saxon.pattern.AnyNodeTest;
import com.icl.saxon.tree.AttributeCollection;

/**
 * Connector for Saxon 6. For newer saxon versions the ConnectorSaxonB should be
 * used.
 */
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
     * Highlight the nodes using the standard configuration file
     * 
     * @param context
     * @param hlCode
     * @param nodes
     * @return
     * @throws Exception
     */
    public static NodeEnumeration highlight(Context context, String hlCode,
	    NodeEnumeration nodes) throws Exception {
	return highlight(context, hlCode, nodes, null);
    }

    /**
     * highlight the nodes using a specific interface
     * 
     * @param context
     * @param hlCode
     * @param nodes
     * @param configFilename
     * @return
     * @throws Exception
     */
    public static NodeEnumeration highlight(Context context, String hlCode,
	    NodeEnumeration nodes, String configFilename) throws Exception {
	try {
	    Config c = Config.getInstance(configFilename);
	    MainHighlighter hl = c.getMainHighlighter(hlCode);

	    NamePool pool = context.getController().getNamePool();

	    List<NodeInfo> resultNodes = new ArrayList<NodeInfo>();

	    while (nodes.hasMoreElements()) {
		NodeInfo ni = nodes.nextElement();
		AxisEnumeration ae = ni.getEnumeration(Axis.CHILD, AnyNodeTest
			.getInstance());
		while (ae.hasMoreElements()) {
		    NodeInfo n2i = ae.nextElement();
		    if (n2i.getNodeType() == NodeInfo.TEXT) {
			if (hl != null) {
			    Builder builder = context.getController()
				    .makeBuilder();
			    builder.startDocument();
			    List<Block> l = hl.highlight(n2i.getStringValue());
			    for (Block b : l) {
				blockToSaxon6Node(b, builder, pool, c);
			    }
			    builder.endDocument();
			    DocumentInfo doc = builder.getCurrentDocument();
			    NodeEnumeration elms = doc.getEnumeration(
				    Axis.CHILD, AnyNodeTest.getInstance());
			    while (elms.hasMoreElements()) {
				resultNodes.add(elms.nextElement());
			    }
			} else {
			    resultNodes.add(n2i);
			}
		    } else {
			// deepCopy(builder, pool, n2i);
			resultNodes.add(n2i);
		    }
		}
	    }
	    return new NodeEnumerationIterator(resultNodes.iterator());
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    static class NodeEnumerationIterator implements NodeEnumeration {

	protected Iterator<NodeInfo> it;

	NodeEnumerationIterator(Iterator<NodeInfo> useit) {
	    it = useit;
	}

	public boolean hasMoreElements() {
	    return it.hasNext();
	}

	public boolean isPeer() {
	    return true;
	}

	public boolean isReverseSorted() {
	    return false;
	}

	public boolean isSorted() {
	    return true;
	}

	public NodeInfo nextElement() throws XPathException {
	    return it.next();
	}

    }

}
