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

import org.apache.commons.digester.Digester;

/**
 * An implementation of the Transform interface which replaces all occurrences
 * of a specified string with a different string.
 * <p>
 * Because this class wishes to configure instances via nested "from" and
 * "to" tags, it needs to define an addRules method to add rules to the
 * Digester dynamically. Note that there are different ways of defining the
 * rules though; for example they can be defined in a separate
 * SubstituteTransformRuleInfo class.
 */
 
public class SubstituteTransform implements Transform {
    private String from;
    private String to;
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public void setTo(String to) {
        this.to = to;
    }
    
    public String transform(String s) {
        StringBuffer buf = new StringBuffer(s);
        while (true) {
            int idx = buf.indexOf(from);
            if (idx == -1)
                break;
            
            StringBuffer buf2 = buf.replace(idx, idx+from.length(), to);
        }
        return buf.toString();
    }
    
    public static void addRules(Digester d, String patternPrefix) {
        d.addCallMethod(patternPrefix+"/from", "setFrom", 0);
        d.addCallMethod(patternPrefix+"/to", "setTo", 0);
    }
}
