package org.codehaus.plexus.util;

import java.util.Iterator;

import junit.framework.TestCase;

/**
 * Created on 21/06/2003
 * 
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 * @version $Revision$
 */
public class ThreadSafeMapTest extends TestCase
{
    private ThreadSafeMap map;

    /**
     * Constructor
     * 
     * 
     */
    public ThreadSafeMapTest()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param arg0
     */
    public ThreadSafeMapTest(String name)
    {
        super(name);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        // TODO Auto-generated method stub
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        // TODO Auto-generated method stub
        super.tearDown();
    }

    public void test()
    {
        map = new ThreadSafeMap();

        TestThreadManager registry = new TestThreadManager(this);
        //make the readers and writers.
        //mix them up incase VM gives startup times
        //dependent on thread creation order
        for (int i = 0; i < 20; i++)
        {
            //use the same key, but different values, so there will be contention
            Object key = Integer.toString(i);
            //a writer
            TestMapThread wTest = new TestMapThread(map, Integer.toString(i), new Object(), false);
            registry.registerThread(wTest);
            //a reader
            TestMapThread rTest = new TestMapThread(map, key, new Object(), true);
            registry.registerThread(rTest);
        }
        //now run the threads
        registry.runTestThreads();

        //now wait for the threads to finish..
        synchronized (this)
        {
            try
            {
                if( registry.isStillRunningThreads() )
                {
                	wait();
                } 
            }
            catch (InterruptedException e)
            {
                //all threads have finished
            }
        }
        //now test for failures...
        if (registry.hasFailedThreads())
        {
            StringBuffer out = new StringBuffer();
            Iterator iter = registry.getFailedTests().iterator();
            String nl = System.getProperty("line.separator");
            while (iter.hasNext())
            {
                TestMapThread test = (TestMapThread) iter.next();
                out.append(nl);
                out.append(test.getErrorMsg());
                out.append(" Exception=" + Tracer.traceToString(test.getError()));
            }
            fail("Failed test threads: " + out);
        }
    }
}

class TestMapThread extends AbstractTestThread
{
    private ThreadSafeMap map;
    /** Indicates whether to read or write to the map */
    private boolean reader = true;

    private Object key;
    private Object value;
    /**
     * Constructor
     * 
     * 
     */
    public TestMapThread(ThreadSafeMap map, Object key, Object value, boolean reader)
    {
        super();
        this.map = map;
        this.key = key;
        this.value = value;
        this.reader = reader;
    }

    /**
    * @see java.lang.Runnable#run()
    */
    public void doRun()
    {
        try
        {

            if (reader)
            {
                value = map.get(key);
            }
            else
            {
                map.put(key, value);
            }
            setPassed(true);
        }
        catch (Throwable t)
        {
            if (reader)
            {
                setErrorMsg("Reader failed ");
            }
            else
            {
                setErrorMsg("Writer failed");
            }
            setError(t);
        }
    }

    /**
     * @return
     */
    public Object getKey()
    {
        return key;
    }

    /**
     * @return
     */
    public boolean isReader()
    {
        return reader;
    }

    /**
     * @return
     */
    public Object getValue()
    {
        return value;
    }

}
