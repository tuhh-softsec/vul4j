package org.apache.xml.security.encryption;

import java.io.IOException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.*;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.InvalidTransformException;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.EncryptionElementProxy;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.CachedXPathAPI;

/**
 * This class maps to the <CODE><B>xenc</B>:ReferenceList</CODE> element. NOTE:
 * this is physically the same as a {@link org.apache.xml.security.transforms.Transforms},
 * but has different semantics. Using <CODE>ds:Transforms</CODE>, signer and
 * verifier perform the same operations on the data. Using <CODE>xenc:Transforms</CODE>,
 * encryptor and decryptor perform opposite operations.
 *
 * @author $Author$
 */
public class Transforms extends EncryptionElementProxy {

   public Transforms(Document doc) {

      super(doc, EncryptionConstants._TAG_TRANSFORMS);

      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
   }

   /**
    * Consturcts {@link Transforms} from {@link Element} which is <code>Transforms</code> Element
    *
    * @param element  is <code>Transforms</code> element
    * @param BaseURI the URI where the XML instance was stored
    * @throws DOMException
    * @throws InvalidTransformException
    * @throws TransformationException
    * @throws XMLSecurityException
    * @throws XMLSignatureException
    */
   public Transforms(Element element, String BaseURI)
           throws XMLSecurityException {

      super(element, BaseURI, EncryptionConstants._TAG_TRANSFORMS);

      int numberOfTransformElems = this.getLength();

      if (numberOfTransformElems == 0) {

         // At least ont Transform element must be present. Bad.
         Object exArgs[] = { "ds:" + Constants._TAG_TRANSFORM,
                             "xenc:" + EncryptionConstants._TAG_TRANSFORMS };

         throw new XMLSecurityException("xml.WrongContent", exArgs);
      }
   }

   /**
    * Adds the <code>Transform</code> with the specified <code>Transform algorithm URI</code>
    *
    * @param transformURI the URI form of transform that indicates which transformation is applied to data
    * @throws TransformationException
    */
   public void addTransform(String transformURI)
           throws TransformationException {

      try {
         Transform transform = Transform.getInstance(this._doc, transformURI);

         this.addTransform(transform);
      } catch (InvalidTransformException ex) {
         throw new TransformationException("empty", ex);
      }
   }

   /**
    * Adds the <code>Transform</code> with the specified <code>Transform algorithm URI</code>
    *
    * @param transformURI the URI form of transform that indicates which transformation is applied to data
    * @param contextElement
    * @throws TransformationException
    * @see Transform#getInstance(Document doc, String algorithmURI, Element childElement)
    */
   public void addTransform(String transformURI, Element contextElement)
           throws TransformationException {

      try {
         Transform transform = Transform.getInstance(this._doc, transformURI,
                                                     contextElement);

         this.addTransform(transform);
      } catch (InvalidTransformException ex) {
         throw new TransformationException("empty", ex);
      }
   }

   /**
    * Adds the <code>Transform</code> with the specified <code>Transform algorithm URI</code>
    *
    * @param transformURI the URI form of transform that indicates which transformation is applied to data
    * @param contextNodes
    * @throws TransformationException
    * @see Transform#getInstance(Document doc, String algorithmURI, NodeList contextNodes)
    */
   public void addTransform(String transformURI, NodeList contextNodes)
           throws TransformationException {

      try {
         Transform transform = Transform.getInstance(this._doc, transformURI,
                                                     contextNodes);

         this.addTransform(transform);
      } catch (InvalidTransformException ex) {
         throw new TransformationException("empty", ex);
      }
   }

   /**
    * Adds a user-provided Transform step.
    *
    * @param transform {@link Transform} object
    */
   private void addTransform(Transform transform) {
      Element transformElement = transform.getElement();

      this._constructionElement.appendChild(transformElement);
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
   }

   /**
    * Applies all included <code>Transform</code>s to xmlSignatureInput and returns the result of these transformations.
    *
    * @param xmlSignatureInput the input for the <code>Transform</code>s
    * @return the result of the <code>Transforms</code>
    * @throws TransformationException
    */
   public XMLSignatureInput performDecryptionTransforms(
           XMLSignatureInput xmlSignatureInput) throws TransformationException {

      try {
         for (int i = 0; i < this.getLength(); i++) {
            Transform t = this.item(i);

            xmlSignatureInput = t.performTransform(xmlSignatureInput);
         }

         return xmlSignatureInput;
      } catch (IOException ex) {
         throw new TransformationException("empty", ex);
      } catch (CanonicalizationException ex) {
         throw new TransformationException("empty", ex);
      } catch (InvalidCanonicalizerException ex) {
         throw new TransformationException("empty", ex);
      }
   }

   /**
    * Return the nonnegative number of transformations.
    *
    * @return the number of transformations
    * @throws TransformationException
    */
   public int getLength() throws TransformationException {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "ds",
                                                  Constants.SignatureSpecNS);
         CachedXPathAPI xpathAPI = new CachedXPathAPI();
         NodeList transformElems =
            xpathAPI.selectNodeList(this._constructionElement,
                                    "./ds:" + Constants._TAG_TRANSFORM + "", nscontext);

         return transformElems.getLength();
      } catch (TransformerException ex) {
         throw new TransformationException("empty", ex);
      }
   }

   /**
    * Return the <it>i</it><sup>th</sup> <code>{@link Transform}</code>.
    * Valid <code>i</code> values are 0 to <code>{@link #getLength}-1</code>.
    *
    * @param i index of {@link Transform} to return
    * @return the <it>i</it><sup>th</sup> transforms
    * @throws TransformationException
    */
   private Transform item(int i) throws TransformationException {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "ds",
                                                  Constants.SignatureSpecNS);
         CachedXPathAPI xpathAPI = new CachedXPathAPI();
         Element transformElem =
            (Element) xpathAPI.selectSingleNode(this._constructionElement,
                                                "./ds:" + Constants._TAG_TRANSFORM + "[" + (i + 1) + "]",
                                                nscontext);

         if (transformElem == null) {
            return null;
         } else {
            return new Transform(transformElem, this._baseURI);
         }
      } catch (TransformerException ex) {
         throw new TransformationException("empty", ex);
      } catch (XMLSecurityException ex) {
         throw new TransformationException("empty", ex);
      }
   }
}