package org.apache.xml.security.encryption.type;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.algorithms.encryption.EncryptionMethod;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.encryption.CipherData;
import org.apache.xml.security.encryption.EncryptionProperties;

/**
 * This interface describes the methods available for element types
 * which extend the abstract <CODE>xenc:EncryptedType</CODE>.
 *
 * @author $Author$
 */
public interface EncryptedType {
   public EncryptionMethod getEncryptionMethod() throws XMLSecurityException;
   public KeyInfo getKeyInfo() throws XMLSecurityException;
   public CipherData getCipherData() throws XMLSecurityException;
   public EncryptionProperties getEncryptionProperties() throws XMLSecurityException;

   public String getId();

   public String getType();
   public boolean getTypeIsElement();
   public boolean getTypeIsContent();
   public boolean getTypeIsMediaType();
   public String getMediaTypeOfType();
}
