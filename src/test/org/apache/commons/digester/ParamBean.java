/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.apache.commons.digester;


/** 
 * This bean is used to replicate a reasonably complex use case 
 * whose behaviour has changed from Digester 1.3 to 1.4.
 *
 * @author robert burrell donkin
 */
public class ParamBean {
    
    private boolean cool;
    private String that;
    private String _this;
    
    public ParamBean() {}
    
    public boolean isCool() {
        return cool;
    }	
    
    public void setCool(boolean cool) {
        this.cool = cool;
    }
    
    public String getThis() {
        return _this;
    }
    
    public String getThat() {
        return that;
    }
    
    public String setThisAndThat(String _this, String that) {
        this._this = _this;
        this.that = that;
        return "The Other";
    }
}
