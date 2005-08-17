/*
 * Copyright  1999-2004 The Apache Software Foundation.
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
package org.apache.xml.security.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.xml.security.transforms.implementations.FuncHere;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.Expression;
import org.apache.xpath.compiler.FuncLoader;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.functions.Function;
import org.w3c.dom.Document;


/**
 * @author Raul Benito
 */
public class CachedXPathAPIHolder {
	 static org.apache.commons.logging.Log log = 
	        org.apache.commons.logging.LogFactory.getLog(CachedXPathAPIHolder.class.getName());

    static ThreadLocal  local=new ThreadLocal();
    static ThreadLocal localDoc=new ThreadLocal();
  
	/**
	 * Sets the doc for the xpath transformation. Resets the cache if needed
	 * @param doc
	 */
	public static void setDoc(Document doc) {                    
       if (localDoc.get()!=doc) {
            CachedXPathAPI cx=(CachedXPathAPI)local.get();
            if (cx==null) {
               cx=new CachedXPathAPI();
               local.set(cx);
               localDoc.set(doc);
               return;
            }
            //Different docs reset.
            cx.getXPathContext().reset();
            localDoc.set(doc);                     
        }		
	}
    /**
     * @return the cachexpathapi for this thread
     */
    public static CachedXPathAPI getCachedXPathAPI() {        
        CachedXPathAPI cx=(CachedXPathAPI)local.get();        
        if (cx==null) {
            cx=new CachedXPathAPI();
            local.set(cx);
            localDoc.set(null);            
        }
    	return cx;
    }
	static {
		registerHereFunction();
	}
	 /**
	   * Class FuncHereLoader
	    *
	    * @author $Author$
	    * @version $Revision$
	    */
	   public static class FuncHereLoader extends FuncLoader {

	      /**
	       * Constructor FuncHereLoader
	       *
	       */
	      public FuncHereLoader() {
	         super(FuncHere.class.getName(), 0);
	      }

	      /**
	       * Method getFunction
	       * @return  a New function       
	       */
	      public Function getFunction() {
	         return new FuncHere();
	      }

	      /**
	       * Method getName
	       * @return  the name of the class.     
	       *
	       */
	      public String getName() {
	         return FuncHere.class.getName();
	      }
	   }
	/**
	 * 
	 */
	private static void registerHereFunction() {
		log.info("Registering Here function");
		/**
		 * Try to register our here() implementation as internal function.
		 */            
		{	    
	        Class []args={String.class, Expression.class};
	        try {
				Method installFunction=FunctionTable.class.getMethod("installFunction",args);
	            if ((installFunction.getModifiers() & Modifier.STATIC)!=0) {
	            	//xalan 1.1
	                Object []params={"here",new FuncHere()};
	                installFunction.invoke(null, params );
	            } else {
	            	log.warn("Xalan new, install function not installed.");
	                //TODO: Right now not a good way to install the function. Let see how this
	                //is resolv, latter in xalan.
	            }
			} catch (Exception e) {
				//what to do here, just log it
	            log.warn("Exception while installing Function",e);
	        }
	        if (log.isDebugEnabled())
	        	log.debug("Registered class " + FuncHere.class.getName()
		            + " for XPath function 'here()' function in internal table");
	
		    /* The following tweak by "Eric Olson" <ego@alum.mit.edu>
		     * is to enable xml-security to play with JDK 1.4 which
		     * unfortunately bundles an old version of Xalan
		     */
		    FuncLoader funcHereLoader = new FuncHereLoader();
	
		    try {
		        java.lang.reflect.Field mFunctions = FunctionTable.class.getField("m_functions");
		        FuncLoader[] m_functions = (FuncLoader[]) mFunctions.get(null);
	
		        for (int i = 0; i < m_functions.length; i++) {
		            FuncLoader loader = m_functions[i];
	
		            if (loader != null) {
	                    if (log.isDebugEnabled())
	                    	log.debug("Func " + i + " " + loader.getName());
	
		                if (loader.getName().equals(funcHereLoader.getName())) {
		                    m_functions[i] = funcHereLoader;
		                }
		            }
		        }
		    } catch (java.lang.NoSuchFieldException e) {
	            if (log.isDebugEnabled()) {
		    	    log.debug("Unable to patch xalan function table.", e);
                }
	        } catch (Exception e) {
	            if (log.isDebugEnabled()) {
		    	    log.debug("Unable to patch xalan function table.", e);
                }
	        } 
		}
	}
}
