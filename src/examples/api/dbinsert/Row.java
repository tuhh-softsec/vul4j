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

import java.util.List;
import java.util.LinkedList;

/**
 * See Main.java.
 */
public class Row {
    
  /**
   * Alas, we can't just use a Map to store the (name, value) pairs
   * because the output will look weird if we don't preserve the column
   * order. This wouldn't be a problem if we were really inserting into
   * a database; it only matters because we are displaying the SQL statements
   * via stdout instead. The LinkedHashMap class would be nice to use, but
   * that would require java 1.4, so we'll use a list instead, and may as
   * well call the entries in the list 'Column' objects.
   */
  public static class Column {
      private String name, value;
      
      public Column(String name, String value) {
          this.name = name; 
          this.value = value;
      }
      
      public String getName() {
          return name;
      }
      
      public String getValue() {
          return value;
      }
  }
    
  private LinkedList columns = new LinkedList();

  public Row() {
  }
  
  public void addColumn(String name, String value) {
      columns.add(new Column(name, value));
  }

  public List getColumns() {
      return columns;
  }
}  

