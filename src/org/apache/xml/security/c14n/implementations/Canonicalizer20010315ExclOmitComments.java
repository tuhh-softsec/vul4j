package org.apache.xml.security.c14n.implementations;

import org.apache.xml.security.c14n.Canonicalizer;


public class Canonicalizer20010315ExclOmitComments
        extends Canonicalizer20010315Excl {

   public Canonicalizer20010315ExclOmitComments() {
      super(false);
   }

   /**
    * Method engineGetURI
    *
    * @return
    */
   public final String engineGetURI() {
      return Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS;
   }

   /**
    * Method engineGetIncludeComments
    *
    * @return
    */
   public final boolean engineGetIncludeComments() {
      return false;
   }
}
