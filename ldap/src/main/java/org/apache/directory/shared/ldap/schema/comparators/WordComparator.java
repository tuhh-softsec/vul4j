/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.shared.ldap.schema.comparators;


import org.apache.directory.shared.ldap.schema.LdapComparator;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * A comparator for Words/KeyWords. RFC 4517 par. 4.2.21 (KeywordMatch) and par.
 * 4.2.32 is pretty vague about the definition of what is a word or a keyword
 * ("...The precise definition of a word is implementation specific...) 
 * ("...The identification of keywords in the attribute value and the exactness
 *  of the match are both implementation specific...).
 * <p>
 * We will simply check that the assertion is present in the value at some place, 
 * after having deep trimmed the word.
 * <p>
 * For instance, the word "  World  " will be found in the value "Hello world!".
 * <p>
 * A word is defined by the following regexp : "(^|[^A-Za-z0-9])([A-Za-z0-9])*([^A-Za-z0-9]|$)". 
 * Anything that is not matched by this regexp will not be considered as a word.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class WordComparator extends LdapComparator<String>
{
    /** A logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger( WordComparator.class );

    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;


    /**
     * The StringComparator constructor. Its OID is the StringMatch matching
     * rule OID.
     */
    public WordComparator( String oid )
    {
        super( oid );
    }


    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( String value, String assertion )
    {
        LOG.debug( "comparing String objects '{}' with '{}'", value, assertion );

        if ( value == assertion )
        {
            return 0;
        }

        // -------------------------------------------------------------------
        // Handle some basis cases
        // -------------------------------------------------------------------
        if ( ( value == null ) || ( assertion == null ) )
        {
            return ( assertion == null ) ? 1 : -1;
        }

        // Now, trim the assertion and find it in the value
        String trimmedAssertion = StringTools.trim( assertion );
        int pos = value.indexOf( trimmedAssertion );

        if ( pos != -1 )
        {
            int assertionLength = trimmedAssertion.length();

            // Check that we are not in a middle of some text
            if ( assertionLength == value.length() )
            {
                return 0;
            }

            if ( pos == 0 )
            {
                char after = value.charAt( assertionLength );

                if ( !Character.isLetterOrDigit( after ) )
                {
                    return 0;
                }
                else
                {
                    return -1;
                }
            }

            if ( pos + assertionLength == value.length() )
            {
                char before = value.charAt( value.length() - assertionLength - 1 );

                if ( !Character.isLetterOrDigit( before ) )
                {
                    return 0;
                }
                else
                {
                    return -1;
                }
            }

            char before = value.charAt( value.length() - assertionLength );
            char after = value.charAt( assertionLength );

            if ( Character.isLetterOrDigit( after ) )
            {
                return -1;
            }

            if ( !Character.isLetterOrDigit( before ) )
            {
                return -1;
            }

            return 0;
        }

        return -1;
    }
}
