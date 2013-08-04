package org.codehaus.plexus.util.xml.pull;

/*
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class EntityReplacementMap
{
    final String entityName[];
    final char[] entityNameBuf[];
    final String entityReplacement[];
    final char[] entityReplacementBuf[];
    int entityEnd;
    final int entityNameHash[];

    public EntityReplacementMap( String[][] replacements ){
        int length = replacements.length;
        entityName = new String[length];
        entityNameBuf = new char[length][];
        entityReplacement = new String[length];
        entityReplacementBuf = new char[length][];
        entityNameHash = new int[length];

        for ( String[] replacement : replacements )
        {
            defineEntityReplacementText( replacement[0],replacement[1] );
        }
    }

    private void defineEntityReplacementText(String entityName,
                                            String replacementText)
    {
        if ( !replacementText.startsWith( "&#" ) && this.entityName != null && replacementText.length() > 1 )
        {
            String tmp = replacementText.substring( 1, replacementText.length() - 1 );
            for ( int i = 0; i < this.entityName.length; i++ )
            {
                if ( this.entityName[i] != null && this.entityName[i].equals( tmp ) )
                {
                    replacementText = this.entityReplacement[i];
                }
            }
        }

        // this is to make sure that if interning works we will take advantage of it ...
        char[] entityNameCharData = entityName.toCharArray();
        //noinspection ConstantConditions
        this.entityName[entityEnd] = newString( entityNameCharData, 0, entityName.length());
        entityNameBuf[entityEnd] = entityNameCharData;

        entityReplacement[entityEnd] = replacementText;
        entityReplacementBuf[entityEnd] = replacementText.toCharArray();
        entityNameHash[ entityEnd ] = fastHash( entityNameBuf[entityEnd], 0, entityNameBuf[entityEnd].length);
        ++entityEnd;
        //TODO disallow < or & in entity replacement text (or ]]>???)
        //TODO keepEntityNormalizedForAttributeValue cached as well ...
    }

    private String newString(char[] cbuf, int off, int len) {
        return new String(cbuf, off, len);
    }

    /**
     * simplistic implementation of hash function that has <b>constant</b>
     * time to compute - so it also means diminishing hash quality for long strings
     * but for XML parsing it should be good enough ...
     */
    private static int fastHash( char ch[], int off, int len ) {
        if(len == 0) return 0;
        //assert len >0
        int hash = ch[off]; // hash at beginning
        //try {
        hash = (hash << 7) + ch[ off +  len - 1 ]; // hash at the end
        //} catch(ArrayIndexOutOfBoundsException aie) {
        //    aie.printStackTrace(); //should never happen ...
        //    throw new RuntimeException("this is violation of pre-condition");
        //}
        if(len > 16) hash = (hash << 7) + ch[ off + (len / 4)];  // 1/4 from beginning
        if(len > 8)  hash = (hash << 7) + ch[ off + (len / 2)];  // 1/2 of string size ...
        // notice that hash is at most done 3 times <<7 so shifted by 21 bits 8 bit value
        // so max result == 29 bits so it is quite just below 31 bits for long (2^32) ...
        //assert hash >= 0;
        return  hash;
    }
}
