/*
 * Copyright  1999-2009 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.xml.security.test.signature;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.axes.NodeSequence;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An implementation of a resource resolver, which evaluates xpointer expressions.
 * 
 * @author wglas
 */
public class XPointerResourceResolver extends ResourceResolverSpi
{
    private static Log log = LogFactory.getLog(XPointerResourceResolver.class);
    
    private static final String XP_OPEN = "xpointer(";
    private static final String XNS_OPEN = "xmlns(";
    
    private Node baseNode;
    
    public XPointerResourceResolver(Node baseNode)
    {
        this.baseNode = baseNode;
    }
    
    private static class XPointerPrefixResolver extends PrefixResolverDefault
    {
        private Map extraPrefixes;
        
        public XPointerPrefixResolver(Node node)
        {
            super(node);
            this.extraPrefixes = new HashMap();
        }

        public void addExtraPrefix (String pfx, String nsURI)
        {
            this.extraPrefixes.put(pfx,nsURI); 
        }
        
        public String getNamespaceForPrefix(String pfx)
        {
            String nsURI = (String)this.extraPrefixes.get(pfx);
            
            if (nsURI != null) return nsURI;
            
            return super.getNamespaceForPrefix(pfx);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.xml.security.utils.resolver.ResourceResolverSpi#engineCanResolve(org.w3c.dom.Attr, java.lang.String)
     */
    public boolean engineCanResolve(Attr uri, String BaseURI)
    {
        String v = uri.getNodeValue();
        
        if (v==null || v.length() <= 0) return false;
        
        if (v.charAt(0) != '#')
            return false;
        
        String xpURI;
        try
        {
            xpURI = URLDecoder.decode(v, "utf-8");
        } catch (UnsupportedEncodingException e)
        {
            log.warn("utf-8 not a valid encoding",e);
            return false;
        }
        
        String parts[] = xpURI.substring(1).split("\\s");
        
        // plain ID reference.
        if (parts.length == 1 && !parts[0].startsWith(XNS_OPEN))
            return true;
        
        int i=0;
        
        for (;i<parts.length-1;++i)
            if (!parts[i].endsWith(")") ||  !parts[i].startsWith(XNS_OPEN))
                    return false;
            
        if (!parts[i].endsWith(")") || !parts[i].startsWith(XP_OPEN))
                return false;
        
        log.debug("xpURI="+xpURI);
        log.debug("BaseURI="+BaseURI);

        return true;
    }

    /* (non-Javadoc)
     * @see org.apache.xml.security.utils.resolver.ResourceResolverSpi#engineResolve(org.w3c.dom.Attr, java.lang.String)
     */
    public XMLSignatureInput engineResolve(Attr uri, String BaseURI) throws ResourceResolverException
    {
        String v = uri.getNodeValue();
        
        if (v.charAt(0) != '#')
            return null;
        
        String xpURI;
        try
        {
            xpURI = URLDecoder.decode(v, "utf-8");
        } catch (UnsupportedEncodingException e)
        {
            log.warn("utf-8 not a valid encoding",e);
            return null;
        }
        
        String parts[] = xpURI.substring(1).split("\\s");
        
        int i=0;

        XPointerPrefixResolver nsContext=null;
        
        if (parts.length > 1)
        {
            nsContext= new XPointerPrefixResolver(this.baseNode);
            
            for (;i<parts.length-1;++i)
            {
                if (!parts[i].endsWith(")") ||  !parts[i].startsWith(XNS_OPEN))
                    return null;
                
                String mapping = parts[i].substring(XNS_OPEN.length(),parts[i].length()-1);
                
                int pos = mapping.indexOf('=');
                
                if (pos <= 0 || pos >= mapping.length()-1)
                    throw new ResourceResolverException("malformed namespace part of XPointer expression",uri,BaseURI);
                
                nsContext.addExtraPrefix(mapping.substring(0,pos), 
                                         mapping.substring(pos+1));
            }
        }
            
        try
        {
            Node node = null;
            NodeList nodes=null;
        
            // plain ID reference.
            if (i==0 && !parts[i].startsWith(XP_OPEN))
            {
                node = this.baseNode.getOwnerDocument().getElementById(parts[i]);
            }
            else
            {
                if (!parts[i].endsWith(")") || !parts[i].startsWith(XP_OPEN))
                    return null;
                
                String xpathExpr = parts[i].substring(XP_OPEN.length(),parts[i].length()-1);
                
                XObject xo;
                
                if (nsContext != null)
                    xo = XPathAPI.eval(this.baseNode, xpathExpr,nsContext);
                else
                    xo = XPathAPI.eval(this.baseNode, xpathExpr);
                
                   
                if (!(xo instanceof NodeSequence)) return null;
                
                nodes = ((NodeSequence)xo).nodelist();
                
                if (nodes.getLength() == 0) return null;
                if (nodes.getLength() == 1)
                    node = nodes.item(0);
            }
        
            XMLSignatureInput result = null;
                        
            if (node != null)
            {
                 result = new XMLSignatureInput(node);
            }
            else if (nodes !=null)
            {
                Set nodeSet = new HashSet(nodes.getLength());
            
                for (int j=0 ; j<nodes.getLength(); ++j)
                {
                    nodeSet.add(nodes.item(j));
                }
                
                result = new XMLSignatureInput(nodeSet);
            }
            else
                return null;
            
            result.setMIMEType("text/xml");
            result.setExcludeComments(true);
            result.setSourceURI((BaseURI != null) ? BaseURI.concat(v):v);      

            return result;
            
        } catch (TransformerException e)
        {
            throw new ResourceResolverException("TransformerException inside XPointer expression",e,uri,BaseURI);
        }
    }

}
