/*
 * Created on Jan 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.xml.security.c14n.implementations;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;


import junit.framework.TestCase;

/**
 * @author raul
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NameSpaceSymbTableTest extends TestCase {
    
	static Attr node1,node2;
    static {
    	try {
            Document doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			node1=doc.createAttributeNS("a","b");
            node2=doc.createAttributeNS("b","c");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
	public void testNullFirstXmlns() {
		NameSpaceSymbTable ns=new NameSpaceSymbTable();
        assertNull(ns.getMapping("xmlns"));
    }
    public void testXmlnsPut() {
        NameSpaceSymbTable ns=new NameSpaceSymbTable();
        ns.push();
        ns.addMapping("xmlns","http://a",node1);
        assertEquals(node1,ns.getMapping("xmlns"));
    }
    public void testXmlnsMap() {
        NameSpaceSymbTable ns=new NameSpaceSymbTable();
        ns.push();
        ns.addMapping("xmlns","http://a",node1);
        assertEquals(node1,ns.getMapping("xmlns"));
        ns.pop();
        assertEquals(null,ns.getMapping("xmlns"));        
    }
    public void testXmlnsMap2() {
        NameSpaceSymbTable ns=new NameSpaceSymbTable();
        ns.push();
        ns.push();
        ns.addMapping("xmlns","http://a",node1);        
        ns.pop();
        ns.pop();
        assertEquals(null,ns.getMapping("xmlns"));        
    }
    public void testXmlnsPrefix() {
        NameSpaceSymbTable ns=new NameSpaceSymbTable();
        ns.push();
        ns.addMapping("xmlns","http://a",node1);
        assertEquals(node1,ns.getMapping("xmlns"));
        ns.push();
        ns.addMapping("xmlns","http://a",node1);
        assertEquals(null,ns.getMapping("xmlns"));     
        ns.push();
        ns.addMapping("xmlns","http://b",node1);
        assertEquals(node1,ns.getMapping("xmlns"));
    }
    public void testXmlnsRemovePrefix() {
        NameSpaceSymbTable ns=new NameSpaceSymbTable();
        ns.push();
        ns.push();
        ns.addMapping("xmlns","http://a",node1);
        assertEquals(node1,ns.getMapping("xmlns"));
        ns.pop();        
        assertNull(ns.getMapping("xmlns"));             
    }
    public void testPrefix() {
    	NameSpaceSymbTable ns=new NameSpaceSymbTable();
        ns.push();
        ns.addMapping("a","http://a",node1);
        assertEquals(node1,ns.getMapping("a"));
        ns.push();
        assertNull(ns.getMapping("a"));
        ns.push();
        ns.addMapping("a","http://c",node1);
        assertEquals(node1,ns.getMapping("a"));
        ns.pop();
        ns.push();
        assertNull(ns.getMapping("a"));
        ns.addMapping("a","http://c",node1);
        assertEquals(node1,ns.getMapping("a"));
    }
    public void testSeveralPrefixes() {
    	NameSpaceSymbTable ns=new NameSpaceSymbTable();
        ns.push();
        ns.addMapping("a","http://a",node1);
        ns.addMapping("b","http://b",node2);
        assertEquals(node1,ns.getMapping("a"));
        assertEquals(node2,ns.getMapping("b"));
        ns.push();
        assertNull(ns.getMapping("a"));
     }
    public void testSeveralPrefixes2() {
        NameSpaceSymbTable ns=new NameSpaceSymbTable();
        ns.push();
        ns.addMapping("a","http://a",node1);
        ns.push();        
        assertEquals(node1,ns.getMapping("a"));
        ns.pop();
        assertEquals(node1,ns.getMapping("a"));
        
     }
    public void notPasstestUnrederedNodes() {
    	NameSpaceSymbTable ns=new NameSpaceSymbTable();
        ns.push();
        List l=new ArrayList();
        ns.getUnrenderedNodes(l);
        assertTrue(l.isEmpty());
        l.clear();
        ns.push();
        ns.addMapping("xmlns","http://a",node1);
        ns.push();
        
        ns.getUnrenderedNodes(l);
        assertTrue(l.contains(node1));
        ns.push();
        l.clear();
        ns.getUnrenderedNodes(l);
        assertFalse(l.contains(node1));
        ns.pop();
        ns.pop();
        l.clear();
        ns.getUnrenderedNodes(l);
        assertTrue(l.contains(node1));
    }
}
