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

import org.apache.xpath.CachedXPathAPI;


/**
 * @author Raul Benito
 */
public class CachedXPathAPIHolder {
    static ThreadLocal  local=new ThreadLocal();
    	           
    CachedXPathAPI cx;
	/**
	 * 
	 */
	public CachedXPathAPIHolder() {
		cx=null;
		// TODO Auto-generated constructor stub
	}
    /**
     * @return
     */
    public CachedXPathAPI getCachedXPathAPI() {
        if (cx==null) { 
                  cx=(CachedXPathAPI)local.get();
                  if (cx==null) {
                     cx=new CachedXPathAPI();
                     local.set(cx);
                  }
                  //cx.getXPathContext().reset();//
                  //cx=new CachedXPathAPI();
        }
    	return cx;
    }
}
