/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European
 * Commission in the <WebSig> project in the ISIS Programme.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
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
 * A simple ResourceResolver for HTTP requests. This class handles only 'pure'
 * HTTP URIs which means without a fragment. The Fragment handling is done by the
 * {@link ResolverFragment} class.
 * <BR>
 * If the user has a corporate HTTP proxy which is to be used, the usage can be
 * switched on by setting properties for the resolver:
 * <PRE>
 * resourceResolver.setProperty("http.proxy.host", "proxy.company.com");
 * resourceResolver.setProperty("http.proxy.port", "8080");
 *
 * // if we need a password for the proxy
 * resourceResolver.setProperty("http.proxy.username", "proxyuser3");
 * resourceResolver.setProperty("http.proxy.password", "secretca");
 * </PRE>
 *
 *
 * @author $Author$
 * @see <A HREF="http://www.javaworld.com/javaworld/javatips/jw-javatip42_p.html">Java Tip 42: Write Java apps that work with proxy-based firewalls</A>
 * @see <A HREF="http://java.sun.com/j2se/1.4/docs/guide/net/properties.html">SUN J2SE docs for network properties</A>
 * @see <A HREF="http://metalab.unc.edu/javafaq/javafaq.html#proxy">The JAVA FAQ Question 9.5: How do I make Java work with a proxy server?</A>
 * $todo$ the proxy behaviour seems not to work; if a on-existing proxy is set, it works ?!?
 */
