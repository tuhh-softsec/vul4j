package org.apache.xml.security.test.c14n.implementations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.CanonicalizerSpi;
import org.apache.xml.security.utils.UnsyncByteArrayOutputStream;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

@SuppressWarnings("unchecked")
public class MockCanonicalizationMethod extends CanonicalizerSpi {

	public static final String MOCK_CANONICALIZATION_METHOD = "mock.canonicalization.method";
	private OutputStream _os;

	@Override
	public byte[] engineCanonicalizeSubTree(Node rootNode)
			throws CanonicalizationException {
		return canonicalize(rootNode);
	}

	private byte[] canonicalize(Node rootNode) throws CanonicalizationException {
		try {
			XMLSerializer serializer = new XMLSerializer(getOutputStream(),
					new OutputFormat());
			serializer.setNamespaces(true);
			serializer.serialize((Element) rootNode);
			return toByteArray();
		} catch (IOException e) {
			throw new CanonicalizationException(e.getMessage(), e);
		}
	}

	private byte[] toByteArray() {
		OutputStream os = getOutputStream();
		if (os instanceof ByteArrayOutputStream) {
			ByteArrayOutputStream os2 = (ByteArrayOutputStream) os;
			byte[] result = os2.toByteArray();
			os2.reset();
			return result;
		}
		if (os instanceof UnsyncByteArrayOutputStream) {
			UnsyncByteArrayOutputStream os2 = (UnsyncByteArrayOutputStream) os;
			byte[] result = os2.toByteArray();
			os2.reset();
			return result;
		}
		return null;
	}

	private OutputStream getOutputStream() {
		if (_os == null) {
			_os = new ByteArrayOutputStream();
		}
		return _os;
	}

	@Override
	public byte[] engineCanonicalizeSubTree(Node rootNode,
			String inclusiveNamespaces) throws CanonicalizationException {
		return canonicalize(rootNode);
	}

	@Override
	public byte[] engineCanonicalizeXPathNodeSet(Set xpathNodeSet)
			throws CanonicalizationException {
		return canonicalize(XMLUtils.getOwnerDocument(xpathNodeSet));
	}

	@Override
	public byte[] engineCanonicalizeXPathNodeSet(Set xpathNodeSet,
			String inclusiveNamespaces) throws CanonicalizationException {
		return canonicalize(XMLUtils.getOwnerDocument(xpathNodeSet));
	}

	@Override
	public boolean engineGetIncludeComments() {
		return false;
	}

	@Override
	public String engineGetURI() {
		return MOCK_CANONICALIZATION_METHOD;
	}

	@Override
	public void setWriter(OutputStream os) {
		_os = os;
	}
}
