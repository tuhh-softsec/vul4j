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
import java.util.List;

import net.sf.saxon.event.Builder;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Axis;
import net.sf.saxon.om.AxisIterator;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.ListIterator;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.pattern.AnyNodeTest;
import net.sf.saxon.type.Type;

/**
 * A new saxon connector to be used with saxon 8.5 and later.
 */
public class ConnectorSaxonB {

	private static void blockToSaxon6Node(Block b, Builder builder,
	        NamePool pool, Config config) throws Exception {
		if (b.isStyled()) {
			int elemId = pool.allocate(config.prefix, config.uri,
			        ((StyledBlock) b).getStyle());
			builder.startElement(elemId, -1, 0, 0);
			builder.characters(b.getText(), 0, b.getText().length());
			builder.endElement();
		} else {
			builder.characters(b.getText(), 0, b.getText().length());
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
	public static SequenceIterator highlight(XPathContext context,
	        String hlCode, SequenceIterator nodes) throws Exception {
		return highlight(context, hlCode, nodes, null);
	}

	/**
	 * highlight the nodes using a specific interface
	 * 
	 * @param context
	 * @param hlCode
	 * @param seq
	 * @param configFilename
	 * @return
	 * @throws Exception
	 */
	public static SequenceIterator highlight(XPathContext context,
	        String hlCode, SequenceIterator seq, String configFilename)
	        throws Exception {
		try {
			Config c = Config.getInstance(configFilename);
			MainHighlighter hl = c.getMainHighlighter(hlCode);

			NamePool pool = context.getController().getNamePool();

			List<Item> resultNodes = new ArrayList<Item>();
			while (seq.next() != null) {
				Item itm = seq.current();
				if (itm instanceof NodeInfo) {
					NodeInfo ni = (NodeInfo) itm;
					SequenceIterator ae = ni.iterateAxis(Axis.CHILD,
					        AnyNodeTest.getInstance());
					while (ae.next() != null) {
						Item itm2 = ae.current();
						if (itm2 instanceof NodeInfo) {
							NodeInfo n2i = (NodeInfo) itm2;
							if (n2i.getNodeKind() == Type.TEXT) {
								if (hl != null) {
									Builder builder = context.getController()
									        .makeBuilder();
									builder.open();
									builder.startDocument(0);
									List<Block> l = hl.highlight(n2i
									        .getStringValue());
									for (Block b : l) {
										blockToSaxon6Node(b, builder, pool, c);
									}
									builder.endDocument();
									builder.close();
									NodeInfo doc = builder.getCurrentRoot();
									AxisIterator elms = doc.iterateAxis(
									        Axis.CHILD, AnyNodeTest
									                .getInstance());
									while (elms.next() != null) {
										resultNodes.add(elms.current());
									}
								} else {
									resultNodes.add(n2i);
								}
							} else {
								resultNodes.add(n2i);
							}
						} else {
							resultNodes.add(itm2);
						}
					}
				} else {
					resultNodes.add(itm);
				}
			}
			return new ListIterator(resultNodes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
