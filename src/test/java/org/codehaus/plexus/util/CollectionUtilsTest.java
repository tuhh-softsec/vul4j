package org.codehaus.plexus.util;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache MavenSession" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache MavenSession", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

public class CollectionUtilsTest
    extends TestCase
{
    public void testMergeMaps()
    {
        Map dominantMap = new HashMap();
        dominantMap.put( "a", "a" );
        dominantMap.put( "b", "b" );
        dominantMap.put( "c", "c" );
        dominantMap.put( "d", "d" );
        dominantMap.put( "e", "e" );
        dominantMap.put( "f", "f" );

        Map recessiveMap = new HashMap();
        recessiveMap.put( "a", "invalid" );
        recessiveMap.put( "b", "invalid" );
        recessiveMap.put( "c", "invalid" );
        recessiveMap.put( "x", "x" );
        recessiveMap.put( "y", "y" );
        recessiveMap.put( "z", "z" );

        Map result = CollectionUtils.mergeMaps( dominantMap, recessiveMap );

        // We should have 9 elements
        assertEquals( 9, result.keySet().size() );

        // Check the elements.
        assertEquals( "a", result.get( "a" ) );
        assertEquals( "b", result.get( "b" ) );
        assertEquals( "c", result.get( "c" ) );
        assertEquals( "d", result.get( "d" ) );
        assertEquals( "e", result.get( "e" ) );
        assertEquals( "f", result.get( "f" ) );
        assertEquals( "x", result.get( "x" ) );
        assertEquals( "y", result.get( "y" ) );
        assertEquals( "z", result.get( "z" ) );
    }

    public void testMergeMapArray()
    {
        // Test empty array of Maps
        Map result0 = CollectionUtils.mergeMaps( new Map[]
        {
        } );

        assertNull( result0 );

        // Test with an array with a single element.
        Map map1 = new HashMap();
        map1.put( "a", "a" );

        Map result1 = CollectionUtils.mergeMaps( new Map[]
        {
            map1
        } );

        assertEquals( "a", result1.get( "a" ) );

        // Test with an array with two elements.
        Map map2 = new HashMap();
        map2.put( "a", "aa" );
        map2.put( "b", "bb" );

        Map result2 = CollectionUtils.mergeMaps( new Map[]
        {
            map1,
            map2
        } );

        assertEquals( "a", result2.get( "a" ) );
        assertEquals( "bb", result2.get( "b" ) );

        // Now swap the dominant order.
        Map result3 = CollectionUtils.mergeMaps( new Map[]
        {
            map2,
            map1
        } );

        assertEquals( "aa", result3.get( "a" ) );
        assertEquals( "bb", result3.get( "b" ) );

        // Test with an array with three elements.
        Map map3 = new HashMap();
        map3.put( "a", "aaa" );
        map3.put( "b", "bbb" );
        map3.put( "c", "ccc" );

        Map result4 = CollectionUtils.mergeMaps( new Map[]
        {
            map1,
            map2,
            map3
        } );

        assertEquals( "a", result4.get( "a" ) );
        assertEquals( "bb", result4.get( "b" ) );
        assertEquals( "ccc", result4.get( "c" ) );

        // Now swap the dominant order.
        Map result5 = CollectionUtils.mergeMaps( new Map[]
        {
            map3,
            map2,
            map1
        } );

        assertEquals( "aaa", result5.get( "a" ) );
        assertEquals( "bbb", result5.get( "b" ) );
        assertEquals( "ccc", result5.get( "c" ) );
    }

    public void testMavenPropertiesLoading()
    {
        // Mimic MavenSession properties loading. Properties listed
        // in dominant order.
        Properties systemProperties = new Properties();
        Properties userBuildProperties = new Properties();
        Properties projectBuildProperties = new Properties();
        Properties projectProperties = new Properties();
        Properties driverProperties = new Properties();

        // System properties
        systemProperties.setProperty( "maven.home", "/projects/maven" );

        // User build properties
        userBuildProperties.setProperty( "maven.username", "jvanzyl" );
        userBuildProperties.setProperty( "maven.repo.remote.enabled", "false" );
        userBuildProperties.setProperty( "maven.repo.local", "/opt/maven/artifact" );

        // Project build properties
        projectBuildProperties.setProperty( "maven.final.name", "maven" );

        String mavenRepoRemote = "http://www.ibiblio.org/maven,http://foo/bar";

        // Project properties
        projectProperties.setProperty( "maven.repo.remote", mavenRepoRemote );

        String basedir = "/home/jvanzyl/projects/maven";
        
        // Driver properties
        driverProperties.setProperty( "basedir", basedir );
        driverProperties.setProperty( "maven.build.src", "${basedir}/src" );
        driverProperties.setProperty( "maven.build.dir", "${basedir}/target" );
        driverProperties.setProperty( "maven.build.dest", "${maven.build.dir}/classes" );
        driverProperties.setProperty( "maven.repo.remote", "http://www.ibiblio.org/maven" );
        driverProperties.setProperty( "maven.final.name", "maven-1.0" );
        driverProperties.setProperty( "maven.repo.remote.enabled", "true" );
        driverProperties.setProperty( "maven.repo.local", "${maven.home}/artifact" );
        
        Map result = CollectionUtils.mergeMaps( new Map[]
        {
            systemProperties,
            userBuildProperties,
            projectBuildProperties,
            projectProperties,
            driverProperties
        } );

        // Values that should be taken from systemProperties.
        assertEquals( "/projects/maven", (String) result.get( "maven.home" ) );

        // Values that should be taken from userBuildProperties.
        assertEquals( "/opt/maven/artifact", (String) result.get( "maven.repo.local" ) );
        assertEquals( "false", (String) result.get( "maven.repo.remote.enabled" ) );
        assertEquals( "jvanzyl", (String) result.get( "maven.username" ) );
        
        // Values take from projectBuildProperties.
        assertEquals( "maven", (String) result.get( "maven.final.name" ) );

        // Values take from projectProperties.
        assertEquals( mavenRepoRemote, (String) result.get( "maven.repo.remote" ) );
    }

    public void testIteratorToListWithAPopulatedList()
    {
        List original = new ArrayList();

        original.add( "en" );
        original.add( "to" );
        original.add( "tre" );

        List copy = CollectionUtils.iteratorToList( original.iterator() );

        assertNotNull( copy );

        assertEquals( 3, copy.size() );

        assertEquals( "en", copy.get( 0 ) );
        assertEquals( "to", copy.get( 1 ) );
        assertEquals( "tre", copy.get( 2 ) );
    }

    public void testIteratorToListWithAEmptyList()
    {
        List original = new ArrayList();

        List copy = CollectionUtils.iteratorToList( original.iterator() );

        assertNotNull( copy );

        assertEquals( 0, copy.size() );
    }
}
