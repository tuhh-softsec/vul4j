package org.apache.xml.security.c14n.implementations;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.c14n.implementations.UtfHelpper;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transforms;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class UtfHelperTest extends TestCase {
	public void testBug40156() {
		String s="הצü";
		byte a[]=UtfHelpper.getStringInUtf8(s);
		try {
			byte correct[]=s.getBytes("UTF8");
			boolean equals=Arrays.equals(correct, a);
			assertTrue(equals);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static Test suite() {
		return new TestSuite(UtfHelperTest.class);
	}
}
