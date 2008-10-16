package org.apache.xml.security.test.c14n.implementations;

import java.io.OutputStream;
import java.util.Set;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.CanonicalizerSpi;
import org.apache.xml.security.c14n.implementations.Canonicalizer11_OmitComments;

import org.w3c.dom.Node;

@SuppressWarnings("unchecked")
public class MockCanonicalizationMethod extends CanonicalizerSpi {

	public static final String MOCK_CANONICALIZATION_METHOD = "mock.canonicalization.method";
	private Canonicalizer11_OmitComments _impl;

	public MockCanonicalizationMethod() {
		_impl = new Canonicalizer11_OmitComments();
	}

	@Override
	public byte[] engineCanonicalizeSubTree(Node rootNode)
			throws CanonicalizationException {
		return _impl.engineCanonicalizeSubTree(rootNode);
	}

	@Override
	public byte[] engineCanonicalizeSubTree(Node rootNode,
			String inclusiveNamespaces) throws CanonicalizationException {
		return _impl.engineCanonicalizeSubTree(rootNode, inclusiveNamespaces);
	}

	@Override
	public byte[] engineCanonicalizeXPathNodeSet(Set xpathNodeSet)
			throws CanonicalizationException {
		return _impl.engineCanonicalizeXPathNodeSet(xpathNodeSet);
	}

	@Override
	public byte[] engineCanonicalizeXPathNodeSet(Set xpathNodeSet,
			String inclusiveNamespaces) throws CanonicalizationException {
		return _impl.engineCanonicalizeXPathNodeSet(xpathNodeSet,
				inclusiveNamespaces);
	}

	@Override
	public boolean engineGetIncludeComments() {
		return _impl.engineGetIncludeComments();
	}

	@Override
	public String engineGetURI() {
		return MOCK_CANONICALIZATION_METHOD;
	}

	@Override
	public void setWriter(OutputStream os) {
		_impl.setWriter(os);
	}

}
