package org.apache.xml.security.keys.provider;

import org.apache.xml.security.utils.*;
import org.w3c.dom.*;

public class Alias extends ElementProxy {

  public Alias(Document doc, String alias) {
     super(doc);
     this.setAlias(alias);
  }
  public String getBaseLocalName() {
    return "Alias";
  }
  public String getBaseNamespace() {
    return ApacheKeyStore.APACHEKEYSTORE_NAMESPACE;
  }
  private void setAlias(String alias) {
     if (this._state == ElementProxy.MODE_CREATE && alias != null && alias.length() > 0) {
        while (this._constructionElement.hasChildNodes()) {
           this._constructionElement.removeChild(this._constructionElement.getFirstChild());
        }
        this._constructionElement.appendChild(this._doc.createTextNode(alias));
     }
  }
  public String getAlias() {
     return ((Text)this._constructionElement.getFirstChild()).getData();
  }
}