/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.digester3.examples.dbinsert;

import org.apache.commons.digester3.AbstractRulesModule;

public final class DBInsertModule extends AbstractRulesModule {

    @Override
    protected void configure() {
        forPattern("database/table")
            // Create a new instance of class Table, and push that
            // object onto the digester stack of objects. We only need
            // this so that when a row is inserted, it can find out what
            // the enclosing tablename was. 
            //
            // Note that the object is popped off the stack at the end of the 
            // "table" tag (normal behaviour for ObjectCreateRule). Because we 
            // never added the table object to some parent object, when it is 
            // popped off the digester stack it becomes garbage-collected. That 
            // is fine in this situation; we've done all the necessary work and
            // don't need the table object any more.
            .createObject().ofType(Table.class)
            .then()

            // Map *any* attributes on the table tag to appropriate
            // setter-methods on the top object on the stack (the Table
            // instance created by the preceeding rule). We only expect one
            // attribute, though: a 'name' attribute specifying what table
            // we are inserting rows into.
            .setProperties();

        // When we encounter a "row" tag, invoke methods on the provided
        // RowInserterRule instance.
        //
        // This rule creates a Row instance and pushes it on the digester
        // object stack, rather like ObjectCreateRule, so that the column
        // tags have somewhere to store their information. And when the
        // </row> end tag is found, the rule will trigger to remove this
        // object from the stack, and also do an actual database insert.
        //
        // Note that the rule instance we are passing to the digester has
        // been initialised with some useful data (the SQL connection).
        //
        // Note also that in this case we are not using the digester's
        // factory methods to create the rule instance; that's just a
        // convenience - and obviously not an option for Rule classes
        // that are not part of the digester core implementation.
        forPattern("database/table/row").addRule(new RowInserterRule());

        // when we encounter a "column" tag, call setColumn on the top
        // object on the stack, passing two parameters: the "name"
        // attribute, and the text within the tag body.
        forPattern("database/table/row/column").
            callMethod("addColumn").withParamTypes(String.class, String.class)
            .then()
            .callParam().fromAttribute("name")
            .then()
            .callParam().ofIndex(1);
    }

}
