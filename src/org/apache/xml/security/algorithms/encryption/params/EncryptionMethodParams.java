package org.apache.xml.security.algorithms.encryption.params;

import org.w3c.dom.*;
import org.apache.xml.security.exceptions.XMLSecurityException;

/**
 *
 * @author $Author$
 */
public abstract class EncryptionMethodParams {

  public abstract DocumentFragment createChildNodes(Document doc) throws XMLSecurityException;

  public abstract String getAlgorithmURI();
}