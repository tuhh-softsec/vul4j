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
	public void testUtf() {
		int it=1024*1024; int chunk=Integer.MAX_VALUE/it;
		for (int j=0;j<it;j++) {
		char chs[]=new char[chunk];
		for (int i=0;i<chunk; i++) {
			chs[i]=(char)((chunk*j)+i);
		}
		String str=new String(chs);
		byte a[]=UtfHelpper.getStringInUtf8(str);
		try {
			byte correct[]=str.getBytes("UTF8");
			boolean equals=Arrays.equals(correct, a);
			if (!equals) {
				for (int i=0;i<chunk; i++) {
					char old[]={(char)((chunk*j)+i)};
					String strChar=new String(old);
					a=UtfHelpper.getStringInUtf8(strChar);
					correct=strChar.getBytes("UTF8");
					if (!Arrays.equals(correct, a)) {
						assertEquals("Error in character :"+(int)old[0],strChar,new String(a));
					}
					
				}
					
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	public static Test suite() {
		return new TestSuite(UtfHelperTest.class);
	}
}
