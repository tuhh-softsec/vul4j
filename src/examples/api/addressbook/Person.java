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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * See Main.java.
 */
public class Person {
  private int id;
  private String category;
  private String name;
  private HashMap emails = new HashMap();
  private List addresses = new ArrayList();
  
  /** 
   * A unique id for this person. Note that the Digester automatically
   * converts the id to an integer.
   */
  public void setId(int id) {
      this.id = id;
  }
  
  public void setCategory(String category) {
      this.category = category;
  }
  
  public void setName(String name) {
      this.name = name;
  }
  
  /** we assume only one email of each type... */
  public void addEmail(String type, String address) {
      emails.put(type, address);
  }
  
  public void addAddress( Address addr ) {
     addresses.add( addr );
  }

  public void print() {
      System.out.println("Person #" + id);
      System.out.println("  category=" + category);
      System.out.println("  name=" + name);
      
      for(Iterator i = emails.keySet().iterator(); i.hasNext(); ) {
          String type = (String) i.next();
          String address = (String) emails.get(type);
          
          System.out.println("  email (type " + type + ") : " + address);
      }
      
      for(Iterator i = addresses.iterator(); i.hasNext(); ) {
          Address addr = (Address) i.next();
          addr.print(System.out, 2);
      }
  }
}
