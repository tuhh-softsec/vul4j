package org.codehaus.plexus.util;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Manages  a number of test threads, which notify this manager when they have
 * completed. Allows TestCases to easily start and manage multiple test threads.
 *
 * <p>Created on 9/06/2003</p>
 *
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 * @version $Revision$
 *
 */
public class TestThreadManager
{
    //~ Instance fields ----------------------------------------------------------------------------

    /** Test threads which have completed running */
    private Collection runThreads = new Vector();
    /** Test threads still needing to be run, or are currently running*/
    private Collection toRunThreads = new Vector();
    private Logger logger = null;
    /** Any test threads which failed */
    private Vector failedThreads = new Vector();

    /**The object to notify when all the test threads have complleted. Clients use this
     * to lock on (wait) while waiting for the tests to complete*/
    private Object notify = null;

    //~ Constructors -------------------------------------------------------------------------------

    public TestThreadManager( Object notify )
    {
        super();
        this.notify = notify;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * @return
     */
    public Collection getRunThreads()
    {
        return runThreads;
    }

    public void runTestThreads()
    {
        failedThreads.clear();
        //use an array as the tests may run very quickly
        //and modify the toRunThreads vector and hence
        //cause a Concurrent ModificationException on an
        //iterator
        Object[] threads = toRunThreads.toArray();
        for ( int i = 0; i < threads.length; i++ )
        {
            //System.out.println("Starting thread " + i +" ..." );
            ( (AbstractTestThread) threads[i] ).start();
        }
    }

    public Collection getFailedTests()
    {
        return failedThreads;
    }

    /**
     * Return the object which threads can wait on to be notified
     * when all the test threads have completed running
     *
     * @return
     */
    public Object getNotifyObject()
    {
        return notify;
    }


    public boolean hasFailedThreads()
    {
        if ( failedThreads.size() == 0 )
        {
            return false;
        }
        else
            return true;
    }

    /**
     * Determine if any threads are still running!
     *
     * @return DOCUMENT ME!
     */
    public boolean isStillRunningThreads()
    {
        return !toRunThreads.isEmpty();
    }

    /**
     * @return
     */
    public Collection getToRunThreads()
    {
        return toRunThreads;
    }

    /**
     * DOCUMENT ME!
     */
    public void clear()
    {
        toRunThreads.clear();
        runThreads.clear();
        failedThreads.clear();
    }

    /* (non-Javadoc)
    * @see java.util.Collection#remove(java.lang.Object)
    */
    public synchronized void completed( AbstractTestThread thread )
    {
        toRunThreads.remove( thread );
        runThreads.add( thread );
        if ( thread.hasFailed() )
        {
            failedThreads.add( thread );
        }
        //wakeup thread which is waiting for the threads to complete
        //execution
        if ( toRunThreads.isEmpty() )
        {
            synchronized ( notify )
            {
                notify.notify();
            }
        }
    }

    /**
     * Overide this to add your own stuff. Called after
     * <code>registerThread(Object)</code>
     *
     * @param thread DOCUMENT ME!
     */
    public void doRegisterThread( AbstractTestThread thread )
    {
    }

    public final void registerThread( AbstractTestThread thread )
    {
        thread.setThreadRegistry( this );
        if ( toRunThreads.contains( thread ) == false )
        {
            toRunThreads.add( thread );
            doRegisterThread( thread );
        }

    }

    /**
     * Put all the runThreads back in the que to be run again and
     * clear the failedTest collection
     */
    public void reset()
    {
        toRunThreads.clear();
        Iterator iter = runThreads.iterator();
        while ( iter.hasNext() )
        {
            AbstractTestThread test = (AbstractTestThread) iter.next();
            test.reset();
            registerThread( test );
        }

        runThreads.clear();
        failedThreads.clear();
    }
}