public class ResolverDirectHTTP extends ResourceResolverSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(ResolverDirectHTTP.class.getName());

   /** Field properties[] */
   static final String properties[] = { "http.proxy.host",
                                        "http.proxy.port",
                                        "http.proxy.username",
                                        "http.proxy.password",
                                        "http.basic.username",
                                        "http.basic.password" };

   /** Field HttpProxyHost */
   private static final int HttpProxyHost = 0;

   /** Field HttpProxyPort */
   private static final int HttpProxyPort = 1;

   /** Field HttpProxyUser */
   private static final int HttpProxyUser = 2;

   /** Field HttpProxyPass */
   private static final int HttpProxyPass = 3;

   /** Field HttpProxyUser */
   private static final int HttpBasicUser = 4;

   /** Field HttpProxyPass */
   private static final int HttpBasicPass = 5;

   /**
    * Method resolve
    *
    * @param uri
    * @param BaseURI
    *
    * @throws ResourceResolverException
    * $todo$ calculate the correct URI from the attribute and the BaseURI
    */
   public XMLSignatureInput engineResolve(Attr uri, String BaseURI)
           throws ResourceResolverException {

      try {
         boolean useProxy = false;
         String proxyHost =
            engineGetProperty(ResolverDirectHTTP
               .properties[ResolverDirectHTTP.HttpProxyHost]);
         String proxyPort =
            engineGetProperty(ResolverDirectHTTP
               .properties[ResolverDirectHTTP.HttpProxyPort]);

         if ((proxyHost != null) && (proxyPort != null)) {
            useProxy = true;
         }

         String oldProxySet =
            (String) System.getProperties().get("http.proxySet");
         String oldProxyHost =
            (String) System.getProperties().get("http.proxyHost");
         String oldProxyPort =
            (String) System.getProperties().get("http.proxyPort");
         boolean switchBackProxy = ((oldProxySet != null)
                                    && (oldProxyHost != null)
                                    && (oldProxyPort != null));

         // switch on proxy usage
         if (useProxy) {
            cat.debug("Use of HTTP proxy enabled: " + proxyHost + ":"
                      + proxyPort);
            System.getProperties().put("http.proxySet", "true");
            System.getProperties().put("http.proxyHost", proxyHost);
            System.getProperties().put("http.proxyPort", proxyPort);
         }

         // make network request
         URI uriNew = getNewURI(uri.getNodeValue(), BaseURI);

         // if the URI contains a fragment, ignore it
         URI uriNewNoFrag = new URI(uriNew);

         uriNewNoFrag.setFragment(null);

         URL url = new URL(uriNewNoFrag.toString());
         URLConnection urlConnection = url.openConnection();

         {

            // set proxy pass
            String proxyUser =
               engineGetProperty(ResolverDirectHTTP
                  .properties[ResolverDirectHTTP.HttpProxyUser]);
            String proxyPass =
               engineGetProperty(ResolverDirectHTTP
                  .properties[ResolverDirectHTTP.HttpProxyPass]);

            if ((proxyUser != null) && (proxyPass != null)) {
               String password = proxyUser + ":" + proxyPass;
               String encodedPassword = Base64.encode(password.getBytes());

               // or was it Proxy-Authenticate ?
               urlConnection.setRequestProperty("Proxy-Authorization",
                                                encodedPassword);
            }
         }

         {
            // check if Basic authentication is required
            String auth = urlConnection.getHeaderField("WWW-Authenticate");

            if (auth != null) {

               // do http basic authentication
               if (auth.startsWith("Basic")) {
                  String user =
                     engineGetProperty(ResolverDirectHTTP
                        .properties[ResolverDirectHTTP.HttpBasicUser]);
                  String pass =
                     engineGetProperty(ResolverDirectHTTP
                        .properties[ResolverDirectHTTP.HttpBasicPass]);

                  if ((user != null) && (pass != null)) {
                     urlConnection = url.openConnection();

                     String password = user + ":" + pass;
                     String encodedPassword =
                        Base64.encode(password.getBytes());

                     // set authentication property in the http header
                     urlConnection.setRequestProperty("Authorization",
                                                      "Basic "
                                                      + encodedPassword);
                  }
               }
            }
         }

         String mimeType = urlConnection.getHeaderField("Content-Type");
         String contentLength = urlConnection.getHeaderField("Content-Length");
         InputStream inputStream = urlConnection.getInputStream();
         BufferedInputStream bufIn = new BufferedInputStream(inputStream);
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte buf[] = new byte[4096];
         int read = 0;
         int summarized = 0;

         while ((read = inputStream.read(buf)) >= 0) {
            baos.write(buf, 0, read);

            summarized += read;
         }

         cat.debug("Fetched " + summarized + " bytes from URI "
                   + uriNew.toString());

         XMLSignatureInput result = new XMLSignatureInput(baos.toByteArray());

         // XMLSignatureInput result = new XMLSignatureInput(inputStream);
         result.setSourceURI(uriNew.toString());
         result.setMIMEType(mimeType);

         // switch off proxy usage
         if (switchBackProxy) {
            System.getProperties().put("http.proxySet", oldProxySet);
            System.getProperties().put("http.proxyHost", oldProxyHost);
            System.getProperties().put("http.proxyPort", oldProxyPort);
         }

         return result;
      } catch (MalformedURLException ex) {
         throw new ResourceResolverException("generic.EmptyMessage", ex, uri,
                                             BaseURI);
      } catch (IOException ex) {
         throw new ResourceResolverException("generic.EmptyMessage", ex, uri,
                                             BaseURI);
      }
   }

   /**
    * We resolve http URIs <I>without</I> fragment...
    *
    * @param uri
    * @param BaseURI
    *
    */
   public boolean engineCanResolve(Attr uri, String BaseURI) {

      if (uri == null) {
         cat.debug("quick fail, uri == null");
         return false;
      }

      String uriNodeValue = uri.getNodeValue();

      if (uriNodeValue.equals("") || uriNodeValue.startsWith("#")) {
         cat.debug("quick fail for empty URIs and local ones");
         return false;
      }

      URI uriNew = getNewURI(uri.getNodeValue(), BaseURI);
      if (uriNew != null && uriNew.getScheme().equals("http")) {
        cat.debug("I state that I can resolve " + uriNew.toString());
        return true;
      }
      
      cat.debug("I state that I can't resolve " + uriNew.toString());
      return false;
   }

   /**
    * Method engineGetPropertyKeys
    *
    *
    */
   public String[] engineGetPropertyKeys() {
      return ResolverDirectHTTP.properties;
   }
   
   private URI getNewURI(String uri, String BaseURI)
   {
      URI uriNew;
      try {
        
        if(BaseURI == null || "".equals(BaseURI) ){
            uriNew = new URI(uri );
        }
        else {
            uriNew = new URI(new URI(BaseURI), uri);
        }
  
      } catch (URI.MalformedURIException ex) {
        cat.debug("MalformedURIException: ", ex);
        return null;
      }
      return uriNew;
  }
}
