package org.codehaus.plexus.util.introspection;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ReflectionValueExtractorTest
    extends TestCase
{
    private Project project;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        Dependency dependency1 = new Dependency();
        dependency1.setArtifactId("dep1");
        Dependency dependency2 = new Dependency();
        dependency2.setArtifactId("dep2");

        project = new Project();
        project.setModelVersion( "4.0.0" );
        project.setGroupId( "org.apache.maven" );
        project.setArtifactId( "maven-core" );
        project.setName( "Maven" );
        project.setVersion( "2.0-SNAPSHOT" );
        project.setScm( new Scm() );
        project.getScm().setConnection( "scm-connection" );
        project.addDependency( dependency1 );
        project.addDependency( dependency2 );
        project.setBuild( new Build() );
    }

    public void testValueExtraction()
        throws Exception
    {
        // ----------------------------------------------------------------------
        // Top level values
        // ----------------------------------------------------------------------

        assertEquals( "4.0.0", ReflectionValueExtractor.evaluate( "project.modelVersion", project ) );

        assertEquals( "org.apache.maven", ReflectionValueExtractor.evaluate( "project.groupId", project ) );

        assertEquals( "maven-core", ReflectionValueExtractor.evaluate( "project.artifactId", project ) );

        assertEquals( "Maven", ReflectionValueExtractor.evaluate( "project.name", project ) );

        assertEquals( "2.0-SNAPSHOT", ReflectionValueExtractor.evaluate( "project.version", project ) );

        // ----------------------------------------------------------------------
        // SCM
        // ----------------------------------------------------------------------

        assertEquals( "scm-connection", ReflectionValueExtractor.evaluate( "project.scm.connection", project ) );

        // ----------------------------------------------------------------------
        // Dependencies
        // ----------------------------------------------------------------------

        List dependencies = (List) ReflectionValueExtractor.evaluate( "project.dependencies", project );

        Assert.assertNotNull( dependencies );

        Assert.assertEquals( 2, dependencies.size() );

        // ----------------------------------------------------------------------
        // Dependencies - using index notation
        // ----------------------------------------------------------------------

        // List
        Dependency dependency = (Dependency)ReflectionValueExtractor.evaluate( "project.dependencies[0]", project );

        Assert.assertNotNull( dependency );

        Assert.assertTrue( "dep1".equals(dependency.getArtifactId()) );

        String artifactId = (String)ReflectionValueExtractor.evaluate( "project.dependencies[1].artifactId", project );

        Assert.assertTrue( "dep2".equals(artifactId) );

        // Array

        dependency = (Dependency)ReflectionValueExtractor.evaluate( "project.dependenciesAsArray[0]", project );

        Assert.assertNotNull( dependency );

        Assert.assertTrue( "dep1".equals(dependency.getArtifactId()) );

        artifactId = (String)ReflectionValueExtractor.evaluate( "project.dependenciesAsArray[1].artifactId", project );

        Assert.assertTrue( "dep2".equals(artifactId) );

        // Map

        dependency = (Dependency)ReflectionValueExtractor.evaluate( "project.dependenciesAsMap(dep1)", project );

        Assert.assertNotNull( dependency );

        Assert.assertTrue( "dep1".equals(dependency.getArtifactId()) );

        artifactId = (String)ReflectionValueExtractor.evaluate( "project.dependenciesAsMap(dep2).artifactId", project );

        Assert.assertTrue( "dep2".equals(artifactId) );

        // ----------------------------------------------------------------------
        // Build
        // ----------------------------------------------------------------------

        Build build = (Build) ReflectionValueExtractor.evaluate( "project.build", project );

        Assert.assertNotNull( build );
    }

    public void testValueExtractorWithAInvalidExpression()
        throws Exception
    {
        Assert.assertNull( ReflectionValueExtractor.evaluate( "project.foo", project ) );
        Assert.assertNull( ReflectionValueExtractor.evaluate( "project.dependencies[10]", project ) );
        Assert.assertNull( ReflectionValueExtractor.evaluate( "project.dependencies[0].foo", project ) );
    }

    public static class Project
    {
        private String modelVersion;

        private String groupId;

        private Scm scm;

        private List dependencies = new ArrayList();

        private Build build;

        private String artifactId;

        private String name;

        private String version;

        public void setModelVersion( String modelVersion )
        {
            this.modelVersion = modelVersion;
        }

        public void setGroupId( String groupId )
        {
            this.groupId = groupId;
        }

        public void setScm( Scm scm )
        {
            this.scm = scm;
        }

        public void addDependency( Dependency dependency )
        {
            this.dependencies.add( dependency );
        }

        public void setBuild( Build build )
        {
            this.build = build;
        }

        public void setArtifactId( String artifactId )
        {
            this.artifactId = artifactId;
        }

        public void setName( String name )
        {
            this.name = name;
        }

        public void setVersion( String version )
        {
            this.version = version;
        }

        public Scm getScm()
        {
            return scm;
        }

        public String getModelVersion()
        {
            return modelVersion;
        }

        public String getGroupId()
        {
            return groupId;
        }

        public List getDependencies()
        {
            return dependencies;
        }

        public Build getBuild()
        {
            return build;
        }

        public String getArtifactId()
        {
            return artifactId;
        }

        public String getName()
        {
            return name;
        }

        public String getVersion()
        {
            return version;
        }

        public Dependency[] getDependenciesAsArray()
        {
            return (Dependency[]) getDependencies().toArray( new Dependency[0]);
        }

        public Map getDependenciesAsMap()
        {
            Map ret = new HashMap();
            for ( Iterator it = getDependencies().iterator(); it.hasNext();)
            {
                Dependency dep = (Dependency)it.next();
                ret.put( dep.getArtifactId(), dep );
            }
            return ret;
        }
    }

    public static class Build
    {

    }

    public static class Dependency
    {
        private String artifactId;

        public String getArtifactId()
        {
            return artifactId;
        }

        public void setArtifactId(String id)
        {
            artifactId = id;
        }
    }

    public static class Scm
    {
        private String connection;

        public void setConnection( String connection )
        {
            this.connection = connection;
        }

        public String getConnection()
        {
            return connection;
        }
    }
}