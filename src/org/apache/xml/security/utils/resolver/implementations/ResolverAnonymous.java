package org.apache.xml.security.utils.resolver.implementations;

import java.net.*;
import java.io.*;
import org.w3c.dom.*;
import org.apache.xml.utils.URI;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.security.utils.Base64;

/**
 *
 * @author $Author$
 */
public class ResolverAnonymous extends ResourceResolverSpi {
   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(ResolverAnonymous.class.getName());

   private XMLSignatureInput _input = null;

   public ResolverAnonymous(String filename) throws FileNotFoundException {
      this._input = new XMLSignatureInput(new FileInputStream(filename));
   }

   public ResolverAnonymous(InputStream is) {
      this._input = new XMLSignatureInput(is);
   }

   public XMLSignatureInput engineResolve(Attr uri, String BaseURI) {
      return this._input;
   }

   /**
    * We resolve anonymous (unspecified) URIs
    *
    * @param uri
    * @param BaseURI
    * @return
    */
   public boolean engineCanResolve(Attr uri, String BaseURI) {
      if (uri == null) {
         return true;
      }
      return false;
   }

   /**
    * Method engineGetPropertyKeys
    *
    * @return
    */
   public String[] engineGetPropertyKeys() {
      return new String[0];
   }
}