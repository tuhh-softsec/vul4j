package org.codehaus.plexus.util.xml;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.WriterFactory;

/**
 * Test the {@link XmlUtil} class.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class XmlUtilTest
    extends TestCase
{
    private static final File basedir = new File( new File( "" ).getAbsolutePath() );

    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    /** {@inheritDoc} */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }

    public void testPrettyFormatInputStreamOutputStream()
        throws Exception
    {
        File testDocument = new File( basedir, "src/test/resources/testDocument.xhtml" );
        assertTrue( testDocument.exists() );

        InputStream is = null;
        OutputStream os = null;
        try
        {
            is = new FileInputStream( testDocument );
            os = new FileOutputStream( new File( basedir, "target/test/prettyFormatTestDocumentOutputStream.xml" ) );

            assertNotNull( is );
            assertNotNull( os );

            XmlUtil.prettyFormat( is, os );
        }
        finally
        {
            IOUtil.close( is );
            IOUtil.close( os );
        }
    }

    public void testPrettyFormatReaderWriter()
        throws Exception
    {
        File testDocument = new File( basedir, "src/test/resources/testDocument.xhtml" );
        assertTrue( testDocument.exists() );

        Reader reader = null;
        Writer writer = null;
        try
        {
            reader = ReaderFactory.newXmlReader( testDocument );
            writer = WriterFactory.newXmlWriter( new File( basedir, "target/test/prettyFormatTestDocumentWriter.xml" ) );

            assertNotNull( reader );
            assertNotNull( writer );

            XmlUtil.prettyFormat( reader, writer );
        }
        finally
        {
            IOUtil.close( reader );
            IOUtil.close( writer );
        }
    }

    public void testPrettyFormatString()
        throws Exception
    {
        File testDocument = new File( basedir, "src/test/resources/testDocument.xhtml" );
        assertTrue( testDocument.exists() );

        Reader reader = null;
        Writer writer = null;
        String content;
        try
        {
            reader = ReaderFactory.newXmlReader( testDocument );
            content = IOUtil.toString( reader );

            reader = ReaderFactory.newXmlReader( testDocument );
            writer = new StringWriter();
            XmlUtil.prettyFormat( reader, writer );
        }
        finally
        {
            IOUtil.close( reader );
            IOUtil.close( writer );
        }

        assertNotNull( content );

        int countEOL = StringUtils.countMatches( content, XmlUtil.DEFAULT_LINE_SEPARATOR );
        assertTrue( countEOL < StringUtils.countMatches( writer.toString(), XmlUtil.DEFAULT_LINE_SEPARATOR ) );
    }

    public void testPrettyFormatReaderWriter2()
        throws Exception
    {
        File testDocument = new File( basedir, "src/test/resources/test.xdoc.xhtml" );
        assertTrue( testDocument.exists() );

        Reader reader = null;
        Writer writer = null;
        try
        {
            reader = ReaderFactory.newXmlReader( testDocument );
            writer = WriterFactory.newXmlWriter( new File( basedir, "target/test/prettyFormatTestXdocWriter.xml" ) );

            assertNotNull( reader );
            assertNotNull( writer );

            XmlUtil.prettyFormat( reader, writer );
        }
        finally
        {
            IOUtil.close( reader );
            IOUtil.close( writer );
        }
    }
}
