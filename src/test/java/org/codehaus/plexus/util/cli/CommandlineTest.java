package org.codehaus.plexus.util.cli;

/* ====================================================================
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
 * ====================================================================
 */

import java.io.File;

import junit.framework.TestCase;

public class CommandlineTest extends TestCase
{
    private String baseDir;
    
    /**
     * @param testName
     */
    public CommandlineTest(final String testName)
    {
        super(testName);
    }

    /*
     * @see TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
        baseDir = System.getProperty("basedir");
        assertNotNull("The system property basedir was not defined.", baseDir);
    }
    
    public void testCommandlineWithoutArgumentInConstructor()
    {
        try
        {
            Commandline cmd = new Commandline();
            cmd.setWorkingDirectory(baseDir);
            cmd.createArgument().setValue("cd");
            cmd.createArgument().setValue(".");
            assertEquals("cd .", cmd.toString());
        }
        catch(Exception e)
        {
            fail(e.getMessage());
        }
    }
    
    public void testCommandlineWithArgumentInConstructor()
    {
        try
        {
            Commandline cmd = new Commandline("cd .");
            cmd.setWorkingDirectory(baseDir);
            assertEquals("cd .", cmd.toString());
        }
        catch(Exception e)
        {
            fail(e.getMessage());
        }
    }
    
    public void testExecute()
    {
        try
        {
            Commandline cmd = new Commandline();
            cmd.setWorkingDirectory(baseDir);
            cmd.setExecutable("echo");
            assertEquals("echo", cmd.getExecutable());
            cmd.createArgument().setValue("Hello");
            assertEquals("echo Hello", cmd.toString());
            cmd.execute();
        }
        catch(Exception e)
        {
            fail(e.getMessage());
        }
    }
    
    public void testSetLine()
    {
        try
        {
            Commandline cmd = new Commandline();
            cmd.setWorkingDirectory(baseDir);
            cmd.setExecutable("echo");
            cmd.createArgument().setLine(null);
            cmd.createArgument().setLine("Hello");
            assertEquals("echo Hello", cmd.toString());
        }
        catch(Exception e)
        {
            fail(e.getMessage());
        }
    }
    
    public void testCreateCommandInReverseOrder()
    {
        try
        {
            Commandline cmd = new Commandline();
            cmd.setWorkingDirectory(baseDir);
            cmd.createArgument().setValue(".");
            cmd.createArgument(true).setValue("cd");
            assertEquals("cd .", cmd.toString());
        }
        catch(Exception e)
        {
            fail(e.getMessage());
        }
    }
    
    public void testSetFile()
    {
        try
        {
            Commandline cmd = new Commandline();
            cmd.setWorkingDirectory(baseDir);
            cmd.createArgument().setValue("more");
            File f = new File("test.txt");
            cmd.createArgument().setFile(f);
            assertEquals("more "+f.getAbsoluteFile(), cmd.toString());
        }
        catch(Exception e)
        {
            fail(e.getMessage());
        }
    }
    
    public void testQuoteArguments()
    {
        try
        {
            String result = Commandline.quoteArgument("Hello");
            System.out.println(result);
            assertEquals("Hello", result);
            result = Commandline.quoteArgument("Hello World");
            System.out.println(result);
            assertEquals("\"Hello World\"", result);
            result = Commandline.quoteArgument("\"Hello World\"");
            System.out.println(result);
            assertEquals("\'\"Hello World\"\'", result);
        }
        catch(Exception e)
        {
            fail(e.getMessage());
        }
        try
        {
            Commandline.quoteArgument("\"Hello \'World\'\'");
            fail();
        }
        catch(Exception e)
        {
        }
    }
    
}
