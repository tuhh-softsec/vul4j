/*
 * Copyright 2003-2004 The Apache Software Foundation.
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

import java.util.Map;
import java.util.Iterator;

/**
 * See Main.java.
 */
public class RowInserterRule extends org.apache.commons.digester.Rule {

    private java.sql.Connection conn;
    
    public RowInserterRule(java.sql.Connection conn) {
        this.conn = conn;
    }
    
    /**
     * This method is invoked when the start tag for an xml element representing
     * a database row is encountered. It pushes a new Row instance onto the
     * digester stack (rather like an ObjectCreateRule) so that column data
     * can be stored on it.
     */
    public void begin(String namespace, String name, org.xml.sax.Attributes attrs) {
        digester.push(new Row());
    }
    
    /**
     * This method is invoked when the end tag for an xml element representing
     * a database row is encountered. It pops a fully-configured Row instance
     * off the digester stack, accesses the object below it on the stack (a
     * Table object) to get the tablename, then does an SQL insert). Actually,
     * here we just print out text rather than do the sql insert, but the
     * real implementation should be fairly simple.
     * <p>
     * Note that after this rule completes, the row/column information is
     * <i>discarded</i>, ie this rule performs actions <i>as the input is
     * parsed</i>. This contrasts with the more usual way digester is used,
     * which is to build trees of objects for later use. But it's a perfectly
     * valid use of Digester.
     */
    public void end(String namespace, String name) {
        Row row = (Row) digester.pop();
        Table table = (Table) digester.peek();

        // Obviously, all this would be replaced by code like:
        //   stmt = conn.prepareStatement();
        //   stmt.setString(n, value);
        //
        // Many improvements can then be implemented, such as using the 
        // PreparedStatement.getParameterMetaData method to retrieve
        // retrieve parameter types, etc.
        
        StringBuffer colnames = new StringBuffer();
        StringBuffer colvalues = new StringBuffer();
        
        for(Iterator i = row.getColumns().iterator(); i.hasNext();)
        {
            Row.Column column = (Row.Column) i.next();
            
            if (colnames.length() > 0)
            {
                colnames.append(", ");
                colvalues.append(", ");
            }
        
            colnames.append("'");
            colnames.append(column.getName());
            colnames.append("'");
            
            colvalues.append("'");
            colvalues.append(column.getValue());
            colvalues.append("'");
        }

        StringBuffer buf = new StringBuffer();
        buf.append("insert into ");
        buf.append(table.getName());
        buf.append(" (");
        buf.append(colnames.toString());
        buf.append(") values (");
        buf.append(colvalues.toString());
        buf.append(")");
        
        // here the prepared statement would be executed....
        System.out.println(buf.toString());
    }
}
